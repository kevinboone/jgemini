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
import me.kevinboone.jgemini.Constants;
import me.kevinboone.jgemini.base.*;
import me.kevinboone.utils.file.*;


public class Config extends Properties
  {
  private int logLevel = Logger.ERROR;
  private int bookmarkMaxMenu = 10;
  private boolean gemtextInlineImages = false;
  private boolean urlbarSearchEnabled = false;
  private boolean historyEnabled = false;
  private boolean emojiStripBookmarks = false;

  public final static int DEFLT_LOG_LEVEL = Logger.ERROR;
  public final static boolean DEFLT_EMOJI_STRIP_BOOKMARKS = false;
  public final static boolean DEFLT_GEMTEXT_INLINE_IMAGES = true;
  public final static String DEFLT_UI_DOCUMENT_CUSTOM_CSS = null;
  public final static String DEFLT_HISTORY_FILE = null;
  public final static String DEFLT_BOOKMARK_FILE = null;
  public final static int DEFLT_BOOKMARK_MAX_MENU = 10;
  public final static boolean DEFLT_HISTORY_ENABLED = false;
  public final static boolean DEFLT_URLBAR_SEARCH_ENABLED = true;

  private Vector<ConfigChangeListener> listeners = new Vector<ConfigChangeListener>();

  private static Config instance = null;

/*=========================================================================
  
  addClientCert

=========================================================================*/
  public void addClientCert (String name, String keystoreFile,
      String keystorePassword) 
    {
    setProperty (Constants.CLIENTCERT_TAG + name,
      keystoreFile + " " + keystorePassword);
    save();
    }

/*=========================================================================
  
  addConfigChangeListener

=========================================================================*/
  public void addConfigChangeListener (ConfigChangeListener l)
    {
    listeners.add (l);
    }

/*=========================================================================
  
  deriveProperties 

=========================================================================*/
  /* Calculate the values of the instance variables from the raw values
     read from the configuration file. */
  private void deriveProperties()
    {
    bookmarkMaxMenu = Integer.parseInt (getProperty 
      (Constants.BOOKMARK_MAX_MENU, ""+DEFLT_BOOKMARK_MAX_MENU));

    logLevel = Integer.parseInt (getProperty (Constants.LOG_LEVEL, 
      "" + DEFLT_LOG_LEVEL));
    Logger.setLevel (logLevel);

    gemtextInlineImages = getBooleanProperty 
      (Constants.GEMTEXT_INLINE_IMAGES, DEFLT_GEMTEXT_INLINE_IMAGES);
   
    urlbarSearchEnabled = getBooleanProperty
      (Constants.URLBAR_SEARCH_ENABLED, DEFLT_URLBAR_SEARCH_ENABLED); 

    historyEnabled = getBooleanProperty 
      (Constants.HISTORY_ENABLED, DEFLT_HISTORY_ENABLED);

    emojiStripBookmarks = getBooleanProperty 
      (Constants.EMOJI_STRIP_BOOKMARKS, DEFLT_EMOJI_STRIP_BOOKMARKS);
    }

/*=========================================================================
  
  getEmojiStripBookmarks

=========================================================================*/
  public boolean getEmojiStripBookmarks()
    {
    return emojiStripBookmarks;
    }

/*=========================================================================
  
  ensureBookmarksFileExists 

=========================================================================*/
  public void ensureBookmarksFileExists() throws IOException
    {
    String filename = getBookmarksFile();
    File file = new File (filename);
    if (file.exists()) return;
    file.createNewFile();
    FileUtil.appendStringToFile (filename, "# " 
      + Constants.BOOKMARKS_COMMENTS + "\n");
    }

/*=========================================================================
  
  ensureUserConfigFileExists 

=========================================================================*/
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

/*=========================================================================
  
  getConfig 

=========================================================================*/
  public static Config getConfig()
    {
    if (instance == null)
      {
      instance = new Config();
      instance.load();
      }
    return instance;
    }

/*=========================================================================
  
  getHomePage 

=========================================================================*/
  public String getHomePage()
    {
    Logger.in();
    String homePage = getProperty (Constants.URL_HOME, 
      Constants.DEFLT_URL_HOME);
    Logger.log (getClass().getName(), Logger.INFO, 
      "Home page is " + homePage);
    Logger.out();
    return homePage;
    }

/*=========================================================================
  
  getHistoryFile

=========================================================================*/
  public String getHistoryFile()
    {
    String historyFile = getProperty (Constants.HISTORY_FILE, 
      DEFLT_HISTORY_FILE);
    if (historyFile == null)
      historyFile = getStateDir() + File.separator + Constants.HISTORY_FILENAME;
    return historyFile;
    }

/*=========================================================================
  
  getBookmarksFile

=========================================================================*/
  public String getBookmarksFile()
    {
    String bookmarkFile = getProperty (Constants.BOOKMARK_FILE, 
      DEFLT_BOOKMARK_FILE);
    if (bookmarkFile == null)
      bookmarkFile = getStateDir() + File.separator 
        + Constants.BOOKMARK_FILENAME;
    return bookmarkFile;
    }

/*=========================================================================
  
  getBookmarkMaxMenu

=========================================================================*/
  public int getBookmarkMaxMenu()
    {
    return bookmarkMaxMenu;
    }

/*=========================================================================
  
  getLogLevel 

=========================================================================*/
  public int getLogLevel()
    {
    return logLevel;
    }

/*=========================================================================
  
  getGemtextInlineImages 

=========================================================================*/
  public boolean getGemtextInlineImages()
    {
    return gemtextInlineImages;
    }

/*=========================================================================
  
  getInlineImageWidth 

=========================================================================*/
  public int getInlineImageWidth()
    {
    return Integer.parseInt (getProperty 
        (Constants.INLINE_IMAGE_WIDTH, Constants.DEFLT_INLINE_IMAGE_WIDTH));
    }

/*=========================================================================
  
  getNewWindowMode

=========================================================================*/
  public int getNewWindowMode ()
    {
    return Integer.parseInt (getProperty 
        (Constants.UI_NEW_WINDOW_MODE, Constants.DEFLT_UI_NEW_WINDOW_MODE));
    }

/*=========================================================================
  
  getWindowWidth

=========================================================================*/
  public int getWindowWidth ()
    {
    return Integer.parseInt (getProperty (Constants.WINDOW_W, 
      Constants.DEFLT_WINDOW_W));
    }

/*=========================================================================
  
  getWindowHeight

=========================================================================*/
  public int getWindowHeight ()
    {
    return Integer.parseInt (getProperty (Constants.WINDOW_H, 
      Constants.DEFLT_WINDOW_H));
    }

/*=========================================================================
  
  getControlFont 

=========================================================================*/
  public String getControlFont()
    {
    return getProperty (Constants.UI_CONTROL_FONT, Constants.
      DEFLT_UI_CONTROL_FONT);
    }

/*=========================================================================
  
  getUserFont

=========================================================================*/
  public String getUserFont()
    {
    return getProperty (Constants.UI_USER_FONT, 
      Constants.DEFLT_UI_USER_FONT);
    }

/*=========================================================================
  
  getCustomCSSFile

=========================================================================*/
  public String getCustomCSSFile()
    {
    return getProperty (Constants.UI_DOCUMENT_CUSTOM_CSS, 
      DEFLT_UI_DOCUMENT_CUSTOM_CSS);
    }

/*=========================================================================
  
  getTheme

=========================================================================*/
  public String getTheme()
    {
    return getProperty (Constants.UI_DOCUMENT_THEME, 
      Constants.DEFLT_UI_DOCUMENT_THEME);
    }

/*=========================================================================
  
  getUrlbarSearchUrl

=========================================================================*/
  public String getUrlbarSearchUrl()
    {
    return getProperty (Constants.URLBAR_SEARCH_URL, 
      Constants.DEFLT_URLBAR_SEARCH_URL);
    }

/*=========================================================================
  
  getDocumentBaseFontSize

=========================================================================*/
  public int getDocumentBaseFontSize()
    {
    String s = getProperty (Constants.UI_DOCUMENT_FONT_SIZE, 
      Constants.DEFLT_UI_DOCUMENT_FONT_SIZE);
    return Integer.parseInt (s);
    }

/*=========================================================================
  
  getHistorySize

=========================================================================*/
  public int getHistorySize()
    {
    String s = getProperty (Constants.HISTORY_SIZE, 
      Constants.DEFLT_HISTORY_SIZE);
    return Integer.parseInt (s);
    }

/*=========================================================================
  
  getUrlbarSearchEnabled 

=========================================================================*/
  public boolean getUrlbarSearchEnabled()
    {
    return urlbarSearchEnabled;
    }

/*=========================================================================
  
  getHistoryEnabled 

=========================================================================*/
  public boolean getHistoryEnabled()
    {
    return historyEnabled;
    }

/*=========================================================================
  
  getBooleanProperty

=========================================================================*/
  public boolean getBooleanProperty (String name, boolean deflt)
    {
    String val = getProperty (name, deflt ? "1" : "0");
    if (val == null) return deflt;
    if (val.equals ("1")) return true;
    if (val.equals ("yes")) return true;
    if (val.equals ("true")) return true;
    if (val.equals ("on")) return true;
    return false;
    }

/*=========================================================================
  
  fireSettingsChangedListeners 

=========================================================================*/
  public void fireSettingsChangedListeners (int ccMode)
    {
    Logger.in();
    int l = listeners.size();
    for (int i = 0; i < l; i++)
      listeners.elementAt(i).configChanged (ccMode);
    Logger.out();
    }

/*=========================================================================
  
  getIdents

=========================================================================*/
  public Set<String> getIdents()
    {
    HashSet<String> s = new HashSet<String>();

    Enumeration e = propertyNames();
    while (e.hasMoreElements())
      {
      String k = (String)e.nextElement();
      if (k.startsWith (Constants.CLIENTCERT_TAG))
        {
        String value = k.substring (Constants.CLIENTCERT_TAG.length());
        if (!value.equals ("any"))
          s.add (value);
        }
      }

    return s;
    }

/*=========================================================================
  
  getIdentisDir

=========================================================================*/
  public String getIdentsDir()
    {
    Logger.in();
    String identsDir = getStateDir() + File.separator + Constants.IDENTS_DIRNAME; 
    Logger.out();
    return identsDir;
    }

/*=========================================================================
  
  getKeystoreSpecForIdent 

=========================================================================*/
public KeystoreSpec getKeystoreSpecForIdent (String ident)
    {
    String clientCertSpec = getProperty (Constants.CLIENTCERT_TAG + ident);
    if (clientCertSpec == null) return null;

    String[] tokens = clientCertSpec.trim().split ("\\s+");
    if (tokens.length != 2) 
      {
      Logger.log (getClass().getName(), Logger.WARNING, 
        "Bad client certificate specification: " + clientCertSpec);
      return null;
      }

    String clientCertKeyStoreFile = tokens[0];
    String clientCertKeyStorePassword = tokens[1];

    return new KeystoreSpec (clientCertKeyStoreFile, 
      clientCertKeyStorePassword);
    }


/*=========================================================================
  
  getStateDir

=========================================================================*/
  public String getStateDir()
    {
    Logger.in();
    String home = System.getProperty ("user.home");
    Logger.out();
    return home + File.separator + Constants.STATE_DIR_NAME; 
    }

/*=========================================================================
  
  getUserConfigFilename

=========================================================================*/
  public String getUserConfigFilename()
    {
    return getStateDir() + File.separator + Constants.PREFS_FILE;
    }

/*=========================================================================
  
  loadFromFile 

=========================================================================*/
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

/*=========================================================================
  
  load

=========================================================================*/
  public void load()
    {
    // Make a new state directory. We have to do this somewhere,
    //   and it's best to do it early, before we try to save
    //   anything there.
    Logger.in();
    new File (getStateDir()).mkdir();
    new File (getIdentsDir()).mkdir();

    Logger.log (getClass().getName(), Logger.INFO, 
      "Loading system-wide configuration");
    // This won't work on Windows, but it won't do any harm.
    String sysPropsFile = "/etc/jgemini/" + Constants.SYS_PREFS_FILE; 
    loadFromFile (sysPropsFile);

    Logger.log (getClass().getName(), Logger.DEBUG, 
      "Loading user configuration");
    String propsFile = getUserConfigFilename(); 
    Logger.log (getClass().getName(), Logger.DEBUG, 
      "User properties file is " + propsFile);
    loadFromFile (propsFile);

    fireSettingsChangedListeners (ConfigChangeListener.CCMODE_REFRESH);
    Logger.out();
    }

/*=========================================================================
  
  removeConfigChangeListener 

=========================================================================*/
  public void removeConfigChangeListener (ConfigChangeListener l)
    {
    listeners.remove (l);
    }

/*=========================================================================
  
  removeIdent 

=========================================================================*/
  public void removeIdent (String hostname)
    {
    remove (Constants.IDENT_TAG + hostname);
    }

/*=========================================================================
  
  save 

=========================================================================*/
  public void save()
    {
    Logger.in();
    String propsFile = getUserConfigFilename(); 
    saveToFile (propsFile);
    Logger.out();
    }

/*=========================================================================
  
  saveToFile 

=========================================================================*/
  private void saveToFile (String filename)
    {
    Logger.in();
    if (Logger.isDebug())
      Logger.log (getClass().getName(), Logger.INFO, 
        "Saving properties to " + filename);
    try (OutputStream os = new FileOutputStream (new File (filename)))
      {
      store (os, Constants.PROPS_COMMENTS);
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

/*=========================================================================
  
  setDocumentBaseFontSize 

=========================================================================*/
  public void setDocumentBaseFontSize (int px)
    {
    setProperty (Constants.UI_DOCUMENT_FONT_SIZE, "" + px);
    }

/*=========================================================================
  
  setEmojiStripBookmark 

=========================================================================*/
  public void setEmojiStripBookmarks (boolean f)
    {
    emojiStripBookmarks = f;
    setProperty (Constants.EMOJI_STRIP_BOOKMARKS, "" + f);
    }

/*=========================================================================
  
  setHomePage 

=========================================================================*/
  public void setHomePage (String uri)
    {
    if (Logger.isDebug())
      Logger.log (Logger.class, Logger.INFO, "setting home page to " + uri);
    setProperty (Constants.URL_HOME, uri);
    }

/*=========================================================================
  
  setCustomCssFile

=========================================================================*/
  public void setCustomCssFile (String file)
    {
    setProperty (Constants.UI_DOCUMENT_CUSTOM_CSS, file);
    }

/*=========================================================================
  
  setHistoryEnabled

=========================================================================*/
  public void setHistoryEnabled (boolean enabled)
    {
    historyEnabled = enabled;
    setProperty (Constants.HISTORY_ENABLED, "" + enabled);
    }

/*=========================================================================
  
  setIdentForHostname

  writes a property "ident.{hostname}={ident}"

  Note that "none" is a valid ident, but null is not. to remove an
  ident from a hostname, use removeIdent()

=========================================================================*/
  public void setIdentForHostname (String hostname, String ident)
    {
    Logger.in();
    if (Logger.isDebug())
      Logger.log (Logger.class, Logger.INFO, "setting ident for " + hostname
        + " to " + ident);
    setProperty (Constants.IDENT_TAG + hostname, ident);
    Logger.out();
    }

/*=========================================================================
  
  setTheme

=========================================================================*/
  public void setTheme (String theme)
    {
    setProperty (Constants.UI_DOCUMENT_THEME, theme);
    }

/*=========================================================================
  
  setGemtextInlineImages 

=========================================================================*/
  public void setGemtextInlineImages (boolean f)
    {
    gemtextInlineImages = f;
    setProperty (Constants.GEMTEXT_INLINE_IMAGES, "" + f);
    }

/*=========================================================================
  
  setHistorySize

=========================================================================*/
  public void setHistorySize (int n)
    {
    setProperty (Constants.HISTORY_SIZE, "" + n);
    }

/*=========================================================================
  
  setInlineImageWidth 

=========================================================================*/
  public void setInlineImageWidth (int n)
    {
    setProperty (Constants.INLINE_IMAGE_WIDTH, "" + n);
    }

/*=========================================================================
  
  getUrlbarSearchEnabled 

=========================================================================*/
  public void setUrlbarSearchEnabled (boolean f)
    {
    urlbarSearchEnabled = f;
    setProperty (Constants.URLBAR_SEARCH_ENABLED, "" + f);
    }

/*=========================================================================
  
  setUrlbarSearchUrl

=========================================================================*/
  public void setUrlbarSearchUrl (String url)
    {
    setProperty (Constants.URLBAR_SEARCH_URL, url);
    }

/*=========================================================================
  
  setWindowHeight

=========================================================================*/
  public void setWindowHeight (int h)
    {
    setProperty (Constants.WINDOW_H, "" + h);
    }

/*=========================================================================
  
  setWindowWidth

=========================================================================*/
  public void setWindowWidth (int w)
    {
    setProperty (Constants.WINDOW_W, "" + w);
    }


  }


