package ch.jamiete.bunbun;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import org.kitteh.irc.client.library.element.Channel;
import org.kitteh.irc.client.library.element.User;

public class URLFetcher extends Thread {
    private final String url;
    private final URLFetcherCallback callback;
    private final User user;
    private final Channel channel;

    public URLFetcher(String url, URLFetcherCallback callback, User user, Channel channel) {
        this.url = url;
        this.callback = callback;
        this.user = user;
        this.channel = channel;

        this.setName("URL Fetcher for " + user.getNick() + " in " + channel.getName() + " called from " + callback.getClass().getSimpleName());
    }

    @Override
    public void run() {
        try {
            URL url = new URL(this.url);
            URLConnection con = url.openConnection();

            con.setConnectTimeout(1750);
            con.setReadTimeout(3000);
            con.setRequestProperty("User-Agent", "BunBun");
            
            con.connect();
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));

            String data = "";
            String line;

            while ((line = in.readLine()) != null) {
                data += line;
            }

            in.close();

            this.callback.onFinish(data, this.user, this.channel);
        } catch (Exception e) {
            this.callback.onError(e, this.user, this.channel);
        }

    }

    public interface URLFetcherCallback {

        public void onFinish(String data, User user, Channel channel);

        public void onError(Exception e, User user, Channel channel);

    }

}
