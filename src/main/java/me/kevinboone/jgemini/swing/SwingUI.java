/*=========================================================================
  
  JGemini

  UI 

  The starting point for the Swing UI

  Copyright (c)2021 Kevin Boone, GPLv3.0 

=========================================================================*/

package me.kevinboone.jgemini.swing;
import me.kevinboone.jgemini.base.*;

public class SwingUI implements UI
  {
  private MainWindow mainWindow;

  @Override
  public void start()
    {
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

