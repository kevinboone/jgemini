package me.kevinboone.jgemini.protocol;

import java.io.*;
import javax.net.ssl.*;
import java.security.cert.X509Certificate;
import java.net.*;

public class GeminiConnection extends URLConnection
  {
  private SSLSocket s = null;
  private String contentType = null;
  private InputStream is = null;
  private byte[] content = null;
  private String meta = null;

  public GeminiConnection (URL url) 
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

  private void readToData()
    {

    }

  @Override
  public void connect() 
      throws IOException 
    {
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
      //System.out.println ("host=" + host);
      //System.out.println ("port=" + port);
      //System.out.println ("path=" + path);
      SSLContext sc = SSLContext.getInstance("SSL");
      sc.init (null, trustAllCerts, new java.security.SecureRandom());
      s = (SSLSocket)sc.getSocketFactory().createSocket (host, port); 
      is = s.getInputStream();
      OutputStream os = s.getOutputStream();
      PrintStream pos = new PrintStream (os);

      pos.print (getURL().toString());
      pos.print ("\r\n"); 
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
          throw new RedirectedException (new URL(meta));
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

        contentType = meta;
	ByteArrayOutputStream content_buffer = new ByteArrayOutputStream();

	int nRead;
	byte[] data = new byte[16384];

        try
          {
	  while (s.isConnected() && 
               (nRead = is.read (data, 0, data.length)) != -1) 
	    {
	    content_buffer.write (data, 0, nRead);
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


