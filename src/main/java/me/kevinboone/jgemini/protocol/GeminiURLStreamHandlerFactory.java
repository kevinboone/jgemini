/*============================================================================

  JGemini

  GeminiURLStreamHandlerFactory

  Creates a stream handler for each protocol we support. The interface
  to the JVM platform is to pass an instance of this class to
  URL.setURLStreamHandlerFactory

  Copyright (c)2021-2026 Kevin Boone, GPLv3.0

============================================================================*/
package me.kevinboone.jgemini.protocol;
import java.net.*;
import java.io.*;

public class GeminiURLStreamHandlerFactory implements URLStreamHandlerFactory 
  {
  @Override
  public URLStreamHandler createURLStreamHandler(String protocol) 
    {
    if ("gemini".equals(protocol)) 
      {
      return new GeminiURLStreamHandler();
      }
    else if ("nex".equals(protocol)) 
      {
      return new NexURLStreamHandler();
      }
    else if ("spartan".equals(protocol)) 
      {
      return new SpartanURLStreamHandler();
      }
    else if ("gopher".equals(protocol)) 
      {
      return new GopherURLStreamHandler();
      }
    else if ("about".equals(protocol)) 
      {
      return new AboutURLStreamHandler();
      }
    return null;
    }
  }

