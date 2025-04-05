/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2022 RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.interactions.slash.structure.meta;

import de.chojo.jdautil.interactions.base.EntitlementMeta;
import de.chojo.jdautil.interactions.base.SimpleMeta;
import net.dv8tion.jda.api.entities.Entitlement;

import java.util.Collection;
import java.util.List;

public class RouteMeta extends SimpleMeta implements EntitlementMeta {

    private final List<Entitlement> entitlements;

    public RouteMeta(String name, String description, List<Entitlement> entitlements) {
        super(name, description);
        this.entitlements = entitlements;
    }

    @Override
    public Collection<Entitlement> entitlements() {
        return entitlements;
    }
}
