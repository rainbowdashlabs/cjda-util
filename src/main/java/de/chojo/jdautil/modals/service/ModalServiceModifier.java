/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2022 RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.modals.service;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import de.chojo.jdautil.interactions.dispatching.InteractionHub;
import de.chojo.jdautil.localization.ILocalizer;
import de.chojo.jdautil.modals.handler.ModalHandler;

import java.util.function.Consumer;

public interface ModalServiceModifier {
    /**
     * Set the {@link ILocalizer} for the {@link ModalService}. This is not required when the service is linked to a {@link InteractionHub}.
     *
     * @param localizer localizer instance
     * @return builder instance
     */
    ModalServiceBuilder withLocalizer(ILocalizer localizer);

    /**
     * Set the cache implementation for the modal service
     *
     * @param cache cache
     * @return builder instance
     */
    ModalServiceBuilder setCache(Cache<String, ModalHandler> cache);

    /**
     * Modifies a provided cache builder instance.
     *
     * @param cache cache
     * @return builder instance
     */
    ModalServiceBuilder withCache(Consumer<CacheBuilder<Object, Object>> cache);
}
