package org.ku.nicheanalyst.maps.controllers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.gdal.ogr.DataSource;
import org.gdal.ogr.Driver;
import org.gdal.ogr.Feature;
import org.gdal.ogr.FeatureDefn;
import org.gdal.ogr.Geometry;
import org.gdal.ogr.Layer;
import org.gdal.ogr.ogr;
import org.gdal.osr.SpatialReference;
import org.gdal.osr.osr;
import org.ku.nicheanalyst.common.CommonFun;

public class DrawShpWithCSV {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		String csvfile = args[0];
		File csv = new File(csvfile);
		String shpfolderstr = args[1];
		ogr.RegisterAll();
		if (csv.getName().endsWith(".csv")){
			Driver driver = ogr.GetDriverByName("ESRI Shapefile");
			DataSource shapeData = driver.CreateDataSource(shpfolderstr);
			String layername = csv.getName().replace(".csv", "");
			Layer layer = shapeData.CreateLayer(layername, new SpatialReference(osr.SRS_WKT_WGS84), ogr.wkbPoint, null);
			FeatureDefn layerDefinition = layer.GetLayerDefn();
			ArrayList<String> data = CommonFun.readFromFile(csv.getAbsolutePath());
			for (String str : data){
				String[] strs = str.split(",");
				if (strs.length==2){
					Feature feature = new Feature(layerDefinition);
					Geometry point = new Geometry(ogr.wkbPoint);
					point.SetPoint(0, Double.valueOf(strs[0]), Double.valueOf(strs[1]));
//						feature.SetField("des", ll[2].replace("p", ""));
					feature.SetGeometry(point );
					//feature.SetFID(id);
					layer.CreateFeature(feature);
				}
			}
			shapeData.ReleaseResultSet(layer);
		}
			

	}

}
