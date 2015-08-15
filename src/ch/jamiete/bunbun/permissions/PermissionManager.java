package ch.jamiete.bunbun.permissions;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;
import org.kitteh.irc.client.library.element.User;
import ch.jamiete.bunbun.BunBun;

public class PermissionManager {
    private Set<PermissionFlag> flags;
    private Set<PermissionUser> users;

    public PermissionManager() {
        this.init();
    }

    /**
     * <b>Only call from ReloadPermissionCommand.</b>
     */
    public void init() {
        this.flags = new LinkedHashSet<PermissionFlag>();
        this.users = new LinkedHashSet<PermissionUser>();

        for (Flag flag : Flag.getFlags()) {
            BunBun.getLogger().info("Creating flag " + flag.getChar());

            File file = new File(flag.getChar() + ".flag");

            if (!file.exists()) {
                throw new RuntimeException("Couldn't read from " + file.getName());
            }

            flags.add(new PermissionFlag(flag));

            BunBun.getLogger().info("Done!");
        }
    }

    public void track(User user) {
        if (isTracked(user)) {
            return;
        }

        PermissionUser puser = new PermissionUser(user);
        puser.addFlag(this.getFlag(Flag.DEFAULT));

        File file = new File(user.getNick() + "@" + user.getHost().replaceAll(":", "-"));

        if (file.exists()) {
            String[] flags = null;

            try {
                BufferedReader in = new BufferedReader(new FileReader(file));
                flags = in.readLine().split("");
                in.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("Failed to read user flag file " + file.getName());
            }

            for (String bit : flags) {
                puser.addFlag(this.getFlag(Flag.byChar(bit.toCharArray()[0])));
            }
        }

        this.users.add(puser);

        BunBun.getLogger().info("Began tracking permissions for " + file.getName() + ": " + puser.getFlagList());
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
