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
import java.awt.event.*;
import java.applet.*;
import java.util.*;
import edu.hws.jcm.data.*;
import edu.hws.jcm.draw.*;
import edu.hws.jcm.awt.*;
import edu.hws.jcm.functions.*;

/**
 * GenericGraphApplet serves as a base class for applets that have a DisplayCanvas in the CENTER of 
 * a BorderLayout and that are configurable by a large number of applet parameters.  This base class
 * sets up the basic structure of the applet and processes many applet parameters.  Most of the
 * work is done in methods that can be overridden in subclasses.    If the height of
 * the applet is greater than 100 and if the applet parameter "LaunchButtonName" is not defined,
 * then the main panel of the applet appears in the applet itself.  Otherwise, 
 * the applet appears as a button.  Clicking the button opens the main panel of the
 * applet in a separate, resizable frame.  
 */

public class GenericGraphApplet extends Applet implements ActionListener, ItemListener {

   /**
    * The parser which is used for parsing input from the functionInput box.
    */
   protected Parser parser;

   /**
    * The main panel of the applet, containing a display canvas and other components.
    */
   protected JCMPanel mainPanel;

   /**
    * The Controller from the mainPanel.
    */
   protected Controller mainController;

   /**
    * The canvas for displaying axes, graphs, etc.
    */
   protected DisplayCanvas canvas;

   /**
    * Controls the limits of the x- and y-coordinates on the canvas.
    */
   protected LimitControlPanel limitsPanel;

   /**
    * An input box for inputting an expression.
    */
   protected ExpressionInput functionInput;
   
   /**
    * The variable that is created and added to the parser.  The default name of this
    * variable is "x", but that can be changed using the "Variable" applet param.
    * The variable is created in the setUpParser() method.
    */
   protected Variable xVar;

   /**
    * A panel containing the functionInput.  It appears at the bottom of the main panel.
    */
   protected JCMPanel inputPanel;

   /**
    * A panel containing examples specified in applet parameters, if any.  It appears
    * at the top of the main panel.
    */
   protected JCMPanel exampleMenuPanel;

   /**
    * A button that the user presses to recompute the display. (Ths user can also just
    * press return in one of the input boxes.)
    */
   protected Button computeButton;
   
   /**
    * The string that is used as the title of the frame, if the applet appears as
    * a launch button.  This title is also used on the launch button, unless the
    * applet parameter "LaunchButtonName" has a value.  This is set in the init() method.
    */
   protected String frameTitle;
   
   /**
    * The default size for the frame when the applet runs a launch button.
    * Can be overidden by the FrameSize applet param.
    */
   protected int[] defaultFrameSize = { 550, 400} ;
   
   /**
    * A hash table that, if non-null, can hold values for applet params.  While this is
    * not set to a non-null value in this class, its value can be set in the setUpAppletDefaults()
    * method.  The values in this hash table will be returned by the getParameter() method
    * when no value is provided in the applet.  For example, this class uses a default value
    * of "s" vor the the param "Variable".  A subclass could put a different value in the
    * parameterDefaults hash table, and this value will be used in preference to "x".
    */
   protected Hashtable parameterDefaults;
   
   /**
    * The init() method is called by the system to set up the applet. 
    * If the applet does not appear as a button, then init() creates the main panel of the applet
    * and calls setUpMainPanel to set it up.  If any error occurs during calls to setUpParser(), 
    * setUpExampleMenu(), or setUpMainPanel(), then the applet will just show an error message,
    * and stack trace will be printed to standard output.
    */
   public void init() {
      setUpParameterDefaults();
      frameTitle = getParameter("FrameTitle");
      if (frameTitle == null) {
         frameTitle = this.getClass().getName();
         int pos = frameTitle.lastIndexOf('.');
         if (pos > -1)
            frameTitle =  frameTitle.substring(pos+1);
      }
      setLayout(new BorderLayout());
      int height = getSize().height;
      launchButtonName = getParameter("LaunchButtonName");
      if ( (height > 0 && height < 100) || launchButtonName != null) {
          if (launchButtonName == null)
               launchButtonName = "Launch " + frameTitle;
          launchButton = new Button(launchButtonName);
          add(launchButton, BorderLayout.CENTER);
          launchButton.addActionListener(this);
      }
      else {
          mainPanel = new JCMPanel();
          try {
             setUpMainPanel();
             add(mainPanel, BorderLayout.CENTER);
          }
          catch (Exception e) {
             System.out.println("Error while opening applet:");
             e.printStackTrace();
             TextArea message = new TextArea("An error occurred while setting up the applet:\n\n");
             message.append(e.toString());
             add(message, BorderLayout.CENTER);
          }
      }
   }
  
   
   /**
    * This method is called to set up the main panel of the applet.  The main panel object, of type JCMPanel,
    * already exists in the variable mainPanel when it is called.  It begins by calling setUpParser()
    * and setUpExampleMenu().  This method processes applet parameters
    * "Insets", "BackgroundColor", and "ForegroundColor" to set the gap and colors of the panel.  It
    * creates a DisplayCanvas and, if the applet parameter "UseLimitsPanel" is not equal to "no", it
    * creates a LimitControlPanel.  The coordinate limits are set using the "Limits" applet param, if 
    * presetn.  This method assigns the Controller from the mainPanel to the member variable
    * mainController.  It then calls setUpBottomPanel(), setUpTopPanel(), setUpCanvas(), addCanvasBorder(),
    * and, if there is a limits panel, setUpLimitsPanel(), in that order.  Finally, if the value
    * of the applet parameter "LoadFirstExample" is not "no", and if any examples were specifed, then
    * the first ewxample is loaded.
    */
   protected void setUpMainPanel() {
      parser = new Parser(null,0);
      setUpParser();
      setUpExampleMenu();
      double[] gap = getNumericParam("Insets");
      if (gap == null || gap.length == 0 || gap[0] < 0 || gap[0] > 50)
         mainPanel.setInsetGap(3);
      else
         mainPanel.setInsetGap( (int)Math.round(gap[0]) );
      Color color;
      color = getColorParam("BackgroundColor", Color.gray);
      mainPanel.setBackground(color);
      color = getColorParam("ForegroundColor", Color.black);
      mainPanel.setForeground(color);
      canvas = new DisplayCanvas();
      double[] limits = getNumericParam("Limits");
      if (limits != null && limits.length >= 4)
         canvas.getCoordinateRect().setLimits(limits);
      if ( "yes".equalsIgnoreCase(getParameter("UseLimitsPanel", "yes")) ) {
         String h = getParameter("XName");
         if (h == null)
            h = getParameter("Variable","x");
         String v =getParameter("YName","y");
         limitsPanel = new LimitControlPanel( h+"min", h+"max", v+"min", v+"max", 0, false );
      }
      mainController = mainPanel.getController();
      setUpBottomPanel();
      setUpTopPanel();
      setUpCanvas();
      addCanvasBorder();
      if ( limitsPanel != null )
         setUpLimitsPanel();
      String loadDefault = (loadExampleButton == null)? "yes" : "no";
      if (exampleStrings != null && exampleStrings.size() > 0 &&
             ! "no".equalsIgnoreCase(getParameter("LoadFirstExample",loadDefault)))
         doLoadExample( (String)exampleStrings.elementAt(0) );
   }
   
   /**
    * This method is called by mainPanel() to set up the display canvas and add it to the
    * main panel.  The canvas already exists in the member variable canvas when this
    * method is called.  This method adds only a set of axes to the canvas, sets the
    * mainController to report errors using the canvas, and adds the canvas to the
    * CENTER of the main panel.  This method processes applet parameters "UsePanner",
    * "CanvasColor", "UseMouseZoom", and "UseOffscreenCanvas".  Typically, this
    * method will be overridden in subclasses to add more Drawable items to the
    * canvas.  In this case, super.setUpCanvas() should be called first.
    */
   protected void setUpCanvas() {
      Color color;
      color = getColorParam("CanvasColor");
      if (color != null)
         canvas.setBackground(color);
      if (! "no".equalsIgnoreCase(getParameter("UsePanner", "no")) )
         canvas.add(new Panner());
      if ( ! "no".equalsIgnoreCase(getParameter("UseGrid", "no")) ) {
         Grid g = new Grid();
         color = getColorParam("GridColor");
         if (color != null)
            g.setColor(color);
         canvas.add(g);
      }
      canvas.add(makeAxes());
      if ( ! "no".equalsIgnoreCase(getParameter("UseMouseZoom", "no")) )
         canvas.setHandleMouseZooms(true);
      if ( "yes".equalsIgnoreCase(getParameter("UseOffscreenCanvas", "yes")) )
         canvas.setUseOffscreenCanvas(true);
      mainController.setErrorReporter(canvas);
      mainPanel.add(canvas, BorderLayout.CENTER);
   }
   
   /**
    *  Construct a set of Axes, based on applet params  "AxesColor", "AxesLightColor", 
    *  "XLabel", "YLabel", "LabelColor".
    */
   protected Axes makeAxes() {
      Axes axes = new Axes();
      Color color = getColorParam("AxesColor");
      if (color != null)
         axes.setAxesColor(color);
      color = getColorParam("AxesLightColor");
      if (color != null)
         axes.setLightAxesColor(color);
      String str = getParameter("XLabel");
      if (str != null)
          axes.setXLabel(str);
      str = getParameter("YLabel");
          axes.setYLabel(str);
      color = getColorParam("LabelColor");
      if (color != null)
         axes.setLabelColor(color);
      return axes;
   }
   
   /**
    * This method is called by setUpMainPanel() to add a border to the canvas (since the border is typically
    * the last thing that should be added, on top of anything else in the canvas.  It processes the 
    * applet parameters "BorderWidth" and "BorderColor".  If the border width is zero, no border is added.
    * (The default width is 2.)
    */
   protected void addCanvasBorder() {
      int borderWidth;
      double[] bw = getNumericParam("BorderWidth");
      if (bw == null || bw.length == 0 || bw[0] > 25)
         borderWidth = 2;
      else
         borderWidth = (int)Math.round(bw[0]);
      if (borderWidth > 0)
         canvas.add( new DrawBorder( getColorParam("BorderColor", Color.black), borderWidth  ) );
   }
   
   /**
    * This method checks the applet parameter "UseFunctionInput".  If the value is anything but "no",
    * then a panel is created that contains an ExpressionInput (stored in the member variable functionInput)
    * and possibly a ComputeButton and lable for the input box.  This panel is a JCMPanel using BorderLayout.
    * In is stored in the member variable inputPanel and is added to the SOUTH position of the mainPanel.
    * The method also processes applet parameters "Function", "UseComputeButton", "ComputeButtonName", and
    * "FunctionLabel".  The ComputeButton, if it exists, is stored in the member variable computeButton.
    * Note that nothing at all is done by this method if the value of the applet parameter "UseFunctionInput" is no.
    * 
    */
   protected void setUpBottomPanel() {
      if ( "yes".equalsIgnoreCase(getParameter("UseFunctionInput", "yes")) ) {
         String func = getParameter("Function");
         String varName = xVar.getName();
         if (func == null)
            func = "abs(" + varName + " ) ^ " + varName;
         functionInput = new ExpressionInput(func,parser);
         inputPanel = new JCMPanel();
         inputPanel.setBackground( getColorParam("PanelBackground", Color.lightGray) );
         inputPanel.add(functionInput, BorderLayout.CENTER);
         if ( "yes".equalsIgnoreCase(getParameter("UseComputeButton", "yes")) ) {
            String cname = getParameter("ComputeButtonName", "New Function");
            computeButton = new Button(cname);
            inputPanel.add(computeButton, BorderLayout.EAST); 
            computeButton.addActionListener(this);
         }
         String flabel = getParameter("FunctionLabel");
         if (flabel == null)
            flabel = " f(" + varName + ") = ";
         if ( ! "none".equalsIgnoreCase(flabel) )
            inputPanel.add(new Label(flabel), BorderLayout.WEST);
         mainPanel.add(inputPanel, BorderLayout.SOUTH);
         functionInput.setOnUserAction(mainPanel.getController());
      }
   }
   
   /**
    * This method sets up the limit control panel and adds it to the main panel.  The limit control panel
    * already exists when this method is called and is stored in the member variable limitsPanel.  The
    * applet parameters "TwoLimitsColumns", "UseSetLimitsButton", "UseZoomButtons", "UseEqualizeButtons",
    * "UseRestoreButton", "PanelBackground", and "LimitsOnLeft" are processed.  The limits panel is
    * set to report its errors using the display canvas.
    */
   protected void setUpLimitsPanel() {
      limitsPanel.addCoords(canvas);
      if ( ! "no".equalsIgnoreCase(getParameter("TwoLimitsColumns", "no")) )
         limitsPanel.setUseTwoColumnsIfPossible(true);
      int buttons = 0;
      if ( "yes".equalsIgnoreCase(getParameter("UseSetLimitsButton", "yes")) )
         buttons |= LimitControlPanel.SET_LIMITS;
      if ( ! "no".equalsIgnoreCase(getParameter("UseZoomButtons", "no")) )
         buttons |= LimitControlPanel.ZOOM_IN | LimitControlPanel.ZOOM_OUT;
      if ( ! "no".equalsIgnoreCase(getParameter("UseEqualizeButton", "no")) )
         buttons |= LimitControlPanel.EQUALIZE;
      if ( ! "no".equalsIgnoreCase(getParameter("UseRestoreButton", "no")) )
         buttons |= LimitControlPanel.RESTORE;
      if (buttons != 0)
         limitsPanel.addButtons(buttons);
      limitsPanel.setBackground( getColorParam("PanelBackground", Color.lightGray) );
      if ( ! "yes".equalsIgnoreCase(getParameter("LimitsOnLeft", "no")))
         mainPanel.add(limitsPanel, BorderLayout.EAST);
      else
         mainPanel.add(limitsPanel, BorderLayout.WEST);
      limitsPanel.setErrorReporter(canvas);
   }

   /**
    * This method is called by setUpMainPanel() to add a panel at the top of the applet.
    * If there is an example menu, then the panel containing that menu is added to the
    * NORTH position of the main panel.  Otherwise, the value of the applet parameter
    * named "PanelTitle" is checked.  If this value exists, it is placed in a label at the top of the panel.
    * The color of the text on the label is given by the valur of the applet parameter
    * "TitleColor" (default is new Color(200,0,0)).
    */   
   protected void setUpTopPanel() { 
      if (exampleMenuPanel != null)
         mainPanel.add(exampleMenuPanel, BorderLayout.NORTH);
      else {
         String title = getParameter("PanelTitle");
         if (title != null) {
             Label titleLabel = new Label(title, Label.CENTER);
             titleLabel.setForeground( getColorParam("TitleForeground", new Color(200,0,0)) );
             titleLabel.setBackground( getColorParam("TitleBackground", Color.lightGray) );
             titleLabel.setFont( new Font("Serif", Font.PLAIN, 14) );
             mainPanel.add(titleLabel, BorderLayout.NORTH);
         }
      }
   }
   
   /**
    * This method processes applet parameters that specify examples for the applet and adds them
    * to a menu of examples.  If any examples exist, they are placed in a menu and a panel
    * is created to hold the menu.  The panel is stored in the instance variable exampleMenuPanel,
    * which is used in the setUpTopPanel() method.  This method is called by init().  If you don't 
    * want to process examples, you could override it to do nothing.  If you do want to process
    * examples, you have to override the doLoadExample() method to process a string from the menu.
    * Examples are specified in applet parameters named "Example", "Example1", "Example2", ....
    * The value should consist of a name for the example (which appears in the menu) followed by
    * a ";" followed by a string that defines the example.  The string that defines the example
    * is passed to the doLoadExample() method when the user loads the example.
    */
   protected void setUpExampleMenu() {
      Vector strings = new Vector();
      Vector names = new Vector();
      int ct = 0;
      String paramName = "Example";
      String param = getParameter(paramName);
      if (param == null) {
         ct++;
         paramName = "Example" + ct;
         param = getParameter(paramName);
      }
      while (true) {
         if (param == null)
            break;
         int pos = param.indexOf(';');
         if (pos < 0) {
            strings.addElement(param);
            names.addElement(param);
         }
         else {
            strings.addElement(param.substring(pos+1));
            names.addElement(param.substring(0,pos));
         }
         ct++;
         paramName = "Example" + ct;
         param = getParameter(paramName);
      }
      if (strings.size() == 0)
         return;
      exampleStrings = strings;
      exampleStrings.trimToSize();
      exampleMenuPanel = new JCMPanel();
      if ("yes".equalsIgnoreCase(getParameter("UseLoadButton","yes"))) {
         loadExampleButton = new Button("Load Example: ");
         loadExampleButton.setBackground(Color.lightGray);
         loadExampleButton.addActionListener(this);
      }
      Component list;
      if (names.size() == 1)
         list = new Label( (String)names.elementAt(0), Label.CENTER );
      else {
         exampleMenu = new Choice();
         list = exampleMenu;
         for (int i = 0; i < names.size(); i++)
            exampleMenu.add( (String)names.elementAt(i) );
         if (loadExampleButton == null)
            exampleMenu.addItemListener(this);
      }
      list.setBackground(Color.white);
      exampleMenuPanel.add(list, BorderLayout.CENTER);
      if (loadExampleButton != null)
         exampleMenuPanel.add(loadExampleButton, BorderLayout.WEST);
   }

   /**
    * This method is called by setUpMainPanel() to set up the parser to be used in the applet.
    * When it is called, a parser already esists and is stored in the member variable
    * named parser.  This method configures the parser according to the values of
    * the applet parameters "StandardFunctions", "Booleans", "OptionalStars", "OptionalParens",
    * and "Factorials".  It then looks for function defintions in applet parameters named
    * "Define", "Define1", "Define2", ...., and if any are found the functions are added
    * to the parser so that they can be used in expressions.  A function definition can
    * take a form such as "g(x)=x^2" or "fred(s,t) = 3*s + sin(t)", for example, or it
    * can be defined by a table of values.  A table function is defined using the
    * syntax decribed in the parseTableFuncDef() method.  Finally, a variable is
    * created and added to the parser using the value of the applet param "Variable",
    * with "x" as the default variable name.
    */   
   protected void setUpParser() {
      if ( "yes".equalsIgnoreCase(getParameter("StandardFunctions", "yes")) )
         parser.addOptions(Parser.STANDARD_FUNCTIONS);
      if ( "yes".equalsIgnoreCase(getParameter("Booleans", "yes")) )
         parser.addOptions(Parser.BOOLEANS);
      if ( ! "no".equalsIgnoreCase(getParameter("OptionalStars", "no")) )
         parser.addOptions(Parser.OPTIONAL_STARS);
      if ( ! "no".equalsIgnoreCase(getParameter("OptionalParens", "no")) )
         parser.addOptions(Parser.OPTIONAL_PARENS);
      if ( ! "no".equalsIgnoreCase(getParameter("Factorials", "no")) )
         parser.addOptions(Parser.FACTORIAL);
      if ( "yes".equalsIgnoreCase(getParameter("Summations", "yes")) )
         parser.add(new SummationParser());
      String str = getParameter("Define");
      if (str != null)
         define(str);
      int ct = 1;
      while (true) {
         str = getParameter("Define" + ct);
         if (str == null)
            break;
         define(str);
         ct++;
      }
      xVar = new Variable( getParameter("Variable", "x") );
      parser.add( xVar );
   }
   
   /**
    * This method is called when the user loads an example from the example menu (if any).
    * The parameter is the string that defines the example.  By default, this method does
    * nothhing.  It should be overridden to load the example.
    */
   protected void doLoadExample(String example) {
	   
   }
   
   /**
    * This method, which is empty in the GenericGraphApplet class, can be defined in a subclass
    * to set default values for applet params that are different from the ones provided in
    * this class.  The method should create a new HashTable, assign it to the instance
    * variable parameterDefaults, and then add name/value pairs to the hash table.  This
    * method is called at the very beginning of the init() method.
    */
   protected void setUpParameterDefaults() {
   }
   
   /**
    * Override the standard applet method getParameter(String) so that when no param value
    * is provided in the applet tag, and if parameterDefaults is non-null, it will check for a value
    * the parameterDefaults.  (The parameterDefaults instance variable can be defined in the 
    * setUpParameterDefaults() method.)
    */
   public String getParameter(String paramName) {
      String val = super.getParameter(paramName);
      if (val == null && parameterDefaults != null)
         val = (String)parameterDefaults.get(paramName);
      return val;
   }
   
   /**
    *  Get the value of an applet parameter, but return a default if the value is null.
    *
    * @param paramName The name of the applet parameter.
    * @param defaultValue The value to be returned if getParameter(paramName) is null.
    */
   protected String getParameter(String paramName, String defaultValue) {
      String val = getParameter(paramName);
      return val == null ? defaultValue : val;
   }
   
   /**
    * Get The value of an applet parameter that consists of a list of numbers.
    * The parameter value, if any, is parsed and returned as array of double values.
    * The numbers can be separated by commas, spaces, tabs, or semicolons.  If there
    * is a parse error, null is returned.
    */
   protected double[] getNumericParam(String paramName) {
      return getNumericParam(paramName, null);
   }

   /**
    * Get The value of an applet parameter that consists of a list of numbers.
    * The parameter value, if any, is parsed and returned as array of double values.
    * The numbers can be separated by commas, spaces, tabs, or semicolons.
    *
    * @param paramName The name of the applet parameter.
    * @param defaultValue The value to be returned if getParameter(paramName) is null or is not a valid list of numbers.
    */
   protected double[] getNumericParam(String paramName, double[] defaults) {
      String data = getParameter(paramName);
      if (data == null)
         return defaults;
      StringTokenizer tokenizer = new StringTokenizer(data," \t,;");
      int count = tokenizer.countTokens();
      if (count == 0) 
         return defaults;
      double[] numbers = new double[count];
      for (int i = 0; i < count; i++) {
         try {
            Double d = new Double(tokenizer.nextToken());
            numbers[i] = d.doubleValue();
         }
         catch (NumberFormatException e) {
            return defaults;
         }
      }
      return numbers;
   }
   
   /**
    * Get The value of an applet parameter that specifies a color.  The color can be specfied
    * as a list of three numbers in the range 0 to 255 or by one of the standard color names
    * ("black", "red", "blue", "green", "yellow", "cyan", "magenta", "gray", "darkgray",
    * "lightgray", "pink", "orange", "white").  Color names are not case sensitive.  If 
    * the value of the applet parameter is null does not specify a legal color, then
    * the return value is null.
    */
   protected Color getColorParam(String data) {
      return getColorParam(data,null);
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
         double[] nums = getNumericParam(paramName,null);
         if (nums == null || nums.length < 3)
            return defaultColor;
         if (nums[0] < 0 || nums[0] > 255 || nums[1] < 0 || nums[1] > 255 || nums[2] < 0 || nums[2] > 255)
            return defaultColor;
         return new Color((int)Math.round(nums[0]), (int)Math.round(nums[1]), (int)Math.round(nums[2]));
      }
   }
   

   // ------------------ Implementation details --------------------------------

   /**
    *  Releases the resources used by the display canvas when the applet is stopped.
    */
   public void stop() {
      if (canvas != null && frame == null)
         canvas.releaseResources();
   }
   
   /**
    *  Closes the frame (if any) when the applet is destroyed.
    */
   public synchronized void destroy() {
      if (frame != null)
         frame.dispose();
   }

   private Choice exampleMenu;
   private Button loadExampleButton;
   private Button launchButton;
   private String launchButtonName;
   private Frame frame;
   private Vector exampleStrings;
   
   private String[] colorNames = { "black", "red", "blue", "green", "yellow",
                                          "cyan", "magenta", "gray", "darkgray",
                                          "lightgray", "pink", "orange", "white" };
   private Color[]  colors = { Color.black, Color.red, Color.blue, Color.green, Color.yellow,
                                      Color.cyan, Color.magenta, Color.gray, Color.darkGray,
                                      Color.lightGray, Color.pink, Color.orange, Color.white };
   
   private void define(String str) {
         // Parse a function definition string from one of the applet parameters named
         // "Define", "Define1", "Define2", ...
      String funcDef = str;
      String name, def;
      String[] paramNames;
      int pos;
      try {
         pos = funcDef.indexOf("=");
         if (pos < 0)
            throw new ParseError("Missing \"=\"", null);
         def = funcDef.substring(pos+1).trim();
         funcDef = funcDef.substring(0,pos);
         if (def.toLowerCase().startsWith("table")) {
            name = funcDef;
            pos = name.indexOf("(");
            if (pos > 0)
               name = name.substring(0,pos).trim();
            TableFunction tf = parseTableFuncDef(def);
            tf.setName(name);
            parser.add(tf);
         }
         else {
            pos = funcDef.indexOf("(");
            if (pos < 0)
               throw new ParseError("Missing \"(\"", null);
            name = funcDef.substring(0,pos).trim();
            if (name.length() == 0)
               throw new ParseError("Missing function name", null);
            funcDef = funcDef.substring(pos+1);
            pos = funcDef.indexOf(")");
            if (pos < 0)
               throw new ParseError("Missing \")\"", null);
            funcDef = funcDef.substring(0,pos).trim();
            if (funcDef.length() == 0)
               throw new ParseError("Missing parameter names", null);
            StringTokenizer toks = new StringTokenizer(funcDef,",");
            int ct = toks.countTokens();
            paramNames = new String[ct];
            for (int i = 0; i < ct; i++)
               paramNames[i] = toks.nextToken();
            new ExpressionFunction(name, paramNames, def, parser);
         }
      }
      catch (ParseError e) {
         throw new IllegalArgumentException("Error parsing function \"" + str + "\":" + e.getMessage());
      }
   }
   
   /**
    *  Create a TableFunction from a string.  The string can start with the word "table",
    *  which is ignored.  The next item can optionally be one of the table styles "smooth",
    *  "linear", or "step".  The default is "smooth".  Then values must be specified.  If the
    *  next word is "intervals", it can be followed by numbers giving the number of
    *  intervals, the minumum x, and the maximum x, and the y-values for up to (intervals+1) points
    *  evenly distributed between xmin and xmax (unassigned y-values will be zero). 
    *  If no data is specified, a table function with
    *  6 intervals between xmin = -5 and xmax = 5  and all y-values zero is created.   
    *  If "intervals" is not specified, the
    *  remaining items are numbers giving pairs of (x,y)-values.  Items can be separated
    *  spaces, tabs, and commas.  A ParseError will be thrown if the data is illegal.
    */
   protected TableFunction parseTableFuncDef(String def) {
      try {
         TableFunction func = new TableFunction();
         StringTokenizer toks = new StringTokenizer(def, " \t,");
         String tok = null;
         if (toks.hasMoreTokens()) {
            tok = toks.nextToken();
            if (tok.equalsIgnoreCase("table") && toks.hasMoreTokens())
               tok = toks.nextToken();
         }
         if ("step".equalsIgnoreCase(tok)) {
            func.setStyle(TableFunction.STEP);
            if (toks.hasMoreTokens())
               tok = toks.nextToken();
         }
         else if ("linear".equalsIgnoreCase(tok)) {
            func.setStyle(TableFunction.PIECEWISE_LINEAR);
            if (toks.hasMoreTokens())
               tok = toks.nextToken();
         }
         else if ("smooth".equalsIgnoreCase(tok) && toks.hasMoreTokens()) {
            if (toks.hasMoreTokens())
               tok = toks.nextToken();
         }
         boolean useIntervals = "intervals".equalsIgnoreCase(tok);
         if (useIntervals && toks.hasMoreTokens())
            tok = toks.nextToken();
         double[] nums = new double[toks.countTokens() + 1];
         try {
            nums[0] = (new Double(tok)).doubleValue();
         }
         catch (NumberFormatException e) {
            throw new ParseError("Unexpected token \"" + tok + "\".",null);
         }
         try {
            for (int i = 1; i < nums.length; i++)
                nums[i] = (new Double(toks.nextToken())).doubleValue();
         }
         catch (NumberFormatException e) {
            throw new ParseError("Illegal number.", null);
         }
         if (useIntervals) {
             int ct = (nums.length == 0)? 6 : (int)Math.round(nums[0]);
             if (ct < 1 || ct > 500)
                ct = 6;
             double xmin = (nums.length < 2)? -5 : nums[1];
             double xmax = (nums.length < 3)? xmin + 10 : nums[2];
             if (xmax <= xmin)
                throw new ParseError("xmax in table must be greater than xmin", null);
             func.addIntervals(ct,xmin,xmax);
             for (int i = 3; i < nums.length && (i-3) <= ct; i++) {
                if (i-3 < ct)
                   func.setY(i-3,nums[i]);
             }
         }
         else {
            if (nums.length < 4)
               throw new ParseError("At least two points must be provided for table function.", null);
            if (nums.length % 2 == 1)
               throw new ParseError("Can't define an table function with an odd number of values.", null);
            for (int i = 0; i < nums.length/2; i++) {
               func.addPoint(nums[2*i],nums[2*i+1]);
            }
         }
         return func;
      }
      catch (Exception e) {
         throw new ParseError("Error while parsing table function: " + e.getMessage(), null);
      }
   }
   
   /**
    *  Respond when user clicks a button; not meant to be called directly.
    */ 
   public void actionPerformed(ActionEvent evt) {
      Object source = evt.getSource();
      if (source == computeButton && computeButton != null)
         mainController.compute();
      else if (source == launchButton && launchButton != null)
         doLaunchButton();
      else if (source == loadExampleButton && exampleStrings != null) {
         if (exampleStrings.size() == 1)
            doLoadExample( (String)exampleStrings.elementAt(0) );
         else
            doLoadExample( (String)exampleStrings.elementAt(exampleMenu.getSelectedIndex()) );
      }
   }
   
   /**
    *  Respond when user chooses an example from the example menu.  (This will only
    *  happen if the param UseLoadButton is not set to "yes".)
    */
   public void itemStateChanged(ItemEvent evt) {
      if (evt.getSource() == exampleMenu)
         doLoadExample( (String)exampleStrings.elementAt(exampleMenu.getSelectedIndex()) );
   }
   
   private synchronized void doLaunchButton() {
      launchButton.setEnabled(false);
      if (frame == null) {
         frame = new Frame(frameTitle);
         mainPanel = new JCMPanel();
         try {
            setUpMainPanel();
            frame.add(mainPanel, BorderLayout.CENTER);
         }
         catch (Throwable e) {
             System.out.println("Error while opening window:");
             e.printStackTrace();
             TextArea message = new TextArea("An error occurred while setting up this window:\n\n");
             message.append(e.toString());
             frame.add(message, BorderLayout.CENTER);
         }
         frame.addWindowListener( new WindowAdapter() {
               public void windowClosing(WindowEvent evt) {
                  frame.dispose();
               }
               public void windowClosed(WindowEvent evt) {
                  frameClosed();
               }
            } );
         double[] frameSize = getNumericParam("FrameSize");
         if (frameSize == null || frameSize.length < 2 || frameSize[0] < 100
               || frameSize[0] > 800 || frameSize[1] < 100 || frameSize[1] > 600)
            frame.setSize(defaultFrameSize[0], defaultFrameSize[1]);
         else
            frame.setSize((int)Math.round(frameSize[0]), (int)Math.round(frameSize[1]));
         frame.setLocation(50,50);
         frame.show();
         launchButton.setLabel("Close Window");
         launchButton.setEnabled(true);
      }
      else {
         frame.dispose();
      }
   }
   
   private synchronized void frameClosed() {
      frame = null;
      launchButton.setLabel(launchButtonName);
      launchButton.setEnabled(true);
      mainPanel = null;
      canvas = null;
      limitsPanel = null;
      inputPanel = null;
      exampleMenuPanel = null;
      loadExampleButton = null;
      computeButton = null;
      parser = null;
   }
   
} // end class GenericGraphApplet
