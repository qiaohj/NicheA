/******************************************************************************
 * Huijie Qiao
 *
 * Project:  Niche Analyst
 * Purpose:  
 * Created date: May 28, 2012 6:41:37 PM
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

import java.util.HashMap;

import javax.media.j3d.Appearance;
import javax.media.j3d.ColoringAttributes;
import javax.media.j3d.Geometry;
import javax.media.j3d.GeometryArray;
import javax.media.j3d.IndexedLineArray;
import javax.media.j3d.Shape3D;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;

/**
 * @author Huijie Qiao
 *
 */
public class ConvesHullFace extends Shape3D{
	private boolean is3D;
	public ConvesHullFace(float maxLength, int[][] faceIndices, quickhull3d.Point3d[] vertices, Color3f color, boolean is3D){
		this.is3D = is3D;
		this.setGeometry(createGeometry(maxLength, faceIndices, vertices));
		
		Appearance app = new Appearance();
	    ColoringAttributes ca = new ColoringAttributes(color,
	        ColoringAttributes.SHADE_FLAT);
	    app.setColoringAttributes(ca);
	    setAppearance(app);
	}
	private Geometry createGeometry(float maxLength, int[][] faceIndices, quickhull3d.Point3d[] vertices) {
		int indexnum = faceIndices.length * 2;
		if (is3D){
			for (int i = 0; i < faceIndices.length; i++) {
		    	for (int j=0;j<faceIndices[i].length;j++){
		    		indexnum = indexnum + 2;
		    	}
			}
//			indexnum = faceIndices.length * faceIndices[0].length * 2;
		}
	    IndexedLineArray axisLines = new IndexedLineArray(vertices.length,
	        GeometryArray.COORDINATES, indexnum);

	    HashMap<Integer, quickhull3d.Point3d> points = new HashMap<Integer, quickhull3d.Point3d>();
	    for (int i=0;i<vertices.length;i++){
	    	quickhull3d.Point3d p = vertices[i];
	    	points.put(i, p);
			axisLines.setCoordinate(i, new Point3d(p.x/maxLength, p.y/maxLength, p.z/maxLength));
	    }
	    
		if (is3D){
			int indexcount = 0;
		    for (int i = 0; i < faceIndices.length; i++) {
//		    	Point3f p1 = new Point3f();
//		    	Point3f p2 = new Point3f();
//		    	Point3f p3 = new Point3f();
//		    	Point3f p4 = new Point3f();
//		    	axisLines.getCoordinate(faceIndices[i][0], p1);
//		    	axisLines.getCoordinate(faceIndices[i][1], p2);
//		    	axisLines.getCoordinate(faceIndices[i][2], p3);
//		    	axisLines.getCoordinate(faceIndices[i][3], p4);
//		    	System.out.print(String.format("%f,%f,%f - %f,%f,%f - %f,%f,%f - %f,%f,%f %n", 
//		    			p1.x, p1.y, p1.z, p2.x, p2.y, p2.z, p3.x, p3.y, p3.z, p4.x, p4.y, p4.z));
		    	for (int j=0;j<faceIndices[i].length;j++){
		    		if (faceIndices[i].length>4){
//		    			System.out.println(faceIndices[i].length);
		    		}
		    		if (j<(faceIndices[i].length-1)){
						axisLines.setCoordinateIndex(indexcount, faceIndices[i][j]);
						indexcount++;
						axisLines.setCoordinateIndex(indexcount, faceIndices[i][j + 1]);
						indexcount++;
		    		}else{
		    			axisLines.setCoordinateIndex(indexcount, faceIndices[i][j]);
						indexcount++;
						axisLines.setCoordinateIndex(indexcount, faceIndices[i][0]);
						indexcount++;
		    		}
		    	}
				
//				System.out.println(String.format("%d, %d, %d, %d %n", 
//						faceIndices[i][0], faceIndices[i][1], faceIndices[i][2], faceIndices[i][3]));
//				if (i==0){
//					break;
//				}
			}
	    }else{
	    	int indexcount = 0;
	    	for (int i = 0; i < faceIndices.length; i++) {
				axisLines.setCoordinateIndex(indexcount, faceIndices[i][0]);
				indexcount++;
				if (i==faceIndices.length-1){
					axisLines.setCoordinateIndex(indexcount, faceIndices[0][0]);
				}else{
					axisLines.setCoordinateIndex(indexcount, faceIndices[i + 1][0]);
				}
				indexcount++;
				
			}
	    	
	    }
	    return axisLines;

	  } // end of Axis createGeometry()
}
