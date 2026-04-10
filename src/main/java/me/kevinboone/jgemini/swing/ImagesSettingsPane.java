/*=========================================================================
  
  JGemini

  ImagesettingsPane 

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

public class ImagesSettingsPane extends SettingsPane
  {
  private MainWindow mainWindow;
  private JCheckBox gemtextInlineImages;
  private JTextField imagesWidth;
  private boolean oldGemtextInlineImages;
  private int oldInlineImageWidth;

/*=========================================================================
  
  Constructor

=========================================================================*/
  protected ImagesSettingsPane (MainWindow mainWindow)
    {
    super ("images_settings_pane");

    this.mainWindow = mainWindow;

    oldGemtextInlineImages = config.getGemtextInlineImages();
    oldInlineImageWidth = config.getInlineImageWidth();

    GridBagLayout gl = new GridBagLayout ();
    setLayout (gl);
    setBorder(new EmptyBorder (10, 10, 10, 10));

    // Row 0, col 0
    GridBagConstraints gbc00 = new GridBagConstraints();
    gbc00.insets = new Insets (5, 5, 5, 5);
    gbc00.gridy = 0;
    gbc00.gridx = 0;
    gbc00.anchor = gbc00.EAST;
    JLabel gemtextInlineImagesLabel = createLabel ("images_settings_pane_gemtext_inline");
    add (gemtextInlineImagesLabel, gbc00);

    // Row 0, col 1
    GridBagConstraints gbc01 = new GridBagConstraints();
    gbc01.insets = new Insets (5, 5, 5, 5);
    gbc01.gridy = 0;
    gbc01.gridx = 1;
    gbc01.anchor = gbc01.WEST;
    gemtextInlineImages = new JCheckBox();
    gemtextInlineImagesLabel.setLabelFor (gemtextInlineImages); 
    add (gemtextInlineImages, gbc01);

    gemtextInlineImages.setSelected (oldGemtextInlineImages);

    // Row 1, col 0
    GridBagConstraints gbc10 = new GridBagConstraints();
    gbc10.insets = new Insets (5, 5, 5, 5);
    gbc10.gridy = 1;
    gbc10.gridx = 0;
    gbc10.anchor = gbc10.EAST;
    JLabel imagesWidthLabel = createLabel 
      ("images_settings_pane_image_width");
    add (imagesWidthLabel, gbc10);

    // Row 1, col 1
    GridBagConstraints gbc11 = new GridBagConstraints();
    gbc11.insets = new Insets (5, 5, 5, 5);
    gbc11.gridy = 1;
    gbc11.gridx = 1;
    imagesWidth = new JTextField(5);
    imagesWidthLabel.setLabelFor (imagesWidth); 
    add (imagesWidth, gbc11);

/*
    // Row 2, col 0
    GridBagConstraints gbc20 = new GridBagConstraints();
    gbc20.insets = new Insets (5, 5, 5, 5);
    gbc20.gridy = 2;
    gbc20.gridx = 0;
    JButton clearImages = createButton ("images_settings_pane_clear_images"); 
    add (clearImages, gbc20);
*/

    // Set digits only
    imagesWidth.setDocument (new PlainDocument() 
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

    imagesWidth.setText ("" + config.getInlineImageWidth());
    }

/*=========================================================================
  
  submit 

=========================================================================*/
  @Override
  protected void submit()
    {
    ccMode = ConfigChangeListener.CCMODE_NOUPDATE; 
    error = false;

    boolean newGemtextInlineImages = gemtextInlineImages.isSelected();

    if (newGemtextInlineImages != oldGemtextInlineImages)
      {
      config.setGemtextInlineImages (newGemtextInlineImages);
      // Force a reload of the page, since that's the only way
      //   we can at present change the HTML in the viewer
      ccMode = ConfigChangeListener.CCMODE_RELOAD; 
      }

    int newInlineImageWidth; 
    try
      {
      newInlineImageWidth = Integer.parseInt (imagesWidth.getText());
      }
    catch (NumberFormatException e)
      {
      newInlineImageWidth = oldInlineImageWidth; // Ignore crappy changes
      }

    if (newInlineImageWidth != oldInlineImageWidth)
      {
      config.setInlineImageWidth (newInlineImageWidth);
      ccMode = ConfigChangeListener.CCMODE_RELOAD; 
      }
    }
  }




