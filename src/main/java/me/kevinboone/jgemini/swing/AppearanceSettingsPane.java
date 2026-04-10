/*=========================================================================
  
  JGemini

  AppearanceSettingsPane 

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

public class AppearanceSettingsPane extends SettingsPane
  {
  private MainWindow mainWindow;
  private JTextField documentBaseFontSize;
  private JCheckBox emojiStripBookmarks; 
  private int oldDocumentBaseFontSize;
  private boolean oldEmojiStripBookmarks;

/*=========================================================================
  
  Constructor

=========================================================================*/
  protected AppearanceSettingsPane (MainWindow mainWindow)
    {
    super ("appearance_settings_pane");

    this.mainWindow = mainWindow;
    oldDocumentBaseFontSize = config.getDocumentBaseFontSize();
    oldEmojiStripBookmarks = config.getEmojiStripBookmarks();

    GridBagLayout gl = new GridBagLayout ();
    setLayout (gl);
    setBorder(new EmptyBorder (10, 10, 10, 10));

    // Row 0, col 0
    GridBagConstraints gbc00 = new GridBagConstraints();
    gbc00.insets = new Insets (5, 5, 5, 5);
    gbc00.gridy = 0;
    gbc00.gridx = 0;
    gbc00.anchor = gbc00.EAST;
    JLabel documentBaseFontSizeLabel = createLabel 
      ("appearance_settings_pane_base_font_size");
    add (documentBaseFontSizeLabel, gbc00);

    // Row 0, col 1
    GridBagConstraints gbc01 = new GridBagConstraints();
    gbc01.insets = new Insets (5, 5, 5, 5);
    gbc01.gridy = 0;
    gbc01.gridx = 1;
    gbc01.anchor = gbc01.WEST;
    documentBaseFontSize = new JTextField (5);
    documentBaseFontSizeLabel.setLabelFor (documentBaseFontSize); 
    add (documentBaseFontSize, gbc01);

    // Row 1, col 0
    GridBagConstraints gbc10 = new GridBagConstraints();
    gbc10.insets = new Insets (5, 5, 5, 5);
    gbc10.gridy = 1;
    gbc10.gridx = 0;
    gbc10.anchor = gbc10.EAST;
    JLabel emojiStripBookmarksLabel = createLabel 
      ("appearance_settings_pane_emoji_strip_bookmarks");
    add (emojiStripBookmarksLabel, gbc10);

    // Row 1, col 1
    GridBagConstraints gbc11 = new GridBagConstraints();
    gbc11.insets = new Insets (5, 5, 5, 5);
    gbc11.gridy = 1;
    gbc11.gridx = 1;
    emojiStripBookmarks = new JCheckBox();
    emojiStripBookmarksLabel.setLabelFor (emojiStripBookmarks); 
    add (emojiStripBookmarks, gbc11);

/*
    // Row 2, col 0
    GridBagConstraints gbc20 = new GridBagConstraints();
    gbc20.insets = new Insets (5, 5, 5, 5);
    gbc20.gridy = 2;
    gbc20.gridx = 0;
    JButton clearAppearance = createButton ("appearance_settings_pane_clear_appearance"); 
    add (clearAppearance, gbc20);
*/

    // Set digits only
    documentBaseFontSize.setDocument (new PlainDocument() 
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

    documentBaseFontSize.setText ("" + oldDocumentBaseFontSize);
    emojiStripBookmarks.setSelected (oldEmojiStripBookmarks);
    }

/*=========================================================================
  
  submit 

=========================================================================*/
  @Override
  protected void submit()
    {
    ccMode = ConfigChangeListener.CCMODE_NOUPDATE; 
    error = false;
   
    int newDocumentBaseFontSize;

    try
      {
      newDocumentBaseFontSize = Integer.parseInt 
        (documentBaseFontSize.getText());
      }
    catch (NumberFormatException e)
      {
      newDocumentBaseFontSize = oldDocumentBaseFontSize;
      }

    if (newDocumentBaseFontSize != oldDocumentBaseFontSize)
      {
      config.setDocumentBaseFontSize (newDocumentBaseFontSize);
      ccMode = ConfigChangeListener.CCMODE_REFRESH; 
      }

    boolean newEmojiStripBookmarks = emojiStripBookmarks.isSelected();
    if (newEmojiStripBookmarks != oldEmojiStripBookmarks)
      {
      config.setEmojiStripBookmarks (newEmojiStripBookmarks);
      ccMode = ConfigChangeListener.CCMODE_REFRESH; 
      }

    }
  }





