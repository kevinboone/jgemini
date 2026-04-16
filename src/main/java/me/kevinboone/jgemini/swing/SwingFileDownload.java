/*=========================================================================
  
  JGemini

  SwingFileDownload 

  Copyright (c)2026 Kevin Boone, GPLv3.0 

=========================================================================*/
package me.kevinboone.jgemini.swing;
import java.util.*;
import javax.swing.*;
import java.io.*;
import java.net.*;
import me.kevinboone.jgemini.base.*; 

/** 
  This class represents a single instance of an ongoing or complete
  download. In fact, this class stinks: it's ridiculously complicated
  because the swing UI is not thread safe. So it makes use of a 
  Swing event queue to transfer status information from the background
  worker thread to the main UI as the download progresses. 
*/
public class SwingFileDownload extends SwingDownload
  {
  private CompletionHandler ch;

  /** Create a new Download. The CompletionHandler will be called,
      on the main UI thread, when the transfer is complete, so
      long as it succeeded. 
  */
  public SwingFileDownload (MainWindow mainWindow, String href, 
      DownloadTarget target, CompletionHandler ch)
    {
    super (mainWindow, href, target);
    this.ch = ch;
    }

  @Override
  public void start()
    {
    Logger.in();
    Logger.log (getClass().getName(), Logger.INFO, 
        "Downloading " + url + " to " + target.getDisplayName());

    SwingFileDownload self = this;

    // Sigh... here we go.
    dlWorker = new SwingWorker() 
      {
      byte[] b = null;
      Exception e = null;
      @Override
      /** _All_ the work of the download is done in this background
          thread, apart from periodically updating the UI. This 
          includes the initial connection, and that means that, if
          it fails, we can only notify the user by an asynchronous
          means, and the notification may arrive some time after
          the user did whatever as supposed to start the transfer.
          In practice, it's not usually that much of a problem, so
          long as it doesn't take many seconds to make a conection.
      */
      protected String doInBackground() 
        { 
        status = DS_ONGOING;
        if (dm != null) dm.notifyChange (self); 
        InputStream is = null;
        try  
          (
          OutputStream fos = target.open(); 
          )
          {
          // Lets not add ourself to the download monitor until we
          //   actually start the connection to the server
          URL _url = new URL (url);
	  URLConnection conn = _url.openConnection();
          downloadMonitor.add (self);
	  conn.connect();
          is = conn.getInputStream();
	  size = 0;
	  int nRead;
	  byte[] data = new byte[16384];
          long lastNotification = System.currentTimeMillis();
	  while ((nRead = is.read (data, 0, data.length)) != -1) 
	    {
	    try
	      {
	      Thread.sleep (1); // We need to get an InterruptedException if canceled
	      }
	    catch (InterruptedException e)
	      {
	      throw new IOException ("Interrupted");
	      }
	    fos.write (data, 0, nRead);
	    size += nRead;
            
            long now = System.currentTimeMillis();
            // Only notify every second. More than that just overwhelms
            //   the inter-thread communication system
	    if (now - lastNotification > 1000)
              {
              // Put the size on the notification queue, where it will
              //   in due course be picked up by process()
              Integer message = new Integer (size);
	      publish (message);
              lastNotification = now;
              }
            }

          // We _must_ notify at the end of the transfer, else the
          //   client class could end up with the wrong final size
          Integer message = new Integer (size);
	  publish (message);

          if (is != null) is.close();
          target.close();
          }
        catch (Exception ee)
          {
          e = ee;
          }
        finally
          {
          if (is != null) try {is.close();} catch (Exception e){};
          }           
        target.close();
        Logger.log (getClass().getName(), 
          Logger.DEBUG, "Download worker doInBackground()");
        return "";
        }

      @Override
      /** There are all sorts of reasons we might arrive here -- not
          all of them good. We'll need to use information stored
          by doInBackground() to work out what action to take. We are
          on the main UI thread here.
      */
      protected void done()  
        { 
        Logger.log (getClass().getName(), Logger.DEBUG, 
          "Download worker done()");
        if (!isCancelled())
          {
          // Well, the user didn't cancel it. So that's good...
	  if (e == null)
	    {
            // And if doInBackground() didn't store an exception,
            //   we can assume the transfer was a success.
	    status = DS_COMPLETE;

            // If the caller gave a CompletionHandler, it's safe 
            //   to run it now. Of course, it might fail, so we
            //   need to be able to raise an exception.
	    if (ch != null)
	      {
	      try
		{
		ch.complete();
		}
	      catch (Exception e)
		{
		status = DS_FAILED;
		DialogHelper.exceptionDialog (mainWindow, url, e);
		}
	      }
	    }
	  else
	    {
            // But if doInBackground() stored an exception, we failed,
            //   and we need to alert the user.
	    status = DS_FAILED;
	    DialogHelper.exceptionDialog (mainWindow, url, e);
	    }
          }
        else
          {
          // User cancelled. Ask the target to delete itself, 
          //   if that makes sense.
	  status = DS_CANCELLED;
          target.delete(); 
          }
        // Finally, notify the DownloadMonitor of our new status,
        //   so it can notify the user interface.
        if (dm != null) 
          {
          dm.notifyChange (self); 
          }
        }

      /** Pass the queue of progress updates along to the UI 
          via the DownloadManager. In practice, this method
          may be called an unknown number of times, even after
          the download itself has finished or been cancelled. 
      */
      @Override
      protected void process (java.util.List chunks) 
        { 
	for (Object message : chunks)
	  {
	  Integer size = (Integer)message;
	  if (dm != null) dm.notifyChange (self); 
          }
        } 
      }; // End of SwingWorker definition
  
    dlWorker.execute();
    Logger.out();
    }
  }


