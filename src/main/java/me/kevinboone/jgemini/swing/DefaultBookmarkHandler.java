/*=========================================================================
  
  JGemini

  DefaultBookmarkHandler 

  Copyright (c)2026 Kevin Boone, GPLv3.0 

=========================================================================*/
package me.kevinboone.jgemini.swing;

import java.net.*;
import java.io.*;
import me.kevinboone.jgemini.base.*;
import me.kevinboone.utils.file.FileUtil;

public class DefaultBookmarkHandler implements BookmarkHandler 
  {
  private Config config;
  private MainWindow mainWindow;

  DefaultBookmarkHandler (MainWindow mainWindow)
    {
    config = Config.getConfig();
    this.mainWindow = mainWindow;
    }

  /** isBookmark() returns true if the URI is definitely bookmarked, and
      false if not, or uncertain. This method throws no exceptions. */
  @Override
  public boolean isBookmarked (URL uri)
    {
    String bookmarksFile = config.getBookmarksFile();
    String sUri = uri.toString();
    boolean found = false;
    try
      {
      BufferedReader br = new BufferedReader (new InputStreamReader 
        (new FileInputStream (bookmarksFile)));
      String line = br.readLine();
      while (!found && (line != null))
	{
        if (line.indexOf ("=>") == 0)
          {
          GemLink link = GemLink.parse (line.substring(2).trim());
          if (link.getUri().equals (sUri))
            found = true; 
          }
	line = br.readLine();
	}
      br.close();
      }
    catch (IOException e)
      {
      return false; 
      }

    return found;
    }

  /** addBookmark() returns true if a new bookmark is added. The only
      reason not to add the bookmark -- other than exception --
      is that the URI is already bookmarked. */
  @Override
  public boolean addBookmark (String displayName, URL uri) throws IOException
    {
    if (Logger.isDebug())
      Logger.log (this.getClass(), "addBookmark(): " + displayName + " " + uri);

    if (isBookmarked(uri)) return false;

    String bookmarksFile = config.getBookmarksFile();
    String newBookmark = "=> " + uri + " " + displayName + "\n";
    FileUtil.appendStringToFile (bookmarksFile, newBookmark);
    return true;
    }

  @Override
  public void editBookmarks() throws IOException
    {
    config.ensureBookmarksFileExists();
    String bookmarksFile = config.getBookmarksFile();
    EditFileDialog d = new EditFileDialog (mainWindow, 
        Strings.BOOKMARKS,
        bookmarksFile);
    d.setVisible (true);
    if (d.didSave())
      {
      // Not sure we need to do anything here
      }
    }

  @Override
  public void showBookmarks() throws IOException
    {
    config.ensureBookmarksFileExists();
    String bookmarksFile = config.getBookmarksFile();
    URL u = new File (bookmarksFile).toURL();
    mainWindow.loadURI (u);
    }
  }

