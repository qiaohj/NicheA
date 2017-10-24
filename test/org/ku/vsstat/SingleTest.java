/******************************************************************************
 * Huijie Qiao
 *
 * Project:  NicheA
 * Purpose:  
 * Created date: Sep 25, 2012 8:17:54 PM
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


package org.ku.vsstat;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

import org.gdal.gdal.gdal;
import org.gdal.gdalconst.gdalconst;
import org.junit.Test;
import org.ku.nicheanalyst.common.CommonFun;
import org.ku.nicheanalyst.common.Const;
import org.ku.nicheanalyst.maps.controllers.GeoTiffController;
import org.ku.nicheanalyst.maps.objects.GeoTiffObject;

/**
 * @author Huijie Qiao
 *
 */
public class SingleTest {
	@Test
	public void getValues() throws IOException{
		gdal.AllRegister();
		String csvname = "/Users/huijieqiao/Dropbox/temp/vp-big-4.0.MAXENT_JAVA.csv";
		GeoTiffObject tiff = new GeoTiffObject("/Users/huijieqiao/Dropbox/temp/vp-big-4.0.MAXENT_JAVA.tiff");
		ArrayList<String> record = CommonFun.readFromFile(csvname);
		StringBuilder sb = new StringBuilder();
		for (String s : record){
			String[] ss = s.split(",");
			if (CommonFun.isDouble(ss[1])){
				double v = tiff.readByLL(Double.valueOf(ss[1]), Double.valueOf(ss[2]));
				sb.append(s + "," + String.valueOf(v) + Const.LineBreak);
			}else{
				sb.append(s + "," + "value" + Const.LineBreak);
			}
		}
		CommonFun.writeFile(sb.toString(), "/Users/huijieqiao/Dropbox/temp/vp-big-4.0.MAXENT_JAVA_P.csv");
	}
	@Test
	public void binarizateTiff() throws FileNotFoundException{
		String filename = "/Users/huijieqiao/Dropbox/temp/vp-big-4.0.MAXENT_JAVA.tiff";
		int threshold = 50;
		GeoTiffObject geo = new GeoTiffObject(filename);
		double[] values = geo.getValueArray();
		for (int i=0; i<values.length;i++){
			if (values[i]>=threshold){
				values[i] = 100;
			}else{
				values[i] = 0;
			}
		}
		GeoTiffController.createTiff(filename.replace(".tiff", "") + "_" + threshold + ".tiff", 
				geo.getXSize(), geo.getYSize(), geo.getDataset().GetGeoTransform(), values, geo.getNoData(), 
				gdalconst.GDT_Int32, geo.getDataset().GetProjection());
	}
	@Test
	public void test(){
		HashSet<Integer> a = new HashSet<Integer>();
		for (int i=0;i<Integer.MAX_VALUE;i++){
			if (a.contains(i)){
				System.out.println(i);
				break;
			}
			a.add(i);
		}
	}
	@Test
	public void geotest(){
		gdal.AllRegister();
	}
}
