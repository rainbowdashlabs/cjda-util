/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2022 RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.buttons;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import de.chojo.jdautil.localization.ILocalizer;

import java.util.function.Consumer;

public interface ButtonServiceModifier {
    ButtonServiceModifier withCache(Cache<Long, ButtonContainer> cache);

    ButtonServiceModifier withCache(Consumer<CacheBuilder<Object, Object>> cache);

    ButtonServiceModifier withLocalizer(ILocalizer localizer);
}
