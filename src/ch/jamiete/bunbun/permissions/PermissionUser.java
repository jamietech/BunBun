package ch.jamiete.bunbun.permissions;

import java.util.LinkedHashSet;
import java.util.Set;
import org.kitteh.irc.client.library.element.User;

public class PermissionUser {
    private final User user;
    private final Set<PermissionFlag> flags;

    public PermissionUser(User user) {
        this.user = user;
        this.flags = new LinkedHashSet<PermissionFlag>();
    }

    public PermissionFlag[] getFlags() {
        return this.flags.toArray(new PermissionFlag[this.flags.size()]);
    }

    public String getFlagList() {
        String ret = "";

        for (PermissionFlag flag : this.flags) {
            ret += flag.getFlag().getChar();
        }

        return ret;
    }

    public boolean hasFlag(PermissionFlag flag) {
        return this.flags.contains(flag);
    }

    public void removeFlag(PermissionFlag flag) {
        this.flags.remove(flag);
    }

    public void addFlag(PermissionFlag flag) {
        this.flags.add(flag);

    }

    public boolean hasPermission(String node) {
        for (PermissionFlag flag : flags) {
            if (flag.hasPermission(node))
                return true;
        }

        return false;
    }

    public User getUser() {
        return this.user;
    }

}
