/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.chojo.jdautil.botlist.builder.stage;

import de.chojo.jdautil.botlist.builder.BotlistBuilder;

public interface BaseUrlStage {
    AuthStage withBaseUrl(String baseUrl);
}