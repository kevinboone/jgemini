/*============================================================================

  JGemini

  RetryWithTimeoutException

  Copyright (c)2021-2026 Kevin Boone, GPLv3.0

============================================================================*/
package me.kevinboone.jgemini.protocol;

import java.io.*;
import javax.net.ssl.*;
import java.security.cert.X509Certificate;
import java.net.*;

/** This Exception is thrown by the Gemini and Spartan connection classes,
    when the server sends a response that indicates it wants some 
    additional data -- which usually means prompting the user for input. 
*/
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

  /** Get the URL that the calling class should retry the request to. */
  public URL getURL() { return url; }

  /** Indicates whether the user interface should conceal the user's 
      input. The value will depend on the specific response received from 
      the server. */
  public boolean getHide() { return hide; }

  /** Gets the prompt the user should be shown, if the server provided
      one. */
  public String getPrompt() { return prompt; }
  }

