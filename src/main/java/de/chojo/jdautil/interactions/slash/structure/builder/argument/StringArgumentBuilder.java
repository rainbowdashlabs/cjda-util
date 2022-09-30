/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2022 RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.interactions.slash.structure.builder.argument;

public interface StringArgumentBuilder extends CompletableArgumentBuilder {
    @Override
    StringArgumentBuilder asRequired();

    @Override
    StringArgumentBuilder withAutoComplete();

    StringArgumentBuilder minLength(int min);

    StringArgumentBuilder maxLength(int max);

}
