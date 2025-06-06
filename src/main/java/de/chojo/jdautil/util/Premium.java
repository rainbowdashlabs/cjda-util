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

    public static void replyPremium(IReplyCallback callback, EventContext context, Collection<Long> skus) {
        callback.reply(context.localize(context.interactionHub().premiumErrorMessage()))
                .addActionRow(buildEntitlementButtons(skus))
                .setEphemeral(true)
                .queue();
    }

    public static void replyPremium(IReplyCallback callback, EventContext context, SkuMeta meta) {
        callback.reply(context.localize(context.interactionHub().premiumErrorMessage()))
                .addActionRow(buildEntitlementButtons(meta))
                .setEphemeral(true)
                .queue();
    }

    public static boolean isNotEntitled(Interaction interaction, SkuMeta meta) {
        return !meta.isEntitled(interaction.getEntitlements());
    }

    public static boolean isNotEntitled(SkuMeta current, SkuMeta expected) {
        return !current.isEntitled(expected);
    }
}
