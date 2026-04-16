/*=========================================================================
  
  JGemini

  ThemeSettingsPane 

  Copyright (c)2021 Kevin Boone, GPLv3.0 

=========================================================================*/
package me.kevinboone.jgemini.swing;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.*;
import java.util.*;
import java.awt.event.*;
import me.kevinboone.jgemini.base.*;
import me.kevinboone.jgemini.Constants;

/** Implements the Theme tab of the Settings dialog. */
public class ThemeSettingsPane extends SettingsPane
  {
  private JList themeList;
  private JTextField filenameField;
  private String cssFilename;
  private String theme;
  private String oldTheme;

/*=========================================================================
  
  Constructor

=========================================================================*/
  protected ThemeSettingsPane()
    {
    super ("theme_settings_pane");

    setLayout(new BorderLayout());
    int iconSize = config.getIconSize();

    themeList = new JList<String>();
    themeList.setVisibleRowCount (5);
    themeList.setSelectionMode (ListSelectionModel.SINGLE_SELECTION);
    themeList.setBorder (BorderFactory.createTitledBorder
      (dialogsBundle.getString ("themesettingspane_built_in_themes")));
    Vector<String> v = new Vector<String>();
    v.add ("light");
    v.add ("light_pastel");
    v.add ("dark");
    v.add ("dark_pastel");
    v.add ("retro");
    v.add ("custom");
    themeList.setListData (v);
    oldTheme = config.getTheme();
    themeList.setSelectedValue (oldTheme, true);

    java.net.URL fileUrl = getClass().getResource("/images/folder.png");
    ImageIcon fileIcon = new ImageIcon (fileUrl);
    fileIcon = new ImageIcon (fileIcon.getImage().getScaledInstance 
      (iconSize, iconSize, Image.SCALE_DEFAULT));
    JButton fileButton = new JButton(fileIcon);

    filenameField = new JTextField(20);
    
    JPanel myself = this;
    fileButton.addActionListener (new ActionListener()
      {
      @Override
      public void actionPerformed (ActionEvent e) 
	{
	JFileChooser chooser = new JFileChooser();
	FileNameExtensionFilter filter = new FileNameExtensionFilter
	  ("CSS files", "css");
	chooser.setFileFilter (filter);
	int returnVal = chooser.showOpenDialog (myself);
	if (returnVal == JFileChooser.APPROVE_OPTION) 
	  {
	  filenameField.setText (chooser.getSelectedFile().getPath());  
	  themeList.setSelectedValue ("custom", true);
	  }
	}
      });

    JPanel filenameAndButtonPanel = new JPanel();
    JLabel filenameLabel = createLabel ("themesettingspane_custom_css");
    filenameLabel.setLabelFor (filenameField);
    filenameAndButtonPanel.add (filenameLabel);
    filenameAndButtonPanel.add (filenameField);
    filenameAndButtonPanel.add (fileButton);


    add (BorderLayout.CENTER, themeList);
    add (BorderLayout.SOUTH, filenameAndButtonPanel);
    }

/*=========================================================================
  
  submit 

=========================================================================*/
  @Override
  protected void submit()
    {
    ccMode = ConfigChangeListener.CCMODE_NOUPDATE; 
    this.error = false;

    theme = (String)themeList.getSelectedValue();
    if (theme.equals (oldTheme)) return;

    if ("custom".equals (theme))
      {
      cssFilename = filenameField.getText();
      if (cssFilename.length() == 0)
	{
	String message = dialogsBundle.getString 
          ("themesettingspane_custom_wo_css"); 
	DialogHelper.errorDialog (this, null, message);
        error = true;
	return;
	}
      if (!(new File (cssFilename)).exists())
	{
	String message = dialogsBundle.getString 
          ("themesettingspane_custom_no_css"); 
	DialogHelper.errorDialog (this, null, message);
        error = true;
	return;
	}
      config.setCustomCssFile (cssFilename);
      config.setTheme (theme);
      // Redraw the HTML window, but don't reload page
      ccMode = ConfigChangeListener.CCMODE_REFRESH; 
      return; 
      }
    else
      {
      config.setTheme (theme);
      ccMode = ConfigChangeListener.CCMODE_REFRESH; 
      }
    }
  }

