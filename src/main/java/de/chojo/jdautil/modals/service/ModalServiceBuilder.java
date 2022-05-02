/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2022 RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.modals.service;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import de.chojo.jdautil.localization.ILocalizer;
import de.chojo.jdautil.modals.handler.ModalHandler;
import net.dv8tion.jda.api.sharding.ShardManager;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class ModalServiceBuilder implements ModalServiceModifier {
    private ILocalizer localizer = ILocalizer.DEFAULT;
    private Cache<String, ModalHandler> handlers = CacheBuilder.newBuilder().expireAfterAccess(30, TimeUnit.MINUTES).build();
    private final ShardManager shardManager;


    public ModalServiceBuilder(ShardManager shardManager) {
        this.shardManager = shardManager;
    }

    @Override
    public ModalServiceBuilder withLocalizer(ILocalizer localizer) {
        this.localizer = localizer;
        return this;
    }

    @Override
    public ModalServiceBuilder setCache(Cache<String, ModalHandler> cache) {
        this.handlers = cache;
        return this;
    }

    @Override
    public ModalServiceBuilder withCache(Consumer<CacheBuilder<Object, Object>> cache) {
        var builder = CacheBuilder.newBuilder();
        cache.accept(builder);
        this.handlers = builder.build();
        return this;
    }

    /**
     * Builds and registers the service at the provided {@link ShardManager}.
     *
     * @return a new modal service instance
     */
    public ModalService build() {
        var service = new ModalService(localizer, handlers);
        shardManager.addEventListener(service);
        return service;
    }
}
