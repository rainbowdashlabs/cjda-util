/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.util;

import de.chojo.jdautil.interactions.base.SkuMeta;
import de.chojo.jdautil.wrapper.EventContext;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.entities.SkuSnowflake;
import net.dv8tion.jda.api.interactions.Interaction;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder;
import net.dv8tion.jda.api.utils.messages.MessageEditData;

import java.util.Collection;
import java.util.List;

public final class Premium {
    private Premium() {
        throw new UnsupportedOperationException("This is a utility class.");
    }

    public static List<ActionRow> buildEntitlementButtons(SkuMeta meta) {
        return ActionRow.partitionOf(meta.sku().stream()
                                         .map(e -> Button.premium(SkuSnowflake.fromId(e.skuId())))
                                         .toList());
    }

    public static List<ActionRow> buildEntitlementButtons(Collection<Long> meta) {
        return ActionRow.partitionOf(meta.stream()
                                         .map(e -> Button.premium(SkuSnowflake.fromId(e)))
                                         .toList());
    }

    @Deprecated(forRemoval = true)
    public static void replyPremium(IReplyCallback callback, EventContext context, Collection<Long> skus) {
        replyPremium(context, buildEntitlementButtons(skus));
    }

    public static void replyPremium(EventContext context, Collection<Long> skus) {
        replyPremium(context, buildEntitlementButtons(skus));
    }

    public static void replyPremium(EventContext context, SkuMeta skus) {
        replyPremium(context, buildEntitlementButtons(skus));
    }


    public static boolean checkAndReplyPremium(EventContext context, SkuMeta expected) {
        if (isNotEntitled(context, expected)) {
            replyPremium(context, buildEntitlementButtons(expected));
            return true;
        }
        return false;
    }

    public static void replyPremium(EventContext context, List<ActionRow> buttons) {
        if (context.event().isAcknowledged()) {
            MessageEditData data = MessageEditBuilder.from(MessageEditData.fromContent(context.localize(context.interactionHub().premiumErrorMessage())))
                                                     .setComponents(buttons)
                                                     .build();
            context.event().getHook().editOriginal(data).queue();
            return;
        }
        context.event().reply(context.localize(context.interactionHub().premiumErrorMessage()))
               .addComponents(buttons)
               .queue();
    }

    @Deprecated(forRemoval = true)
    public static boolean isNotEntitled(Interaction interaction, SkuMeta meta) {
        return !meta.isEntitled(interaction.getEntitlements());
    }

    public static boolean isNotEntitled(EventContext context, SkuMeta expected) {
        return !context.entitlements().isEntitled(expected);
    }

    public static boolean isNotEntitled(SkuMeta current, SkuMeta expected) {
        return !current.isEntitled(expected);
    }
}
