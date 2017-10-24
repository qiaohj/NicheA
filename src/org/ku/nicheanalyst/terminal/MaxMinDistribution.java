/******************************************************************************
 * Huijie Qiao
 *
 * Project:  NicheA
 * Purpose:  
 * Created date: Jan 4, 2013 1:59:22 PM
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


package org.ku.nicheanalyst.terminal;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.gdal.gdal.gdal;
import org.ku.nicheanalyst.common.CommonFun;
import org.ku.nicheanalyst.common.Const;
import org.ku.nicheanalyst.maps.objects.GeoTiffObject;

/**
 * @author Huijie Qiao
 *
 */
public class MaxMinDistribution {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		if (args.length<3){
			System.out.println("Please input points file, TIFF Folder, and result files.");
			System.exit(0);
		}
		gdal.AllRegister();
		File[] tiffs = new File(args[1]).listFiles();
		ArrayList<String> pp = CommonFun.readFromFile(args[0]);
		StringBuilder sb = new StringBuilder();
		HashMap<String, String> maxtiff = new HashMap<String, String>();
		HashMap<String, String> mintiff = new HashMap<String, String>();
		HashMap<String, Double> min = new HashMap<String, Double>();
		HashMap<String, Double> max = new HashMap<String, Double>();
		sb.append("Long,Lat,Max,MaxFile,Min,MinFile" + Const.LineBreak);
		for (String p : pp){
			System.out.println("Calculating " + p);
			String[] ll = p.replace("\t", ",").split(",");
			if ((CommonFun.isDouble(ll[0]))&&(CommonFun.isDouble(ll[1]))){
				maxtiff.put(p, "");
				mintiff.put(p, "");
				min.put(p, Double.MAX_VALUE);
				max.put(p, -1 * Double.MAX_VALUE);
			}
		}
		int i=0;
		for (File tiff : tiffs){
			
			if (tiff.getAbsolutePath().toLowerCase().endsWith(".tif")||tiff.getAbsolutePath().toLowerCase().endsWith(".tiff")){
				i++;
				System.out.println(i + "/" + tiffs.length + ":" + tiff.getAbsolutePath());
				GeoTiffObject geo = new GeoTiffObject(tiff.getAbsolutePath());
				for (String key : max.keySet()){
					String[] ll = key.replace("\t", ",").split(",");
					double value = geo.readByLL(Double.valueOf(ll[0]), Double.valueOf(ll[1]));
					if (value>max.get(key)){
						max.put(key, value);
						maxtiff.put(key, tiff.getAbsolutePath());
					}
					if (value<min.get(key)){
						min.put(key, value);
						mintiff.put(key, tiff.getAbsolutePath());
					}
				}
			}
		}
		for (String key : pp){
			sb.append(String.format("%s,%f,%s,%f,%s%n", key.replace("\t", ","), max.get(key), maxtiff.get(key), min.get(key), mintiff.get(key)));
		}
		CommonFun.writeFile(sb.toString(), args[2]);

	}

}
