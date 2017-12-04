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
  private static final int TIMER = 120000;  // 2 minutes, ++hunger, ++boredom
  private static final int HUNGER = 5;
  private static final int BOREDOM = 5;

  private final JFileChooser chooser_;
  private Dimension size_;
  private JLabel stats_;
  private Timer timer_;

  private int food_, hunger_, boredom_, intelligence_;
  private boolean isSurvival_;
  private Cutscene cutscene_;
  private Menu menu_;
  private FireMinigame fire_;
  private PaintMinigame paint_;
  private HuntMinigame hunt_;
  private FishMinigame fish_;
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
    size_ = size;

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
    isSurvival_ = false;
    cutscene_ = null;
    menu_ = null;
    fire_ = null;
    paint_ = null;
    hunt_ = null;
    fish_ = null;
    screen_ = null;

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
  }  // public Multiplexer(Dimension)

  //Plays cutscene and starts the main game infinite loop
  //Must be called externally so that the GUI doesn't hang
  public void startCaveSim()
  {
    cutscene(size_);  //Play openning cutscene
    gameMenu(size_, isSurvival_);  //Initialize menu (done in loop as well)
    while(true)
    {
      if(isSurvival_)
        checkVictoryConditions();
      getMenuSelection();
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
          saveGame();
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
          loadGame();
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
          saveGame();
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
                messageScreen(size_, 4);  //Rules message
                gameMenu(size_, isSurvival_);
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

  //Polls menu_ for a selection and runs the corresponding minigame
  private void getMenuSelection()
  {
    switch(menu_.getSelection())
    {
      case 0:  // No selection, menu waits
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
        fishMinigame(size_);
        if(isSurvival_)
        {
          ++boredom_;
          food_ += fish_.getScore();
          if(fish_.getScore() > 3)
            ++intelligence_;
        }
        gameMenu(size_, isSurvival_);
        break;
      }
      case 2:  // Hunt minigame
      {
        huntMinigame(size_); 
        if(isSurvival_)
        {
          ++boredom_;
          food_ += hunt_.getScore();
          if(hunt_.getScore() > 4)
            ++intelligence_;
        }
        gameMenu(size_, isSurvival_);
        break;
      }
      case 3:  // Fire minigame
      {
        fireMinigame(size_); 
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
        gameMenu(size_, isSurvival_);
        break;
      }
      case 4:  // Paint minigame
      {
        paintMinigame(size_);
        if(isSurvival_)
        {
          --boredom_;
          ++hunger_;
          intelligence_ += paint_.getScore();
        } 
        gameMenu(size_, isSurvival_);
        break;
      }
      case 5:  // Cups minigame
      {
        break;
      }
      case 6:
      {
        isSurvival_ = true;
        gameMenu(size_, isSurvival_);
      }
    } // switch
  }  // private void getMenuSelection()

  //For survival mode, checks whether the game is won or lost
  //and displays corresponding message
  private void checkVictoryConditions()
  {
    writeStats();  //Adds stats label
    if(hunger_ >= 10)
    {
      messageScreen(size_, 0);  //Died of starvation
      resetStats();
      isSurvival_ = false;
      gameMenu(size_, isSurvival_);
    }
    else if(boredom_ >= 10)  //Died of boredom
    {
      messageScreen(size_, 1);
      resetStats();
      isSurvival_ = false;
      gameMenu(size_, isSurvival_);
    }
    else if(intelligence_ >= 5)  //Win
    {
      messageScreen(size_, 2);
      resetStats();
      isSurvival_ = false;
      gameMenu(size_, isSurvival_);
    }
  }  // private void CheckVictoryConditions()

  private void writeStats()
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
  }

  private void cutscene(Dimension size)
  { 
    cutscene_ = new Cutscene(size);
    getContentPane().removeAll();
    getContentPane().add(cutscene_, BorderLayout.CENTER);
    pack();
    revalidate();
    repaint();
    cutscene_.togglePlay();
    while(!cutscene_.Done())
    {
      try 
      {  
        Thread.sleep(2000);
      }
      catch(InterruptedException ex)
      {
        Thread.currentThread().interrupt();
      }
    }
  }
 
  private void gameMenu(Dimension size, boolean isSurvival)
  {
    menu_ = new Menu(size, isSurvival);
    getContentPane().removeAll();
    getContentPane().add(menu_, BorderLayout.CENTER);
    pack();
    revalidate();
    repaint();
    menu_.togglePlay();
  }

  private void fireMinigame(Dimension size)
  {
    fire_ = new FireMinigame(size);
    getContentPane().removeAll();
    getContentPane().add(fire_, BorderLayout.CENTER);
    pack();
    fire_.setFocusable(true); 
    fire_.requestFocus();
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
  }

  private void paintMinigame(Dimension size)
  {
    paint_ = new PaintMinigame(size);
    getContentPane().removeAll();
    getContentPane().add(paint_, BorderLayout.CENTER);
    pack();
    revalidate();
    repaint();
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
  }

  private void huntMinigame(Dimension size)
  {
    hunt_ = new HuntMinigame(size);
    getContentPane().removeAll();
    getContentPane().add(hunt_, BorderLayout.CENTER);
    pack();
    revalidate();
    repaint();
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
  }

  private void fishMinigame(Dimension size)
  {
    fish_ = new FishMinigame(size);
    getContentPane().removeAll();
    getContentPane().add(fish_, BorderLayout.CENTER);
    pack();
    fish_.setFocusable(true); 
    fish_.requestFocus();
    revalidate();
    repaint();
    fish_.togglePlay();
    while(!fish_.Done())
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
  }

  private void messageScreen(Dimension size, int index)
  {
    screen_ = new MessageScreen(size, index);
    getContentPane().removeAll();
    getContentPane().add(screen_, BorderLayout.CENTER);
    pack();
    revalidate();
    repaint();
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

  private void saveGame()
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
 
  private void loadGame()
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

