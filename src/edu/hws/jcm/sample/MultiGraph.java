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
import java.util.*;
import edu.hws.jcm.draw.*;
import edu.hws.jcm.data.*;
import edu.hws.jcm.functions.*;
import edu.hws.jcm.awt.*;

// The MultiApplet can display the graphs of several functions, in different colors.
// By default, there is only one function, but you can configure the applet to
// use more than one function with applet params.
// The definitions of these functions can, optionally, use parameters whose
// values are controled by sliders at the bottom of the applet.  

public class MultiGraph extends GenericGraphApplet {


   private Vector sliders;  // Elements of this vector are the VariableSlider
                            //   objects that represent the parameter values.
                            //   The sliders are created in the setUpParser() method.
                            
   private ExprIn[] inputs;  // The function input boxes (or null if inputs aren't used)
   private Graph1D[] graphs; // The graphs of the functions, in the case function input boxes are NOT used
   private int functionCt;   // Number of functions -- size of inputs or graphs array
   
   private Color[] graphColors = { Color.magenta, new Color(0,180,0), 
                           Color.red, new Color(0,200,200),
                           Color.orange, Color.gray, Color.blue, Color.pink };


   private static class ColorPatch extends Canvas { 
          // a canvas with a preferred size
      ColorPatch(Color c) {
         setBackground(c);
      }
      public Dimension getPreferredSize() {
         return new Dimension(25,10);
      }
      public void paint(Graphics g) {
         g.drawRect(0,0,getSize().width-1,getSize().height-1);
      }
   }
   
   private static class ExprIn extends ExpressionInput { 
            // Doesn't throw an error if empty, just sets function in graph to null
      Graph1D graph;  // Graph associated with this function input.
      Function func;  // The function of x defined by this graph.
      ExprIn(String definition, Parser p, Graph1D g, Variable v) {
         super(definition,p);
         graph = g;
         func = getFunction(v);
         if (definition.trim().length() > 0)
            graph.setFunction(func);
      }
      public void checkInput() { // (will be called during constructor -- hence the funny bit with checking if graphe is null)
         boolean hasChanged = previousContents == null || !previousContents.equals(getText());
         if (!hasChanged)
            return;
         String text = getText().trim();
         if (text.length() == 0) {  // set graph's function to null so it doesn't have to do any computations.
            if (graph != null)
               graph.setFunction(null);
            previousContents = getText();
         }
         else {
            super.checkInput();
            if (graph != null)
               graph.setFunction(func);
         }
      }
   }

   protected void setUpParser() {  // Override this to add VariableSliders to parser.
   
      // Get the data for any sliders from applet params named "Parameter", "Parameter1", ...
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
      
      super.setUpParser();  // Call this last so function definitions 
                            // in applet params can use the parameter names 
                                                        
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
   
   
   private void getColors() { // get graph colors from color parameters, if any.
      
      Vector vec = new Vector();
      int ct = 0;
      Color c = getColorParam("GraphColor");
      if (c == null) {
         ct++;
         c = getColorParam("GraphColor" + ct);
      }
      while (true) {
         if (c == null)
            break;
         vec.addElement(c);
         ct++;
         c = getColorParam("GraphColor" + ct);
      }
      if (vec.size() > 0) {
         graphColors = new Color[vec.size()];
         for (int i = 0; i < vec.size(); i++)
            graphColors[i] = (Color)vec.elementAt(i);
      }
   }
   
   private Vector getFunctions() {  // Read applet parms "Function", "Funcion1", ...
                                    // Return a vector containing the function definition strings
      Vector functions = new Vector();
      int ct = 0;
      String c = getParameter("Function");
      if (c == null) {
         ct++;
         c = getParameter("Function" + ct);
      }
      while (true) {
         if (c == null)
            break;
         functions.addElement(c);
         ct++;
         c = getParameter("Function" + ct);
      }
      if (functions.size() == 0)
         functions.addElement( " abs( " + xVar.getName() + ") ^ " + xVar.getName() );
      double[] d = getNumericParam("FunctionCount");
      if (d == null || d.length == 0 || d[0] <= 0.5)
         functionCt = functions.size();
      else {
         functionCt = (int)Math.round(d[0]);
         if (functionCt < functions.size()) { // use number of functions specified as functionCt
            functionCt = functions.size();
         }
         else {  // make extra empty functions to bring total up to functionCt
            int extra = functionCt - functions.size();
            for (int i = 0; i < extra; i++)
               functions.addElement("");
         }
      }
      return functions;
   }
   

   private Panel makeFunctionInput(Vector functions, int funcNum) {  
           // make input box for specified function
           // also adds the input box to the inputs[] array
      Graph1D graph = new Graph1D();
      graph.setColor(graphColors[funcNum % graphColors.length]);
      ExprIn in = new ExprIn((String)functions.elementAt(funcNum),parser,graph,xVar);
      in.setOnUserAction(mainController);
      JCMPanel p = new JCMPanel();
      p.add(in,BorderLayout.CENTER);
      String name;
      if (functions.size() > 1)
         name = " " + getParameter("FunctionName","f") + (funcNum+1) + "(" + xVar.getName() + ") = ";
      else
         name = " " + getParameter("FunctionName","f") +  "(" + xVar.getName() + ") = ";
      p.add(new Label(name), BorderLayout.WEST);
      if (graphColors.length > 1 && functions.size() > 1)
         p.add(new ColorPatch( graphColors[funcNum % graphColors.length] ), BorderLayout.EAST);
      inputs[funcNum] = in;
      return p;
   }


   protected void setUpBottomPanel() {  
      // Overridden to create an appropriate input panel

      // Create a panel holding all the function inputs and
      // sliders, with a display label for each slider to show its value.
      
      boolean funcInput = "yes".equalsIgnoreCase(getParameter("UseFunctionInput","yes"));
      
      if ( funcInput && "yes".equalsIgnoreCase(getParameter("UseComputeButton", "yes")) ) { // make the compute button
         String cname = getParameter("ComputeButtonName", "New Functions");
         computeButton = new Button(cname);
         computeButton.addActionListener(this);
      }
      Panel firstPanel = null;  // To help find a place for the compute button
     
      getColors();
      Vector functions = getFunctions();

      if (!funcInput && sliders.size() == 0)  // nothing to put in the input panel
         return;

      JCMPanel panel = new JCMPanel();
      if (! "no".equalsIgnoreCase(getParameter("TwoInputColumns","no")))
         panel.setLayout(new GridLayout(0,2,12,3));
      else 
         panel.setLayout(new GridLayout(0,1,3,3));
      panel.setBackground(getColorParam("PanelBackground", Color.lightGray));

      if (funcInput) { // make an input box for each function and add it to the panel
         inputs = new ExprIn[functions.size()];
         for (int i = 0; i < functions.size(); i++) {
            Panel p = makeFunctionInput(functions,i);
            if (firstPanel == null)
               firstPanel = p;
            panel.add(p);
         }
      }
      else {  // just make graphs from the function definition strings.
         graphs = new Graph1D[functions.size()];
         for (int i = 0; i < functions.size(); i++) {
            graphs[i] = new Graph1D();
            graphs[i].setColor(graphColors[ i % graphColors.length ]);
            String def = ((String)functions.elementAt(i)).trim();
            if (def.length() > 0) {  // if the definition string is empty, leave graph's function undefined
                Function f = new SimpleFunction( parser.parse(def), xVar );
                graphs[i].setFunction(f);
            }
         }
      }

      for (int i = 0; i < sliders.size(); i++) {  // add sliders to the input panel
         JCMPanel p = new JCMPanel();
         VariableSlider slide = (VariableSlider)sliders.elementAt(i);
         p.add(slide, BorderLayout.CENTER);
         p.add(new DisplayLabel("  " + slide.getName() + " = # ", new Value[] { slide.getVariable() } ), 
                      BorderLayout.EAST);
         panel.add(p);
         slide.setOnUserAction(mainController);
      }
      
      if (computeButton != null) {  // find a place for the compute button!
         if (functions.size() == 1)
            firstPanel.add(computeButton, BorderLayout.EAST);
         else if (limitsPanel == null) {
            Panel p = new Panel();
            p.add(computeButton);
            panel.add(p);  
         }
         // otherwise, add it at the end of setUpLimitPanel();
      }
      
      mainPanel.add(panel, BorderLayout.SOUTH);
      
   } // end setUpBottomPanel()

   protected void setUpLimitsPanel() { // add compute button if it hasn't been put somewhere else
      super.setUpLimitsPanel();
      if (limitsPanel != null && computeButton != null && functionCt != 1)
         limitsPanel.addComponent(computeButton);
   }

   protected void setUpCanvas() { // Overridden to add the graph to the canvas.

      super.setUpCanvas();  // Do the default setup.

      // set up bottom panel has already been defined
      // add the graphs to the canvas
      
      if (graphs != null) {
         for (int i = 0; i < graphs.length; i++)
            canvas.add(graphs[i]);
      }
      else {
         for (int i = 0; i < inputs.length; i++)
            canvas.add(inputs[i].graph);
      }

   } // end setUpCanvas



   protected void doLoadExample(String example) {
         // This method is called when the user loads an example from the 
         // example menu (if there is one).  It overrides an empty method
         // in GenericGraphApplet.
         //   For the FamiliesOfGraphs applet, the example string should contain
         // an expression that defines the function to be graphed.  This must
         // be followed by a semicolon and list of zero or more numbers.
         // Then there is another semicolon and one or more function definitions,
         // separated by semicolons.  You can have as many function
         // definitions as you have functions in your applet setup.
         // (Note that having the numbers before the
         // functions is different from the format of examples in all the
         // other configurable applets.  This is to allow more than one function.)  Note that even if you leave
         // out the numbers, you still need two semicolons.  The list of numbers has the following meaning:
         // The first four numbers give the x- and y-limits to be used for the
         // example.  If they are not present, then -5,5,-5,5 is used.  The
         // remaining numbers occur in groups of three. Each group give the maximum, minimum, and value of a parameters that was defined
         // with the "Parameter", "Parameter1", ... applet params.
         
      int pos = example.indexOf(";");
      
      double[] limits = { -5,5,-5,5 }; // x- and y-limits to use

      if (pos > 0) { 
               // Get limits from example text.
         String nums = example.substring(0,pos);
         example = example.substring(pos+1);
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
      StringTokenizer toks = new StringTokenizer(example,";");
      int funcNum = 0;
      while (funcNum < functionCt) {
         if (toks.hasMoreElements()) {  // define the function using definition from example text
             String def = toks.nextToken();
             if (graphs != null) {
                try {
                    graphs[funcNum].setFunction(new SimpleFunction( parser.parse(def), xVar ));
                 }
                 catch (ParseError e) {
                    graphs[funcNum].setFunction(null); 
                 }
             }
             else
                inputs[funcNum].setText(def);
         }
         else {  // function is undefined
            if (graphs != null)
               graphs[funcNum].setFunction(null);
            else
               inputs[funcNum].setText("");
         }
         funcNum++;
      }

      CoordinateRect coords = canvas.getCoordinateRect(0);
      coords.setLimits(limits);
      coords.setRestoreBuffer();
      mainController.compute();
      
   } // end doLoadExample()
 
   
} // end class MultiGraph

