package ch.jamiete.bunbun.command;

import org.kitteh.irc.client.library.element.Channel;
import org.kitteh.irc.client.library.element.User;
import ch.jamiete.bunbun.BunBun;

public abstract class ChannelCommand extends GenericCommand {

    protected ChannelCommand(final BunBun bun) {
        super(bun);
    }

    public abstract void execute(User user, Channel channel, String[] arguments, String label);

    protected void reply(User user, Channel channel, String message) {
        channel.sendMessage(user.getNick() + ": " + message);
    }

    protected void usage(User user, Channel channel, String params) {
        if (this.getAliases() != null) {
            BunBun.getLogger().warning(this.getName() + " has aliases but called #usage without providing the label called.");
        }

        this.usage(user, channel, params, this.getName());
    }

    protected void usage(User user, Channel channel, String params, String label) {
        this.reply(user, channel, "Incorrect usage. " + CommandManager.PREFIX + label + " " + params);
    }

}
