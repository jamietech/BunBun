package ch.jamiete.bunbun;

import java.util.function.Consumer;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import org.kitteh.irc.client.library.Client;
import org.kitteh.irc.client.library.ClientBuilder;
import org.kitteh.irc.client.library.auth.protocol.GameSurge;
import org.kitteh.irc.client.library.auth.protocol.NickServ;
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

    @Parameter(names = { "-user", "-username", "-u" }, description = "Account to auth to")
    private String username;

    @Parameter(names = { "-pass", "-password" }, description = "Auth account password")
    private String password;

    @Parameter(names = { "-auth", "-authtype", "-a" }, description = "Auth service (GameSurge/NickServ)")
    private String authtype;

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

        final ClientBuilder builder = Client.builder();

        builder.name("BunBun").nick("BunBun").user("BunBun");
        builder.realName("BunBun — jamietech’s IRC bot : #jamietech ABUSE/COMPLAINTS");
        builder.server(this.network).server(this.port);
        builder.secure(this.ssl);

        // TODO: Exception consumer

        if (this.server_password != null) {
            builder.serverPassword(this.server_password);
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

        builder.after(new Consumer<Client>() {

            @Override
            public void accept(Client client) {
                if (Start.this.username != null && Start.this.password != null) {
                    if (Start.this.authtype.equalsIgnoreCase("GameSurge")) {
                        client.getAuthManager().addProtocol(new GameSurge(client, Start.this.username, Start.this.password));
                        BunBun.getLogger().fine("Added GameSurge authentication protocol");
                    }

                    if (Start.this.authtype.equalsIgnoreCase("NickServ")) {
                        client.getAuthManager().addProtocol(new NickServ(client, Start.this.username, Start.this.password));
                        BunBun.getLogger().fine("Added NickServ authentication protocol");
                    }
                }
            }

        });

        new BunBun(builder.build()).prepare();
    }
}
