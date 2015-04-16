package ch.jamiete.bunbun.commands;

import java.io.FileNotFoundException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import org.kitteh.irc.client.library.IRCFormat;
import org.kitteh.irc.client.library.element.Channel;
import org.kitteh.irc.client.library.element.User;
import ch.jamiete.bunbun.BunBun;
import ch.jamiete.bunbun.URLFetcher;
import ch.jamiete.bunbun.URLFetcher.URLFetcherCallback;
import ch.jamiete.bunbun.command.ChannelCommand;

public class GeoIPCommand extends ChannelCommand implements URLFetcherCallback {

    public GeoIPCommand(BunBun bun) {
        super(bun);

        this.setName("geoip");
        this.setPermission("general.geoip");
        this.setDescription("Looks up the location of an IP or an online user.");
    }

    @Override
    public void execute(User user, Channel channel, String[] arguments, String label) {
        if (arguments.length != 1) {
            this.usage(user, channel, "<IP address/online user>");
        } else {
            User target = this.bun.findUser(arguments[0]);
            InetAddress address;

            try {
                address = InetAddress.getByName(target == null ? arguments[0] : target.getHost());
            } catch (UnknownHostException e) {
                this.bun.notifyException(e, channel, user, label);
                this.reply(user, channel, "The IP address supplied was not valid.");
                return;
            }

            new URLFetcher("http://ipinfo.io/" + address.getHostAddress() + "/json", this, user, channel).run();
        }
    }

    @Override
    public void onFinish(String data, User user, Channel channel) {
        /*if (data.equalsIgnoreCase("Please provide a valid IP address")) {
            this.reply(user, channel, "The IP address supplied was not valid.");
            return;
        } Returns 404 */

        GeoIPCommandResponse response = this.bun.getGson().fromJson(data, GeoIPCommandResponse.class);
        StringBuilder message = new StringBuilder();

        message.append("IP " + IRCFormat.BOLD + response.ip + IRCFormat.BOLD + " ");

        if (response.hostname != null) {
            message.append("(" + response.hostname + ") ");
        }

        if (response.city == null && response.country == null) {
            message.append("couldn't be located. ");
        } else {
            message.append("is located (approximately) in: ");
        }

        if (response.city != null) {
            message.append(response.city + ", ");
        }

        if (response.region != null) {
            message.append(response.region + ", ");
        }

        if (response.country != null) {
            message.append(response.country + ". ");
        }

        if (response.org != null) {
            message.append("Owned/administered by " + response.org);
        }

        this.reply(user, channel, message.toString());
    }

    @Override
    public void onError(Exception e, User user, Channel channel) {
        if (e instanceof FileNotFoundException) {
            this.reply(user, channel, "The IP address supplied was not valid.");
            return;
        }

        this.reply(user, channel, "Error occured in attempt to fetch GeoIP.");
        this.bun.notifyException(e, channel, user, this.getName());
    }

    private class GeoIPCommandResponse {

        String ip;
        String hostname;
        String city;
        String region;
        String country;
        String org;

    }

}
