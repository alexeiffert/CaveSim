/*----------------------------------------- 
*
*   Menu.java
*   Alex Eiffert
*   CAP3027  
*   2 November, 2017 
*   "CaveSim" v1.0
*
------------------------------------------*/

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.awt.geom.*;
import java.io.*;
import javax.imageio.*;
import javax.swing.*;

import java.util.Random;
import java.util.Scanner;

public class Menu extends JPanel
{
  private static final int MILLISECONDS_BETWEEN_FRAMES = 16;  // i.e. ~60fps

  private BufferedImage img_;
  private Graphics2D g2d_;
  private Timer timer_;
  private Color[] colorArr_;
  private String[] strArr_;
  private int index_, selection_;
  private boolean isPlay_, isDone_, isSurvival_, isMainMenu_, isSandboxMenu_, isAbout_;

  public Menu(Dimension size) 
  {
    //Set attributes
    setMinimumSize(size);
    setMaximumSize(size);
    setPreferredSize(size);
    setCursor(new Cursor(Cursor.WAIT_CURSOR));

    //Initialize data members, mouse, and timer
    strArr_ = new String[]{"Fetching bones", "Searching for suitable caves", "Reticulating splines",
                            "Evolving", "Spawning fish", "Mixing cave paint", "Gathering kindling",
                            "Rearranging rock furniture", "Teaching AI", "Inventing the wheel"};
    colorArr_ = getGreys(100);
    index_ = -1;
    selection_ = 0;
    isPlay_ = isDone_ = isSurvival_ = isMainMenu_ = isSandboxMenu_ = isAbout_ = false;
    img_ = new BufferedImage((int)size.getWidth(), (int)size.getHeight(), 
                             BufferedImage.TYPE_INT_ARGB);
    g2d_ = (Graphics2D)img_.createGraphics();
    g2d_.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    addMouseListener
    (
      new MouseAdapter()
      {
        public void mouseReleased(MouseEvent e)  // Allows for the more intuitive drag-if-misclicked
        {
          if(isAbout_)
            drawMenu();
          else if(isMainMenu_)
          {
            Point p = e.getPoint();
            if(p.y > 400 && p.y < 450 && p.x > 630 && p.x < 970)  //Survival option
            {
              survival();
            } // if(survival mode)
            else if(p.y > 510 && p.y < 565 && p.x > 635 && p.x < 970)  //Sandbox option
            {
              sandbox();
            }
            else if(p.y > 635 && p.y < 685 && p.x > 725 && p.x < 880)  //About option
            {
              about();
            }
          }  // else if(isMainMenu_) 
          else if(isSandboxMenu_)
          {
            Point p = e.getPoint();
            if(p.y > 400 && p.y < 450 && p.x > 580 && p.x < 1025)  // Fish Minigame
            {
              broadcast(1);
            } 
            else if(p.y > 505 && p.y < 550 && p.x > 640 && p.x < 975)  // Fire Minigame
            {
              broadcast(3);
            }
            else if(p.y > 595 && p.y < 650 && p.x > 470 && p.x < 1120)  // Paint Minigame
            {
              broadcast(4);
            }
            else if(p.y > 755 && p.y < 810 && p.x > 700 && p.x < 910)  //Back
            {
              drawMenu();
            }
          }
        }
      }
    );  // addMouseListener()

    //For opening menu animation
    timer_ = new Timer(MILLISECONDS_BETWEEN_FRAMES, new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          timer_.stop();
          g2d_.setPaintMode();
          changeBackground(colorArr_[++index_]);
          g2d_.setFont(new Font(Font.SERIF, Font.BOLD, 50));
          g2d_.setXORMode(colorArr_[99 - index_]);
          g2d_.drawString(strArr_[index_/10], 50, 100);
          g2d_.setPaintMode();
          setImage();
          timer_.restart();
          if(index_ == colorArr_.length - 1)
          {
            timer_.stop();
            setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            drawMenu();
          }
        }
      }
    );  // new Timer
  }  // public Menu(Dimension)

  public boolean isSurvival()
  {
    return isSurvival_;
  }

  public boolean Done()
  {
    return isDone_;
  }

  public boolean togglePlay()
  {
    isPlay_ = !isPlay_;
    if(isPlay_)
      timer_.restart();
    else
      timer_.stop();
    return isPlay_;
  }

  public int getSelection()
  {
    int temp = selection_;
    selection_ = 0;
    return temp;
  }

  private void broadcast(int n)
  {
    selection_ = n;
  }

  private void drawMenu()
  {
    isMainMenu_ = true;
    isSandboxMenu_ = isAbout_ = false;
    try
    {
      BufferedImage mainMenu = ImageIO.read(new File("img/main_menu.png"));
      g2d_.setPaintMode();
      g2d_.drawImage(mainMenu,
                     0, 0, img_.getWidth(), img_.getHeight(),
                     0, 0, mainMenu.getWidth(), mainMenu.getHeight(),
                     Color.BLACK, null);
      repaint();
    }
    catch(IOException exc)
    {
      System.out.println("Menu image missing");
    }
/* Old menu was Java text
    changeBackground(Color.BLACK);
    g2d_.setPaintMode();
    g2d_.setColor(new Color(200, 200, 200));
    g2d_.setFont(new Font(Font.SERIF, Font.BOLD, 100));
    g2d_.drawString("CaveSim v1.0", img_.getWidth()/4, img_.getHeight()/4);
    g2d_.drawString("- Survival Mode", 550, img_.getHeight()/4 + 200);
    g2d_.drawString("- Sandbox Mode", 550, img_.getHeight()/4 + 400);
    g2d_.drawString("- About", img_.getWidth()/4, img_.getHeight()/4 + 600);
    try
    {
      BufferedImage neanderthal = ImageIO.read(new File("img/neanderthal.png"));
      g2d_.drawImage(neanderthal,
                     50, 350, 1200, (img_.getHeight() - 1),
                     0, 0, (img_.getWidth() - 1), (img_.getHeight() - 1),
                     Color.BLACK, null);
      repaint();
    }
    catch(IOException exc)
    {
      System.out.println("Menu image missing");
    }
*/
  }

  private void survival() 
  {
    isDone_ = true;
    isSurvival_ = true;
    BufferedImage homeImg = null;
    try
    {
      homeImg = ImageIO.read(new File("img/home.png"));
    }
    catch(Exception ex)
    {
      System.out.println("ERROR: Image(s) missing from img/ directory");
      System.exit(-1);
    }
    g2d_.drawImage(homeImg,
                   0, 0, img_.getWidth(), img_.getHeight(),
                   0, 0, homeImg.getWidth(), homeImg.getHeight(),
                   Color.BLACK, null);
    setImage();
  }

  //Updates img_ to the sanbox menu
  private void sandbox()
  {
    isSandboxMenu_ = true;
    isMainMenu_ = isAbout_ = false;
    try
    {
      BufferedImage sandbox = ImageIO.read(new File("img/sandbox.png"));
      g2d_.setPaintMode();
      g2d_.drawImage(sandbox,
                     0, 0, img_.getWidth(), img_.getHeight(),
                     0, 0, sandbox.getWidth(), sandbox.getHeight(),
                     Color.BLACK, null);
      repaint();
    }
    catch(IOException exc)
    {
      System.out.println("Menu image missing");
    }
  }

  //Updates img_ to the about page
  private void about()
  {
    isAbout_ = true;
    isMainMenu_ = isSandboxMenu_ = false;
    try
    {
      BufferedImage mainMenu = ImageIO.read(new File("img/about.png"));
      g2d_.setPaintMode();
      g2d_.drawImage(mainMenu,
                     0, 0, img_.getWidth(), img_.getHeight(),
                     0, 0, mainMenu.getWidth(), mainMenu.getHeight(),
                     Color.BLACK, null);
      repaint();
    }
    catch(IOException exc)
    {
      System.out.println("Menu image missing");
    }
    /*
    changeBackground(Color.BLACK);
    g2d_.setColor(Color.WHITE);
    g2d_.setFont(new Font(Font.SERIF, Font.BOLD, 100));
    g2d_.drawString("About CaveSim v1.0:", 100, 100);
    g2d_.setFont(new Font(Font.SERIF, Font.PLAIN, 25));
    g2d_.drawString("CaveSim is the world's premier, Java Swing-based caveman simulation game.",
                    img_.getWidth()/6, 250);
    g2d_.drawString("In \"Survival\" mode, YOU are the caveman. Forage for food, discover" + 
                    " fire, and more.", img_.getWidth()/6, 350);
    g2d_.drawString("In \"Sandbox Mode\", practice each of the minigames individually and" + 
                    " hone your skills.", img_.getWidth()/6, 400);
    */
  }

  private void changeBackground(Color color)
  {
    g2d_.setColor(color);
    g2d_.fillRect(0, 0, img_.getWidth(), img_.getHeight());
  }

  private Color[] getGreys(int n)
  {
    double B = 0;
    double dB = (double)230/(double)n;  // Light grey
    Color color[] = new Color[n];
    for(int i = 0; i < n; ++i)
    {
      color[i] = new Color((int)B, (int)B, (int)B);
      B += dB;
    }
    return color;
  }

  public BufferedImage getImage()
  {
    return img_;
  }

  //setImage() is always sent to EDT
  public void setImage()
  {
    SwingUtilities.invokeLater
    (
      new Runnable()
      {
        public void run()
        {
          g2d_.drawImage(img_,
                         0, 0, img_.getWidth(), img_.getHeight(),
                         0, 0, img_.getWidth(), img_.getHeight(),
                         Color.BLACK, null);
          repaint();
        }
      }
    );
  }
  
  public void paintComponent(Graphics g)  // Class override
  {
    super.paintComponent(g);
    g.drawImage(img_, 0, 0, null);
  }

}  // class Menu

