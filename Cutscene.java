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
  private static final int MILLISECONDS_BETWEEN_FRAMES = 16;  // i.e. ~60fps

  private BufferedImage img_;
  private Graphics2D g2d_;
  private Timer timer_;
  private String[] strArr_;

  public Cutscene(Dimension size) 
  {
    setMinimumSize(size);
    setMaximumSize(size);
    setPreferredSize(size);

    img_ = new BufferedImage((int)size.getHeight(), (int)size.getWidth(), 
                             BufferedImage.TYPE_INT_ARGB);
    g2d_ = (Graphics2D)img_.createGraphics();
    strArr_ = new String[]{"You are a caveman"};

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
          timer_.restart();
        }
      }
    );  // new Timer
  }  // public FractalSelectPanel(int, int)

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

  private void dispText()
  {
    g2d_.setColor(Color.RED);
    g2d_.setFont(new Font( "SansSerif", Font.BOLD, 12 ));
    g2d_.drawString(strArr_[0], 100, 100);
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
