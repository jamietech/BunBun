package ch.jamiete.bunbun;

import org.kitteh.irc.client.library.element.Actor;
import org.kitteh.irc.client.library.element.User;

public class Ignore {
    private final String username;
    private final String hostname;

    public Ignore(final String username) {
        this(username, null);
    }

    public Ignore(final String username, final String hostname) {
        this.username = username;
        this.hostname = hostname;
    }

    public String getHostname() {
        return this.hostname;
    }

    public String getUsername() {
        return this.username;
    }

    public boolean hasHostname() {
        return this.hostname != null;
    }

    public boolean match(final Actor actor) {
        if (actor instanceof User) {
            final User user = (User) actor;
            return this.username.equalsIgnoreCase(user.getNick()) || this.hostname.equalsIgnoreCase(user.getHost());
        } else {
            return this.username.equalsIgnoreCase(actor.getName());
        }
    }

}
