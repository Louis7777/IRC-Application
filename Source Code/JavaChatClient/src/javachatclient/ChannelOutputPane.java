package javachatclient;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SortOrder;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.jdesktop.swingx.JXList;

public class ChannelOutputPane extends MessagePanel {

    //================================================================================
    // Fields
    //================================================================================
    public JTextArea topic = new JTextArea();
    public DefaultListModel listModel = new DefaultListModel();
    private JXList list = new JXList(listModel);

    //================================================================================
    // Constructors
    //================================================================================
    public ChannelOutputPane(final Connection con) {

        super.con = con;

        //----------------------------------------------------------------------------
        // Create a panel for the text pane panel and the topic panel.
        //----------------------------------------------------------------------------
        
        JPanel chat = new JPanel(new GridBagLayout());
        
        //----------------------------------------------------------------------------
        // Create a panel to display the channel's topic.
        //----------------------------------------------------------------------------
    
        JPanel topicPanel = new JPanel(new GridBagLayout());
        
        JLabel topicLabel = new JLabel(" Topic: ");
        topicLabel.setBackground(new Color(238, 238, 238));
        topic.getDocument().addDocumentListener(new DocumentListener() { // Update the tooltip text.

            @Override
            public void insertUpdate(DocumentEvent e) {
                topic.setToolTipText(topic.getText());
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                topic.setToolTipText(topic.getText());
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                topic.setToolTipText(topic.getText());
            }
        });
        topic.setEditable(false);
        topic.setMargin(new Insets(4, 6, 0, 0));
        topic.setLineWrap(true);
        topic.setBackground(new Color(238, 238, 238));
        topic.setText("");
        topic.setToolTipText(topic.getText()); // Update tooltip text?
        JScrollPane topicAreaScroll = new JScrollPane(topic, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        topicAreaScroll.setPreferredSize(new Dimension(70, 25));
        topicAreaScroll.setMinimumSize(topicAreaScroll.getPreferredSize());
        topicAreaScroll.setBorder(BorderFactory.createEmptyBorder());
        
        GridBagConstraints t = new GridBagConstraints();

        t.gridx = 0;
        t.gridy = 0;
        t.fill = GridBagConstraints.BOTH;
        topicPanel.add(topicLabel, t);

        t.gridx = 1;
        t.weightx = 1.0;
        topicPanel.add(topicAreaScroll, t);

        t.gridx = 0;
        t.gridy = 0;
        t.weightx = 1.0;
        t.fill = GridBagConstraints.BOTH;
        chat.add(topicPanel, t);

        t.gridy = 1;
        t.weighty = 1.0;
        chat.add(textPanelScroll, t);

        //----------------------------------------------------------------------------
        // Create a panel to display the channel's nicknames list.
        //----------------------------------------------------------------------------

        JPanel nickList = new JPanel(new GridBagLayout());

        //----------------------------------------------------------------------------
        // Create a header for the nicknames list.
        //----------------------------------------------------------------------------

        JTextField header = new JTextField() {

            @Override
            public void setBorder(Border border) {
                // No border!
            }
        };
        String text = "Nick List";
        header.setHorizontalAlignment(JTextField.CENTER);
        header.setPreferredSize(new Dimension(70, 25));
        header.setMinimumSize(header.getPreferredSize());
        header.setEditable(false);
        header.setOpaque(true);
        header.setMargin(new Insets(1, 1, 1, 1));
        header.setBackground(new Color(68, 68, 68));
        header.setForeground(Color.WHITE);
        header.setSelectedTextColor(Color.RED);
        header.setSelectionColor(Color.LIGHT_GRAY);
        header.setFont(new Font(null, Font.BOLD, 14));
        header.setText(text);

        //----------------------------------------------------------------------------
        // Configure the nicknames list.
        //----------------------------------------------------------------------------

        JScrollPane listScroller = new JScrollPane(list);

        list.setSortsOnUpdates(true);
        list.setAutoCreateRowSorter(true);
        list.toggleSortOrder();
        list.setSortOrder(SortOrder.ASCENDING);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setLayoutOrientation(JList.VERTICAL);
        list.setBackground(new Color(236, 233, 216));
        list.setForeground(Color.BLACK);
        list.setSelectionBackground(Color.black);
        list.setSelectionForeground(Color.lightGray);
        list.setBorder(new EmptyBorder(0, 1, 0, 1));
        list.addMouseListener(new MouseAdapter() {

            Rectangle r = list.getCellBounds(0, list.getLastVisibleIndex()); // Rectangle that contains all items.

            @Override
            public void mouseClicked(MouseEvent evt) { // Double click.
                JList list = (JList) evt.getSource();

                /*if (evt.getClickCount() == 1) {
                list.clearSelection();
                }*/

                if (evt.getClickCount() == 2) {

                    Integer index = GUI.tabbedPane.indexOfTab(list.getSelectedValue().toString());

                    //if (r != null && r.contains(evt.getPoint())) {

                    if (index < 0) { // If tab doesn't exist.
                        MessagePanel privateChat = new QueryOutputPane(con);
                        privateChat.setTitle(list.getSelectedValue().toString());
                        GUI.addTab(privateChat, con);
                    } else { // if tab exists just bring it to front.
                        synchronized (Connection.MUTEX) {
                            GUI.tabbedPane.setSelectedIndex(index);
                        }
                    }

                    //}
                }
            }
        });

        //----------------------------------------------------------------------------
        // Separate the chat panel from the nicknames panel.
        //----------------------------------------------------------------------------

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, chat, nickList); // How to Split, What goes left, What goes right.
        split.setContinuousLayout(true);
        //split.setOneTouchExpandable(true);
        split.setDividerSize(3);
        split.setBorder(BorderFactory.createEmptyBorder());
        split.setResizeWeight(1.0); // The right panel will remain static on resize and the left one will take the extra space.

        //////////////////////////////////////////////////////////////////////////////

        setLayout(new GridBagLayout());
        setDividerLocation(split, 0.75); // Set the initial position of divider.

        //////////////////////////////////////////////////////////////////////////////

        GridBagConstraints c = new GridBagConstraints();

        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1.0;
        c.fill = GridBagConstraints.BOTH;
        c.anchor = GridBagConstraints.NORTH;
        nickList.add(header, c);

        c.gridy = 1;
        c.weighty = 1.0; // Get all the available height.
        c.ipadx = 3;
        c.anchor = GridBagConstraints.WEST;
        nickList.add(listScroller, c);

        /* Add the split pane */

        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1.0;
        c.weighty = 1.0;
        c.fill = GridBagConstraints.BOTH;
        add(split, c);
    }

    //================================================================================
    // ChannelOutputPane Methods
    //================================================================================

    /* Add a user */
    public void addNickListRecord(String nickname) {

        listModel.addElement(nickname);

    }

    /* Remove a user */
    public void delNickListRecord(String nickname) {

        listModel.removeElement(nickname);

    }

    /* Modify a user's nickname/details */
    public void modifyNickListRecord(int index, String nickname) {

        listModel.setElementAt(nickname, index);

    }

    /* Configure the divider location of the JSplitPane. */
    public static JSplitPane setDividerLocation(final JSplitPane splitter, final double proportion) {
        if (splitter.isShowing()) {
            if (splitter.getWidth() > 0 && splitter.getHeight() > 0) {
                splitter.setDividerLocation(proportion);
            } else {
                splitter.addComponentListener(new ComponentAdapter() {

                    @Override
                    public void componentResized(ComponentEvent ce) {
                        splitter.removeComponentListener(this);
                        setDividerLocation(splitter, proportion);
                    }
                });
            }
        } else {
            splitter.addHierarchyListener(new HierarchyListener() {

                @Override
                public void hierarchyChanged(HierarchyEvent e) {
                    if ((e.getChangeFlags() & HierarchyEvent.SHOWING_CHANGED) != 0 && splitter.isShowing()) {
                        splitter.removeHierarchyListener(this);
                        setDividerLocation(splitter, proportion);
                    }
                }
            });
        }
        return splitter;
    }
}
