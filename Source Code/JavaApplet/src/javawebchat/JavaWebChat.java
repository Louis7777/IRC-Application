package javawebchat;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.undo.UndoManager;

public class JavaWebChat extends JApplet {

    //================================================================================
    // Fields
    //================================================================================
    private static final ImageIcon CLOSE_TAB_ICON = new ImageIcon(JavaWebChat.class.getResource("resources/close_tab_gray.png"));
    private static final ImageIcon CLOSE_TAB_ICON2 = new ImageIcon(JavaWebChat.class.getResource("resources/close_tab_red.png"));
    public static final ImageIcon CONSOLE_ICON = new ImageIcon(JavaWebChat.class.getResource("resources/console.png"));
    private static final ImageIcon SETTINGS_ICON = new ImageIcon(JavaWebChat.class.getResource("resources/settings.png"));
    public static final String WINDOW_TITLE = "Java Chat Client";
    public static boolean undoKeyOneIsPressed = false;
    public static boolean undoKeyTwoIsPressed = false;
    public static JPanel consolePanel = new JPanel(new GridBagLayout());
    public static UndoManager undoManager = new UndoManager();
    public static Connection con; // We need to manipulate the connection we are using in order to send messages to the correct server.
    public static String currentView = "Console"; // Where are we currently typing?
    public static JTabbedPane tabbedPane = new JTabbedPane();
    public static Map<String, ConsoleOutputPane> connections = new HashMap<String, ConsoleOutputPane>(); // Key is server name.
    public static JTextField messagebox;
    /////////////////////////////////// STARTUP CONNECTION REQUIRED FIELDS ///////////////////////////////////
    public static JTextField nickInput = new JTextField(); // Used for the StartUp connection.
    public static JButton connectButton; // Used for the StartUp connection.
    public static JLabel startUpConnectionError = new JLabel(""); // Used for the StartUp connection.
    /////////////////////////////////////////////////////////////////////////////////////////////////////////

    // http://docs.oracle.com/javase/tutorial/deployment/webstart/customProgressIndicatorForAppln.html
    //JApplet methods that our class inherits: init(), start(), paint(Graphics g), stop(), destroy()
    @Override
    public void init() {

        new Config(this);

        setSize(480, 640); // Width and height of the Frame.
        setMinimumSize(new Dimension(330, 200)); // Minimum allowed width and height of the applet.
        setMaximumSize(getPreferredSize());

        setLayout(new GridBagLayout());

        //----------------------------------------------------------------------------
        // Configuration of the look and feel of scrollbars.
        //----------------------------------------------------------------------------

        UIManager.put("ScrollBar.background", new Color(192, 192, 192));
        //UIManager.put("ScrollBar.darkShadow", new Color(232, 240, 240)); // Like a second border color around track.
        UIManager.put("ScrollBar.highlight", new Color(0, 49, 60)); // Border color around up/down buttons.
        UIManager.put("ScrollBar.shadow", new Color(232, 240, 247)); // Like a border color around track.
        UIManager.put("ScrollBar.thumbHighlight", new Color(232, 240, 247)); // Like a border color around scrollbar whenever it is visible.
        UIManager.put("ScrollBar.thumbShadow", new Color(0, 49, 60)); // Like a second border color around scrollbar whenever it is visible.

        //----------------------------------------------------------------------------
        // Listeners
        //----------------------------------------------------------------------------

        ListenersCollection listenersCollection = new ListenersCollection();

        //----------------------------------------------------------------------------
        // Configuration of the Tabbed Pane.
        //----------------------------------------------------------------------------

        tabbedPane.addChangeListener(listenersCollection.getTabChangeListener()); // Change the window title when the selected tab changes.

        /* Tabbed Pane look and feel configuration. */

        //UIManager.put("TabbedPane.borderHightlightColor", Color.CYAN); // Border color of a selected tab.
        UIManager.put("TabbedPane.contentAreaColor", Color.DARK_GRAY); // Background color of panels/content.
        //UIManager.put("TabbedPane.darkShadow", Color.YELLOW); // Border shadow color.
        //UIManager.put("TabbedPane.focus", Color.RED); // Color of border when double-clicking on a tab.
        //UIManager.put("TabbedPane.foreground", Color.RED); // Color of text in tabs.
        //UIManager.put("TabbedPane.highlight", Color.RED);
        UIManager.put("TabbedPane.light", Color.GRAY); // Border color of an unselected tab.
        UIManager.put("TabbedPane.selected", new Color(238, 238, 238)); // Background color of a selected tab.
        UIManager.put("TabbedPane.selectedForeground", Color.BLACK); // Text color of a selected tab.
        UIManager.put("TabbedPane.selectHighlight", Color.GREEN); // Border color of a selected tab.

        tabbedPane.setBackground(Color.LIGHT_GRAY); // Background color of all unselected tabs.
        tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        tabbedPane.addTab("Console", CONSOLE_ICON, consolePanel, "Not connected."); // Add the initial console panel.

        //----------------------------------------------------------------------------
        // Buttons
        //----------------------------------------------------------------------------

        connectButton = new JButton("<html><font color='#ff8f00'>Connect!</font></html>");
        connectButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        connectButton.setPreferredSize(new Dimension(100, 25));
        connectButton.setMinimumSize(connectButton.getPreferredSize());
        connectButton.setFocusPainted(false);
        connectButton.setBackground(Color.BLACK);
        //connectButton.setForeground(Color.ORANGE);
        connectButton.addActionListener(listenersCollection.getConnectButtonListener());

        JButton sendButton = new JButton("Send"); // Create a button to send messages.
        sendButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        sendButton.setPreferredSize(new Dimension(64, 26));
        sendButton.addActionListener(listenersCollection.getSendButtonListener());

        JButton configButton = new JButton(SETTINGS_ICON); // Create a button that calls the configuration window.
        configButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        configButton.setPreferredSize(new Dimension(30, 30));
        //configButton.addActionListener(listenersCollection.getConfigButtonListener());

        //----------------------------------------------------------------------------
        // Textfields
        //----------------------------------------------------------------------------

        Border border_line = BorderFactory.createLineBorder(Color.DARK_GRAY, 1);
        Border empty_border = new EmptyBorder(0, 5, 0, 0);
        CompoundBorder border = new CompoundBorder(border_line, empty_border);

        messagebox = new JTextField(); // Create the message area.
        messagebox.setPreferredSize(new Dimension(110, 25));
        messagebox.setMinimumSize(messagebox.getPreferredSize());
        messagebox.setBorder(border);
        messagebox.setSelectionColor(Color.LIGHT_GRAY);
        messagebox.getDocument().addUndoableEditListener(undoManager);
        messagebox.addMouseListener(new RightClickMenu());
        messagebox.addKeyListener(listenersCollection.getMessageboxListener());

        nickInput.setPreferredSize(new Dimension(150, 25));
        nickInput.setMinimumSize(nickInput.getPreferredSize());
        nickInput.setMargin(new Insets(2, 2, 2, 2));
        nickInput.setText(Config.nickname);
        nickInput.setBackground(new Color(240, 248, 255));
        nickInput.setCaretColor(Color.ORANGE);
        nickInput.setSelectionColor(Color.LIGHT_GRAY);
        nickInput.addKeyListener(listenersCollection.getNickInputListener());

        //----------------------------------------------------------------------------
        // Labels
        //----------------------------------------------------------------------------

        JLabel enterNickLabel = new JLabel("Enter a Nickname");
        enterNickLabel.setFont(new Font(null, Font.BOLD, 14));
        JLabel nicknameLabel = new JLabel("Nickname:");
        nicknameLabel.setFont(new Font(null, Font.PLAIN, 14));
        startUpConnectionError.setForeground(Color.red);

        //----------------------------------------------------------------------------
        // Panels (The GUI consists of two main panels. TopPanel and bottomPanel.)
        //----------------------------------------------------------------------------

        JPanel topPanel = new JPanel(new GridBagLayout()); // Top main panel.
        topPanel.setBackground(new Color(0, 49, 77));
        topPanel.setPreferredSize(new Dimension(100, 100));

        JPanel bottomPanel = new JPanel(new GridBagLayout()); // Bottom main panel.
        bottomPanel.setBackground(new Color(192, 192, 192));
        bottomPanel.setPreferredSize(new Dimension(100, 40));
        bottomPanel.setMinimumSize(bottomPanel.getPreferredSize());

        JPanel buttonPanel = new JPanel(); // Part of the main bottom panel.
        buttonPanel.setBackground(new Color(192, 192, 192));
        buttonPanel.add(sendButton);
        buttonPanel.add(configButton);

        JPanel startUpConnect = new JPanel(new GridBagLayout()); // Part of the consolePanel.
        startUpConnect.setBackground(new Color(162, 181, 205));
        startUpConnect.setPreferredSize(new Dimension(320, 200));
        startUpConnect.setMinimumSize(startUpConnect.getPreferredSize());
        startUpConnect.setMaximumSize(startUpConnect.getPreferredSize());

        JPanel nickNameArea = new JPanel(); // Part of the above startUpConnect.
        nickNameArea.setBackground(new Color(162, 181, 205));
        nickNameArea.add(nicknameLabel);
        nickNameArea.add(nickInput);

        //////////////////////////////////////////////////////////////////////////////

        GridBagConstraints c = new GridBagConstraints();

        /* StartUp Connection */

        c.insets = new Insets(6, 20, 15, 20); // Top, right, bottom and left padding.
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1.0;
        c.weighty = 1.0;
        c.anchor = GridBagConstraints.WEST;
        startUpConnect.add(enterNickLabel, c);

        c.gridy = 2;
        c.gridwidth = 2;
        c.anchor = GridBagConstraints.EAST;
        startUpConnect.add(nickNameArea, c);

        c.gridx = 1;
        c.gridy = 3;
        c.gridwidth = 1;
        startUpConnect.add(connectButton, c);

        /* ConsolePanel */

        c.insets = new Insets(0, 0, 0, 0); // Top, right, bottom and left padding.
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1.0;
        c.weighty = 0.4;
        c.anchor = GridBagConstraints.CENTER;
        consolePanel.add(startUpConnectionError, c);

        c.gridy = 1;
        c.weighty = 0.6;
        c.anchor = GridBagConstraints.NORTH;
        consolePanel.add(startUpConnect, c);

        /* Main */

        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1.0;
        c.weighty = 1.0;
        c.fill = GridBagConstraints.BOTH;
        topPanel.add(tabbedPane, c);

        /* BottomPanel */

        c.weighty = 0.2;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.NORTHWEST;
        bottomPanel.add(messagebox, c);

        c.gridy = 1;
        c.weighty = 0.8;
        c.fill = GridBagConstraints.NONE;
        bottomPanel.add(buttonPanel, c);

        /* GUI */

        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1.0;
        c.weighty = 0.9;
        c.fill = GridBagConstraints.BOTH;
        add(topPanel, c);

        c.gridy = 1;
        c.weighty = 0.1;
        add(bottomPanel, c);

        setVisible(true);
    }

    @Override
    public void start() {
        super.start();
        this.repaint();
    }

    /*
    public void stop() {
    m = m + "stop";
    }
     */
    /*@Override
    public void paint(Graphics g) {
        //g = this.getGraphics();
        update(g);
    }*/

    //================================================================================
    // JavaWebChat Methods
    //================================================================================
    public static void addTab(final MessagePanel msgPanel, final Connection con) {
        synchronized (Connection.MUTEX) {

            Integer index = tabbedPane.indexOfTab(msgPanel.getTitle());

            /* Proceed ONLY if tab name doesn't already exist (i.e. index = -1) */

            if (index < 0) {

                /* Add the tab to the pane without any label. */
                tabbedPane.addTab(msgPanel.getTitle(), msgPanel);
                int position = tabbedPane.indexOfComponent(msgPanel);

                /* Create a FlowLayout that will space things 5px apart. */
                FlowLayout f = new FlowLayout(FlowLayout.CENTER, 5, 0);

                /* Make a small JPanel with the layout and make it non-opaque. */
                JPanel tabHeader = new JPanel(f);
                tabHeader.setOpaque(false);
                tabHeader.setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0));

                /* Add a JLabel with title and the left-side tab icon. */
                JLabel lblTitle = new JLabel(msgPanel.getTitle());
                lblTitle.setPreferredSize(new Dimension(80, 15));
                lblTitle.setForeground(Color.BLACK);
                lblTitle.setHorizontalAlignment(JLabel.CENTER); // This will center the label inside the tab.

                /* Create a JButton for the close tab button. */
                JButton btnClose = new JButton();
                btnClose.setOpaque(false);
                btnClose.setRolloverIcon(CLOSE_TAB_ICON2);
                btnClose.setRolloverEnabled(true);
                btnClose.setIcon(CLOSE_TAB_ICON);
                btnClose.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                btnClose.setToolTipText("Close tab");
                btnClose.setBorder(null);
                btnClose.setFocusable(false);

                /* Put the panel together. */
                tabHeader.add(lblTitle);
                tabHeader.add(btnClose);

                /* Now assign the component for the tab. */
                tabbedPane.setTabComponentAt(position, tabHeader);

                if (msgPanel.getTitle().startsWith("#")) {
                    tabbedPane.setToolTipTextAt(position, msgPanel.getTitle() + " on " + con.socket.getRemoteSocketAddress().toString());
                } else {
                    tabbedPane.setToolTipTextAt(position, "Query with " + msgPanel.getTitle() + " on " + con.socket.getRemoteSocketAddress().toString());
                }

                /* Add the listener that removes the tab. */
                btnClose.addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent evt) {
                        if (msgPanel.getTitle().startsWith("#")) { // If we are closing a channel (and not a private chat).
                            con.outStream.send("/part " + msgPanel.getTitle());
                        }
                        tabbedPane.remove(msgPanel);
                    }
                });

                /* Optionally bring the new tab to the front at creation time. */
                tabbedPane.setSelectedComponent(msgPanel);
            }
        }
    }

    public static void removeTab(MessagePanel msgPanel) {
        synchronized (Connection.MUTEX) {

            Integer index = tabbedPane.indexOfTab(msgPanel.getTitle());

            if (index > 0) {
                tabbedPane.remove((int) index);
            }
        }
    }

    public static void preprocessMessage() {
        try {
            if (!messagebox.getText().equals("") && con != null) { // If locked on a connection and a message has been typed.

                if (!messagebox.getText().startsWith("/")) { // If the user has not typed in a command, but a simple message.

                    if (currentView.equals("Console")) { // If the user is currently viewing the Console.

                        if (!messagebox.getText().trim().equals("")) { // If the user has not typed only spaces in the Console.
                            con.consoleOutputPane.drawMessage("=>> You are not in a channel/query (" + messagebox.getText() + ")\n", con.consoleOutputPane.clientErrorKeyWord);
                        }

                    } else { // If the user is currently viewing a channel/query.

                        /* PRIVMSG <msgtarget> <message> */
                        con.outStream.send("PRIVMSG " + currentView + " " + messagebox.getText());
                    }
                } else { // The user has typed in a command.

                    con.outStream.send(messagebox.getText()); // Send the command.

                }

                messagebox.setText(""); // Empty the message box anyhow.
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
