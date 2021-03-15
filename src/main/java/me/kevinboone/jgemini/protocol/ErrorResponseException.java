package me.kevinboone.jgemini.protocol;

import java.io.*;
import javax.net.ssl.*;
import java.security.cert.X509Certificate;
import java.net.*;

public class ErrorResponseException extends IOException 
  {
  URL url;
  int status;

  public ErrorResponseException (URL url, int status, String message)
    {
    super (message);
    this.url = url;
    this.status = status;
    }

  public URL getURL() { return url; }
  public int getStatus() { return status; }
  }


