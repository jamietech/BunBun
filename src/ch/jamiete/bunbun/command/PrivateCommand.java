package ch.jamiete.bunbun.command;

import org.kitteh.irc.client.library.element.User;
import ch.jamiete.bunbun.BunBun;

public abstract class PrivateCommand extends GenericCommand {

    protected PrivateCommand(final BunBun bun) {
        super(bun);
    }

    public abstract void execute(User user, String[] arguments);

}
