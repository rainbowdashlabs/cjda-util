/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2022 RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.interactions.premium;

import net.dv8tion.jda.api.entities.Entitlement;
import net.dv8tion.jda.api.entities.Entitlement.EntitlementType;

import java.util.function.Predicate;

public class SKU implements Predicate<Entitlement> {

    private boolean test = false;
    private long skuId;

    public SKU() {
    }

    @Override
    public boolean test(Entitlement entitlement) {
        return entitlement.getSkuIdLong() == skuId;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Entitlement)) return false;
        return test((Entitlement) obj);
    }

    public boolean isTest() {
        return test;
    }

    public long getSkuIdLong() {
        return skuId;
    }
}
