/*=========================================================================
  
  JGemini

  EditFileDialog 

  Copyright (c)2021 Kevin Boone, GPLv3.0 

=========================================================================*/
package me.kevinboone.jgemini.swing;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.awt.event.*;
import java.net.URL;
import java.net.URLEncoder;
import java.util.stream.Collectors;
import me.kevinboone.jgemini.base.*;
import me.kevinboone.jgemini.Constants;

/** A simple file editor, which JGemini uses to edit the bookmarks file,
    settings file, and perhaps others in future. It's pretty rudimentary,
    but functional for simple changes. */
public class EditFileDialog extends JGeminiDialog
{
private JTextArea textArea;
private File file;
private boolean didSave = false;
private Config config = Config.getConfig();
private String docUrl;

/*=========================================================================
  
  Constructor 

=========================================================================*/
public EditFileDialog (JFrame parent, String caption, 
          String filename, String docUrl) throws IOException
  {
  super (parent, Constants.APP_NAME, Dialog.ModalityType.DOCUMENT_MODAL);
  
  this.docUrl = docUrl;

  file = new File (filename);
  InputStream is = new FileInputStream (file);
  String s = new BufferedReader (new InputStreamReader (is))
        .lines().collect (Collectors.joining("\n"));
  is.close();
  
  setLayout(new BorderLayout());
 
  textArea = new JTextArea (20, 70);
  textArea.setBorder (BorderFactory.createTitledBorder (caption));
  textArea.setLineWrap (true); 
  textArea.setWrapStyleWord (true); 
  textArea.setText (s);
  textArea.setCaretPosition (0);

  add (new JScrollPane(textArea), BorderLayout.CENTER); 

  JButton docsButton = createButton ("editfiledialog_docs"); 
  JButton saveButton = createButton ("editfiledialog_save");
  JButton cancelButton = createButton ("editfiledialog_cancel");

  cancelButton.addActionListener (new ActionListener() 
    {
    @Override
    public void actionPerformed (ActionEvent e) 
      {
      didSave = false;
      dispose(); 
      }});

  docsButton.addActionListener (new ActionListener() 
    {
    @Override
    public void actionPerformed (ActionEvent e) 
      {
      handleDocs();
      }
    });

  saveButton.addActionListener (new ActionListener() 
    {
    @Override
    public void actionPerformed (ActionEvent e) 
      {
      save();
      }});

  Action performCancel = new AbstractAction ("Cancel") 
    {  
    public void actionPerformed(ActionEvent e) 
      {     
      didSave = false;
      dispose();
      }
    };

/*
  Action performSubmit = new AbstractAction ("Submit") 
    {  
    public void actionPerformed(ActionEvent e) 
      {     
      save();
      }
    };
*/

  JPanel buttonPanel = new JPanel();
  buttonPanel.add (docsButton);
  buttonPanel.add (cancelButton);
  buttonPanel.add (saveButton);

  add (buttonPanel, BorderLayout.SOUTH);

  KeyStroke keyCancel = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
  cancelButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put (keyCancel, "performCancel"); 
  cancelButton.getActionMap().put ("performCancel", performCancel);

  pack();
  setResizable (true); 
  setLocationRelativeTo (parent);
  }

/*=========================================================================
  
  didSave 

=========================================================================*/
/** Returns true if the user made a change that needs to be saved.
*/
public boolean didSave() { return didSave; }

/*=========================================================================
  
  handleDocs

=========================================================================*/
private void handleDocs()
  {
  MainWindow.newWindow (docUrl,
    "New identity dialog"); // Caption will not show when loaded
  }

/*=========================================================================
  
  save 

=========================================================================*/
/** Save changes to the file, and set "didSave" so the calling class
    can find out that the user made changes, and take the appropriate
    action (e.g., reloading the bookmarks file.
*/
public void save()
  {
  try
    {
    String s = textArea.getText(); 
    FileOutputStream fos = new FileOutputStream (file);
    PrintWriter pw = new PrintWriter (fos);
    pw.print (s);
    pw.println(); // Not really sure why we need this
    pw.flush();
    fos.close();
    didSave = true;
    dispose();
    }
  catch (Exception e)
    {
    reportGenException (e);
    }
  }

}



