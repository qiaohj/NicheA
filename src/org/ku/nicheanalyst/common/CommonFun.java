/******************************************************************************
 * Huijie Qiao
 *
 * Project:  Niche Analyst
 * Purpose:  
 * Created date: Apr 5, 2012 12:57:04 PM
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


package org.ku.nicheanalyst.common;

import java.awt.Point;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

import javax.media.jai.InterpolationBilinear;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.ku.nicheanalyst.algorithms.MinimumVolumeEllipsoid;
import org.ku.nicheanalyst.algorithms.MinimumVolumeEllipsoidResult;
import org.ku.nicheanalyst.dataset.SpeciesData;
import org.ku.nicheanalyst.maps.objects.GeoTiffObject;
import org.proj4.Proj4;
import org.proj4.ProjectionData;

import quickhull3d.Point3d;
import quickhull3d.QuickHull2D;
import quickhull3d.QuickHull3D;
import rjava.rcaller.RCaller;
import Jama.EigenvalueDecomposition;
import Jama.Matrix;

/**
 * @author Huijie Qiao
 *
 */

public class CommonFun {
	public static boolean between(int a, int min, int max){
		if ((a>=min)&&(a<=max)){
			return true;
		}
		return false;
	}
	public static boolean between(double a, double min, double max){
		if ((a>=min)&&(a<=max)){
			return true;
		}
		return false;
	}


	public static Matrix power(Matrix M, double p) {
	    EigenvalueDecomposition evd = M.eig();
	    Matrix D = evd.getD();
	    for (int i = 0; i < D.getColumnDimension(); i++) {
	        D.set(i, i, Math.pow(D.get(i, i), p));
	    }

	    Matrix V = evd.getV();

	    return V.times(D.times(V.transpose()));
	}
	public static double standardized(double[] maxmin_orginal, double[] maxmin_new, double value){
		if (CommonFun.equal(maxmin_orginal[1], maxmin_orginal[0], 1000)){
			return 0;
		}
		return ((value - maxmin_orginal[0])/(maxmin_orginal[1] - maxmin_orginal[0])) * (maxmin_new[1] - maxmin_new[0]) + maxmin_new[0];
	}
	public static double distanceEllipsoid(Matrix A, Matrix C, double[] v){
		Matrix xM = new Matrix(v, v.length);
//		CommonFun.printMatrix(xM);
//		System.out.println("");
		Matrix tempMatrix = xM.minus(C);
		Matrix distanceMatrix = tempMatrix.transpose();
		distanceMatrix = distanceMatrix.times(A);
		distanceMatrix = distanceMatrix.times(tempMatrix);
		double distance = distanceMatrix.getArray()[0][0];
		return distance;
	}
	public static boolean isInEllipsoid(Matrix A, Matrix C, double[] v){
		double distance = distanceEllipsoid(A, C, v);
		if (distance<=1){
			return true;
		}else{
			return false;
		}
	}
	public static boolean isInEllipsoid(Matrix A, Matrix C, double x, double y, double z){
		double[] tt = new double[3];
		tt[0] = x;
		tt[1] = y;
		tt[2] = z;
		return isInEllipsoid(A, C, tt);
	}
	public static quickhull3d.Point3d[] getVertices(HashSet<double[]> values){
		quickhull3d.Point3d[] points = new quickhull3d.Point3d[values.size()];
		int ii = 0;
		for (double[] value : values){
			points[ii++] = new quickhull3d.Point3d(value[0], value[1], value[2]);
		}
		QuickHull3D hull = new QuickHull3D();
		hull.build(points);
		quickhull3d.Point3d[] vertices = hull.getVertices();
		return vertices;
	}
	public static boolean inConvexHull(Point3d point, Point3d[] vertices){
		if (isin(point, vertices)){
			return true;
		}
		quickhull3d.Point3d[] points = new quickhull3d.Point3d[vertices.length + 1];
		int i = 0;
		for (Point3d p : vertices){
			points[i++] = p;
		}
		points[i] = point;
		QuickHull3D hull = new QuickHull3D();
		hull.build(points);
		quickhull3d.Point3d[] rvertices = hull.getVertices();
		return !isin(point, rvertices);
	}
	private static double distance(Point3d p, Point3d point) {
		return Math.sqrt(Math.pow(p.x - point.x, 2) + Math.pow(p.y - point.y, 2) + Math.pow(p.z - point.z, 2));
	}
	/**
	 * @param point
	 * @param vertices
	 * @return
	 */
	private static boolean isin(Point3d point, Point3d[] vertices) {
		for (Point3d p : vertices){
			if (distance(p, point)<0.0001){
				return true;
			}
		}
		return false;
	}
	public static String getFileNameWithoutPathAndExtension(String filename){
		File f = new File(filename);
		return FilenameUtils.removeExtension(f.getName());
	}
	public static String getFileNameWithoutExtension(String filename){
		return FilenameUtils.removeExtension(filename);
	}
	public static MinimumVolumeEllipsoidResult getMVE(HashMap<String, SpeciesData> values, int dimension){
		quickhull3d.Point3d[] points = new quickhull3d.Point3d[values.size()];
		int ii = 0;
		for (SpeciesData value : values.values()){
			points[ii++] = new quickhull3d.Point3d(value.getValues()[0], value.getValues()[1], value.getValues()[2]);
		}
		quickhull3d.Point3d[] vertices = null;
		if (dimension==3){
			QuickHull3D hull = new QuickHull3D();
			try{
				hull.build(points);
			}catch(Exception e){
				System.out.println(points.length);
			}
			vertices = hull.getVertices();
		}else{
			QuickHull2D hull = new QuickHull2D();
			vertices = hull.execute(points);
		}
		double[][] hullvalues = new double[3][vertices.length];
		for (int i=0;i<vertices.length;i++){
			hullvalues[0][i] = vertices[i].x;
			hullvalues[1][i] = vertices[i].y;
			hullvalues[2][i] = vertices[i].z;
		}
		MinimumVolumeEllipsoidResult mve = MinimumVolumeEllipsoid.getMatrix(hullvalues, dimension);
//		mve.print();
		return mve;
	}
	public static HashSet<String> readFromFile2HashSet(String filename) throws IOException{
		File file = new File(filename);
		if (file.exists()){
			HashSet<String> value = new HashSet<String>();
			InputStream is = new FileInputStream(filename);
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
	        String line = reader.readLine();
	        while (line != null) {
	        	value.add(line);
	            line = reader.readLine();
	        }
	        reader.close();
	        is.close();
	        return value;
		}else{
			return new HashSet<String>();
		}
	}
	public static boolean isLinux(){
		return System.getProperty("os.name").contains("Linux");
	}
	public static boolean isWindows(){
		return System.getProperty("os.name").contains("Windows");
	}
	public static String getUserFolder(){
		String path = System.getProperty("user.home");
		path = path.replace("\\", "/");
		if (path.endsWith("/.")){
			path = path.substring(0, path.length()-1);
		}
		if (!path.endsWith("/")){
			path += "/";
		}
		path = path.replaceAll("%20", " ");
		return path;
	}
	public static String getCurrentPath(){
		String path = new File(CommonFun.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getAbsolutePath();
		path = path.replace("nichea.jar", "");
		path = path.replace("\\", "/");
		if (path.endsWith("/.")){
			path = path.substring(0, path.length()-1);
		}
		if (!path.endsWith("/")){
			path += "/";
		}
		path = path.replaceAll("%20", " ");
		return path;
	}
	public static ArrayList<File> getFileList(File file, HashSet<String> extentionFilters) {
		ArrayList<File> result = new ArrayList<File>();
		if (file.isFile()){
			String extention = getExtension(file);
			if (extention==null){
				return result;
			}
			for (String extentionItem : extentionFilters){
				if (extention.equalsIgnoreCase(extentionItem)){
					result.add(file);
					break;
				}
			}
		}else{
			for (File subfile : file.listFiles()){
				ArrayList<File> subresult = getFileList(subfile, extentionFilters);
				for (File sub : subresult){
					result.add(sub);
				}
			}
		}
		return result;
	}
	public static void rmdir(String foldername){
		File file = new File(foldername);
		if (!file.isDirectory()){
			file.delete();
		}else if (file.isDirectory()){
			for (String filename : file.list()){
				File subfile = new File(foldername + "/" + filename);
				if (!subfile.isDirectory()){
					subfile.delete();
				}else if (subfile.isDirectory()){
					rmdir(foldername + "/" + filename);
				}
			}
		}
	}
	public static void rmfiles(String folderName, String fileNameStartWith) {
		File file = new File(folderName);
		
		if (file.isDirectory()){
			for (String filename : file.list()){
				if (filename.startsWith(fileNameStartWith)){
					File subfile = new File(folderName + "/" + filename);
					if (subfile.isFile()){
						subfile.delete();
					}
				}
			}
		}
		
	}
	public static int[] LLToPosition(double[] geoTransform, double[] long_lat){

		int x = (int)((long_lat[0] - geoTransform[0]) / geoTransform[1]);
		int y = (int)((long_lat[1] - geoTransform[3]) / geoTransform[5]);
        return new int[]{x, y};
	}
	
	public static double[] PositionToLL(double[] geoTransform, int[] xy){
		double dfGeoX, dfGeoY;
		dfGeoX = geoTransform[0] + geoTransform[1] * (xy[0] + .5f) + geoTransform[2] * (xy[1] + .5f);
		dfGeoY = geoTransform[3] + geoTransform[4] * (xy[0] + .5f) + geoTransform[5] * (xy[1] + .5f);
        return new double[]{dfGeoX, dfGeoY};
	}
	public static boolean isInteger(String str){
    	if (str==null){
    		return false;
    	}
    	try{
    		int i = Integer.valueOf(str);
    	}catch (Exception e){
    		return false;
    	}
    	return true;
    }
    public static boolean isDouble(String str){
    	if (str==null){
    		return false;
    	}
    	try{
    		double i = Double.valueOf(str);
    	}catch (Exception e){
    		return false;
    	}
    	return true;
    }
	public static boolean mkdirs(String foldername, boolean removed){
		File file = new File(foldername);
		if (file.exists()) {
			if (removed){
				rmdir(foldername);
			}else{
				return true;
			}
		}
		return file.mkdirs();
	}
	
	public static void writeFile(String Content,String FileName) throws IOException{
		File file = new File(FileName);
		BufferedWriter bw = new BufferedWriter(new FileWriter(file));
		bw.write(Content);
		bw.close();
	}
	public static void writeFile(ArrayList<String> ListContent,String FileName) throws IOException{
		StringBuilder sb = new StringBuilder();
		for (String item : ListContent){
			sb.append(item + Const.LineBreak);
		}
		File file = new File(FileName);
		BufferedWriter bw = new BufferedWriter(new FileWriter(file));
		bw.write(sb.toString());
		bw.close();
	}
	public static void writeXML(Element rootElement, String fileName) throws IOException{
		Document out_doc = new Document();
		out_doc.setRootElement(rootElement);
		Format f = Format.getPrettyFormat();
		f.setEncoding("utf-8");
		XMLOutputter outputter = new XMLOutputter(f);
		//logger.info(filename);
		FileWriter out_file = new FileWriter(fileName);
		outputter.output(out_doc, out_file);
		out_file.close();
	}
	public static Element readXML(String fileName) throws FileNotFoundException, JDOMException, IOException{
		SAXBuilder sb = new SAXBuilder();
		Document doc = sb.build(new FileInputStream(fileName));
		Element rootElement = doc.getRootElement();
		return rootElement;
	}
	public static HashSet<double[]> File2Array(String file) throws IOException{
		
		ArrayList<String> strings = readFromFile(file);
		HashSet<double[]> s = new HashSet<double[]>();
		for (String key : strings){
			String[] ll = key.replace("\t", ",").split(",");
			if (ll.length==2){
				if (CommonFun.isDouble(ll[0])&&(CommonFun.isDouble(ll[1]))){
					s.add(new double[]{Double.valueOf(ll[0]), Double.valueOf(ll[1])});
				}
			}
			if (ll.length==3){
				if (CommonFun.isDouble(ll[1])&&(CommonFun.isDouble(ll[2]))){
					s.add(new double[]{Double.valueOf(ll[1]), Double.valueOf(ll[2])});
				}
			}
		}
		return s;
	}
	public static ArrayList<String> readFromFile(String filename) throws IOException{
		if (filename==null){
			return new ArrayList<String>();
		}
		File file = new File(filename);
		if (file.exists()){
			ArrayList<String> value = new ArrayList<String>();
			InputStream is = new FileInputStream(filename);
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
	        String line = reader.readLine();
	        while (line != null) {
	        	value.add(line);
	            line = reader.readLine();
	        }
	        reader.close();
	        is.close();
	        return value;
		}else{
			return new ArrayList<String>();
		}
	}
	/**
	 * @param linearvalues
	 * @return
	 */
	public static double[] normalization(double[] linearvalues, int Nodata) {
		//y=(x-MinValue)/(MaxValue-MinValue) 
		double min = Double.MAX_VALUE;
		double max = -1 * Double.MAX_VALUE;
		
		for (double value : linearvalues){
			if (value==Nodata){
				continue;
			}
				
			min = (value<min)?value:min;
			max = (value>max)?value:max;
		}
		double[] values = new double[linearvalues.length];
		for (int i=0;i<linearvalues.length;i++){
			if (values[i]==Nodata){
				continue;
			}
			values[i] = (linearvalues[i]-min)/(max-min);
		}
		return values;
	}

	/**
	 * @param x
	 * @param y
	 * @param x2
	 * @param y2
	 */
	public static double getDistance(int x1, int y1, int x2, int y2) {
		return Math.sqrt(Math.pow((x2 - x1), 2) + Math.pow((y2 - y1), 2));
	}
	public static double getDistance(double[] p1, double[] p2) {
		if (p1.length!=p2.length){
			return -1;
		}
		double summary = 0;
		for (int i=0;i<p1.length;i++){
			summary += Math.pow((p1[i] - p2[i]), 2);
		}
		return Math.sqrt(summary);
	}
	/**
	 * @param values
	 */
	public static HashMap<Double, double[][]> normalization(double[][] values) {
		HashMap<Double, double[][]> result = new HashMap<Double, double[][]>();
		double min = Double.MAX_VALUE;
		double max = -1 * Double.MAX_VALUE;
		if (values.length>0){
			for (int x=0;x<values.length;x++){
				for (int y=0;y<values[0].length;y++){
					double value = values[x][y];
					min = (value<min)?value:min;
					max = (value>max)?value:max;
				}
			}
			double[][] new_values = new double[values.length][values[0].length];
			for (int x=0;x<values.length;x++){
				for (int y=0;y<values[0].length;y++){
					
					new_values[x][y] = (values[x][y]-min)/(max-min);
				}
			}
			double middleValue = (-min)/(max-min);
			result.put(middleValue, new_values);
			return result;
		}else{
			double middleValue = 0;
			result.put(middleValue, values);
			return result;
		}
		
		
	}
	public static String getExtension(String f) {
        return getExtension(new File(f));
    }
	public static String getExtension(File f) {
        String ext = "";
        String s = f.getName();
        int i = s.lastIndexOf('.');

        if (i > 0 &&  i < s.length() - 1) {
            ext = s.substring(i+1).toLowerCase();
        }
        return ext;
    }
	/**
	 * @return
	 */
	public static String generateID() {
		return UUID.randomUUID().toString().replace("-", "");
	}
	/**
	 * @param filename
	 * @return
	 */
	public static boolean fileExisted(String filename) {
		File file = new File(filename);
		return file.exists();
	}
	/**
	 * @param x
	 * @return
	 */
	public static byte[] int2byte(int value) {
		return new byte[] {
                (byte)(value >>> 24),
                (byte)(value >>> 16),
                (byte)(value >>> 8),
                (byte)value};

	}
	/**
	 * @param b
	 * @return
	 */
	public static int byte2int(byte[] b) {
		return (b[0] << 24)
        + ((b[1] & 0xFF) << 16)
        + ((b[2] & 0xFF) << 8)
        + (b[3] & 0xFF);
	}
	
	public static double[][] interpolate(double[][] values, int target_xsize, int target_ysize){
		int xsize = values.length;
		int ysize = values.length;
		if (ysize>0){
			xsize = values[0].length;
		}else{
			return null;
		}
		
		double[][] array = new double[target_xsize][target_ysize];
		
		InterpolationBilinear a = new InterpolationBilinear();
		 
		for (int y=0;y<target_ysize;y++){
			for (int x=0;x<target_xsize;x++){
				float xspace = (float)(xsize - 1)/(float)(target_xsize -1);
				float yspace = (float)(ysize - 1)/(float)(target_ysize -1);
				array[x][y] = a.interpolate(values, (float)x * xspace, (float)y * yspace);
			}
		}
		return array;
	}
	public static String getParentPath(String path){
		File f = new File(path);
		return f.getParent();
	}
	public static String getTiffName(String path, String id){
		return String.format("%s/%s.layer.tiff", getParentPath(path), id);
	}
	/**
	 * @param path
	 * @param id
	 * @return
	 */
	public static String getHishName(String path, String id) {
		return String.format("%s/%s.layer.png", getParentPath(path), id);
	}
	/**
	 * @param path
	 * @param id
	 * @return
	 */
	public static String get3DName(String path, String id) {
		return String.format("%s/%s.layer.3d.png", getParentPath(path), id);
	}
	/**
	 * @param path
	 * @param id
	 * @return
	 */
	public static String getLayerXMLName(String path, String id) {
		return String.format("%s/%s.layer.xml", getParentPath(path), id);
	}
	/**
	 * @param tempFile
	 */
	public static void rmfile(String tempFile) {
		File file = new File(tempFile);
		if (file.exists()){
			if (file.isFile()){
				file.delete();
			}
		}
		
	}
	public static String getGifName(String path, String id) {
		return String.format("%s/%s.layer.gif", getParentPath(path), id);
	}
	/**
	 * @param x
	 * @param a
	 * @param i
	 * @return
	 */
	public static boolean equal(float v1, float v2, int precision) {
		if ((int)Math.round(v1 * precision) == (int)Math.round(v2 * precision)){
			return true;
		}
		return false;
	}
	public static boolean equal(double v1, double v2, int precision) {
		if ((int)Math.round(v1 * precision) == (int)Math.round(v2 * precision)){
			return true;
		}
		return false;
	}
	public static void printMatrix(String name, Matrix m){
		double[][] v= m.getArray();
		printArray(name, v);
		
	}
	public static void printArray(String name, double[] v){
		System.out.println("----------------" + name + "---------------");
		for (int j=0;j<v.length;j++){
			System.out.print(v[j] + "\t");
		}
		System.out.println();
	}
	public static void writeArray(double[] v, String filename){
		StringBuilder sb = new StringBuilder();
		for (int j=0;j<v.length;j++){
			
			sb.append(v[j] + Const.LineBreak);
			
		}
		try {
			CommonFun.writeFile(sb.toString(), filename);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void writeArray(double[][] v, String filename, String split){
		StringBuilder sb = new StringBuilder();
		for (int j=0;j<v.length;j++){
			for (int i=0;i<v[0].length;i++){
				if (i==v[0].length-1){
					sb.append(v[j][i]);
				}else{
					sb.append(v[j][i] + split);
				}
			}
			sb.append(Const.LineBreak);
		}
		try {
			CommonFun.writeFile(sb.toString(), filename);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void printArray(String name, double[][] v){
		System.out.println("----------------" + name + "---------------");
		for (int j=0;j<v.length;j++){
			for (int i=0;i<v[0].length;i++){
				System.out.print(v[j][i] + "\t");
			}
			System.out.println();
		}
	}
	public static String printArray(int[] v){
		StringBuilder sb = new StringBuilder();
		for (int j=0;j<v.length;j++){
			sb.append(v[j] + "\t");
		}
		sb.append(Const.LineBreak);
		return sb.toString();
	}
	
	public static String printArray(double[] v){
		StringBuilder sb = new StringBuilder();
		for (int j=0;j<v.length;j++){
			sb.append(v[j] + "\t");
		}
		sb.append(Const.LineBreak);
		return sb.toString();
	}
	public static String printArray(double[][] v){
		StringBuilder sb = new StringBuilder();
		for (int j=0;j<v.length;j++){
			for (int i=0;i<v[0].length;i++){
				sb.append(v[j][i] + "\t");
			}
			sb.append(Const.LineBreak);
		}
		return sb.toString();
	}
	
	/**
	 * @param name
	 * @param allrange
	 */
	public static void printArray(String name, double[][][] v) {
		System.out.println("----------------" + name + "---------------");
		for (int k=0;k<v.length;k++){
			for (int j=0;j<v[0].length;j++){
				for (int i=0;i<v[0][0].length;i++){
					System.out.print(v[k][j][i] + "\t");
				}
				System.out.println("");
			}
			System.out.println("-------");
		}
		
	}
	/**
	 * @param name
	 * @param rangeIndex
	 */
	public static void printArray(String name, int[] v) {
		System.out.println("----------------" + name + "---------------");
		for (int j=0;j<v.length;j++){
			System.out.print(v[j] + "\t");
		}
		System.out.println();
		
	}
	/**
	 * @param values
	 * @return
	 */
	public static float getMaxValue(float[] values) {
		float max = Float.MIN_VALUE;
		for (float v : values){
			max = (max<v)?v:max;
		}
		return max;
	}
	public static float getMinValue(float[] values) {
		float min = Float.MAX_VALUE;
		for (float v : values){
			min = (min>v)?v:min;
		}
		return min;
	}
	/**
	 * @param a
	 * @return
	 */
	public static String getMatrixString(Matrix m) {
		if (m==null){
			return "";
		}
		double[][] v= m.getArray();
		if (v.length==0){
			return "";
		}
		StringBuilder sb = new StringBuilder();
		for (int i=0;i<v.length;i++){
			if (i!=0){
				sb.append("/");
			}
			for (int j=0;j<v[0].length;j++){
				if (j==0){
					sb.append(v[i][j]);
				}else{
					sb.append("," + v[i][j]);
				}
			}
			
		}
		return sb.toString();
	}
	public static double[][] getMeters(double[][] Coords){
//		return Coords;
		double[] testValues = new double[Coords.length];
	    for (int i=0;i<Coords.length;i++){
	    	testValues[i] = new Double(0);
	    }
	    ProjectionData dataTP = new ProjectionData(Coords, testValues);
	    Proj4 testProjection = new Proj4("+proj=latlong +datum=WGS84 +to ",
	            "+proj=cea +lat_ts=30");
	    
	    testProjection.transform(dataTP, Coords.length, 1);
	    double[][] values = new double[dataTP.x.length][2];
	    for (int i=0;i<dataTP.x.length;i++){
	    	values[i][0] = dataTP.x[i];
	    	values[i][1] = dataTP.y[i];
	    }
	    
	    return values;
	}
	/**
	 * @param attributeValue
	 * @return
	 */
	public static Matrix setMatrixString(String v) {
		String[] strs = v.split("/");
		String[] str = strs[0].split(",");
		double[][] values = new double[strs.length][str.length];
		for (int i=0;i<strs.length;i++){
			str = strs[i].split(",");
			for (int j=0;j<str.length;j++){
//				System.out.println(str[j]);
				values[i][j] = Double.valueOf(str[j]);
			}
		}
		Matrix m = new Matrix(values);
		return m;
	}
	public static boolean inPolygon(Point p, ArrayList<Point> points){
		int i, j;
		boolean c = false;
		j = points.size() - 1;
		for (i=0, j = points.size() - 1;i<points.size()-1; j = i++){
			if ((((points.get(i).getY()<=p.getY())&&(p.getY()<points.get(j).getY())) ||
			 ((points.get(j).getY()<=p.getY()) &&(p.getY()<points.get(i).getY()))) && 
			 (p.getX() < (points.get(j).getX() - points.get(i).getX()) 
					 * (p.getY() - points.get(i).getY()) / (points.get(j).getY() - points.get(i).getY()) 
					 + points.get(i).getX())){
				c = !c;
			}
		}
		return c;
	}
	/**
	 * @throws NoSuchFieldException 
	 * @throws SecurityException 
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 * 
	 */
//	public static void refreshSystem(){
//		System.setProperty("java.library.path", ConfigInfo.getInstance().getGdaljni());
//		Field fieldSysPath;
//		try {
//			fieldSysPath = ClassLoader.class.getDeclaredField( "sys_paths" );
//			fieldSysPath.setAccessible( true );
//			fieldSysPath.set( null, null );
//			
//		} catch (SecurityException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (NoSuchFieldException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IllegalArgumentException e) {
//		// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IllegalAccessException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}		
	/**
	 * @param matrix
	 */
	public static String Realmatrix2String(RealMatrix matrix) {
		StringBuilder sb = new StringBuilder();
		for (int col=0;col<matrix.getColumnDimension();col++){
			for (int row=0;row<matrix.getRowDimension();row++){
				sb.append(matrix.getEntry(row, col) + ",");	
			}
			sb.append(Const.LineBreak);
		}
		return sb.toString();
		
	}
	
	
	public static Matrix loadMatrixFromString(String str) {
		String[] strs = str.split(Const.LineBreak);
		String[] colstr = strs[0].split(",");
		double[][] v = new double[strs.length][colstr.length];
		for (int i=0;i<strs.length;i++){
			colstr = strs[i].split(",");
			for (int j=0;j<colstr.length;j++){
				if (isDouble(colstr[j])){
					v[i][j] = Double.valueOf(colstr[j]).doubleValue();
				}else{
					throw new IllegalArgumentException();
				}
			}
		}
		return new Matrix(v);
	}

	public static String MatrixtoString(Matrix m){
		StringBuilder sb = new StringBuilder();
		double[][] v = m.getArray();
		for (int j=0;j<v.length;j++){
			String t = "";
			for (int i=0;i<v[0].length;i++){
				t +=v[j][i] + " ";
			}
			sb.append(String.format("%s%n", t.trim().replace(" ", ",")));
		}
		return sb.toString();
	}
	public static String MatrixtoString_Single_Line(Matrix m){
		StringBuilder sb = new StringBuilder();
		double[][] v = m.getArray();
		for (int j=0;j<v.length;j++){
			String t = "";
			for (int i=0;i<v[0].length;i++){
				t +=v[j][i] + " ";
			}
			sb.append(String.format("%s/", t.trim().replace(" ", ",")));
		}
		return sb.toString();
	}
	public static String Integer2String(int v, int length) {
		String value = String.valueOf(v);
		while (value.length()<length){
			value = "0" + value;
		}
		return value;
	}
	public static void copyFolder(String src_str, String dest_str, boolean isHTML)
	    	throws IOException{
			File src = new File(src_str);
			File dest = new File(dest_str);
	    	if(src.isDirectory()){
	 
	    		//if directory not exists, create it
	    		if(!dest.exists()){
	    		   dest.mkdir();
	    		   System.out.println("Directory copied from " 
	                              + src + "  to " + dest);
	    		}
	 
	    		//list all the directory contents
	    		String files[] = src.list();
	 
	    		for (String file : files) {
	    		   //construct the src and dest file structure
	    		   File srcFile = new File(src, file);
	    		   File destFile = new File(dest, file);
	    		   //recursive copy
	    		   copyFolder(srcFile.getAbsolutePath(), destFile.getAbsolutePath(), isHTML);
	    		}
	 
	    	}else{
	    		if (isHTML){
	    			if ((!src_str.endsWith(".html"))&&(!src_str.endsWith(".png"))){
	    				return;
	    			}
	    		}
	    		//if file, then copy it
	    		//Use bytes stream to support all file types
	    		InputStream in = new FileInputStream(src);
	    	        OutputStream out = new FileOutputStream(dest); 
	 
	    	        byte[] buffer = new byte[1024];
	 
	    	        int length;
	    	        //copy the file content in bytes 
	    	        while ((length = in.read(buffer)) > 0){
	    	    	   out.write(buffer, 0, length);
	    	        }
	 
	    	        in.close();
	    	        out.close();
//	    	        System.out.println("File copied from " + src + " to " + dest);
	    	}
	    }
	
	public static void copyFile(String file1,String file2) throws IOException {
		File f1 = new File(file1);
		File f2 = new File(file2);
        InputStream in = new FileInputStream(f1);

        //For Append the file.
        //OutputStream out = new FileOutputStream(f2,true);

        //For Overwrite the file.
        OutputStream out = new FileOutputStream(f2);

        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0){
          out.write(buf, 0, len);
        }
        in.close();
        out.close();
		
	}
	public static double[][] getRange(HashMap<String, SpeciesData> values, int dimension) {
		double[][] ranges = new double[dimension][2]; 
		for (int i=0;i<dimension;i++){
			ranges[i][0] = Double.MAX_VALUE;
			ranges[i][1] = -1d * Double.MAX_VALUE;
		}
		for (SpeciesData value : values.values()){
			for (int i=0;i<dimension;i++){
				ranges[i][0] = ranges[i][0]>value.getValues()[i]?value.getValues()[i]:ranges[i][0];
				ranges[i][1] = ranges[i][1]<value.getValues()[i]?value.getValues()[i]:ranges[i][1];
			}
		}
		return ranges;
	}
	
	
	public static double getHullVolume(HashMap<String, SpeciesData> values, int dimension) {
		quickhull3d.Point3d[] points = new quickhull3d.Point3d[values.size()];
		int ii = 0;
		for (SpeciesData value : values.values()){
			points[ii++] = new quickhull3d.Point3d(value.getValues()[0], value.getValues()[1], value.getValues()[2]);
		}
		if (dimension==3){
			QuickHull3D hull = new QuickHull3D();
			hull.build(points);
			return hull.getVolume();
		}else{
			QuickHull2D hull = new QuickHull2D();
			return 0;
		}
	}


	public static void show_in_filder(String folder) {
		String[] command = new String[2];
		if (CommonFun.isWindows()){
			command[0] = "Explorer.exe";
			
		}else{
			if (CommonFun.isLinux()){
				command[0] = "/usr/bin/nautilus";
			}
			else{
				command[0] = "/usr/bin/open";
			}
		}
		command[1] = folder;
		CommandExecutor.run(command);
		
	}


	public static String toHTML(String message) {
		String[] strs = message.split("\\n");
		StringBuilder sb = new StringBuilder();
		for (String str : strs){
			sb.append(str + "<br/>");
		}
		
		return sb.toString();
	}


	public static String RunRScript(InputStream rscript,
			HashMap<String, String> parameters, HashSet<String> libraries,
			boolean isRun, String target) throws IOException {
		StringWriter writer = new StringWriter();
		IOUtils.copy(rscript, writer);
		String r = writer.toString();
		
		for (String key : parameters.keySet()){
			r = r.replace(key, parameters.get(key));
		}
		
		if (target!=null){
			writeFile(r, target);
		}
		
		if (isRun){
			RCaller caller = new RCaller();
			caller.setRscriptExecutable(ConfigInfo.getInstance().getRScript());
			for (String library : libraries){
				caller.checkLibrary(library);
			}
			caller.cleanRCode();
			caller.addRCode(r);
			caller.runOnly();
		}
		return r;
	}


	public static String toHTMLTable(String message) {
		String[] strs = message.split("\\n");
		StringBuilder sb = new StringBuilder();
		sb.append("<table border=1>");
		for (String str : strs){
			String[] items = str.split(",");
			sb.append("<tr>");
			for (String item : items){
				sb.append("<td>" + item + "</td>");
			}
			sb.append("</tr>" + Const.LineBreak);
		}
		sb.append("</table>");
		return sb.toString();
	}


	public static void show_in_firefox(String folder) {
		String[] command = new String[2];
		command[0] = "/usr/bin/firefox";
		command[1] = folder;
		CommandExecutor.run(command);
		
	}
	private static double getOmissionError(double threshold, ArrayList<String> presence, GeoTiffObject map) {
		// out(test)/n(test)
		int oe = 0;
		for (String str : presence){
			String[] p = str.split(",");
			
			double value = map.readByLL(Double.valueOf(p[0]), Double.valueOf(p[1]));
			if (value<threshold){
				oe++;
			}
		}
		return (double)oe/(double)presence.size();
	}
	private static double getProportionOfPredictedArea(double threshold,
			HashSet<Double> allvalue) {
		int c = 0;
		for (Double v : allvalue){
			if ((v>=threshold)&&(v>0)){
				c++;
			}
		}
		return (double)c/(double)allvalue.size();
	}
	public static AUCResult getAUC(String resultTiff, int precision, ArrayList<String> presence, double E) throws FileNotFoundException, IOException {
		AUCResult auc = new AUCResult(E);
		GeoTiffObject map = new GeoTiffObject(resultTiff);
		HashMap<Double, Double> roc = new HashMap<Double, Double>();
		auc.setEnmResult(resultTiff);
		roc.put(0.0d, 0.0d);
		roc.put(1.0d, 1.0d);
		HashSet<Double> subvalue = new HashSet<Double>();
		for (double v : map.getValueArray()){
			if ((v>0)&&(!CommonFun.equal(map.getNoData(), v, 1000))){
				subvalue.add(v);
			}
		}
		for (int i=0; i<=100; i += 100/precision){
			double oe = getOmissionError(i/100d, presence, map);
			double p_in_area = getProportionOfPredictedArea(i/100d, subvalue);
			roc.put(p_in_area, 1 - oe);
		}
		
		
		
		auc.setRoc(roc);
		auc = calcAUC(auc);
		map.release();
		return auc;
	}
	private static AUCResult calcAUC(AUCResult auc) {
//		System.out.println("----------begin to calc auc------------");
		//计算AUC值
		//如果E=1，则计算完整的AUC，如果E<1且>0，计算1到1-E的部分AUC值，如果E=0，计算p_in_area中第一个出现值的时候，后面的部分AUC值
		double fixedauc;
		double Ex = 0;
		double Ey = 1 - auc.getE();
		//如果是二值结果，则计算roc=1时候的结果
		double aucValue;
		double aucNULL;
		if (auc.getRoc().size()==2){
			auc.getRoc().put(0d, auc.getRoc().get(1d));
			fixedauc = auc.getRoc().get(1.0);
			aucValue = fixedauc;
			aucNULL = 0.5;
		}else{
			Object [] keys = auc.getRoc().keySet().toArray();
			Arrays.sort(keys);
			
			aucValue = 0;
			aucNULL = 0;
			for (int i=0;i<keys.length;i++){
				Double p_in_area = (Double) keys[i];
				Double tp = auc.getRoc().get(p_in_area);
				//如果是自动寻找第一个非零的p_in_area，需要先找到Ex和Ey
				if (Ey==1){
					if (tp>0){
						if (CommonFun.equal(p_in_area, 0, 1000)){
							Ey = 0;
							break;
						}
						Ex = p_in_area;
						Ey = tp;
						break;
					}
				}
			}
			
			double[] p1 = null;
			double[] p2 = null;
			double[] p3 = null;
			double[] p4 = null;
			
			double[] p1_null = null;
			double[] p2_null = null;
			double[] p3_null = null;
			double[] p4_null = null;
			
			for (int i=0;i<keys.length;i++){
				Double p_in_area = (Double) keys[i];
				Double om = auc.getRoc().get(p_in_area);
				if (om>=Ey){
					if (p1==null){
						Ex = p_in_area;
						
						p1 = new double[]{p_in_area, om};
						p2 = new double[]{p_in_area, om};
						p3 = new double[]{p_in_area, 0};
						p4 = new double[]{p_in_area, 0};
						
						p1_null = new double[]{p_in_area, p_in_area};
						p2_null = new double[]{p_in_area, p_in_area};
						p3_null = new double[]{p_in_area, 0};
						p4_null = new double[]{p_in_area, 0};
						
					}else{
						p2 = new double[]{p_in_area, om};
						p3 = new double[]{p_in_area, 0};
						p2_null = new double[]{p_in_area, p_in_area};
						p3_null = new double[]{p_in_area, 0};
						aucValue += calcArea(p1, p2, p3, p4);
						aucNULL +=  calcArea(p1_null, p2_null, p3_null, p4_null);
						auc.addPoint(p1, p2, p3, p4);
						auc.addPointNull(p1_null, p2_null, p3_null, p4_null);
						p1 = p2;
						p4 = p3;
						p1_null = p2_null;
						p4_null = p3_null;
					}
				}
			}
			fixedauc = aucValue / (1 - Ex);
		}
		auc.setAuc(aucValue);
		auc.setAucNull(aucNULL);
		auc.setEx(Ex);
		auc.setEy(Ey);
		auc.setFixedAUC(fixedauc);
		
		return auc;
	}
	private static double calcArea(double[] p1, double[] p2, double[] p3, double[] p4) {
//		System.out.format("p1(%f,%f),p2(%f,%f),p3(%f,%f),p4(%f,%f)\r\n", 
//				p1[0],p1[1],p2[0],p2[1],p3[0],p3[1],p4[0],p4[1]);
		double l1 = p1[1] - p4[1];
		double l2 = p2[1] - p3[1];
		double height = p3[0] - p4[0];
		double area = (l2 + l1) * height / 2.0; 
//		System.out.format("l1:%f;l2:%f;height:%f;area:%f\r\n", 
//					l1,l2,height,area);	
		return area;
	}


	
}
