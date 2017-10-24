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
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.ku.nicheanalyst.common.CommonFun;
import org.ku.nicheanalyst.common.Message;
import org.ku.nicheanalyst.maps.controllers.GeoTiffController;
import org.ku.nicheanalyst.maps.objects.EfficientGeoTiffObject;
import org.ku.nicheanalyst.ui.display.Displayer;

/**
 * @author Huijie Qiao
 *
 */

public class VariablesStator extends SwingWorker<Void, Void> {
	private Displayer app;
	private String result;
	private HashSet<String> averagePercentiles;
	private File[] files;
	private double nodata;
	private boolean isSuccessed;
	private String error_msg;
	private int step;
	
	public VariablesStator(Displayer app, File[] files, String result, HashSet<String> averagePercentiles, double nodata, int step){
		
		this.app = app;
		this.result = result;
		this.files = files;
		this.averagePercentiles = averagePercentiles;
		this.nodata = nodata;
		this.step = step;
	}
	
	@Override
	protected Void doInBackground() throws Exception {
		
		
		setProgress(0);
		if (files==null){
			this.app.addLog("No GeoTiff file.");
			isSuccessed = false;
			setProgress(100);
		}
		if (files.length==0){
			this.app.addLog("No GeoTiff file.");
			isSuccessed = false;
			setProgress(100);
		}
		step = step * 1000000/files.length;
		int pos = 1;
		setProgress(pos++);
		EfficientGeoTiffObject[] tiffs = new EfficientGeoTiffObject[files.length];
		for (int i=0;i<files.length;i++){
			tiffs[i] = new EfficientGeoTiffObject(files[i].getAbsolutePath());
		}
		EfficientGeoTiffObject sampletiff = tiffs[0];
		HashSet<Double> ignore = new HashSet<Double>();
		ignore.add(nodata);
		int ystep = step / sampletiff.getXSize();
		if (ystep<1){
			ystep = 1;
		}
		this.app.addLog(String.format("x size:%d\t y size:%d\t y step:%d\t nodata:%f", sampletiff.getXSize(), sampletiff.getYSize(), ystep, nodata));
		setProgress(pos++);
		double [] mean = new double[sampletiff.getXSize() * sampletiff.getYSize()];
		setProgress(pos++);
		double [] max = new double[sampletiff.getXSize() * sampletiff.getYSize()];
		setProgress(pos++);
		double [] min = new double[sampletiff.getXSize() * sampletiff.getYSize()];
		setProgress(pos++);
		double [] sd = new double[sampletiff.getXSize() * sampletiff.getYSize()];
		setProgress(pos++);
		double [] median = new double[sampletiff.getXSize() * sampletiff.getYSize()];
		setProgress(pos++);
		double [] range = new double[sampletiff.getXSize() * sampletiff.getYSize()];
		setProgress(pos++);
		HashMap<String, double[]> percentiles = new HashMap<String, double[]>();
		for (String averagePercentile : averagePercentiles){
			double[] value = new double[sampletiff.getXSize() * sampletiff.getYSize()];
			for (int i=0;i<mean.length;i++){
				value[i] = nodata;
			}
			setProgress(pos++);
			percentiles.put(averagePercentile, value);
		}
		for (int i=0;i<mean.length;i++){
			mean[i] = nodata;
			min[i] = nodata;
			max[i] = nodata;
			sd[i] = nodata;
			median[i] = nodata;
			range[i] = nodata;
		}
		setProgress(pos++);
		
		HashMap<Integer, ArrayList<Double>> values = null;
		ArrayList<Double> singleValues = null;
		double[] initvalues = null;
		ArrayList<Double> valuesub = null;
		DescriptiveStatistics stat = null;
		for (int yoff=0;yoff<sampletiff.getYSize();yoff=yoff+ystep){
			
		
			this.app.addLog("-------" + yoff + "/" + sampletiff.getYSize());
			pos = (yoff * 80)/(sampletiff.getYSize()) + 20;
			if (pos>100){
				pos = 100;
			}
			setProgress(pos);
			values = new HashMap<Integer, ArrayList<Double>>();
			int xoff = 0;
			int xsize = sampletiff.getXSize();
			int ysize = ystep;
			if (ysize>(sampletiff.getYSize()-yoff)){
				ysize = sampletiff.getYSize() - yoff;
			}
			for (EfficientGeoTiffObject geo : tiffs){
				double[] geovalues = geo.readToDouble(xoff, yoff, xsize, ysize);
				for (int j=0;j<geovalues.length;j++){
					singleValues = values.get(j);
					if (singleValues==null){
						singleValues = new ArrayList<Double>();
						values.put(j, singleValues);
					}
	//				System.out.format("%d:%d\r\n", x, y);
					double value = geovalues[j];
					if (!CommonFun.equal(value, nodata, 1000)){
						singleValues.add(value);
						values.put(j, singleValues);
					}
				}
				
			}
			
			for (Integer key : values.keySet()){
				
				initvalues = new double[values.get(key).size()];
				if (initvalues.length==0){
					continue;
				}
				int k=0;
				valuesub = values.get(key);
				for (Double v : valuesub){
					initvalues[k] = v.doubleValue();
					k++;
				}
				stat = new DescriptiveStatistics(initvalues);
				int y = key / sampletiff.getXSize() + yoff;
				int x = key % sampletiff.getXSize() + xoff;
				int arraypos = y * sampletiff.getXSize() + x;
				mean[arraypos] = stat.getMean();
				max[arraypos] = stat.getMax();
				min[arraypos] = stat.getMin();
				sd[arraypos] = stat.getStandardDeviation();
				median[arraypos] = stat.getPercentile(50);
				range[arraypos] = max[arraypos] - min[arraypos];
				for (String percentileKey : percentiles.keySet()){
					String[] percentileKeys = percentileKey.split(":");
					double[] value = percentiles.get(percentileKey);
					int method = 0;
					if (percentileKeys[0].contains(Message.getString("average_lower_percentile"))){
						method = 1;
					}
					if (percentileKeys[0].contains(Message.getString("average_higher_percentile"))){
						method = 2;
					}
					double percentilePos = Double.valueOf(percentileKeys[1]);
					if (method==0){
						value[arraypos] = stat.getPercentile(percentilePos);
					}
					if (method==1){
						double keyvalue = stat.getPercentile(percentilePos);
						double summary = 0;
						int count = 0;
						for (int ii=0;ii<initvalues.length;ii++){
							if (initvalues[ii]<=keyvalue){
								count++;
								summary += initvalues[ii];
							}
						}
						value[arraypos] = summary / (double) count;
					}
					if (method==2){
						double keyvalue = stat.getPercentile(percentilePos);
						double summary = 0;
						int count = 0;
						for (int ii=0;ii<initvalues.length;ii++){
							if (initvalues[ii]>=keyvalue){
								count++;
								summary += initvalues[ii];
							}
						}
						value[arraypos] = summary / (double) count;
					}
					percentiles.put(percentileKey, value);
				}
				
//				System.out.println(key + "/" + mean[key] + "/" + max[key] + "/" + min[key] + "/" + sd[key]);
			}
			initvalues = null;
			valuesub = null;
			values = null;
			System.gc();
//			System.exit(0);
		}
			
		String targetPrefix = "result";
		String targetFolderString = result;
		GeoTiffController.createTiff(targetFolderString + "/" + targetPrefix + "_mean.tiff", sampletiff.getXSize(), sampletiff.getYSize(), 
				sampletiff.getDataset().GetGeoTransform(), mean, nodata, sampletiff.getBand().getDataType(), sampletiff.getDataset().GetProjection());
		GeoTiffController.createTiff(targetFolderString + "/" + targetPrefix + "_max.tiff", sampletiff.getXSize(), sampletiff.getYSize(), 
				sampletiff.getDataset().GetGeoTransform(), max, nodata, sampletiff.getBand().getDataType(), sampletiff.getDataset().GetProjection());
		GeoTiffController.createTiff(targetFolderString + "/" + targetPrefix + "_min.tiff", sampletiff.getXSize(), sampletiff.getYSize(), 
				sampletiff.getDataset().GetGeoTransform(), min, nodata, sampletiff.getBand().getDataType(), sampletiff.getDataset().GetProjection());
		GeoTiffController.createTiff(targetFolderString + "/" + targetPrefix + "_sd.tiff", sampletiff.getXSize(), sampletiff.getYSize(), 
				sampletiff.getDataset().GetGeoTransform(), sd, nodata, sampletiff.getBand().getDataType(), sampletiff.getDataset().GetProjection());
		GeoTiffController.createTiff(targetFolderString + "/" + targetPrefix + "_median.tiff", sampletiff.getXSize(), sampletiff.getYSize(), 
				sampletiff.getDataset().GetGeoTransform(), median, nodata, sampletiff.getBand().getDataType(), sampletiff.getDataset().GetProjection());
		GeoTiffController.createTiff(targetFolderString + "/" + targetPrefix + "_range.tiff", sampletiff.getXSize(), sampletiff.getYSize(), 
				sampletiff.getDataset().GetGeoTransform(), range, nodata, sampletiff.getBand().getDataType(), sampletiff.getDataset().GetProjection());
		
		for (String key : percentiles.keySet()){
			String name = key.replace(" ", "_").replace(":", "_");
			double[] value = percentiles.get(key);
			GeoTiffController.createTiff(targetFolderString + "/" + targetPrefix + "_" + name + ".tiff", 
					sampletiff.getXSize(), sampletiff.getYSize(), 
					sampletiff.getDataset().GetGeoTransform(), value, nodata, sampletiff.getBand().getDataType(), sampletiff.getDataset().GetProjection());
		}
		sampletiff.release();
		for (EfficientGeoTiffObject tiff : tiffs){
			tiff.release();
		}
		setProgress(100);
		return null;
	}
	
	private Exception msgs;
	@Override
    public void done() {
        Toolkit.getDefaultToolkit().beep();
        try {
        	setProgress(100);
//            System.out.println("Done");
            get();
            isSuccessed = true;
            error_msg = "";
        } catch (ExecutionException e) {
            e.getCause().printStackTrace();
            String msg = String.format("Unexpected problem: %s", 
                           e.getCause().toString());
//            System.out.println(msg);
            error_msg = msg;
            isSuccessed = false;
            msgs = e;
            firePropertyChange("done-exception", null, e);
        } catch (InterruptedException e) {
        	isSuccessed = false;
        	msgs = e;
            firePropertyChange("done-exception", null, e);
        }
    }

	public boolean isSuccessed() {
		return isSuccessed;
	}

	/**
	 * @return
	 */
	public String getErrorMsg() {
		
		return error_msg;
	}
	public Exception getException(){
		return msgs;
	}
}
