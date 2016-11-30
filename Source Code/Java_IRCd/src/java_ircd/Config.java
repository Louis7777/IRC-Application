package java_ircd;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/* Configuration of Server.
 *
 */
public class Config {

    //================================================================================
    // Fields
    //================================================================================
    private static final Config CONFIGURATION = new Config(); // Singleton.
    private Properties serverProperties = new Properties();
    private static String serverName;
    private static String keySeed;
    public static final int ALLOWED_CONNECTIONS_FROM_HOST = 2;

    //================================================================================
    // Constructors
    //================================================================================
    private Config() {

        try {
            serverProperties.load(new FileInputStream("config.properties")); // Load a properties file.

            if (serverProperties.containsKey("servername")) {
                serverName = serverProperties.getProperty("servername"); // Read the servername value.
            } else {
                System.err.println("The 'servername' property is missing from file 'config.properties'.");
            }

            if (serverProperties.containsKey("keyseed")) {
                keySeed = serverProperties.getProperty("keyseed"); // Read the keyseed value.
            } else {
                System.err.println("The 'keyseed' property is missing from file 'config.properties'.");
            }

        } catch (IOException e) {
            System.err.println("File 'config.properties' is missing or corrupt.");
        }

    }

    //================================================================================
    // Accessors
    //================================================================================
    public static String getServerName() {
        return serverName;
    }

    public static String getKeySeed() {
        return keySeed;
    }

    public static Config getConfiguration() {
        return CONFIGURATION;
    }
    //================================================================================
    // Config Methods
    //================================================================================
    // do any required methods
}
