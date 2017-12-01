/*----------------------------------------- 
*
*   HuntMinigame.java
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

public class HuntMinigame extends JPanel
{
  private static final int MILLISECONDS_BETWEEN_FRAMES = 2000;
  private static final int TIME = 20;

  //Main animation variables
  private BufferedImage img_;  //Display img
  private Graphics2D g2d_, g2d2_;  //g2d2_ handles the pen, g2d_ everything else
  private Timer timer_;
  private boolean isPlay_, isClickable_, isDone_; 

  //Game variables
  Random rng_;

  public HuntMinigame(Dimension size) 
  {
    //Set frame attributes & cursor
    setMinimumSize(size);
    setMaximumSize(size);
    setPreferredSize(size);

    //Initialize data members & visual components
    isPlay_ = isClickable_ = false;
    rng_ = new Random();
    img_ = new BufferedImage((int)size.getWidth(), (int)size.getHeight(), BufferedImage.TYPE_INT_ARGB);
    g2d_ = (Graphics2D)img_.createGraphics();
    g2d_.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    //Images array
    BufferedImage hunt[] = new BufferedImage[15];
    try
    {
      hunt[0] = ImageIO.read(new File("img/forest.png"));
      hunt[1] = ImageIO.read(new File("img/bobcat1.png"));
      hunt[2] = ImageIO.read(new File("img/bobcat2.png"));
      hunt[3] = ImageIO.read(new File("img/lynx1.png"));
      hunt[4] = ImageIO.read(new File("img/lynx1.png"));
      hunt[5] = ImageIO.read(new File("img/mouse1.png"));
      hunt[6] = ImageIO.read(new File("img/mouse2.png"));
      hunt[7] = ImageIO.read(new File("img/possum1.png"));
      hunt[8] = ImageIO.read(new File("img/possum2.png"));
      hunt[9] = ImageIO.read(new File("img/racoon1.png"));
      hunt[10] = ImageIO.read(new File("img/racoon2.png"));
      hunt[11] = ImageIO.read(new File("img/squirrel1.png"));
      hunt[12] = ImageIO.read(new File("img/squirrel2.png"));
      hunt[13] = ImageIO.read(new File("img/woodchuck1.png"));
      hunt[14] = ImageIO.read(new File("img/woodchuck2.png"));
    }
    catch(IOException e)
    {
      System.out.println("ERROR: Image(s) missing from img/ directory");
      System.exit(-1);
    }
      g2d_.drawImage(hunt[0],
                     0, 0, img_.getWidth(), img_.getHeight(),
                     0, 0, hunt[0].getWidth(), hunt[0].getHeight(),
                     Color.BLACK, null);

    addMouseListener
    (
      new MouseAdapter()
      {
        public void mousePressed(MouseEvent e)
        {
          if(e.getButton() == MouseEvent.BUTTON1)  // LMB
          {
          } 
        }
       
        public void mouseReleased(MouseEvent e)
        {
          ///if(isClickable_)
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
        }
      }
    );

    timer_ = new Timer(MILLISECONDS_BETWEEN_FRAMES, new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          timer_.stop();
          int startX = rng_.nextInt(img_.getWidth() - 50);
          int startY = rng_.nextInt(img_.getHeight()/2 - 50) + img_.getHeight()/2;
          int index = rng_.nextInt(14) + 1;
          g2d_.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 0.3f));
          g2d_.drawImage(hunt[index],
                         startX, startY, (startX + 50), (startY + 50),
                         0, 0, hunt[index].getWidth(), hunt[index].getHeight(),
                         Color.BLACK, null);
          setImage();
          timer_.restart();
        }
      }
    );  // new Timer
  }  // public HuntMinigame(Dimension)

  //Compares the user drawn part to the "missing" part of
  //the image. Returns the ratio drawn correctly

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

