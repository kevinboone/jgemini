/*============================================================================

  JGemini

  GopherConnection

  Copyright (c)2021-2026 Kevin Boone, GPLv3.0

============================================================================*/
package me.kevinboone.jgemini.protocol;

import java.io.*;
import java.net.*;
import java.util.*;
import me.kevinboone.jgemini.base.*;
import me.kevinboone.utils.file.ContentGuesser;

public class GopherConnection extends URLConnection
  {
  private Socket s = null;
  private String contentType = null;
  private InputStream is = null;
  private byte[] content = null;
  private String meta = null;
  private static StatusHandler statusHandler = StatusHandler.getInstance();
  private char gopherType = '0';
  private final static ResourceBundle messagesBundle = 
    ResourceBundle.getBundle ("me.kevinboone.jgemini.bundles.Messages");

  public GopherConnection (URL url) 
    {
    super (url);
    }

  @Override
  public void connect() 
      throws IOException 
    {
    if (s != null && s.isConnected()) return;
    String host = getURL().getHost();
    String path = getURL().getPath();
    String query = getURL().getQuery();
    int port = getURL().getPort();
    if (port == -1) port = 70;

    s = new Socket (host, port);
    is = s.getInputStream();
    OutputStream os = s.getOutputStream();
    PrintStream pos = new PrintStream (os);

    String request = path; 
    if (request.length() > 1)
      {
      if (request.startsWith ("/"));
        request = request.substring(1);
      }

    if (request.length() >= 2)
      {
      gopherType = request.charAt(0);
      request = request.substring(1);
      }

    if (query != null && query.length() > 0)
      request = request + "\t" + query;

    pos.print (request);

    pos.print ("\r\n"); 
    pos.flush();
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

      String path = url.getPath();
      if (path.length() == 0 || path.equals ("/"))
        contentType = "text/gophermap";
      else
        {
        contentType = ContentGuesser.guessMimeTypeFromFilename (url.getPath()); 
        //System.out.println ("ct = " + contentType + " gt=" + gopherType);
	if (contentType == null) 
	  {
	  if (gopherType == '0')
	    contentType = "text/plain"; 
	  else if (gopherType == 'g')
	    contentType = "image/gif"; 
	  else
	    contentType = "text/gophermap"; // TODO
	  }
        }

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
            statusHandler.writeMessage (messagesBundle.getString ("loaded") + " " 
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


