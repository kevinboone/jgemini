/*=========================================================================
  
  JGemini

  HistorySettingsPane 

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

/** Implements the History tab of the Settings dialog. 
*/
public class HistorySettingsPane extends SettingsPane
  {
  private MainWindow mainWindow;
  private JCheckBox enableHistory;
  private JTextField historySize;
  private boolean oldHistoryEnabled;
  private int oldHistorySize;

/*=========================================================================
  
  Constructor

=========================================================================*/
  protected HistorySettingsPane (MainWindow mainWindow)
    {
    super ("history_settings_pane");

    this.mainWindow = mainWindow;

    oldHistoryEnabled = config.getHistoryEnabled();
    oldHistorySize = config.getHistorySize();

    GridBagLayout gl = new GridBagLayout ();
    setLayout (gl);
    setBorder(new EmptyBorder (10, 10, 10, 10));

    // Row 0, col 0
    GridBagConstraints gbc00 = new GridBagConstraints();
    gbc00.insets = new Insets (5, 5, 5, 5);
    gbc00.gridy = 0;
    gbc00.gridx = 0;
    gbc00.anchor = gbc00.EAST;
    JLabel enableHistoryLabel = createLabel ("history_settings_pane_enable");
    add (enableHistoryLabel, gbc00);

    // Row 0, col 1
    GridBagConstraints gbc01 = new GridBagConstraints();
    gbc01.insets = new Insets (5, 5, 5, 5);
    gbc01.gridy = 0;
    gbc01.gridx = 1;
    gbc01.anchor = gbc01.WEST;
    enableHistory = new JCheckBox();
    enableHistoryLabel.setLabelFor (enableHistory); 
    add (enableHistory, gbc01);

    enableHistory.setSelected (oldHistoryEnabled);

    // Row 1, col 0
    GridBagConstraints gbc10 = new GridBagConstraints();
    gbc10.insets = new Insets (5, 5, 5, 5);
    gbc10.gridy = 1;
    gbc10.gridx = 0;
    gbc10.anchor = gbc10.EAST;
    JLabel historySizeLabel = createLabel 
      ("history_settings_pane_history_size");
    historySizeLabel.setLabelFor (historySize); 
    add (historySizeLabel, gbc10);

    // Row 1, col 1
    GridBagConstraints gbc11 = new GridBagConstraints();
    gbc11.insets = new Insets (5, 5, 5, 5);
    gbc11.gridy = 1;
    gbc11.gridx = 1;
    historySize = new JTextField(5);
    enableHistoryLabel.setLabelFor (historySize); 
    add (historySize, gbc11);

    // Row 2, col 0
    GridBagConstraints gbc20 = new GridBagConstraints();
    gbc20.insets = new Insets (5, 5, 5, 5);
    gbc20.gridy = 2;
    gbc20.gridx = 0;
    JButton clearHistory = createButton ("history_settings_pane_clear_history"); 
    add (clearHistory, gbc20);

    // Set digits only
    historySize.setDocument (new PlainDocument() 
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

    historySize.setText ("" + config.getHistorySize());

    clearHistory.addActionListener (new ActionListener()
      {
      @Override
      public void actionPerformed (ActionEvent e) 
	{
        mainWindow.clearHistory();
	}
      });

    }

/*=========================================================================
  
  submit 

=========================================================================*/
  @Override
  protected void submit()
    {
    ccMode = ConfigChangeListener.CCMODE_NOUPDATE; 
    error = false;

    boolean newHistoryEnabled = enableHistory.isSelected();
    int newHistorySize; 
    try
      {
      newHistorySize = Integer.parseInt (historySize.getText());
      }
    catch (NumberFormatException e)
      {
      newHistorySize = oldHistorySize; // Ignore crappy changes
      }

    if (newHistoryEnabled != oldHistoryEnabled)
      {
      if (oldHistoryEnabled)
        {
       String message = dialogsBundle.getString 
          ("history_settings_pane_confirm_delete_history");
        if (JOptionPane.showConfirmDialog (this, message, Constants.APP_NAME, 
            JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) 
              == JOptionPane.YES_OPTION)
          {
          mainWindow.clearHistory();
          }
        }
      config.setHistoryEnabled (newHistoryEnabled);
      config.save();
      }

    if (newHistorySize != oldHistorySize)
      {
      config.setHistorySize (newHistorySize);
      config.save();
      }
    // Leave ccMode at 'do nothing'. No change we make here requires
    //   anything in the rest of the program to be updated
    ccMode = ConfigChangeListener.CCMODE_NOUPDATE; 
    }
  }



