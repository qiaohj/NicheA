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

import javax.media.j3d.Geometry;
import javax.media.j3d.IndexedLineArray;
import javax.media.j3d.Shape3D;
import javax.vecmath.Point3f;

/**
 * @author Huijie Qiao
 *
 */
public class Grids extends Shape3D{
	private float axisLength;
	private int steps;
	public Grids (float axisLength, int steps){
		this.axisLength = axisLength;
		this.steps = steps;
		setGeometry(createGeometry());
	}
	public Geometry createGeometry(){
	    IndexedLineArray la = new IndexedLineArray(3 * (steps + 1) * 2 * (steps + 1), IndexedLineArray.COORDINATES, 3 * (steps + 1) * 2 * (steps + 1));
	    float x1 = -1f * axisLength;
	    float x2 = axisLength;
	    int coodindex = 0;
	    int lineindex = 0;
	    for (float y = (-1f) * axisLength; y<= axisLength; y += (axisLength * 2f) / (float)(steps)){
    		for (float z = (-1f) * axisLength; z<= axisLength; z += (axisLength * 2f) / (float)(steps)){
    			
    			la.setCoordinate(coodindex++, new Point3f(x1, y, z));
    			la.setCoordinate(coodindex++, new Point3f(x2, y, z));
    			la.setCoordinateIndex(lineindex++, coodindex - 2);
    			la.setCoordinateIndex(lineindex++, coodindex - 1);
    		}
    	}
//	    System.out.println(coodindex);
	    float y1 = -1f * axisLength;
	    float y2 = axisLength;
	
	    for (float x = (-1f) * axisLength; x<= axisLength; x += (axisLength * 2f) / (float)(steps)){
    		for (float z = (-1f) * axisLength; z<= axisLength; z += (axisLength * 2f) / (float)(steps)){
    			
    			la.setCoordinate(coodindex++, new Point3f(x, y1, z));
    			la.setCoordinate(coodindex++, new Point3f(x, y2, z));
    			la.setCoordinateIndex(lineindex++, coodindex - 2);
    			la.setCoordinateIndex(lineindex++, coodindex - 1);
    		}
    	}
//	    System.out.println(coodindex);
	    float z1 = -1f * axisLength;
	    float z2 = axisLength;
	    for (float y = (-1f) * axisLength; y<= axisLength; y += (axisLength * 2f) / (float)(steps)){
    		for (float x = (-1f) * axisLength; x<= axisLength; x += (axisLength * 2f) / (float)(steps)){
    			la.setCoordinate(coodindex++, new Point3f(x, y, z1));
    			la.setCoordinate(coodindex++, new Point3f(x, y, z2));
    			la.setCoordinateIndex(lineindex++, coodindex - 2);
    			la.setCoordinateIndex(lineindex++, coodindex - 1);
    		}
    	}
		
		return(la);
	} // end Line()
}
