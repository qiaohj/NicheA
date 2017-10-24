/******************************************************************************
 * Huijie Qiao
 *
 * Project:  NicheA
 * Purpose:  
 * Created date: Jan 31, 2013 1:31:19 PM
 * Author:   Huijie Qiao
 *
 ******************************************************************************
 * Copyright (c) 2013, Huijie Qiao
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


package org.ku.nicheanalyst.ui.display.worker;

import java.io.FileNotFoundException;

/**
 * @author Huijie Qiao
 *
 */
public class MultiVarObject {
	
	private double[] values;
	private double[] ll;
	private double precision;
	public MultiVarObject(double[] values, double[] ll, double precision) throws FileNotFoundException{
		this.values = values;
		this.ll = ll;
		this.precision = precision;
	}

	public double[] getValues() {
		return values;
	}

	public double[] getLl() {
		return ll;
	}
	public String toString(){
		String result = "";
		for (double value : values){
			result += (int)Math.round(value * (1f/precision)) + ",";
		}
		result = result.substring(0, result.length()-1);
		return String.format("%f,%f,%s%n", ll[0], ll[1], result);
	}
}
