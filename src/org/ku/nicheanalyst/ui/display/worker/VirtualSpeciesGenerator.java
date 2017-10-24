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
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

import javax.swing.SwingWorker;

import org.gdal.gdal.gdal;
import org.gdal.gdalconst.gdalconst;
import org.ku.nicheanalyst.algorithms.MinimumVolumeEllipsoidResult;
import org.ku.nicheanalyst.common.CommonFun;
import org.ku.nicheanalyst.common.Const;
import org.ku.nicheanalyst.dataset.SpeciesData;
import org.ku.nicheanalyst.dataset.SpeciesDataset;
import org.ku.nicheanalyst.exceptions.IllegalSelectionException;
import org.ku.nicheanalyst.maps.controllers.GeoTiffController;
import org.ku.nicheanalyst.maps.objects.GeoTiffObject;
import org.ku.nicheanalyst.ui.display.component.j3d.EllipsoidGroup;

import quickhull3d.Point3d;

/**
 * @author Huijie Qiao
 *
 */
public class VirtualSpeciesGenerator extends SwingWorker<Void, Void> {
	private HashMap<String, SpeciesData> point_pool;
	private String folder;
	private EllipsoidGroup ellipsoidGroup;
	private float scale;
	private String sampleTiff;
	private SpeciesDataset vs_use;
	private int selection;
	private boolean is3D;
	private int inout;
	public VirtualSpeciesGenerator(HashMap<String, SpeciesData> point_pool, 
			String folder, EllipsoidGroup ellipsoidGroup, float scale, 
			String sampleTiff, SpeciesDataset vs_use, int selection, boolean is3D, int inout){
		this.selection = selection;
		this.vs_use = vs_use;
		this.point_pool = point_pool;
		this.folder = folder;
		this.ellipsoidGroup = ellipsoidGroup;
		this.scale = scale;
		this.sampleTiff = sampleTiff;
		this.is3D = is3D;
		this.inout = inout;
	}
	
	@Override
	protected Void doInBackground() throws Exception {
		gdal.AllRegister();
		StringBuilder sb1 = new StringBuilder();
		sb1.append("V1,V2,V3" + Const.LineBreak);
		
		StringBuilder sbXY1 = new StringBuilder();
		sbXY1.append("X,Y" + Const.LineBreak);
		
		StringBuilder sbLL1 = new StringBuilder();
		sbLL1.append("long,lat" + Const.LineBreak);
		
		
		StringBuilder sb2 = new StringBuilder();
		sb2.append("V1,V2,V3" + Const.LineBreak);
		
		StringBuilder sbXY2 = new StringBuilder();
		sbXY2.append("X,Y" + Const.LineBreak);
		
		StringBuilder sbLL2 = new StringBuilder();
		sbLL2.append("long,lat" + Const.LineBreak);
		
		
		GeoTiffObject geosample = new GeoTiffObject(sampleTiff);
		int[] tiffvalues1 = new int[geosample.getXSize() * geosample.getYSize()];
		for (int i=0;i<tiffvalues1.length;i++){
			tiffvalues1[i] = 0;
		}
		
		int[] tiffvalues2 = new int[geosample.getXSize() * geosample.getYSize()];
		for (int i=0;i<tiffvalues2.length;i++){
			tiffvalues2[i] = 0;
		}
		
		int currentProgress = -1;
		int i = 0;
		MinimumVolumeEllipsoidResult mve = null;
		if (this.selection==1){
			 mve = CommonFun.getMVE(this.vs_use.getVs(), (this.is3D)?3:2);
		}
		Point3d[] vertices = null;
		if (this.selection==2){
			HashSet<double[]> values = new HashSet<double[]>();
			for (SpeciesData data : this.vs_use.getVs().values()){
				values.add(data.getValues());
			}
			vertices = CommonFun.getVertices(values);
		}
		
		for (String xy : this.point_pool.keySet()){
			int progress = Math.round((float)i * 100 / (float)this.point_pool.size());
			if (progress!=currentProgress){
				setProgress(progress);
				currentProgress = progress;
			}
			SpeciesData vs = this.point_pool.get(xy);
			double x = vs.getValues()[0];
			double y = vs.getValues()[1];
			double z = vs.getValues()[2];
			boolean isIn = false;
			if (this.selection==0){
				if (ellipsoidGroup.getEllipsoid().isInEllipsoid(
						(float)x / scale, 
						(float)y / scale, 
						(float)z / scale)){
					isIn = true;
					
				}
			}
			if (this.selection==1){
				isIn = CommonFun.isInEllipsoid(mve.getA(), mve.getCenter(), x, y, z);
			}
			if (this.selection==2){
				isIn = CommonFun.inConvexHull(new Point3d(x, y, z), vertices);
			}
			
			if (isIn){
				sb1.append(String.format("%f,%f,%f", x, y, z) + Const.LineBreak);
				sbXY1.append(String.format("%d,%d", vs.getX(), vs.getY()) + Const.LineBreak);
				sbLL1.append(String.format("%f,%f", vs.getLongitude(), vs.getLatitude()) + Const.LineBreak);
				tiffvalues1[vs.getY() * geosample.getXSize() + vs.getX()] = 255;
			}else{
				sb2.append(String.format("%f,%f,%f", x, y, z) + Const.LineBreak);
				sbXY2.append(String.format("%d,%d", vs.getX(), vs.getY()) + Const.LineBreak);
				sbLL2.append(String.format("%f,%f", vs.getLongitude(), vs.getLatitude()) + Const.LineBreak);
				tiffvalues2[vs.getY() * geosample.getXSize() + vs.getX()] = 255;
			}
			i++;
		}
		
		if ((inout==0)||(inout==2)){
			String t_folder = (inout==0)?folder:folder+"/inside";
			saveResult(t_folder, sb1, sbXY1, sbLL1, tiffvalues1, geosample);
		}
		if ((inout==1)||(inout==2)){
			String t_folder = (inout==1)?folder:folder+"/outside";
			saveResult(t_folder, sb2, sbXY2, sbLL2, tiffvalues2, geosample);
		}
		
		geosample.release();
		setProgress(100);
		return null;
	}
	private void saveResult(String t_folder, StringBuilder sb,
			StringBuilder sbXY, StringBuilder sbLL, int[] tiffvalues,
			GeoTiffObject geosample) throws IOException, IllegalSelectionException, InterruptedException {
		CommonFun.mkdirs(t_folder, false);
		GeoTiffController.createTiff(t_folder + "/present.tiff", geosample.getXSize(), geosample.getYSize(), geosample.getDataset().GetGeoTransform(), tiffvalues, 0, gdalconst.GDT_Byte, geosample.getDataset().GetProjection());
		GeoTiffController.toPNG(t_folder + "/present.tiff", t_folder + "/present.png");
		CommonFun.writeFile(sb.toString(), t_folder + "/value.txt");
		CommonFun.writeFile(sbXY.toString(), t_folder + "/xy.txt");
		CommonFun.writeFile(sbLL.toString(), t_folder + "/ll.txt");
		if (ellipsoidGroup!=null){
			if (ellipsoidGroup.getEllipsoid()!=null){
				ellipsoidGroup.getEllipsoid().save(t_folder + "/selection." + Const.ellipsoid);		
			}
		}
		GeoTiffController.resizePNG(t_folder + "/present.png", t_folder + "/present_1000.png", 1000);
		
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

	
	public String getFolder() {
		return folder;
	}

	public EllipsoidGroup getEllipsoidGroup() {
		return ellipsoidGroup;
	}

	public float getScale() {
		return scale;
	}
	
}
