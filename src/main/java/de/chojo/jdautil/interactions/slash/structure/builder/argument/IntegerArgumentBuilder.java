/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2022 RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.interactions.slash.structure.builder.argument;

public interface IntegerArgumentBuilder extends CompletableArgumentBuilder {

    @Override
    IntegerArgumentBuilder asRequired();

    @Override
    IntegerArgumentBuilder withAutoComplete();

    IntegerArgumentBuilder min(long min);

    IntegerArgumentBuilder max(long max);
}
