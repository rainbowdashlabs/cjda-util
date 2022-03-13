/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2022 RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.conversation.elements;

public class Result {
    private ResultType type;
    private int next;
    private boolean silent = false;

    private Result(ResultType type, int next, boolean silent) {
        this.type = type;
        this.next = next;
    }

    public static Result proceed(int next) {
        return new Result(ResultType.PROCEED, next, false);
    }

    public static Result finish() {
        return new Result(ResultType.FINISH, 0, false);
    }

    public static Result fail() {
        return new Result(ResultType.FAILED, 0, false);
    }

    public static Result failSilent(boolean silent) {
        return new Result(ResultType.FAILED, 0, silent);
    }

    public static Result freeze() {
        return new Result(ResultType.FREEZE, 0, false);
    }

    public boolean isSilent() {
        return silent;
    }

    public ResultType type() {
        return type;
    }

    public int next() {
        if (type != ResultType.PROCEED) throw new IllegalStateException("Next can only be called on PROCEED results.");
        return next;
    }
}
