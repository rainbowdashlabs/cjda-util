/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.parsing;

import de.chojo.jdautil.text.TextFormatting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class ArgumentUtil {

    public static String[] parseQuotedArgs(String args, boolean strip) {
        if (strip) {
            args = args.replace("\\s+", " ");
        }
        return parseQuotedArgs(args.split("\\s"));
    }

    /**
     * Parse arguments and keep quotes together
     *
     * @param args args to parse
     *
     * @return parsed args
     */
    public static String[] parseQuotedArgs(String[] args) {
        List<String> currArgs = new ArrayList<>();
        List<String> currArg = new ArrayList<>();
        var open = false;
        for (var arg : args) {
            if (arg.startsWith("\"") && arg.endsWith("\"")) {
                currArgs.add(arg.replace("\"", ""));
                continue;
            }
            if (arg.startsWith("\"")) {
                open = true;
            }
            if (!open) {
                currArgs.add(arg);
                continue;
            }

            if (arg.endsWith("\"")) {
                currArg.add(arg);
                currArgs.add(String.join(" ", currArg).replace("\"", ""));
                currArg.clear();
                open = false;
                continue;
            }

            currArg.add(arg);
        }
        return currArgs.toArray(new String[0]);
    }

    /**
     * Get a message from string array from 'from' to array.length().
     *
     * @param strings array of strings.
     * @param from    start index (included). Use negative counts to count from the last index.
     *
     * @return array sequence as string
     */
    public static String getMessage(String[] strings, int from) {
        return getMessage(strings, from, 0);
    }

    /**
     * Get a message from string array from 'from' to 'to'.
     *
     * @param strings array of strings.
     * @param from    start index (included). Use negative counts to count from the last index.
     * @param to      end index (excluded). Use negative counts to count from the last index.
     *
     * @return array sequence as string
     */
    public static String getMessage(String[] strings, int from, int to) {
        return TextFormatting.getRangeAsString(" ", strings, from, to);
    }

    /**
     * Get a array as sublist from 'from' to array.length().
     *
     * @param objects arguments
     * @param from    start index included
     * @param <T>     Type of objects
     *
     * @return sublist
     */
    public static <T> List<T> getRangeAsList(T[] objects, int from) {
        return getRangeAsList(objects, from, 0);
    }

    /**
     * Get a sublist from array from 'from' to 'to'.
     *
     * @param objects list of objects.
     * @param from    start index (included). Use negative counts to count from the last index.
     * @param to      end index (excluded). Use negative counts to count from the last index.
     * @param <T>     Type of objects
     *
     * @return sublist.
     */
    public static <T> List<T> getRangeAsList(T[] objects, int from, int to) {
        return getRangeAsList(Arrays.asList(objects), from, to);
    }

    /**
     * Get a sublist from 'from' to list.size()
     *
     * @param objects list of objects.
     * @param from    start index (included). Use negative counts to count from the last index.
     * @param <T>     Type of objects
     *
     * @return sublist.
     */
    public static <T> List<T> getRangeAsList(List<T> objects, int from) {
        return getRangeAsList(objects, from, 0);
    }

    /**
     * Get a sublist of a list.
     *
     * @param objects list of objects.
     * @param from    start index (included). Use negative counts to count from the last index.
     * @param to      end index (excluded). Use negative counts to count from the last index.
     * @param <T>     Type of objects
     *
     * @return sublist.
     */
    public static <T> List<T> getRangeAsList(List<T> objects, int from, int to) {
        var finalTo = to;
        if (to < 1) {
            finalTo = objects.size() + to;
        }
        var finalFrom = from;
        if (from < 0) {
            finalFrom = objects.size() + from;
        }

        if (finalFrom > finalTo || finalFrom < 0 || finalTo > objects.size()) {
            return Collections.emptyList();
        }

        return objects.subList(finalFrom, finalTo);
    }
}
