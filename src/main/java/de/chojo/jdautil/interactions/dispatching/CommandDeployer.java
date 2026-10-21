/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.interactions.dispatching;

import de.chojo.jdautil.interactions.base.CommandDataProvider;
import de.chojo.jdautil.interactions.base.InteractionMeta;
import de.chojo.jdautil.interactions.base.InteractionScope;
import de.chojo.jdautil.interactions.slash.Slash;
import de.chojo.jdautil.localization.ILocalizer;
import de.chojo.jdautil.util.Guilds;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.sharding.ShardManager;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;
import java.util.function.LongPredicate;
import java.util.stream.Stream;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Deploys global and guild commands to discord and keeps track of guild specific command aliases.
 * <p>
 * The initial deployment runs on its own thread, so startup is not held back until every guild received its commands.
 * Every write of guild commands holds a lock for that guild and builds the data of that guild when it runs.
 * A guild that was refreshed while the initial deployment is still running is skipped by it, since the snapshot the
 * initial deployment works with is older than the refresh.
 * <p>
 * An alias renames a private slash command on a guild and adds a guild copy of a global slash command under the alias.
 * Aliases are only supported for slash commands.
 */
final class CommandDeployer {
    private static final Logger log = getLogger(CommandDeployer.class);
    private static final String THREAD_NAME = "cjda-command-deployment";

    private final ShardManager shardManager;
    private final ILocalizer localizer;
    private final Collection<? extends Slash> slash;
    private final Collection<? extends CommandDataProvider> contextInteractions;
    private final Function<InteractionMeta, List<Long>> guildCommandMapper;
    private final Function<InteractionMeta, Map<Long, String>> aliasMapper;
    private final boolean cleanGuildCommands;
    private final boolean testMode;
    private final Map<Long, ReentrantLock> guildLocks = new ConcurrentHashMap<>();
    private final Map<Long, Map<String, String>> aliases = new ConcurrentHashMap<>();
    private final Set<Long> refreshedDuringDeployment = ConcurrentHashMap.newKeySet();
    private CompletableFuture<Void> deployment = CompletableFuture.completedFuture(null);

    CommandDeployer(ShardManager shardManager, ILocalizer localizer,
                    Collection<? extends Slash> slash, Collection<? extends CommandDataProvider> contextInteractions,
                    Function<InteractionMeta, List<Long>> guildCommandMapper,
                    Function<InteractionMeta, Map<Long, String>> aliasMapper,
                    boolean cleanGuildCommands, boolean testMode) {
        this.shardManager = shardManager;
        this.localizer = localizer;
        this.slash = slash;
        this.contextInteractions = contextInteractions;
        this.guildCommandMapper = guildCommandMapper;
        this.aliasMapper = aliasMapper;
        this.cleanGuildCommands = cleanGuildCommands;
        this.testMode = testMode;
    }

    /**
     * Builds the command data and the alias index synchronously and deploys it on a separate thread.
     *
     * @return future completing once every command is deployed
     */
    CompletableFuture<Void> start() {
        var global = globalData();
        var guilds = guildData();
        deployment = CompletableFuture.runAsync(() -> deploy(global, guilds), this::runOnDeploymentThread)
                .whenComplete((result, err) -> {
                    refreshedDuringDeployment.clear();
                    if (err != null) log.error("Command deployment failed.", err);
                });
        return deployment;
    }

    /**
     * Future of the initial deployment. Already completed when no deployment was started.
     *
     * @return deployment future
     */
    CompletableFuture<Void> deployment() {
        return deployment;
    }

    /**
     * Rebuilds and deploys the guild commands of a single guild, including its aliases.
     *
     * @param guild guild to refresh
     */
    void refresh(Guild guild) {
        var guildId = guild.getIdLong();
        withGuildLock(guildId, () -> {
            refreshedDuringDeployment.add(guildId);
            var commands = new ArrayList<CommandData>();
            if (testMode) commands.addAll(globalData());
            commands.addAll(guildData(guildId));
            log.debug("Refreshing {} guild commands for {}.", commands.size(), Guilds.prettyName(guild));
            guild.updateCommands().addCommands(commands).complete();
        });
    }

    /**
     * Resolves an alias on a guild to the name of the command it stands for.
     *
     * @param guildId guild id
     * @param name    name the command was invoked with
     * @return the canonical command name if the name is an alias on this guild
     */
    Optional<String> canonicalName(long guildId, String name) {
        return Optional.ofNullable(aliases.getOrDefault(guildId, Collections.emptyMap()).get(name.toLowerCase(Locale.ROOT)));
    }

    private void runOnDeploymentThread(Runnable runnable) {
        var thread = new Thread(runnable, THREAD_NAME);
        thread.setDaemon(true);
        thread.start();
    }

    private void deploy(List<CommandData> global, Map<Long, List<CommandData>> guildCommands) {
        awaitShards();

        for (var command : global) {
            log.info("Registering command {}.", command.getName());
        }

        if (testMode) {
            log.warn("Interactions hub is running in test mode. All interactions will be published on guilds and not globally");
            for (var guild : shardManager.getGuilds()) {
                var deploy = new ArrayList<>(global);
                deploy.addAll(guildCommands.getOrDefault(guild.getIdLong(), Collections.emptyList()));
                deployGuild(guild, deploy);
            }
            log.debug("Global slash commands were unregistered.");
            shardManager.getShards().getFirst().updateCommands().complete();
            return;
        }

        deployGlobal(global);

        if (cleanGuildCommands) {
            log.warn("Clean guild command deployment requested. Updating commands for all guilds.");
            for (var guild : shardManager.getGuilds()) {
                deployGuild(guild, guildCommands.getOrDefault(guild.getIdLong(), Collections.emptyList()));
            }
            return;
        }

        for (var entry : guildCommands.entrySet()) {
            var guild = shardManager.getGuildById(entry.getKey());
            if (guild == null) {
                log.warn("Guild {} not found. Can't deploy commands.", entry.getKey());
                continue;
            }
            deployGuild(guild, entry.getValue());
        }
    }

    private void awaitShards() {
        for (var shard : shardManager.getShards()) {
            try {
                shard.awaitReady();
                log.info("Shard {} is ready.", shard.getShardInfo().getShardId());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException("Interrupted while waiting for shards.", e);
            }
        }
    }

    private void deployGlobal(List<CommandData> global) {
        log.info("Updating {} global commands.", global.size());
        shardManager.getShards().getFirst().updateCommands().addCommands(global).queue(RestAction.getDefaultSuccess(), err -> {
            if (err instanceof ErrorResponseException e && e.getErrorCode() == 50035) {
                for (var error : e.getSchemaErrors()) {
                    var loc = error.getLocation().split("\\.", 2);
                    var command = global.get(Integer.parseInt(loc[0]));
                    log.error("Invalid values in command {} at {}. Caused by:\n{}", command.getName(), loc[1], error.getErrors());
                }
                return;
            }
            log.error("Could not deploy commands", err);
        });
    }

    private void deployGuild(Guild guild, List<CommandData> commands) {
        withGuildLock(guild.getIdLong(), () -> {
            if (refreshedDuringDeployment.contains(guild.getIdLong())) {
                log.debug("Skipping {}. It was refreshed during deployment.", Guilds.prettyName(guild));
                return;
            }
            log.debug("Updating {} guild commands for {}.", commands.size(), Guilds.prettyName(guild));
            try {
                guild.updateCommands().addCommands(commands).complete();
            } catch (RuntimeException e) {
                log.error("Could not deploy commands on {}.", Guilds.prettyName(guild), e);
            }
        });
    }

    private void withGuildLock(long guildId, Runnable runnable) {
        var lock = guildLocks.computeIfAbsent(guildId, k -> new ReentrantLock());
        lock.lock();
        try {
            runnable.run();
        } finally {
            lock.unlock();
        }
    }

    private List<CommandData> globalData() {
        return allInteractions().stream()
                .filter(command -> command.meta().scope() == InteractionScope.GLOBAL)
                .map(this::commandData)
                .toList();
    }

    private Map<Long, List<CommandData>> guildData() {
        var collected = collectGuildCommands(guildId -> true);
        aliases.clear();
        aliases.putAll(collected.aliases());
        return collected.commands();
    }

    private List<CommandData> guildData(long guildId) {
        var collected = collectGuildCommands(id -> id == guildId);
        var guildAliases = collected.aliases().get(guildId);
        if (guildAliases == null) {
            aliases.remove(guildId);
        } else {
            aliases.put(guildId, guildAliases);
        }
        return collected.commands().getOrDefault(guildId, Collections.emptyList());
    }

    /**
     * Collects the guild commands and aliases of every guild accepted by the filter.
     * <p>
     * A private command is deployed on the guilds of the guild command mapper, under its alias where the guild has one.
     * A global command is only added to a guild when the guild has an alias for it.
     */
    private GuildCommands collectGuildCommands(LongPredicate guildFilter) {
        var collected = new GuildCommands(new HashMap<>(), new HashMap<>());
        for (var command : allInteractions()) {
            var meta = command.meta();
            Map<Long, String> commandAliases = command instanceof Slash ? aliasMapper.apply(meta) : Map.of();
            if (meta.scope() == InteractionScope.PRIVATE) {
                CommandData data = null;
                for (var guildId : guildCommandMapper.apply(meta)) {
                    if (!guildFilter.test(guildId)) continue;
                    var alias = commandAliases.get(guildId);
                    if (alias != null) {
                        collected.addAlias(guildId, alias, command, aliased(command, alias));
                        continue;
                    }
                    if (data == null) data = commandData(command);
                    collected.add(guildId, data);
                }
                continue;
            }
            for (var entry : commandAliases.entrySet()) {
                if (!guildFilter.test(entry.getKey())) continue;
                collected.addAlias(entry.getKey(), entry.getValue(), command, aliased(command, entry.getValue()));
            }
        }
        return collected;
    }

    private List<CommandDataProvider> allInteractions() {
        return Stream.concat(slash.stream(), contextInteractions.stream()).toList();
    }

    private CommandData commandData(CommandDataProvider command) {
        try {
            return command.toCommandData(localizer);
        } catch (Exception e) {
            throw new IllegalStateException("Bot command deployment failed in command " + command.meta().name(), e);
        }
    }

    /**
     * Builds the command data of a command under a different name. Name localizations are dropped, since they would
     * show the original name again in every localized client.
     */
    private CommandData aliased(CommandDataProvider command, String alias) {
        var data = commandData(command).toData();
        data.put("name", alias.toLowerCase(Locale.ROOT));
        data.remove("name_localizations");
        return CommandData.fromData(data);
    }

    private record GuildCommands(Map<Long, List<CommandData>> commands, Map<Long, Map<String, String>> aliases) {
        void add(long guildId, CommandData data) {
            commands.computeIfAbsent(guildId, k -> new ArrayList<>()).add(data);
        }

        void addAlias(long guildId, String alias, CommandDataProvider command, CommandData data) {
            add(guildId, data);
            aliases.computeIfAbsent(guildId, k -> new HashMap<>()).put(alias.toLowerCase(Locale.ROOT), command.meta().name());
        }
    }
}
