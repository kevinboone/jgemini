/*=========================================================================
  
  JGemini

  FeedException 

  Copyright (c)2021 Kevin Boone, GPLv3.0 

=========================================================================*/
package me.kevinboone.jgemini.base;
import me.kevinboone.jgemini.Constants;

/** An exception thrown to indicate a problem with feed parsing.
*/
public class FeedException extends JGeminiException
{
public FeedException (String s)
  {
  super (s);
  }

public FeedException (Exception e)
  {
  super ("Error parsing feed: " + e.toString());
  }
}


