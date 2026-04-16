/*=========================================================================
  
  JGemini

  SwingDownload 

  Copyright (c)2026 Kevin Boone, GPLv3.0 

=========================================================================*/
package me.kevinboone.jgemini.swing;
import javax.swing.*;
import java.util.*;
import java.io.*;
import java.net.*;
import me.kevinboone.jgemini.base.*; 

/** 
  This class represents a single instance of an ongoing or complete
  download. It's abstract because it doesn't have a start() method which
  is where, in practice, all the work will be done. The concrete 
  implementation if SwingFileDownload, which handles both local files and
  streams. This class exists because I envisage having other kinds of
  download but, with hindsight, I'm not sure what they would be.
  TODO: merge with SwingFileDownload.
*/
public abstract class SwingDownload implements Download
  {
  protected String url = null;
  protected int status = Download.DS_UNKNOWN;
  protected int size = 0; 
  protected boolean isExposed = false;
  protected MainWindow mainWindow;
  protected SwingWorker dlWorker = null;
  protected CompletionHandler ch = null;
  protected DownloadMonitor dm = null;
  protected static DownloadMonitor downloadMonitor 
    = DefaultDownloadMonitor.getInstance();
  protected final static ResourceBundle messagesBundle = 
    ResourceBundle.getBundle ("me.kevinboone.jgemini.bundles.Messages");
  protected DownloadTarget target;

  public SwingDownload (MainWindow mainWindow, String url, 
      DownloadTarget target)
    {
    this.url = url;
    this.mainWindow = mainWindow;
    this.target = target;
    }

  @Override
  public void cancel()
    {
    dlWorker.cancel (true);
    }

  @Override
  public DownloadTarget getTarget()
    {
    return target;
    }

  @Override
  public String getURL()
    {
    return url;
    }

  @Override
  public int getStatus()
    {
    return status;
    }

  @Override
  public int getSize()
    {
    return size;
    }

  @Override
  public boolean isExposed()
    {
    return isExposed;
    }

  public void setCompletionHandler (CompletionHandler ch)
    {
    this.ch = ch;
    }

  public void setDownloadMonitor (DownloadMonitor dm)
    {
    this.dm = dm;
    }

  @Override
  public abstract void start();

  }


