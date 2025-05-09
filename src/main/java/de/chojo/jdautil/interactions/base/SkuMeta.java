/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2022 RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.interactions.base;

import de.chojo.jdautil.interactions.premium.SKU;
import net.dv8tion.jda.api.entities.Entitlement;

import java.util.Collection;
import java.util.List;

public interface SkuMeta {
    Collection<SKU> sku();

    default boolean isEntitled(Entitlement entitlement) {
        return sku().stream().anyMatch(e -> e.test(entitlement));
    }

    default boolean isEntitled(List<Entitlement> entitlement) {
        if (sku().isEmpty()) return true;
        return entitlement.stream().anyMatch(this::isEntitled);
    }

    default boolean isEntitled(SkuMeta skuMeta) {
        return skuMeta.sku().stream().anyMatch(this::isEntitled);
    }

    default boolean isEntitled(SKU sku) {
        return sku().stream().anyMatch(sku::equals);
    }
}
