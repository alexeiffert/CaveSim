import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.awt.geom.*;
import java.io.*;
import javax.imageio.*;
import javax.swing.*;

import java.util.Random;
import java.util.Scanner;

public class ClickPanel extends JPanel
{
  private static final int MILLISECONDS_BETWEEN_FRAMES = 500;  // i.e. 2 fps
  private static final int SQUARE_SIZE = 8, GRID_SIZE = 100;

  private BufferedImage img_;
  private Graphics2D g2d_;
  private Timer timer_;
  private char[][] creatures_;
  private boolean isPlay_, isReady_;

  public ClickPanel(int side)  // width = height
  {
    Dimension size = new Dimension(side, side);
    setMinimumSize(size);
    setMaximumSize(size);
    setPreferredSize(size);

    img_ = new BufferedImage(side, side, BufferedImage.TYPE_INT_ARGB);
    g2d_ = (Graphics2D)img_.createGraphics();
    creatures_ = new char[GRID_SIZE][GRID_SIZE];
    isPlay_ = isReady_ = false;

    addMouseListener
    (
      new MouseAdapter()
      {
        public void mouseReleased(MouseEvent e)  // A better choice than mouseClicked
        {
          if(!isReady_) 
            return;
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
                  if(!isPlay_)
                  {
                    int square = getSquare(p);
                    ageSquare(square);
                  }
                } 
              }
            }
          ).start();
        }
      }
    );

    timer_ = new Timer(MILLISECONDS_BETWEEN_FRAMES, new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          timer_.stop();
          letThereBeLife();
          setImage();
          timer_.restart();
        }
      }
    );  // new Timer
  }  // public FractalSelectPanel(int, int)

  public boolean loadSystem(File inputFile)
  {
    try
    {
      Scanner s = new Scanner(new BufferedReader(new FileReader(inputFile)));
      String str = s.next();
      for(int i = 0; i < GRID_SIZE; ++i)
      {
        for(int j = 0; j < GRID_SIZE; ++j)
        {
          creatures_[i][j] = str.charAt(i*GRID_SIZE + j);
        }
      } 
      showGrid();
      return true;
    }
    catch(Exception e)
    {
      return false;
    }
  }

  public boolean saveSystem(File outputFile)
  {
    if(!isReady_)
      return false;
    try
    {
      BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));
      for(int i = 0; i < GRID_SIZE; ++i)
        writer.write(new String(creatures_[i]));
      writer.close();
      return true;
    }
    catch(Exception e)
    {
      return false;
    }
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
