/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2022 RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.buttons;

import com.google.common.cache.Cache;
import de.chojo.jdautil.localization.ILocalizer;
import de.chojo.jdautil.parsing.ValueParser;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.sharding.ShardManager;
import org.jetbrains.annotations.NotNull;

public class ButtonService extends ListenerAdapter {
    private final Cache<Long, ButtonContainer> cache;
    private final ILocalizer localizer;

    public ButtonService(Cache<Long, ButtonContainer> cache, ILocalizer localizer) {
        this.cache = cache;
        this.localizer = localizer;
    }

    public static ButtonServiceBuilder builder(ShardManager shardManager) {
        return new ButtonServiceBuilder(shardManager);
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        var split = event.getComponentId().split(":", 2);
        var pageId = ValueParser.parseLong(split[0]);

        if (pageId.isEmpty() || split.length != 2) {
            return;
        }

        var buttonContainer = cache.getIfPresent(pageId.get());
        if (buttonContainer == null || !buttonContainer.canInteract(event.getUser())) return;
        buttonContainer.invoke(event, split[1]);
    }

    public void register(ButtonAction interaction) {
        var id = nextId();
        interaction.send(localizer, id);
        cache.put(id, new ButtonContainer(interaction.buttons(), interaction.user()));
    }

    private long nextId() {
        return System.currentTimeMillis();
    }
}
