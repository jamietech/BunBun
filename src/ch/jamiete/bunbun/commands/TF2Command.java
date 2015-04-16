package ch.jamiete.bunbun.commands;

import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.TimeoutException;
import org.kitteh.irc.client.library.IRCFormat;
import org.kitteh.irc.client.library.element.Channel;
import org.kitteh.irc.client.library.element.User;
import ch.jamiete.bunbun.BunBun;
import ch.jamiete.bunbun.command.ChannelCommand;
import com.github.koraktor.steamcondenser.exceptions.SteamCondenserException;
import com.github.koraktor.steamcondenser.steam.servers.GoldSrcServer;

public class TF2Command extends ChannelCommand {

    public TF2Command(BunBun bun) {
        super(bun);

        this.setName("source");
        this.setAliases(Arrays.asList(new String[] { "tf2", "css", "hl2" }));
        this.setPermission("general.source");
        this.setDescription("Grab general information about a Source server.");
    }

    @Override
    public void execute(User user, Channel channel, String[] arguments, String label) {
        if (arguments.length != 1) {
            this.usage(user, channel, "<server_ip>[:port]", label);
        } else {
            String[] arguments_sub = arguments[0].split(":");

            int port = 27015;

            if (arguments_sub.length == 2) {
                try {
                    port = Integer.parseInt(arguments_sub[1]);
                } catch (NumberFormatException e) {
                    this.reply(user, channel, "The port you specified was not a valid number.");
                    return;
                }
            }

            this.reply(user, channel, "Pinging " + arguments_sub[0] + " : " + port);
            new TF2ServerInfoFetcher(this, user, channel, arguments_sub[0], port).start();
        }
    }

    public void onFinish(TF2ServerInfo server_info, User user, Channel channel) {
        StringBuilder message = new StringBuilder();
        message.append(IRCFormat.BOLD).append(server_info.server_name).append(IRCFormat.RESET);
        message.append(" on ").append(IRCFormat.BOLD).append(server_info.map_name).append(IRCFormat.RESET);
        message.append(" with ").append(IRCFormat.BOLD).append(server_info.players).append("/").append(server_info.max_players);

        if (server_info.password) {
            message.append(" [Password Protected]");
        }

        if (!server_info.secure) {
            message.append(" [NOT VAC Secured]");
        }

        this.reply(user, channel, message.toString());
    }

    public void onError(Exception e, User user, Channel channel) {
        if (e instanceof ClassCastException || e instanceof NumberFormatException) {
            this.reply(user, channel, "Server returned invalid data.");
        } else if (e instanceof SteamCondenserException) {
            this.reply(user, channel, "Couldn't connect to server.");
        } else if (e instanceof TimeoutException) {
            this.reply(user, channel, "Server is offline.");
            return;
        } else {
            this.reply(user, channel, "Exception occured while connecting to server.");
        }

        this.bun.notifyException(e, channel, user, this.getName());
    }

    private class TF2ServerInfoFetcher extends Thread {
        private final TF2Command callback;
        private final User user;
        private final Channel channel;
        private final String ip;
        private final int port;

        public TF2ServerInfoFetcher(TF2Command callback, User user, Channel channel, String ip, int port) {
            this.callback = callback;
            this.user = user;
            this.channel = channel;
            this.ip = ip;
            this.port = port;

            this.setName("TF2 Server Info Fetcher for " + user.getNick() + " in " + channel.getName());
        }

        @Override
        public void run() {
            try {
                GoldSrcServer server = new GoldSrcServer(this.ip, this.port);
                server.updateServerInfo();

                TF2ServerInfo info = new TF2ServerInfo();
                HashMap<String, Object> map = server.getServerInfo();

                //info.network_version = (String) map.get("networkVersion");
                info.server_name = (String) map.get("serverName");
                info.map_name = (String) map.get("mapName");
                info.players = Integer.valueOf(String.valueOf(map.get("numberOfPlayers")));
                info.max_players = Integer.valueOf(String.valueOf(map.get("maxPlayers")));
                //info.players = (int) map.get("numberofPlayers");
                //info.max_players = (int) map.get("maxPlayers");
                info.password = (boolean) map.get("passwordProtected");
                info.secure = (boolean) map.get("secure");

                this.callback.onFinish(info, user, channel);
            } catch (Exception e) {
                this.callback.onError(e, user, channel);
            }
        }

    }

    @SuppressWarnings("unused")
    private class TF2ServerInfo {

        public String network_version;
        public String server_name;
        public String map_name;
        public int players;
        public int max_players;
        public boolean password;
        public boolean secure;

    }

}
