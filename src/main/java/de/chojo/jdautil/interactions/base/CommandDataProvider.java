/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.interactions.base;

import de.chojo.jdautil.localization.ILocalizer;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

public interface CommandDataProvider extends Interaction {
    CommandData toCommandData(ILocalizer localizer);
}
