package de.chojo.jdautil.conversation.elements;

public enum ResultType {
    /**
     * Could not process input. Please repeat.
     */
    FAILED,
    /**
     * Proceed with the next conversation step.
     */
    PROCEED,
    /**
     * Mark the conversation as finished
     */
    FINISH,
    /**
     * Freeze the conversation.
     *
     * The conversation has to be continued by calling
     */
    FREEZE
}
