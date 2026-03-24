/*============================================================================

  JGemini

  GeminiConnection

  Copyright (c)2021-2026 Kevin Boone, GPLv3.0

============================================================================*/
package me.kevinboone.jgemini.ssl;

import java.io.*;
import javax.net.ssl.*;
import java.security.cert.X509Certificate;
import java.net.*;
import java.util.Collections;
import java.security.KeyStore;
import me.kevinboone.jgemini.base.Config;

/*============================================================================

  ClientCertManager

  Methods for handling client certificates that are associated with
  a specific remote resource.

============================================================================*/
public class ClientCertManager
{

/*============================================================================

  getKMFForURL

  Given the URL of a remote resource, return a configure KeyManagerFactory,
  that can be passed to SSLContext.init()

  It's fine for the return value to be null, and it should be if the
  remote resource does not have an assigned client certificate. We'll
  throw an IOException if, for example, there is an assigned certificate
  but it can't be loaded.

============================================================================*/
public static KeyManagerFactory getKMFForURL (URL url) throws IOException
  {
  Config config = Config.getConfig();
  String clientCertSpec = config.getClientCertSpecForHost (url.getHost());
  if (clientCertSpec == null) return null;

  String[] tokens = clientCertSpec.trim().split ("\\s+"); 
  if (tokens.length != 2) throw new IOException 
    ("Bad client certificate specification: " + clientCertSpec);

  String clientCertKeyStoreFile = tokens[0]; 
  String clientCertKeyStorePassword = tokens[1]; 

  try
    {
    KeyStore keyStore = KeyStore.getInstance (KeyStore.getDefaultType());  
    FileInputStream fis = new FileInputStream (clientCertKeyStoreFile);
    keyStore.load (fis, clientCertKeyStorePassword.toCharArray());  
    fis.close();
    KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());  
    kmf.init (keyStore, clientCertKeyStorePassword.toCharArray()); 
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

}

