package javawebchat;

/* List of server responses, in alphabetical order:
 *
 * ERROR
 * JOIN
 * MODE
 * NICK
 * NOTICE
 * PART
 * PING
 * QUIT
 * TOPIC
 * 001
 * 002
 * 003
 * 004
 * 005
 * 251
 * 252
 * 253
 * 254
 * 255
 * 265
 * 266
 * 396
 * 331
 * 353
 * 366
 * 372
 * 375
 * 376
 *
 */
public class ServerResponses {

    /***************************** BEGIN OF COMMANDS SET *****************************/
    public enum ServerReply {

        ERROR(1, 2) {

            @Override
            public void execute(Connection con, String prefix, String[] params) throws Exception {

                String output = prefix + " " + params[params.length - 1] + "\n";
                con.consoleOutputPane.drawMessage(output, con.consoleOutputPane.clientErrorKeyWord);
            }
        },
        JOIN(1, 2) { // Accepted in new channel

            @Override
            public void execute(Connection con, String prefix, String[] params) throws Exception {

                //------------------------------------------------------------------------
                // Step 1: Analyze the hostmask in the prefix.
                //------------------------------------------------------------------------

                /*
                 * prefix = nick!ident@hostname
                 */

                String[] tokens = prefix.split("!", 2); // Split in two (2) strings.
                String nick = tokens[0];
                prefix = (tokens.length > 1 ? tokens[1] : "");

                /* prefix = ident@hostname
                 * or
                 * prefix = ""
                 */

                if (prefix.equals("")) {
                    System.err.println("JOIN: Invalid hostmask syntax.");
                    return;
                }

                String[] tokens1 = prefix.split("@", 2); // Split in two (2) strings.
                String ident = tokens1[0];
                prefix = (tokens1.length > 1 ? tokens1[1] : "");

                /* prefix = hostname
                 * or
                 * prefix = ""
                 */

                if (prefix.equals("")) {
                    System.err.println("JOIN: Invalid hostmask syntax.");
                    return;
                }

                String hostname = prefix;

                //------------------------------------------------------------------------
                // Step 2: If we are the specified user then proceed to create a
                // channel area for the reception of the messages.
                //------------------------------------------------------------------------

                ChannelOutputPane chan = new ChannelOutputPane(con);

                if (nick.equals(con.user.getNick())) { // If we are joining the channel.
                    chan.setTitle(params[0]);

                    con.joinedChannels.put(chan.getTitle(), chan); // Add the new channel to the list of joined channels for this connection.

                    JavaWebChat.addTab(chan, con); // Also add it to the tabbed pane.

                    chan.drawMessage("=>> You joined channel " + chan.getTitle() + "\n", con.consoleOutputPane.joinedKeyWord);
                } else { // If another user has joined the channel.

                    /* NOTE: Add a check in case that the channel doesn't exist. */

                    chan = con.joinedChannels.get(params[0]);
                    chan.drawMessage("* " + nick + " (" + ident + "@" + hostname + ") has joined " + chan.getTitle() + "\n", con.consoleOutputPane.joinedKeyWord);

                    if (!chan.listModel.contains(nick)) {
                        chan.addNickListRecord(nick);
                    }

                }
            }
        },
        MODE(0, 2) { // Direct this message to the corresponding channel or to self if a user mode.

            @Override
            public void execute(Connection con, String prefix, String[] params) throws Exception {

                /* Syntax:
                 * MODE <nickname/channel> <flags>
                 */

                if (params[0].startsWith("#")) {
                    ChannelOutputPane chan = con.joinedChannels.get(params[0]);
                    chan.drawMessage("=>> MODE ", con.consoleOutputPane.channelInfoKeyWord);
                    for (String s : params) {
                        chan.drawMessage(s, con.consoleOutputPane.channelInfoKeyWord);
                    }
                    chan.drawMessage("\n", null);
                } else {
                    for (String s : params) {
                        con.consoleOutputPane.drawMessage(s, null);
                    }
                    con.consoleOutputPane.drawMessage("\n", null);
                }

            }
        },
        NICK(1, 1) {

            @Override
            public void execute(Connection con, String prefix, String[] params) throws Exception {

                //------------------------------------------------------------------------
                // Step 1: Analyze the hostmask in the prefix.
                //------------------------------------------------------------------------

                /*
                 * prefix = nick!ident@hostname
                 */

                String[] tokens = prefix.split("!", 2); // Split in two (2) strings.
                String nick = tokens[0];
                prefix = (tokens.length > 1 ? tokens[1] : "");

                /* prefix = ident@hostname
                 * or
                 * prefix = ""
                 */

                if (prefix.equals("")) {
                    System.err.println("JOIN: Invalid hostmask syntax.");
                    return;
                }

                String[] tokens1 = prefix.split("@", 2); // Split in two (2) strings.
                String ident = tokens1[0];
                prefix = (tokens1.length > 1 ? tokens1[1] : "");

                /* prefix = hostname
                 * or
                 * prefix = ""
                 */

                if (prefix.equals("")) {
                    System.err.println("JOIN: Invalid hostmask syntax.");
                    return;
                }

                String hostname = prefix;

                //------------------------------------------------------------------------
                // Step 2: If we are the specified user then proceed to change how
                // our nickname appears on the client since the server informed us that
                // it has changed how it appears to other users.
                // If someone else has changed a nickname,
                // we simply want to output that information.
                //------------------------------------------------------------------------

                String oldNick = con.user.getNick();

                if (nick.equals(oldNick)) { // If it is our nickname that has changed.
                    con.user.setNick(params[0]);
                    con.consoleOutputPane.drawMessage("=>> You are now known as " + params[0] + "\n", con.consoleOutputPane.nickChangeKeyWord);
                }

                for (ChannelOutputPane chan : con.joinedChannels.values()) { // For every channel that we are a member of.

                    for (int i = 0; i < chan.listModel.size(); i++) { // For every nickname in the channel.

                        if (nick.equals(chan.listModel.getElementAt(i).toString())) { // If the nickname that changed exists in its nicknames list.

                            if (nick.equals(oldNick)) { // If it is our nickname that has changed.
                                chan.modifyNickListRecord(i, params[0]);
                                break;
                            } else { // If another user has changed nickname.
                                chan.drawMessage("* " + nick + " is now known as " + params[0] + "\n", con.consoleOutputPane.nickChangeKeyWord);
                                chan.modifyNickListRecord(i, params[0]);
                                break;
                            }
                        }
                    }
                }

            }
        },
        NOTICE(1, 2) {

            @Override
            public void execute(Connection con, String prefix, String[] params) throws Exception {

                String output = prefix + " " + params[params.length - 1] + "\n";
                con.consoleOutputPane.drawMessage(output, con.consoleOutputPane.noticeKeyWord);

            }
        },
        PART(1, 2) { // Confirmed leaving a channel.

            @Override
            public void execute(Connection con, String prefix, String[] params) throws Exception {

                //------------------------------------------------------------------------
                // Step 1: Analyze the hostmask in the prefix.
                //------------------------------------------------------------------------

                /*
                 * prefix = nick!ident@hostname
                 */

                String[] tokens = prefix.split("!", 2); // Split in two (2) strings.
                String nick = tokens[0];
                prefix = (tokens.length > 1 ? tokens[1] : "");

                /* prefix = ident@hostname
                 * or
                 * prefix = ""
                 */

                if (prefix.equals("")) {
                    System.err.println("JOIN: Invalid hostmask syntax.");
                    return;
                }

                String[] tokens1 = prefix.split("@", 2); // Split in two (2) strings.
                String ident = tokens1[0];
                prefix = (tokens1.length > 1 ? tokens1[1] : "");

                /* prefix = hostname
                 * or
                 * prefix = ""
                 */

                if (prefix.equals("")) {
                    System.err.println("JOIN: Invalid hostmask syntax.");
                    return;
                }

                String hostname = prefix;

                //------------------------------------------------------------------------
                // Step 2: If we are the specified user then proceed to remove the channel
                // from the joinedChannels table.
                //------------------------------------------------------------------------

                ChannelOutputPane chan = con.joinedChannels.get(params[0]);

                if (nick.equals(con.user.getNick())) { // If we are leaving the channel.

                    String channelName = null;

                    if (chan != null) { // If channel exists in the list of this connection's channels.
                        channelName = chan.getTitle();
                        con.joinedChannels.remove(params[0]); // Remove it from the list.
                    }

                    /* In case the tab still exists in the tabbed pane... */
                    JavaWebChat.removeTab(chan);

                    con.consoleOutputPane.drawMessage("=>> You parted channel " + channelName + "\n", con.consoleOutputPane.partedKeyWord);
                } else { // If another user has left the channel.

                    if (params.length < 2) { // If no part reason was given.
                        chan.drawMessage("* " + nick + " (" + ident + "@" + hostname + ") has left " + chan.getTitle() + "\n", con.consoleOutputPane.partedKeyWord);
                    } else { // If a part reason was given.
                        chan.drawMessage("* " + nick + " (" + ident + "@" + hostname + ") has left " + chan.getTitle() + " (" + params[1] + ")\n", con.consoleOutputPane.partedKeyWord);
                    }

                    chan.delNickListRecord(nick);

                }
            }
        },
        PING(1, 1) { // Server is doing PING, must reply with PONG

            @Override
            public void execute(Connection con, String prefix, String[] params) throws Exception {

                //Thread.sleep(1000); //Pause for 1 second before sending reply.

                //System.out.println("PONG MESSAGE WILL BE: " + arguments[arguments.length - 1]);

                con.outStream.send("/PONG " + params[params.length - 1]);

            }
        },
        PRIVMSG(2, 2) {

            @Override
            public void execute(Connection con, String prefix, String[] params) throws Exception {

                //------------------------------------------------------------------------
                // Step 1: Analyze the hostmask in the prefix.
                //------------------------------------------------------------------------

                /*
                 * prefix = nick!ident@hostname
                 */

                String[] tokens = prefix.split("!", 2); // Split in two (2) strings.
                String nick = tokens[0];
                prefix = (tokens.length > 1 ? tokens[1] : "");

                /* prefix = ident@hostname
                 * or
                 * prefix = ""
                 */

                if (prefix.equals("")) {
                    System.err.println("JOIN: Invalid hostmask syntax.");
                    return;
                }

                String[] tokens1 = prefix.split("@", 2); // Split in two (2) strings.
                String ident = tokens1[0];
                prefix = (tokens1.length > 1 ? tokens1[1] : "");

                /* prefix = hostname
                 * or
                 * prefix = ""
                 */

                if (prefix.equals("")) {
                    System.err.println("JOIN: Invalid hostmask syntax.");
                    return;
                }

                String hostname = prefix;

                //------------------------------------------------------------------------
                // Step 2: If we are the specified user...
                //------------------------------------------------------------------------

                String[] recipients = params[0].split(",");
                String message = params[1];

                for (String recipient : recipients) {
                    /* If the message destination is not a channel then it is a user. */
                    if (recipient.startsWith("#")) { // Message to channel.
                        ChannelOutputPane chan = con.joinedChannels.get(recipient);
                        if (chan == null) {
                            con.consoleOutputPane.drawMessage("Not member of such channel, so can't send a message to it: " + recipient + "\n", con.consoleOutputPane.clientErrorKeyWord);
                        } else {
                            chan.drawMessage("<" + nick + "> " + message + "\n", null);
                        }
                    } else { // Message to user.

                        Integer index = JavaWebChat.tabbedPane.indexOfTab(nick);

                        MessagePanel privateChat = new QueryOutputPane(con);
                        privateChat.setTitle(nick);

                        if (index < 0) { // If tab for private chat with the user doesn't exist.
                            JavaWebChat.addTab(privateChat, con);
                        } else { // If it exists, get its panel.
                            synchronized (Connection.MUTEX) {
                                privateChat = (QueryOutputPane) JavaWebChat.tabbedPane.getComponentAt(index);
                            }
                        }

                        privateChat.drawMessage("<" + nick + "> " + message + "\n", null); // Send it to self.

                    }
                }
            }
        },
        QUIT(1, 1) {

            @Override
            public void execute(Connection con, String prefix, String[] params) throws Exception {

                //------------------------------------------------------------------------
                // Step 1: Analyze the hostmask in the prefix.
                //------------------------------------------------------------------------

                /*
                 * prefix = nick!ident@hostname
                 */

                String[] tokens = prefix.split("!", 2); // Split in two (2) strings.
                String nick = tokens[0];
                prefix = (tokens.length > 1 ? tokens[1] : "");

                /* prefix = ident@hostname
                 * or
                 * prefix = ""
                 */

                if (prefix.equals("")) {
                    System.err.println("JOIN: Invalid hostmask syntax.");
                    return;
                }

                String[] tokens1 = prefix.split("@", 2); // Split in two (2) strings.
                String ident = tokens1[0];
                prefix = (tokens1.length > 1 ? tokens1[1] : "");

                /* prefix = hostname
                 * or
                 * prefix = ""
                 */

                if (prefix.equals("")) {
                    System.err.println("JOIN: Invalid hostmask syntax.");
                    return;
                }

                String hostname = prefix;

                //------------------------------------------------------------------------
                // Step 2: If we are the specified user then proceed to remove the channel
                // from the joinedChannels table.
                //------------------------------------------------------------------------

                if (!nick.equals(con.user.getNick())) { // If a user has quitted.

                    for (ChannelOutputPane chan : con.joinedChannels.values()) {

                        for (int i = 0; i < chan.listModel.size(); i++) {

                            if (nick.equals(chan.listModel.getElementAt(i).toString())) {
                                chan.drawMessage("* " + nick + " (" + ident + "@" + hostname + ") Quit ( " + params[0] + " )\n", con.consoleOutputPane.partedKeyWord);
                                chan.delNickListRecord(nick);
                                break;
                            }
                        }
                    }

                }

            }
        },
        TOPIC(1, 2) {

            @Override
            public void execute(Connection con, String prefix, String[] params) throws Exception {

                //------------------------------------------------------------------------
                // Step 1: Analyze the hostmask in the prefix.
                //------------------------------------------------------------------------

                /*
                 * prefix = nick!ident@hostname
                 */

                String[] tokens = prefix.split("!", 2); // Split in two (2) strings.
                String nick = tokens[0];
                prefix = (tokens.length > 1 ? tokens[1] : "");

                /* prefix = ident@hostname
                 * or
                 * prefix = ""
                 */

                if (prefix.equals("")) {
                    System.err.println("JOIN: Invalid hostmask syntax.");
                    return;
                }

                String[] tokens1 = prefix.split("@", 2); // Split in two (2) strings.
                String ident = tokens1[0];
                prefix = (tokens1.length > 1 ? tokens1[1] : "");

                /* prefix = hostname
                 * or
                 * prefix = ""
                 */

                if (prefix.equals("")) {
                    System.err.println("JOIN: Invalid hostmask syntax.");
                    return;
                }

                String hostname = prefix;

                //------------------------------------------------------------------------
                // Step 2: If we are the specified user then proceed to remove the channel
                // from the joinedChannels table.
                //------------------------------------------------------------------------

                ChannelOutputPane chan = new ChannelOutputPane(con);
                chan = con.joinedChannels.get(params[0]);
                if (chan == null) {
                    return;
                } else {
                    chan.drawMessage("* " + nick + " sets topic to: " + params[1] + "\n", null);
                    chan.topic.setText(params[1]);
                }

            }
        },
        n001(1, 2) { // RPL_WELCOME - The first message sent after client registration. The text used varies widely.

            @Override
            public void execute(Connection con, String prefix, String[] params) throws Exception {
                String output = prefix + " " + params[params.length - 1] + "\n";
                con.consoleOutputPane.drawMessage(output, null);
            }
        },
        n002(1, 5) { // Part of the post-registration greeting. Text varies widely.

            @Override
            public void execute(Connection con, String prefix, String[] params) throws Exception {
                String output = prefix + " " + params[params.length - 1] + "\n";
                con.consoleOutputPane.drawMessage(output, null);
            }
        },
        n003(1, 5) { // Part of the post-registration greeting. Text varies widely.

            @Override
            public void execute(Connection con, String prefix, String[] params) throws Exception {
                String output = prefix + " " + params[params.length - 1] + "\n";
                con.consoleOutputPane.drawMessage(output, null);
            }
        },
        n004(1, 5) { // RPL_MYINFO - Part of the post-registration greeting.

            @Override
            public void execute(Connection con, String prefix, String[] params) throws Exception {
                String output = prefix + " " + params[params.length - 1] + "\n";
                con.consoleOutputPane.drawMessage(output, null);
            }
        },
        n005(1, 15) { // RPL_MYINFO - Part of the post-registration greeting.

            @Override
            public void execute(Connection con, String prefix, String[] params) throws Exception {
                String output = prefix + " " + params[params.length - 1] + "\n";
                con.consoleOutputPane.drawMessage(output, null);
            }
        },
        n331(1, 3) { // (331) Response to TOPIC when no topic is set.

            @Override
            public void execute(Connection con, String prefix, String[] params) throws Exception {
                ChannelOutputPane chan = con.joinedChannels.get(params[1]);
                if (chan == null) {
                    con.consoleOutputPane.drawMessage("=>> Topic of " + params[1] + " is: <None>\n", null);
                } else {
                    chan.drawMessage("=>> Topic is: <None>\n", con.consoleOutputPane.channelInfoKeyWord);
                }
            }
        },
        n332(1, 3) { // (332) Response to TOPIC with the set topic.

            @Override
            public void execute(Connection con, String prefix, String[] params) throws Exception {
                ChannelOutputPane chan = con.joinedChannels.get(params[1]);
                if (chan == null) {
                    con.consoleOutputPane.drawMessage("=>> Topic of " + params[1] + " is: " + params[params.length - 1] + "\n", null);
                } else {
                    chan.drawMessage("=>> Topic is: " + params[params.length - 1] + "\n", con.consoleOutputPane.channelInfoKeyWord);
                    if (!chan.topic.getText().equals(params[params.length - 1])) {
                        chan.topic.setText(params[params.length - 1]);
                    }
                }

            }
        },
        n353(1, 4) { //(353) Reply to NAMES.

            @Override
            public void execute(Connection con, String prefix, String[] params) throws Exception {
                ChannelOutputPane chan = con.joinedChannels.get(params[2]);

                String[] nickname = params[3].split(" ");

                for (int i = 0; i < nickname.length; i++) {
                    chan.addNickListRecord(nickname[i]);
                }
            }
        },
        n366(1, 4) { // (366) Termination of an RPL_NAMREPLY list.

            @Override
            public void execute(Connection con, String prefix, String[] params) throws Exception {
                //ChannelPanel chan = con.joinedChannels.get(arguments[1]);
                //chan.drawMessage(arguments[0] + "\n", null);
            }
        },
        n372(1, 2) { // (372) Reply to MOTD.

            @Override
            public void execute(Connection con, String prefix, String[] params) throws Exception {
                con.consoleOutputPane.drawMessage(params[params.length - 1] + "\n", con.consoleOutputPane.motdKeyWord);
            }
        },
        n375(1, 2) { // (375) Start of an RPL_MOTD list.

            @Override
            public void execute(Connection con, String prefix, String[] params) throws Exception {
                con.consoleOutputPane.drawMessage("*** Message of the day ***\n", con.consoleOutputPane.motdKeyWord);
            }
        },
        n376(1, 2) { // (376) Termination of an RPL_MOTD list.

            @Override
            public void execute(Connection con, String prefix, String[] params) throws Exception {
                con.consoleOutputPane.drawMessage("*** End of Message of the Day ***\n", con.consoleOutputPane.motdKeyWord);
            }
        };
        /* Fields */
        private int minArguments;
        private int maxArguments;

        /* Constructor */
        private ServerReply(int min, int max) {
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
