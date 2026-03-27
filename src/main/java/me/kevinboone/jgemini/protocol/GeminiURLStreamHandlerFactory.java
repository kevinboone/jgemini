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
    return null;
    }
  }

