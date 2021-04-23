package de.chojo.jdautil.parsing;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.sharding.ShardManager;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static de.chojo.jdautil.parsing.Verifier.getIdRaw;
import static de.chojo.jdautil.parsing.Verifier.isValidId;

/**
 * Class which can be used to resolve different discord entities from strings.
 */
public class DiscordResolver {
    private static final Pattern DISCORD_TAG = Pattern.compile(".+?#[0-9]{4}");
    private final ShardManager shardManager;

    /**
     * Create a new argument parser.
     */
    public DiscordResolver(ShardManager shardManager) {
        this.shardManager = shardManager;
    }

    private static Optional<Member> byNameOnGuild(String memberString, Guild guild) {
        var collect = guild.getMembers().stream()
                .filter(m -> m.getEffectiveName().toLowerCase()
                        .startsWith(memberString.toLowerCase())).findFirst();
        if (collect.isPresent()) {
            return collect;
        }
        return guild.getMembers().stream().filter(m -> m.getUser().getName().toLowerCase()
                .startsWith(memberString.toLowerCase())).findFirst();
    }

    /**
     * Get a text channels by a list of id or name.
     *
     * @param guild         guild for lookup
     * @param channelString id or name
     *
     * @return text channel or null
     */
    public static Optional<TextChannel> getTextChannel(Guild guild, String channelString) {
        if (channelString == null) {
            return Optional.empty();
        }

        var textChannel = byId(channelString, guild::getTextChannelById);

        if (textChannel == null) {
            textChannel = byName(channelString, s -> guild.getTextChannelsByName(s, true));
        }
        return Optional.ofNullable(textChannel);
    }


    private static <T> T byId(String id, Function<String, T> convert) {
        if (isValidId(id)) {
            return convert.apply(getIdRaw(id).orElse("0"));
        }
        return null;
    }

    private static <T> T byName(String name, Function<String, List<T>> convert) {
        var nameMatches = convert.apply(name);
        if (nameMatches.isEmpty()) {
            return null;
        }
        return nameMatches.get(0);
    }

    /**
     * Returns from a list of role ids all valid roles.
     *
     * @param guild guild for role lookup
     * @param args  array of role id
     *
     * @return list of valid roles
     */
    public static List<Role> getValidRoles(Guild guild, List<String> args) {
        return getValidRoles(guild, args.toArray(String[]::new));
    }

    /**
     * Returns from a list of role ids all valid roles.
     *
     * @param guild guild for role lookup
     * @param args  array of role ids
     *
     * @return list of valid roles
     */
    public static List<Role> getValidRoles(Guild guild, String[] args) {
        return Arrays.stream(args).map(roleId -> guild.getRoleById(getIdRaw(roleId).orElse("0")))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * Returns from a list of text channels ids all valid channels.
     *
     * @param guild guild for role lookup
     * @param args  array of role id
     *
     * @return list of valid roles
     */
    public static List<TextChannel> getValidTextChannels(Guild guild, List<String> args) {
        return getValidTextChannels(guild, args.toArray(String[]::new));
    }

    /**
     * Returns from a list of text channel ids all valid channels.
     *
     * @param guild guild for channel lookup
     * @param args  array of channel ids
     *
     * @return list of valid channels
     */
    public static List<TextChannel> getValidTextChannels(Guild guild, String[] args) {
        return Arrays.stream(args).map(channelId -> guild.getTextChannelById(getIdRaw(channelId).orElse("0")))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * Returns from a list of text channel ids all valid channels.
     *
     * @param guild guild for channel lookup
     * @param ids   array of channel ids
     *
     * @return list of valid channels
     */
    public static List<TextChannel> getValidTextChannelsById(Guild guild, List<Long> ids) {
        return ids.stream().map(guild::getTextChannelById)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * Get the valid users by id.
     *
     * @param shardManager jda for user lookup
     * @param collect      list of long ids
     *
     * @return list of valid users.
     */
    public static List<User> getValidUserByLong(ShardManager shardManager, List<Long> collect) {
        return collect.stream()
                .map(shardManager::getUserById)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public static Optional<GuildChannel> getGuildChannel(Guild guild, String content) {
        GuildChannel channel = byId(content, guild::getTextChannelById);
        if (channel != null) {
            return Optional.of(channel);
        }
        return Optional.ofNullable(byId(content, guild::getVoiceChannelById));
    }

    /**
     * Get a user object by id, tag or name.
     *
     * @param guild      guild for lookup
     * @param userString string for lookup
     *
     * @return user object if user is present or null
     */
    public static Optional<User> getGuildUser(Guild guild, String userString) {
        return getGuildMember(guild, userString).map(Member::getUser);
    }

    /**
     * Get a member object from a guild member by id, tag, nickname or effective name.
     *
     * @param guild        guild for lookup
     * @param memberString string for lookup
     *
     * @return member object of member is present or null
     */
    public static Optional<Member> getGuildMember(Guild guild, String memberString) {
        if (memberString == null) {
            return Optional.empty();
        }
        //Lookup by id
        var foundUser = byId(memberString, guild::getMemberById);

        //Lookup by tag
        if (foundUser == null && DISCORD_TAG.matcher(memberString).matches()) {
            foundUser = guild.getMemberByTag(memberString);
        }

        //lookup by nickname
        if (foundUser == null) {
            foundUser = byName(memberString, s -> guild.getMembersByNickname(s, true));
        }

        //lookup by effective name
        if (foundUser == null) {
            foundUser = byName(memberString, s -> guild.getMembersByEffectiveName(s, true));
        }

        //lookup by name
        if (foundUser == null) {
            foundUser = byName(memberString, s -> guild.getMembersByName(s, true));
        }

        if (foundUser == null) {
            return byNameOnGuild(memberString, guild);
        }

        return Optional.of(foundUser);
    }

    /**
     * Searches for a user. First on a guild and after this on all users the bot currently know. Equal to calling {@link
     * #getGuildUser(Guild, String)} and {@link #getUser(String)}.
     *
     * @param userString string for lookup
     * @param guild      guild for lookup
     *
     * @return user object or null if no user is found
     */
    public Optional<User> getUserDeepSearch(String userString, Guild guild) {
        var user = getGuildUser(guild, userString);
        return user.or(() -> getUser(userString));
    }

    /**
     * Get a user object by id, name or tag.
     *
     * @param userString string for lookup
     *
     * @return user object or null if no user is found
     */
    public Optional<User> getUser(String userString) {
        if (userString == null) {
            return Optional.empty();
        }

        User user = null;
        var idRaw = getIdRaw(userString);
        if (idRaw.isPresent()) {
            user = byId(userString, shardManager::getUserById);
        }

        if (user == null && DISCORD_TAG.matcher(userString).matches()) {
            user = shardManager.getUserByTag(userString);
        }

        if (user == null) {
            return shardManager.getUserCache().stream()
                    .filter(cu -> cu.getName().toLowerCase().startsWith(userString.toLowerCase())).findFirst();
        }
        return Optional.of(user);
    }

    /**
     * Get a text channels by a list of ids and/or names.
     *
     * @param guild          guild for lookup
     * @param channelStrings list of ids and/or names
     *
     * @return list of text channels without null objects
     */
    public static List<TextChannel> getTextChannels(Guild guild, Collection<String> channelStrings) {
        return channelStrings.stream().map(s -> getTextChannel(guild, s))
                .filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList());
    }

    /**
     * Get roles from a list of role ids and/or names.
     *
     * @param guild       guild for lookup
     * @param roleStrings list of ids and/or names
     *
     * @return list of roles. without null objects
     */
    public static List<Role> getRoles(Guild guild, Collection<String> roleStrings) {
        return roleStrings.stream().map(roleString -> getRole(guild, roleString))
                .filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList());
    }

    /**
     * Get a role from id or name.
     *
     * @param guild      guild for lookup
     * @param roleString id or name of role
     *
     * @return role object or null
     */
    public static Optional<Role> getRole(Guild guild, String roleString) {
        if (roleString == null) {
            return Optional.empty();
        }

        var role = byId(roleString, guild::getRoleById);

        if (role == null) {
            var roles = guild.getRolesByName(roleString, true);
            if (!roles.isEmpty()) {
                role = roles.get(0);
            }
        }
        return Optional.ofNullable(role);
    }

    /**
     * Get user objects from a list of ids, names and/or tags.
     *
     * @param userStrings list of ids, names and/or tags
     *
     * @return a list of user objects. without null
     */
    public List<User> getUsers(Collection<String> userStrings) {
        return userStrings.stream()
                .map(this::getUser)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    /**
     * Get guild member objects by a list of ids, tags, nickname or effective name.
     *
     * @param guild         guild for lookup
     * @param memberStrings list of member ids, tags, nicknames or effective names
     *
     * @return list of member object without nulls
     */
    public static List<Member> getGuildMembers(Guild guild, Collection<String> memberStrings) {
        return memberStrings.stream()
                .map(memberString -> getGuildMember(guild, memberString))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    /**
     * Get user objects which are valid on the guild by a list of ids, tags, or name.
     *
     * @param guild       guild for lookup
     * @param userStrings list of user ids, names and/or tags
     *
     * @return list of users without null
     */
    public static List<User> getGuildUsers(Guild guild, Collection<String> userStrings) {
        return userStrings.stream()
                .map(memberString -> getGuildUser(guild, memberString))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    /**
     * Get guilds by ids or names.
     *
     * @param guildStrings list of ids and/or names.
     *
     * @return list of guilds. without null
     */
    public List<Guild> getGuilds(List<String> guildStrings) {
        return guildStrings.stream()
                .map(this::getGuild)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    /**
     * Get guilds by id or name.
     *
     * @param guildString guild id or name
     *
     * @return guild object or null
     */
    public Optional<Guild> getGuild(String guildString) {
        var guild = byId(guildString, s -> shardManager.getGuildById(guildString));

        if (guild == null) {
            var guilds = shardManager.getGuildsByName(guildString, false);
            if (!guilds.isEmpty()) {
                guild = guilds.get(0);
            }
        }

        if (guild == null) {
            return shardManager.getGuildCache().stream()
                    .filter(g -> g.getName().toLowerCase().startsWith(guildString.toLowerCase())).findFirst();
        }
        return Optional.of(guild);
    }

    /**
     * Search a user by fuzzy search.
     *
     * @param userString user string to search
     *
     * @return a list of users. if a direct match was found only
     */
    public List<User> fuzzyGlobalUserSearch(String userString) {
        if (userString == null) {
            return null;
        }

        var user = byId(userString, shardManager::getUserById);
        if (user != null) {
            return Collections.singletonList(user);
        }

        var idRaw = getIdRaw(userString);
        if (idRaw.isPresent()) {
            user = shardManager.getUserById(idRaw.get());
            if (user != null) {
                return Collections.singletonList(user);
            }
        }

        if (DISCORD_TAG.matcher(userString).matches()) {
            user = shardManager.getUserByTag(userString);
            if (user != null) {
                return Collections.singletonList(user);
            }
        }

        return shardManager.getUserCache().stream()
                .filter(cu -> cu.getName().toLowerCase().contains(userString.toLowerCase()))
                .collect(Collectors.toList());
    }

    /**
     * Search a user by fuzzy search on a guild.
     *
     * @param userString user string to search
     * @param guild      guild to search
     *
     * @return a list of users. if a direct match was found only 1 user. if guild id is invalid a empty list is
     * returned.
     */

    public static List<WeightedEntry<Member>> fuzzyGuildUserSearch(Guild guild, String userString) {
        if (guild == null) {
            return Collections.emptyList();
        }
        //Lookup by id
        var foundUser = byId(userString, guild::getMemberById);
        if (foundUser != null) {
            return Collections.singletonList(WeightedEntry.directMatch(foundUser));
        }

        //Lookup by tag
        if (DISCORD_TAG.matcher(userString).matches()) {
            foundUser = guild.getMemberByTag(userString);
            if (foundUser != null) {
                return Collections.singletonList(WeightedEntry.directMatch(foundUser));
            }
        }

        //lookup by nickname
        foundUser = byName(userString, s -> guild.getMembersByNickname(s, true));
        if (foundUser != null) {
            return Collections.singletonList(WeightedEntry.directMatch(foundUser));
        }

        //lookup by effective name
        foundUser = byName(userString, s -> guild.getMembersByEffectiveName(s, true));
        if (foundUser != null) {
            return Collections.singletonList(WeightedEntry.directMatch(foundUser));
        }

        //lookup by name
        foundUser = byName(userString, s -> guild.getMembersByName(s, true));
        if (foundUser != null) {
            return Collections.singletonList(WeightedEntry.directMatch(foundUser));
        }

        return guild.getMemberCache().stream().map(m -> WeightedEntry.withJaro(m, userString))
                .sorted(Comparator.reverseOrder())
                .limit(10)
                .collect(Collectors.toList());
    }

}
