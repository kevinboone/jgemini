/*=========================================================================
  
  JGemini

  Dialogs bundle 

  Copyright (c)2021 Kevin Boone, GPLv3.0 

=========================================================================*/
package me.kevinboone.jgemini.bundles;
import java.util.*;
import java.awt.event.*;

public class Dialogs extends ListResourceBundle 
  {
  private Object[][] contents = 
    {
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
    {"editfiledialog_cancel", "Cancel"},
    {"editfiledialog_save", "Save"},
    {"editfiledialog_save_mnemonic", KeyEvent.VK_S},
    {"editfiledialog_docs", "Help"},
    {"editfiledialog_docs_mnemonic", KeyEvent.VK_H},
    {"enter_gemini_url", "Enter a URL"},
    {"enter_search_text", "Enter search text"},
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
    {"textentrydialog_cancel", "Cancel"},
    {"textentrydialog_enter_text", "Enter text"},
    {"textentrydialog_submit", "Submit"},
    {"textentrydialog_submit_mnemonic", KeyEvent.VK_S},
    };

  @Override
  public Object[][] getContents() 
     {
     return contents;
     }
  }


