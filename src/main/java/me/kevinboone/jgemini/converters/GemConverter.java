/*=========================================================================
  
  JGemini

  GemConverter

  A class for converting GemText to HTML 

  Copyright (c)2021 Kevin Boone, GPLv3.0 

=========================================================================*/
package me.kevinboone.jgemini.converters;
import java.net.*;
import java.io.*;
import java.util.regex.Pattern;
import me.kevinboone.jgemini.base.*;
import net.fellbaum.jemoji.*;

public class GemConverter extends TextLikeConverter implements Converter
  {
  private boolean verbatim;

  /** Construct a GemConverter, supplying a base URL. We need the URL so
      we can construct proper links. */
  public GemConverter (URL baseUrl)
    {
    super (baseUrl);
    verbatim = false;
    }

  /** Foo. Nowt to do.*/
  private String formatLineAsHtml (String line)
    {
    return line; 
    }

  /** Convert a single (maybe long) Gemtext line to HTML. */
  private String lineToHtml (String gem)
    {
    if (gem.startsWith ("```"))
      {
      if (verbatim)
        {
        verbatim = false;
        return "</pre>\n";
        }
      else
        {
        verbatim = true;
        return "<pre>\n";
        }
      }
    if (verbatim) return escapeHtml(gem) + "\n";
    if (gem.length() == 0) return "<br/>\n";
    if (gem.startsWith (">"))
      return "<blockquote> ❝ " + escapeHtml(gem.substring(1)) + "</blockquote>\n";
    if (gem.startsWith ("###"))
      return "<h3>" + escapeHtml(gem.substring(3)) + "</h3>\n";
    if (gem.startsWith ("##"))
      return "<h2>" + escapeHtml(gem.substring(2)) + "</h2>\n";
    if (gem.startsWith ("#"))
      return "<h1>" + escapeHtml(gem.substring(1)) + "</h1>\n";
    if (gem.startsWith ("* "))
      return "<ul><li>&nbsp;" + escapeHtml(gem.substring(2)) + "</li></ul>\n";
    if (gem.startsWith ("=>"))
      return parseLink (gem.substring(2).trim());
      
    //return "<p>" + escapeHtml(gem) + "</p>\n";
    return formatLineAsHtml (escapeHtml(gem)) + "<br/>\n";
    }

  /** Convert the Gemtext file to HTML. */ 
  @Override
  public String toHtml (String gem)
    {
    //System.out.println ("gem=" + gem);
    StringBuffer sb = new StringBuffer();
    String lines[] = gem.split ("\n");
    //sb.append ("<html><meta charset=\"UTF-8\"><head><body>\n");
    sb.append ("<html><head><body>\n");

    for (int i = 0; i < lines.length; i++)
      {
      String line = lines[i];
      String htmlLine = lineToHtml (line);
      sb.append (htmlLine);
      }

    sb.append ("</body></html>\n");
    return new String (sb);
    }
  }


