/*=========================================================================
  
  JGemini

  UI 

  Copyright (c)2021 Kevin Boone, GPLv3.0 

=========================================================================*/

package me.kevinboone.jgemini.swing;
import me.kevinboone.jgemini.base.*;

import java.io.*;
import java.awt.*;

/** Start the Swing UI. This amounts, essentially, to instantiating
    MainWindow. */
public class SwingUI implements UI
  {
  private MainWindow mainWindow;

  @Override
  public void start()
    {
    /*
    // Code of this sort should make an emoji font available to style
    //   controls -- but does not seem to
    try
      {
      File ttf = new File ("NotoEmoji-Regular.ttf");
      Font font = Font.createFont(Font.TRUETYPE_FONT, ttf).deriveFont(16f);
      GraphicsEnvironment GE = GraphicsEnvironment.getLocalGraphicsEnvironment();
      GE.registerFont(font);
      } catch (Exception e) {e.printStackTrace();}
    */

    Logger.log (Main.class, Logger.INFO, "Starting Swing UI");
    System.setProperty ("swing.aatext", "true");
    String userFont = Config.getConfig().getUserFont();
    String controlFont = Config.getConfig().getControlFont();
    System.setProperty ("swing.plaf.metal.userFont", userFont.trim());
    System.setProperty ("swing.plaf.metal.controlFont", controlFont.trim());

    mainWindow = new MainWindow();
    mainWindow.setVisible (true);
    } 

  @Override
  public void loadURI (String uri)
    {
    mainWindow.loadURI (uri);
    }
  }

