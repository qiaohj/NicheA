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

import edu.hws.jcm.data.*;

/**
 * A Crosshair is a small cross, 15 pixels wide and high, that is drawn in
 * a CoordinateRect at a specified point.
 *     A Crosshair is a Computable object, so should be added to a Controller to be 
 * recomputed when the coordinates of the point change. 
 */

public class Crosshair extends DrawGeometric {

   /**
    * Create a cross that appears at the point with coordinates (x,y).
    */
   public Crosshair(Value x, Value y) {
      super(CROSS, x, y, 7, 7);
   }
   
   /**
    * Create a cross that appears on the graph of the function y=f(x)
    * at the point with coordinates (x,f(x)).  f should be a function
    * of one variable.
    */
   public Crosshair(Value x, Function f) {
      super(CROSS, x, new ValueMath(f,x), 7, 7);
   }
   public Crosshair(Value x, Function func1, Function func2) {
	  super(CROSS, x, new ValueMath(func1,x), new ValueMath(func2,x), 7, 7);
   }
}

