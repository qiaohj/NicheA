/******************************************************************************
 * Huijie Qiao
 *
 * Project:  NicheA
 * Purpose:  
 * Created date: Sep 13, 2012 9:02:46 PM
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


package org.ku.nicheanalyst.terminal;

import java.io.IOException;
import java.util.ArrayList;

import org.gdal.gdal.gdal;
import org.gdal.gdalconst.gdalconst;
import org.ku.nicheanalyst.common.CommonFun;
import org.ku.nicheanalyst.common.Const;
import org.ku.nicheanalyst.maps.controllers.GeoTiffController;
import org.ku.nicheanalyst.maps.objects.GeoTiffObject;

/**
 * @author Huijie Qiao
 *
 */
public class MergeShapeFile {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		gdal.AllRegister();
		ArrayList<String> pvalues = CommonFun.readFromFile(args[0]);
		GeoTiffObject sample = null;
		int[] values = null;
		String output = args[2];
		double[] geoTransform = null;
		double nodata = Double.valueOf(args[3]);
		for (String value : pvalues){
			System.out.println(value);
			String[] v = value.split(",");
			int v1 = Integer.valueOf(v[0]);

			String file = args[1] + "/" + v[1] + ".tiff";
			sample = new GeoTiffObject(file);
			
			
			if (values==null){
				values = new int[sample.getXSize() * sample.getYSize()];
				geoTransform = sample.getDataset().GetGeoTransform();
				for (int x=0;x<sample.getXSize();x++){
					for (int y=0;y<sample.getYSize();y++){
						values[y * sample.getXSize() + x] = Const.NoData;
					}
				}
			}
			
			for (int x=0;x<sample.getXSize();x++){
				for (int y=0;y<sample.getYSize();y++){
					if (!CommonFun.equal(nodata, sample.readByXY(x, y), 1000)){
						values[y * sample.getXSize() + x] = v1;
					}
				}
			}
		}
		GeoTiffController.createTiff(output, 
				sample.getXSize(), sample.getYSize(), geoTransform, values, Const.NoData, gdalconst.GDT_Int16, sample.getDataset().GetProjection());

	}

}
