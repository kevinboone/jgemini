package me.kevinboone.jgemini.protocol;
import javax.net.ssl.*;
import java.security.cert.X509Certificate;
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
    return null;
    }
  }

