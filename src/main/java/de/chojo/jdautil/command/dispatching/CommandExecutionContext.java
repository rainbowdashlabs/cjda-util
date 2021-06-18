package de.chojo.jdautil.command.dispatching;

import de.chojo.jdautil.command.SimpleCommand;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageChannel;

public class CommandExecutionContext<Command extends SimpleCommand> {
    private Command command;
    private String args;
    private Guild guild;
    private MessageChannel channel;

    public CommandExecutionContext(Command command, String args, Guild guild, MessageChannel channel) {
        this.command = command;
        this.args = args;
        this.guild = guild;
        this.channel = channel;
    }

    public Command command() {
        return command;
    }

    public String args() {
        return args;
    }

    public Guild guild() {
        return guild;
    }

    public MessageChannel channel() {
        return channel;
    }
}
