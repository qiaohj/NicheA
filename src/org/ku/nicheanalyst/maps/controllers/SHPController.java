package org.ku.nicheanalyst.maps.controllers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;

import org.gdal.ogr.DataSource;
import org.gdal.ogr.Driver;
import org.gdal.ogr.Feature;
import org.gdal.ogr.Geometry;
import org.gdal.ogr.Layer;
import org.gdal.ogr.ogr;
import org.gdal.osr.SpatialReference;
import org.gdal.osr.osr;
import org.ku.nicheanalyst.common.CommonFun;
import org.ku.nicheanalyst.maps.objects.GeoTiffObject;

public class SHPController {
	private static Driver driver;
	public SHPController(){
		driver = ogr.GetDriverByName("ESRI Shapefile");
	}
	/**
	 * 从sourceFolder的source_LayerName中取得在range中圈出来的，在polygonList中出现的polygon相交的polygon
	 * @param sourceFolder 待比较的原始图层文件夹
	 * @param source_LayerName 待比较的原始图层名
	 * @param fieldName  sourceFolder中图层用于比较的字段名
	 * @param polygonList  哪些图层参与计算
	 * @param crossFolder 保存的文件夹
	 * @param crossed_layerName 保存的图层名
	 * @param range  区域
	 * @return
	 */
	public static Geometry getCrossedGeometryByRange(String sourceFolder, String source_LayerName, String fieldName, HashSet<String> polygonList,  
			String crossFolder, String crossed_layerName,
			double[][] range){
		
		Geometry geometry = new Geometry(ogr.wkbMultiPoint);
		for (int i=0;i<range.length;i++){
			Geometry point = new Geometry(ogr.wkbPoint);
			point.SetPoint(0, range[i][0], range[i][1]);
			geometry.AddGeometry(point);
		}
		return getCrossedGeometry(sourceFolder, source_LayerName, fieldName, polygonList, crossFolder, crossed_layerName,  geometry);
	}
	/**
	 * 从sourceFolder的source_LayerName中取得在geometry中的，在polygonList中出现的polygon相交的polygon
	 * @param sourceFolder 待比较的原始图层文件夹
	 * @param source_LayerName 待比较的原始图层名
	 * @param fieldName  sourceFolder中图层用于比较的字段名
	 * @param polygonList  哪些图层参与计算
	 * @param crossFolder 保存的文件夹
	 * @param crossed_layerName 保存的图层名
	 * @param geometry  区域
	 * @return
	 */
	public  static Geometry getCrossedGeometry(String sourceFolder, String source_LayerName, String fieldName, HashSet<String> polygonList,  
			String crossFolder, String crossed_layerName,
			Geometry geometry){
		
		DataSource sourceData_crossed = driver.Open(sourceFolder);
		Layer source_layer_crossed = sourceData_crossed.GetLayerByName(source_LayerName);
		source_layer_crossed.ResetReading();
		Feature source_feature_crossed = source_layer_crossed.GetNextFeature();
		Geometry geometrys = new Geometry(ogr.wkbMultiPolygon);
		while (source_feature_crossed!=null){
			if (polygonList.contains(source_feature_crossed.GetFieldAsString(fieldName))){
				
				Geometry polygon = getGeometryCrossed(geometry, source_feature_crossed.GetGeometryRef());
				if (polygon!=null){
					if (polygon.GetGeometryType()==ogr.wkbPolygon){						
						geometrys.AddGeometry(polygon);
					}else if (polygon.GetGeometryType()==ogr.wkbMultiPolygon){
						for (int i=0; i<polygon.GetGeometryCount(); i++){
							geometrys.AddGeometry(polygon.GetGeometryRef(i));
						}
					}
				}
			}
			source_feature_crossed = source_layer_crossed.GetNextFeature();
		}
		
		if (geometrys.GetGeometryCount()>0){
//			saveGeometry(geometrys, crossFolder, crossed_layerName);
		}

		return geometrys;
	}
	/**
	 * 取得交叉的polygon
	 * @param geometry1
	 * @param geometry2
	 * @return
	 */
	private  static Geometry getGeometryCrossed(Geometry geometry1, Geometry geometry2){
		return geometry1.ConvexHull().Intersection(geometry2);
	}
	
	
	/**
	 * 根据一组点坐标求该点坐标的质心
	 * @param points
	 * @return
	 */
	public  static Geometry getCentroidXY(double[][] points){
		return getCentroid(getConvexHull(points));
	}
	/**
	 * 取得质心坐标
	 * @param g
	 * @return
	 */
	public  static Geometry getCentroid(Geometry g){
		if ((g.GetGeometryType() == ogr.wkbPolygon)||(g.GetGeometryType() == ogr.wkbMultiPolygon)){
			Geometry c = g.Centroid();
			return c;
		}
		return null;
	}
	/**
	 * 取得凸包
	 * @param points
	 * @return
	 */
	public  static Geometry getConvexHull(double[][] points){
		Geometry g_points = new Geometry(ogr.wkbMultiPoint);
		for (double[] point : points){
			Geometry g_point = new Geometry(ogr.wkbPoint);
			g_point.SetPoint(0, point[0], point[1]);
			g_points.AddGeometry(g_point);
		}
		return g_points.ConvexHull();
	}
	/**
	 * 计算一个多边形的面积(平方米)
	 * @param g
	 * @return
	 */
	public  static double getGeometryArea(Geometry g){
		double area = 0;
		HashSet<Geometry> gs = new HashSet<Geometry>();
		if (g.GetGeometryType()==ogr.wkbPolygon){
			gs.add(g);
		}
		if (g.GetGeometryType()==ogr.wkbMultiPolygon){
			for (int i=0;i<g.GetGeometryCount();i++){
				area += getGeometryArea(g.GetGeometryRef(i));
			}
		}
		
		for (Geometry sub_g : gs){
//			System.out.println(sub_g.Boundary().GetGeometryType());
			Geometry Boundary = sub_g.GetBoundary();
//			System.out.println(Boundary.GetPointCount());
			if (Boundary.GetPointCount()>2){
				Geometry temp_g = new Geometry(ogr.wkbMultiPoint);
				double[][] ll = new double[Boundary.GetPointCount()][2];
				for (int i=0;i<Boundary.GetPointCount();i++){
					ll[i][0] = Boundary.GetPoint(i)[0];
					ll[i][1] = Boundary.GetPoint(i)[1];
				}
				double[][] xys = CommonFun.getMeters(ll);
				for (double[] xy : xys){
					Geometry p = new Geometry(ogr.wkbPoint);
					p.SetPoint(0, xy[0], xy[1]);
//					System.out.format("%f,%f\n", xy[0], xy[1]);
					temp_g.AddGeometry(p);
				}
//				System.out.println(temp_g.ConvexHull().GetGeometryType());
				area += temp_g.ConvexHull().GetArea()/1000000;
			}
		}
		return area;
	}
	/**
	 * 计算一个图层中所有多边形的面积
	 * @param l
	 * @return
	 */
	public  static double getGeometryArea(Layer l){
		double sumarea = 0;
		for (int i=0;i<l.GetFeatureCount();i++){
			Feature feature = l.GetFeature(i);
			sumarea += getGeometryArea(feature.GetGeometryRef());
		}
		return sumarea;
	}
	/**
	 * 保存图层
	 * @param Geometry
	 * @param fileName
	 * @param layerName
	 */
	public  static void saveGeometry(Geometry geometry, String fileName, String layerName) {
		CommonFun.mkdirs(fileName, false);
		CommonFun.rmfiles(fileName, layerName);
		DataSource shapeData = driver.CreateDataSource(fileName);
		
		Layer layer = shapeData.CreateLayer(layerName, new SpatialReference(osr.SRS_WKT_WGS84), geometry.GetGeometryType(), null);
		Feature feature = new Feature(layer.GetLayerDefn());
		feature.SetGeometry(geometry);
		layer.CreateFeature(feature);
//		shapeData.ReleaseResultSet(layer);
		
	}
	public static void createPoints(double[][] longitudeLatitudeList, String fileName, String layerName) {
		CommonFun.mkdirs(fileName, false);
		CommonFun.rmfiles(fileName, layerName);
//		System.out.println(fileName);
//		gdal.AllRegister();
//		ogr.RegisterAll();
//		for (int i=0;i<ogr.GetDriverCount();i++){
//			System.out.println(ogr.GetDriver(i).getName());
//		}
		driver = ogr.GetDriverByName("ESRI Shapefile");
//		System.out.println(driver);
		DataSource shapeData = driver.CreateDataSource(fileName);
		
		Layer layer = shapeData.CreateLayer(layerName, new SpatialReference(osr.SRS_WKT_WGS84), ogr.wkbPoint, null);
		for (double[] ll : longitudeLatitudeList){
			Geometry geometry = new Geometry(ogr.wkbPoint);
			geometry.SetPoint(0, ll[0], ll[1]);
			Feature feature = new Feature(layer.GetLayerDefn());
			feature.SetGeometry(geometry);
			layer.CreateFeature(feature);	
		}
		
		shapeData.ReleaseResultSet(layer);
//		driver.DeleteDataSource(fileName);
	}
	public void release() {
//		driver.();
	}
	public static void main(String[] args) throws Exception {
//		gdal.AllRegister();
		ogr.RegisterAll();
		
		String filename = args[0];
		String mask = args[1];
		GeoTiffObject map = new GeoTiffObject(mask);
		double[] geo = map.getDataset().GetGeoTransform();
//		System.out.println(filename);
		File file = new File(filename);
		
				
		String layername = file.getName().replace(".txt", "").replace(".data", "");
		String shpname = file.getParent() + "/shp";
//		System.out.println(String.format("LayerName:%s ShpName:%s", layername, shpname));
		

		InputStream is = new FileInputStream(filename);
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        String line = reader.readLine();       // 读取第一行
        ArrayList<double[]> lllist = new ArrayList<double[]>();
        boolean isxy = filename.contains(".data")?true:false;
        while (line != null) {          // 如果 line 为空说明读完了
        	if (!line.contains("\"")){
        		String[] llstr = line.split("\t");
        		if (llstr.length==2){
        			double[] ll;
        			if (isxy){
        				ll = CommonFun.PositionToLL(geo, new int []{Integer.valueOf(llstr[0]), Integer.valueOf(llstr[1])});
        			}else{
        				ll = new double[]{Double.valueOf(llstr[0]), Double.valueOf(llstr[1])};
        			}
        			lllist.add(ll);
        		}
        	}
            line = reader.readLine();   // 读取下一行
            
        }
        reader.close();
        is.close();
        double[][] lll = new double[lllist.size()][2];
        int i=0;
        for (double[] ll : lllist){
        	lll[i] = ll;
//        	System.out.println(i + ":" + ll[0] + ":" + ll[1]);
        	i++;
        }
		createPoints(lll, shpname, layername);
	}
}
