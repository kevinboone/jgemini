/*=========================================================================
  
  JGemini

  IdentException 

  Copyright (c)2021 Kevin Boone, GPLv3.0 

=========================================================================*/
package me.kevinboone.jgemini.base;
import me.kevinboone.jgemini.Constants;

/** This exception is thrown from methods in the IdentUtil class, to
    indicate that an identity name selected by the user is invalid in
    some way.
*/
public class IdentException extends JGeminiException 
{
public IdentException (String s)
  {
  super (s);
  }
}


