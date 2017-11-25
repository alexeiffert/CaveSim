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

public class Cutscene extends JPanel
{
  private static final int MILLISECONDS_BETWEEN_FRAMES = 250;  

  private BufferedImage img_;
  private Graphics2D g2d_;
  private Timer timer_;
  private String[] strArr_;
  private int index_;
  private int x_;
  private boolean isDraw_, isPlay_, isDone_;

  public Cutscene(Dimension size) 
  {
    setMinimumSize(size);
    setMaximumSize(size);
    setPreferredSize(size);

    img_ = new BufferedImage((int)size.getWidth(), (int)size.getHeight(), 
                             BufferedImage.TYPE_INT_ARGB);
    g2d_ = (Graphics2D)img_.createGraphics();
    changeBackground(Color.BLACK);
    strArr_ = new String[]{"\"The caveman is a stock character", "\"The caveman is a stock character", 
                           "\"based upon widespread but", "\"based upon widespread but",
                           "\"ANACHRONISTIC", "\"ANACHRONISTIC",  
                           "\"and", "\"and", 
                           "\"CONFLATED", "\"CONFLATED", 
                           "\"concepts", "\"concepts", 
                           "\"of the way in which", "\"of the way in which", 
                           "\"Neanderthals,", "\"Neanderthals,",
                           "\"early modern humans,", "\"early modern humans,", 
                           "\"and archaic humans", "\"and archaic humans",
                           "\"may have looked and behaved...", "\"may have looked and behaved...",
                           "But he doesn't care about that", "But he doesn't care about that", 
                             "But he doesn't care about that", 
                           "Now, YOU are the caveman", "Now, YOU are the caveman", 
                           "m", "im", "Sim", "eSim", "veSim", "aveSim", "CaveSim", " CaveSim", "  CaveSim"};
    index_ = x_ = 0;
    isDraw_ = isPlay_ = isDone_ = false;
    addMouseListener
    (
      new MouseAdapter()
      {
        public void mouseReleased(MouseEvent e)  // A better choice than mouseClicked
        {
        }
      }
    );

    timer_ = new Timer(MILLISECONDS_BETWEEN_FRAMES, new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          timer_.stop();
          if(toggleDraw())
          {
            dispText();
            setImage();
          }
          else
          {
            errText();
          }
          timer_.restart();
          if(index_ == strArr_.length)
          {
            timer_.stop();
            isDone_ = true;
          }
        }
      }
    );  // new Timer
  }  // public FractalSelectPanel(int, int)

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

  private void changeBackground(Color color)
  {
    g2d_.setColor(color);
    g2d_.fillRect(0, 0, img_.getWidth(), img_.getHeight());
  }

  private void dispText()
  {
    g2d_.setXORMode(Color.WHITE);
    g2d_.setFont(new Font(Font.SERIF, Font.BOLD, 75 ));
    g2d_.drawString(strArr_[index_], x_, img_.getHeight()/2);
    g2d_.setPaintMode();
  }

  private void errText()
  {
    g2d_.setXORMode(Color.WHITE);
    g2d_.setFont(new Font(Font.SERIF, Font.BOLD, 75));
    g2d_.drawString(strArr_[index_++], x_, img_.getHeight()/2);
    g2d_.setPaintMode();
  }

  public BufferedImage getImage()
  {
    return img_;
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
