package ch.jamiete.bunbun.commands.admin;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import org.kitteh.irc.client.library.element.Channel;
import org.kitteh.irc.client.library.element.User;
import ch.jamiete.bunbun.BunBun;
import ch.jamiete.bunbun.command.ChannelCommand;

public class ChannelAddCommand extends ChannelCommand {

    public ChannelAddCommand(BunBun bun) {
        super(bun);

        this.setName("channeladd");
        this.setAliases(Arrays.asList(new String[] { "addchannel" }));
        this.setDescription("Adds a channel to join at startup.");
        this.setPermission("admin.channeladd");
    }

    @Override
    public void execute(User user, Channel channel, String[] arguments, String label) {
        if (arguments.length == 1) {
            BunBun.getLogger().info("Joining channel " + arguments[0] + " at behest of " + user.getNick());

            this.reply(user, channel, "Attempting join...");
            this.bun.getClient().addChannel(arguments[0]);

            try {
                BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("channels.txt", true), "utf-8"));

                out.newLine();
                out.write("# At the behest of " + user.getNick());
                out.newLine();
                out.write(arguments[0]);

                out.close();
            } catch (IOException e) {
                this.bun.notifyException(e, channel, user, label);
                this.reply(user, channel, "Had difficulty writing change to file.");
            }

            this.reply(user, channel, "Done!");
        } else {
            this.usage(user, channel, "<channel>");
        }
    }
}
