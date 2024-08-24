/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.pagination.bag;

/**
 * A default page bag which circles through the pages.
 */
public abstract class PageBag implements IPageBag {
    private final int pages;
    private int currentPage;

    public PageBag(int pages) {
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

    @Override
    public void current(int page) {
        if (page < 0 || page >= pages) {
            throw new IndexOutOfBoundsException("The page is not contained in the bag");
        }
        currentPage = page;
    }
}
