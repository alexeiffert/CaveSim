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
  private static final int TIMER = 120000;  // 2 minutes, ++hunger, ++boredom
  private static final int HUNGER = 5;
  private static final int BOREDOM = 5;

  private final JFileChooser chooser_;
  private Dimension size_;
  private JLabel stats_;
  private Timer timer_;
  int food_, hunger_, boredom_, intelligence_;

  private Cutscene cutscene_;
  private Menu menu_;
  private boolean isSurvival_;
  private FireMinigame fire_;
  private PaintMinigame paint_;
  private HuntMinigame hunt_;
  private MessageScreen screen_;

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

    //Initialize game components
    stats_ = new JLabel("", SwingConstants.CENTER);
    stats_.setOpaque(true);
    stats_.setBackground(Color.BLACK);
    food_ = 0;
    hunger_ = HUNGER;
    boredom_ = BOREDOM;
    intelligence_ = 0;
    size_ = size;
    cutscene_ = null;
    menu_ = null;
    isSurvival_ = false;
    fire_ = null;
    paint_ = null;
    hunt_ = null;

    //Attrition timer
    timer_ = new Timer(TIMER, new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          if(isSurvival_)
          {
            ++boredom_;
            ++hunger_;
          }
        }
      });
    timer_.start();
  }

    //Plays cutscene and starts the main game infinite loop
    //Must be called externally so that the GUI doesn't hang
  public void startCaveSim()
  {
    this.addCutscene(size_);
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
    this.addGameMenu(size_, isSurvival_);
    menu_.togglePlay();
    while(true)
    {
      if(isSurvival_)
      {
        if(food_ > 5)
          food_ = 5;
        else if(food_ < 0)
          food_ = 0;
        if(hunger_ < 0)
          hunger_ = 0;
        if(boredom_ < 0)
          boredom_ = 0;
        if(intelligence_ < 0)
          intelligence_ = 0;
        stats_.setText("Food: " + food_ + " Hunger: " + hunger_ + 
                       " Boredom: " + boredom_ + " Intelligence " + 
                       intelligence_);
        stats_.setFont(new Font(Font.SERIF, Font.BOLD, 50));
        this.getContentPane().add(stats_, BorderLayout.NORTH);
        pack(); 
        revalidate();
        repaint();
        if(hunger_ >= 10)
        {
          addMessageScreen(size_, 0);
          while(!screen_.Done())
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
          resetStats();
          isSurvival_ = false;
          this.addGameMenu(size_, isSurvival_);
          menu_.togglePlay();
        }
        else if(boredom_ >= 10)
        {
          addMessageScreen(size_, 1);
          while(!screen_.Done())
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
          resetStats();
          isSurvival_ = false;
          this.addGameMenu(size_, isSurvival_);
          menu_.togglePlay();
        }
        else if(intelligence_ >= 5)
        {
          addMessageScreen(size_, 2);
          while(!screen_.Done())
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
          resetStats();
          isSurvival_ = false;
          this.addGameMenu(size_, isSurvival_);
          menu_.togglePlay();
        }
      }  //if Survival Mode only

      switch(menu_.getSelection())
      {
        case 0:  // No selection
        {
          try        
          {
            Thread.sleep(500);
          } 
          catch(InterruptedException ex) 
          {
            Thread.currentThread().interrupt();
          }
          break;
        }
        case 1:  // Fish minigame
        {
          if(isSurvival_)
          {
            ++boredom_;
            //food_ += fish_.getScore();
            //if(fish_.getScore() > 5)
            ++intelligence_;
          }
          break;
        }
        case 2:  // Hunt minigame
        {
          this.addHuntMinigame(size_); 
          hunt_.togglePlay();
          while(!hunt_.Done())
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
          if(isSurvival_)
          {
            ++boredom_;
            food_ += hunt_.getScore();
            if(hunt_.getScore() > 5)
              ++intelligence_;
          }
          this.addGameMenu(size_, isSurvival_);
          menu_.togglePlay();
          break;
        }
        case 3:  // Fire minigame
        {
          this.addFireMinigame(size_); 
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
          if(isSurvival_)
          {
            if(fire_.success())
            {
              --boredom_;
              hunger_ -= food_;
              food_ = 0;
              ++intelligence_;
            }
            else
            {
              --food_;
              ++hunger_;
              --intelligence_;
            }
          }
          this.addGameMenu(size_, isSurvival_);
          menu_.togglePlay();
          break;
        }
        case 4:  // Paint minigame
        {
          this.addPaintMinigame(size_);
          paint_.togglePlay();
          while(!paint_.Done())
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
          if(isSurvival_)
          {
            --boredom_;
            ++hunger_;
            intelligence_ += paint_.getScore();
          } 
          this.addGameMenu(size_, isSurvival_);
          menu_.togglePlay();
          break;
        }
        case 5:  // Cups minigame
        {
          break;
        }
        case 6:
        {
          isSurvival_ = true;
          this.addGameMenu(size_, isSurvival_);
          menu_.togglePlay();
        }
      } // switch
    }  // while true
  }  // public void startCaveSim()

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
          chooser_.setCurrentDirectory(new File("./Screenshots"));
          screenshot();
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
          if(!isSurvival_)
          {
            displayError("Nothing to save!");
            return;
          }
          chooser_.setCurrentDirectory(new File("./SavedGames"));
          File outputFile = getFile();
          try
          {
            BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));
            writer.write(food_ + " " + hunger_ + " " + 
                         boredom_ + " " + intelligence_);
            writer.close();
          }
          catch(Exception exc)
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
          chooser_.setCurrentDirectory(new File("./SavedGames"));
          File inputFile = getFile();
          try
          {
            Scanner s = new Scanner(new BufferedReader(new FileReader(inputFile)));
            food_ = Integer.parseInt(s.next());
            hunger_ = Integer.parseInt(s.next());
            boredom_ = Integer.parseInt(s.next());
            intelligence_ = Integer.parseInt(s.next());
          }
          catch(Exception exc)
          {
            displayError("Could not load config file. Please " +
                         "make sure file is of the correct type and try again.");            
          }
        }
      }
    );
    fileMenu.add(loadSystemItem);

    //Add "Save & Exit" option
    JMenuItem saveExitItem = new JMenuItem("Save & Exit...");
    saveExitItem.addActionListener
    (
      new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {          
          if(!isSurvival_)
          {
            displayError("Nothing to save!");
            return;
          }
          chooser_.setCurrentDirectory(new File("./SavedGames"));
          File outputFile = getFile();
          try
          {
            BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));
            writer.write(food_ + " " + hunger_ + " " + 
                         boredom_ + " " + intelligence_);
            writer.close();
          }
          catch(Exception exc)
          {
            displayError("Could not load config file. Please " +
                         "make sure file is of the correct type and try again.");         
          }   
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
          //Called on worker thread, but displays on EDT
          new Thread
          (
            new Runnable()
            {
              public void run()
              {
                addMessageScreen(size_, 4);
                while(!screen_.Done())
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
              addGameMenu(size_, isSurvival_);
              menu_.togglePlay();
              }
            }
          ).start();
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
    getContentPane().removeAll();
    getContentPane().add(cutscene_, BorderLayout.CENTER);
    pack();
    revalidate();
    repaint();
  }
 
  private void addGameMenu(Dimension size, boolean isSurvival)
  {
    menu_ = new Menu(size, isSurvival);
    getContentPane().removeAll();
    getContentPane().add(menu_, BorderLayout.CENTER);
    pack();
    revalidate();
    repaint();
  }

  private void addFireMinigame(Dimension size)
  {
    fire_ = new FireMinigame(size);
    getContentPane().removeAll();
    getContentPane().add(fire_, BorderLayout.CENTER);
    pack();
    fire_.setFocusable(true); 
    fire_.requestFocus();
    revalidate();
    repaint();
  }

  private void addPaintMinigame(Dimension size)
  {
    paint_ = new PaintMinigame(size);
    getContentPane().removeAll();
    getContentPane().add(paint_, BorderLayout.CENTER);
    pack();
    revalidate();
    repaint();
  }

  private void addHuntMinigame(Dimension size)
  {
    hunt_ = new HuntMinigame(size);
    getContentPane().removeAll();
    getContentPane().add(hunt_, BorderLayout.CENTER);
    pack();
    revalidate();
    repaint();
  }

  private void addMessageScreen(Dimension size, int index)
  {
    screen_ = new MessageScreen(size, index);
    getContentPane().removeAll();
    getContentPane().add(screen_, BorderLayout.CENTER);
    pack();
    revalidate();
    repaint();
  }

  private void resetStats()
  {
    hunger_ = HUNGER;
    boredom_ = BOREDOM;
    food_ = 0;
    intelligence_ = 0;
  }

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
  private void screenshot()
  {
    File outputFile = getFile();
    BufferedImage img = new BufferedImage((int)getWidth(), (int)getHeight(), 
                                          BufferedImage.TYPE_INT_ARGB);
    paint(img.getGraphics());
    
    try
    {
      javax.imageio.ImageIO.write(img, "png", outputFile);
    }
    catch (Exception e)
    {
      JOptionPane.showMessageDialog(this, "Could not save file. Please try again.", 
                                    "ERROR!", JOptionPane.ERROR_MESSAGE);
      return;
    }
  }

}  // Class Multiplexer

