/******************************************************************************
 * Huijie Qiao
 *
 * Project:  Niche Analyst
 * Purpose:  
 * Created date: May 3, 2012 9:20:14 PM
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


package org.ku.nicheanalyst.ui.display.component.j3d;

import java.awt.Color;

import javax.media.j3d.Appearance;
import javax.media.j3d.Geometry;
import javax.media.j3d.GeometryArray;
import javax.media.j3d.IndexedLineArray;
import javax.media.j3d.Shape3D;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;

import com.sun.j3d.utils.geometry.Sphere;

/**
 * @author Huijie Qiao
 *
 */
public class EllipsoidVertex extends Ellipsoid {
	private float radius;
	private Point3f centre;

	public EllipsoidVertex(Point3f centre, float radius, Appearance appearance, Color3f color, boolean is3D){
		super(1f, radius, radius, radius, 10, 10, 0, centre.x, centre.y, centre.z, 0, 0, 0, color, false, is3D);
		this.radius = radius;
		this.centre = centre;
	}

	/**
	 * @param startPosition
	 * @param radius2
	 * @param createAppearance
	 * @param red
	 */
	public EllipsoidVertex(Point3d centre, float radius,
			Appearance createAppearance, Color3f color, boolean is3D) {
		super(1f, radius, radius, radius, 10, 10, 0, (float)centre.x, (float)centre.y, (float)centre.z, 0f, 0f, 0f, color, false, is3D);
		this.radius = radius;
		this.centre = new Point3f((float)centre.x, (float)centre.y, (float)centre.z);
	}
	
	
}
