/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2022 RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.pagination.exceptions;

public class EmptyPageBagException extends RuntimeException {
    public EmptyPageBagException() {
        super("The page bag is empty");
    }

    public EmptyPageBagException(String message) {
        super(message);
    }
}
