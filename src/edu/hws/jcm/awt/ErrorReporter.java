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
 * To allow different styles of reporting errors, a
 * Controller uses an ErrorReporter to report any
 * errors that are thrown during its checkInput/compute
 * cycle.  The DisplayCanvas and MessagePopup classes
 * implement this interface.
 *
 * @author David Eck
 */
public interface ErrorReporter {

   /**
    * Report the specifed message as an error.  If source is non-null,
    * then it is the Controller that called this routine.  In that case,
    * if the error reporter is capable of clearing its own error
    * condition, it should call source.errorCleared() when it does so.
    *
    * @param source Controller that called this method (if non-null).
    * @param message error message to report.
    */
   public void setErrorMessage(Controller source, String message);

   /**
    * Clear the error reprort, if there is one.
    */
   public void clearErrorMessage();

   /**
    * Get the error message that is currently being displayed, or
    * return null if there is no error message.
    */
   public String getErrorMessage();
   
}

