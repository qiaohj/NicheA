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
import java.awt.*;
import java.awt.event.*;

/**
 * A compute button is a button that can have an associated Controller.
 * When the user clicks the button, the compute() method of the 
 * Controller is called.  This class really just exists for convenience.
 */
public class ComputeButton extends Button {

   private Controller onUserAction;  // The Controller whose compute()
                                     // method is called when the user clicks
                                     // the button.

   /**   
    * Create a Compute button labeled "Compute!".
    */
   public ComputeButton() {
      this("Compute!");
   }
   
   /**
    * Create a Compute button displaying the given text.
    */
   public ComputeButton(String label) {
      super(label);
      setBackground(Color.lightGray);
      enableEvents(AWTEvent.ACTION_EVENT_MASK);
   }
   
   /**
    * Set the controller whose compute() method is called
    * when the user clicks this button.
    */
   public void setOnUserAction(Controller c) {
      onUserAction = c;
   }

   /**   
    * Return the controlller whose compute() method is
    * called when the user clicks this button.
    */
   public Controller getOnUserAction() {
      return onUserAction;
   }
   
   /**
    * This is called by the system when the user clicks the
    * button.  Not meant to be called directly.
    */
   public void processActionEvent(ActionEvent evt) {
      if (onUserAction != null)
         onUserAction.compute();
      super.processActionEvent(evt);
   }   

} // end class ComputeButton

