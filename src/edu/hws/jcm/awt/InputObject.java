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
 * An InputObject represents some sort of value that can be changed
 * by, for example, user interaction with a GUI element.  The value can
 * actually change only when the checkInput() method is called.  Generally,
 * an InputObject is a GUI element with an associated MathObject such as
 * a Variable or Expression.  For example, a VariableInput is a text-input
 * box where the user can enter the value of a Variable.  However, the
 * input is only checked and the value of the variable can only change
 * when the VariableInput's checkInput() method is called.  The checkInput()
 * method is generally meant to be called by a Controller object.  The
 * checkInput() method should throw a JCMError if an error occurs.
 * See the Controller class for more information.
 *
 * @author David Eck
 */
public interface InputObject extends java.io.Serializable {

   /**
    * Check and possibly change the value associated with this InputObject.
    */
   public void checkInput();
   
   /**
    * This method was introduced to provide a common interface for setting
    * a Controller that is to be notified when there is a change in the
    * InputObject.  (This was introduced late in development, to be used
    * by edu.hws.jcm.awt.JCMPanel.gatherInputs().  In all the standard
    * classes that implement the InputObject interface, this method 
    * simply calls a setOnChange or setOnUserAction method.)
    */
   public void notifyControllerOnChange(Controller c);

} // end interface InputObject
