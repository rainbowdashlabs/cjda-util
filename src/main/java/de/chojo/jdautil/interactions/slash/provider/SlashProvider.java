/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2022 RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.interactions.slash.provider;

import de.chojo.jdautil.interactions.slash.Slash;

public interface SlashProvider<T extends Slash> {
    T slash();
}
