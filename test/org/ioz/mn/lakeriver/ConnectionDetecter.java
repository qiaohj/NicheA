package org.ioz.mn.lakeriver;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.gdal.ogr.DataSource;
import org.gdal.ogr.Driver;
import org.gdal.ogr.Feature;
import org.gdal.ogr.FieldDefn;
import org.gdal.ogr.Geometry;
import org.gdal.ogr.Layer;
import org.gdal.ogr.ogr;
import org.gdal.ogr.ogrConstants;
import org.gdal.osr.CoordinateTransformation;
import org.gdal.osr.SpatialReference;
import org.gdal.osr.osr;
import org.junit.Test;
import org.ku.nicheanalyst.common.CommonFun;
import org.ku.nicheanalyst.common.Const;
import org.ku.nicheanalyst.maps.objects.GeoTiffObject;



public class ConnectionDetecter {
	@Test
	public void extractByID() throws SQLException, IOException{
		java.sql.Connection con= (java.sql.Connection) DriverManager.getConnection("jdbc:mysql://127.0.0.1:3307/MN_River_Lake?useUnicode=true&amp;characterEncoding=utf-8","root","mikania");
//		
//		
		PreparedStatement stmt = con.prepareStatement("Select * from connections where ID_1=?");
		for (int i=1; i<=12980; i++){
			System.out.println(i + "/12980");
			StringBuilder sb = new StringBuilder();
			sb.append("ID_1,ID_2,ID_1_Type,ID_2_Type,distance" + Const.LineBreak);
			stmt.setInt(1, i);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()){
				sb.append(String.format("%d,%d,%d,%d,%f%n", 
						rs.getInt("ID_1"),rs.getInt("ID_2"),
						rs.getInt("ID_1_Type"),rs.getInt("ID_2_Type"),
						rs.getDouble("distance")));
			}
			CommonFun.writeFile(sb.toString(), "/Volumes/HD_Qiao/Experiments/Lake_river/connections_id/"+i+".csv");
		}
	}
	@Test
	public void insertIntoDB() throws IOException, SQLException{
		java.sql.Connection con= (java.sql.Connection) DriverManager.getConnection("jdbc:mysql://127.0.0.1:3307/MN_River_Lake?useUnicode=true&amp;characterEncoding=utf-8","root","mikania");
//		
//		
		PreparedStatement stmt_insert = con.prepareStatement("INSERT INTO connections "
				+ "(ID_1, ID_2, ID_1_Type, ID_2_Type, distance) VALUES (?,?,?,?,?)");
		File f = new File("/Volumes/HD_Qiao/Experiments/connections/subconnections");
		for (File fitem : f.listFiles()){
			if (fitem.getName().endsWith("csv")){
				System.out.println(fitem.getName());
				ArrayList<String> connections_str = CommonFun.readFromFile(fitem.getAbsolutePath());
				for (String connection_str : connections_str){
					String[] con_str = connection_str.split(",");
					if (con_str.length==5){
						if (CommonFun.isInteger(con_str[0])){
							Connection connection = new Connection(Integer.valueOf(con_str[0]), 
									Integer.valueOf(con_str[1]), Integer.valueOf(con_str[2]), 
									Integer.valueOf(con_str[3]), Double.valueOf(con_str[4]));
							
							stmt_insert.setInt(1, connection.getId1());
							stmt_insert.setInt(2, connection.getId2());
							stmt_insert.setInt(3, connection.getType1());
							stmt_insert.setInt(4, connection.getType2());
							stmt_insert.setDouble(5, connection.getDistance());
							stmt_insert.execute();
						}
					}
				}
			}
		}
	}
	@Test
	public void addMoreConnections() throws IOException{
		ArrayList<String> connections_str = CommonFun.readFromFile("D:/Lake_river/GISFiles/LakeConnections/5131/500m/connection_5131.csv");
		ArrayList<Connection> connections = new ArrayList<Connection>();
		for (String connection_str : connections_str){
			String[] con_str = connection_str.split(",");
			if (con_str.length==6){
				if (CommonFun.isInteger(con_str[0])){
					Connection connection = new Connection(Integer.valueOf(con_str[1]), 
							Integer.valueOf(con_str[2]), Integer.valueOf(con_str[3]), 
							Integer.valueOf(con_str[4]), Double.valueOf(con_str[5]));
					connections.add(connection);
				}
			}
		}
		
		ogr.RegisterAll();
		String filename = "D:/Lake_river/GISFiles/LakeConnections/5131/500m";
		String Lake_name = "Lake_5131";
		Driver driver = ogr.GetDriverByName("ESRI Shapefile");
		
		
		DataSource shapeData;
		Layer layer_lake;
		shapeData = driver.Open(filename, 1);
		
		layer_lake = shapeData.GetLayer(Lake_name);	
		//layer_lake.CreateField(new FieldDefn("l_long", ogrConstants.OFTReal));
		//layer_lake.CreateField(new FieldDefn("l_lat", ogrConstants.OFTReal));
		HashMap<Integer, Feature> all_f = new HashMap<Integer, Feature>();
		for (int i=0;i<layer_lake.GetFeatureCount();i++){
			Feature feature = layer_lake.GetFeature(i);
			all_f.put(feature.GetFieldAsInteger("ID"), feature);
		}
		for (int i=0;i<layer_lake.GetFeatureCount();i++){
			Feature feature = layer_lake.GetFeature(i);
			int id = feature.GetFieldAsInteger("ID");
			for (Connection connection : connections){
				if (connection.getId2()==id){
					feature.SetField("l_long", all_f.get(connection.getId1()).GetFieldAsDouble("long"));
					feature.SetField("l_lat", all_f.get(connection.getId1()).GetFieldAsDouble("lat"));
				}
			}
			layer_lake.SetFeature(feature);
		}
		layer_lake.delete();
	}
	@Test
	public void test() throws FileNotFoundException{
		GeoTiffObject geo = new GeoTiffObject("C:/Users/Huijie Qiao/temp/1_G_median.asc");
		GeoTiffObject bio1 = new GeoTiffObject("C:/Users/Huijie Qiao/temp/bio12.asc");
		double threshold = 0.00000875895;
		int i=0;
		
		for (int x=0;x<geo.getXSize();x++){
			for (int y=0;y<geo.getYSize();y++){
				double v=geo.readByXY(x, y);
				if ((v-threshold+0.000000001111)>0){
					i++;
				}
			}
		}
		double[] initialDoubleArray = new double[i];
		i=0;
		
		for (int x=0;x<geo.getXSize();x++){
			for (int y=0;y<geo.getYSize();y++){
				double v=geo.readByXY(x, y);
				if ((v-threshold+0.000000001111)>0){
					double vv = bio1.readByXY(x, y);
					if (CommonFun.equal(vv, bio1.getNoData(), 1000)){
						System.out.println(vv);
					}
					initialDoubleArray[i] = vv;
					i++;
				}
			}
		}
		DescriptiveStatistics stat = new DescriptiveStatistics(initialDoubleArray);
		System.out.println("min:" + stat.getMin());
		System.out.println("max:" + stat.getMax());
		System.out.println("mean:" + stat.getMean());
		System.out.println("1st:" + stat.getPercentile(25));
		System.out.println("Median:" + stat.getPercentile(50));
		System.out.println("3rd:" + stat.getPercentile(75));
		System.out.println(i);
	}
	@Test
	public void mergeConnections() throws IOException{
		StringBuilder sb = new StringBuilder();
		sb.append("ID_1,ID_2,ID_1_Type,ID_2_Type,distance" + Const.LineBreak);
		File folder = new File("E:/Network_Analysis/connections");
		int i = 0;
		for (File f : folder.listFiles()){
			System.out.println(i++);
			if (f.isFile()&&f.getName().endsWith(".csv")){
				ArrayList<String> content = CommonFun.readFromFile(f.getAbsolutePath());
				for (String c : content){
					if (!c.startsWith("ID_1")){
						String[] items = c.split(",");
						if (items.length==5){
							double distance = Double.valueOf(items[4]).doubleValue();
							if (distance<=5000){
								sb.append(c + Const.LineBreak);
							}
						}
						
					}
				}
			}
			
		}
		CommonFun.writeFile(sb.toString(), "E:/Network_Analysis/connections.csv");
	}
	@Test
	public void updateElevationtoDB() throws SQLException{
		int min_distance = 50;
		DataSource shapeData;
		Layer layer_lake;
		Layer layer_river;
		
		ogr.RegisterAll();
		String filename = "/Users/huijieqiao/River_Lake/GISFiles/merged";
		String Lake_name = "Lake_merged";
		String River_name = "Riverine_merged";
		Driver driver = ogr.GetDriverByName("ESRI Shapefile");
		
		java.sql.Connection con= (java.sql.Connection) DriverManager.getConnection("jdbc:mysql://127.0.0.1:3307/MN_River_Lake?useUnicode=true&amp;characterEncoding=utf-8","root","mikania");
//		
//		
		PreparedStatement stmt = con.prepareStatement("Insert into Polygons (FID, ID, ATTRIBUTE, Type, Elevation, SHAPE_Area, ACRES) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?)");
		
		shapeData = driver.Open(filename, 1);
		
		layer_lake = shapeData.GetLayer(Lake_name);
		SpatialReference outSpatialRef = new SpatialReference();
		outSpatialRef.ImportFromEPSG(4326);
				
		CoordinateTransformation trans = null;//osr。CreateCoordinateTransformation(layer_lake.GetSpatialRef(), outSpatialRef);
		
		layer_river = shapeData.GetLayer(River_name);
		System.out.println("Lake:" + layer_lake.GetFeatureCount() + " and River:" + layer_river.GetFeatureCount());
		
		for (int i=0;i<layer_lake.GetFeatureCount();i++){
			Feature feature = layer_lake.GetFeature(i);
			System.out.println("FID is " + feature.GetFID());
			Geometry geo = feature.GetGeometryRef();
			double Elevation = feature.GetFieldAsDouble("Elevation");
			int id = feature.GetFieldAsInteger("ID");
			double SHAPE_Area = feature.GetFieldAsDouble("SHAPE_Area");
			double ACRES = feature.GetFieldAsDouble("ACRES");
			int fid = feature.GetFID();
			int type = feature.GetFieldAsString("WETLAND_TY").equals("Lake")?1:0;
			String ATTRIBUTE = feature.GetFieldAsString("ATTRIBUTE");
			stmt.setInt(1, fid);
			stmt.setInt(2, id);
			stmt.setString(3, ATTRIBUTE);
			stmt.setInt(4, type);
			stmt.setDouble(5, Elevation);
			stmt.setDouble(6, SHAPE_Area);
			stmt.setDouble(7, ACRES);
			stmt.execute();
			
		}
		
		for (int i=0;i<layer_river.GetFeatureCount();i++){
			Feature feature = layer_river.GetFeature(i);
			System.out.println("FID is " + feature.GetFID());
			Geometry geo = feature.GetGeometryRef();
			int id = feature.GetFieldAsInteger("ID");
			double SHAPE_Area = feature.GetFieldAsDouble("SHAPE_Area");
			double ACRES = feature.GetFieldAsDouble("ACRES");
			int fid = feature.GetFID();
			int type = feature.GetFieldAsString("WETLAND_TY").equals("Lake")?1:0;
			String ATTRIBUTE = feature.GetFieldAsString("ATTRIBUTE");
			stmt.setInt(1, fid);
			stmt.setInt(2, id);
			stmt.setString(3, ATTRIBUTE);
			stmt.setInt(4, type);
			stmt.setDouble(5, 0);
			stmt.setDouble(6, SHAPE_Area);
			stmt.setDouble(7, ACRES);
			stmt.execute();
			
		}
		layer_lake.delete();
		layer_river.delete();
		shapeData.delete();
	}
	@Test
	public void getLakeElevation() throws FileNotFoundException{
		int min_distance = 50;
		DataSource shapeData;
		Layer layer_lake;
		Layer layer_river;
		
		ogr.RegisterAll();
		String filename = "/Users/huijieqiao/Lake_river/GISFiles/merged";
		String Lake_name = "Lake_merged";
		String River_name = "Riverine_merged";
		Driver driver = ogr.GetDriverByName("ESRI Shapefile");
		
		
		
		shapeData = driver.Open(filename, 1);
		
		layer_lake = shapeData.GetLayer(Lake_name);
		SpatialReference outSpatialRef = new SpatialReference();
		outSpatialRef.ImportFromEPSG(4326);
				
		CoordinateTransformation trans = null;//osr。CreateCoordinateTransformation(layer_lake.GetSpatialRef(), outSpatialRef);
		
		layer_river = shapeData.GetLayer(River_name);
		System.out.println("Lake:" + layer_lake.GetFeatureCount() + " and River:" + layer_river.GetFeatureCount());
		
		GeoTiffObject raster_lake = new GeoTiffObject(filename + "/merged.tif");
		//layer_lake.CreateField(new FieldDefn("Elevation", ogrConstants.OFTReal));
		for (int i=0;i<layer_lake.GetFeatureCount();i++){
			Feature feature = layer_lake.GetFeature(i);
			System.out.println("FID is " + feature.GetFID());
			Geometry geo = feature.GetGeometryRef();
			
			System.out.println("Points number: " + geo.GetGeometryRef(0).GetPointCount());
			double min_x = Double.MAX_VALUE;
			double min_y = Double.MAX_VALUE;
			double max_x = Double.MIN_VALUE;
			double max_y = Double.MIN_VALUE;
			DescriptiveStatistics stat = new DescriptiveStatistics();
			
			for (int j=0;j<geo.GetGeometryRef(0).GetPointCount();j++){
				double[] points = geo.GetGeometryRef(0).GetPoint(j);
				Geometry point = new Geometry(ogr.wkbPoint);
				point.AddPoint(points[0], points[1]);
				//System.out.println(CommonFun.printArray(points));
				point.Transform(trans);
				double v = raster_lake.readByLL(point.GetX(), point.GetY());
				//System.out.println(v);
				if (CommonFun.equal(v, raster_lake.getNoData(), 1000)){
					
				}else{
					stat.addValue(v);	
				}
			}
			System.out.println(String.format("Max:%f, Min:%f, Mean:%f, Medium:%f", 
					stat.getMax(), stat.getMin(), stat.getMean(), stat.getPercentile(0.5)));
			double medium = stat.getPercentile(0.5);
			feature.SetField("Elevation", medium);
			layer_lake.SetFeature(feature);
			
		}
		layer_lake.delete();
		shapeData.delete();
	}
	@Test
	public void getConnections() throws IOException, SQLException{
		
		
		int min_distance = 1000;
		DataSource shapeData;
		Layer layer_lake;
		Layer layer_river;
		
		ogr.RegisterAll();
		String filename = "E:/Network_Analysis/Merged";
		String Lake_name = "Lake_merged";
		String River_name = "DNR_Rivers_Merged";
		Driver driver = ogr.GetDriverByName("ESRI Shapefile");
		
		
		
		shapeData = driver.Open(filename);
		
		layer_lake = shapeData.GetLayer(Lake_name);	
		layer_river = shapeData.GetLayer(River_name);
		ArrayList<Feature> all_g = new ArrayList<Feature>();
		for (int i=0;i<layer_lake.GetFeatureCount();i++){
			all_g.add(layer_lake.GetFeature(i));
		}
		for (int j=0;j<layer_river.GetFeatureCount();j++){
			//all_g.add(layer_river.GetFeature(j));
		}
		
		//System.exit(1);
		
		//int f=0; int t=200;
		//int f=200; int t=500;
		//int f=500; int t=700;
		//int f=700; int t=900;
		//int f=900; int t=1300;
		//int f=1300; int t=1900;
		//int f=1900; int t=2300;
		//int f=2300; int t=2900;
		//int f=2900; int t=3800;
		//int f=3800; int t=4800;
		//int f=4800; int t=5800;
		//int f=5800; int t=6800;
		//int f=6800; int t=9000;
		//int f=9000; int t=10000;
		//int f=10000; int t=all_g.size()-1;
		
		
		//int all = (12980-f-1 + (12980 - (t-f)))*(t-f)/2;
		for (int i=0;i<all_g.size()-1;i++){
			File f = new File("E:/Network_Analysis/lake_distance/connection_" + i + ".csv");
			if (f.exists()){
				System.out.println("Skip");
				continue;
			}
			
			StringBuilder sb = new StringBuilder();
			sb.append("ID_1,ID_2,ID_1_Type,ID_2_Type,distance" + Const.LineBreak);
			CommonFun.writeFile(sb.toString(), f.getAbsolutePath());
			Feature f1 = all_g.get(i);
			for (int j=i+1;j<all_g.size();j++){
				System.out.println(i + "/" + j + "/" + all_g.size() + " " + ((double)i*100d/(double)all_g.size()) + "%");
				Feature f2 = all_g.get(j);
				if ((f1.GetFieldAsInteger("ID")<100000)&&((f2.GetFieldAsInteger("ID")<100000))){
					//continue;
				}
				boolean is_exist = false;
				//File filecheck = new File(String.format("E:/Network_Analysis/connections/subconnections/%s_%s.csv", f1.GetFieldAsInteger("ID"), f2.GetFieldAsInteger("ID")));
				//is_exist = filecheck.exists();
				is_exist = false;
//				Connection con= (Connection) DriverManager.getConnection("jdbc:mysql://127.0.0.1:3307/MN_River_Lake?useUnicode=true&amp;characterEncoding=utf-8","root","mikania");
//				
//				
//				PreparedStatement stmt_select = con.prepareStatement("SELECT * FROM connections WHERE ID_1=? AND ID_2=?");
//				stmt_select.setInt(1, f1.GetFieldAsInteger("ID"));
//				stmt_select.setInt(2, f2.GetFieldAsInteger("ID"));
//				ResultSet rs = stmt_select.executeQuery();
//				while (rs.next()){
//					is_exist = true;
//				}
//				rs.close();
//				stmt_select.close();
				
				if (!is_exist){
					//CommonFun.writeFile("", filecheck.getAbsolutePath());
					double distance = f1.GetGeometryRef().Distance(f2.GetGeometryRef());
					//if (distance<min_distance){
//						PreparedStatement stmt_insert = con.prepareStatement("INSERT INTO connections "
//								+ "(ID_1, ID_2, ID_1_Type, ID_2_Type, distance) VALUES (?,?,?,?,?)");
//						stmt_insert.setInt(1, f1.GetFieldAsInteger("ID"));
//						stmt_insert.setInt(2, f2.GetFieldAsInteger("ID"));
//						stmt_insert.setInt(3, (f1.GetFieldAsInteger("ID")>=3106)?1:0);
//						stmt_insert.setInt(4, (f2.GetFieldAsInteger("ID")>=3106)?1:0);
//						stmt_insert.setDouble(5, distance);
//						stmt_insert.execute();
//						
//						stmt_insert.setInt(1, f2.GetFieldAsInteger("ID"));
//						stmt_insert.setInt(2, f1.GetFieldAsInteger("ID"));
//						stmt_insert.setInt(3, (f2.GetFieldAsInteger("ID")>=3106)?1:0);
//						stmt_insert.setInt(4, (f1.GetFieldAsInteger("ID")>=3106)?1:0);
//						stmt_insert.setDouble(5, distance);
//						stmt_insert.execute();
//						stmt_insert.close();
						sb.append(String.format("%d,%d,%d,%d,%f%n", 
								f1.GetFieldAsInteger("ID"), f2.GetFieldAsInteger("ID"),
								(f1.GetFieldAsInteger("ID")>=100000)?0:1, (f2.GetFieldAsInteger("ID")>=100000)?0:1, 
								distance));
						sb.append(String.format("%d,%d,%d,%d,%f%n", 
								f2.GetFieldAsInteger("ID"), f1.GetFieldAsInteger("ID"),
								(f2.GetFieldAsInteger("ID")>=100000)?0:1, (f1.GetFieldAsInteger("ID")>=100000)?0:1, 
								distance));
						//CommonFun.writeFile(String.format("%d,%d,%d,%d,%f%n", 
						//		f1.GetFieldAsInteger("ID"), f2.GetFieldAsInteger("ID"),
						//		(f1.GetFieldAsInteger("ID")>=100000)?0:1, (f2.GetFieldAsInteger("ID")>=100000)?0:1, 
						//		distance), filecheck.getAbsolutePath());
						//CommonFun.writeFile(sb.toString(), f.getAbsolutePath());
					//}
				}else{
					System.out.println("skip");
				}
				
//				con.close();\dataset_training
			}
			CommonFun.writeFile(sb.toString(), f.getAbsolutePath());
		}
	}
	@Test
	public void splitConnection() throws IOException{
		ArrayList<String> connections = CommonFun.readFromFile("D:/Lake_river/GISFiles/connections/connection_10612.csv");
		for (String connection : connections){
			String[] split = connection.split(",");
			CommonFun.writeFile(connection, String.format("D:/Lake_river/GISFiles/connections/subconnections/%s_%s.csv", split[0], split[1]));
		}
	}
	@Test
	public void createConnectMap() throws IOException{
		ArrayList<String> connections_str = CommonFun.readFromFile("D:/Lake_river/GISFiles/connections.csv");
		ArrayList<Connection> connections = new ArrayList<Connection>();
		for (String connection_str : connections_str){
			String[] con_str = connection_str.split(",");
			if (con_str.length==5){
				if (CommonFun.isInteger(con_str[0])){
					Connection connection = new Connection(Integer.valueOf(con_str[0]), 
							Integer.valueOf(con_str[1]), Integer.valueOf(con_str[2]), 
							Integer.valueOf(con_str[3]), Double.valueOf(con_str[4]));
					connections.add(connection);
				}
			}
		}
		int maxDistance = 30;
		int target = 5131;
		ogr.RegisterAll();
		String filename = "D:/Lake_river/GISFiles/merged";
		String Lake_name = "Lake_merged";
		String River_name = "Riverine_merged";
		Driver driver = ogr.GetDriverByName("ESRI Shapefile");
		
		
		DataSource shapeData;
		Layer layer_lake;
		Layer layer_river;
		shapeData = driver.Open(filename);
		
		layer_lake = shapeData.GetLayer(Lake_name);	
		layer_river = shapeData.GetLayer(River_name);
		HashMap<Integer, Feature> lakes = new HashMap<Integer, Feature>();
		HashMap<Integer, Feature> rivers = new HashMap<Integer, Feature>();
		for (int i=0;i<layer_lake.GetFeatureCount();i++){
			lakes.put(layer_lake.GetFeature(i).GetFieldAsInteger("ID"), layer_lake.GetFeature(i));
		}
		for (int j=0;j<layer_river.GetFeatureCount();j++){
			rivers.put(layer_river.GetFeature(j).GetFieldAsInteger("ID"), layer_river.GetFeature(j));
		}
		//寻找连接的湖
		HashMap<Integer, HashSet<Feature>> all_f = new HashMap<Integer, HashSet<Feature>>();
		HashSet<Feature> f = new HashSet<Feature>();
		f.add(lakes.get(target));
		all_f.put(0, f);
		HashSet<Integer> handled = new HashSet<Integer>();
		handled.add(target);
		int level = 0;
		int feature_size = 0;
		HashMap<Integer, HashSet<Connection>> target_connections = new HashMap<Integer, HashSet<Connection>>();
		while (feature_size!=handled.size()){
			feature_size = handled.size();
			//寻找当前level的所有feature的连接对象
			for (Feature feature : all_f.get(level)){
				int id1 = feature.GetFieldAsInteger("ID");
				for (Connection connection : connections){
					if ((connection.getId1()==id1)&&(!handled.contains(connection.getId2()))&&(connection.getDistance()<=maxDistance)){
						HashSet<Feature> ff = all_f.get(level + 1);
						if (ff==null){
							ff = new HashSet<Feature>();
						}
						ff.add(lakes.containsKey(connection.getId2())?lakes.get(connection.getId2()):rivers.get(connection.getId2()));
						all_f.put(level + 1, ff);
						HashSet<Connection> c = target_connections.get(level + 1);
						if (c==null){
							c = new HashSet<Connection>();
						}
						c.add(connection);
						target_connections.put(level + 1, c);
						handled.add(connection.getId2());
					}
				}
			}
			
			level++;
			System.out.println(level + ":" + handled.size());
		}
		
		
		
		
		Driver new_driver = ogr.GetDriverByName("ESRI Shapefile");
		String folder = "D:/Lake_river/GISFiles/LakeConnections/" + target + "/" + maxDistance + "m";
		CommonFun.mkdirs(folder, true);
		DataSource new_shapeData = new_driver.CreateDataSource(folder);
		
		Layer new_layer = new_shapeData.CreateLayer("Lake_" + target, new SpatialReference(osr.SRS_DN_NAD83), 
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
		new_layer.CreateField(new FieldDefn("Level", ogrConstants.OFTInteger));
		new_layer.CreateField(new FieldDefn("Elevation", ogrConstants.OFTReal));
		
		int i = 0;
		for (Integer key : all_f.keySet()){
			for (Feature feature : all_f.get(key)){
				System.out.println(feature.GetFieldAsInteger("ID"));
				Feature new_feature = new Feature(new_layer.GetLayerDefn());
				new_feature.SetFID(++i);
				new_feature.SetField("ID", feature.GetFieldAsInteger("ID"));
				new_feature.SetField("ATTRIBUTE", feature.GetFieldAsString("ATTRIBUTE"));
				new_feature.SetField("WETLAND_TY", feature.GetFieldAsString("WETLAND_TY"));
				
				new_feature.SetField("ACRES", feature.GetFieldAsDouble("ACRES"));
				new_feature.SetField("SHAPE_Area", feature.GetFieldAsDouble("SHAPE_Area"));
				if (feature.GetFieldAsString("WETLAND_TY").equals("Lake")){
					new_feature.SetField("Elevation", feature.GetFieldAsDouble("Elevation"));
				}
				new_feature.SetField("Level", key);
				
				new_feature.SetGeometry(feature.GetGeometryRef());
				new_layer.CreateFeature(new_feature);	
			}
		}
		StringBuilder sb = new StringBuilder();
		sb.append("level,ID_1,ID_2,ID_1_Type,ID_2_Type,distance" + Const.LineBreak);
		for (Integer key : target_connections.keySet()){
			for (Connection connection : target_connections.get(key)){
				sb.append(String.format("%d,%d,%d,%d,%d,%f%n", key, connection.getId1(), connection.getId2(), connection.getType1(), connection.getType2(), connection.getDistance()));
			}
		}
		CommonFun.writeFile(sb.toString(), folder + "/connection_" + target + ".csv");
		new_shapeData.delete();
		System.out.println("done!");
	}
}
