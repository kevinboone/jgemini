/*=========================================================================
  
  JGemini

  TextConverter

  Copyright (c)2021 Kevin Boone, GPLv3.0 

=========================================================================*/
package me.kevinboone.jgemini.converters;
import java.net.*;
import java.io.*;
import java.util.regex.Pattern;

/** A class for converting plain text to HTML. Mostly we just wrap up the
    whole thing in a pre-formatted block, but we have to convert characters
    that would mess up the HTML. 
*/
public class TextConverter implements Converter
  {
  /** Convert common punctuation like &amp; into HTML-friendly forms. */
  public static String escapeHtml (String gem)
    {
    String s = gem.replace (">", "£££gt;");
    s = s.replace ("<", "£££lt;");
    s = s.replace ("&", "&amp;");
    s = s.replace ("£££", "&");
    s = s.replace ("\r>", "");
    return s;
    }

  /** Foo. */
  private String formatLineAsHtml (String line)
    {
    return line; 
    }

  /** Convert a single (text line to HTML. */
  private String lineToHtml (String text)
    {
    return formatLineAsHtml (escapeHtml(text)) + "\n";
    }

  /** Convert the plain text file to HTML. */ 
  @Override
  public String toHtml (String text)
    {
    StringBuffer sb = new StringBuffer();
    String lines[] = text.split ("\n");
    sb.append ("<html><head><body><pre>\n");

    for (int i = 0; i < lines.length; i++)
      {
      String line = lines[i];
      String htmlLine = lineToHtml (line);
      sb.append (htmlLine);
      }

    sb.append ("</pre></body></html>\n");
    return new String (sb);
    }
  }



