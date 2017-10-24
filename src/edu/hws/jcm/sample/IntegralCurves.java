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


// This applet displays a vector field (f1(x,y),f2(x,y)) and integral curves
// for that vector field (although the integral curve feature can be turned off
// with an applet param).  The drawing of the curves is animated; they are
// drawn segment-by-segment.  In the default setup, a curve is started when the 
// user clicks on the canvas.  A curve can also be started by entering the
// starting x and y coords in a pair of text input boxes and clicking a button.

import java.awt.*;
import java.awt.event.*;
import java.applet.Applet;
import java.util.*;
import edu.hws.jcm.draw.*;
import edu.hws.jcm.data.*;
import edu.hws.jcm.functions.*;
import edu.hws.jcm.awt.*;



public class IntegralCurves extends GenericGraphApplet {

   private Variable yVar;           // The seond variable, usually y.
   private Function xFunc,yFunc;    // The functions that give the components of the vector field
   private ExpressionInput functionInput2;  // For inputting yFunc.
   private VectorField field;       // The vector/direction field

   private Animator animator;       // for incrementally drawing integral curves.
   private Vector curves = new Vector();           // Holds the integral curves

   private VariableInput deltaT;    // input the deltaT for the curve
   double dt = 0.1;  // The value of delat t in the case where there is no deltaT input box
   private VariableInput xStart,yStart;  // Starting point for curve
   private Choice methodChoice;     // select integration method
   private Button startCurveButton; // user clicks to start curve from (x,y) in xStart, yStart input boxes
   private Button clearButton;      // clears curves
   private Color curveColor;        // color for integral curves
   private Draw curveDrawer = new Draw(); // A DrawTemp object that draws one segment of the integral curves.
   
   private double[] nextPoint = new double[2];  // Help in computing next point of integral curve.
   private double[] params = new double[2];     // ditto
   
   private static final int RK4 = 0, RK2 = 1, EULER = 2;   // constants for integration methos
   

   private class Curve {  // holds the data for one integral curve
      double dt;
      int method;
      double x,y; // point on the curve
      double lastX = Double.NaN, lastY; // previous point, so we can draw a line.
   }
   
   private class Draw implements DrawTemp { // For drawing the next segment in each integral curve (as a DrawTemp)
      public void draw(Graphics g, CoordinateRect coords) {
         int size = curves.size();
         g.setColor(curveColor);
         for (int i = 0; i < size; i++) {
            Curve c = (Curve)(curves.elementAt(i));
            if (! (Double.isNaN(c.x) || Double.isNaN(c.y) || Double.isNaN(c.lastX) || Double.isNaN(c.lastY)) ) {
               int x1 = coords.xToPixel(c.lastX);
               int y1 = coords.yToPixel(c.lastY);
               int x2 = coords.xToPixel(c.x);
               int y2 = coords.yToPixel(c.y);
               g.drawLine(x1,y1,x2,y2);
            }
         }
      }
   }

   protected void setUpParser() {
          // create the "y" variable; also set up some parameter defaults.
      yVar = new Variable(getParameter("Variable2","y"));
      parser.add(yVar);
      super.setUpParser();  // sets up xVar, among other things.
      parameterDefaults = new Hashtable();
      parameterDefaults.put("FunctionLabel", " f1(" + xVar.getName() + "," + yVar.getName() + ") = ");
      parameterDefaults.put("FunctionLabel2", " f2(" + xVar.getName() + "," + yVar.getName() + ") = ");
      parameterDefaults.put("Function", " " + yVar.getName() + " - 0.1*" + xVar.getName());
      parameterDefaults.put("Function2", " - " + xVar.getName() + " - 0.1*" + yVar.getName());
      defaultFrameSize = new int[] { 580, 440 };
   }

   protected void setUpCanvas() {  // Override this to add more stuff to the canvas.
   
      super.setUpCanvas();  // Do the common setup: Add the axes and
      
      // set up the vector field and add it to the canvas
      
      if (functionInput != null) {
         xFunc = functionInput.getFunction(new Variable[] {xVar,yVar});
         yFunc = functionInput2.getFunction(new Variable[] {xVar,yVar});
      }
      else {
         String xFuncDef = getParameter("Function");
         String yFuncDef = getParameter("Function2");
         Function f = new SimpleFunction( parser.parse(xFuncDef), new Variable[] {xVar,yVar} );
         xFunc = new WrapperFunction(f);
         f = new SimpleFunction( parser.parse(yFuncDef), new Variable[] {xVar,yVar} );
         yFunc = new WrapperFunction(f);
      }
      String type = (getParameter("VectorStyle", "") + "A").toUpperCase();
      int style = 0;
      switch (type.charAt(0)) {
         case 'A': style = VectorField.ARROWS; break;
         case 'L': style = VectorField.LINES; break;
         case 'S': style = VectorField.SCALED_VECTORS; break;
      }
      field = new VectorField(xFunc,yFunc,style);
      Color color = getColorParam("VectorColor");
      if (color != null)
         field.setColor(color);
      int space = (style == VectorField.LINES)? 20 : 30;
      double[] d = getNumericParam("VectorSpacing");
      if (d != null && d.length > 0 && d[0] >= 1)
         space = (int)Math.round(d[0]);
      field.setPixelSpacing(space);

      canvas.add(field);  // Finally, add the graph to the canvas.

      curveColor = getColorParam("CurveColor", Color.magenta);

      // add a mouse listener to the canvas for starting curves.

      if ("yes".equalsIgnoreCase(getParameter("MouseStartsCurves","yes")) && "yes".equalsIgnoreCase(getParameter("DoCurves","yes")))
         canvas.addMouseListener(new MouseAdapter() {
                public void mousePressed(MouseEvent evt) {
                   CoordinateRect coords = canvas.getCoordinateRect();
                   double x = coords.pixelToX(evt.getX());
                   double y = coords.pixelToY(evt.getY());
                   if (xStart != null)
                      xStart.setVal(x);
                   if (yStart != null)
                      yStart.setVal(y);
                   startCurve(x,y);
                }
            });

   } // end setUpCanvas()
   

   
   protected void setUpBottomPanel() { 
         // Override this to make a panel containing controls.  This is complicated
         // because it's possible to turn off a lot of the inputs with applet params.
         
      // Check on the value of delta t, which has to be set even if there are no input controls.
      double[] DT = getNumericParam("DeltaT"); 
      if  ( ! (DT == null || DT.length == 0 || DT[0] <= 0) )
         dt = DT[0];

      boolean doCurves = "yes".equalsIgnoreCase(getParameter("DoCurves","yes"));
      boolean useInputs = "yes".equalsIgnoreCase(getParameter("UseFunctionInput","yes"));
      if (!doCurves && !useInputs)  // no input controls at all.
         return;

      // make the input panel

      inputPanel = new JCMPanel();
      inputPanel.setBackground( getColorParam("PanelBackground", Color.lightGray) );
      mainPanel.add(inputPanel,BorderLayout.SOUTH);

      // Make the function inputs and the compute button, if these are in the configuration.

      JCMPanel in1 = null, in2 = null;  // hold function inputs, if any
      if (useInputs) {
         if ( "yes".equalsIgnoreCase(getParameter("UseComputeButton", "yes")) ) {
            String cname = getParameter("ComputeButtonName", "New Functions");
            computeButton = new Button(cname);
            computeButton.addActionListener(this);
         }
          functionInput = new ExpressionInput(getParameter("Function"),parser);
          in1 = new JCMPanel();
          in1.add(functionInput,BorderLayout.CENTER);
          in1.add(new Label(getParameter("FunctionLabel")), BorderLayout.WEST);
          functionInput.setOnUserAction(mainController);
          functionInput2 = new ExpressionInput(getParameter("Function2"),parser);
          in2 = new JCMPanel();
          in2.add(functionInput2,BorderLayout.CENTER);
          in2.add(new Label(getParameter("FunctionLabel2")), BorderLayout.WEST);
          functionInput2.setOnUserAction(mainController);
      }
      
      // If we're not doing curves, all we have to do is put the function inputs in the inputPanel
      
      if (!doCurves) {  
         Panel p = new JCMPanel(2,1,3);
         p.add(in1);
         p.add(in2);
         inputPanel.add(p, BorderLayout.CENTER);
         if (computeButton != null)
            inputPanel.add(computeButton,BorderLayout.EAST);
         return;
      }
      
      // Now we know that doCurves is true.  First, make the animator and clear button

      animator = new Animator(Animator.STOP_BUTTON);
      animator.setStopButtonName("Stop Curves");
      animator.setOnChange(new Computable() { // animator drives curves
           public void compute() {
              extendCurves();
           }
        });
      mainController.add(new InputObject() { // curves must stop if main controller is triggered
            public void checkInput() {
               curves.setSize(0);
               animator.stop();
            }
            public void notifyControllerOnChange(Controller c) {
            }
        });
      clearButton = new Button("Clear");
      clearButton.addActionListener(this);

      // Make a panel to contain the xStart and yStart inputs, if they are in the configuration.

      Panel bottom = null;
      if ("yes".equalsIgnoreCase(getParameter("UseStartInputs","yes"))) {
         xStart = new VariableInput();
         xStart.addActionListener(this);
         yStart = new VariableInput();
         yStart.addActionListener(this);
         bottom = new Panel();  // not a JCMPanel -- I don't want their contents checked automatically
         startCurveButton = new Button("Start curve at:");
         startCurveButton.addActionListener(this);
         bottom.add(startCurveButton);
         bottom.add(new Label(xVar.getName() + " ="));
         bottom.add(xStart);
         bottom.add(new Label(yVar.getName() + " ="));
         bottom.add(yStart);
      }
      
      // Now, make a panel to contain the methodChoice and deltaT input if they are in the configuration.
      // The animator and clear button will be added to this panel if it exists.  If not, and if
      // an xStart/yStart panel exists, then it will be added there.  If neither exists,
      // it goes in its own panel.  The variable bottom ends up pointing to a panel that
      // contains all the curve controls.
      
      boolean useChoice = "yes".equalsIgnoreCase(getParameter("UseMethodChoice","yes"));
      boolean useDelta = "yes".equalsIgnoreCase(getParameter("UseDeltaInput","yes"));
      if (useChoice || useDelta) {
         Panel top = new Panel();  // not a JCMPanel!
         if (useDelta) {
            top.add(new Label("dt ="));
            deltaT = new VariableInput(null,""+dt);
            top.add(deltaT);
         }
         if (useChoice) {
            top.add(new Label("Method:"));
            methodChoice = new Choice();
            methodChoice.add("Runge-Kutta 4");
            methodChoice.add("Runge-Kutta 2");
            methodChoice.add("Euler");
            top.add(methodChoice);
         }
         top.add(animator);
         top.add(clearButton);
         if (bottom == null)
            bottom = top;
         else {
            Panel p = new Panel();
            p.setLayout(new BorderLayout());
            p.add(top, BorderLayout.NORTH);
            p.add(bottom, BorderLayout.CENTER);
            bottom = p;
         }
      }
      else  {
         if (bottom == null)
            bottom = new Panel();
         bottom.add(animator);
         bottom.add(clearButton);
      }
      
      // Add the panels "bottom" to the inputPanel, and ruturn
      // if there are no function inputs.
      
      inputPanel.add(bottom, BorderLayout.CENTER);
      if (in1 == null)
         return;

      // Add the function inputs and compute button to the inputPanel         
         
      Panel in = new JCMPanel(1,2);
      in.add(in1);
      in.add(in2);
      if (computeButton != null) {
         Panel p = new JCMPanel();
         p.add(in,BorderLayout.CENTER);
         p.add(computeButton,BorderLayout.EAST);
         in = p;
      }
      inputPanel.add(in,BorderLayout.NORTH);
      
   } // end setUpBottomPanel()


   public void actionPerformed(ActionEvent evt) {
         // React if user presses return in xStart or yStart, or pass evt on to GenericGraphApplet
      Object src = evt.getSource();
      if (src == clearButton) {
          canvas.clearErrorMessage();
          curves.setSize(0);
          animator.stop();
          canvas.compute();  // force recompute of off-screen canvas!
      }
      else if (src == xStart || src == yStart || src == startCurveButton) {
              // Start a curve from x and y values in xStart and yStart
         canvas.clearErrorMessage();
         double x=0, y=0;
         try {
            xStart.checkInput();
            x = xStart.getVal();
            yStart.checkInput();
            y = yStart.getVal();
            startCurve(x,y);
            if (deltaT != null) {
               deltaT.checkInput();
               dt = deltaT.getVal();
               if (dt <= 0) {
                  deltaT.requestFocus();
                  throw new JCMError("dt must be positive", deltaT);
               }
            }
         }
         catch (JCMError e) {
            curves.setSize(0);
            animator.stop();
            canvas.setErrorMessage(null,"Illegal Data For Curve.  " + e.getMessage());
         }
      }
      else
         super.actionPerformed(evt);
   } // end actionPerfromed
   
   public void startCurve(double x, double y) {
        // Start an integral curve at the point (x,y)
      synchronized (curves) {
         if (deltaT != null) {
            try {
               deltaT.checkInput();
               dt = deltaT.getVal();
               if (dt <= 0) {
                  deltaT.requestFocus();
                  throw new JCMError("dt must be positive", deltaT);
               }  
            }
            catch (JCMError e) {
               curves.setSize(0);
               animator.stop();
               canvas.setErrorMessage(null,"Illegal Data For Curve.  " + e.getMessage());
               return;
            }
         }
         Curve c = new Curve();
         c.dt = dt;
         int method = (methodChoice == null)? RK4 : methodChoice.getSelectedIndex();
         c.method = method;
         c.x = x;
         c.y = y;
         curves.addElement(c);
         animator.start();
      }
   }
   
   public void extendCurves() {
         // Add the next segment to each integral curve.  This function
         // is called repeatedly by the animator.
      synchronized(curves) {
         if (canvas == null || canvas.getCoordinateRect() == null)  // can happen when frame closes
            return;  
         while (canvas.getCoordinateRect().getWidth() <= 0) {
               // need this at startup to make sure that the canvas has appeared on the screen
             try {
                Thread.sleep(200);
             }
             catch (InterruptedException e) {
             }
         }
         int size = curves.size();
         for (int i = 0; i < size; i++) {
            Curve curve = (Curve)curves.elementAt(i);
            curve.lastX = curve.x;
            curve.lastY = curve.y;
            nextPoint(curve.x, curve.y, curve.dt, curve.method);
            curve.x = nextPoint[0];
            curve.y = nextPoint[1];
         }
         CoordinateRect c = canvas.getCoordinateRect();
         double pixelWidthLimit = 100000*c.getPixelWidth();
         double pixelHeightLimit = 100000*c.getPixelHeight();
         for (int i = size-1; i >= 0; i--) {
            Curve curve = (Curve)curves.elementAt(i);
            if (Double.isNaN(curve.x) || Double.isNaN(curve.y) ||
                    Math.abs(curve.x) > pixelWidthLimit || 
                    Math.abs(curve.y) > pixelWidthLimit) // stop processing this curve
               curves.removeElementAt(i);
         }
         if (curves.size() > 0)
            canvas.drawTemp(curveDrawer);
         else {
            animator.stop();
         }
      }
   }

   private void nextPoint(double x, double y, double dt, int method) {
         // Find next point from (x,y) by applying specified method over time interval dt
      switch (method) {
         case EULER:
            nextEuler(x,y,dt);
            break;
         case RK2:
            nextRK2(x,y,dt);
            break;
         case RK4:
            nextRK4(x,y,dt);
            break;
      }
   }
   
   private void nextEuler(double x, double y, double dt) {
      params[0] = x;
      params[1] = y;
      double dx = xFunc.getVal(params);
      double dy = yFunc.getVal(params);
      nextPoint[0] = x + dt*dx;
      nextPoint[1] = y + dt*dy;
   }
   
   private void nextRK2(double x, double y, double dt) {
      params[0] = x;
      params[1] = y;
      double dx1 = xFunc.getVal(params);
      double dy1 = yFunc.getVal(params);
      double x2 = x + dt*dx1;
      double y2 = y + dt*dy1;
      params[0] = x2;
      params[1] = y2;
      double dx2 = xFunc.getVal(params);
      double dy2 = yFunc.getVal(params);
      nextPoint[0] = x + 0.5*dt*(dx1+dx2);
      nextPoint[1] = y + 0.5*dt*(dy1+dy2);
   }
   
   private void nextRK4(double x, double y, double dt) {
      params[0] = x;
      params[1] = y;
      double dx1 = xFunc.getVal(params);
      double dy1 = yFunc.getVal(params);
      
      double x2 = x + 0.5*dt*dx1;
      double y2 = y + 0.5*dt*dy1;
      params[0] = x2;
      params[1] = y2;
      double dx2 = xFunc.getVal(params);
      double dy2 = yFunc.getVal(params);
      
      double x3 = x + 0.5*dt*dx2;
      double y3 = y + 0.5*dt*dy2;
      params[0] = x3;
      params[1] = y3;
      double dx3 = xFunc.getVal(params);
      double dy3 = yFunc.getVal(params);
      
      double x4 = x + dt*dx3;
      double y4 = y + dt*dy3;
      params[0] = x4;
      params[1] = y4;
      double dx4 = xFunc.getVal(params);
      double dy4 = yFunc.getVal(params);
      
      nextPoint[0] = x + (dt / 6) * (dx1 + 2 * dx2 + 2 * dx3 + dx4);
      nextPoint[1] = y + (dt / 6) * (dy1 + 2 * dy2 + 2 * dy3 + dy4);
   }
   
   protected void doLoadExample(String example) {
         // This method is called when the user loads an example from the 
         // example menu (if there is one).  It overrides an empty method
         // in GenericGraphApplet.
         //   For the IntegrapCurves applet, the example string should contain
         // two expression that defines the vector field, separated 
         // by a semicolon.  This can optionally
         // be followed by another semicolon and a list of numbers, separated by spaces and/or commas.
         // The first four numbers give the x- and y-limits to be used for the
         // example.  If they are not present, then -5,5,-5,5 is used.  The next number, if present,
         // specifies a value for delta t.  If there are more numbers, they should come in pairs.
         // each pair specifies a point where a curve will be started when the
         // example is loaded.  There is a 0.5 second delay between loading and starting the
         // curves to allow time for the redrawing (although it seems to block the redrawing, at least
         // on some platforms).
         
      if (animator != null) {
         curves.setSize(0);
         animator.stop();
      }
         
      int pos = example.indexOf(";");
      if (pos == -1)
         return; // illegal example -- must have two functions
      String example2 = example.substring(pos+1);
      example = example.substring(0,pos);
      pos = example2.indexOf(";");   
      
         
      double[] limits = { -5,5,-5,5 }; // x- and y-limits to use
      
      StringTokenizer toks = null;

      if (pos > 0) { 
               // Get limits from example2 text.
         String nums = example2.substring(pos+1);
         example2 = example2.substring(0,pos);
         toks = new StringTokenizer(nums, " ,");
         if (toks.countTokens() >= 4) {
            for (int i = 0; i < 4; i++) {
               try {
                   Double d = new Double(toks.nextToken());
                   limits[i] = d.doubleValue();
               }
               catch (NumberFormatException e) {
               }
            }
         }
         if (toks.hasMoreTokens()) {
            double d = Double.NaN;
            try {
               d = (new Double(toks.nextToken())).doubleValue();
            }
            catch (NumberFormatException e) {
            }
            if (Double.isNaN(d) || d <= 0 || d > 100)
               d = 0.1;
             if (deltaT != null)
                deltaT.setVal(d);
             else
                dt = d;
         }
      }
      
      // Set up the example data and recompute everything.

      if (functionInput != null) {
            // If there is a function input box, put the example text in it.
         functionInput.setText(example);
         functionInput2.setText(example2);
      }
      else { 
           // If there is no user input, set the function in the graph directly.
         try {
            Function f = new SimpleFunction( parser.parse(example), xVar );
            ((WrapperFunction)xFunc).setFunction(f);
            Function g = new SimpleFunction( parser.parse(example2), xVar );
            ((WrapperFunction)yFunc).setFunction(g);
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
      
      if (animator != null && toks != null) { // get any extra nums from the tokenizer and use them as starting points for curves
         int ct = 2*(toks.countTokens()/2);
         if (ct > 0) {
            synchronized(curves) {
               for (int i = 0; i < ct; i++) {
                  try {
                     double x = (new Double(toks.nextToken())).doubleValue();
                     double y = (new Double(toks.nextToken())).doubleValue();
                     startCurve(x,y);
                  }
                  catch (Exception e) {
                  }
               }
               if (curves.size() > 0) {  // start the curves going
                  try {  
                     Thread.sleep(500);  // wait a bit to give the canvas time to start drawing itself.
                  }
                  catch (InterruptedException e) {
                  }
               }
            }
         }      
      }
      
   } // end doLoadExample()
   

   public void stop() {  // stop animator when applet is stopped
      if (animator != null) {
         curves.setSize(0);
         animator.stop();
      }
      super.stop();
   }


} // end class IntegralCurves
