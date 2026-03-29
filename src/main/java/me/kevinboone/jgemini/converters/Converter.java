/*=========================================================================
  
  JGemini

  Converter

  Interface implemented by all something-to-HTML converters 

  Copyright (c)2026 Kevin Boone, GPLv3.0 

=========================================================================*/
package me.kevinboone.jgemini.converters;
import java.net.*;
import java.io.*;
import java.util.regex.Pattern;
import me.kevinboone.jgemini.base.*;
import com.vdurmont.emoji.EmojiManager;

public interface Converter 
  {
  public String toHtml (String s);
  }

