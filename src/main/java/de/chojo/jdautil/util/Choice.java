/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.util;

import net.dv8tion.jda.api.interactions.commands.Command;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class Choice {
    public static List<Command.Choice> toStringChoice(List<String> values) {
        return toStringChoice(values.stream());
    }

    public static List<Command.Choice> toStringChoice(Stream<String> values) {
        return values.map(Choice::toChoice).toList();
    }

    public static List<Command.Choice> toIntegerChoice(List<Integer> values) {
        return toIntegerChoice(values.stream());
    }

    public static List<Command.Choice> toIntegerChoice(Stream<Integer> values) {
        return values.map(Choice::toChoice).toList();
    }

    public static List<Command.Choice> toDoubleChoice(List<Double> values) {
        return toDoubleChoice(values.stream());
    }

    public static List<Command.Choice> toDoubleChoice(Stream<Double> values) {
        return values.map(Choice::toChoice).toList();
    }

    public static Command.Choice toChoice(String value) {
        return new Command.Choice(value, value);
    }

    public static Command.Choice toChoice(int value) {
        return new Command.Choice(String.valueOf(value), value);
    }

    public static Command.Choice toChoice(double value) {
        return new Command.Choice(String.valueOf(value), value);
    }

    public static ChoiceBuilder builder() {
        return new ChoiceBuilder();
    }

    public static class ChoiceBuilder {
        private final List<Command.Choice> choices = new ArrayList<>();

        private ChoiceBuilder add(Command.Choice choice) {
            choices.add(choice);
            return this;
        }

        public ChoiceBuilder add(String name, String value) {
            return add(new Command.Choice(name, value));
        }

        public ChoiceBuilder add(String name, int value) {
            return add(new Command.Choice(name, value));
        }

        public ChoiceBuilder add(String name, long value) {
            return add(new Command.Choice(name, value));
        }

        public List<Command.Choice> build() {
            return choices;
        }
    }
}
