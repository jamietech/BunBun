package ch.jamiete.bunbun.permissions;

import java.io.File;
import java.util.LinkedHashSet;
import java.util.Set;
import org.kitteh.irc.client.library.element.User;
import ch.jamiete.bunbun.BunBun;

public class PermissionManager {
    private Set<PermissionFlag> flags;
    private Set<PermissionUser> users;
    private BunBun bun;

    public PermissionManager(BunBun bun) {
        this.init();
        this.bun = bun;
    }

    /**
     * <b>Only call from ReloadPermissionCommand.</b>
     */
    public void init() {
        BunBun.getLogger().info("Initialising permissions...");

        this.flags = new LinkedHashSet<PermissionFlag>();
        this.users = new LinkedHashSet<PermissionUser>();

        for (Flag flag : Flag.getFlags()) {
            BunBun.getLogger().info("Creating flag " + flag.getChar() + "...");

            File file = new File(flag.getChar() + ".flag");

            if (!file.exists()) {
                throw new RuntimeException("Couldn't read from " + file.getName());
            }

            flags.add(new PermissionFlag(flag));

            BunBun.getLogger().info("Permissions intialised.");
        }
    }

    public String getPath(User user) {
        return user.getNick() + "@" + user.getHost().replaceAll(":", "-");
    }

    public PermissionFlag[] getFlags() {
        return this.flags.toArray(new PermissionFlag[this.flags.size()]);
    }

    public void track(User user) {
        if (isTracked(user)) {
            return;
        }

        PermissionUser puser = new PermissionUser(user);
        puser.addFlag(this.getFlag(Flag.DEFAULT));

        puser.read(this.bun);

        this.users.add(puser);

        BunBun.getLogger().info("Began tracking permissions for " + this.getPath(user) + ": " + puser.getFlagList());
    }

    public void untrack(User user) {
        if (!isTracked(user)) {
            return;
        }

        this.users.remove(this.getUser(user));
    }

    @Deprecated
    protected User findKicked(String name) {
        for (PermissionUser puser : this.users) {
            if (puser.getUser().getNick().equalsIgnoreCase(name)) {
                return puser.getUser();
            }
        }

        return null;
    }

    public PermissionUser getUser(User user) {
        for (PermissionUser puser : this.users) {
            if (puser.getUser().equals(user)) {
                return puser;
            }
        }

        return null;
    }

    public boolean isTracked(User user) {
        return this.getUser(user) != null;
    }

    public PermissionFlag getFlag(Flag flag) {
        return this.getFlag(flag.getChar());
    }

    protected PermissionFlag getFlag(char flagc) {
        for (PermissionFlag flag : this.flags) {
            if (flag.getFlag().getChar() == flagc) {
                return flag;
            }
        }

        return null;
    }

    public boolean hasPermission(User user, String permission) {
        PermissionUser puser = this.getUser(user);

        if (puser == null) {
            BunBun.getLogger().warning("Couldn't find PermissionUser for " + user.getNick() + "!");
            return false;
        }

        for (PermissionFlag flag : puser.getFlags()) {
            if (flag.hasPermission(permission)) {
                return true;
            }
        }

        return false;
    }

}
