/*=========================================================================
  
  JGemini

  GophermapConverter

  Copyright (c)2021 Kevin Boone, GPLv3.0 

=========================================================================*/
package me.kevinboone.jgemini.converters;
import java.net.*;
import java.io.*;
import java.util.regex.Pattern;

/** A class for converting a gophermap to HTML. */
public class GophermapConverter extends TextLikeConverter implements Converter
  {
  public GophermapConverter (URL baseURL)
    {
    super (baseURL);
    }

  private String getIconForGopherType (char typeChar)
    {
    switch (typeChar)
      {
      case '0': case 'd':
        return "🗀"; 
      case 'h': 
        return "🌍"; 
      case '1':
	return "🗁"; 
      case '5':
        return "🗁"; 
      case '7':
        return "🔍"; 
      case '8':
        return "🖵"; 
      case 's':
        return "♫"; 
      case ';':
        return "🎞"; 
      case 'p': case 'I': case 'g':
        return "📷"; 
      }
    return "→"; 
    }

  /** Foo. */
  private String formatLineAsHtml (String line)
    {
    line = line.trim();
    if (line.equals (".")) return "";

    String[] tokens = line.split ("\t");
    String name = "";
    String selector = "";
    String host = "";
    String port = "";
    char typeChar = 0;

    if (tokens.length >= 1)
      {
      // First token is one-char type then name
      String typeAndName = tokens[0];
      if (typeAndName.length() > 0)
        {
        typeChar = typeAndName.charAt(0);
        }
      if (typeAndName.length() > 1)
        name = typeAndName.substring (1);
      }

    if (tokens.length >= 2)
      {
      selector = tokens[1];
      }

    if (tokens.length >= 3)
      {
      host = tokens[2];
      }

    if (tokens.length >= 4)
      {
      port = tokens[3];
      }

    if (selector.length() == 0) selector = "/";
    if (port.length() == 0) port = "70"; 

    switch (typeChar)
      {
      case 'i':
        return escapeHtml (name) + "\n";
      case '0': case '1': case '5': case '7': case '8':
      case '9': case 'p': case 'g': case 'h': case 'I':
      case 'd': case 's': case ';': case 'c': case 'M':
        if (tokens.length >= 4)
          {
          String ret = "\n<a href=\"gopher://" + host + ":" 
            + port +  "/" + typeChar +  selector + "\">" 
            + getIconForGopherType(typeChar) + " " 
            +  escapeHtml (name) + "</a>\n";
          return ret;
          }
      }
    return escapeHtml (line); // Could not interpret this line
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
      String htmlLine = formatLineAsHtml (line);
      sb.append (htmlLine);
      }

    sb.append ("</pre></body></html>\n");
    return new String (sb);
    }

  }

