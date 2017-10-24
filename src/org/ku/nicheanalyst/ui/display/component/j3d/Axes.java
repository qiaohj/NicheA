/******************************************************************************
 * Huijie Qiao
 *
 * Project:  Niche Analyst
 * Purpose:  
 * Created date: May 4, 2012 8:09:23 PM
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
import javax.media.j3d.IndexedLineArray;
import javax.media.j3d.Shape3D;
import javax.vecmath.Color3f;
import javax.vecmath.Point3f;

/**
 * @author Huijie Qiao
 *
 */
public class Axes extends Shape3D{
	private String whichOne;
	private float axisLength;
	public Axes (float axisLength, String whichOne, Color3f color){
		this.whichOne = whichOne;
		this.axisLength = axisLength;
		setGeometry(createGeometry());
		Appearance app = new Appearance();
	    ColoringAttributes ca = new ColoringAttributes(color,
	        ColoringAttributes.SHADE_FLAT);
	    app.setColoringAttributes(ca);
	    setAppearance(app);
	}
	public Geometry createGeometry(){
	    IndexedLineArray la = new IndexedLineArray(11, IndexedLineArray.COORDINATES, 14);
	    float angle = .015f; 
	    if (whichOne.equalsIgnoreCase("X")){
	    	la.setCoordinate(0, new Point3f(-1 * axisLength, 0f, 0f));
	    	la.setCoordinate(1, new Point3f(axisLength, 0f, 0f));
	    	la.setCoordinate(2, new Point3f(axisLength - .05f * axisLength, angle, angle));
	    	la.setCoordinate(3, new Point3f(axisLength - .05f * axisLength, -angle, angle));
	    	la.setCoordinate(4, new Point3f(axisLength - .05f * axisLength, angle, -angle));
	    	la.setCoordinate(5, new Point3f(axisLength - .05f * axisLength, -angle, -angle));
	    	
		}
	    
		if (whichOne.equalsIgnoreCase("Y")){
			la.setCoordinate(0, new Point3f(0f, -1 * axisLength, 0f));
	    	la.setCoordinate(1, new Point3f(0f, axisLength, 0f));
	    	la.setCoordinate(2, new Point3f(angle, axisLength - .05f * axisLength, angle));
	    	la.setCoordinate(3, new Point3f(-angle, axisLength - .05f * axisLength, angle));
	    	la.setCoordinate(4, new Point3f(angle, axisLength - .05f * axisLength, -angle));
	    	la.setCoordinate(5, new Point3f(-angle, axisLength - .05f * axisLength, -angle));
		}
		if (whichOne.equalsIgnoreCase("Z")){
			la.setCoordinate(0, new Point3f(0f, 0f, -1 * axisLength));
	    	la.setCoordinate(1, new Point3f(0f, 0f, axisLength));
	    	la.setCoordinate(2, new Point3f(angle, angle, axisLength - .05f * axisLength));
	    	la.setCoordinate(3, new Point3f(angle, -angle, axisLength - .05f * axisLength));
	    	la.setCoordinate(4, new Point3f(-angle, angle, axisLength - .05f * axisLength));
	    	la.setCoordinate(5, new Point3f(-angle, -angle, axisLength - .05f * axisLength));
		}
		
		
		la.setCoordinateIndex(0, 0);
		la.setCoordinateIndex(1, 1);
		la.setCoordinateIndex(2, 2);
		la.setCoordinateIndex(3, 1);
		la.setCoordinateIndex(4, 3);
		la.setCoordinateIndex(5, 1);
		la.setCoordinateIndex(6, 4);
		la.setCoordinateIndex(7, 1);
		la.setCoordinateIndex(8, 5);
		la.setCoordinateIndex(9, 1);
		
		return(la);
	} // end Line()
}
