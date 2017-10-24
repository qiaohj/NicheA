/******************************************************************************
 * Huijie Qiao
 *
 * Project:  Niche Analyst
 * Purpose:  
 * Created date: Jun 12, 2012 2:09:06 PM
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


package org.ku.nicheanalyst.ui.display.worker;

import java.awt.Toolkit;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;

import javax.swing.SwingWorker;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.gdal.gdal.gdal;
import org.gdal.gdalconst.gdalconst;
import org.ku.nicheanalyst.common.CommonFun;
import org.ku.nicheanalyst.common.Const;
import org.ku.nicheanalyst.common.Message;
import org.ku.nicheanalyst.maps.controllers.GeoTiffController;
import org.ku.nicheanalyst.maps.objects.GeoTiffObject;
import org.ku.nicheanalyst.ui.display.Displayer;

/**
 * @author Huijie Qiao
 *
 */
public class VariableStandardizationProcessor extends SwingWorker<Void, Void> {
	private ArrayList<String> allfilenames;
	private String target;
	private String method;
	private boolean individually;
	public VariableStandardizationProcessor(
			Displayer displayer, ArrayList<String> filenames, String target, String method, boolean individually){
		this.allfilenames = filenames;
		this.target = target;
		this.method = method;
		this.individually = individually;
	}
	
	@Override
	protected Void doInBackground() throws Exception {
		
		gdal.AllRegister();
		setProgress(0);
		
		HashSet<ArrayList<String>> filenames_group = new HashSet<ArrayList<String>>();
		if (individually){
			for (String file : allfilenames){
				ArrayList<String> f = new ArrayList<String>();
				f.add(file);
				filenames_group.add(f);
			}
		}else{
			filenames_group.add(allfilenames);
		}
		int process = 0;
		int steps = 100/(allfilenames.size() * 2);
		for (ArrayList<String> filenames : filenames_group){
			double[] values = null;
			int value_index = 0;
			process += steps;
			double nodata = Const.NoData;
	        for (String geofile : filenames){
	        	process += steps;
	        	GeoTiffObject geo = new GeoTiffObject(geofile);
	        	nodata = geo.getNoData();
	        	if (values==null){
	        		values = new double[geo.getDataCount() * filenames.size()];
	        		for (int i=0;i<values.length;i++){
	        			values[i] = nodata;
	        		}
	        	}
	        	
	        	for (int x=0;x<geo.getXSize();x++){
	        		for (int y=0;y<geo.getYSize();y++){
	        			double value = geo.readByXY(x, y);
	        			if (!CommonFun.equal(value, geo.getNoData(), 1000)){
	        				if (value_index>=values.length){
	        					continue;
	        				}
	        				values[value_index] = value;
	        				value_index++;
	        			}
	        		}
	        	}
	        	if (process>=100){
	        		process = 99;
	        	}
	        	setProgress(process);
	        	geo.release();
	        }
	        
	        
	        int valuecount = 0;
	        for (int i=0;i<values.length;i++){
	        	if (!CommonFun.equal(values[i], nodata, 1000)){
	        		valuecount++;
	        	}
	        }
	        double[] newvalue = new double[valuecount];
	        valuecount = 0;
	        for (int i=0;i<values.length;i++){
	        	if (!CommonFun.equal(values[i], nodata, 1000)){
	        		newvalue[valuecount] = values[i];
	        		valuecount++;
	        	}
	        }
	        if (method.equals(Message.getString("standard_score"))){
		        DescriptiveStatistics stat = new DescriptiveStatistics(newvalue);
		        double mean = stat.getMean();
		        double sd = stat.getStandardDeviation();
		        for (String geofile : filenames){   
		        	
		        	GeoTiffObject geo = new GeoTiffObject(geofile);
		        	double[] value_2 = new double[geo.getXSize() * geo.getYSize()];
		        	for (int x=0;x<geo.getXSize();x++){
		        		for (int y=0;y<geo.getYSize();y++){
		        			double value = geo.readByXY(x, y);
		        			if (!CommonFun.equal(value, geo.getNoData(), 1000)){
		        				value_2[y * geo.getXSize() + x] = (value - mean) / sd;
		        			}else{
		        				value_2[y * geo.getXSize() + x] = geo.getNoData();
		        			}
		        		}
		        	}
		        	File f = new File(geofile);
		        	
		        	GeoTiffController.createTiff(target + "/" + f.getName(), 
		        			geo.getXSize(), geo.getYSize(), 
		        			geo.getDataset().GetGeoTransform(), 
		        			value_2, geo.getNoData(), gdalconst.GDT_Float32, geo.getDataset().GetProjection());
		        	process += steps;
		        	if (process>=100){
		        		process = 99;
		        	}
		        	setProgress(process);
		        	geo.release();
		        }
	        }
	        if (method.equals(Message.getString("feature_scaling"))){
		        DescriptiveStatistics stat = new DescriptiveStatistics(newvalue);
		        double max = stat.getMax();
		        double min = stat.getMin();
		        for (String geofile : filenames){
		        	GeoTiffObject geo = new GeoTiffObject(geofile);
		        	double[] value_2 = new double[geo.getXSize() * geo.getYSize()];
		        	for (int x=0;x<geo.getXSize();x++){
		        		for (int y=0;y<geo.getYSize();y++){
		        			double value = geo.readByXY(x, y);
		        			if (!CommonFun.equal(value, geo.getNoData(), 1000)){
		        				value_2[y * geo.getXSize() + x] = (value - min) / (max - min);
		        			}else{
		        				value_2[y * geo.getXSize() + x] = geo.getNoData();
		        			}
		        		}
		        	}
		        	File f = new File(geofile);
		        	
		        	GeoTiffController.createTiff(target + "/" + f.getName(), 
		        			geo.getXSize(), geo.getYSize(), 
		        			geo.getDataset().GetGeoTransform(), 
		        			value_2, geo.getNoData(), gdalconst.GDT_Float32, geo.getDataset().GetProjection());
		        	process += steps;
		        	if (process>=100){
		        		process = 99;
		        	}
		        	geo.release();
		        	setProgress(process);
		        }
	        }
		}
		setProgress(100);
		return null;
	}
	
	private Exception msg;
	@Override
    public void done() {
        Toolkit.getDefaultToolkit().beep();
        try {
            get();
        } catch (Exception e) {
        	e.printStackTrace();
        	msg = e;
            firePropertyChange("done-exception", null, e);
        }
    }

	public Exception getException(){
		return msg;
	}
	public String getTarget() {
		return target;
	}
	public void setTarget(String target) {
		this.target = target;
	}
}
