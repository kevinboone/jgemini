/*============================================================================

  JGemini

  FileUtil 

  Copyright (c)2021-2026 Kevin Boone, GPLv3.0

============================================================================*/
package me.kevinboone.utils.file;

import java.io.*;
import java.net.*;

public class FileUtil 
  {
  public static void appendStringToFile (String filename, String string)
      throws IOException
    {
    FileWriter fw = new FileWriter (filename, true);
    BufferedWriter bw = new BufferedWriter (fw);
    bw.write (string);
    bw.flush();
    bw.close();
    }
  }

