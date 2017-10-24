/******************************************************************************
 * Huijie Qiao
 *
 * Project:  Niche Analyst
 * Purpose:  
 * Created date: May 6, 2012 10:49:12 PM
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

import java.awt.Font;

import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Color3f;
import javax.vecmath.Vector3f;

import com.sun.j3d.utils.geometry.Text2D;

/**
 * @author Huijie Qiao
 *
 */
public class AxesLabel extends TransformGroup{

	/**
	 * @param text
	 * @param color
	 * @param fontName
	 * @param fontSize
	 * @param fontStyle
	 */
	public AxesLabel(String text, float axisLength, Color3f color) {
		Transform3D t = new Transform3D();
		float x = 0f;
		float y = 0f;
		float z = 0f;
		if (text.equals("X")){
			x = axisLength + .02f * axisLength;
		}
		if (text.equals("Y")){
			y = axisLength + .02f * axisLength;
		}
		if (text.equals("Z")){
			z = axisLength + .02f * axisLength;
		}
		t.set(new Vector3f(x, y, z));
		setTransform(t);
		addChild(new Text2D(text, color, "Serif", 15 * (int)axisLength, Font.BOLD));
	}
	
}
