/*=========================================================================
  
  JGemini

  FileDownloadTarget

  Copyright (c)2027 Kevin Boone, GPLv3.0 

=========================================================================*/

package me.kevinboone.jgemini.base;

import java.io.*;
import java.net.*;
import java.awt.Desktop;

/** A DownloadTarget that represents a local file.
*/
public class FileDownloadTarget implements DownloadTarget
  {
  private File file;
  private OutputStream os = null;

  public FileDownloadTarget (File file)
    {
    this.file = file;
    }

  @Override
  public boolean canOpenLocation()
    {
    return true;
    }

  @Override
  public void delete()
    {
    try
      {
      file.delete();
      }
    catch (Exception e)
      {
      Logger.log (getClass().getName(), Logger.WARNING, e.toString());
      }
    }

  @Override
  public OutputStream open() throws IOException
    {
    os = new FileOutputStream (file);
    return os;
    }

  @Override
  public void close()
    {
    try
      {
      if (os != null) os.close();
      }
    catch (Exception e){}
    }

  @Override
  public String getDisplayName()
    {
    return file.getName();
    }

  /** Use the Desktop to show the location of the file. 
  */
  @Override
  public void openLocation()
    {
    try
      {
      Desktop.getDesktop().browse (new URI ("file://" + file.getParent()));
      }
    catch (Exception e)
      {
      Logger.log (getClass().getName(), Logger.ERROR, e.toString());
      }
    }
  }


