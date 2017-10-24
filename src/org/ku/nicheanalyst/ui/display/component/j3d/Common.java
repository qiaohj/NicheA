/******************************************************************************
 * Huijie Qiao
 *
 * Project:  Niche Analyst
 * Purpose:  
 * Created date: May 4, 2012 3:52:32 PM
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
import javax.media.j3d.PolygonAttributes;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;

/**
 * @author Huijie Qiao
 *
 */
public class Common {
	public static Appearance createAppearance(boolean showWireframe, Color3f color){
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
//		    app.setMaterial(new Material(color, color, color, color, 1f));
		    return app;
	}

	/**
	 * @param processingPoint
	 * @return
	 */
	public static String pointToString(Point3d point) {
		return String.format("x:%f y:%f z:%f", point.x, point.y, point.z);
	}

	
}
