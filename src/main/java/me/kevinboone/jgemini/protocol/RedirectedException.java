/*============================================================================

  JGemini

  RedirectedException 

  Copyright (c)2021-2026 Kevin Boone, GPLv3.0

============================================================================*/
package me.kevinboone.jgemini.protocol;

import java.io.*;
import javax.net.ssl.*;
import java.security.cert.X509Certificate;
import java.net.*;

/** A URLConnection subclass throws this exception when it gets a response
    indicating a redirection. The calling class is expected to catch the
    exception, and make the request again on the new URL. */
public class RedirectedException extends IOException 
  {
  URL url;

  public RedirectedException (URL url)
    {
    super ("Redirected to " + url.toString());
    this.url = url;
    }

  /** Get the URL the calling class is expected to re-issue
      the request on. */
  public URL getURL() { return url; }
  }


