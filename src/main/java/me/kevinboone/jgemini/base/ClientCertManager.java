/*=========================================================================
  
  JGemini

  ClientCertManager 

  Copyright (c)2026 Kevin Boone, GPLv3.0 

=========================================================================*/
package me.kevinboone.jgemini.base;

import java.net.*;
import java.io.*;
import java.util.*;
import javax.net.ssl.*;

/** The interface that governs client certificate management. */
public interface ClientCertManager 
  {
  /* Adds a new identity; that is, adds a mappingg between the identity
     name, and the corresponding client certificate specification. */
  public void addIdent (String name, KeystoreSpec keystoreSpec);
 
  /** 
  Gets the Keystore specification (keystore/password) for
  the specified ident, or null if there is not one. */
  public KeystoreSpec getKeystoreSpecForIdent (String ident);
 
  /**
  Given the URL of a remote resource, return a configured KeyManagerFactory,
  that can be passed to SSLContext.init().  It's fine for the return 
  value to be null, and it should be if the remote resource does 
  not have an assigned client certificate. We'll throw an IOException 
  if, for example, there is an assigned certificate but it can't be loaded.*/
  public KeyManagerFactory getKMFForURL (URL url) throws IOException;

  /**
  Returns some opaque identifier string for a URL. It could be a filename,
  perhaps, but it need not be. */
  public String getIdentForURL (URL url);

  /**
  Returns the names of all the known identities. Does not include "all" or
  "unassigned" -- just the real ones. */
  public Set<String> getIdents();

  /** 
  Removes an ident for the specified url.
  */
  public void removeIdentForURL (URL url);

   /** Sets the identity for a specific URL to "none", so it does not
       even get the fallback identity.  */
  public void setNoneIdentForURL (URL url);

   /** Sets the identity for a specific URL. */
  public void setIdentForURL (URL url, String ident);
  }

