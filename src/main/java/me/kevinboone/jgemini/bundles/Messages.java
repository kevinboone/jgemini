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
    = "A browser for 'small net' protocols.\n\n"
    + "Maintained by Kevin Boone, and distributed under the terms "
    + "of the GNU PUblic Licence, v3.0. "
    + "For more information, see "
    +  "https://kevinboone.me/jgemini.html.\n";

  private Object[][] contents = 
    {
    {"about", ABOUT_MESSAGE},
    {"already_bookmarked", "Page already bookmarked"},
    {"already_subscribed", "Already subscribed"},
    {"bookmark_added", "Bookmark added"},
    {"cancelled", "Cancelled"},
    {"could_not_parse_uri", "Could not parse URI"},
    {"done", "Done"},
    {"downloaded", "Downloaded"},
    {"downloading", "Downloading..."},
    {"empty_resp", "File/document is empty"},
    {"empty_aggregation_text", "No feeds have so far been aggregated"},
    {"feed_added", "Feed added"},
    {"feed_aggregation_complete", "Feed aggregation complete"},
    {"feed_aggregation_cancelled", "Feed aggregation cancelled"},
    {"feed_aggregation_started", "Feed aggregation started"},
    {"feed_aggregator", "Feed aggregator"},
    {"feed_no_updates", "No feed has been updated within the current time limit"},
    {"feed_aggregated_title", "Aggregated posts from subscriptions"},
    {"handling_cleared", "Default file handling cleared"},
    {"identity_exists", "Identity already exists"},
    {"ident_name_empty", "Identity name cannot be empty"},
    {"ident_name_format", "Identity name can contain only letters, digits, and '_'"},
    {"ident_remote_only", "Identity is only relevant to remote hosts"},
    {"keystore_file_empty", "Keystore filename is blank"},
    {"loaded", "loaded"},
    {"loading", "Loading..."},
    {"not_found", "Not found"},
    {"name_empty", "'Name' field cannot be empty"},
    {"no_certinfo", "No certificate information is available for this page"},
    {"no_stream_player", "No stream player application is defined in the configuration"},
    {"password_empty", "'Password' field cannot be empty"},
    {"page_not_remote", "This page is not from a remote server"},
    {"parsing_as_atom", "Parsing the response as an Atom feed"},
    {"parsing_as_gemlog", "Parsing the response as a gmisub feed"},
    {"posts_added", "Posts added"},
    {"protocol_no_ident", "This protocol does not support identity"},
    {"query_cancel_download", "This will cancel an active transfer. Continue?"},
    {"query_cancel_downloads", "This will cancel all active transfers. Continue?"},
    {"query_overwrite_file", "Overwrite file?"},
    {"save_only_text_message", "'Save' can only be used on text documents"},
    {"response_not_feed", "Response was not of a type associated with a feed"},
    {"search_wrapped_around", "Search wrapped around to top"},
    {"saved_file", "Saved file"},
    {"status_line_too_long", "Status line too long"},
    {"streamed", "Streamed"},
    {"streaming", "Streaming..."},
    {"stream_interrupted", "Stream playback interrupted"},
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
