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

import java.util.function.Consumer;

public interface PageServiceModifier {
    /**
     * Sets the custom id of the previous button.
     *
     * @param id custom id
     * @return builder instance
     */
    PageServiceModifier previousId(String id);

    /**
     * Sets the custom id of the next button.
     *
     * @param id custom id
     * @return builder instance
     */
    PageServiceModifier nextId(String id);

    /**
     * Sets the text of the previous button
     *
     * @param text text
     * @return builder instance
     */
    PageServiceModifier previousText(String text);

    /**
     * Sets the text of the next button
     *
     * @param text text
     * @return builder instance
     */
    PageServiceModifier nextText(String text);

    /**
     * Sets the cache used to cache registered pages.
     *
     * @param cache cache instance
     * @return builder instance
     */
    PageServiceModifier withCache(Cache<Long, IPageBag> cache);

    /**
     * Modify a new cache builder which will be used to build the underlying cache to cache registered pages.
     *
     * @param cache cache builder instance
     * @return builder instance
     */
    PageServiceModifier withCache(Consumer<CacheBuilder<Object, Object>> cache);

    /**
     * Defined the localizer to be used to localize the {@link #previousText} and {@link #nextText}
     *
     * @param localizer localizer instance
     * @return builder instance
     */
    PageServiceModifier withLocalizer(ILocalizer localizer);
}
