package ch.jamiete.bunbun.commands.superadmin;

import java.util.Arrays;
import org.kitteh.irc.client.library.element.Channel;
import org.kitteh.irc.client.library.element.User;
import ch.jamiete.bunbun.BunBun;
import ch.jamiete.bunbun.command.ChannelCommand;
import ch.jamiete.bunbun.permissions.Flag;
import ch.jamiete.bunbun.permissions.PermissionFlag;

public class FlagPermissionsCommand extends ChannelCommand {

    public FlagPermissionsCommand(BunBun bun) {
        super(bun);

        this.setName("flagpermissions");
        this.setAliases(Arrays.asList(new String[] { "flagperms" }));
        this.setDescription("Lists permissions for a flag.");
        this.setPermission("superadmin.flagpermissions");
        this.setSilent(true);
    }

    @Override
    public void execute(User user, Channel channel, String[] arguments, String label) {
        if (arguments.length != 1) {
            this.usage(user, channel, "<flag>", label);
            return;
        }

        StringBuilder permissions = new StringBuilder();
        PermissionFlag flag = this.bun.getPermissionManager().getFlag(Flag.byChar(arguments[0].charAt(0)));

        if (flag == null) {
            this.reply(user, channel, "Couldn't find that flag.");
            return;
        }

        for (String permission : flag.getGrants()) {
            permissions.append(permission + ", ");
        }

        for (String permission : flag.getExclusions()) {
            permissions.append("-" + permission + ", ");
        }

        permissions.setLength(permissions.length() - 2);
        this.reply(user, channel, permissions.toString());
    }
}
