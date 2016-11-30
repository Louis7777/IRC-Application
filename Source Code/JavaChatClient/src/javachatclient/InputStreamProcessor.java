package javachatclient;

import java.util.ArrayList;
import java.util.Arrays;
import javachatclient.ServerResponses.ServerReply; // Import the set of server responses from the "ServerResponses" Class.

/*
 * IRC messages are limited to 512 characters in length.
 * The IRC message format:
 *
 *                  :<prefix> <command> <params> :<trailing>CR-LF
 *
 * *****************************************************************************
 * *****************************************************************************
 *
 *                             - RFC 1459 §2.3 -
 *
 * 1. The prefix (optional).
 *
 * The presence of a prefix is indicated with a single leading ASCII
 * colon character (‘:’, 0x3b), which must be the first character
 * of the message itself. There must be no gap (whitespace)
 * between the colon and the prefix.
 *
 * 2. The command.
 *
 * The command must either be a valid IRC command or
 * a three (3) digit number represented in ASCII text.
 *
 * 3. The command's parameters.
 *
 * There may be up to 15 parameters, counting the trailing.
 *
 * 4. The trailing message.
 *
 * A string of text. It counts as a special command parameter.
 * It may contain spaces, and thus must be prefixed with a colon.
 *
 * 5. The message separator.
 *
 * IRC messages are always lines of charactes terminated with a
 * CR-LF (Carriage Return - Line Feed) pair,
 * and these messages shall not exceed 512 characters in length,
 * counting all characters including the trailing CR-LF.
 *
 * *****************************************************************************
 */
public class InputStreamProcessor {

    //================================================================================
    // Fields
    //================================================================================
    private Connection con;

    //================================================================================
    // Constructors
    //================================================================================
    public InputStreamProcessor(Connection con) {
        this.con = con;
    }

    //================================================================================
    // InputStreamProcessor Methods
    //================================================================================
    public void processLine(String line) throws Exception {

        /* NOTE: The trim below will disable extra whitespace
         * at the end of messages sent by other users.
         * Perhaps this will be reconsidered at later builds.
         */

        line = line.trim(); // Remove leading and trailing whitespace.

        System.out.println("***Processing line*** " + line);

        //----------------------------------------------------------------------------
        // Message analysis.
        //----------------------------------------------------------------------------

        String prefix = "";
        String command;
        ArrayList<String> paramList = new ArrayList<String>();
        String[] params;
        String trailing = null;

        /* line = :<prefix> <command> <params> :<trailing>
         * or
         * line = ""
         * We will extract the prefix, if any.
         */

        if (line.startsWith(":")) {
            String[] tokens = line.split(" ", 2); // Split in two (2) strings.

            /* If a split was not performed,
             * the entire line will be stored at the first position of the array */

            prefix = tokens[0].substring(1); // Store the prefix.
            line = (tokens.length > 1 ? tokens[1] : "");
        }

        /* line = <command> <params> :<trailing>
         * or
         * line = ""
         * We will extract the command.
         */

        String[] tokens1 = line.split(" ", 2); // Split in two (2) strings.
        command = tokens1[0]; // Store the command.
        line = tokens1.length > 1 ? tokens1[1] : "";

        /* line = <params> :<trailing>
         * or
         * line = ""
         * We will extract the trailing message.
         */

        String[] tokens2 = line.split("(^| )\\:", 2); // Regex "^:" or " :"
        if (tokens2.length > 1) {
            trailing = tokens2[1]; // Store the trailing message.
        }
        line = tokens2[0];

        /* line = <params>
         * or
         * line = ""
         * We will store all the parameters.
         */

        if (!line.equals("")) {
            /* At this point the line is in this form:
             * <param_1> <param_2> ... <param_n)
             */
            paramList.addAll(Arrays.asList(line.split(" ")));
        }
        if (trailing != null) {
            paramList.add(trailing);
        }
        params = paramList.toArray(new String[0]); // Store the parameters.

        //----------------------------------------------------------------------------
        // Processing the command.
        //----------------------------------------------------------------------------

        /* Is the command numeric? */

        if (command.matches("[0-9][0-9][0-9]")) { // Regex: Three consequent digits, each one from 0 to 9.
            command = "n" + command;
        }

        /* Let's search for the command and find out whether our server knows it,
         * and whether it meets the requirements.
         */

        ServerReply replyObject = null;

        try {
            replyObject = ServerReply.valueOf(command.toLowerCase()); // Search in the commands set to see if the command exists.
        } catch (Exception e) {
        }
        if (replyObject == null) {
            try {
                replyObject = ServerReply.valueOf(command.toUpperCase()); // Search in the commands set to see if the command exists.
            } catch (Exception e) {
            }
        }
        if (replyObject == null) {
            con.consoleOutputPane.drawMessage("=>> Failed to understand the server command...\n", con.consoleOutputPane.parseErrorKeyWord);
            return;
        }
        if (params.length < replyObject.getMinArguments() || params.length > replyObject.getMaxArguments()) {
            con.consoleOutputPane.drawMessage("=>> Failed to understand server command parameters...\n", con.consoleOutputPane.parseErrorKeyWord);
            return;
        }

        /* Command has been found and will be executed. */
        replyObject.execute(con, prefix, params);
    }
}
