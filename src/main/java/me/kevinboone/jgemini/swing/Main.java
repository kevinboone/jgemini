/*=========================================================================
  
  JGemini

  Main 

  Program execution starts here. All we do is create an HtmlViewer
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

public class Main
  {
  static HtmlViewer viewer;

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

    System.setProperty ("swing.aatext", "true");
    //System.setProperty ("swing.plaf.metal.controlFont", 
    //      Config.getConfig().getControlFont());
    String userFont = Config.getConfig().getUserFont();
    String controlFont = Config.getConfig().getControlFont();
    System.setProperty ("swing.plaf.metal.userFont", userFont.trim());
    System.setProperty ("swing.plaf.metal.controlFont", controlFont.trim());
    URL.setURLStreamHandlerFactory (new GeminiURLStreamHandlerFactory());

    // Boilerplate "invokeLater" to separate the main thread from the
    //  event dispatcher thread. Probably not necessary here.
    SwingUtilities.invokeLater (new Runnable()
      {
      public void run()
        {
        viewer = new HtmlViewer();
        viewer.setVisible (true);
        Logger.log (Main.class, "Show first page on startup");
        if (args.length >= 1)
          viewer.loadURL (args[0]);
        else
          viewer.loadURL (Config.getConfig().getHomePage());
        }
      });
    }
  }

