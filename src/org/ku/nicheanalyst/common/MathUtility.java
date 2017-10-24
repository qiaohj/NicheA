/******************************************************************************
 * Huijie Qiao
 *
 * Project:  MMWeb
 * Purpose:  
 * Created date: Sep 29, 2012 11:47:28 AM
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


package org.ku.nicheanalyst.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

/**
 * @author Huijie Qiao
 *
 */
public class MathUtility {
	public static double Min (ArrayList<Double> values){
		double v = Double.MAX_VALUE;
		for (double value : values){
			v = Math.min(value, v);
		}
		return v;
	}
	public static double Max (ArrayList<Double> values){
		double v = -1 * Double.MAX_VALUE;
		for (double value : values){
			v = Math.max(value, v);
		}
		return v;
	}
	public static ArrayList<Double> CreateDoubleList(double ... values)
	{
	    ArrayList<Double> results = new ArrayList<Double>();
	 
	    for (double d : values)
		results.add(d);
	    return results;
	}
	public static double Median(ArrayList<Double> values)
	{
	    Collections.sort(values);
	 
	    if (values.size() % 2 == 1)
		return values.get((values.size()+1)/2-1);
	    else
	    {
		double lower = values.get(values.size()/2-1);
		double upper = values.get(values.size()/2);
	 
		return (lower + upper) / 2.0;
	    }	
	}
	public static double Quartiles(double[] initvalues, int position)
	{
		DescriptiveStatistics stat = new DescriptiveStatistics(initvalues);
		return stat.getPercentile(position);
	}
	 
	public static ArrayList<Double> GetValuesGreaterThan(ArrayList<Double> values, double limit, boolean orEqualTo)
	{
	    ArrayList<Double> modValues = new ArrayList<Double>();
	 
	    for (double value : values)
	        if (value > limit || (value == limit && orEqualTo))
	            modValues.add(value);
	 
	    return modValues;
	}
	 
	public static ArrayList<Double> GetValuesLessThan(ArrayList<Double> values, double limit, boolean orEqualTo)
	{
	    ArrayList<Double> modValues = new ArrayList<Double>();
	 
	    for (double value : values)
	        if (value < limit || (value == limit && orEqualTo))
	            modValues.add(value);
	 
	    return modValues;
	}
	/**
	 * @param thresholds
	 * @param f
	 * @return
	 */
	public static double Average(double[] values, float f) {
		Arrays.sort(values);
		double summary = 0;
		int i = 0;
		for (i=0;i<=(float)values.length * f; i++){
			summary += values[i];
		}
		return summary / (float)i;
	}
}
