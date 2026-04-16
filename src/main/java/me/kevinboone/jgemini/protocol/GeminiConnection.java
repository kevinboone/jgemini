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

/** A subclass of URLConnection that handles the Gemini protocol.
*/
public class GeminiConnection extends URLConnection
  {
  public static final int GEMINI_MAX_HEADER = 1024;
  private SSLSocket s = null;
  private String contentType = null;
  private InputStream is = null;
  private String meta = null;
  private static StatusHandler statusHandler = StatusHandler.getInstance();
  private ClientCertManager clientCertManager = 
    DefaultClientCertManager.getInstance();
  private final static ResourceBundle messagesBundle = 
    ResourceBundle.getBundle ("me.kevinboone.jgemini.bundles.Messages");
  private String certinfo = null;

/*============================================================================

  Constructor

============================================================================*/
  public GeminiConnection (URL url) 
    {
    super (url);
    Logger.in();
    Logger.out();
    }

/*============================================================================

  getRequestProperty

  We override this method so client classes can retrieve certificate
  information from this connection. It's a bit ugly, but there aren't
  many alternatives, when we embed our protocol handling into the
  JVM's infrastructure.  

============================================================================*/
  @Override
  public String getRequestProperty (String key)
    {
    if ("certinfo".equals (key)) return certinfo;
    return super.getRequestProperty (key);
    }

/*============================================================================

  parseStatus

============================================================================*/
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

/*============================================================================

  parseMeta

============================================================================*/
  private static String parseMeta (String line)
    {
    String[] args = line.split ("\\s+", 2); 
    if (args.length > 1)
      return args[1]; 
    else
      return "";
    }

/*============================================================================

  connect 

============================================================================*/
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

      // If we got this far, we must have had a reasonable SSL handshake.
      // So let's save the certificate information for future use.
      StringBuffer sb = new StringBuffer();
      SSLSession session = s.getSession();
      java.security.cert.Certificate[] certs = session.getPeerCertificates();
      int cl = certs.length;
      for (int i = 0; i < cl; i++)
        {
        java.security.cert.Certificate cert = certs[i];
        if (cert instanceof java.security.cert.X509Certificate)
          {
          java.security.cert.X509Certificate xcert = (java.security.cert.X509Certificate)cert;
          sb.append ("" + i);
          sb.append (":\nSubject: ");
          sb.append (xcert.getSubjectDN().toString());
          sb.append ("\nIssuer: ");
          sb.append (xcert.getIssuerDN().toString());
          sb.append ("\nExpires: ");
          sb.append (xcert.getNotAfter().toString());
          sb.append ("\nAlgorithm: ");
          sb.append (xcert.getSigAlgName());
          sb.append ("\n");
          }
        }
      certinfo = new String (sb); 
     
      int status = parseStatus (line);
      Logger.log (getClass().getName(), Logger.DEBUG, "Got status code " + status);
      meta = parseMeta (line);
      contentType = meta;
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

/*============================================================================

  getContentType 

============================================================================*/
  @Override
  public String getContentType()
    {
    return contentType;
    }
   
/*============================================================================

  getContent

============================================================================*/
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

/*============================================================================

  getInputStream

  This method forces a connection and the initital request. If the request
    results in an "OK" response, the input stream should be positioned
    at the start of the real content, ready to read.

============================================================================*/
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


