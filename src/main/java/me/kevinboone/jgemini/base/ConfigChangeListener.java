/*=========================================================================
  
  JGemini

  ConfigChangeListener 

  Copyright (c)2021-6 Kevin Boone, GPLv3.0 

=========================================================================*/
package me.kevinboone.jgemini.base;

/** This interface is implemented by any class that can respond to
    configuration changes. It also defines constants that  
    control the action that should be taken after making a configuration 
    change using the UI. */
public interface ConfigChangeListener
  {
  public static final int CCMODE_NOUPDATE = 0;
  public static final int CCMODE_REFRESH = 1;
  public static final int CCMODE_RELOAD = 2;
  public void configChanged (int ccMode);
  }


