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

public class EditFileDialog extends JDialog
{
private JTextArea textArea;
private File file;
private boolean didSave = false;
private Config config = Config.getConfig();

public EditFileDialog (JFrame parent, String caption, 
          String filename) throws IOException
  {
  super (parent, Strings.APP_NAME, Dialog.ModalityType.DOCUMENT_MODAL);

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

  JButton saveButton = new JButton ("Save [ctrl+S]");
  JButton cancelButton = new JButton ("Cancel [esc]");

  cancelButton.addActionListener (new ActionListener() 
    {
    @Override
    public void actionPerformed (ActionEvent e) 
      {
      didSave = false;
      dispose(); 
      }});

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

  Action performSubmit = new AbstractAction ("Submit") 
    {  
    public void actionPerformed(ActionEvent e) 
      {     
      save();
      }
    };

  JPanel buttonPanel = new JPanel();
  buttonPanel.add (cancelButton);
  buttonPanel.add (saveButton);

  add (buttonPanel, BorderLayout.SOUTH);

  KeyStroke keyCancel = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
  cancelButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put (keyCancel, "performCancel"); 
  cancelButton.getActionMap().put ("performCancel", performCancel);

  KeyStroke keySubmit = KeyStroke.getKeyStroke(KeyEvent.VK_S, Event.CTRL_MASK);
  saveButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put (keySubmit, "performSubmit"); 
  saveButton.getActionMap().put ("performSubmit", performSubmit);

  pack();
  setResizable (true); 
  setLocationRelativeTo (parent);
  }

public boolean didSave() { return didSave; }

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
    JOptionPane.showMessageDialog (this, e.getMessage(), // TODO -- expand
      Strings.APP_NAME, JOptionPane.ERROR_MESSAGE); 
    }
  }

}



