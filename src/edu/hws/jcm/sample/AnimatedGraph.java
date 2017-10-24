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

// An applet belonging to the class AnimatedGraph displays a graph
// of a function that can depend on a parameter.  The value of the 
// parameter can be "animated" so that it ranges from one value ot
// another over a sequence of frames.

import java.awt.*;
import java.applet.Applet;
import java.util.*;
import edu.hws.jcm.draw.*;
import edu.hws.jcm.data.*;
import edu.hws.jcm.functions.*;
import edu.hws.jcm.awt.*;


public class AnimatedGraph extends GenericGraphApplet {


   // Declare some private variables that are created in one method in
   // this class and used in a second method.

   private Function func;   // The function that is graphed.
   private Graph1D graph;   // The graph of the function.
   
   private Animator animator; // Animates the graph
   private Variable kVar;   // The parameter variable
   
   private VariableInput kMin, kMax, kIntervals;  // min, max, and number of intervals for the animator.  Might be null.


   protected void setUpParser() {  // Override this to create the animator and add its variable to the parser.
   
      int options = Animator.START_STOP_BUTTON | Animator.PAUSE_BUTTON  | Animator.LOOP_CHOICE;
      if  ( ! "no".equalsIgnoreCase(getParameter("UseNextAndPrev","yes")) )
         options |=  Animator.PREV_BUTTON | Animator.NEXT_BUTTON;
      animator = new Animator(options);
      kVar = animator.getValueAsVariable( getParameter("Parameter","k") );   
      parser.add(kVar);
      
      super.setUpParser();

      parameterDefaults = new Hashtable();
      String defaultFunction = xVar.getName() + " / (" + kVar.getName() + " - " + xVar.getName() + "^2)";
      parameterDefaults.put("Function",defaultFunction);
      if (! "no".equalsIgnoreCase(getParameter("UseAnimatorInputs")))
         parameterDefaults.put("TwoLimitsColumns","yes"); // change default if we need space for animator inputs
      
   } // end setUpParser()
   

   protected void setUpBottomPanel() {  // Overridden to add the sliders at the bottom of the applet.

      super.setUpBottomPanel();  // Do the default setup.
      
      // If there is a functionInput box, then the SOUTH position of the mainPanel already contains
      // the inputPanel that contains that box.  If so, add the animator to the SOUTH position of
      // the inputPanel.  (This is a good place, in general, to put extra input objects.)
      // If there is no inputPanel, then the SOUTH position of the mainPanel is empty, so put
      // the animator there.
      
      if (inputPanel != null)
         inputPanel.add(animator, BorderLayout.SOUTH);
      else
         mainPanel.add(animator, BorderLayout.SOUTH);

   } // end setUpBottomPanel()



   protected void setUpCanvas() { // Overridden to add the graph to the canvas and do other chores.

      super.setUpCanvas();  // Do the default setup.

      // When setUpCanvas() is called, the functionInput already exists, if one is
      // to be used, since it is created in setUpBopttomPanel(), which is called
      // before setUpCanvas.  If functionInput exists, add a graph of the function
      // from functionInput to the canvas.  If not, create a graph of the function
      // specified by the parameter named "Function" (or use sin(k*x) if none is specified).

      if (functionInput != null)
         func = functionInput.getFunction(xVar);
      else {
         String def = getParameter("Function");  // default value is set in setUpParser()
         func = new SimpleFunction( parser.parse(def), xVar );
      }

      // Create a graph of the function and add it to the canvas.
      
      graph = new Graph1D(func);
      graph.setColor(getColorParam("GraphColor", Color.magenta));
      canvas.add(graph);
      
      // Set up the min, max, and intervals property of the animator
      
      if  (! "no".equalsIgnoreCase(getParameter("UseAnimatorInputs"))) {
         kMin = new VariableInput(kVar.getName() + "Start",getParameter("ParameterMin","-2"));
         kMax = new VariableInput(kVar.getName() + "End",getParameter("ParameterMax","2"));
         kIntervals = new VariableInput("Intervals", getParameter("Intervals","25"));
         kIntervals.setInputStyle(VariableInput.INTEGER);
         kIntervals.setMin(1);
         kIntervals.setMax(1000);
         kMin.setOnUserAction(mainController);
         kMax.setOnUserAction(mainController);
         kIntervals.setOnUserAction(mainController);
         animator.setMin(kMin);
         animator.setMax(kMax);
         animator.setIntervals(kIntervals);
         if (limitsPanel != null) {
               // componets will be added to limitsPanel in setUpLimitsPanel()
            mainController.add(kMin);  // This is not done automatically, since they are in a limits panel  
            mainController.add(kMax);
            mainController.add(kIntervals);
         }
         else {
            JCMPanel ap = new JCMPanel(9,0);
            ap.setBackground(getColorParam("PanelBackground", Color.lightGray));
            ap.add(new Label(kMin.getName()));
            ap.add(kMin);
            ap.add(new Label());
            ap.add(new Label(kMax.getName()));
            ap.add(kMax);
            ap.add(new Label());
            ap.add(new Label(kIntervals.getName()));
            ap.add(kIntervals);
            ap.add(new Label());
            mainPanel.add(ap,BorderLayout.EAST);
         }
      }
      else {
         try {
            animator.setMin( (new Double(getParameter("ParameterMin","-2"))).doubleValue() );
            animator.setMax( (new Double(getParameter("ParameterMax","2"))).doubleValue() );
            animator.setIntervals( (int)Math.round((new Double(getParameter("Intervals","25"))).doubleValue()) );
         }
         catch (NumberFormatException e) {
         }
      }
      animator.setOnChange(mainController);

      // Add a DrawString to show the current value of the parameter

      if ( ! "no".equalsIgnoreCase(getParameter("ShowParameter","yes")) ) {
         DrawString param = new DrawString(kVar.getName() + " = #", DrawString.BOTTOM_LEFT, new Value[] { kVar });
         param.setBackgroundColor(canvas.getBackground());
         Color c = getColorParam("ParameterColor",Color.black);
         param.setColor(c);
         canvas.add(param);
      }

   } // end setUpCanvas


   protected void setUpLimitsPanel() {
     super.setUpLimitsPanel();
     if (limitsPanel != null && kMin != null) {  // add animator inputs to limits panel
         limitsPanel.addComponentPair(kMin,kMax);
         limitsPanel.addComponent(kIntervals);
     }
   }
 

   protected void doLoadExample(String example) {
         // This method is called when the user loads an example from the 
         // example menu (if there is one).  It overrides an empty method
         // in GenericGraphApplet.
         //   For the AnimatedGraph applet, the example string should contain
         // an expression that defines the function to be graphed.  This can optionally
         // be followed by a semicolon and a list of four to nine numbers.
         // The first four numbers give the x- and y-limits to be used for the
         // example.  If they are not present, then -5,5,-5,5 is used.  The
         // next three numbers specify the minimum value for the parameter, the
         // maximum number, and the number of intervals in the animation.
         // The eigth number, if present, specifies the starting loop style
         // for the animation with the following code:  0 for once-through,
         // 1 for loop, and 2 for back-and-forth.  The ninth number, if
         // present, tells whether to start the animation immediately upon
         // loading.  If it is 1, the animation is started.  If it is
         // not specified or is any value other than 1, the animation is not started.
         
      animator.stop();
         
      int pos = example.indexOf(";");
      boolean startAnimation = false;
      double[] limits = { -5,5,-5,5 }; // x- and y-limits to use

      if (pos > 0) { 
               // Get limits from example text.
         String nums = example.substring(pos+1);
         example = example.substring(0,pos);
         StringTokenizer toks = new StringTokenizer(nums, " ,");
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
            try {
               double d = (new Double(toks.nextToken())).doubleValue();
               if (kMin == null)
                  animator.setMin(d);
               else
                  kMin.setVal(d);
            }
            catch (NumberFormatException e) {
            }
         }
         if (toks.hasMoreTokens()) {
            try {
               double d = (new Double(toks.nextToken())).doubleValue();
               if (kMax == null)
                  animator.setMax(d);
               else
                  kMax.setVal(d);
            }
            catch (NumberFormatException e) {
            }
         }
         if (toks.hasMoreTokens()) {
            try {
               int d = (int)Math.round((new Double(toks.nextToken())).doubleValue());
               if (kIntervals == null)
                  animator.setIntervals(d);
               else
                  kIntervals.setVal(d);
            }
            catch (NumberFormatException e) {
            }
         }
         if (toks.hasMoreTokens()) {
            try {
               int d = (int)Math.round((new Double(toks.nextToken())).doubleValue());
               animator.setLoopStyle(d);
            }
            catch (NumberFormatException e) {
            }
         }
         if (toks.hasMoreTokens()) {
            try {
               int d = (int)Math.round((new Double(toks.nextToken())).doubleValue());
               startAnimation = (d == 1);
            }
            catch (NumberFormatException e) {
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
         try {
            func = new SimpleFunction( parser.parse(example), xVar );
            graph.setFunction(func);
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
      if (startAnimation) {
         try {  // insert a small delay before animation starts
            synchronized(this) {
               wait(250);
            }
         }
         catch (InterruptedException e) {
         }
         animator.start();
      }
      
   } // end doLoadExample()
   
   public void stop() {  // stop animator when applet is stopped
      animator.stop();
      super.stop();
   }
 
   
} // end class FamiliesOfGraphs

