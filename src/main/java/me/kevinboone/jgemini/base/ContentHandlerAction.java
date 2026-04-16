/*=========================================================================
  
  JGemini

  ContentHandlerAction 

  Copyright (c)2021-6 Kevin Boone, GPLv3.0 

=========================================================================*/
package me.kevinboone.jgemini.base;

/** This interface is just a place to group constants that define
    how JGemini should behave when the user tries to download something
    that the application does not natively support. */
public interface ContentHandlerAction
{
  /** Do nothing -- skip the request. */
  public static int CHA_NONE = -1;
  /** Save the document in the default location. */
  public static int CHA_SAVE = 0;
  /** Prompt for a location, and save there. */
  public static int CHA_PROMPTSAVE = 1;
  /** Stream the document out to the media helper. */
  public static int CHA_STREAM = 2;
  /** Write a temporary file, and invoke the desktop. */
  public static int CHA_DESKTOP = 3;
}

