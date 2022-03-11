/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2022 RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.pagination.bag;

import de.chojo.jdautil.pagination.exceptions.EmptyPageBagException;

/**
 * A default page bag which circles through the pages.
 */
public abstract class PageBag implements IPageBag {
    private final int pages;
    private int currentPage = 0;

    public PageBag(int pages) {
        if (pages == 0) throw new EmptyPageBagException();
        this.pages = pages;
    }

    @Override
    public int pages() {
        return pages;
    }

    @Override
    public int current() {
        return currentPage;
    }

    @Override
    public void scrollNext() {
        if (currentPage + 1 == pages) {
            currentPage = 0;
        } else {
            currentPage++;
        }
    }

    @Override
    public boolean hasNext() {
        return true;
    }

    @Override
    public void scrollPrevious() {
        if (currentPage == 0) {
            currentPage = pages - 1;
        } else {
            currentPage--;
        }
    }

    @Override
    public boolean hasPrevious() {
        return true;
    }
}
