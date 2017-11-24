/*----------------------------------------- 
*
*   FireMiniGame.java
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

  private int position_;

  public FireMinigame(Dimension size)
  {
    setMinimumSize(size);
    setMaximumSize(size);
    setPreferredSize(size);
    
    position_ = 0;
    img_ = new BufferedImage((int)size.getWidth(), (int)size.getHeight(),
                             BufferedImage.TYPE_INT_ARGB);
    g2d_ = (Graphics2D)img_.createGraphics();
    try
    {
      BufferedImage fire0 = ImageIO.read(new File("fire0.png"));
      g2d_.drawImage(fire0,
                     0, img_.getHeight()/2, (img_.getWidth() - 1), (img_.getHeight() - 1),
                     0, 0, (fire0.getWidth() - 1), (fire0.getHeight() - 1),
                     Color.BLACK, null); 
      BufferedImage fire1 = ImageIO.read(new File("fire1.png"));
      g2d_.drawImage(fire1,
                     0, 0, (img_.getWidth() - 1), (int)((img_.getHeight() - 1)/1.06),
                     0, 0, (fire1.getWidth() - 1), (fire1.getHeight() - 1),
                     Color.BLACK, null); 
      repaint();
    }
    catch(IOException exc)
    {
      System.out.println("Menu image missing");
    }

    addKeyListener
    (
      new KeyAdapter()
      {
        public void keyReleased(KeyEvent e)
        {
          if(e.getKeyCode() == KeyEvent.VK_LEFT)
          {
            System.out.println(--position_);
            if(position_ < -20)
              position_ = -20;
          }
          else if(e.getKeyCode() == KeyEvent.VK_RIGHT)
          {
            System.out.println(++position_);
            if(position_ > 45)
              position_ = 45;
          }
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
    timer_ = new Timer(MILLISECONDS_BETWEEN_FRAMES, new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          timer_.stop();
          //TODO
    try
    {
      BufferedImage fire1 = ImageIO.read(new File("fire1.png"));
      g2d_.drawImage(fire1,
                     position_*10, 0, (img_.getWidth() - 1), (int)((img_.getHeight() - 1)/1.06),
                     0, 0, (fire1.getWidth() - 1), (fire1.getHeight() - 1),
                     Color.BLACK, null); 
      repaint();
    }
    catch(IOException exc)
    {
      System.out.println("Menu image missing");
    }
          timer_.restart();
        }
      }
    );  // new Timer
    timer_.start();
  }  // public FireMinigame(Dimension)

/*
  public boolean togglePlay()
  {
    isPlay_ = !isPlay_;
    if(isPlay_)
      timer_.restart();
    else
      timer_.stop();
    return isPlay_;
  }
*/

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
                         0, 0, 799, 799,
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
