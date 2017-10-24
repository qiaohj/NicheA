/******************************************************************************
 * Huijie Qiao
 *
 * Project:  Niche Analyst
 * Purpose:  
 * Created date: Aug 12, 2012 6:23:38 PM
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


package org.ku.nicheanalyst.dataset;

import javax.vecmath.Color3f;

import org.ku.nicheanalyst.common.CommonFun;
import org.ku.nicheanalyst.exceptions.ErrorSDSErrorException;

/**
 * @author Huijie Qiao
 *
 */
public class SpeciesData {
	private int x;
	private int y;
	private double longitude;
	private double latitude;
	private double[] values;
	private Color3f color;
	public SpeciesData(int x, int y, double longitude, double latitude, double[] values, Color3f color){
		this.x = x;
		this.y = y;
		this.longitude = longitude;
		this.latitude = latitude;
		this.values = values;
		this.color = color;
	}
	public SpeciesData(String xy, String ll, String value, Color3f color) throws ErrorSDSErrorException{
		String[] xys = xy.split(",");
		if (xys.length>=2){
			if (CommonFun.isInteger(xys[0])&&CommonFun.isInteger(xys[1])){
				x = Integer.valueOf(xys[0]).intValue();
				y = Integer.valueOf(xys[1]).intValue();
			}else{
				throw new ErrorSDSErrorException();
			}
		}else{
			throw new ErrorSDSErrorException();
		}
		
		String[] lls = ll.split(",");
		if (lls.length>=2){
			if (CommonFun.isDouble(lls[0])&&CommonFun.isDouble(lls[1])){
				longitude = Double.valueOf(lls[0]).doubleValue();
				latitude = Double.valueOf(lls[1]).doubleValue();
			}else{
				throw new ErrorSDSErrorException();
			}
		}else{
			throw new ErrorSDSErrorException();
		}
		String[] values = value.split(",");
		if (values.length>=3){
			for (String v : values){
				if (!CommonFun.isDouble(v)){
					throw new ErrorSDSErrorException();
				}
			}
			this.values = new double[values.length];
			for (int i=0;i<values.length;i++){
				this.values[i] = Double.valueOf(values[i]).doubleValue();
			}
			
		}else{
			throw new ErrorSDSErrorException();
		}
		this.color = color;
	}
	public int getX() {
		return x;
	}
	public int getY() {
		return y;
	}
	public double getLongitude() {
		return longitude;
	}
	public double getLatitude() {
		return latitude;
	}
	public double[] getValues() {
		return values;
	}
	public Color3f getColor() {
		return color;
	}
	public void setColor(Color3f color) {
		this.color = color;
		
	}
	
}
