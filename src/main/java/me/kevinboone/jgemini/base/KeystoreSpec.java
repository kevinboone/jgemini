/*=========================================================================
  
  JGemini

  KeystoreSpec

  A simple carried for a keystore/password combination 

  Copyright (c)2026 Kevin Boone, GPLv3.0 

=========================================================================*/
package me.kevinboone.jgemini.base;

import java.net.*;
import java.io.*;
import java.util.*;
import javax.net.ssl.*;

public class KeystoreSpec 
  {
  private String keystore;
  private String password;

  public KeystoreSpec (String keystore, String password)
    {
    this.keystore = keystore;
    this.password = password;
    }

  public String getKeystore() { return keystore; }
  public String getPassword() { return password; }

  }

