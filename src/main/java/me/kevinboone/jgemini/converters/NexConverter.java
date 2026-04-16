/*=========================================================================
  
  JGemini

  TextConverter

  Copyright (c)2026 Kevin Boone, GPLv3.0 

=========================================================================*/
package me.kevinboone.jgemini.converters;
import java.net.*;
import java.io.*;
import java.util.regex.Pattern;

/** A class for converting nex-flavoured plain text to HTML. It's just
    plain text, but with link lines starting with "=&gt;"
*/
public class NexConverter extends TextLikeConverter implements Converter
  {
  public NexConverter (URL baseURL)
    {
    super (baseURL);
    }

  private String formatLineAsHtml (String line)
    {
    return line; 
    }

  /** Convert a single (text line to HTML. */
  private String lineToHtml (String text)
    {
    if (text.startsWith ("=>"))
      {
      return parseLink (text.substring(2).trim());
      }
    else
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




