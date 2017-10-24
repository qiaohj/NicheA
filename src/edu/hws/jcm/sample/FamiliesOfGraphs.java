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

// An applet belonging to the class FamiliesOfGraphs displays a graph
// of a function that can depend on one or more parameters.  The values of
// the parameters are controlled by the user using sliders at the bottom of
// the applet.


import java.awt.*;
import java.applet.Applet;
import java.util.*;
import edu.hws.jcm.draw.*;
import edu.hws.jcm.data.*;
import edu.hws.jcm.functions.*;
import edu.hws.jcm.awt.*;


public class FamiliesOfGraphs extends GenericGraphApplet {


   // Declare some private variables that are created in one method in
   // this class and used in a second method.

   private Function func;   // The function that is graphed.
   private Graph1D graph;   // The graph of the function.
   
   private Vector sliders;  // Elements of this vector are the VariableSlider
                            //   objects that represent the parameter values.
                            //   The sliders are created in the setUpParser() method.



   protected void setUpParser() {  // Override this to add VariableSliders to parser.
   
      // Get the data for the sliders from applet params named "Parameter", "Parameter1", ...
      // The sliders are created and the variables are added to the parser by the
      // addParameter() method, which is defined below.
      
      sliders = new Vector();
      int ct = 0;
      String param = getParameter("Parameter");
      if (param == null) {
         ct++;
         param = getParameter("Parameter" + ct);
      }
      while (true) {
         if (param == null)
            break;
         addParameter(param);
         ct++;
         param = getParameter("Parameter" + ct);
      }
      
      // If no parameters were specified in applet params, create one with name "k".
      
      if (sliders.size() == 0)
         addParameter("k");
         
      super.setUpParser();  // Call this last so function definitions 
                            // in applet params can use the parameter names 
                            // that have just been added to the parser
                            // (even though it's probably not a good idea).
                            // Note that this also defines the independent variable,
                            // whose name is given by the applet param "Variable"
                            // and which is referred to as xVar in this program.
                            
      VariableSlider slide = (VariableSlider)sliders.elementAt(0);
      String def = getParameter("Function", "sin(" + slide.getName() + " * " + xVar.getName() + ")");
      parameterDefaults = new Hashtable();  // I want to set a different default value for
                                            // the "Function" applet param.
      parameterDefaults.put("Function",def);
         
                            
   } // end setUpParser()
   


   private void addParameter(String data) {
         // Create a VariableSlider from the information in name and add it to the
         // Vector of sliders.  The data must contain the name of the variable 
         // associated with the slider.  The name can be followed by a ";" and up to
         // three numbers.  (If there is no ";", a space after the name will do.)
         // The numbers can be separated by commas, spaces, or tabs.  The first
         // number gives the minimum value on the slider, the second gives the maximum,
         // and the third gives the initial value of the slider variable.

      double min = -5, max = 5, val = 0;  // min, max, and value for slider

      data = data.trim();
      int pos = data.indexOf(';');
      if (pos < 0)
         pos = data.indexOf(' ');
         
      String name; //  The name of the parameter

      if (pos < 0) {
            // If there is no space or ";", the data is just the name of the variable.
         name = data;
      }
      else {
            // Get the name from the front of the data, then look for min, max, and val.
          String nums = data.substring(pos+1);
          name = data.substring(0,pos).trim();
          StringTokenizer toks = new StringTokenizer(nums," ,\t");
          try {
             if (toks.hasMoreElements())
                 min = (new Double(toks.nextToken())).doubleValue();
             if (toks.hasMoreElements())
                 max = (new Double(toks.nextToken())).doubleValue();
             if (toks.hasMoreElements())
                 val = (new Double(toks.nextToken())).doubleValue();
          }
          catch (NumberFormatException e) {
             min = -5;
             max = 5;
             val = 0;
          }
      }
      
      // Create the slider, adding the associated variable to the parser, and set its value.
      
      VariableSlider slide = new VariableSlider(name, new Constant(min), new Constant(max), parser);
      slide.setVal(val);
      
      sliders.addElement(slide);  // Save the slider in the array of sliders for later use.
      
   } // end setUpParser();
   


   protected void setUpBottomPanel() {  // Overridden to add the sliders at the bottom of the applet.

      super.setUpBottomPanel();  // Do the default setup.

      // Create a panel holding all the sliders, with a display label for each slider to show its value.

      JCMPanel sliderPanel = new JCMPanel();
      sliderPanel.setLayout(new GridLayout(0,1,3,3));
      sliderPanel.setBackground(getColorParam("PanelBackground", Color.lightGray));
      for (int i = 0; i < sliders.size(); i++) {
         JCMPanel p = new JCMPanel();
         VariableSlider slide = (VariableSlider)sliders.elementAt(i);
         p.add(slide, BorderLayout.CENTER);
         p.add(new DisplayLabel("  " + slide.getName() + " = # ", new Value[] { slide.getVariable() } ), 
                      BorderLayout.EAST);
         sliderPanel.add(p);
         slide.setOnUserAction(mainController);
      }
      
      
      // If there is a functionInput box, then the SOUTH position of the mainPanel already contains
      // the inputPanel that contains that box.  If so, add the new panel to the SOUTH position of
      // the inputPanel.  (This is a good place, in general, to put extra input objects.)
      // If there is no inputPanel, then the SOUTH position of the mainPanel is empty, so put
      // the newly created panel there.
      
      if (inputPanel != null)
         inputPanel.add(sliderPanel, BorderLayout.SOUTH);
      else
         mainPanel.add(sliderPanel, BorderLayout.SOUTH);

   } // end setUpBottomPanel()



   protected void setUpCanvas() { // Overridden to add the graph to the canvas.

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

   } // end setUpCanvas



   protected void doLoadExample(String example) {
         // This method is called when the user loads an example from the 
         // example menu (if there is one).  It overrides an empty method
         // in GenericGraphApplet.
         //   For the FamiliesOfGraphs applet, the example string should contain
         // an expression that defines the function to be graphed.  This can optionally
         // be followed by a semicolon and a list of four or more numbers.
         // The first four numbers give the x- and y-limits to be used for the
         // example.  If they are not present, then -5,5,-5,5 is used.  The
         // remaining numbers occur in groups of three and specify the minimumn, 
         // maximum and values of the parameters, in the
         // same order that they were encountered in the setUpParser() method.
         
      int pos = example.indexOf(";");
      
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
         int i = 0;
         while (i < sliders.size() && toks.hasMoreElements()) {
               // Look for a value for the i-th slider.
            try {
                double min = (new Double(toks.nextToken())).doubleValue();
                double max = (new Double(toks.nextToken())).doubleValue();
                double d = (new Double(toks.nextToken())).doubleValue();
                VariableSlider slider = ((VariableSlider)sliders.elementAt(i));
                slider.setMin(new Constant(min));
                slider.setMax(new Constant(max));
                slider.setVal(d);
            }
            catch (Exception e) {
            }
            i++;
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
      
   } // end doLoadExample()
 
   
} // end class FamiliesOfGraphs

