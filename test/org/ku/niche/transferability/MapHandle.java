/******************************************************************************
 * Huijie Qiao
 *
 * Project:  NicheA
 * Purpose:  
 * Created date: Nov 27, 2012 9:36:30 PM
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


package org.ku.niche.transferability;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

import org.gdal.gdalconst.gdalconst;
import org.junit.Test;
import org.ku.nicheanalyst.common.CommonFun;
import org.ku.nicheanalyst.common.Const;
import org.ku.nicheanalyst.maps.controllers.GeoTiffController;
import org.ku.nicheanalyst.maps.objects.GeoTiffObject;

/**
 * @author Huijie Qiao
 *
 */
public class MapHandle {
	@Test
	public void calculateNoDataCount() throws FileNotFoundException{
		String tiffFile = "/Users/huijieqiao/Dropbox/Papers/NicheTransferability/VS/VS1_Small/present.%d_left.tiff";
		for (int i=1;i<=6;i++){
			GeoTiffObject geo = new GeoTiffObject(String.format(tiffFile, i));
			int pointCount = 0;
			
			for (int x=0;x<geo.getXSize();x++){
				for (int y=0;y<geo.getYSize();y++){
					if (CommonFun.equal(geo.readByXY(x, y), 255f, 1000)){
						pointCount++;
					}
				}
			}
			System.out.println(i + ":" + pointCount);
		}
		
	}
	@Test
	public void createBarriers() throws FileNotFoundException{
		int barriersCount = 6;
		String tiffFile = "/Users/huijieqiao/Dropbox/Papers/NicheTransferability/VS/VS2_SMALL/present.tiff";
		GeoTiffObject bg = new GeoTiffObject("/Users/huijieqiao/Dropbox/Papers/NicheTransferability/Layers/EnvironmentalLayers/bio5_cut.tiff");
		GeoTiffObject geo = new GeoTiffObject(tiffFile);
		int pointCount = 0;
		
		for (int x=0;x<geo.getXSize();x++){
			for (int y=0;y<geo.getYSize();y++){
				if (CommonFun.equal(geo.readByXY(x, y), 255f, 1000)){
					pointCount++;
				}
			}
		}
		System.out.println(pointCount);
		int currentCound = 0;
		int currentStep = 1;
		double idealCount = (double)(pointCount * currentStep)/(double)(barriersCount);
		int[] values = new int[geo.getXSize() * geo.getYSize()];
		double[] values_2 = geo.getValueArray();
		for (int x=0;x<geo.getXSize();x++){
			for (int y=0;y<geo.getYSize();y++){
				values[y * geo.getXSize() + x] = -9999;
				
			}
		}
		
		for (int x=0;x<geo.getXSize();x++){
			for (int y=0;y<geo.getYSize();y++){
				
				int v = (int) geo.readByXY(x, y);
				int bgvalue = (int) bg.readByXY(x, y);
				if (v==255){
					values[y * geo.getXSize() + x] = v;
				}else{
					if (bgvalue!=0){
						values[y * geo.getXSize() + x] = 0;
					}
				}
				if (CommonFun.equal(geo.readByXY(x, y), 255f, 1000)){
					currentCound++;
				}
			}
			if (currentCound>=idealCount){
				System.out.println("Creating " + currentStep);
				for (int x2=0;x2<=x;x2++){
					for (int y=0;y<geo.getYSize();y++){
						values_2[y * geo.getXSize() + x2] = -9999;
					}
				}
				for (int x2=x+1;x2<geo.getXSize();x2++){
					for (int y=0;y<geo.getYSize();y++){
						int bgvalue = (int) bg.readByXY(x2, y);
						if (bgvalue==0){
							values_2[y * geo.getXSize() + x2] = -9999;
						}
					}
				}
				GeoTiffController.createTiff(tiffFile.replace(".tiff", "") + "." + currentStep + "_left.tiff", 
						geo.getXSize(), geo.getYSize(), geo.getDataset().GetGeoTransform(), 
						values, -9999, gdalconst.GDT_Int32, geo.getDataset().GetProjection());
				GeoTiffController.createTiff(tiffFile.replace(".tiff", "") + "." + currentStep + "_right.tiff", 
						geo.getXSize(), geo.getYSize(), geo.getDataset().GetGeoTransform(), 
						values_2, -9999, gdalconst.GDT_Int32, geo.getDataset().GetProjection());
				currentStep++;
				idealCount = (double)(pointCount * currentStep)/(double)(barriersCount);
			}
		}
	}
	@Test
	public void convertLL2XY() throws IOException{
		File folder = new File("/Users/huijieqiao/Dropbox/Papers/NicheTransferability/VS");
		GeoTiffObject geo = new GeoTiffObject(folder.getAbsolutePath() + "/VS1_BIG/present.tiff");
		double[] geotrans = geo.getDataset().GetGeoTransform();
		HashSet<File> llFiles = getAllLL(folder);
		for (File llFile : llFiles){
			ArrayList<String> lls = CommonFun.readFromFile(llFile.getAbsolutePath());
			StringBuilder sb = new StringBuilder();
			for (String llstr : lls){
				if (!llstr.contains("long,")){
					double[] ll = new double[]{Double.valueOf(llstr.split(",")[0]), Double.valueOf(llstr.split(",")[1])};
					int[] xy = CommonFun.LLToPosition(geotrans, ll);
					sb.append(String.format("%d,%d%n", xy[0], xy[1]));
				}else{
					sb.append(llstr + Const.LineBreak);
				}
			}
			CommonFun.writeFile(sb.toString(), llFile.getAbsolutePath().replace("ll", "xy"));
		}
	}
	/**
	 * @param folder
	 * @return
	 */
	private HashSet<File> getAllLL(File folder) {
		HashSet<File> files = new HashSet<File>();
		for (File f : folder.listFiles()){
			if (f.isDirectory()){
				HashSet<File> subfiles = getAllLL(f);
				for (File subf : subfiles){
					files.add(subf);
				}
			}else{
				if (f.getName().contains("ll")&&f.getName().endsWith(".txt")){
					files.add(f);
				}
			}
		}
		return files;
	}
}
