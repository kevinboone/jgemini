/*=========================================================================
  
  JGemini

  BookmarkHandler 

  Copyright (c)2026 Kevin Boone, GPLv3.0 

=========================================================================*/
package me.kevinboone.jgemini.base;

import java.net.*;
import java.io.*;

public interface BookmarkHandler 
  {
  /** isBookmark() returns true if the URI is definitely bookmarked, and
      false if not, or uncertain. This method throws no exceptions. */
  public boolean isBookmarked (URL uri);
  /** addBookmark() returns true if a new bookmark is added. The only
      reason not to add the bookmark -- other than exception --
      is that the URI is already bookmarked. */
  public boolean addBookmark (String displayName, URL uri) throws IOException;
  public void showBookmarks() throws IOException;
  public void editBookmarks() throws IOException;
  }

