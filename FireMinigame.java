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

  private BufferedImage img_;
  private Graphics2D g2d_;
  private Timer timer_;
  private boolean isPlay_;

  private double ambient_;
  private double temp_;
  private int position_;
  private double velocity_;
  private boolean isLeft_;

  public FireMinigame(Dimension size)
  {
    setMinimumSize(size);
    setMaximumSize(size);
    setPreferredSize(size);

    isPlay_ = false;
    
    Random rng = new Random();
    ambient_ = temp_ = rng.nextDouble()*110 - 10;
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
    velocity_ = 0;
    isLeft_ = false;
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
    g2d_.drawImage(fire[0],
                   0, img_.getHeight()/2, (img_.getWidth() - 1), (img_.getHeight() - 1),
                   0, 0, (fire[0].getWidth() - 1), (fire[0].getHeight() - 1),
                   Color.BLACK, null); 

    timer_ = new Timer(MILLISECONDS_BETWEEN_FRAMES, new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          timer_.stop();
          g2d_.drawImage(fire[1],
                         position_, 0, (img_.getWidth() - 1), (int)(img_.getHeight()/1.06),
                         0, 0, fire[1].getWidth(), fire[1].getHeight(),
                         Color.BLACK, null); 
          if(temp_ > 800)
          {
            g2d_.drawImage(fire[2],
                           position_, 0, (img_.getWidth() - 1), (int)(img_.getHeight()/1.06),
                           0, 0, fire[1].getWidth(), fire[1].getHeight(),
                           Color.BLACK, null); 
            g2d_.setColor(Color.BLACK);
            g2d_.drawString("Press 'F' to add kindling!", 50, 250);
          }
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
          g2d_.drawString("" + Math.round(temp_*100)/100 + "\u00b0F", 350, 150);
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
          temp_ += -.01*(temp_ - ambient_);  
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
          System.out.println(position_);
          if(e.getKeyCode() == KeyEvent.VK_LEFT)
          {
            if(isLeft_)
              velocity_ += .1;
            position_ = (int)(position_ - 2*velocity_);
            if(position_ < -125)
            {
              position_ = -125;
              velocity_ = 1;
            }
            isLeft_ = true;
            temp_ += velocity_/2;
          }
          else if(e.getKeyCode() == KeyEvent.VK_RIGHT)
          {
            if(!isLeft_)
              velocity_ += .1;
            position_ = (int)(position_ + 2*velocity_);
            if(position_ > 350)
            {
              position_ = 350;
              velocity_ = 1;
            }
            isLeft_ = false;
            temp_ += velocity_/2;
          }
          else if(e.getKeyCode() == KeyEvent.VK_F)
          {
            if(temp_ > 800)
            {
              if(rng.nextDouble() > .5)
                System.out.println("You smothered the fire.");
              timer_.stop();
              tempTimer.stop();
              g2d_.drawImage(fire[3],
                             0, 0, (img_.getWidth() - 1), (img_.getHeight() - 1),
                             0, 0, fire[3].getWidth(), fire[3].getHeight(),
                             Color.BLACK, null); 
              repaint();
            }
          }
        }
        public void keyReleased(KeyEvent e)
        {
          //if(position_ < 
          velocity_ = 1;
        }
      }
    );

/*
    addMouseListener
    (
      new MouseAdapter()
      {
        public void mouseReleased(MouseEvent e)  // A better choice than mouseClicked
        {
          final Point p = e.getPoint();
          if(p.x < 0 || p.x > img_.getWidth() || p.y < 0 || p.y > img_.getHeight())
            return;

          new Thread
          (
            new Runnable()
            {
              public void run()
              {
                if(e.getButton() == MouseEvent.BUTTON1)  // LMB
                {
                } 
              }
            }
          ).start();
        }
      }
    );

*/
  }  // public FireMinigame(Dimension)

  public boolean togglePlay()
  {
    isPlay_ = !isPlay_;
    if(isPlay_)
      timer_.restart();
    else
      timer_.stop();
    return isPlay_;
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
                         0, 0, (img_.getWidth() - 1), (img_.getHeight() - 1),
                         0, 0, (img_.getWidth() - 1), (img_.getHeight() - 1),
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

}  // class ClickPanel

