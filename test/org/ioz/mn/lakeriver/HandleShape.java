package org.ioz.mn.lakeriver;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.gdal.ogr.DataSource;
import org.gdal.ogr.Driver;
import org.gdal.ogr.Feature;
import org.gdal.ogr.FieldDefn;
import org.gdal.ogr.Geometry;
import org.gdal.ogr.Layer;
import org.gdal.ogr.ogr;
import org.gdal.ogr.ogrConstants;
import org.gdal.osr.SpatialReference;
import org.gdal.osr.osr;
import org.junit.Test;
import org.ku.nicheanalyst.common.CommonFun;
import org.ku.nicheanalyst.common.Const;

public class HandleShape {
	@Test
	public void readInfo(){
		DataSource shapeData;
		Layer layer;
		
		ogr.RegisterAll();
		String filename = "/Users/huijieqiao/Lake_river/GISFiles/MN/MN_Wetlands.shp";
		String layername = "MN_Wetlands";
		Driver driver = ogr.GetDriverByName("ESRI Shapefile");
		shapeData = driver.Open(filename);
		layer = shapeData.GetLayer(layername);
		System.out.println("GetFeatureCount" + layer.GetFeatureCount());
		HashMap<String, Integer> types = new HashMap<String, Integer>();
		
		for (int i=0;i<layer.GetFeatureCount(); i++){
			System.out.println(i + "/" + layer.GetFeatureCount());
			String WETLAND_TY = layer.GetFeature(i).GetFieldAsString("WETLAND_TY"); 
			if (types.containsKey(WETLAND_TY)){
				Integer v = types.get(WETLAND_TY);
				v++;
				types.put(WETLAND_TY, v);
			}else{
				types.put(WETLAND_TY, 1);
			}
		}
		System.out.println("WETLAND_TY,Count");
		for (String WETLAND_TY : types.keySet()){
			System.out.println(WETLAND_TY + "," + types.get(WETLAND_TY));
		}
	}
	
	@Test
	public void merge() throws SQLException, IOException{
		DataSource shapeData;
		Layer layer;
		String layer_name="Blank";
		String base_folder = "E:/Network_Analysis/Emergent Wetland/";
		ogr.RegisterAll();
		String filename = base_folder + layer_name + ".shp";
		Driver driver = ogr.GetDriverByName("ESRI Shapefile");
		
		
		
		File a = new File(base_folder + layer_name + "_Merged.shp");
		a.delete();
		a = new File(base_folder + layer_name + "_Merged.dbf");
		a.delete();
		a = new File(base_folder + layer_name + "_Merged.shx");
		a.delete();
		
		shapeData = driver.Open(filename);
		layer = shapeData.GetLayer(layer_name);
		System.out.println("GetFeatureCount " + layer.GetFeatureCount());
		HashMap<Integer, ShapeObject> geometries = new HashMap<Integer, ShapeObject>();
		StringBuilder sb = new StringBuilder();
		int index = 0;
		sb.append("ID,FID,ATTRIBUTE,WETLAND_TY,ACRES,SHAPE_Leng,SHAPE_Area"+Const.LineBreak);
		for (int i=0;i<layer.GetFeatureCount(); i++){
			System.out.println(i + "/" + layer.GetFeatureCount());
			Feature feature = layer.GetFeature(i);
			Geometry geometry = feature.GetGeometryRef();
			if (geometry.GetGeometryType()==6){
				for (int j=0;j<geometry.GetGeometryCount();j++){
					Geometry subgeometry = geometry.GetGeometryRef(j);
					
					
					
					geometries.put(index, new ShapeObject(index, subgeometry, feature.GetFieldAsString("ATTRIBUTE"), 
							feature.GetFieldAsString("WETLAND_TY"), feature.GetFieldAsDouble("ACRES"),
							feature.GetFieldAsDouble("SHAPE_Leng"), feature.GetFieldAsDouble("SHAPE_Area")));
					sb.append(String.format("%d,%d,%s,%s,%f,%f,%f%n", index, feature.GetFID(), feature.GetFieldAsString("ATTRIBUTE"), 
							feature.GetFieldAsString("WETLAND_TY"), feature.GetFieldAsDouble("ACRES"),
							feature.GetFieldAsDouble("SHAPE_Leng"), feature.GetFieldAsDouble("SHAPE_Area")));
					index ++;
					
				}
			}else{
				geometries.put(index, new ShapeObject(index, geometry, feature.GetFieldAsString("ATTRIBUTE"), 
						feature.GetFieldAsString("WETLAND_TY"), feature.GetFieldAsDouble("ACRES"),
						feature.GetFieldAsDouble("SHAPE_Leng"), feature.GetFieldAsDouble("SHAPE_Area")));
				sb.append(String.format("%d,%d,%s,%s,%f,%f,%f%n", index, feature.GetFID(), 
						feature.GetFieldAsString("ATTRIBUTE"), 
						feature.GetFieldAsString("WETLAND_TY"), feature.GetFieldAsDouble("ACRES"),
						feature.GetFieldAsDouble("SHAPE_Leng"), feature.GetFieldAsDouble("SHAPE_Area")));
				index ++;
//				
			}
		}
		CommonFun.writeFile(sb.toString(), base_folder + layer_name + "_origin.csv");
		System.out.println("geometries count " + geometries.size());
		
		for (Integer key1=0; key1< geometries.size()-1; key1++){
			System.out.println(key1 + "/" + geometries.size());
			ShapeObject obj1 = geometries.get(key1);
			for (Integer key2=key1+1; key2< geometries.size(); key2++){
				if (key2.intValue()==key1.intValue()){
					continue;
				}
				
				ShapeObject obj2 = geometries.get(key2);
				
				if (!obj1.getWetland_type().equals(obj2.getWetland_type())){
					continue;
				}
				if (obj1.getConvex().Intersects(obj2.getConvex())){
					if (obj1.getGeometry().Intersects(obj2.getConvex())&&(obj1.getConvex().Intersects(obj2.getGeometry()))){
						if (obj1.getGeometry().Intersects(obj2.getGeometry())){
							if (obj1.getGeometry().Union(obj2.getGeometry()).GetGeometryType()==3){
								obj1.addLinked(obj2.getIndex(), obj2);
								obj2.addLinked(obj1.getIndex(), obj1);
							}
						}
					}
				}
			}
		}
		
		
		HashMap<Integer, ShapeObject> new_polygons = new HashMap<Integer, ShapeObject>();
		index = 0;
		while (geometries.size()>0){
			System.out.println(geometries.size());
			Map.Entry<Integer,ShapeObject> entry=geometries.entrySet().iterator().next();
			ShapeObject obj = entry.getValue();
			HashMap<Integer, ShapeObject> mergedGroupItem = new HashMap<Integer, ShapeObject>();
			Geometry geometry = null;
			geometry = getLinked(geometry, mergedGroupItem, obj);
			HashSet<String> attribute = new HashSet<String>();
			String wetland_type = "";
			double arces = 0;
			double shape_leng = 0;
			double shape_area = 0;
			
			index ++;
			
			
			for (Integer key : mergedGroupItem.keySet()){
				ShapeObject item = mergedGroupItem.get(key);
				wetland_type = item.getWetland_type();
				arces += item.getArces();
				shape_leng += item.getShape_leng();
				shape_area += item.getShape_area();
				attribute.add(item.getAttribute());
				geometries.remove(key);
			}
			StringBuilder attribute_sb = new StringBuilder();
			for (String aa : attribute){
				attribute_sb.append(aa + "|");
			}
			ShapeObject shapeObject = new ShapeObject(index, geometry, attribute_sb.toString(), 
					wetland_type, arces, shape_leng, shape_area);
			new_polygons.put(index, shapeObject);
		}
		
		
		System.out.println("writing results. Number of geometry is " + new_polygons.size());
		
		Driver new_driver = ogr.GetDriverByName("ESRI Shapefile");
		
		DataSource new_shapeData = new_driver.CreateDataSource(base_folder.replace("Emergent Wetland/", "Merged"));
		
		Layer new_layer = new_shapeData.CreateLayer(layer_name + "_Merged", new SpatialReference(osr.SRS_DN_NAD83), 
				ogrConstants.wkbPolygon, null);
		
		new_layer.CreateField(new FieldDefn("ID", ogrConstants.OFTInteger));
		
		FieldDefn fdef1 = new FieldDefn("ATTRIBUTE", ogrConstants.OFTString);
		fdef1.SetWidth(254);
		new_layer.CreateField(fdef1);
		
		FieldDefn fdef2 = new FieldDefn("WETLAND_TY", ogrConstants.OFTString);
		fdef2.SetWidth(50);
		new_layer.CreateField(fdef2);
		
		new_layer.CreateField(new FieldDefn("ACRES", ogrConstants.OFTReal));
		new_layer.CreateField(new FieldDefn("SHAPE_Area", ogrConstants.OFTReal));
		
		for (Integer key : new_polygons.keySet()){
			ShapeObject obj = new_polygons.get(key);
			Feature new_feature = new Feature(new_layer.GetLayerDefn());
			new_feature.SetFID(obj.getIndex());
			new_feature.SetField("ID", obj.getIndex());
			new_feature.SetField("ATTRIBUTE", obj.getAttribute());
			new_feature.SetField("WETLAND_TY", obj.getWetland_type());
			obj.getGeometry().Area();
			new_feature.SetField("ACRES", obj.getArces());
			new_feature.SetField("SHAPE_Area", obj.getGeometry().Area());
			new_feature.SetGeometry(obj.getGeometry());
			new_layer.CreateFeature(new_feature);	
		}
		
		new_shapeData.delete();
		System.out.println("done!");
	}

	@Test
	public void disjoin_river() throws SQLException, IOException{
		DataSource shapeData;
		Layer layer;
		String layer_name="dnr_rivers_and_streams";
		String base_folder = "E:/Network_Analysis/River/";
		ogr.RegisterAll();
		String filename = base_folder + layer_name + ".shp";
		Driver driver = ogr.GetDriverByName("ESRI Shapefile");
		
		
		
		File a = new File(base_folder + layer_name + "_Merged.shp");
		a.delete();
		a = new File(base_folder + layer_name + "_Merged.dbf");
		a.delete();
		a = new File(base_folder + layer_name + "_Merged.shx");
		a.delete();
		
		shapeData = driver.Open(filename);
		layer = shapeData.GetLayer(layer_name);
		System.out.println("GetFeatureCount " + layer.GetFeatureCount());
		HashMap<Integer, RiverObject> geometries = new HashMap<Integer, RiverObject>();
		int index = 0;
//		HashMap<Integer, Integer> geotype = new HashMap<Integer, Integer>();
//		for (int i=0;i<layer.GetFeatureCount(); i++){
//			System.out.println(i + "/" + layer.GetFeatureCount());
//			Feature feature = layer.GetFeature(i);
//			Geometry geometry = feature.GetGeometryRef();
//			if (geometry!=null){
//				if (geotype.containsKey(geometry.GetGeometryType())){
//					geotype.put(geometry.GetGeometryType(), geotype.get(geometry.GetGeometryType()) + 1);
//				}else{
//					geotype.put(geometry.GetGeometryType(), 1);
//				}
//			}
//		}
//		
//		for (Integer key : geotype.keySet()){
//			System.out.println(key + ":" + geotype.get(key));
//		}
//		System.exit(1);
		for (int i=0;i<layer.GetFeatureCount(); i++){
			System.out.println(i + "/" + layer.GetFeatureCount());
			Feature feature = layer.GetFeature(i);
			Geometry geometry = feature.GetGeometryRef();
			if (geometry==null){
				continue;
			}
			if (geometry.GetGeometryType()==ogr.wkbMultiLineString25D){
				for (int j=0;j<geometry.GetGeometryCount();j++){
					Geometry subgeometry = geometry.GetGeometryRef(j);
					geometries.put(index, new RiverObject(index, subgeometry, feature.GetFieldAsDouble("Shape_Leng")));
					index ++;
					
				}
			}else{
				geometries.put(index, new RiverObject(index, geometry, feature.GetFieldAsDouble("Shape_Leng")));
				index ++;
//				
			}
		}
		System.out.println("geometries count " + geometries.size());
		
//		HashMap<Integer, Integer> geotype = new HashMap<Integer, Integer>();
		for (Integer key1=0; key1< geometries.size()-1; key1++){
			//System.out.println(key1 + "/" + geometries.size());
			RiverObject obj1 = geometries.get(key1);
			for (Integer key2=key1+1; key2< geometries.size(); key2++){
				if (key2.intValue()==key1.intValue()){
					continue;
				}
				
				RiverObject obj2 = geometries.get(key2);
				
				if (obj1.getConvex().Intersects(obj2.getConvex())){
					if (obj1.getGeometry().Intersects(obj2.getConvex())&&(obj1.getConvex().Intersects(obj2.getGeometry()))){
						if (obj1.getGeometry().Intersects(obj2.getGeometry())){
							//System.out.println(obj1.getGeometry().Union(obj2.getGeometry()).GetGeometryType());
							int type = obj1.getGeometry().Union(obj2.getGeometry()).GetGeometryType();
//							if (geotype.containsKey(type)){
//								geotype.put(type, geotype.get(type) + 1);
//							}else{
//								geotype.put(type, 1);
//							}
							if (type!=5){
								System.out.println(type);
							}
							if (type==ogr.wkbLineString){
								obj1.addLinked(obj2.getIndex(), obj2);
								obj2.addLinked(obj1.getIndex(), obj1);
							}
						}
					}
				}
			}
			
		
		}
		
		
		HashMap<Integer, RiverObject> new_polygons = new HashMap<Integer, RiverObject>();
		index = 0;
		while (geometries.size()>0){
			System.out.println(geometries.size());
			Map.Entry<Integer,RiverObject> entry=geometries.entrySet().iterator().next();
			RiverObject obj = entry.getValue();
			HashMap<Integer, RiverObject> mergedGroupItem = new HashMap<Integer, RiverObject>();
			Geometry geometry = null;
			geometry = getLinkedRiver(geometry, mergedGroupItem, obj);
			HashSet<String> attribute = new HashSet<String>();
			double shape_leng = 0;
			
			index ++;
			
			
			for (Integer key : mergedGroupItem.keySet()){
				RiverObject item = mergedGroupItem.get(key);
				shape_leng += item.getShape_Leng();
				geometries.remove(key);
			}
			RiverObject RiverObject = new RiverObject(index, geometry, shape_leng);
			new_polygons.put(index, RiverObject);
		}
		
		
		System.out.println("writing results. Number of geometry is " + new_polygons.size());
		
		Driver new_driver = ogr.GetDriverByName("ESRI Shapefile");
		
		DataSource new_shapeData = new_driver.CreateDataSource(base_folder.replace("River/", "Merged_River"));
		
		Layer new_layer = new_shapeData.CreateLayer(layer_name + "_Merged", new SpatialReference(osr.SRS_DN_NAD83), 
				ogr.wkbLineString, null);
		
		new_layer.CreateField(new FieldDefn("ID", ogrConstants.OFTInteger));
		
		new_layer.CreateField(new FieldDefn("Shape_Leng", ogrConstants.OFTReal));
		
		for (Integer key : new_polygons.keySet()){
			RiverObject obj = new_polygons.get(key);
			Feature new_feature = new Feature(new_layer.GetLayerDefn());
			new_feature.SetFID(obj.getIndex());
			new_feature.SetField("Shape_Leng", obj.getGeometry().Length());
			new_feature.SetGeometry(obj.getGeometry());
			new_layer.CreateFeature(new_feature);	
		}
		
		new_shapeData.delete();
		System.out.println("done!");
	}
	
	private Geometry getLinkedRiver(Geometry geometry, HashMap<Integer, RiverObject> v, RiverObject obj) {
		if (geometry==null){
			geometry = obj.getGeometry();
		}else{
			geometry = geometry.Union(obj.getGeometry());
		}
		v.put(obj.getIndex(), obj);
		for (Integer key : obj.getLinked().keySet()){
			RiverObject linked = obj.getLinked().get(key);
			if (!v.containsKey(key)){
				geometry = getLinkedRiver(geometry, v, linked);
			}
		}
		return geometry;
	}

	private Geometry getLinked(Geometry geometry, HashMap<Integer, ShapeObject> v, ShapeObject obj) {
		if (geometry==null){
			geometry = obj.getGeometry();
		}else{
			geometry = geometry.Union(obj.getGeometry());
		}
		v.put(obj.getIndex(), obj);
		for (Integer key : obj.getLinked().keySet()){
			ShapeObject linked = obj.getLinked().get(key);
			if (!v.containsKey(key)){
				geometry = getLinked(geometry, v, linked);
			}
		}
		return geometry;
	}
}
