/******************************************************************************
 * Huijie Qiao
 *
 * Project:  Niche Analyst
 * Purpose:  
 * Created date: Apr 10, 2012 4:25:44 PM
 * Author:   Huijie Qiao
 *
 ******************************************************************************
 * Copyright (c) 2012, Huijie Qiao
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 ****************************************************************************/


package org.ku.nicheanalyst.ui.display.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.jdom.JDOMException;
import org.ku.nicheanalyst.common.CommonFun;
import org.ku.nicheanalyst.common.Message;
import org.ku.nicheanalyst.dataset.GompertzCurveParameters;
import org.ku.nicheanalyst.ui.display.Displayer;
import org.ku.nicheanalyst.ui.display.component.ui.LabeledTextField;
import org.ku.nicheanalyst.ui.filefilters.XMLFileFilter;

import edu.hws.jcm.awt.Controller;
import edu.hws.jcm.awt.ExpressionInput;
import edu.hws.jcm.awt.JCMPanel;
import edu.hws.jcm.awt.Tie;
import edu.hws.jcm.awt.VariableInput;
import edu.hws.jcm.awt.VariableSlider;
import edu.hws.jcm.data.Function;
import edu.hws.jcm.data.Parser;
import edu.hws.jcm.data.Value;
import edu.hws.jcm.data.Variable;
import edu.hws.jcm.draw.Axes;
import edu.hws.jcm.draw.CoordinateRect;
import edu.hws.jcm.draw.Crosshair;
import edu.hws.jcm.draw.DisplayCanvas;
import edu.hws.jcm.draw.Graph1DStepFunction;
import edu.hws.jcm.draw.LimitControlPanel;

/**
 * @author Huijie Qiao
 *
 */
public class CurveForm extends JDialog {
	private Controller controller;
	private ExpressionInput input1;
	private ExpressionInput input2;
	private Graph1DStepFunction graph;
	private Parser parser;
	private Variable x;
	private Function func1;
	private Function func2;
	private DisplayCanvas canvas;
	private LabeledTextField inputText1;
	private LabeledTextField b1;
	private LabeledTextField c1;
	private LabeledTextField b2;
	private LabeledTextField c2;
	private LabeledTextField inputText2;
	private LabeledTextField sampling_frequency;
	private JLabel errorLabel;
	private LimitControlPanel limits;
	private GompertzCurveParameters parameters;
	private VariableInput xInput;
	private Controller subController = new Controller();
	private Crosshair cross;
	private Displayer theApp;
	private VariableSlider xSlider;
	public CurveForm(GompertzCurveParameters parameters, Displayer theApp){
		
		super();
		this.theApp = theApp;
		this.parameters = null;
        this.parameters = parameters;
        if (this.parameters==null){
        	this.parameters = new GompertzCurveParameters(-0.001, -0.0005, -6, -0.00005, 1000, 
        			null, null, "1 - e ^ (#b# * e ^ (#c# * t * sampling_frequency))", "e ^ (#b# * e ^ (#c# * t * sampling_frequency)) - (e ^ (#b#))", 
        			0, -100, 100);
        }
        setSize(new Dimension(800, 560));
        getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        parser = new Parser();
        x = new Variable("t"); 
        parser.add(x); 
//        TableFunction tf = parseTableFuncDef();
//        tf.setName("F1");
//        parser.add(tf);
        
        
        canvas = new DisplayCanvas();
        canvas.setUseOffscreenCanvas(false);
        canvas.setHandleMouseZooms(true);
        limits = new LimitControlPanel(
        		Message.getString("cold_years"),
        		Message.getString("warm_years"),
        		Message.getString("value_min"),
        		Message.getString("value_max"),
        		LimitControlPanel.SET_LIMITS, false);
        limits.addButtons(LimitControlPanel.ZOOM_IN);
        limits.addButtons(LimitControlPanel.ZOOM_OUT);
        limits.setLimits(new double[]{
        		this.parameters.getCold_years(), 
        		this.parameters.getWarm_years(), 
        		-0.1, 1.1});
        limits.addCoords(canvas);
//        ExpressionInput input = new ExpressionInput("sin(x)+2*cos(3*x)", parser);
        graph = new Graph1DStepFunction();
        
        
        JCMPanel main = new JCMPanel();
        main.add(canvas, BorderLayout.CENTER);  // Add the DisplayCanvas to the panel.
        JPanel panel_input = new JPanel();
        panel_input.setLayout(new BoxLayout(panel_input, BoxLayout.Y_AXIS));
        JPanel panel_line = new JPanel();
        panel_line.setLayout(new BoxLayout(panel_line, BoxLayout.X_AXIS));
        inputText1 = new LabeledTextField(Message.getString("cold_func"), this.parameters.getFunc1Str());
        b1 = new LabeledTextField("b: ", String.valueOf(this.parameters.getB1()));
        c1 = new LabeledTextField("c: ", String.valueOf(this.parameters.getC1()));
        panel_line.add(inputText1);
        panel_line.add(b1);
        panel_line.add(c1);
        panel_input.add(panel_line);
        
        panel_line = new JPanel();
        panel_line.setLayout(new BoxLayout(panel_line, BoxLayout.X_AXIS));
        inputText2 = new LabeledTextField(Message.getString("warm_func"), this.parameters.getFunc2Str());
        b2 = new LabeledTextField("b: ", String.valueOf(this.parameters.getB2()));
        c2 = new LabeledTextField("c: ", String.valueOf(this.parameters.getC2()));
        panel_line.add(inputText2);
        panel_line.add(b2);
        panel_line.add(c2);
        panel_input.add(panel_line);
        
        
        
        sampling_frequency = new LabeledTextField(Message.getString("sampling_frequency"), String.valueOf(this.parameters.getSamplingFrequency()));
       
        JPanel panel_button_east = new JPanel();
        panel_button_east.setLayout(new BoxLayout(panel_button_east, BoxLayout.Y_AXIS));
        final JButton setfunctionButton = new JButton();
        setfunctionButton.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                setfunButtonPressed();
            }
        });
        setfunctionButton.setAlignmentY(CENTER_ALIGNMENT);
        setfunctionButton.setAlignmentX(CENTER_ALIGNMENT);
        setfunctionButton.setText(Message.getString("setfunction"));
        
        
        final JButton getrangeButton = new JButton();
        getrangeButton.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
            	getrangeButtonPressed();
            }
        });
        getrangeButton.setAlignmentY(CENTER_ALIGNMENT);
        getrangeButton.setAlignmentX(CENTER_ALIGNMENT);
        getrangeButton.setText(
        		String.format(
        				Message.getString("getrange"), 
        				Message.getString("cold_years"), 
        				Message.getString("warm_years")));
        errorLabel = new JLabel("");
        errorLabel.setForeground(Color.red);
        JPanel errorPanel = new JPanel();
        errorPanel.add(errorLabel);
        panel_button_east.add(errorPanel);
        panel_button_east.add(getrangeButton);
        panel_button_east.add(setfunctionButton);
        
        
        
        
        main.add(panel_input, BorderLayout.SOUTH);    // Add the ExprssionInput.
        JPanel paneleast = new JPanel();
        paneleast.setLayout(new BoxLayout(paneleast, BoxLayout.Y_AXIS));
        paneleast.add(sampling_frequency);
        
        
        Value xMin = canvas.getCoordinateRect().getValueObject(CoordinateRect.XMIN);
        Value xMax = canvas.getCoordinateRect().getValueObject(CoordinateRect.XMAX);
        xSlider = new VariableSlider(xMin, xMax);
        xSlider.setOnUserAction(subController);
        
        xInput = new VariableInput(Message.getString("starting_point"), String.valueOf(this.parameters.getStartingPoint()));
        xInput.setOnTextChange(subController);
        xSlider.setVal(xInput.getVal());
        subController.add(xSlider);
        subController.add(xInput);
        subController.add( new Tie(xSlider,xInput) );
        paneleast.add(xSlider);
        paneleast.add(xInput.withLabel());
        
        
        paneleast.add(limits);
        paneleast.add(panel_button_east);
        
        main.add(paneleast, BorderLayout.EAST);    // Add the LimitControlPanel.
        main.setInsetGap(3);
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(main, BorderLayout.CENTER);
        add(panel);
        Axes axes = new Axes(String.format("Years (* %s)", Message.getString("sampling_frequency")), "value");
        axes.setYAxisPosition(Axes.BOTTOM);
        axes.setXAxisPosition(Axes.LEFT);
        canvas.add(axes);
        canvas.add(graph);
        
        
        
        
        canvas.getCoordinateRect().setOnChange(subController);
        
        
        
        redraw();
        
        controller = main.getController();
        controller.setErrorReporter(canvas);
//        limits.setErrorReporter(canvas); 
        main.gatherInputs();
        
        JPanel panel_button = new JPanel();
        panel_button.setLayout(new BoxLayout(panel_button, BoxLayout.X_AXIS));
        
        
        final JButton loadButton = new JButton();
        loadButton.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                try {
					loadButtonPressed();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (JDOMException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}
            }
        });
        loadButton.setAlignmentY(CENTER_ALIGNMENT);
        loadButton.setAlignmentX(CENTER_ALIGNMENT);
        loadButton.setText(Message.getString("load_parameters"));
        panel_button.add(loadButton);
        
        final JButton applyButton = new JButton();
        applyButton.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                applyButtonPressed();
            }
        });
        applyButton.setAlignmentY(CENTER_ALIGNMENT);
        applyButton.setAlignmentX(CENTER_ALIGNMENT);
        applyButton.setText(Message.getString("apply_parameters"));
        panel_button.add(applyButton);
        
        
        final JButton cancelButton = new JButton();
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                cancelButtonPressed();
            }
        });
        cancelButton.setAlignmentY(CENTER_ALIGNMENT);
        cancelButton.setAlignmentX(CENTER_ALIGNMENT);
        cancelButton.setText(Message.getString("close"));
        panel_button.add(cancelButton);

        add(panel_button);
        
        setModal(true);
        
	}
	protected void loadButtonPressed() throws IOException, JDOMException {
		JFileChooser fc = new JFileChooser();
		fc.setDialogTitle(Message.getString("select_a_parameter_file"));
		fc.setSelectedFile(new File(theApp.getLastFolder()));
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fc.setFileFilter(new XMLFileFilter());
		int returnVal = fc.showOpenDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			String target = fc.getSelectedFile().getAbsolutePath();
			theApp.setLastFolder(target);
			File f = new File(target);
			if (f.exists()){
				this.parameters = new GompertzCurveParameters(target);
				this.b1.setText(String.valueOf(this.parameters.getB1()));
				this.b2.setText(String.valueOf(this.parameters.getB2()));
				this.c1.setText(String.valueOf(this.parameters.getC1()));
				this.c2.setText(String.valueOf(this.parameters.getC2()));
				this.inputText1.setText(this.parameters.getFunc1Str());
				this.inputText2.setText(this.parameters.getFunc2Str());
				
				this.sampling_frequency.setText(String.valueOf(this.parameters.getSamplingFrequency()));
				
				this.limits.setLimits(new double[]{
						this.parameters.getCold_years(), 
		        		this.parameters.getWarm_years(), 
		        		-0.1, 1.1});
				
				this.xInput.setText(String.valueOf(this.parameters.getStartingPoint()));
				this.redraw();
			}
		}
		
	}
	protected void applyButtonPressed() {
		getFunctions();
		parameters = new GompertzCurveParameters(
				b1.getDouble(), 
				c1.getDouble(), 
				b2.getDouble(), 
				c2.getDouble(), 
				sampling_frequency.getInt(), 
				func1, func2,
				inputText1.getText(),
				inputText2.getText(),
				Double.valueOf(xInput.getVal()).intValue(),
				Double.valueOf(limits.getLimits()[0]).intValue(),
				Double.valueOf(limits.getLimits()[1]).intValue());
		setVisible(false);
		
	}
	protected void getrangeButtonPressed() {
		getFunctions();
        
        int maxstep = 10000;
        int coldrange = 1;
        int warmrange = -1;
        for (int i=0;i<maxstep;i++){
        	
        	if (coldrange==1){
        		double value = func1.getVal(new double[]{-1 * i});
	        	if (value>=1){
	        		coldrange = -1 * i;
	        	}
	        	if (CommonFun.equal(value, 1, 100)){
	        		coldrange = -1 * i;
	        	}
        	}
        	if (warmrange==-1){
	        	double value = func2.getVal(new double[]{i});
	        	if (value>=1){
	        		warmrange = i;
	        	}
	        	if (CommonFun.equal(value, 1, 100)){
	        		warmrange = i;
	        	}
        	}
        	if ((coldrange!=1)&&(warmrange!=-1)){
        		break;
        	}
        }
        errorLabel.setText("");
        if ((coldrange!=1)&&(warmrange!=-1)){
        	
        	this.limits.setLimits(new double[]{coldrange, warmrange, -0.1, 1.1});
        	canvas.getCoordinateRect().setLimits(new double[]{coldrange, warmrange, -0.1, 1.1});
        }else{
        	errorLabel.setText(Message.getString("norange"));
        }
        redraw();
	}
	private void getFunctions() {
		String funcstr = inputText1.getText();
		funcstr = funcstr.replace("#b#", b1.getText());
		funcstr = funcstr.replace("#c#", c1.getText());
		funcstr = funcstr.replace("sampling_frequency", sampling_frequency.getText());
		input1 = new ExpressionInput(funcstr, parser);
        func1 = input1.getFunction(x);
        
        funcstr = inputText2.getText();
		funcstr = funcstr.replace("#b#", b2.getText());
		funcstr = funcstr.replace("#c#", c2.getText());
		funcstr = funcstr.replace("sampling_frequency", sampling_frequency.getText());
        input2 = new ExpressionInput(funcstr, parser);
        func2 = input2.getFunction(x);
		
	}
	protected void setfunButtonPressed() {
		redraw();
	}
	
	private void cancelButtonPressed() {
		parameters = null;
		this.dispose();
    }
	private void redraw(){
		getFunctions();
		
    	canvas.getCoordinateRect().setLimits(this.limits.getLimits());
    	
        graph.setFunction(func1, func2, 0);
        if (cross!=null){
        	canvas.remove(cross);
        }
        cross = new Crosshair(xInput, func1, func2);
        
    	canvas.add(cross, 0);
        subController.add(cross);
        
        
        
	}
	public GompertzCurveParameters getParemeters() {
		return parameters;
	}
	
	
}
