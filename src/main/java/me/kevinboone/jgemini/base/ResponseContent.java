/*============================================================================

  JGemini

  ResponseContent 

  A carrier for data received from a URL handler, including the exception,
  if any.

  Copyright (c)2021-2026 Kevin Boone, GPLv3.0

============================================================================*/
package me.kevinboone.jgemini.base;
import java.net.URL;

public class ResponseContent 
  {
  private byte[] content;
  private String mime;
  private Exception exception;
  private URL url;

  public ResponseContent (URL url)
    {
    this.url = url;
    mime = null;
    exception = null;
    content = null;
    }

  public URL getURL() { return url; }
  public String getMime() { return mime; }
  public Exception getException() { return exception; }
  public byte[] getContent() { return content; }

  public void setMime (String mime) { this.mime = mime; }
  public void setException (Exception exception) { this.exception = exception; }
  public void setContent (byte[] content) { this.content= content; }

  }
