package javachatclient;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class HelpPanel extends JPanel {
    //================================================================================
    // Fields
    //================================================================================
    // add fields here
    //================================================================================
    // Constructors
    //================================================================================

    public HelpPanel() {

        setAutoscrolls(true);
        setBackground(new Color(229, 229, 229));
        setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 5));

        makeLayout();

        setVisible(true);
    }

    //================================================================================
    // HelpWindow Methods
    //================================================================================
    private void makeLayout() {

        setLayout(new GridBagLayout());

        //----------------------------------------------------------------------------
        // Labels
        //----------------------------------------------------------------------------

        JLabel header1 = new JLabel("Commands");
        JLabel header2 = new JLabel("Format");

        JLabel command1 = new JLabel("JOIN");
        JLabel command2 = new JLabel("NICK");
        JLabel command3 = new JLabel("PART");
        JLabel command4 = new JLabel("PRIVMSG");
        JLabel command5 = new JLabel("SERVER");
        JLabel command6 = new JLabel("TOPIC");

        JLabel command12 = new JLabel("/join <#channel>");
        JLabel command22 = new JLabel("/nick <nickname>");
        JLabel command32 = new JLabel("/part <#channel> <reason>");
        JLabel command42 = new JLabel("/privmsg <#channel>|<nickname> <message>");
        JLabel command52 = new JLabel("/server <servername>");
        JLabel command62 = new JLabel("/topic <#channel> <topic>");

        header1.setForeground(Color.BLUE);
        header2.setForeground(Color.BLUE);

        header1.setFont(new Font("Serif", Font.PLAIN, 20));
        header2.setFont(new Font("Serif", Font.PLAIN, 20));

        //----------------------------------------------------------------------------
        // Text areas
        //----------------------------------------------------------------------------

        Insets insets = new Insets(10, 10, 10, 10);

        JTextArea area1 = new JTextArea();
        JTextArea area2 = new JTextArea();
        JTextArea area3 = new JTextArea();
        JTextArea area4 = new JTextArea();
        JTextArea area5 = new JTextArea();
        JTextArea area6 = new JTextArea();

        area1.setEditable(false);
        area2.setEditable(false);
        area3.setEditable(false);
        area4.setEditable(false);
        area5.setEditable(false);
        area6.setEditable(false);

        area1.setLineWrap(true);
        area2.setLineWrap(true);
        area3.setLineWrap(true);
        area4.setLineWrap(true);
        area5.setLineWrap(true);
        area6.setLineWrap(true);

        area1.setWrapStyleWord(true);
        area2.setWrapStyleWord(true);
        area3.setWrapStyleWord(true);
        area4.setWrapStyleWord(true);
        area5.setWrapStyleWord(true);
        area6.setWrapStyleWord(true);

        area1.setSelectedTextColor(Color.RED);
        area2.setSelectedTextColor(Color.RED);
        area3.setSelectedTextColor(Color.RED);
        area4.setSelectedTextColor(Color.RED);
        area5.setSelectedTextColor(Color.RED);
        area6.setSelectedTextColor(Color.RED);

        area1.setSelectionColor(Color.LIGHT_GRAY);
        area2.setSelectionColor(Color.LIGHT_GRAY);
        area3.setSelectionColor(Color.LIGHT_GRAY);
        area4.setSelectionColor(Color.LIGHT_GRAY);
        area5.setSelectionColor(Color.LIGHT_GRAY);
        area6.setSelectionColor(Color.LIGHT_GRAY);

        area1.setMargin(insets);
        area2.setMargin(insets);
        area3.setMargin(insets);
        area4.setMargin(insets);
        area5.setMargin(insets);
        area6.setMargin(insets);

        String textOfArea1 = "Use it to join a channel or a list of comma separated channels. Examples:\n\n"
                + "/join #IST_university\n"
                + "/join #Chatroom1,#Chatroom2,Chatroom3\n\n"
                + "Note: A channel name must begin with a number sign (#) and should not contain any spaces.";
        String textOfArea2 = "Use it to change your nickname while you are chatting. Type \"/nick\", leave a space and type your new nickname. Example:\n\n"
                + "/nick Louis\n\n"
                + "Note: A nickname can contain any letter, number, or any of the following characters: - _ [ ] | ^ ` { } \"\n"
                + "However, it cannot start with a number and most servers allow nicknames up to 15 characters long.";
        String textOfArea3 = "Use it to leave from a channel that you are already a member of. Examples:\n\n"
                + "/part #IST_university\n"
                + "/part #Chatroom1,#Chatroom2,Chatroom3\n\n";
        String textOfArea4 = "Use it to send a message to a channel or to another user. Examples:\n\n"
                + "/privmsg #athens Hello everyone!\n"
                + "/privmsg Maria Hey Maria, how's it going?\n";
        String textOfArea5 = "Use it to connect to a server. Example:\n\n"
                + "/server irc.icq.com";
        String textOfArea6 = "Use it to see or set a channel's topic. Examples:\n\n"
                + "/topic #Cafeteria\n"
                + "/topic #Cafeteria Have a nice time chatting in Cafeteria!";

        area1.setText(textOfArea1);
        area2.setText(textOfArea2);
        area3.setText(textOfArea3);
        area4.setText(textOfArea4);
        area5.setText(textOfArea5);
        area6.setText(textOfArea6);

        area1.setPreferredSize(new Dimension(400, 200));
        area2.setPreferredSize(new Dimension(400, 200));
        area3.setPreferredSize(new Dimension(400, 200));
        area4.setPreferredSize(new Dimension(400, 200));
        area5.setPreferredSize(new Dimension(400, 200));
        area6.setPreferredSize(new Dimension(400, 200));

        //////////////////////////////////////////////////////////////////////////////

        GridBagConstraints c = new GridBagConstraints();

        c.insets = insets;

        /* Add all the command names. */

        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 0.2;
        c.anchor = GridBagConstraints.WEST;
        add(header1, c);

        c.gridy = 2;
        add(command1, c);

        c.gridy = 4;
        add(command2, c);

        c.gridy = 6;
        add(command3, c);

        c.gridy = 8;
        add(command4, c);

        c.gridy = 10;
        add(command5, c);

        c.gridy = 12;
        add(command6, c);

        /* Add all the command formats. */

        c.gridx = 1;
        c.gridy = 0;
        c.weightx = 0.8;
        add(header2, c);

        c.gridy = 2;
        add(command12, c);

        c.gridy = 4;
        add(command22, c);

        c.gridy = 6;
        add(command32, c);

        c.gridy = 8;
        add(command42, c);

        c.gridy = 10;
        add(command52, c);

        c.gridy = 12;
        add(command62, c);

        /* Add all the command descriptions. */

        c.gridx = 0;
        c.gridy = 3;
        c.weightx = 1.0;
        c.gridwidth = 2;
        c.fill = GridBagConstraints.BOTH;
        add(area1, c);

        c.gridy = 5;
        add(area2, c);

        c.gridy = 7;
        add(area3, c);

        c.gridy = 9;
        add(area4, c);

        c.gridy = 11;
        add(area5, c);

        c.gridy = 13;
        add(area6, c);
    }
}
