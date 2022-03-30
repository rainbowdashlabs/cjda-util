/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2022 RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.pagination;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import de.chojo.jdautil.localization.ILocalizer;
import de.chojo.jdautil.pagination.bag.IPageBag;
import net.dv8tion.jda.api.sharding.ShardManager;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class PageServiceBuilder {
    private final ShardManager shardManager;
    private String previousLabel = "pageService:previous";
    private String nextLabel = "pageService:next";
    private String previousText = "Previous";
    private String nextText = "Next";
    private Cache<Long, IPageBag> cache = CacheBuilder.newBuilder().expireAfterAccess(10, TimeUnit.MINUTES).build();
    private ILocalizer localizer = ILocalizer.DEFAULT;

    public PageServiceBuilder(ShardManager shardManager) {
        this.shardManager = shardManager;
    }

    /**
     * Sets the button label of the previous button.
     *
     * @param label button label
     * @return builder instance
     */
    public PageServiceBuilder previousLabel(String label) {
        this.previousLabel = label;
        return this;
    }

    /**
     * Sets the button label of the next button.
     *
     * @param label button label
     * @return builder instance
     */
    public PageServiceBuilder nextLabel(String label) {
        this.nextLabel = label;
        return this;
    }

    /**
     * Sets the text of the previous button
     *
     * @param text text
     * @return builder instance
     */
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
        var service = new PageService(previousLabel, nextLabel, previousText, nextText, cache, localizer);
        shardManager.addEventListener(service);
        return service;
    }
}
