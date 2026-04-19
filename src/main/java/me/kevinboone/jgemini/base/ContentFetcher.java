/*=========================================================================
  
  JGemini

  ContentFetcher 

  Copyright (c)2026 Kevin Boone, GPLv3.0 

=========================================================================*/
package me.kevinboone.jgemini.base;

import java.net.*;
import java.io.*;
import java.util.*;
import me.kevinboone.jgemini.Constants;
import me.kevinboone.jgemini.protocol.*;

/** A helper class for downloading content in background threads. 
*/
public class ContentFetcher 
  {
  private final static ResourceBundle messagesBundle = 
    ResourceBundle.getBundle ("me.kevinboone.jgemini.bundles.Messages");

/*=========================================================================
  
  fetch

=========================================================================*/
  /** Like fetch (URL), but takes a String, and therefore has an
      additional mode of failure that the caller must anticipate. 
  */
  public static ResponseContent fetch (String url)
    {
    try
      {
      return fetch (new URL (url));
      }
    catch (MalformedURLException e)
      {
      ResponseContent rc = new ResponseContent (null);
      rc.setException (e);
      return rc;
      }
    }

/*=========================================================================
  
  fetch

=========================================================================*/
  /**
    Download from the specified URL, and store the result -- or the
    exception -- in a Responsecontent object. Because it's intended
    to be used asynchronously in a background thread, this method
    throws no exception. Instead, if something fails, the exception
    gets stored in the ResponseContent rather than actual content.
    Callers should be aware that this method stores the entire
    response in memory. It's not an appropriate method to use
    for large or unknown remote items.
  */
  public static ResponseContent fetch (URL url)
    {
    ResponseContent rc;
    rc = new ResponseContent (url);
    try
      {
      String sUrl = url.toString();
      URLConnection conn = url.openConnection();
      conn.connect();
      String contentType = conn.getContentType();
      if (!FileUtil.canHandleContent (sUrl, contentType))
        {
        throw new UnsupportedContentException (sUrl); 
        }

      Object o = conn.getContent();
      byte[] content;
      if (o instanceof InputStream)
        {
        InputStream is = (InputStream)o;
        content = FileUtil.readInputStreamFully 
          (messagesBundle.getString ("feed_aggregator"), is);
        is.close();
        }
      else
        {
        content = (byte[]) o; 
        }

      rc.setMime (contentType);
      rc.setContent (content);
      }
    catch (MalformedURLException e)
      {
      rc.setException (e);
      }
    catch (RedirectedException e)
      {
      // TODO check for redirect loops
      return fetch (e.getURL());
      }
    catch (IOException e)
      {
      rc.setException (e);
      }
    catch (UnsupportedContentException e)
      {
      rc.setException (e);
      }
    return rc;
    }

  }


