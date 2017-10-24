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

import edu.hws.jcm.awt.*;
import edu.hws.jcm.data.*;
import edu.hws.jcm.draw.*;
import edu.hws.jcm.functions.*;

import java.awt.*;
import java.awt.event.*;
import java.util.StringTokenizer;
import java.applet.Applet;

/**
 * An Evaluator applet lets the user enter the values of one or more variables,
 * and it displayes the values of one or more expressions that can involve those
 * variables.  The expression values are updated continuously as the user types.
 */

public class Evaluator extends Applet implements ActionListener {

   private Frame frame;       // If non-null, a separate window.
   private String frameTitle; // Title for the separate window.
   private Button launchButton;  // If non-null, then clicking this buttons opens a separate window.
   private String launchButtonName;  // Name for the launch button.

   /**
    * The init() method is called by the system to set up the applet. 
    * If the applet does not appear as a button, then init() creates the main panel of the applet
    * and calls setUpMainPanel to set it up.
    */
   public void init() {
      frameTitle = getParameter("FrameTitle"); // Get title to be used for separate window, if any.
      if (frameTitle == null) {
         frameTitle = "Calculator";
         int pos = frameTitle.lastIndexOf('.');
         if (pos > -1)
            frameTitle =  frameTitle.substring(pos+1);
      }
      setLayout(new BorderLayout());
      int height = getSize().height;
      launchButtonName = getParameter("LaunchButtonName");
      if ( (height > 0 && height <= 35) || launchButtonName != null) {
              // Use a separater window and only show a button in the applet.
          if (launchButtonName == null)
               launchButtonName = "Launch " + frameTitle;
          launchButton = new Button(launchButtonName);
          add(launchButton, BorderLayout.CENTER);
          launchButton.addActionListener(this);
      }
      else {
             // Show the main panel in the applet, not in a separate window.
          add(makeMainPanel(), BorderLayout.CENTER);
      }
   }

   /*
    * Create the main panel of the applet.
    */ 
   public JCMPanel makeMainPanel() {
   
      // Get values of color params.
      
      Color background = getColorParam("BackgroundColor", Color.gray);
      Color labelBackground = getColorParam("LabelBackground", new Color(225,225,225));
      Color labelForeground = getColorParam("LabelForeground", new Color(0,0,200));
      Color answerBackground = getColorParam("AnswerBackground", labelBackground);
      Color answerForeground = getColorParam("AnswerForeground", Color.red);
      Color inputBackground = getColorParam("InputBackground", Color.white);
      Color inputForeground = getColorParam("InputForeground", Color.black);

      // Create the panel and subpanel.  The left subpanel will hold labes for the
      // variables and expressions.  The right subpanel will hold the variable input
      // boxes and expression values.

      JCMPanel panel = new JCMPanel(5);
      panel.setBackground(background);
      panel.setInsetGap(3);
      setLayout(new BorderLayout());
      add(panel,BorderLayout.CENTER);
      JCMPanel left = new JCMPanel(0,1,3);
      panel.add(left, BorderLayout.CENTER);
      JCMPanel right = new JCMPanel(0,1,3);
      panel.add(right, BorderLayout.EAST);
      
      // Create a parser and configure it to allow factorials and summations.
      
      Parser parser = new Parser();
      parser.addOptions(Parser.FACTORIAL);
      parser.add( new SummationParser() );
      
      // Create the variable input boxes, using variable names given by
      // applet parameters.  If no names are provided in applet parameters,
      // use one variable named "x".  Add the Variables from the variable inputs
      // to the parser so that they can be used in expressions.
      
      int ct = 0;
      String variableName = getParameter("Variable");
      if (variableName == null) {
         variableName = getParameter("Variable1");
         if (variableName == null) {
            variableName = "x";
         }
         else
            ct = 1;
      }
      String firstVar = variableName;
      while (variableName != null) {
         String valString = "0";
         variableName = variableName.trim();
         int pos = variableName.indexOf(" ");
         if (pos > 0) {
                // If there is anything in the string after the variable name, use it as the variable value.
             valString = variableName.substring(pos+1).trim();
             variableName = variableName.substring(0,pos);
         }
         Label lab = new Label(" Input:  " + variableName + " =  ", Label.RIGHT);
         lab.setBackground(labelBackground);
         lab.setForeground(labelForeground);
         left.add(lab);
         VariableInput v = new VariableInput(variableName,valString,parser);
         v.setBackground(inputBackground);
         v.setForeground(inputForeground);
         v.setThrowErrors(false);
         v.setOnTextChange(panel.getController());
         v.setOnUserAction(panel.getController());
         right.add(v);
         ct++;
         variableName = getParameter("Variable" + ct);
      }
      
      // Get the expressions to be evalueated from applet parameters and add evaluators
      // to the applet.  If not expressions are provided in applet parameters, use
      // one expression, "log2(x)".
      
      ct = 0;
      String function = getParameter("Expression");
      if (function == null) {
         function = getParameter("Expression1");
         if (function == null)
            function = "log2(" + firstVar + ")";
         else
            ct = 1;
      }
      while (function != null) {
         Label lab = new Label(" " + function + " =  ", Label.RIGHT);
         lab.setBackground(labelBackground);
         lab.setForeground(labelForeground);
         left.add(lab);
         try {
            DisplayLabel d = new DisplayLabel("#", parser.parse(function));
            d.setBackground(answerBackground);
            d.setForeground(answerForeground);
            d.setAlignment(Label.CENTER);
            right.add(d);
         }
         catch (ParseError e) {
            right.add(new Label("invalid function"));
         }
         ct++;
         function = getParameter("Expression" + ct);
      }
      
      return panel;

   } // end makeMainPanel()
   

   /**
    *  Respond when user clicks a button; not meant to be called directly.
    *  This opens and closes the separate window.
    */ 
   synchronized public void actionPerformed(ActionEvent evt) {
      Object source = evt.getSource();
      if (source == launchButton && launchButton != null) {
         launchButton.setEnabled(false);
         if (frame == null) {
            frame = new Frame(frameTitle);
            frame.add(makeMainPanel());
            frame.addWindowListener( new WindowAdapter() {
                  public void windowClosing(WindowEvent evt) {
                     frame.dispose();
                  }
                  public void windowClosed(WindowEvent evt) {
                     frameClosed();
                  }
               } );
            frame.pack();
            frame.setLocation(50,50);
            frame.show();
            launchButton.setLabel("Close Window");
            launchButton.setEnabled(true);
         }
         else {
            frame.dispose();
         }
      }
   }
   
   synchronized private void frameClosed() {
        // respond when separate window closes.
      frame = null;
      launchButton.setLabel(launchButtonName);
      launchButton.setEnabled(true);
   }
   
   /**
    * Get The value of an applet parameter that specifies a color.  The color can be specfied
    * as a list of three numbers in the range 0 to 255 or by one of the standard color names
    * ("black", "red", "blue", "green", "yellow", "cyan", "magenta", "gray", "darkgray",
    * "lightgray", "pink", "orange", "white").  Color names are not case sensitive.
    *
    * @param paramName The name of the applet parameter.
    * @param defaultColor The value to be returned if getParameter(paramName) is null or is not a valid color.
    */
   protected Color getColorParam(String paramName, Color defaultColor) {
      String data = getParameter(paramName);
      if (data == null || data.trim().length() == 0)
         return defaultColor;
      data = data.trim();
      if (Character.isLetter(data.charAt(0))) {
         for (int i = 0; i < colorNames.length; i++)
            if (data.equalsIgnoreCase(colorNames[i]))
               return colors[i];
         return defaultColor;
      }
      else {
         StringTokenizer tokenizer = new StringTokenizer(data," \t,;");
         int count = tokenizer.countTokens();
         if (count < 3) 
            return defaultColor;
         double[] nums = new double[3];
         for (int i = 0; i < 3; i++) {
            try {
               Double d = new Double(tokenizer.nextToken());
               nums[i] = d.doubleValue();
            }
            catch (NumberFormatException e) {
               return defaultColor;
            }
         }
         if (nums[0] < 0 || nums[0] > 255 || nums[1] < 0 || nums[1] > 255 || nums[2] < 0 || nums[2] > 255)
            return defaultColor;
         return new Color((int)Math.round(nums[0]), (int)Math.round(nums[1]), (int)Math.round(nums[2]));
      }
   }
   
   private String[] colorNames = { "black", "red", "blue", "green", "yellow",
                                          "cyan", "magenta", "gray", "darkgray",
                                          "lightgray", "pink", "orange", "white" };
   private Color[]  colors = { Color.black, Color.red, Color.blue, Color.green, Color.yellow,
                                      Color.cyan, Color.magenta, Color.gray, Color.darkGray,
                                      Color.lightGray, Color.pink, Color.orange, Color.white };

} // end class evaluator
