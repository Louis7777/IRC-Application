package javawebchat;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BoundedRangeModel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.Timer;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

public abstract class MessagePanel extends JPanel {

    //================================================================================
    // Fields
    //================================================================================
    private final int BUFFER_SIZE = 1048576; // 1mb buffer
    private String title;
    private Timer timer;
    public Connection con;
    private StyleContext context = new StyleContext();
    public StyledDocument document = new DefaultStyledDocument(context);
    private javax.swing.text.Style style = context.getStyle(StyleContext.DEFAULT_STYLE);
    public JTextPane display = new JTextPane(document);
    public JPanel textPanel = new JPanel(new BorderLayout());
    public JScrollPane textPanelScroll = new JScrollPane(textPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    public SimpleAttributeSet channelInfoKeyWord = new SimpleAttributeSet();
    public SimpleAttributeSet clientKeyWord = new SimpleAttributeSet();
    public SimpleAttributeSet clientErrorKeyWord = new SimpleAttributeSet();
    public SimpleAttributeSet clientFatalErrorKeyWord = new SimpleAttributeSet();
    public SimpleAttributeSet joinedKeyWord = new SimpleAttributeSet();
    public SimpleAttributeSet motdKeyWord = new SimpleAttributeSet();
    public SimpleAttributeSet nickChangeKeyWord = new SimpleAttributeSet();
    public SimpleAttributeSet noticeKeyWord = new SimpleAttributeSet();
    public SimpleAttributeSet parseErrorKeyWord = new SimpleAttributeSet();
    public SimpleAttributeSet partedKeyWord = new SimpleAttributeSet();

    //================================================================================
    // Constructors
    //================================================================================
    public MessagePanel() {

        display.setEditable(false); // Prevent editing of the document.
        display.setMargin(new Insets(0, 5, 0, 0));
        display.setAutoscrolls(true);
        display.addMouseListener(new RightClickMenu()); // Add a right-click context menu.

        textPanel.setBackground(display.getBackground());
        textPanel.add(display, BorderLayout.SOUTH);

        timer = new Timer(1000, new ActionListener() { // Timer MUST be initialized.

            @Override
            public void actionPerformed(ActionEvent e) {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        });

        //----------------------------------------------------------------------------
        // Adjust the position of the scrollbar.
        //----------------------------------------------------------------------------

        /* Scrollbars appear automatically when the "preferred size" of the component
         * added to the viewport of the scrollpane is greater than the size of the scrollpane. */

        textPanelScroll.setViewportView(display);

        textPanelScroll.getVerticalScrollBar().setUnitIncrement(16); // Set vertical scrolling speed.

        textPanelScroll.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {

            BoundedRangeModel brm = textPanelScroll.getVerticalScrollBar().getModel();
            boolean wasAtBottom = true;

            @Override
            public void adjustmentValueChanged(AdjustmentEvent e) {
                //e.getAdjustable().setValue(e.getAdjustable().getMaximum());

                /* If the scroll bar is not being moved by a person. */
                if (!brm.getValueIsAdjusting()) {

                    /* If the scrollbar was at already adjusted at the bottom,
                     * then keep it at the bottom by resetting it to the maximum.
                     */
                    if (wasAtBottom) {
                        brm.setValue(brm.getMaximum());
                    }
                    /*else {
                    // Code to do:
                    // If scroll bar is not at bottom.
                    // And then if new text arrives then...
                    // the bottom part of the scrollbar's track should become red.
                    }*/
                } else {
                    wasAtBottom = ((brm.getValue() + brm.getExtent()) == brm.getMaximum());
                }
            }
        });

        //----------------------------------------------------------------------------
        // Create styles for all types of messages.
        //----------------------------------------------------------------------------

        StyleConstants.setForeground(channelInfoKeyWord, Color.GRAY);

        StyleConstants.setForeground(clientKeyWord, Color.BLUE);
        StyleConstants.setBackground(clientKeyWord, Color.LIGHT_GRAY);
        StyleConstants.setBold(clientKeyWord, true);

        StyleConstants.setForeground(clientErrorKeyWord, Color.RED);

        StyleConstants.setForeground(clientFatalErrorKeyWord, Color.RED);
        StyleConstants.setBackground(clientFatalErrorKeyWord, Color.LIGHT_GRAY);
        StyleConstants.setBold(clientFatalErrorKeyWord, true);

        StyleConstants.setForeground(joinedKeyWord, new Color(0, 100, 0)); // Dark green.

        StyleConstants.setForeground(motdKeyWord, Color.GRAY);
        StyleConstants.setBold(motdKeyWord, true);

        StyleConstants.setForeground(nickChangeKeyWord, Color.BLUE);

        StyleConstants.setForeground(noticeKeyWord, new Color(149, 58, 0));

        StyleConstants.setForeground(partedKeyWord, Color.RED);

        StyleConstants.setForeground(parseErrorKeyWord, Color.RED);
        StyleConstants.setBackground(parseErrorKeyWord, Color.LIGHT_GRAY);
        StyleConstants.setBold(parseErrorKeyWord, true);

    }

    //================================================================================
    // Accessors and Mutators (Getters and Setters)
    //================================================================================
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    //================================================================================
    // MessagePanel Methods
    //================================================================================
    public void drawMessage(String message, SimpleAttributeSet keyword) {

        /* This method draws the message to the document
         * with respect to its limit of characters.
         */

        int overLength = document.getLength() + message.length() - BUFFER_SIZE;
        if (overLength > 0) {
            try {
                document.remove(0, overLength);
            } catch (BadLocationException ex) {
                Logger.getLogger(ConsoleOutputPane.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        try {

            document.insertString(document.getLength(), message, keyword);

            /* If the user is not viewing the panel
             * and a new message arrives, we want to make
             * the tab flash for a while as a notification.
             *
             * NOTE: The foreground color (the title's color) of non-console
             * tabs is currently not affected because the tab title
             * is a label within the header.
             */

            if (!JavaWebChat.currentView.equals(this.getTitle())) {

                synchronized (Connection.MUTEX) {

                    final String _tabTitle = this.getTitle();
                    final int _tabIndex = JavaWebChat.tabbedPane.indexOfTab(this.getTitle());

                    //System.err.println("Title is: " + this.getTitle() + " . Index is: " + _tabIndex);

                    final Color _savedForeground = JavaWebChat.tabbedPane.getForeground();
                    final Color _savedBackground = JavaWebChat.tabbedPane.getBackground();
                    final Color _foreground = Color.ORANGE;
                    final Color _background = Color.YELLOW;

                    long start = System.currentTimeMillis();
                    final long end = start + 5000; // duration of flashing in milliseconds

                    if (!timer.isRunning()) { // If timer has finished running, create a new one.
                            /* 500ms delay */
                        timer = new Timer(500, new ActionListener() { // Requirement: import javax.swing.Timer;

                            boolean on = false;

                            @Override
                            public void actionPerformed(ActionEvent e) {
                                if (System.currentTimeMillis() < end && !JavaWebChat.currentView.equals(_tabTitle)) {
                                    if (on) {
                                        if (_foreground != null) {
                                            JavaWebChat.tabbedPane.setForegroundAt(_tabIndex, _foreground);
                                        }
                                        if (_background != null) {
                                            JavaWebChat.tabbedPane.setBackgroundAt(_tabIndex, _background);
                                        }
                                    } else {
                                        if (_savedForeground != null) {
                                            JavaWebChat.tabbedPane.setForegroundAt(_tabIndex, _savedForeground);
                                        }
                                        if (_savedBackground != null) {
                                            JavaWebChat.tabbedPane.setBackgroundAt(_tabIndex, _savedBackground);
                                        }
                                    }
                                    repaint();
                                    on = !on;
                                } else {
                                    timer.stop();
                                    JavaWebChat.tabbedPane.setForegroundAt(_tabIndex, _savedForeground);
                                    JavaWebChat.tabbedPane.setBackgroundAt(_tabIndex, _savedBackground);
                                }
                            }
                        });
                        timer.start();

                    }
                }

            }
        } catch (BadLocationException ex) {
            ex.printStackTrace();
        }
    }
}
