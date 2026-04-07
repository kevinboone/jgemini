/*=========================================================================
  
  JGemini

  IdentUtil 

  Copyright (c)2021 Kevin Boone, GPLv3.0 

=========================================================================*/
package me.kevinboone.jgemini.ssl;
import java.io.*;
import java.awt.*;
import java.util.*;
import java.net.URL;
import me.kevinboone.jgemini.Constants;
import me.kevinboone.jgemini.base.*;

public class IdentUtil 
{
private final static ResourceBundle messagesBundle = 
    ResourceBundle.getBundle ("me.kevinboone.jgemini.bundles.Messages");

private final static Config config = Config.getConfig();

/*=========================================================================
  
  checkIdentName

  Check that a putative identity name consists only of sensible
    characters. It's going to be the key in a Java propsfile, and part
    of a filename, so there are limitations.

=========================================================================*/
public static void checkIdentName (String name)
    throws IdentException
  {
  if (name == null || name.length() == 0)
    throw new IdentException (messagesBundle.getString 
      ("ident_name_empty"));

  int l = name.length();
  for (int i = 0; i < l; i++)
    {
    char c = name.charAt(i);
    if (!(Character.isLetterOrDigit (c) || c == '_'))
      {
      throw new IdentException (messagesBundle.getString 
        ("ident_name_format") + ": " + c);
      }
    }
  }

/*=========================================================================
  
  identNameToFilename

=========================================================================*/

public static String identToFilename (String identName)
  {
  return config.getIdentsDir() + File.separator 
    + identName + ".p12";
  }

}


