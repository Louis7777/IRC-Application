package javachatclient;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.MalformedURLException;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;

public class AboutWindow extends JFrame {

    //================================================================================
    // Fields
    //================================================================================
    private final ImageIcon LABEL_ICON = new ImageIcon(GUI.class.getResource("resources/zoidberg.png"));
    private final ImageIcon PAUSE_ICON = new ImageIcon(GUI.class.getResource("resources/pause.png"));
    private final ImageIcon PLAY_ICON = new ImageIcon(GUI.class.getResource("resources/play.png"));
    private boolean playing = true;
    private JButton pauseButton = null;
    private Sequencer sequencer = null;
    private String windowTitle = "About Java Chat Client v1.0";

    //================================================================================
    // Constructors
    //================================================================================
    public AboutWindow(int x, int y) {

        GUI.isAboutBeingUsed = true;

        //----------------------------------------------------------------------------
        // Create a window close listener.
        //----------------------------------------------------------------------------

        addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                //System.out.println("Closing info...");
                GUI.isAboutBeingUsed = false;
                sequencer.close();
            }
        });

        //----------------------------------------------------------------------------
        // Create a MIDI player.
        //----------------------------------------------------------------------------

        try {

            /* From resources */
            Sequence sequence = MidiSystem.getSequence(GUI.class.getResource("resources/song.mid"));

            /* From file */
            //Sequence sequence = MidiSystem.getSequence(new File(GUI.class.getResourceAsStream("/resources/song.mid").getPath()));

            /* From URL */
            //sequence = MidiSystem.getSequence(new URL("http://hostname/midiaudiofile"));

            /* Create a sequencer for the sequence */
            sequencer = MidiSystem.getSequencer();
            sequencer.open();
            sequencer.setSequence(sequence);

            /* Start playing */
            sequencer.start();

            /* Loop infinite times */
            sequencer.setLoopCount(Sequencer.LOOP_CONTINUOUSLY);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MidiUnavailableException e) {
            if (e.getMessage().equals("There is no driver installed on your system.")) {
                System.err.println("Couldn't play MIDI: " + e.getMessage());
            } else {
                e.printStackTrace();
            }
        } catch (InvalidMidiDataException e) {
            e.printStackTrace();
        }

        //----------------------------------------------------------------------------
        // Window layout.
        //----------------------------------------------------------------------------

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setTitle(windowTitle);
        ImageIcon img = new ImageIcon(GUI.WINDOW_ICON.getImage());
        setIconImage(img.getImage());
        setSize(480, 440);
        setLocation(x, y);
        setResizable(false);

        makeLayout();

        //setUndecorated(true);
        setVisible(true);
    }

    //================================================================================
    // About Methods
    //================================================================================
    private void makeLayout() {

        setLayout(new GridBagLayout()); // Layout must be set before any JPanels are made.

        setBackground(new Color(132, 112, 122));

        /* Create MIDI player pause/play button */

        pauseButton = new JButton(PAUSE_ICON);
        pauseButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        pauseButton.setPreferredSize(new Dimension(46, 34));
        pauseButton.setFocusPainted(false);
        pauseButton.setBackground(Color.ORANGE);
        pauseButton.setToolTipText("Pause / Play");
        pauseButton.setFont(new Font("Serif", Font.BOLD, 20));
        pauseButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                if (playing == true) {
                    sequencer.stop();
                    playing = false;
                    pauseButton.setIcon(PLAY_ICON);
                } else {
                    sequencer.start();
                    playing = true;
                    pauseButton.setIcon(PAUSE_ICON);
                }
            }
        });

        /* Create an exit button */

        JButton exitButton = new JButton("I've seen enough");
        exitButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        exitButton.setPreferredSize(new Dimension(150, 30));
        exitButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                AboutWindow.super.dispose();
                //System.out.println("Closing info...");
                GUI.isAboutBeingUsed = false;
                sequencer.close();
            }
        });

        /* Create an image label */

        JLabel lblimage = new JLabel(LABEL_ICON);

        /* Create the "About" text area */

        JTextArea aboutText = new JTextArea();
        String message =
                "Version 1.0 Memo:\n\n"
                + "Java Chat Client v1.0 is an IRC (Internet Relay Chat) program. "
                + "It is used to connect to an unlimited number of IRC servers "
                + "simultaneously and chat in channels to other IRC users. "
                + "IRC provides a way of communicating in real time with people "
                + "from all over the world.\n\n"
                + "It consists of various separate networks of IRC servers, machines "
                + "that allow users to connect to IRC. The largest networks are EFnet "
                + "(the original IRC net, often having more than 32.000 people at once), "
                + "Quakenet, Undernet, IRCnet, and DAlnet.";
        aboutText.setEditable(false);
        aboutText.setOpaque(true);
        aboutText.setLineWrap(true);
        aboutText.setWrapStyleWord(true);
        aboutText.setMargin(new Insets(10, 10, 10, 10));
        aboutText.setSelectedTextColor(Color.RED);
        aboutText.setSelectionColor(Color.LIGHT_GRAY);
        aboutText.setFont(new Font("Serif", Font.PLAIN, 14));
        aboutText.setText(message);

        //////////////////////////////////////////////////////////////////////////////

        GridBagConstraints c = new GridBagConstraints();

        c.insets = new Insets(10, 10, 10, 10); // Top, right, bottom and left padding.

        c.gridx = 1;
        c.gridy = 0;
        c.weighty = 0.1;
        c.gridwidth = 1;
        c.anchor = GridBagConstraints.NORTHEAST;
        add(pauseButton, c);

        c.gridx = 0;
        c.gridy = 1;
        c.weightx = 1.0;
        c.weighty = 0.7;
        c.gridwidth = 2;
        c.fill = GridBagConstraints.BOTH;
        c.anchor = GridBagConstraints.CENTER;
        add(aboutText, c);

        c.gridx = 0;
        c.gridy = 2;
        c.weighty = 0.2;
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.WEST;
        add(lblimage, c);

        c.gridx = 1;
        c.anchor = GridBagConstraints.CENTER;
        add(exitButton, c);

    }
}
