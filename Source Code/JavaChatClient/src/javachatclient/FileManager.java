package javachatclient;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

/*
 * Manager for .properties files.
 */
public class FileManager {

    //================================================================================
    // Fields
    //================================================================================
    private Properties serverProperties = new Properties();

    //================================================================================
    // Constructors
    //================================================================================
    public FileManager() {
    }

    //================================================================================
    // FileManager Methods
    //================================================================================
    /*
     * putProperties():
     * Writes values to a .properties file.
     */
    public void putProperties(String serverName, ArrayList<String> properties) {

        String fileName = Config.SERVER_PREFS_REL_PATH + serverName + ".properties"; // The relative path to the file.

        openFile(fileName); // Open or create the file.

        for (int i = 0; i < properties.size(); i++) {
            if (properties.get(i) != null) {

                try {
                    String[] s = properties.get(i).split("="); // Get the name and value of the property.
                    serverProperties.setProperty(s[0], s[1]); // Make a property with the above name and value.
                } catch (ArrayIndexOutOfBoundsException e) {
                    String s = properties.get(i).substring(0, properties.get(i).indexOf("=")); // OK
                    serverProperties.setProperty(s, "");
                }
            }
        }

        FileOutputStream out = null;

        try {
            out = new FileOutputStream(fileName);
            serverProperties.store(out, "Server parameters. *** DO NOT MODIFY ***"); // Save all properties to file.
            out.close(); // !IMPORTANT! Close the opened properties file.
        } catch (IOException ex) {
            System.err.println("Error: Could not write the properties to file.");
        }

    }

    /* getProperties():
     * Opens an existing .properties file,
     * retrieves property values and returns them in an ArrayList.
     */
    public ArrayList<String> getProperties(String serverName) {

        ArrayList<String> properties = new ArrayList<String>(); // The properties to return.

        String fileName = Config.SERVER_PREFS_REL_PATH + serverName + ".properties"; // The relative path to the file.

        try {

            FileInputStream in = openFile(fileName); // Open or create the file.

            if (in == null) {
                System.out.println("Well, fuck you too.");
                return properties;
            }

            serverProperties.load(in); // Load a properties file.

            /* 1 */

            if (serverProperties.containsKey("server_name") && !serverProperties.getProperty("server_name").equals("")) {
                properties.add(0, serverProperties.getProperty("server_name")); // Read the server_name value.
            } else {
                properties.add(0, "");
            }

            /* 2 */

            if (serverProperties.containsKey("server_port") && !serverProperties.getProperty("server_port").equals("")) {
                properties.add(1, serverProperties.getProperty("server_port")); // Read the server_port value.
            } else {
                properties.add(1, "");
            }

            /* 3 */

            if (serverProperties.containsKey("nickname") && !serverProperties.getProperty("nickname").equals("")) {
                properties.add(2, serverProperties.getProperty("nickname")); // Read the nickname value.
            } else {
                properties.add(2, "");
            }

            /* 4 */

            if (serverProperties.containsKey("ident_name") && !serverProperties.getProperty("ident_name").equals("")) {
                properties.add(3, serverProperties.getProperty("ident_name")); // Read the ident_name value.
            } else {
                properties.add(3, "");
            }

            /* 5 */

            if (serverProperties.containsKey("description") && !serverProperties.getProperty("description").equals("")) {
                properties.add(4, serverProperties.getProperty("description")); // Read the description value.
            } else {
                properties.add(4, "");
            }

            /* 6 */

            if (serverProperties.containsKey("quit_message") && !serverProperties.getProperty("quit_message").equals("")) {
                properties.add(5, serverProperties.getProperty("quit_message")); // Read the quit_message value.
            } else {
                properties.add(5, "");
            }

            in.close(); // !IMPORTANT! Close the opened properties file.

        } catch (IOException e) {
            System.out.println("File " + fileName + " is missing or corrupt.");
        }

        return properties;
    }

    /* openFile():
     * Opens an existing .properties file or creates one
     * if the given file was not found.
     */
    private FileInputStream openFile(String filename) {

        FileInputStream in = null;

        try {
            in = new FileInputStream(filename);
            serverProperties.load(in); // Load a properties file.
        } catch (IOException ex) {
            System.out.println("------------------------------------------------");
            System.out.println(ex.getMessage());
            System.out.println("File could not be found and will be created.");
            try {
                /* We will create a new file if the provided file has failed to load. */

                File file = new File(filename);
                file.createNewFile(); // Create a new empty file.
                System.out.println("*** New file created. ***");
                System.out.println("----------------------------------------------------");
                return in;
            } catch (IOException ex1) {
                System.err.println("Error: Could not create new .properties file.");
            }
        }

        return in;
    }

    /* deleteFile():
     * Deletes a selected file provided that it is not currently in use.
     */
    public boolean deleteServer(String serverName) {

        String filename = Config.SERVER_PREFS_REL_PATH + serverName + ".properties"; // The relative path to the file.

        File file = new File(filename);

        if (file.delete()) {
            System.out.println(file.getName() + " is deleted!");
            return true;
        } else {
            System.err.println("Delete operation failed.");
            return false;
        }
    }
}
