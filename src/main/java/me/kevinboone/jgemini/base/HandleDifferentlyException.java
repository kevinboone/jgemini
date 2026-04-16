/*============================================================================

  JGemini

  HandleDifferentlyException 

  Copyright (c)2021-2026 Kevin Boone, GPLv3.0

============================================================================*/
package me.kevinboone.jgemini.base;

/** Protocol handlers throw this exception to indicate that the
    caller should do something differently than just slurp the remote document
    into memory. The exception will be thrown, for example, if the protocol
    handler starts to download a media file. In some cases the download may
    have been interrupted whilst the application asks the user what to do
    with the file. In others, default handling instructions will have been
    retrieved from the configuration file. In any case, the "action" 
    attribute will contain one of the codes in ContentHandlerAction.
*/
public class HandleDifferentlyException extends Exception 
  {
  private int action;

  public HandleDifferentlyException (int action)
    {
    this.action = action;
    }

  public int getAction()
    {
    return action;
    }
  }



