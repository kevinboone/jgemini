/*============================================================================

  JGemini

  ContentGuesser 

  Copyright (c)2021-2026 Kevin Boone, GPLv3.0

============================================================================*/
package me.kevinboone.utils.file;

import java.io.*;
import java.net.*;

public class ContentGuesser 
  {
  /** This method can return null, and will if the filename is empty or
      looks like a directory. */
  public static String guessMimeTypeFromFilename (String filename)
    {
    if (filename.endsWith (".gmi")) return "text/gemini";
    return URLConnection.guessContentTypeFromName (filename); 
    }

  }
