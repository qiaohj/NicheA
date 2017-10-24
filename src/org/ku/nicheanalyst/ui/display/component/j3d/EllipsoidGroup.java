/******************************************************************************
 * Huijie Qiao
 *
 * Project:  Niche Analyst
 * Purpose:  
 * Created date: May 3, 2012 8:07:40 PM
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
import javax.media.j3d.PolygonAttributes;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Point3f;


/**
 * @author Huijie Qiao
 *
 */
public class EllipsoidGroup extends TransformGroup{
	private Ellipsoid ellipsoid;
	private HashMap<Integer, Point3f> vertexes;
	public EllipsoidGroup(float maxLength, float a, float b, float c, int u_resolution, int v_resolution, int type,
			float offset_x, float offset_y, float offset_z, float rotate_x, float rotate_y, float rotate_z, boolean is3D){
		this.ellipsoid = new Ellipsoid(maxLength, a, b, c, u_resolution, v_resolution, type, 
				offset_x, offset_y, offset_z, rotate_x, rotate_y, rotate_z, Color3D.white, true, is3D);
		addChild(this.ellipsoid);
		this.vertexes = this.ellipsoid.getVertexes();
		for (Integer pointType : vertexes.keySet()){
			if (pointType==EllipsoidVertexType.Centre){
				addChild(new EllipsoidVertex(vertexes.get(pointType), 0.01f, createAppearance(false), Color3D.green, is3D));
			}else{
				addChild(new EllipsoidVertex(vertexes.get(pointType), 0.01f, createAppearance(true), Color3D.red, is3D));
			}
		}
	}
	
	private Appearance createAppearance(boolean showWireframe) {
		Appearance app = new Appearance();
	    PolygonAttributes polyAtt = new PolygonAttributes();
	 
	     if(showWireframe){
	        polyAtt.setPolygonMode(PolygonAttributes.POLYGON_LINE);
	        polyAtt.setCullFace(PolygonAttributes.CULL_NONE);
	     }
	     else{
	        polyAtt.setCullFace(PolygonAttributes.CULL_BACK);
	        polyAtt.setPolygonMode(PolygonAttributes.POLYGON_FILL);
	    }
	    app.setPolygonAttributes(polyAtt); 
	    return app;
	}
	public Ellipsoid getEllipsoid() {
		return ellipsoid;
	}
	
}
