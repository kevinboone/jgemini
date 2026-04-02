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
  private static String level_names[] = {"ERROR", "WARNING", "INFO", "DEBUG"};

  public static void in()
    {
    if (level < DEBUG) return; 
    Throwable throwable = new Throwable();
    StackTraceElement[] stackTrace = throwable.getStackTrace();
        
    if (stackTrace.length > 2)
      {
      String methodName = stackTrace[2].getMethodName();
      String className = stackTrace[2].getClassName();
      log (className, DEBUG, methodName + " IN");
      }
    }

  public static void out()
    {
    if (level < DEBUG) return; 
    Throwable throwable = new Throwable();
    StackTraceElement[] stackTrace = throwable.getStackTrace();
        
    if (stackTrace.length > 2)
      {
      String methodName = stackTrace[2].getMethodName();
      String className = stackTrace[2].getClassName();
      log (className, DEBUG, methodName + " OUT");
      }
    }

  public static boolean isDebug() { return level >= DEBUG; }

  /** Log at a particular log level. */
  public static void log (String className, int level, String message)
    {
    if (level > DEBUG) level = DEBUG;
    if (level < ERROR) level = ERROR;
    if (level <= Config.getConfig().logLevel())
      {
      System.err.print (level_names[level]);

      System.err.print (" ");

      int p = className.lastIndexOf (".");
      if (p >= 0) className = className.substring (p + 1);
      System.err.print (className);

      if (level >= DEBUG)
        {
        Throwable throwable = new Throwable();
        StackTraceElement[] stackTrace = throwable.getStackTrace();
        
	String methodName = null; 
	if (stackTrace.length > 2)
	  {
	  methodName = stackTrace[2].getMethodName();;
          System.err.print (" ");
          System.err.print (methodName);
	  }
        }
 
      System.err.print (": ");
      System.err.println (message);
      }
    }

  /** Log at a particular log level. */
  public static void log (Class cls, int level, String message)
    {
    log (cls.getName(), level, message);
    }

  public static void setLevel (int _level)
    {
    level = _level;
    }
  }

