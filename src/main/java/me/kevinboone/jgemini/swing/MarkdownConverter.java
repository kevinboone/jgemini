/*=========================================================================
  
  JGemini

  MarkdownConverter

  A class for converting Markdown to HTML 

  Copyright (c)2021 Kevin Boone, GPLv3.0 

=========================================================================*/
package me.kevinboone.jgemini.swing;
import java.net.*;
import java.io.*;
import java.util.regex.Pattern;

public class MarkdownConverter
  {
  private boolean verbatim;
  private URL baseUrl;
  private static Pattern italicPattern = Pattern.compile ("_(.*?)_");
  private static Pattern boldPattern = Pattern.compile ("\\*(.*?)\\*");
  private static Pattern linkPattern = Pattern.compile ("\\[(.*?)\\]\\((.*?)\\)");

  /** Construct a GemConverter, supplying a base URL. We need the URL so
      we can construct proper links. */
  public MarkdownConverter (URL baseUrl)
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
    String s = italicPattern.matcher(line).replaceAll("<em>$1</em>");
    s = boldPattern.matcher(s).replaceAll("<b>$1</b>");
    s = linkPattern.matcher(s).replaceAll("<a href=\"$2\">$1</a>");
    return s;
    }

  /** Convert a single (maybe long) Gemtext line to HTML. */
  private String lineToHtml (String gem)
    {
    if (gem.startsWith ("    "))
       {
       verbatim = true;
       return "<pre>" + gem.substring (4);
       }
    else
       {
       if (verbatim == true)
         {
         verbatim = false;
         return "</pre><p></p>\n";
         }
       }

    verbatim = false;
    if (gem.startsWith (">"))
      return "<blockquote>" + escapeHtml(gem.substring(1)) + "</blockquote>\n";
    if (gem.startsWith ("# "))
      return "<h1>" + escapeHtml(gem.substring(2)) + "</h1>\n";
    if (gem.startsWith ("## "))
      return "<h2>" + escapeHtml(gem.substring(3)) + "</h2>\n";
    if (gem.startsWith ("### "))
      return "<h3>" + escapeHtml(gem.substring(4)) + "</h3>\n";
    if (gem.startsWith ("- "))
      return "<ul><li>&nbsp;" + escapeHtml(gem.substring(2)) + "</li></ul>\n";
    if (gem.length() == 0) return "<p></p>\n";
      
    return formatLineAsHtml (escapeHtml(gem)) + "\n";
    }

  /** Convert the Markdown file to HTML. */ 
  public String markdownToHtml (String md, String encoding)
    {
    //System.out.println ("md=" + md);
    StringBuffer sb = new StringBuffer();
    String lines[] = md.split ("\n");
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

      byte[] md = content_buffer.toByteArray();

      content_buffer.close();

      MarkdownConverter gc = new MarkdownConverter (null);

      System.out.println (gc.markdownToHtml (new String(md), null));

      fis.close();
      }
    else
      {
      System.out.println 
        ("Usage: java MarkdownConverter {markdown_file}");
      }
    }
  }



