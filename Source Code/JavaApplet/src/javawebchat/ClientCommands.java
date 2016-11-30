package javawebchat;

/* List of commands, in alphabetical order:
 *
 * JOIN         -> Attempt to join a channel.
 * LIST         -> Display all channels on the server.
 * NICK         -> Change nickname.
 * PART         -> Leave a channel.
 * PRIVMSG      -> Send a message to a channel or to a user.
 * QUIT         -> Disconnect.
 * SERVER       -> Connect to a server.
 * TOPIC        -> Get/set a channel's topic.
 *
 */
public class ClientCommands {

    /***************************** BEGIN OF COMMANDS SET *****************************/
    public enum Command {

        JOIN(1, 2) { // Attempting to join a channel.

            @Override
            public void execute(Connection con, String messageTyped, String[] params) throws Exception {

                System.out.println("***Sending line to " + con.socket.getRemoteSocketAddress() + "*** " + messageTyped);
                con.consoleOutputPane.drawMessage("=>> Attempting to join channel " + params[0] + "\n", con.consoleOutputPane.joinedKeyWord);
                con.outStream.messageQueue.add(messageTyped);

            }
        },
        LIST(1, 1) { // Request list of all channels.

            @Override
            public void execute(Connection con, String messageTyped, String[] params) throws Exception {
                // todo
            }
        },
        NICK(1, 1) { // Request a change of nickname.

            @Override
            public void execute(Connection con, String messageTyped, String[] params) throws Exception {
                System.out.println("***Sending line to " + con.socket.getRemoteSocketAddress() + "*** " + messageTyped);
                con.outStream.messageQueue.add(messageTyped);
            }
        },
        PART(1, 2) { // Leaving a channel.

            @Override
            public void execute(Connection con, String messageTyped, String[] params) throws Exception {

                String msg;

                /* messageTyped = PART <channel> <reason>
                 * or
                 * messageTyped = PART <channel>
                 */

                String[] tokens = messageTyped.split(" ", 3);// Split in two (3) strings.

                if (tokens.length < 3) {
                    System.out.println("***Sending line to " + con.socket.getRemoteSocketAddress() + "*** " + messageTyped);
                    con.outStream.messageQueue.add("PART " + tokens[1]);
                    return;
                }

                msg = "PART " + tokens[1] + " :" + tokens[2];

                System.out.println("***Sending line to " + con.socket.getRemoteSocketAddress() + "*** " + msg);
                con.outStream.messageQueue.add(msg);
            }
        },
        PRIVMSG(2, 2) {

            @Override
            public void execute(Connection con, String destination, String[] message) throws Exception {
                String[] recipients = destination.split(",");
                String msg = "PRIVMSG " + destination + " :" + message[0]; // !IMPORTANT! A colon must precede the message.

                for (String recipient : recipients) {

                    /* We need to draw our own messages in a channel. */
                    if (recipient.startsWith("#")) { // Message to channel.
                        ChannelOutputPane channel = con.joinedChannels.get(recipient);
                        if (channel == null) {
                            con.consoleOutputPane.drawMessage("Not member of such channel, so can't send a message to it: " + recipient + "\n", con.consoleOutputPane.clientErrorKeyWord);
                        } else {
                            channel.drawMessage("<" + con.user.getNick() + "> " + message[0] + "\n", null); // Send it to self.
                        }
                    } else { // Message to user.
                        MessagePanel privateChat;
                        synchronized (Connection.MUTEX) {
                            Integer index = JavaWebChat.tabbedPane.indexOfTab(recipient);
                            if (index > 0) { // If the tab for the private chat exists.
                                privateChat = (QueryOutputPane) JavaWebChat.tabbedPane.getComponentAt(index);
                                if (JavaWebChat.currentView.equals(privateChat.getTitle())) {
                                    privateChat.drawMessage("<" + con.user.getNick() + "> " + message[0] + "\n", null); // Send it to self.
                                }
                            }
                        }
                    }
                }

                System.out.println("***Sending line to " + con.socket.getRemoteSocketAddress() + "*** " + msg);
                con.outStream.messageQueue.add(msg);
            }
        },
        QUIT(1, 1) {

            @Override
            public void execute(Connection con, String messageTyped, String[] params) throws Exception {

                String msg;

                /* messageTyped = QUIT <reason>
                 * or
                 * messageTyped = QUIT
                 */

                String[] tokens = messageTyped.split(" ", 2);// Split in two (2) strings.

                if (tokens.length < 2) {
                    System.out.println("***Sending line to " + con.socket.getRemoteSocketAddress() + "*** " + messageTyped);
                    con.outStream.messageQueue.add("QUIT :" + Config.quit_message);
                    return;
                }

                msg = "QUIT :" + tokens[1];

                System.out.println("***Sending line to " + con.socket.getRemoteSocketAddress() + "*** " + msg);
                con.outStream.messageQueue.add(msg);

            }
        },
        SERVER(2, 2) { // Connect to a server.

            @Override
            public void execute(Connection con, String prefix, String[] params) throws Exception {
                // todo
            }
        },
        TOPIC(1, 2) {

            @Override
            public void execute(Connection con, String messageTyped, String[] params) throws Exception {

                String msg;

                /* messageTyped = TOPIC <channel> <topic>
                 * or
                 * messageTyped = TOPIC <channel>
                 */

                String[] tokens = messageTyped.split(" ", 3);// Split in two (3) strings.

                if (tokens.length < 3) {
                    System.out.println("***Sending line to " + con.socket.getRemoteSocketAddress() + "*** " + messageTyped);
                    con.outStream.messageQueue.add("TOPIC " + tokens[1]);
                    return;
                }

                msg = "TOPIC " + tokens[1] + " :" + tokens[2];

                System.out.println("***Sending line to " + con.socket.getRemoteSocketAddress() + "*** " + msg);
                con.outStream.messageQueue.add(msg);
            }
        };
        /* Fields */
        private int minArguments;
        private int maxArguments;

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
