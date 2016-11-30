package java_ircd;

import java.util.ArrayList;

/* List of commands, in alphabetical order:
 *
 *
 * JOIN         -> Join/create a channel.
 * MODE         -> Set a mode for the User (self).
 * NICK         -> Set/Change nickname.
 * PART         -> Leave from a channel.
 * PING         -> Check whether the connection to the server is alive.
 * PRIVMSG      -> Send a message to a channel or to a specific user.
 * QUIT         -> Terminate connection to the server.
 * TOPIC        -> See/Set a channel's topic.
 * USER         -> Send client information at the begin of the connection.
 * USERHOST     -> Returns a list of information about the nicknames specified.
 * WHO          -> See who is on the channel.
 *
 *
 */
public class Commands {

    /***************************** BEGIN OF COMMANDS SET *****************************/
    public enum Command {

        /* Each object of the enum Commands,
         * calls a constructor with a minimum and a maximum
         * allowed number of arguments for that command. */
        JOIN(1, 2) {

            @Override
            public void execute(Connection con, String prefix, String[] params) throws Exception {

                if (params.length == 2) {
                    con.outStream.sendNotice("This server does not support channel keys at this time. JOIN will act as if you hadn't specified any keys.");
                }
                String[] channelNames = params[0].split(","); // Get all specified channelnames.

                /* Check for invalid channelnames. */
                for (String channelName : channelNames) {
                    if (!channelName.startsWith("#")) {
                        con.outStream.sendNotice("This server only allows channel names that start with a # sign.");
                        return;
                    }
                    if (channelName.contains(" ")) {
                        con.outStream.sendNotice("This server does not allow spaces in channel names.");
                        return;
                    }
                }
                /* Join the specified channel(s) */
                for (String channelName : channelNames) {
                    doJoin(con, channelName);
                }
            }

            public void doJoin(Connection con, String channelName) throws Exception {
                if (!channelName.startsWith("#")) {
                    con.outStream.sendNotice("This server only allows channel names that start with a # sign.");
                    return;
                }
                if (channelName.contains(" ")) {
                    con.outStream.sendNotice("This server does not allow spaces in channel names.");
                }
                synchronized (Connection.MUTEX) {
                    Channel channel = Connection.serverChannels.get(channelName);
                    boolean added = false;
                    if (channel == null) {
                        added = true;
                        channel = new Channel();
                        channel.name = channelName;
                        Connection.serverChannels.put(channelName, channel);
                    }
                    if (channel.getNickList().contains(con)) {
                        con.outStream.sendNotice("You're already a member of " + channelName);
                        return;
                    }
                    channel.getNickList().add(con);
                    channel.send(":" + con.client.getHostmask() + " JOIN " + channelName);
                    if (added) {
                        con.outStream.sendGlobal("MODE " + channelName + " +nt");
                    }

                    if (channel.getTopic() != null) {
                        con.outStream.sendGlobal("332 " + con.client.getNick() + " " + channel.name + " :" + channel.getTopic()); // (332) Response to TOPIC with the set topic.
                    } else {
                        con.outStream.sendGlobal("331 " + con.client.getNick() + " " + channel.name + " :No topic is set"); // (331) Response to TOPIC when no topic is set
                    }
                    for (Connection channelMember : channel.getNickList()) {
                        con.outStream.sendGlobal("353 " + con.client.getNick() + " = " + channelName + " :" + channelMember.client.getNick()); // (353) Reply to NAMES
                    }
                    con.outStream.sendGlobal("366 " + con.client.getNick() + " " + channelName + " :End of /NAMES list"); // (366) Termination of an RPL_NAMREPLY list
                }
            }
        },
        MODE(0, 2) {

            @Override
            public void execute(Connection con, String prefix, String[] params) throws Exception {
                if (params.length == 1) {
                    if (params[0].startsWith("#")) { // MODE <#channelname>
                        con.outStream.sendGlobal("324 " + con.client.getNick() + " " + params[0] + " +nt"); // (324) RPL_CHANNELMODEIS
                    } else { // MODE <nickname>
                        con.outStream.sendNotice("User mode querying not supported yet.");
                    }
                } else if (params.length == 2 && (params[1].equals("+b") || params[1].equals("+e"))) {
                    if (params[0].startsWith("#")) { // MODE <#channelname> +<flags>
                        if (params[1].equals("+b")) {
                            con.outStream.sendGlobal("368 " + con.client.getNick() + " " + params[0] + " :End of channel ban list"); // (368) Termination of an RPL_BANLIST list.
                        } else {
                            con.outStream.sendGlobal("349 " + con.client.getNick() + " " + params[0] + " :End of channel exception list");
                        }
                    } else { // MODE <nickname> +<flags>
                        con.outStream.sendNotice("User mode setting not supported yet for +b or +e.");
                    }
                } else {
                    con.outStream.sendNotice("Specific modes not supported yet.");
                }
            }
        },
        NICK(1, 1) { // first command sent by the Client.

            @Override
            public void execute(Connection con, String prefix, String[] params) throws Exception {

                String nick = params[0].replace(":", "").replace("#", "").replace(" ", "").replace("!", "").replace("@", "");

                synchronized (Connection.MUTEX) {
                    if (con.client.getNick() == null || con.client.getNick().equals(con.client.getTempEntryNick())) { // If nickname is empty (first time only).
                        doStartUpNick(con, nick);
                    } else if (con.client.getNick() != null && Connection.serverConnections.containsKey(nick)) { // If nickname already exists.
                        if (!con.client.getNick().equals(nick)) {
                            con.outStream.sendNotice("Nickname " + nick + " already in use.");
                            doSwitchNick(con, generateGuestNick());
                        } else {
                            return;
                        }
                    } else { // Getting a new nickname.
                        if (!con.client.getNick().equals(nick)) {
                            doSwitchNick(con, nick);
                        } else {
                            return;
                        }
                    }
                }
            }

            private void doSwitchNick(Connection con, String nick) {
                String oldNick = con.client.getNick();
                synchronized (Connection.MUTEX) {

                    con.client.setNick(nick);
                    Connection.serverConnections.remove(oldNick);
                    Connection.serverConnections.put(con.client.getNick(), con);
                    con.outStream.send(":" + oldNick + "!" + con.client.getIdent() + "@" + con.client.getHostname() + " NICK :" + con.client.getNick()); // Reply to user.
                    /*
                     * Notify all channels where the user is in that the user has changed nickname.
                     */
                    for (Channel chan : Connection.serverChannels.values()) {
                        if (chan.getNickList().contains(con)) {
                            chan.broadcast(con, ":" + oldNick + "!" + con.client.getIdent() + "@" + con.client.getHostname() + " NICK :" + con.client.getNick());
                        }
                    }
                }
            }

            private void doStartUpNick(Connection con, String nick) throws InterruptedException {
                String oldNick = con.client.getNick(); // This is the temporary nickname assigned by the server.
                synchronized (Connection.MUTEX) {

                    if (Connection.serverConnections.containsKey(nick)) {
                        con.outStream.sendNotice("Nickname " + nick + " already in use.");
                        con.client.setNick(generateGuestNick());
                    } else {
                        con.client.setNick(nick);
                    }

                    Connection.serverConnections.put(con.client.getNick(), con);
                    Connection.serverConnections.remove(oldNick);
                    Connection.temporaryIDs.remove(con.client.getTempEntryNick()); // Free the given ID.
                    con.outStream.send(":" + nick + "!" + con.client.getIdent() + "@" + con.client.getHostname() + " NICK :" + con.client.getNick());
                }
            }

            private String generateGuestNick() {
                String guestNick = "Guest" + (MIN + (int) (Math.random() * ((MAX - MIN) + 1)));

                synchronized (Connection.MUTEX) {

                    while (Connection.serverConnections.containsKey(guestNick)) {
                        guestNick = "Guest" + (MIN + (int) (Math.random() * ((MAX - MIN) + 1)));
                    }
                }
                return guestNick;
            }
        },
        PART(1, 2) {

            @Override
            public void execute(Connection con, String prefix, String[] params) throws Exception {
                String[] channels = params[0].split(",");
                for (String channelName : channels) {
                    synchronized (Connection.MUTEX) {

                        Channel channel = Connection.serverChannels.get(channelName); // Check if the channel exists (has it been created?) on the server.

                        if (channel == null) { // Channel does not exist.

                            con.outStream.sendNotice("The channel " + channelName + " does not exist.");

                        } else { // Channel exists.

                            if (!channel.getNickList().contains(con)) { // User is not a member of the channel.
                                con.outStream.sendNotice("You're not a member of the channel " + channelName + ", so you can't part it.");
                            } else { // User is a member of the channel.

                                if (params.length < 2) { // If no part reason was given. {
                                    channel.send(":" + con.client.getHostmask() + " PART " + channelName); // Inform the other users that a user is leaving.
                                } else { // If a part reason was given.
                                    channel.send(":" + con.client.getHostmask() + " PART " + channelName + " :" + params[1]);
                                }
                                channel.getNickList().remove(con); // Remove the user from the channel.
                            }

                            if (channel.getNickList().isEmpty()) { // If there aren't any users left in the channel, destroy it.
                                Connection.serverChannels.remove(channelName);
                            }

                        }
                    }
                }
            }
        },
        PING(1, 1) {

            @Override
            public void execute(Connection con, String prefix, String[] params) throws Exception {
                con.outStream.send(":" + Config.getServerName() + " PONG " + Config.getServerName() + " :" + params[0]);
            }
        },
        PRIVMSG(2, 2) {

            @Override
            public void execute(Connection con, String prefix, String[] params) throws Exception {
                String[] recipients = params[0].split(",");
                String message = params[1];
                for (String recipient : recipients) {
                    /* If the message destination is not a channel then it is a user. */
                    if (recipient.startsWith("#")) { // Message to channel.
                        Channel channel = Connection.serverChannels.get(recipient);
                        if (channel == null) {
                            con.outStream.sendNotice("No such channel, so can't send a message to it: " + recipient);
                        } else if (!channel.getNickList().contains(con)) {
                            con.outStream.sendNotice("You can't send messages to channels you're not at.");
                        } else {
                            channel.broadcast(con, ":" + con.client.getHostmask() + " PRIVMSG " + recipient + " :" + message); // Send to everyone (except to the sender) in the channel.
                        }
                    } else { // Message to user.
                        Connection recipientConnection = Connection.serverConnections.get(recipient);
                        if (recipientConnection == null) {
                            con.outStream.sendNotice("The user " + recipient + " is not online.");
                        } else {
                            recipientConnection.outStream.send(":" + con.client.getHostmask() + " PRIVMSG " + recipient + " :" + message);
                        }
                    }
                }
            }
        },
        QUIT(1, 1) {

            @Override
            public void execute(Connection con, String prefix, String[] params) throws Exception {
                con.outStream.sendQuit("Quit: " + params[0]);
            }
        },
        TOPIC(1, 2) {

            @Override
            public void execute(Connection con, String prefix, String[] params) throws Exception {
                Channel channel = Connection.serverChannels.get(params[0]);
                if (channel == null) {
                    con.outStream.sendNotice("No such channel for topic viewing: " + params[0]);
                    return;
                }
                if (params.length == 1) { // TOPIC <#channelname>
                    /*
                     * The user wants to see the channel topic.
                     */
                    if (channel.getTopic() != null) {
                        con.outStream.sendGlobal("332 " + con.client.getNick() + " " + channel.name + " :" + channel.getTopic()); // Response to TOPIC with the set topic
                    } else {
                        con.outStream.sendGlobal("331 " + con.client.getNick() + " " + channel.name + " :No topic is set"); // Response to TOPIC when no topic is set
                    }
                } else { // // TOPIC <#channelname> <topic>
                    /*
                     * The user wants to set the channel topic.
                     */
                    if (channel.getNickList().contains(con)) {
                        channel.setTopic(params[1]);
                        channel.broadcast(con, ":" + con.client.getHostmask() + " TOPIC " + channel.name + " :" + channel.getTopic()); // Topic changed.
                        con.outStream.send(":" + con.client.getHostmask() + " TOPIC " + channel.name + " :" + channel.getTopic());
                    } else {
                        con.outStream.send("NOTICE :You are not a member of that channel.");
                    }
                }
            }
        },
        USER(1, 4) { // second command sent only once by the Client

            @Override
            public void execute(Connection con, String prefix, String[] params) throws Exception {
                if (con.client.getIdent() != null) {
                    con.outStream.send("NOTICE AUTH :You can't change your user information after you've logged in right now.");
                    return;
                }
                con.client.setIdent(params[0]);
                String forDescription = params.length > 3 ? params[3] : "(no description)";
                con.client.setDescription(forDescription);
                /*
                 * Now we'll send the user their initial information.
                 */
                con.outStream.sendGlobal("001 " + con.client.getNick() + " :Welcome to " + Config.getServerName() + ", an IRC network."); // RPL_WELCOME - The first message sent after client registration. The text used varies widely.
                con.outStream.sendGlobal("004 " + con.client.getNick() + " " + Config.getServerName() + " Java IRC server"); // RPL_MYINFO - Part of the post-registration greeting
                con.outStream.sendGlobal("004 " + con.client.getNick() + " :- I have " + Connection.serverConnections.size() + " clients connected.");
                con.outStream.sendGlobal("004 " + con.client.getNick() + " :- I have " + Connection.serverChannels.size() + " channels formed.");
                con.outStream.sendGlobal("375 " + con.client.getNick() + " :- " + Config.getServerName() + " Message of the Day -"); // Start of an RPL_MOTD list
                con.outStream.sendGlobal("372 " + con.client.getNick() + " :- Hello. Welcome to " + Config.getServerName() + ", IRC network."); // (372) Reply to MOTD
                con.outStream.sendGlobal("372 " + con.client.getNick() + " :- Enjoy your stay.");
                con.outStream.sendGlobal("376 " + con.client.getNick() + " :End of /MOTD command."); // Termination of an RPL_MOTD list
            }
        },
        USERHOST(1, 5) {

            @Override
            public void execute(Connection con, String prefix, String[] params) throws Exception {
                ArrayList<String> replies = new ArrayList<String>();
                for (String s : params) {
                    Connection user = Connection.serverConnections.get(s);
                    if (user != null) {
                        replies.add(user.client.getNick() + "=+" + user.client.getIdent() + "@" + user.client.getHostname());
                    }
                }

                StringBuffer response = new StringBuffer(); // StringBuffer is synchronized.

                boolean first = true;
                for (String s : replies.toArray(new String[0])) {
                    if (first) {
                        first = false;
                    } else {
                        response.append(" ");
                    }
                    response.append(s);
                }

                con.outStream.sendGlobal("302 " + con.client.getNick() + " :" + response.toString()); // Reply used by USERHOST
            }
        },
        WHO(0, 2) {

            @Override
            public void execute(Connection con, String prefix, String[] params) throws Exception {
                if (params.length > 1) {
                    con.outStream.sendNotice("Filtering by operator only using the WHO command isn't yet supported. WHO will act as if \"o\" has not been specified.");
                }
                String person = "";
                if (params.length > 0) {
                    person = params[0];
                }
                synchronized (Connection.MUTEX) {
                    Channel channel = Connection.serverChannels.get(person);
                    if (channel != null) {
                        for (Connection channelMember : channel.getNickList()) {
                            con.outStream.sendGlobal("352 " + con.client.getNick() + " " + person + " " + channelMember.client.getIdent() + " " + channelMember.client.getHostname() + " " + Config.getServerName() + " " + channelMember.client.getNick() + " H :0 " + channelMember.client.getDescription());
                        }
                    } else {
                        con.outStream.sendNotice("WHO with something other than a channel as arguments is not supported right now. WHO will display an empty list of people.");
                    }
                }
                con.outStream.send("315 " + con.client.getNick() + " " + person + " :End of /WHO list."); // (315) Used to terminate a list of RPL_WHOREPLY replies
            }
        };

        /* Fields */
        private int minArguments;
        private int maxArguments;
        private static final int MIN = 11111; // For setting random Guest nicknames.
        private static final int MAX = 99999; // For setting random Guest nicknames.

        /* Constructor */
        private Command(int min, int max) {
            minArguments = min;
            maxArguments = max;
        }

        /* Accessors */
        public int getMinArguments() {
            return minArguments;
        }

        public int getMaxArguments() {
            return maxArguments;
        }

        /* Abstract method that shall be implemented by all commands */
        public abstract void execute(Connection con, String prefix, String[] params) throws Exception;
    }
    /****************************** END OF COMMANDS SET ******************************/
    //================================================================================
    // Assisting methods.
    //================================================================================
    // do any required methods
}
