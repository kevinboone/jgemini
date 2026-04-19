/*=========================================================================
  
  JGemini

  FeedPost 

  Copyright (c)2026 Kevin Boone, GPLv3.0 

=========================================================================*/
package me.kevinboone.jgemini.base;

import java.net.*;
import java.util.*;

/** FeedPost represents a post from a feed. It's just a carrier for
    URL, date, and id (title), and some logic to compare these
    items for sorting purposes. */
public class FeedPost implements Comparable
  {
  private Date date;
  private URL url;
  private String articleId;

  public FeedPost (Date date, URL url, String articleId)
    {
    this.date = date;
    this.url = url;
    this.articleId = articleId;
    }

  /** compareTo is tricky here. We want to sort by date, but we don't want
      to remove posts with the same date -- we want to remove posts with
      the same URL. So we compare the dates and, only if they're equal,
      we compare URLs. This process will fail if the same post URL
      appears in different sources on different days. */
  public int compareTo (Object o)
    {
    FeedPost other = (FeedPost)o;
    int c = other.date.compareTo (date);
    if (c != 0) return c;
    return url.toString().compareTo (other.url.toString());
    }

  public String getArticleId()
    {
    return articleId;
    }

  public Date getDate()
    {
    return date;
    }

  public URL getUrl()
    {
    return url;
    }
  }

