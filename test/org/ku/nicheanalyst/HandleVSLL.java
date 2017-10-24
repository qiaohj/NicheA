/******************************************************************************
 * Huijie Qiao
 *
 * Project:  NicheA
 * Purpose:  
 * Created date: Oct 12, 2012 1:31:33 PM
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


package org.ku.nicheanalyst;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.gdal.gdal.gdal;
import org.junit.Test;
import org.ku.nicheanalyst.common.CommonFun;
import org.ku.nicheanalyst.maps.controllers.GeoTiffController;
import org.ku.nicheanalyst.maps.objects.GeoTiffObject;

/**
 * @author Huijie Qiao
 *
 */
public class HandleVSLL {
	@Test
	public void handle() throws IOException{
		gdal.AllRegister();
		GeoTiffObject pca1 = new GeoTiffObject("/Users/huijieqiao/Dropbox/GISLayers/virtual_species/pca/1.tiff");
		GeoTiffObject pca2 = new GeoTiffObject("/Users/huijieqiao/Dropbox/GISLayers/virtual_species/pca/2.tiff");
		GeoTiffObject pca3 = new GeoTiffObject("/Users/huijieqiao/Dropbox/GISLayers/virtual_species/pca/3.tiff");
		String folder = "/Users/huijieqiao/Dropbox/Papers/VirtualSpecies/vp";
		ArrayList<File> allLL = getLL(folder);
		
		for (File f :allLL){
			StringBuffer sb_ll = new StringBuffer();
			StringBuffer sb_value = new StringBuffer();
			System.out.println(f.getAbsolutePath());
			ArrayList<String> xys = CommonFun.readFromFile(f.getParent() + "/xy.txt");
			for (String xystr : xys){
				int[] xy = new int[]{Integer.valueOf(xystr.split(",")[0]), Integer.valueOf(xystr.split(",")[1])};
				double[] ll = CommonFun.PositionToLL(pca1.getDataset().GetGeoTransform(), xy);
				sb_ll.append(String.format("%f,%f%n", ll[0], ll[1]));
				sb_value.append(String.format("%f,%f,%f%n", pca1.readByXY(xy[0], xy[1]), pca2.readByXY(xy[0], xy[1]), pca3.readByXY(xy[0], xy[1])));
			}
			CommonFun.writeFile(sb_ll.toString(), f.getAbsolutePath());
			CommonFun.writeFile(sb_value.toString(), f.getParent() + "/value.txt");
		}
	}
	private ArrayList<File> getLL(String folder) {
		ArrayList<File> ll = new ArrayList<File>();
		File fold = new File(folder);
		for (File f : fold.listFiles()){
			if (f.isDirectory()){
				ArrayList<File> lltemp = getLL(f.getAbsolutePath());
				for (File ff : lltemp){
					ll.add(ff);
				}
			}else{
				if (f.getName().equalsIgnoreCase("ll.txt")){
					ll.add(f);
				}
			}
		}
		return ll;
	}
}
