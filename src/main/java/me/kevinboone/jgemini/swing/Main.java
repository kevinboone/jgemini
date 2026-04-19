/*=========================================================================
  
  JGemini

  Main 

  Copyright (c)2021 Kevin Boone, GPLv3.0 

=========================================================================*/

package me.kevinboone.jgemini.swing;
import  me.kevinboone.jgemini.protocol.*;
import javax.swing.*;
import javax.net.ssl.*;
import java.security.cert.X509Certificate;
import java.net.*;
import java.io.*;
import java.awt.*;
import me.kevinboone.jgemini.Constants;
import me.kevinboone.jgemini.base.*;

import java.security.*;
import javax.security.auth.x500.*;
import javax.security.cert.*;
import org.bouncycastle.asn1.*;
import org.bouncycastle.asn1.x509.*;
import org.bouncycastle.cert.*;
import org.bouncycastle.cert.jcajce.*;
import java.util.*;
import java.math.*;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.jce.provider.*;

/** Main contains methods for management of the application as 
    a whole. 
*/
public class Main
  {
  private static MainWindow viewer;
  private final static ResourceBundle messagesBundle = 
    ResourceBundle.getBundle ("me.kevinboone.jgemini.bundles.Messages");

/**
  Program execution starts here. Mostly what we do is create an instance of
  the "UI" class, and have it load a page.
*/
  public static void main (String[] args)
      throws Exception
    {
    Security.addProvider (new BouncyCastleProvider());  

    if (System.getProperty ("jgemini.dumpfonts") != null)
      {
      String fonts[] = 
        GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();

      for (int i = 0; i < fonts.length; i++)
        System.out.println(fonts[i]);
      
      System.exit(0);
      }

    // Tell the JVM about all the new URLs we support in this application
    URL.setURLStreamHandlerFactory (new JGeminiURLStreamHandlerFactory());

    Logger.log (Main.class, Logger.INFO, "Starting up");

    UI ui = new SwingUI();
    ui.start();

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

  /** Returns true if closing a window now would (all being well)
      lead to the application closing down. 
  */
  public static boolean closingWouldExit()
    {
    int topLevels = 0;
    Frame[] frames = Frame.getFrames();
    for (Frame frame : frames) 
      {
      if (frame instanceof JFrame && frame.isDisplayable()) 
        {
        JFrame jframe = (JFrame)frame;
        topLevels++;
        }
      }
    if (topLevels <= 1) return true;
    return false;
    }

  /** Do a full exit, whether Swing wants to or not. It's more likely
      to want to, if we can clean up all the background downloads, which
      is more elegant than just shutting down the JVM.
  */
  public static void exit()
    {
    DownloadMonitor downloadMonitor = DefaultDownloadMonitor.getInstance();
    downloadMonitor.cancelAll();
    FeedHandler feedHandler = DefaultFeedHandler.getInstance();
    feedHandler.cancelUpdate();

    System.exit (0);
    } 

  /** Returns true either if there are no ongoing background transfers,
      or the user is willing for them to be cancelled. 
  */
  public static boolean okToExit ()
    {
    DownloadMonitor downloadMonitor = DefaultDownloadMonitor.getInstance();
    int active = downloadMonitor.getActiveDownloadCount();
    if (active == 0) return true;
    String message;
    if (active == 1)
      message = messagesBundle.getString ("query_cancel_download"); 
    else
      message = messagesBundle.getString ("query_cancel_downloads"); 

    if (JOptionPane.showConfirmDialog (null, message, Constants.APP_NAME,
      JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE)
        == JOptionPane.YES_OPTION)
      return true;
    return false;
    } 
  }


