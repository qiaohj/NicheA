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
public class PartialROCGenerator extends SwingWorker<Void, Void> {
	private String target;
	private StringBuilder  sb_html;
	private String occ;
	private HashSet<Double> Es;
	private File[] enmResults;
	public PartialROCGenerator(String target, String occ, HashSet<Double> Es, File[] enmResults){
		
		this.occ = occ;
		this.target = target;
		this.Es = Es;
		this.enmResults = enmResults;
	}
	
	@Override
	protected Void doInBackground() throws Exception {
		setProgress(1);
		ArrayList<String> presence = new ArrayList<String>();
		ArrayList<String> lls = CommonFun.readFromFile(occ);
		sb_html = new StringBuilder();
		sb_html.append(Message.getString("html_head"));
		StringBuilder sb_detail = new StringBuilder();
		StringBuilder sb_table = new StringBuilder();
		sb_table.append("<table><tr><th>ENM result</th><th>E</th><th>P-ROC AUC</th><th>Fixed P-ROC AUC</th><th>Ratio</th><th>AUC null</th></tr>");
		int c_step = 1;
		for (File enmFile : this.enmResults){
			int process = (100*c_step) / enmResults.length;
			if (process>100){
				process = 100; 
			}
			setProgress(process);
			for (String ll : lls){
				
				
				
				String[] llsplit = ll.replace("\t", ",").split(",");
				if (llsplit.length==2){
					if (CommonFun.isDouble(llsplit[0])&&(CommonFun.isDouble(llsplit[1]))){
						presence.add(String.format("%s,%s", llsplit[0], llsplit[1]));
					}
				}
				if (llsplit.length>=3){
					if (CommonFun.isDouble(llsplit[1])&&(CommonFun.isDouble(llsplit[2]))){
						presence.add(String.format("%s,%s", llsplit[1], llsplit[2]));
					}
				}
			}
			
			for (Double E : this.Es){
				AUCResult roc = CommonFun.getAUC(enmFile.getAbsolutePath(), 100, presence, E);
				sb_detail.append(roc.save(target + "/" + CommonFun.getFileNameWithoutExtension(enmFile.getName())));
				sb_table.append(String.format(
						"<tr><td>%s</td><td>%f</td><td>%f</td><td>%f</td><td>%f</td><td>%f</td></tr>%n", 
						enmFile.getName(), roc.getE(), roc.getAuc(), roc.getFixedAUC(), roc.getRatio(), roc.getAucNull()));
			}
			c_step++;
		}
		sb_table.append("</table>");
		sb_html.append(sb_table.toString());
		sb_html.append(sb_detail.toString());
		sb_html.append(Message.getString("html_tail"));
		CommonFun.writeFile(sb_html.toString(), target + "/result.html");
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

	public String getHTMLResult() throws IOException {
		String html = target + "/result.html";
		CommonFun.writeFile(sb_html.toString(), html);
		return html;
	}
	
}
