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
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import javax.swing.SwingWorker;

import org.gdal.gdalconst.gdalconst;
import org.ku.nicheanalyst.algorithms.MinimumVolumeEllipsoidResult;
import org.ku.nicheanalyst.common.CommonFun;
import org.ku.nicheanalyst.common.Const;
import org.ku.nicheanalyst.common.Message;
import org.ku.nicheanalyst.dataset.SpeciesData;
import org.ku.nicheanalyst.dataset.SpeciesDataset;
import org.ku.nicheanalyst.maps.controllers.GeoTiffController;
import org.ku.nicheanalyst.maps.objects.GeoTiffObject;
import org.ku.nicheanalyst.ui.display.Displayer;

import Jama.Matrix;

import com.jogamp.newt.Display;

/**
 * @author Huijie Qiao
 *
 */
public class ExportToENMWorker extends SwingWorker<Void, Void> {
	private String enm;
	private String species_label;
	private SpeciesDataset vp;
	private int repeat;
	private int number;
	private double beta;
	private double alpha;
	private String target;
	private String method;
	private String backgroundTiff;
	private Displayer theApp;
	public ExportToENMWorker(String enm, String species_label, SpeciesDataset vp, int repeat,
			int number, double beta, double alpha, String target, String method, 
			String backgroundTiff, Displayer theApp){
		this.enm = enm;
		this.species_label = species_label;
		this.vp = vp;
		this.repeat = repeat;
		this.number = number;
		this.target = target;
		this.alpha = alpha;
		this.beta = beta;
		this.method = method;
		this.backgroundTiff = backgroundTiff;
		this.theApp = theApp;
	}
	
	@Override
	protected Void doInBackground() throws Exception {
		setProgress(0);
		
		
		if (enm.equals(Message.getString("openModeller"))||
				enm.equals(Message.getString("Maxent"))||
				enm.equals(Message.getString("BIOMOD2"))||
				enm.equals(Message.getString("dismo"))||
				enm.equals(Message.getString("mkde"))){
			
			
			
			//output the background
			GeoTiffObject sample = new GeoTiffObject(this.backgroundTiff);
			double[] probabilities = new double[sample.getXSize() * sample.getYSize()];
			double[] log_probabilities = null;
			if (this.method.equalsIgnoreCase(Message.getString("probability"))){
				log_probabilities = new double[sample.getXSize() * sample.getYSize()];
			}
			
			if (beta>0){
				probabilities = getProbability(sample, vp.getMve(), vp.getVs(), vp.getVs());
			}else{
				for (int i=0;i<probabilities.length;i++){
					probabilities[i] = 1;
				}
			}
					
			
			for (int i=0;i<probabilities.length;i++){
				if (!CommonFun.equal(probabilities[i], sample.getNoData(), 1000)){
					if (probabilities[i]<0){
						probabilities[i] = 0;
					}else{
						probabilities[i] = probabilities[i];
					}
				}
				if (this.method.equalsIgnoreCase(Message.getString("probability"))){
					if (CommonFun.equal(probabilities[i], sample.getNoData(), 1000)){
						log_probabilities[i] = sample.getNoData();
					}else{
						double v = 1d/(1d + Math.exp((probabilities[i] - beta)/alpha));
						log_probabilities[i] = v;
					}
				}
			}
			GeoTiffController.createTiff(target+"/probability.tif", sample.getXSize(), sample.getYSize(), sample.getDataset().GetGeoTransform(),
					probabilities, sample.getNoData(), gdalconst.GDT_Float32, sample.getDataset().GetProjection());
			if (this.method.equalsIgnoreCase(Message.getString("probability"))){
				GeoTiffController.createTiff(target+"/logistic.tif", sample.getXSize(), sample.getYSize(), sample.getDataset().GetGeoTransform(),
						log_probabilities, sample.getNoData(), gdalconst.GDT_Float32, sample.getDataset().GetProjection());
			}
			
			double[][] values = new double[3][sample.getXSize() * sample.getYSize()];
			for (int i=0;i<3;i++){
				for (int j=0;j<values[i].length;j++){
					values[i][j] = Const.NoData;
				}
			}
			for (String xy : this.theApp.getBackgroundValues().keySet()){
				SpeciesData vs = this.theApp.getBackgroundValues().get(xy);
				for (int j=0;j<3;j++){
					values[j][vs.getY() * sample.getXSize() + vs.getX()] = Double.valueOf(vs.getValues()[j]);
				}
			}
			for (int i=1;i<=3;i++){
				GeoTiffController.createTiff(String.format("%s/%d.tif", target, i), 
						sample.getXSize(), sample.getYSize(), sample.getDataset().GetGeoTransform(), 
						values[i-1], Const.NoData, gdalconst.GDT_Float32, sample.getDataset().GetProjection());
			}
			if (enm.equals(Message.getString("Maxent"))){
				for (int i=1;i<3;i++){
					GeoTiffController.toAAIGrid(String.format("%s/%d.tif", target, i), String.format("%s/%d.asc", target, i));
				}
				for (int i=1;i<=repeat;i++){
					StringBuilder sb = new StringBuilder();
					int ii = 1;
					ArrayList<String> lls = new ArrayList<String>();
					for (String xystr : vp.getVs().keySet()){
						SpeciesData vsdata = vp.getVs().get(xystr);
						lls.add(String.format("%f,%f", vsdata.getLongitude(), vsdata.getLatitude()));
					}
					int record_count = number;
					int max = lls.size() * 10;
					int indexx = 0;
					while (((ii-1)<record_count)&&(indexx<max)){
						indexx ++;
						if (lls.size()==0){
							break;
						}
						int index = (int) Math.round(Math.random() * lls.size());
						if (index==lls.size()){
							index--;
						}
						String ll = lls.get(index);
						String[] llstr = ll.split(",");
						double[] lld = new double[]{Double.valueOf(llstr[0]), Double.valueOf(llstr[1])};
						int[] xy = CommonFun.LLToPosition(sample.getDataset().GetGeoTransform(), lld);
						double probability = probabilities[xy[1] * sample.getXSize() + xy[0]];
						if (this.method.equalsIgnoreCase(Message.getString("threshold"))){
							if (probability>=beta){
								sb.append(String.format("%s,%s", species_label + "." + i, ll) + Const.LineBreak);
								ii++;
								lls.remove(index);
							}
						}
						if (this.method.equalsIgnoreCase(Message.getString("probability"))){
							double log_probability = log_probabilities[xy[1] * sample.getXSize() + xy[0]];
							double random = Math.random();
							if (log_probability>random){
								sb.append(String.format("%s,%s", species_label + "." + i, ll) + Const.LineBreak);
								ii++;
								lls.remove(index);
							}
						}
						
					}
					CommonFun.writeFile(sb.toString(), target + "/" + i + ".csv");
				}
			}
			if (enm.equals(Message.getString("openModeller"))){
				//output the occurrences
				for (int i=1;i<=repeat;i++){
					StringBuilder sb = new StringBuilder();
					sb.append("#id	label	long	lat	abundance" + Const.LineBreak);
					int ii = 1;
					ArrayList<String> lls = new ArrayList<String>();
					for (String xystr : vp.getVs().keySet()){
						SpeciesData vsdata = vp.getVs().get(xystr);
						lls.add(String.format("%f,%f", vsdata.getLongitude(), vsdata.getLatitude()));
					}
					int record_count = number;
					int max = lls.size() * 10;
					int indexx = 0;
					while (((ii-1)<record_count)&&(indexx<max)){
						indexx++;
						if (lls.size()==0){
							break;
						}
						int index = (int) Math.round(Math.random() * lls.size());
						if (index==lls.size()){
							index--;
						}
						String ll = lls.get(index).replace(",", "\t");
						
						String[] llstr = ll.split("\t");
						double[] lld = new double[]{Double.valueOf(llstr[0]), Double.valueOf(llstr[1])};
						int[] xy = CommonFun.LLToPosition(sample.getDataset().GetGeoTransform(), lld);
						double probability = probabilities[xy[1] * sample.getXSize() + xy[0]];
						if (this.method.equalsIgnoreCase(Message.getString("threshold"))){
							if (probability>=beta){
								sb.append(String.format("%d	%s	%s	1", ii++, species_label + "." + i, ll) + Const.LineBreak);
								lls.remove(index);
								
							}
						}
						if (this.method.equalsIgnoreCase(Message.getString("probability"))){
							double log_probability = log_probabilities[xy[1] * sample.getXSize() + xy[0]];
							double random = Math.random();
							if (log_probability>random){
								sb.append(String.format("%s,%s", species_label + "." + i, ll) + Const.LineBreak);
								ii++;
								lls.remove(index);
							}
						}
						
						
					}
					CommonFun.writeFile(sb.toString(), target + "/" + i + ".txt");
				}
			}
			
			if (enm.equals(Message.getString("BIOMOD2"))){
				//output the occurrences
				for (int i=1;i<=repeat;i++){
					StringBuilder sb = new StringBuilder();
					sb.append("\"\",\"X_WGS84\",\"Y_WGS84\",\"" + species_label + "\"" + Const.LineBreak);
					int ii = 1;
					ArrayList<String> lls = new ArrayList<String>();
					for (String xystr : vp.getVs().keySet()){
						SpeciesData vsdata = vp.getVs().get(xystr);
						lls.add(String.format("%f,%f", vsdata.getLongitude(), vsdata.getLatitude()));
					}
					int record_count = number;
					int max = lls.size() * 10;
					int indexx = 0;
					while (((ii-1)<record_count)&&(indexx<max)){
						indexx++;
						if (lls.size()==0){
							break;
						}
						int index = (int) Math.round(Math.random() * lls.size());
						if (index==lls.size()){
							index--;
						}
						String ll = lls.get(index);
						String[] llstr = ll.split(",");
						double[] lld = new double[]{Double.valueOf(llstr[0]), Double.valueOf(llstr[1])};
						int[] xy = CommonFun.LLToPosition(sample.getDataset().GetGeoTransform(), lld);
						double probability = probabilities[xy[1] * sample.getXSize() + xy[0]];
						if (this.method.equalsIgnoreCase(Message.getString("threshold"))){
							if (probability>=beta){
								sb.append(String.format("\"%d\",%s,1", ii++, ll) + Const.LineBreak);
								lls.remove(index);
							}
						}
						if (this.method.equalsIgnoreCase(Message.getString("probability"))){
							double log_probability = log_probabilities[xy[1] * sample.getXSize() + xy[0]];
							double random = Math.random();
							if (log_probability>random){
								sb.append(String.format("%s,%s", species_label + "." + i, ll) + Const.LineBreak);
								ii++;
								lls.remove(index);
							}
						}
						
						
					}
					CommonFun.writeFile(sb.toString(), target + "/" + i + ".txt");
					
					InputStream rscript = this.getClass().getResourceAsStream("/biomod2.r");
					HashMap<String, String> parameters = new HashMap<String, String>();
					parameters.put("@Target", target.replace("\\", "/"));
					parameters.put("@Filename", i + ".txt");
					parameters.put("@SpeciesName", species_label);
					
					
					HashSet<String> libraries = new HashSet<String>();
					libraries.add("biomod2");

					
					String r_script = CommonFun.RunRScript(rscript, parameters, libraries, false, target + "/rscript." + i + ".r");
					
					
				}
			}
			if (enm.equals(Message.getString("mkde"))){
				//output the occurrences
				for (int i=1;i<=repeat;i++){
					
					StringBuilder sb = new StringBuilder();
					sb.append("X,Y,V1,V2,V3" + Const.LineBreak);
					int ii = 1;
					ArrayList<String> xy = new ArrayList<String>();
					for (String xystr : vp.getVs().keySet()){
						xy.add(xystr);
					}
					int record_count = number;
					
					int max = xy.size() * 10;
					int indexx = 0;
					while (((ii-1)<record_count)&&(indexx<max)){
						indexx++;
						if (xy.size()==0){
							break;
						}
						int index = (int) Math.round(Math.random() * xy.size());
						if (index==xy.size()){
							index--;
						}
						String xystr = xy.get(index);
						
						String[] xys = xystr.split(",");
						int[] xyv = new int[]{Integer.valueOf(xys[0]), Integer.valueOf(xys[1])};
						double probability = probabilities[xyv[1] * sample.getXSize() + xyv[0]];
						if (this.method.equalsIgnoreCase(Message.getString("threshold"))){
							if (probability>=beta){
								SpeciesData vsdata = vp.getVs().get(xystr);
								sb.append(String.format("%f,%f,%f,%f,%f", 
										vsdata.getLongitude(),vsdata.getLatitude(), 
										vsdata.getValues()[0], vsdata.getValues()[1], vsdata.getValues()[2]) + Const.LineBreak);
								ii++;
								xy.remove(index);
							}
						}
						if (this.method.equalsIgnoreCase(Message.getString("probability"))){
							double log_probability = log_probabilities[xyv[1] * sample.getXSize() + xyv[0]];
							double random = Math.random();
							if (log_probability>random){
								SpeciesData vsdata = vp.getVs().get(xystr);
								sb.append(String.format("%f,%f,%f,%f,%f", 
										vsdata.getLongitude(),vsdata.getLatitude(), 
										vsdata.getValues()[0], vsdata.getValues()[1], vsdata.getValues()[2]) + Const.LineBreak);
								ii++;
								xy.remove(index);
							}
						}
						
						
					}
					CommonFun.writeFile(sb.toString(), target + "/" + i + ".txt");
					sb = new StringBuilder();
					sb.append("X,Y,V1,V2,V3" + Const.LineBreak);
					for (String key : this.theApp.getBackgroundValues().keySet()){
						SpeciesData vsdata = this.theApp.getBackgroundValues().get(key);
						sb.append(String.format("%f,%f,%f,%f,%f", 
								vsdata.getLongitude(),vsdata.getLatitude(), 
								vsdata.getValues()[0], vsdata.getValues()[1], vsdata.getValues()[2]) + Const.LineBreak);
					}
					CommonFun.writeFile(sb.toString(), target + "/all.txt");
					
					InputStream rscript = this.getClass().getResourceAsStream("/mkde.r");
					HashMap<String, String> parameters = new HashMap<String, String>();
					parameters.put("@Target", target.replace("\\", "/"));
					parameters.put("@Filename", i + ".txt");
					parameters.put("@Result", "result." + i + ".txt");
					HashSet<String> libraries = new HashSet<String>();
					libraries.add("hypervolume");
					libraries.add("geometry");
					String r_script = CommonFun.RunRScript(rscript, parameters, libraries, false, target + "/rscript." + i + ".r");
					
				}
			}
			
			if (enm.equals(Message.getString("dismo"))){
				//output the occurrences
				for (int i=1;i<=repeat;i++){
					
					StringBuilder sb = new StringBuilder();
					sb.append("X,Y,L,V1,V2,V3" + Const.LineBreak);
					int ii = 1;
					ArrayList<String> xy = new ArrayList<String>();
					for (String xystr : vp.getVs().keySet()){
						xy.add(xystr);
					}
					int record_count = number;
					
					int max = xy.size() * 10;
					int indexx = 0;
					while (((ii-1)<record_count)&&(indexx<max)){
						indexx++;
						if (xy.size()==0){
							break;
						}
						int index = (int) Math.round(Math.random() * xy.size());
						if (index==xy.size()){
							index--;
						}
						String xystr = xy.get(index);
						String[] xys = xystr.split(",");
						int[] xyv = new int[]{Integer.valueOf(xys[0]), Integer.valueOf(xys[1])};
						double probability = probabilities[xyv[1] * sample.getXSize() + xyv[0]];
						if (this.method.equalsIgnoreCase(Message.getString("threshold"))){
							if (probability>=beta){
								SpeciesData vsdata = vp.getVs().get(xystr);
								sb.append(String.format("%f,%f,1,%f,%f,%f", 
										vsdata.getLongitude(),vsdata.getLatitude(), 
										vsdata.getValues()[0], vsdata.getValues()[1], vsdata.getValues()[2]) + Const.LineBreak);
								ii++;
								xy.remove(index);
							}
						}
						
						if (this.method.equalsIgnoreCase(Message.getString("probability"))){
							double log_probability = log_probabilities[xyv[1] * sample.getXSize() + xyv[0]];
							double random = Math.random();
							if (log_probability>random){
								SpeciesData vsdata = vp.getVs().get(xystr);
								sb.append(String.format("%f,%f,1,%f,%f,%f", 
										vsdata.getLongitude(),vsdata.getLatitude(), 
										vsdata.getValues()[0], vsdata.getValues()[1], vsdata.getValues()[2]) + Const.LineBreak);
								ii++;
								xy.remove(index);
							}
						}
					}
					CommonFun.writeFile(sb.toString(), target + "/" + i + ".txt");
					sb = new StringBuilder();
					sb.append("X,Y,L,V1,V2,V3" + Const.LineBreak);
					for (String key : this.theApp.getBackgroundValues().keySet()){
						SpeciesData vsdata = this.theApp.getBackgroundValues().get(key);
						sb.append(String.format("%f,%f,%d,%f,%f,%f", 
								vsdata.getLongitude(),vsdata.getLatitude(), vp.getVs().containsKey(key)?1:0,
								vsdata.getValues()[0], vsdata.getValues()[1], vsdata.getValues()[2]) + Const.LineBreak);
					}
					CommonFun.writeFile(sb.toString(), target + "/all.txt");
					
					InputStream rscript = this.getClass().getResourceAsStream("/dismo.r");
					HashMap<String, String> parameters = new HashMap<String, String>();
					parameters.put("@Target", target.replace("\\", "/"));
					parameters.put("@Filename", i + ".txt");
					parameters.put("@Result", "result." + i + ".txt");
					HashSet<String> libraries = new HashSet<String>();
					libraries.add("dismo");
					libraries.add("glm");
					
					String r_script = CommonFun.RunRScript(rscript, parameters, libraries, false, target + "/rscript." + i + ".r");

				}
			}
			sample.release();
		}
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
		while (occ_pool.size()!=0){
			int v = i;
			if (v>100){
				v = 100;
			}
			setProgress(v);
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


	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public String getHTMLResult() {
		return target + "/result.html";
	}

	
	
}
