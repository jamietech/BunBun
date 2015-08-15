package ch.jamiete.bunbun;

import java.util.ArrayList;
import java.util.logging.Logger;
import org.kitteh.irc.client.library.Client;
import org.kitteh.irc.client.library.element.Channel;
import org.kitteh.irc.client.library.element.User;
import ch.jamiete.bunbun.command.CommandManager;
import ch.jamiete.bunbun.commands.GeoIPCommand;
import ch.jamiete.bunbun.commands.HelpCommand;
import ch.jamiete.bunbun.commands.PermTestCommand;
import ch.jamiete.bunbun.commands.PingCommand;
import ch.jamiete.bunbun.commands.TF2Command;
import ch.jamiete.bunbun.commands.UrbanCommand;
import ch.jamiete.bunbun.commands.admin.ChannelAddCommand;
import ch.jamiete.bunbun.commands.admin.ChannelFlowCommand;
import ch.jamiete.bunbun.commands.admin.IgnoreCommand;
import ch.jamiete.bunbun.commands.minecraft.HistoryCommand;
import ch.jamiete.bunbun.commands.minecraft.UUIDCommand;
import ch.jamiete.bunbun.commands.superadmin.AddFlagCommand;
import ch.jamiete.bunbun.commands.superadmin.FlagPermissionsCommand;
import ch.jamiete.bunbun.commands.superadmin.ListFlagsCommand;
import ch.jamiete.bunbun.commands.superadmin.ReloadPermissionsCommand;
import ch.jamiete.bunbun.commands.superadmin.RemoveFlagCommand;
import ch.jamiete.bunbun.commands.superadmin.ShutdownCommand;
import ch.jamiete.bunbun.commands.superadmin.ThreadsCommand;
import ch.jamiete.bunbun.listeners.ConnectListener;
import ch.jamiete.bunbun.listeners.LogListener;
import ch.jamiete.bunbun.listeners.URLListener;
import ch.jamiete.bunbun.listeners.YouTubeListener;
import ch.jamiete.bunbun.permissions.PermissionListener;
import ch.jamiete.bunbun.permissions.PermissionManager;
import com.google.gson.Gson;

public class BunBun {
    private final Client client;
    private static final Logger LOGGER = Logger.getLogger("BunBun");
    private final PermissionManager permissions = new PermissionManager(this);
    private final CommandManager commander;
    private final Gson gson = new Gson();

    public BunBun(final Client client) {
        this.client = client;
        new PermissionListener(this, this.permissions);
        this.commander = new CommandManager(this);
    }

    public String combineSplit(int startIndex, String[] string, String seperator) {
        if (startIndex + 1 > string.length) {
            return "";
        }

        final StringBuilder builder = new StringBuilder();

        for (int i = startIndex; i < string.length; i++) {
            builder.append(string[i]);
            builder.append(seperator);
        }

        builder.deleteCharAt(builder.length() - seperator.length());
        return builder.toString();
    }

    public Gson getGson() {
        return this.gson;
    }

    public Client getClient() {
        return this.client;
    }

    /**
     * Searches the channels BunBun is on for a user with the exact name provided. <br>
     * If no matches are found null is returned.
     * @param name
     * @return
     */
    public User findUser(String name) {
        for (Channel channel : this.getClient().getChannels()) {
            for (User user : channel.getUsers()) {
                if (user.getNick().equalsIgnoreCase(name)) {
                    return user;
                }
            }
        }

        return null;
    }

    /**
     * Searches the channels BunBun is on for a user starting with the String provided. <br>
     * If multiple results are found null is returned. <br>
     * If no matches are found null is returned.
     * @param name
     * @return
     */
    public User findUserPartial(String name) {
        ArrayList<User> matches = new ArrayList<User>();

        for (Channel channel : this.getClient().getChannels()) {
            for (User user : channel.getUsers()) {
                if (user.getNick().toLowerCase().startsWith(name.toLowerCase())) {
                    matches.add(user);
                }
            }
        }

        if (matches.size() == 1) {
            return matches.get(0);
        }

        return null;
    }

    /**
     * Returns a *!*@* representation of the user.
     * @param user
     * @return
     */
    public String getFullUser(User user) {
        return user.getNick() + "!" + user.getUserString() + "@" + user.getHost();
    }

    public void notifyException(Exception e, Channel channel, User user, String label) {
        String message = "Exception occured while running " + label + " by " + user.getNick() + " in " + channel.getName() + ". Logged to console.";
        this.getClient().sendMessage("#jamietech", message);
        BunBun.getLogger().severe(message);
        e.printStackTrace();
    }

    public PermissionManager getPermissionManager() {
        return this.permissions;
    }

    public CommandManager getCommandManager() {
        return this.commander;
    }

    public static Logger getLogger() {
        return BunBun.LOGGER;
    }

    public void prepare() {
        /* CHANNEL COMMANDS */

        // General
        this.commander.registerChannelCommand(new GeoIPCommand(this));
        this.commander.registerChannelCommand(new HelpCommand(this));
        this.commander.registerChannelCommand(new PingCommand(this));
        this.commander.registerChannelCommand(new PermTestCommand(this));
        this.commander.registerChannelCommand(new TF2Command(this));
        this.commander.registerChannelCommand(new UrbanCommand(this));

        // Minecraft
        this.commander.registerChannelCommand(new UUIDCommand(this));
        this.commander.registerChannelCommand(new HistoryCommand(this));

        // Administration
        this.commander.registerChannelCommand(new ChannelAddCommand(this));
        this.commander.registerChannelCommand(new ChannelFlowCommand(this));
        this.commander.registerChannelCommand(new IgnoreCommand(this));

        // Super Administration
        this.commander.registerChannelCommand(new AddFlagCommand(this));
        this.commander.registerChannelCommand(new FlagPermissionsCommand(this));
        this.commander.registerChannelCommand(new ListFlagsCommand(this));
        this.commander.registerChannelCommand(new ReloadPermissionsCommand(this));
        this.commander.registerChannelCommand(new RemoveFlagCommand(this));
        this.commander.registerChannelCommand(new ShutdownCommand(this));
        this.commander.registerChannelCommand(new ThreadsCommand(this));

        /* LISTENERS */
        new ConnectListener(this);
        new LogListener(this);

        // URLs
        new URLListener(this);
        new YouTubeListener(this);
    }

}
