/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.functions;

import java.util.Objects;
import java.util.function.Function;

/**
 * Represents a function that accepts four arguments and produces a result. This is the quad-arity specialization of
 * {@link Function}.
 *
 * <p>This is a <a href="package-summary.html">functional interface</a>
 * whose functional method is {@link #apply(Object, Object, Object, Object)}.
 *
 * @param <T1> the type of the first argument to the function
 * @param <T2> the type of the second argument to the function
 * @param <T3> the type of the third argument to the function
 * @param <R>  the type of the result of the function
 *
 * @see Function
 * @since 1.0.0
 */
@FunctionalInterface
public interface QuadFunction<T1, T2, T3, T4, R> {

    /**
     * Applies this function to the given arguments.
     *
     * @param t1 the first function argument
     * @param t2 the second function argument
     * @param t3 the third function argument
     * @param t4 the fourth function argument
     *
     * @return the function result
     */
    R apply(T1 t1, T2 t2, T3 t3, T4 t4);

    /**
     * Returns a composed function that first applies this function to its input, and then applies the {@code after}
     * function to the result. If evaluation of either function throws an exception, it is relayed to the caller of the
     * composed function.
     *
     * @param <V>   the type of output of the {@code after} function, and of the composed function
     * @param after the function to apply after this function is applied
     *
     * @return a composed function that first applies this function and then applies the {@code after} function
     *
     * @throws NullPointerException if after is null
     */
    default <V> QuadFunction<T1, T2, T3, T4, V> andThen(Function<? super R, ? extends V> after) {
        Objects.requireNonNull(after);
        return (T1 t1, T2 t2, T3 t3, T4 t4) -> after.apply(apply(t1, t2, t3, t4));
    }

}