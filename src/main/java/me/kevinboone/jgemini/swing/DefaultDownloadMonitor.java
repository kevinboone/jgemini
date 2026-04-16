/*=========================================================================
  
  JGemini

  DefaultDownloadMonitor 

  Copyright (c)2026 Kevin Boone, GPLv3.0 

=========================================================================*/
package me.kevinboone.jgemini.swing;
import java.util.*;
import javax.swing.*;
import java.io.*;
import java.net.*;
import me.kevinboone.jgemini.base.*; 

/** This is the default implementation of DownloadMonitor for the Swing UI. 
    It maintains a list of Download objects that represent transfers in 
    various states. When these transfers change, it invokes a 
    DownloadMonitorListener which, in practice, is the DownloadDialog, 
    which provides the UI. See DownloadMonitor for method
    definitions.
*/
public class DefaultDownloadMonitor implements DownloadMonitor 
  {
  private static DefaultDownloadMonitor instance = null;
  private Vector<Download> list = new Vector<Download>();
  private DownloadMonitorListener listener = null;

  /** Private constructor -- use getInstance() */
  private DefaultDownloadMonitor() { }

  private DownloadsDialog dialog = null;

  @Override
  public void add (Download download)
    {
    download.setDownloadMonitor (this);
    list.addElement (download);
    if (listener != null) 
      {
      listener.downloadAdded (download);
      }
    }

  @Override
  public void addListener (DownloadMonitorListener listener)
    {
    this.listener = listener;
    }

  @Override 
  public void cancelAll()
    {
    if (listener != null) 
      {
      list.forEach (download -> 
        {
        if (download.getStatus() == download.DS_ONGOING) 
          listener.downloadChanged (download);
        }); 
      }
    list.forEach (download -> 
      {
      download.cancel();
      });
    }

  @Override 
  public void clear()
    {
    // First we'll notify any listener of all the downloads we're _going_
    //   to remove...
    if (listener != null) 
      {
      list.forEach (download -> 
        {
        if (download.getStatus() != download.DS_ONGOING) 
          listener.downloadRemoved (download);
        }); 
      }
    // ...then we'll remove them. Obviously, the two sets of downloads
    //   have to be the same. We can't do the notification and removal
    //   on a per-Download basis, because the size of the Vector will
    //   change a we remove.
    list.removeIf (download -> (download.getStatus() != download.DS_ONGOING));
    }

  @Override
  public int getActiveDownloadCount()
    {
    int count = 0;
    int l = list.size();
    for (int i = 0; i < l; i++)
      {
      Download d = list.elementAt (i);
      if (d.getStatus() == d.DS_ONGOING)
        count++;
      }
    return count;
    }

  @Override
  public int getDownloadCount()
    {
    return list.size();
    }

  @Override
  public Download getDownload (int index)
    {
    return list.elementAt (index);
    }

  /** Get the single, application-wide instance of this DownloadMonitor. */
  public static DefaultDownloadMonitor getInstance()
    { 
    if (instance == null)
      instance = new DefaultDownloadMonitor();
    return instance;
    }

  @Override
  public int indexOfDownload (Download download)
    {
    return list.indexOf (download);
    }

  public void notifyChange (Download download)
    {
    int index = list.indexOf (download);
    list.setElementAt (download, index);
    if (listener != null) 
      {
      listener.downloadChanged (download);
      }
    }

  @Override
  public void removeListener (DownloadMonitorListener listener)
    {
    this.listener = null;
    }

  public void setDownloadsDialog (DownloadsDialog dialog)
    {
    this.dialog = dialog;
    }

  }



