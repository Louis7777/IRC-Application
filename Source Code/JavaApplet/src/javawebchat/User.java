package javawebchat;

/* Hostmask format:     nick!ident@hostname
 * User info syntax:    nick!ident@hostname (description)
 *
 * Where:
 * nick                 -> Nickname used in the Chat by the user of a specific Client.
 * ident                -> Identity/Username of a specific Client on the specified host (hostname).
 * hostname             -> Hostname/IP-Address. The real name of the host that a specific Client is running on.
 * description          -> A description set by the user of a specific Client.
 *
 */
public class User {

    //================================================================================
    // Fields
    //================================================================================
    private String nick;
    private String ident;
    private String hostname; // Has a maximum length of 63 characters.
    private String description;

    //================================================================================
    // Constructors
    //================================================================================
    public User() {
    }

    //================================================================================
    // Accessors and Mutators (Getters and Setters)
    //================================================================================
    public String getNick() {
        return nick;
    }

    public String getIdent() {
        return ident;
    }

    public String getHostname() {
        return hostname;
    }

    public String getDescription() {
        return description;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public void setIdent(String ident) {
        this.ident = ident;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    //================================================================================
    // User Methods
    //================================================================================
    // do any required methods
}
