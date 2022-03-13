/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2022 RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.botlist.modules.submission;

import de.chojo.jdautil.botlist.BotListData;

import java.util.Map;

public interface ISubmissionService {
    Map<String, Object> data(BotListData data);
}
