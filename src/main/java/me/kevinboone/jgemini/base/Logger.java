/*=========================================================================
  
  JGemini

  Logger 

  Centralize logging in this one class, so we can control it with 
  configuration. Note that I'm not using a full logging framework --
  something like Log4Jv2 would be about ten times larger than the
  whole of this program.

  Copyright (c)2021 Kevin Boone, GPLv3.0 

=========================================================================*/

package me.kevinboone.jgemini.base;

public class Logger 
  {
  public static int ERROR = 0;
  public static int WARNING = 1;
  public static int INFO = 2;
  public static int DEBUG = 3;
  private static int level = WARNING;

  public static boolean isDebug() { return level >= DEBUG; }

  /** Without a specific level, log at DEBUG. */
  public static void log (Class cls, String message)
    {
    log (cls, DEBUG, message);
    }

  /** Log at a particular log level. */
  public static void log (Class cls, int level, String message)
    {
    if (level <= Config.getConfig().logLevel())
      System.err.println ("" + level + ": " + message);
    }

  public static void setLevel (int _level)
    {
    level = _level;
    }
  }

