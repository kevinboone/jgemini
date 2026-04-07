/*=========================================================================
  
  JGemini

  Tooltips bundle 

  Copyright (c)2021 Kevin Boone, GPLv3.0 

=========================================================================*/
package me.kevinboone.jgemini.bundles;
import java.util.*;

public class Tooltips extends ListResourceBundle 
  {
  private Object[][] contents = 
    {
    {"back", "Go back"},
    {"home", "Go home"},
    {"identity", "Manage identity"},
    {"refresh", "Refresh page"},
    {"stop", "Stop loading page"},
    };

  @Override
  public Object[][] getContents() 
     {
     return contents;
     }
  }


