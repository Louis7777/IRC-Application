package javawebchat;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.JScrollPane;

public class ConsoleOutputPane extends MessagePanel {

    //================================================================================
    // Fields
    //================================================================================
    // add fields here
    //================================================================================
    // Constructors
    //================================================================================
    public ConsoleOutputPane(Connection con) {

        super.con = con;
        super.setTitle("Console");
        super.textPanelScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        //////////////////////////////////////////////////////////////////////////////

        setLayout(new GridBagLayout());

        //////////////////////////////////////////////////////////////////////////////

        GridBagConstraints c = new GridBagConstraints();

        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1.0;
        c.weighty = 1.0;
        c.fill = GridBagConstraints.BOTH;
        add(textPanelScroll, c);
    }
    //================================================================================
    // ConsoleOutputPane Methods
    //================================================================================
    // do any required methods
}
