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

import java.awt.Point;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.SwingWorker;

import org.gdal.gdal.gdal;
import org.gdal.gdalconst.gdalconst;
import org.ku.nicheanalyst.common.CommonFun;
import org.ku.nicheanalyst.common.Const;
import org.ku.nicheanalyst.dataset.SpeciesData;
import org.ku.nicheanalyst.dataset.SpeciesDataset;
import org.ku.nicheanalyst.maps.controllers.GeoTiffController;
import org.ku.nicheanalyst.maps.objects.GeoTiffObject;
import org.ku.nicheanalyst.ui.display.component.j3d.EllipsoidGroup;

/**
 * @author Huijie Qiao
 *
 */
public class BarrierSpeciesGenerator extends SwingWorker<Void, Void> {
	private String tiffFile;
	private ArrayList<Point> barriers;
	private SpeciesDataset vs;
	private String barrierName;
	private String folder;
	public BarrierSpeciesGenerator(String folder, SpeciesDataset vs, String tiffFile, String barrierName, ArrayList<Point> barriers) throws IOException{
		this.barrierName = barrierName;
		this.vs = vs;
		this.tiffFile = tiffFile;
		this.barriers = barriers;
		this.folder = folder;
	}
	
	@Override
	protected Void doInBackground() throws Exception {
		gdal.AllRegister();
		StringBuilder sb_in = new StringBuilder();
		sb_in.append("V1,V2,V3" + Const.LineBreak);
		StringBuilder sbXY_in = new StringBuilder();
		sbXY_in.append("X,Y" + Const.LineBreak);
		StringBuilder sbLL_in = new StringBuilder();
		sbLL_in.append("long,lat" + Const.LineBreak);
		
		GeoTiffObject geosample = new GeoTiffObject(tiffFile);
		int[] tiffvalues_in = new int[geosample.getXSize() * geosample.getYSize()];
		for (int i=0;i<tiffvalues_in.length;i++){
			tiffvalues_in[i] = 0;
		}
		
		StringBuilder sb_out = new StringBuilder();
		sb_out.append("V1,V2,V3" + Const.LineBreak);
		
		StringBuilder sbXY_out = new StringBuilder();
		sbXY_out.append("X,Y" + Const.LineBreak);
		
		StringBuilder sbLL_out = new StringBuilder();
		sbLL_out.append("long,lat" + Const.LineBreak);
		
		int[] tiffvalues_out = new int[geosample.getXSize() * geosample.getYSize()];
		for (int i=0;i<tiffvalues_out.length;i++){
			tiffvalues_out[i] = 0;
		}
		
		
		int currentProgress = -1;
		int i = 0;
		for (String xystr : this.vs.getVs().keySet()){
			SpeciesData vsdata = this.vs.getVs().get(xystr);
			int progress = Math.round((float)i * 100 / (float)this.vs.getVs().size());
			if (progress!=currentProgress){
				setProgress(progress);
				currentProgress = progress;
			}
			
			if (CommonFun.inPolygon(new Point(vsdata.getX(), vsdata.getY()), barriers)){
				sb_in.append(String.format("%f,%f,%f", vsdata.getValues()[0], vsdata.getValues()[1], vsdata.getValues()[2]) + Const.LineBreak);
				sbXY_in.append(String.format("%d,%d", vsdata.getX(), vsdata.getY()) + Const.LineBreak);
				sbLL_in.append(String.format("%f,%f", vsdata.getLongitude(), vsdata.getLatitude()) + Const.LineBreak);
				tiffvalues_in[vsdata.getY() * geosample.getXSize() + vsdata.getX()] = 255;
			}else{
				sb_out.append(String.format("%f,%f,%f", vsdata.getValues()[0], vsdata.getValues()[1], vsdata.getValues()[2]) + Const.LineBreak);
				sbXY_out.append(String.format("%d,%d", vsdata.getX(), vsdata.getY()) + Const.LineBreak);
				sbLL_out.append(String.format("%f,%f", vsdata.getLongitude(), vsdata.getLatitude()) + Const.LineBreak);
				tiffvalues_out[vsdata.getY() * geosample.getXSize() + vsdata.getX()] = 255;
			}
			i++;
		}
		CommonFun.mkdirs(folder + "/" + barrierName + "/in", true);
		CommonFun.mkdirs(folder + "/" + barrierName + "/out", true);
		StringBuilder barriersSB = new StringBuilder();
		for (Point p : barriers){
			barriersSB.append(String.format("%d,%d" + Const.LineBreak, p.x, p.y));
		}
		CommonFun.writeFile(barriersSB.toString(), folder + "/" + barrierName + "/barriers.bar");
		GeoTiffController.createTiff(folder + "/" + barrierName + "/in/present.tiff", 
				geosample.getXSize(), geosample.getYSize(), geosample.getDataset().GetGeoTransform(), 
				tiffvalues_in, 0, gdalconst.GDT_Byte, geosample.getDataset().GetProjection());
		GeoTiffController.toPNG(folder + "/"+ barrierName + "/in/present.tiff", folder + "/" + barrierName + "/in/present.png");
		CommonFun.writeFile(sb_in.toString(), folder + "/" + barrierName+ "/in/value.txt");
		CommonFun.writeFile(sbXY_in.toString(), folder + "/" + barrierName + "/in/xy.txt");
		CommonFun.writeFile(sbLL_in.toString(), folder + "/" + barrierName + "/in/ll.txt");
		GeoTiffController.resizePNG(folder + "/" + barrierName + "/in/present.png", folder + "/" + barrierName + "/in/present_1000.png", 1000);
		
		GeoTiffController.createTiff(folder + "/" + barrierName + "/out/present.tiff", 
				geosample.getXSize(), geosample.getYSize(), geosample.getDataset().GetGeoTransform(), 
				tiffvalues_out, 0, gdalconst.GDT_Byte, geosample.getDataset().GetProjection());
		GeoTiffController.toPNG(folder + "/"+ barrierName + "/out/present.tiff", folder + "/" + barrierName + "/out/present.png");
		CommonFun.writeFile(sb_out.toString(), folder + "/" + barrierName+ "/out/value.txt");
		CommonFun.writeFile(sbXY_out.toString(), folder + "/" + barrierName + "/out/xy.txt");
		CommonFun.writeFile(sbLL_out.toString(), folder + "/" + barrierName + "/out/ll.txt");
		GeoTiffController.resizePNG(folder + "/" + barrierName + "/out/present.png", folder + "/" + barrierName + "/out/present_1000.png", 1000);
		geosample.release();
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
	
}
