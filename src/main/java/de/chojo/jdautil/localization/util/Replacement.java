/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2022 RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.localization.util;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import net.dv8tion.jda.api.entities.IMentionable;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

public final class Replacement {
    private final String key;
    private String value;
    private boolean caseSensitive;

    private Replacement(String key, String value) {
        this.key = key;
        this.value = value;
    }

    /**
     * Creates a new replacement.
     *
     * @param key     key of replacement
     * @param value   value for replacement
     * @param formats format which should be applied on the replacement.
     * @return replacement with registered replacement
     */
    public static Replacement create(String key, String value, Format... formats) {
        var replacement = new Replacement("%%%s%%".formatted(key), value);
        return replacement.addFormatting(formats);
    }

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public static Replacement deserialize(@JsonProperty("key") String key,
                                          @JsonProperty("value") String value,
                                          @JsonProperty("caseSensitive") boolean caseSensitive) {
        if (caseSensitive) {
            return new Replacement(key, value).matchCase();
        }
        return new Replacement(key, value);
    }

    /**
     * Creates a new replacement.
     *
     * @param key     key of replacement
     * @param value   value which provides a string via {@link Object#toString()}
     * @param formats format which should be applied on the replacement.
     * @return replacement with registered replacement
     */
    public static Replacement create(String key, Object value, Format... formats) {
        return create(key, value.toString(), formats);
    }

    /**
     * Creates a new replacement.
     *
     * @param key     key of replacement
     * @param value   value which provides the name of the player
     * @param formats format which should be applied on the replacement.
     * @return replacement with registered replacement
     */
    public static Replacement create(String key, User value, Format... formats) {
        return create(key, value.getName(), formats);
    }

    /**
     * Creates a new replacement.
     *
     * @param value value which provides the name of the player
     * @return replacement with registered replacement
     */
    public static Replacement createMention(Role value) {
        return create("ROLE", value.getAsMention());
    }

    /**
     * Creates a new replacement.
     *
     * @param value value which provides the name of the player
     * @return replacement with registered replacement
     */
    public static Replacement createMention(User value) {
        return create("USER", value.getAsMention());
    }

    /**
     * Creates a new replacement.
     *
     * @param value value which provides the name of the player
     * @return replacement with registered replacement
     */
    public static Replacement createMention(Member value) {
        return create("USER", value.getAsMention());
    }

    /**
     * Creates a new replacement.
     *
     * @param value value which provides the name of the player
     * @return replacement with registered replacement
     */
    public static Replacement createMention(String key, IMentionable value) {
        return create(key, value.getAsMention());
    }

    /**
     * Creates a new replacement.
     *
     * @param value value which provides the name of the player
     * @return replacement with registered replacement
     */
    public static Replacement createMention(TextChannel value) {
        return create("CHANNEL", value.getAsMention());
    }


    /**
     * Add formatting codes to the replacement. A §r will be appended after the replacement. Only provide the formatting
     * character. Without § or &amp;.
     *
     * @param formats formats which should be applied on the replacement.
     * @return replacement with formatting set
     */
    public Replacement addFormatting(Format... formats) {
        for (var format : formats) {
            value = format.apply(value);
        }
        return this;
    }

    /**
     * Set the replacement to ignore case of placeholder value
     *
     * @return Replacement with value changed
     */
    public Replacement matchCase() {
        this.caseSensitive = false;
        return this;
    }


    /**
     * Invoke the replacement on the string.
     *
     * @param string string to replace
     * @return string with key replaced by value.
     */
    public String invoke(String string) {
        if (!caseSensitive) {
            return string.replaceAll("(?i)" + key, "{PL}").replace("{PL}", value);
        }
        return string.replace(key, value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Replacement that)) return false;

        if (caseSensitive != that.caseSensitive) return false;
        if (!key.equals(that.key)) return false;
        return value.equals(that.value);
    }

    @Override
    public int hashCode() {
        int result = key.hashCode();
        result = 31 * result + value.hashCode();
        result = 31 * result + (caseSensitive ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Replacement{" +
               "key='" + key + '\'' +
               ", value='" + value + '\'' +
               ", caseSensitive=" + caseSensitive +
               '}';
    }
}
