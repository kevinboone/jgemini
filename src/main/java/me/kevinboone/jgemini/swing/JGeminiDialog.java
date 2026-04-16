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
import me.kevinboone.jgemini.Constants;
import me.kevinboone.jgemini.base.*;
import me.kevinboone.jgemini.ssl.*;

/** A base class for all the dialogs JGemini uses. It exists solely to
    reduce code duplication, by including methods that all dialogs 
    require, such as creating buttons and labels with mnemonics
    and translatable text.
*/
public class JGeminiDialog extends JDialog
  {
  protected final static ResourceBundle dialogsBundle = 
    ResourceBundle.getBundle ("me.kevinboone.jgemini.bundles.Dialogs");

  final String caption;

/*=========================================================================
  
  constructor 

=========================================================================*/
  JGeminiDialog (Window parent, String caption, Dialog.ModalityType modality)
    {
    super (parent, caption, modality);
    this.caption = caption;
    }

/*=========================================================================
  
  createButton

=========================================================================*/
  protected static JButton createButton (String key)
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

/*=========================================================================
  
  createRadioButton

=========================================================================*/
  protected static JRadioButton createRadioButton (String key)
    {
    String label = dialogsBundle.getString (key);
    JRadioButton button = new JRadioButton (label);
    String mKey = key + "_mnemonic";
    if (dialogsBundle.containsKey (mKey))
      {
      Object o = dialogsBundle.getObject (mKey);
      button.setMnemonic ((int)o);
      }
    return button;
    }

/*=========================================================================
  
  createLabel

=========================================================================*/
  protected static JLabel createLabel (String key)
    {
    String label = dialogsBundle.getString (key);
    JLabel l = new JLabel (label);
    String mKey = key + "_mnemonic";
    if (dialogsBundle.containsKey (mKey))
      {
      Object o = dialogsBundle.getObject (mKey);
      l.setDisplayedMnemonic ((int)o);
      }
    return l;
    }

/*=========================================================================
  
  reportGenError

  Do something vaguely useful with error messages

=========================================================================*/
  protected void reportGenError (String message)
    {
    DialogHelper.errorDialog (this, null, message);
    }

/*=========================================================================
  
  reportGenException

  Do something vaguely useful with exceptions. In the longer term,
  we need to make the whole exception text available 

=========================================================================*/
  protected void reportGenException (Exception e)
    {
    reportGenError (e.toString()); // TODO
    }

  }
