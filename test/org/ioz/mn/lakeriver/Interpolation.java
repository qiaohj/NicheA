package org.ioz.mn.lakeriver;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

import org.gdal.ogr.DataSource;
import org.gdal.ogr.Driver;
import org.gdal.ogr.Feature;
import org.gdal.ogr.Geometry;
import org.gdal.ogr.Layer;
import org.gdal.ogr.ogr;
import org.junit.Test;
import org.ku.nicheanalyst.common.CommonFun;
import org.ku.nicheanalyst.common.Const;
import org.ku.nicheanalyst.maps.objects.GeoTiffObject;

public class Interpolation {
	@Test
	public void interpolation() throws IOException{
		ogr.RegisterAll();
		String filename = "F:/ZebraMussel/Watershed/MNwatershed.shp";
		Driver driver = ogr.GetDriverByName("ESRI Shapefile");
		DataSource shapeData;
		Layer layer;
		String layer_name="MNwatershed";
	
		
		shapeData = driver.Open(filename);
		layer = shapeData.GetLayer(layer_name);
		System.out.println("GetFeatureCount " + layer.GetFeatureCount());
		HashMap<Integer, Feature> geometries = new HashMap<Integer, Feature>();
		for (int i=0;i<layer.GetFeatureCount(); i++){
			Feature feature = layer.GetFeature(i);
			int fid = feature.GetFID();
			geometries.put(fid, feature);
		}
		
		filename = "F:/ZebraMussel/Lake/Lake_merged.shp";
		
		layer_name="Lake_merged";
	
		
		shapeData = driver.Open(filename);
		layer = shapeData.GetLayer(layer_name);
		System.out.println("GetFeatureCount " + layer.GetFeatureCount());
		//HashMap<Integer, Feature> lakes = new HashMap<Integer, Feature>();
		StringBuilder sb = new StringBuilder();
		sb.append("Watershold_ID,Lake_ID"+Const.LineBreak);
		for (int i=0;i<layer.GetFeatureCount(); i++){
			System.out.println(i+"/"+layer.GetFeatureCount());
			Feature feature = layer.GetFeature(i);
			int id = feature.GetFieldAsInteger("ID");
			Geometry lake = feature.GetGeometryRef();
			int fid = -1;
			for (Feature fea : geometries.values()){
				Geometry watersheld = fea.GetGeometryRef();
				//if ((watersheld.GetBoundary().Intersect(lake))||(watersheld.GetBoundary().Contains(lake))){
					if ((watersheld.Intersect(lake))||(watersheld.Contains(lake))){
						fid = fea.GetFID();
						break;
					}
				//}
			}
			sb.append(String.format("%d,%d%n", fid, id));
		}
		CommonFun.writeFile(sb.toString(), "F:/ZebraMussel/Watershed/lake_watersheld.csv");
		
		
	}
}
