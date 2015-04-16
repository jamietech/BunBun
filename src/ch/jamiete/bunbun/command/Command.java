package ch.jamiete.bunbun.command;

import java.util.List;

public interface Command {

    /**
     * Gets a copy of the list of label aliases that this command will respond to.
     * Changes to this list will not be reflected on the command.
     * @return a list of label aliases.
     */
    public List<String> getAliases();

    /**
     * Gets the description of this command.
     * @return the command description
     */
    public String getDescription();

    /**
     * Gets the main label that this command responds to.
     * @return the main command label
     */
    public String getName();

    /**
     * Gets the permission required to use this command.
     * @return command's permission
     */
    public String getPermission();

    /**
     * Gets whether or not the command should return output on lack of permission.
     * @return permission silence
     */
    public boolean getSilent();

    /**
     * Gets whether or not the specified alias is registered with the command.
     * Case insensitive.
     * @param alias to test
     * @return boolean
     */
    public boolean hasAlias(String alias);

    /**
     * Gets whether or not the command has a permission set.
     * @return
     */
    public boolean hasPermission();

    /**
     * <b>Aliases cannot be set again after they've been set by the constructor.</b>
     * @param aliases
     */
    public void setAliases(List<String> aliases);

    /**
     * Sets the description of the command for the session.
     * @param description
     */
    public void setDescription(String description);

    /**
     * <b>The command name cannot be set again after its been set by the constructor.</b>
     * @param name
     */
    public void setName(String name);

    /**
     * Sets the permission required to use the command for the session.
     * @param permission
     */
    public void setPermission(String permission);

    /**
     * Sets whether or not permission failure output will be silent for the session.
     * @param silent
     */
    public void setSilent(boolean silent);

}
