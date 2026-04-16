/*============================================================================

  JGemini

  StatusListener

  Copyright (c)2021-2026 Kevin Boone, GPLv3.0

============================================================================*/
package me.kevinboone.jgemini.base;

/** An interface that is implemented by anything that can add itself to the
    status monitoring framework implemented by StatusHandler. 
*/
public interface StatusListener 
  {
  public void writeStatus (String message);
  }

