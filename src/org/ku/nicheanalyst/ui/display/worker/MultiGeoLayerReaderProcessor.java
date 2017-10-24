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

import org.gdal.gdal.gdal;
import org.ku.nicheanalyst.common.CommonFun;
import org.ku.nicheanalyst.common.Const;
import org.ku.nicheanalyst.maps.objects.GeoTiffObject;
import org.ku.nicheanalyst.ui.display.Displayer;

/**
 * @author Huijie Qiao
 *
 */
public class MultiGeoLayerReaderProcessor extends SwingWorker<Void, Void> {
	private ArrayList<String> filenames;
	private String target;
	private double[] ll;
	public MultiGeoLayerReaderProcessor(Displayer displayer, ArrayList<String> filenames, String target, double[] ll){
		this.filenames = filenames;
		this.target = target;
		this.ll = ll;
	}
	
	@Override
	protected Void doInBackground() throws Exception {
		
		gdal.AllRegister();
		setProgress(0);
		StringBuilder sb = new StringBuilder();
		sb.append("filename,value" + Const.LineBreak);
		int i = 0;
        for (String geofile : filenames){
        	
        	File f = new File(geofile);
        	GeoTiffObject geo = new GeoTiffObject(geofile);
        	sb.append(String.format("%s,%f%n", f.getName(), geo.readByLL(ll[0], ll[1])));
            CommonFun.writeFile(sb.toString(), target);
        	setProgress((i) * 100 / filenames.size());
        	geo.release();
        	i++;

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
