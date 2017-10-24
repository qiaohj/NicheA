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
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.SwingWorker;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.gdal.gdal.gdal;
import org.gdal.gdalconst.gdalconst;
import org.ku.nicheanalyst.common.CommonFun;
import org.ku.nicheanalyst.common.Const;
import org.ku.nicheanalyst.common.HtmlBuilder;
import org.ku.nicheanalyst.common.Message;
import org.ku.nicheanalyst.common.ThresholdMethod;
import org.ku.nicheanalyst.maps.controllers.GeoTiffController;
import org.ku.nicheanalyst.maps.objects.GeoTiffObject;

/**
 * @author Huijie Qiao
 *
 */
public class ModelThresholdGenerator extends SwingWorker<Void, Void> {
	private GeoTiffObject[] environmentalLayers;
	private String target;
	private double[][] occurrences;
	private GeoTiffObject modelResult;
	private ThresholdMethod[] thresholdMethods;
	private StringBuilder html;
	public ModelThresholdGenerator(String[] environmentalLayers, 
			String target, double[][] occurrences, String modelResult, 
			ThresholdMethod[] thresholdMethods) throws FileNotFoundException{
		this.target = target;
		this.occurrences = occurrences;
		this.modelResult = new GeoTiffObject(modelResult);
		this.thresholdMethods = thresholdMethods;
		this.environmentalLayers = new GeoTiffObject[environmentalLayers.length];
		for (int i=0;i<environmentalLayers.length;i++){
			GeoTiffObject geo = new GeoTiffObject(environmentalLayers[i]);
			this.environmentalLayers[i] = geo;
		}
	}
	
	@Override
	protected Void doInBackground() throws Exception {
		
		gdal.AllRegister();
		
		CommonFun.mkdirs(target, false);
		HtmlBuilder html_list = new HtmlBuilder(Message.getString("threshold_variables"));
		StringBuilder sb_occ = new StringBuilder();
		sb_occ.append("Long,Lat,Value" + Const.LineBreak);
		double[] presenceValuesTemp = new double[occurrences.length];
		int dataCount = 0;
		for (double[] ll : this.occurrences){
			double value = modelResult.readByLL(ll[0], ll[1]);
			sb_occ.append(String.format("%f,%f,%f%n", ll[0], ll[1], value));
			if (!CommonFun.equal(value, modelResult.getNoData(), 10000)){
//				value = value / (max - min);
				if (value<0){
					continue;
				}
				presenceValuesTemp[dataCount] = value;
				dataCount++;
			}
		}
		CommonFun.writeFile(sb_occ.toString(), target + "/occurrences.log");
		double[] presenceValues = new double[dataCount];
		for (int i=0;i<dataCount;i++){
			presenceValues[i] = presenceValuesTemp[i];
		}
		html_list.append(String.format("<ul><li>Number of presence: <font color='red'>%d</font></li>", dataCount));
		html_list.append(String.format("<ul><li>ENM result: <font color='red'>%s</font></li>", modelResult.getFilename()));
		int totalprogress = thresholdMethods.length * environmentalLayers.length;
		int currentprocess = 0;
		int thresholdCount = 1;
		for (ThresholdMethod method : thresholdMethods){
			currentprocess++;
			totalprogress = currentprocess * (100/thresholdMethods.length);
			if (totalprogress>=100){
				totalprogress = 99;
			}
			setProgress(totalprogress);
			String target_item = target + "/" + thresholdCount;
			CommonFun.mkdirs(target_item, false);
			
			StringBuilder sb = new StringBuilder();
			double threshold = 0;
			String image = "";
			switch (method.getMethod()){
				//for presence value
				case 0: case 1: case 2: case 3: case 4: case 5:
					threshold = method.getThreshold(presenceValues);
					break;
				//for model value
				 case 6: case 7: 
					threshold = method.getThreshold(modelResult.getValueArray());
					break;
				//for fixed value
				case 8:
					threshold = method.getThreshold();
					break;
				//for presence and model value (Max TSS, Equal Sn and Sp)
				case 9: case 10:
					threshold = method.getThreshold(modelResult, occurrences, target_item);
					image = String.format("<li><img src='file://localhost/%s/output_chart.png'></li>", target_item);
					break;
				default:
					threshold = method.getThreshold();
					break;
					
			}
			html_list.append(String.format("<li>Threshold method No.<b>%d</b>: <font color='red'>%s</font>%n", thresholdCount, 
					method.toString().replace("<", "&lt;").replace(">", "&gt;")));
			html_list.append(String.format("<ul><li>Threshold: <font color='red'>%f</font></li>", threshold));
			html_list.append(image);
			
			sb.append("----------------Threshold Method (" + method.toString() + ") ---------------" + Const.LineBreak);
			sb.append(String.format("Threshold: %f%n", threshold));
			double[] modelValues = modelResult.getValueArray().clone();
			double[] modelValues_bin = new double[modelValues.length];
			for (int x=0;x<modelResult.getXSize();x++){
				for (int y=0;y<modelResult.getYSize();y++){
					double modelValue = modelResult.readByXY(x, y);
					if (modelValue>=threshold){
						modelValues_bin[y * modelResult.getXSize() + x] = 255;
					}else{
						modelValues[y * modelResult.getXSize() + x] = modelResult.getNoData();
						modelValues_bin[y * modelResult.getXSize() + x] = modelResult.getNoData();
					}
					
				}
			}
			for (GeoTiffObject environmentalLayer : environmentalLayers){
				html_list.append("<li>Raster layer (" + environmentalLayer.getFilename() + ")</li>");
				sb.append("=============Raster layer (" + environmentalLayer.getFilename() + ")============" + Const.LineBreak);
				double[] values = environmentalLayer.getValueArray();
				double min = Double.MAX_VALUE;
				double max = -1 * Double.MAX_VALUE;
				
				for (int x=0;x<modelResult.getXSize();x++){
					for (int y=0;y<modelResult.getYSize();y++){
						double modelValue = modelResult.readByXY(x, y);
						double[] new_ll = CommonFun.PositionToLL(
								environmentalLayer.getDataset().GetGeoTransform(), 
								new int[]{x, y});
						double environmentalValue = environmentalLayer.readByLL(new_ll[0], new_ll[1]);
						if (modelValue>=threshold){
							min = (min>environmentalValue)?environmentalValue:min;
							max = (max<environmentalValue)?environmentalValue:max;
//							modelValues_bin[y * modelResult.getXSize() + x] = 255;
						}else{
//							modelValues[y * modelResult.getXSize() + x] = modelResult.getNoData();
//							modelValues_bin[y * modelResult.getXSize() + x] = 0;
						}
						
					}
				}
				int valueLength = 0;
				for (int i=0;i<values.length;i++){
					if ((values[i]>min)&&(values[i]<max)){
						valueLength++;
					}else{
						values[i] = environmentalLayer.getNoData();
					}
				}
				double[] statValues =new double[valueLength];
				int index = 0;
				for (int i=0;i<values.length;i++){
					if (!CommonFun.equal(values[i], environmentalLayer.getNoData(), 1000)){
						if (values[i]<0){
							continue;
						}
						statValues[index] = values[i];
						index++;
					}
				}
				DescriptiveStatistics stat = new DescriptiveStatistics(statValues);
				html_list.append("<ul>");
				html_list.append(String.format("<li>Minimum value: %f</li>", stat.getMin()));
				html_list.append(String.format("<li>Maximum value: %f</li>", stat.getMax()));
				html_list.append(String.format("<li>Mean value: %f</li>", stat.getMean()));
				html_list.append(String.format("<li>Standard deviation: %f</li>", stat.getStandardDeviation()));
				html_list.append("</ul>");
				sb.append(String.format("Minimum value: %f%n", stat.getMin()));
				sb.append(String.format("Maximum value: %f%n", stat.getMax()));
				sb.append(String.format("Mean value: %f%n", stat.getMean()));
				sb.append(String.format("Standard deviation: %f%n", stat.getStandardDeviation()));
				sb.append("=============End for (" + environmentalLayer.getFilename() + ")============" + Const.LineBreak);
				File f = new File(environmentalLayer.getFilename());
				GeoTiffController.createTiff(target_item + "/" + f.getName(), 
						environmentalLayer.getXSize(), environmentalLayer.getYSize(), 
						environmentalLayer.getDataset().GetGeoTransform(),
						values, environmentalLayer.getNoData(), environmentalLayer.getDataType(), environmentalLayer.getDataset().GetProjection());
			}
			html_list.append("</ul>");
			
			sb.append("----------------End for (" + method.toString() + ") ---------------" + Const.LineBreak);
			GeoTiffController.createTiff(target_item + "/model_result.tiff", 
					modelResult.getXSize(), modelResult.getYSize(), 
					modelResult.getDataset().GetGeoTransform(), modelValues, 
					modelResult.getNoData(), modelResult.getDataType(), modelResult.getDataset().GetProjection());
			GeoTiffController.createTiff(target_item + "/model_result_bin.tiff", 
					modelResult.getXSize(), modelResult.getYSize(), 
					modelResult.getDataset().GetGeoTransform(), modelValues_bin, 
					modelResult.getNoData(), gdalconst.GDT_Int32, modelResult.getDataset().GetProjection());
			
			CommonFun.writeFile(sb.toString(), target_item + "/threshold.log");
			thresholdCount++;
		}
		html_list.append("</ul>");
		html_list.save(target + "/results.html");
		setProgress(100);
		return null;
	}
	
	private Exception msg;
	@Override
	
    public void done() {
		Toolkit.getDefaultToolkit().beep();
        try {
        	modelResult.release();
        	for (GeoTiffObject env : this.environmentalLayers){
        		env.release();
        	}
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

	public String getHTMLResult() throws IOException {
		
		return target + "/results.html";
	}
	
}
