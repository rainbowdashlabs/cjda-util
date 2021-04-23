package de.chojo.jdautil.parsing;

import net.dv8tion.jda.api.entities.Member;
import org.apache.commons.text.similarity.JaroWinklerSimilarity;
import org.jetbrains.annotations.NotNull;

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
        var weight = Math.max(SIMILARITY.apply(member.getEffectiveName(), name), SIMILARITY.apply(member.getUser().getName(), name));
        return new WeightedEntry<>(member, weight);
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
}
