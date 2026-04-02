/*=========================================================================
  
  JGemini

  AtomConverter

  A class for converting an Atom feed to HTML. 

  Copyright (c)2021 Kevin Boone, GPLv3.0 

=========================================================================*/
package me.kevinboone.jgemini.converters;
import java.net.*;
import java.io.*;
import java.util.Map;
import java.util.List;
import javax.xml.transform.*;
import javax.xml.transform.stream.*;
import me.kevinboone.jgemini.base.*;


public class AtomConverter implements Converter
  {
  private URL baseUrl;

  public AtomConverter (URL baseUrl)
    {
    Logger.in();
    this.baseUrl = baseUrl;
    Logger.out();
    }

  /** Convert the Markdown file to HTML. */ 
  public String toHtml (String xml)
    {
    Logger.in();
    String s = ""; 
    try
      {
      TransformerFactory transformerFactory = TransformerFactory.newInstance();
      InputStream isX = getClass().getClassLoader().getResourceAsStream ("xslt/atom.xslt");
      InputStream isT = new ByteArrayInputStream (xml.getBytes());
      Source xsltSource = new StreamSource (isX);
      Source xmlSource = new StreamSource (isT);
      Transformer transformer = transformerFactory.newTransformer (xsltSource);
      StreamResult result = new StreamResult();
      ByteArrayOutputStream boas = new ByteArrayOutputStream();
      result.setOutputStream (boas);
      transformer.transform (xmlSource, result);
      isX.close();
      isT.close();
      String ret = boas.toString();
      return ret;
      }
    catch (Exception e)
      {
      s = e.toString();
      }
    Logger.out();
    return s;
    }
  }




