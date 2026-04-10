/*=========================================================================
  
  JGemini

  ConfigChangeListener 

  Copyright (c)2021-6 Kevin Boone, GPLv3.0 

=========================================================================*/
package me.kevinboone.jgemini.base;

public interface ConfigChangeListener
  {
  public static final int CCMODE_NOUPDATE = 0;
  public static final int CCMODE_REFRESH = 1;
  public static final int CCMODE_RELOAD = 2;
  public void configChanged (int ccMode);
  }


