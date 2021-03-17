/*=========================================================================
  
  JGemini

  GemConverter

  A class for converting GemText to HTML 

  Copyright (c)2021 Kevin Boone, GPLv3.0 

=========================================================================*/
package me.kevinboone.jgemini.swing;
import java.net.*;
import java.io.*;
import java.util.regex.Pattern;

public class GemConverter
  {
  private boolean verbatim;
  private URL baseUrl;
  private static Pattern italicPattern = Pattern.compile ("\\s_(\\w+?)_\\s");
  private static Pattern boldPattern = Pattern.compile ("\\*(\\w+?)\\*");

  /** Construct a GemConverter, supplying a base URL. We need the URL so
      we can construct proper links. */
  public GemConverter (URL baseUrl)
    {
    verbatim = false;
    this.baseUrl = baseUrl;
    }

  /** Given a link target, rewrite it to a complete link that can
      be parsed to a java.net.URL. This involves resolving it against the
      baseURL to allow for relative links, etc. */
  private String rewriteLink (String link)
    {
    Logger.log (getClass(), "rewriteLink() link is " + link);
    try
      {
      // I'm still not 100% sure about this
      URI newUri =  new URI (baseUrl.toString());
      newUri =  newUri.resolve(link); 
      Logger.log (getClass(), "rewriteLink() newlink is " + newUri);
      return newUri.toString();
      }
    catch (Exception e)
      {
      e.printStackTrace();
      return link;
      }
    }

  /** Convert common punctuation like & into HTML-friendly forms. */
  public static String escapeHtml (String gem)
    {
    String s = gem.replace (">", "£££gt;");
    s = s.replace ("<", "£££lt;");
    s = s.replace ("&", "&amp;");
    s = s.replace ("£££", "&");
    return s;
    }

  /** Foo. */
  private String formatLineAsHtml (String line)
    {
    String s = italicPattern.matcher(line).replaceAll(" <em>$1</em> ");
    return boldPattern.matcher(s).replaceAll("<b>$1</b>");
    }

  /** Parse and convert a Gemtext link line. */
  private String parseLink (String gem)
    {
    String[] args = gem.split ("\\s+", 2);
    if (args.length >= 2)
      {
      String link = args[0];
      String text = args[1];
      return "<a href=\"" + rewriteLink (link) + "\">" 
        + escapeHtml(text) + "</a><br/>\n"; 
      }
    else if (args.length == 1)
      {
      String link = args[0];
      String text = args[0];
      return "<a href=\"" + rewriteLink (link) + "\">" 
        + escapeHtml(text) + "</a><br/>\n"; 
      }
    else
      {
      // Can not happen
      return "";
      }
    }

  /** Convert a single (maybe long) Gemtext line to HTML. */
  private String lineToHtml (String gem)
    {
    if (gem.startsWith ("```"))
      {
      if (verbatim)
        {
        verbatim = false;
        return "</pre>\n";
        }
      else
        {
        verbatim = true;
        return "<pre>\n";
        }
      }
    if (verbatim) return escapeHtml(gem) + "\n";
    if (gem.length() == 0) return "<br/>\n";
    if (gem.startsWith (">"))
      return "<blockquote>" + escapeHtml(gem.substring(1)) + "</blockquote>\n";
    if (gem.startsWith ("###"))
      return "<h3>" + escapeHtml(gem.substring(4)) + "</h3>\n";
    if (gem.startsWith ("##"))
      return "<h2>" + escapeHtml(gem.substring(3)) + "</h2>\n";
    if (gem.startsWith ("#"))
      return "<h1>" + escapeHtml(gem.substring(2)) + "</h1>\n";
    if (gem.startsWith ("*"))
      return "<ul><li>&nbsp;" + escapeHtml(gem.substring(2)) + "</li></ul>\n";
    if (gem.startsWith ("=>"))
      return parseLink (gem.substring(2).trim());
      
    //return "<p>" + escapeHtml(gem) + "</p>\n";
    return formatLineAsHtml (escapeHtml(gem)) + "<br/>\n";
    }

  // TODO -- write a META with the supplied encoding
  //  RIght now, I have no way to test this works, so you'll get
  //  whatever the built-in HTML browser default is. With luck, it will
  //  be platform encoding, because the method that calls this in
  //  HtmlViewer converts server text to platform encoding. So with luck
  //  it will all work out. The fly in the wossname is that I don't know
  //  what the browser uses, if you don't specify an encoding. Or, frankly,
  //  even if you do.
  /** Convert the Gemtext file to HTML. */ 
  public String gemToHtml (String gem, String encoding)
    {
    //System.out.println ("gem=" + gem);
    StringBuffer sb = new StringBuffer();
    String lines[] = gem.split ("\n");
    sb.append ("<html><head><body>\n");

    for (int i = 0; i < lines.length; i++)
      {
      String line = lines[i];
      String htmlLine = lineToHtml (line);
      sb.append (htmlLine);
      }

    sb.append ("</body></html>\n");
    //System.out.println ("html=" + new String(sb));
    return new String (sb);
    }

  public static void main (String[] args)
       throws Exception
    {
    if (args.length == 1)
      {
      FileInputStream fis = new FileInputStream (new File (args[0]));
      ByteArrayOutputStream content_buffer = new ByteArrayOutputStream();

      int nRead;
      byte[] data = new byte[16384];

      while ((nRead = fis.read (data, 0, data.length)) != -1) 
       content_buffer.write (data, 0, nRead);

      byte[] gmi = content_buffer.toByteArray();

      content_buffer.close();

      GemConverter gc = new GemConverter (null);

      System.out.println (gc.gemToHtml (new String(gmi), null));

      fis.close();
      }
    else
      {
      System.out.println 
        ("Usage: java GemConverter {gemtext_file}");
      }
    }
  }


