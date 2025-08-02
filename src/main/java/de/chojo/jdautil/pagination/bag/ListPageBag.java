/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.pagination.bag;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public abstract class ListPageBag<T> extends PageBag implements IListPageBag<T> {
    private final List<T> content;

    public ListPageBag(Collection<T> content) {
        super(content.size());
        this.content = new LinkedList<>(content);
    }

    /**
     * Get the element of the current page
     *
     * @return element from the current page
     */
    @Override
    public T currentElement() {
        return content.get(current());
    }
}
