/*=========================================================================
  
  JGemini

  Clipboard

  Copyright (c)2021 Kevin Boone, GPLv3.0 

=========================================================================*/
package me.kevinboone.jgemini.swing;
import java.awt.datatransfer.StringSelection;
import java.awt.Toolkit;
import java.awt.datatransfer.*;

/** A simple helper class for clipboad operations. 
*/
public class Clipboard 
  {
  public static void copyTextToClipboard (String text)
    {
    Toolkit.getDefaultToolkit()
      .getSystemClipboard()
      .setContents
         ( new StringSelection (text), null);
    }
  }

