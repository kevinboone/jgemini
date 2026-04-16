/*=========================================================================
  
  JGemini

  StreamEater 

  Copyright (c)2010-2026 Kevin Boone, GPLv3.0 

=========================================================================*/
package me.kevinboone.jgemini.base;
import java.io.*;

/** A class that reads a stream and throws the data away. We need this
    to absort the output of Runtime.exec() when we launch an external
    helper application. The JVM comes to a hault if the process writes
    to stdout or stderr, and we don't read it -- whether the information
    is meaningful or not. This class is used by ApplicationDownloadTarget,
    to slurp up useless output from the external media player.
*/
public class StreamEater extends Thread
  {
  BufferedReader br;

  /** Construct a StreamEater on an InputStream. */
  public StreamEater (InputStream is)
    {
    this.br = new BufferedReader (new InputStreamReader (is));
    }

  public void run ()
    {
    try
      {
      String line;
      while ((line = br.readLine()) != null)
        {
	// Process the line of output in some way
        }
      }
    catch (IOException e)
      {
      // Do something to handle exception
      }
    finally
      {
      try { br.close(); } catch (Exception e) {};
      }
    }
  }
