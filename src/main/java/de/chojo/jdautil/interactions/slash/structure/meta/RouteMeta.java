/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.interactions.slash.structure.meta;

import de.chojo.jdautil.interactions.base.SimpleMeta;
import de.chojo.jdautil.interactions.base.SkuMeta;
import de.chojo.jdautil.interactions.premium.SKU;

import java.util.Collection;
import java.util.List;

public class RouteMeta extends SimpleMeta implements SkuMeta {

    private final List<SKU> skus;

    public RouteMeta(String name, String description, List<SKU> skus) {
        super(name, description);
        this.skus = skus;
    }

    @Override
    public Collection<SKU> sku() {
        return skus;
    }
}
