/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2022 RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.pagination.bag;

import de.chojo.jdautil.pagination.exceptions.EmptyPageBagException;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.requests.restaction.MessageEditAction;
import net.dv8tion.jda.api.utils.messages.MessageEditData;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface IPageBag {
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
