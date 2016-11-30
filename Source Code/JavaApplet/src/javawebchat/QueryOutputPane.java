package javawebchat;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

public class QueryOutputPane extends MessagePanel {

    //================================================================================
    // Fields
    //================================================================================
    // add fields here
    //================================================================================
    // Constructors
    //================================================================================
    public QueryOutputPane(Connection con) {

        super.con = con;
        display.setBackground(new Color(236, 233, 216));
        textPanel.setBackground(display.getBackground());

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
    // QueryOutputPane Methods
    //================================================================================
    // do any required methods
}
