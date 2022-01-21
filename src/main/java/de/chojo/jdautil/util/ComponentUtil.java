/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.chojo.jdautil.util;

import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Component;

import java.util.ArrayList;
import java.util.List;

public class ComponentUtil {
    public static List<ActionRow> getActionRows(List<Component> components) {
        var rows = new ArrayList<ActionRow>();
        var from = 0;
        var to = 5;

        while (from < components.size()) {
            rows.add(ActionRow.of(components.subList(from, Math.min(to, components.size()))));
            from += 5;
            to += 5;
        }

        return rows;
    }
}
