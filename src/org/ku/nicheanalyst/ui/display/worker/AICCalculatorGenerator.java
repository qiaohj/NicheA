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
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;

import javax.swing.SwingWorker;

import org.gdal.gdal.gdal;
import org.gdal.gdalconst.gdalconst;
import org.ku.nicheanalyst.common.AUCResult;
import org.ku.nicheanalyst.common.CommonFun;
import org.ku.nicheanalyst.common.ConfigInfo;
import org.ku.nicheanalyst.common.Const;
import org.ku.nicheanalyst.common.Message;
import org.ku.nicheanalyst.exceptions.ErrorSDSErrorException;
import org.ku.nicheanalyst.exceptions.ParameterErrorException;
import org.ku.nicheanalyst.maps.controllers.GeoTiffController;
import org.ku.nicheanalyst.maps.objects.GeoTiffObject;
import org.ku.nicheanalyst.ui.display.component.j3d.EllipsoidGroup;
import org.ku.nicheanalyst.ui.display.dialog.HTMLViewer;

import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.PrincipalComponents;
import weka.filters.unsupervised.attribute.Standardize;

/**
 * @author Huijie Qiao
 *
 */
public class AICCalculatorGenerator extends SwingWorker<Void, Void> {
	private String target;
	private File[] enmResults;
	private HashSet<double[]> lls;
	private int k;

	public AICCalculatorGenerator(String target, File[] enmResults, String occ, int k) throws IOException{
		this.target = target;
		this.enmResults = enmResults;
		lls = new HashSet<double[]>();
		ArrayList<String> llstr = CommonFun.readFromFile(occ);
		for (String ll : llstr){
			String[] llsplit = ll.replace("\t", ",").split(",");
			if (llsplit.length==2){
				if (CommonFun.isDouble(llsplit[0])&&(CommonFun.isDouble(llsplit[1]))){
					lls.add(new double[]{Double.valueOf(llsplit[0]), Double.valueOf(llsplit[1])});
				}
			}
			if (llsplit.length>=3){
				if (CommonFun.isDouble(llsplit[1])&&(CommonFun.isDouble(llsplit[2]))){
					lls.add(new double[]{Double.valueOf(llsplit[1]), Double.valueOf(llsplit[2])});
				}
			}
		}
		this.k = k;
	}
	
	@Override
	protected Void doInBackground() throws Exception {
		setProgress(0);
		
		StringBuilder sb = new StringBuilder();
		sb.append("ENMResult,occ_count,K,likelihood,log_likelihood,AIC,AICc,BIC" + Const.LineBreak);
		StringBuilder sb_html = new StringBuilder();
		sb_html.append(Message.getString("html_head"));
		for (int i=0; i<enmResults.length; i++){
			File f = enmResults[i];
			sb_html.append("<h3>ENM result: " + f.getAbsolutePath() + "</h3>");
			// a powerful but slow way
//			EfficientGeoTiffObject enm = new EfficientGeoTiffObject(f.getAbsolutePath());
//			double likelihood = 0;
//			double log_likelihood = 0;
//			int occ_count = 0;
//			double probsum = 0;
//			int block = 1000;
//			for (int x=0;x<=enm.getXSize();x += block){
//				int process = (x * (100/enmResults.length))/ enm.getXSize()  + i * (100/enmResults.length);
//				if (process>100){
//					process = 100;
//				}
//				setProgress(process);
//				
//				for (int y=0;y<=enm.getYSize();y+=block){
//					System.out.println(x + "/" + enm.getXSize() + " " + y + "/" + enm.getYSize());
//					double[] v = enm.readDouble(x, y, block, block);
//					System.out.println(x + "/" + enm.getXSize() + " " + y + "/" + enm.getYSize());
//					for (int ii = 0; ii<v.length; ii++){
//						if (!CommonFun.equal(v[ii], enm.getNoData(), 1000)){
//							if (v[ii]>0){
//								probsum += v[ii];
//							}
//						}
//					}
//				}
//			}
//			for (double[] ll : lls){
//				int[] xy = CommonFun.LLToPosition(enm.getDataset().GetGeoTransform(), new double[]{ll[0], ll[1]});
//				double v = enm.readDouble(xy[0], xy[1], 1, 1)[0];
//				if (!CommonFun.equal(v, enm.getNoData(), 1000)){
//					if (v>0){
//						occ_count++;
//						likelihood += v/probsum;
//						log_likelihood += Math.log(v/probsum);
//					}
//				}
//			}
//			
			GeoTiffObject enm = new GeoTiffObject(f.getAbsolutePath());
			double likelihood = 0;
			double log_likelihood = 0;
			int occ_count = 0;
			double probsum = 0;
			for (int x=0;x<=enm.getXSize();x++){
				int process = (x * (100/enmResults.length))/ enm.getXSize()  + i * (100/enmResults.length);
				if (process>100){
					process = 100;
				}
				setProgress(process);
				for (int y=0;y<=enm.getYSize();y++){
					double v=enm.readByXY(x, y);
					if (!CommonFun.equal(v, enm.getNoData(), 1000)){
						if (v>0){
							probsum += v;
						}
					}
				}
			}
			for (double[] ll : lls){
				double v = enm.readByLL(ll[0], ll[1]);
				if (!CommonFun.equal(v, enm.getNoData(), 1000)){
					if (v>0){
						occ_count++;
						likelihood += v/probsum;
						log_likelihood += Math.log(v/probsum);
					}
				}
			}
			double occ_count_d = Double.valueOf(occ_count);
			double k_d = Double.valueOf(k);
			double AICcscore = (2d * k_d - 2d * log_likelihood) + (2d*(k_d)*(k_d+1d)/(occ_count - k_d - 1d));
			double AICscore = 2d * k_d - 2d * log_likelihood;	
			double BICscore = k_d * Math.log(occ_count_d) - 2d * log_likelihood;
			sb_html.append(String.format("<li>Number of occurrence: %d"
					+ "<li>Number of parameters: %d"
					+ "<li>value of likelihood: %f"
					+ "<li>value of ln(likelihood): %f"
					+ "<li>AIC: %f"
					+ "<li>AICc: %f"
					+ "<li>BIC: %f", occ_count, k, likelihood, log_likelihood, AICscore, AICcscore, BICscore));
			sb.append(String.format("%s,%d,%d,%f,%f,%f,%f,%f%n", 
					f.getAbsolutePath(), occ_count, k, likelihood, log_likelihood, AICscore, AICcscore, BICscore));
			enm.release();
		}
		sb_html.append(Message.getString("html_tail"));
		CommonFun.writeFile(sb_html.toString(), target + "/result.html");
		CommonFun.writeFile(sb.toString(), target + "/result.csv");
		setProgress(100);
		return null;
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
