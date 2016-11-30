package java_ircd;

import java.net.ServerSocket;
import java.net.Socket;

/* "Java Internet Relay Chat Daemon"
 *
 * About the IRC protocol (RFC 1459):
 * http://www.irchelp.org/irchelp/rfc/rfc.html
 *
 */
public class Java_IRCd {

    public static void main(String[] args) throws Throwable {


        //================================================================================
        // Server Setup.
        //================================================================================

        boolean listening = true;
        Config.getConfiguration();
        ServerSocket server_entrance = new ServerSocket(6667); // Creates a server socket bound to the specified port and listens for connections.

        //================================================================================
        // Listen for connections and establish connections.
        //================================================================================

        System.out.println("Server: " + Config.getServerName());
        System.out.println("Listening for incoming connections...\n");

        while (listening) {
            Socket s = server_entrance.accept(); // Receives an incoming connection request to this socket and accepts it.
            Connection IRCd = new Connection(s); // Creates a new client-server connection - handled by the "Connection" class - using the above socket.

            IRCd.client.setNick(User.generateTemporaryUserID()); // Set a temporary nickname for the client.
            IRCd.client.setTempEntryNick(IRCd.client.getNick());
            synchronized (Connection.MUTEX) {
                Connection.serverConnections.put(IRCd.client.getNick(), IRCd); // Add it to the connections table with the temporary nickname.
            }

            Thread connection = new Thread(IRCd); // Will put the connection in its own thread. Thread is "Ready to Run".
            connection.start(); // Connection is alive. Thread is "Running".
        }

        server_entrance.close();
    }
}
