/******************************************************************************
 * Huijie Qiao
 *
 * Project:  NicheA
 * Purpose:  
 * Created date: Dec 20, 2012 1:45:29 PM
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


package org.ku.niche.transferability;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.ku.nicheanalyst.common.CommonFun;
import org.ku.nicheanalyst.maps.objects.GeoTiffObject;

/**
 * @author Huijie Qiao
 *
 */
public class ResultObject {
	private HashMap<String, Boolean> full;
	private HashMap<String, Boolean> self;
	private HashMap<String, Boolean> opposite;
	private HashMap<Integer, ArrayList<String>> experiments;
	public ResultObject(){
		full = new HashMap<String, Boolean>();
		self = new HashMap<String, Boolean>();
		opposite = new HashMap<String, Boolean>();
		experiments = new HashMap<Integer, ArrayList<String>>();
		
	}
	public HashMap<String, Boolean> getFull() {
		return full;
	}
	public void setFull(String fullFilename, String bg) throws FileNotFoundException {
		this.full = addData(fullFilename, bg);
	}
	/**
	 * @param fullFilename
	 * @throws FileNotFoundException 
	 */
	private HashMap<String, Boolean> addData(String filename, String bg) throws FileNotFoundException {
		HashMap<String, Boolean> data = new HashMap<String, Boolean>();
		GeoTiffObject geo = new GeoTiffObject(filename);
		GeoTiffObject bggeo = null;
		if (bg!=null){
			bggeo = new GeoTiffObject(bg);
		}
		for (int x=0;x<geo.getXSize();x++){
			for (int y=0;y<geo.getYSize();y++){
				int value = (int) geo.readByXY(x, y);
				int bgvalue = 1;
				if (bggeo!=null){
					bgvalue = (int) bggeo.readByXY(x, y);
				}
				if (bgvalue!=0){
					if (value==255){
						data.put(String.format("%d,%d", x, y), true);
					}
					if (value==0){
						data.put(String.format("%d,%d", x, y), false);
					}
				}
			}
		}
		return data;
	}
	public HashMap<String, Boolean> getSelf() {
		return self;
	}
	public void setSelf(String selfFilename) throws FileNotFoundException {
		this.self = addData(selfFilename, null);
	}
	public HashMap<String, Boolean> getOpposite() {
		return opposite;
	}
	public void setOpposite(String oppositeFilename) throws FileNotFoundException {
		this.opposite = addData(oppositeFilename, null);
	}
	public HashMap<Integer, ArrayList<String>> getExperiments() {
		return experiments;
	}
	public void setExperiments(int experimentNumber, String experimentFilename) throws IOException {
		ArrayList<String> content = CommonFun.readFromFile(experimentFilename);
		for (int i=content.size()-1;i>=0;i--){
			String[] xy  = content.get(i).split(",");
			if (!CommonFun.isInteger(xy[0])){
				content.remove(i);
			}
		}
		this.experiments.put(experimentNumber, content);
	}
	
	
}
