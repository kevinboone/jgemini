/*=========================================================================
  
  JGemini

  FeedManagerStatusListener 

  Copyright (c)2026 Kevin Boone, GPLv3.0 

=========================================================================*/
package me.kevinboone.jgemini.base;

/** This interface is implemented by anything that wants to be updated
    about the status of the feed aggregation process. */
public interface FeedManagerStatusListener
  {
  public void cancelled();
  public void finished();
  public void newMessage (String message);
  public void started();
  }

