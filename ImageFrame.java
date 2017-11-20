/*----------------------------------------- 
*
*   ImagePanel.java
*   Alex Eiffert
*   CAP3027  
*   2 November, 2017 
*   "CaveSim" v1.0
*
------------------------------------------*/

//GUI Support
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.awt.geom.*;
import java.io.*;
import javax.imageio.*;
import javax.swing.*;

//Utilities
import java.util.Random;
import java.util.Scanner;

class ImageFrame extends JFrame
{
  //Constants
  private static final int WIDTH = 1080, HEIGHT = 607;
  private static final int CANCEL = -123456, DEFAULT = -654321;

  private final JFileChooser chooser_;
  private Cutscene cutscene_;
  private Menu menu_;
  private ClickPanel click_;

  //Constructor
  public ImageFrame(int width, int height)
  {
    //File selection dialog
    chooser_ = new JFileChooser();
    chooser_.setCurrentDirectory(new File("."));

    //Set frame attributes
    this.setTitle("CaveSim v1.0");
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    screenSize.setSize(screenSize.getWidth()/2, screenSize.getHeight()/2);
    this.setSize(screenSize);

    //Add menu
    this.addMenu();

    //Add and play Cutscene
    //this.addCutscene(screenSize);
    //this.addMenu(screenSize);

    FireMinigame fire = new FireMinigame(screenSize);
    getContentPane().add(fire, BorderLayout.CENTER);
    pack();
    fire.setFocusable(true); 

  }

  //Add menu to frame
  private void addMenu()
  {
    //Frame menu bar
    JMenu fileMenu = new JMenu("File");

    //Add "Screenshot" option
    JMenuItem screenshotItem = new JMenuItem("Screenshot");
    screenshotItem.addActionListener
    (
      new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          save();
        }
      }
    );  // saveItem.addActionListener
    fileMenu.add(screenshotItem);

    //Add "Save Game" Option
    JMenuItem saveSystemItem = new JMenuItem("Save Game...");
    saveSystemItem.addActionListener
    (
      new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          File outputFile = getFile();
          if(true)//TODO
          {
            displayError("Could not load config file. Please " +
                         "make sure file is of the correct type and try again."); 
          }   
        }
      }
    );
    fileMenu.add(saveSystemItem);

    //Add "Load Game" Option
    JMenuItem loadSystemItem = new JMenuItem("Load Game...");
    loadSystemItem.addActionListener
    (
      new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          File inputFile = getFile();
          if(true)//TODO
          {
            displayError("Could not save config file. Please " +
                         "make sure grid is initialized and try again."); 
          }
        }
      }
    );
    fileMenu.add(loadSystemItem);

/*
    //Add "Randomly Populated World" option
    JMenuItem randomWorldItem = new JMenuItem("Randomly Populated World");
    randomWorldItem.addActionListener
    (
      new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
        }
      }
    );  // randomWorldItem.addActionListener
    fileMenu.add(randomWorldItem);
*/

    //Add "Save & Exit" option
    JMenuItem saveExitItem = new JMenuItem("Save & Exit...");
    saveExitItem.addActionListener
    (
      new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          //TODO
          System.exit(0);
        }
      }
    );
    fileMenu.add(saveExitItem);

    //Add "Exit" option
    JMenuItem exitItem = new JMenuItem("Exit");
    exitItem.addActionListener
    (
      new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          System.exit(0);
        }
      } 
    );
    fileMenu.add(exitItem);

    //Add menu to a menu bar and add to this
    JMenuBar menuBar = new JMenuBar();
    menuBar.add(fileMenu);
    this.setJMenuBar(menuBar);
  }  // private void addMenu()

  private void addCutscene(Dimension size)
  { 
    cutscene_ = new Cutscene(size);
    getContentPane().add(cutscene_, BorderLayout.CENTER);
    pack();
    cutscene_.togglePlay();
  }
 
  private void addMenu(Dimension size)
  {
    menu_ = new Menu(size);
    getContentPane().add(menu_, BorderLayout.CENTER);
    pack();
    menu_.togglePlay();
  }

  //---------------------------------------------------------------------//
  //-------------------------- Helper Functions -------------------------//
  //---------------------------------------------------------------------//

  //Display dialog and parse input for int value
  private int getInt(String message)
  {
    int val;
    try
    {
      String input = JOptionPane.showInputDialog(message);
      if(input == null)
        return CANCEL;
      else if(input.isEmpty())
        return DEFAULT;  
      val = Integer.parseInt(input);
    }
    catch(NumberFormatException e)
    {
      JOptionPane.showMessageDialog(this, "Please enter a valid input integer.", "ERROR",
                                    JOptionPane.ERROR_MESSAGE);
      return getInt(message);
    }
    if(val < 1)
    {
      JOptionPane.showMessageDialog(this, "Please enter an integer x >= 1.", "ERROR!",
                                    JOptionPane.ERROR_MESSAGE);
      return getInt(message);
    }
    return val;
  }  // private int getInput()

  //Display dialog and parse input for double
  private double getDbl(String message)
  {
    double val;
    try
    {
      String input = JOptionPane.showInputDialog(message);
      if(input == null)
         return CANCEL; 
      else if(input.isEmpty())
        return DEFAULT;
      val = Double.parseDouble(input);
    } 
    catch(NumberFormatException e)
    {
      JOptionPane.showMessageDialog(this, "Please enter a valid input.", "ERROR!",
                                    JOptionPane.ERROR_MESSAGE);
      return getDbl(message);
    }
    return val;
  }  // private double getDbl();

  //Display dialog and parse input for double with min, max
  private double getDbl(String message, double min, double max)
  {
    double val;
    try
    {
      String input = JOptionPane.showInputDialog(message);
      if(input == null)
         return CANCEL; 
      else if(input.isEmpty())
        return DEFAULT;
      val = Double.parseDouble(input);
    } 
    catch(NumberFormatException e)
    {
      JOptionPane.showMessageDialog(this, "Please enter a valid input.", "ERROR!",
                                    JOptionPane.ERROR_MESSAGE);
      return getDbl(message, min, max);
    }
    if(val < min || val > max)
    {
      JOptionPane.showMessageDialog(this, "Please enter a valid input " + min + 
                                    " <= x <= " + max + ".", "ERROR!",
                                    JOptionPane.ERROR_MESSAGE);
      return getDbl(message, min, max);
                                    
    }
    return val;
  }  // private double getDbl(); min, max

  private void displayError(String str)
  {
    JOptionPane.showMessageDialog(this, str, "ERROR!", JOptionPane.ERROR_MESSAGE);
  }

  private File getFile()
  {
    File outputFile = null;
    if(chooser_.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
      outputFile = chooser_.getSelectedFile();
    else
      return null;
    return outputFile;
  }

  //Save current BufferedImage to .png file
  private void save()
  {
    File outputFile = getFile();
    try
    {
      javax.imageio.ImageIO.write(cutscene_.getImage(), "png", outputFile);
    }
    catch (Exception e)
    {
      JOptionPane.showMessageDialog(this, "Could not save file. Please try again.", 
                                    "ERROR!", JOptionPane.ERROR_MESSAGE);
      return;
    }
  }

}  // Class ImageFrame

