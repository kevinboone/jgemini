/*=========================================================================
  
  JGemini

  DialogHelper 

  Copyright (c)2021-6 Kevin Boone, GPLv3.0 

=========================================================================*/
package me.kevinboone.jgemini.swing;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.text.*;
import javax.swing.border.EmptyBorder;
import java.net.*;
import java.io.*;
import java.util.*;
import me.kevinboone.jgemini.Constants;
import me.kevinboone.jgemini.base.*;

/** A helper class for raising error and information dialogs, to reduce
    the duplication of code each of the many times this happens in the
    rest of the application. */
public class DialogHelper 
  {
/*=========================================================================
  
  errorDialog

=========================================================================*/
  public static void errorDialog (Container parent, 
      String url, String message)
    {
    StringBuffer sb = new StringBuffer();
    // Be aware that a URL could, in theory, be > 1000 characters long.
    // We don't want to put all those into the dialog box.
    if (url != null)
      {
      if (url.length() > 50)
        url = url.substring (0, 20) + "...";
      sb.append (url);
      sb.append ("\n\n");
      }

    sb.append (message);

    JTextArea textArea = new JTextArea (message);
    textArea.setWrapStyleWord (true);
    textArea.setLineWrap (true);
    textArea.setEditable (false);
    textArea.setRows (Constants.DIALOG_ROWS);
    textArea.setColumns (Constants.DIALOG_COLS);

    textArea.setBorder(new EmptyBorder (20, 20, 20, 20));

    JScrollPane scrollPane = new JScrollPane (textArea);

    JOptionPane.showMessageDialog (parent, scrollPane, 
         Constants.APP_NAME, JOptionPane.ERROR_MESSAGE); 
    }

/*=========================================================================
  
  exceptionDialog

=========================================================================*/
  public static void exceptionDialog (Container parent, 
      String url, Exception e)
    {
    errorDialog (parent, url, e.getMessage());
    // TODO -- something useful with the exception
    }

/*=========================================================================
  
  infoDialog

=========================================================================*/
  public static void infoDialog (Window parent, String url, String message)
    {
    StringBuffer sb = new StringBuffer();
    // Be aware that a URL could, in theory, be > 1000 characters long.
    // We don't want to put all those into the dialog box.
    if (url != null)
      {
      if (url.length() > 50)
        url = url.substring (0, 20) + "...";
      sb.append (url);
      sb.append ("\n\n");
      }

    sb.append (message);

    JTextArea textArea = new JTextArea (message);
    textArea.setWrapStyleWord (true);
    textArea.setLineWrap (true);
    textArea.setEditable (false);
    textArea.setRows (Constants.DIALOG_ROWS);
    textArea.setColumns (Constants.DIALOG_COLS);

    textArea.setBorder(new EmptyBorder (20, 20, 20, 20));

    JScrollPane scrollPane = new JScrollPane (textArea);

    JOptionPane.showMessageDialog (parent, scrollPane, 
         Constants.APP_NAME, JOptionPane.INFORMATION_MESSAGE); 
    }
  }

