/*============================================================================

  JGemini

  MimeUtil 

  Copyright (c)2021-2026 Kevin Boone, GPLv3.0

============================================================================*/
package me.kevinboone.utils.mime;

import java.io.*;
import java.net.*;

public class MimeUtil 
  {
/*=========================================================================
  
  getEncodingFromMime

  Extract an encoding name from a MIME type. If there is none, 
  return "UTF-8".

=========================================================================*/
  public static String getEncodingFromMime (String mime)
    {
    String[] args = mime.split (";");
    for (int i = 0; i < args.length; i++)
      {
      String arg = args[i].trim();
      if (arg.startsWith ("charset="))
        return arg.substring (8);
      }
    return "UTF-8"; 
    }

  }


