/*=========================================================================
  
  JGemini

  SearchSettingsPane 

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

public class SearchSettingsPane extends SettingsPane
  {
  private MainWindow mainWindow;
  private JCheckBox urlbarSearchEnabled; 
  private JTextField urlbarSearchUrl;
  private boolean oldUrlbarSearchEnabled;
  private String oldUrlbarSearchUrl;

/*=========================================================================
  
  Constructor

=========================================================================*/
  protected SearchSettingsPane (MainWindow mainWindow)
    {
    super ("search_settings_pane");

    this.mainWindow = mainWindow;
    oldUrlbarSearchEnabled = config.getUrlbarSearchEnabled();
    oldUrlbarSearchUrl = config.getUrlbarSearchUrl();

    GridBagLayout gl = new GridBagLayout ();
    setLayout (gl);
    setBorder(new EmptyBorder (10, 10, 10, 10));

    // Row 0, col 0
    GridBagConstraints gbc00 = new GridBagConstraints();
    gbc00.insets = new Insets (5, 5, 5, 5);
    gbc00.gridy = 0;
    gbc00.gridx = 0;
    gbc00.anchor = gbc00.EAST;
    JLabel urlbarSearchEnabledLabel = createLabel 
      ("search_settings_pane_urlbar_search_enabled");
    add (urlbarSearchEnabledLabel, gbc00);

    // Row 0, col 1
    GridBagConstraints gbc01 = new GridBagConstraints();
    gbc01.insets = new Insets (5, 5, 5, 5);
    gbc01.gridy = 0;
    gbc01.gridx = 1;
    gbc01.anchor = gbc01.WEST;
    urlbarSearchEnabled = new JCheckBox();
    urlbarSearchEnabledLabel.setLabelFor (urlbarSearchEnabled); 
    add (urlbarSearchEnabled, gbc01);

    urlbarSearchEnabled.setSelected (oldUrlbarSearchEnabled);

    // Row 1, col 0
    GridBagConstraints gbc10 = new GridBagConstraints();
    gbc10.insets = new Insets (5, 5, 5, 5);
    gbc10.gridy = 1;
    gbc10.gridx = 0;
    gbc10.anchor = gbc10.EAST;
    JLabel urlbarSearchUrlLabel = createLabel 
      ("search_settings_pane_urlbar_search_url");
    add (urlbarSearchUrlLabel, gbc10);

    // Row 1, col 1
    GridBagConstraints gbc11 = new GridBagConstraints();
    gbc11.insets = new Insets (5, 5, 5, 5);
    gbc11.gridy = 1;
    gbc11.gridx = 1;
    urlbarSearchUrl = new JTextField (30);
    urlbarSearchUrlLabel.setLabelFor (urlbarSearchUrl); 
    add (urlbarSearchUrl, gbc11);

    urlbarSearchUrl.setText (oldUrlbarSearchUrl);
    }

/*=========================================================================
  
  submit 

=========================================================================*/
  @Override
  protected void submit()
    {
    ccMode = ConfigChangeListener.CCMODE_NOUPDATE; 
    error = false;
    boolean newUrlbarSearchEnabled = urlbarSearchEnabled.isSelected();
    if (newUrlbarSearchEnabled != oldUrlbarSearchEnabled)
      config.setUrlbarSearchEnabled (newUrlbarSearchEnabled);

    String newUrlbarSearchUrl = urlbarSearchUrl.getText();
    if (newUrlbarSearchUrl.length() == 0)
      newUrlbarSearchUrl = oldUrlbarSearchUrl;
    if (!newUrlbarSearchUrl.equals (oldUrlbarSearchUrl))
      {
      config.setUrlbarSearchUrl (newUrlbarSearchUrl);
      }
    }
  }






