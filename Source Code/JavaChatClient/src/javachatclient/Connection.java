package javachatclient;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class Connection implements Runnable {

    //================================================================================
    // Fields
    //================================================================================
    public static final Object MUTEX = new Object(); // This will prevent (whenever applied) multiple "Connection" threads to access/modify a resource (static field) while it is being used by a thread.
    public Socket socket = null; // Socket used for a specific connection.
    public User user = new User(); // This instance of "User" gets destroyed along with the corresponding instance of "Connection".
    public InputStreamProcessor inStream = new InputStreamProcessor(this); // Handles all received messages.
    public OutputStreamProcessor outStream = new OutputStreamProcessor(this); // Handles all to-be-sent messages.
    public ConsoleOutputPane consoleOutputPane = new ConsoleOutputPane(this); // Every connection has ONE console output pane.
    public Map<String, ChannelOutputPane> joinedChannels = new HashMap<String, ChannelOutputPane>(); // Key is channel name. Value is ChannelOutputPane object.
    public String server; // Name of server that we are connected to.

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
    public void run() {
        try {
            doCommunication();
        } catch (Exception e) {

            try {
                socket.close(); // Terminate the connection to the specific Server.
                //System.exit(1);
            } catch (Exception e2) {
                e2.printStackTrace();
            }

            if (e.getMessage().equals("Connection reset")) {
                System.err.println("Disconnected\n");
                consoleOutputPane.drawMessage("=>> Disconnected\n", consoleOutputPane.clientFatalErrorKeyWord);
            } else {
                e.printStackTrace();
            }

        } finally {
            synchronized (Connection.MUTEX) {
                GUI.connections.remove(server); // And remove it from the table of connections.
            }
        }
    }

    private void doCommunication() throws Exception {

        /* We will get the IP address of the client (who is at the other/remote side of the socket) */
        InetSocketAddress remoteAddress = (InetSocketAddress) socket.getRemoteSocketAddress(); // IP:PORTNUM

        consoleOutputPane.drawMessage("=>> Connected to " + remoteAddress + "\n", consoleOutputPane.clientKeyWord);

        Thread outThread = new Thread(outStream); // Meanwhile we want to be able to send messages (stored in a queue) to the Server concurrently.
        outThread.start();

        InputStream socketIn = socket.getInputStream(); //  getInputStream() returns an input stream (a sequence of characters) for this socket.
        BufferedReader reader = new BufferedReader(new InputStreamReader(socketIn));

        String line;
        while ((line = reader.readLine()) != null) {
            inStream.processLine(line); // Process every line that was received in the socket.
        }
    }
}
