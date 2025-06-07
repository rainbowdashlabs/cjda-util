/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2022 RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.util;

import de.chojo.jdautil.interactions.base.SkuMeta;
import de.chojo.jdautil.wrapper.EventContext;
import net.dv8tion.jda.api.entities.SkuSnowflake;
import net.dv8tion.jda.api.interactions.Interaction;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.util.Collection;
import java.util.List;

public final class Premium {
    private Premium() {
        throw new UnsupportedOperationException("This is a utility class.");
    }

    public static List<Button> buildEntitlementButtons(SkuMeta meta) {
        return meta.sku().stream()
                   .map(e -> Button.premium(SkuSnowflake.fromId(e.skuId())))
                   .toList();
    }

    public static List<Button> buildEntitlementButtons(Collection<Long> meta) {
        return meta.stream()
                   .map(e -> Button.premium(SkuSnowflake.fromId(e)))
                   .toList();
    }

    @Deprecated
    public static void replyPremium(IReplyCallback callback, EventContext context, Collection<Long> skus) {
       replyPremium(context, buildEntitlementButtons(skus));
    }

    public static void replyPremium(EventContext context, Collection<Long> skus) {
        context.event().reply(context.localize(context.interactionHub().premiumErrorMessage()))
               .addActionRow(buildEntitlementButtons(skus))
               .setEphemeral(true)
               .queue();
    }

    public static void replyPremium(EventContext context, SkuMeta skus) {
        context.event().reply(context.localize(context.interactionHub().premiumErrorMessage()))
               .addActionRow(buildEntitlementButtons(skus))
               .setEphemeral(true)
               .queue();
    }

    public static boolean checkAndReplyPremium(EventContext context, SkuMeta expected) {
        if (isNotEntitled(context, expected)) {
            replyPremium(context, buildEntitlementButtons(expected));
            return true;
        }
        return false;
    }

    @Deprecated
    public static void replyPremium(IReplyCallback callback, EventContext context, SkuMeta expected) {
        replyPremium(context, buildEntitlementButtons(expected));
    }

    public static void replyPremium(EventContext context, List<Button> buttons) {
        if (context.event().isAcknowledged()) {
            context.event().getHook().editOriginal(context.localize(context.interactionHub().premiumErrorMessage()))
                    .setActionRow(buttons)
                    .queue();
            return;
        }
        context.event().reply(context.localize(context.interactionHub().premiumErrorMessage()))
                .addActionRow(buttons)
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
