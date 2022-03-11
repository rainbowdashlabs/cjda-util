package de.chojo.jdautil.pagination.bag;

import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;

import java.util.concurrent.CompletableFuture;

public interface IPageBag {
    /**
     * The amount of pages.
     *
     * @return Page count
     */
    int pages();

    /**
     * Get the current index of the page. The index is zero based
     *
     * @return the current page index. Zero based
     */
    int current();

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
     * Build the embed for the page.
     *
     * @return A {@link CompletableFuture} providing the message embed.
     */
    CompletableFuture<MessageEmbed> buildPage();

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
