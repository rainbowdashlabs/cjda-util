/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2022 RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.conversation.elements;

/**
 * Class which represents the result of a {@link Step}
 */
public class Result {
    private final ResultType type;
    private final int next;
    private final boolean silent;

    private Result(ResultType type, int next, boolean silent) {
        this.type = type;
        this.next = next;
        this.silent = silent;
    }

    /**
     * Proceeds to the next step
     *
     * @param next next step
     * @return new result
     */
    public static Result proceed(int next) {
        return new Result(ResultType.PROCEED, next, false);
    }

    /**
     * Ends the conversation
     *
     * @return new result
     */
    public static Result finish() {
        return new Result(ResultType.FINISH, 0, false);
    }

    /**
     * Fails the current step and repeats the last step again
     *
     * @return new result
     */
    public static Result fail() {
        return new Result(ResultType.FAILED, 0, false);
    }

    /**
     * Fails the current step and repeats the last step again
     *
     * @param silent true if the message should not be sent again
     * @return new result
     * @deprecated Deprecated in favor of {@link #freeze()}
     */
    @Deprecated(forRemoval = true)
    public static Result failSilent(boolean silent) {
        return new Result(ResultType.FAILED, 0, silent);
    }

    /**
     * Freezes the conversation in the current step and handles another input.
     *
     * @return new result
     */
    public static Result freeze() {
        return new Result(ResultType.FREEZE, 0, false);
    }

    /**
     * Checks if the failure is silent. Only applicable if {@link #type()} is {@link ResultType#FAILED}
     *
     * @return true if silent
     */
    public boolean isSilent() {
        return silent;
    }

    /**
     * @return
     */
    public ResultType type() {
        return type;
    }

    /**
     * Returns the id of the next stage. Only applicable if {@link #type()} is {@link ResultType#PROCEED}
     *
     * @return id of next step
     */
    public int next() {
        if (type != ResultType.PROCEED) throw new IllegalStateException("Next can only be called on PROCEED results.");
        return next;
    }
}
