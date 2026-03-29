/*=========================================================================
  
  JGemini

  TextEntryDialog 

  A text entry dialog specifically for entering text that will be
  encoded into the 'query' part of a URL. The caller must set the maximum
  size of the response _in bytes_, since the Gemini specification 
  stipulates a query size in bytes.

  Copyright (c)2021 Kevin Boone, GPLv3.0 

=========================================================================*/
package me.kevinboone.jgemini.swing;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.URL;
import java.net.URLEncoder;
import me.kevinboone.jgemini.base.*;

public class TextEntryDialog extends JDialog
{
private String input;
private JTextArea textArea;
private JLabel countLabel;
private int maxInputBytes;

public TextEntryDialog (JFrame parent, int maxInputBytes)
  {
  super (parent, Strings.APP_NAME, Dialog.ModalityType.DOCUMENT_MODAL);

  input = null;	
  this.maxInputBytes = maxInputBytes;
  setLayout(new BorderLayout());
 
  textArea = new JTextArea (8, 40);
  textArea.setBorder(BorderFactory.createTitledBorder(Strings.ENTER_TEXT));
  textArea.setLineWrap (true); 
  textArea.setWrapStyleWord (true); 
  textArea.addKeyListener(new KeyAdapter() 
    {
    @Override
    public void keyPressed(KeyEvent e) 
      {
      checkLength();
      }
    });

  add (new JScrollPane(textArea), BorderLayout.CENTER); 

  JButton submitButton = new JButton ("Submit [ctrl+S]");
  JButton cancelButton = new JButton ("Cancel [esc]");

  cancelButton.addActionListener (new ActionListener() 
    {
    @Override
    public void actionPerformed (ActionEvent e) {
        dispose(); 
    }});

  submitButton.addActionListener (new ActionListener() 
    {
    @Override
    public void actionPerformed (ActionEvent e) {
        input = textArea.getText();
        if (checkLength())
          dispose();
    }});

  Action performCancel = new AbstractAction ("Cancel") 
    {  
    public void actionPerformed(ActionEvent e) 
      {     
      dispose();
      }
    };

  Action performSubmit = new AbstractAction ("Submit") 
    {  
    public void actionPerformed(ActionEvent e) 
      {     
      input = textArea.getText();
      if (checkLength())
        dispose();
      }
    };

  countLabel = new JLabel ("");

  JPanel buttonPanel = new JPanel();
  buttonPanel.add (countLabel);
  buttonPanel.add (new JSeparator (SwingConstants.VERTICAL));
  buttonPanel.add (cancelButton);
  buttonPanel.add (submitButton);

  add (buttonPanel, BorderLayout.SOUTH);

  KeyStroke keyCancel = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
  cancelButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put (keyCancel, "performCancel"); 
  cancelButton.getActionMap().put ("performCancel", performCancel);

  KeyStroke keySubmit = KeyStroke.getKeyStroke(KeyEvent.VK_S, Event.CTRL_MASK);
  submitButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put (keySubmit, "performSubmit"); 
  submitButton.getActionMap().put ("performSubmit", performSubmit);

  checkLength();

  pack();
  setResizable (true); 
  setLocationRelativeTo (parent);
  }

/** Input will be null if the user cancelled. It the user entered no text but clicked
    Submit, input will be an empty String. */
public String getInput()
  {
  return input;
  }

public boolean checkLength()
  {
  String prefix = "http://foo.bar/?";
  String s = textArea.getText();
  try
    {
    URL url = new URL (prefix + URLEncoder.encode (s));
    String sURL = url.toString().replace ("+", "%20");
    int len = sURL.getBytes().length - prefix.getBytes().length;
    countLabel.setText ("" + (maxInputBytes - len));
    return len < maxInputBytes;
    } 
  catch (Exception e) 
    {
    return false;
    } // Should never happen, since we're making the URL
 
  }
}


