/******************************************************************************
 * Huijie Qiao
 *
 * Project:  Niche Analyst
 * Purpose:  
 * Created date: Aug 11, 2012 2:24:10 PM
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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;

import org.apache.commons.math3.distribution.BinomialDistribution;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.distribution.PoissonDistribution;
import org.apache.commons.math3.distribution.UniformRealDistribution;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.ku.nicheanalyst.common.CommonFun;
import org.ku.nicheanalyst.common.Message;
import org.ku.nicheanalyst.maps.objects.GeoTiffObject;
import org.ku.nicheanalyst.ui.display.Displayer;
import org.ku.nicheanalyst.ui.display.component.ui.JNumberTextField;
import org.ku.nicheanalyst.ui.filefilters.RasterFileFilter;

/**
 * @author Huijie Qiao
 *
 */
public class OccurrenceMappingForm extends JDialog implements ActionListener {
	
	private JTextField tiffText;
	private JTextField occurrenceFileText;
	private JTextField resultText;
	private JNumberTextField precisionText;
	private JNumberTextField nodataText;
	private Displayer theApp;
	
	private String tiff;
	private ArrayList<Double> occurrence;
	private String result;
	private double precision;
	private JNumberTextField numberofOccurrenceText;
	private JTextArea geoinfoText;
	private JPanel occurrenceMethodPanel;
	private JPanel uniformMethodPanel;
	private JPanel normalMethodPanel;
	private JPanel binomialMethodPanel;
	private JPanel poissonMethodPanel;
	
	private JNumberTextField minValueText;
	private JNumberTextField maxValueText;
	private JNumberTextField meanNormalText;
	private JNumberTextField sdNormalText;
	private JNumberTextField trialsBinomialText;
	private JNumberTextField pBinomialText;
	private JNumberTextField lPoissonText;
	
	//private JRadioButton occurrenceMethodRadio;
	private JRadioButton uniformMethodRadio;
	private JRadioButton normalMethodRadio;
	private JRadioButton binomialMethodRadio;
	private JRadioButton poissonMethodRadio;
	
	
	
	public OccurrenceMappingForm(Displayer theApp){
		this.theApp = theApp;
		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.PAGE_AXIS));
		
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.PAGE_AXIS));
		JLabel l = new JLabel(Message.getString("select_tiff_file"), JLabel.TRAILING);
		
		JPanel p2 = new JPanel();
		
		p2.setLayout(new BoxLayout(p2, BoxLayout.X_AXIS));
		p2.add(l);
		tiffText = new JTextField();
		p2.add(tiffText);
		JButton targetb = new JButton("...");
		p2.add(targetb);
		targetb.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				selectTiffFile();
			}
		});
		p.add(p2);
		p2 = new JPanel();
		l = new JLabel(Message.getString("env_nodata"));
		p2.add(l);
		nodataText = new JNumberTextField(10, JNumberTextField.DECIMAL);
		
		nodataText.setText("-9999");
		p2.add(nodataText);
		JButton getinfobutton = new JButton(Message.getString("get_geo_info"));
		p2.add(getinfobutton);
		getinfobutton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				getTiffFileinfo();
			}
		});
		p.add(p2);
		
		geoinfoText = new JTextArea(7, 50);
		geoinfoText.setBorder(new LineBorder(Color.black));
		p.add(geoinfoText);
		
		
		
		p2 = new JPanel();
		p2.setLayout(new BoxLayout(p2, BoxLayout.X_AXIS));
		l = new JLabel(Message.getString("min"));
		p2.add(l);
		minValueText = new JNumberTextField(10, JNumberTextField.DECIMAL);
		minValueText.setText("0");
		p2.add(minValueText);
		l = new JLabel(Message.getString("max"));
		p2.add(l);
		maxValueText = new JNumberTextField(10, JNumberTextField.DECIMAL);
		maxValueText.setText("0");
		p2.add(maxValueText);
		p.add(p2);
		add(p);
		
		p2 = new JPanel(new GridLayout(1, 2));
        JLabel label = new JLabel(Message.getString("set_precision"));
        p2.add(label);
        precisionText = new JNumberTextField(4, JNumberTextField.DECIMAL);
        precisionText.setText("1");
		p2.add(precisionText);
		p.add(p2);
		
		
		
//		occurrenceMethodPanel = new JPanel();
//		occurrenceMethodPanel.setBorder(new LineBorder(Color.black));
//		occurrenceMethodPanel.setLayout(new BoxLayout(occurrenceMethodPanel, BoxLayout.X_AXIS));
//		occurrenceMethodRadio = new JRadioButton(Message.getString("select_value_file"));
//		occurrenceMethodRadio.setSelected(true);
//		occurrenceMethodRadio.setActionCommand(Message.getString("select_value_file"));
//		occurrenceMethodRadio.addActionListener(this);
		
		
//		occurrenceMethodPanel.add(occurrenceMethodRadio);
//		occurrenceFileText = new JTextField();
//		occurrenceMethodPanel.add(occurrenceFileText);
//		JButton targetbs = new JButton("...");
//		occurrenceMethodPanel.add(targetbs);
//		targetbs.addActionListener(new ActionListener(){
//			public void actionPerformed(ActionEvent e){
//				selectOccurrenceFile();
//			}
//		});
//		add(occurrenceMethodPanel);
		
		uniformMethodPanel = new JPanel();
		uniformMethodPanel.setBorder(new LineBorder(Color.black));
		uniformMethodPanel.setLayout(new BoxLayout(uniformMethodPanel, BoxLayout.X_AXIS));
		uniformMethodRadio = new JRadioButton(Message.getString("uniform"));
		uniformMethodRadio.setSelected(true);
		uniformMethodRadio.setActionCommand(Message.getString("uniform"));
		uniformMethodRadio.addActionListener(this);
		uniformMethodPanel.add(uniformMethodRadio);
		l = new JLabel("                                                                                               ");
		uniformMethodPanel.add(l);
		add(uniformMethodPanel);
		
		normalMethodPanel = new JPanel();
		normalMethodPanel.setBorder(new LineBorder(Color.black));
		normalMethodPanel.setLayout(new BoxLayout(normalMethodPanel, BoxLayout.X_AXIS));
		normalMethodRadio = new JRadioButton(Message.getString("normal"));
		normalMethodRadio.setSelected(false);
		normalMethodRadio.setActionCommand(Message.getString("normal"));
		normalMethodRadio.addActionListener(this);
		normalMethodPanel.add(normalMethodRadio);
		l = new JLabel(Message.getString("mean_normal"));
		normalMethodPanel.add(l);
		meanNormalText = new JNumberTextField(10, JNumberTextField.DECIMAL);
		meanNormalText.setText("0");
		normalMethodPanel.add(meanNormalText);
		l = new JLabel(Message.getString("sd_normal"));
		normalMethodPanel.add(l);
		sdNormalText = new JNumberTextField(10, JNumberTextField.DECIMAL);
		sdNormalText.setText("1");
		normalMethodPanel.add(sdNormalText);
		
		add(normalMethodPanel);
		
		binomialMethodPanel = new JPanel();
		binomialMethodPanel.setBorder(new LineBorder(Color.black));
		binomialMethodPanel.setLayout(new BoxLayout(binomialMethodPanel, BoxLayout.X_AXIS));
		binomialMethodRadio = new JRadioButton(Message.getString("binomial"));
		binomialMethodRadio.setSelected(false);
		binomialMethodRadio.setActionCommand(Message.getString("binomial"));
		binomialMethodRadio.addActionListener(this);
		binomialMethodPanel.add(binomialMethodRadio);
		l = new JLabel(Message.getString("trails_binomial"));
		binomialMethodPanel.add(l);
		trialsBinomialText = new JNumberTextField(10, JNumberTextField.NUMERIC);
		trialsBinomialText.setText("100");
		trialsBinomialText.setEditable(false);
		trialsBinomialText.setBackground(Color.LIGHT_GRAY);
		binomialMethodPanel.add(trialsBinomialText);
		l = new JLabel(Message.getString("properbility_binomial"));
		binomialMethodPanel.add(l);
		pBinomialText = new JNumberTextField(10, JNumberTextField.DECIMAL);
		pBinomialText.setText("0.5");
		binomialMethodPanel.add(pBinomialText);
		
		add(binomialMethodPanel);
		
		poissonMethodPanel = new JPanel();
		poissonMethodPanel.setBorder(new LineBorder(Color.black));
		poissonMethodPanel.setLayout(new BoxLayout(poissonMethodPanel, BoxLayout.X_AXIS));
		poissonMethodRadio = new JRadioButton(Message.getString("poisson"));
		poissonMethodRadio.setSelected(false);
		poissonMethodRadio.setActionCommand(Message.getString("poisson"));
		poissonMethodRadio.addActionListener(this);
		poissonMethodPanel.add(poissonMethodRadio);
		
		l = new JLabel(Message.getString("l_poisson"));
		poissonMethodPanel.add(l);
		lPoissonText = new JNumberTextField(10, JNumberTextField.DECIMAL);
		lPoissonText.setText("0");
		poissonMethodPanel.add(lPoissonText);
		
		add(poissonMethodPanel);
		
		p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
		l = new JLabel(Message.getString("occurrence_number"));
		p.add(l);
		numberofOccurrenceText = new JNumberTextField(10, JNumberTextField.NUMERIC);
		numberofOccurrenceText.setText("1000");
		p.add(numberofOccurrenceText);
		add(p);
		
		
		p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
		l = new JLabel(Message.getString("select_result_file"), JLabel.TRAILING);
		p.add(l);
		p2 = new JPanel();
		p2.setLayout(new BoxLayout(p2, BoxLayout.X_AXIS));
		resultText = new JTextField();
		p2.add(resultText);
		JButton resultbs = new JButton("...");
		p2.add(resultbs);
		resultbs.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				selectResultFile();
			}
		});
		add(p);
		add(p2);
		
        p2 = new JPanel(new GridLayout(1, 2));
        JButton cancel = new JButton(Message.getString("cancel"));
		cancel.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				cancelClick();
			}
		});
		JButton ok = new JButton(Message.getString("ok"));
		ok.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				okClick();
			}
		});
		p2.add(ok);
		p2.add(cancel);
		add(p2);
		
		
		ButtonGroup group = new ButtonGroup();
//		group.add(occurrenceMethodRadio);
		group.add(uniformMethodRadio);
		group.add(normalMethodRadio);
		group.add(binomialMethodRadio);
		group.add(poissonMethodRadio);
		
		setSize(new Dimension(500, 550));
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);
		setModal(true);
		
		
		
	}
	/**
	 * 
	 */
	protected void getTiffFileinfo() {
		StringBuilder sb = new StringBuilder();
		try {
			GeoTiffObject geo = new GeoTiffObject(tiffText.getText());
			double nodata1 = geo.getNoData();
			double nodata2 = nodataText.getDouble();
			double[] maxmin = geo.getMaxMin(nodata2);
			sb.append(String.format("%s:%d\t%s:%d%n%s\t%f%n%s\t%f%n%s\t%f%n%s\t%f%n", 
					Message.getString("x_size"), geo.getXSize(),
					Message.getString("y_size"), geo.getYSize(),
					Message.getString("nodata_internal"), nodata1, 
					Message.getString("nodata_user"), nodata2,
					Message.getString("min"), maxmin[0], 
					Message.getString("max"), maxmin[1]));
			HashSet<Double> valueSet = new HashSet<Double>();
 			for (int x=0;x<geo.getXSize();x++){
				for (int y=0;y<geo.getYSize();y++){
					double value = geo.readByXY(x, y);
					if (CommonFun.equal(value, nodata1, 1000)){
						continue;
					}
					if (CommonFun.equal(value, nodata2, 1000)){
						continue;
					}
					valueSet.add(value);
				}
			}
			double[] initvalues = new double[valueSet.size()];
			int index = 0;
			for (Double v : valueSet){
				initvalues[index] = v.doubleValue();
				index++;
			}
			DescriptiveStatistics stat = new DescriptiveStatistics(initvalues);
			sb.append(String.format("%s\t%f%n%s\t%f%n%s\t%f%n",
					Message.getString("mean"), stat.getMean(),
					Message.getString("median"), stat.getPercentile(50),
					Message.getString("sd"), stat.getStandardDeviation()));
			geo.release();
		} catch (FileNotFoundException e) {
			sb.append(Message.getString("error_format"));
		}
		geoinfoText.setText(sb.toString());
	}
	protected void selectTiffFile() {
		JFileChooser fc = new JFileChooser();
		fc.setDialogTitle(Message.getString("select_tiff_file"));
		fc.setSelectedFile(new File(theApp.getLastFolder()));
		fc.setFileFilter(new RasterFileFilter());
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		int returnVal = fc.showOpenDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File target = fc.getSelectedFile().getAbsoluteFile();
			this.tiffText.setText(target.getAbsolutePath());
		}
		
	}
	protected void selectResultFile() {
		JFileChooser fc = new JFileChooser();
		fc.setDialogTitle(Message.getString("select_result_file"));
		fc.setSelectedFile(new File(theApp.getLastFolder()));
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		int returnVal = fc.showSaveDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File target = fc.getSelectedFile().getAbsoluteFile();
			this.resultText.setText(target.getAbsolutePath());
			theApp.setLastFolder(target.getAbsolutePath());
		}
		
	}
	/**
	 * 
	 */
	protected void selectOccurrenceFile() {
		JFileChooser fc = new JFileChooser();
		fc.setDialogTitle(Message.getString("select_occurrence_file"));
		fc.setSelectedFile(new File(theApp.getLastFolder()));
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		int returnVal = fc.showOpenDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File target = fc.getSelectedFile().getAbsoluteFile();
			this.occurrenceFileText.setText(target.getAbsolutePath());
			theApp.setLastFolder(target.getAbsolutePath());
		}
		
	}

	private void cancelClick() {
		this.result = null;
		this.dispose();
		
	}
	private void okClick(){
		
		this.result = this.resultText.getText();
		if (CommonFun.isDouble(this.precisionText.getText())){
			precision = Double.valueOf(this.precisionText.getText());
		}else{
			precision = 1;
		}
		this.tiff = this.tiffText.getText();
		File f = new File(tiff);
		if (!f.exists()){
			this.theApp.ShowAlert(String.format(Message.getString("no_file"), this.tiff));
			return;
		}else{
			
		}
		this.occurrence = new ArrayList<Double>();
//		if (this.occurrenceMethodRadio.isSelected()){
//			String occurrenceFile = this.occurrenceFileText.getText();
//			f = new File(occurrenceFile);
//			if (!f.exists()){
//				this.theApp.ShowAlert(String.format(Message.getString("no_file"), occurrenceFile));
//				return;
//			}else{
//				ArrayList<String> occurrences;
//				try {
//					occurrences = CommonFun.readFromFile(occurrenceFile);
//					for (int i=0;i<occurrences.size();i++){
//						String occurrence = occurrences.get(i);
//						if (!CommonFun.isDouble(occurrence)){
//							continue;
//						}
//						this.occurrence.add(Double.valueOf(occurrence));
//					}
//				} catch (IOException e) {
//					this.theApp.ShowAlert(String.format(Message.getString("error_read_occurrence"), occurrenceFile));
//					return;
//				}
//				
//				
//			}
//		}
		
		
		
//		if (!this.occurrenceMethodRadio.isSelected()){
			double min = minValueText.getDouble();
			double max = maxValueText.getDouble();
			if (min>max){
				this.theApp.ShowAlert(String.format(Message.getString("min_max_error")));
			}else{
				Object distribution = null;
				if (this.uniformMethodRadio.isSelected()){
					distribution = new UniformRealDistribution(min, max);
				}
				if (this.normalMethodRadio.isSelected()){
					double mean = meanNormalText.getDouble();
					double sd = sdNormalText.getDouble();					
					distribution = new NormalDistribution(mean, sd);
				}
				if (this.poissonMethodRadio.isSelected()){
					double l = lPoissonText.getDouble();
					if (l<=0){
						this.theApp.ShowAlert(String.format(Message.getString("poisson_l_error")));
						return;
					}
					distribution = new PoissonDistribution(l);
				}
				if (this.binomialMethodRadio.isSelected()){
					int trials = trialsBinomialText.getInt();
					double p = pBinomialText.getDouble();
					if ((p>1)||(p<0)){
						this.theApp.ShowAlert(String.format(Message.getString("binomial_p_error")));
						return;
					}
					distribution = new BinomialDistribution(trials, p);
				}
				int number = numberofOccurrenceText.getInt();
				double[] values = new double[number];
				double max_sample = -1 * Double.MAX_VALUE;
				double min_sample = Double.MAX_VALUE;
				for (int i=0;i<number;i++){
					if (this.uniformMethodRadio.isSelected()){
						values[i] = ((UniformRealDistribution)distribution).sample();
					}
					if (this.normalMethodRadio.isSelected()){
						values[i] = ((NormalDistribution)distribution).sample();						
					}
					if (this.poissonMethodRadio.isSelected()){
						values[i] = ((PoissonDistribution)distribution).sample();
					}
					if (this.binomialMethodRadio.isSelected()){
						values[i] = ((BinomialDistribution)distribution).sample();
					}
					
					max_sample = (values[i]>max_sample)?values[i]:max_sample;
					min_sample = (values[i]<min_sample)?values[i]:min_sample;
				}
				
				for (int i=0;i<number;i++){
					this.occurrence.add((values[i] - min_sample) * (max - min) / (max_sample - min_sample) + min);
				}
			}
//		}
		setVisible(false);
	}
	public String getTiff() {
		return tiff;
	}
	public ArrayList<Double> getOccurrence() {
		return occurrence;
	}
	public String getResult() {
		return result;
	}
	public double getPrecision() {
		return precision;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals(Message.getString("binomial"))){
			trialsBinomialText.setText(String.valueOf(Math.round(maxValueText.getDouble() - minValueText.getDouble())));
		}
		
	}
	
	
}
