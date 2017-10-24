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

import org.apache.commons.math3.distribution.MultivariateNormalDistribution;
import org.apache.commons.math3.linear.BlockRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.stat.correlation.Covariance;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.ku.nicheanalyst.common.CommonFun;
import org.ku.nicheanalyst.common.Const;
import org.ku.nicheanalyst.maps.objects.GeoTiffObject;
import org.ku.nicheanalyst.ui.display.Displayer;

/**
 * @author Huijie Qiao
 *
 */

public class MultiVarNormalDistributionGenerator extends SwingWorker<Void, Void> {
	
	private double precision;
	private File[] files;
	private Displayer app;
	private double nodata;
	private boolean isNodata;
	private int maxcount;
	private int maxtry;
	private String result;
	private boolean isSuccessed;
	private String error_msg;
	private String parameter;
	public MultiVarNormalDistributionGenerator(Displayer app, File[] files, int maxcount, int maxtry, 
			double precision, double nodata, boolean isNodata, String result, String parameter){
		this.isNodata = isNodata;
		this.precision = precision;
		this.files = files;
		this.app = app;
		this.nodata = nodata;
		this.maxcount = maxcount;
		this.maxtry = maxtry;
		this.result = result;
		this.parameter = parameter;
	}
	private String getValueLabel(double[] values){
		String result = "";
		for (double value : values){
			result += (int)Math.round(value * (1f/precision)) + ",";
		}
		return result;
	}
	@Override
	protected Void doInBackground() throws Exception {
		RealMatrix values;
		double[] means;
		double[][] covariance;
		
		setProgress(0);
		double nodata1 = 0;
		double nodata2 = 0;
		if (files==null){
			return null;
		}
		if (files.length==0){
			return null;
		}
		
		HashMap<String, ArrayList<MultiVarObject>> allvalues = new HashMap<String, ArrayList<MultiVarObject>>();
		GeoTiffObject[] geos = new GeoTiffObject[files.length];
		
		for (int i=0;i<files.length;i++){
			setProgress((i + 1) * 20 / files.length);
			geos[i] = new GeoTiffObject(files[i].getAbsolutePath());
			nodata1 = geos[i].getNoData();
		}
		nodata2 = (isNodata)?nodata:nodata1;
		int totalItemNumber = 0;
		for (int x=0;x<geos[0].getXSize();x++){
			for (int y=0;y<geos[0].getYSize();y++){
				setProgress((x + 1) * 30 / geos[0].getXSize() + 20);
				boolean isValue = true;
				double[] valueItem = new double[files.length];
				for (int i=0;i<files.length;i++){
					double value = geos[i].readByXY(x, y);
					valueItem[i] = value;
					if ((CommonFun.equal(value, nodata1, 1000))
							||(CommonFun.equal(value, nodata2, 1000))){
						isValue = false;
						break;
					}
					
				}
				if (isValue){
					double[] ll = CommonFun.PositionToLL(geos[0].getDataset().GetGeoTransform(), new int[]{x, y});
					String label = getValueLabel(valueItem);
					ArrayList<MultiVarObject> obj = allvalues.get(label);
					if (obj==null){
						obj = new ArrayList<MultiVarObject>();
					}
					obj.add(new MultiVarObject(valueItem, ll, precision));
					allvalues.put(getValueLabel(valueItem), obj);
					totalItemNumber++;
				}
			}
		}
		values = new BlockRealMatrix(totalItemNumber, files.length);
		int index = 0;
		for (String key : allvalues.keySet()){
			ArrayList<MultiVarObject> objs = allvalues.get(key);
			for (MultiVarObject obj : objs){
				double[] value = obj.getValues();
				values.setRow(index, value);
				index++;
			}
		}
		means = new double[files.length];
		for (int i=0;i<files.length;i++){
			setProgress((i + 1) * 10 / files.length + 50);
			Mean mean = new Mean();
			means[i] = mean.evaluate(values.getColumn(i));
		}
		Covariance cov = new Covariance(values);
		covariance = cov.getCovarianceMatrix().getData();
		
		StringBuilder sb_par = new StringBuilder();
		sb_par.append("------Mean-------" + Const.LineBreak);
		sb_par.append(CommonFun.printArray(means));
		
		sb_par.append("------Covariance-------" + Const.LineBreak);
		sb_par.append(CommonFun.printArray(covariance));
		CommonFun.writeFile(sb_par.toString(), parameter);
		
		MultivariateNormalDistribution distribution = new MultivariateNormalDistribution(
				means, covariance);
		int currentItemNumber = 0;
		int currentTryTimes = 0;
		StringBuilder sb = new StringBuilder();
		sb.append("Long,Lat");
		for (int i=0;i<files.length;i++){
			sb.append(String.format(",%s", CommonFun.getFileNameWithoutExtension(files[i].getName())));
		}
		sb.append(Const.LineBreak);
		while ((currentTryTimes<=maxtry)&&(currentItemNumber<maxcount)){
			int v1 = (currentItemNumber + 1) * 40 / maxcount;
			int v2 = (currentTryTimes + 1) * 40 / maxtry;
			v2 = (v1>v2)?v1:v2;
			setProgress(v2 + 60);
			double[] sample = distribution.sample();
			String label = getValueLabel(sample);
			if (allvalues.containsKey(label)){
				currentItemNumber++;
				ArrayList<MultiVarObject> objs = allvalues.get(label);
				int objindex = (int) (Math.random() * objs.size());
				objindex = (objindex>=objs.size())?objs.size()-1:objindex;
				sb.append(objs.get(objindex).toString());
				objs.remove(objindex);
				if (objs.size()==0){
					allvalues.remove(label);
				}else{
					allvalues.put(label, objs);
				}
			}
			currentTryTimes++;
		}
		CommonFun.writeFile(sb.toString(), result);
		for (GeoTiffObject geo : geos){
			geo.release();
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
	public Exception getException(){
		return msgs;
	}
	/**
	 * @return
	 */
	public String getErrorMsg() {
		
		return error_msg;
	}
	
}
