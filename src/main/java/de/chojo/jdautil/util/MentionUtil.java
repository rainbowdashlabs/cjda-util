package de.chojo.jdautil.util;

public final class MentionUtil {
    public static final String USER_DECORATOR = "@";
    public static final String MEMBER_DECORATOR = "@!";
    public static final String CHANNEL_DECORATOR = "#";
    public static final String ROLE_DECORATOR = "@&";

    private MentionUtil() {
        throw new UnsupportedOperationException("This is a utility class.");
    }

    /**
     * Get a mention for a user.
     *
     * @param id id of user
     * @return user mention
     */
    public static String user(long id) {
        return mention(USER_DECORATOR, id);
    }

    /**
     * Get a mention of a member.
     *
     * @param id id of member
     * @return member mention
     */
    public static String member(long id) {
        return mention(MEMBER_DECORATOR, id);
    }

    /**
     * Get a mention of a channel
     *
     * @param id id of channel
     * @return channel mention
     */
    public static String channel(long id) {
        return mention(CHANNEL_DECORATOR, id);
    }

    /**
     * Get a mention of a role
     *
     * @param id id of role
     * @return role mention
     */
    public static String role(long id) {
        return mention(ROLE_DECORATOR, id);
    }

    /**
     * Get a mention of an id with an decorator
     *
     * @param decorator decorator for mention. indicates the mention type.
     * @param id        id to mention
     * @return id as mention
     */
    public static String mention(String decorator, long id) {
        return String.format("<%s%s>", decorator, id);
    }
}
