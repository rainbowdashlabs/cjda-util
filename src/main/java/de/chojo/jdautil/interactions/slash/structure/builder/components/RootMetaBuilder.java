/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.interactions.slash.structure.builder.components;

import de.chojo.jdautil.interactions.base.InteractionScope;
import de.chojo.jdautil.interactions.slash.structure.handler.SlashHandler;
import net.dv8tion.jda.api.Permission;

public interface RootMetaBuilder extends ExtendableRootBuilder {

    RootArgumentBuilder command(SlashHandler handler);

    RootMetaBuilder withPermission(Permission permission, Permission... permissions);

    /**
     * Marks a command as public command.
     * <p>
     * This is the default value.
     *
     * @return builder
     */
    RootMetaBuilder publicCommand();

    /**
     * Marks a command as admin command.
     * <p>
     * The command will only be accessable for administrators of a guild.
     *
     * @return builder
     */
    RootMetaBuilder adminCommand();

    RootMetaBuilder guildOnly();

    RootMetaBuilder scope(InteractionScope scope);

    RootMetaBuilder unlocalized();

    default RootMetaBuilder globalCommand() {
        return scope(InteractionScope.GLOBAL);
    }

    default RootMetaBuilder privateCommand() {
        return scope(InteractionScope.PRIVATE);
    }
}
