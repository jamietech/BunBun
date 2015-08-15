package ch.jamiete.bunbun.listeners;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.kitteh.irc.client.library.Client;
import org.kitteh.irc.client.library.element.Channel;
import org.kitteh.irc.client.library.element.User;
import org.kitteh.irc.client.library.event.abstractbase.ChannelEventBase;
import org.kitteh.irc.client.library.event.channel.ChannelMessageEvent;
import org.kitteh.irc.lib.net.engio.mbassy.listener.Handler;
import ch.jamiete.bunbun.BunBun;
import ch.jamiete.bunbun.EventListener;

public class URLListener extends EventListener {
    private static final Pattern REGEX = Pattern.compile("[(http(s)?):\\/\\/(www\\.)?a-zA-Z0-9@:%._\\+~#=]{2,256}\\.[a-z]{2,6}\\b([-a-zA-Z0-9@:%_\\+.~#?&//=]*)", Pattern.CASE_INSENSITIVE);

    public URLListener(BunBun bun) {
        super(bun);
    }

    @Handler
    public void onChannelMessage(ChannelMessageEvent event) {
        if (event.getMessage().trim().length() > 0) {
            for (String part : event.getMessage().split(" ")) {
                Matcher matcher = REGEX.matcher(part);

                if (matcher.matches()) {
                    try {
                        ChannelURLEvent urlEvent = new ChannelURLEvent(event.getClient(), part, event.getActor(), event.getChannel());
                        event.getClient().getEventManager().callEvent(urlEvent);
                    } catch (MalformedURLException e) {
                        BunBun.getLogger().warning("Malformed URL: " + part);
                    }
                }
            }
        }
    }

    @Handler(priority = 0)
    public void onChannelURLEvent(ChannelURLEvent event) {
        if (event.getHandled()) {
            return;
        }

        try {
            String title = TitleExtractor.getPageTitle(event.getUrlString());
            event.getChannel().sendMessage(title);
        } catch (IOException e) {
            //
        }
    }

    public class ChannelURLEvent extends ChannelEventBase {
        private final String urlstr;
        private final URL url;
        private final User user;
        private boolean handled = false;

        protected ChannelURLEvent(Client client, String urlstr, User user, Channel channel) throws MalformedURLException {
            super(client, channel);

            this.urlstr = urlstr;
            this.url = new URL(urlstr);
            this.user = user;
        }

        public URL getUrl() {
            return this.url;
        }

        public String getUrlString() {
            return this.urlstr;
        }

        public User getUser() {
            return this.user;
        }

        public boolean getHandled() {
            return this.handled;
        }

        public void setHandled(boolean handled) {
            this.handled = handled;
        }

    }

    public static class TitleExtractor {
        /* the CASE_INSENSITIVE flag accounts for
         * sites that use uppercase title tags.
         * the DOTALL flag accounts for sites that have
         * line feeds in the title text */
        private static final Pattern TITLE_TAG = Pattern.compile("\\<title>(.*)\\</title>", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

        /**
         * @param url the HTML page
         * @return title text (null if document isn't HTML or lacks a title tag)
         * @throws IOException
         */
        public static String getPageTitle(String url) throws IOException {
            URL u = new URL(url);
            URLConnection conn = u.openConnection();

            // ContentType is an inner class defined below
            ContentType contentType = getContentTypeHeader(conn);
            if (!contentType.contentType.equals("text/html"))
                return null; // don't continue if not HTML
            else {
                // determine the charset, or use the default
                Charset charset = getCharset(contentType);
                if (charset == null)
                    charset = Charset.defaultCharset();

                // read the response body, using BufferedReader for performance
                InputStream in = conn.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(in, charset));
                int n = 0, totalRead = 0;
                char[] buf = new char[1024];
                StringBuilder content = new StringBuilder();

                // read until EOF or first 8192 characters
                while (totalRead < 8192 && (n = reader.read(buf, 0, buf.length)) != -1) {
                    content.append(buf, 0, n);
                    totalRead += n;
                }
                reader.close();

                // extract the title
                Matcher matcher = TITLE_TAG.matcher(content);
                if (matcher.find()) {
                    /* replace any occurrences of whitespace (which may
                     * include line feeds and other uglies) as well
                     * as HTML brackets with a space */
                    return matcher.group(1).replaceAll("[\\s\\<>]+", " ").trim();
                } else
                    return null;
            }
        }

        /**
         * Loops through response headers until Content-Type is found.
         * @param conn
         * @return ContentType object representing the value of
         * the Content-Type header
         */
        private static ContentType getContentTypeHeader(URLConnection conn) {
            int i = 0;
            boolean moreHeaders = true;
            do {
                String headerName = conn.getHeaderFieldKey(i);
                String headerValue = conn.getHeaderField(i);
                if (headerName != null && headerName.equals("Content-Type"))
                    return new ContentType(headerValue);

                i++;
                moreHeaders = headerName != null || headerValue != null;
            } while (moreHeaders);

            return null;
        }

        private static Charset getCharset(ContentType contentType) {
            if (contentType != null && contentType.charsetName != null && Charset.isSupported(contentType.charsetName))
                return Charset.forName(contentType.charsetName);
            else
                return null;
        }

    }

    /**
     * Class holds the content type and charset (if present)
     */
    private static final class ContentType {
        private static final Pattern CHARSET_HEADER = Pattern.compile("charset=([-_a-zA-Z0-9]+)", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

        private String contentType;
        private String charsetName;

        private ContentType(String headerValue) {
            if (headerValue == null)
                throw new IllegalArgumentException("ContentType must be constructed with a not-null headerValue");
            int n = headerValue.indexOf(";");
            if (n != -1) {
                contentType = headerValue.substring(0, n);
                Matcher matcher = CHARSET_HEADER.matcher(headerValue);
                if (matcher.find())
                    charsetName = matcher.group(1);
            } else
                contentType = headerValue;
        }
    }
}
