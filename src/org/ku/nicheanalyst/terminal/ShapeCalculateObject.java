/******************************************************************************
 * Huijie Qiao
 *
 * Project:  NicheA
 * Purpose:  
 * Created date: Sep 13, 2012 4:29:04 PM
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

import java.util.HashMap;
import java.util.HashSet;

import org.ku.nicheanalyst.algorithms.MinimumVolumeEllipsoid;
import org.ku.nicheanalyst.algorithms.MinimumVolumeEllipsoidResult;

import quickhull3d.Point3d;
import quickhull3d.QuickHull3D;

/**
 * @author Huijie Qiao
 *
 */
public class ShapeCalculateObject {
	private HashSet<double[]> values;
	private Point3d[] vertices;
	private MinimumVolumeEllipsoidResult mve;
	private HashSet<Point3d> vertexes;
	public ShapeCalculateObject(HashSet<double[]> values){
		
	}
	private void init(HashSet<double[]> values){
		vertices = null;
		mve = null;
		this.values = values;
	}
	public HashSet<double[]> getValues() {
		return values;
	}
	public Point3d[] getVertices() {
		if (vertices==null){
			vertices = getVertices(values);
		}
		return vertices;
	}
	public MinimumVolumeEllipsoidResult getMve() {
		if (mve==null){
			mve = getMVE(getVertices());
			mve.getVertexes(100, 100, true);
		}
		return mve;
	}
	public HashSet<Point3d> getVertexes() {
		return vertexes;
	}
	private quickhull3d.Point3d[] getVertices(HashSet<double[]> values){
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
	private MinimumVolumeEllipsoidResult getMVE(quickhull3d.Point3d[] vertices){
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
	public void setValues(HashSet<double[]> values) {
		init(values);
	}
	
}
