package ch.jamiete.bunbun.commands.admin;

import org.kitteh.irc.client.library.element.Channel;
import org.kitteh.irc.client.library.element.User;
import ch.jamiete.bunbun.BunBun;
import ch.jamiete.bunbun.Ignore;
import ch.jamiete.bunbun.command.ChannelCommand;

public class IgnoreCommand extends ChannelCommand {

    public IgnoreCommand(BunBun bun) {
        super(bun);

        this.setName("ignore");
        this.setDescription("Ignores a user.");
        this.setPermission("admin.ignore");
    }

    @Override
    public void execute(User user, Channel channel, String[] arguments, String label) {
        if (arguments.length > 0) {
            Ignore ignore = null;

            if (arguments.length == 1) {
                ignore = new Ignore(arguments[0]);
            }

            if (arguments.length == 2 && arguments[1].equalsIgnoreCase("hostname")) {
                User ignoree = this.bun.findUser(arguments[0]);

                if (ignoree != null) {
                    ignore = new Ignore(ignoree.getNick(), ignoree.getHost());
                } else {
                    this.reply(user, channel, "Couldn't find that user.");
                    return;
                }
            }

            if (arguments.length == 3 && arguments[2].equalsIgnoreCase("hostname")) {
                ignore = new Ignore(arguments[0], arguments[1]);
            }

            if (arguments.length > 3) {
                this.usage(user, channel, "<nick/user> [hostname (cmd)/hostname] [hostname (cmd)]");
            }

            this.bun.getCommandManager().ignore(ignore);
            this.reply(user, channel, "Ignore added.");
        } else {
            this.usage(user, channel, "<nick/user> [hostname (cmd)/hostname] [hostname (cmd)]");
        }
    }
}
