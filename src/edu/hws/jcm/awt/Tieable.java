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
 * A Tieable object has an associated serial number.  The value of the serial
 * should increase when the value of the object changes.  A Tieable can "sync" with another
 * Tieable, presumably by copying its serial number and other information.  
 * A given Tieable might only be able to synchronize with other Tiebles of
 * certain types.  If its sync() method is called with an object of the wrong 
 * type, it should probably thrown an IllegalArguemntException. 
 *
 * See the "Tie" and "Controller" classes for information about how Tieable 
 * are used. 
 *
 */
public interface Tieable extends java.io.Serializable {

   /**
    * Get the serial number associated with this Tieable.  If the
    * value of this Tieable changes, then the serial number should
    * increase.
    */
   public long getSerialNumber();

   /**      
    * This routine is called to tell this Tieable that the serial
    * numbers of the Tieables that have been added to the Tie do not
    * match.  newest has a serial number that is at least as
    * large as the serial number of any other Tieable in the Tie.
    * This Tieable should synchronize its value and serial number
    * with the "newest" Tieables.
    *    (Note:  So far, I haven't found any reason to use
    * the Tie parameter in this method!  Maybe it should be removed.)
    */
   public void sync(Tie tie, Tieable newest);

}
