/*============================================================================

  JGemini

  ResponseContent 

  A carrier for data received from a URL handler, including the exception,
  if any.

  Copyright (c)2021-2026 Kevin Boone, GPLv3.0

============================================================================*/
package me.kevinboone.jgemini.base;
import java.net.URL;

/** Holds all the information that came from an asynchronous file transfer.
    This includes the data and the MIME type, and any exception that was
    thrown. Transfers are asynchronous, so the background thread that does
    the transfer bundles up the content in single instance of this class,
    and passes it to the user interface when completed. */ 
public class ResponseContent 
  {
  private byte[] content;
  private String mime;
  private Exception exception;
  private URL url;
  private String certinfo;

  public ResponseContent (URL url)
    {
    this.url = url;
    mime = null;
    exception = null;
    content = null;
    }

  public String getCertinfo() { return certinfo; } // Might validly by null
  public byte[] getContent() { return content; }
  public Exception getException() { return exception; }
  public String getMime() { return mime; }
  public URL getURL() { return url; }

  public void setCertinfo (String certinfo) { this.certinfo = certinfo; }
  public void setContent (byte[] content) { this.content= content; }
  public void setMime (String mime) { this.mime = mime; }
  public void setException (Exception exception) { this.exception = exception; }

  }
