/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.botlist.builder.stage;

import net.dv8tion.jda.api.sharding.ShardManager;

public interface ClientStage {
    BaseUrlStage forClient(ShardManager shardManager);
}
