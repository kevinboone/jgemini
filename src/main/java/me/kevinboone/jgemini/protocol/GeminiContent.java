package me.kevinboone.jgemini.protocol;
import java.net.URL;

public class GeminiContent 
  {
  private byte[] content;
  private String mime;
  private Exception exception;
  private URL url;

  public GeminiContent (URL url)
    {
    this.url = url;
    mime = null;
    exception = null;
    content = null;
    }

  public String getMime() { return mime; }
  public Exception getException() { return exception; }
  public byte[] getContent() { return content; }

  public void setMime (String mime) { this.mime = mime; }
  public void setException (Exception exception) { this.exception = exception; }
  public void setContent (byte[] content) { this.content= content; }

  }
