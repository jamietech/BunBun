package ch.jamiete.bunbun;

import java.util.function.Consumer;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import org.kitteh.irc.client.library.AuthType;
import org.kitteh.irc.client.library.ClientBuilder;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

public class Start {
    public static void main(final String[] args) {
        new Start().start(args);
    }

    @Parameter(names = { "-network", "-n" }, description = "IRC server hostname to connect to", required = true)
    private String network;

    @Parameter(names = { "-port", "-p" }, description = "IRC server port to connect to", required = true)
    private int port;

    @Parameter(names = { "-secure", "-ssl", "-s" }, description = "Connect to IRC over SSL?")
    private final boolean ssl = false;

    @Parameter(names = { "-spass", "-serverpassword", "-sp" }, description = "IRC server password")
    private String server_password;

    @Parameter(names = { "-user", "-username", "-u" }, description = "NickServ account to auth to")
    private String username;

    @Parameter(names = { "-pass", "-password" }, description = "NickServ account password")
    private String password;

    @Parameter(names = { "-debug" }, description = "Verbose output?")
    private final boolean debug = false;

    public void start(final String[] args) {
        new JCommander(this, args);

        BunBun.getLogger().setUseParentHandlers(false);
        ConsoleHandler handler = new ConsoleHandler();
        handler.setFormatter(new LogFormat());
        BunBun.getLogger().addHandler(handler);

        if (debug) {
            BunBun.getLogger().setLevel(Level.FINE);
        }

        BunBun.getLogger().info("Connecting to " + this.network + ":" + this.port);

        final ClientBuilder builder = new ClientBuilder();
        builder.name("BunBun").nick("BunBun").user("BunBun");
        builder.realName("BunBun — jamietech’s IRC bot : #jamietech ABUSE/COMPLAINTS");
        builder.server(this.network).server(this.port);
        builder.secure(this.ssl);

        // TODO: Exception consumer

        if (this.server_password != null) {
            builder.serverPassword(this.server_password);
        }

        if (this.username != null && this.password != null) {
            builder.auth(AuthType.NICKSERV, this.username, this.password);
        }

        builder.listenInput(new Consumer<String>() {

            @Override
            public void accept(String t) {
                BunBun.getLogger().log(Level.FINE, "> " + t);
            }

        });

        builder.listenOutput(new Consumer<String>() {

            @Override
            public void accept(String t) {
                BunBun.getLogger().log(Level.FINE, "< " + t);
            }

        });

        new BunBun(builder.build()).prepare();
    }

}
