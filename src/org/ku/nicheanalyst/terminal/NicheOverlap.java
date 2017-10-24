/******************************************************************************
 * Huijie Qiao
 *
 * Project:  Niche Analyst
 * Purpose:  
 * Created date: Aug 17, 2012 1:29:49 PM
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

import java.awt.Point;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

import org.ku.nicheanalyst.algorithms.MinimumVolumeEllipsoid;
import org.ku.nicheanalyst.algorithms.MinimumVolumeEllipsoidResult;
import org.ku.nicheanalyst.common.CommonFun;
import org.ku.nicheanalyst.common.Const;
import org.ku.nicheanalyst.maps.controllers.GeoTiffController;
import org.ku.nicheanalyst.maps.objects.GeoTiffObject;
import org.ku.nicheanalyst.ui.filefilters.ASCFileFilter;

import quickhull3d.Point3d;
import quickhull3d.QuickHull3D;
import Jama.Matrix;

/**
 * @author Huijie Qiao
 *
 */
public class NicheOverlap {
	/**
	 * @param args
	 */
	private static void log(String message, boolean newline){
		if (newline){
			System.out.println(message);
		}else{
			System.out.print(message);
		}
	}
	public static void main(String[] args) throws IOException {
		args = (args==null)?new String[]{}:args;
		if (args.length<8){
			log("not engouth parameters", true);
			System.exit(0);
		}
		int times = 1;
		if (args[6].toLowerCase().endsWith("k")){
			times = 1000;
		}
		if (args[6].toLowerCase().endsWith("m")){
			times = 1000000;
		}
		if (args[6].toLowerCase().endsWith("b")){
			times = 1000000000;
		}
		String t = args[6].toLowerCase().replace("k", "").replace("m", "").replace("b", "");
		int maxrepeat = (CommonFun.isInteger(t))?Integer.valueOf(t):1000000;
		maxrepeat = maxrepeat * times;
		double precision = (CommonFun.isDouble(args[5]))?Double.valueOf(args[5]):0.01;
		log("loading data ...", true);
		String input = args[3];
		boolean isconvexhull = args[4].equalsIgnoreCase("true")?true:false;
		GeoTiffObject[] pca = new GeoTiffObject[3];
		for (int i=0;i<3;i++){
			pca[i] = checkFile(new File(args[i]));
			if (pca[i]==null){
				log(String.format("There are some error for pca file %s, exit.", args[i]), true);
				System.exit(0);
			}
		}
		ArrayList<String> inFiles = CommonFun.readFromFile(input);
		log(String.format("%d command was found.", inFiles.size()), true);
		
		for (String f : inFiles){
			String[] fs = f.split(",");
			File f1 = new File(fs[0]);
			File f2 = new File(fs[1]);
			String output = fs[2];
			log(String.format("%s vs %s. output: %s", f1.getAbsolutePath(), f2.getAbsolutePath(), output), true);
			File[] f1s = null;
			if (f1.isFile()){
				f1s = new File[]{f1};
			}else{
				f1s = f1.listFiles();
			}
			File[] f2s = null;
			if (f2.isFile()){
				f2s = new File[]{f2};
			}else{
				f2s = f2.listFiles();
			}
			
			String extend = args[7];
			StringBuilder sb = new StringBuilder();
			sb.append("No.1,No.2,Overlap,Volume.Cube,Volume.1(f),Volume.2(f),Volume.1(e),Volume.2(e),Overlap.ConvexHull,Volume.ConvexHull.1,Volume.ConvexHull.2,Repeat,Precision" + Const.LineBreak);
			for (File f1item : f1s){
				if (!(f1item.getAbsolutePath().toLowerCase().endsWith("." + extend.toLowerCase()))){
					log(String.format("unknown file %s, skip it.", f1item.getAbsolutePath()), true);
					continue;
				}
				GeoTiffObject geo1 = checkFile(f1item);
				for (File f2item : f2s){
					if (!(f2item.getAbsolutePath().toLowerCase().endsWith("." + extend.toLowerCase()))){
						log(String.format("unknown file %s, skip it.", f2item.getAbsolutePath()), true);
						continue;
					}
					if (f2item.getAbsolutePath().equals(f1item.getAbsolutePath())){
						log(String.format("it's the same file %s, skip it.", f2item.getAbsolutePath()), true);
						continue;
					}
					GeoTiffObject geo2 = checkFile(f2item);
					log(String.format("calculating %s and %s", f1item.getName(), f2item.getName()), true);
					if ((geo1==null)||(geo2==null)){
						log(String.format("cannot calculate %s and %s. skip them", f1.getName(), f2.getName()), true);
					}
					
					log("calculating No.1 ...", true);
					Point3d[] vertices1 = getVertices(getValues(pca, geo1));
					MinimumVolumeEllipsoidResult mve1 = getMVE(vertices1);
					HashSet<Point3d> vertexes1 = mve1.getVertexes(100, 100, true);
					float volume1 = mve1.getVolume(true);
					log(String.format("volume 1: %f", volume1), true);
					log("calculating No.2 ...", true);
					Point3d[] vertices2 = getVertices(getValues(pca, geo2));
					MinimumVolumeEllipsoidResult mve2 = getMVE(vertices2);
					HashSet<Point3d> vertexes2 = mve2.getVertexes(100, 100, true);
					float volume2 = mve2.getVolume(true);
					log(String.format("volume 2: %f", volume2), true);
					log("judging the connectedness ...", true);
					//intersection, disjoint or contain
					HashSet<Point3d> vertexes = (volume1>volume2)?vertexes2:vertexes1;
					MinimumVolumeEllipsoidResult mve = (volume1>volume2)?mve1:mve2;
					boolean in = false;
					boolean out = false;
					for (Point3d point : vertexes){
						if (in&&out){
							break;
						}
						if (isInEllipsoid(mve.getA(), mve.getCenter(), point.x, point.y, point.z)){
							in = true;
						}else{
							out = true;
						}
					}
					double minD_Value = Double.MAX_VALUE;
					double p_volume1 = 0;
					double p_volume2 = 0;
					double p_cvolume1 = 0;
					double p_cvolume2 = 0;
					double overlap;
					double coverlap;
					
					if (out&&(!in)){
						overlap = 0;
						coverlap = 0;
						log("These two ellipsoids are disjoint", true);
					}
					if ((!out)&&in){
						overlap = (volume1>volume2)?volume2:volume1;
						log(String.format("Ellipsoid %s is in Ellipsoid %s", (volume1>volume2)?2:1, (volume1>volume2)?1:2), true);
					}
					
					//intersection
					if (in&&out){
						log("These two ellipsoids are intersection", true);
						double minx = Double.MAX_VALUE;
						double miny = Double.MAX_VALUE;
						double minz = Double.MAX_VALUE;
						double maxx = -1 * Double.MAX_VALUE;
						double maxy = -1 * Double.MAX_VALUE;
						double maxz = -1 * Double.MAX_VALUE;
						for (Point3d point : vertexes1){
							minx = Math.min(point.x, minx);
							miny = Math.min(point.y, miny);
							minz = Math.min(point.z, minz);
							maxx = Math.max(point.x, maxx);
							maxy = Math.max(point.y, maxy);
							maxz = Math.max(point.z, maxz);
						}
						for (Point3d point : vertexes2){
							minx = Math.min(point.x, minx);
							miny = Math.min(point.y, miny);
							minz = Math.min(point.z, minz);
							maxx = Math.max(point.x, maxx);
							maxy = Math.max(point.y, maxy);
							maxz = Math.max(point.z, maxz);
						}
						double cuboidVolume = (maxx - minx) * (maxy - miny) * (maxz - minz);
						double in1 = 0;
						double in2 = 0;
						double in_both = 0;
						double cin1 = 0;
						double cin2 = 0;
						double cin_both = 0;
						double allcount = 0;
						int repeat = maxrepeat;
						log("calculating the overlap ...", true);
						double[] returnValue = new double[4];
						while ((minD_Value>precision)&&(repeat>=0)){
							double rand_x = Math.random() * (maxx - minx) + minx;
							double rand_y = Math.random() * (maxy - miny) + miny;
							double rand_z = Math.random() * (maxz - minz) + minz;
							boolean isin1 = false;
							boolean cisin1 = false;
							if (isInEllipsoid(mve1.getA(), mve1.getCenter(), rand_x, rand_y, rand_z)){
								in1++;
								isin1 = true;
								if (isconvexhull){
									if (inConvexHull(new Point3d(rand_x, rand_y, rand_z), vertices1)){
										cin1++;
										cisin1 = true;
									}
								}
							}
							
							boolean isin2 = false;
							boolean cisin2 = false;
							if (isInEllipsoid(mve2.getA(), mve2.getCenter(), rand_x, rand_y, rand_z)){
								in2++;
								isin2 = true;
								if (isconvexhull){
									if (inConvexHull(new Point3d(rand_x, rand_y, rand_z), vertices2)){
										cin2++;
										cisin2 = true;
									}
								}
							}
							
							
							if (isin1&&isin2){
								in_both++;
							}
							if (cisin1&&cisin2){
								cin_both++;
							}
							
							allcount++;
							p_volume1 = in1/allcount * cuboidVolume;
							p_volume2 = in2/allcount * cuboidVolume;
							p_cvolume1 = cin1/allcount * cuboidVolume;
							p_cvolume2 = cin2/allcount * cuboidVolume;
							
			//				if (minD_Value < Math.max(Math.abs(p_volume1 - volume1), Math.abs(p_volume2 - volume2))){
			//					in1 = returnValue[0];
			//					in2 = returnValue[1];
			//					in_both = returnValue[2];
			//					allcount = returnValue[3];
			//				}else{
			//					returnValue[0] = in1;
			//					returnValue[1] = in2;
			//					returnValue[2] = in_both;
			//					returnValue[3] = allcount;
								minD_Value = Math.max(Math.abs(p_volume1 - volume1), Math.abs(p_volume2 - volume2));
			//				}
							repeat--;
							if ((double)repeat/1000f==Math.floor(repeat/1000)){
								String logstr = String.format("%7dk repetition left. Current precision:%.5f. Expected precision:%.5f", 
										repeat/1000, minD_Value, precision);
								String back = "";
								for (int i=0;i<logstr.length();i++){
									back += "\b";
								}
								log(logstr + back, false);
							}
							
						}
						log("", true);
						overlap = in_both/allcount * cuboidVolume;
						coverlap = cin_both/allcount * cuboidVolume;
						log(String.format("Volume 1 (formula/estimate): %f/%f", volume1, p_volume1), true);
						log(String.format("Volume 2 (formula/estimate): %f/%f", volume2, p_volume2), true);
						log(String.format("Repeat %10d times. Error: %f", maxrepeat - repeat, Math.max(Math.abs(p_volume1 - volume1), Math.abs(p_volume2 - volume2))), true);
						log(String.format("Overlap: %f", overlap), true);
						sb.append(String.format("%s,%s,%f,%f,%f,%f,%f,%f,%f,%f,%f,%d,%f", f1item.getName(), f2item.getName(), overlap,
								cuboidVolume, volume1, volume2, p_volume1, p_volume2,
								coverlap,p_cvolume1,p_cvolume2,
								maxrepeat - repeat, Math.max(Math.abs(p_volume1 - volume1), Math.abs(p_volume2 - volume2))) + Const.LineBreak);
					}
				}
			}
			CommonFun.writeFile(sb.toString(), output);
		}
		log("done!", true);
	}
	private static boolean isInEllipsoid(Matrix A, Matrix C, double x, double y, double z){
		double[] tt = new double[3];
		tt[0] = x;
		tt[1] = y;
		tt[2] = z;
		Matrix xM = new Matrix(tt, 3);
//		CommonFun.printMatrix(xM);
//		System.out.println("");
		Matrix tempMatrix = xM.minus(C);
		Matrix distanceMatrix = tempMatrix.transpose();
		distanceMatrix = distanceMatrix.times(A);
		distanceMatrix = distanceMatrix.times(tempMatrix);
		double distance = distanceMatrix.getArray()[0][0];
		
		if (distance<=1){
			return true;
		}else{
			return false;
		}
	}
	/**
	 * @param pca
	 * @param f1
	 * @return
	 */
	private static HashSet<double[]> getValues(GeoTiffObject[] pca,
			GeoTiffObject f1) {
		HashSet<double[]> values = new HashSet<double[]>();
		for (int y=0;y<f1.getYSize();y++){
			for (int x=0;x<f1.getXSize();x++){
				if (f1.readByXY(x, y)!=1){
					continue;
				}
				double[] value = new double[3];
				boolean allValue = true;
				for (int i=0;i<3;i++){
					value[i] = pca[i].readByXY(x, y);
					if (CommonFun.equal(value[i], pca[i].getNoData(), 1000)){
						allValue = false;
					}
				}
				if (allValue){
					values.add(value);
				}
			}
		}
		return values;
	}
	private static quickhull3d.Point3d[] getVertices(HashSet<double[]> values){
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
	private static MinimumVolumeEllipsoidResult getMVE(quickhull3d.Point3d[] vertices){
		double[][] hullvalues = new double[3][vertices.length];
		for (int i=0;i<vertices.length;i++){
			hullvalues[0][i] = vertices[i].x;
			hullvalues[1][i] = vertices[i].y;
			hullvalues[2][i] = vertices[i].z;
		}
		MinimumVolumeEllipsoidResult mve = MinimumVolumeEllipsoid.getMatrix(hullvalues, 3);
//		mve.print();
		return mve;
	}
	/**
	 * @param f1
	 */
	private static GeoTiffObject checkFile(File file) {
		try{
			String filename = file.getAbsolutePath();
			if (CommonFun.getExtension(file).equalsIgnoreCase("asc")){
				filename = file.getAbsolutePath().replace(".asc", ".tif");
				GeoTiffController.toGeoTIFF(file.getAbsolutePath(), filename);
			}
			return new GeoTiffObject(filename);
		}catch(FileNotFoundException e){
			log(String.format("can't open file '%s'", file.getAbsoluteFile()), true);
			return null;
		}
		
	}

	private static boolean inConvexHull(Point3d point, Point3d[] vertices){
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
		return isin(point, rvertices);
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
	/**
	 * @param p
	 * @param point
	 * @return
	 */
	private static double distance(Point3d p, Point3d point) {
		return Math.sqrt(Math.pow(p.x - point.x, 2) + Math.pow(p.y - point.y, 2) + Math.pow(p.z - point.z, 2));
	}

}
