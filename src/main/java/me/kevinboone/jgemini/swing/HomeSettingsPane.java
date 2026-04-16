/*=========================================================================
  
  JGemini

  HomeSettingsPane 

  Copyright (c)2021 Kevin Boone, GPLv3.0 

=========================================================================*/
package me.kevinboone.jgemini.swing;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.*;
import java.util.*;
import java.awt.event.*;
import me.kevinboone.jgemini.base.*;
import me.kevinboone.jgemini.Constants;

/** Implements the Home tab of the Settings dialog.
*/
public class HomeSettingsPane extends SettingsPane
  {
  private JTextField homeTextField;
  private String oldHomePage;
  private MainWindow mainWindow;

/*=========================================================================
  
  Constructor

=========================================================================*/
  protected HomeSettingsPane (MainWindow mainWindow)
    {
    super ("home_settings_pane");

    this.mainWindow = mainWindow;

    setLayout(new BorderLayout(10, 10));
    setBorder (new EmptyBorder (20, 20, 20, 20));

    homeTextField = new JTextField (30);
    oldHomePage = config.getHomePage();
    homeTextField.setText (oldHomePage);
    JLabel homeLabel = createLabel ("home_settings_pane_home");
    homeLabel.setLabelFor (homeTextField);
    homeLabel.setHorizontalAlignment (SwingConstants.RIGHT);
    JPanel homePanel = new JPanel();
    homePanel.setLayout(new BorderLayout(10, 10));
    homePanel.add (BorderLayout.WEST, homeLabel);
    homePanel.add (BorderLayout.CENTER, homeTextField);

    JButton currentButton = createButton ("home_settings_pane_current");
    currentButton.addActionListener (new ActionListener()
      {
      @Override
      public void actionPerformed (ActionEvent e) 
	{
        homeTextField.setText (mainWindow.getCurrentURI().toString());
	}
      });


    homePanel.add (BorderLayout.EAST, currentButton);
  
    add (homePanel, BorderLayout.NORTH);
    }

/*=========================================================================
  
  submit 

=========================================================================*/
  @Override
  protected void submit()
    {
    ccMode = ConfigChangeListener.CCMODE_NOUPDATE; 
    error = false;

    String newHomePage = homeTextField.getText();
    if (newHomePage.length() == 0) 
      {
      String message = dialogsBundle.getString 
         ("home_settings_pane_home_empty"); 
      DialogHelper.errorDialog (this, null, message);
      error = true;
      return;
      }

    if (newHomePage.equals (oldHomePage)) return;

    config.setHomePage (newHomePage);
    // We don't need to update anything in the rest of the program
    ccMode = ConfigChangeListener.CCMODE_NOUPDATE; 
    }
  }


