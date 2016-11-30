package javawebchat;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import javawebchat.ClientCommands.Command;

public class OutputStreamProcessor implements Runnable {

    //================================================================================
    // Fields
    //================================================================================
    private Connection con;
    /* This queue orders elements FIFO (first-in-first-out) and it has a given capacity of elements.
     * The head of the queue is the element that has been on the queue the longest time. */
    public LinkedBlockingQueue<String> messageQueue = new LinkedBlockingQueue<String>(1000);

    //================================================================================
    // Constructors
    //================================================================================
    public OutputStreamProcessor(Connection con) {
        this.con = con;
    }

    //================================================================================
    // OutputStreamProcessor Methods
    //================================================================================
    @Override
    public void run() {
        try {
            OutputStream out = con.socket.getOutputStream();

            /* Below we designate two characters, CR (Carriage return) )and LF (Line feed), as message separators. */
            while (true) {
                String s = messageQueue.take(); // Retrieve the oldest string from the queue.
                s = s.replace("\n", "").replace("\r", ""); // We replace any LF and CR characters.
                s = s + "\r\n"; // Add the message separator at the end of the message.
                out.write(s.getBytes()); // Message to send to the client.
                out.flush(); // Clear the Output Stream.
            }
        } catch (Exception e) {
            System.err.println("The message queue died.");
            messageQueue.clear();
            messageQueue = null; // Ensure that the queue is empty.
            try {
                con.socket.close(); // Terminate the connection to the specific server.
                if (e.getMessage().equals("Socket closed")) {
                    con.consoleOutputPane.drawMessage("=>> You are not connected!\n", con.consoleOutputPane.clientFatalErrorKeyWord);
                } else {
                    con.consoleOutputPane.drawMessage("=>> Disconnected\n", con.consoleOutputPane.clientFatalErrorKeyWord);
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }

    /* For better understanding of this method, see BottomPanel.preprocessMessage()
     * as the messages that the user types in the message box are preprocessed there
     * before reaching the send() method which ultimately adds them to the message queue.
     */
    public void send(String s) {
        if (messageQueue != null) {

            //------------------------------------------------------------------------
            // Message analysis.
            //------------------------------------------------------------------------


            /* Check whether the message that was typed in the
             * message area is a <command message> or a <chat message>.
             * Commands always begin with a forward slash (/)
             */
            if (s.startsWith("/")) {

                s = s.trim(); // Remove leading and trailing whitespace.

                Command commandObject = null;
                ArrayList<String> paramList = new ArrayList<String>();
                String command;
                String[] params;
                String initialMessage;

                //--------------------------------------------------------------------
                // Command analysis.
                //--------------------------------------------------------------------

                s = s.substring(1); // Keep all the message after the forward slash.

                if (s.startsWith(" ")) { // There must be no gap between the / and the command.
                    return;
                }

                initialMessage = s; // Keep the initial command message.

                /* Let's check if it is the special command PRIVMSG */
                if (s.toUpperCase().startsWith("PRIVMSG")) {
                    sendPrivMsg(s);
                    return;
                }

                /* s = <command> <param_1> <param_2> ... <param_n>
                 * or
                 * s = ""
                 */

                if (s.equals("")) {
                    return;
                } else {
                    String[] tokens = s.split(" ", 2);// Split in two (2) strings.
                    command = tokens[0]; // Store the command
                    s = (tokens.length > 1 ? tokens[1] : ""); // If there were no parameters given, then s = ""
                }

                /* s = <param_1> <param_2> ... <param_n>
                 * or
                 * s = ""
                 */

                if (s.equals("")) {
                    con.consoleOutputPane.drawMessage("=>> Error - " + command + " :Not enough parameters.\n", con.consoleOutputPane.clientErrorKeyWord);
                    return;
                } else {
                    paramList.add(s);
                    params = paramList.toArray(new String[0]); // Store the parameters.
                }

                //--------------------------------------------------------------------
                // Processing the command.
                //--------------------------------------------------------------------

                /* Let's check if the command is a special client command or else send it as is. */
                try {
                    commandObject = Command.valueOf(command.toLowerCase()); // Search in the commands set to see if the command exists.
                } catch (Exception e) {
                }
                if (commandObject == null) {
                    try {
                        commandObject = Command.valueOf(command.toUpperCase()); // Search in the commands set to see if the command exists.
                    } catch (Exception e) {
                    }
                }


                if (commandObject == null) {
                    //System.err.println("Some commmand huh?");
                    System.out.println("***Sending line to " + con.socket.getRemoteSocketAddress() + "*** " + initialMessage);
                    messageQueue.add(initialMessage); // Sending our command.
                } /*else if (command.toLowerCase().equals("part") && GUI.currentView.startsWith("#")) {
                try {
                commandObject.execute(con, command + " " + GUI.currentView + " " + params[params.length-1], params); // One last step before sending our command.
                } catch (Exception ex) {
                ex.printStackTrace();
                }
                }*/ else {
                    try {
                        commandObject.execute(con, initialMessage, params); // One last step before sending our command.
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }

            } else if (s.toUpperCase().startsWith("PRIVMSG")) {
                sendPrivMsg(s);
            } else {
                System.err.println("Message was not sent.");
                return;
            }

        }
    }

    /* This method is only used by method OutPutStreamProcessor.send()
     * Using this method we can send a message to a channel or to a user.
     */
    private void sendPrivMsg(String s) {

        Command commandObject = null;
        ArrayList<String> paramList = new ArrayList<String>();
        String destination = null;
        String[] message;

        /* s = PRIVMSG <destination> <message>
         * or
         * s = PRIVMSG <destination>
         * or
         * s = PRIVMSG
         */

        String[] tokens = s.split(" ", 3);// Split in two (3) strings.

        if (tokens.length < 3) {
            con.consoleOutputPane.drawMessage("=>> Error - PRIVMSG :Not enough parameters.\n", con.consoleOutputPane.clientErrorKeyWord);
            return;
        }

        destination = tokens[1];
        paramList.add(tokens[2]);
        message = paramList.toArray(new String[0]); // Store the message.

        commandObject = Command.valueOf("PRIVMSG");
        try {
            commandObject.execute(con, destination, message); // One last step before sending our message.
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }
}
