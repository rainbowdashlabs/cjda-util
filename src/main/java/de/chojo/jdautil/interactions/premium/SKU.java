/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.interactions.premium;

import net.dv8tion.jda.api.entities.Entitlement;

import java.util.function.Predicate;

public class SKU implements Predicate<Entitlement> {

    private long skuId;

    public SKU() {}

    public SKU(long skuId) {
        this.skuId = skuId;
    }

    public SKU(Entitlement entitlement) {
        this.skuId = entitlement.getSkuIdLong();
    }

    @Override
    public boolean test(Entitlement entitlement) {
        return entitlement.getSkuIdLong() == skuId;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof SKU sku && skuId == sku.skuId;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(skuId);
    }

    public long skuId() {
        return skuId;
    }
}
