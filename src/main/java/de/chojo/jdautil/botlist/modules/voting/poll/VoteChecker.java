package de.chojo.jdautil.botlist.modules.voting.poll;

import net.dv8tion.jda.api.entities.User;

public interface VoteChecker {
    boolean hasVoted(User user);
}
