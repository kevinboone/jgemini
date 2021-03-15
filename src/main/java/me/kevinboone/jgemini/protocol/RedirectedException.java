package me.kevinboone.jgemini.protocol;

import java.io.*;
import javax.net.ssl.*;
import java.security.cert.X509Certificate;
import java.net.*;

public class RedirectedException extends IOException 
  {
  URL url;

  public RedirectedException (URL url)
    {
    super ("Redirected to " + url.toString());
    this.url = url;
    }

  public URL getURL() { return url; }
  }


