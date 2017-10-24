/******************************************************************************
 * Huijie Qiao
 *
 * Project:  Niche Analyst
 * Purpose:  
 * Created date: May 8, 2012 9:45:44 PM
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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;

import org.apache.commons.lang3.ArrayUtils;
import org.ku.nicheanalyst.common.CommonFun;

import Jama.Matrix;
import Jama.SingularValueDecomposition;

/**
 * @author Huijie Qiao
 *
 */ 
public class MinimumVolumeEllipsoid {
	public static MinimumVolumeEllipsoidResult getMatrix(double[][] vv, int dimension){
		if (vv==null){
			return null;
		}
		int d = dimension;
		if (d==0){
			return null;
		}
		double[][] v = null;
		if (vv.length==d){
			v = vv;
		}else{
			v = new double[d][vv[0].length];
			for (int i=0;i<v.length;i++){
				v[i] = vv[i];
			}
		}
		int N = v[0].length;
		double[][] QArray = new double[d + 1][N];
		double[] u = new double[N];
		for (int i=0;i<N;i++){
			u[i] = 1/(double)N;
		}
		for (int i=0;i<d;i++){
			for (int j=0;j<N;j++){
				QArray[i][j] = v[i][j];
				QArray[d][j] = 1;
			}
		}
		Matrix Q = new Matrix(QArray);
		Matrix P = new Matrix(v);
		double err = 1;
		double tolerance = 0.01;
		int count = 1;
		double min = Double.MAX_VALUE;
		while (err>tolerance){
			Matrix X = Q.times(Matrix.diagonal(u, N, N)).times(Q.transpose());
			double[] M = Q.transpose().times(X.inverse()).times(Q).diagonal();
			double maxM = maxValue(M);
			int j = order(M, N-1);
			double step_size = ((double)(maxM-d-1))/((double)((d+1)*(maxM-1)));
			double[] new_u = arrayMultiply(1 - step_size, u);
			new_u[j] = new_u[j] + step_size;
			count++;
			double[] tempu = arrayMinus(new_u, u);
			SingularValueDecomposition sing = new SingularValueDecomposition(new Matrix(tempu, 1));
			err = maxValue(sing.getSingularValues());
//			System.out.println(err);
			u = new_u;
			min = err<min?err:min;
//			System.out.println("Min is " + min);
		}
		Matrix U = Matrix.diagonal(u, N, N);
		Matrix A = P.times(U);
		A = A.times(P.transpose());
		Matrix B = P.times(u);
		Matrix C = P.times(u).transpose();
		B = B.times(C);
		A = A.minus(B);
		A = A.inverse();
		A = A.divide(d);
		Matrix c = P.times(u);
		MinimumVolumeEllipsoidResult result = new MinimumVolumeEllipsoidResult();
		result.setA(A);
		result.setCenter(c);
		return result;
	}
	/**
	 * @param newU
	 * @param u
	 * @return
	 */
	private static double[] arrayMinus(double[] v, double[] u) {
		double[] vv = new double[v.length];
		for (int i=0;i<vv.length;i++){
			vv[i] = v[i] - u[i];
		}
		return vv;
	}

	/**
	 * @param d
	 * @param u
	 * @return
	 */
	private static double[] arrayMultiply(double d, double[] v) {
		double[] vv = new double[v.length];
		for (int i=0;i<v.length;i++){
			vv[i] = v[i] * d;
		}
		return vv;
	}

	/**
	 * @param m
	 * @param n
	 * @return
	 */
	private static int order(double[] m, int n) {
		TreeMap<Double, Integer> a = new TreeMap<Double, Integer>();
		for (int i=0;i<m.length;i++){
			a.put(m[i], i);
		}
		return a.get(a.lastKey());
	}
	private static boolean equal(double a, double b, int precision){
		if ((int)(a * precision)==(int)(b * precision)){
			return true;
		}else{
			return false;
		}
	}

	
	private static double maxValue(double[] v){
		List b = Arrays.asList(ArrayUtils.toObject(v));
        return Collections.max(b);
	}
	private static double minValue(double[] v){
		List b = Arrays.asList(ArrayUtils.toObject(v));
        return Collections.min(b);
	}
}
