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
import java.util.HashMap;
import java.util.HashSet;

import javax.swing.SwingWorker;

import org.ku.nicheanalyst.common.CommonFun;
import org.ku.nicheanalyst.common.Const;
import org.ku.nicheanalyst.common.Message;
import org.ku.nicheanalyst.maps.objects.GeoTiffObject;
import org.ku.nicheanalyst.ui.display.Displayer;

/**
 * @author Huijie Qiao
 *
 */
class Obj {
	private double range_min;
	private double range_max;
	private boolean is_max;
	private int count;

	public Obj (double range_min, double range_max, boolean is_max){
		this.range_max = range_max;
		this.range_min = range_min;
		this.is_max = is_max;
		this.count = 0;
	}
	public boolean isInRange(double value){
		if (is_max){
			if ((value>=range_min)&&(value<=range_max)){
				return true;
			}else{
				return false;
			}
		}else{
			if ((value>=range_min)&&(value<range_max)){
				return true;
			}else{
				return false;
			}
		}
		
	}
	public double getRange_min() {
		return range_min;
	}
	public void setRange_min(double rangeMin) {
		range_min = rangeMin;
	}
	public double getRange_max() {
		return range_max;
	}
	public void setRange_max(double rangeMax) {
		range_max = rangeMax;
	}
	public boolean isIs_max() {
		return is_max;
	}
	public void setIs_max(boolean isMax) {
		is_max = isMax;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public void addCount(){
		this.count++;
	}
}
public class OccurrenceDistributionStator extends SwingWorker<Void, Void> {
	private File[] Files;
	private String target;
	private int steps;
	private String occurrenceFile;
	private Displayer app;
	private boolean isnodata;
	private int nodata;
	private String enm_result;
	private double threshold;
	private StringBuilder  sb_html;
	
	public OccurrenceDistributionStator(Displayer app, File[] Files, String target, 
			int steps, String occurrence, String enm_result, boolean isnodata, int nodata, double threshold){
		this.Files = Files;
		this.target = target;
		this.steps = steps;
		this.occurrenceFile = occurrence;
		this.app = app;
		this.isnodata = isnodata;
		this.nodata = nodata;
		this.enm_result = enm_result;
		this.threshold = threshold;
	}
	
	@Override
	protected Void doInBackground() throws Exception {
		setProgress(0);
		int progress = 0;
		HashSet<double[]> lls = null;
		GeoTiffObject enm_geo = null;
		if (occurrenceFile!=null){
			File f = new File(occurrenceFile);
			if (f.exists()){
				lls = CommonFun.File2Array(occurrenceFile);
			}
			
		}
			
		if (enm_result!=null){
			File f = new File(enm_result);
			if (f.exists()){
				enm_geo = new GeoTiffObject(enm_result);
			}
		}
		sb_html = new StringBuilder();
		sb_html.append(Message.getString("html_head"));
		sb_html.append("<h3>Occurrence statistics</h3>" + Const.LineBreak);
		for (File f : Files){
			progress++;
			setProgress((int) (100f * progress / Files.length));
			GeoTiffObject geo = new GeoTiffObject(f.getAbsolutePath());
			
			
			double nodata = geo.getNoData();
			if (isnodata){
				nodata = this.nodata;
			}
			double[] maxmin = geo.getMaxMin(nodata);
			Obj[] objs = new Obj[steps];
			Obj[] objs_occurr = new Obj[steps];
			Obj[] objs_enm_result = new Obj[steps];
			int nodata_obj = 0;
			int nodata_occurr = 0;
			int nodata_enm = 0;
			this.app.addLog(String.format("Init occurrence distribution stat for '%s'.", f.getAbsoluteFile()));
			sb_html.append("<h4>Environmental layers: " + f.getAbsolutePath() + "</h4>" + Const.LineBreak);
			for (int i=0;i<steps;i++){
				boolean is_max = (i==(steps-1))?true:false;
				double range = (maxmin[1] - maxmin[0])/(double)steps;
				Obj obj1 = new Obj(maxmin[0] + (i * range), maxmin[0] + (i + 1) * range, is_max);
				objs[i] = obj1;
				Obj obj2 = new Obj(maxmin[0] + (i * range), maxmin[0] + (i + 1) * range, is_max);
				objs_occurr[i] = obj2;
				Obj obj3 = new Obj(maxmin[0] + (i * range), maxmin[0] + (i + 1) * range, is_max);
				objs_enm_result[i] = obj3;
			}
			this.app.addLog("Reading layer data.");
			for (int x=0;x<geo.getXSize();x++){
				for (int y=0;y<geo.getYSize();y++){
					double value = geo.readByXY(x, y);
					if (CommonFun.equal(nodata, value, 1000)){
						nodata_obj++;
						nodata_enm++;
						continue;
					}
					for (Obj obj : objs){
						if (obj.isInRange(value)){
							obj.addCount();
							break;
						}
					}
					if (enm_geo!=null){
						double enm_value = enm_geo.readByXY(x, y);
						if ((enm_value>=threshold)&&(!CommonFun.equal(nodata, enm_value, 1000))){
							for (Obj obj : objs_enm_result){
								if (obj.isInRange(value)){
									obj.addCount();
									break;
								}
							}
						}else{
							nodata_enm++;
						}
					}
				}
			}
			this.app.addLog("Finished to read layer data.");
			this.app.addLog("Reading occurrence data.");
			for (double[] ll : lls){
				double value = geo.readByLL(ll[0], ll[1]);
				if (CommonFun.equal(nodata, value, 1000)){
					nodata_occurr++;
					continue;
				}
				for (Obj obj : objs_occurr){
					if (obj.isInRange(value)){
						obj.addCount();
						break;
					}
				}
			}
			this.app.addLog("Finished to read occurrence data.");
			
			StringBuilder sb = new StringBuilder();
			sb.append("Range,Total_count,Occu_count,Enm_count" + Const.LineBreak);
			for (int i=0;i<objs.length;i++){
				sb.append(String.format("%f,%d,%d,%d%n", objs[i].getRange_min(), 
						objs[i].getCount(), objs_occurr[i].getCount(), objs_enm_result[i].getCount()));
			}
			sb.append(String.format("nodata,%d,%d,%d%n", nodata_obj, nodata_occurr, nodata_enm));
			
			String environment_layer = CommonFun.getFileNameWithoutExtension(f.getName());
			String filename = target + "/" + environment_layer + ".txt";
			this.app.addLog(String.format("Writing to file '%s'.", filename));
			CommonFun.writeFile(sb.toString(), filename);
			
			InputStream rscript = this.getClass().getResourceAsStream("/occurrence_statistics.r");
			HashMap<String, String> parameters = new HashMap<String, String>();
			parameters.put("@DataFile", filename.replace("\\", "/"));
			parameters.put("@Variable", environment_layer);
			parameters.put("@ResultName", target.replace("\\", "/") + "/" + environment_layer);
			parameters.put("@Target", target.replace("\\", "/"));
			
			HashSet<String> libraries = new HashSet<String>();
			libraries.add("ggplot2");
			libraries.add("plyr");
			libraries.add("scales");
			
			String r_script = CommonFun.RunRScript(rscript, parameters, libraries, true, target + "/rscript.r");

			sb_html.append("<h5>Point distribution (Occurrence points, ENM result with threshold " + this.threshold + " and Background)</h5>" + Const.LineBreak);
			sb_html.append("<img width=600 src='file://localhost/" + target.replace("\\", "/") + "/" + environment_layer + ".all.png'/>");
			sb_html.append("<h5>Log transferred point distribution (Occurrence points, ENM result with threshold " + this.threshold + " and Background)</h5>" + Const.LineBreak);
			sb_html.append("<img width=600 src='file://localhost/" + target.replace("\\", "/") + "/" + environment_layer + ".all.log.png'/>");
			sb_html.append("<h5>Shadowed point distribution (Occurrence points and ENM result with threshold " + this.threshold + ")</h5>" + Const.LineBreak);
			sb_html.append("<img width=600 src='file://localhost/" + target.replace("\\", "/") + "/" + environment_layer + ".shadow.png'/>");
			geo.release();
		}
		
		sb_html.append(Message.getString("html_tail"));
		enm_geo.release();
		setProgress(100);
		return null;
	}
	

	/**
	 * @param geoTransform
	 * @param xsize
	 * @param ysize
	 * @param getGeoTransform
	 * @param xSize2
	 * @param ySize2
	 * @return
	 */
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
	

	public File[] getFiles() {
		return Files;
	}

	public String getTarget() {
		return target;
	}

	public int getSteps() {
		return steps;
	}

	public String getHTMLResult() throws IOException{
		String html = target + "/result.html";
		CommonFun.writeFile(sb_html.toString(), html);
		return html;
	}
	
	
	
}
