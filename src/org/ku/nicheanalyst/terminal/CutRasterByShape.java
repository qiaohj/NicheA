/******************************************************************************
 * Huijie Qiao
 *
 * Project:  NicheA
 * Purpose:  
 * Created date: Sep 13, 2012 5:56:36 PM
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


package org.ku.nicheanalyst.terminal;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

import org.gdal.gdal.gdal;
import org.gdal.gdalconst.gdalconst;
import org.gdal.ogr.Feature;
import org.gdal.ogr.ogr;
import org.ku.nicheanalyst.common.CommandExecutor;
import org.ku.nicheanalyst.common.CommonFun;
import org.ku.nicheanalyst.common.Const;
import org.ku.nicheanalyst.maps.controllers.GeoTiffController;
import org.ku.nicheanalyst.maps.objects.GeoTiffObject;
import org.ku.nicheanalyst.maps.objects.ShapeObject;

/**
 * @author Huijie Qiao
 *
 */
public class CutRasterByShape {
	private static void log(String message, boolean newline){
		if (newline){
			System.out.println(message);
		}else{
			System.out.print(message);
		}
	}
	public static void main(String[] args) throws IOException {
		String[] command = new String[11];
		command[0] = "%s";
		command[1] = "-of";
		command[2] = "GTiff";
		command[3] = "-cutline";
		command[4] = "%s";
		command[5] = "-cl";
		command[6] = "%s";
		command[7] = "-cwhere";
		command[8] = "%s=%s";
		command[9] = "%s";
		command[10] = "%s";

		ogr.RegisterAll();
//		gdal.AllRegister();
		ShapeObject shp = new ShapeObject(args[0], args[1], args[2]);
		File s = new File(new File(args[0]) + "/split");
		HashMap<String, HashSet<Feature>> groups = new HashMap<String, HashSet<Feature>>();
		log("init shape layer", true);
		for (String key : shp.getFeatures().keySet()){
			Feature feature = shp.getFeatures().get(key);
			HashSet<Feature> features = groups.get(key);
			if (features==null){
				features = new HashSet<Feature>();
			}
			features.add(feature);
			groups.put(key, features);
		}
		if (!s.exists()){
			CommonFun.mkdirs(s.getAbsolutePath(), true);
			int i = 0;
			for (String key : groups.keySet()){
				i++;
				log(String.format("%s/%s", i, groups.size()), true);
				String filename = s.getAbsolutePath() + "/" + key + ".tiff";
				command[0] = args[5];
				command[4] = args[0];
				command[6] = args[1];
				command[8] = String.format(command[8], args[2], key);
				command[9] = args[3];
				command[10] = filename;
				CommandExecutor.run(command);
			}
		}
		GeoTiffObject sample = new GeoTiffObject(args[3]);
		double[] geoTransform = sample.getDataset().GetGeoTransform();
		String output = args[4];
		int[] values = new int[sample.getXSize() * sample.getYSize()];
		for (int x=0;x<sample.getXSize();x++){
			for (int y=0;y<sample.getYSize();y++){
				values[y * sample.getXSize() + x] = -9999;
			}
		}
		int i=0;
		StringBuilder sb = new StringBuilder();
		for (String key : groups.keySet()){
			i++;
			log(String.format("%s/%s", i, groups.size()), true);
//			String filename = s.getAbsolutePath() + "/" + key + ".tiff";
//			GeoTiffObject geo = new GeoTiffObject(filename);
//			for (int x=0;x<sample.getXSize();x++){
//				for (int y=0;y<sample.getYSize();y++){
//					if (geo.readByXY(x, y)!=geo.getNoData()){
//						values[y * sample.getXSize() + x] = i;
//					}
//				}
//			}
			sb.append(String.format("%d,%s", i, key) + Const.LineBreak);
		}
//		GeoTiffController.createTiff(output, 
//				sample.getXSize(), sample.getYSize(), geoTransform, values, Const.NoData, gdalconst.GDT_Int16);
		CommonFun.writeFile(sb.toString(), output);
	}
}
