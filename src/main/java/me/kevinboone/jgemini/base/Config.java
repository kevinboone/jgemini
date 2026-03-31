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
  public final static String VERSION = "2.0.1";
  private int logLevel = Logger.ERROR;
  private boolean gemtextInlineImages = false;
  private boolean urlbarSearchEnabled = false;
  private boolean historyEnabled = false;

  private final static String SYS_PREFS_FILE = "jgemini.properties"; 
  private final static String STATE_DIR_NAME = ".jgemini"; 
  private final static String PREFS_FILE = "jgemini.properties"; 
  private final static String BOOKMARK_FILENAME = "bookmarks.gmi"; 
  private final static String HISTORY_FILENAME = "jgemini.history";

  public final static String URL_HOME = "url.home";
  public final static String DEFLT_URL_HOME = 
      "gemini://geminiprotocol.net/";
  public final static String LOG_LEVEL = "log.level";
  public final static int DEFLT_LOG_LEVEL = Logger.ERROR;
  public final static String WINDOW_W = "window.w";
  public final static String DEFLT_WINDOW_W = "800";
  public final static String WINDOW_H = "window.h";
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
  public final static String DEFLT_GEMTEXT_INLINE_IMAGES = "1";
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
  public final static String HISTORY_ENABLED = "history.enabled";
  public final static String DEFLT_HISTORY_ENABLED = "0";
  public final static String URLBAR_SEARCH_ENABLED = "urlbar.search.enabled";
  public final static String DEFLT_URLBAR_SEARCH_ENABLED = "1";
  public final static String URLBAR_SEARCH_URL = "urlbar.search.url";
  public final static String DEFLT_URLBAR_SEARCH_URL = "gemini://tlgs.one/search";

  private Vector<ConfigChangeListener> listeners = new Vector<ConfigChangeListener>();

  private static Config instance = null;

  public static Config getConfig()
    {
    if (instance == null)
      {
      instance = new Config();
      instance.load();
      }
    return instance;
    }

  public void addConfigChangeListener (ConfigChangeListener l)
    {
    listeners.add (l);
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

  public String getHomePage()
    {
    String homePage = getProperty (URL_HOME, DEFLT_URL_HOME);
    Logger.log (Logger.class, "getHomePage() return " + homePage);
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

  public void setDocumentBaseFontSize (int px)
    {
    setProperty (UI_DOCUMENT_FONT_SIZE, "" + px);
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

  /* Calculate the values of the instance variables from the raw values
     read from the configuration file. */
  private void deriveProperties()
    {
    logLevel = Integer.parseInt (getProperty (LOG_LEVEL, ""+DEFLT_LOG_LEVEL));
    if (getProperty (GEMTEXT_INLINE_IMAGES, DEFLT_GEMTEXT_INLINE_IMAGES).equals ("1")) 
      gemtextInlineImages = true;
    else
      gemtextInlineImages = false;
    if (getProperty (URLBAR_SEARCH_ENABLED, DEFLT_URLBAR_SEARCH_ENABLED).equals ("1")) 
      urlbarSearchEnabled = true;
    else
      urlbarSearchEnabled = false;
    if (getProperty (HISTORY_ENABLED, DEFLT_HISTORY_ENABLED).equals ("1")) 
      historyEnabled = true;
    else
      historyEnabled = false;

    Logger.log (Config.class, "URLBar search is " 
      + (urlbarSearchEnabled ? "enabled" : "disabled"));
    Logger.log (Config.class, "Inline images are " 
      + (gemtextInlineImages ? "enabled" : "disabled"));
    }

  private void fireSettingsChangedListeners()
    {
    int l = listeners.size();
    for (int i = 0; i < l; i++)
      listeners.elementAt(i).configChanged();
    }

  public String getStateDir()
    {
    String home = System.getProperty ("user.home");
    return home + File.separator + STATE_DIR_NAME; 
    }

  public String getUserConfigFilename()
    {
    return getStateDir() + File.separator + PREFS_FILE;
    }

  public void loadFromFile (String filename)
    {
    if (Logger.isDebug())
      Logger.log (Config.class, "Loading properties from " + filename);
    try (InputStream is = new FileInputStream (new File (filename)))
      {
      load (is);
      is.close();
      }
    catch (Exception e)
      {
      // This may not be an error
      Logger.log (this.getClass(), e.toString());
      }
    deriveProperties();
    }

  public void load()
    {
    // Make a new state directory. We have to do this somewhere,
    //   and it's best to do it early, before we try to save
    //   anything there.
    new File (getStateDir()).mkdir();

    Logger.log (Config.class, "Loading system configuration");
    // This won't work on Windows, but it won't do any harm.
    String sysPropsFile = "/etc/jgemini/" + SYS_PREFS_FILE; 
    loadFromFile (sysPropsFile);

    Logger.log (Config.class, "Loading user configuration");
    String propsFile = getUserConfigFilename(); 
    Logger.log (Config.class, "User properties file is " + propsFile);
    loadFromFile (propsFile);

    fireSettingsChangedListeners();
    }

  public void removeConfigChangeListener (ConfigChangeListener l)
    {
    listeners.remove (l);
    }

  public void save()
    {
    String propsFile = getUserConfigFilename(); 
    saveToFile (propsFile);
    }

  private void saveToFile (String filename)
    {
    if (Logger.isDebug())
      Logger.log (Config.class, "Saving properties to " + filename);
    try (OutputStream os = new FileOutputStream (new File (filename)))
      {
      store (os, Strings.PROPS_COMMENTS);
      os.close();
      }
    catch (Exception e)
      {
      // This may not be an error
      Logger.log (this.getClass(), Logger.ERROR, e.toString());
      }
    deriveProperties();
    }

  public void setHomePage (String url)
    {
    if (Logger.isDebug())
      Logger.log (Logger.class, "setHomePage(), url is" + url);
    setProperty (URL_HOME, url);
    }

  }


