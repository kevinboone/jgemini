/*=========================================================================
  
  JGemini

  DownloadMonitor 

  Copyright (c)2026 Kevin Boone, GPLv3.0 

=========================================================================*/
package me.kevinboone.jgemini.base;
import java.util.*;
import java.io.*;

/** 
  This interface governs the behavior of download monitors.
*/
public interface DownloadMonitor
  {
  /** Add a DownloadMonitorListener. The DownloadMonitor will inform
      the listener when downloads start, or change status. In practice,
      the only listener is DownloadsDialog. */
  public void addListener (DownloadMonitorListener listener);

  /** Add a new download to the monitor's list.
  */
  public void add (Download download);

  /** Get the number of downloads in the "ongoing" state.
  */
  public int getActiveDownloadCount();

  /** Kill 'em all.
  */
  public void cancelAll();

  /** Clear all downloads that are not in the "ongoing" state.
      Implementations are expected to notify listeners of
      downloads removed from the list. 
  */
  public void clear();

  /** Get the total number of downloads, in all states. */
  public int getDownloadCount();

  /** Called by a Download instance when it has changed status.
  */
  public void notifyChange (Download download);

  /** Get a Download object by its index in the list. */
  public Download getDownload (int index);

  /** It's crucial that download listeners detach themselves when
      they go away, so Downloads don't get stuck with dangling
      threads.
  */
  public void removeListener (DownloadMonitorListener listener);

  /** Return the index of the specified download in our list, or -1 if
      there is no such download.
  */
  public int indexOfDownload (Download download);
  }

