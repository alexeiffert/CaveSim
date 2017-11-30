/*----------------------------------------- 
*
*   CaveSim.java
*   Alex Eiffert
*   CAP3027  
*   2 November, 2017 
*   "CaveSim" v1.0
*
------------------------------------------*/

//GUI Support
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.awt.geom.*;
import java.io.*;
import javax.imageio.*;
import javax.swing.*;

//Utilities
import java.util.Random;
import java.util.Scanner;

public class CaveSim
{
  private static final int WIDTH = 1080, HEIGHT = 607;  // 16:9 Aspect ratio

//-----------------------------------------------------//
//-------------------- main() -------------------------//
//-----------------------------------------------------//

  public static void main(String[] args)
  {
    //Invoke on EDT
    SwingUtilities.invokeLater
    (
      new Runnable()
      {
        public void run()
        {
          Multiplexer multiplex = createGUI();
        }
      }
    );
  }  // public static void main() 

  public static Multiplexer createGUI()
  {
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    screenSize.setSize(screenSize.getWidth()/2, screenSize.getHeight()/2);
    Multiplexer multiplex = new Multiplexer(screenSize);
    multiplex.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    multiplex.setVisible(true);
    multiplex.setResizable(false);
    new Thread
    (
      new Runnable()
      {
        public void run()
        {
          multiplex.startCaveSim();
        }
      }
    ).start();
    return multiplex;
  }
}  // class CaveSim 

