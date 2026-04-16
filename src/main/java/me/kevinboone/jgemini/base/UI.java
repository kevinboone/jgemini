/*=========================================================================
  
  JGemini

  UI 

  This interface is implemented by all user interfaces

  Copyright (c)2021-6 Kevin Boone, GPLv3.0 

=========================================================================*/
package me.kevinboone.jgemini.base;

/** The base class for user interfaces. In practice, only the Swing
    UI is implemented so far. */
public interface UI 
  {
  public void start();
  public void loadURI (String uri);
  }



