/*============================================================================

  JGemini

  SpartanConnection

  Copyright (c)2021-2026 Kevin Boone, GPLv3.0

============================================================================*/
package me.kevinboone.jgemini.protocol;

import java.io.*;
import java.net.*;
import java.util.Collections;
import me.kevinboone.jgemini.base.*;
import me.kevinboone.utils.file.ContentGuesser;

public class SpartanConnection extends URLConnection
  {
  private Socket s = null;
  private String contentType = null;
  private InputStream is = null;
  private byte[] content = null;
  private String meta = null;
  private static StatusHandler statusHandler = StatusHandler.getInstance();

  public SpartanConnection (URL url) 
    {
    super (url);
    }

  private static int parseStatus (String line)
    {
    String[] args = line.split ("\\s+", 2); 
    try 
      {
      int status = Integer.parseInt (args[0]);
      return status;
      }
    catch (NumberFormatException e) 
      {
      return -1;
      }
    }

  private static String parseMeta (String line)
    {
    String[] args = line.split ("\\s+", 2); 
    if (args.length > 1)
      return args[1]; 
    else
      return "";
    }

  @Override
  public void connect() 
      throws IOException 
    {
    if (s != null && s.isConnected()) return;
    String host = getURL().getHost();
    String path = getURL().getPath();
    int port = getURL().getPort();
    if (port == -1) port = 300;

    s = new Socket (host, port);
    is = s.getInputStream();
    OutputStream os = s.getOutputStream();
    PrintStream pos = new PrintStream (os);

    String request = getURL().getHost() + " " + getURL().getPath() + " 0\r\n";
    pos.print (request); 
    pos.flush();

    char c; 
    String line = ""; 
    do 
       {
       c = (char)is.read(); 
       if (c == '\n')
	 break; 
       if (c != '\r') line += c + "";
       } while (c != -1);
   
    int status = parseStatus (line);
    Logger.log (getClass().getName(), Logger.DEBUG, 
      "Got status code " + status);
    meta = parseMeta (line);
    if (status == 2)
      {
      // Nothing to do -- input stream is now positioned
      //  to read data
      }
    else
      {
      s.close();
      s = null;
      if (status < 2) 
	{
	throw new ErrorResponseException (getURL(), status, 
	    "Invalid status code in response");
	}
      if (status >= 10 && status < 20)
	{
	throw new RetryWithInputException (getURL(), status == 11, meta);
	}
      else if (status == 3)
	{
	Logger.log (getClass().getName(), Logger.DEBUG, 
          "Throwing a redirect to " + meta);
	throw new RedirectedException (new URL(getURL(), meta));
	}
      else if (status == 4)
	{
	throw new ErrorResponseException (getURL(), status, meta);
	}
      else if (status == 5)
	{
	throw new ErrorResponseException (getURL(), status, meta);
	}
      }
    }

  @Override
  public String getContentType()
    {
    return contentType;
    }
   
  @Override
  public Object getContent() 
      throws IOException 
    {
    if (content != null) return content;
    try
      {
      connect();

      int totalRead = 0;
      contentType = meta;
      ByteArrayOutputStream content_buffer = new ByteArrayOutputStream();

      int nRead;
      byte[] data = new byte[16384];

      try
	{
	while (s.isConnected() && 
	     (nRead = is.read (data, 0, data.length)) != -1) 
	  {
          Thread.sleep(1); // We need to get an InterruptedException if canceled
	  content_buffer.write (data, 0, nRead);
          totalRead += nRead;
          if (totalRead > 1024)
            statusHandler.writeMessage (Strings.LOADED + " " 
              + (totalRead / 1024) + " kb");
	  }
	}
      catch (java.net.SocketException e)
	{
	}

      content = content_buffer.toByteArray();

      content_buffer.close();
      s.close();
      s = null;

      return content;
      }
    catch (IOException e)
     {
     throw e; 
     }
    catch (Exception e2)
     {
     throw new IOException (e2);
     }
    }

  @Override
  public InputStream getInputStream() 
      throws IOException 
    {
    connect();
    return is;
    }
  }


