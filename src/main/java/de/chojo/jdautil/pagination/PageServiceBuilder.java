/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.pagination;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import de.chojo.jdautil.localization.ILocalizer;
import de.chojo.jdautil.pagination.bag.IPageBag;
import net.dv8tion.jda.api.sharding.ShardManager;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class PageServiceBuilder implements PageServiceModifier {
    private final ShardManager shardManager;
    private String previousId = "pageService:previous";
    private String nextId = "pageService:next";
    private String previousText = "Previous";
    private String nextText = "Next";
    private Cache<Long, IPageBag> cache =
            CacheBuilder.newBuilder().expireAfterAccess(10, TimeUnit.MINUTES).build();
    private ILocalizer localizer = ILocalizer.DEFAULT;

    public PageServiceBuilder(ShardManager shardManager) {
        this.shardManager = shardManager;
    }

    /**
     * Sets the custom id of the previous button.
     *
     * @param id custom id
     * @return builder instance
     */
    @Override
    public PageServiceBuilder previousId(String id) {
        this.previousId = id;
        return this;
    }

    /**
     * Sets the custom id of the next button.
     *
     * @param id custom id
     * @return builder instance
     */
    @Override
    public PageServiceBuilder nextId(String id) {
        this.nextId = id;
        return this;
    }

    /**
     * Sets the text of the previous button
     *
     * @param text text
     * @return builder instance
     */
    @Override
    public PageServiceBuilder previousText(String text) {
        this.previousText = text;
        return this;
    }

    /**
     * Sets the text of the next button
     *
     * @param text text
     * @return builder instance
     */
    @Override
    public PageServiceBuilder nextText(String text) {
        this.nextText = text;
        return this;
    }

    /**
     * Sets the cache used to cache registered pages.
     *
     * @param cache cache instance
     * @return builder instance
     */
    @Override
    public PageServiceBuilder withCache(Cache<Long, IPageBag> cache) {
        this.cache = cache;
        return this;
    }

    /**
     * Modify a new cache builder which will be used to build the underlying cache to cache registered pages.
     *
     * @param cache cache builder instance
     * @return builder instance
     */
    @Override
    public PageServiceBuilder withCache(Consumer<CacheBuilder<Object, Object>> cache) {
        var builder = CacheBuilder.newBuilder();
        cache.accept(builder);
        this.cache = builder.build();
        return this;
    }

    /**
     * Defined the localizer to be used to localize the {@link #previousText} and {@link #nextText}
     *
     * @param localizer localizer instance
     * @return builder instance
     */
    @Override
    public PageServiceBuilder withLocalizer(ILocalizer localizer) {
        this.localizer = localizer;
        return this;
    }

    /**
     * Builds and registers the service at the provided {@link ShardManager}.
     *
     * @return a new page service instance
     */
    public PageService build() {
        var service = new PageService(previousId, nextId, previousText, nextText, cache, localizer);
        shardManager.addEventListener(service);
        return service;
    }
}
