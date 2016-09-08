/*
 * ClientUtil.java
 *
 * Created on 05 December 2007, 17:06
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package ngat.oss.client.gui.util;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Toolkit;
import javax.swing.JFrame;

/**
 *
 * @author nrc
 */
public class FrameCenterer extends Thread
{
  private JFrame frame;

  public FrameCenterer(JFrame frame)
  {
    this.frame = frame;
  }

  public void run()
  {
    Dimension screenDimension = Toolkit.getDefaultToolkit().getScreenSize();
    double hd = screenDimension.getHeight();
    double wd = screenDimension.getWidth();

    double yd = (hd - frame.getBounds().getHeight()) / 2;
    double xd = (wd - frame.getBounds().getWidth()) / 2;

    final int x = (int)xd;
    final int y = (int)yd;

    EventQueue.invokeLater(
                            new Runnable()
                            {
                              public void run()
                              {
                                frame.setLocation(x, y);
                              }
                            }
                          );
  }
}
