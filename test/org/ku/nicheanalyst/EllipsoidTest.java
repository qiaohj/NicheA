/******************************************************************************
 * Huijie Qiao
 *
 * Project:  Niche Analyst
 * Purpose:  
 * Created date: Jun 13, 2012 9:46:45 PM
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


package org.ku.nicheanalyst;

import java.io.IOException;

import javax.vecmath.Point3f;

import org.junit.Test;
import org.ku.nicheanalyst.maps.controllers.GeoTiffController;
import org.ku.nicheanalyst.ui.display.component.j3d.Color3D;
import org.ku.nicheanalyst.ui.display.component.j3d.Ellipsoid;

/**
 * @author Huijie Qiao
 *
 */
public class EllipsoidTest {
	@Test
	public void test(){
		Ellipsoid e = new Ellipsoid(15f, 0.5f, 0.3f, 0.1f, 20, 20, 1, 0f, 0f, 0f, .3f, .4f, .1f, Color3D.black, false, true);
		Point3f p2 = e.getPoint(0.4f, 0.4f, 0.4f, true);
		System.out.println(String.format("x:%f, y:%f, z:%f", p2.x, p2.y, p2.z));
		p2 = e.getPoint(p2.x, p2.y, p2.z, false);
		System.out.println(String.format("x:%f, y:%f, z:%f", p2.x, p2.y, p2.z));
	}
	@Test
	public void testresize() throws IOException, InterruptedException{
		GeoTiffController.resizePNG("/Users/huijieqiao/temp/vp/vp1/present.png", "/Users/huijieqiao/temp/vp/vp1/present_1000.png", 1000);
	}
}
