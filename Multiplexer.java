/*----------------------------------------- 
*
*   Multiplexer.java
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

class Multiplexer extends JFrame
{
  //Constants
  private static final int CANCEL = -123456, DEFAULT = -654321;

  private final JFileChooser chooser_;
  private Cutscene cutscene_;
  private Menu menu_;
  private FireMinigame fire_;
  private PaintMinigame paint_;
  private ClickPanel click_;

  //Constructor
  public Multiplexer(Dimension size)
  {
    //File selection dialog
    chooser_ = new JFileChooser();
    chooser_.setCurrentDirectory(new File("."));

    //Set frame attributes
    this.setTitle("CaveSim v1.0");
    this.setSize(size);
    ImageIcon icon = new ImageIcon("img/icon.png");  // Set icon
    this.setIconImage(icon.getImage());

    //Add menu bar
    this.addMenu();

    //Add game components
    //this.addCutscene(size);
    this.addGameMenu(size);
    this.addFireMinigame(size);
    this.addPaintMinigame(size);
  }

   //Must be called externally so that the GUI doesn't hang
   public void startCaveSim()
   {
/*
     cutscene_.togglePlay();
     while(!cutscene_.Done())
     {
       try        
       {
         Thread.sleep(1000);
       }  
       catch(InterruptedException ex) 
       {
         Thread.currentThread().interrupt();
       }
     }
     this.remove(cutscene_);
     revalidate();
     repaint();
*/
     menu_.togglePlay();
     while(!menu_.Done())
     {
       try        
       {
         Thread.sleep(1000);
       } 
       catch(InterruptedException ex) 
       {
         Thread.currentThread().interrupt();
       }
     }
     this.remove(menu_);
     revalidate();
     repaint();
     fire_.togglePlay();
     while(!fire_.Done())
     {
       try 
       {
         Thread.sleep(1000);
       }
       catch(InterruptedException ex)
       {
         Thread.currentThread().interrupt();
       }
     }
     paint_.togglePlay();
   }

  //Add menu to frame
  private void addMenu()
  {
    //-----------------------------------------------//
    //------------------File Menu--------------------//
    //-----------------------------------------------//

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

    //---------------------------------------------------------//
    //-------------------- Help Menu --------------------------//
    //---------------------------------------------------------//

    JMenu helpMenu = new JMenu("Help");

    JMenuItem rulesItem = new JMenuItem("Rules");
    rulesItem.addActionListener
    (
      new ActionListener() 
      {
        public void actionPerformed(ActionEvent e)
        {
        }
      }
     );
     helpMenu.add(rulesItem);

    //Add menu to a menu bar and add to this
    JMenuBar menuBar = new JMenuBar();
    menuBar.add(fileMenu);
    menuBar.add(helpMenu);
    this.setJMenuBar(menuBar);
  }  // private void addMenu()

  //---------------------------------------------------------------------//
  //-------------------------- Helper Functions -------------------------//
  //---------------------------------------------------------------------//

  private void addCutscene(Dimension size)
  { 
    cutscene_ = new Cutscene(size);
    getContentPane().add(cutscene_, BorderLayout.CENTER);
    pack();
  }
 
  private void addGameMenu(Dimension size)
  {
    menu_ = new Menu(size);
    getContentPane().add(menu_, BorderLayout.CENTER);
    pack();
  }

  private void addFireMinigame(Dimension size)
  {
    fire_ = new FireMinigame(size);
    getContentPane().add(fire_, BorderLayout.CENTER);
    pack();
    fire_.setFocusable(true); 
  }

  private void addPaintMinigame(Dimension size)
  {
    paint_ = new PaintMinigame(size);
    getContentPane().add(paint_, BorderLayout.CENTER);
    pack();
  }

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

}  // Class Multiplexer

