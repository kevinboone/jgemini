/*=========================================================================
  
  JGemini

  SettingsDialog 

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

/** Implements the Settings dialog. This is a hugely complicated chunk
    of code, so I've split each tab of the dialog into its own class,
    like SearchSettingsPane. When the user clicks Submit, this class
    will enumerate all the individual tabs, and check whether its
    safe to close the dialog. It will also find out from the tabs
    what kind of action needs to be taken -- refresh the screen,
    reload settings, etc. */
public class SettingsDialog extends JGeminiDialog
{
private boolean didChange = false;
private Config config = Config.getConfig();
private MainWindow mainWindow;
private JTabbedPane tabbedPane;

private final static ResourceBundle captionsBundle = 
    ResourceBundle.getBundle ("me.kevinboone.jgemini.bundles.Captions");
private final static ResourceBundle dialogsBundle = 
    ResourceBundle.getBundle ("me.kevinboone.jgemini.bundles.Dialogs");
private final static String caption = captionsBundle.getString ("settings");

/*=========================================================================
  
  Constructor 

=========================================================================*/
public SettingsDialog (MainWindow mainWindow)
  {
  super (mainWindow, caption, Dialog.ModalityType.DOCUMENT_MODAL);
  
  this.mainWindow = mainWindow;
  this.didChange = false;

  tabbedPane = new JTabbedPane();
  tabbedPane.setBorder (BorderFactory.createEmptyBorder (10, 10, 10, 10));

  AppearanceSettingsPane appearanceSettingsPane 
    = new AppearanceSettingsPane (mainWindow);
  tabbedPane.addTab (appearanceSettingsPane.getTabName(), null, 
     appearanceSettingsPane, appearanceSettingsPane.getTabName());
  tabbedPane.setMnemonicAt (0, appearanceSettingsPane.getMnemonic());

  ThemeSettingsPane themeSettingsPane = new ThemeSettingsPane ();
  tabbedPane.addTab (themeSettingsPane.getTabName(), null, 
     themeSettingsPane, themeSettingsPane.getTabName());
  tabbedPane.setMnemonicAt (1, themeSettingsPane.getMnemonic());

  HomeSettingsPane homeSettingsPane = new HomeSettingsPane (mainWindow);
  tabbedPane.addTab (homeSettingsPane.getTabName(), null, 
     homeSettingsPane, homeSettingsPane.getTabName());
  tabbedPane.setMnemonicAt (2, homeSettingsPane.getMnemonic());

  HistorySettingsPane historySettingsPane = new HistorySettingsPane (mainWindow);
  tabbedPane.addTab (historySettingsPane.getTabName(), null, 
     historySettingsPane, historySettingsPane.getTabName());
  tabbedPane.setMnemonicAt (3, historySettingsPane.getMnemonic());

  ImagesSettingsPane imagesSettingsPane = new ImagesSettingsPane (mainWindow);
  tabbedPane.addTab (imagesSettingsPane.getTabName(), null, 
     imagesSettingsPane, imagesSettingsPane.getTabName());
  tabbedPane.setMnemonicAt (4, imagesSettingsPane.getMnemonic());

  SearchSettingsPane searchSettingsPane = new SearchSettingsPane (mainWindow);
  tabbedPane.addTab (searchSettingsPane.getTabName(), null, 
     searchSettingsPane, searchSettingsPane.getTabName());
  tabbedPane.setMnemonicAt (5, searchSettingsPane.getMnemonic());

  MediaSettingsPane mediaSettingsPane = new MediaSettingsPane (mainWindow);
  tabbedPane.addTab (mediaSettingsPane.getTabName(), null, 
     mediaSettingsPane, mediaSettingsPane.getTabName());
  tabbedPane.setMnemonicAt (6, mediaSettingsPane.getMnemonic());

  JButton docsButton = createButton ("settingsdialog_docs"); 
  JButton submitButton = createButton ("settingsdialog_submit");
  JButton cancelButton = createButton ("settingsdialog_close");

  cancelButton.addActionListener (new ActionListener() 
    {
    @Override
    public void actionPerformed (ActionEvent e) 
      {
      didChange = false;
      dispose(); 
      }
    });

  docsButton.addActionListener (new ActionListener() 
    {
    @Override
    public void actionPerformed (ActionEvent e) 
      {
      docs();
      }
    });

  submitButton.addActionListener (new ActionListener() 
    {
    @Override
    public void actionPerformed (ActionEvent e) 
      {
      submit();
      }
    });

  Action performCancel = new AbstractAction ("Cancel") 
    {  
    public void actionPerformed(ActionEvent e) 
      {     
      didChange = false;
      dispose();
      }
    };

  setLayout(new BorderLayout());
 
  JPanel buttonPanel = new JPanel();
  buttonPanel.add (docsButton);
  buttonPanel.add (cancelButton);
  buttonPanel.add (submitButton);

  add (tabbedPane, BorderLayout.CENTER);
  add (buttonPanel, BorderLayout.SOUTH);

  KeyStroke keyCancel = KeyStroke.getKeyStroke (KeyEvent.VK_ESCAPE, 0);
  cancelButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put  
    (keyCancel, "performCancel"); 
  cancelButton.getActionMap().put ("performCancel", performCancel);

  pack();
  setResizable (true); 
  setLocationRelativeTo (mainWindow);
  }

/*=========================================================================
  
  didChange

=========================================================================*/
/** Returns true if anything changed in any tab. 
*/
public boolean didChange() { return didChange; }

/*=========================================================================
  
  handleDocs

=========================================================================*/
private void docs()
  {
  mainWindow.newWindow (Constants.DOC_SETTINGS_DIALOG,
    "Settings dialog"); // Caption will not show when loaded
  }

/*=========================================================================
  
  submit 

=========================================================================*/
private void submit()
  {
  int l = tabbedPane.getTabCount();
  boolean error = false;
  int ccMode = ConfigChangeListener.CCMODE_NOUPDATE; 

  for (int i = 0; i < l && !error; i++)
    {
    SettingsPane settingsPane = (SettingsPane)tabbedPane.getComponentAt(i); 
    settingsPane.submit();
    if (settingsPane.error)
      {
      tabbedPane.setSelectedIndex (i);
      error = true;
      }
    else if (settingsPane.ccMode > ccMode) 
      {
      ccMode = settingsPane.ccMode;
      }
    }

  if (!error)
    config.save();

  if (ccMode > ConfigChangeListener.CCMODE_NOUPDATE)
    {
    config.fireSettingsChangedListeners (ccMode); 
    }

  if (!error)
    dispose();
  }

}





