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
import org.ku.nicheanalyst.common.CommonFun;
import org.ku.nicheanalyst.common.ConfigInfo;
import org.ku.nicheanalyst.common.Const;
import org.ku.nicheanalyst.common.Message;
import org.ku.nicheanalyst.exceptions.ParameterErrorException;
import org.ku.nicheanalyst.maps.controllers.GeoTiffController;
import org.ku.nicheanalyst.maps.objects.GeoTiffObject;
import org.ku.nicheanalyst.ui.display.component.j3d.EllipsoidGroup;

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
public class PCAGenerator extends SwingWorker<Void, Void> {
	private ArrayList<String> filenames_origin;
	private ArrayList<String> filenames_target;
	private String target;
	private StringBuilder  sb_html;
	private boolean is_trans;
	public PCAGenerator(ArrayList<String> filenames_origin, ArrayList<String> filenames_target, String target, boolean is_trans){
		this.filenames_origin = filenames_origin;
		this.filenames_target = filenames_target;
		this.target = target;
		this.is_trans = is_trans;
	}
	
	@Override
	protected Void doInBackground() throws Exception {
		
		gdal.AllRegister();
		int i = 0;
		while(true){
	    	Thread.sleep(100);
	    	setProgress(i++);
	    	if (i>10){
	    		break;
	    	}
	    }
		
		InputStream rscript = this.getClass().getResourceAsStream("/pca_t.r");
		HashMap<String, String> parameters = new HashMap<String, String>();
		parameters.put("@ISTRANS", (is_trans)?"T":"F");
		parameters.put("@TARGET", target.replace("\\", "/"));
		
		String ORITARGET = (is_trans)?target.replace("\\", "/") + "/origin":target.replace("\\", "/");
		String TRANSTARGET = (is_trans)?target.replace("\\", "/") + "/trans":target.replace("\\", "/");
		parameters.put("@ORITARGET", ORITARGET);
		parameters.put("@TRANSTARGET", TRANSTARGET);
		
		CommonFun.mkdirs(ORITARGET, true);
		CommonFun.mkdirs(TRANSTARGET, true);
		
		String f = "";
		
		for (String file : filenames_origin){
			if (i>=100){
				i = 99;
			}
			setProgress(i++);
			
			if (f.equals("")){
				f = "\"" + file.replace("\\", "/") + "\"" + Const.LineBreak;
			}else{
				f += ",\"" + file.replace("\\", "/") + "\"" + Const.LineBreak;
			}
		}
		parameters.put("@ORILIST", f);
		
		f = "";
		for (String file : filenames_target){
			if (i>=100){
				i = 99;
			}
			setProgress(i++);
			
			if (f.equals("")){
				f = "\"" + file.replace("\\", "/") + "\"" + Const.LineBreak;
			}else{
				f += ",\"" + file.replace("\\", "/") + "\"" + Const.LineBreak;
			}
		}
		parameters.put("@TRANSLIST", f);
		
		HashSet<String> libraries = new HashSet<String>();
		libraries.add("ggplot2");
		libraries.add("grid");
		setProgress(20);
		String r_script = CommonFun.RunRScript(rscript, parameters, libraries, true, target + "/rscript.r");
		
		
	    
	    
	    File cf = new File(ORITARGET);
	    
	    if (cf.list().length>=3){
	    	generateBackgroundFolder(ORITARGET);
	    	setProgress(90);
	    	if (is_trans){
	    		generateBackgroundFolder(TRANSTARGET);
	    	}
	    }
	    
	    
		sb_html = new StringBuilder();
		sb_html.append(Message.getString("html_head"));
		sb_html.append("<img width=600 src='file://localhost/" + target + "/proportion.png'/>");
		sb_html.append("<h3>Biplot for Principal Components 1 and 2</h3>" + Const.LineBreak);
		
		sb_html.append("<img width=600 src='file://localhost/" + target + "/biplot.png'/>");
		sb_html.append(Message.getString("html_tail"));
		
		setProgress(100);
		return null;
	}
	private void generateBackgroundFolder(String target) throws IOException {

    	GeoTiffObject geo1 = new GeoTiffObject(target + "/PC1.tif");
	    GeoTiffObject geo2 = new GeoTiffObject(target + "/PC2.tif");
	    GeoTiffObject geo3 = new GeoTiffObject(target + "/PC3.tif");
	    int xsize = geo1.getXSize();
	    int ysize = geo1.getYSize();
    	int[] presentvalues = new int[xsize * ysize];
	    StringBuilder ssll = new StringBuilder();
	    ssll.append("long,lat" + Const.LineBreak);
	    
	    StringBuilder ssxy = new StringBuilder();
	    ssxy.append("X,Y" + Const.LineBreak);
	    
	    StringBuilder ssvalue = new StringBuilder();
	    ssvalue.append("V1,V2,V3" + Const.LineBreak);
	    
	    CommonFun.mkdirs(target + "/background", true);
	    for (int y=0;y<ysize;y++){
			for (int x=0;x<xsize;x++){
				double value1 = geo1.readByXY(x, y);
				double value2 = geo2.readByXY(x, y);
				double value3 = geo3.readByXY(x, y);
				if ((!CommonFun.equal(value1, geo1.getNoData(), 1000))&&
						(!CommonFun.equal(value2, geo2.getNoData(), 1000))&&
						(!CommonFun.equal(value3, geo3.getNoData(), 1000))){
					double[] ll = CommonFun.PositionToLL(geo1.getDataset().GetGeoTransform(), new int[]{x, y});
					ssll.append(String.format(Locale.ENGLISH, "%f,%f", ll[0], ll[1]) + Const.LineBreak);
					ssxy.append(String.format("%d,%d", x, y) + Const.LineBreak);
					ssvalue.append(String.format(Locale.ENGLISH, "%f,%f,%f", value1, value2, value3) + Const.LineBreak);
					presentvalues[y * xsize + x] = 255;
				}else{
					presentvalues[y * xsize + x] = Const.NoData;
				}
					
			}
	    }
	    GeoTiffController.createTiff(target + "/background/present.tiff", 
	    		xsize, ysize, geo1.getDataset().GetGeoTransform() , presentvalues, Const.NoData, gdalconst.GDT_Int32, geo1.getDataset().GetProjection());
	    CommonFun.writeFile(ssll.toString(), target + "/background/ll.txt");
	    CommonFun.writeFile(ssxy.toString(), target + "/background/xy.txt");
	    CommonFun.writeFile(ssvalue.toString(), target + "/background/value.txt");
	    geo1.release();
	    geo2.release();
	    geo3.release();
		
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
	private boolean equalParameter(double[] geoTransform, int xsize, int ysize,
			double[] geoTransform2, int xSize2, int ySize2) {
		if (CommonFun.equal(geoTransform[0], geoTransform2[0], 1000)&&
				CommonFun.equal(geoTransform[1], geoTransform2[1], 1000)&&
				CommonFun.equal(geoTransform[2], geoTransform2[2], 1000)&&
				CommonFun.equal(geoTransform[3], geoTransform2[3], 1000)&&
				CommonFun.equal(geoTransform[4], geoTransform2[4], 1000)&&
				CommonFun.equal(geoTransform[5], geoTransform2[5], 1000)&&
				(xsize==xSize2)&&
				(ysize==ySize2)){
			return true;
		}
		return false;
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

	public String getHTMLResult() throws IOException {
		String html = target + "/result.html";
		CommonFun.writeFile(sb_html.toString(), html);
		return html;
	}
	
}
