/*=========================================================================
  
  JGemini

  Logger 

  Centralize logging in this one class, so we can control it with 
  configuration. Note that I'm not using a full logging framework --
  something like Log4Jv2 would be about ten times larger than the
  whole of this program.

  Copyright (c)2021 Kevin Boone, GPLv3.0 

=========================================================================*/

package me.kevinboone.jgemini.swing;

public class Logger 
  {
  public static void log (Class cls, String message)
    {
    if (Config.getConfig().debug())
      System.out.println (cls.toString() + ": " + message);
    }
  }

