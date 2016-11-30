package java_ircd;

import java.util.ArrayList;

/* Channel properties:
 *
 * name                 -> The name of the specific Channel.
 * nickList             -> Nicknames list. A list of all channel members.
 * topic                -> A headline or announcement for the specific Channel.
 *
 */
public class Channel {

    //================================================================================
    // Fields
    //================================================================================
    protected String name; // Up to 200 characters long. No spaces (' '), commas (',') or control G (^G or ASCII 7)
    private ArrayList<Connection> nickList = new ArrayList<Connection>();
    private String topic;
    /* private Object banlist; */

    //================================================================================
    // Constructors
    //================================================================================
    public Channel() {
    }

    //================================================================================
    // Accessors and Mutators (Getters and Setters)
    //================================================================================
    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public ArrayList<Connection> getNickList() {
        return nickList;
    }

    //================================================================================
    // Channel Methods
    //================================================================================
    public void broadcast(Connection not, String message) {
        synchronized (Connection.MUTEX) {
            for (Connection con : nickList) {
                if (con != not) {
                    con.outStream.send(message);
                }
            }
        }
    }

    public void send(String message) { // Send to everyone.
        broadcast(null, message);
    }
}
