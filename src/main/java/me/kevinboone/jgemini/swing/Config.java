/*=========================================================================
  
  JGemini

  Config 

  Retrieves and manages application configuration

  Copyright (c)2021 Kevin Boone, GPLv3.0 

=========================================================================*/
package me.kevinboone.jgemini.swing;
import java.util.*;
import java.io.*;

public class Config extends Properties
  {
  public final static String APP_NAME = "JGemini";
  public final static String VERSION = "1.0";
  private boolean debug = false;
  private final static String PREFS_FILE = ".jgemini.properties"; 
  public final static String URL_HOME = "url.home";
  public final static String DEFLT_URL_HOME = 
      "gemini://gemini.circumlunar.space/index.gmi";
  public final static String STYLE_BODY = "style.body";
  public final static String DEFLT_STYLE_BODY = 
      "color:black; font: 16px Serif; margin-left: 20px; margin-right: 20px; margin-bottom: 10px";
  public final static String STYLE_H1 = "style.h1";
  public final static String DEFLT_STYLE_H1 = 
      "color: black; font: 22px Sans";
  public final static String STYLE_H2 = "style.h2";
  public final static String DEFLT_STYLE_H2 = 
      "color: black; font: 20px Sans";
  public final static String STYLE_H3 = "style.h3";
  public final static String DEFLT_STYLE_H3 = 
      "color: black; font: 18px Sans";
  public final static String STYLE_PRE = "style.pre";
  public final static String DEFLT_STYLE_PRE = 
      "color:black; font: 16px monospace";
  public final static String STYLE_A = "style.a";
  public final static String DEFLT_STYLE_A = 
      "text-decoration: none; color: red";
  public final static String STYLE_A_HOVER = "style.a_hover";
  public final static String DEFLT_STYLE_A_HOVER = 
      "text-decoration: none; color: red; font-weight: bold";
  public final static String DEBUG = "debug";
  public final static String DEFLT_DEBUG = "1";
  public final static String WINDOW_W = "window.w";
  public final static String DEFLT_WINDOW_W = "800";
  public final static String WINDOW_H = "window.h";
  public final static String DEFLT_WINDOW_H = "600";
  public final static String UI_USER_FONT = "ui.user_font"; 
  public final static String DEFLT_UI_USER_FONT = "Sans 20";
  public final static String UI_CONTROL_FONT = "ui.control_font"; 
  public final static String DEFLT_UI_CONTROL_FONT = "Sans 20";
  public final static String UI_NEW_WINDOW_MODE = "ui,new_window";
  public final static String DEFLT_UI_NEW_WINDOW_MODE = "0";

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

  public boolean debug()
    {
    return debug;
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

  public void load()
    {
    Logger.log (Config.class, "Loading configuration");
    String home = System.getProperty ("user.home");
    String propsFile = home + File.separator + PREFS_FILE;
    Logger.log (Config.class, "Properties file is " + propsFile);
    try (InputStream is = new FileInputStream (new File (propsFile)))
      {
      load (is);
      }
    catch (Exception e)
      {
      // This may not be an error
      Logger.log (this.getClass(), e.toString());
      }
    if (getProperty (DEBUG, "0").equals ("1")) debug = true;
    }

  }


