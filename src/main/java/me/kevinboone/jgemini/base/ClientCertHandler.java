/*=========================================================================
  
  JGemini

  ClientCertHandler

  The interface governing user interface elements that manage
    identity.

  Not to be confused with ClientCertManager.

  Copyright (c)2026 Kevin Boone, GPLv3.0 

=========================================================================*/
package me.kevinboone.jgemini.base;

import java.net.*;
import java.io.*;

public interface ClientCertHandler
  {
  /** Bring up a user interface element to manage the
      identity associated with the URL. The URL may
      not be valid: implementing classes should check.*/
  public void manageIdentity (URL baseUri);
  }


