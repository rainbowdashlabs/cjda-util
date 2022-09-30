/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2022 RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.interactions.slash.structure.builder.argument;

public interface CompletableArgumentBuilder extends ArgumentBuilder {
    @Override
    CompletableArgumentBuilder asRequired();

    CompletableArgumentBuilder withAutoComplete();
}
