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

import javax.swing.SwingWorker;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.gdal.gdal.gdal;
import org.gdal.gdalconst.gdalconst;
import org.ku.nicheanalyst.common.CommonFun;
import org.ku.nicheanalyst.common.Const;
import org.ku.nicheanalyst.maps.controllers.GeoTiffController;
import org.ku.nicheanalyst.maps.objects.GeoTiffObject;
import org.ku.nicheanalyst.ui.display.Displayer;

/**
 * @author Huijie Qiao
 *
 */
public class SetNodataGeoLayerProcessor extends SwingWorker<Void, Void> {
	private ArrayList<String> filenames;
	private String target;
	private int nodata;
	public SetNodataGeoLayerProcessor(Displayer displayer, ArrayList<String> filenames, String target, int nodata){
		this.filenames = filenames;
		this.target = target;
		this.nodata = nodata;
	}
	
	@Override
	protected Void doInBackground() throws Exception {
		
		gdal.AllRegister();
		setProgress(0);
		int index = 0;
		
        for (String geofile : filenames){        	
        	GeoTiffObject geo = new GeoTiffObject(geofile);
        	double[] values = new double[geo.getXSize() * geo.getYSize()];
        	for (int x=0;x<geo.getXSize();x++){
        		for (int y=0;y<geo.getYSize();y++){
        			double value = geo.readByXY(x, y);
        			if (!CommonFun.equal(value, geo.getNoData(), 1000)){
        				values[y * geo.getXSize() + x] = value;  
        			}else{
        				values[y * geo.getXSize() + x] = nodata;
        			}
        		}
        	}
        	File file = new File(geofile);
        	GeoTiffController.createTiff(target + "/" + file.getName(), 
        			geo.getXSize(), geo.getYSize(), 
        			geo.getDataset().GetGeoTransform(), 
        			values, nodata, geo.getDataType(), geo.getDataset().GetProjection());
        	setProgress((index) * 100 / filenames.size());
        	index++;
        	geo.release();
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

	public ArrayList<String> getFilenames() {
		return filenames;
	}

	public void setFilenames(ArrayList<String> filenames) {
		this.filenames = filenames;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}
	
	
}
