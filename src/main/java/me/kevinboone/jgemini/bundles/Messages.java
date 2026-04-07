/*=========================================================================
  
  JGemini

  Messages bundle 

  Copyright (c)2021 Kevin Boone, GPLv3.0 

=========================================================================*/
package me.kevinboone.jgemini.bundles;
import java.util.*;

public class Messages extends ListResourceBundle 
  {
  private final static String ABOUT_MESSAGE 
    = "<p></p><p>A browser for 'Small Net' protocols."
    + "</p><p></p><p>Maintained by Kevin Boone, and distributed under the terms "
    + "of the GNU PUblic Licence, v3.0.</p>"
    + "<p></p><p>For more information, see "
    +  "<b>https://kevinboone.me/jgemini.html</b>";

  private Object[][] contents = 
    {
    {"about", ABOUT_MESSAGE},
    {"already_bookmarked", "Page already bookmarked"},
    {"bookmark_added", "Bookmark added"},
    {"could_not_parse_uri", "Could not parse URI"},
    {"empty_resp", "File/document is empty"},
    {"identity_exists", "Identity already exists"},
    {"ident_name_empty", "Identity name cannot be empty"},
    {"ident_name_format", "Identity name can contain only letters, digits, and '_'"},
    {"ident_remote_only", "Identity is only relevant to remote hosts"},
    {"keystore_file_empty", "Keystore filename is blank"},
    {"loaded", "Loaded"},
    {"loading", "Loading..."},
    {"not_found", "Not found"},
    {"name_empty", "'Name' field cannot be empty"},
    {"password_empty", "'Password' field cannot be empty"},
    {"protocol_no_ident", "This protocol does not support identity"},
    {"save_only_text_message", "'Save' can only be used on text documents"},
    {"search_wrapped_around", "Search wrapped around to top"},
    {"saved_file", "Saved file"},
    {"status_line_too_long", "Status line too long"},
    {"unknown_host", "Unknown host"},
    {"unknown_url", "Unknown URL"},
    {"unsup_encoding_resp", "Server returned a response with an unsupported encoding"},
    {"wrote_file", "Wrote file"},
    };

  @Override
  public Object[][] getContents() 
     {
     return contents;
     }
  }
