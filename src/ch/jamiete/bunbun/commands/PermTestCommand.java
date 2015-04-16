package ch.jamiete.bunbun.commands;

import java.util.Arrays;
import org.kitteh.irc.client.library.element.Channel;
import org.kitteh.irc.client.library.element.User;
import ch.jamiete.bunbun.BunBun;
import ch.jamiete.bunbun.command.ChannelCommand;

public class PermTestCommand extends ChannelCommand {
    final static String PERMISSION_OTHERS = "general.permtest.others";

    public PermTestCommand(BunBun bun) {
        super(bun);

        this.setName("permtest");
        this.setAliases(Arrays.asList(new String[] { "permissiontest", "ptest" }));
        this.setPermission("general.permtest");
        this.setDescription("Check if you have a permission node.");
    }

    @Override
    public void execute(User user, Channel channel, String[] arguments, String label) {

        String test = "";
        for (String s : arguments) {
            test += s + " ";
        }
        BunBun.getLogger().info(test);

        if (arguments.length == 0 || arguments.length > 2) {
            String params = "<permission>";

            if (this.bun.getPermissionManager().hasPermission(user, PermTestCommand.PERMISSION_OTHERS)) {
                params += " OR <online_user> <permission>";
            }

            this.usage(user, channel, params, label);
        } else if (arguments.length == 1) {
            this.reply(user, channel, "You " + (this.bun.getPermissionManager().hasPermission(user, arguments[0]) ? "have" : "haven't") + " got " + arguments[0]);
        } else if (arguments.length == 2) {
            if (this.bun.getPermissionManager().hasPermission(user, PermTestCommand.PERMISSION_OTHERS)) {
                User puser = this.bun.findUser(arguments[0]);

                if (puser == null) {
                    this.reply(user, channel, "Couldn't find an online user by that name.");
                } else {
                    this.reply(user, channel, puser.getNick() + " " + (this.bun.getPermissionManager().hasPermission(user, arguments[1]) ? "has" : "hasn't") + " got " + arguments[1]);
                }
            }
        }
    }

}
