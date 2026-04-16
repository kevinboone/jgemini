/*=========================================================================
  
  JGemini

  GemUtil 

  Copyright (c)2021-6 Kevin Boone, GPLv3.0 

=========================================================================*/
package me.kevinboone.jgemini.base;
import java.io.*;

/** Various utilities for interpreting the contents of Gemtext files. */
public class GemUtil
  {
/*=========================================================================
  
  removeLeadingHashes 

=========================================================================*/
  /** Removes all the # characters from the start of a line. The resut
  might start with spaces, and might even be empty. */
  static public String removeLeadingHashes (String s)
    {
    while (s.length() > 0 && s.charAt(0) == '#')
      s = s.substring(1);
    return s;
    }

/*=========================================================================
  
  getFirstHeading

=========================================================================*/
  /** Given a whole or partial Gemtext or Markdown document, return the
  first heading -- that is, the first line beginning '#'.*/
  static public String getFirstHeading (String s)
    {
    String ret = null;
    boolean found = false;

    try
      {
      BufferedReader br = new BufferedReader (new StringReader (s));
      String line = br.readLine();
      while (!found && (line != null))
	{
        if (line.length() > 0 && line.charAt(0) == '#')
          {
          ret = removeLeadingHashes (line);
          ret = ret.trim();
          found = true;
          }
	line = br.readLine();
	}
      br.close();
      }
    catch (IOException e)
      {
      // Can't happen when we're reading a string!
      }

    return ret;
    }


  }


