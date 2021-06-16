package de.chojo.jdautil.conversation.elements;

public class Result {
    private ResultType type;
    private int next;

    private Result(ResultType type, int next) {
        this.type = type;
        this.next = next;
    }

    public static Result proceed(int next) {
        return new Result(ResultType.PROCEED, next);
    }

    public static Result finish() {
        return new Result(ResultType.FINISH, 0);
    }

    public static Result failed() {
        return new Result(ResultType.FAILED, 0);
    }

    public ResultType type() {
        return type;
    }

    public int next() {
        if (type != ResultType.PROCEED) throw new IllegalStateException("Next can only be called on PROCEED results.");
        return next;
    }
}
