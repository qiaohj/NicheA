/******************************************************************************
 * Huijie Qiao
 *
 * Project:  Niche Analyst
 * Purpose:  
 * Created date: Aug 9, 2012 6:42:13 PM
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


package org.ku.epidemiology;

import org.junit.Test;
import org.ku.nicheanalyst.common.CommonFun;

import quickhull3d.Point3d;
import quickhull3d.QuickHull3D;

/**
 * @author Huijie Qiao
 *
 */
public class VirtualSpecies {
	@Test
	public void cutRaster(){
		String rasterFolder = "/Users/huijieqiao/Dropbox/GISLayers/Bioclim/10arc";
		String shpFolder = "/Users/huijieqiao/Dropbox/GISLayers/China Map/国界";
	}
	@Test
	public void a(){
		Point3d[] vertices = new Point3d[8];
		int ii = 0;
		for (int x=-1;x<=1;x=x+2){
			for (int y=-1;y<=1;y=y+2){
				for (int z=-1;z<=1;z=z+2){
					vertices[ii++] = new Point3d(x, y, z);
				}
			}
		}
		for (int i=0;i<vertices.length;i++){
			
		}
		Point3d p = new Point3d(0, 0, 0);
		System.out.println(inConvexHull(p, vertices));
		p = new Point3d(1, 1, 1);
		System.out.println(inConvexHull(p, vertices));
		p = new Point3d(1, 1, 2);
		System.out.println(inConvexHull(p, vertices));
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
				return false;
			}
		}
		return true;
	}
	/**
	 * @param p
	 * @param point
	 * @return
	 */
	private static double distance(Point3d p, Point3d point) {
		return Math.sqrt(Math.pow(p.x - point.x, 2) + Math.pow(p.y - point.y, 2) + Math.pow(p.z - point.z, 2));
	}

	@Test
	public void b(){
		System.out.println(Math.acos(-1d));
	}
}
