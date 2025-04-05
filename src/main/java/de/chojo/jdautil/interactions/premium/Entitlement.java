package de.chojo.jdautil.interactions.premium;

import net.dv8tion.jda.api.entities.Entitlement.EntitlementType;

import java.time.OffsetDateTime;
import java.util.function.Predicate;

public class Entitlement implements Predicate<net.dv8tion.jda.api.entities.Entitlement> {

    EntitlementType type;
    long skuId;

    @Override
    public boolean test(net.dv8tion.jda.api.entities.Entitlement entitlement) {
        if (entitlement.getType() != type) return false;

        if (entitlement.getSkuIdLong() != skuId) return false;
        if (entitlement.getTimeEnding() != null && entitlement.getTimeEnding().isBefore(OffsetDateTime.now())) {
            return false;
        }
        return true;
    }
}
