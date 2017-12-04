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
  private static final int MILLISECONDS_BETWEEN_FRAMES = 900;
  private static final int TIME = 20;

  //Main animation variables
  private BufferedImage img_;  //Display img
  private Graphics2D g2d_;  
  private Timer timer_;
  private boolean isPlay_, isClickable_, isDone_; 
  private int score_;

  //Game variables
  private BufferedImage[] hunt_;
  private Random rng_;
  private MouseAdapter oldMA_;
  private MouseMotionAdapter oldMMA_;
  private int time_, points_;

  public HuntMinigame(Dimension size) 
  {
    //Set frame attributes & cursor
    setMinimumSize(size);
    setMaximumSize(size);
    setPreferredSize(size);

    //Initialize data members & visual components
    score_ = 0;
    isPlay_ = isClickable_ = false;
    rng_ = new Random();
    oldMA_ = null;
    oldMMA_ = null;
    time_ = TIME;
    points_ = 0;
    img_ = new BufferedImage((int)size.getWidth(), (int)size.getHeight(), 
                             BufferedImage.TYPE_INT_ARGB);
    g2d_ = (Graphics2D)img_.createGraphics();
    g2d_.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
                          RenderingHints.VALUE_ANTIALIAS_ON);

    //Initialize hunt_[] images array on new thread
    initHunt();

    changeBackground(Color.BLACK);
    repaint();

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
          changeBackground(Color.BLACK);
          g2d_.setColor(Color.WHITE);
          g2d_.setFont(new Font(Font.SERIF, Font.BOLD, 150));
          g2d_.drawString("Score: " + points_, 50, 200);
          int startX = rng_.nextInt(img_.getWidth() - 51);
          int startY = rng_.nextInt(img_.getHeight()/2 - 51) + img_.getHeight()/2;
          int index = rng_.nextInt(14) + 1;
          g2d_.drawImage(hunt_[index],
                         startX, startY, (startX + 50), (startY + 50),
                         0, 0, hunt_[index].getWidth(), hunt_[index].getHeight(),
                         Color.BLACK, null);
          
          //Dynamically create mouse adapters
          MouseAdapter MA = new MouseAdapter()
            {
              public void mouseReleased(MouseEvent e)
              {
                Point p = e.getPoint();
                if(p.x > startX && p.x < startX + 50 && p.y > startY && p.y < startY + 50)
                  ++points_;
                else
                  --points_;
                changeBackground(Color.BLACK);
                g2d_.setColor(Color.WHITE);
                g2d_.setFont(new Font(Font.SERIF, Font.BOLD, 150));
                g2d_.drawString("Score: " + points_, 50, 200);
                repaint();
              }
            };
          MouseMotionAdapter MMA = new MouseMotionAdapter()
            {
              public void mouseMoved(MouseEvent e)
              {
                Point p = e.getPoint();
                if(p.x > startX && p.x < startX + 50 && p.y > startY && p.y < startY + 50)
                  setCursor(new Cursor(Cursor.HAND_CURSOR));
                else
                  setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
              }
            };
          removeMouseListener(oldMA_);
          removeMouseMotionListener(oldMMA_);
              
          repaint();
          if(--time_ == 0)
          {
            score_ = points_/3;
            if(score_ < 0)
              score_ = 0;
            changeBackground(Color.BLACK);
            g2d_.setColor(Color.WHITE);
            g2d_.setFont(new Font(Font.SERIF, Font.BOLD, 50));
            g2d_.drawString("You caught " + score_ + " critters.", 
                             0, img_.getHeight()/2);
            if(points_ < 4) 
              g2d_.drawString("Try to do better next time.", 0, img_.getHeight()/2 + 60);
            else if(points_ < 7) 
              g2d_.drawString("That's okay, some cavemen were gatherers.", 0, img_.getHeight()/2 + 60);
            else if(points_ < 13) 
              g2d_.drawString("You must've been hungry.", 0, img_.getHeight()/2 + 60);
            else 
              g2d_.drawString("Creatures beware.", 0, img_.getHeight()/2 + 60);
            g2d_.drawString("[Click anywhere to continue]", 0, img_.getHeight()/2 + 120);
            addMouseListener
            (
              new MouseAdapter()
              {
                public void mouseReleased(MouseEvent e)
                {
                  isDone_ = true;
                }
              }
            );
          }  // if(time_ == 0)
          else
          {
            addMouseListener(MA);
            addMouseMotionListener(MMA);
            oldMA_ = MA;
            oldMMA_ = MMA;
            timer_.restart();
          }
        }
      }
    );  // new Timer
  }  // public HuntMinigame(Dimension)

  public int getScore()
  {
    return score_;
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

  public BufferedImage getImage()
  {
    return img_;
  }
  
  private void initHunt()
  {
    new Thread
    (
      new Runnable()
      {
        public void run()
        {
          hunt_ = new BufferedImage[15];
          try
          {
            hunt_[0] = ImageIO.read(new File("img/forest.png"));
            hunt_[1] = ImageIO.read(new File("img/bobcat1.png"));
            hunt_[2] = ImageIO.read(new File("img/bobcat2.png"));
            hunt_[3] = ImageIO.read(new File("img/lynx1.png"));
            hunt_[4] = ImageIO.read(new File("img/lynx1.png"));
            hunt_[5] = ImageIO.read(new File("img/mouse1.png"));
            hunt_[6] = ImageIO.read(new File("img/mouse2.png"));
            hunt_[7] = ImageIO.read(new File("img/possum1.png"));
            hunt_[8] = ImageIO.read(new File("img/possum2.png"));
            hunt_[9] = ImageIO.read(new File("img/racoon1.png"));
            hunt_[10] = ImageIO.read(new File("img/racoon2.png"));
            hunt_[11] = ImageIO.read(new File("img/squirrel1.png"));
            hunt_[12] = ImageIO.read(new File("img/squirrel2.png"));
            hunt_[13] = ImageIO.read(new File("img/woodchuck1.png"));
            hunt_[14] = ImageIO.read(new File("img/woodchuck2.png"));
          }
          catch(IOException e)
          {
            System.out.println("ERROR: Image(s) missing from img/ directory");
            System.exit(-1);
          }
        }
      }
    ).start();
  }  // private void initHunt()

  private void changeBackground(Color color)
  {
    g2d_.setColor(color);
    g2d_.fillRect(0, 0, img_.getWidth(), img_.getHeight());
    repaint();
  }
  
  public void paintComponent(Graphics g)  // Class override
  {
    super.paintComponent(g);
    g.drawImage(img_, 0, 0, null);
  }

}  // class HuntMinigame

