/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.interactions.dispatching;

import de.chojo.jdautil.interactions.base.InteractionMeta;
import de.chojo.jdautil.interactions.base.InteractionScope;
import de.chojo.jdautil.interactions.slash.Slash;
import de.chojo.jdautil.localization.ILocalizer;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import net.dv8tion.jda.api.sharding.ShardManager;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CommandDeployerTest {
    private static final long GUILD = 1L;
    private static final long OTHER_GUILD = 2L;

    private final Slash reputation = Slash.of("reputation", "give reputation")
                                          .unlocalized()
                                          .scope(InteractionScope.PRIVATE)
                                          .command((event, context) -> {})
                                          .build();
    private final Slash profile = Slash.of("profile", "show profile")
                                       .unlocalized()
                                       .command((event, context) -> {})
                                       .build();

    @Test
    void privateCommandIsReplacedByAlias() {
        var guild = guild(GUILD);
        var deployer = deployer(meta -> List.of(GUILD, OTHER_GUILD), aliases("reputation", "thanks"), mock(ShardManager.class));

        deployer.refresh(guild.guild());

        assertEquals(List.of("thanks"), guild.deployedNames());
        assertEquals(Optional.of("reputation"), deployer.canonicalName(GUILD, "thanks"));
        assertEquals(Optional.empty(), deployer.canonicalName(OTHER_GUILD, "thanks"));
    }

    @Test
    void privateCommandKeepsItsNameWithoutAlias() {
        var guild = guild(OTHER_GUILD);
        var deployer = deployer(meta -> List.of(GUILD, OTHER_GUILD), aliases("reputation", "thanks"), mock(ShardManager.class));

        deployer.refresh(guild.guild());

        assertEquals(List.of("reputation"), guild.deployedNames());
    }

    @Test
    void aliasOfPrivateCommandIsIgnoredOnGuildsWithoutTheCommand() {
        var guild = guild(GUILD);
        var deployer = deployer(meta -> List.of(), aliases("reputation", "thanks"), mock(ShardManager.class));

        deployer.refresh(guild.guild());

        assertEquals(List.of(), guild.deployedNames());
        assertEquals(Optional.empty(), deployer.canonicalName(GUILD, "thanks"));
    }

    @Test
    void globalCommandIsAddedUnderAlias() {
        var guild = guild(GUILD);
        var deployer = deployer(meta -> List.of(), aliases("profile", "Karma"), mock(ShardManager.class));

        deployer.refresh(guild.guild());

        assertEquals(List.of("karma"), guild.deployedNames());
        assertEquals(Optional.of("profile"), deployer.canonicalName(GUILD, "KARMA"));
    }

    @Test
    void refreshRemovesAliasFromIndex() {
        var guild = guild(GUILD);
        var aliases = new HashMap<String, Map<Long, String>>();
        aliases.put("profile", Map.of(GUILD, "karma"));
        var deployer = deployer(meta -> List.of(), meta -> aliases.getOrDefault(meta.name(), Map.of()), mock(ShardManager.class));
        deployer.refresh(guild.guild());

        aliases.clear();
        deployer.refresh(guild.guild());

        assertEquals(Optional.empty(), deployer.canonicalName(GUILD, "karma"));
    }

    @Test
    void startReturnsBeforeShardsAreReady() throws Exception {
        var ready = new CountDownLatch(1);
        var shard = mock(JDA.class);
        when(shard.awaitReady()).then(invocation -> {
            ready.await();
            return shard;
        });
        when(shard.getShardInfo()).thenReturn(new JDA.ShardInfo(0, 1));
        var globalUpdate = updateAction(new ArrayList<>());
        when(shard.updateCommands()).thenReturn(globalUpdate);
        var shardManager = mock(ShardManager.class);
        when(shardManager.getShards()).thenReturn(List.of(shard));
        var deployer = deployer(meta -> List.of(), aliases("profile", "karma"), shardManager);

        var deployment = deployer.start();

        assertFalse(deployment.isDone());
        assertEquals(Optional.of("profile"), deployer.canonicalName(GUILD, "karma"));
        ready.countDown();
        deployment.get(5, TimeUnit.SECONDS);
    }

    @Test
    void deploymentSkipsGuildsRefreshedMeanwhile() throws Exception {
        var guild = guild(GUILD);
        var shard = mock(JDA.class);
        when(shard.getShardInfo()).thenReturn(new JDA.ShardInfo(0, 1));
        var globalUpdate = updateAction(new ArrayList<>());
        when(shard.updateCommands()).thenReturn(globalUpdate);
        var shardManager = mock(ShardManager.class);
        when(shardManager.getShards()).thenReturn(List.of(shard));
        when(shardManager.getGuildById(GUILD)).thenReturn(guild.guild());
        var deployer = deployer(meta -> List.of(GUILD), aliases("reputation", "thanks"), shardManager);

        deployer.refresh(guild.guild());
        deployer.start().get(5, TimeUnit.SECONDS);

        verify(guild.guild(), times(1)).updateCommands();
    }

    @Test
    void deploymentContinuesAfterFailingGuild() throws Exception {
        var failing = mock(Guild.class);
        when(failing.getIdLong()).thenReturn(OTHER_GUILD);
        when(failing.updateCommands()).thenThrow(new IllegalStateException("rate limited"));
        var guild = guild(GUILD);
        var shard = mock(JDA.class);
        when(shard.getShardInfo()).thenReturn(new JDA.ShardInfo(0, 1));
        var globalUpdate = updateAction(new ArrayList<>());
        when(shard.updateCommands()).thenReturn(globalUpdate);
        var shardManager = mock(ShardManager.class);
        when(shardManager.getShards()).thenReturn(List.of(shard));
        when(shardManager.getGuilds()).thenReturn(List.of(failing, guild.guild()));
        var deployer = new CommandDeployer(shardManager, ILocalizer.DEFAULT, List.of(reputation, profile), List.of(),
                meta -> List.of(GUILD, OTHER_GUILD), meta -> Map.of(), true, false);

        deployer.start().get(5, TimeUnit.SECONDS);

        assertEquals(List.of("reputation"), guild.deployedNames());
    }

    private CommandDeployer deployer(Function<InteractionMeta, List<Long>> guildMapper,
                                     Function<InteractionMeta, Map<Long, String>> aliasMapper,
                                     ShardManager shardManager) {
        return new CommandDeployer(shardManager, ILocalizer.DEFAULT, List.of(reputation, profile), List.of(),
                guildMapper, aliasMapper, false, false);
    }

    private static Function<InteractionMeta, Map<Long, String>> aliases(String command, String alias) {
        return meta -> meta.name().equals(command) ? Map.of(GUILD, alias) : Map.of();
    }

    private static GuildMock guild(long id) {
        var deployed = new ArrayList<CommandData>();
        var guild = mock(Guild.class);
        when(guild.getIdLong()).thenReturn(id);
        when(guild.getName()).thenReturn("guild " + id);
        when(guild.getId()).thenReturn(String.valueOf(id));
        var action = updateAction(deployed);
        when(guild.updateCommands()).thenReturn(action);
        return new GuildMock(guild, deployed);
    }

    @SuppressWarnings("unchecked")
    private static CommandListUpdateAction updateAction(List<CommandData> deployed) {
        var action = mock(CommandListUpdateAction.class);
        when(action.addCommands(any(Collection.class))).then(invocation -> {
            deployed.clear();
            deployed.addAll(invocation.getArgument(0));
            return action;
        });
        when(action.complete()).thenReturn(List.of());
        return action;
    }

    private record GuildMock(Guild guild, List<CommandData> deployed) {
        List<String> deployedNames() {
            return deployed.stream().map(CommandData::getName).toList();
        }
    }
}
