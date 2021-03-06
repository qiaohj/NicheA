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

package edu.hws.jcm.awt;

/**
 * The Limits interface is implemented by edu.hws.jcm.data.CoordinateRect
 * and by other objects that can be "Tied" to a CoordinateRect, such as
 * LimitControlPanel. This will be used to synchronize the (x,y)
 * limits on the CoordinateRect with limit values obtained elsewhere.
 * See the Tie class for more information.
 *
 * @author David Eck
 */
public interface Limits extends java.io.Serializable {

   /**
    * Return a 4-element array containing xmin, xmax, ymin, and ymax. 
    */
   public double[] getLimits();
   
   /**
    * Set the current limits.
    *
    * @param A 4-element array containing the new xmin, xmax, ymin, and ymax.
    */
   public void setLimits(double[] limits);

}
