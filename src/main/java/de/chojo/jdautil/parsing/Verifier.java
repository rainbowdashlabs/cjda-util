/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.chojo.jdautil.parsing;

import net.dv8tion.jda.api.entities.ISnowflake;

import java.util.Optional;
import java.util.regex.Pattern;


public final class Verifier {
    private static final Pattern ID_PATTERN = Pattern.compile("(?:<[@#!&]{1,2})?(?<id>[0-9]{15,19})(?:>)?");
    private static final Pattern IPV_4 = Pattern.compile("^(?:[0-9]{1,3}\\.){3}[0-9]{1,3}(:[0-9]{1,5})?$");
    private static final Pattern IPV_6 = Pattern.compile("(^\\[([a-fA-F0-9]{0,4}:){4,7}[a-fA-F0-9]{0,4}]:"
            + "[0-9]{1,5}$)|(([a-fA-F0-9]{0,4}:){4,7}[a-fA-F0-9]{0,4}$)");
    private static final Pattern DOMAIN = Pattern.compile("^(?!://)([a-zA-Z0-9-_]+\\.)*[a-zA-Z0-9][a-zA-Z0-9-_]"
            + "+\\.[a-zA-Z]{2,11}?(:[0-9]{1,5})?$");

    private Verifier() {
    }

    /**
     * Returns true if the id is a valid id.
     *
     * @param id id to test.
     *
     * @return true if id is valid
     */
    public static boolean isValidId(String id) {
        return getIdRaw(id).isPresent();
    }

    /**
     * Check if two object have the same Snowflake.
     *
     * @param a first object
     * @param b second object
     *
     * @return true if the snowflakes are equal.
     */
    public static boolean equalSnowflake(ISnowflake a, ISnowflake b) {
        if (a == null || b == null) return false;
        return a.getIdLong() == b.getIdLong();
    }


    /**
     * Returns true if on of the arguments matches the argument. Not case sensitive.
     *
     * @param argument argument to match against.
     * @param args     one or more arguments to check.
     *
     * @return true if one argument matches.
     */
    public static boolean isArgument(String argument, String... args) {
        for (var arg : args) {
            if (argument.equalsIgnoreCase(arg)) return true;
        }
        return false;
    }

    /**
     * Extracts an id from discord's formatting.
     *
     * @param id the formatted id.
     *
     * @return the extracted id.
     */
    public static Optional<String> getIdRaw(String id) {
        var matcher = ID_PATTERN.matcher(id);
        return !matcher.matches() ? Optional.empty() : Optional.ofNullable(matcher.group(1));
    }

}
