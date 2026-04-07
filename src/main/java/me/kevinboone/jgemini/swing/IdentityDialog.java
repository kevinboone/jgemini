/*=========================================================================
  
  JGemini

  IdentityDialog 

  Copyright (c)2021 Kevin Boone, GPLv3.0 

=========================================================================*/
package me.kevinboone.jgemini.swing;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.*;
import java.awt.event.*;
import java.net.URL;
import java.net.URLEncoder;
import me.kevinboone.jgemini.Constants;
import me.kevinboone.jgemini.base.*;
import me.kevinboone.jgemini.ssl.*;

public class IdentityDialog extends JGeminiDialog
{
private String hostname;
private URL url;
private JList<String> identList;
private JButton newButton;
private JPopupMenu newMenu;
private ClientCertManager clientCertManager = 
  DefaultClientCertManager.getInstance();

private MainWindow mainWindow;

private final static ResourceBundle messagesBundle = 
    ResourceBundle.getBundle ("me.kevinboone.jgemini.bundles.Messages");
private final static ResourceBundle captionsBundle = 
    ResourceBundle.getBundle ("me.kevinboone.jgemini.bundles.Captions");
private final static ResourceBundle dialogsBundle = 
    ResourceBundle.getBundle ("me.kevinboone.jgemini.bundles.Dialogs");
private final static String caption = captionsBundle.getString ("set_identity");

private final static String unassignedText = "<" 
    + dialogsBundle.getString ("identitydialog_unassigned") + ">";
private final static String noneText = "<" 
    + dialogsBundle.getString ("identitydialog_none") + ">";

/*=========================================================================
  
  constructor

=========================================================================*/
public IdentityDialog (JFrame parent, URL url, MainWindow mainWindow) 
  {
  super (parent, caption, Dialog.ModalityType.DOCUMENT_MODAL);

  this.url = url;
  this.hostname = url.getHost();
  this.mainWindow = mainWindow;

  java.net.URL iconUrl = getClass().getResource("/images/person_128.png");
  ImageIcon icon = new ImageIcon (iconUrl);
  JLabel iconLabel = new JLabel (icon);

  identList = new JList<String>();
  identList.setVisibleRowCount (10);
  identList.setBorder (BorderFactory.createTitledBorder 
    (dialogsBundle.getString("identitydialog_identity_names")));
  identList.setSelectionMode (ListSelectionModel.SINGLE_SELECTION);

  JPanel outerPanel = new JPanel();
  outerPanel.add (iconLabel, BorderLayout.EAST);

  JPanel centerPanel = new JPanel();
  centerPanel.setBorder(new EmptyBorder (20, 20, 20, 20));
  centerPanel.setLayout (new BorderLayout());
  JLabel hostnameLabel = new JLabel (dialogsBundle.getString 
    ("identitydialog_set_identity_for") + " " + hostname);
  hostnameLabel.setBorder(new EmptyBorder (10, 10, 10, 10));
  centerPanel.add (BorderLayout.NORTH, hostnameLabel);
  
  centerPanel.add (identList, BorderLayout.CENTER);

  outerPanel.add (centerPanel, BorderLayout.CENTER);

  newButton = createButton ("identitydialog_new");
  JButton submitButton = createButton ("identitydialog_submit");
  JButton cancelButton = createButton ("identitydialog_cancel");

  cancelButton.addActionListener (new ActionListener() 
    {
    @Override
    public void actionPerformed (ActionEvent e) {
        dispose(); 
    }});

  newButton.addActionListener (new ActionListener() 
    {
    @Override
    public void actionPerformed (ActionEvent e) {
        handleNew(); 
    }});

  submitButton.addActionListener (new ActionListener() 
    {
    @Override
    public void actionPerformed (ActionEvent e) {
          handleSubmit();
    }});

/*
  Action performNew = new AbstractAction ("New") 
    {  
    public void actionPerformed(ActionEvent e) 
      {     
      handleNew();
      }
    };
*/

  Action performCancel = new AbstractAction ("Cancel") 
    {  
    public void actionPerformed(ActionEvent e) 
      {     
      dispose();
      }
    };

  JButton docsButton = createButton ("identitydialog_help"); 
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
  buttonPanel.add (newButton);
  buttonPanel.add (cancelButton);
  buttonPanel.add (submitButton);

  add (outerPanel, BorderLayout.CENTER);
  add (buttonPanel, BorderLayout.SOUTH);

  KeyStroke keyCancel = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
  cancelButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put (keyCancel, "performCancel"); 
  cancelButton.getActionMap().put ("performCancel", performCancel);

  newMenu = new JPopupMenu();
  JMenuItem attachExistingKeystore = new JMenuItem 
    (dialogsBundle.getString ("identitydialog_attach_keystore"));

  attachExistingKeystore.addActionListener (new ActionListener()
    {
    @Override
    public void actionPerformed (ActionEvent e) 
      {
      handleAttachIdentityToExistingKeystore(); 
      }
    });

  JMenuItem createNewKeystore = new JMenuItem 
    (dialogsBundle.getString ("identitydialog_create_new_keystore"));

  createNewKeystore.addActionListener (new ActionListener()
    {
    @Override
    public void actionPerformed (ActionEvent e) 
      {
      handleCreateNewKeystore(); 
      }
    });

  newMenu.add (attachExistingKeystore);
  newMenu.add (createNewKeystore);

  populateIdentList();

  pack();
  setResizable (true); 
  setLocationRelativeTo (parent);
  }

/*=========================================================================
  
  handleDocs

=========================================================================*/
private void handleDocs()
  {
  mainWindow.newWindow (Constants.DOC_SET_IDENT_DIALOG, 
    "Set identity dialog");
  }

/*=========================================================================
  
  handleAttachIdentityToExistingKeystore

=========================================================================*/
private void handleAttachIdentityToExistingKeystore()
  {
  AttachIdentityDialog id = new AttachIdentityDialog (this, mainWindow);
  id.show();
  populateIdentList();
  }

/*=========================================================================
  
  handleCreateNewKeystore

=========================================================================*/
private void handleCreateNewKeystore()
  {
  NewIdentityDialog id = new NewIdentityDialog (this, mainWindow);
  id.show();
  populateIdentList();
  }

/*=========================================================================
  
  handleNew

=========================================================================*/
private void handleNew()
  {
  newMenu.show (newButton, 0, newButton.getHeight()); 
  }

/*=========================================================================
  
  handleSubmit 

=========================================================================*/
private void handleSubmit()
  {
  String sel = (String)identList.getSelectedValue();
  if (sel != null)
    {
    if (sel.equals (unassignedText))
      {
      clientCertManager.removeIdentForURL (url);
      }
    else if (sel.equals (noneText))
      {
      clientCertManager.setNoneIdentForURL (url);
      }
    else
      {
      clientCertManager.setIdentForURL (url, sel);
      }
    dispose();
    }
  }

/*=========================================================================
  
  populateIdentList 

=========================================================================*/
private void populateIdentList()
  {
  String currentIdent = clientCertManager.getIdentForURL (url);
  Vector<String> v = new Vector<String>();
  v.add (unassignedText);
  v.add (noneText);
  v.addAll (clientCertManager.getIdents());
  identList.setListData (v);
  if (currentIdent != null)
    identList.setSelectedValue (currentIdent, true);
  else
    identList.setSelectedValue (unassignedText, true);
  }

}



