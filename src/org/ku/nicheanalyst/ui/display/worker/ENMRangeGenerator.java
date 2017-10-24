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
import java.util.Locale;

import javax.swing.SwingWorker;

import org.gdal.gdal.gdal;
import org.gdal.gdalconst.gdalconst;
import org.ku.nicheanalyst.common.AUCResult;
import org.ku.nicheanalyst.common.CommonFun;
import org.ku.nicheanalyst.common.ConfigInfo;
import org.ku.nicheanalyst.common.Const;
import org.ku.nicheanalyst.common.Message;
import org.ku.nicheanalyst.exceptions.ParameterErrorException;
import org.ku.nicheanalyst.maps.controllers.GeoTiffController;
import org.ku.nicheanalyst.maps.objects.GeoTiffObject;
import org.ku.nicheanalyst.ui.display.component.j3d.EllipsoidGroup;
import org.ku.nicheanalyst.ui.display.dialog.HTMLViewer;

import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.PrincipalComponents;
import weka.filters.unsupervised.attribute.Standardize;

/**
 * @author Huijie Qiao
 *
 */
public class ENMRangeGenerator extends SwingWorker<Void, Void> {
	private String target;
	private File[] enmResults;
	private double[] fromRange;
	private double[] toRange;

	public ENMRangeGenerator(String target, File[] enmResults, double[] fromRange, double[] toRange){
		this.target = target;
		this.enmResults = enmResults;
		this.fromRange = fromRange;
		this.toRange = toRange;
	}
	
	@Override
	protected Void doInBackground() throws Exception {
		setProgress(0);
		double scale = (toRange[1] - toRange[0])/(fromRange[1] - fromRange[0]);
		StringBuilder sb = new StringBuilder();
		sb.append("X,Y,old_value,new_value" + Const.LineBreak);
		StringBuilder sb_html = new StringBuilder();
		sb_html.append(Message.getString("html_head"));
		sb_html.append(String.format(
				"<h3>Change all enm results below from : [%f, %f]</b> to [%f, %f].</h3>%n", 
				fromRange[0], fromRange[1], toRange[0], toRange[1]));
		for (int i=0; i<enmResults.length; i++){
			File f = enmResults[i];
			sb_html.append("<h3>ENM result: " + f.getAbsolutePath() + "</h3>");
			
			GeoTiffObject enm = new GeoTiffObject(f.getAbsolutePath());
			double[] values = enm.getValueArray();
			for (int x=0;x<enm.getXSize();x++){
				int process = (x * (100/enmResults.length))/ enm.getXSize()  + i * (100/enmResults.length);
				if (process>100){
					process = 100;
				}
				setProgress(process);
				for (int y=0;y<enm.getYSize();y++){
					double v = enm.readByXY(x, y);
					if (CommonFun.equal(v, enm.getNoData(), 1000)){
						continue;
					}
					if (v<0){
						continue;
					}
					
					double new_v = (v-fromRange[0]) * scale + toRange[0];
					sb.append(String.format("%d,%d,%f,%f%n", x, y, v, new_v));
					values[y * enm.getXSize() + x] = new_v;
				}
			}
			String t_name = CommonFun.getFileNameWithoutExtension(f.getName());
			GeoTiffController.createTiff(target + "/" + t_name + ".tif", 
					enm.getXSize(), enm.getYSize(), enm.getDataset().GetGeoTransform(), values, enm.getNoData(), gdalconst.GDT_Float32, enm.getDataset().GetProjection());
			CommonFun.writeFile(sb.toString(), target + "/" + t_name + ".csv");
			
			InputStream rscript = this.getClass().getResourceAsStream("/range.r");
			HashMap<String, String> parameters = new HashMap<String, String>();
			parameters.put("@Target", target.replace("\\", "/"));
			parameters.put("@DataSet", String.valueOf(t_name));
			
			HashSet<String> libraries = new HashSet<String>();
			libraries.add("ggplot2");

			
			String r_script = CommonFun.RunRScript(rscript, parameters, libraries, true, target + "/rscript." + t_name + ".r");
			
			sb_html.append("<h3>Histogram of original dataset</h3>");
			sb_html.append("<img width=600 src='file://localhost/" + target + "/histogram." + t_name + ".old.png'/>");
			
			sb_html.append("<h3>Histogram of changed dataset</h3>");
			sb_html.append("<img width=600 src='file://localhost/" + target + "/histogram." + t_name + ".new.png'/>");
			
			enm.release();
		}
		sb_html.append(Message.getString("html_tail"));
		CommonFun.writeFile(sb_html.toString(), target + "/result.html");
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

	public String getHTMLResult() {
		return target + "/result.html";
	}

	
	
}
