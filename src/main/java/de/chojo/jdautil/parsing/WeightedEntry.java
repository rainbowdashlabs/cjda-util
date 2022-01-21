/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.chojo.jdautil.parsing;

import net.dv8tion.jda.api.entities.Member;
import org.apache.commons.text.similarity.JaroWinklerSimilarity;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.Objects;

public class WeightedEntry<T> implements Comparable<WeightedEntry<T>> {
    private static final JaroWinklerSimilarity SIMILARITY = new JaroWinklerSimilarity();

    private final double weight;
    private final T reference;

    private WeightedEntry(T reference, double weight) {
        this.reference = reference;
        this.weight = weight;
    }

    public static <T> WeightedEntry<T> withWeight(T member, double weight) {
        return new WeightedEntry<>(member, weight);
    }

    public static <T> WeightedEntry<T> directMatch(T member) {
        return new WeightedEntry<>(member, 1);
    }

    public static WeightedEntry<Member> withJaro(Member member, String name) {
        var lowerName = name.toLowerCase(Locale.ROOT);
        var lowerEffective = member.getEffectiveName().toLowerCase(Locale.ROOT);
        var lowerUser = member.getUser().getName().toLowerCase(Locale.ROOT);
        var jaro = Math.max(SIMILARITY.apply(member.getEffectiveName(), name), SIMILARITY.apply(member.getUser().getName(), name));
        var score = jaro;
        if (jaro < 0.85) {
            double startsWith = 0.65;
            var contains = 0.85;
            if (lowerEffective.startsWith(lowerName) || lowerUser.startsWith(lowerName)) {
                startsWith = 1;
            } else {
                contains = lowerEffective.contains(lowerName) || lowerUser.contains(lowerName) ? 0.95 : 0.4;
            }
            score = ((startsWith + contains) / 2 + jaro) / 2;
        }
        return new WeightedEntry<>(member, score);
    }

    @Override
    public int compareTo(@NotNull WeightedEntry<T> o) {
        return Double.compare(weight, o.weight);
    }

    public double getWeight() {
        return weight;
    }

    public T getReference() {
        return reference;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        var that = (WeightedEntry<?>) o;
        return reference.equals(that.reference);
    }

    @Override
    public int hashCode() {
        return Objects.hash(reference);
    }
}
