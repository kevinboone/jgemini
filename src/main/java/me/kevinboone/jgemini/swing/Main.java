/*=========================================================================
  
  JGemini

  Main 

  Program execution starts here. All we do is create a MainWindow
  object and have it load a page.

  Copyright (c)2021 Kevin Boone, GPLv3.0 

=========================================================================*/

package me.kevinboone.jgemini.swing;
import  me.kevinboone.jgemini.protocol.*;
import javax.swing.SwingUtilities;
import javax.swing.JOptionPane;
import javax.net.ssl.*;
import java.security.cert.X509Certificate;
import java.net.*;
import java.io.*;
import java.awt.GraphicsEnvironment;
import me.kevinboone.jgemini.base.*;

public class Main
  {
  static MainWindow viewer;

  public static void main (String[] args)
      throws Exception
    {
    if (System.getProperty ("jgemini.dumpfonts") != null)
      {
      String fonts[] = 
        GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();

      for (int i = 0; i < fonts.length; i++)
        System.out.println(fonts[i]);
      
      System.exit(0);
      }

    // Tell the JVM about all the new URLs we support in this application
    URL.setURLStreamHandlerFactory (new GeminiURLStreamHandlerFactory());

    Logger.log (Main.class, Logger.INFO, "Starting up");

    UI ui = new SwingUI();
    ui.start();

/*
    System.setProperty ("swing.aatext", "true");
    String userFont = Config.getConfig().getUserFont();
    String controlFont = Config.getConfig().getControlFont();
    System.setProperty ("swing.plaf.metal.userFont", userFont.trim());
    System.setProperty ("swing.plaf.metal.controlFont", controlFont.trim());

    viewer = new MainWindow();
    viewer.setVisible (true);
*/
    Logger.log (Main.class, Logger.INFO, "Show first page on startup");
    if (args.length >= 1)
      {
      // Just for ease of use, let's see if it's a local file
      File file = new File (args[0]);
      if (file.isFile())
	{
	// It don't know if this will work on Windows. But who
	//   uses the command line on Windows?
	ui.loadURI ("file://" + file.getAbsolutePath());
	}
      else
	{
	ui.loadURI (args[0]);
	}
      }
    else
      ui.loadURI (Config.getConfig().getHomePage());

    Logger.log (Main.class, Logger.INFO, "Initial set-up done");
    }
  }

