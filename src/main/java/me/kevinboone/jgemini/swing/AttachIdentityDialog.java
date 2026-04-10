/*=========================================================================
  
  JGemini

  AttachIdentityDialog 

  Copyright (c)2021 Kevin Boone, GPLv3.0 

=========================================================================*/
package me.kevinboone.jgemini.swing;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.*;
import java.awt.*;
import java.util.*;
import java.awt.event.*;
import java.net.URL;
import java.net.URLEncoder;
import me.kevinboone.jgemini.Constants;
import me.kevinboone.jgemini.base.*;
import me.kevinboone.jgemini.ssl.*;
import me.kevinboone.utils.ssl.*;

public class AttachIdentityDialog extends JGeminiDialog
{
private ClientCertManager clientCertManager = 
  DefaultClientCertManager.getInstance();

private Config config = Config.getConfig();

private JTextField idName;
private JTextField keystore;
private JTextField password;

private MainWindow mainWindow;

private final static ResourceBundle messagesBundle = 
    ResourceBundle.getBundle ("me.kevinboone.jgemini.bundles.Messages");
private final static ResourceBundle captionsBundle = 
    ResourceBundle.getBundle ("me.kevinboone.jgemini.bundles.Captions");
private final static String caption = captionsBundle.getString ("attach_identity");

/*=========================================================================
  
  constructor

=========================================================================*/
public AttachIdentityDialog (Window parent, MainWindow mainWindow) 
  {
  super (parent, caption, Dialog.ModalityType.DOCUMENT_MODAL);

  this.mainWindow = mainWindow;

  java.net.URL iconUrl = getClass().getResource("/images/person_128.png");
  ImageIcon icon = new ImageIcon (iconUrl);
  JLabel iconLabel = new JLabel (icon);

  JPanel mainPanel = new JPanel();
  GridBagLayout gl = new GridBagLayout ();
  mainPanel.setLayout (gl);
  mainPanel.setBorder(new EmptyBorder (10, 10, 10, 10));

  GridBagConstraints gbc11 = new GridBagConstraints();
  gbc11.gridx = 0;
  gbc11.gridy = 0;
  gbc11.anchor = gbc11.EAST;
  JLabel idNameLabel = new JLabel 
    (dialogsBundle.getString ("attachidentitydialog_name") + ":");
  mainPanel.add (idNameLabel, gbc11);
  GridBagConstraints gbc12 = new GridBagConstraints();
  gbc11.gridx = 1;
  gbc11.gridy = 0;
  idName = new JTextField(20);
  mainPanel.add (idName, gbc12);
  idNameLabel.setLabelFor (idName); 
  idNameLabel.setDisplayedMnemonic ((int)dialogsBundle.getObject 
    ("attachidentitydialog_name_mnemonic"));

  GridBagConstraints gbc21 = new GridBagConstraints();
  gbc21.insets = new Insets (5, 5, 5, 5);
  gbc21.gridx = 0;
  gbc21.gridy = 1;
  gbc21.anchor = gbc11.EAST;
  JLabel keystoreLabel = new JLabel 
    (dialogsBundle.getString ("attachidentitydialog_keystore_file") + ":");
  mainPanel.add (keystoreLabel, gbc21);
  keystore = new JTextField(20);
  GridBagConstraints gbc22 = new GridBagConstraints();
  gbc21.insets = new Insets (5, 5, 5, 5);
  gbc21.gridx = 1;
  gbc21.gridy = 1;
  mainPanel.add (keystore, gbc21);
  keystoreLabel.setLabelFor (keystore); 
  keystoreLabel.setDisplayedMnemonic ((int)dialogsBundle.getObject
    ("attachidentitydialog_keystore_file_mnemonic"));
  GridBagConstraints gbc23 = new GridBagConstraints();
  gbc21.insets = new Insets (5, 5, 5, 5);
  gbc23.gridx = 2;
  gbc23.gridy = 1;
  java.net.URL fileUrl = getClass().getResource("/images/folder.png");
  ImageIcon fileIcon = new ImageIcon (fileUrl);
  JButton fileButton = new JButton(fileIcon);
  JDialog myself = this;
  fileButton.addActionListener (new ActionListener()
    {
    @Override
    public void actionPerformed (ActionEvent e) 
      {
      JFileChooser chooser = new JFileChooser();
      FileNameExtensionFilter filter = new FileNameExtensionFilter
        ("Keystores", "p12", "jks");
      chooser.setFileFilter (filter);
      chooser.setCurrentDirectory (new File(config.getIdentsDir()));
      int returnVal = chooser.showOpenDialog (myself);
      if (returnVal == JFileChooser.APPROVE_OPTION) 
        {
        keystore.setText (chooser.getSelectedFile().getPath());  
        }
      }});
  mainPanel.add (fileButton, gbc23);

  GridBagConstraints gbc31 = new GridBagConstraints();
  gbc21.insets = new Insets (5, 5, 5, 5);
  gbc31.gridx = 0;
  gbc31.gridy = 2;
  gbc31.anchor = gbc11.EAST;
  JLabel passwordLabel = new JLabel 
    (dialogsBundle.getString ("attachidentitydialog_keystore_password") + ":");
  mainPanel.add (passwordLabel, gbc31);
  GridBagConstraints gbc32 = new GridBagConstraints();
  gbc21.insets = new Insets (5, 5, 5, 5);
  gbc32.gridx = 1;
  gbc32.gridy = 2;
  password = new JTextField(20);
  mainPanel.add (password, gbc32);
  passwordLabel.setLabelFor (password); 
  passwordLabel.setDisplayedMnemonic ((int)dialogsBundle.getObject 
    ("attachidentitydialog_keystore_password_mnemonic"));

  JPanel outerPanel = new JPanel();
  outerPanel.add (iconLabel, BorderLayout.EAST);
  outerPanel.add (mainPanel, BorderLayout.CENTER);
  
  JButton docsButton = createButton ("attachidentitydialog_help"); 
  JButton submitButton = createButton ("attachidentitydialog_submit"); 
  JButton cancelButton = new JButton ("Cancel");

  cancelButton.addActionListener (new ActionListener() 
    {
    @Override
    public void actionPerformed (ActionEvent e) 
      {
      dispose(); 
      }});

  submitButton.addActionListener (new ActionListener() 
    {
    @Override
    public void actionPerformed (ActionEvent e) 
      {
      handleSubmit();
      }});

  docsButton.addActionListener (new ActionListener() 
    {
    @Override
    public void actionPerformed (ActionEvent e) 
      {
      handleDocs();
      }});

  Action performCancel = new AbstractAction ("Cancel") 
    {  
    public void actionPerformed(ActionEvent e) 
      {     
      dispose();
      }
    };

  JPanel buttonPanel = new JPanel();
  buttonPanel.add (docsButton);
  buttonPanel.add (cancelButton);
  buttonPanel.add (submitButton);

  add (outerPanel, BorderLayout.CENTER);
  add (buttonPanel, BorderLayout.SOUTH);

  KeyStroke keyCancel = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
  cancelButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put (keyCancel, "performCancel"); 
  cancelButton.getActionMap().put ("performCancel", performCancel);

  pack();
  setResizable (true); 
  setLocationRelativeTo (parent);
  }

/*=========================================================================
  
  handleDocs

=========================================================================*/
private void handleDocs()
  {
  mainWindow.newWindow (Constants.DOC_ATTACH_IDENT_DIALOG,
    "Attach identity dialog");
  }

/*=========================================================================
  
  handleSubmit 

=========================================================================*/
private void handleSubmit()
  {
  String name = idName.getText();
  String _keystore = keystore.getText();
  String _password = password.getText();
  try
    { 
    IdentUtil.checkIdentName (name);
    }
  catch (Exception e)
    {
    reportGenError (e.getMessage());
    idName.requestFocus();
    return;
    }
  if (_keystore.length() == 0)
    {
    String keystoreEmpty = dialogsBundle.getString 
      ("attachidentitydialog_keystore_empty");
    reportGenError (keystoreEmpty);
    keystore.requestFocus();
    return;
    }
  if (_password.length() == 0)
    {
    String passwordEmpty = dialogsBundle.getString 
      ("attachidentitydialog_password_empty");
    reportGenError (passwordEmpty);
    password.requestFocus();
    return;
    }

  try
    {
    CertUtil.verifyKeystore (_keystore, _password);
    }
  catch (Exception e)
    {
    reportGenException (e);
    password.requestFocus();
    return;
    }

  KeystoreSpec keystoreSpec = 
    clientCertManager.getKeystoreSpecForIdent (name);

  if (keystoreSpec != null)
    {
    reportGenError (messagesBundle.getString ("identity_exists") + ": " + name);
    idName.requestFocus();
    return ; 
    }

  try
    {
    KeystoreSpec newKeystoreSpec = new KeystoreSpec (_keystore, _password);
    clientCertManager.addIdent (name, newKeystoreSpec);
    dispose();
    }
  catch (Exception e)
    {
    reportGenException (e);
    }
  }
}


