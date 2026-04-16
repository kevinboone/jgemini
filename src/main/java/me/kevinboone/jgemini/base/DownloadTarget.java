/*=========================================================================
  
  JGemini

  DownloadTarget

  Copyright (c)2027 Kevin Boone, GPLv3.0 

=========================================================================*/

package me.kevinboone.jgemini.base;

import java.io.*;

/** A DownloadTarget controls where a Download sends its data to. A
    target might be a file or a media player, perhaps other things. */
public interface DownloadTarget
  {
/** Returns true if it makes sense to offer the user the choice to
      show the target location. It will if it's a local file. 
*/
  public boolean canOpenLocation();
/** It's important that the close() method close _all_ resources,
      particularly threads. 
*/
  public void close();

/** Delete the target, if it makes sense. Often it won't. 
*/
  public void delete();

/** Get a name to show the user. 'null' is never a good choice. 
*/
  public String getDisplayName();

/** Prepare the target to accept data. 
*/
  public OutputStream open() throws IOException;

/** If it makes sense to do so, show the storage location of the
    target. 
*/
  public void openLocation();
  }

