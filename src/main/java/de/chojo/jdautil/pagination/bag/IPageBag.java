/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.pagination.bag;

import de.chojo.jdautil.pagination.exceptions.EmptyPageBagException;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.utils.messages.MessageEditData;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Supplier;

public interface IPageBag {

    static IPageBag standard(int pages, Supplier<MessageEditData> embed){
        return new PageBag(pages) {
            @Override
            public CompletableFuture<MessageEditData> buildPage() {
                return CompletableFuture.completedFuture(embed.get());
            }
        };
    }
    static IPageBag standard(int pages, Function<IPageBag,MessageEditData> embed, Function<IPageBag,MessageEditData> empty){
        return new PageBag(pages) {
            @Override
            public CompletableFuture<MessageEditData> buildPage() {
                return CompletableFuture.completedFuture(embed.apply(this));
            }

            @Override
            public CompletableFuture<MessageEditData> buildEmptyPage() {
                return CompletableFuture.completedFuture(empty.apply(this));
            }
        };
    }

    static <T>IPageBag list(List<T> pages, Function<ListPageBag<T>, MessageEditData> embed){
        return new ListPageBag<>(pages) {
            @Override
            public CompletableFuture<MessageEditData> buildPage() {
                return CompletableFuture.completedFuture(embed.apply(this));
            }
        };
    }

    static <T>IPageBag list(List<T> pages, Function<ListPageBag<T>, MessageEditData> embed, Function<ListPageBag<T>, MessageEditData> empty){
        return new ListPageBag<>(pages) {
            @Override
            public CompletableFuture<MessageEditData> buildPage() {
                return CompletableFuture.completedFuture(embed.apply(this));
            }

            @Override
            public CompletableFuture<MessageEditData> buildEmptyPage() {
                return CompletableFuture.completedFuture(empty.apply(this));
            }
        };
    }

    /**
     * The amount of pages.
     *
     * @return Page count
     */
    int pages();

    /**
     * Checks if the page count is 0.
     *
     * @return true if {@link #pages()} returns 0
     */
    default boolean isEmpty() {
        return pages() == 0;
    }

    /**
     * Get the current index of the page. The index is zero based
     *
     * @return the current page index. Zero based
     */
    int current();

    /**
     * Set the current index of the page. The index is zero based
     *
     * @param page the current page index. Zero based
     */
    void current(int page);

    /**
     * Goes to the next page index
     */
    void scrollNext();

    /**
     * Checks if there is a next page
     *
     * @return true when there is a next page
     */
    boolean hasNext();

    /**
     * Goes to the previous page index
     */
    void scrollPrevious();

    /**
     * Checks if there is a previous page
     *
     * @return true when there is a previous page
     */
    boolean hasPrevious();

    /**
     * Get additional buttons which should be added to the page
     *
     * @return list fo buttons
     */
    default List<PageButton> buttons() {
        return Collections.emptyList();
    }

    /**
     * Build the embed for the page.
     *
     * @return A {@link CompletableFuture} providing the message embed.
     */
    CompletableFuture<MessageEditData> buildPage();

    /**
     * Build an embed for an empty page when the bag is empty
     *
     * @return A {@link CompletableFuture} providing the message embed.
     * @throws EmptyPageBagException when not overridden.
     */
    default CompletableFuture<MessageEditData> buildEmptyPage() {
        throw new EmptyPageBagException("The provided page bag is empty. Escape empty page submission or implement IPageBag#buildEmptyPage()");
    }

    /**
     * Defines if a user can interact with this page or not.
     *
     * @param user user to check
     * @return true if the user can interact
     */
    default boolean canInteract(User user) {
        return true;
    }
}
