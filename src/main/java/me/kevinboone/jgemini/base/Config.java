/*=========================================================================
  
  JGemini

  Config 

  Retrieves and manages application configuration

  Copyright (c)2021 Kevin Boone, GPLv3.0 

=========================================================================*/
package me.kevinboone.jgemini.base;
import java.util.*;
import java.io.*;
import me.kevinboone.jgemini.base.*;


public class Config extends Properties
  {
  public final static String VERSION = "2.0";
  private int logLevel = Logger.ERROR;
  private boolean gemtextInlineImages = false;
  private boolean urlbarSearchEnabled = false;
  private final static String SYS_PREFS_FILE = "jgemini.properties"; 
  private final static String PREFS_FILE = ".jgemini.properties"; 
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
  public final static String URLBAR_SEARCH_ENABLED = "urlbar.search.enabled";
  public final static String DEFLT_URLBAR_SEARCH_ENABLED = "1";
  public final static String URLBAR_SEARCH_URL = "urlbar.search.url";
  public final static String DEFLT_URLBAR_SEARCH_URL = "gemini://tlgs.one/search";

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

  public String getHomePage()
    {
    String homePage = getProperty (URL_HOME, DEFLT_URL_HOME);
    Logger.log (Logger.class, "getHomePage() return " + homePage);
    return homePage;
    }

  public String getHistoryFile()
    {
    String historyFile = getProperty (HISTORY_FILE, DEFLT_HISTORY_FILE);
    Logger.log (Logger.class, "getHistoryFile() return " + historyFile);
    return historyFile;
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

  public void load()
    {
    Logger.log (Config.class, "Loading system configuration");
    String sysPropsFile = "/etc/jgemini/" + SYS_PREFS_FILE; 
    Logger.log (Config.class, "System properties file is " + sysPropsFile);
    try (InputStream is = new FileInputStream (new File (sysPropsFile)))
      {
      load (is);
      }
    catch (Exception e)
      {
      // This may not be an error
      Logger.log (this.getClass(), e.toString());
      }
    Logger.log (Config.class, "Loading user configuration");
    String home = System.getProperty ("user.home");
    String propsFile = home + File.separator + PREFS_FILE;
    Logger.log (Config.class, "User properties file is " + propsFile);
    try (InputStream is = new FileInputStream (new File (propsFile)))
      {
      load (is);
      }
    catch (Exception e)
      {
      // This may not be an error
      Logger.log (this.getClass(), e.toString());
      }
    logLevel = Integer.parseInt (getProperty (LOG_LEVEL, ""+DEFLT_LOG_LEVEL));
    if (getProperty (GEMTEXT_INLINE_IMAGES, DEFLT_GEMTEXT_INLINE_IMAGES).equals ("1")) 
      gemtextInlineImages = true;
    else
      gemtextInlineImages = false;
    if (getProperty (URLBAR_SEARCH_ENABLED, DEFLT_URLBAR_SEARCH_ENABLED).equals ("1")) 
      urlbarSearchEnabled = true;
    else
      urlbarSearchEnabled = false;

    Logger.log (Config.class, "URLBar search is " 
      + (urlbarSearchEnabled ? "enabled" : "disabled"));
    Logger.log (Config.class, "Inline images are " 
      + (gemtextInlineImages ? "enabled" : "disabled"));
    }

  }


