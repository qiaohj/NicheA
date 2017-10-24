/******************************************************************************
 * Huijie Qiao
 *
 * Project:  NicheA
 * Purpose:  
 * Created date: Jan 12, 2013 2:16:03 PM
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


package org.ku.stattest;

import java.io.File;
import java.io.FileNotFoundException;

import org.apache.commons.math3.distribution.BinomialDistribution;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.distribution.PoissonDistribution;
import org.apache.commons.math3.distribution.UniformRealDistribution;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.stat.descriptive.rank.Median;
import org.apache.commons.math3.stat.descriptive.rank.Percentile;
import org.junit.Test;
import org.ku.nicheanalyst.common.CommonFun;
import org.ku.nicheanalyst.maps.controllers.GeoTiffController;
import org.ku.nicheanalyst.maps.objects.GeoTiffObject;
import org.ku.nicheanalyst.ui.display.worker.MyPrincipalComponents;

import Jama.Matrix;

/**
 * @author Huijie Qiao
 *
 */
public class Stat {
	@Test
	public void testPercentile(){
		double[] initvalues = new double[5];
		initvalues[0] = 1;
		initvalues[1] = 3.5;
		initvalues[2] = 2;
		initvalues[3] = 4;
		initvalues[4] = 5;
		Median a = new Median();
		Percentile p = new Percentile();
		
		DescriptiveStatistics stat = new DescriptiveStatistics(initvalues);
		System.out.println(stat.getMean());
		for (int i=1;i<=20;i++){
			System.out.println((i*5) + ":" + stat.getPercentile(i*5));
			System.out.println((i*5) + ":" + p.evaluate(initvalues, i * 5));
		}
	}
	@Test
	public void testDistribution(){
		UniformRealDistribution uniformreaddistribution = new UniformRealDistribution(0, 1);
		
		for (int i=0;i<10;i++){
			System.out.println(uniformreaddistribution.sample());
		}
		System.out.println("=-----------------");
		
		BinomialDistribution binomialDistribution = new BinomialDistribution(10, 0);
		for (int i=0;i<10;i++){
			System.out.println(binomialDistribution.sample());
		}
		System.out.println("=-----------------");
		NormalDistribution normalDistribution = new NormalDistribution(0, 1);
		for (int i=0;i<10;i++){
			System.out.println(normalDistribution.sample());
		}
		System.out.println("=-----------------");
		PoissonDistribution poissonDistribution = new PoissonDistribution(1);
		for (int i=0;i<10;i++){
			System.out.println(poissonDistribution.sample());
		}
		
	}
	@Test
	public void testPCA(){
		MyPrincipalComponents principalComponents = new MyPrincipalComponents();
		
	}
	@Test
	public void testAdjacencyMatrix(){
		int xsize = 3;
		int ysize = 4;
		int max_path_length = 2;
		double[][] edges = new double[xsize * ysize][xsize * ysize];
		for (int x1=0;x1<xsize;x1++){
			for (int y1=0;y1<ysize;y1++){
				for (int x2=0;x2<xsize;x2++){
					for (int y2=0;y2<ysize;y2++){
						double distance = Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
						int v = (distance>1)?0:1;
						edges[y1 * xsize + x1][y2 * xsize + x2] = v;
					}
				}
			}
		}
		
		Matrix m = new Matrix(edges);
		CommonFun.printMatrix("test initial matrix", m);
		
		m = CommonFun.power(m, max_path_length);
	
		CommonFun.printMatrix("test number of path matrix", m);
	}
	@Test
	public void testPower(){
		int xsize = 3;
		int ysize = 1;
		int max_path_length = 2;
		double[][] edges = new double[1][xsize * ysize];
		for (int x1=0;x1<xsize;x1++){
			edges[0][x1] = 1;
		}
		
		Matrix m = new Matrix(edges);
		CommonFun.printMatrix("test initial matrix", m);
		
		m = CommonFun.power(m, max_path_length);
	
		CommonFun.printMatrix("test number of path matrix", m);
	}
	@Test
	public void mask() throws FileNotFoundException{
		String folder = "/Users/huijieqiao/NicheBreadth/simulations/";
		GeoTiffObject mask_st = new GeoTiffObject(folder + "mask_asia.tif");
		GeoTiffObject mask_target = new GeoTiffObject(folder + "a.tif");
		double[] values = mask_target.getValueArray();
		for (int x = 0; x<mask_target.getXSize(); x++){
			for (int y=0;y<mask_target.getYSize();y++){
				double[] ll = CommonFun.PositionToLL(mask_target.getDataset().GetGeoTransform(), new int[]{x, y});
				double value = mask_st.readByLL(ll[0], ll[1]);
				values[y * mask_target.getXSize() + x] = value;
			}
		}
		GeoTiffController.createTiff(folder + "mask_easten_asia_low.tif", 
				mask_target.getXSize(), mask_target.getYSize(), 
				mask_target.getDataset().GetGeoTransform(),
				values, mask_st.getNoData(),mask_st.getDataType(), mask_target.getDataset().GetProjection());
	}
	
	@Test
	public void mergeLayers() throws FileNotFoundException{
		
		GeoTiffObject masks = new GeoTiffObject("/Users/huijieqiao/Dropbox/Experiments/NicheA_fossorial_nalysis/merged/mask.tif");
		File ff = new File("/Users/huijieqiao/Dropbox/Experiments/NicheA_fossorial_nalysis/M_bin");
		for (File fitem : ff.listFiles()){
			String fname = fitem.getAbsolutePath();
			if (fname.endsWith(".asc")){
				GeoTiffObject layer = new GeoTiffObject(fname);
				double[] values = masks.getValueArray();
				for (int i=0;i<values.length;i++){
					values[i] = -9999;
				}
				for (int x=0;x<layer.getXSize();x++){
					for (int y=0;y<layer.getYSize();y++){
						double[] ll = CommonFun.PositionToLL(layer.getDataset().GetGeoTransform(), new int[]{x, y});
						double value = layer.readByXY(x, y);
						if (!CommonFun.equal(value, -9999, 1000)){
							int[] xy = CommonFun.LLToPosition(masks.getDataset().GetGeoTransform(), ll);
							values[xy[1] * masks.getXSize() + xy[0]] = 1;
						}
					}
				}
				GeoTiffController.createTiff(fname.replace(".asc", ".tif"), 
						masks.getXSize(), masks.getYSize(), masks.getDataset().GetGeoTransform(), values, -9999, masks.getDataType(), masks.getDataset().GetProjection());
			}
		}
		
		
		for (int j=1;j<16;j++){
			if (true){
				continue;
			}
			File f = new File("/Users/huijieqiao/Dropbox/Experiments/NicheA_fossorial_nalysis/annual/bio" + j + ".asc");
			if (!f.exists()){
				continue;
			}
			GeoTiffObject mask = new GeoTiffObject("/Users/huijieqiao/Dropbox/Experiments/NicheA_fossorial_nalysis/merged/mask.tif");
			GeoTiffObject layer1 = new GeoTiffObject("/Users/huijieqiao/Dropbox/Experiments/NicheA_fossorial_nalysis/annual/bio" + j + ".asc");
			GeoTiffObject layer2 = new GeoTiffObject("/Users/huijieqiao/Dropbox/Experiments/NicheA_fossorial_nalysis/estacional/bio" + j + ".asc");
			double[] values = mask.getValueArray();
			for (int i=0;i<values.length;i++){
				values[i] = -9999;
			}
			for (int x=0;x<layer1.getXSize();x++){
				for (int y=0;y<layer1.getYSize();y++){
					double[] ll1 = CommonFun.PositionToLL(layer1.getDataset().GetGeoTransform(), new int[]{x, y});
					double[] ll2 = CommonFun.PositionToLL(layer2.getDataset().GetGeoTransform(), new int[]{x, y});
					double value1 = layer1.readByXY(x, y);
					double value2 = layer2.readByXY(x, y);
					if (!CommonFun.equal(value1, -9999, 1000)){
						int[] xy1 = CommonFun.LLToPosition(mask.getDataset().GetGeoTransform(), ll1);
						values[xy1[1] * mask.getXSize() + xy1[0]] = value1;
					}
					if (!CommonFun.equal(value2, -9999, 1000)){
						int[] xy2 = CommonFun.LLToPosition(mask.getDataset().GetGeoTransform(), ll2);
						values[xy2[1] * mask.getXSize() + xy2[0]] = value2;
					}	
				}
			}
			GeoTiffController.createTiff("/Users/huijieqiao/Dropbox/Experiments/NicheA_fossorial_nalysis/merged/bio" + j + ".tif", 
					mask.getXSize(), mask.getYSize(), mask.getDataset().GetGeoTransform(), values, -9999, mask.getDataType(), mask.getDataset().GetProjection());
		}
		
	}
}
