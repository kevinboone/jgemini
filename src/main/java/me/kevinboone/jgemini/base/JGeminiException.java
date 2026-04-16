/*=========================================================================
  
  JGemini

  JGeminiException 

  Copyright (c)2021 Kevin Boone, GPLv3.0 

=========================================================================*/
package me.kevinboone.jgemini.base;
import me.kevinboone.jgemini.Constants;

/** This exception was supposed to have been the base for many different
    application exceptions. In practice, it isn't used all that much.
*/
public class JGeminiException extends Exception
{
public JGeminiException (String s)
  {
  super (s);
  }
}

