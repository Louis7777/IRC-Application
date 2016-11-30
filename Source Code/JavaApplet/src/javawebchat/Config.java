package javawebchat;

import java.util.regex.Pattern;
import javax.swing.JApplet;

public class Config {

    //================================================================================
    // Fields
    //================================================================================
    public static final int SV_NAME_MAX_CHARS = 63;
    public static final int NICK_MAX_CHARS = 15;
    public static final int IDENT_MAX_CHARS = 15;
    public static final int DESC_MAX_CHARS = 50;
    public static final int QUIT_MSG_MAX_CHARS = 160;
    public static String server_name = "localhost";
    public static int server_port = 6667;
    public static String nickname = "Guest";
    public static String ident = "JavaWebChat";
    public static String description = "I am awesome!";
    public static String quit_message = "Quitting...";

    //================================================================================
    // Constructors
    //================================================================================
    public Config(JApplet environment) {

        /*
         * A server name cannot contain any illegal filename characters
         * such as: \ / : * ? " < > |
         * It may only contain alphanumeric, underscore, hypen and dot.
         */

        String server_name_regex = "[_a-zA-Z0-9\\-\\.]+";

        /*
         * An IRC nickname can contain any letter, number,
         * or any of the following characters: - _ [ ] | ^ ` { } \
         * It cannot start with a number.
         */

        String nickname_regex = "(?i)^[a-z\\-_\\[\\]\\|\\^`{}\\\\]"
                + "[a-z0-9\\-_\\[\\]\\|\\^`{}\\\\]*"; // (?i) --> case-insensitive

        /* <applet><param name="name_here" value="value_here"></param></applet> */

        try {
            server_name = environment.getParameter("SERVER").trim();

            /*
             * Theoretically, a domain name can be up to 256 characters long.
             * Officially, many registries including the .com, .net and .org
             * registries limit domain names to less than 63 characters.
             *
             */
            if (server_name.length() > SV_NAME_MAX_CHARS || !Pattern.matches(server_name_regex, server_name)) {
                server_name = "localhost";
            }

        } catch (Exception e1) {
            System.err.println("Parameter 'SERVER' was not found!");
        }

        try {
            server_port = Integer.parseInt(environment.getParameter("PORT").trim());

            if (server_port < 0) {
                server_port = 6667;
            }

        } catch (Exception e2) {
            System.err.println("Parameter 'PORT' was not found!");
        }

        try {
            nickname = environment.getParameter("NICKNAME").trim();

            if (nickname.length() > NICK_MAX_CHARS || !Pattern.matches(nickname_regex, nickname)) {
                nickname = "Guest";
            }

        } catch (Exception e3) {
            System.err.println("Parameter 'NICKNAME' was not found!");
        }

        try {
            ident = environment.getParameter("IDENT").trim();

            if (ident.length() > IDENT_MAX_CHARS || !Pattern.matches(nickname_regex, ident)) {
                ident = "JavaWebChat";
            }

        } catch (Exception e4) {
            System.err.println("Parameter 'IDENT' was not found!");
        }

        try {
            description = environment.getParameter("DESC").trim();

            if (description.length() > DESC_MAX_CHARS) {
                description = "I am awesome!";
            }
        } catch (Exception e5) {
            System.err.println("Parameter 'DESC' was not found!");
        }

        try {
            quit_message = environment.getParameter("QUITMSG").trim();

            if (quit_message.length() > QUIT_MSG_MAX_CHARS) {
                quit_message = "Quitting...";
            }
        } catch (Exception e6) {
            System.err.println("Parameter 'QUITMSG' was not found!");
        }

        /*System.out.println(server_name);
        System.out.println(server_port);
        System.out.println(nickname);
        System.out.println(ident);
        System.out.println(description);
        System.out.println(quit_message);*/

    }
    //================================================================================
    // Config Methods
    //================================================================================
    // do any required methods
}
