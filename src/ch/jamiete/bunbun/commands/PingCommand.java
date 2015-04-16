package ch.jamiete.bunbun.commands;

import java.util.Arrays;
import org.kitteh.irc.client.library.element.Channel;
import org.kitteh.irc.client.library.element.User;
import ch.jamiete.bunbun.BunBun;
import ch.jamiete.bunbun.command.ChannelCommand;

public class PingCommand extends ChannelCommand {

    public PingCommand(final BunBun bun) {
        super(bun);

        this.setName("ping");
        this.setAliases(Arrays.asList(new String[] { "pong" }));
        this.setPermission("general.ping");
        this.setDescription("Bot replies pong.");
    }

    @Override
    public void execute(final User user, final Channel channel, final String[] arguments, String label) {
        this.reply(user, channel, "PONG");
    }

}
