/*----------------------------------------- 
*
*   MessageScreen.java
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


public class MessageScreen extends JPanel
{
  private static final int MILLISECONDS_BETWEEN_FRAMES = 1000;
  private static final int TIME = 20;

  //Main animation variables
  private BufferedImage img_;  //Display img
  private Graphics2D g2d_;  
  private boolean isDone_; 

  public MessageScreen(Dimension size, int index) 
  {
    //Set frame attributes & cursor
    setMinimumSize(size);
    setMaximumSize(size);
    setPreferredSize(size);

    //Initialize data members & visual components
    img_ = new BufferedImage((int)size.getWidth(), (int)size.getHeight(), 
                             BufferedImage.TYPE_INT_ARGB);
    g2d_ = (Graphics2D)img_.createGraphics();
    g2d_.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
                          RenderingHints.VALUE_ANTIALIAS_ON);

    //Images array
    BufferedImage msg[] = new BufferedImage[3];
    try
    {
      msg[0] = ImageIO.read(new File("img/hunger.png"));
      msg[1] = ImageIO.read(new File("img/boredom.png"));
      msg[2] = ImageIO.read(new File("img/victory.png"));
    }
    catch(IOException e)
    {
      System.out.println("ERROR: Image(s) missing from img/ directory");
      System.exit(-1);
    }
    g2d_.drawImage(msg[index],
                   0, 0, img_.getWidth(), img_.getHeight(),
                   0, 0, msg[index].getWidth(), msg[index].getHeight(),
                   Color.BLACK, null);
    setImage();

    addMouseListener
    (
      new MouseAdapter()
      {
        public void mousePressed(MouseEvent e)
        {
          isDone_ = true;
        }
      }
    );

  }  // public MessageScreen(Dimension, int)

  //Compares the user drawn part to the "missing" part of
  //the image. Returns the ratio drawn correctly

  public boolean Done()
  {
    return isDone_;
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

}  // class HuntMinigame

