package javachatclient;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileFilter;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.regex.Pattern;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class Config extends JFrame {

    //================================================================================
    // Fields
    //================================================================================
    public static final String DEFAULT_SERVER_NAME = "localhost";
    public static final int DEFAULT_SERVER_PORT = 6667;
    public static final String DEFAULT_NICKNAME = "Guest";
    public static final String DEFAULT_IDENT = "JavaChat";
    public static final String DEFAULT_DESCRIPTION = "I am awesome!";
    public static final String DEFAULT_QUIT_MESSAGE = "Quitting...";
    public static final String SERVER_PREFS_REL_PATH = "./servers/"; // Directory to save server preferences.
    public static final int SV_NAME_MAX_CHARS = 63;
    public static final int NICK_MAX_CHARS = 15;
    public static final int IDENT_MAX_CHARS = 15;
    public static final int DESC_MAX_CHARS = 50;
    public static final int QUIT_MSG_MAX_CHARS = 160;
    private static String lastServer = "";
    private final ImageIcon TOOLTIP_ICON = new ImageIcon(GUI.class.getResource("resources/help_tooltip_icon.png"));
    private final ImageIcon TRASH_ICON = new ImageIcon(GUI.class.getResource("resources/trash.png"));
    private final ImageIcon TRASH_ICON2 = new ImageIcon(GUI.class.getResource("resources/trash2.png"));
    private final File DIR = new File(Config.SERVER_PREFS_REL_PATH); // Instantiate with given path to the servers directory.
    private JPanel settingsPanel = new JPanel();
    private JComboBox serverList;
    private Component menu = null;
    private JPanel serversDropdownMenuPanel = new JPanel();
    private JFormattedTextField sv_name_input; // Filename allowed characters only. Up to 63 characters.
    private JFormattedTextField sv_port_input; // An integer for a port number. Maximum digits: 5
    private JFormattedTextField nick_input; // 1-15 characters (may vary depending on the server)
    private JFormattedTextField ident_input; // 1-15 characters (may vary depending on the server)
    private JFormattedTextField desc_input; // Up to 50 characters (may vary depending on the server).
    private JFormattedTextField quit_msg_input; // Up to 160 characters (may vary depending on the server).

    //================================================================================
    // Constructors
    //================================================================================
    public Config(int x, int y) {

        GUI.isConfigBeingUsed = true;

        //----------------------------------------------------------------------------
        // Create a window close listener.
        //----------------------------------------------------------------------------

        addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                //System.out.println("Closing configuration...");
                GUI.isConfigBeingUsed = false;
                if (serverList.getItemCount() != 0) {
                    Config.lastServer = serverList.getSelectedItem().toString(); // Remember the last selected server.

                    /* Re-Configure the Start Up Connection */
                    if (!lastServer.equals("") && GUI.isPastStartUpConnectDialog == false) {
                        GUI.startUpServerName = lastServer; // Change Start Up Connection server name.
                        GUI.setStartUpConnectionPrefs(); // Change Start Up Connection preferences for the new server name.
                        GUI.lockedOnServer.setText("<html>Locked on: <font color='#0000cc'>" + lastServer + "</font></html>");
                    }
                }
            }
        });

        //----------------------------------------------------------------------------
        // Window layout.
        //----------------------------------------------------------------------------

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setTitle("Configuration");
        ImageIcon img = new ImageIcon(GUI.WINDOW_ICON.getImage());
        setIconImage(img.getImage());
        setSize(480, 440);
        setLocation(x, y);
        setResizable(false);

        makeLayout();

        setVisible(true);
    }

    //================================================================================
    // Config Methods
    //================================================================================
    private void makeLayout() {

        setLayout(new GridBagLayout()); // Layout must be set before any JPanels are made.

        setMinimumSize(new Dimension(200, 200)); // Minimum allowed width and height of the JFrame.

        setBackground(new Color(132, 112, 122));

        //----------------------------------------------------------------------------
        // Labels
        //----------------------------------------------------------------------------

        JLabel sv_name_label = new JLabel("Server Name: ");
        JLabel sv_port_label = new JLabel("Server Port: ");
        JLabel nick_label = new JLabel("Nickname: ");
        JLabel ident_label = new JLabel("Ident Name: ");
        JLabel desc_label = new JLabel("Full Name: ");
        JLabel quit_msg_label = new JLabel("Quit Message: ");

        JLabel serversDropdownMenuLabel = new JLabel("Servers: ");

        sv_name_label.setForeground(Color.RED);
        nick_label.setForeground(Color.RED);

        //----------------------------------------------------------------------------
        // Textfields
        //----------------------------------------------------------------------------

        NumberFormat f = NumberFormat.getNumberInstance();
        f.setParseIntegerOnly(true);
        f.setGroupingUsed(false);
        f.setMaximumIntegerDigits(5);

        sv_name_input = new JFormattedTextField();
        sv_port_input = new JFormattedTextField(f);
        nick_input = new JFormattedTextField();
        ident_input = new JFormattedTextField();
        desc_input = new JFormattedTextField();
        quit_msg_input = new JFormattedTextField();

        sv_name_input.setPreferredSize(new Dimension(200, 25));
        sv_name_input.setForeground(Color.BLACK);
        sv_port_input.setPreferredSize(new Dimension(50, 25));
        nick_input.setPreferredSize(new Dimension(150, 25));
        nick_input.setForeground(Color.BLACK);
        ident_input.setPreferredSize(new Dimension(150, 25));
        desc_input.setPreferredSize(new Dimension(200, 25));
        quit_msg_input.setPreferredSize(new Dimension(200, 25));

        //----------------------------------------------------------------------------
        // Tooltips
        //----------------------------------------------------------------------------

        JButton sv_name_tooltip = new JButton(TOOLTIP_ICON);
        JButton sv_port_tooltip = new JButton(TOOLTIP_ICON);
        JButton nick_tooltip = new JButton(TOOLTIP_ICON);
        JButton ident_tooltip = new JButton(TOOLTIP_ICON);
        JButton desc_tooltip = new JButton(TOOLTIP_ICON);
        JButton quit_msg_tooltip = new JButton(TOOLTIP_ICON);

        sv_name_tooltip.setPreferredSize(new Dimension(14, 14));
        sv_port_tooltip.setPreferredSize(new Dimension(14, 14));
        nick_tooltip.setPreferredSize(new Dimension(14, 14));
        ident_tooltip.setPreferredSize(new Dimension(14, 14));
        desc_tooltip.setPreferredSize(new Dimension(14, 14));
        quit_msg_tooltip.setPreferredSize(new Dimension(14, 14));

        sv_name_tooltip.setBorder(null);
        sv_port_tooltip.setBorder(null);
        nick_tooltip.setBorder(null);
        ident_tooltip.setBorder(null);
        desc_tooltip.setBorder(null);
        quit_msg_tooltip.setBorder(null);

        sv_name_tooltip.setOpaque(false);
        sv_port_tooltip.setOpaque(false);
        nick_tooltip.setOpaque(false);
        ident_tooltip.setOpaque(false);
        desc_tooltip.setOpaque(false);
        quit_msg_tooltip.setOpaque(false);

        sv_name_tooltip.setBackground(Color.BLACK);
        sv_port_tooltip.setBackground(Color.BLACK);
        nick_tooltip.setBackground(Color.BLACK);
        ident_tooltip.setBackground(Color.BLACK);
        desc_tooltip.setBackground(Color.BLACK);
        quit_msg_tooltip.setBackground(Color.BLACK);

        sv_name_tooltip.setFocusable(false); // Disable tab index.
        sv_port_tooltip.setFocusable(false);
        nick_tooltip.setFocusable(false);
        ident_tooltip.setFocusable(false);
        desc_tooltip.setFocusable(false);
        quit_msg_tooltip.setFocusable(false);

        sv_name_tooltip.setToolTipText("<html>"
                + "<strong>The server address to connect to.</strong><br>e.g. <font color='#0000cc'>irc.icq.com</font>"
                + "</html>");
        sv_port_tooltip.setToolTipText("<html>"
                + "<strong>The port on which the above server is running on.</strong><br>Default IRC port is <font color='#0000cc'>6667</font>"
                + "</html>");
        nick_tooltip.setToolTipText("<html>"
                + "<strong>The name that you wish to be known by in the chatrooms.</strong><br>e.g. <font color='#0000cc'>John</font>"
                + "</html>");
        ident_tooltip.setToolTipText("<html>"
                + "<strong>Sometimes used by servers to verify your username (nickname).</strong><br>e.g. <font color='#0000cc'>ISTstudent</font>"
                + "</html>");
        desc_tooltip.setToolTipText("<html>"
                + "<strong>Your real name or a short description.</strong><br>e.g. <font color='#0000cc'>I'm awesome!</font>"
                + "</html>");
        quit_msg_tooltip.setToolTipText("<html>"
                + "<strong>The reason for quitting that will appear to other users when you disconnect from the server.</strong>"
                + "<br>e.g. <font color='#0000cc'>Quitting...</font>"
                + "</html>");

        //----------------------------------------------------------------------------
        // Buttons
        //----------------------------------------------------------------------------

        JButton trashButton = new JButton(TRASH_ICON); // Create a button to delete a selected server.
        JButton connectButton = new JButton("Connect Now"); // Create a button to connect to a configured server.
        JButton saveButton = new JButton("Save"); // Create a button to save the settings for a server.
        JButton cancelButton = new JButton("Cancel"); // Create a cancel button to exit configuration.

        trashButton.setRolloverEnabled(true);
        trashButton.setRolloverIcon(TRASH_ICON2);
        trashButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        trashButton.setToolTipText("Remove selected server");
        trashButton.setPreferredSize(new Dimension(30, 30));

        connectButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        connectButton.setPreferredSize(new Dimension(120, 30));

        saveButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        saveButton.setPreferredSize(new Dimension(80, 30));

        cancelButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        cancelButton.setPreferredSize(new Dimension(80, 30));

        //----------------------------------------------------------------------------
        // Action Listeners
        //----------------------------------------------------------------------------

        /* (1) Textfield listeners to verify the input. */

        sv_name_input.addPropertyChangeListener("value", new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                String text = sv_name_input.getText().trim();
                /*
                 * A server name cannot contain any illegal filename characters
                 * such as: \ / : * ? " < > |
                 * It may only contain alphanumeric, underscore, hyphen and dot.
                 */
                String regex = "[_a-zA-Z0-9\\-\\.]+";

                /*
                 * Theoretically, a domain name can be up to 256 characters long.
                 * Officially, many registries including the .com, .net and .org
                 * registries limit domain names to less than 63 characters.
                 *
                 */
                if (text.length() > SV_NAME_MAX_CHARS) {
                    sv_name_input.setText(text.substring(0, SV_NAME_MAX_CHARS)); // Truncate the string.
                }
                if (!Pattern.matches(regex, sv_name_input.getText().trim())) { // If the patterns don't match
                    sv_name_input.setText("");
                }
            }
        });

        nick_input.addPropertyChangeListener("value", new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                String text = nick_input.getText().trim();
                /*
                 * An IRC nickname can contain any letter, number,
                 * or any of the following characters: - _ [ ] | ^ ` { } \
                 * It cannot start with a number.
                 */
                String regex = "(?i)^[a-z\\-_\\[\\]\\|\\^`{}\\\\]"
                        + "[a-z0-9\\-_\\[\\]\\|\\^`{}\\\\]*"; // (?i) --> case-insensitive

                if (text.length() > NICK_MAX_CHARS) {
                    nick_input.setText(text.substring(0, NICK_MAX_CHARS)); // Truncate the string.
                }
                if (!Pattern.matches(regex, nick_input.getText().trim())) { // If the patterns don't match...
                    nick_input.setText("");
                }
            }
        });

        ident_input.addPropertyChangeListener("value", new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                String text = ident_input.getText().trim();
                /*
                 * An IRC ident name can contain any letter, number,
                 * or any of the following characters: - _ [ ] | ^ ` { } \
                 * It cannot start with a number.
                 */
                String regex = "(?i)^[a-z\\-_\\[\\]\\|\\^`{}\\\\]"
                        + "[a-z0-9\\-_\\[\\]\\|\\^`{}\\\\]*"; // (?i) --> case-insensitive

                if (text.length() > IDENT_MAX_CHARS) {
                    ident_input.setText(text.substring(0, IDENT_MAX_CHARS)); // Truncate the string.
                }
                if (!Pattern.matches(regex, ident_input.getText().trim())) { // If the patterns don't match...
                    ident_input.setText("");
                }
            }
        });

        desc_input.addPropertyChangeListener("value", new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                String text = desc_input.getText().trim();

                if (text.length() > DESC_MAX_CHARS) {
                    desc_input.setText(text.substring(0, DESC_MAX_CHARS)); // Truncate the string.
                }
            }
        });

        quit_msg_input.addPropertyChangeListener("value", new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                String text = quit_msg_input.getText().trim();

                if (text.length() > QUIT_MSG_MAX_CHARS) {
                    quit_msg_input.setText(text.substring(0, QUIT_MSG_MAX_CHARS)); // Truncate the string.
                }
            }
        });

        /* (2) Button listeners. */

        trashButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (serverList.getItemCount() != 0 && serverList.getSelectedItem() != null) {
                    FileManager file = new FileManager();
                    System.out.println("File to be deleted: " + serverList.getSelectedItem().toString());
                    boolean wasDeleted = file.deleteServer(serverList.getSelectedItem().toString()); // delete selected server


                    if (wasDeleted == true) {

                        /* We will clear the settings panel. */

                        JTextField t = new JTextField();
                        for (Component c : settingsPanel.getComponents()) {
                            if (c instanceof JTextField) {
                                t = (JTextField) c;
                                t.setText(""); //clear all the fields
                            }
                        }

                        /* We will re-draw the dropdown menu with the new list. */

                        serversDropdownMenuPanel.remove(1); // "1" because the menu was added after the label.
                        serversDropdownMenuPanel.revalidate();
                        serversDropdownMenuPanel.repaint();
                        menu = getList(DIR.listFiles(new PropertiesFileFilter())); // Re-get servers.
                        serverList.addItemListener(new ItemListener() {

                            @Override
                            public void itemStateChanged(ItemEvent e) {

                                //if (e.getStateChange() == ItemEvent.SELECTED)
                                if (e.getStateChange() == 1) { // If deselected (i.e. something else gets selected)
                                    fillSettingsPanel();
                                }
                            }
                        });
                        serversDropdownMenuPanel.add(menu, 1); // Re-add servers list to the menu.

                        /* We will re-fill the settings panel with the preferences for the new selected server. */

                        fillSettingsPanel();
                    }
                }
            }
        });

        cancelButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                //System.out.println("Closing configuration...");
                Config.super.dispose();
                GUI.isConfigBeingUsed = false;
                if (serverList.getItemCount() != 0) {
                    Config.lastServer = serverList.getSelectedItem().toString();
                }

                /* Re-Configure the Start Up Connection */
                if (!lastServer.equals("") && GUI.isPastStartUpConnectDialog == false) {
                    GUI.startUpServerName = lastServer; // Change Start Up Connection server name.
                    GUI.setStartUpConnectionPrefs(); // Change Start Up Connection preferences for the new server name.
                    GUI.lockedOnServer.setText("<html>Locked on: <font color='#0000cc'>" + lastServer + "</font></html>");
                }
            }
        });

        saveButton.addActionListener(
                new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {

                        FileManager fileManager = new FileManager();
                        ArrayList<String> properties = new ArrayList<String>();

                        /* Get all configured values */

                        String serverName = sv_name_input.getText().toLowerCase().trim();
                        String serverPort = sv_port_input.getText().trim();
                        String nickName = nick_input.getText().trim();
                        String ident = ident_input.getText().trim();
                        String desc = desc_input.getText().trim();
                        String quitMsg = quit_msg_input.getText().trim();

                        /* Put all values in a hashmap */

                        properties.add(0, "server_name=" + serverName);
                        properties.add(1, "server_port=" + serverPort);
                        properties.add(2, "nickname=" + nickName);
                        properties.add(3, "ident_name=" + ident);
                        properties.add(4, "description=" + desc);
                        properties.add(5, "quit_message=" + quitMsg);

                        /* We will try to load a properties file for the specific server. */

                        if (!serverName.equals("") && !nickName.equals("")) { // If "Server Name" and "Nickname" have been filled.
                            fileManager.putProperties(serverName, properties);
                            Config.lastServer = serverName;

                            Config.super.dispose();
                            //System.out.println("closing configuration...");
                            GUI.isConfigBeingUsed = false;

                            /* Re-Configure the Start Up Connection */
                            if (!lastServer.equals("") && GUI.isPastStartUpConnectDialog == false) {
                                GUI.startUpServerName = lastServer; // Change Start Up Connection server name.
                                GUI.setStartUpConnectionPrefs(); // Change Start Up Connection preferences for the new server name.
                                GUI.lockedOnServer.setText("<html>Locked on: <font color='#0000cc'>" + lastServer + "</font></html>");
                            }
                        }

                    }
                });

        //----------------------------------------------------------------------------
        // Panels
        //----------------------------------------------------------------------------

        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new FlowLayout());
        buttonsPanel.add(saveButton);
        buttonsPanel.add(cancelButton);

        serversDropdownMenuPanel = new JPanel();
        menu = getList(DIR.listFiles(new PropertiesFileFilter())); // The menu.
        serverList.addItemListener(new ItemListener() { // Create a list to display all configured servers.

            @Override
            public void itemStateChanged(ItemEvent e) {

                //if (e.getStateChange() == ItemEvent.SELECTED)
                if (e.getStateChange() == 1) { // If deselected (i.e. something else gets selected)
                    fillSettingsPanel();
                }
            }
        });
        serversDropdownMenuPanel.add(serversDropdownMenuLabel);
        serversDropdownMenuPanel.add(menu);
        serversDropdownMenuPanel.add(trashButton);


        /* At this point check the last viewed server and set it as selected item */

        if (serverList.getItemCount() != 0 && !Config.lastServer.equals("")) {

            boolean find = false;
            int i;

            for (i = 0; i < serverList.getItemCount(); i++) {

                if (Config.lastServer.equals(serverList.getItemAt(i).toString())) {
                    find = true;
                    break;
                }
            }

            if (find == true) {
                serverList.getModel().setSelectedItem(Config.lastServer);
            }

        }

        settingsPanel.setLayout(new GridBagLayout());
        settingsPanel.setMinimumSize(new Dimension(400, 200)); // Minimum allowed width and height of the JPanel.
        settingsPanel.setBorder(BorderFactory.createTitledBorder("Settings"));

        fillSettingsPanel();

        //////////////////////////////////////////////////////////////////////////////

        GridBagConstraints c = new GridBagConstraints();

        c.insets = new Insets(5, 5, 5, 5); // Top, right, bottom and left padding.
        c.gridx = 0;
        c.gridy = 0;
        c.anchor = GridBagConstraints.WEST;
        settingsPanel.add(sv_name_label, c);
        c.gridy = 1;
        settingsPanel.add(sv_port_label, c);
        c.gridy = 2;
        settingsPanel.add(nick_label, c);
        c.gridy = 3;
        settingsPanel.add(ident_label, c);
        c.gridy = 4;
        settingsPanel.add(desc_label, c);
        c.gridy = 5;
        settingsPanel.add(quit_msg_label, c);

        c.gridx = 1;
        c.gridy = 0;
        settingsPanel.add(sv_name_input, c);
        c.gridy = 1;
        settingsPanel.add(sv_port_input, c);
        c.gridy = 2;
        settingsPanel.add(nick_input, c);
        c.gridy = 3;
        settingsPanel.add(ident_input, c);
        c.gridy = 4;
        settingsPanel.add(desc_input, c);
        c.gridy = 5;
        settingsPanel.add(quit_msg_input, c);

        c.gridx = 2;
        c.gridy = 0;
        settingsPanel.add(sv_name_tooltip, c);
        c.gridy = 1;
        settingsPanel.add(sv_port_tooltip, c);
        c.gridy = 2;
        settingsPanel.add(nick_tooltip, c);
        c.gridy = 3;
        settingsPanel.add(ident_tooltip, c);
        c.gridy = 4;
        settingsPanel.add(desc_tooltip, c);
        c.gridy = 5;
        settingsPanel.add(quit_msg_tooltip, c);

        c.insets = new Insets(10, 10, 10, 10); // Top, right, bottom and left padding.
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1.0;
        c.weighty = 0.1;
        c.gridwidth = 2;
        c.anchor = GridBagConstraints.NORTHEAST;
        add(serversDropdownMenuPanel, c);

        c.gridy = 1;
        c.weighty = 0.8;
        c.gridwidth = 2;
        c.anchor = GridBagConstraints.WEST;
        add(settingsPanel, c);

        c.gridy = 2;
        c.weighty = 0.1;
        c.gridwidth = 1;
        c.anchor = GridBagConstraints.WEST;
        add(connectButton, c);

        c.gridx = 1;
        c.anchor = GridBagConstraints.EAST;
        add(buttonsPanel, c);
    }

    /* A method to create a directory to store servers' preferences. */
    public static void makeDir(String directory) {
        File dir = new File(directory);
        if (!dir.exists()) {
            dir.mkdir();
        }

    }

    /* A method to fill the setting panel's fields with a server's preferences. */
    public void fillSettingsPanel() {

        if (serverList.getItemCount() != 0 && serverList.getSelectedItem() != null) {

            FileManager fileManager = new FileManager();
            ArrayList<String> tempList = fileManager.getProperties(serverList.getSelectedItem().toString());


            if (!tempList.isEmpty()) {

                //if (!tempList.get(0).equals("")) {
                sv_name_input.setText(serverList.getSelectedItem().toString());
                //}
                //else {
                //    settingsPanel.sv_name_input.setText("");
                //}

                if (!tempList.get(1).equals("")) {
                    sv_port_input.setText(tempList.get(1));
                } else {
                    sv_port_input.setText("");
                }

                if (!tempList.get(2).equals("")) {
                    nick_input.setText(tempList.get(2));
                } else {
                    nick_input.setText("");
                }

                if (!tempList.get(3).equals("")) {
                    ident_input.setText(tempList.get(3));
                } else {
                    ident_input.setText("");
                }

                if (!tempList.get(4).equals("")) {
                    desc_input.setText(tempList.get(4));
                } else {
                    desc_input.setText("");
                }

                if (!tempList.get(5).equals("")) {
                    quit_msg_input.setText(tempList.get(5));
                } else {
                    quit_msg_input.setText("");
                }
            }
        }
    }

    public Component getList(File[] all) {

        /* The following FOR loop converts all of the array elements */
        for (int i = 0; i < all.length; i++) {
            File f = new File(all[i].toString());
            int lastOccurence = f.getName().lastIndexOf(".properties");
            String s = f.getName().substring(0, lastOccurence);
            all[i] = new File(s);
        }

        serverList = new JComboBox(all); // Create the box with ->all<- the filenames.
        serverList.setMaximumRowCount(7);

        return serverList;
    }

    //================================================================================
    // Config Inner Classes
    //================================================================================
    private class PropertiesFileFilter implements FileFilter {

        //============================================================================
        // PropertiesFileFilter Methods
        //============================================================================
        @Override
        public boolean accept(File file) {

            String name = file.getName().toLowerCase();

            if (name != null && name.endsWith(".properties")) {
                return true;
            }

            return false;

        }
    }
}
