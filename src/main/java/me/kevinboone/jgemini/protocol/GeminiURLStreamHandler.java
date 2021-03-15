package me.kevinboone.jgemini.protocol; 

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

public class GeminiURLStreamHandler extends URLStreamHandler
  {
  @Override
  protected URLConnection openConnection (URL url)
  throws IOException
    {
    return new GeminiConnection (url);
    }

  @Override
  protected int getDefaultPort ()
    {
    return 1965;
    }
  }
