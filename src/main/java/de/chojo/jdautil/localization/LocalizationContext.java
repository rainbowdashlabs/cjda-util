/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2022 RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.localization;

import de.chojo.jdautil.localization.util.LocaleProvider;
import de.chojo.jdautil.localization.util.Replacement;

public record LocalizationContext(ILocalizer localizer, LocaleProvider provider) {

    /**
     * Localize a message in the given guild or private context.
     *
     * @param message      message
     * @param replacements replacements
     * @return localized message
     */
    public String localize(String message, Replacement... replacements) {
        return localizer.localize(message, provider, replacements);
    }

    /**
     * The wrapped localizer instance.
     *
     * @return localizer
     */
    @Override
    public ILocalizer localizer() {
        return localizer;
    }
}
