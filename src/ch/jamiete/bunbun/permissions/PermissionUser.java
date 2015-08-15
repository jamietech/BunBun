package ch.jamiete.bunbun.permissions;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;
import org.kitteh.irc.client.library.element.User;
import ch.jamiete.bunbun.BunBun;

public class PermissionUser {
    private final User user;
    private Set<PermissionFlag> flags;

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

    public void read(BunBun bun) {
        this.flags = new LinkedHashSet<PermissionFlag>();
        this.flags.add(bun.getPermissionManager().getFlag(Flag.DEFAULT));

        File file = new File(bun.getPermissionManager().getPath(this.user));

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
                PermissionFlag flag = bun.getPermissionManager().getFlag(Flag.byChar(bit.toCharArray()[0]));

                if (flag == null) {
                    BunBun.getLogger().warning("User flag file " + file.getName() + " attempted to add erroneous flag " + bit);
                } else {
                    this.addFlag(flag);
                }
            }
        }
    }

    public void write(BunBun bun) {
        File file = new File(bun.getPermissionManager().getPath(user));

        if (file.exists() && this.getFlags().length == 1) {
            file.delete();
            BunBun.getLogger().info("Pruned permission file for " + bun.getFullUser(user));
        }

        try {
            if (!file.exists()) {
                file.createNewFile();
            }

            BufferedWriter out = new BufferedWriter(new FileWriter(file));

            out.write(this.getFlagList());

            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to write user flag file " + file.getName());
        }
    }

}
