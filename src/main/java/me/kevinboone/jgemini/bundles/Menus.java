/*=========================================================================
  
  JGemini

  Menus bundle 

  Copyright (c)2021 Kevin Boone, GPLv3.0 

=========================================================================*/
package me.kevinboone.jgemini.bundles;
import java.util.*;
import java.awt.event.*;
import javax.swing.*;

public class Menus extends ListResourceBundle 
  {
  private Object[][] contents = 
    {
    {"bookmarks", "Bookmarks"},
    {"bookmarks_mnemonic", KeyEvent.VK_B},

    {"bookmarks_edit", "Edit..."},
    {"bookmarks_edit_mnemonic", KeyEvent.VK_E},

    {"bookmarks_show_all", "Show all"},
    {"bookmarks_show_all_mnemonic", KeyEvent.VK_S},

    {"bookmarks_this_page", "Bookmark this page"},
    {"bookmarks_this_page_mnemonic", KeyEvent.VK_B},

    {"context_copy_link", "Copy link"},
    {"context_download", "Download..."},
    {"context_open", "Open"},
    {"context_open_in_new_window", "Open in new window"},
    {"context_stream", "Stream to player"},

    {"file", "File"},
    {"file_mnemonic", KeyEvent.VK_F},

    {"edit", "Edit"},
    {"edit_mnemonic", KeyEvent.VK_E},

    {"edit_copy", "Copy"},
    {"edit_copy_mnemonic", KeyEvent.VK_C},
    {"edit_copy_accel", (KeyStroke.getKeyStroke
      (KeyEvent.VK_C, ActionEvent.CTRL_MASK))},

    {"edit_find_in_page", "Find in page..."},
    {"edit_find_in_page_mnemonic", KeyEvent.VK_F},
    {"edit_find_in_page_accel", (KeyStroke.getKeyStroke
      (KeyEvent.VK_F, ActionEvent.CTRL_MASK))},

    {"edit_find_next", "Find next"},
    {"edit_find_next_mnemonic", KeyEvent.VK_N},
    {"edit_find_next_accel", (KeyStroke.getKeyStroke
      (KeyEvent.VK_G, ActionEvent.CTRL_MASK))},

    {"edit_select_all", "Select all"},
    {"edit_select_all_mnemonic", KeyEvent.VK_A},
    {"edit_select_all_accel", (KeyStroke.getKeyStroke
      (KeyEvent.VK_A, ActionEvent.CTRL_MASK))},

    {"edit_settings", "Settings..."},
    {"edit_settings_mnemonic", KeyEvent.VK_S},

    {"feeds_aggregate", "Update subscriptions"},
    {"feeds_aggregate_mnemonic", KeyEvent.VK_U},
    {"feeds", "Subscriptions"},
    {"feeds_mnemonic", KeyEvent.VK_S},
    {"feeds_edit", "Edit..."},
    {"feeds_edit_mnemonic", KeyEvent.VK_E},
    {"feeds_subscribe", "Subscribe to this page"},
    {"feeds_subscribe_mnemonic", KeyEvent.VK_S},
    {"feeds_view_aggregated", "View aggregated posts"},
    {"feeds_view_aggregated_mnemonic", KeyEvent.VK_V},


    {"file_close", "Close"},
    {"file_close_mnemonic", KeyEvent.VK_C},
    {"file_close_accel", (KeyStroke.getKeyStroke
      (KeyEvent.VK_W, ActionEvent.CTRL_MASK))},

    {"file_exit", "Exit"},
    {"file_exit_mnemonic", KeyEvent.VK_X},
    {"file_exit_accel", (KeyStroke.getKeyStroke
      (KeyEvent.VK_Q, ActionEvent.CTRL_MASK))},

    {"file_new", "New"},
    {"file_new_mnemonic", KeyEvent.VK_N},
    {"file_new_accel", (KeyStroke.getKeyStroke
      (KeyEvent.VK_N, ActionEvent.CTRL_MASK))},

    {"file_open_link", "Open link..."},
    {"file_open_link_mnemonic", KeyEvent.VK_O},
    {"file_open_link_accel", (KeyStroke.getKeyStroke
      (KeyEvent.VK_O, ActionEvent.CTRL_MASK))},

    {"file_save", "Save..."},
    {"file_save_mnemonic", KeyEvent.VK_S},
    {"file_save_accel", (KeyStroke.getKeyStroke
      (KeyEvent.VK_S, ActionEvent.CTRL_MASK))},

    {"go", "Go"},
    {"go_mnemonic", KeyEvent.VK_G},

    {"go_back", "Back"},
    {"go_back_mnemonic", KeyEvent.VK_B},
    {"go_back_accel", (KeyStroke.getKeyStroke
      (KeyEvent.VK_BACK_SPACE, 0))},

    {"go_home", "Home"},
    {"go_home_mnemonic", KeyEvent.VK_H},
    {"go_home_accel", (KeyStroke.getKeyStroke
      (KeyEvent.VK_H, ActionEvent.CTRL_MASK))},

    {"go_root", "Site root"},
    {"go_root_mnemonic", KeyEvent.VK_R},
    {"go_root_accel", (KeyStroke.getKeyStroke
      (KeyEvent.VK_I, ActionEvent.CTRL_MASK))},

    {"go_stop", "Stop"},
    {"go_stop_mnemonic", KeyEvent.VK_S},

    {"help", "Help"},
    {"help_mnemonic", KeyEvent.VK_H},

    {"help_docs", "Documentation"},
    {"help_docs_mnemonic", KeyEvent.VK_D},

    {"help_about", "About JGemini..."},
    {"help_about_mnemonic", KeyEvent.VK_A},

    {"help_release_notes", "Release notes"},
    {"help_release_notes_mnemonic", KeyEvent.VK_R},

    {"settings", "Settings"},
    {"settings_mnemonic", KeyEvent.VK_T},

    {"settings_edit", "Settings editor..."},
    {"settings_edit_mnemonic", KeyEvent.VK_E},

    {"settings_reload", "Reload settings"},
    {"settings_reload_mnemonic", KeyEvent.VK_R},

    {"tools", "Tools"},
    {"tools_mnemonic", KeyEvent.VK_T},

    {"tools_downloads", "Downloads..."},
    {"tools_downloads_mnemonic", KeyEvent.VK_D},
    {"tools_feed_manager", "Feed aggregator..."},
    {"tools_feed_manager_mnemonic", KeyEvent.VK_F},
    {"tools_identity", "Set/manage idenity..."},
    {"tools_identity_mnemonic", KeyEvent.VK_S},
    {"tools_server_cert", "Server certificate info..."},
    {"tools_server_cert_mnemonic", KeyEvent.VK_E},

    {"view", "View"},
    {"view_mnemonic", KeyEvent.VK_V},

    {"view_refresh", "Refresh"},
    {"view_refresh_mnemonic", KeyEvent.VK_R},
    {"view_refresh_accel", (KeyStroke.getKeyStroke
      (KeyEvent.VK_R, ActionEvent.CTRL_MASK))},
    {"view_theme", "Theme..."},
    {"view_theme_mnemonic", KeyEvent.VK_T},
    {"view_zoom_in", "Zoom in"},
    {"view_zoom_in_mnemonic", KeyEvent.VK_I},
    {"view_zoom_in_accel", (KeyStroke.getKeyStroke
      (KeyEvent.VK_OPEN_BRACKET, ActionEvent.CTRL_MASK))},

    {"view_zoom_out", "Zoom out"},
    {"view_zoom_out_mnemonic", KeyEvent.VK_O},
    {"view_zoom_out_accel", (KeyStroke.getKeyStroke
      (KeyEvent.VK_CLOSE_BRACKET, ActionEvent.CTRL_MASK))},

    };

  @Override
  public Object[][] getContents() 
     {
     return contents;
     }
  }


