package edu.hws.jcm.sample;
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


import java.awt.*;
import java.applet.Applet;
import java.util.StringTokenizer;
import edu.hws.jcm.draw.*;
import edu.hws.jcm.data.*;
import edu.hws.jcm.functions.*;
import edu.hws.jcm.awt.*;

/**
 *  An applet for exploring the epsilon-delta definition of a limit.
 */
public class EpsilonDelta extends GenericGraphApplet {

   // Declare some private variables that are created in one method in
   // this class and used in a second method.

   private VariableInput xInput;
   private VariableInput epsilonInput;
   private VariableInput deltaInput;
   private VariableInput limitInput;
   
   private VariableSlider xSlider;
   private VariableSlider epsilonSlider;
   private VariableSlider deltaSlider;
   private VariableSlider limitSlider;

   private Controller subController;
   
   private Variable xValue, limitValue;

   private Function func;   // The function that is graphed.
   private Graph1D graph;   // The graph of the function.
   

   protected void setUpBottomPanel() {
      super.setUpBottomPanel();
      subController = new Controller();
      mainController.add(subController);
      JCMPanel inputs = new JCMPanel(3);
      subController.add(inputs);
      inputs.setBackground(getColorParam("PanelBackground", Color.lightGray));
      if (inputPanel == null)
         mainPanel.add(inputs,BorderLayout.SOUTH);
      else
         inputPanel.add(inputs,BorderLayout.SOUTH);
      JCMPanel left = new JCMPanel(0,1,2);
      JCMPanel right = new JCMPanel(0,1,2);
      JCMPanel middle = new JCMPanel(0,1,2);
      inputs.add(middle, BorderLayout.CENTER);
      inputs.add(left, BorderLayout.WEST);
      inputs.add(right, BorderLayout.EAST);
      double[] a = getNumericParam("AValue");
      double avalue = (a == null || a.length < 1)? 0 : a[0];
      if ("yes".equalsIgnoreCase(getParameter("UseAInput","yes"))) {
         xSlider = new VariableSlider();
         xInput = new VariableInput();
         xInput.setVal(avalue);
         xSlider.setVal(avalue);
         xInput.setThrowErrors(false);
         subController.add(new Tie(xSlider, xInput));
         xValue = xInput.getVariable();
         left.add(new Label("limit at a = ",Label.RIGHT));
         middle.add(xSlider);
         right.add(xInput);
      }
      else {
         xValue = new Variable();
         xValue.setVal(avalue);
      }
      a = getNumericParam("LimitValue");
      double Lvalue = (a == null || a.length < 1)? 1 : a[0];
      if ("yes".equalsIgnoreCase(getParameter("UseLimitInput","yes"))) {
         limitSlider = new VariableSlider();
         limitInput = new VariableInput();
         limitInput.setVal(Lvalue);
         limitSlider.setVal(Lvalue);
         limitInput.setThrowErrors(false);
         subController.add(new Tie(limitSlider, limitInput));
         limitValue = limitInput.getVariable();
         left.add(new Label(" test limit L = ",Label.RIGHT));
         middle.add(limitSlider);
         right.add(limitInput);
      }
      else {
         limitValue = new Variable();
         limitValue.setVal(Lvalue);
      }
      a = getNumericParam("EpsilonValue");
      double epsilonValue = (a == null || a.length < 1)? 0.25 : a[0];
      epsilonSlider = new VariableSlider(new Constant(0), new Constant(2));
      epsilonInput = new VariableInput();
      epsilonInput.setVal(epsilonValue);
      epsilonSlider.setVal(epsilonValue);
      epsilonInput.setThrowErrors(false);
      subController.add(new Tie(epsilonSlider, epsilonInput));
      left.add(new Label("epsilon = ", Label.RIGHT));
      middle.add(epsilonSlider);
      right.add(epsilonInput);
      a = getNumericParam("DeltaValue");
      double deltaValue = (a == null || a.length < 1)? 1 : a[0];
      deltaSlider = new VariableSlider(new Constant(0), new Constant(2));
      deltaInput = new VariableInput();
      deltaInput.setVal(deltaValue);
      deltaSlider.setVal(deltaValue);
      deltaInput.setThrowErrors(false);
      subController.add(new Tie(deltaSlider, deltaInput));
      left.add(new Label("delta = ", Label.RIGHT));
      middle.add(deltaSlider);
      right.add(deltaInput);
   }   


   protected void setUpCanvas() {  // Override this to add more stuff to the canvas.
   
      // When setUpCanvas is called, the functionInput already exists, if one is
      // to be used, since it is created in setUpBopttomPanel(), which is called
      // before setUpCanvas.  If functionInput exists, add a graph of the function
      // from functionInput to the canvas.  If not, create a graph of the function
      // specified by the parameter named "Function".
      
      if (functionInput != null)
         func = functionInput.getFunction(xVar);
      else {
         String def = getParameter("Function", "abs(" + xVar.getName() + ") ^ " + xVar.getName());
         Function f = new SimpleFunction( parser.parse(def), xVar );
         func = new WrapperFunction(f);
      }
      graph = new Graph1D(func);
      graph.setColor(getColorParam("GraphColor", Color.black));
         
      Value xMinusDelta = new ValueMath(xValue, deltaInput, '-');
      Value xPlusDelta  = new ValueMath(xValue, deltaInput, '+');
      Value limitMinusEpsilon = new ValueMath(limitValue, epsilonInput, '-');
      Value limitPlusEpsilon  = new ValueMath(limitValue, epsilonInput, '+');
      
      Value xmin = canvas.getCoordinateRect().getValueObject(CoordinateRect.XMIN);
      Value xmax = canvas.getCoordinateRect().getValueObject(CoordinateRect.XMAX);
      Value ymin = canvas.getCoordinateRect().getValueObject(CoordinateRect.YMIN);
      Value ymax = canvas.getCoordinateRect().getValueObject(CoordinateRect.YMAX);
      
      if (xSlider != null) {
         xSlider.setMin(xmin);
         xSlider.setMax(xmax);
      }
      if (limitSlider != null) {
         limitSlider.setMin(ymin);
         limitSlider.setMax(ymax);
      }
      
      DrawGeometric deltaBox = new DrawGeometric(DrawGeometric.RECT_ABSOLUTE, xMinusDelta, ymin, xPlusDelta, ymax);
      deltaBox.setFillColor(new Color(225,255,225));
      deltaBox.setLineWidth(0);
      DrawGeometric epsilonBox = new DrawGeometric(DrawGeometric.RECT_ABSOLUTE, xmin, limitMinusEpsilon, xmax, limitPlusEpsilon);
      epsilonBox.setFillColor(new Color(255,230,230));
      epsilonBox.setLineWidth(0);
      DrawGeometric overlap = new DrawGeometric(DrawGeometric.RECT_ABSOLUTE, xMinusDelta, limitMinusEpsilon, xPlusDelta,limitPlusEpsilon);
      overlap.setFillColor(new Color(255,255,225));
      overlap.setColor(Color.yellow);
      DrawGeometric xLine = new DrawGeometric(DrawGeometric.LINE_ABSOLUTE, xValue, ymin, xValue, ymax);
      xLine.setColor(new Color(130,255,130));
      DrawGeometric limitLine = new DrawGeometric(DrawGeometric.LINE_ABSOLUTE, xmin, limitValue, xmax, limitValue);
      limitLine.setColor(new Color(255,150,150));
      
      canvas.add(deltaBox);
      canvas.add(epsilonBox);
      canvas.add(overlap);
      canvas.add(xLine);
      canvas.add(limitLine);
      
      DrawString ds = new DrawString("a = #\nL = #\nf(a) = #", DrawString.TOP_LEFT,
                               new Value[] { xValue, limitValue, new ValueMath(func,xValue) });
      ds.setBackgroundColor(Color.white);
      ds.setFrameWidth(1);
      
      subController.add(ds);
      subController.add(deltaBox);
      subController.add(epsilonBox);
      subController.add(overlap);
      subController.add(xLine);
      subController.add(limitLine);
      mainController.remove(canvas);
      mainController.add(graph);
      canvas.getCoordinateRect().setOnChange(mainController);
      
      deltaSlider.setOnUserAction(subController);
      epsilonSlider.setOnUserAction(subController);
      deltaInput.setOnTextChange(subController);
      epsilonInput.setOnTextChange(subController);
      subController.add(deltaSlider);
      subController.add(epsilonSlider);
      subController.add(deltaInput);
      subController.add(epsilonInput);
      if (xInput != null) {
         xSlider.setOnUserAction(subController);
         xInput.setOnTextChange(subController);
         subController.add(xSlider);
         subController.add(xInput);
      }
      if (limitInput != null) {
         limitSlider.setOnUserAction(subController);
         limitInput.setOnTextChange(subController);
         subController.add(limitSlider);
         subController.add(limitInput);
      }

      super.setUpCanvas();  // Do the common setup: Add the axes, grid, etc

      canvas.add(graph);
      canvas.add(ds);
      
   } // end setUpCanvas()
   
   

   


   protected void doLoadExample(String example) {
         // This method is called when the user loads an example from the 
         // example menu (if there is one).  It overrides an empty method
         // in GenericGraphApplet.
   
         // After the function definition, there can be a semicolon and
         // up to ten numbers (numbers can be separated by spaces and/or commas).
         // The first four numbers specify the limits on the coordinate rect.
         // .
   
      int pos = example.indexOf(";");

      double[] limits = { -5,5,-5,5 };  // x- and y-limits to use
      
      if (pos > 0) { // get limits from example text
         String limitsText = example.substring(pos+1);
         example = example.substring(0,pos);
         StringTokenizer toks = new StringTokenizer(limitsText, " ,");
         double nums[] = new double[toks.countTokens()];
         for (int i = 0; i < nums.length; i++) {
            try {
               nums[i] = (new Double(toks.nextToken())).doubleValue();
            }
            catch (Exception e) {
               nums[i] = Double.NaN;
            }
         }
         for (int i = 0; i < 4; i++)
            if (nums.length >= i && !Double.isNaN(nums[i]))
               limits[i] = nums[i];
         if (nums.length > 4 && !Double.isNaN(nums[4]))
             xValue.setVal( nums[4] );
         else
             xValue.setVal((limits[0]+limits[1])/2);
         if (nums.length > 5 && !Double.isNaN(nums[5]))
             limitValue.setVal( nums[5] );
         else
             limitValue.setVal((limits[0]+limits[1])/2);
         if (nums.length > 8 && !Double.isNaN(nums[8]))
             epsilonSlider.setMax( new Constant(nums[8]) );
         else
             epsilonSlider.setMax(new Constant(Math.abs(limits[2]-limits[3])/2));
         if (nums.length > 9 && !Double.isNaN(nums[9]))
             deltaSlider.setMax( new Constant(nums[9]) );
         else
             deltaSlider.setMax(new Constant(Math.abs(limits[0]-limits[1])/2));
         if (nums.length > 6 && !Double.isNaN(nums[6])) {
             epsilonInput.setVal( nums[6] );
             epsilonSlider.setVal( nums[6] );
         }
         else {
             epsilonInput.setVal(Math.abs(limits[2]-limits[3])/8);
             epsilonSlider.setVal(Math.abs(limits[2]-limits[3])/8);
         }
         if (nums.length > 7 && !Double.isNaN(nums[7])) {
             deltaInput.setVal( nums[7] );
             deltaSlider.setVal( nums[7] );
         }
         else {
             deltaInput.setVal(Math.abs(limits[0]-limits[1])/8);
             deltaSlider.setVal(Math.abs(limits[0]-limits[1])/8);
         }
      }
      
      // Set up the example data and recompute everything.

      if (functionInput != null) {
            // If there is a function input box, put the example text in it.
         functionInput.setText(example);
      }
      else { 
           // If there is no user input, set the function in the graph directly.
           // Also, in this case, func is a "WrapperFunction".  Set the
           // definition of that WrapperFunction to be the same as f
         try {
            Function f = new SimpleFunction( parser.parse(example), xVar );
            ((WrapperFunction)func).setFunction(f);
         }
         catch (ParseError e) {  
             // There should't be parse error's in the Web-page
             // author's examples!  If there are, the function
             // just won't change.
         }
      }
      CoordinateRect coords = canvas.getCoordinateRect(0);
      coords.setLimits(limits);
      coords.setRestoreBuffer();
      mainController.compute();
      
   } // end doLoadExample()
   

} // end class EpsilonDelta
