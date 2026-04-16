/*=========================================================================
  
  JGemini

  Download 

  Copyright (c)2026 Kevin Boone, GPLv3.0 

=========================================================================*/
package me.kevinboone.jgemini.base;
import java.util.*;
import java.io.*;

/** This interface controls the behaviour of a DownloadMonitorListener.
    At present, only DownloadsDialog is such a listener. The 
    DownloadMonitor will call these methods whenever downloads
    change.
*/
public interface DownloadMonitorListener
  {
  public void downloadAdded (Download d);
  public void downloadChanged (Download d);
  public void downloadRemoved (Download d);
  }


