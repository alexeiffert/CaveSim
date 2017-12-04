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
import javax.sound.midi.*;

import java.util.Random;
import java.util.Scanner;

public class Cutscene extends JPanel
{
  private static final int MILLISECONDS_BETWEEN_FRAMES = 400;  

  private Sequencer seq_;
  private BufferedImage img_;
  private Graphics2D g2d_;
  private Timer timer_;
  private boolean isPlay_, isDone_;

  private String[] strArr_;
  private int index_;
  private int x_;
  private boolean isDraw_;

  public Cutscene(Dimension size) 
  {
    setMinimumSize(size);
    setMaximumSize(size);
    setPreferredSize(size);

    isDraw_ = isPlay_ = isDone_ = false;

    img_ = new BufferedImage((int)size.getWidth(), (int)size.getHeight(), 
                             BufferedImage.TYPE_INT_ARGB);
    g2d_ = (Graphics2D)img_.createGraphics();
    changeBackground(Color.BLACK);
  
    //The below array seems preposterous, but it turned out to be the easiest
    //way to control each element's screen time as well as implement
    //the vintage scrolling effect.
    index_ = 0;
    strArr_ = new String[]{"", "", "", "", "",
                           "\"The caveman is a stock character", "\"The caveman is a stock character", 
                           "\"The caveman is a stock character", "\"The caveman is a stock character", 
                           "\"The caveman is a stock character", "\"The caveman is a stock character", 
                           "\"based upon widespread but", "\"based upon widespread but",
                           "\"based upon widespread but", "\"based upon widespread but",
                           "\"based upon widespread but", "\"based upon widespread but",
                           "\"ANACHRONISTIC", "\"ANACHRONISTIC",  
                           "\"ANACHRONISTIC", "\"ANACHRONISTIC",  
                           "\"ANACHRONISTIC", "\"ANACHRONISTIC",  
                           "\"ANACHRONISTIC", "\"ANACHRONISTIC",  
                           "\"ANACHRONISTIC", "\"ANACHRONISTIC",  
                           "\"concepts of the way in which", "\"concepts of the way in which", 
                           "\"concepts of the way in which", "\"concepts of the way in which", 
                           "\"concepts of the way in which", "\"concepts of the way in which", 
                           "\"Neanderthals,", "\"Neanderthals,",
                           "\"Neanderthals,", "\"Neanderthals,",
                           "\"early modern humans,", "\"early modern humans,", 
                           "\"early modern humans,", "\"early modern humans,", 
                           "\"and archaic humans", "\"and archaic humans",
                           "\"and archaic humans", "\"and archaic humans",
                           "\"may have looked and behaved...\"", "\"may have looked and behaved...\"",
                           "\"may have looked and behaved...\"", "\"may have looked and behaved...\"",
                           "\"may have looked and behaved...\"", "\"may have looked and behaved...\"",
                           "\"may have looked and behaved...\"", "\"may have looked and behaved...\"",
                           "\"may have looked and behaved...\"", "\"may have looked and behaved...\"",
                           "\"may have looked and behaved...\"", "\"may have looked and behaved...\"",
                           "\"may have looked and behaved...\"", "\"may have looked and behaved...\"",
                           "But.. he doesn't care about that.", "But.. he doesn't care about that.", 
                           "But.. he doesn't care about that.", "But.. he doesn't care about that.", 
                           "But.. he doesn't care about that.", "But.. he doesn't care about that.", 
                           "But.. he doesn't care about that.", "But.. he doesn't care about that.", 
                           "But.. he doesn't care about that.", "But.. he doesn't care about that.", 
                           "But.. he doesn't care about that.", "But.. he doesn't care about that.", 
                           "But.. he doesn't care about that.", "But.. he doesn't care about that.", 
                           "Now, YOU are the caveman.", "Now, YOU are the caveman.", 
                           "Now, YOU are the caveman.", "Now, YOU are the caveman.", 
                           "Now, YOU are the caveman.", "Now, YOU are the caveman.", 
                           "Now, YOU are the caveman.", "Now, YOU are the caveman.", 
                           "Now, YOU are the caveman.", "Now, YOU are the caveman.", 
                           "Now, YOU are the caveman.", "Now, YOU are the caveman.", 
                           "m", "im", "Sim", "eSim", "veSim", "aveSim", 
                           "CaveSim", " CaveSim", "  CaveSim", "   CaveSim", "    CaveSim",
                           "     CaveSim", "      CaveSim", "       CaveSim", "       CaveSim"};
    //Initialize XORMode
    dispText();
    --index_;

    //Skip cutscene
    addMouseListener
    (
      new MouseAdapter()
      {
        public void mouseClicked(MouseEvent e) 
        {
          timer_.stop();
          isDone_ = true;
          stopAlsoSprachZarathustra();
        }
      }
    );
    addKeyListener
    (
      new KeyAdapter()
      {
        public void keyPressed(KeyEvent e)
        {
          timer_.stop();
          isDone_ = true;
          stopAlsoSprachZarathustra();
        }
      }
    );

    timer_ = new Timer(MILLISECONDS_BETWEEN_FRAMES, new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          timer_.stop();
          errText();
          dispText();
          repaint();
          if(index_ == strArr_.length - 1)
          {
            timer_.stop();
            isDone_ = true;
          }
          else
            timer_.restart();
        }
      }
    );  // new Timer
  }  // public Cutscene(Dimension) 

  public boolean Done()
  {
    return isDone_;
  }

  public boolean togglePlay()
  {
    AlsoSprachZarathustra();
    isPlay_ = !isPlay_;
    if(isPlay_)
      timer_.restart();
    else
      timer_.stop();
    return isPlay_;
  }  //TogglePlay

  private void dispText()
  {
    g2d_.setXORMode(Color.WHITE);
    g2d_.setFont(new Font(Font.SERIF, Font.BOLD, 75 ));
    g2d_.drawString(strArr_[++index_], 0, img_.getHeight()/2);
    g2d_.setPaintMode();
  }

  private void errText()
  {
    g2d_.setXORMode(Color.WHITE);
    g2d_.setFont(new Font(Font.SERIF, Font.BOLD, 75));
    g2d_.drawString(strArr_[index_], 0, img_.getHeight()/2);
    g2d_.setPaintMode();
  }

  private void changeBackground(Color color)
  {
    g2d_.setColor(color);
    g2d_.fillRect(0, 0, img_.getWidth(), img_.getHeight());
    repaint();
  }

  //Opens and plays ASZ.mid on new thread
  public void AlsoSprachZarathustra()
  {
    new Thread 
    (
      new Runnable()
      {
        public void run()
        {
          try 
          {
            seq_ = MidiSystem.getSequencer();
            seq_.setSequence(MidiSystem.getSequence(new File("audio/ASZ.mid")));
            seq_.open();
            seq_.start();
            while(true) 
            {
              seq_.setTempoInBPM(100f);
              if(seq_.isRunning()) 
              {
                try 
                {
                  Thread.sleep(1000); // Check every second
                } 
                  catch(InterruptedException ignore) 
                {
                  break;
                }
              }
              else 
              {
                break;
              }
            }
            seq_.stop();
            seq_.close();
          } 
          catch(Exception e)
          {
            //This will output after stopping the cutscene... 
            //I am not sure how to fix, though.
            System.out.println("ERROR: Midi file is missing");
          } 
        }
      }
    ).start();
  }

  public void stopAlsoSprachZarathustra()
  {
    try 
    {
      seq_.stop();
      seq_.close();
    } 
    catch(Exception e)
    {
      System.out.println("ERROR: Problem stopping midi file");
    } 
  }

  public void paintComponent(Graphics g)  // Class override
  {
    super.paintComponent(g);
    g.drawImage(img_, 0, 0, null);
  }

}  // class Cutscene 

