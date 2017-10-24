/******************************************************************************
 * Huijie Qiao
 *
 * Project:  Niche Analyst
 * Purpose:  
 * Created date: Jun 12, 2012 2:09:06 PM
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


package org.ku.nicheanalyst.ui.display.worker;

import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;

import org.ku.nicheanalyst.algorithms.MinimumVolumeEllipsoid;
import org.ku.nicheanalyst.algorithms.MinimumVolumeEllipsoidResult;
import org.ku.nicheanalyst.common.CommonFun;
import org.ku.nicheanalyst.common.Message;
import org.ku.nicheanalyst.dataset.SpeciesData;
import org.ku.nicheanalyst.dataset.SpeciesDataset;

import quickhull3d.Point3d;
import quickhull3d.QuickHull3D;
import Jama.Matrix;

/**
 * @author Huijie Qiao
 *
 */
public class EllipsoidOverlapCalculator extends SwingWorker<Void, Void> {
	private SpeciesDataset vp1;
	private SpeciesDataset vp2;
	private double precision_per;
	private double minD_Value;
	private double mve_volume1;
	private double mve_volume2;
	private double p_mve_volume1;
	private double p_mve_volume2;
	private double mve_overlap;
	private double convex_volume1;
	private double convex_volume2;
	private double p_convex_volume1;
	private double p_convex_volume2;
	private double convex_overlap;
	private double allcount;
	private MinimumVolumeEllipsoidResult mve1;
	private MinimumVolumeEllipsoidResult mve2;
	private HashSet<Point3d> vertexes1;
	private HashSet<Point3d> vertexes2;
	private String vs1_label;
	private String vs2_label;
	private boolean is3D;
	private String overlap_method;
	private double maxsteps;
	private double precision;
	private String target;
	public EllipsoidOverlapCalculator(SpeciesDataset vp1, SpeciesDataset vp2, double precision_per,
			boolean is3D, String overlap_method, String target){
		this.is3D = is3D;
		this.vp1 = vp1;
		this.vp2 = vp2;
		this.precision_per = precision_per;
		this.minD_Value = Double.MAX_VALUE;
		this.vs1_label = this.vp1.getLabel();
		this.vs2_label = this.vp2.getLabel();
		this.overlap_method = overlap_method;
		this.target = target;
	}
	
	@Override
	protected Void doInBackground() throws Exception {
		
		setProgress(0);
		convex_overlap = -1;
		mve_overlap = -1;
		
		mve1 = CommonFun.getMVE(vp1.getVs(), (this.is3D)?3:2);
		vertexes1 = mve1.getVertexes(100, 100, this.is3D);
		mve_volume1 = mve1.getVolume(this.is3D);
		mve2 = CommonFun.getMVE(vp2.getVs(), (this.is3D)?3:2);
		vertexes2 = mve2.getVertexes(100, 100, this.is3D);
		mve_volume2 = mve2.getVolume(this.is3D);
		
		convex_volume1 = CommonFun.getHullVolume(vp1.getVs(), (this.is3D)?3:2);
		convex_volume2 = CommonFun.getHullVolume(vp2.getVs(), (this.is3D)?3:2);
				
		//MVE: intersection, disjoint or contain
		HashSet<Point3d> vertexes = (mve_volume1>mve_volume2)?vertexes2:vertexes1;
		MinimumVolumeEllipsoidResult mve = (mve_volume2>mve_volume2)?mve1:mve2;
		boolean in = false;
		boolean out = false;
		for (Point3d point : vertexes){
			if (in&&out){
				break;
			}
			if (CommonFun.isInEllipsoid(mve.getA(), mve.getCenter(), point.x, point.y, point.z)){
				in = true;
			}else{
				out = true;
			}
		}
		
		Point3d[] vertices1 = null;
		Point3d[] vertices2 = null;
		//init convex hull information
		boolean is_convex_overlap = this.overlap_method.equalsIgnoreCase(Message.getString("convexhull"));
		if (is_convex_overlap){
			
			HashSet<double[]> values1 = new HashSet<double[]>();
			for (SpeciesData data : this.vp1.getVs().values()){
				values1.add(data.getValues());
			}
			vertices1 = CommonFun.getVertices(values1);	
			
			HashSet<double[]> values2 = new HashSet<double[]>();
			for (SpeciesData data : this.vp2.getVs().values()){
				values2.add(data.getValues());
			}
			vertices2 = CommonFun.getVertices(values2);
			
		}
		
		
		//intersection
		if ((in&&out)||(is_convex_overlap)){
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
			double mve_in1 = 0;
			double mve_in2 = 0;
			double convex_in1 = 0;
			double convex_in2 = 0;
			double mve_in_both = 0;
			double convex_in_both = 0;
			allcount = 0;
			precision = Math.min(mve_volume1, mve_volume2) * precision_per;
			double currentPrecision = precision_per;
//			boolean a = true;
//			while ((minD_Value>precision)&&(allcount<=maxsteps)){
//			while (a){
				currentPrecision = currentPrecision / 2d;
				currentPrecision = Math.min(Math.min(maxx - minx, maxy - miny), maxz - minz) * currentPrecision;
				
				maxsteps = (Math.round((maxx - minx)/currentPrecision)+1) *
						(Math.round((maxy - miny)/currentPrecision)+1) * 
						(Math.round((maxz - minz)/currentPrecision)+1);
				int currentProgress = 0;
				minD_Value = Double.MAX_VALUE;
//				if (isCancelled()){
//					break;
//				}
				
				for (double rand_x=minx;rand_x<=maxx;rand_x+=currentPrecision){
					if (isCancelled()){
						break;
					}
					
					for (double rand_y=miny;rand_y<=maxy;rand_y+=currentPrecision){
						if (isCancelled()){
							break;
						}
						
						for (double rand_z=minz;rand_z<=maxz;rand_z+=currentPrecision){
							currentProgress++;
							int p = (int) (currentProgress * 100/maxsteps + 1);
							if (p>100){
								p = 100;
							}
							if (p<0){
								p = 0;
							}
							try{
								setProgress(p);
							}catch(Exception e){
								System.out.println(p);
							}
							if (isCancelled()){
								break;
							}
							
							boolean is_mve_in1 = false;
							boolean is_convex_in1 = false;
							if (CommonFun.isInEllipsoid(mve1.getA(), mve1.getCenter(), rand_x, rand_y, rand_z)){
								mve_in1++;
								is_mve_in1 = true;
							}
							if (is_convex_overlap){
								if (CommonFun.inConvexHull(new Point3d(rand_x, rand_y, rand_z), vertices1)){
									convex_in1++;
									is_convex_in1 = true;
								}
							}
							boolean is_mve_in2 = false;
							boolean is_convex_in2 = false;
							if (CommonFun.isInEllipsoid(mve2.getA(), mve2.getCenter(), rand_x, rand_y, rand_z)){
								mve_in2++;
								is_mve_in2 = true;
							}
							if (is_convex_overlap){
								if (CommonFun.inConvexHull(new Point3d(rand_x, rand_y, rand_z), vertices2)){
									convex_in2++;
									is_convex_in2 = true;
								}
							}
							
							if (is_mve_in1&&is_mve_in2){
								mve_in_both++;
							}
							
							if (is_convex_overlap){
								if (is_convex_in1&&is_convex_in2){
									convex_in_both++;
								}
							}
							allcount++;
							p_mve_volume1 = mve_in1/allcount * cuboidVolume;
							p_mve_volume2 = mve_in2/allcount * cuboidVolume;
							
							if (is_convex_overlap){
								p_convex_volume1 = convex_in1/allcount * cuboidVolume;
								p_convex_volume2 = convex_in2/allcount * cuboidVolume;
							}
							
							
							double minD_Value_mve = Math.max(Math.abs(p_mve_volume1 - mve_volume1), Math.abs(p_mve_volume2 - mve_volume2));
							
							if (is_convex_overlap){
								double minD_Value_convexhull = Math.max(Math.abs(p_convex_volume1 - convex_volume1), Math.abs(p_convex_volume2 - convex_volume2));
								minD_Value = Math.max(minD_Value_convexhull, minD_Value_mve);
							}else{
								minD_Value = minD_Value_mve;
							}
							if (minD_Value<=precision){
								break;
							}
							
						}
//						System.out.println("end for z");
					}
//					System.out.println("end for y");
				}
//				System.out.println("finish " + currentPrecision);
//			}
			mve_overlap = mve_in_both/allcount * cuboidVolume;
			if (is_convex_overlap){
				convex_overlap = convex_in_both/allcount * cuboidVolume;
			}
		}
		//disjoint
		if (out&&(!in)){
			mve_overlap = 0;
			convex_overlap = 0;
		}
		if ((!out)&&in){
			mve_overlap = (mve_volume1>mve_volume2)?mve_volume2:mve_volume1;
		}
		setProgress(100);
		return null;
	}
	
	/**
	 * @param geoTransform
	 * @param xsize
	 * @param ysize
	 * @param getGeoTransform
	 * @param xSize2
	 * @param ySize2
	 * @return
	 */
	private Exception msg;
	@Override
    public void done() {
		Toolkit.getDefaultToolkit().beep();
        try {
            get();
        } catch (Exception e) {
        	e.printStackTrace();
        	msg = e;
            firePropertyChange("done-exception", null, e);
        }
    }
	public Exception getException(){
		return msg;
	}

	public double getPrecision() {
		return precision;
	}

	public void setPrecision_per(double precision_per) {
		this.precision_per = precision_per;
	}

	public double getMinD_Value() {
		return minD_Value;
	}

	public void setMinD_Value(double minDValue) {
		minD_Value = minDValue;
	}

	public double getMVEVolume1() {
		return mve_volume1;
	}

	public double getMVEVolume2() {
		return mve_volume2;
	}

	
	public double getConvex_volume1() {
		return convex_volume1;
	}

	public double getConvex_volume2() {
		return convex_volume2;
	}

	public double getP_mve_volume1() {
		return p_mve_volume1;
	}

	public double getP_mve_volume2() {
		return p_mve_volume2;
	}

	public double getMve_overlap() {
		return mve_overlap;
	}

	public double getP_convex_volume1() {
		return p_convex_volume1;
	}

	public double getP_convex_volume2() {
		return p_convex_volume2;
	}

	public double getConvex_overlap() {
		return convex_overlap;
	}

	public MinimumVolumeEllipsoidResult getMve1() {
		return mve1;
	}

	public MinimumVolumeEllipsoidResult getMve2() {
		return mve2;
	}

	public HashSet<Point3d> getVertexes1() {
		return vertexes1;
	}

	public HashSet<Point3d> getVertexes2() {
		return vertexes2;
	}

	public double getMaxsteps() {
		return maxsteps;
	}

	public int getAllSteps(){
		return (int)allcount;
	}

	public String getVs1_label() {
		return vs1_label;
	}

	public String getVs2_label() {
		return vs2_label;
	}

	public String getOverlap_method() {
		return overlap_method;
	}

	public String getTarget() {
		return target;
	}
	
	
}
