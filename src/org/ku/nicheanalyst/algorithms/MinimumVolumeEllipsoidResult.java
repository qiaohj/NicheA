/******************************************************************************
 * Huijie Qiao
 *
 * Project:  Niche Analyst
 * Purpose:  
 * Created date: May 8, 2012 9:46:03 PM
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


package org.ku.nicheanalyst.algorithms;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

import org.apache.commons.math3.stat.descriptive.moment.Variance;
import org.ku.nicheanalyst.common.CommonFun;


import quickhull3d.Point3d;
import Jama.EigenvalueDecomposition;
import Jama.Matrix;

/**
 * @author Huijie Qiao
 *
 */
public class MinimumVolumeEllipsoidResult {
	private Matrix center;
	private Matrix a;
	private float semi_axis_a;
	private float semi_axis_b;
	private float semi_axis_c;
	private float offset_x;
	private float offset_y;
	private float offset_z;
	private float rotate_x;
	private float rotate_y;
	private float rotate_z;
	private HashSet<Point3d> vertexes;
	private Matrix eigenValue;
	private Matrix eigenVector;
	public MinimumVolumeEllipsoidResult(){
		this.vertexes = null;
	}
	public Matrix getCenter() {
		return center;
	}
	public void setCenter(Matrix center) {
		this.center = center;
	}
	public Matrix getA() {
		return a;
	}
	public void setA(Matrix a) {
		this.a = a;
	}
	public float getSemi_axis_Var(){
		float sum = semi_axis_a + semi_axis_b + semi_axis_c;
		float var = (float) (new Variance()).evaluate(new double[]{semi_axis_a/sum, semi_axis_b/sum, semi_axis_c/sum});
		return var;
	}
	public float getVolume(boolean is3D){
		if (semi_axis_a == 0){
			getVertexes(100, 100, is3D);
		}
		
		float volume = 0;
		if (is3D){
			volume = (float)((4f/3f) * Math.PI * semi_axis_a * semi_axis_b * semi_axis_c);
		}else{
			volume = (float)Math.PI * semi_axis_a * semi_axis_b;
		}
		return volume;
	}
	public HashSet<Point3d> getVertexes(int u_resolution, int v_resolution, boolean is3D){
		if (this.vertexes!=null){
			return this.vertexes;
		}
		EigenvalueDecomposition e = new EigenvalueDecomposition(a.inverse());
		this.eigenValue = e.getD();
		this.eigenVector = e.getV();
		this.semi_axis_a = (float) Math.sqrt(eigenValue.get(0, 0));
		this.semi_axis_b = (float) Math.sqrt(eigenValue.get(1, 1));
		if (is3D){
			this.semi_axis_c = (float) Math.sqrt(eigenValue.get(2, 2));
		}else{
			this.semi_axis_c = 0;
		}
		this.offset_x = (float) center.get(0, 0);
		this.offset_y = (float) center.get(1, 0);
		if (is3D){
			this.offset_z = (float) center.get(2, 0);
		}else{
			this.offset_z = 0;
		}
		this.rotate_x = 0;
		this.rotate_y = 0;
		this.rotate_z = 0;
		this.vertexes = new HashSet<Point3d>();
		for (int i=0;i<u_resolution;i++){
			for (int j=0;j<=v_resolution;j++){ 
				float ui = (float) ((Math.PI*2) * (float)i/(float)u_resolution);
				float vi = (float) (Math.PI * (float)j/(float)v_resolution);
				float x = (float) (semi_axis_a * Math.cos(ui) * Math.sin(vi));
				float y = (float) (semi_axis_b * Math.sin(ui) * Math.sin(vi));
				float z = (float) (semi_axis_c * Math.cos(vi));
				if (is3D){
					double[][] t = new double[3][1];
					t[0][0] = x;
					t[1][0] = y;
					t[2][0] = z;
					Matrix p = new Matrix(t);
					p = eigenVector.times(p);
					x = (float) p.get(0, 0);
					y = (float) p.get(1, 0);
					z = (float) p.get(2, 0);
				}else{
					double[][] t = new double[2][1];
					t[0][0] = x;
					t[1][0] = y;
					Matrix p = new Matrix(t);
					p = eigenVector.times(p);
					x = (float) p.get(0, 0);
					y = (float) p.get(1, 0);
					z = 0;
				}
				
				
				Point3d point = getPoint(x, y, z, true);
				this.vertexes.add(point);
			}
		}
		return vertexes;
		
	}
	public Point3d getPoint(float x, float y, float z, boolean isForward) {
		if (isForward){
			float xx, yy, zz;
			//x rotate
			xx = x;
			yy = (float) (y * Math.cos(this.rotate_x) - z * Math.sin(this.rotate_x));
			zz = (float) (y * Math.sin(this.rotate_x) + z * Math.cos(this.rotate_x));
			x = xx;
			y = yy;
			z = zz;
			//y rotate
			xx = (float) (x * Math.cos(this.rotate_y) + z * Math.sin(this.rotate_y));
			yy = y;
			zz = (float) (-1f * x * Math.sin(this.rotate_y) + z * Math.cos(this.rotate_y));
			x = xx;
			y = yy;
			z = zz;
			//z rotate
			xx = (float) (x * Math.cos(this.rotate_z) - y * Math.sin(this.rotate_z));
			yy = (float) (x * Math.sin(this.rotate_z) + y * Math.cos(this.rotate_z));
			zz = z;
			x = xx;
			y = yy;
			z = zz;
	//		//offset
			x += this.offset_x;
			y += this.offset_y;
			z += this.offset_z;
		}else{
			x -= this.offset_x;
			y -= this.offset_y;
			z -= this.offset_z;
			
			float rotate_x = -1f * this.rotate_x;
			float rotate_y = -1f * this.rotate_y;
			float rotate_z = -1f * this.rotate_z;
			
			float xx, yy, zz;
			//z rotate
			xx = (float) (x * Math.cos(rotate_z) - y * Math.sin(rotate_z));
			yy = (float) (x * Math.sin(rotate_z) + y * Math.cos(rotate_z));
			zz = z;
			x = xx;
			y = yy;
			z = zz;
			//y rotate
			xx = (float) (x * Math.cos(rotate_y) + z * Math.sin(rotate_y));
			yy = y;
			zz = (float) (-1f * x * Math.sin(rotate_y) + z * Math.cos(rotate_y));
			x = xx;
			y = yy;
			z = zz;
			//x rotate
			xx = x;
			yy = (float) (y * Math.cos(rotate_x) - z * Math.sin(rotate_x));
			zz = (float) (y * Math.sin(rotate_x) + z * Math.cos(rotate_x));
			x = xx;
			y = yy;
			z = zz;
			
		}
		Point3d point = new Point3d(x, y, z);
		return point;
	}
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("%s%n", "A:"));
		sb.append(CommonFun.MatrixtoString(a));
		sb.append(String.format("%s%n", "C:"));
		sb.append(CommonFun.MatrixtoString(center));
		return sb.toString();
	}
	
	public MinimumVolumeEllipsoidResult(String configurationfile) throws IOException{
		ArrayList<String> configuration = CommonFun.readFromFile(configurationfile);
		int is_a_c = -1;
		int dim = -1;
		for (String str : configuration){
			if (is_a_c==0){
				dim = str.trim().split(",").length;
			}
			if (str.trim().equalsIgnoreCase("A:")){
				is_a_c = 0;
			}
			if (dim!=-1){
				break;
			}
		}
		if (dim>0){
			double[][] a = new double[dim][dim];
			double[][] c = new double[dim][1];
			is_a_c = -1;
			int index = 0;
			boolean is_pass = true;
			boolean is_a = false;
			boolean is_c = false;
			for (String str : configuration){
				if (is_a_c==0){
					String[] valuestr = str.trim().split(",");
					if (index<dim){						
						for (int i=0;i<dim;i++){
							if (CommonFun.isDouble(valuestr[i])){
								a[index][i] = Double.valueOf(valuestr[i]).doubleValue();
							}else{
								is_pass = false;
							}
						}
					}
					index ++;
					is_a = true;
				}
				if (is_a_c==1){
					if (CommonFun.isDouble(str)){
						c[index][0] = Double.valueOf(str).doubleValue();
					}else{
						is_pass = false;
					}
					index ++;
					is_c = true;
				}
				if (str.trim().equalsIgnoreCase("A:")){
					is_a_c = 0;
					index = 0;
				}
				if (str.trim().equalsIgnoreCase("C:")){
					is_a_c = 1;
					index = 0;
				}
			}
			if ((is_a)&&(is_c)&&(is_pass)){
				this.a = new Matrix(a);	
				this.center = new Matrix(c);
			}else{
				this.a = null;
				this.center = null;
			}
			
		}
		
	}
	
	public void print(){
		System.out.println("Minimum Volumn Ellipsoid Result:");
		CommonFun.printMatrix("A", a);
		CommonFun.printMatrix("Center", center);
		System.out.println("-------------------------------");
	}
	public float getSemi_axis_a() {
		return semi_axis_a;
	}
	public void setSemi_axis_a(float semiAxisA) {
		semi_axis_a = semiAxisA;
	}
	public float getSemi_axis_b() {
		return semi_axis_b;
	}
	public void setSemi_axis_b(float semiAxisB) {
		semi_axis_b = semiAxisB;
	}
	public float getSemi_axis_c() {
		return semi_axis_c;
	}
	public void setSemi_axis_c(float semiAxisC) {
		semi_axis_c = semiAxisC;
	}
	public double getSemi_axis(int i) {
		if (this.eigenValue==null){
			EigenvalueDecomposition e = new EigenvalueDecomposition(a.inverse());
			this.eigenValue = e.getD();
			this.eigenVector = e.getV();
		}
		
		return (float) Math.sqrt(this.eigenValue.get(i, i));
	}
	public Matrix getEigenValue() {
		return eigenValue;
	}
	public Matrix getEigenVector() {
		return eigenVector;
	}
	
}
