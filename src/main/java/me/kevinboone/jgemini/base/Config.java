/*=========================================================================
  
  JGemini

  Config 

  Retrieves and manages application configuration

  Copyright (c)2021 Kevin Boone, GPLv3.0 

=========================================================================*/
package me.kevinboone.jgemini.base;
import java.util.*;
import java.util.stream.Collectors;
import java.io.*;
import me.kevinboone.jgemini.base.*;
import me.kevinboone.utils.file.*;


public class Config extends Properties
  {
  public final static String VERSION = "2.0.2";
  private int logLevel = Logger.ERROR;
  private int bookmarkMaxMenu = 10;
  private boolean gemtextInlineImages = false;
  private boolean urlbarSearchEnabled = false;
  private boolean historyEnabled = false;
  private boolean emojiStripBookmarks = false;

  private final static String SYS_PREFS_FILE = "jgemini.properties"; 
  private final static String STATE_DIR_NAME = ".jgemini"; 
  private final static String PREFS_FILE = "jgemini.properties"; 
  private final static String BOOKMARK_FILENAME = "bookmarks.gmi"; 
  private final static String HISTORY_FILENAME = "jgemini.history";

  public final static String URL_HOME = "url.home";
  public final static String DEFLT_URL_HOME = "about:/jgemini_overview.md";
  public final static String LOG_LEVEL = "log.level";
  public final static int DEFLT_LOG_LEVEL = Logger.ERROR;
  public final static String WINDOW_W = "window.w";
  public final static String DEFLT_WINDOW_W = "800";
  public final static String WINDOW_H = "window.h";
  public final static String EMOJI_STRIP_BOOKMARKS = "emoji.strip.bookmarks";
  public final static boolean DEFLT_EMOJI_STRIP_BOOKMARKS = false;
  public final static String DEFLT_WINDOW_H = "600";
  public final static String UI_USER_FONT = "ui.user_font"; 
  public final static String DEFLT_UI_USER_FONT = "Sans 20";
  public final static String UI_CONTROL_FONT = "ui.control_font"; 
  public final static String DEFLT_UI_CONTROL_FONT = "Sans 20";
  public final static String UI_DOCUMENT_FONT_SIZE = "ui.document.font.size";
  public final static String DEFLT_UI_DOCUMENT_FONT_SIZE = "16";
  public final static String UI_NEW_WINDOW_MODE = "ui,new_window";
  public final static String DEFLT_UI_NEW_WINDOW_MODE = "0";
  public final static String GEMTEXT_INLINE_IMAGES = "gemtext.inline.images";
  public final static boolean DEFLT_GEMTEXT_INLINE_IMAGES = true;
  public final static String INLINE_IMAGE_WIDTH = "inline.image.width";
  public final static String DEFLT_INLINE_IMAGE_WIDTH = "600";
  public final static String UI_DOCUMENT_THEME = "ui.document.theme";
  public final static String DEFLT_UI_DOCUMENT_THEME = "light";
  public final static String UI_DOCUMENT_CUSTOM_CSS = "ui.document.custom.css";
  public final static String DEFLT_UI_DOCUMENT_CUSTOM_CSS = null;
  public final static String HISTORY_SIZE = "history.size";
  public final static String DEFLT_HISTORY_SIZE = "30";
  public final static String HISTORY_FILE = "history.file";
  public final static String DEFLT_HISTORY_FILE = null;
  public final static String BOOKMARK_FILE = "bookmark.file";
  public final static String DEFLT_BOOKMARK_FILE = null;
  public final static String BOOKMARK_MAX_MENU = "bookmark.max.menu";
  public final static int DEFLT_BOOKMARK_MAX_MENU = 10;
  public final static String HISTORY_ENABLED = "history.enabled";
  public final static boolean DEFLT_HISTORY_ENABLED = false;
  public final static String URLBAR_SEARCH_ENABLED = "urlbar.search.enabled";
  public final static boolean DEFLT_URLBAR_SEARCH_ENABLED = true;
  public final static String URLBAR_SEARCH_URL = "urlbar.search.url";
  public final static String DEFLT_URLBAR_SEARCH_URL = "gemini://tlgs.one/search";

  private Vector<ConfigChangeListener> listeners = new Vector<ConfigChangeListener>();

  private static Config instance = null;

  public void addConfigChangeListener (ConfigChangeListener l)
    {
    listeners.add (l);
    }

  public boolean emojiStripBookmark()
    {
    return emojiStripBookmarks;
    }

  public void ensureBookmarksFileExists() throws IOException
    {
    String filename = getBookmarksFile();
    File file = new File (filename);
    if (file.exists()) return;
    file.createNewFile();
    FileUtil.appendStringToFile (filename, "# " + Strings.BOOKMARKS + "\n");
    }

  public void ensureUserConfigFileExists() throws IOException
    {
    String filename = getUserConfigFilename();
    File file = new File (filename);
    if (file.exists()) return;

    // No config file. We need to retrieve, and then save,
    //   the template.
    InputStream is = getClass().getClassLoader().getResourceAsStream 
      ("templates/jgemini.properties");
    String s = new BufferedReader (new InputStreamReader (is))
        .lines().collect (Collectors.joining("\n"));

    FileOutputStream fos = new FileOutputStream (file);
    PrintWriter pw = new PrintWriter (fos);
    pw.println (s);
    pw.flush();
    fos.close();
    is.close();
    }

  public static Config getConfig()
    {
    if (instance == null)
      {
      instance = new Config();
      instance.load();
      }
    return instance;
    }

  public String getHomePage()
    {
    Logger.in();
    String homePage = getProperty (URL_HOME, DEFLT_URL_HOME);
    Logger.log (getClass().getName(), Logger.INFO, 
      "Home page is " + homePage);
    Logger.out();
    return homePage;
    }

  public String getHistoryFile()
    {
    String historyFile = getProperty (HISTORY_FILE, DEFLT_HISTORY_FILE);
    if (historyFile == null)
      historyFile = getStateDir() + File.separator + HISTORY_FILENAME;
    return historyFile;
    }

  public String getBookmarksFile()
    {
    String bookmarkFile = getProperty (BOOKMARK_FILE, DEFLT_BOOKMARK_FILE);
    if (bookmarkFile == null)
      bookmarkFile = getStateDir() + File.separator + BOOKMARK_FILENAME;
    return bookmarkFile;
    }

  public int getBookmarkMaxMenu()
    {
    return bookmarkMaxMenu;
    }

  public int logLevel()
    {
    return logLevel;
    }

  public boolean gemtextInlineImages()
    {
    return gemtextInlineImages;
    }

  public String inlineImageWidth()
    {
    return getProperty 
        (INLINE_IMAGE_WIDTH, DEFLT_INLINE_IMAGE_WIDTH);
    }

  public int getNewWindowMode ()
    {
    return Integer.parseInt (getProperty 
        (UI_NEW_WINDOW_MODE, DEFLT_UI_NEW_WINDOW_MODE));
    }

  public int getWindowWidth ()
    {
    return Integer.parseInt (getProperty (WINDOW_W, DEFLT_WINDOW_W));
    }

  public int getWindowHeight ()
    {
    return Integer.parseInt (getProperty (WINDOW_H, DEFLT_WINDOW_H));
    }

  public String getControlFont()
    {
    return getProperty (UI_CONTROL_FONT, DEFLT_UI_CONTROL_FONT);
    }

  public String getUserFont()
    {
    return getProperty (UI_USER_FONT, DEFLT_UI_USER_FONT);
    }

  public String getCustomCSSFile()
    {
    return getProperty (UI_DOCUMENT_CUSTOM_CSS, DEFLT_UI_DOCUMENT_CUSTOM_CSS);
    }

  public String getTheme()
    {
    return getProperty (UI_DOCUMENT_THEME, DEFLT_UI_DOCUMENT_THEME);
    }

  public String getUrlbarSearchUrl()
    {
    return getProperty (URLBAR_SEARCH_URL, DEFLT_URLBAR_SEARCH_URL);
    }

  public int getDocumentBaseFontSize()
    {
    String s = getProperty (UI_DOCUMENT_FONT_SIZE, DEFLT_UI_DOCUMENT_FONT_SIZE);
    return Integer.parseInt (s);
    }

  public int getHistorySize()
    {
    String s = getProperty (HISTORY_SIZE, DEFLT_HISTORY_SIZE);
    return Integer.parseInt (s);
    }

  /** The method returns null if there is no entry in the configuration 
        file for the specific host. */
  public String getClientCertSpecForHost (String hostname)
    {
    String key = "clientcert." + hostname; 
    String result = getProperty (key);
    if (result == null)
      result = getProperty ("clientcert.*");
    return result;
    }

  public boolean urlbarSearchEnabled()
    {
    return urlbarSearchEnabled;
    }

  public boolean historyEnabled()
    {
    return historyEnabled;
    }

  public boolean getBooleanProperty (String name, boolean deflt)
    {
    String val = getProperty (name, deflt ? "1" : "0");
    if (val == null) return deflt;
    if (val.equals ("1")) return true;
    if (val.equals ("yes")) return true;
    if (val.equals ("on")) return true;
    return false;
    }

  /* Calculate the values of the instance variables from the raw values
     read from the configuration file. */
  private void deriveProperties()
    {
    bookmarkMaxMenu = Integer.parseInt (getProperty 
      (BOOKMARK_MAX_MENU, ""+DEFLT_BOOKMARK_MAX_MENU));

    logLevel = Integer.parseInt (getProperty (LOG_LEVEL, ""+DEFLT_LOG_LEVEL));
    Logger.setLevel (logLevel);

    gemtextInlineImages = getBooleanProperty 
      (GEMTEXT_INLINE_IMAGES, DEFLT_GEMTEXT_INLINE_IMAGES);
   
    urlbarSearchEnabled = getBooleanProperty
      (URLBAR_SEARCH_ENABLED, DEFLT_URLBAR_SEARCH_ENABLED); 

    historyEnabled = getBooleanProperty 
      (HISTORY_ENABLED, DEFLT_HISTORY_ENABLED);

    emojiStripBookmarks = getBooleanProperty 
      (EMOJI_STRIP_BOOKMARKS, DEFLT_EMOJI_STRIP_BOOKMARKS);
    }

  private void fireSettingsChangedListeners()
    {
    Logger.in();
    int l = listeners.size();
    for (int i = 0; i < l; i++)
      listeners.elementAt(i).configChanged();
    Logger.out();
    }

  public String getStateDir()
    {
    Logger.in();
    String home = System.getProperty ("user.home");
    Logger.out();
    return home + File.separator + STATE_DIR_NAME; 
    }

  public String getUserConfigFilename()
    {
    return getStateDir() + File.separator + PREFS_FILE;
    }

  public void loadFromFile (String filename)
    {
    Logger.in();
    if (Logger.isDebug())
      Logger.log (getClass().getName(), Logger.INFO, 
         "Loading settings from " + filename);
    try (InputStream is = new FileInputStream (new File (filename)))
      {
      load (is);
      is.close();
      }
    catch (Exception e)
      {
      // This may not be an error
      Logger.log (getClass().getName(), Logger.DEBUG, e.toString());
      }
    deriveProperties();
    Logger.out();
    }

  public void load()
    {
    // Make a new state directory. We have to do this somewhere,
    //   and it's best to do it early, before we try to save
    //   anything there.
    Logger.in();
    new File (getStateDir()).mkdir();

    Logger.log (getClass().getName(), Logger.INFO, 
      "Loading system-wide configuration");
    // This won't work on Windows, but it won't do any harm.
    String sysPropsFile = "/etc/jgemini/" + SYS_PREFS_FILE; 
    loadFromFile (sysPropsFile);

    Logger.log (getClass().getName(), Logger.DEBUG, 
      "Loading user configuration");
    String propsFile = getUserConfigFilename(); 
    Logger.log (getClass().getName(), Logger.DEBUG, 
      "User properties file is " + propsFile);
    loadFromFile (propsFile);

    fireSettingsChangedListeners();
    Logger.out();
    }

  public void removeConfigChangeListener (ConfigChangeListener l)
    {
    listeners.remove (l);
    }

  public void save()
    {
    Logger.in();
    String propsFile = getUserConfigFilename(); 
    saveToFile (propsFile);
    Logger.out();
    }

  private void saveToFile (String filename)
    {
    Logger.in();
    if (Logger.isDebug())
      Logger.log (getClass().getName(), Logger.INFO, 
        "Saving properties to " + filename);
    try (OutputStream os = new FileOutputStream (new File (filename)))
      {
      store (os, Strings.PROPS_COMMENTS);
      os.close();
      }
    catch (Exception e)
      {
      // This may not be an error
      Logger.log (getClass().getName(), Logger.DEBUG, e.toString());
      }
    deriveProperties();
    Logger.out();
    }

  public void setDocumentBaseFontSize (int px)
    {
    setProperty (UI_DOCUMENT_FONT_SIZE, "" + px);
    }

  public void setHomePage (String uri)
    {
    if (Logger.isDebug())
      Logger.log (Logger.class, Logger.INFO, "setting home page to " + uri);
    setProperty (URL_HOME, uri);
    }

  }


