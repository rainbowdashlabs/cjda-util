/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2022 RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.pagination;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import de.chojo.jdautil.localization.ILocalizer;
import de.chojo.jdautil.pagination.bag.IPageBag;
import de.chojo.jdautil.pagination.exceptions.EmptyPageBagException;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildMessageChannel;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import net.dv8tion.jda.api.sharding.ShardManager;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class PageService extends ListenerAdapter {
    private final String previousLabel;
    private final String nextLabel;
    private final String previousText;
    private final String nextText;
    private final Cache<Long, IPageBag> cache;
    private final ILocalizer localizer;

    private PageService(String previousLabel, String nextLabel, String previousText, String nextText, Cache<Long, IPageBag> cache, ILocalizer localizer) {
        this.previousLabel = previousLabel;
        this.nextLabel = nextLabel;
        this.previousText = previousText;
        this.nextText = nextText;
        this.cache = cache;
        this.localizer = localizer;
    }

    /**
     * Registers a new page on a slash command event. The page will be send as a reply to this event.
     *
     * @param event event
     * @param page  page
     */
    public void registerPage(IReplyCallback event, IPageBag page) {
        if (page.pages() == 0) {
            throw new EmptyPageBagException();
        }

        page.buildPage().thenAccept(embed -> {
            event.replyEmbeds(embed)
                    .addActionRows(getPageButtons(event.getGuild(), page))
                    .flatMap(InteractionHook::retrieveOriginal)
                    .queue(message -> cache.put(message.getIdLong(), page));
        });
    }

    /**
     * Registers a new page on a channel. The page will be send to this channel.
     *
     * @param channel channel
     * @param page    page
     */
    public void registerPage(MessageChannel channel, IPageBag page) {
        if (page.pages() == 0) {
            throw new EmptyPageBagException();
        }

        page.buildPage().thenAccept(embed -> {
            channel.sendMessageEmbeds(embed)
                    .setActionRows(getPageButtons(channel.getType() == ChannelType.PRIVATE ? null : ((GuildMessageChannel) channel).getGuild(), page))
                    .queue(message -> cache.put(message.getIdLong(), page));
        });
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        event.getHook().retrieveOriginal().queue(message -> {
            var page = cache.getIfPresent(message.getIdLong());
            if (page == null || !page.canInteract(event.getUser())) return;

            var label = event.getButton().getId();
            if (nextLabel.equals(label) && page.hasNext()) {
                page.scrollNext();
                sendPage(message);
            } else if (previousLabel.equals(label) && page.hasPrevious()) {
                page.scrollPrevious();
                sendPage(message);
            }
        });
    }

    private ActionRow getPageButtons(Guild guild, IPageBag page) {
        return ActionRow.of(
                Button.of(ButtonStyle.SUCCESS, previousLabel, localizer.localize(previousText, guild), Emoji.fromUnicode("⬅")).withDisabled(!page.hasPrevious()),
                Button.of(ButtonStyle.SECONDARY, "pageService:page", page.current() + 1 + "/" + page.pages()),
                Button.of(ButtonStyle.SUCCESS, nextLabel, localizer.localize(nextText, guild), Emoji.fromUnicode("➡️")).withDisabled(!page.hasNext())
        );
    }

    private void sendPage(Message message) {
        var optPage = Optional.ofNullable(cache.getIfPresent(message.getIdLong()));
        optPage.ifPresent(page -> page.buildPage()
                .thenAccept(embed -> {
                    message.editMessageEmbeds()
                            .setEmbeds(embed)
                            .setActionRows(getPageButtons(message.isFromGuild() ? message.getGuild() : null, page))
                            .queue();
                }));
    }

    public static Builder builder(ShardManager shardManager) {
        return new Builder(shardManager);
    }

    public static class Builder {
        private final ShardManager shardManager;
        private String previousLabel = "pageService:previous";
        private String nextLabel = "pageService:next";
        private String previousText = "Previous";
        private String nextText = "Next";
        private Cache<Long, IPageBag> cache = CacheBuilder.newBuilder().expireAfterAccess(10, TimeUnit.MINUTES).build();
        private ILocalizer localizer = ILocalizer.DEFAULT;

        public Builder(ShardManager shardManager) {
            this.shardManager = shardManager;
        }

        /**
         * Sets the button label of the previous button.
         *
         * @param label button label
         * @return builder instance
         */
        public Builder previousLabel(String label) {
            this.previousLabel = label;
            return this;
        }

        /**
         * Sets the button label of the next button.
         *
         * @param label button label
         * @return builder instance
         */
        public Builder nextLabel(String label) {
            this.nextLabel = label;
            return this;
        }

        /**
         * Sets the text of the previous button
         *
         * @param text text
         * @return builder instance
         */
        public Builder previousText(String text) {
            this.previousText = text;
            return this;
        }

        /**
         * Sets the text of the next button
         *
         * @param text text
         * @return builder instance
         */
        public Builder nextText(String text) {
            this.nextText = text;
            return this;
        }

        /**
         * Sets the cache used to cache registered pages.
         *
         * @param cache cache instance
         * @return builder instance
         */
        public Builder cache(Cache<Long, IPageBag> cache) {
            this.cache = cache;
            return this;
        }

        /**
         * Modify a new cache builder which will be used to build the underlying cache to cache registered pages.
         *
         * @param cache cache builder instance
         * @return builder instance
         */
        public Builder cache(Consumer<CacheBuilder<Object, Object>> cache) {
            var builder = CacheBuilder.newBuilder();
            cache.accept(builder);
            this.cache = builder.build();
            return this;
        }

        /**
         * Defined the localizer to be used to localize the {@link #previousText} and {@link #nextText}
         *
         * @param localizer localizer instance
         * @return builder instance
         */
        public Builder localizer(ILocalizer localizer) {
            this.localizer = localizer;
            return this;
        }

        /**
         * Builds and registers the service at the provided {@link ShardManager}.
         *
         * @return a new page service instance
         */
        public PageService build() {
            var service = new PageService(previousLabel, nextLabel, previousText, nextText, cache, localizer);
            shardManager.addEventListener(service);
            return service;
        }
    }
}
