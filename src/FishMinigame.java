/*----------------------------------------- 
*
*   FishMinigame.java
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

public class FishMinigame extends JPanel
{
  private static final int X = 0, Y = 1, THETA = 2, SPEED = 3;
  private static final int BG = 0xFF000030;
  private static final int NUMFISH = 8;
  private static final int MILLISECONDS_BETWEEN_FRAMES = 32;  // i.e. ~ 30 fps
  private static final int TIME = 600;  // i.e. ~ 20s

  //Animation objects
  private BufferedImage img_;
  private Graphics2D g2d_;
  private Timer timer_;
  private int time_;
  private Random rng_;
  private boolean isPlay_, isClickable_, isDone_;

  //Game objects
  private BufferedImage[] fishImgArr_;
  private double[][] fishArr_;
  private double[] spearArr_;
  private boolean spearThrown_;
  private double power_;
  private int score_;

  public FishMinigame(Dimension size)
  {
    setMinimumSize(size);
    setMaximumSize(size);
    setPreferredSize(size);

    isDone_ = false;
    time_ = TIME;

    img_ = new BufferedImage((int)size.getWidth(), (int)size.getHeight(),
                             BufferedImage.TYPE_INT_ARGB_PRE);
    g2d_ = (Graphics2D)img_.createGraphics();
    g2d_.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                          RenderingHints.VALUE_ANTIALIAS_ON);
    changeBackground(new Color(BG, true));
    rng_ = new Random();

    spearArr_ = new double[3];
    spearArr_[X] = .5*img_.getWidth();
    spearArr_[Y] = img_.getHeight();
    spearArr_[THETA] = -.5*Math.PI;
    spearThrown_ = false;
    power_ = 10;
    score_ = 0;
    fishArr_ = new double[NUMFISH][4];
    for(int i = 0; i < NUMFISH; ++i)
    {
      fishArr_[i][X] = rng_.nextInt(img_.getWidth());
      fishArr_[i][Y] = .5*rng_.nextInt(img_.getHeight());
      fishArr_[i][THETA] = rng_.nextDouble()*Math.PI;
      fishArr_[i][SPEED] = rng_.nextInt(11) + 5;
    }

    //Images array
    fishImgArr_ = new BufferedImage[NUMFISH];
    try
    {
      for(int i = 0; i < NUMFISH; ++i)
      {
        if(rng_.nextInt(2) == 1)
          fishImgArr_[i] = ImageIO.read(new File("img/fish1.png"));
        else
          fishImgArr_[i] = ImageIO.read(new File("img/fish2.png"));
      }
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
          if(--time_ == 0)
          {
            isClickable_ = true;
            changeBackground(Color.BLACK);
            g2d_.setColor(Color.WHITE);
            g2d_.setFont(new Font(Font.SERIF, Font.BOLD, 50));
            g2d_.drawString("You caught " + score_ + " fish.", 
                             0, img_.getHeight()/2);
            if(score_ < 2) 
              g2d_.drawString("They just slithered right through your fingers.", 0, img_.getHeight()/2 + 60);
            else if(score_ < 4) 
              g2d_.drawString("That's okay, some cavemen were gatherers.", 0, img_.getHeight()/2 + 60);
            else if(score_ < 6) 
              g2d_.drawString("Fish fry, anyone?", 0, img_.getHeight()/2 + 60);
            else 
              g2d_.drawString("Fish everywhere, you better beware.", 0, img_.getHeight()/2 + 60);
            g2d_.drawString("[Click anywhere to continue]", 0, img_.getHeight()/2 + 120); 
            return;
          }            

          changeBackground(new Color(BG, true));
          for(int i = 0; i < NUMFISH; ++i)
          {
            fishArr_[i][X] += fishArr_[i][SPEED]*Math.cos(fishArr_[i][THETA]); 
            fishArr_[i][Y] += fishArr_[i][SPEED]*Math.sin(fishArr_[i][THETA]);
            if(fishArr_[i][X] < -50)
            {
              fishArr_[i][X] = img_.getWidth();
              fishArr_[i][SPEED] = rng_.nextInt(11) + 5;
            }
            int x0 = (int)fishArr_[i][X];
            int y0 = (int)fishArr_[i][Y];
            fishArr_[i][THETA] += rng_.nextDouble()*.5 - .25;
            if(fishArr_[i][THETA] < .75*Math.PI)
              fishArr_[i][THETA] = .75*Math.PI;
            else if(fishArr_[i][THETA] > 1.25*Math.PI)
              fishArr_[i][THETA] = 1.25*Math.PI;
            g2d_.drawImage(fishImgArr_[i],
                           x0, y0, x0 + 100, y0 + 40,
                           0, 0, fishImgArr_[i].getWidth(), fishImgArr_[i].getHeight(), 
                           Color.BLACK, null);
          }
          g2d_.setStroke(new BasicStroke(4f));
          g2d_.setColor(new Color(0x181008));
          double dXs = 230*Math.cos(spearArr_[THETA]); 
          double dYs = 230*Math.sin(spearArr_[THETA]);
          if(spearThrown_)
          {
            spearArr_[X] += power_*Math.cos(spearArr_[THETA]);
            spearArr_[Y] += power_*Math.sin(spearArr_[THETA]);
 
            //If spear hits a fish
            if((spearArr_[X] + dXs) > 0 && spearArr_[X] + dXs < img_.getWidth() &&
               (spearArr_[Y] + dYs) > 0 && spearArr_[Y] + dYs < img_.getHeight())
            {
              if(img_.getRGB((int)(spearArr_[X] + dXs), (int)(spearArr_[Y] + dYs)) != BG)
              {
                ++score_;
                spearArr_[X] = .5*img_.getWidth();
                spearArr_[Y] = img_.getHeight();
                power_ = 10;
                spearThrown_ = false;
              }
            }
            //If spear goes off screen
            else if(spearArr_[Y] < 0 || spearArr_[Y] > img_.getHeight() + 200)
            {
              spearArr_[X] = .5*img_.getWidth();
              spearArr_[Y] = img_.getHeight();
              power_ = 10;
              spearThrown_ = false;
            }
          }
          g2d_.draw(new Line2D.Double(spearArr_[X], spearArr_[Y],
                                      (spearArr_[X] + dXs), spearArr_[Y] + dYs));
          repaint();
          timer_.restart();
        }
      }
    );  // new Timer

    addKeyListener
    (
      new KeyAdapter()
      {
        public void keyPressed(KeyEvent e)
        {
          if(spearThrown_)
            return;
          if(e.getKeyCode() == KeyEvent.VK_LEFT)
            spearArr_[THETA] -= .03; 
          else if(e.getKeyCode() == KeyEvent.VK_RIGHT)
            spearArr_[THETA] += .03;
          else if(e.getKeyCode() == KeyEvent.VK_SPACE)
          {
            if(power_ < 50)
              ++power_;
          }
        }
        public void keyReleased(KeyEvent e)
        {
          if(e.getKeyCode() == KeyEvent.VK_SPACE)
            spearThrown_ = true;
        }
      }
    );

    addMouseListener
    (
      new MouseAdapter() 
      {
        public void mouseClicked(MouseEvent e)
        {
          if(isClickable_)
            isDone_ = true;
        }
      }
    );

  }  // public FishMinigame(Dimension)

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

  private void changeBackground(Color color)
  {
    g2d_.setColor(color);
    g2d_.fillRect(0, 0, img_.getWidth(), img_.getHeight());
    repaint();
  }

  public BufferedImage getImage()
  {
    return img_;
  }
  
  public void paintComponent(Graphics g)  // Class override
  {
    super.paintComponent(g);
    g.drawImage(img_, 0, 0, null);
  }

}  // class FishMinigame 

