package de.chojo.jdautil.pagination.bag;

import net.dv8tion.jda.api.entities.User;

/**
 * A default page bag which circles through the pages with ownership.
 */
public abstract class PrivatePageBag extends PageBag {
    private final long ownerId;

    public PrivatePageBag(int pages, long ownerId) {
        super(pages);
        this.ownerId = ownerId;
    }

    @Override
    public boolean canInteract(User user) {
        return user.getIdLong() == ownerId;
    }
}
