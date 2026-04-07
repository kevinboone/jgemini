/*=========================================================================
  
  JGemini

  JGeminiDialog 

  Base class for many of the JGemini dialogs. It just contains helper
    functions to simplify the coding of the individual dialogs.

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
import me.kevinboone.jgemini.base.*;
import me.kevinboone.jgemini.ssl.*;

public class JGeminiDialog extends JDialog
  {
  protected final static ResourceBundle dialogsBundle = 
    ResourceBundle.getBundle ("me.kevinboone.jgemini.bundles.Dialogs");

/*=========================================================================
  
  constructor 

=========================================================================*/
  JGeminiDialog (Window parent, String caption, Dialog.ModalityType modality)
    {
    super (parent, caption, modality);
    }

/*=========================================================================
  
  createButton

=========================================================================*/
  JButton createButton (String key)
    {
    String label = dialogsBundle.getString (key);
    JButton button = new JButton (label);
    String mKey = key + "_mnemonic";
    if (dialogsBundle.containsKey (mKey))
      {
      Object o = dialogsBundle.getObject (mKey);
      button.setMnemonic ((int)o);
      }
    return button;
    }

  }
