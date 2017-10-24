/******************************************************************************
 * Huijie Qiao
 *
 * Project:  Niche Analyst
 * Purpose:  
 * Created date: Aug 12, 2012 3:17:47 PM
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

import java.util.HashMap;
import java.util.TreeMap;

import org.ku.nicheanalyst.algorithms.MinimumVolumeEllipsoidResult;
import org.ku.nicheanalyst.common.CommonFun;

/**
 * @author Huijie Qiao
 *
 */
public class SpeciesDataset {
	private HashMap<String, SpeciesData> vs;
	private boolean is3D;
	private String label;
	private MinimumVolumeEllipsoidResult mve;
	private double hull_volume;
	private double[][] ranges;
	
	public SpeciesDataset(HashMap<String, SpeciesData> vs, boolean is3D, String label){
		this.is3D = is3D;
		this.vs = vs;
		this.label = label;
		this.mve = CommonFun.getMVE(vs, (this.is3D)?3:2);
		this.hull_volume = CommonFun.getHullVolume(vs, (this.is3D)?3:2);
		this.ranges = CommonFun.getRange(vs, (this.is3D)?3:2);
	}
	public HashMap<String, SpeciesData> getVs() {
		return vs;
	}
	public TreeMap<String, String> getInfo(String label){
		TreeMap<String, String> info = new TreeMap<String, String>();
		info.put("1. Name", label);
		info.put("2. Number of points", String.valueOf(vs.size()));
		for (int i=0;i<ranges.length;i++){
			info.put("3. Range of Convex polyhedron, variable " + (i + 1), String.valueOf("[" + ranges[i][0] + "," + ranges[i][1] + "]"));
		}
		info.put("3. Volume of Convex polyhedron", String.valueOf(hull_volume));
		
		info.put("4. Volume of MVE", String.valueOf(mve.getVolume(this.is3D)));
		info.put("4. Eigenmatrix and Centra of MVE", mve.toString());
		info.put("4. Semi-Axis of MVE", String.format("x: %f, y: %f, z: %f", mve.getSemi_axis_a(), mve.getSemi_axis_b(), mve.getSemi_axis_c()));
		return info;
	}
	public boolean isIs3D() {
		return is3D;
	}
	public String getLabel() {
		return label;
	}
	public MinimumVolumeEllipsoidResult getMve() {
		return mve;
	}
	public double getHull_volume() {
		return hull_volume;
	}
	public double[][] getRanges() {
		return ranges;
	}
	public double getMVEVolume(){
		return this.mve.getVolume(is3D);
	}
}
