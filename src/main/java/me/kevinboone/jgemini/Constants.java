/*=========================================================================
  
  JGemini

  Constants

  This file contains definitions of all the constant text items that are
    referenced in more than one place. In the case of settings keys,
    one of those places might be the documentation -- not everything in
    this file is referenced more than once in Java code.

  I haven't included here configuration defaults that aren't textual,
    or for which 'null' is a necessary default.

  None of these strings need to be translated.

  Copyright (c)2026 Kevin Boone, GPLv3.0 

=========================================================================*/
package me.kevinboone.jgemini;

public interface Constants
  {
  // General

  public final static String APP_NAME = "JGemini";
  public final static String VERSION = "2.0.3";
  // Header of user properties file
  public final static String PROPS_COMMENTS = "JGemini user configuration"; 
  // Header of bookmarks file
  public final static String BOOKMARKS_COMMENTS = "JGemini bookmarks"; 

  // Settings file tags

  public final static String CLIENTCERT_TAG = "clientcert.";
  public final static String IDENT_TAG = "ident.";
  public final static String NONE_IDENT_TAG = "none";

  // Settings file keys

  public final static String BOOKMARK_FILE = "bookmark.file";
  public final static String BOOKMARK_MAX_MENU = "bookmark.max.menu";
  public final static String EMOJI_STRIP_BOOKMARKS = "emoji.strip.bookmarks";
  public final static String GEMTEXT_INLINE_IMAGES = "gemtext.inline.images";
  public final static String LOG_LEVEL = "log.level";
  public final static String HISTORY_FILE = "history.file";
  public final static String HISTORY_SIZE = "history.size";
  public final static String HISTORY_ENABLED = "history.enabled";
  public final static String INLINE_IMAGE_WIDTH = "inline.image.width";
  public final static String UI_CONTROL_FONT = "ui.control_font"; 
  public final static String UI_DOCUMENT_CUSTOM_CSS = "ui.document.custom.css";
  public final static String UI_DOCUMENT_THEME = "ui.document.theme";
  public final static String UI_USER_FONT = "ui.user_font"; 
  public final static String UI_DOCUMENT_FONT_SIZE = "ui.document.font.size";
  public final static String UI_NEW_WINDOW_MODE = "ui,new_window";
  public final static String URLBAR_SEARCH_ENABLED = "urlbar.search.enabled";
  public final static String URLBAR_SEARCH_URL = "urlbar.search.url";
  public final static String URL_HOME = "url.home";
  public final static String WINDOW_W = "window.w";
  public final static String WINDOW_H = "window.h";

  // Settings file default values

  public final static String DEFLT_HISTORY_SIZE = "30";
  public final static String DEFLT_INLINE_IMAGE_WIDTH = "600";
  public final static String DEFLT_UI_CONTROL_FONT = "Sans 20; Emoji 20";
  public final static String DEFLT_UI_DOCUMENT_THEME = "light";
  public final static String DEFLT_UI_USER_FONT = "Sans 20; Emoji 20";
  public final static String DEFLT_UI_DOCUMENT_FONT_SIZE = "16";
  public final static String DEFLT_UI_NEW_WINDOW_MODE = "0";
  public final static String DEFLT_URLBAR_SEARCH_URL = "gemini://tlgs.one/search";
  public final static String DEFLT_WINDOW_H = "900";
  public final static String DEFLT_WINDOW_W = "1200";

  // File and directory names

  public final static String BOOKMARK_FILENAME = "bookmarks.gmi"; 
  public final static String HISTORY_FILENAME = "jgemini.history";
  public final static String IDENTS_DIRNAME = "idents"; 
  public final static String PREFS_FILE = "jgemini.properties"; 
  public final static String STATE_DIR_NAME = ".jgemini"; 
  public final static String SYS_PREFS_FILE = "jgemini.properties"; 

  // URLs
  
  // Default home page
  public final static String DEFLT_URL_HOME = "about:/jgemini_overview.md";
  // "New identity in new keystore" dialog
  public final static String DOC_EDIT_BOOKMARKS = 
    "about:/edit_bookmarks_dialog.md"; 
  public final static String DOC_EDIT_SETTINGS = 
    "about:/edit_settings_dialog.md"; 
  public final static String DOC_NEW_IDENT_DIALOG = 
    "about:/new_identity_dialog.md"; 
  // "Attach identity to keystore" dialog
  public final static String DOC_ATTACH_IDENT_DIALOG = 
    "about:/attach_identity_dialog.md"; 
  public final static String DOC_SET_IDENT_DIALOG = 
    "about:/set_identity_dialog.md"; 
  public final static String DOC_INDEX = 
    "about:/index.md"; 
  }

