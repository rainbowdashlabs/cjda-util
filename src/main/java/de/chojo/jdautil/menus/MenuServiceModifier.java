/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.menus;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import de.chojo.jdautil.localization.ILocalizer;

import java.util.function.Consumer;

public interface MenuServiceModifier {
    MenuServiceModifier withCache(Cache<Long, MenuContainer> cache);

    MenuServiceModifier withCache(Consumer<CacheBuilder<Object, Object>> cache);

    MenuServiceModifier withLocalizer(ILocalizer localizer);
}
