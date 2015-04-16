package ch.jamiete.bunbun.commands.superadmin;

import org.kitteh.irc.client.library.IRCFormat;
import org.kitteh.irc.client.library.element.Channel;
import org.kitteh.irc.client.library.element.User;
import ch.jamiete.bunbun.BunBun;
import ch.jamiete.bunbun.command.ChannelCommand;

public class ThreadsCommand extends ChannelCommand {

    public ThreadsCommand(BunBun bun) {
        super(bun);

        this.setName("threads");
        this.setDescription("Lists all active threads");
        this.setPermission("superadmin.threads");
        this.setSilent(true);
    }

    @Override
    public void execute(User user, Channel channel, String[] arguments, String label) {
        ThreadGroup group = Thread.currentThread().getThreadGroup();
        int no_threads = group.activeCount();

        this.reply(user, channel, "Found " + no_threads + " active threads (estimated):");

        Thread[] threads = new Thread[no_threads];
        group.enumerate(threads);

        for (int i = 0; i < no_threads; i++) {
            Thread thread = threads[i];
            StringBuilder message = new StringBuilder();

            message.append(" -> ").append(thread.getName());
            message.append(" ").append(thread.isAlive() ? IRCFormat.GREEN + "Alive" : IRCFormat.RED + "Dead");
            message.append(IRCFormat.RESET).append(" State: ").append(thread.getState().name());

            this.bun.getClient().sendMessage(channel, message.toString());
        }

        this.bun.getClient().sendMessage(channel, "End list.");
    }

}
