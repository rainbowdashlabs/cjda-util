package de.chojo.jdautil.wrapper;

public class UserMessageKey {
    private final long user;
    private final long message;

    public UserMessageKey(long user, long message) {
        this.user = user;
        this.message = message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserMessageKey that = (UserMessageKey) o;

        if (user != that.user) return false;
        return message == that.message;
    }

    @Override
    public int hashCode() {
        int result = (int) (user ^ (user >>> 32));
        result = 31 * result + (int) (message ^ (message >>> 32));
        return result;
    }
}
