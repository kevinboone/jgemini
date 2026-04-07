/*============================================================================

  JGemini

  GeminiConnection

  Copyright (c)2021-2026 Kevin Boone, GPLv3.0

============================================================================*/
package me.kevinboone.jgemini.protocol;

import java.io.*;
import javax.net.ssl.*;
import java.security.cert.X509Certificate;
import java.net.*;
import java.util.*;
import java.util.Collections;
import java.security.KeyStore;
import me.kevinboone.jgemini.ssl.*;
import me.kevinboone.jgemini.base.*;

public class GeminiConnection extends URLConnection
  {
  public static final int GEMINI_MAX_HEADER = 1024;
  private SSLSocket s = null;
  private String contentType = null;
  private InputStream is = null;
  private byte[] content = null;
  private String meta = null;
  private static StatusHandler statusHandler = StatusHandler.getInstance();
  private ClientCertManager clientCertManager = 
    DefaultClientCertManager.getInstance();
  private final static ResourceBundle messagesBundle = 
    ResourceBundle.getBundle ("me.kevinboone.jgemini.bundles.Messages");

  public GeminiConnection (URL url) 
    {
    super (url);
    Logger.in();
    Logger.out();
    }

  private static int parseStatus (String line)
    {
    Logger.in();
    String[] args = line.split ("\\s+", 2); 
    try 
      {
      int status = Integer.parseInt (args[0]);
      Logger.out();
      return status;
      }
    catch (NumberFormatException e) 
      {
      Logger.out();
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

  private void readToData()
    {
    }

  @Override
  public void connect() 
      throws IOException 
    {
    Logger.in();
    if (s != null && s.isConnected()) return;
    try
      {
      // TODO: make this certificate check configurable
      TrustManager[] trustAllCerts = new TrustManager[] 
	{ new X509TrustManager() 
	  {
	  public java.security.cert.X509Certificate[] getAcceptedIssuers() 
	    { return null; }
	  public void checkClientTrusted(X509Certificate[] certs, 
              String authType) { }
	   public void checkServerTrusted(X509Certificate[] certs, 
	       String authType) { }
	  }
	};
      String host = getURL().getHost();
      String path = getURL().getPath();
      int port = getURL().getPort();
      if (port == -1) port = 1965;

      KeyManagerFactory kmf = 
        clientCertManager.getKMFForURL (url); 

      SSLContext sc = SSLContext.getInstance("SSL");
      if (kmf != null)
        sc.init (kmf.getKeyManagers(), trustAllCerts, new java.security.SecureRandom());
      else
        sc.init (null, trustAllCerts, new java.security.SecureRandom());
      s = (SSLSocket)sc.getSocketFactory().createSocket (host, port); 
      SSLParameters params = new SSLParameters();
      params.setServerNames (Collections.singletonList(new SNIHostName(host)));
      s.setSSLParameters(params);
      is = s.getInputStream();
      OutputStream os = s.getOutputStream();
      PrintStream pos = new PrintStream (os);

      if (Logger.isDebug())
        Logger.log (getClass().getName(), Logger.DEBUG, "Sending request: " + getURL().toString());
      pos.print (getURL().toString());
      pos.print ("\r\n"); 
      pos.flush();

      char c; 
      String line = ""; 
      int len = 0;
      do 
         {
         c = (char)is.read(); 
         len++;
         if (c == '\n')
           break; 
         if (c != '\r') line += c + "";
         } while (c != -1 && len <= GEMINI_MAX_HEADER);
      if (len >= GEMINI_MAX_HEADER)
        {
        String tooLong = messagesBundle.getString ("status_line_too_long");
        Logger.log (getClass().getName(), Logger.WARNING, 
          tooLong + ", URI=" +  getURL());
        throw new IOException (tooLong + ", URI=" +  getURL());
        }
     
      int status = parseStatus (line);
      Logger.log (getClass().getName(), Logger.DEBUG, "Got status code " + status);
      meta = parseMeta (line);
      if (status >= 20 && status < 30)
        {
        // Nothing to do -- input stream is now positioned
        //  to read data
        }
      else
        {
        s.close();
        s = null;
        if (status < 10) 
          {
          throw new ErrorResponseException (getURL(), status, 
              "Invalid status code in response");
          }
        if (status >= 10 && status < 20)
          {
          throw new RetryWithInputException (getURL(), status == 11, meta);
          }
        else if (status >= 30 && status < 40)
          {
          Logger.log (getClass().getName(), Logger.DEBUG, "Throwing a redirect to " + meta);
          throw new RedirectedException (new URL(getURL(), meta));
          }
        else if (status >= 40)
          {
          throw new ErrorResponseException (getURL(), status, meta);
          }
        }
      }
    catch (java.security.NoSuchAlgorithmException e)
      {
      throw new IOException (e);
      }
    catch (java.security.KeyManagementException e)
      {
      throw new IOException (e);
      }
    Logger.out();
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
            statusHandler.writeMessage (messagesBundle.getString ("loaded") + " " 
              + (totalRead / 1024) + " kb");
	  }
	}
      catch (java.net.SocketException e)
	{
	}
      catch (javax.net.ssl.SSLException e)
	{
	// I think that sometimes the server closes its end of the
	//  socket so abrubptly that isConnected() can say that the
	//  connection is still open, but the following read() can
	//  fail. We don't even get to a position where the read()
	//  returns -1 to indicate EOT. But do we need to distinguish
	//  this from "real" SSL errors? I really don't know. 
	}

      content = content_buffer.toByteArray();

      content_buffer.close();
      s.close();
      s = null;

      Logger.out();
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
    Logger.in();
    connect();
    Logger.out();
    return is;
    }
  }


