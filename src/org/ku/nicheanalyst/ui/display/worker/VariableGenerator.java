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

import org.gdal.gdal.gdal;
import org.gdal.gdalconst.gdalconst;
import org.ku.nicheanalyst.common.CommandExecutor;
import org.ku.nicheanalyst.common.CommonFun;
import org.ku.nicheanalyst.common.Const;
import org.ku.nicheanalyst.dataset.GompertzCurveParameters;
import org.ku.nicheanalyst.maps.controllers.GeoTiffController;
import org.ku.nicheanalyst.maps.objects.GeoTiffObject;

/**
 * @author Huijie Qiao
 *
 */
public class VariableGenerator extends SwingWorker<Void, Void> {
	private String glacialFile;
	private String target;
	private String interglacialFile;
	private GompertzCurveParameters parameters;
	private int status;
	private int currentIteration;
	public VariableGenerator(String glacialFile, String interglacialFile, String target, 
			GompertzCurveParameters parameters){
		this.glacialFile = glacialFile;
		this.target = target;
		this.interglacialFile = interglacialFile;
		this.parameters = parameters;
	}
	
	@Override
	protected Void doInBackground() throws Exception {
		
		GeoTiffObject glacial = new GeoTiffObject(glacialFile);
		GeoTiffObject interglacial = new GeoTiffObject(interglacialFile);
		

		CommonFun.mkdirs(target, true);
		CommonFun.writeFile(this.parameters.getDescription(), target + "/parameters.txt");
		this.parameters.saveToXML(target + "/parameters.xml");
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("%s,%s%n", "Years", "Value"));
		setProgress(1);
		int currentYears = parameters.getStartingPoint();
		currentIteration = 1;
		
		while (true){
			int vx = currentYears;
			int c = 1;
			while (parameters.getWarm_years()<vx){
				vx = currentYears - c * (parameters.getWarm_years() - parameters.getCold_years());
				c++;
			}
			currentIteration = c;
			String tiffName = "";
			if (vx>=0){
				tiffName = String.format("%s/P%05d.tif", target, vx);
			}else{
				tiffName = String.format("%s/N%05d.tif", target, Integer.valueOf(Math.abs(vx)).intValue());
			}
			File file = new File(tiffName);
			if (file.exists()){
				break;
			}
			
			double vy = (vx>0)?
					parameters.getFunc2().getVal(new double[]{vx}):
						parameters.getFunc1().getVal(new double[]{vx});
			if (vy<0){
				vy = 0;
			}
			if (vy>1){
				vy = 1;
			}
			double[] values = new double[glacial.getXSize() * glacial.getYSize()];
			int[] firstDataPos = null;
			for (int x = 0; x<glacial.getXSize();x++){
				for (int y = 0; y<glacial.getYSize();y++){
					double highValue = interglacial.readByXY(x, y);
					double lowValue = glacial.readByXY(x, y);
					if (CommonFun.equal(highValue, interglacial.getNoData(), 1000)){
						values[y * glacial.getXSize() + x] = interglacial.getNoData();
					}else{
						if (CommonFun.equal(lowValue, glacial.getNoData(), 1000)){
							values[y * glacial.getXSize() + x] = glacial.getNoData();
						}else{
							// need to discuss
							//values[y * glacial.getXSize() + x] = (highValue - lowValue) * vy + lowValue;
							values[y * glacial.getXSize() + x] = lowValue - (lowValue - highValue) * vy;
							if ((!CommonFun.equal(values[y * glacial.getXSize() + x], 0, 1000))&&(firstDataPos==null)){
								firstDataPos = new int[]{x, y};
							}
						}
					}
					
				}
			}
			sb.append(String.format("%d,%f%n", 
					currentYears * parameters.getSamplingFrequency(), 
					values[firstDataPos[1] * glacial.getXSize() + firstDataPos[0]]));
			GeoTiffController.createTiff(
					tiffName, 
					glacial.getXSize(), glacial.getYSize(), 
					glacial.getDataset().GetGeoTransform(), 
					values, glacial.getNoData(), gdalconst.GDT_Float32, glacial.getDataset().GetProjection());
			CommonFun.writeFile(sb.toString(), target + "/curve.txt");
			setProgress((currentYears - parameters.getStartingPoint()) * 100 / 
					((parameters.getWarm_years() - parameters.getCold_years())));
			currentYears++;
			
			
		}
		glacial.release();
		interglacial.release();
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

	public String getGlacialFile() {
		return glacialFile;
	}

	public String getTarget() {
		return target;
	}

	public String getInterglacialFile() {
		return interglacialFile;
	}

	public GompertzCurveParameters getParameters() {
		return parameters;
	}

	public int getStatus() {
		return status;
	}

	

	
	
	
	
}
