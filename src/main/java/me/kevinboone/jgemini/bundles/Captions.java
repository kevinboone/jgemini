/*=========================================================================
  
  JGemini

  Captions bundle 

  Copyright (c)2021 Kevin Boone, GPLv3.0 

=========================================================================*/
package me.kevinboone.jgemini.bundles;
import java.util.*;

public class Captions extends ListResourceBundle 
  {
  private Object[][] contents = 
    {
    {"attach_identity", "Attach identity to keystore"},
    {"bookmarks", "Bookmarks"},
    {"documentation", "JGemini documentation"},
    {"edit_config_file", "Edit settings"},
    {"new_identity", "New identity"},
    {"set_identity", "Set identity"},
    {"settings", "Settings"},
    {"release_notes", "Release notes"},
    };

  @Override
  public Object[][] getContents() 
     {
     return contents;
     }
  }

