/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2022 RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.buttons;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import de.chojo.jdautil.localization.ILocalizer;
import net.dv8tion.jda.api.sharding.ShardManager;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class ButtonServiceBuilder {
    private Cache<Long, ButtonContainer> cache = CacheBuilder.newBuilder().expireAfterAccess(10, TimeUnit.MINUTES).build();
    private ILocalizer localizer = ILocalizer.DEFAULT;
    private final ShardManager shardManager;

    public ButtonServiceBuilder(ShardManager shardManager) {
        this.shardManager = shardManager;
    }

    public ButtonServiceBuilder withCache(Cache<Long, ButtonContainer> cache) {
        this.cache = cache;
        return this;
    }
    public ButtonServiceBuilder withCache(Consumer<CacheBuilder<Object, Object>> cache) {
        var builder = CacheBuilder.newBuilder();
        cache.accept(builder);
        this.cache = builder.build();
        return this;
    }

    public ButtonServiceBuilder withLocalizer(ILocalizer localizer) {
        this.localizer = localizer;
        return this;
    }

    public ButtonService build() {
        var buttonService = new ButtonService(cache, localizer);
        shardManager.addEventListener(buttonService);
        return buttonService;
    }
}
