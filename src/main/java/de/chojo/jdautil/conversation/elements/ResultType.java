/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

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
     * <p>
     * The conversation has to be continued by calling
     */
    FREEZE
}
