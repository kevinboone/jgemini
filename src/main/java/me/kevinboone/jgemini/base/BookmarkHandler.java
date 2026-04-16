/*=========================================================================
  
  JGemini

  BookmarkHandler 

  Copyright (c)2026 Kevin Boone, GPLv3.0 

=========================================================================*/
package me.kevinboone.jgemini.base;

import java.net.*;
import java.io.*;

/** The interface that defines a bookmark handler. */
public interface BookmarkHandler 
  {
  /** isBookmark() returns true if the URI is definitely bookmarked, and
      false if not, or uncertain. This method throws no exceptions. */
  public boolean isBookmarked (URL uri);
  /** addBookmark() returns true if a new bookmark is added. The only
      reason not to add the bookmark -- other than exception --
      is that the URI is already bookmarked. */
  public boolean addBookmark (String displayName, URL uri) throws IOException;
  /** Bring up some kind of editor for the user to manage bookmarks. */
  public void editBookmarks() throws IOException;
  public int getBookmarkCount();
  public GemLink getBookmarkLink (int index);
  /** Bring up some kind of list of all bookmarks for the user. */
  public void showBookmarks() throws IOException;
  }

