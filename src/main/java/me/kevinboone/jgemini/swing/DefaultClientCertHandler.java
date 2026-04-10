/*=========================================================================
  
  JGemini

  DefaultClientCertHandler

  The user interface for identity management. 

  Copyright (c)2026 Kevin Boone, GPLv3.0 

=========================================================================*/
package me.kevinboone.jgemini.swing;

import java.net.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import me.kevinboone.jgemini.base.*;
import me.kevinboone.jgemini.ssl.*;
import me.kevinboone.jgemini.Constants;

public class DefaultClientCertHandler 
    implements ClientCertHandler
  {
  private Config config;
  private MainWindow mainWindow;
  private ClientCertManager clientCertManager = 
    DefaultClientCertManager.getInstance();
  private final static ResourceBundle messagesBundle = 
    ResourceBundle.getBundle ("me.kevinboone.jgemini.bundles.Messages");

/*=========================================================================
  
  Constructor

=========================================================================*/
  DefaultClientCertHandler (MainWindow mainWindow)
    {
    config = Config.getConfig();
    this.mainWindow = mainWindow;
    }

/*=========================================================================
  
  manageIdentity 

  Bring up a user interface element to manage the
  identity associated with the URL. The URL may
  not be valid: we have to check.
 
=========================================================================*/
  public void manageIdentity (URL baseUri)
    {
    String hostname = baseUri.getHost();
    String protocol = baseUri.getProtocol();
    if (hostname == null || hostname.length() == 0)
      {
      mainWindow.reportGenError 
        (messagesBundle.getString ("ident_remote_only"));
      return;
      }

    if (protocol != null && protocol.equals ("gemini"))
      {
      IdentityDialog id = new IdentityDialog (mainWindow, 
        baseUri, mainWindow);
      id.show();
      }
    else
      {
      mainWindow.reportGenInfo (messagesBundle.getString ("protocol_no_ident") 
        + ": " + protocol); 
      return;
      }
    }

  }


