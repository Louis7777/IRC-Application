package javachatclient;

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
import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class ListenersCollection {

    //================================================================================
    // Fields
    //================================================================================
    private JFrame frame;
    public ActionListener timerListener;
    public Timer timer = new Timer(300, initializeTimerListener());
    //public FocusListener messageboxFocusListener; // Target frame: GUI
    public KeyAdapter messageboxListener; // Target frame: GUI
    public KeyAdapter nickInputListener; // Target frame: GUI
    public ChangeListener tabChangeListener; // Target frame: GUI
    public ActionListener sendButtonListener; // Target frame: GUI
    public ActionListener configButtonListener; // Target frame: GUI
    public ActionListener helpButtonListener; // Target frame: GUI
    public ActionListener aboutButtonListener; // Target frame: GUI
    public ActionListener connectButtonListener; // Target frame: GUI

    //================================================================================
    // Constructors
    //================================================================================
    public ListenersCollection(JFrame frame) {
        this.frame = frame;
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

    /*public FocusListener getMessageboxFocusListener() {

        messageboxFocusListener = new FocusAdapter() {

            @Override
            public void focusLost(FocusEvent fe) {
                GUI.messagebox.requestFocus(); // Focusing the messagebox textfield.
            }
        };
        return messageboxFocusListener;
    }*/

    public KeyAdapter getMessageboxListener() {

        messageboxListener = new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent e) {
                int key = e.getKeyCode();
                /* Process and send message if ENTER was pressed. */
                if (key == KeyEvent.VK_ENTER && !GUI.messagebox.getText().equals("") && GUI.con != null) {
                    GUI.preprocessMessage();
                }
                /* CTRL-Z undo operations. */
                if (key == KeyEvent.VK_CONTROL) {
                    GUI.undoKeyOneIsPressed = true;
                }
                if (key == KeyEvent.VK_Z) {
                    GUI.undoKeyTwoIsPressed = true;
                }
                if (GUI.undoKeyOneIsPressed == true && GUI.undoKeyTwoIsPressed == true) {
                    try {
                        GUI.undoManager.undoOrRedo();
                    } catch (Exception ex) {
                    }
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                int key = e.getKeyCode();
                if (key == KeyEvent.VK_CONTROL) {
                    GUI.undoKeyOneIsPressed = false;
                }
                if (key == KeyEvent.VK_Z) {
                    GUI.undoKeyTwoIsPressed = false;
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
                    GUI.connectButton.setEnabled(false);
                }
                text = GUI.nickInput.getText().trim();

                if (key == KeyEvent.VK_ENTER && !GUI.nickInput.getText().equals("")) {
                    GUI.connectButton.doClick();
                }
                if (text.length() > Config.NICK_MAX_CHARS) {
                    GUI.nickInput.setText(text.substring(0, Config.NICK_MAX_CHARS)); // Truncate the string.
                }
                if (!Pattern.matches(regex, GUI.nickInput.getText().trim())) { // If the patterns don't match...
                    GUI.nickInput.setText("");
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                text = GUI.nickInput.getText().trim();

                if (text.length() > Config.NICK_MAX_CHARS) {
                    GUI.nickInput.setText(text.substring(0, Config.NICK_MAX_CHARS)); // Truncate the string.
                }
                if (!Pattern.matches(regex, GUI.nickInput.getText().trim())) { // If the patterns don't match...
                    GUI.nickInput.setText("");
                }
                GUI.connectButton.setEnabled(true);
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
                GUI.currentView = sourceTabbedPane.getTitleAt(index);
                frame.setTitle(GUI.WINDOW_TITLE + " - [" + sourceTabbedPane.getTitleAt(index) + "]"); // Change window title.
            }
        };

        return tabChangeListener;
    }

    public ActionListener getSendButtonListener() {

        sendButtonListener = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                GUI.preprocessMessage();
            }
        };

        return sendButtonListener;
    }

    public ActionListener getConfigButtonListener() {

        configButtonListener = new ActionListener() {

            Config configPanel = null;

            @Override
            public void actionPerformed(ActionEvent e) {
                try {

                    if (!GUI.isConfigBeingUsed) {
                        configPanel = new Config(frame.getLocationOnScreen().x, frame.getLocationOnScreen().y);
                    } else {
                        configPanel.setLocation(frame.getLocationOnScreen().x, frame.getLocationOnScreen().y);
                        configPanel.toFront(); // Brings focus to the config window.
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            }
        };

        return configButtonListener;
    }

    public ActionListener getHelpButtonListener() {

        helpButtonListener = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                //GUI.cards.show(GUI.mainPanel, "");
                GUI.CARDS.next(GUI.topPanel);
            }
        };

        return helpButtonListener;
    }

    public ActionListener getAboutButtonListener() {

        aboutButtonListener = new ActionListener() {

            AboutWindow aboutWindow = null;

            @Override
            public void actionPerformed(ActionEvent e) {
                try {

                    if (!GUI.isAboutBeingUsed) {
                        aboutWindow = new AboutWindow(frame.getLocationOnScreen().x, frame.getLocationOnScreen().y);
                    } else {
                        aboutWindow.setLocation(frame.getLocationOnScreen().x, frame.getLocationOnScreen().y);
                        aboutWindow.toFront(); // Brings focus to the about window.
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            }
        };

        return aboutButtonListener;
    }

    public ActionListener getConnectButtonListener() {

        connectButtonListener = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (!GUI.nickInput.getText().equals("")) {
                    try {

                        /* *** CONNECTING *** */

                        Socket s = new Socket(GUI.startUpServerName, GUI.startUpServerPort);
                        Connection con = new Connection(s);
                        new Thread(con).start(); // Connection is alive.

                        con.server = GUI.startUpServerName; // !IMPORTANT! Otherwise cannot remove the connection from the table.
                        con.user.setNick(GUI.nickInput.getText()); // Let our program know what nickname we are using.
                        GUI.connections.put(GUI.startUpServerName, con.consoleOutputPane); // Connection added to the map.

                        GUI.tabbedPane.setToolTipTextAt(0, GUI.startUpServerName);
                        GUI.con = con; // Lock messagebox on this connection.

                        /* We have been connected to a server so let's clear
                         * the console from the StartUp screen and draw new content.
                         */

                        GUI.consolePanel.removeAll();
                        GUI.consolePanel.revalidate();
                        GUI.consolePanel.repaint();

                        /* StartUp Connection Dialog is gone.
                         * No need to lock on a server anymore,
                         * so let's set the appropriate flag's status
                         * accordingly to prevent any unnecessary locking.
                         */

                        GUI.isPastStartUpConnectDialog = true;

                        /* Set the new layout of the console panel. */

                        GUI.consolePanel.setLayout(new GridBagLayout());

                        GridBagConstraints c = new GridBagConstraints();

                        c.gridx = 0;
                        c.gridy = 0;
                        c.weightx = 1.0;
                        c.weighty = 1.0;
                        c.fill = GridBagConstraints.BOTH;
                        GUI.consolePanel.add(con.consoleOutputPane, c);

                        /* Send our information to the server in order to register. */

                        InetSocketAddress localAddress = (InetSocketAddress) con.socket.getLocalSocketAddress(); // IP:PORTNUM

                        con.outStream.send("/NICK " + GUI.nickInput.getText());
                        con.outStream.send("/USER " + GUI.nickInput.getText() //USER chatzilla * * :New Now Know How
                                + " " + localAddress.getAddress().getHostAddress()
                                + " " + GUI.startUpServerName
                                + " :" + GUI.startUpDesc);
                        con.outStream.send("/MODE " + GUI.nickInput.getText() + " +i");

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
                                        GUI.startUpConnectionError.setForeground(on ? Color.red : null);
                                        on = !on;
                                    } else {
                                        timer.stop();
                                        GUI.startUpConnectionError.setText("");
                                    }
                                }
                            });

                            System.err.println("Error: " + ex.getMessage());
                            GUI.startUpConnectionError.setText("<html><center>Connection failed.</center><br>\"" + ex.getMessage() + "\"</html>");
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
