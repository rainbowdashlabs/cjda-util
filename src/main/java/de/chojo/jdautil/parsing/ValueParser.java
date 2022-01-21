/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.chojo.jdautil.parsing;

import java.util.Optional;
import java.util.OptionalLong;
import java.util.regex.Pattern;

public class ValueParser {
    private static final Pattern INTERVAL = Pattern.compile("([0-9])+\\s(((min|hour|day|week)s?)|month)",
            Pattern.MULTILINE);

    /**
     * Get a boolean as a boolean state.
     *
     * @param bool boolean as string.
     *
     * @return boolean state.
     */
    public static Optional<Boolean> parseBoolean(String bool) {
        return parseBoolean(bool, "true", "false");
    }

    /**
     * Get a boolean as boolean state.
     *
     * @param bool    boolean as string
     * @param isTrue  string value for true. case is ignored
     * @param isFalse string value for false. case is ignored
     *
     * @return boolean state.
     */
    public static Optional<Boolean> parseBoolean(String bool, String isTrue, String isFalse) {
        if (bool.equalsIgnoreCase(isTrue) || bool.equalsIgnoreCase(isFalse)) {
            return Optional.of(bool.equalsIgnoreCase(isTrue));
        }
        return Optional.empty();
    }

    /**
     * Parse a string to integer.
     *
     * @param number number as string
     *
     * @return number or null if parse failed
     */
    public static Optional<Integer> parseInt(String number) {
        try {
            return Optional.of(Integer.parseInt(number));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    /**
     * Parse a string to double.
     *
     * @param number number as string
     *
     * @return number or null if parse failed
     */
    public static Optional<Double> parseDouble(String number) {
        try {
            return Optional.of(Double.parseDouble(number));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    /**
     * Parse a string to long.
     *
     * @param number number as string
     *
     * @return number or null if parse failed
     */
    public static Optional<Long> parseLong(String number) {
        try {
            return Optional.of(Long.parseLong(number));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    /**
     * Check if a string contains a interval.
     *
     * @param timeStampString interval as string
     *
     * @return true if the String is a Intervall
     */
    public static boolean getInterval(String timeStampString) {
        return INTERVAL.matcher(timeStampString).matches();
    }

    /**
     * Parse a string to long.
     *
     * @param number number as string
     *
     * @return number or null if parse failed
     */
    public static OptionalLong hexToLong(String number) {
        try {
            return OptionalLong.of(Long.parseLong(number, 16));
        } catch (NumberFormatException e) {
            return OptionalLong.empty();
        }
    }
}
