/*=========================================================================
  
  JGemini

  DefaultBookmarkHandler 

  Copyright (c)2026 Kevin Boone, GPLv3.0 

=========================================================================*/
package me.kevinboone.jgemini.swing;

import java.net.*;
import java.io.*;
import java.util.*;
import me.kevinboone.jgemini.base.*;
import me.kevinboone.utils.file.FileUtil;

public class DefaultBookmarkHandler implements BookmarkHandler 
  {
  private Config config;
  private MainWindow mainWindow;
  // fileWasUpdated is set whenever a new line is written to the
  //   bookmark file, and at start-up. When set, it causes calls like
  //   getBookmarkCount to reload from file.
  private boolean fileWasUpdated = true;
  private Vector<GemLink> bookmarks = new Vector<GemLink>();

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
    String sUri = uri.toString();
    readFromFile();
    int l = bookmarks.size();
    for (int i = 0; i < l; i++)
      {
      GemLink link = bookmarks.elementAt(i);
System.out.println ("suri=" + sUri + " link=" + link);
      if (link.getUri().equals (sUri)) return true;
      }
    return false;
    }

  /** addBookmark() returns true if a new bookmark is added. The only
      reason not to add the bookmark -- other than exception --
      is that the URI is already bookmarked. */
  @Override
  public boolean addBookmark (String displayName, URL uri) throws IOException
    {
    if (Logger.isDebug())
      Logger.log (getClass().getName(), Logger.DEBUG, "addBookmark(): " + displayName + " " + uri);

    if (isBookmarked(uri)) return false;

    String bookmarksFile = config.getBookmarksFile();
    String newBookmark = "=> " + uri + " " + displayName + "\n";
    FileUtil.appendStringToFile (bookmarksFile, newBookmark);
    fileWasUpdated = true;
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
      fileWasUpdated = true;
      }
    }

  public int getBookmarkCount()
    {
    Logger.log (getClass().getName(), Logger.DEBUG, "getBookmarkCount()");
    readFromFile();
    return bookmarks.size();
    }

  /** This method will throw an exception if index < 0 or > the size
      of the bookmark list. It should only called after a call to
      getBookmarkCount(), which will also reload from file if
      necessary. */
  @Override
  public GemLink getBookmarkLink (int index)
    {
    readFromFile();
    return bookmarks.elementAt (index);
    }

  public void readFromFile() 
    {
    // Just creating a new Vector and leaving the old one for GC is
    //   probably quicker than clearing the existing one
    if (fileWasUpdated)
      {
      Logger.log (getClass().getName(), Logger.DEBUG, "readFromFile()");
      bookmarks = new Vector<GemLink>();
      Logger.log (getClass().getName(), Logger.DEBUG, "Bookmark data stale -- need to read");
      String bookmarksFile = config.getBookmarksFile();
      try
	{
	BufferedReader br = new BufferedReader (new InputStreamReader 
	  (new FileInputStream (bookmarksFile)));
	String line = br.readLine();
	while (line != null)
	  {
	  if (line.indexOf ("=>") == 0)
	    {
	    GemLink link = GemLink.parse (line.substring(2).trim());
	    bookmarks.add (link); 
	    }
	  line = br.readLine();
	  }
	br.close();
	}
      catch (IOException e)
	{
	}
      }
    fileWasUpdated = false;
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

