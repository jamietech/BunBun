package ch.jamiete.bunbun.listeners;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.kitteh.irc.client.library.event.channel.ChannelMessageEvent;
import org.kitteh.irc.lib.net.engio.mbassy.listener.Handler;
import ch.jamiete.bunbun.BunBun;
import ch.jamiete.bunbun.EventListener;

public class URLListener extends EventListener {
    // https://mathiasbynens.be/demo/url-regex - @diegoperini (at bottom of page)
    private static final Pattern REGEX = Pattern.compile("_^(?:(?:https?|ftp)://)(?:\\S+(?::\\S*)?@)?(?:(?!10(?:\\.\\d{1,3}){3})(?!127(?:\\.\\d{1,3}){3})(?!169\\.254(?:\\.\\d{1,3}){2})(?!192\\.168(?:\\.\\d{1,3}){2})(?!172\\.(?:1[6-9]|2\\d|3[0-1])(?:\\.\\d{1,3}){2})(?:[1-9]\\d?|1\\d\\d|2[01]\\d|22[0-3])(?:\\.(?:1?\\d{1,2}|2[0-4]\\d|25[0-5])){2}(?:\\.(?:[1-9]\\d?|1\\d\\d|2[0-4]\\d|25[0-4]))|(?:(?:[a-z\\x{00a1}-\\x{ffff}0-9]+-?)*[a-z\\x{00a1}-\\x{ffff}0-9]+)(?:\\.(?:[a-z\\x{00a1}-\\x{ffff}0-9]+-?)*[a-z\\x{00a1}-\\x{ffff}0-9]+)*(?:\\.(?:[a-z\\x{00a1}-\\x{ffff}]{2,})))(?::\\d{2,5})?(?:/[^\\s]*)?$_iuS");

    public URLListener(BunBun bun) {
        super(bun);
    }

    @Handler
    public void onChannelMessage(ChannelMessageEvent event) {
        if (event.getMessage().trim().length() > 0) {
            for (String part : event.getMessage().split(" ") ) {
                Matcher matcher = REGEX.matcher(part);
                
                if (matcher.matches()) {
                    while (matcher.find()) {
                        System.out.println(matcher.);
                    }
                }
            }
        }
    }
}
