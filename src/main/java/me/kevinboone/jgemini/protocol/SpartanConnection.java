/*============================================================================

  JGemini

  SpartanConnection

  Copyright (c)2021-2026 Kevin Boone, GPLv3.0

============================================================================*/
package me.kevinboone.jgemini.protocol;

import java.io.*;
import java.net.*;
import java.util.*;
import me.kevinboone.jgemini.base.*;

/** A subclass of URLConnection that handles the Spartan protocol. */
public class SpartanConnection extends URLConnection
  {
  private Socket s = null;
  private String contentType = null;
  private InputStream is = null;
  private String meta = null;
  private static StatusHandler statusHandler = StatusHandler.getInstance();
  private final static ResourceBundle messagesBundle = 
    ResourceBundle.getBundle ("me.kevinboone.jgemini.bundles.Messages");

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
    contentType = meta;
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
    Logger.in();
    connect();
    BufferedInputStream bis = new BufferedInputStream (getInputStream());
    Logger.out();
    return bis;
    }

  @Override
  public InputStream getInputStream() 
      throws IOException 
    {
    connect();
    return is;
    }
  }


