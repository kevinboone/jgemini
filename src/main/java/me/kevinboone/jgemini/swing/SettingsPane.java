/*=========================================================================
  
  JGemini

  SettingsPane 

  Copyright (c)2021 Kevin Boone, GPLv3.0 

=========================================================================*/
package me.kevinboone.jgemini.swing;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.*;
import java.awt.event.*;
import me.kevinboone.jgemini.base.*;
import me.kevinboone.jgemini.Constants;

public abstract class SettingsPane extends JPanel
  {
  protected final static ResourceBundle dialogsBundle = 
    ResourceBundle.getBundle ("me.kevinboone.jgemini.bundles.Dialogs");
  protected String tabKey;
  protected Config config = Config.getConfig();
  protected boolean error = false;
  protected int ccMode = ConfigChangeListener.CCMODE_NOUPDATE;

  /** Submit sets `error` and `ccMode` */
  protected abstract void submit();

/*=========================================================================
  
  Constructor 

=========================================================================*/
  public SettingsPane (String tabKey)
    {
    this.tabKey = tabKey;
    this.ccMode = ConfigChangeListener.CCMODE_NOUPDATE;
    this.error = false;
    }

/*=========================================================================
  
  createButton

=========================================================================*/
  protected JButton createButton (String key)
    {
    String label = dialogsBundle.getString (key);
    JButton b = new JButton (label);
    String mKey = key + "_mnemonic";
    if (dialogsBundle.containsKey (mKey))
      {
      Object o = dialogsBundle.getObject (mKey);
      b.setMnemonic ((int)o);
      }
    return b;
    }

/*=========================================================================
  
  createLabel

=========================================================================*/
  protected JLabel createLabel (String key)
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
  
  getTabName

=========================================================================*/
  protected String getTabName()
    {
    return dialogsBundle.getString (tabKey + "_name");
    }

/*=========================================================================
  
  getMnemonic

=========================================================================*/
  protected int getMnemonic()
    {
    return (int)dialogsBundle.getObject (tabKey + "_mnemonic");
    }

  }


