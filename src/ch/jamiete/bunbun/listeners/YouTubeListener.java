package ch.jamiete.bunbun.listeners;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.kitteh.irc.client.library.IRCFormat;
import org.kitteh.irc.client.library.element.Channel;
import org.kitteh.irc.client.library.element.User;
import org.kitteh.irc.lib.net.engio.mbassy.listener.Handler;
import ch.jamiete.bunbun.BunBun;
import ch.jamiete.bunbun.EventListener;
import ch.jamiete.bunbun.URLFetcher;
import ch.jamiete.bunbun.URLFetcher.URLFetcherCallback;
import ch.jamiete.bunbun.listeners.URLListener.ChannelURLEvent;

public class YouTubeListener extends EventListener implements URLFetcherCallback {
    private final Pattern pattern = Pattern.compile("(?:https?:\\/\\/)?(?:youtu\\.be\\/|(?:www\\.)?youtube\\.com\\/watch(?:\\.php)?\\?.*v=)([a-zA-Z0-9\\-_]+)");
    public static String KEY;

    public YouTubeListener(BunBun bun) {
        super(bun);
    }

    @Handler(priority = 99)
    public void onChannelURLEvent(ChannelURLEvent event) {
        if (YouTubeListener.KEY == null) {
            return;
        }

        Matcher matcher = pattern.matcher(event.getUrlString());

        if (matcher.matches()) {
            new URLFetcher("https://www.googleapis.com/youtube/v3/videos?part=snippet%2Cstatistics%2CcontentDetails&key=" + YouTubeListener.KEY + "&id=" + matcher.group(1), this, event.getUser(), event.getChannel()).start();
            event.setHandled(true);
        }
    }

    @Override
    public void onFinish(String data, User user, Channel channel) {
        YouTubeResponse response = this.getBun().getGson().fromJson(data, YouTubeResponse.class);

        if (response.items != null && response.items.length == 1) {
            Item item = response.items[0];
            StringBuilder reply = new StringBuilder();

            reply.append(IRCFormat.BOLD).append(item.snippet.title).append(IRCFormat.RESET);
            reply.append(" by ").append(IRCFormat.BOLD).append(item.snippet.channelTitle != null ? item.snippet.channelTitle : item.snippet.channelId).append(IRCFormat.RESET);

            if (item.contentDetails.definition.equalsIgnoreCase("hd")) {
                reply.append(" (HD)");
            }

            if (!item.contentDetails.dimension.equalsIgnoreCase("2d")) {
                reply.append(" (").append(item.contentDetails.dimension.toUpperCase()).append(")");
            }

            reply.append(" ").append(item.statistics.viewCount).append(" ").append(item.statistics.viewCount == 1 ? "view" : "views");
            reply.append(" (").append(item.statistics.likeCount).append(" ").append(item.statistics.likeCount == 1 ? "like" : "likes").append(", ");
            reply.append(item.statistics.dislikeCount).append(" ").append(item.statistics.dislikeCount == 1 ? "dislike" : "dislikes").append(")");

            this.getBun().getClient().sendMessage(channel, reply.toString());
        }
    }

    @Override
    public void onError(Exception e, User user, Channel channel) {
        e.printStackTrace();
        this.getBun().getClient().sendMessage(channel, "Error fetching YouTube video information.");
    }

    private class YouTubeResponse {

        public Item[] items;

    }

    private class Item {

        public Snippet snippet;
        public ContentDetails contentDetails;
        public Statistics statistics;

    }

    private class Snippet {

        public String channelTitle;
        public String channelId;
        public String title;

    }

    private class ContentDetails {

        public String definition;
        public String dimension;

    }

    private class Statistics {

        public int viewCount;
        public int likeCount;
        public int dislikeCount;

    }
}
