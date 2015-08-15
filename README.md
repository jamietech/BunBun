# BunBun IRC bot

BunBun is an IRC bot based on *Kitteh IRC Client Library* (KICL) with a built-in command manager and URL listener.

Java 8 is required to compile BunBun due to KICL.

## Commands

Commands can be either for a `Channel` or for a `Private Message`. Information regarding creating commands is outlined below.

### Generic Commands

All commands extend the `GenericCommand` class. This class allows you to set the following:
* Name (**required**, String) — the main command name (e.g. ‘help’)
* Aliases (String list) — a list of commands that this class will respond to
* Description (String) — a simple message explaining the command that is displayed with the help command
* Permission (String) — the permission required to use the command
* Silent (boolean) — whether the command will alert the user that they don’t have sufficient permission to invoke it

### Channel Commands

Channel commands implement the following method:

    public abstract void execute(User user, Channel channel, String[] arguments, String label);

Channel commands also have the following convenience methods available:

    protected void reply(User user, Channel channel, String message);

    protected void usage(User user, Channel channel, String params);

    protected void usage(User user, Channel channel, String params, String label);

Please use `usage(User, Channel, String, String)` where the user has invoked the command using an alias so that they understand why they are receiving the message.

The command prefix isn’t required in a usage reply as it is automatically provided.

The reply method prefixes the user’s nickname to the response.

### Private Commands

Private commands implement the following method:

    public abstract void execute(User user, String[] arguments);
 
Please don’t prefix responses with the user’s nickname.

### Registering Commands

Register commands in `BunBun#prepare()` under their category’s comment header. Use `this.commander.registerChannelCommmand(ChannelCommand)` or `this.commander.registerPrivateCommand(PrivateCommand)`. If you attempt to register a command name that is already registered, an `IllegalArgumentException` will be thrown.

### Quieting Channels or Users

To ignore a user, create a new instance of `Ignore` and pass it to `CommandManager#ignore(Ignore)` (non-static). When deciding whether a user is ignored, the logic in `Ignore#match(Actor)` is used.

Quiet channels aren’t currently implemented. A List\<String\> of quiet channels is kept in `CommandManager`, however there is no public method to add to this list as yet.

## Logging

To log, use the static method `BunBun.getLogger()`. Debug messages should be logged at `Level.FINE`, normal messages at `Level.INFO` and non-critical errors at `Level.WARNING`. Any errors that should cause the bot to disconnect should be logged at `Level.SEVERE`.

## Requesting URLs

A convenience class for requesting URLs is provided as `URLFetcher`. It requests the provided URL in a thread and returns it through primitive callbacks. Simply pass it `URLFetcher(String url, URLFetcherCallback callback, User user, Channel channel)` and it will call its methods when complete.

## Listening for URLs

If you wish to respond to a certain URL, listen for the `ChannelURLEvent` and set a priority. Once you have dealt with the URL call `event.setHandled(true)`. For this to work, please check that the event hasn’t been handled before continuing. The `YouTubeListener` listens at priority 99, the default listener listens at priority 0.

## Configuration

Currently no file-based configuration exists other than for permission tracking. Simply add a command-line configuration in `Start` and pass it statically to your class at the bottom of `Start`.