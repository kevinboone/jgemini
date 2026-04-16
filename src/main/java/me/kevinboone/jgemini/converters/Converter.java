/*=========================================================================
  
  JGemini

  Converter

  Copyright (c)2026 Kevin Boone, GPLv3.0 

=========================================================================*/
package me.kevinboone.jgemini.converters;
import java.net.*;
import java.io.*;
import java.util.regex.Pattern;
import me.kevinboone.jgemini.base.*;

/** This interface is implemented by all something-to-HTML converters 
*/
public interface Converter 
  {
  /** Given a String s of whatever the content is, return a new String
      of HTML. */
  public String toHtml (String s);
  }

