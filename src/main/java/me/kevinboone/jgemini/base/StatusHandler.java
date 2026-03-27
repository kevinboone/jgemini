/*============================================================================

  JGemini

  StatusHandler 

  Copyright (c)2021-2026 Kevin Boone, GPLv3.0

============================================================================*/
package me.kevinboone.jgemini.base;
import java.util.Vector;

public class StatusHandler 
  {
  private static StatusHandler instance = null;
  private Vector<StatusListener> listeners;

  private StatusHandler ()
    {
    listeners = new Vector<StatusListener>();
    }

  public void addListener (StatusListener listener)
    {
    listeners.add (listener);
    }

  public void writeMessage (String s)
    {
    for (StatusListener l : listeners) 
      {
      l.writeStatus (s);
      }
    }

  public static StatusHandler getInstance()
    {
    if (instance == null)
      instance = new StatusHandler();

    return instance;
    }  

  }
