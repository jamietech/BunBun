package ch.jamiete.bunbun.permissions;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import ch.jamiete.bunbun.BunBun;

public class PermissionFlag {
    private final Flag flag;
    private final Set<String> grants, exclusions;

    public PermissionFlag(Flag flag) {
        this.flag = flag;
        this.grants = new LinkedHashSet<String>();
        this.exclusions = new LinkedHashSet<String>();

        this.init();
    }

    protected void init() {
        File file = new File(this.flag.getChar() + ".flag");

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
            }

            throw new RuntimeException("Permission flag " + this.flag.getChar() + " is not configured.");
        }

        try {
            for (String line : Files.readAllLines(file.toPath())) {
                if (line.startsWith("#") || line.trim().equals("")) {
                    continue;
                }

                if (line.startsWith("-")) {
                    exclusions.add(line.toLowerCase().substring(1));
                    BunBun.getLogger().info("  -> - " + line.toLowerCase());
                } else {
                    grants.add(line.toLowerCase());
                    BunBun.getLogger().info("  -> + " + line.toLowerCase());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean hasPermission(String node) {
        for (String permission : grants) {
            if (node.equalsIgnoreCase(permission)) {
                return true;
            }

            if (permission.contains("*")) {
                Pattern pattern = Pattern.compile(permission.toLowerCase().replace(".", "\\.").replace("*", "(.*)"));
                Matcher matcher = pattern.matcher(node.toLowerCase());

                if (matcher.matches() && !exclusions.contains(node.toLowerCase())) {
                    return true;
                }
            }
        }

        //return this.grants.contains(node.toLowerCase());
        return false;
    }

    public Flag getFlag() {
        return flag;
    }

    public String[] getGrants() {
        return grants.toArray(new String[grants.size()]);
    }

    public String[] getExclusions() {
        return exclusions.toArray(new String[exclusions.size()]);
    }

}
