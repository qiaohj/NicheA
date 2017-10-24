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

import java.awt.Graphics;

/**
 * A Drawable object can be added to a CoordinateRect, which is itself in 
 * a DisplayCanvas.  Its purpose is, generally, to draw something in the
 * rectangular area represented by the CoordinateRect.  The drawing can
 * use information in the CoordinateRect, which includes both the real
 * number coordinates and the pixel coordinates of the rectangular area.
 */
abstract public class Drawable implements java.io.Serializable {
   
   /**
    * The CoordinateRect for the rectagular area where this
    * Drawable is drawn.  This is set automatically when the
    * Drawable is added to a CoordingteRect and should not be
    * changed.  (It will be changed automatically if the 
    * Drawable is removed from the CoordinateRect.)
    */
   protected CoordinateRect coords;

   /**
    * The canvas on which this Drawable is drawn.  This is set
    * automatically when the Drawable is added to a CoordinateRect
    * and it should not be changed.  (It will be changed automatically
    * if the Drawable is removed from the CoordinateRect.)
    */
   protected DisplayCanvas canvas;
                            
   private boolean visible = true;  // If visible is false, then the CoordinateRect
                                    // that manages this Drawable will ignore it.
                                    // It will not call the draw() routine, so when
                                    // draw is called, it can be assumed that the
                                    // Drawable is visible.
                                    
   /**
     * Draw this drawable in the graphics context g.  This is meant to
     * be called only by the CoordinateRect, coords, that manages this Drawable.
     * The coords contains information about the rectangular area in which
     * this Drawable is displayed, both in terms of pixels and in terms
     * of real (x,y)-coordinates.
     *    The value of coordsChanged is true if any of the values
     *  coords.getXmin(), coords.getXmax(), coords.getYmin(), coords.getYmax(), 
     * coords.getLeft(), coords.getRight(), coords.getTop(), coords.getBottom(), 
     * or coords.getGap() has changed.  Drawables that depend only on this
     * information can check the value of coordsChanged to see whether they
     * need to update any previously computed member variables that
     * depend on these values.  This method is meant to be called only by the system.
     * 
     * @param g The graphics context in which the Drawble is to be drawn.  (The drawing 
     *            can change the color in g, but should not permanently change font, painting mode, etc.
     *            Thus, every drawable is responsible for setting the color it wants to use.)
     * @param coordsChanged Indicates whether the CoordinateRect has changed.
     */
   abstract public void draw(Graphics g, boolean coordsChanged);

   /**
     * Return true if this Drawable is visible, false if it is hidden.
     * A hidden Drawable is ignored by the CoordinateRect that manages it.
     */
   public boolean getVisible() {
      return visible;
   }
   
  /**
   * Set the visibility of this Drawable.  If show is false, then
   * the Drawable is hidden.  If it is true, the Drawable is shown.
   */
   public void setVisible(boolean show) {
      if (show != visible) {
          visible = show;
          needsRedraw();
      }
   }

   /**
    * This routine should be called if the appearance of the Drawable changes
    * so that the rectangular area that it occupies has to be redrawn.
    * The routine is generally meant to be called by the Drawable itself.
    * It will notify the DisplayCanvas, canvas, that the CoordinateRect,
    * coords, needs to be redrawn, where canvas and coords are the member
    * variables in this class.  If canvas is null, nothing happens, since
    * presumably the Drawable is not displayed anywhere in that case.
    */
   public void needsRedraw() {
       if (canvas != null)
          canvas.doRedraw(coords);
   }
   
   /**
    * Sets the values of member variables canvas and coords.  This is
    * designed to be called only by the CoordinateRect class.
    */
   protected void setOwnerData(DisplayCanvas canvas, CoordinateRect coords) {
      this.canvas = canvas;
      this.coords = coords;
   }
   
} // end class Drawable
