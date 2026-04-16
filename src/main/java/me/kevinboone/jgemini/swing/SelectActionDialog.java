/*=========================================================================
  
  JGemini

  SelectActionDialog 

  Copyright (c)2021 Kevin Boone, GPLv3.0 

=========================================================================*/
package me.kevinboone.jgemini.swing;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.*;
import java.awt.event.*;
import java.net.URL;
import me.kevinboone.jgemini.Constants;
import me.kevinboone.jgemini.base.*;

/** Implements the Select action dialog, which is raised when the
    user follows a link to a type of content we don't handle. */
public class SelectActionDialog extends JGeminiDialog
{
private MainWindow mainWindow;

private final static ResourceBundle messagesBundle = 
    ResourceBundle.getBundle ("me.kevinboone.jgemini.bundles.Messages");
private final static ResourceBundle captionsBundle = 
    ResourceBundle.getBundle ("me.kevinboone.jgemini.bundles.Captions");
private final static ResourceBundle dialogsBundle = 
    ResourceBundle.getBundle ("me.kevinboone.jgemini.bundles.Dialogs");
private final static String caption = captionsBundle.getString ("select_action");

private final static String unassignedText = "<" 
    + dialogsBundle.getString ("identitydialog_unassigned") + ">";
private final static String noneText = "<" 
    + dialogsBundle.getString ("identitydialog_none") + ">";

private boolean alwaysThisWay;
private JCheckBox always;
private JTextField appFilename;
private JRadioButton choicePromptSaveButton; 
private JRadioButton choiceSaveButton; 
private JRadioButton choiceDesktopButton; 
private JRadioButton choiceStreamButton; 

private int action = ContentHandlerAction.CHA_NONE; 

/*=========================================================================
  
  constructor

=========================================================================*/
public SelectActionDialog (MainWindow mainWindow, String contentType) 
  {
  super (mainWindow, caption, Dialog.ModalityType.DOCUMENT_MODAL);

  this.mainWindow = mainWindow;

  Box choicesBox = new Box (BoxLayout.Y_AXIS);
  choicesBox.setAlignmentX (Component.LEFT_ALIGNMENT);
  //choicesBox.setBorder (BorderFactory.createTitledBorder
  //  (dialogsBundle.getString("select_action_dialog_choices_title")));
  choicesBox.setBorder (BorderFactory.createEmptyBorder (10, 20, 10, 20));
  choicePromptSaveButton = createRadioButton 
    ("select_action_dialog_choice_prompt_save"); 
  choiceSaveButton = createRadioButton ("select_action_dialog_choice_save");
  choiceStreamButton = createRadioButton 
    ("select_action_dialog_choice_stream");
  choiceDesktopButton = createRadioButton 
    ("select_action_dialog_choice_desktop");
  ButtonGroup choiceGroup = new ButtonGroup();
  choicesBox.add (choiceDesktopButton);
  choicesBox.add (choicePromptSaveButton);
  choicesBox.add (choiceSaveButton);
  choicesBox.add (choiceStreamButton);
  choiceGroup.add (choicePromptSaveButton);
  choiceGroup.add (choiceSaveButton);
  choiceGroup.add (choiceStreamButton);
  choiceGroup.add (choiceDesktopButton);

  JButton docsButton = createButton ("select_action_dialog_docs");
  JButton submitButton = createButton ("select_action_dialog_submit");
  JButton cancelButton = createButton ("select_action_dialog_cancel");

  cancelButton.addActionListener (new ActionListener() 
    {
    @Override
    public void actionPerformed (ActionEvent e) 
      {
      action = -1;
      dispose(); 
      }
    });

  submitButton.addActionListener (new ActionListener() 
    {
    @Override
    public void actionPerformed (ActionEvent e) 
      {
      handleSubmit();
      }
    });

  Action performCancel = new AbstractAction ("Cancel") 
    {  
    public void actionPerformed(ActionEvent e) 
      {     
      dispose();
      }
    };

  docsButton.addActionListener (new ActionListener() 
    {
    @Override
    public void actionPerformed (ActionEvent e) 
      {
      handleDocs();
      }
    });

  docsButton.setMnemonic (KeyEvent.VK_H);

  JPanel buttonPanel = new JPanel();
  buttonPanel.add (docsButton);
  buttonPanel.add (cancelButton);
  buttonPanel.add (submitButton);

  Box titleBox = new Box (BoxLayout.Y_AXIS);
  titleBox.setBorder (BorderFactory.createEmptyBorder (10, 20, 10, 20));
  titleBox.setAlignmentX (Component.LEFT_ALIGNMENT);
  JLabel contentLabel = new JLabel (contentType);
  Font f = contentLabel.getFont();
  contentLabel.setFont (f.deriveFont(f.getStyle() | Font.BOLD));
  JLabel titleLabel = new JLabel 
    (dialogsBundle.getString ("select_action_dialog_how_question"));
  titleBox.add (contentLabel);
  titleBox.add (titleLabel);

  JLabel alwaysLabel = createLabel ("select_action_dialog_always");
  always = new JCheckBox();
  alwaysLabel.setLabelFor (always); 

  JPanel alwaysPanel = new JPanel();
  alwaysPanel.setAlignmentX (Component.LEFT_ALIGNMENT);
  alwaysPanel.add (alwaysLabel);
  alwaysPanel.add (always);

  Box outerBox = new Box (BoxLayout.Y_AXIS);
  outerBox.add (titleBox);
  outerBox.add (choicesBox);
  outerBox.add (alwaysPanel);

  add (outerBox, BorderLayout.CENTER);
  add (buttonPanel, BorderLayout.SOUTH);

  KeyStroke keyCancel = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
  cancelButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put (keyCancel, "performCancel"); 
  cancelButton.getActionMap().put ("performCancel", performCancel);

  choiceDesktopButton.setSelected (true);
  always.setSelected (true);

  pack();
  setResizable (true); 
  setLocationRelativeTo (mainWindow);
  }

/*=========================================================================
  
  getAction 

=========================================================================*/
/* Return the action chosen by the user, as one of the
   ContentHandlerAction constants. The return will be -1 if the user
   hit Escape without choosing an action. 
*/
public int getAction()
  {
  return action;
  }

/*=========================================================================
  
  getAlways

=========================================================================*/
/** Returns true if the user selected the checkbox to apply the
    same action always. */
public boolean getAlways()
  {
  return alwaysThisWay;
  }

/*=========================================================================
  
  handleDocs

=========================================================================*/
private void handleDocs()
  {
  mainWindow.newWindow (Constants.DOC_SELECT_ACTION_DIALOG, 
    "Select action dialog"); // Not seen
  }

/*=========================================================================
  
  handleSubmit 

=========================================================================*/
private void handleSubmit()
  {
  if (choicePromptSaveButton.isSelected())
    {
    action = ContentHandlerAction.CHA_PROMPTSAVE; 
    }
  else if (choiceStreamButton.isSelected())
    {
    action = ContentHandlerAction.CHA_STREAM; 
    }
  else if (choiceDesktopButton.isSelected())
    {
    action = ContentHandlerAction.CHA_DESKTOP; 
    }
  else
    action = ContentHandlerAction.CHA_SAVE; 

  alwaysThisWay = always.isSelected();

  dispose();
  }

}




