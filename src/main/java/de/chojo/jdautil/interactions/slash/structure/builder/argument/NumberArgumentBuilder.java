/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.interactions.slash.structure.builder.argument;

public interface NumberArgumentBuilder extends IntegerArgumentBuilder {

    @Override
    NumberArgumentBuilder asRequired();

    @Override
    NumberArgumentBuilder withAutoComplete();

    @Override
    NumberArgumentBuilder min(long min);

    @Override
    NumberArgumentBuilder max(long max);

    NumberArgumentBuilder min(double min);

    NumberArgumentBuilder max(double max);
}
