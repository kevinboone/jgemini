/*=========================================================================
  
  JGemini

  FeedSettingsPane 

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

/** Implements the Feed tab of the Settings dialog. 
*/
public class FeedSettingsPane extends SettingsPane
  {
  private MainWindow mainWindow;
  private JTextField feedMaxAge;
  private JCheckBox feedUpdateOnStartup;
  private int oldFeedMaxAge;
  private boolean oldFeedUpdateOnStartup;

/*=========================================================================
  
  Constructor

=========================================================================*/
  protected FeedSettingsPane (MainWindow mainWindow)
    {
    super ("feed_settings_pane");

    this.mainWindow = mainWindow;

    oldFeedMaxAge = config.getFeedsMaxAge();
    oldFeedUpdateOnStartup = config.getFeedsUpdateOnStartup();

    GridBagLayout gl = new GridBagLayout ();
    setLayout (gl);
    setBorder(new EmptyBorder (10, 10, 10, 10));

    // Row 0, col 0
    GridBagConstraints gbc00 = new GridBagConstraints();
    gbc00.insets = new Insets (5, 5, 5, 5);
    gbc00.gridy = 0;
    gbc00.gridx = 0;
    gbc00.anchor = gbc00.EAST;
    JLabel feedMaxAgeLabel = createLabel ("feed_settings_pane_max_age");
    add (feedMaxAgeLabel, gbc00);

    // Row 0, col 1
    GridBagConstraints gbc01 = new GridBagConstraints();
    gbc01.insets = new Insets (5, 5, 5, 5);
    gbc01.gridy = 0;
    gbc01.gridx = 1;
    gbc01.anchor = gbc01.WEST;
    feedMaxAge = new JTextField (5);
    feedMaxAgeLabel.setLabelFor (feedMaxAge); 
    add (feedMaxAge, gbc01);

    // Row 1, col 0
    GridBagConstraints gbc10 = new GridBagConstraints();
    gbc10.insets = new Insets (5, 5, 5, 5);
    gbc10.gridy = 1;
    gbc10.gridx = 0;
    gbc10.anchor = gbc10.EAST;
    JLabel feedUpdateOnStartupLabel 
      = createLabel ("feed_settings_pane_update_on_startup");
    add (feedUpdateOnStartupLabel, gbc10);

    // Row 1, col 1
    GridBagConstraints gbc11 = new GridBagConstraints();
    gbc11.insets = new Insets (5, 5, 5, 5);
    gbc11.gridy = 1;
    gbc11.gridx = 1;
    gbc11.anchor = gbc11.WEST;
    feedUpdateOnStartup = new JCheckBox();
    feedUpdateOnStartupLabel.setLabelFor (feedUpdateOnStartup); 
    add (feedUpdateOnStartup, gbc11);

    // Set digits only
    feedMaxAge.setDocument (new PlainDocument() 
      {
      public void insertString (int offs, String str, AttributeSet a) 
         throws BadLocationException 
        {
        if (str == null) return;
        if (str.matches("[0-9]+")) 
          {  
          super.insertString(offs, str, a);
          }
        }
      });

    feedMaxAge.setText ("" + config.getFeedsMaxAge());
    feedUpdateOnStartup.setSelected (oldFeedUpdateOnStartup);
    }

/*=========================================================================
  
  submit 

=========================================================================*/
  @Override
  protected void submit()
    {
    ccMode = ConfigChangeListener.CCMODE_NOUPDATE; 
    error = false;

    boolean newFeedUpdateOnStartup = feedUpdateOnStartup.isSelected();
    int newFeedMaxAge;
    try
      {
      newFeedMaxAge = Integer.parseInt (feedMaxAge.getText());
      }
    catch (NumberFormatException e)
      {
      newFeedMaxAge = oldFeedMaxAge; // Ignore crappy changes
      }

    if (newFeedMaxAge != oldFeedMaxAge)
      {
      config.setFeedsMaxAge (newFeedMaxAge);
      config.save();
      }

    if (newFeedUpdateOnStartup != oldFeedUpdateOnStartup)
      {
      config.setFeedsUpdateOnStartup (newFeedUpdateOnStartup);
      config.save();
      }

    // Leave ccMode at 'do nothing'. No change we make here requires
    //   anything in the rest of the program to be updated
    ccMode = ConfigChangeListener.CCMODE_NOUPDATE; 
    }
  }




