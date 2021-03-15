/*=========================================================================
  
  JGemini

  TextConverter

  A class for converting plain text to HTML 

  Copyright (c)2021 Kevin Boone, GPLv3.0 

=========================================================================*/
package me.kevinboone.jgemini.swing;
import java.net.*;
import java.io.*;
import java.util.regex.Pattern;

public class TextConverter
  {
  /** Convert common punctuation like & into HTML-friendly forms. */
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
  public String textToHtml (String text, String encoding)
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

/*
  public static void main (String[] args)
       throws Exception
    {
    if (args.length == 1)
      {
      FileInputStream fis = new FileInputStream (new File (args[0]));
      ByteArrayOutputStream content_buffer = new ByteArrayOutputStream();

      int nRead;
      byte[] data = new byte[16384];

      while ((nRead = fis.read (data, 0, data.length)) != -1) 
       content_buffer.write (data, 0, nRead);

      byte[] gmi = content_buffer.toByteArray();

      content_buffer.close();

      Converter gc = new GemConverter (null);

      System.out.println (gc.gemToHtml (new String(gmi), null));

      fis.close();
      }
    else
      {
      System.out.println 
        ("Usage: java GemConverter {gemtext_file}");
      }
    }
*/
  }



