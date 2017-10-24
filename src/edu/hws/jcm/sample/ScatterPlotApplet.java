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

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.applet.Applet;

/**
 * A ScatterPlotApplet shows a scatter plot of data from a DataTableInput.
 * The user can enter the data in a two-column table that is shown in
 * the applet.  It is also possible to configure the applet with a menu
 * of file names.  These files, which must be in the same directory as
 * the Web page on which the applet appears, will appear in a menu.
 * A file can contain data for the table, with two numbers per line.
 * When the user loads the file, the data replaces the data in the table.
 */

public class ScatterPlotApplet extends Applet implements ActionListener {

   private Frame frame;       // If non-null, a separate window.
   private String frameTitle; // Title for the separate window.
   private Button launchButton;  // If non-null, then clicking this buttons opens a separate window.
   private String launchButtonName;  // Name for the launch button.
   
   private DataTableInput table;    //  The table for input of data.
   private ScatterPlot scatterPlot; //  The scatter plot of the data.
   private DisplayCanvas canvas;    //  The DisplayCanvas on which the plot is drawn.
   private Button loadFileButton;   //  When clicked, a data file is loaded.
   private Choice fileMenu;         //  Pop-up menu containing names of functions.
   private String[] fileNames;      //  Names of data files associated with menu entries.
   private Controller mainController;  // Controller from the main JCMPanel.

   /**
    * The init() method is called by the system to set up the applet. 
    * If the applet does not appear as a button, then init() creates the main panel of the applet
    * and calls setUpMainPanel to set it up.
    */
   public void init() {
      frameTitle = getParameter("FrameTitle"); // Get title to be used for separate window, if any.
      if (frameTitle == null) {
         frameTitle = "Scatter Plots";
         int pos = frameTitle.lastIndexOf('.');
         if (pos > -1)
            frameTitle =  frameTitle.substring(pos+1);
      }
      setLayout(new BorderLayout());
      int height = getSize().height;
      launchButtonName = getParameter("LaunchButtonName");
      if ( (height > 0 && height <= 50) || launchButtonName != null) {
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
   public Panel makeMainPanel() {
   
      // Make the main panel
   
      JCMPanel panel = new JCMPanel(2);
      mainController = panel.getController();
      panel.setBackground(new Color(0,0,180));
      panel.setInsetGap(2);
      setLayout(new BorderLayout());
      
      // Make a DataInputTable with two columns
      
      table = new DataTableInput(null, 2);
      table.setColumnName(0, getParameter("ColumnName1", "X"));
      table.setColumnName(1, getParameter("ColumnName2", "Y"));
      table.setThrowErrors(true);
      if ( "yes".equalsIgnoreCase(getParameter("ShowColumnTitles","yes")))
         table.setShowColumnTitles(true);
      if ( "yes".equalsIgnoreCase(getParameter("ShowRowNumbers","yes")))
         table.setShowRowNumbers(true);
                                 
      // Make input boxes for getting expressions that can include
      // the variables associated with the table.  Initially, the 
      // expressions are just the column names.

      Parser parser = new Parser();
      table.addVariablesToParser(parser);
      ExpressionInput input1 = new ExpressionInput(table.getColumnName(0),parser);
      input1.setOnUserAction(mainController);
      ExpressionInput input2 = new ExpressionInput(table.getColumnName(1),parser);
      input2.setOnUserAction(mainController);
      
      // Make a scatter plot that graphs the first expressiong vs. the second expression.

      scatterPlot = new ScatterPlot(table, input1.getExpression(), input2.getExpression());
      if ( ! "yes".equalsIgnoreCase(getParameter("ShowRegressionLine","yes")))
        scatterPlot.setShowRegressionLine(false);
      if ( ! "yes".equalsIgnoreCase(getParameter("MissingValueIsError","yes")))
        scatterPlot.setMissingValueIsError(false);
        
      // Create the display canvas where the scater plot will be shown.

      canvas = new DisplayCanvas();
      canvas.add(new Axes());
      canvas.add(scatterPlot);
      mainController.setErrorReporter(canvas);
      
      // A compute button to recompute everything.
      
      ComputeButton computeButton = new ComputeButton("Update Display");
      computeButton.setOnUserAction(mainController);
      computeButton.setBackground(Color.lightGray);
      
      // A menu of files that can be loaded.  If no filenames are provided as
      // applet parameters, then menu is null.
      
      Panel menu = makefileMenu();
      
      // Lay out the components in the applet.
      
      JCMPanel inputPanel = null;
      Panel bottom = null;  //might not be a JCMPanel
      if ( "yes".equalsIgnoreCase(getParameter("UseExpressionInputs","yes"))) {
         inputPanel = new JCMPanel(1,2);
         inputPanel.setBackground(Color.lightGray);
         JCMPanel leftInput = new JCMPanel();
         leftInput.add(new Label("  Plot:  "), BorderLayout.WEST);
         leftInput.add(input1, BorderLayout.CENTER);
         inputPanel.add(leftInput);
         JCMPanel rightInput = new JCMPanel();
         rightInput.add(new Label(" versus: "), BorderLayout.WEST);
         rightInput.add(input2, BorderLayout.CENTER);
         inputPanel.add(rightInput);
         bottom = new JCMPanel(new BorderLayout(12,3));
         bottom.add(inputPanel, BorderLayout.CENTER);
         bottom.add(computeButton, BorderLayout.EAST);
      }
      
      if ( scatterPlot.getShowRegressionLine() && "yes".equalsIgnoreCase(getParameter("ShowStats","yes")) ) {
            // Make a display label to show some statistics about the data.
         DisplayLabel dl = new DisplayLabel(
               "Slope = #;  Intercept = #;  Correlation = #",
                new Value[] { scatterPlot.getValueObject(ScatterPlot.SLOPE), 
                              scatterPlot.getValueObject(ScatterPlot.INTERCEPT), 
                              scatterPlot.getValueObject(ScatterPlot.CORRELATION) }
            );
         dl.setAlignment(Label.CENTER);
         dl.setBackground(Color.lightGray);
         dl.setForeground(new Color(200,0,0));
         dl.setFont(new Font("Serif",Font.PLAIN,14));
         if (bottom != null) 
            bottom.add(dl, BorderLayout.SOUTH);
         else {
            bottom = new JCMPanel(new BorderLayout(12,3));
            bottom.add(dl, BorderLayout.CENTER);
            bottom.add(computeButton, BorderLayout.EAST);
         }
      }
      
      if (bottom == null) {
         if (menu != null)
            menu.add(computeButton, BorderLayout.EAST);
         else {
            bottom = new Panel();
            bottom.add(computeButton);
         }
      }
      
      panel.add(canvas, BorderLayout.CENTER);
      panel.add(table, BorderLayout.WEST);
      if (bottom != null)
         panel.add(bottom, BorderLayout.SOUTH);
      if (menu != null)
         panel.add(menu, BorderLayout.NORTH);
      else {
         String title = getParameter("PanelTitle");
         if (title != null) {
            Label pt = new Label(title, Label.CENTER);
            pt.setBackground(Color.lightGray);
            pt.setForeground(new Color(200,0,0));
            pt.setFont(new Font("Serif",Font.PLAIN,14));
            panel.add(pt, BorderLayout.NORTH);
         }
      }
         
      return panel;
      
   } // end makeMainPanel()
   
   
   private Panel makefileMenu() {
         // If the applet tag contains params named "File", "File1", "File2", ..., use
         // their values to make a file menu.  If the value of the param contains a ";",
         // then the first part, up to the ";", goes into the menu and the second part
         // is the name of the file.  If there is no ";", then the entire value is
         // shown in the menu and is also used as the name of the file.  The actual
         // files must be in the same directory as the Web page that contains the applet.
      Vector names = new Vector();
      fileMenu = new Choice();
      String file = getParameter("File");
      int ct = 1;
      if (file == null) {
         file = getParameter("File1");
         ct = 2;
      }
      while (file != null) {
         file = file.trim();
         int pos = file.indexOf(";");
         String menuEntry;
         if (pos == -1)
            menuEntry = file;
         else {
            menuEntry = file.substring(0,pos).trim();
            file = file.substring(pos+1).trim();
         }
         names.addElement(file);
         fileMenu.add(menuEntry);
         file = getParameter("File" + ct);
         ct++;
      }
      if (names.size() == 0) {
         fileMenu = null;
         return null;
      }
      else {
         fileNames  = new String[names.size()];
         for (int i = 0; i < names.size(); i++)
            fileNames[i] = (String)names.elementAt(i);
         Panel p = new Panel();
         p.setBackground(Color.lightGray);
         p.setLayout(new BorderLayout(5,5));
         p.add(fileMenu,BorderLayout.CENTER);
         loadFileButton = new Button("Load Data File: ");
         loadFileButton.addActionListener(this);
         p.add(loadFileButton,BorderLayout.WEST);
         fileMenu.setBackground(Color.white);
         return p;
      }
   }
   
   private void doLoadFile(String name) {
        // Load the file from the same directory as the Web page and put the data
        // from the file into the table.  The file should contain two numbers on
        // each line.
      InputStream in;
      try {
         URL url = new URL(getDocumentBase(), name);
         in = url.openStream();
      }
      catch (Exception e) {
         canvas.setErrorMessage(null,"Unable to open file named \"" + name + "\": " + e);
         return;
      }
      Reader inputReader = new InputStreamReader(in);
      try {
         table.readFromStream(inputReader);
         inputReader.close();
      }
      catch (Exception e) {
         canvas.setErrorMessage(null,"Unable to get data from file \"" + name + "\": " + e.getMessage());
         return;
      }
      mainController.compute();
   }

   /**
    *  Respond when user clicks a button; not meant to be called directly.
    *  This opens and closes the separate window.
    */ 
   synchronized public void actionPerformed(ActionEvent evt) {
      Object source = evt.getSource();
      if (loadFileButton != null && source == loadFileButton) {
         doLoadFile( fileNames[fileMenu.getSelectedIndex()] );
      }
      else if (source == launchButton && launchButton != null) {
            // Open or close separate frame.
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
    *  Return the applet parameter with a given param name, but if no
    *  such applet param exists, return a default value instead.
    */
   protected String getParameter(String paramName, String defaultValue) {
       String val = getParameter(paramName);
       return (val == null)? defaultValue : val;
   }
   
} // end class ScatterPlotApplet

