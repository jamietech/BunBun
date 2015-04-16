package ch.jamiete.bunbun.commands.admin;

import java.util.Arrays;
import org.kitteh.irc.client.library.element.Channel;
import org.kitteh.irc.client.library.element.User;
import ch.jamiete.bunbun.BunBun;
import ch.jamiete.bunbun.command.ChannelCommand;

public class ChannelFlowCommand extends ChannelCommand {

    public ChannelFlowCommand(BunBun bun) {
        super(bun);

        this.setName("join");
        this.setAliases(Arrays.asList(new String[] { "part" }));
        this.setDescription("Join or part a channel.");
        this.setPermission("admin.flow");
    }

    @Override
    public void execute(User user, Channel channel, String[] arguments, String label) {
        if (arguments.length > 0) {
            BunBun.getLogger().info(label + "ing channel " + arguments[0] + " at behest of " + user.getNick());

            switch (label.toLowerCase()) {
                case "join":
                    this.bun.getClient().addChannel(arguments[0]);
                    break;

                case "part":
                    this.bun.getClient().removeChannel(arguments[0], this.bun.combineSplit(1, arguments, " "));
                    break;
            }

            this.reply(user, channel, "Done!");
        } else {
            this.usage(user, channel, "<channel>");
        }
    }
}
