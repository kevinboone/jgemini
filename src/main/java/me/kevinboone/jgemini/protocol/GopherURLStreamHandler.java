/*============================================================================

  JGemini

  GopherURLStreamHandler

  Copyright (c)2021-2026 Kevin Boone, GPLv3.0

============================================================================*/
package me.kevinboone.jgemini.protocol; 

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

/** A URLStreamHandler that instantiates GopherConnection. 
*/
public class GopherURLStreamHandler extends URLStreamHandler
  {
  @Override
  protected URLConnection openConnection (URL url)
  throws IOException
    {
    return new GopherConnection (url);
    }

  @Override
  protected int getDefaultPort ()
    {
    return 70;
    }
  }


