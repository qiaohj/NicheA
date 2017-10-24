package org.ioz.mn.lakeriver;

import java.io.File;
import java.io.FileNotFoundException;

import org.gdal.ogr.DataSource;
import org.gdal.ogr.Driver;
import org.gdal.ogr.Layer;
import org.gdal.ogr.ogr;
import org.junit.Test;
import org.ku.nicheanalyst.maps.objects.GeoTiffObject;
public class TestClass {
	@Test
	public void tif() throws FileNotFoundException{
		GeoTiffObject tif = new GeoTiffObject("E:/ENM_Theory/Samples/v1.tif");
		System.out.println(tif.getXSize());
	}
	
	@Test
	public void shape(){
		DataSource shapeData;
		Layer layer;
		
		ogr.RegisterAll();
		String filename = "E:/Network_Analysis/WaterBody/MN_Wetlands_V1.shp";
		String layername = "MN_Wetlands_V1";
		Driver driver = ogr.GetDriverByName("ESRI Shapefile");
		
		
		
		File a = new File("E:/Network_Analysis/WaterBody/MN_Wetlands_V1_Merged.shp");
		a.delete();
		a = new File("E:/Network_Analysis/WaterBody/MN_Wetlands_V1_Merged.dbf");
		a.delete();
		a = new File("E:/Network_Analysis/WaterBody/MN_Wetlands_V1_Merged.shx");
		a.delete();
		
		
		shapeData = driver.Open(filename);
		layer = shapeData.GetLayer(layername);
		System.out.println("GetFeatureCount " + layer.GetFeatureCount());
	}
}	
