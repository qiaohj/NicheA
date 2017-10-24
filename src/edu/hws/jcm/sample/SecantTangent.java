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


// The SecantTangent applet shows the graph of a function with two points
// marked on the function.  The tangent to the graph is drawn at the first
// of these points.  A second line is drawn between the two points.
// Both of the points can be dragged by the user.  Alternatively, the
// x-coords of the points can be typed into input boxes.

import java.awt.*;
import java.applet.Applet;
import java.util.StringTokenizer;
import edu.hws.jcm.draw.*;
import edu.hws.jcm.data.*;
import edu.hws.jcm.functions.*;
import edu.hws.jcm.awt.*;


public class SecantTangent extends GenericGraphApplet {


   private Function func;   // The function that is graphed.

   VariableInput x1Input = new VariableInput(); // x-coord where tangent is drawn.
   VariableInput x2Input = new VariableInput(); // x-coord for other point of secant.

   protected void setUpParameterDefaults() {
      parameterDefaults = new java.util.Hashtable();
      String varName = getParameter("Variable","x");
      parameterDefaults.put("Function", " e ^ " + varName);
   }

   protected void setUpCanvas() {  // Override this to add more stuff to the canvas.
   
      super.setUpCanvas();  // Do the common setup: Add the axes and

      // When setUpCanvas is called, the functionInput already exists, if one is
      // to be used, since it is created in setUpBopttomPanel(), which is called
      // before setUpCanvas.  If functionInput exists, add a graph of the function
      // from functionInput to the canvas.  If not, create a graph of the function
      // specified by the parameter named "Function".
      
      if (functionInput != null)
         func = functionInput.getFunction(xVar);
      else {
         String def = getParameter("Function");
         Function f = new SimpleFunction( parser.parse(def), xVar );
         func = new WrapperFunction(f);
      }
      Graph1D graph = new Graph1D(func);
      Color color = getColorParam("GraphColor",Color.black);
      graph.setColor(color);
         
      // Create two DraggablePoint objects, which will be the points on the canvas
      // that the user can drag.  The x-coordinate of drag1 will be tied later to
      // x1Input, so that either drag1 or x1Input can be used for setting the
      // values of the point.  Same for drag2 and x2Input.
      
      Color tangentColor = getColorParam("TangentColor", Color.red);
      Color secantColor = getColorParam("SecantColor",new Color(0,200,0));
      
      DraggablePoint drag1 = new DraggablePoint();  // point where tangent is drawn
      DraggablePoint drag2 = new DraggablePoint();  // other point on secant line
      
      drag1.clampY(func);   // Both points are clamped to move along the function.
      drag2.clampY(func);
      
      drag1.setColor(tangentColor);  
      drag1.setGhostColor(lighten(tangentColor)); 
      
      drag2.setColor(secantColor);    
      drag2.setGhostColor(lighten(secantColor)); 


      // Create the tangent line and the secant line.  

      DrawGeometric secant = new DrawGeometric(DrawGeometric.INFINITE_LINE_ABSOLUTE,
                                      drag1.getXVar(), drag1.getYVar(), 
                                      drag2.getXVar(), drag2.getYVar());      
      secant.setColor(secantColor);
                                      
      TangentLine tangent = new TangentLine(drag1.getXVar(), func);
      tangent.setColor(tangentColor);      
      
      canvas.add(drag1);
      canvas.add(drag2);
      canvas.add(tangent);
      canvas.add(secant);
      canvas.add(graph);
      
      // Create a DrawString to display the slopes of the tangent and secant.
      
      Value tangentSlope = new ValueMath(func.derivative(1), drag1.getXVar());
      Value secantSlope = new ValueMath( new ValueMath(drag2.getYVar(), drag1.getYVar(), '-'),
                                         new ValueMath(drag2.getXVar(), drag1.getXVar(), '-'),
                                         '/');
      
      DrawString info;
      if ( "no".equalsIgnoreCase(getParameter("ShowTangentSlope","yes")) ) {        
         info = new DrawString( "Secant Slope = #", 
                              DrawString.TOP_LEFT,
                              new Value[] { secantSlope } );
      }
      else {
         info = new DrawString( "Secant Slope = #\nTangent Slope = #", 
                              DrawString.TOP_LEFT,
                              new Value[] { secantSlope, tangentSlope } );
      }
      info.setFont(new Font("Monospaced",Font.PLAIN,10));
      info.setNumSize(7);
      info.setColor(getColorParam("SlopeTextColor", Color.black));
      info.setBackgroundColor(getColorParam("SlopeTextBackground",Color.white));
      info.setFrameWidth(1);
      canvas.add(info);
      
      // Create input boxes and add them to the bottom of the applet
      
      Panel xIn = new Panel();  // not a JCMPanel, since I don't want the input boxes to be controlled
                                // by the main controller anyway.
      xIn.setBackground(getColorParam("PanelColor",Color.lightGray));
      xIn.setLayout(new GridLayout(1,4,3,3));
      xIn.add(new Label("Tangent at " + xVar.getName() + " = ", Label.RIGHT));
      xIn.add(x1Input);
      xIn.add(new Label("Secant to  " + xVar.getName() + " = ", Label.RIGHT));
      xIn.add(x2Input);
      
      // Put the inputs at the bottom of the inputPanel, if there is one, otherwise
      // at the bottom of the mainPanel
      if (inputPanel == null)
         mainPanel.add(xIn, BorderLayout.SOUTH);
      else
         inputPanel.add(xIn, BorderLayout.SOUTH);
      

      // Set up controllers.  I want to arrange things so that the controls that position the
      // two points on the graph do not cause the graph to be recomputted when they are changed.

      Controller dragControl = new Controller();  // A controller to respond to dragging
      mainController.remove(canvas);
      mainController.add(graph);
      mainController.add(dragControl);  // Things in dragController should be recomputed when graph is changed

      dragControl.add(x1Input);  // dragControl checks the contents of the x-inputs
      dragControl.add(x2Input);  //    and recomputes everything except the graph.
      dragControl.add(drag1);
      dragControl.add(drag2);
      dragControl.add(tangent);
      dragControl.add(secant);
      dragControl.add(info);

      drag1.setOnUserAction(dragControl);     // dragControl.compute() is called when the
      drag2.setOnUserAction(dragControl);     //    user drags one of the points or types
      x1Input.setOnTextChange(dragControl);   //    in one of the x-input boxes.
      x2Input.setOnTextChange(dragControl);

      
      // By adding Tie's to dragControll, we make sure that the positions of the
      // draggable points are synchronized with the contents of the x-input boxes.

      dragControl.add(new Tie((Tieable)drag1.getXVar(), x1Input));
      dragControl.add(new Tie((Tieable)drag2.getXVar(), x2Input));
      
      // Get initial values for draggable points
      
      double[] d1 = getNumericParam("X1");
      double x1 = (d1 != null && d1.length == 1)? d1[0] : 0;
      x1Input.setVal(x1);
      drag1.setLocation(x1,0);  // y-value will be changed to make the point lie on the curve
      double[] d2 = getNumericParam("X2");
      double x2 = (d2 != null && d2.length == 1)? d2[0] : 1;
      x2Input.setVal(x2);
      drag2.setLocation(x2,0);

   } // end setUpCanvas()
   

   private Color lighten(Color c) { // for making "Ghost" color of draggable point
      int r = c.getRed();
      int g = c.getGreen();
      int b = c.getBlue();
      int nr, ng, nb;
      if (r <= 200 || g <= 200 || b <= 200) {
         nb = 255 - (255 - b) / 3;
         ng = 255 - (255 - g) / 3;
         nr = 255 - (255 - r) / 3;
      }
      else { 
         nb = b / 2;
         ng = g / 2;
         nr = r / 2; 
      }
      return new Color(nr,ng,nb);
   }



   protected void doLoadExample(String example) {
         // This method is called when the user loads an example from the 
         // example menu (if there is one).  It overrides an empty method
         // in GenericGraphApplet.
         //   For the SecantTangent applet, the example string should contain
         // an expression that defines the function to be graphed.  This can optionally
         // be followed by a semicoloon and a list of fourto six numbers.
         // The first four numbers give the x- and y-limits to be used for the
         // example.  If they are not present, then -5,5,-5,5 is used.  The
         // fifth number, if present, gives the x-coord where the tangent line
         // is drawn.  The sixth number gives the x-coord of the second point
         // on the secant line.
   
      int pos = example.indexOf(";");

      double[] limits = { -5,5,-5,5 };  // x- and y-limits to use
      
      if (pos > 0) { // get limits from example text
         String limitsText = example.substring(pos+1);
         example = example.substring(0,pos);
         StringTokenizer toks = new StringTokenizer(limitsText, " ,");
         if (toks.countTokens() >= 4) {
            for (int i = 0; i < 4; i++) {
               try {
                   Double d = new Double(toks.nextToken());
                   limits[i] = d.doubleValue();
               }
               catch (NumberFormatException e) {
               }
            }
            if (toks.countTokens() > 0) { // Get point for tangent line
               try {
                   Double d = new Double(toks.nextToken());
                   x1Input.setVal( d.doubleValue() );
               }
               catch (NumberFormatException e) {
               }
            }
            if (toks.countTokens() > 0) { // Get other point for secant line
               try {
                   Double d = new Double(toks.nextToken());
                   x2Input.setVal( d.doubleValue() );
               }
               catch (NumberFormatException e) {
               }
            }
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
   


} // end class SimpleGraph
