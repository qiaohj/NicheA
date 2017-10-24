/******************************************************************************
 * Huijie Qiao
 *
 * Project:  Niche Analyst
 * Purpose:  
 * Created date: May 4, 2012 3:58:07 PM
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

import javax.media.j3d.Appearance;
import javax.media.j3d.ColoringAttributes;
import javax.media.j3d.Geometry;
import javax.vecmath.Color3f;

import com.sun.j3d.utils.geometry.Sphere;

/**
 * @author Huijie Qiao
 *
 */
public class Dot extends Sphere{
	private Color3f color;
	public Dot(Color3f color, float size){
		super(size);
		this.color = color;
		Appearance ap = new Appearance();
		ColoringAttributes ca = new ColoringAttributes(color, ColoringAttributes.NICEST);
		ap.setColoringAttributes(ca); 
		setAppearance(ap); 
//		setGeometry(createGeometry());
//		setAppearance(Common.createAppearance(false, color));
	}
	private Geometry createGeometry() {
//		Sphere point = new Sphere(5f);
//		PointArray point = new PointArray(1, PointArray.COORDINATES | PointArray.ALLOW_COLOR_WRITE | PointArray.COLOR_3);
//		Point3f p = new Point3f(0f, 0f, 0f);
//		point.setCoordinate(0, p);
//		point.setColor(0, color);
//		return point;
		return null;
	}
}
