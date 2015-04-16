package ch.jamiete.bunbun.commands.minecraft;

import java.util.Arrays;
import org.kitteh.irc.client.library.element.Channel;
import org.kitteh.irc.client.library.element.User;
import ch.jamiete.bunbun.BunBun;
import ch.jamiete.bunbun.URLFetcher;
import ch.jamiete.bunbun.URLFetcher.URLFetcherCallback;
import ch.jamiete.bunbun.command.ChannelCommand;

public class HistoryCommand extends ChannelCommand implements URLFetcherCallback {
    private final String regex = "[a-zA-Z0-9_]{3,16}";

    public HistoryCommand(BunBun bun) {
        super(bun);

        this.setName("history");
        this.setPermission("minecraft.history");
        this.setAliases(Arrays.asList(new String[] { "mchistory" }));
        this.setDescription("Fetches a player's username history.");
    }

    @Override
    public void execute(User user, Channel channel, String[] arguments, String label) {
        if (arguments.length != 1) {
            this.usage(user, channel, "<current_username>", label);
        } else {
            if (arguments[0].matches(this.regex)) {
                new URLFetcher("http://api.fishbans.com/history/" + arguments[0], this, user, channel).start();
            } else {
                this.reply(user, channel, "Invalid username supplied.");
            }
        }
    }

    @Override
    public void onFinish(String data, User user, Channel channel) {
        HistoryCommandResponse response = this.bun.getGson().fromJson(data, HistoryCommandResponse.class);

        if (response.success) {
            if (response.data.history.length <= 1) {
                this.reply(user, channel, response.data.username + " (" + response.data.uuid + ") has not changed their name.");
            } else {
                int changes = response.data.history.length;
                this.reply(user, channel, response.data.username + " (" + response.data.uuid + ") has " + changes + " username " + (changes == 1 ? "change" : "changes") + " logged:");

                String reply = "";
                for (String username : response.data.history) {
                    reply += username + ", ";
                }
                reply = reply.substring(0, reply.length() - 2);

                this.reply(user, channel, reply);
            }
        } else {
            this.reply(user, channel, "API error occurred in attempt to fetch user history.");
        }
    }

    @Override
    public void onError(Exception e, User user, Channel channel) {
        this.reply(user, channel, "Error occured in attempt to fetch user history.");

        //this.bun.getClient().sendMessage("#jamietech", String.format("Error occured in UUIDCommand triggered by %s in %s. Stack trace printed.", user.getNick(), channel.getName()));
        //e.printStackTrace();
        this.bun.notifyException(e, channel, user, this.getName());
    }

    private class HistoryCommandResponse {

        public boolean success = false;
        public HistoryCommandResponseData data;


    }

    private class HistoryCommandResponseData {

        public String username;
        public String uuid;
        public String[] history;

    }

}
