package ch.jamiete.bunbun.listeners;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import org.kitteh.irc.client.library.event.client.ClientConnectedEvent;
import org.kitteh.irc.lib.net.engio.mbassy.listener.Handler;
import ch.jamiete.bunbun.BunBun;
import ch.jamiete.bunbun.EventListener;

public class ConnectListener extends EventListener {

    public ConnectListener(BunBun bun) {
        super(bun);
    }

    @Handler
    public void onConnect(ClientConnectedEvent event) {
        BunBun.getLogger().info("Connected! Attempting to join channels...");

        File file = new File("channels.txt");
        int attempted = 0;

        try {
            if (!file.exists()) {
                file.createNewFile();
                BunBun.getLogger().warning("Channel list didn't exist. Created an empty one.");
            }

            for (String line : Files.readAllLines(file.toPath())) {
                this.getBun().getClient().addChannel(line);
                attempted++;
            }
        } catch (IOException e) {
            BunBun.getLogger().severe("Failed to read channel list.");
        }

        BunBun.getLogger().info("Attempted to join " + attempted + " channels.");
    }
}
