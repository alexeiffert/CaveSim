/*----------------------------------------- 
*
*   Cutscene.java
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
  private int index_;
  private boolean isDraw_, isPlay_, isDone_;

  public Menu(Dimension size) 
  {
    setMinimumSize(size);
    setMaximumSize(size);
    setPreferredSize(size);

    img_ = new BufferedImage((int)size.getWidth(), (int)size.getHeight(), 
                             BufferedImage.TYPE_INT_ARGB);
    g2d_ = (Graphics2D)img_.createGraphics();
    colorArr_ = getGreys(100);
    index_ = -1;
    isDraw_ = isPlay_ = isDone_ = false;
    addMouseListener
    (
      new MouseAdapter()
      {
        public void mouseReleased(MouseEvent e)  // A better choice than mouseClicked
        {
          Point p = e.getPoint();
          if(p.y > 345 && p.y < 435 && p.x > 555 && p.x < 1400)
          {
            //Survival Mode
            System.out.println("Survival");
            isDone_ = true;
            
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
          else if(p.y > 550 && p.y < 630 && p.x > 555 && p.x < 1425)
            //Sandbox Mode
            System.out.println("Sandbox");
          else if(p.y > 750 && p.y < 830 && p.x > 485 && p.x < 815)
          {
            about();
            setImage();
          }
        }
      }
    );

    timer_ = new Timer(MILLISECONDS_BETWEEN_FRAMES, new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          timer_.stop();
          g2d_.setPaintMode();
          changeBackground(colorArr_[++index_]);
          g2d_.setFont(new Font(Font.SERIF, Font.BOLD, 50));
          g2d_.setXORMode(colorArr_[99 - index_]);
          g2d_.drawString("CaveSim", 50, 100);
          g2d_.setPaintMode();
          setImage();
          timer_.restart();
          if(index_ == colorArr_.length - 1)
          {
            timer_.stop();
            drawMenu();
            setImage();
          }
        }
      }
    );  // new Timer
  }  // public Menu(Dimension)

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

  private boolean toggleDraw()
  {
    isDraw_ = !isDraw_;
    return isDraw_;
  }

  private void about()
  {
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

  private void dispText()
  {
    g2d_.setXORMode(Color.WHITE);
    g2d_.setFont(new Font(Font.SERIF, Font.BOLD, 100));
    //g2d_.drawString(strArr_[index_], x_, 100);
    g2d_.setPaintMode();
  }

  private void errText()
  {
    g2d_.setXORMode(Color.WHITE);
    g2d_.setFont(new Font(Font.SERIF, Font.BOLD, 100));
    //g2d_.drawString(strArr_[index_++], x_, 100);
    g2d_.setPaintMode();
  }

  public BufferedImage getImage()
  {
    return img_;
  }

  private void drawMenu()
  {
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
  }

  private void close()
  {
    img_ = null;
    timer_.stop();
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
