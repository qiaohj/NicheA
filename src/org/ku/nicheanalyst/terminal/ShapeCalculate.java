/******************************************************************************
 * Huijie Qiao
 *
 * Project:  NicheA
 * Purpose:  
 * Created date: Sep 13, 2012 3:26:18 PM
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.gdal.gdal.gdal;
import org.gdal.ogr.Geometry;
import org.gdal.ogr.ogr;
import org.ku.nicheanalyst.common.CommonFun;
import org.ku.nicheanalyst.maps.objects.GeoTiffObject;
import org.ku.nicheanalyst.maps.objects.ShapeObject;

import quickhull3d.QuickHull3D;

/**
 * @author Huijie Qiao
 *
 */
public class ShapeCalculate {
	private static void log(String message, boolean newline){
		if (newline){
			System.out.println(message);
		}else{
			System.out.print(message);
		}
	}
	public static void main(String[] args) throws IOException {
		log("init...", true);
		
		ogr.RegisterAll();
		ArrayList<GeoTiffObject> pcas = new ArrayList<GeoTiffObject>();
		pcas.add(new GeoTiffObject(args[0]));
		pcas.add(new GeoTiffObject(args[1]));
		pcas.add(new GeoTiffObject(args[2]));
		
		ShapeObject shp1 = new ShapeObject(args[3], args[4], args[5]);
		ShapeObject shp2 = null;
		if (args.length>=9){
			shp2 = new ShapeObject(args[6], args[7], args[8]);
		}
		log("cuting rasters by polygon 1 ...", true);
		HashMap<String, ShapeCalculateObject> variables1 = getVariables(pcas, shp1);
		log("calculate volume of Convex hull and MVE in polygeon 1", true);
		for (String key: variables1.keySet()){
			log(key, true);
			ShapeCalculateObject shapeCalculateObject = variables1.get(key);
			log(String.valueOf(shapeCalculateObject.getMve().getVolume(true)), true);
		}
	}
	
	public static HashMap<String, ShapeCalculateObject> getVariables(ArrayList<GeoTiffObject> pcas, ShapeObject shp){
		HashMap<String, ShapeCalculateObject> variables = new HashMap<String, ShapeCalculateObject>();
		double[] geoTransform = pcas.get(0).getDataset().GetGeoTransform();
		for (String key : shp.getFeatures().keySet()){
			log(key, true);
			Geometry geometry = shp.getFeatures().get(key).GetGeometryRef();
			ShapeCalculateObject shapeCalculateObject = variables.get(key);
			HashSet<double[]> values = (shapeCalculateObject==null)?new HashSet<double[]>():shapeCalculateObject.getValues();
			for (int x=0;x<pcas.get(0).getXSize();x++){
				log(String.format("%d/%d", x, pcas.get(0).getXSize()), true);
				for (int y=0;y<pcas.get(0).getYSize();y++){
					int[] xy = new int[]{x, y};
					double[] ll = CommonFun.PositionToLL(geoTransform, xy);
					Geometry point = new Geometry(ogr.wkbPoint);
					point.SetPoint(0, ll[0], ll[1]);
					if (geometry.Contains(point)){
						values.add(new double[]{
								pcas.get(0).readByXY(x, y), pcas.get(1).readByXY(x, y), pcas.get(2).readByXY(x, y)});
					}
					
				}
			}
			shapeCalculateObject.setValues(values);
			variables.put(key, shapeCalculateObject);
		}
		return variables;
	}
}
