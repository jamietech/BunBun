package ch.jamiete.bunbun.commands.superadmin;

import java.util.Arrays;
import org.kitteh.irc.client.library.element.Channel;
import org.kitteh.irc.client.library.element.User;
import ch.jamiete.bunbun.BunBun;
import ch.jamiete.bunbun.command.ChannelCommand;
import ch.jamiete.bunbun.permissions.Flag;

public class RemoveFlagCommand extends ChannelCommand {

    public RemoveFlagCommand(BunBun bun) {
        super(bun);

        this.setName("removeflag");
        this.setAliases(Arrays.asList(new String[] { "flagremove", "deleteflag", "flagdelete", "flagrem", "flagdel", "delflag", "remflag" }));
        this.setDescription("Removes a flag from an online user.");
        this.setPermission("superadmin.removeflag");
        this.setSilent(true);
    }

    @Override
    public void execute(User user, Channel channel, String[] arguments, String label) {
        if (arguments.length != 2) {
            this.usage(user, channel, "<user> <flag>", label);
        } else {
            User modify = this.bun.findUserPartial(arguments[0]);
            if (modify == null) {
                this.reply(user, channel, "Couldn't find that user.");
                return;
            }

            Flag flag = Flag.byChar(arguments[1].charAt(0));
            if (flag == null) {
                this.reply(user, channel, "Couldn't find that flag.");
                return;
            }

            this.bun.getPermissionManager().getUser(modify).removeFlag(this.bun.getPermissionManager().getFlag(flag));
            this.bun.getPermissionManager().getUser(modify).write(bun);

            BunBun.getLogger().info(this.bun.getFullUser(user) + " removed flag " + flag.getChar() + " from " + this.bun.getFullUser(modify));
            this.reply(user, channel, "Removed flag " + flag.getChar() + " from " + modify.getNick());
        }
    }

}
