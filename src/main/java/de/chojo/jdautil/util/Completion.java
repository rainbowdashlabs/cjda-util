/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2022 RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.util;

import net.dv8tion.jda.api.interactions.commands.Command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SuppressWarnings("unused")
public final class Completion {
    private Completion() {
        throw new UnsupportedOperationException("This is a utility class.");
    }


    /**
     * Complete an array of strings.
     *
     * @param value  current value
     * @param inputs possible values
     * @return list of strings
     */
    public static List<Command.Choice> complete(String value, String... inputs) {
        return ArrayUtil.startingWithInArray(value, inputs)
                .map(e -> new Command.Choice(e, e))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Complete an stream of strings
     *
     * @param value  current value
     * @param inputs possible values
     * @return list of strings
     */
    public static List<Command.Choice> complete(String value, Stream<String> inputs) {
        if (value.isEmpty()) return inputs.map(Choice::toChoice).toList();
        var lowerValue = value.toLowerCase(Locale.ROOT);
        return inputs.filter(i -> i.toLowerCase().startsWith(lowerValue))
                .map(Choice::toChoice)
                .toList();
    }

    /**
     * Complete an collection of strings
     *
     * @param value  current value
     * @param inputs possible values
     * @return list of strings
     */
    public static List<Command.Choice> complete(String value, Collection<String> inputs) {
        return complete(value, inputs.stream());
    }

    /**
     * Complete an object stream.
     *
     * @param value   current value
     * @param inputs  possible values
     * @param mapping mapping of stream objects to string
     * @param <T>     type of stream
     * @return list of strings
     */
    public static <T> List<Command.Choice> complete(String value, Stream<T> inputs, Function<T, String> mapping) {
        return complete(value, inputs.map(mapping));
    }

    /**
     * Complete a collection of objects
     *
     * @param value   current value
     * @param inputs  possible values
     * @param mapping mapping of collection objects to string
     * @param <T>     type of collection
     * @return list of strings
     */
    public static <T> List<Command.Choice> complete(String value, Collection<T> inputs, Function<T, String> mapping) {
        return complete(value, inputs.stream(), mapping);
    }

    /**
     * Completes a enum. will return the enum values in lower case with underscores.
     *
     * @param value current value
     * @param clazz enum clazz
     * @param <T>   type of enum
     * @return list of strings
     */
    public static <T extends Enum<T>> List<Command.Choice> complete(String value, Class<T> clazz) {
        return complete(value, clazz, true, false);
    }

    /**
     * Completes a enum
     *
     * @param value     current value
     * @param clazz     enum clazz
     * @param lowerCase will make values lower case if true
     * @param strip     will strip underscores if true
     * @param <T>       type of enum
     * @return list of strings
     */
    public static <T extends Enum<T>> List<Command.Choice> complete(String value, Class<T> clazz, boolean lowerCase, boolean strip) {
        return complete(value,
                Arrays.stream(clazz.getEnumConstants())
                        .map(Enum::name)
                        .map(v -> lowerCase ? v.toLowerCase() : v)
                        .map(v -> strip ? v.replace("_", "") : v));
    }

    public static <T> List<T> singleEntryList(T value) {
        List<T> list = new ArrayList<>();
        list.add(value);
        return list;
    }
}
