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
import java.awt.event.MouseEvent;

/**
 *  An interface that can be implemented by an object that can be dragged
 *  with the mouse.
 */
public interface Draggable {

   /**
    *  Tell the object that a drag operation might be beginning.
    *  The Draggable object can decide whether it really wants
    *  to be dragged, based on the MouseEvent.  It should return
    *  true to indicate that a drag should really be started, and
    *  false if it wants to ignore the MouseEvent.
    */
   public boolean startDrag(MouseEvent evt);
   
   /**
    *  Continue a drag that was started in startDrag().  Presumably
    *  the event is a mouseDragged event.
    */
   public void continueDrag(MouseEvent evt);
   
   /**
    *  Finish a draw that was started in startDrag().  Presumably
    *  the event is a mouseReleased event.
    */
   public void finishDrag(MouseEvent evt);

}
