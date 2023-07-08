package de.chojo.jdautil.pagination.bag;

public interface IListPageBag<T> {
        /**
     * Get the element of the current page
     *
     * @return element from the current page
     */
    T currentElement();
}
