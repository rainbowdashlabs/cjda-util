package de.chojo.jdautil.util;

import de.chojo.jdautil.interactions.base.EntitlementMeta;
import de.chojo.jdautil.wrapper.EventContext;
import net.dv8tion.jda.api.entities.Entitlement;
import net.dv8tion.jda.api.entities.SkuSnowflake;
import net.dv8tion.jda.api.interactions.Interaction;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.util.List;

public final class Premium {
    private Premium() {
        throw new UnsupportedOperationException("This is a utility class.");
    }

    public static List<Button> buildEntitlementButtons(EntitlementMeta meta) {
        return meta.entitlements().stream()
                   .filter(e -> e.getType() != Entitlement.EntitlementType.TEST_MODE_PURCHASE)
                   .map(e -> Button.premium(SkuSnowflake.fromId(e.getSkuIdLong())))
                   .toList();
    }

    public static void replyPremium(IReplyCallback callback, EventContext context, EntitlementMeta meta) {
        callback.reply(context.localize(context.interactionHub().premiumErrorMessage())).addActionRow(buildEntitlementButtons(meta)).queue();
    }

    public static boolean isNotEntitled(Interaction interaction, EntitlementMeta meta) {
        return interaction.getEntitlements().stream().noneMatch(meta::isEntitled);
    }
}
