/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2022 RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.interactions.slash.structure.builder;

import de.chojo.jdautil.interactions.slash.Argument;
import de.chojo.jdautil.interactions.slash.Slash;
import de.chojo.jdautil.interactions.slash.Group;
import de.chojo.jdautil.interactions.slash.SubCommand;

class RouteBuilderTest {

    void testSyntax() {
        // no arguments can be set on the route
        Slash.of("command", "description")
                // Creates a new command group
                .group(Group.of("subcommand-group1", "description")
                        // only sub commands can be defined here. No arguments are allowed on groups
                        .subCommand(SubCommand.of("subcommand1", "description")
                                .handler(null)
                                .argument(Argument.integer("name1", "descr"))
                                .argument(Argument.text("name2", "descr").withAutoComplete()))
                        // Another sub command
                        .subCommand(SubCommand.of("subcommand2", "description")
                                .handler(null)
                                .argument(Argument.integer("arg1", "descr").asRequired())))
                // Leaves can be still defined beside a branch
                .subCommand(SubCommand.of("subcommand1", "description")
                        .handler(null)
                        .argument(Argument.role("", "")));

        /*
        // Not branchable anymore
        Command.of("commandName", "description")
                .command(null)
                .argument(Argument.role("arg1", "descr"))
                .argument(Argument.role("arg2", "descr"));

        Command.of("name", "descr")
                .argument(Argument.string("arg", "descr")); // You cant add an argument, because handler is missing

        Command.of("name", "descr")
                .command(null)
                .argument(Argument.string("arg", "descr")); // You can add an argument. Handler is present.

        Command.of("name", "descr")
                .command(null)
                .subCommand(SubCommand.of("name", "descr"));// You cant add an subcommand because the base command is used

        Command.of("name", "descr")
                .group(Group.of("group", "descr"))// invalid. Group does not have a command

        Command.of("name", "descr")
                .group(Group.of("group", "descr")
                        .subCommand(SubCommand.of("name", "descr")))// invalid. No handler for subcommand

        Command.of("name", "descr")
                .group(Group.of("group", "descr")
                        .subCommand(SubCommand.of("name", "descr")
                                .handler(null)));// Valid. Group has a valid subcommand

        Command.of("name", "descr")
                .subCommand(SubCommand.of("name", "descr"));// invalid. No handler for subcommand

        Command.of("name", "descr")
                .subCommand(SubCommand.of("name", "descr")
                        .handler(null));// invalid. No handler for subcommand
        Command.of("name", "descr")
                .subCommand(SubCommand.of("name", "descr")
                        .handler(null))
                .argument(); // You cant add an argument because a subCommand is already set

        Command.of("name", "descr")
                .adminCommand()
                .publicCommand()
                .build(); // You cant build. Handler is missing

        Command.of("name", "descr")
                .adminCommand()
                .publicCommand()
                .command(null)
                .build(); // You can build. Handler is present


         */
    }

}
