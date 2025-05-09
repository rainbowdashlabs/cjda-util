/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2022 RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.interactions.premium;

import net.dv8tion.jda.api.entities.Entitlement;

import java.util.function.Predicate;

public class SKU implements Predicate<Entitlement> {

    private long skuId;

    public SKU() {
    }

    public SKU(long skuId) {
        this.skuId = skuId;
    }

    @Override
    public boolean test(Entitlement entitlement) {
        return entitlement.getSkuIdLong() == skuId;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof SKU sku) {
            return skuId == (sku).skuId;
        }
        if (!(obj instanceof Entitlement)) return false;
        return test((Entitlement) obj);
    }

    public long skuId() {
        return skuId;
    }
}
