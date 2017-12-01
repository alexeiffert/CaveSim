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

public class PaintMinigame extends JPanel
{
  private static final int MILLISECONDS_BETWEEN_FRAMES = 1000;
  private static final int TIME = 20;

  //Main animation variables
  private BufferedImage img_;  //Display img
  private Graphics2D g2d_, g2d2_;  //g2d2_ handles the pen, g2d_ everything else
  private Timer timer_;
  private boolean isPlay_, isClickable_, isDone_; 

  //Game variables
  private BufferedImage compare_[];  //Comparison drawings
  private BufferedImage cave_[];  //For simplicity, all drawings are loaded
  private int time_, index_;
  private Point prev_;
  private boolean isDraw_, canDraw_;

  public PaintMinigame(Dimension size) 
  {
    //Set frame attributes & cursor
    setMinimumSize(size);
    setMaximumSize(size);
    setPreferredSize(size);
    setCursor(new Cursor(Cursor.HAND_CURSOR));

    //Initialize data members & visual components
    time_ = TIME;
    index_ = 0;
    prev_ = null;
    canDraw_ = true;  //Master flag
    isPlay_ = isClickable_ = isDraw_ = false;
    index_ = (new Random()).nextInt(3)*2 + 2;  //Choose random image to draw
    img_ = new BufferedImage((int)size.getWidth(), (int)size.getHeight(), BufferedImage.TYPE_INT_ARGB);
    g2d_ = (Graphics2D)img_.createGraphics();
    g2d_.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g2d2_ = (Graphics2D)img_.createGraphics();
    g2d2_.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g2d2_.setStroke(new BasicStroke(10.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
    g2d2_.setColor(new Color(0xFFFF6A33));
    cave_ = new BufferedImage[7];
    try
    {
      cave_[0] = ImageIO.read(new File("img/cave.png"));
      cave_[1] = ImageIO.read(new File("img/cave11.png"));
      cave_[2] = ImageIO.read(new File("img/cave12.png"));
      cave_[3] = ImageIO.read(new File("img/cave21.png"));
      cave_[4] = ImageIO.read(new File("img/cave22.png"));
      cave_[5] = ImageIO.read(new File("img/cave31.png"));
      cave_[6] = ImageIO.read(new File("img/cave32.png"));
    }
    catch(IOException e)
    {
      System.out.println("ERROR: Image(s) missing from img/ directory");
      System.exit(-1);
    }

    //Resize comparison images and store in compare_[] (not displayed)
    compare_ = new BufferedImage[2];
    compare_[0] = new BufferedImage(img_.getWidth(), img_.getHeight(), BufferedImage.TYPE_INT_ARGB);
    compare_[1] = new BufferedImage(img_.getWidth(), img_.getHeight(), BufferedImage.TYPE_INT_ARGB);
    Graphics2D cg2d = (Graphics2D)compare_[0].createGraphics();
    cg2d.drawImage(cave_[index_ - 1],
                   0, 0, img_.getWidth(), img_.getHeight(),
                   0, 0, cave_[0].getWidth(), cave_[0].getHeight(),
                   Color.BLACK, null);
    cg2d = (Graphics2D)compare_[1].createGraphics();
    cg2d.drawImage(cave_[index_],
                   0, 0, img_.getWidth(), img_.getHeight(),
                   0, 0, cave_[0].getWidth(), cave_[0].getHeight(),
                   Color.BLACK, null);
     cg2d.dispose();
    
    //Draw display image & graphical timer
    g2d_.drawImage(cave_[index_ - 1],
                   0, 0, img_.getWidth(), img_.getHeight(),
                   0, 0, cave_[0].getWidth(), cave_[0].getHeight(),
                   Color.BLACK, null);
    g2d_.setXORMode(Color.BLACK);
    g2d_.setFont(new Font(Font.SERIF, Font.BOLD, 150));
    g2d_.drawString(time_ + "", 150, 200);
    g2d_.setPaintMode();

    addMouseListener
    (
      new MouseAdapter()
      {
        public void mousePressed(MouseEvent e)
        {
          if(!canDraw_)
            return;
          if(e.getButton() == MouseEvent.BUTTON1)  // LMB
          {
            prev_ = e.getPoint();
            g2d2_.fill(new Ellipse2D.Double(prev_.x - 5, prev_.y - 5, 10, 10));
            setImage();
            isDraw_ = true;
          } 
        }
       
        public void mouseReleased(MouseEvent e)
        {
          if(isClickable_)
            isDone_ = true;
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
            g2d2_.draw(new Line2D.Double(prev_.x, prev_.y, p.x, p.y));
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
          g2d_.setXORMode(Color.BLACK);
          g2d_.drawString(time_ + "", 150, 200);
          g2d_.drawString(--time_ + "", 150, 200);
          g2d_.setPaintMode();
          setImage();
          if(time_ == 0)
          {
            canDraw_ = isDraw_ = false;
            isClickable_ = true;
            setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            final double score = Math.round(compare())*100;
            new Thread
            (
              new Runnable()
              {
                public void run()
                {
                  SwingUtilities.invokeLater
                  (
                    new Runnable()
                    {
                      public void run()
                      {
                        g2d_.setFont(new Font(Font.SERIF, Font.BOLD, 50));
                        if(score < 10) 
                        {
                          g2d_.drawString("You scored " + score + " and gained 0 intelligence points.", 
                                          0, img_.getHeight()/2);
                          g2d_.drawString("Try to do better next time.", 0, img_.getHeight()/2 + 60);
                        }
                        else if(score < 20) 
                        {
                          g2d_.drawString("You scored " + score + " and gained 1 intelligence point.", 
                                          0, img_.getHeight()/2);
                          g2d_.drawString("Try to do better next time.", 0, img_.getHeight()/2 + 60);
                        }
                        else if(score < 40) 
                        {
                          g2d_.drawString("You scored " + score + " and gained 2 intelligence points.", 
                                          0, img_.getHeight()/2);
                          g2d_.drawString("Are you remembering the scenes?", 0, img_.getHeight()/2 + 60);
                        }
                        else if(score < 60) 
                        {
                          g2d_.drawString("You scored " + score + " and gained 3 intelligence points.", 
                                          0, img_.getHeight()/2);
                          g2d_.drawString("You're turning into quite the cave artist.",
                                          0, img_.getHeight()/2 + 60);
                        }
                        else  
                        {
                          g2d_.drawString("You scored " + score + " and gained 4 intelligence points.",
                                          0, img_.getHeight()/2);
                          g2d_.drawString("Your masterpiece was saved in the Screenshots/ directory.", 
                                          0, img_.getHeight()/2 + 60);
                          String randomStr = "Screenshots/" + (new Random()).nextInt(100000) + "";
                          File outputFile = new File(randomStr);
                          try
                          {
                            javax.imageio.ImageIO.write(img_, "png", outputFile);
                          }
                          catch(Exception e)
                          {
                            System.out.println("ERROR: There was an error taking a screenshot.");
                          }
                        }
                        g2d_.drawString("[Click anywhere to continue]", 0, img_.getHeight()/2 + 120);
                        setImage();
                      }
                    }
                  );
                }
              }
            ).start();
            g2d_.drawImage(cave_[index_],
                           0, 0, img_.getWidth(), img_.getHeight(),
                           0, 0, cave_[0].getWidth(), cave_[0].getHeight(),
                           Color.BLACK, null);
            setImage();
          }
          else
            timer_.restart();
        }
      }
    );  // new Timer
  }  // public PaintMinigame(Dimension)

  //Compares the user drawn part to the "missing" part of
  //the image. Returns the ratio drawn correctly
  private double compare()
  {
    double count = 0, correct = 0, incorrect = 0;
    for(int i = 0; i < img_.getWidth() - 1; ++i)
    {
      for(int j = 0; j < img_.getHeight() - 1; ++j)
      {
        if(compare_[1].getRGB(i, j) == 0xFFFF6A33)
        {
          if(compare_[0].getRGB(i, j) != 0xFFFF6A33)
          {
            ++count;
            if(img_.getRGB(i, j) == 0xFFFF6A33)
              ++correct;
          }
        }
        else if(img_.getRGB(i, j) == 0xFFFF6A33)
          ++incorrect;
      }
    }
    correct -= incorrect*.1;  //Weight incorrect pixels
    if(correct < 0)
      correct = 0;
    return correct/count; 
  }  // private double compare()

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

}  // class PaintMinigame

