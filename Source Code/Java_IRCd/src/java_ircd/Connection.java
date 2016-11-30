package java_ircd;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Connection implements Runnable {

    //================================================================================
    // Fields
    //================================================================================
    public static final Object MUTEX = new Object(); // This will prevent (whenever applied) multiple "Connection" threads to access/modify a resource (static field) while it is being used by a thread.
    public static ArrayList<String> temporaryIDs = new ArrayList<String>();
    public static Map<String, Connection> serverConnections = new HashMap<String, Connection>(); // A list of all connections to the server. The keys are the nicknames.
    public static Map<String, Channel> serverChannels = new HashMap<String, Channel>(); // A list of all channels on the server. The keys are the channel names.
    public Socket socket; // Socket used for a specific connection.
    public User client = new User(); // This instance of "User" gets destroyed along with the corresponding instance of "Connection".
    public InputStreamProcessor inStream = new InputStreamProcessor(this); // Handles all received messages.
    public OutputStreamProcessor outStream = new OutputStreamProcessor(this); // Handles all to-be-sent messages.

    //================================================================================
    // Constructors
    //================================================================================
    public Connection(Socket socket) {
        this.socket = socket;
    }
    //================================================================================
    // Connection Methods
    //================================================================================

    @Override
    public void run() { // Thread start for a "Connection" instance.
        try {
            doCommunication();
        } catch (Exception e) {

            try {
                socket.close(); // Terminate the connection to the specific Client.
            } catch (Exception e2) {
                e2.printStackTrace();
            }

            if (e.getMessage().equals("Connection reset")) {
                System.err.println("Client disconnected (" + client.getHostname() + ")");
            } else {
                System.err.println("Connection closed (" + client.getHostname() + ")");
            }

        } finally {
            if (client.getNick() != null && serverConnections.get(client.getNick()) == this) {
                try {
                    outStream.sendQuit("Client disconnected"); // Inform all channels that the user has quitted and remove the connection from the table.
                } catch (Exception ex) {
                    Logger.getLogger(Connection.class.getName()).log(Level.SEVERE, null, ex);
                }
                synchronized (Connection.MUTEX) {
                    serverConnections.remove(client.getNick()); // And remove the user from the table of connections.
                    temporaryIDs.remove(client.getTempEntryNick()); // Free the given ID.
                }
            }
        }
    }

    private void doCommunication() throws Exception {

        /* We will get the IP address of the client (who is at the other/remote side of the socket) */
        InetSocketAddress remoteAddress = (InetSocketAddress) socket.getRemoteSocketAddress(); // IP:PORTNUM
        String IP = remoteAddress.getAddress().getHostAddress();

        client.setHostname(IP); // We set the hostname for the Client.
        System.out.println("\nConnection from host " + client.getHostname());
        System.out.print("[ Encrypted Address = " + User.encryptHostname(client.getHostname()).toUpperCase());
        System.out.println(" , Decrypted Address = " + User.decryptHostname(User.encryptHostname(client.getHostname())) + " ]\n");

        //----------------------------------------------------------------------------
        // Handle the sending of messages.
        //----------------------------------------------------------------------------

        Thread outThread = new Thread(outStream); // Meanwhile we want to be able to send messages (stored in a queue) to the Client concurrently.
        outThread.start();

        //----------------------------------------------------------------------------
        // Disallow multiple connections from a host.
        //----------------------------------------------------------------------------

        outStream.sendNotice("Looking up your hostname...");

        if (checkForMultipleConnections(IP) > Config.ALLOWED_CONNECTIONS_FROM_HOST) {
            outStream.send("ERROR :Closing link (" + IP + ") [No more connections allowed from your host]");
            Thread.sleep(1000); // Sleep for 1 second.
            try {
                socket.shutdownOutput();
                socket.close(); // Terminate the connection to the specific Client.
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        //----------------------------------------------------------------------------
        // Handle the reception of messages.
        //----------------------------------------------------------------------------

        InputStream socketIn = socket.getInputStream(); //  getInputStream() returns an input stream (a sequence of characters) for this socket.
        BufferedReader reader = new BufferedReader(new InputStreamReader(socketIn));

        String line;
        while ((line = reader.readLine()) != null) {
            inStream.processLine(line); // Process every line that was received in the socket.
        }

    }

    private int checkForMultipleConnections(String IP) {
        int IP_count = 0;
        synchronized (Connection.MUTEX) {
            for (Connection con : serverConnections.values()) {
                if (con.client.getHostname().equals(IP)) {
                    IP_count++;
                }
            }
        }
        return IP_count;
    }
}
