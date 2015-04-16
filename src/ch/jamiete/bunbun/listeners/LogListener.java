package ch.jamiete.bunbun.listeners;


import org.kitteh.irc.client.library.event.channel.ChannelJoinEvent;
import org.kitteh.irc.client.library.event.channel.ChannelKickEvent;
import org.kitteh.irc.client.library.event.channel.ChannelPartEvent;
import org.kitteh.irc.lib.net.engio.mbassy.listener.Handler;
import ch.jamiete.bunbun.BunBun;
import ch.jamiete.bunbun.EventListener;

public class LogListener extends EventListener {

    public LogListener(BunBun bun) {
        super(bun);
    }

    @Handler
    public void onChannelJoin(ChannelJoinEvent event) {
        if (event.getActor().getNick().equals(this.getBun().getClient().getNick())) {
            BunBun.getLogger().info("Joined channel " + event.getChannel().getName());
        }
    }

    @Handler
    public void onChannelPart(ChannelPartEvent event) {
        if (event.getActor().getNick().equals(this.getBun().getClient().getNick())) {
            BunBun.getLogger().info("Left channel " + event.getChannel().getName());
        }
    }

    @Handler
    public void onChannelKick(ChannelKickEvent event) {
        if (event.getTarget().getNick().equalsIgnoreCase(this.getBun().getClient().getNick())) {
            BunBun.getLogger().info("Kicked from channel " + event.getChannel().getName() + " for " + event.getMessage());
        }
    }

}
