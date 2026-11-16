/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.localization.util;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ReplacementTest {

    @Test
    public void testSerialization() throws JsonProcessingException {
        ObjectMapper mapper = JsonMapper.builder()
                .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
                .build()
                .setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE)
                .setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);

        var replacement = Replacement.create("abc", "abc").matchCase();

        var json = mapper.writeValueAsString(replacement);
        var deserialized = mapper.readValue(json, Replacement.class);

        Assertions.assertEquals(replacement, deserialized);
    }

    @Test
    public void ignoresCaseByDefault() {
        Assertions.assertEquals("Hi Nora", Replacement.create("NAME", "Nora").invoke("Hi %name%"));
    }

    @Test
    public void matchCaseOnlyReplacesExactCase() {
        var replacement = Replacement.create("NAME", "Nora").matchCase();

        Assertions.assertEquals("Hi %name%", replacement.invoke("Hi %name%"));
        Assertions.assertEquals("Hi Nora", replacement.invoke("Hi %NAME%"));
    }

    @Test
    public void keysAndValuesAreLiteral() {
        Assertions.assertEquals("Costs $5", Replacement.create("PRICE.", "$5").invoke("Costs %PRICE.%"));
        Assertions.assertEquals(
                "Costs %PRICEX%", Replacement.create("PRICE.", "$5").invoke("Costs %PRICEX%"));
    }
}
