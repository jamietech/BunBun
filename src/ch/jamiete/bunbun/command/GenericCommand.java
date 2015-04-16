package ch.jamiete.bunbun.command;

import java.util.Collections;
import java.util.List;
import ch.jamiete.bunbun.BunBun;

public abstract class GenericCommand implements Command {
    protected final BunBun bun;

    private String name;

    private List<String> aliases;
    private String description;
    private String permission;
    private boolean silent;

    protected boolean aliases_final = false;

    protected GenericCommand(final BunBun bun) {
        this.bun = bun;
    }

    @Override
    public List<String> getAliases() {
        return this.aliases;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getPermission() {
        return this.permission;
    }

    @Override
    public boolean getSilent() {
        return this.silent;
    }

    @Override
    public boolean hasAlias(final String test) {
        if (this.aliases == null) {
            return false;
        }

        for (final String alias : this.aliases) {
            if (alias.equalsIgnoreCase(test)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean hasPermission() {
        return this.permission != null && !this.permission.trim().equals("");
    }

    @Override
    public void setAliases(final List<String> aliases) {
        if (aliases_final) {
            throw new RuntimeException("Command already has aliases specified.");
        }

        this.aliases = Collections.unmodifiableList(aliases);
    }

    @Override
    public void setDescription(final String description) {
        this.description = description;
    }

    @Override
    public void setName(final String name) {
        if (this.name != null) {
            throw new RuntimeException("Command already named.");
        }

        this.name = name;
    }

    @Override
    public void setPermission(final String permission) {
        this.permission = permission;
    }

    @Override
    public void setSilent(final boolean silent) {
        this.silent = silent;
    }

}
