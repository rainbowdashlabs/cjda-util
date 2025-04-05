package de.chojo.jdautil.interactions.base;

import net.dv8tion.jda.api.entities.Entitlement;

import java.util.Collection;

public interface EntitlementMeta {
    Collection<de.chojo.jdautil.interactions.premium.Entitlement> entitlements();

    default boolean isEntitled(Entitlement entitlement) {
        if (entitlements().isEmpty()) return true;
        return entitlements().stream().anyMatch(entitlement);
    }
}
