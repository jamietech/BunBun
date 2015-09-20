package ch.jamiete.bunbun.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.kitteh.irc.client.library.element.Actor;
import org.kitteh.irc.client.library.element.Channel;
import org.kitteh.irc.client.library.element.User;
import org.kitteh.irc.client.library.event.channel.ChannelMessageEvent;
import org.kitteh.irc.client.library.event.user.PrivateMessageEvent;
import org.kitteh.irc.client.library.util.Sanity;
import org.kitteh.irc.lib.net.engio.mbassy.listener.Handler;
import ch.jamiete.bunbun.BunBun;
import ch.jamiete.bunbun.EventListener;
import ch.jamiete.bunbun.Ignore;
import ch.jamiete.bunbun.permissions.Flag;

public class CommandManager extends EventListener {
    private final List<ChannelCommand> channelcommands;
    private final List<PrivateCommand> privatecommands;
    private final List<String> quiet;
    private final List<Ignore> ignored;
    public static final String PREFIX = "!";

    public CommandManager(final BunBun bun) {
        super(bun);

        this.channelcommands = new ArrayList<ChannelCommand>();
        this.privatecommands = new ArrayList<PrivateCommand>();

        this.quiet = new ArrayList<String>();
        this.ignored = new ArrayList<Ignore>();
    }

    public List<String> cleanChannelAliases(final List<String> aliases) {
        Sanity.nullCheck(aliases, "Cannot supply null object.");

        final List<String> clean = new ArrayList<String>();

        for (final String alias : aliases) {
            if (!this.isChannelCommand(alias)) {
                clean.add(alias);
            }
        }

        return clean;
    }

    public List<String> cleanPrivateAliases(final List<String> aliases) {
        Sanity.nullCheck(aliases, "Cannot supply null object.");

        final List<String> clean = new ArrayList<String>();

        for (final String alias : aliases) {
            if (!this.isPrivateCommand(alias)) {
                clean.add(alias);
            }
        }

        return clean;
    }

    public ChannelCommand getChannelCommand(final String label) {
        for (final ChannelCommand command : this.channelcommands) {
            if (command.getName().equalsIgnoreCase(label) || command.hasAlias(label)) {
                return command;
            }
        }

        return null;
    }

    public PrivateCommand getPrivateCommand(final String label) {
        for (final PrivateCommand command : this.privatecommands) {
            if (command.getName().equalsIgnoreCase(label) || command.hasAlias(label)) {
                return command;
            }
        }

        return null;
    }

    public ChannelCommand[] getChannelCommands() {
        return this.channelcommands.toArray(new ChannelCommand[this.channelcommands.size()]);
    }

    public PrivateCommand[] getPrivateCommands() {
        return this.privatecommands.toArray(new PrivateCommand[this.privatecommands.size()]);
    }

    public boolean isChannelCommand(final String label) {
        return this.getChannelCommand(label) != null;
    }

    public boolean isIgnored(final Actor actor) {
        for (final Ignore ignore : this.ignored) {
            if (ignore.match(actor)) {
                return true;
            }
        }

        return false;
    }

    public void ignore(final Ignore ignore) {
        this.ignored.add(ignore);
    }

    public boolean isPrivateCommand(final String label) {
        return this.getPrivateCommand(label) != null;
    }

    @Handler
    public void onChannelMessage(final ChannelMessageEvent event) {
        long start = System.currentTimeMillis();
        BunBun.getLogger().fine("Executing command " + event.getMessage() + " by " + this.getBun().getFullUser(event.getActor()) + "...");

        if (this.quiet.contains(event.getChannel().getName().toLowerCase()) || this.isIgnored(event.getActor())) {
            return;
        }

        String[] args = event.getMessage().split(" ");

        if (args[0].length() > 0 && args[0].startsWith(CommandManager.PREFIX) || args[0].startsWith(this.getBun().getClient().getNick())) {
            String label;
            Channel channel = event.getChannel();

            if (this.getBun().getPermissionManager().getUser(event.getActor()).hasFlag(this.getBun().getPermissionManager().getFlag(Flag.ADMIN))) {
                boolean chan_starts = false;

                for (Character c : this.getBun().getClient().getServerInfo().getChannelPrefixes()) {
                    if (args[0].startsWith(CommandManager.PREFIX + c)) {
                        chan_starts = true;
                        break;
                    }
                }

                if (chan_starts) {
                    String search = args[0].substring(1);
                    boolean found = false;

                    for (Channel c : this.getBun().getClient().getChannels()) {
                        if (c.getName().equalsIgnoreCase(search)) {
                            channel = c;
                            args = Arrays.copyOfRange(args, 1, args.length);
                            found = true;
                            break;
                        }
                    }

                    if (!found) {
                        event.getActor().sendNotice("I'm not on that channel. Aborting command performance.");
                        return;
                    }
                }
            }

            if (args[0].startsWith(this.getBun().getClient().getNick())) {
                label = args[1];
                args = Arrays.copyOfRange(args, 2, args.length);
            } else {
                label = args[0].substring(1);
                args = Arrays.copyOfRange(args, 1, args.length);
            }

            try {
                if (this.isChannelCommand(label)) {
                    if (!(event.getActor() instanceof User)) {
                        throw new IllegalArgumentException("No user passed: " + event.toString());
                    } // Not needed on latest.

                    User user = event.getActor();
                    ChannelCommand command = this.getChannelCommand(label);

                    if (command.hasPermission()) {
                        if (this.getBun().getPermissionManager().hasPermission(user, command.getPermission())) {
                            command.execute(user, channel, args, label);
                        } else {
                            if (!command.getSilent()) { // TODO: Per-channel configuration overriding silences
                                event.getChannel().sendMessage(user.getNick() + ": You don't have permission.");
                            } else {
                                user.sendNotice("You don't have permission.");
                            }
                        }
                    } else {
                        command.execute(user, channel, args, label);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                this.getBun().getClient().sendMessage("#jamietech", "Exception occured while processing " + args[0] + " from " + event.getActor().toString());
            }
        }

        BunBun.getLogger().fine("Finished command execution in " + (System.currentTimeMillis() - start) + "ms");
    }

    @Handler
    public void onPrivateMessage(final PrivateMessageEvent event) {
        if (this.isIgnored(event.getActor())) {
            return;
        }

        final String[] args = event.getMessage().split(" ");

        try {
            if (this.isPrivateCommand(args[0])) {
                if (!(event.getActor() instanceof User)) {
                    throw new IllegalArgumentException("No user passed: " + event.toString());
                }

                this.getPrivateCommand(args[0]).execute(event.getActor(), args);
            }
        } catch (Exception e) {
            e.printStackTrace();
            this.getBun().getClient().sendMessage("#jamietech", "Exception occured while processing " + args[0] + " from " + event.getActor().toString());
        }
    }

    public void registerChannelCommand(final ChannelCommand command) {
        Sanity.nullCheck(command.getName(), "Command must be named.");
        Sanity.truthiness(!this.channelcommands.contains(command), "Cannot register duplicate command.");
        Sanity.truthiness(!this.isChannelCommand(command.getName()), "Command name is already registered.");

        if (command.getAliases() != null) {
            command.setAliases(this.cleanChannelAliases(command.getAliases()));
            command.aliases_final = true;
        }

        this.channelcommands.add(command);
        BunBun.getLogger().info("Registered channel command " + command.getName() + (command.getAliases() != null ? this.getBun().combineSplit(0, command.getAliases().toArray(new String[command.getAliases().size()]), ", ") : ""));
    }

    public void registerPrivateCommand(final PrivateCommand command) {
        Sanity.nullCheck(command.getName(), "Command must be named.");
        Sanity.truthiness(!this.privatecommands.contains(command), "Cannot register duplicate command.");
        Sanity.truthiness(!this.isPrivateCommand(command.getName()), "Command name is already registered.");

        if (command.getAliases() != null) {
            command.setAliases(this.cleanPrivateAliases(command.getAliases()));
        }

        this.privatecommands.add(command);
        BunBun.getLogger().info("Registered private command " + command.getName() + (command.getAliases() != null ? this.getBun().combineSplit(0, command.getAliases().toArray(new String[command.getAliases().size()]), ", ") : ""));
    }

}
