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
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import javax.swing.SwingWorker;

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

public class OccurrenceMapper extends SwingWorker<Void, Void> {
	private String tiff;
	private double precision;
	private ArrayList<Double> occurrences;
	private Displayer app;
	private String result;
	private StringBuilder  sb_html;
	
	public OccurrenceMapper(Displayer app, String tiff, double precision, ArrayList<Double> occurrences, String result){
		this.tiff = tiff;
		this.precision = precision;
		this.occurrences = occurrences;
		this.app = app;
		this.result = result;
		this.sb_html = new StringBuilder();
	}
	
	@Override
	protected Void doInBackground() throws Exception {
		setProgress(0);
		
		GeoTiffObject tiffgeo = new GeoTiffObject(tiff);
		
		HashMap<String, ArrayList<String>> valuearray = new HashMap<String, ArrayList<String>>();
		for (int i=0;i<occurrences.size();i++){
			setProgress((int) (10f * i / occurrences.size()));
			double occurrence = occurrences.get(i);
			
			String occurrence_value = String.format("#%d#", Math.round((Double.valueOf(occurrence) * (1f/precision))));
			ArrayList<String> array = valuearray.get(occurrence_value);
			if (array==null){
				array = new ArrayList<String>();
			}
			valuearray.put(occurrence_value, array);
		}
		double[] values = tiffgeo.getValueArray();
		for (int x=0;x<tiffgeo.getXSize();x++){
			setProgress((int) (70f * x / tiffgeo.getXSize()) + 10);
			for (int y=0;y<tiffgeo.getYSize();y++){
				double value = tiffgeo.readByXY(x, y);
				if (CommonFun.equal(value, tiffgeo.getNoData(), 1000)){
					continue;
				}
				String v = String.format("#%d#", Math.round((value * (1f/precision))));
				ArrayList<String> array = valuearray.get(v);
				if (array!=null){
					double[] ll = CommonFun.PositionToLL(tiffgeo.getDataset().GetGeoTransform(), new int[]{x, y});
					array.add(String.format("%f,%f,%f", ll[0], ll[1], value));
					valuearray.put(v, array);
					values[y * tiffgeo.getXSize() + x] = 255;
				}else{
					values[y * tiffgeo.getXSize() + x] = 0;
				}
				
			}
		}
		GeoTiffController.createTiff(result.replace(".txt", ".tif"), tiffgeo.getXSize(), tiffgeo.getYSize(), 
				tiffgeo.getDataset().GetGeoTransform(), values, tiffgeo.getNoData(), gdalconst.GDT_Int32, 
				tiffgeo.getDataset().GetProjection());
		GeoTiffController.toPNG(result.replace(".txt", ".tif"), result.replace(".txt", ".png"));
		
		
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("Value,Trunc,Long,Lat,Value_In_Tiff%n"));
		for (int i=0;i<occurrences.size();i++){
			setProgress((int) (20f * i / occurrences.size()) + 80);
			double occurrence = occurrences.get(i);
			
			String occurrence_value = String.format("#%d#", Math.round((Double.valueOf(occurrence) * (1f/precision))));
			ArrayList<String> a = valuearray.get(occurrence_value);
			String str = occurrences.get(i) + "," + occurrence_value.replace("#", "") + ",,,";
			if (a==null){
				sb.append(str + Const.LineBreak);
				continue;
			}
			if (a.size()==0){
				sb.append(str + Const.LineBreak);
				continue;
			}
			int index = (int) Math.round(Math.random() * a.size());
			if (index==a.size()){
				index--;
			}
			str = occurrences.get(i) + "," + occurrence_value.replace("#", "") + "," + a.get(index);
			sb.append(str + Const.LineBreak);
			a.remove(index);
		}
		CommonFun.writeFile(sb.toString(), result);
		File f = new File(result);
		sb_html.append(Message.getString("html_head"));
		sb_html.append("<h3>Occurrence distribution map</h3>" + Const.LineBreak);
		sb_html.append("<img width=600 src='file://localhost/" + result.replace(".txt", ".png").replace("\\", "/") + "'/>");
		
		
		InputStream rscript = this.getClass().getResourceAsStream("/VirtualOccur2Geo.r");
		HashMap<String, String> parameters = new HashMap<String, String>();
		
		parameters.put("@Target", f.getParent().replace("\\", "/"));
		parameters.put("@datafile",result.replace("\\", "/"));
		parameters.put("@result", f.getParent().replace("\\", "/") + "/hist.png");
		
		HashSet<String> libraries = new HashSet<String>();
		libraries.add("ggplot2");
		
		String r_script = CommonFun.RunRScript(rscript, parameters, libraries, true, f.getParent() + "/rscript.r");
		
		sb_html.append("<h3>Occurrence histogram</h3>" + Const.LineBreak);
		sb_html.append("<img width=600 src='file://localhost/" + f.getParent().replace("\\", "/") + "/hist.png" + "'/>");
		sb_html.append(Message.getString("html_tail"));
		
		setProgress(100);
		tiffgeo.release();
		return null;
	}
	public String getHTMLResult() throws IOException{
		File f = new File(result);
		
		String html = f.getParent() + "/result.html";
		CommonFun.writeFile(sb_html.toString(), html);
		return html;
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
	
}
