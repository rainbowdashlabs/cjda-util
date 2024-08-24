/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.modals.service;

import com.google.common.cache.Cache;
import de.chojo.jdautil.localization.ILocalizer;
import de.chojo.jdautil.localization.util.LocaleProvider;
import de.chojo.jdautil.modals.handler.ModalHandler;
import de.chojo.jdautil.modals.handler.ModalHandlerBuilder;
import de.chojo.jdautil.util.SnowflakeCreator;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import net.dv8tion.jda.api.sharding.ShardManager;
import org.jetbrains.annotations.NotNull;

public class ModalService extends ListenerAdapter {
    private final ILocalizer localizer;
    private final SnowflakeCreator snowflakeCreator = SnowflakeCreator.builder().build();
    private final Cache<String, ModalHandler> handlers;

    public ModalService(ILocalizer localizer, Cache<String, ModalHandler> handlers) {
        this.localizer = localizer;
        this.handlers = handlers;
    }

    /**
     * Get a new builder instance for the modal service
     *
     * @param shardManager shardmanager where the service should be registered
     * @return builder instance
     */
    public static ModalServiceBuilder builder(ShardManager shardManager) {
        return new ModalServiceBuilder(shardManager);
    }

    @SubscribeEvent
    @Override
    public void onModalInteraction(@NotNull ModalInteractionEvent event) {
        var modalId = event.getModalId();
        var handler = handlers.getIfPresent(modalId);
        if (handler == null) return;
        handler.handle(event);
    }

    /**
     * Registers and sends a new modal.
     *
     * @param callback callback to send the modal
     * @param handler  handler to handle the submission
     */
    public void registerModal(GenericCommandInteractionEvent callback, ModalHandler handler) {
        var snowflake = snowflakeCreator.nextString();
        callback.replyModal(handler.createModal(snowflake, localizer.context(LocaleProvider.guild(callback)))).queue();
        handlers.put(snowflake, handler);
    }

    public void registerModal(GenericCommandInteractionEvent callback, ModalHandlerBuilder handler) {
        registerModal(callback, handler.build());
    }
}
