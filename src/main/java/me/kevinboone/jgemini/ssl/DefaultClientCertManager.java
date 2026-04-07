/*============================================================================

  JGemini

  DefaultClientCertManager 

  Methods for handling client certificates that are associated with
  a specific remote resource.

  Copyright (c)2021-2026 Kevin Boone, GPLv3.0

============================================================================*/
package me.kevinboone.jgemini.ssl;

import java.io.*;
import javax.net.ssl.*;
import java.net.*;
import java.util.*;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.security.KeyStore;
import me.kevinboone.jgemini.Constants;
import me.kevinboone.jgemini.base.*;

public class DefaultClientCertManager implements ClientCertManager
{
private Config config = Config.getConfig();
private static ClientCertManager instance = null;

/* Inaccessible constructor */
private DefaultClientCertManager() {}

/*============================================================================
  
  addIdent 

============================================================================*/
  @Override
  public void addIdent (String name, KeystoreSpec keystoreSpec)
    {
    config.addClientCert (name, keystoreSpec.getKeystore(),
      keystoreSpec.getPassword());
    }

/*============================================================================
  
  getInstance

  Gets the Keystore specification (keystore/password) for
  the specified ident, or null if there is not one.

============================================================================*/
  public KeystoreSpec getKeystoreSpecForIdent (String ident)
    {
    return config.getKeystoreSpecForIdent (ident);
    }
 
/*============================================================================
  
  getInstance

============================================================================*/
public static ClientCertManager getInstance()
  {
  if (instance == null)
    instance = new DefaultClientCertManager();
  return instance;
  }

/*============================================================================
  
  getIdentForURL

============================================================================*/
@Override
public String getIdentForURL (URL url)
  {
  String ident = null;
  String host = url.getHost();
  if (host != null && host.length() != 0)
    {
    ident = config.getProperty (Constants.IDENT_TAG + host);
    }
  if (ident == null)
    {
    ident = config.getProperty ("ident.any");
    }
  return ident;
  }

/*============================================================================

  getIdents 

============================================================================*/
  @Override
  public Set<String> getIdents()
    {
    return config.getIdents();
    }

/*============================================================================

  getKMFForURL

  Given the URL of a remote resource, return a configure KeyManagerFactory,
  that can be passed to SSLContext.init()

  It's fine for the return value to be null, and it should be if the
  remote resource does not have an assigned client certificate. We'll
  throw an IOException if, for example, there is an assigned certificate
  but it can't be loaded.

============================================================================*/
@Override
public KeyManagerFactory getKMFForURL (URL url) throws IOException
  {
  Logger.in();
  String ident = getIdentForURL (url);
  if (ident == null)
    {
    if (Logger.isDebug())
      Logger.log (getClass().getName(), Logger.DEBUG,
        "No identity for URL " + url);
    Logger.out();
    }

  KeystoreSpec keystoreSpec = getKeystoreSpecForIdent (ident);

  if (keystoreSpec == null)
    {
    // No ident. Let's see if there's a client cert spec called "any"
    keystoreSpec = getKeystoreSpecForIdent ("any");
    if (keystoreSpec == null)
      return null; 
    }

  String clientCertKeyStoreFile = keystoreSpec.getKeystore(); 
  String clientCertKeyStorePassword = keystoreSpec.getPassword(); 

  try
    {
    KeyStore keyStore = KeyStore.getInstance (KeyStore.getDefaultType());  
    FileInputStream fis = new FileInputStream (clientCertKeyStoreFile);
    keyStore.load (fis, clientCertKeyStorePassword.toCharArray());  
    fis.close();
    KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());  
    kmf.init (keyStore, clientCertKeyStorePassword.toCharArray()); 
    Logger.out();
    return kmf;
    }
  catch (IOException e)
    {
    throw e; 
    }
  catch (Exception e)
    {
    throw new IOException (e.getMessage()); 
    }
  }

/*============================================================================

  removeIdentForUrl 

============================================================================*/
  @Override
  public void removeIdentForURL (URL url)
    {
    String hostname = url.getHost();
    if (hostname != null && hostname.length() > 0)
      {
      config.removeIdent (hostname); 
      config.save();
      }
    }

/*============================================================================

  removeIdentForUrl 

  Sets the identity for a specific URL to "none", so it does not
  even get the fallback identity. 

============================================================================*/
  public void setNoneIdentForURL (URL url)
    {
    setIdentForURL (url, "none");
    }

/*============================================================================

  removeIdentForUrl 

  Sets the identity for a specific URL. 

============================================================================*/
  public void setIdentForURL (URL url, String ident)
    {
    String hostname = url.getHost();
    if (hostname != null && hostname.length() > 0)
      {
      config.setIdentForHostname (hostname, ident); 
      config.save();
      }
    }
}

