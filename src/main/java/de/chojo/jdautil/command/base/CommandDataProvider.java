/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2022 RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.command.base;

import de.chojo.jdautil.localization.ILocalizer;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

public interface CommandDataProvider {
    CommandData toCommandData(ILocalizer localizer);
}
