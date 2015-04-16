package ch.jamiete.bunbun.listeners;

import org.kitteh.irc.client.library.event.channel.ChannelMessageEvent;
import org.kitteh.irc.client.library.event.user.PrivateMessageEvent;
import org.kitteh.irc.lib.net.engio.mbassy.listener.Handler;
import ch.jamiete.bunbun.BunBun;
import ch.jamiete.bunbun.EventListener;

public class CommandListener extends EventListener {

    public CommandListener(final BunBun bun) {
        super(bun);
    }

    @Handler
    public void onChannelMessage(final ChannelMessageEvent event) {

    }

    @Handler
    public void onPrivateMessage(final PrivateMessageEvent event) {

    }

}
