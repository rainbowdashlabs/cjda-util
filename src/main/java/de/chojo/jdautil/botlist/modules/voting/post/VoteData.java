package de.chojo.jdautil.botlist.modules.voting.post;

public class VoteData {
    private final long userId;
    private final boolean bonus;

    public VoteData(long userId, boolean bonus) {
        this.userId = userId;
        this.bonus = bonus;
    }

    public long userId() {
        return userId;
    }

    public boolean isBonus() {
        return bonus;
    }
}
