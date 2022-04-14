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
import de.chojo.jdautil.parsing.ValueParser;
import de.chojo.jdautil.util.Futures;
import de.chojo.jdautil.util.SnowflakeCreator;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildMessageChannel;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import net.dv8tion.jda.api.sharding.ShardManager;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.util.List;
import java.util.stream.Collectors;

import static org.slf4j.LoggerFactory.getLogger;

public class PageService extends ListenerAdapter {

    private static final Logger log = getLogger(PageService.class);

    private final SnowflakeCreator snowflakeCreator = SnowflakeCreator.builder().build();
    private final String previousId;
    private final String nextId;
    private final String previousLabel;
    private final String nextLabel;
    private final Cache<Long, IPageBag> cache;
    private final ILocalizer localizer;

    PageService(String previousId, String nextId, String previousLabel, String nextLabel, Cache<Long, IPageBag> cache, ILocalizer localizer) {
        this.previousId = previousId;
        this.nextId = nextId;
        this.previousLabel = previousLabel;
        this.nextLabel = nextLabel;
        this.cache = cache;
        this.localizer = localizer;
    }

    public static PageServiceBuilder builder(ShardManager shardManager) {
        return new PageServiceBuilder(shardManager);
    }

    /**
     * Registers a new page on a slash command event. The page will be send as a reply to this event.
     *
     * @param event event
     * @param page  page
     * @throws EmptyPageBagException when {@link IPageBag#isEmpty()} is true and the method {@link IPageBag#buildEmptyPage()} is not implemented.
     */
    public void registerPage(IReplyCallback event, IPageBag page) throws EmptyPageBagException {
        registerPage(event, page, false);
    }

    /**
     * Registers a new page on a slash command event. The page will be send as a reply to this event.
     *
     * @param event     event
     * @param page      page
     * @param ephemeral define if the message should be ephemeral
     * @throws EmptyPageBagException when {@link IPageBag#isEmpty()} is true and the method {@link IPageBag#buildEmptyPage()} is not implemented.
     */
    public void registerPage(IReplyCallback event, IPageBag page, boolean ephemeral) throws EmptyPageBagException {
        if (page.isEmpty()) {
            page.buildEmptyPage()
                    .whenComplete(Futures.whenComplete(
                            embed -> event.replyEmbeds(embed)
                                    .setEphemeral(ephemeral)
                                    .queue(),
                            err -> log.error("Could not build page", err)));
            return;
        }

        var id = nextId();

        page.buildPage().whenComplete(Futures.whenComplete(embed -> {
            event.replyEmbeds(embed)
                    .addActionRows(getPageButtons(event.getGuild(), page, id))
                    .setEphemeral(ephemeral)
                    .queue();
            cache.put(id, page);
        }, err -> log.error("Could not build page", err)));
    }

    /**
     * Registers a new page on a channel. The page will be send to this channel.
     *
     * @param channel channel
     * @param page    page
     * @throws EmptyPageBagException when {@link IPageBag#isEmpty()} is true and the method {@link IPageBag#buildEmptyPage()} is not implemented.
     */
    public void registerPage(MessageChannel channel, IPageBag page) throws EmptyPageBagException {
        if (page.isEmpty()) {
            page.buildEmptyPage().whenComplete(Futures.whenComplete(
                    embed -> channel.sendMessageEmbeds(embed).queue(),
                    err -> log.error("Could not build page", err)));
            return;
        }

        var id = nextId();

        page.buildPage()
                .whenComplete(Futures.whenComplete(
                        embed -> {
                            channel.sendMessageEmbeds(embed)
                                    .setActionRows(getPageButtons(channel.getType() == ChannelType.PRIVATE ? null : ((GuildMessageChannel) channel).getGuild(), page, id))
                                    .queue();
                            cache.put(id, page);
                        },
                        err -> log.error("Could not build page", err)));
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        var split = event.getComponentId().split(":", 2);
        var pageId = ValueParser.parseLong(split[0]);

        if (pageId.isEmpty() || split.length != 2) {
            return;
        }

        var page = cache.getIfPresent(pageId.get());
        if (page == null || !page.canInteract(event.getUser())) return;

        if (!event.isAcknowledged()) {
            event.deferEdit().queue();
        }

        var id = split[1];
        if (nextId.equals(id) && page.hasNext()) {
            page.scrollNext();
            sendPage(event, pageId.get());
        } else if (previousId.equals(id) && page.hasPrevious()) {
            page.scrollPrevious();
            sendPage(event, pageId.get());
        } else {
            page.buttons().stream()
                    .filter(button -> button.button(page).getId().equals(id))
                    .findFirst()
                    .ifPresent(button -> button.invoke(page, event));
        }
    }

    private List<ActionRow> getPageButtons(Guild guild, IPageBag page, long id) {
        var buttons = page.buttons().stream()
                .map(b -> b.button(page))
                .map(b -> b.withId(addId(id, b.getId())).withLabel(localizer.localize(b.getLabel(), guild)))
                .collect(Collectors.toList());
        var actionRows = ActionRow.partitionOf(buttons);
        actionRows.add(ActionRow.of(
                Button.of(ButtonStyle.SUCCESS, addId(id, previousId), localizer.localize(previousLabel, guild), Emoji.fromUnicode("⬅")).withDisabled(!page.hasPrevious()),
                Button.of(ButtonStyle.SECONDARY, addId(id, "pageService:page"), page.current() + 1 + "/" + page.pages()),
                Button.of(ButtonStyle.SUCCESS, addId(id, nextId), localizer.localize(nextLabel, guild), Emoji.fromUnicode("➡️")).withDisabled(!page.hasNext())
        ));
        return actionRows;
    }

    private String addId(long id, String other) {
        return String.format("%s:%s", id, other);
    }

    private void sendPage(ButtonInteractionEvent event, long pageId) {
        var page = cache.getIfPresent(pageId);
        if (page.isEmpty()) {
            page.buildEmptyPage().whenComplete(Futures.whenComplete(
                    embed -> event.getHook().editOriginalEmbeds(embed).queue(),
                    err -> log.error("Could not build page", err)));
            return;
        }
        page.buildPage()
                .whenComplete(Futures.whenComplete(
                        embed -> event.getHook()
                                .editOriginalEmbeds(embed)
                                .setActionRows(getPageButtons(event.isFromGuild() ? event.getGuild() : null, page, pageId))
                                .queue(),
                        err -> {
                            log.error("Could not build page", err);
                            event.getHook().editOriginal("Something went wrong")
                                    .setActionRows(getPageButtons(event.isFromGuild() ? event.getGuild() : null, page, pageId))
                                    .queue();
                        }));
    }

    public long nextId() {
        return snowflakeCreator.nextId();
    }
}
