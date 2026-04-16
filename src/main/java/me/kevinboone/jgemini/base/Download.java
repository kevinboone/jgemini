/*=========================================================================
  
  JGemini

  Download 

  Copyright (c)2026 Kevin Boone, GPLv3.0 

=========================================================================*/
package me.kevinboone.jgemini.base;
import java.util.*;
import java.io.*;

/** 
  This interface represents a single instance of an ongoing or complete
  download. The only concrete implementation at present is SwingFileDownload,
  which uses Swing's built-in thread management to try to prevent 
  background operations messing up the UI.
*/
public interface Download 
  {
  // Status codes.
  public static final int DS_UNKNOWN = 0;
  public static final int DS_ONGOING = 1;
  public static final int DS_COMPLETE = 2;
  public static final int DS_FAILED = 3;
  public static final int DS_CANCELLED= 4;

  /** Cancel the transfer, free resources. 
  */
  public void cancel();

  /** Get the URL that is the source of the transfer.  
  */
  public String getURL();

  /** Get the target object, which might represent a local file or
      a stream player, among others. 
  */
  public DownloadTarget getTarget();

  /** Get the size of the transfer. Implementations are expected to
      keep this up to date as the transfer progresses. 
  */
  public int getSize(); 
  public int getStatus(); 

  /** Not yet used -- it was supposed to indicate whether a transfer
      should be shown in the UI. */
  public boolean isExposed(); 

  /** Associate this download with a instance of DownloadMonitor.
      Implementations are expected to update the DownloadMonitor
      with their progress.
  */
  public void setDownloadMonitor (DownloadMonitor dm);

  /* Well ...duh!
  */
  public void start();
  }
