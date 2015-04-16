package ch.jamiete.bunbun.commands;

import org.kitteh.irc.client.library.IRCFormat;
import org.kitteh.irc.client.library.element.Channel;
import org.kitteh.irc.client.library.element.User;
import ch.jamiete.bunbun.BunBun;
import ch.jamiete.bunbun.command.ChannelCommand;
import ch.jamiete.bunbun.command.CommandManager;

public class HelpCommand extends ChannelCommand {

    public HelpCommand(BunBun bun) {
        super(bun);

        this.setName("help");
        this.setDescription("List all commands and their descriptions.");
        this.setPermission("general.help");
    }

    @Override
    public void execute(User user, Channel channel, String[] arguments, String label) {
        if (arguments.length == 1) {
            ChannelCommand command = this.bun.getCommandManager().getChannelCommand(arguments[0]);
            
            if (command == null) {
                this.reply(user, channel, "That's not a command. Try " + CommandManager.PREFIX + label + " to list commands.");
            } else {
                if (command.hasPermission() && !this.bun.getPermissionManager().hasPermission(user, command.getPermission())) {
                    this.reply(user, channel, "That's not a command. Try " + CommandManager.PREFIX + label + " to list commands.");
                } else {
                    StringBuilder reply = new StringBuilder();
                    reply.append(IRCFormat.BOLD).append(CommandManager.PREFIX).append(command.getName()).append(IRCFormat.RESET);
                    reply.append(" Ñ ");

                    if (command.getDescription() != null && !command.getDescription().trim().equals("")) {
                        reply.append(command.getDescription());
                    } else {
                        reply.append("No description.");
                    }

                    this.reply(user, channel, reply.toString());
                }
            }
        } else if (arguments.length > 1) {
            this.usage(user, channel, "<command_name> for command details OR without any parameters for command list.");
        } else {
            StringBuilder reply = new StringBuilder();
            reply.append(IRCFormat.BOLD).append("Commands: ").append(IRCFormat.RESET);

            for (ChannelCommand command : this.bun.getCommandManager().getChannelCommands()) {
                if (command == this) {
                    continue;
                }

                if (command.hasPermission() && this.bun.getPermissionManager().hasPermission(user, command.getPermission()) || !command.hasPermission()) {
                    reply.append(command.getName()).append(", ");
                }
            }

            reply.setLength(reply.length() - 2);
            this.reply(user, channel, reply.toString());
        }
    }

}
