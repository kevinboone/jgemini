/*=========================================================================
  
  JGemini

  TextLikeConverter

  Base class for converters that basically handle text, but have a 
  specific "=>" syntax for links. Gemtext and the Nex variety of plain
  text both use this format.

  Copyright (c)2026 Kevin Boone, GPLv3.0 

=========================================================================*/
package me.kevinboone.jgemini.converters;
import java.net.*;
import java.io.*;
import java.util.regex.Pattern;
import me.kevinboone.jgemini.base.*;
import net.fellbaum.jemoji.*;

public class TextLikeConverter
  {
  protected URL baseURL;

  public TextLikeConverter (URL baseURL)
    {
    this.baseURL = baseURL;
    }

  public static boolean isImageUri (String uri)
    {
    if (uri.endsWith(".gif") || uri.endsWith (".jpg") || 
        uri.endsWith (".png") || uri.endsWith (".jpeg"))
      return true;
    return false;
    }

  /** Convert common punctuation like & into HTML-friendly forms. */
  protected static String escapeHtml (String gem)
    {
    String s = gem.replace (">", "£££gt;");
    s = s.replace ("<", "£££lt;");
    s = s.replace ("&", "&amp;");
    s = s.replace ("£££", "&");
    return s;
    }

  private String getLinkIcon (String link, String text)
    {
    if (text == null) return "";
    if (text.length() < 2) return "";
    if (EmojiManager.isEmoji(text.substring(0,2))) 
      return ""; // Don't decorate an emoji
    if (isImageUri (link))
      return "📷";
    if (link.startsWith ("http"))
      return "🌍";
    return "→";
    }

  protected String writeLink (String uri, String title)
    {
    Config config = Config.getConfig();
    if (isImageUri (uri) && config.gemtextInlineImages())
      {
      return "<p><img width=\"" + config.inlineImageWidth() + "\" src=\"" 
        + rewriteLink (uri) + "\">" + "<br/>" + "<a href=\"" 
           + rewriteLink (uri) + "\">" + getLinkIcon (uri, title) + " " 
             + escapeHtml (title) + "</a><br/></p>"; 
      }
    return "<a href=\"" + rewriteLink (uri) + "\">" + 
      getLinkIcon (uri, title) + " " + escapeHtml (title) + "</a><br/>"; 
    } 

  /** Parse and convert a Gemtext link line. */
  protected String parseLink (String gem)
    {
    String[] args = gem.split ("\\s+", 2);
    if (args.length >= 2)
      {
      return writeLink (args[0], args[1]);
      }
    else if (args.length == 1)
      {
      return writeLink (args[0], args[0]);
      }
    else
      {
      // Can not happen
      return "";
      }
    }

  /** Given a link target, rewrite it to a complete link that can
      be parsed to a java.net.URL. This involves resolving it against the
      baseURL to allow for relative links, etc. */
  protected String rewriteLink (String link)
    {
    Logger.in();
    Logger.log (getClass().getName(), Logger.DEBUG, "old link=" + link);
    try
      {
      // I'm still not 100% sure about this
      URI newUri =  new URI (baseURL.toString());
      newUri =  newUri.resolve(link); 
      Logger.log (getClass().getName(), Logger.DEBUG, "new link=" + newUri);
      Logger.out();
      return newUri.toString();
      }
    catch (Exception e)
      {
      Logger.out();
      e.printStackTrace();
      return link;
      }
    }



  }

