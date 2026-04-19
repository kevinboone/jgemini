/*=========================================================================
  
  JGemini

  Dialogs bundle 

  Copyright (c)2021 Kevin Boone, GPLv3.0 

=========================================================================*/
package me.kevinboone.jgemini.bundles;
import java.util.*;
import java.awt.event.*;
import javax.swing.*;

public class Dialogs extends ListResourceBundle 
  {
  private Object[][] contents = 
    {
    {"appearance_settings_pane_emoji_strip_bookmarks", 
      "Remove emojis from bookmarks"},
    {"appearance_settings_pane_emoji_strip_bookmarks_mnemonic", 
      KeyEvent.VK_J},
    {"appearance_settings_pane_base_font_size", "Document base font size (px)"},
    {"appearance_settings_pane_base_font_size_mnemonic", KeyEvent.VK_F},
    {"appearance_settings_pane_name", "Appearance"},
    {"appearance_settings_pane_mnemonic", KeyEvent.VK_A},

    {"attachidentitydialog_keystore_empty", "Keystore filename is blank"},
    {"attachidentitydialog_keystore_file", "Keystore file"},
    {"attachidentitydialog_keystore_file_mnemonic", KeyEvent.VK_F},
    {"attachidentitydialog_keystore_password", "Password"},
    {"attachidentitydialog_keystore_password_mnemonic", KeyEvent.VK_P},
    {"attachidentitydialog_help", "Help"},
    {"attachidentitydialog_help_mnemonic", KeyEvent.VK_H},
    {"attachidentitydialog_name", "Name"},
    {"attachidentitydialog_name_mnemonic", KeyEvent.VK_N},
    {"attachidentitydialog_password_empty", "Keystore password is blank"},
    {"attachidentitydialog_submit", "Submit"},
    {"attachidentitydialog_submit_mnemonic", KeyEvent.VK_S},

    {"downloads_no_file", "<No file>"},
    {"downloads_clear", "Clear"},
    {"downloads_clear_mnemonic", KeyEvent.VK_C},
    {"downloads_close", "Close"},
    {"downloads_close_accel", (KeyStroke.getKeyStroke
      (KeyEvent.VK_W, ActionEvent.CTRL_MASK))},
    {"downloads_help", "Help"},
    {"downloads_help_mnemonic", KeyEvent.VK_H},

    {"feed_manager_close", "Close"},
    {"feed_manager_close_accel", (KeyStroke.getKeyStroke
      (KeyEvent.VK_W, ActionEvent.CTRL_MASK))},
    {"feed_manager_help", "Help"},
    {"feed_manager_help_mnemonic", KeyEvent.VK_H},
    {"feed_manager_update", "Run"},
    {"feed_manager_update_mnemonic", KeyEvent.VK_R},
    {"feed_manager_stop", "Stop"},
    {"feed_manager_stop_mnemonic", KeyEvent.VK_S},
    {"feed_manager_already_running", 
        "Feed aggregation is already in progress"},
    {"feed_manager_not_running", 
        "Feed aggregator is not running"},

    {"feed_settings_pane_name", "Feeds"},
    {"feed_settings_pane_mnemonic", KeyEvent.VK_F},
    {"feed_settings_pane_max_age", "Maximum age (days)"},
    {"feed_settings_pane_max_age_mnemonic", KeyEvent.VK_X},
    {"feed_settings_pane_update_on_startup", "Update on startup"},
    {"feed_settings_pane_update_on_startup_mnemonic", KeyEvent.VK_U},

    {"editfiledialog_cancel", "Cancel"},
    {"editfiledialog_save", "Save"},
    {"editfiledialog_save_mnemonic", KeyEvent.VK_S},
    {"editfiledialog_docs", "Help"},
    {"editfiledialog_docs_mnemonic", KeyEvent.VK_H},

    {"enter_gemini_url", "Enter a URL"},
    {"enter_search_text", "Enter search text"},

    {"history_settings_pane_confirm_delete_history", "Delete existing history?"},
    {"history_settings_pane_enable", "Save history"},
    {"history_settings_pane_enable_mnemonic", KeyEvent.VK_E},
    {"history_settings_pane_name", "History"},
    {"history_settings_pane_mnemonic", KeyEvent.VK_Y},
    {"history_settings_pane_history_size", "Maximum history length"},
    {"history_settings_pane_history_size_mnemonic", KeyEvent.VK_M},
    {"history_settings_pane_clear_history", "Clear history"},
    {"history_settings_pane_clear_history_mnemonic", KeyEvent.VK_L},

    {"home_settings_pane_name", "Home"},
    {"home_settings_pane_mnemonic", KeyEvent.VK_O},
    {"home_settings_pane_home", "Home"},
    {"home_settings_pane_home_empty", "Home page URL is empty"},
    {"home_settings_pane_home_mnemonic", KeyEvent.VK_M},
    {"home_settings_pane_current", "Use current"},
    {"home_settings_pane_current_mnemonic", KeyEvent.VK_U},

    {"identitydialog_attach_keystore", "Attach new identity to an existing keystore"},
    {"identitydialog_cancel", "Cancel"},
    {"identitydialog_create_new_keystore", "Create a new keystore for a new identity"},
    {"identitydialog_identity_names", "Identity names"},
    {"identitydialog_help", "Help"},
    {"identitydialog_help_mnemonic", KeyEvent.VK_H},
    {"identitydialog_submit", "Submit"},
    {"identitydialog_submit_mnemonic", KeyEvent.VK_S},
    {"identitydialog_new", "New"},
    {"identitydialog_new_mnemonic", KeyEvent.VK_N},
    {"identitydialog_set_identity_for", "Set identity for"},
    {"identitydialog_unassigned", "Unassigned"},
    {"identitydialog_none", "None"},

    {"images_settings_pane_image_width", "Inline image width (px)"},
    {"images_settings_pane_image_width_mnemonic", KeyEvent.VK_W},
    {"images_settings_pane_gemtext_inline", "Gemtext inline images"},
    {"images_settings_pane_gemtext_inline_mnemonic", KeyEvent.VK_M},
    {"images_settings_pane_name", "Images"},
    {"images_settings_pane_mnemonic", KeyEvent.VK_I},

    {"media_settings_pane_media_clear", "Clear handling defaults"},
    {"media_settings_pane_media_clear_mnemonic", KeyEvent.VK_C},
    {"media_settings_pane_media_player", "Player"},
    {"media_settings_pane_media_player_mnemonic", KeyEvent.VK_P},
    {"media_settings_pane_name", "Media"},
    {"media_settings_pane_mnemonic", KeyEvent.VK_M},
    {"media_settings_pane_player_help", "Use full pathname of player application on Windows"},
    
    {"newidentitydialog_cancel", "Cancel"},
    {"newidentitydialog_created_identity", "Created identity"},
    {"newidentitydialog_cn", "Name for certificate"},
    {"newidentitydialog_cn_empty", "Name for certificate is blank"},
    {"newidentitydialog_cn_mnemonic", KeyEvent.VK_N},
    {"newidentitydialog_help", "Help"},
    {"newidentitydialog_help_mnemonic", KeyEvent.VK_H},
    {"newidentitydialog_keystore_exists", "This keystore file already exists.\nIf you want to use an existing keystore with a new identity, use the 'Attach identity' command instead"},
    {"newidentitydialog_keystore_password", "Password"},
    {"newidentitydialog_keystore_password_mnemonic", KeyEvent.VK_P},
    {"newidentitydialog_name", "Identity name"},
    {"newidentitydialog_password_empty", "Keystore password is blank"},
    {"newidentitydialog_name_mnemonic", KeyEvent.VK_I},
    {"newidentitydialog_submit", "Submit"},
    {"newidentitydialog_submit_mnemonic", KeyEvent.VK_S},

    {"search_settings_pane_name", "Search"},
    {"search_settings_pane_mnemonic", KeyEvent.VK_R},
    {"search_settings_pane_urlbar_search_enabled", "Search directly from URL bar"},
    {"search_settings_pane_urlbar_search_enabled_mnemonic", KeyEvent.VK_B}, 
    {"search_settings_pane_urlbar_search_url", "Search provider URL"}, 
    {"search_settings_pane_urlbar_search_url_mnemonic", KeyEvent.VK_U}, 

    {"settingsdialog_close", "Close"},
    {"settingsdialog_docs", "Help"},
    {"settingsdialog_docs_mnemonic", KeyEvent.VK_H},
    {"settingsdialog_submit", "Submit"},
    {"settingsdialog_submit_mnemonic", KeyEvent.VK_S},

    {"select_action_dialog_always", "Always handle this content this way"},
    {"select_action_dialog_always_mnemonic", KeyEvent.VK_L}, 
    {"select_action_dialog_cancel", "Cancel"},
    {"select_action_dialog_choice_desktop", "Hand off to desktop"},
    {"select_action_dialog_choice_desktop_mnemonic", KeyEvent.VK_A},
    {"select_action_dialog_choice_prompt_save", 
      "Prompt for a filename and save"},
    {"select_action_dialog_choice_prompt_save_mnemonic", KeyEvent.VK_P}, 
    {"select_action_dialog_choice_save", "Save without prompting"},
    {"select_action_dialog_choice_save_mnemonic", KeyEvent.VK_S},
    {"select_action_dialog_choice_stream", "Stream to player"},
    {"select_action_dialog_choice_stream_mnemonic", KeyEvent.VK_S},
    {"select_action_dialog_docs", "Help"},
    {"select_action_dialog_docs_mnemonic", KeyEvent.VK_H},
    {"select_action_dialog_filename_label", "Application"},
    {"select_action_dialog_filename_label_mnemonic", KeyEvent.VK_A},
    {"select_action_dialog_how_question", 
      "How should JGemini handle content of this type?"},
    {"select_action_dialog_submit", "Submit"},
    {"select_action_dialog_submit_mnemonic", KeyEvent.VK_S},

    {"textentrydialog_cancel", "Cancel"},
    {"textentrydialog_enter_text", "Enter text"},
    {"textentrydialog_submit", "Submit"},
    {"textentrydialog_submit_mnemonic", KeyEvent.VK_S},
    {"textentrydialog_cancel", "Cancel"},
    {"textentrydialog_enter_text", "Enter text"},

    {"theme_settings_pane_name", "Theme"},
    {"theme_settings_pane_mnemonic", KeyEvent.VK_T},
    {"themesettingspane_custom_css", "Custom CSS"},
    {"themesettingspane_custom_css_mnemonic", KeyEvent.VK_C},
    {"themesettingspane_built_in_themes", "Built-in themes"},
    {"themesettingspane_custom_wo_css", "The 'custom' theme requires a custom CSS file"},
    {"themesettingspane_custom_no_css", "Custom CSS file does not exist"},
    };

  @Override
  public Object[][] getContents() 
     {
     return contents;
     }
  }


