/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2022 RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.buttons;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import de.chojo.jdautil.localization.ILocalizer;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class ButtonServiceBuilder {
    private Cache<Long, ButtonContainer> cache = CacheBuilder.newBuilder().expireAfterAccess(10, TimeUnit.MINUTES).build();
    private ILocalizer localizer = ILocalizer.DEFAULT;

    public ButtonServiceBuilder setCache(Cache<Long, ButtonContainer> cache) {
        this.cache = cache;
        return this;
    }
    public ButtonServiceBuilder setCache(Consumer<CacheBuilder<Object, Object>> cache) {
        var builder = CacheBuilder.newBuilder();
        cache.accept(builder);
        this.cache = builder.build();
        return this;
    }

    public ButtonServiceBuilder setLocalizer(ILocalizer localizer) {
        this.localizer = localizer;
        return this;
    }

    public ButtonService build() {
        return new ButtonService(cache, localizer);
    }
}
