/*=========================================================================
  
  JGemini

  FeedHandler 

  Copyright (c)2026 Kevin Boone, GPLv3.0 

=========================================================================*/
package me.kevinboone.jgemini.base;

import java.net.*;
import java.io.*;

/** The interface that defines a feed handler. A FeedHandler takes care
    of basic feed management, but also of aggregation of multiple
    feeds.
*/
public interface FeedHandler 
  {
  /** The listener will receive update about the status of feed
      aggregation, as the aggregator runs.
  */
  public void addFeedManagerStatusListener (FeedManagerStatusListener l);

  /** Cancel any ongoing update, which may mean killing a thread. */
  public void cancelUpdate();

  /** Returns true if the URI is definitely in the list, and
      false if not, or uncertain. This method throws no exceptions. */
  public boolean isInFeedList (URL uri);

  /** addFeed() returns true if a new feed is added. The only
      reason not to add the feed -- other than exception --
      is that the URI is already feeded. */
  public boolean addFeed (String displayName, URL uri) throws IOException;

  /** Bring up some kind of editor for the user to manage feeds. */
  public void editFeeds() throws IOException;

  /** Get the total number of feeds in our list. */
  public int getFeedCount();

  /** Get the URI for a specific feed. */
  public GemLink getFeedLink (int index);

  /** Returns true if the feed aggregator is running. */
  public boolean isRunning();

  public void removeFeedManagerStatusListener (FeedManagerStatusListener l);

  /** Start the feed aggregator (in the background). */
  public void start();

  /** Bring up some kind of list of all feeds for the user. */
  public void showFeeds() throws IOException;
  }


