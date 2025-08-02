/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.functions;

public interface ThrowingFunction<R, T, E extends Exception> {
    R apply(T t) throws E;
}
