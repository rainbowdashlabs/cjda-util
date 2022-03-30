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
import org.slf4j.Logger;

import java.util.List;
import java.util.stream.Collectors;

import static org.slf4j.LoggerFactory.getLogger;

public class PageService extends ListenerAdapter {

    private static final Logger log = getLogger(PageService.class);

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
        var page = cache.getIfPresent(event.getMessageIdLong());
        if (page == null || !page.canInteract(event.getUser())) return;

        if (!event.isAcknowledged()) {
            event.deferEdit().queue();
        }

        var id = event.getButton().getId();
        if (nextId.equals(id) && page.hasNext()) {
            page.scrollNext();
            sendPage(event);
        } else if (previousId.equals(id) && page.hasPrevious()) {
            page.scrollPrevious();
            sendPage(event);
        } else {
            page.buttons().stream()
                    .filter(button -> button.button(page).getId().equals(id))
                    .findFirst()
                    .ifPresent(button -> button.invoke(page, event));
        }
    }

    private List<ActionRow> getPageButtons(Guild guild, IPageBag page) {
        var buttons = page.buttons().stream()
                .map(b -> b.button(page))
                .map(b -> b.withLabel(localizer.localize(b.getLabel(), guild)))
                .collect(Collectors.toList());
        var actionRows = ActionRow.partitionOf(buttons);
        actionRows.add(ActionRow.of(
                Button.of(ButtonStyle.SUCCESS, previousId, localizer.localize(previousLabel, guild), Emoji.fromUnicode("⬅")).withDisabled(!page.hasPrevious()),
                Button.of(ButtonStyle.SECONDARY, "pageService:page", page.current() + 1 + "/" + page.pages()),
                Button.of(ButtonStyle.SUCCESS, nextId, localizer.localize(nextLabel, guild), Emoji.fromUnicode("➡️")).withDisabled(!page.hasNext())
        ));
        return actionRows;
    }

    private void sendPage(ButtonInteractionEvent event) {
        var page = cache.getIfPresent(event.getMessageIdLong());
        page.buildPage()
                .thenAccept(embed -> {
                    event.getHook()
                            .editOriginalEmbeds(embed)
                            .setActionRows(getPageButtons(event.isFromGuild() ? event.getGuild() : null, page))
                            .queue();
                }).exceptionally(err -> {
                    log.error("Could not build page", err);
                    event.getHook().editOriginal("Something went wrong")
                            .setActionRows(getPageButtons(event.isFromGuild() ? event.getGuild() : null, page))
                            .queue();
                    return null;
                });
    }

    public static PageServiceBuilder builder(ShardManager shardManager) {
        return new PageServiceBuilder(shardManager);
    }

}
