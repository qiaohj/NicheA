/******************************************************************************
 * Huijie Qiao
 *
 * Project:  Niche Analyst
 * Purpose:  
 * Created date: Jun 12, 2012 2:09:06 PM
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


package org.ku.nicheanalyst.ui.display.worker;

import java.awt.Toolkit;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeMap;

import javax.swing.SwingWorker;

import org.gdal.gdalconst.gdalconst;
import org.ku.nicheanalyst.algorithms.MinimumVolumeEllipsoidResult;
import org.ku.nicheanalyst.common.CommonFun;
import org.ku.nicheanalyst.common.Const;
import org.ku.nicheanalyst.dataset.SpeciesData;
import org.ku.nicheanalyst.maps.controllers.GeoTiffController;
import org.ku.nicheanalyst.maps.objects.GeoTiffObject;
import org.ku.nicheanalyst.ui.display.component.ui.IDMenuItem;

import Jama.Matrix;

/**
 * @author Huijie Qiao
 *
 */
public class NicheMapGenerator extends SwingWorker<Void, Void> {
	private HashSet<String> keys;
	private String folder;
	private HashMap<String, SpeciesData> backgroundValues;
	private String backgroundTiff;
	private TreeMap<String, IDMenuItem> virtualspecies;
	public NicheMapGenerator(String folder, HashSet<String> keys, 
			HashMap<String, SpeciesData> backgroundValues, String backgroundTiff, 
			TreeMap<String, IDMenuItem> virtualspecies){
		this.folder = folder;
		this.keys = keys;
		this.backgroundValues = backgroundValues;
		this.backgroundTiff = backgroundTiff;
		this.virtualspecies = virtualspecies;
	}
	
	@Override
	protected Void doInBackground() throws Exception {
		GeoTiffObject sample = new GeoTiffObject(this.backgroundTiff);
		double[] values = sample.getValueArray();
		int j = 1;
		for (String key : keys){
			int s = j * 100 / keys.size();
			if (s>100){
				s = 100;
			}
			setProgress(s);
			values = getProbability(sample, virtualspecies.get(key).getVs().getMve(),
					virtualspecies.get(key).getVs().getVs(), this.backgroundValues);
			String filename = CommonFun.getFileNameWithoutPathAndExtension(key);
			GeoTiffController.createTiff(folder + "/" + filename + ".tiff", sample.getXSize(), sample.getYSize(), 
					sample.getDataset().GetGeoTransform(), values, Const.NoData, gdalconst.GDT_Float32, 
					sample.getDataset().GetProjection());
			j++;
		}
		sample.release();
		setProgress(100);
		return null;
	}
	private double[] getProbability(GeoTiffObject sample, 
			MinimumVolumeEllipsoidResult mve, HashMap<String, SpeciesData> occ_pool_raw,
			HashMap<String, SpeciesData> point_pool) {
		double[] values = sample.getValueArray();
		for (int i=0;i<values.length;i++){
			values[i] = (CommonFun.equal(values[i], sample.getNoData(), 1000))?Const.NoData:-1;
		}
		HashMap<String, SpeciesData> occ_pool = new HashMap<String, SpeciesData>();
		for (String key : occ_pool_raw.keySet()){
			occ_pool.put(key, occ_pool_raw.get(key));
		}
//		point_pool = occ_pool;
		Matrix A = mve.getA();
		Matrix c = mve.getCenter();
		int i = 1;
		int max = occ_pool.size();
		while (occ_pool.size()!=0){
			int v = i * 100/max;
			if (v>100){
				v = 100;
			}
			setProgress(v);
			//System.out.println(i);
			double[][] m = new double[3][3];
			m[0][0] = 100d/(double)i;
			m[1][1] = 100d/(double)i;
			m[2][2] = 100d/(double)i;
			Matrix matrix = new Matrix(m);
			for (SpeciesData data : point_pool.values()){
				if (CommonFun.equal(values[data.getY() * sample.getXSize() + data.getX()], -1, 1000)){
					if (CommonFun.isInEllipsoid(A.times(matrix), c, data.getValues())){
						values[data.getY() * sample.getXSize() + data.getX()] = i;
					}
				}
			}
			HashSet<String> removed_keys = new HashSet<String>();
			for (String occ_key : occ_pool.keySet()){
				SpeciesData data = occ_pool.get(occ_key);
				if (CommonFun.isInEllipsoid(A.times(matrix), c, data.getValues())){
					removed_keys.add(occ_key);
				}
			}
			for (String k : removed_keys){
				occ_pool.remove(k);
			}
			i++;
		}
		for (int x=0; x<values.length; x++){
			if (CommonFun.equal(values[x], Const.NoData, 1000)){
				continue;
			}
			if (values[x]<0){
				continue;
			}
			values[x] = 1 - (values[x] / (double)(i-1));
		}
		return values;
	}
	/**
	 * @param geoTransform
	 * @param xsize
	 * @param ysize
	 * @param getGeoTransform
	 * @param xSize2
	 * @param ySize2
	 * @return
	 */
	private Exception msg;
	@Override
    public void done() {
		Toolkit.getDefaultToolkit().beep();
        try {
            get();
        } catch (Exception e) {
        	e.printStackTrace();
        	msg = e;
            firePropertyChange("done-exception", null, e);
        }
    }
	public Exception getException(){
		return msg;
	}
	
	
}
