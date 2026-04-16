/*=========================================================================
  
  JGemini

  MarkdownConverter

  Copyright (c)2021 Kevin Boone, GPLv3.0 

=========================================================================*/
package me.kevinboone.jgemini.converters;
import java.net.*;
import java.io.*;
import java.util.Map;
import java.util.List;
import me.kevinboone.jgemini.base.*;
import org.commonmark.Extension;
import org.commonmark.node.*;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.commonmark.renderer.html.AttributeProviderFactory;
import org.commonmark.renderer.html.AttributeProvider;
import org.commonmark.renderer.html.AttributeProviderContext;
import org.commonmark.ext.gfm.tables.TablesExtension;


/** LinkAttributeProvider is a helper class that rewrites relative links to
    absolute links, based on the base URL of the page. Java's built-in renderer
    will not do this itself. */
class LinkAttributeProvider implements AttributeProvider 
  {
  private URL baseUrl;
  LinkAttributeProvider (URL baseUrl)
    {
    this.baseUrl = baseUrl;
    }

  @Override
  public void setAttributes (Node node, String tagName, 
       Map<String, String> attributes) 
    {
    if (node instanceof Image) 
      {
      Config config = Config.getConfig();
      String oldSrc = attributes.get ("src");
      attributes.put("width", "" + config.getInlineImageWidth());
      try
        {
        URL src = new URL (baseUrl, oldSrc);
        attributes.put("src", src.toString());
        }
      catch (MalformedURLException e)
        {
        attributes.put("src", oldSrc);
        }
      }
    else if (node instanceof Link) 
      {
      String oldHref = attributes.get ("href");
      try
        {
        URL href = new URL (baseUrl, oldHref);
        attributes.put("href", href.toString());
        }
      catch (MalformedURLException e)
        {
        attributes.put("href", oldHref);
        }
      }
    }
  }

/** A class for converting Markdown to HTML. This is based on the
    commonmark-java library, but with some modifications to handle
    things like link rewriting. */
public class MarkdownConverter implements Converter
  {
  private boolean verbatim;
  private URL baseUrl;
  // The TablesExtension adds support for GitHub-style tables.
  private static List<Extension> extensions = List.of(TablesExtension.create());
  private static Parser parser = Parser.builder().extensions(extensions).build();
  private HtmlRenderer renderer;

  /** Construct a GemConverter, supplying a base URL. We need the URL so
      we can construct proper links. */
  public MarkdownConverter (URL baseUrl)
    {
    this.baseUrl = baseUrl;
    // Create the renderer with our AttributeProvider in place.
    renderer = HtmlRenderer.builder()
        .attributeProviderFactory(new AttributeProviderFactory() {
            public AttributeProvider create (AttributeProviderContext context) {
                return new LinkAttributeProvider (baseUrl);
            }
        }) .extensions(extensions).build();
    }

  /** Convert the Markdown file to HTML. */ 
  public String toHtml (String md)
    {
    Node document = parser.parse(md);
    String s = renderer.render(document);
    return s;
    }
  }



