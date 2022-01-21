/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.chojo.jdautil.util;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class QueryParamBuilder {
    List<String> params;

    private QueryParamBuilder() {

    }

    public static QueryParamBuilder builder() {
        return new QueryParamBuilder();
    }

    public QueryParamBuilder add(String key, String value) {
        var keyEncode = URLEncoder.encode(key, StandardCharsets.UTF_8);
        var valueEncode = URLEncoder.encode(value, StandardCharsets.UTF_8);
        params.add(keyEncode + "=" + valueEncode);
        return this;
    }

    public String append(String url) {
        if (params.isEmpty()) {
            return url;
        }
        return url + "?" + String.join("&", params);
    }
}
