/******************************************************************************
 * Huijie Qiao
 *
 * Project:  Niche Analyst
 * Purpose:  
 * Created date: Aug 23, 2012 2:51:23 PM
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

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;

import org.gdal.gdalconst.gdalconst;
import org.ku.nicheanalyst.algorithms.MinimumVolumeEllipsoid;
import org.ku.nicheanalyst.algorithms.MinimumVolumeEllipsoidResult;
import org.ku.nicheanalyst.common.CommonFun;
import org.ku.nicheanalyst.common.Const;
import org.ku.nicheanalyst.maps.controllers.GeoTiffController;
import org.ku.nicheanalyst.maps.objects.GeoTiffObject;

import Jama.Matrix;

import quickhull3d.QuickHull3D;

/**
 * @author Huijie Qiao
 *
 */
public class MVEReMap {
	private static void log(String message, boolean newline){
		if (newline){
			System.out.println(message);
		}else{
			System.out.print(message);
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		GeoTiffObject[] pcas = new GeoTiffObject[3];
		for (int i=0;i<3;i++){
			pcas[i] = checkFile(new File(args[i]));
		}
		File fs = new File(args[3]);
		String target = args[4];
		if (args[3].equalsIgnoreCase(args[4])){
			log("source can't equals to target!", true);
			System.exit(0);
		}
		File tar = new File(target);
		CommonFun.mkdirs(tar.getAbsolutePath(), true);
		for (File f : fs.listFiles()){
			if (f.isFile()){
				String ext = CommonFun.getExtension(f);
				if (ext.equalsIgnoreCase("asc")){
					log("handling " + f.getAbsolutePath(), true);
					GeoTiffObject geo = checkFile(f);
					MinimumVolumeEllipsoidResult mve = getMVE(getVertices(getValues(pcas, geo)));
					int[] values = new int[geo.getXSize() * geo.getYSize()];
					for (int x=0;x<geo.getXSize();x++){
						for (int y=0;y<geo.getYSize();y++){
							double v1 = pcas[0].readByXY(x, y);
							double v2 = pcas[1].readByXY(x, y);
							double v3 = pcas[2].readByXY(x, y);
							values[x + y * geo.getXSize()] = Const.NoData;
							if (CommonFun.equal(v1, pcas[0].getNoData(), 10000)){
								continue;
							}
							if (CommonFun.equal(v2, pcas[1].getNoData(), 10000)){
								continue;
							}
							if (CommonFun.equal(v3, pcas[2].getNoData(), 10000)){
								continue;
							}
							if (isInEllipsoid(mve.getA(), mve.getCenter(), v1, v2, v3)){
								values[x + y * geo.getXSize()] = 1;
							}else{
								values[x + y * geo.getXSize()] = Const.NoData;
							}
						}
					}
					
					String filename = tar.getAbsolutePath() + "/" + f.getName() + ".tif";
					GeoTiffController.createTiff(filename, geo.getXSize(), geo.getYSize(), geo.getDataset().GetGeoTransform(),
							values, Const.NoData, gdalconst.GDT_CInt32, geo.getDataset().GetProjection());
					GeoTiffController.toAAIGrid(filename, tar.getAbsolutePath() + "/" + f.getName());
				}
			}
		}

	}
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
}
