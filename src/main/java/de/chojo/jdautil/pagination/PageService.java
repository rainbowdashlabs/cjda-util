/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2022 RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.pagination;

import com.google.common.cache.Cache;
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

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class PageService extends ListenerAdapter {
    private final String previousLabel;
    private final String nextLabel;
    private final String previousText;
    private final String nextText;
    private final Cache<Long, IPageBag> cache;
    private final ILocalizer localizer;

    PageService(String previousLabel, String nextLabel, String previousText, String nextText, Cache<Long, IPageBag> cache, ILocalizer localizer) {
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

            var id = event.getButton().getId();
            if (nextLabel.equals(id) && page.hasNext()) {
                page.scrollNext();
                sendPage(message);
            } else if (previousLabel.equals(id) && page.hasPrevious()) {
                page.scrollPrevious();
                sendPage(message);
            } else {
                page.buttons().stream()
                        .filter(button -> button.button(page).getId().equals(id))
                        .findFirst()
                        .ifPresent(button -> button.invoke(page, event));
            }
        });
    }

    private List<ActionRow> getPageButtons(Guild guild, IPageBag page) {
        var buttons = page.buttons().stream()
                .map(b -> b.button(page))
                .map(b -> b.withLabel(localizer.localize(b.getLabel(), guild)))
                .collect(Collectors.toList());
        var actionRows = ActionRow.partitionOf(buttons);
        actionRows.add(ActionRow.of(
                Button.of(ButtonStyle.SUCCESS, previousLabel, localizer.localize(previousText, guild), Emoji.fromUnicode("⬅")).withDisabled(!page.hasPrevious()),
                Button.of(ButtonStyle.SECONDARY, "pageService:page", page.current() + 1 + "/" + page.pages()),
                Button.of(ButtonStyle.SUCCESS, nextLabel, localizer.localize(nextText, guild), Emoji.fromUnicode("➡️")).withDisabled(!page.hasNext())
        ));
        return actionRows;
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

    public static PageServiceBuilder builder(ShardManager shardManager) {
        return new PageServiceBuilder(shardManager);
    }

}
