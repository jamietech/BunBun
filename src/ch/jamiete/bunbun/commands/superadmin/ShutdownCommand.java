package ch.jamiete.bunbun.commands.superadmin;

import org.kitteh.irc.client.library.element.Channel;
import org.kitteh.irc.client.library.element.User;
import ch.jamiete.bunbun.BunBun;
import ch.jamiete.bunbun.command.ChannelCommand;

public class ShutdownCommand extends ChannelCommand {

    public ShutdownCommand(BunBun bun) {
        super(bun);

        this.setName("shutdown");
        this.setDescription("Shuts down the bot instance gracefully. Use OVERRIDE to remove behest message.");
        this.setPermission("superadmin.shutdown");
        this.setSilent(true);
    }

    @Override
    public void execute(User user, Channel channel, String[] arguments, String label) {
        String message = "Shutting down at behest of " + user.getNick();
        BunBun.getLogger().info(message);

        if (arguments.length > 0) {
            if (arguments[0].equals("OVERRIDE")) {
                message = this.bun.combineSplit(1, arguments, " ");
            } else {
                message = message + " ัส" + this.bun.combineSplit(0, arguments, " ");
            }
        }

        this.bun.getClient().shutdown(message);
    }

}
