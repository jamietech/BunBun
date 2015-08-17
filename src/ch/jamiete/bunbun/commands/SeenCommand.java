package ch.jamiete.bunbun.commands;

import java.util.ArrayList;
import org.kitteh.irc.client.library.element.Channel;
import org.kitteh.irc.client.library.element.ChannelModeStatus;
import org.kitteh.irc.client.library.element.User;
import org.kitteh.irc.client.library.event.channel.ChannelJoinEvent;
import org.kitteh.irc.client.library.event.channel.ChannelMessageEvent;
import org.kitteh.irc.client.library.event.channel.ChannelPartEvent;
import org.kitteh.irc.client.library.event.user.UserNickChangeEvent;
import org.kitteh.irc.client.library.event.user.UserQuitEvent;
import org.kitteh.irc.lib.net.engio.mbassy.listener.Handler;
import ch.jamiete.bunbun.BunBun;
import ch.jamiete.bunbun.command.ChannelCommand;
import com.github.kevinsawicki.timeago.TimeAgo;

public class SeenCommand extends ChannelCommand {
    private ArrayList<SeenUser> users = new ArrayList<SeenUser>(); // TODO: Persistent tracking (e.g. save this list to disk on shutdown and reload on startup)
    private TimeAgo timeago = new TimeAgo();

    public SeenCommand(BunBun bun) {
        super(bun);

        this.setName("seen");
        this.setPermission("general.seen");
        this.setDescription("Tracks where users have been and what they have said.");

        bun.getClient().getEventManager().registerEventListener(this);
    }

    @Override
    public void execute(User user, Channel channel, String[] arguments, String label) {
        if (arguments.length != 1) {
            this.usage(user, channel, "<user>");
            return;
        }

        SeenUser seen = this.byName(arguments[0]);

        if (seen == null) {
            this.reply(user, channel, "I haven't seen " + arguments[0]);
            return;
        }

        StringBuilder response = new StringBuilder();
        response.append("I last saw ").append(seen.nickname).append(" ");

        switch (seen.action) {
            case JOIN:
                response.append("joining ");
                response.append((seen.hidden && !channel.getName().equalsIgnoreCase(seen.channel)) ? "a channel" : seen.channel);
                break;

            case MESSAGE:
                response.append("saying ");
                if (seen.hidden && !channel.getName().equalsIgnoreCase(seen.channel)) {
                    response.append("something somwhere");
                } else {
                    response.append('"').append(seen.message).append("\" in ").append(seen.channel);
                }
                break;

            case NICK_CHANGING:
                response.append("changing nick to ").append(seen.modNick);
                break;

            case NICK_CHANGED:
                response.append("changing nick from ").append(seen.modNick);
                break;

            case PART:
                response.append("leaving ");
                response.append((seen.hidden && !channel.getName().equalsIgnoreCase(seen.channel)) ? "a channel" : seen.channel);
                break;

            case QUIT:
                response.append("quitting IRC");
                if (seen.message != null && !seen.message.equals("")) {
                    response.append(" (").append(seen.message).append(")");
                }
                break;
        }

        response.append(" about ").append(timeago.timeAgo(seen.time));
    }

    @Handler
    public void onChannelMessage(ChannelMessageEvent event) {
        SeenUser user = this.isKnown(event.getActor()) ? this.byName(event.getActor().getNick()) : this.create(event.getActor().getNick());

        user.action = Action.MESSAGE;
        user.channel = event.getChannel().getName();
        user.hidden = shouldHide(event.getChannel());
        user.message = event.getMessage();
        user.time = System.currentTimeMillis();
    }

    @Handler
    public void onUserNickChange(UserNickChangeEvent event) {
        SeenUser user = this.isKnown(event.getUser()) ? this.byName(event.getUser().getNick()) : this.create(event.getUser().getNick());

        user.action = Action.NICK_CHANGING;
        user.modNick = event.getNewUser().getNick();
        user.time = System.currentTimeMillis();

        SeenUser newNick = this.create(event.getNewUser().getNick());

        newNick.action = Action.NICK_CHANGED;
        newNick.modNick = event.getUser().getNick();
        newNick.time = System.currentTimeMillis();
    }

    @Handler
    public void onChannelJoin(ChannelJoinEvent event) {
        SeenUser user = this.isKnown(event.getUser()) ? this.byName(event.getUser().getNick()) : this.create(event.getUser().getNick());

        user.action = Action.JOIN;
        user.channel = event.getChannel().getName();
        user.hidden = shouldHide(event.getChannel());
        user.time = System.currentTimeMillis();
    }

    @Handler
    public void onChannelPart(ChannelPartEvent event) {
        SeenUser user = this.isKnown(event.getUser()) ? this.byName(event.getUser().getNick()) : this.create(event.getUser().getNick());

        user.action = Action.PART;
        user.channel = event.getChannel().getName();
        user.hidden = shouldHide(event.getChannel());
        user.time = System.currentTimeMillis();
    }

    @Handler
    public void onQuit(UserQuitEvent event) {
        SeenUser user = this.isKnown(event.getUser()) ? this.byName(event.getUser().getNick()) : this.create(event.getUser().getNick());

        user.action = Action.QUIT;
        user.message = event.getMessage();
        user.time = System.currentTimeMillis();
    }

    private SeenUser create(String nick) {
        SeenUser user = new SeenUser(nick);
        this.users.add(user);
        return user;
    }

    private boolean shouldHide(Channel channel) {
        for (ChannelModeStatus status : channel.getModes().getStatuses()) {
            if (status.getMode().getChar() == 'p' || status.getMode().getChar() == 's') {
                return true;
            }
        }

        return false;
    }

    private SeenUser byName(String nick) {
        for (SeenUser seen : this.users) {
            if (seen.nickname.equalsIgnoreCase(nick)) {
                return seen;
            }
        }

        return null;
    }

    private boolean isKnown(User user) {
        return this.byName(user.getNick()) != null;
    }

    private class SeenUser {

        public String nickname;
        public String modNick;
        public Action action;
        public String message;
        public String channel;
        public boolean hidden;
        public long time;

        private SeenUser(String nickname) {
            this(nickname, null, null);
        }

        private SeenUser(String nickname, Action action) {
            this(nickname, action, null);
        }

        private SeenUser(String nickname, Action action, String channel) {
            this.nickname = nickname;
            this.action = action;
            this.channel = channel;
        }

    }

    private enum Action {

        MESSAGE, NICK_CHANGING, NICK_CHANGED, JOIN, PART, QUIT;

    }

}
