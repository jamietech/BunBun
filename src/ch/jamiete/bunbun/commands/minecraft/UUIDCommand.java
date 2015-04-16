package ch.jamiete.bunbun.commands.minecraft;

import java.util.Arrays;
import org.kitteh.irc.client.library.element.Channel;
import org.kitteh.irc.client.library.element.User;
import ch.jamiete.bunbun.BunBun;
import ch.jamiete.bunbun.URLFetcher;
import ch.jamiete.bunbun.URLFetcher.URLFetcherCallback;
import ch.jamiete.bunbun.command.ChannelCommand;

public class UUIDCommand extends ChannelCommand implements URLFetcherCallback {
    private final String regex = "[a-zA-Z0-9_]{3,16}";

    public UUIDCommand(BunBun bun) {
        super(bun);

        this.setName("uuid");
        this.setPermission("minecraft.uuid");
        this.setAliases(Arrays.asList(new String[] { "mcuuid" }));
        this.setDescription("Fetches a player's UUID");
    }

    @Override
    public void execute(User user, Channel channel, String[] arguments, String label) {
        if (arguments.length != 1) {
            this.usage(user, channel, "<current_username>", label);
        } else {
            if (arguments[0].matches(this.regex)) {
                new URLFetcher("http://api.fishbans.com/uuid/" + arguments[0], this, user, channel).start();
            } else {
                this.reply(user, channel, "Invalid username supplied.");
            }
        }
    }

    @Override
    public void onFinish(String data, User user, Channel channel) {
        UUIDCommandResponse response = this.bun.getGson().fromJson(data, UUIDCommandResponse.class);

        if (response.success) {
            this.reply(user, channel, response.uuid);
        } else {
            this.reply(user, channel, "API error occurred in attempt to fetch UUID.");
        }
    }

    @Override
    public void onError(Exception e, User user, Channel channel) {
        this.reply(user, channel, "Error occured in attempt to fetch UUID.");

        //this.bun.getClient().sendMessage("#jamietech", String.format("Error occured in UUIDCommand triggered by %s in %s. Stack trace printed.", user.getNick(), channel.getName()));
        //e.printStackTrace();
        this.bun.notifyException(e, channel, user, this.getName());
    }

    private class UUIDCommandResponse {

        public boolean success = false;
        public String uuid;

    }

}
