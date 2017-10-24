/******************************************************************************
 * Huijie Qiao
 *
 * Project:  NicheA
 * Purpose:  
 * Created date: Oct 7, 2012 3:28:19 PM
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


package org.ku.geotest;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.junit.Test;
import org.ku.nicheanalyst.common.CommonFun;
import org.ku.nicheanalyst.common.Const;
import org.ku.nicheanalyst.maps.controllers.GeoTiffController;
import org.ku.nicheanalyst.maps.objects.GeoTiffObject;

/**
 * @author Huijie Qiao
 *
 */
public class GEOTest {
	@Test
	public void bin() throws FileNotFoundException{
		File folder = new File("/Users/huijieqiao/Dropbox/GISLayers/Bioclim/cutted_10m_tiff/sd/split");
		for (File f : folder.listFiles()){
			if (f.getAbsolutePath().endsWith(".tiff")){
				GeoTiffObject geo = new GeoTiffObject(f.getAbsolutePath());
				double[] values = geo.getValueArray();
				for (int i=0;i<values.length;i++){
					if (!CommonFun.equal(values[i], geo.getNoData(), 1000)){
						values[i] = 100;
					}
				}
				GeoTiffController.createTiff(folder.getAbsolutePath() + "/bin/" + f.getName(), 
						geo.getXSize(), geo.getYSize(), 
						geo.getDataset().GetGeoTransform(), values, geo.getNoData(), geo.getDataType(), geo.getDataset().GetProjection());
			}
			
		}
	}
	@Test
	//去掉图层的10%和90%数据
	public void handleEV() throws FileNotFoundException{
		GeoTiffObject geo = new GeoTiffObject("/Users/huijieqiao/Dropbox/GISLayers/Bioclim/bio_10m_tiff/bio12.tiff");
		double[] values = geo.getValueArray();
		int valuecount = 0;
        for (int i=0;i<values.length;i++){
        	if (!CommonFun.equal(values[i], Const.NoData, 1000)){
        		valuecount++;
        	}
        }
        double[] newvalue = new double[valuecount];
        valuecount = 0;
        for (int i=0;i<values.length;i++){
        	if (!CommonFun.equal(values[i], Const.NoData, 1000)){
        		newvalue[valuecount] = values[i];
        		valuecount++;
        	}
        }
        DescriptiveStatistics stat = new DescriptiveStatistics(newvalue);
        double v01 = stat.getPercentile(1);
        double v99 = stat.getPercentile(99);
        System.out.println(String.format("P1:%f   P99:%f", v01, v99));
        for (int i=0;i<values.length;i++){
        	if (!CommonFun.equal(values[i], Const.NoData, 1000)){
        		if ((values[i]<v01)||(values[i]>v99)){
        			values[i] = Const.NoData;
        		}
        	}
        }
        GeoTiffController.createTiff("/Users/huijieqiao/Dropbox/GISLayers/Bioclim/cutted_10m_tiff/bio12.tiff", 
        		geo.getXSize(), geo.getYSize(), geo.getDataset().GetGeoTransform(), values, geo.getNoData(), geo.getDataType(), geo.getDataset().GetProjection());
	}
	@Test
	public void geotest() throws FileNotFoundException{
		GeoTiffObject geo = new GeoTiffObject("/Users/huijieqiao/Dropbox/Applications/NicheA/Jar/nichea_temp/present.tiff");
		double[] ll = new double[]{72.750000,47.750000};
		System.out.println(String.format("%f,%f", ll[0], ll[1]));
		int[] xy = CommonFun.LLToPosition(geo.getDataset().GetGeoTransform(), ll);
		System.out.println(String.format("%d,%d", xy[0], xy[1]));
		ll = CommonFun.PositionToLL(geo.getDataset().GetGeoTransform(), xy);
		System.out.println(String.format("%f,%f", ll[0], ll[1]));
		xy = CommonFun.LLToPosition(geo.getDataset().GetGeoTransform(), ll);
		System.out.println(String.format("%d,%d", xy[0], xy[1]));
	}
	@Test
	public void readData() throws IOException{
		GeoTiffObject geo = new GeoTiffObject("/Users/huijieqiao/Dropbox/Experiments/NicheOverlap_Procapra/Przewalski_past_Last_inter_glacial_avg.tif");
		ArrayList<String> llstrs = CommonFun.readFromFile("/Users/huijieqiao/Dropbox/Experiments/NicheOverlap_Procapra/Przewalski.csv");
		for (String llstr : llstrs){
			String[] item = llstr.split(",");
			if (item.length==2){
				if (CommonFun.isDouble(item[0])&&CommonFun.isDouble(item[1])){
					double[] ll = new double[]{Double.valueOf(item[0]).doubleValue(), Double.valueOf(item[1]).doubleValue()};
					System.out.println(String.format("%f,%f,%f", ll[0], ll[1], geo.readByLL(ll[0], ll[1])));
				}
			}
		}
	}
}
