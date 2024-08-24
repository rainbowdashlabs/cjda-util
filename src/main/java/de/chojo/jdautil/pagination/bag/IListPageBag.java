/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.pagination.bag;

public interface IListPageBag<T> {
        /**
     * Get the element of the current page
     *
     * @return element from the current page
     */
    T currentElement();
}
