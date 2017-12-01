/*----------------------------------------- 
*
*   FireMinigame.java
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

public class FireMinigame extends JPanel
{
  private static final int MILLISECONDS_BETWEEN_FRAMES = 16;  // i.e. ~ 60 fps

  //Animation objects
  private BufferedImage img_;
  private Graphics2D g2d_;
  private Timer timer_;
  private Random rng_;
  private boolean isPlay_, isDone_;

  //Game objects
  private double ambient_, temp_;
  private double acceleration_, velocity_;
  private int position_;
  private boolean isKindling_;

  public FireMinigame(Dimension size)
  {
    setFocusable(true);
    setMinimumSize(size);
    setMaximumSize(size);
    setPreferredSize(size);

    isPlay_ = isDone_ = false;
    isKindling_ = false;
    rng_ = new Random();
    ambient_ = temp_ = rng_.nextDouble()*110 - 10;
    String weatherStr;
    Color weatherClr;
    if(ambient_ > 90)
    {
      weatherStr = "Blazing hot!";
      weatherClr = Color.RED;
    }
    else if(ambient_ > 76)
    {
      weatherStr = "Hot";
      weatherClr = Color.ORANGE;
    }
    else if(ambient_ > 66)
    {
      weatherStr = "Cool";
      weatherClr = Color.GREEN;
    }
    else if(ambient_ > 50)
    {
      weatherStr = "Chilly";
      weatherClr = new Color(50, 50, 100);
    }
    else if(ambient_ > 32)
    {
      weatherStr = "Cold";
      weatherClr = new Color(50, 50, 150);
    }
    else if(ambient_ > 0)
    {
      weatherStr = "Freezing!";
      weatherClr = new Color(50, 50, 200);
    }
    else
    {
      weatherStr = "Below zero!";
      weatherClr = Color.BLUE;
    }
    position_ = 0;
    velocity_ = acceleration_ = 0;
    img_ = new BufferedImage((int)size.getWidth(), (int)size.getHeight(),
                             BufferedImage.TYPE_INT_ARGB);
    g2d_ = (Graphics2D)img_.createGraphics();

    //Images array
    BufferedImage fire[] = new BufferedImage[4];
    try
    {
      fire[0] = ImageIO.read(new File("img/fire0.png"));
      fire[1] = ImageIO.read(new File("img/fire1.png"));
      fire[2] = ImageIO.read(new File("img/fire2.png"));
      fire[3] = ImageIO.read(new File("img/fire3.png"));
    }
    catch(IOException e)
    {
      System.out.println("ERROR: Image(s) missing from img/ directory");
      System.exit(-1);
    }

    //Main animation timer
    timer_ = new Timer(MILLISECONDS_BETWEEN_FRAMES, new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          timer_.stop();
          changeBackground(Color.WHITE);  // Remove artifacts 

          //Calculate position, etc., then draw
          velocity_ += acceleration_;
          temp_ += .2*Math.abs(velocity_);
          position_ += (int)velocity_;
          if(velocity_ != 0 || acceleration_ != 0)
          {
            velocity_ *= .999;
            acceleration_ *= .85;
          }
          if(position_ > 500)
          {
            position_ = 500;
            velocity_ = -1*velocity_;
            acceleration_ = 0;
          }
          else if(position_ < -125)
          {
            position_ = -125;
            velocity_ = -1*velocity_;
            acceleration_ = 0;
          }
          g2d_.drawImage(fire[0],
                         0, img_.getHeight()/2, img_.getWidth(), img_.getHeight(),
                         0, 0, fire[0].getWidth(), fire[0].getHeight(),
                         Color.BLACK, null); 
          g2d_.drawImage(fire[1],
                         position_, 0, img_.getWidth(), (int)(img_.getHeight()/1.065),
                         0, 0, fire[1].getWidth(), fire[1].getHeight(),
                         Color.BLACK, null); 

          //Text display, etc.
          if(temp_ > 800)
          {
            isKindling_ = true;
            g2d_.drawImage(fire[2],
                           position_, 0, img_.getWidth(), (int)(img_.getHeight()/1.06),
                           0, 0, fire[1].getWidth(), fire[1].getHeight(),
                           Color.BLACK, null); 
            g2d_.setColor(Color.BLACK);
          }
          else if(temp_ < 700)
            isKindling_ = false;
          if(isKindling_)
            g2d_.drawString("Press 'F' to add kindling!", 50, 250);
          g2d_.setColor(Color.BLACK);
          g2d_.setFont(new Font(Font.SERIF, Font.BOLD, 50));
          g2d_.drawString("Fire Plow: ", 50, 150); 
          Color fireClr;
          if(temp_ > 700)
            fireClr = Color.RED;
          else if(temp_ > 500)
            fireClr = Color.ORANGE;
          else if(temp_ > 300)
            fireClr = Color.YELLOW;
          else if(temp_ > 150)
            fireClr = new Color(50, 50, 255);
          else
            fireClr = Color.BLUE;
          g2d_.setColor(fireClr);
          g2d_.drawString(Math.round(temp_) + "\u00b0F", 350, 150);
          g2d_.setColor(Color.BLACK);
          g2d_.drawString("Today's Weather:", 50, 100);
          g2d_.setColor(weatherClr);
          g2d_.drawString(weatherStr, 545, 100);
          repaint();
          timer_.restart();
        }
      }
    );  // new Timer

    //Approximation of Newton's Law of Cooling
    Timer tempTimer = new Timer(250, new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          temp_ += -.07*(temp_ - ambient_);  
        }
      }
    );
    tempTimer.start();

    addKeyListener
    (
      new KeyAdapter()
      {
        public void keyPressed(KeyEvent e)
        {
          if(e.getKeyCode() == KeyEvent.VK_LEFT)
          {
            acceleration_ -= .1;
          }
          else if(e.getKeyCode() == KeyEvent.VK_RIGHT)
          {
            acceleration_ += .1;
          }
          else if(e.getKeyCode() == KeyEvent.VK_F)
          {
            isDone_ = true;
            timer_.stop();
            tempTimer.stop();
            if(isKindling_)
            {
              //TODO
              if(rng_.nextDouble() > .9)
                System.out.println("You accidentally smothered the fire!");
              g2d_.drawImage(fire[3],
                             0, 0, (img_.getWidth() - 1), (img_.getHeight() - 1),
                             0, 0, fire[3].getWidth(), fire[3].getHeight(),
                             Color.BLACK, null); 
              repaint();
            }
            else
            {
              //TODO
              System.out.println("You added kindling too early and smothered the fire!");
            }
          }
        }
      }
    );

  }  // public FireMinigame(Dimension)

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

  private void changeBackground(Color color)
  {
    g2d_.setColor(color);
    g2d_.fillRect(0, 0, img_.getWidth(), img_.getHeight());
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

}  // class FireMinigame 

