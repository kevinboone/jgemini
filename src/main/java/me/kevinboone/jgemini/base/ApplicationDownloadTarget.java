/*=========================================================================
  
  JGemini

  ApplicationDownloadTarget

  Copyright (c)2027 Kevin Boone, GPLv3.0 

=========================================================================*/

package me.kevinboone.jgemini.base;

import java.io.*;
import java.net.*;

/** A DownloadTarget for use with SwingFileDownload (and possibly 
    others in due course), that sends 
    data to an external media player. */
public class ApplicationDownloadTarget implements DownloadTarget
  {
  private OutputStream os = null;
  private String cmdLine;
  private Process streamProcess = null;

  public ApplicationDownloadTarget (String cmdLine)
    {
    this.cmdLine = cmdLine;
    }

  /** Always returns false -- we can't open the location of a stream. */
  @Override
  public boolean canOpenLocation()
    {
    return false;
    }

  /** Does nothing: there is nothing stored to delete. */
  @Override
  public void delete()
    {
    Logger.log (getClass().getName(), Logger.DEBUG, 
      "Tried to call delete() on a stream target");
    }

  /** Open a Process for the external player, and capture its
      stdin channel to be the target of the stream. */
  @Override
  public OutputStream open() throws IOException
    {
    streamProcess = Runtime.getRuntime().exec (cmdLine);
    InputStream stdout = streamProcess.getInputStream();
    InputStream stderr = streamProcess.getErrorStream();
    StreamEater stdoutEater = new StreamEater (stdout);
    StreamEater stderrEater = new StreamEater (stderr);
    stdoutEater.start();
    stderrEater.start();
    OutputStream stdin = streamProcess.getOutputStream();
    os = stdin;
    return os;
    }

  /* Close the stream, and also the player process if possible. */
  @Override
  public void close()
    {
    try
      {
      if (os != null) os.close();
      if (streamProcess != null) streamProcess.destroy();
      }
    catch (Exception e){}
    }

  /** The display name will just be the player command line. It's only used
      by DownloadsDialog. */
  @Override
  public String getDisplayName()
    {
    return cmdLine;
    }

  /** We can't open the location of a stream. */
  @Override
  public void openLocation()
    {
    Logger.log (getClass().getName(), Logger.DEBUG, 
      "Tried to call openLocation() on a stream target");
    }
  }



