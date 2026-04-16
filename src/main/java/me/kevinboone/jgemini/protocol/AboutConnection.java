/*============================================================================

  JGemini

  AboutConnection

  Copyright (c)2021-2026 Kevin Boone, GPLv3.0

============================================================================*/
package me.kevinboone.jgemini.protocol;

import java.io.*;
import java.net.*;
import java.util.Collections;
import me.kevinboone.jgemini.base.*;

/** A subclass of URLConnection that handles URLs of the form
    "about:/xxx". The data is just retrieved from the classloader,
    under the "about/" directory. At the source level, this content
    starts in "docs/", but the build script copies it to 
    "src/main/resources/about", where Maven puts it into "about/" in the
    JAR.
*/
public class AboutConnection extends URLConnection
  {
  private String contentType = null;
  private InputStream is = null;
  private byte[] content = null;
  private String meta = null;
  private static StatusHandler statusHandler = StatusHandler.getInstance();

  public AboutConnection (URL url) 
    {
    super (url);
    }

  @Override
  public void connect() 
      throws IOException 
    {
    if (is != null) return;
    String path = url.getPath();
    if (path.startsWith ("/")) path = path.substring(1);
    String relPath = "about/" + path;
    is = getClass().getClassLoader().getResourceAsStream (relPath);
    if (is == null) throw new IOException ("Path not found: " + relPath);
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

      contentType = "text/markdown";

      byte[] data = new byte[16384];

      int nRead;
      ByteArrayOutputStream content_buffer = new ByteArrayOutputStream();

      while ((nRead = is.read (data, 0, data.length)) != -1) 
	{
	content_buffer.write (data, 0, nRead);
	}

      content = content_buffer.toByteArray();

      content_buffer.close();
      is.close();
      is = null;

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


