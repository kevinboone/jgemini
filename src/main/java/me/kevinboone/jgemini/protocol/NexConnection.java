/*============================================================================

  JGemini

  NexConnection

  Copyright (c)2021-2026 Kevin Boone, GPLv3.0

============================================================================*/
package me.kevinboone.jgemini.protocol;

import java.io.*;
import java.net.*;
import java.util.*;
import me.kevinboone.jgemini.base.*;

/** A subclass of URLConnection that handles the nex: protocol.
*/
public class NexConnection extends URLConnection
  {
  private Socket s = null;
  private String contentType = null;
  private InputStream is = null;
  private static StatusHandler statusHandler = StatusHandler.getInstance();
  private final static ResourceBundle messagesBundle = 
    ResourceBundle.getBundle ("me.kevinboone.jgemini.bundles.Messages");

  public NexConnection (URL url) 
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
    int port = getURL().getPort();
    if (port == -1) port = 1900;

    s = new Socket (host, port);
    is = s.getInputStream();
    OutputStream os = s.getOutputStream();
    PrintStream pos = new PrintStream (os);

    pos.print (getURL().getPath().toString());
    pos.print ("\r\n"); 
    pos.flush();

    contentType = FileUtil.guessMimeTypeFromFilename (url.getPath()); 
    if (contentType == null) contentType = "text/nex";
    else if ("application/octet-stream".equals (contentType)) contentType = "text/nex";
    else if ("text/plain".equals (contentType)) contentType = "text/nex";
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

