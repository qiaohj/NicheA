/**************************************************************************
* Copyright (c) 2001, 2005 David J. Eck                                   *
*                                                                         *
* Permission is hereby granted, free of charge, to any person obtaining   *
* a copy of this software and associated documentation files (the         *
* "Software"), to deal in the Software without restriction, including     *
* without limitation the rights to use, copy, modify, merge, publish,     *
* distribute, sublicense, and/or sell copies of the Software, and to      *
* permit persons to whom the Software is furnished to do so, subject to   *
* the following conditions:                                               *
*                                                                         *
* The above copyright notice and this permission notice shall be included *
* in all copies or substantial portions of the Software.                  *
*                                                                         *
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,         *
* EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF      *
* MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.  *
* IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY    *
* CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,    *
* TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE       *
* SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.                  *
*                                                                         *
* ----                                                                    *
* (Released under new license, April 2012.)                               *
*                                                                         *
*             David J. Eck                                                *
*             Department of Mathematics and Computer Science              *
*             Hobart and William Smith Colleges                           *
*             300 Pulteney Street                                         *
*             Geneva, NY 14456                                            *
*             eck@hws.edu                                                 *
*             http://math.hws.edu/eck                                     *
**************************************************************************/

package edu.hws.jcm.draw;
import java.awt.*;

/**
 * A Grid object draws a graph paper-like grid on a Canvas.  The pixel width
 * and height between adjacent grid lines is specified as a parameter to the
 * constructer, or through the access methods "setXSP(double)" and
 * "setYSP(double)".  Note that the spacing will be scaled to between PIX_MIN
 * and PIX_MAX (20 and 80, respectively, by default).  The color of the grid
 * lines can be set, and defaults to (220, 220, 220).
 *
 * <p>This class was written by Gabriel Weinstock (with some modifications by David Eck).
 */
public class Grid extends Drawable {

   private Color gcol = new Color(220, 220, 220);
   private double xsp, ysp;
   private final int PIX_MAX = 50, PIX_MIN = 20;
   
   /**
    * Create a Grid object with x and y spacing 1.0.  This does not mean that
    * the actual spacing between grid lines will be 1.  It will be some reasonable
    * fraction or multiply of 1, with the value chosen to give a reasonable
    * spacing between the grid lines.
    */
   public Grid() { 
      this(1.0, 1.0);
   }
   
   /**
    * Create a Grid object with spacing specified.
    */
   public Grid(double xspace, double yspace) { 
      xsp = xspace; ysp = yspace;
   }
   
   /**
    * Access method which returns the Color of the grid lines.
    */
   public Color getColor() { 
      return gcol;
   }
   
   /**
    * Method to set the Color used to draw grid lines.
    */

   public void setColor(Color c) 
   { 
      if(c != null && !c.equals(gcol))
      {
         gcol = c; 
         needsRedraw();
      }
   }

   /**
    * Access method to return the x spacing used between grid lines.
    */
   public double getXSP() { return xsp; }
   /**
    * Access method to return the y spacing used between grid lines
    */
   public double getYSP() { return ysp; }
   /**
    * Method to set the x spacing between grid lines.  This does not mean that
    * the actual spacing between grid lines will be x.  It will be some reasonable
    * fraction or multiply of s, with the value chosen to give a reasonable
    * spacing between the grid lines.
    */
   public void setXSP(double x) { xsp = x; needsRedraw(); }
   /**
    * Method to set the y spacing between grid lines.  This does not mean that
    * the actual spacing between grid lines will be y.  It will be some reasonable
    * fraction or multiply of s, with the value chosen to give a reasonable
    * spacing between the grid lines.
    */
   public void setYSP(double y) { ysp = y; needsRedraw(); }
   
   /**
    * Draws the grid if an update is required.  This is not usually called directly.
    *
    * @param g the Graphics context
    * @param coordsch boolean describing whether coordinates have changed
    */
   public void draw(Graphics g, boolean coordsch)
   {
      if(coords == null)
         return;
      double pixwidth = coords.getPixelWidth();
      double pixheight = coords.getPixelHeight();
      if (Double.isNaN(pixwidth) || Double.isNaN(pixheight) || Double.isInfinite(pixheight) 
             || Double.isInfinite(pixwidth) || pixwidth == 0 || pixheight == 0)
         return;
      g.setColor(gcol);
      // start by drawing vertical grid lines (starting with center):
      if(xsp > 0)
      {
         double x = xsp;
         while(x > (pixwidth * PIX_MAX))
            x /= 10;
         if (x < (pixwidth * PIX_MIN))
            x *= 5;
         if (x > (pixwidth * PIX_MAX))
            x /= 2;
         int j = (int) (Math.ceil(coords.getXmin() / x));
         double i = j * x;
         while(coords.xToPixel(i) < (coords.getWidth() + coords.getLeft()))
         {
            g.drawLine(coords.xToPixel(i), coords.getTop(), coords.xToPixel(i), coords.getTop() + coords.getHeight());
            i += x;
         }
      }
      // next draw horizontal grid lines
      if(ysp > 0)
      {
         double y = ysp;
         while(y > (pixheight * PIX_MAX))
            y /= 10;
         if (y < (pixheight * PIX_MIN))
            y *= 5;
         if (y > (pixheight * PIX_MAX))
            y /= 2;
         int j = (int) (Math.ceil(coords.getYmin() / y));
         double i = j * y;
         while(coords.yToPixel(i) > coords.getTop())
         {
            g.drawLine(coords.getLeft(), coords.yToPixel(i), coords.getLeft() + coords.getWidth(), coords.yToPixel(i));
            i += y;
         }
      }
   }

} // end class Grid
