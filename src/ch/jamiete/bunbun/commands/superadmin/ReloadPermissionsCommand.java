package ch.jamiete.bunbun.commands.superadmin;

import org.kitteh.irc.client.library.element.Channel;
import org.kitteh.irc.client.library.element.User;
import ch.jamiete.bunbun.BunBun;
import ch.jamiete.bunbun.command.ChannelCommand;

public class ReloadPermissionsCommand extends ChannelCommand {

    public ReloadPermissionsCommand(BunBun bun) {
        super(bun);

        this.setName("reloadpermissions");
        this.setDescription("Reloads permission flags from disk and untracks all users.");
        this.setPermission("superadmin.reloadpermissions");
        this.setSilent(true);
    }

    @Override
    public void execute(User user, Channel channel, String[] arguments, String label) {
        BunBun.getLogger().info("Reloading permissions at behest of " + user.getName());
        this.reply(user, channel, "Reloading...");
        this.bun.getPermissionManager().init();

        for (Channel c : this.bun.getClient().getChannels()) {
            for (User u : c.getUsers()) {
                this.bun.getPermissionManager().track(u);
            }
        }

        this.reply(user, channel, "Done!");
    }

}
