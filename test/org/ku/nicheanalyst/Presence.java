/******************************************************************************
 * Huijie Qiao
 *
 * Project:  Niche Analyst
 * Purpose:  
 * Created date: Jun 14, 2012 5:16:27 PM
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


package org.ku.nicheanalyst;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.gdal.gdalconst.gdalconst;
import org.junit.Test;
import org.ku.nicheanalyst.algorithms.MinimumVolumeEllipsoid;
import org.ku.nicheanalyst.algorithms.MinimumVolumeEllipsoidResult;
import org.ku.nicheanalyst.common.CommonFun;
import org.ku.nicheanalyst.common.Const;
import org.ku.nicheanalyst.maps.controllers.GeoTiffController;
import org.ku.nicheanalyst.maps.objects.GeoTiffObject;

import quickhull3d.QuickHull3D;
import Jama.Matrix;

/**
 * @author Huijie Qiao
 *
 */
public class Presence {
	@Test
	public void random() throws IOException{
		String llfile = "/Users/huijieqiao/temp/vp/vp4/ll.txt";
		ArrayList<String> a = CommonFun.readFromFile(llfile);
		int record = 100;
		StringBuilder sb = new StringBuilder();
		int count = 0;
		while (count<record){
			int index = (int) (Math.random() * a.size());
			sb.append(a.get(index) + Const.LineBreak);
			count++;
			a.remove(index);
			if (a.size()==0){
				break;
			}
		}
		CommonFun.writeFile(sb.toString(),"/Users/huijieqiao/temp/vp/vp4/samplell.txt");
	}
	
	@Test
	public void reMapRN() throws IOException{
		String folder = "/Users/huijieqiao/data/vp";
		ArrayList<File> vslist = getLL(folder);
		for (File vs : vslist){
			System.out.println(vs.getAbsolutePath());
			String vsFolder = vs.getParent();
			ArrayList<String> values = CommonFun.readFromFile(vsFolder + "/value.txt");
			quickhull3d.Point3d[] points = new quickhull3d.Point3d[values.size()];
			for (int i=0;i<values.size();i++){
				String[] value = values.get(i).split(",");
				if (value.length==3){
					points[i] = new quickhull3d.Point3d(Double.valueOf(value[0]), Double.valueOf(value[1]), Double.valueOf(value[2]));
				}
			}
			QuickHull3D hull = new QuickHull3D();
			hull.build(points);
			quickhull3d.Point3d[] vertices = hull.getVertices();
			double[][] hullvalues = new double[3][vertices.length];
			for (int i=0;i<vertices.length;i++){
				hullvalues[0][i] = vertices[i].x;
				hullvalues[1][i] = vertices[i].y;
				hullvalues[2][i] = vertices[i].z;
			}
			MinimumVolumeEllipsoidResult mve = MinimumVolumeEllipsoid.getMatrix(hullvalues, 3);
			mve.print();
			String vsParentFolder = vs.getParentFile().getParentFile().getParent();
			ArrayList<String> parentValues = CommonFun.readFromFile(vsParentFolder + "/value.txt");
			ArrayList<String> parentXY = CommonFun.readFromFile(vsParentFolder + "/xy.txt");
			HashMap<String, Integer> xylist = new HashMap<String, Integer>();
			for (int i=0;i<parentXY.size();i++){
				xylist.put(parentXY.get(i), i);
			}
			GeoTiffObject geosample = new GeoTiffObject(vsFolder + "/present.tiff"); 
			int xsize = geosample.getXSize();
			int ysize = geosample.getYSize();
			double[] geotransform = geosample.getDataset().GetGeoTransform();
			int[] array = new int[xsize * ysize];
			StringBuilder sb = new StringBuilder();
			for (int x = 0; x<xsize; x++){
//				System.out.println(x + "/" + xsize);
				for (int y = 0; y<ysize; y++){
					array[y * xsize + x] = Const.NoData;
					Integer index =xylist.get(String.format("%d,%d", x, y));
					if (index==null){
						continue;
					}
					String[] value = parentValues.get(index).split(",");
					if (isInEllipsoid(mve.getA(), mve.getCenter(), 
							Float.valueOf(value[0]), Float.valueOf(value[1]), Float.valueOf(value[2]))){
						array[y * xsize + x] = 255;
						sb.append(String.format("%d,%d", x, y) + Const.LineBreak);
					}
				}
			}
			CommonFun.writeFile(sb.toString(), vsFolder + "/xy_rn.txt");
			GeoTiffController.createTiff(vsFolder + "/present_rn.tiff", xsize, ysize, geotransform, array, Const.NoData, gdalconst.GDT_Byte, geosample.getDataset().GetProjection());
		}
	}
	private boolean isInEllipsoid(Matrix A, Matrix C, float x, float y, float z){
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
	 * @param folder
	 * @return
	 */
	private ArrayList<File> getLL(String folder) {
		ArrayList<File> ll = new ArrayList<File>();
		File fold = new File(folder);
		for (File f : fold.listFiles()){
			if (f.isDirectory()){
				ArrayList<File> lltemp = getLL(f.getAbsolutePath());
				for (File ff : lltemp){
					ll.add(ff);
				}
			}else{
				if (f.getName().equalsIgnoreCase("ll.txt")&&(!f.getAbsolutePath().contains("out"))&&(!f.getAbsolutePath().contains("background"))&&
						(f.getAbsolutePath().contains("CB")||f.getAbsolutePath().contains("WD"))){
					ll.add(f);
				}
			}
		}
		return ll;
	}
}
