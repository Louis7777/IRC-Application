package javawebchat;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.regex.Pattern;
import javax.swing.Timer;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class ListenersCollection {

    //================================================================================
    // Fields
    //================================================================================
    public ActionListener timerListener;
    public Timer timer = new Timer(300, initializeTimerListener());
    public KeyAdapter messageboxListener;
    public KeyAdapter nickInputListener;
    public ChangeListener tabChangeListener;
    public ActionListener sendButtonListener;
    public ActionListener configButtonListener;
    public ActionListener connectButtonListener;

    //================================================================================
    // Constructors
    //================================================================================
    public ListenersCollection() {
    }

    //================================================================================
    // Accessors (Getters)
    //================================================================================
    public ActionListener initializeTimerListener() {

        timerListener = new ActionListener() { // Timer MUST be initialized.

            @Override
            public void actionPerformed(ActionEvent e) {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        };

        return timerListener;

    }

    public KeyAdapter getMessageboxListener() {

        messageboxListener = new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent e) {
                int key = e.getKeyCode();
                /* Process and send message if ENTER was pressed. */
                if (key == KeyEvent.VK_ENTER && !JavaWebChat.messagebox.getText().equals("") && JavaWebChat.con != null) {
                    JavaWebChat.preprocessMessage();
                }
                /* CTRL-Z undo operations. */
                if (key == KeyEvent.VK_CONTROL) {
                    JavaWebChat.undoKeyOneIsPressed = true;
                }
                if (key == KeyEvent.VK_Z) {
                    JavaWebChat.undoKeyTwoIsPressed = true;
                }
                if (JavaWebChat.undoKeyOneIsPressed == true && JavaWebChat.undoKeyTwoIsPressed == true) {
                    try {
                        JavaWebChat.undoManager.undoOrRedo();
                    } catch (Exception ex) {
                    }
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                int key = e.getKeyCode();
                if (key == KeyEvent.VK_CONTROL) {
                    JavaWebChat.undoKeyOneIsPressed = false;
                }
                if (key == KeyEvent.VK_Z) {
                    JavaWebChat.undoKeyTwoIsPressed = false;
                }

            }
        };
        return messageboxListener;
    }

    public KeyAdapter getNickInputListener() {

        nickInputListener = new KeyAdapter() {

            /*
             * An IRC nickname can contain any letter, number,
             * or any of the following characters: - _ [ ] | ^ ` { } \
             * It cannot start with a number.
             */
            String regex = "(?i)^[a-z\\-_\\[\\]\\|\\^`{}\\\\]"
                    + "[a-z0-9\\-_\\[\\]\\|\\^`{}\\\\]*"; // (?i) --> case-insensitive
            String text;

            @Override
            public void keyPressed(KeyEvent e) {
                int key = e.getKeyCode();
                if (key != KeyEvent.VK_ENTER) {
                    JavaWebChat.connectButton.setEnabled(false);
                }
                text = JavaWebChat.nickInput.getText().trim();

                if (key == KeyEvent.VK_ENTER && !JavaWebChat.nickInput.getText().equals("")) {
                    JavaWebChat.connectButton.doClick();
                }
                if (text.length() > Config.NICK_MAX_CHARS) {
                    JavaWebChat.nickInput.setText(text.substring(0, Config.NICK_MAX_CHARS)); // Truncate the string.
                }
                if (!Pattern.matches(regex, JavaWebChat.nickInput.getText().trim())) { // If the patterns don't match...
                    JavaWebChat.nickInput.setText("");
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                text = JavaWebChat.nickInput.getText().trim();

                if (text.length() > Config.NICK_MAX_CHARS) {
                    JavaWebChat.nickInput.setText(text.substring(0, Config.NICK_MAX_CHARS)); // Truncate the string.
                }
                if (!Pattern.matches(regex, JavaWebChat.nickInput.getText().trim())) { // If the patterns don't match...
                    JavaWebChat.nickInput.setText("");
                }
                JavaWebChat.connectButton.setEnabled(true);
            }
        };

        return nickInputListener;
    }

    public ChangeListener getTabChangeListener() {

        tabChangeListener = new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent changeEvent) {
                JTabbedPane sourceTabbedPane = (JTabbedPane) changeEvent.getSource();
                int index = sourceTabbedPane.getSelectedIndex();
                JavaWebChat.currentView = sourceTabbedPane.getTitleAt(index);
            }
        };

        return tabChangeListener;
    }

    public ActionListener getSendButtonListener() {

        sendButtonListener = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                JavaWebChat.preprocessMessage();
            }
        };

        return sendButtonListener;
    }

    public ActionListener getConnectButtonListener() {

        connectButtonListener = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (!JavaWebChat.nickInput.getText().equals("")) {
                    try {

                        /* *** CONNECTING *** */

                        Socket s = new Socket(Config.server_name, Config.server_port);
                        Connection con = new Connection(s);
                        new Thread(con).start(); // Connection is alive.

                        con.server = Config.server_name; // !IMPORTANT! Otherwise cannot remove the connection from the table.
                        con.user.setNick(JavaWebChat.nickInput.getText()); // Let our program know what nickname we are using.
                        JavaWebChat.connections.put(Config.server_name, con.consoleOutputPane); // Connection added to the map.

                        JavaWebChat.tabbedPane.setToolTipTextAt(0, Config.server_name);
                        JavaWebChat.con = con; // Lock messagebox on this connection.

                        /* We have been connected to a server so let's clear
                         * the console from the StartUp screen and draw new content.
                         */

                        JavaWebChat.consolePanel.removeAll();
                        JavaWebChat.consolePanel.revalidate();
                        JavaWebChat.consolePanel.repaint();

                        /* Set the new layout of the console panel. */

                        JavaWebChat.consolePanel.setLayout(new GridBagLayout());

                        GridBagConstraints c = new GridBagConstraints();

                        c.gridx = 0;
                        c.gridy = 0;
                        c.weightx = 1.0;
                        c.weighty = 1.0;
                        c.fill = GridBagConstraints.BOTH;
                        JavaWebChat.consolePanel.add(con.consoleOutputPane, c);

                        /* Send our information to the server in order to register. */

                        InetSocketAddress localAddress = (InetSocketAddress) con.socket.getLocalSocketAddress(); // IP:PORTNUM

                        con.outStream.send("/NICK " + JavaWebChat.nickInput.getText());
                        con.outStream.send("/USER " + JavaWebChat.nickInput.getText() //USER chatzilla * * :New Now Know How
                                + " " + localAddress.getAddress().getHostAddress()
                                + " " + Config.server_name
                                + " :" + Config.description);
                        con.outStream.send("/MODE " + JavaWebChat.nickInput.getText() + " +i");

                    } catch (Exception ex) {

                        long start = System.currentTimeMillis();
                        final long end = start + 3500; // duration in milliseconds

                        if (!timer.isRunning()) { // If timer has finished running, create a new one.
                            /* 300 ms delay */
                            timer = new Timer(800, new ActionListener() { // Requirement: import javax.swing.Timer;

                                boolean on = false;

                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    if (System.currentTimeMillis() < end) {
                                        JavaWebChat.startUpConnectionError.setForeground(on ? Color.red : null);
                                        on = !on;
                                    } else {
                                        timer.stop();
                                        JavaWebChat.startUpConnectionError.setText("");
                                    }
                                }
                            });

                            System.err.println("Error: " + ex.getMessage());
                            JavaWebChat.startUpConnectionError.setText("<html><center>Connection failed.</center><br>\"" + ex.getMessage() + "\"</html>");
                            timer.start();
                        }
                    }
                }
            }
        };

        return connectButtonListener;
    }
    //================================================================================
    // ListenersCollection Methods
    //================================================================================
    // do any required methods
}
