/*=========================================================================
  
  JGemini

  GemLink

  Copyright (c)2021-6 Kevin Boone, GPLv3.0 

=========================================================================*/
package me.kevinboone.jgemini.base;
import java.io.*;

public class GemLink
  {
  private String uri;
  private String text;

  public GemLink (String uri)
    {
    this.uri = uri;
    this.text = uri;
    }

  public GemLink (String uri, String text)
    {
    this.uri = uri;
    this.text = text;
    }

  public String getUri() { return uri; }
  public String getText() { return text; }


  /**
  Creates a GemLink instance from a string of the form "url text", optionally
  preceded by "=>". This method can't fail: a link line can legitimately 
  consist of anything.
  */
  public static GemLink parse (String line)
    {
    if (line.indexOf ("=>") == 0) 
      line = line.substring (2);
    line = line.trim();
    String[] args = line.split ("\\s+", 2);
    if (args.length >= 2)
      {
      return new GemLink (args[0], args[1]);
      }
    else 
      {
      return new GemLink (args[0], args[0]);
      }
    }

  public String toString ()
    {
    return "=> " + uri + " " + text;
    }

  }

