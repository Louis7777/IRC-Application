package java_ircd;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;

public class OutputStreamProcessor implements Runnable {

    //================================================================================
    // Fields
    //================================================================================
    private Connection con;
    private LinkedBlockingQueue<String> messageQueue = new LinkedBlockingQueue<String>(1000);

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

            /* Below we designate two characters, CR (Carriage return)and LF (Line feed), as message separators. */
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
                con.socket.close(); // Terminate the connection to the specific Client.
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }

    protected void sendQuit(String quitMessage) throws Exception {
        synchronized (Connection.MUTEX) {
            for (String channelName : new ArrayList<String>(Connection.serverChannels.keySet())) {
                Channel channel = Connection.serverChannels.get(channelName); // Passes a channel object.
                channel.getNickList().remove(con); // Removes this connection (i.e. user) from the channel nicklist.
                channel.send(":" + con.client.getHostmask() + " QUIT :" + quitMessage);
                if (channel.getNickList().isEmpty()) {
                    Connection.serverChannels.remove(channel.name);
                }
            }
        }
    }

    public void sendNotice(String string) {
        send(":" + Config.getServerName() + " NOTICE " + con.client.getNick() + " :" + string);
    }

    protected void sendGlobal(String string) {
        send(":" + Config.getServerName() + " " + string);
    }

    public void send(String s) {
        if (messageQueue != null) {
            System.out.println("***Sending line to " + con.client.getNick() + "*** " + s);
            messageQueue.add(s);
        }
    }
}
