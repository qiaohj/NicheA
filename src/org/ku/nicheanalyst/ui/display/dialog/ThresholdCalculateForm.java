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

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.gdal.gdalconst.gdalconst;
import org.ku.nicheanalyst.common.CommonFun;
import org.ku.nicheanalyst.common.Const;
import org.ku.nicheanalyst.common.Message;
import org.ku.nicheanalyst.maps.controllers.GeoTiffController;
import org.ku.nicheanalyst.maps.objects.GeoTiffObject;
import org.ku.nicheanalyst.ui.display.Displayer;
import org.ku.nicheanalyst.ui.display.component.ui.LabeledTextField;
import org.ku.nicheanalyst.ui.filefilters.RasterFileFilter;

/**
 * @author Huijie Qiao
 *
 */
public class ThresholdCalculateForm extends JDialog implements ActionListener {
	private Displayer theApp;
	
	private SelectFilePanal ENMresult;
	private SelectFilePanal llList;
	private LabeledTextField threshold;
	private SelectFilePanal target;
	private JRadioButton radioCal;
	private JRadioButton radioThreshold;
	private int selection;
	public ThresholdCalculateForm(Displayer theApp){

		this.selection = 1;
		this.theApp = theApp;
		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		
		ENMresult = new SelectFilePanal(this.theApp, Message.getString("enm_result"), "", false, new RasterFileFilter(), false);
		add(ENMresult);
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
		JPanel pp = new JPanel();
//		pp.setLayout(new BoxLayout(pp, BoxLayout.X_AXIS));
		radioCal = new JRadioButton(Message.getString("calculate_training_presence"));
		radioCal.setSelected(false);
		radioCal.setActionCommand(Message.getString("calculate_training_presence"));
		radioCal.addActionListener(this);
		radioCal.setSelected(true);
		pp.add(radioCal);
		p.add(pp);
		
		llList = new SelectFilePanal(this.theApp, Message.getString("select_ll_file"), "", false, null, false);
		p.add(llList);
		add(p);
		
		p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
		
		radioThreshold = new JRadioButton(Message.getString("threshold_result"));
		radioThreshold.setSelected(false);
		radioThreshold.setActionCommand(Message.getString("threshold_result"));
		radioThreshold.addActionListener(this);
		p.add(radioThreshold);
		threshold = new LabeledTextField(Message.getString("set_threshold"), "0.5");
		target = new SelectFilePanal(this.theApp, Message.getString("threshold_calculate_target"), "", false, new RasterFileFilter(), true);
		p.add(threshold);
		p.add(target);
		add(p);
		JPanel p2 = new JPanel(new GridLayout(1, 2));
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
		group.add(radioCal);
		group.add(radioThreshold);

		
		setSize(new Dimension(600, 230));
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);
		
		setModal(true);
		
	}
	
	private StringBuilder getInfo(){
		StringBuilder log = new StringBuilder();
		
		return log;
	}
	


	public String getTarget(){
		if (this.target==null){
			return null;
		}else{
			return this.target.getText();
		}
	}
	
	private void cancelClick() {
		this.target = null;
		this.dispose();
	}
	
	private void okClick(){
		if (this.selection == 1){
			GeoTiffObject geo = null;
			try {
				geo = new GeoTiffObject(this.ENMresult.getText());
			} catch (FileNotFoundException e) {
				theApp.ShowAlert(String.format(Message.getString("error_read_tifffile"), this.ENMresult.getText()));
				return;
			}
			ArrayList<String> llstrs = null;
			ArrayList<double[]> lls = new ArrayList<double[]>();
			try {
				llstrs = CommonFun.readFromFile(this.llList.getText());
				for (String llstr : llstrs){
					String[] str = llstr.replace(",", "\t").replace(" ", "\t").split("\t");
					if (str.length>=2){
						if (CommonFun.isDouble(str[0])&&(CommonFun.isDouble(str[1]))){
							lls.add(new double[]{Double.valueOf(str[0]).doubleValue(), Double.valueOf(str[1]).doubleValue()});
						}
					}
				}
			} catch (IOException e) {
				theApp.ShowAlert(String.format(Message.getString("error_read_occurrence"), this.llList.getText()));
				return;
			}
			if (lls.size()==0){
				theApp.ShowAlert(String.format(Message.getString("error_read_occurrence"), this.llList.getText()));
				return;
			}
			int nodataCount = 0;
			int zeroCount = 0;
			ArrayList<String> llresults = new ArrayList<String>();
			
			for (double[] ll : lls){
				double value = geo.readByLL(ll[0], ll[1]);
				if (CommonFun.equal(value, geo.getNoData(), 1000)){
					nodataCount++;
				}
				if (CommonFun.equal(value, 0, 1000)){
					zeroCount++;
				}
				llresults.add(String.format("%f\t%f\t%f%n", ll[0], ll[1], value));
			}
			double[] initvalues = new double[lls.size() - nodataCount - zeroCount];
			int index = 0;
			for (double[] ll : lls){
				double value = geo.readByLL(ll[0], ll[1]);
				if (CommonFun.equal(value, geo.getNoData(), 1000)){
					continue;
				}
				if (CommonFun.equal(value, 0, 1000)){
					continue;
				}
				
				initvalues[index] = value;
				index ++;
			}
			DescriptiveStatistics stat = new DescriptiveStatistics(initvalues);
			StringBuilder sb = new StringBuilder();
			sb.append("Long\tLat\tValue" + Const.LineBreak);
			for (String llresult : llresults){
				sb.append(llresult);
			}
			sb.append("------------Stat info-----------" + Const.LineBreak);
			sb.append(String.format("Max:\t%f%n", stat.getMax()));
			sb.append(String.format("Min:\t%f%n", stat.getMin()));
			sb.append(String.format("Mean:\t%f%n", stat.getMean()));
			sb.append(String.format("Median:\t%f%n", stat.getPercentile(50)));
			sb.append(String.format("SD:\t%f%n", stat.getStandardDeviation()));
			sb.append(String.format("Percentile(5):\t%f%n", stat.getPercentile(5)));
			sb.append(String.format("Percentile(10):\t%f%n", stat.getPercentile(10)));
			sb.append(String.format("Percentile(15):\t%f%n", stat.getPercentile(15)));
			sb.append(String.format("NODATA:\t%d%n", nodataCount));
			sb.append(String.format("Zero:\t%d%n", zeroCount));
			sb.append(String.format("ALL:\t%d%n", llresults.size()));
			LogForm log = new LogForm("", sb, this.theApp);
			geo.release();
		}
		if (this.selection == 2){
			GeoTiffObject geo = null;
			try {
				geo = new GeoTiffObject(this.ENMresult.getText());
			} catch (FileNotFoundException e) {
				theApp.ShowAlert(String.format(Message.getString("error_read_tifffile"), this.ENMresult.getText()));
				return;
			}
			double threshold = 1;
			if (CommonFun.isDouble(this.threshold.getText())){
				threshold = Double.valueOf(this.threshold.getText()).doubleValue();
			}else{
				theApp.ShowAlert(Message.getString("threshold_error"));
				return;
			}
			double[] values = new double[geo.getXSize() * geo.getYSize()];
			for (int x=0;x<geo.getXSize();x++){
				for (int y=0;y<geo.getYSize();y++){
					double value = geo.readByXY(x, y);
					if (CommonFun.equal(value, geo.getNoData(), 1000)){
						values[geo.getXSize() * y + x] = geo.getNoData();
					}else{
						values[geo.getXSize() * y + x] = (value>=threshold)?1:0;
					}
				}
			}
			GeoTiffController.createTiff(this.target.getText(), geo.getXSize(), geo.getYSize(), 
					geo.getDataset().GetGeoTransform(), values, geo.getNoData(), gdalconst.GDT_Float32, geo.getDataset().GetProjection());
			this.theApp.ShowAlert(Message.getString("done"));
			geo.release();
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals(Message.getString("calculate_training_presence"))){
			this.selection = 1;
		}
		if (e.getActionCommand().equals(Message.getString("threshold_result"))){
			this.selection = 2;
		}
		
	}
	
}
