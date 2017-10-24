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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import javax.swing.SwingWorker;

import org.gdal.gdal.gdal;
import org.gdal.gdalconst.gdalconst;
import org.ku.nicheanalyst.algorithms.MinimumVolumeEllipsoidResult;
import org.ku.nicheanalyst.common.CommonFun;
import org.ku.nicheanalyst.common.Const;
import org.ku.nicheanalyst.dataset.SpeciesData;
import org.ku.nicheanalyst.maps.controllers.GeoTiffController;
import org.ku.nicheanalyst.maps.objects.GeoTiffObject;


/**
 * @author Huijie Qiao
 *
 */
public class SpeciesGenerator extends SwingWorker<Void, Void> {
	private String[] llfilenames;
	private String parent_target;
	private HashMap<String, SpeciesData> backgroundValues;
	private String sampleTiff;
	private int type;
	public SpeciesGenerator(String[] llfilenames, String parent_target, 
			HashMap<String, SpeciesData> backgroundValues,
			String sampleTiff, int type){
		this.llfilenames = llfilenames;
		this.parent_target = parent_target;
		this.backgroundValues = backgroundValues;
		this.sampleTiff = sampleTiff;
		this.type = type;
	}
	
	@Override
	protected Void doInBackground() throws Exception {
		gdal.AllRegister();
		
		GeoTiffObject geosample = new GeoTiffObject(sampleTiff);
		double[] geoTransform = new double[6];
		geoTransform = geosample.getDataset().GetGeoTransform();
		
		int[] tiffvalues = new int[geosample.getXSize() * geosample.getYSize()];
		for (int i=0;i<tiffvalues.length;i++){
			tiffvalues[i] = 0;
		}
		setProgress(0);
		try {
			for (String llfilename : this.llfilenames){
				
				StringBuilder sb = new StringBuilder();
				sb.append("V1,V2,V3" + Const.LineBreak);
				
				StringBuilder sbXY = new StringBuilder();
				sbXY.append("X,Y" + Const.LineBreak);
				
				StringBuilder sbLL = new StringBuilder();
				sbLL.append("long,lat" + Const.LineBreak);
				
				String target = parent_target + "/" +  CommonFun.getFileNameWithoutPathAndExtension(llfilename);
				
				if (type==0){
					ArrayList<String> lls = CommonFun.readFromFile(llfilename);
					HashSet<String> xystr = new HashSet<String>();
					int ii = 0;
					for (String ll : lls){
						int progress = (int) ((20f * ii)/lls.size());
						if (progress >= 100){
							progress = 99;
						}
						setProgress(progress);
						
						String[] llsplit = ll.replace("\t", ",").split(",");
						if (llsplit.length==2){
							if (CommonFun.isDouble(llsplit[0])&&(CommonFun.isDouble(llsplit[1]))){
								double[] long_lat = new double[]{Double.valueOf(llsplit[0]), Double.valueOf(llsplit[1])};
								int[] xy = CommonFun.LLToPosition(geoTransform, long_lat);
								ii++;
								xystr.add(String.format("%d,%d", xy[0], xy[1]));
							}
						}
						if (llsplit.length>=3){
							if (CommonFun.isDouble(llsplit[1])&&(CommonFun.isDouble(llsplit[2]))){
								double[] long_lat = {Double.valueOf(llsplit[1]).doubleValue(), Double.valueOf(llsplit[2]).doubleValue()};
								int[] xy = CommonFun.LLToPosition(geoTransform, long_lat);
								ii++;
								xystr.add(String.format("%d,%d", xy[0], xy[1]));
							}
						}
					}
					ii = 0;
					for (String xy : backgroundValues.keySet()){	
						setProgress(20 + (int) ((79f * ii)/backgroundValues.size()));
						
						
						if (xystr.contains(xy)){
							SpeciesData vs = backgroundValues.get(xy);
							tiffvalues[vs.getY() * geosample.getXSize() + vs.getX()] = 255;
							sb.append(String.format("%f,%f,%f", vs.getValues()[0], vs.getValues()[1], vs.getValues()[2]) + Const.LineBreak);
							sbXY.append(String.format("%d,%d", vs.getX(), vs.getY()) + Const.LineBreak);
							sbLL.append(String.format("%f,%f", vs.getLongitude(), vs.getLatitude()) + Const.LineBreak);
						}
					}
				}
				
				if (type==1){
					MinimumVolumeEllipsoidResult mve = new MinimumVolumeEllipsoidResult(llfilename);
					if (mve.getA()==null){
						continue;
					}
					int ii = 0;
					for (String xy : backgroundValues.keySet()){
						int progress = (int) ((100f * ii)/backgroundValues.size());
						if (progress >= 100){
							progress = 99;
						}
						setProgress(progress);
						SpeciesData vs = backgroundValues.get(xy);
						double[] value = vs.getValues();
						if (CommonFun.isInEllipsoid(mve.getA(), mve.getCenter(), value)){
							tiffvalues[vs.getY() * geosample.getXSize() + vs.getX()] = 255;
							sb.append(String.format("%f,%f,%f", vs.getValues()[0], vs.getValues()[1], vs.getValues()[2]) + Const.LineBreak);
							sbXY.append(String.format("%d,%d", vs.getX(), vs.getY()) + Const.LineBreak);
							sbLL.append(String.format("%f,%f", vs.getLongitude(), vs.getLatitude()) + Const.LineBreak);
						}
					}
				}
				CommonFun.mkdirs(target, false);
				GeoTiffController.createTiff(target + "/present.tiff", geosample.getXSize(), 
						geosample.getYSize(), geosample.getDataset().GetGeoTransform(), tiffvalues, 0, gdalconst.GDT_Byte, geosample.getDataset().GetProjection());
				GeoTiffController.toPNG(target + "/present.tiff", target + "/present.png");
				CommonFun.writeFile(sb.toString(), target + "/value.txt");
				CommonFun.writeFile(sbXY.toString(), target + "/xy.txt");
				CommonFun.writeFile(sbLL.toString(), target + "/ll.txt");
				GeoTiffController.resizePNG(target + "/present.png", target + "/present_1000.png", 1000);
			}
			
			setProgress(100);
			geosample.release();
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			setProgress(50);
			geosample.release();
			return null;
		}
		
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
		return parent_target;
	}

	public String getSampleTiff() {
		return sampleTiff;
	}

	
	
	
}
