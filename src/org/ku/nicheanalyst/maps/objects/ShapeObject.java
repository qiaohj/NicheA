/******************************************************************************
 * Huijie Qiao
 *
 * Project:  NicheA
 * Purpose:  
 * Created date: Sep 13, 2012 3:36:39 PM
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


package org.ku.nicheanalyst.maps.objects;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;

import org.gdal.ogr.DataSource;
import org.gdal.ogr.Driver;
import org.gdal.ogr.Feature;
import org.gdal.ogr.Geometry;
import org.gdal.ogr.Layer;
import org.gdal.ogr.ogr;

/**
 * @author Huijie Qiao
 *
 */
public class ShapeObject {
	private DataSource shapeData;
	private String filename;
	private String key;
	private String layername;
	private Layer layer;
	private HashMap<String, Feature> features;
	public ShapeObject(String filename, String layername, String key) throws FileNotFoundException{
		ogr.RegisterAll();
		this.filename = filename;
		this.key = key;
		this.layername = layername;
		features = null;
		File f = new File(filename);
		if (!f.exists()){
			throw new FileNotFoundException(filename);
		}
		Driver driver = ogr.GetDriverByName("ESRI Shapefile");
		shapeData = driver.Open(filename);
		layer = shapeData.GetLayer(layername);
	}
	
	public HashMap<String, Feature> getFeatures() {
		if (features==null){
			features = new HashMap<String, Feature>();
			for (int i=0;i<layer.GetFeatureCount();i++){
				Feature feature = layer.GetFeature(i);
				features.put(feature.GetFieldAsString(key), feature);
			}
		}
		return features;
	}
	
}
