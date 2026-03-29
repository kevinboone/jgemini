/*============================================================================

  JGemini

  AboutURLStreamHandler

  Copyright (c)2021-2026 Kevin Boone, GPLv3.0

============================================================================*/
package me.kevinboone.jgemini.protocol; 

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

public class AboutURLStreamHandler extends URLStreamHandler
  {
  @Override
  protected URLConnection openConnection (URL url)
  throws IOException
    {
    return new AboutConnection (url);
    }

  @Override
  protected int getDefaultPort ()
    {
    return -1;
    }
  }

