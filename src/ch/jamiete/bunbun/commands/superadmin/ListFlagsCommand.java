package ch.jamiete.bunbun.commands.superadmin;

import java.util.Arrays;
import org.kitteh.irc.client.library.element.Channel;
import org.kitteh.irc.client.library.element.User;
import ch.jamiete.bunbun.BunBun;
import ch.jamiete.bunbun.command.ChannelCommand;
import ch.jamiete.bunbun.permissions.PermissionFlag;

public class ListFlagsCommand extends ChannelCommand {

    public ListFlagsCommand(BunBun bun) {
        super(bun);

        this.setName("listflags");
        this.setAliases(Arrays.asList(new String[] { "flaglist", "flagslist" }));
        this.setDescription("Lists available flags.");
        this.setPermission("superadmin.listflags");
        this.setSilent(true);
    }

    @Override
    public void execute(User user, Channel channel, String[] arguments, String label) {
        StringBuilder flags = new StringBuilder();

        for (PermissionFlag flag : this.bun.getPermissionManager().getFlags()) {
            flags.append(flag.getFlag().getChar());
        }

        this.reply(user, channel, flags.toString());
    }

}
