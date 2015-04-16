package ch.jamiete.bunbun.permissions;


import org.kitteh.irc.client.library.element.Channel;
import org.kitteh.irc.client.library.element.User;
import org.kitteh.irc.client.library.event.channel.ChannelJoinEvent;
import org.kitteh.irc.client.library.event.channel.ChannelKickEvent;
import org.kitteh.irc.client.library.event.channel.ChannelPartEvent;
import org.kitteh.irc.client.library.event.channel.ChannelUsersUpdatedEvent;
import org.kitteh.irc.client.library.event.client.ClientConnectionClosedEvent;
import org.kitteh.irc.client.library.event.user.UserQuitEvent;
import org.kitteh.irc.lib.net.engio.mbassy.listener.Handler;
import ch.jamiete.bunbun.BunBun;
import ch.jamiete.bunbun.EventListener;

public class PermissionListener extends EventListener {
    private final PermissionManager manager;

    public PermissionListener(BunBun bun, PermissionManager manager) {
        super(bun);

        this.manager = manager;
    }

    @Handler
    public void onClientConnectionClosed(ClientConnectionClosedEvent event) {
        for (Channel channel : this.getBun().getClient().getChannels()) {
            for (User user : channel.getUsers()) {
                manager.untrack(user);
                this.getBun();
                BunBun.getLogger().info("Untracked " + user.getNick() + " because the server connection closed.");
            }
        }
    }

    @Handler
    public void onChannelUsersUpdated(ChannelUsersUpdatedEvent event) {
        for (User user : event.getChannel().getUsers()) {
            manager.track(user);
        }
    }

    @Handler
    public void onChannelJoin(ChannelJoinEvent event) {
        manager.track(event.getActor());
    }

    @Handler
    public void onChannelPart(ChannelPartEvent event) {
        if (!isTrackable(event.getActor())) {
            manager.untrack(event.getActor());
            this.getBun();
            BunBun.getLogger().info("Untracked " + event.getActor().getNick() + " because they parted last channel.");
        }
    }

    @Handler
    public void onChannelKick(ChannelKickEvent event) {
        for (Channel channel : this.getBun().getClient().getChannels()) {
            if (channel.getUsers().contains(event.getTarget())) {
                return; // Still on a channel
            }
        } // If for loop exists, no longer on a channel
        
        manager.untrack(event.getTarget());
        BunBun.getLogger().info("Untracked " + event.getTarget().getNick() + " because they were kicked from last channel.");
        
        /*User user = this.getBun().findUser(event.getTarget());

        //if (user == null || !isTrackable(user)) { - If a user is null they're not on a channel, i.e. not trackable
        if (event.getTarget() == null) {
            user = manager.findKicked(event.getTarget());

            if (event.getTarget() == null) {
                BunBun.getLogger().severe("Failed to find user " + event.getTarget().getNick() + " when they were kicked from last channel.");
                return;
            }

            manager.untrack(event.getTarget());
            BunBun.getLogger().info("Untracked " + event.getTarget().getNick() + " because they were kicked from last channel.");
        }*/
    }

    @Handler
    public void onUserQuit(UserQuitEvent event) {
        if (!isTrackable(event.getActor())) {
            manager.untrack(event.getActor());
            this.getBun();
            BunBun.getLogger().info("Untracked " + event.getActor().getNick() + " because they quit.");
        }
    }


    private boolean isTrackable(User user) {
        for (Channel channel : this.getBun().getClient().getChannels()) {
            if (channel.getUsers().contains(user)) {
                return true;
            }
        }

        return false;
    }

}
