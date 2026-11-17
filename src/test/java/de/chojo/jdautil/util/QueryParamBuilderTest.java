/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class QueryParamBuilderTest {

    @Test
    void appendsEncodedParameters() {
        var url =
                QueryParamBuilder.builder().add("user id", "7").add("q", "a&b").append("https://example.com");

        assertEquals("https://example.com?user+id=7&q=a%26b", url);
    }

    @Test
    void extendsAnExistingQuery() {
        var url = QueryParamBuilder.builder().add("k", "v").append("https://example.com/a?b=1");

        assertEquals("https://example.com/a?b=1&k=v", url);
    }

    @Test
    void leavesTheUrlWithoutParameters() {
        assertEquals("https://example.com", QueryParamBuilder.builder().append("https://example.com"));
    }
}
