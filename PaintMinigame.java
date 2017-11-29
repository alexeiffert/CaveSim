/*----------------------------------------- 
*
*   PaintingMinigame.java
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

public class PaintMinigame extends JPanel
{
  private static final int MILLISECONDS_BETWEEN_FRAMES = 16;

  private BufferedImage img_;
  private Graphics2D g2d_;
  private Timer timer_;
  private Point prev_;
  private boolean isPlay_, isDraw_;

  public PaintMinigame(Dimension size)  // width = height
  {
    //Set frame attributes & cursor
    setMinimumSize(size);
    setMaximumSize(size);
    setPreferredSize(size);
    setCursor(new Cursor(Cursor.HAND_CURSOR));

    //Initialize visual components
    img_ = new BufferedImage((int)size.getWidth(), (int)size.getHeight(), BufferedImage.TYPE_INT_ARGB);
    g2d_ = (Graphics2D)img_.createGraphics();
    g2d_.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g2d_.setStroke(new BasicStroke(10.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
    BufferedImage caveImg = null;
    try
    {
      caveImg = ImageIO.read(new File("img/cave.jpg"));
    }
    catch(IOException e)
    {
      System.out.println("ERROR: Image(s) missing from img/ directory");
      System.exit(-1);
    }
    g2d_.drawImage(caveImg,
                   0, 0, img_.getWidth(), img_.getHeight(),
                   0, 0, caveImg.getWidth(), caveImg.getHeight(),
                   Color.BLACK, null);
    prev_ = null;
    isPlay_ = isDraw_ = false;

    addMouseListener
    (
      new MouseAdapter()
      {
        public void mousePressed(MouseEvent e)
        {
          prev_ = e.getPoint();
          if(e.getButton() == MouseEvent.BUTTON1)  // LMB
            isDraw_ = true;
        }
       
        public void mouseReleased(MouseEvent e)
        {
          isDraw_ = false;
        }
      }
    );

    addMouseMotionListener
    (
      new MouseMotionAdapter()
      {
        public void mouseDragged(MouseEvent e)
        {
          Point p = e.getPoint();
          if(p.x < 0 || p.x > img_.getWidth() || p.y < 0 || p.y > img_.getHeight())
            return;
          if(isDraw_)  // LMB from MouseClicked
          {
            g2d_.setColor(Color.RED);
            g2d_.draw(new Line2D.Double(prev_.x, prev_.y, p.x, p.y));
            prev_ = p;
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
          //TODO
          timer_.restart();
        }
      }
    );  // new Timer
  }  // public PaintMinigame(Dimension)

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

