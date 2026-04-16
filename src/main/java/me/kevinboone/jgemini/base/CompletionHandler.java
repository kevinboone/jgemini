/*============================================================================

  JGemini

  CompletionHandler 

  Copyright (c)2021-2026 Kevin Boone, GPLv3.0

============================================================================*/
package me.kevinboone.jgemini.base;

import java.io.*;
import java.net.*;
import java.util.*;

/** An interface that is passed to various methods that run asynchronously,
    and then do something when they have completed. For example, remote 
    documents are transferred asynchronously, and then the user interface
    has to be updated once the download is complete. */
public interface CompletionHandler 
  {
  public void complete() throws Exception; 
  }

