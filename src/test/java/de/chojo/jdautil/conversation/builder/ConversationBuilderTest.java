package de.chojo.jdautil.conversation.builder;

import de.chojo.jdautil.conversation.elements.Result;
import de.chojo.jdautil.conversation.elements.Step;
import org.junit.jupiter.api.Test;

class ConversationBuilderTest {

    @Test
    public void creationTest() {
        var conversation = ConversationBuilder
                .builder(Step.of("Enter something",
                        context -> {
                            var content = context.message().getContentRaw();
                            if (content.equals("1")) {
                                return Result.failed();
                            }
                            if (content.equalsIgnoreCase("2")) {
                                return Result.finish();
                            }
                            if (content.equals("3")) {
                                return Result.proceed(1);
                            }
                            return Result.failed();
                        })
                ).addStep(1, Step.of("Please enter something else",
                        context -> {
                            if (context.message().getContentRaw().equals("hewo")) return Result.failed();
                            return Result.finish();
                        })).build();
    }

}