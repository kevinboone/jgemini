/*=========================================================================
  
  JGemini

  MediaSettingsPane 

  Copyright (c)2021 Kevin Boone, GPLv3.0 

=========================================================================*/
package me.kevinboone.jgemini.swing;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.*;
import java.util.*;
import java.awt.event.*;
import javax.swing.text.*;
import me.kevinboone.jgemini.base.*;
import me.kevinboone.jgemini.Constants;

/** Implements the Media tab of the Settings dialog.
*/
public class MediaSettingsPane extends SettingsPane
  {
  private MainWindow mainWindow;
  private String oldMediaPlayer;
  private JTextField mediaPlayer;

/*=========================================================================
  
  Constructor

=========================================================================*/
  protected MediaSettingsPane (MainWindow mainWindow)
    {
    super ("media_settings_pane");

    this.mainWindow = mainWindow;
    oldMediaPlayer = config.getStreamPlayer();

    GridBagLayout gl = new GridBagLayout ();
    setLayout (gl);
    setBorder(new EmptyBorder (10, 10, 10, 10));

    // Row 0, col 0
    GridBagConstraints gbc00 = new GridBagConstraints();
    gbc00.insets = new Insets (5, 5, 5, 5);
    gbc00.gridwidth = 2;
    gbc00.gridy = 0;
    gbc00.gridx = 0;
    gbc00.anchor = gbc00.EAST;
    JLabel helpLabel = createLabel ("media_settings_pane_player_help"); 
    add (helpLabel, gbc00);

    // Row 1, col 0
    GridBagConstraints gbc10 = new GridBagConstraints();
    gbc10.insets = new Insets (5, 5, 5, 5);
    gbc10.gridy = 1;
    gbc10.gridx = 0;
    gbc10.anchor = gbc10.EAST;
    JLabel mediaPlayerLabel = createLabel 
      ("media_settings_pane_media_player");
    add (mediaPlayerLabel, gbc10);

    // Row 1, col 1
    GridBagConstraints gbc11 = new GridBagConstraints();
    gbc11.insets = new Insets (5, 5, 5, 5);
    gbc11.gridy = 1;
    gbc11.gridx = 1;
    gbc11.anchor = gbc11.WEST;
    mediaPlayer = new JTextField (30);
    mediaPlayerLabel.setLabelFor (mediaPlayer); 
    add (mediaPlayer, gbc11);

    // Row 2, col 2
    GridBagConstraints gbc22 = new GridBagConstraints();
    gbc22.insets = new Insets (5, 5, 5, 5);
    gbc22.gridy = 2;
    gbc22.gridx = 1;
    gbc22.anchor = gbc22.WEST;
    JButton clearButton = createButton ("media_settings_pane_media_clear"); 
    clearButton.addActionListener (new ActionListener()
      {
      public void actionPerformed (ActionEvent e)
        {
        String message = messagesBundle.getString ("handling_cleared");
        config.clearDefaultFileHandling();
        config.save();
        statusHandler.writeMessage (message);
        }
      });
    add (clearButton, gbc22);

    mediaPlayer.setText (oldMediaPlayer);
    }

/*=========================================================================
  
  submit 

=========================================================================*/
  @Override
  protected void submit()
    {
    ccMode = ConfigChangeListener.CCMODE_NOUPDATE; 
    error = false;

    String newMediaPlayer = mediaPlayer.getText();
    // It's not strictly an error if the media player
    //   setting is empty.
    if (!newMediaPlayer.equals (oldMediaPlayer))
      {
      config.setStreamPlayer (newMediaPlayer);
      }
    }
  }







