package me.kevinboone.jgemini.protocol;

import java.io.*;
import javax.net.ssl.*;
import java.security.cert.X509Certificate;
import java.net.*;

public class RetryWithInputException extends IOException 
  {
  URL url;
  boolean hide;
  String prompt;

  public RetryWithInputException (URL url, boolean hide, String prompt)
    {
    super ("Retry with input");
    this.url = url;
    this.hide = hide;
    this.prompt = prompt;
    }

  public URL getURL() { return url; }
  public boolean getHide() { return hide; }
  public String getPrompt() { return prompt; }
  }

