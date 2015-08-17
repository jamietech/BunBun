package ch.jamiete.bunbun.commands;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import org.kitteh.irc.client.library.IRCFormat;
import org.kitteh.irc.client.library.element.Channel;
import org.kitteh.irc.client.library.element.User;
import ch.jamiete.bunbun.BunBun;
import ch.jamiete.bunbun.URLFetcher;
import ch.jamiete.bunbun.URLFetcher.URLFetcherCallback;
import ch.jamiete.bunbun.command.ChannelCommand;

public class UrbanCommand extends ChannelCommand implements URLFetcherCallback {

    public UrbanCommand(BunBun bun) {
        super(bun);

        this.setName("urban");
        this.setPermission("general.urban");
        this.setDescription("Looks up the definition of a word or phrase on Urban Dictionary.");
    }

    @Override
    public void execute(User user, Channel channel, String[] arguments, String label) {
        if (arguments.length == 0) {
            this.usage(user, channel, "<word(s)>");
        } else {
            try {
                new URLFetcher("http://api.urbandictionary.com/v0/define?term=" + URLEncoder.encode(this.bun.combineSplit(0, arguments, " "), "UTF-8"), this, user, channel).run();
            } catch (UnsupportedEncodingException e) {
                this.reply(user, channel, "Failed.");
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onFinish(String data, User user, Channel channel) {
        UrbanResponse response = this.bun.getGson().fromJson(data, UrbanResponse.class);

        if (response.list == null) {
            this.reply(user, channel, "Couldn't find a definition for that word.");
            return;
        }

        Word word = response.list[0];
        StringBuilder message = new StringBuilder();

        message.append(IRCFormat.BOLD).append(word.word).append(": ").append(IRCFormat.RESET);
        message.append(word.definition.replace("\r\n", " ").replace("\r", " ").replace("\n", " ").replace("\0", ""));

        message.append("\n").append(IRCFormat.BOLD).append("Example: ").append(IRCFormat.RESET);
        message.append(word.example);

        for (String send : message.toString().split("\n")) {
            if (message.equals("")) {
                continue;
            }

            this.reply(user, channel, send);
        }
    }

    @Override
    public void onError(Exception e, User user, Channel channel) {
        this.reply(user, channel, "Error occured in attempt to look up definition.");
        this.bun.notifyException(e, channel, user, this.getName());
    }

    private class UrbanResponse {

        public Word[] list;

    }

    private class Word {

        public String word;
        public String definition;
        public String example;

    }

}
