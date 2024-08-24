/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.botlist.builder.stage;

import de.chojo.jdautil.botlist.modules.shared.AuthHandler;

public interface AuthStage {
    ConfigurationStage withAuthentication(AuthHandler authHandler);
}
