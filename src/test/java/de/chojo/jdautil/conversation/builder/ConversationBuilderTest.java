/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2022 RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.conversation.builder;

import de.chojo.jdautil.conversation.elements.Result;
import de.chojo.jdautil.conversation.elements.Step;
import org.junit.jupiter.api.Test;

class ConversationBuilderTest {

    @Test
    public void creationTest() {
        var conversation = ConversationBuilder
                .builder(Step.message("Enter something",
                        context -> {
                            var content = context.message().getContentRaw();
                            if (content.equals("1")) {
                                return Result.fail();
                            }
                            if (content.equalsIgnoreCase("2")) {
                                return Result.finish();
                            }
                            if (content.equals("3")) {
                                return Result.proceed(1);
                            }
                            return Result.fail();
                        }).build()
                ).addStep(1, Step.message("Please enter something else",
                        context -> {
                            if (context.message().getContentRaw().equals("hewo")) return Result.fail();
                            return Result.finish();
                        }).build())
                .build();
    }

}