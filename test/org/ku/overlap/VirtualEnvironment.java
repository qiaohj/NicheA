/******************************************************************************
 * Huijie Qiao
 *
 * Project:  Niche Analyst
 * Purpose:  
 * Created date: Aug 22, 2012 11:30:38 AM
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


package org.ku.overlap;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import org.apache.commons.math3.distribution.AbstractRealDistribution;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.gdal.gdal.gdal;
import org.gdal.gdalconst.gdalconst;
import org.junit.Test;
import org.ku.nicheanalyst.algorithms.MinimumVolumeEllipsoid;
import org.ku.nicheanalyst.algorithms.MinimumVolumeEllipsoidResult;
import org.ku.nicheanalyst.common.CommonFun;
import org.ku.nicheanalyst.common.Const;
import org.ku.nicheanalyst.dataset.GradientType;
import org.ku.nicheanalyst.dataset.Point;
import org.ku.nicheanalyst.maps.controllers.GeoTiffController;
import org.ku.nicheanalyst.maps.objects.GeoTiffObject;
import org.ku.nicheanalyst.ui.display.component.j3d.Color3D;
import org.ku.nicheanalyst.ui.display.component.j3d.Ellipsoid;

import Jama.Matrix;

import quickhull3d.Point3d;
import quickhull3d.QuickHull3D;

/**
 * @author Huijie Qiao
 *
 */
public class VirtualEnvironment {
	private int step = 50;
	@Test
	public void createRandomLayer() throws FileNotFoundException{
		gdal.AllRegister();
		GeoTiffObject sample = new GeoTiffObject("/Users/huijieqiao/Dropbox/Papers/NicheOverlap/cutted_70-65_110_25/bio1_cut.tiff");
		String target = "/Users/huijieqiao/Dropbox/Papers/NicheOverlap/virtual/random/";
		for (int i=1;i<4;i++){
			double[] values = new double[sample.getXSize() * sample.getYSize()];
			
			for (int y=0;y<sample.getYSize();y++){
				for (int x=0;x<sample.getXSize();x++){
					double value = Math.random();
					values[y * sample.getXSize() + x] = value;
				}
			}
			GeoTiffController.createTiff(target + i + ".tif", sample.getXSize(), sample.getYSize(), sample.getDataset().GetGeoTransform(),
					values, Const.NoData, gdalconst.GDT_Float32, sample.getDataset().GetProjection());
		}
		
	}
	@Test
	public void createLinearTrendLayer() throws FileNotFoundException{
		gdal.AllRegister();
		GeoTiffObject sample = new GeoTiffObject("/Users/huijieqiao/Dropbox/Papers/NicheOverlap/cutted_70-65_110_25/bio1_cut.tiff");
		String target = "/Users/huijieqiao/Dropbox/Papers/NicheOverlap/virtual/trend/";
		
		NormalDistribution generator = new NormalDistribution(0, 0.5);
//		double[] values = generateSample(sample.getXSize(), sample.getYSize(), GradientType.Top2Bottom, generator);
//		
//		GeoTiffController.createTiff(target + "top_botton_normal.tif", sample.getXSize(), sample.getYSize(), sample.getDataset().GetGeoTransform(),
//				values, Const.NoData, gdalconst.GDT_Float32);
//		
//		values = generateSample(sample.getXSize(), sample.getYSize(), GradientType.Top2Bottom, null);
//		
//		GeoTiffController.createTiff(target + "top_botton_linear.tif", sample.getXSize(), sample.getYSize(), sample.getDataset().GetGeoTransform(),
//				values, Const.NoData, gdalconst.GDT_Float32);
//		
//		values = generateSample(sample.getXSize(), sample.getYSize(), GradientType.Left2Right, generator);
//		
//		GeoTiffController.createTiff(target + "left_right_normal.tif", sample.getXSize(), sample.getYSize(), sample.getDataset().GetGeoTransform(),
//				values, Const.NoData, gdalconst.GDT_Float32);
//		
//		values = generateSample(sample.getXSize(), sample.getYSize(), GradientType.Left2Right, null);
//		
//		GeoTiffController.createTiff(target + "left_right_linear.tif", sample.getXSize(), sample.getYSize(), sample.getDataset().GetGeoTransform(),
//				values, Const.NoData, gdalconst.GDT_Float32);
		
//		double[] values = generateSample(sample.getXSize(), sample.getYSize(), GradientType.TopLeft2BottomRight, generator);
//		
//		GeoTiffController.createTiff(target + "TopLeft2BottomRight_normal.tif", sample.getXSize(), sample.getYSize(), sample.getDataset().GetGeoTransform(),
//				values, Const.NoData, gdalconst.GDT_Float32);
//		
//		values = generateSample(sample.getXSize(), sample.getYSize(), GradientType.TopLeft2BottomRight, null);
//		
//		GeoTiffController.createTiff(target + "TopLeft2BottomRight_linear.tif", sample.getXSize(), sample.getYSize(), sample.getDataset().GetGeoTransform(),
//				values, Const.NoData, gdalconst.GDT_Float32);
		double[] values = generateSample(sample.getXSize(), sample.getYSize(), GradientType.Center2Round, generator);
		
		GeoTiffController.createTiff(target + "Center2Round_normal.tif", sample.getXSize(), sample.getYSize(), sample.getDataset().GetGeoTransform(),
				values, Const.NoData, gdalconst.GDT_Float32, sample.getDataset().GetProjection());
		
		values = generateSample(sample.getXSize(), sample.getYSize(), GradientType.Center2Round, null);
		
		GeoTiffController.createTiff(target + "Center2Round_linear.tif", sample.getXSize(), sample.getYSize(), sample.getDataset().GetGeoTransform(),
				values, Const.NoData, gdalconst.GDT_Float32, sample.getDataset().GetProjection());
	}
	
	private double[] generateSample(int xsize, int ysize, GradientType gradient, AbstractRealDistribution distributionGenerator) {
		//generate the values firstly
		double[] linearvalues = new double[xsize * ysize];
		double[] sortedlinearvalues = new double[xsize * ysize];
		for (int i=0;i<xsize * ysize;i++){
			linearvalues[i] = (distributionGenerator==null)?Math.random():distributionGenerator.sample();
		}
		linearvalues = CommonFun.normalization(linearvalues, Const.NoData);
		for (int i=0;i<xsize * ysize;i++){
			sortedlinearvalues[i] = linearvalues[i];
		}
		Arrays.sort(sortedlinearvalues);
		//calculate the position (x,y) of each value based on the gradient;
		int xPosition = 0;
		int yPosition = 0;
		double value = 0;
		double [] values = new double[xsize * ysize];
		for (int x=0;x<xsize;x++){
			for (int y=0;y<ysize;y++){
				values[x + y * xsize] = Const.NoData;
			}
		}
		int[][] posList = null;
		if (gradient==GradientType.TopLeft2BottomRight){
			posList = getPosList(xsize, ysize, gradient);
		}
		if (gradient==GradientType.Center2Round){
			posList = getPosList(xsize, ysize, gradient);
		}
		
		for (int i=0;i<linearvalues.length;i++){
			switch (gradient){
				//Random
				case Random:
					yPosition = i / ysize;
					xPosition = getRandom(yPosition, 0, values, xsize, ysize);
					value = linearvalues[i];
					break;
				//Top2Bottom = 1;
				case Top2Bottom:
					yPosition = i / xsize;
					xPosition = getRandom(yPosition, 0, values, xsize, ysize);
					value = sortedlinearvalues[i];
					break;
				//Bottom2Top = 2;
				case Bottom2Top:
					yPosition = ysize - (i / xsize + 1);
					xPosition = getRandom(yPosition, 0, values, xsize, ysize);
					value = sortedlinearvalues[i];
					break;
				//Left2Right = 3;
				case Left2Right:
					xPosition = i / ysize;
					yPosition = getRandom(xPosition, 1, values, xsize, ysize);
					value = sortedlinearvalues[i];
					break;
				//Right2Left = 4;
				case Right2Left:
					xPosition = xsize - (i / ysize + 1);
					yPosition = getRandom(xPosition, 1, values, xsize, ysize);
					value = sortedlinearvalues[i];
					break;
				//TopLeft2BottomRight = 5;
				case TopLeft2BottomRight:
					
					int[] xy = getXY(posList, i);
					xPosition = xy[0];
					yPosition = xy[1];
					value = sortedlinearvalues[i];
					break;
				//BottomRight2TopLeft = 6;
				case BottomRight2TopLeft:
					break;
				//TopRight2BottomLeft = 7;
				case TopRight2BottomLeft:
					break;
				//BottomLeft2TopRight = 8;
				case BottomLeft2TopRight:
					break;
				//Middle2SideVertical = 9;
				case Middle2SideVertical:
					break;
				//Side2MiddleVertical = 10;
				case Side2MiddleVertical:
					break;
				//Middle2SideHorizontal = 11;
				case Middle2SideHorizontal:
					break;
				//Side2MiddleHorizontal = 12;
				case Side2MiddleHorizontal:
					break;
				//Round2Center = 13;
				case Round2Center:
					break;
				//Center2Round = 14;
				case Center2Round:
					int[] xyc = getXY(posList, i);
					xPosition = xyc[0];
					yPosition = xyc[1];
					value = sortedlinearvalues[i];
					break;
				default:
					xPosition = i % xsize;
					yPosition = i / ysize;
					value = linearvalues[i];
					break;
			}
			values[xPosition + yPosition * xsize] = value;
		}
		
		return values;
	}
	/**
	 * @param posList
	 * @param i
	 * @return
	 */
	private int[] getXY(int[][] posList, int i) {
		for (int x=0;x<posList.length;x++){
			for (int y=0;y<posList[0].length;y++){
				if (posList[x][y]==i){
					return new int[]{x, y};
				}
			}
		}
		return null;
	}
	/**
	 * @param xsize
	 * @param ysize
	 * @return
	 */
	private int[][] getPosList(int xsize, int ysize, GradientType gradient) {
		int[][] values = new int[xsize][ysize];
		int Px = 0;
		int Py = 1;
		int xpos = 0;
		int ypos = 0;
		for (int x=0;x<xsize;x++){
			for (int y=0;y<ysize;y++){
				values[x][y] = -1;
			}
		}
		int currentC = 0;
		ArrayList<int[]> cPath = new ArrayList<int[]>();
		cPath.add(new int[]{1, 0});
		cPath.add(new int[]{0, 1});
		cPath.add(new int[]{-1, 0});
		cPath.add(new int[]{0, -1});
		for (int i=0;i<ysize * xsize;i++){
			switch (gradient) {
			case TopLeft2BottomRight:
				values[xpos][ypos] = i;
				xpos = xpos + Px;
				ypos = ypos + Py;
				int[] nextPos = getNextCell(values, xpos, ypos, Px, Py, xsize, ysize);
				Px = nextPos[0];
				Py = nextPos[1];
				
				break;
			case Center2Round:
				if (i==0){
					xpos = (xsize-1)/2;
					ypos = (ysize-1)/2;
				}
//				System.out.println(xpos + "," + ypos);
				values[xpos][ypos] = i;
				if (values[xpos + cPath.get(currentC)[0]][ypos + cPath.get(currentC)[1]]==-1){
					Px = cPath.get(currentC)[0];
					Py = cPath.get(currentC)[1];
					xpos = xpos + Px;
					ypos = ypos + Py;
					currentC++;
					if (currentC>=cPath.size()){
						currentC = 0;
					}
				}else{
					xpos = xpos + Px;
					ypos = ypos + Py;
				}
				
				
				break;
			default:
				break;
			}
		}
		
		return values;
	}
	/**
	 * @param values
	 * @param xpos
	 * @param ypos
	 * @param px
	 * @param py
	 * @param xsize
	 * @param ysize
	 * @return
	 */
	private int[] getNextCellc(int[][] values, int xpos, int ypos, int px,
			int py, int xsize, int ysize) {
		// TODO Auto-generated method stub
		return null;
	}
	/**
	 * @param values
	 * @param xpos
	 * @param ypos
	 * @param py 
	 * @param px 
	 * @return
	 */
	private int[] getNextCell(int[][] values, int xpos, int ypos, int px, int py, int xsize, int ysize) {
		//get the next cell
		//if |px + py| = 1, seek the diagonal 
		if (Math.abs(px + py)==1){
			if ((xpos - 1<0)||(ypos + 1>=ysize)){
				return new int[]{1, -1};
			}else{
				return new int[]{-1, 1};
			}
		}else{
			if ((xpos + px<0)||(xpos + px>=xsize)||(ypos + py<0)||(ypos + py>=ysize)){
				//left to right
				if (px - py==2){
					if (xpos + px>=xsize){
						return new int[]{0, 1};
					}else{
						return new int[]{1, 0};
					}
				}else{
					if (ypos + py>=ysize){
						return new int[]{1, 0};
					}else{
						return new int[]{0, 1};
					}
				}
			}
		}
		return new int[]{px, py};
	}
	private int getRandom(int fix, int variable, double [] values, int xsize, int ysize) {
		ArrayList<Integer> pos = new ArrayList<Integer>();
		//select x value
		if (variable==0){
			for (int i=0;i<xsize;i++){
				if (values[i + fix * xsize]<0){
					pos.add(i);
				}
			}
		}else{
			for (int i=0;i<ysize;i++){
				if (values[fix + i * xsize]<0){
					pos.add(i);
				}
			}
		}
		int resultvalue =pos.get((int) (pos.size() * Math.random())); 
		return resultvalue;
	}
	@Test
	public void test(){
		System.out.println(Math.log(0));
	}
	@Test
	public void testTrend(){
		int[][] values = getPosList(4, 4, GradientType.Center2Round);
		for (int x=0;x<values.length;x++){
			for (int y=0;y<values[0].length;y++){
				System.out.print(values[x][y] + "\t,");
			}
			System.out.println();
		}
	}
	@Test
	public void createDistribution_slide() throws FileNotFoundException{
		gdal.AllRegister();
		GeoTiffObject sample = new GeoTiffObject("/Users/huijieqiao/Dropbox/Papers/NicheOverlap/cutted_70-65_110_25/bio1_cut.tiff");
		ArrayList<Point> points = new ArrayList<Point>();
		points.add(new Point(0, 160));
		points.add(new Point(0, 240));
		points.add(new Point(80, 240));
		points.add(new Point(80, 160));
		points.add(new Point(0, 160));
		String target = "/Users/huijieqiao/Dropbox/Papers/NicheOverlap/vs_g/slide/bottom/";
		for (int i=0;i<step;i++){
			int[] values = new int[sample.getXSize() * sample.getYSize()];
			for (Point p : points){
				p.x = p.x + ((double)sample.getXSize() - 80d) / (double)step;
			}
//			Point pp = new Point(40, 140);
//			System.out.println(CommonFun.inPolygon(pp, points));
			for (int x=0;x<sample.getXSize();x++){
				for (int y=0;y<sample.getYSize();y++){
					Point p = new Point(x, y);
					values[x + y * sample.getXSize()] = inPolygon(p, points)?255:Const.NoData;
				}
			}
			GeoTiffController.createTiff(target + formatNumber(i, 3) + ".tif", sample.getXSize(), sample.getYSize(), 
					sample.getDataset().GetGeoTransform(), values, Const.NoData, gdalconst.GDT_Byte, sample.getDataset().GetProjection());
		}
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
	@Test
	public void getResult() throws IOException{
		gdal.AllRegister();
		String baseFile = "/Users/huijieqiao/Dropbox/Papers/NicheOverlap/vs_g/slide/bottom/";
		StringBuilder sb = new StringBuilder();
		ArrayList<String> lreal = new ArrayList<String>();
		lreal.add("/Users/huijieqiao/Dropbox/Papers/NicheOverlap/cutted_70-65_110_25/pca/1.tiff");
		lreal.add("/Users/huijieqiao/Dropbox/Papers/NicheOverlap/cutted_70-65_110_25/pca/2.tiff");
		lreal.add("/Users/huijieqiao/Dropbox/Papers/NicheOverlap/cutted_70-65_110_25/pca/3.tiff");
		ArrayList<String> lrnd = new ArrayList<String>();
		lrnd.add("/Users/huijieqiao/Dropbox/Papers/NicheOverlap/virtual/random/random1.tif");
		lrnd.add("/Users/huijieqiao/Dropbox/Papers/NicheOverlap/virtual/random/random2.tif");
		lrnd.add("/Users/huijieqiao/Dropbox/Papers/NicheOverlap/virtual/random/random3.tif");
		ArrayList<String> ltrend_linear = new ArrayList<String>();
		ltrend_linear.add("/Users/huijieqiao/Dropbox/Papers/NicheOverlap/virtual/trend/linear/left_right_linear.tif");
		ltrend_linear.add("/Users/huijieqiao/Dropbox/Papers/NicheOverlap/virtual/trend/linear/top_botton_linear.tif");
		ltrend_linear.add("/Users/huijieqiao/Dropbox/Papers/NicheOverlap/virtual/trend/linear/TopLeft2BottomRight_linear.tif");
		ArrayList<String> ltrend_norm = new ArrayList<String>();
		ltrend_norm.add("/Users/huijieqiao/Dropbox/Papers/NicheOverlap/virtual/trend/norm/left_right_normal.tif");
		ltrend_norm.add("/Users/huijieqiao/Dropbox/Papers/NicheOverlap/virtual/trend/norm/top_botton_normal.tif");
		ltrend_norm.add("/Users/huijieqiao/Dropbox/Papers/NicheOverlap/virtual/trend/norm/TopLeft2BottomRight_normal.tif");
		
		sb.append("No.x,D,Imod,Icor,R,O,BC,Ornd,Otrend_linear_diagonal,Otrend_norm_diagonal,Oreal_diagonal" +
				",Otrend_linear_center,Otrend_norm_center" + Const.LineBreak);
		double[] v = getValue(baseFile + formatNumber((step - 1), 3) + ".tif");
		double sumV = getSum(v);
		for (int i=0;i<step;i++){
			double[] vtest = getValue(baseFile + formatNumber(i, 3) + ".tif");
			double sumVTest = getSum(vtest);
			double rnd = getMVEOverlap(baseFile + formatNumber(i, 3) + ".tif", baseFile + formatNumber((step - 1), 3) + ".tif", lrnd, 1000000, 0.01);
			double linear = getMVEOverlap(baseFile + formatNumber(i, 3) + ".tif", baseFile + formatNumber((step - 1), 3) + ".tif", ltrend_linear, 1000000, 0.000001);
			double norm = getMVEOverlap(baseFile + formatNumber(i, 3) + ".tif", baseFile + formatNumber((step - 1), 3) + ".tif", ltrend_norm, 1000000, 0.000001);
			double real = getMVEOverlap(baseFile + formatNumber(i, 3) + ".tif", baseFile + formatNumber((step - 1), 3) + ".tif", lreal, 1000000, 0.01);
			ltrend_linear.remove(2);
			ltrend_linear.add("/Users/huijieqiao/Dropbox/Papers/NicheOverlap/virtual/trend/linear/Center2Round_linear.tif");
			ltrend_norm.remove(2);
			ltrend_norm.add("/Users/huijieqiao/Dropbox/Papers/NicheOverlap/virtual/trend/norm/Center2Round_normal.tif");
			double linear_c = getMVEOverlap(baseFile + formatNumber(i, 3) + ".tif", baseFile + formatNumber((step - 1), 3) + ".tif", ltrend_linear, 1000000, 0.000001);
			double norm_c = getMVEOverlap(baseFile + formatNumber(i, 3) + ".tif", baseFile + formatNumber((step - 1), 3) + ".tif", ltrend_norm, 1000000, 0.000001);
			
			sb.append(String.format("%d,%f,%f,%f,%f,%f,%f,%f,%f,%f,%f,%f,%f%n", i, 
					getD(v, vtest, 0, sumV, sumVTest),
					getD(v, vtest, 1, sumV, sumVTest),
					getD(v, vtest, 2, sumV, sumVTest),
					getD(v, vtest, 3, sumV, sumVTest),
					getD(v, vtest, 4, sumV, sumVTest),
					getD(v, vtest, 5, sumV, sumVTest),
					rnd,
					linear,
					norm,
					real,
					linear_c,
					norm_c));
		}
		CommonFun.writeFile(sb.toString(), "/Users/huijieqiao/Dropbox/Papers/NicheOverlap/result_gs_bottom.csv");
	}
	private static void log(String message, boolean newline){
		if (newline){
			System.out.println(message);
		}else{
			System.out.print(message);
		}
	}
	/**
	 * @param string
	 * @param string2
	 * @param lrnd
	 * @param i
	 * @param d
	 * @return
	 * @throws FileNotFoundException 
	 */
	private double getMVEOverlap(String f1, String f2,
			ArrayList<String> layers, int maxrepeat, double precision) throws FileNotFoundException {
		GeoTiffObject[] pca = new GeoTiffObject[3];
		for (int i=0;i<3;i++){
			pca[i] = new GeoTiffObject(layers.get(i));
		}
		
		GeoTiffObject geo1 = new GeoTiffObject(f1);
		GeoTiffObject geo2 = new GeoTiffObject(f2);
		HashSet<double[]> v1 = getValues(pca, geo1);
		HashSet<double[]> v2 = getValues(pca, geo2);
		if ((v1.size()<=4)||(v2.size()<=4)){
			return 0;
		}
		
		log("calculating No.1 ...", true);
		
		Point3d[] vertices1 = getVertices(v1);
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
		double overlap = 0;
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
			double maxx = Double.MIN_VALUE;
			double maxy = Double.MIN_VALUE;
			double maxz = Double.MIN_VALUE;
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
				}
				
				boolean isin2 = false;
				boolean cisin2 = false;
				if (isInEllipsoid(mve2.getA(), mve2.getCenter(), rand_x, rand_y, rand_z)){
					in2++;
					isin2 = true;
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
				
					minD_Value = Math.max(Math.abs(p_volume1 - volume1), Math.abs(p_volume2 - volume2));
				repeat--;
				if ((double)repeat/1000f==Math.floor(repeat/1000)){
					String logstr = String.format("%7dk repetition left. Current precision:%.5f. Expected precision:%.5f", 
							repeat/1000, minD_Value, precision);
					String back = "";
					for (int i=0;i<logstr.length();i++){
						back += "\b";
					}
//					log(logstr + back, false);
				}
				
			}
			log("", true);
			overlap = in_both/allcount * cuboidVolume;
			coverlap = cin_both/allcount * cuboidVolume;
			log(String.format("Volume 1 (formula/estimate): %f/%f", volume1, p_volume1), true);
			log(String.format("Volume 2 (formula/estimate): %f/%f", volume2, p_volume2), true);
			log(String.format("Repeat %10d times. Error: %f", maxrepeat - repeat, Math.max(Math.abs(p_volume1 - volume1), Math.abs(p_volume2 - volume2))), true);
			log(String.format("Overlap: %f", overlap), true);
		}
		if ((overlap==0)||(p_volume2==0)){
			return 0;
		}else{
			return overlap/p_volume2;
		}
	}
	/**
	 * @param v
	 * @param vtest
	 * @return
	 */
	private double getD(double[] v1, double[] v2, int type, double sumV1, double sumV2) {
		double value = 0;
		double value1 = 0;
		double value2 = 0;
		double value3 = 0;
		
		for (int i=0;i<v1.length;i++){
			switch (type){
				case 0:
					value += Math.abs(v1[i]/sumV1 - v2[i]/sumV2);
					break;
				case 1:
					value += Math.pow((Math.sqrt(v1[i]/sumV1) - Math.sqrt(v2[i]/sumV2)), 2);
					break;
				case 2:
					value += Math.pow((Math.sqrt(v1[i]/sumV1) - Math.sqrt(v2[i]/sumV2)), 2);
					break;
				case 3:
					value1 += (v1[i]/sumV1 + v2[i]/sumV2) * Math.log(v1[i]/sumV1 + v2[i]/sumV2);
					value2 += v1[i]/sumV1 * Math.log(v1[i]/sumV1);
					value3 += v2[i]/sumV2 * Math.log(v2[i]/sumV2);
					break;
				case 4:
					
					break;
				case 5:
					value1 += 2 * Math.min(v1[i]/sumV1, v2[i]/sumV2);
					value2 += v1[i]/sumV1 + v2[i]/sumV2;
					break;
			}
		}
		switch (type){
			case 0:
				value = 1d - .5d * value;
				break;
			case 1:
				value = 1d - .5d * Math.sqrt(value);
				break;
			case 2:
				value = 1d - .5d * Math.pow(Math.sqrt(value), 2);
				break;
			case 3:
				value = (value1 - value2 - value3)/(2d * Math.log(2d));
				break;
			case 4:
				value = 0;
				break;
			case 5:
				value = value1 / value2;
				break;
		}
		return value;
	}
	/**
	 * @param v1
	 * @return
	 */
	private double getSum(double[] v) {
		double value = 0;
		for (int i=0;i<v.length;i++){
			value+=v[i];
		}
		return value;
	}
	private double[] getValue(String filename) throws FileNotFoundException{
		GeoTiffObject geo = new GeoTiffObject(filename);
		double[] values = new double[geo.getXSize() * geo.getYSize()];
		for (int i=0;i<geo.getValueArray().length;i++){
			values[i]=(geo.getValueArray()[i]>0)?1:0;
		}
		return values;
	}
	private static HashSet<double[]> getValues(GeoTiffObject[] pca,
			GeoTiffObject f1) {
		HashSet<double[]> values = new HashSet<double[]>();
		for (int y=0;y<f1.getYSize();y++){
			for (int x=0;x<f1.getXSize();x++){
				if (f1.readByXY(x, y)!=255){
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
	
	@Test
	public void createVS() throws FileNotFoundException{
		gdal.AllRegister();
		float[] centre = new float[]{0f, 0f, 0f}; 
		float[] semiAxes = new float[]{.2f,.2f,.2f};
		ArrayList<GeoTiffObject> lrnd = new ArrayList<GeoTiffObject>();
		lrnd.add(new GeoTiffObject("/Users/huijieqiao/Dropbox/Papers/NicheOverlap/virtual/random/random1.tif"));
		lrnd.add(new GeoTiffObject("/Users/huijieqiao/Dropbox/Papers/NicheOverlap/virtual/random/random2.tif"));
		lrnd.add(new GeoTiffObject("/Users/huijieqiao/Dropbox/Papers/NicheOverlap/virtual/random/random3.tif"));
		ArrayList<GeoTiffObject> ltrend_linear = new ArrayList<GeoTiffObject>();
		ltrend_linear.add(new GeoTiffObject("/Users/huijieqiao/Dropbox/Papers/NicheOverlap/virtual/trend/linear/left_right_linear.tif"));
		ltrend_linear.add(new GeoTiffObject("/Users/huijieqiao/Dropbox/Papers/NicheOverlap/virtual/trend/linear/top_botton_linear.tif"));
		ltrend_linear.add(new GeoTiffObject("/Users/huijieqiao/Dropbox/Papers/NicheOverlap/virtual/trend/linear/TopLeft2BottomRight_linear.tif"));
		ArrayList<GeoTiffObject> ltrend_norm = new ArrayList<GeoTiffObject>();
		ltrend_norm.add(new GeoTiffObject("/Users/huijieqiao/Dropbox/Papers/NicheOverlap/virtual/trend/norm/left_right_normal.tif"));
		ltrend_norm.add(new GeoTiffObject("/Users/huijieqiao/Dropbox/Papers/NicheOverlap/virtual/trend/norm/top_botton_normal.tif"));
		ltrend_norm.add(new GeoTiffObject("/Users/huijieqiao/Dropbox/Papers/NicheOverlap/virtual/trend/norm/TopLeft2BottomRight_normal.tif"));
		for (int i=0;i<step;i++){
			System.out.println(i);
			for (int j=0;j<centre.length;j++){
				centre[j] += 1d / (double)step;
			}
			Ellipsoid ellipsoid = new Ellipsoid(1f, semiAxes[0], semiAxes[1], semiAxes[2], 20, 20, 1, 
					centre[0], centre[1], centre[2], 0, 0, 0, Color3D.red, false, true);
			String target = "/Users/huijieqiao/Dropbox/Papers/NicheOverlap/virtual/random/G/";
			getMap(lrnd, ellipsoid, target + formatNumber(i, 3) + ".tif");
			getMap(ltrend_linear, ellipsoid, "/Users/huijieqiao/Dropbox/Papers/NicheOverlap/virtual/trend/linear/G/" + formatNumber(i, 3) + ".tif");
			getMap(ltrend_norm, ellipsoid, "/Users/huijieqiao/Dropbox/Papers/NicheOverlap/virtual/trend/norm/G/" + formatNumber(i, 3) + ".tif");
		}
	}
	private String formatNumber(int i, int length){
		String a = "" + i;
		while (a.length()<length){
			a = "0" + a;
		}
		return a;
	}
	/**
	 * @param lrnd
	 * @param ellipsoid
	 * @param string
	 */
	private void getMap(ArrayList<GeoTiffObject> layers, Ellipsoid ellipsoid,
			String target) {
		int[] values = new int[layers.get(0).getXSize() * layers.get(0).getYSize()];
		for (int x=0;x<layers.get(0).getXSize();x++){
			for (int y=0;y<layers.get(0).getYSize();y++){
				if (ellipsoid.isInEllipsoid(layers.get(0).readByXY(x, y), layers.get(1).readByXY(x, y), layers.get(2).readByXY(x, y))){
					values[x + y * layers.get(0).getXSize()] = 255;
				}else{
					values[x + y * layers.get(0).getXSize()] = Const.NoData;
				}
			}
		}
		GeoTiffController.createTiff(target, layers.get(0).getXSize(), layers.get(0).getYSize(), 
				layers.get(0).getDataset().GetGeoTransform(), values, Const.NoData, gdalconst.GDT_Byte, layers.get(0).getDataset().GetProjection());
	}
	
	
	@Test
	public void getResult_ES() throws IOException{
		gdal.AllRegister();
//		String baseFile = "/Users/huijieqiao/Dropbox/Papers/NicheOverlap/virtual/random/G/";
		String baseFile = "/Users/huijieqiao/Dropbox/Papers/NicheOverlap/virtual/trend/linear/G/";
//		String baseFile = "/Users/huijieqiao/Dropbox/Papers/NicheOverlap/virtual/trend/norm/G/";
		StringBuilder sb = new StringBuilder();
		
		ArrayList<String> lrnd = new ArrayList<String>();
//		lrnd.add("/Users/huijieqiao/Dropbox/Papers/NicheOverlap/virtual/random/random1.tif");
//		lrnd.add("/Users/huijieqiao/Dropbox/Papers/NicheOverlap/virtual/random/random2.tif");
//		lrnd.add("/Users/huijieqiao/Dropbox/Papers/NicheOverlap/virtual/random/random3.tif");
		lrnd.add("/Users/huijieqiao/Dropbox/Papers/NicheOverlap/virtual/trend/linear/left_right_linear.tif");
		lrnd.add("/Users/huijieqiao/Dropbox/Papers/NicheOverlap/virtual/trend/linear/top_botton_linear.tif");
		lrnd.add("/Users/huijieqiao/Dropbox/Papers/NicheOverlap/virtual/trend/linear/TopLeft2BottomRight_linear.tif");
//		lrnd.add("/Users/huijieqiao/Dropbox/Papers/NicheOverlap/virtual/trend/norm/left_right_normal.tif");
//		lrnd.add("/Users/huijieqiao/Dropbox/Papers/NicheOverlap/virtual/trend/norm/top_botton_normal.tif");
//		lrnd.add("/Users/huijieqiao/Dropbox/Papers/NicheOverlap/virtual/trend/norm/TopLeft2BottomRight_normal.tif");
		
		sb.append("No.x,D,Imod,Icor,R,O,BC,Omve_diagonal,Omve_center" + Const.LineBreak);
		double[] v = getValue(baseFile + formatNumber((step/2), 3) + ".tif");
		double sumV = getSum(v);
		for (int i=0;i<step;i++){
			double[] vtest = getValue(baseFile + formatNumber(i, 3) + ".tif");
			double sumVTest = getSum(vtest);
			double rnd = getMVEOverlap(baseFile + formatNumber(i, 3) + ".tif", baseFile + formatNumber((step/2), 3) + ".tif", lrnd, 1000000, 0.000001);
			lrnd.remove(2);
			lrnd.add("/Users/huijieqiao/Dropbox/Papers/NicheOverlap/virtual/trend/norm/Center2Round_normal.tif");
//			lrnd.remove(2);
//			lrnd.add("/Users/huijieqiao/Dropbox/Papers/NicheOverlap/virtual/trend/norm/Center2Round_normal.tif");
			double rnd2 = getMVEOverlap(baseFile + formatNumber(i, 3) + ".tif", baseFile + formatNumber((step/2), 3) + ".tif", lrnd, 1000000, 0.000001);
			
			sb.append(String.format("%d,%f,%f,%f,%f,%f,%f,%f,%f%n", i, 
					getD(v, vtest, 0, sumV, sumVTest),
					getD(v, vtest, 1, sumV, sumVTest),
					getD(v, vtest, 2, sumV, sumVTest),
					getD(v, vtest, 3, sumV, sumVTest),
					getD(v, vtest, 4, sumV, sumVTest),
					getD(v, vtest, 5, sumV, sumVTest),
					rnd,
					rnd2
					));
		}
//		CommonFun.writeFile(sb.toString(), "/Users/huijieqiao/Dropbox/Papers/NicheOverlap/result_es_random_center.csv");
		CommonFun.writeFile(sb.toString(), "/Users/huijieqiao/Dropbox/Papers/NicheOverlap/result_es_linear_center.csv");
//		CommonFun.writeFile(sb.toString(), "/Users/huijieqiao/Dropbox/Papers/NicheOverlap/result_es_norm_center.csv");
	}
}
