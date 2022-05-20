/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2022 RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.menus;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import de.chojo.jdautil.localization.ILocalizer;
import net.dv8tion.jda.api.sharding.ShardManager;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class MenuServiceBuilder implements MenuServiceModifier {
    private final ShardManager shardManager;
    private Cache<Long, MenuContainer> cache = CacheBuilder.newBuilder().expireAfterAccess(10, TimeUnit.MINUTES).build();
    private ILocalizer localizer = ILocalizer.DEFAULT;

    public MenuServiceBuilder(ShardManager shardManager) {
        this.shardManager = shardManager;
    }

    @Override
    public MenuServiceModifier withCache(Cache<Long, MenuContainer> cache) {
        this.cache = cache;
        return this;
    }

    @Override
    public MenuServiceModifier withCache(Consumer<CacheBuilder<Object, Object>> cache) {
        var builder = CacheBuilder.newBuilder();
        cache.accept(builder);
        this.cache = builder.build();
        return this;
    }

    @Override
    public MenuServiceModifier withLocalizer(ILocalizer localizer) {
        this.localizer = localizer;
        return this;
    }

    public MenuService build() {
        var buttonService = new MenuService(cache, localizer);
        shardManager.addEventListener(buttonService);
        return buttonService;
    }
}
