/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2022 RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.menus;

import com.google.common.cache.Cache;
import de.chojo.jdautil.localization.ILocalizer;
import de.chojo.jdautil.localization.util.LocaleProvider;
import de.chojo.jdautil.parsing.ValueParser;
import de.chojo.jdautil.util.SnowflakeCreator;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent;
import net.dv8tion.jda.api.events.interaction.component.SelectMenuInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.sharding.ShardManager;
import org.jetbrains.annotations.NotNull;

public class MenuService extends ListenerAdapter {
    private final Cache<Long, MenuContainer> cache;
    private final ILocalizer localizer;
    private final SnowflakeCreator snowflakeCreator = SnowflakeCreator.builder().build();

    public MenuService(Cache<Long, MenuContainer> cache, ILocalizer localizer) {
        this.cache = cache;
        this.localizer = localizer;
    }

    public static MenuServiceBuilder builder(ShardManager shardManager) {
        return new MenuServiceBuilder(shardManager);
    }

    @Override
    public void onSelectMenuInteraction(@NotNull SelectMenuInteractionEvent event) {
        handleInteraction(event);
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        handleInteraction(event);
    }

    private void handleInteraction(GenericComponentInteractionCreateEvent event) {
        var split = event.getComponentId().split(":", 2);
        var pageId = ValueParser.parseLong(split[0]);

        if (pageId.isEmpty() || split.length != 2) {
            return;
        }

        var menuContainer = cache.getIfPresent(pageId.get());
        if (menuContainer == null || !menuContainer.canInteract(event.getUser())) return;
        menuContainer.invoke(event, split[1]);
    }

    public void register(MenuAction interaction) {
        var id = nextId();
        interaction.send(localizer, id);
        cache.put(id, new MenuContainer(id, localizer.context(LocaleProvider.guild(interaction.guild())), interaction.components(), interaction.user()));
    }

    private long nextId() {
        return snowflakeCreator.nextLong();
    }
}
