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
import javax.swing.SwingWorker;
import org.ku.nicheanalyst.common.CommonFun;
import org.ku.nicheanalyst.common.Message;
import org.ku.nicheanalyst.maps.controllers.GeoTiffController;

/**
 * @author Huijie Qiao
 *
 */
public class FormatConvertor extends SwingWorker<Void, Void> {
	private File[] Files;
	private String target;
	private String format;
	private boolean is_pass;
	public FormatConvertor(File[] Files, String target, String format){
		this.Files = Files;
		this.target = target;
		this.format = format;
	}
	
	@Override
	protected Void doInBackground() throws Exception {
		setProgress(0);
		int i = 0;
		is_pass = true;
		for (File f : Files){
			i++;
			setProgress((int) (100f * i / Files.length));
			
			if (format.equals(Message.getString("asc"))){
				String result = GeoTiffController.toAAIGrid(f.getAbsolutePath(), 
						target + "/" + f.getName().toLowerCase().replace(CommonFun.getExtension(f), "") + "asc");
				if (result.toLowerCase().contains("error ")){
					is_pass = false;
				}
			}
			if (format.equals(Message.getString("gtiff"))){
				String result = GeoTiffController.toGeoTIFF(f.getAbsolutePath(), 
						target + "/" + f.getName().toLowerCase().replace(CommonFun.getExtension(f), "") + "tif");
				if (result.toLowerCase().contains("error ")){
					is_pass = false;
				}
			}
		}
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
	public boolean isPass(){
		return this.is_pass;
	}
	public File[] getFiles() {
		return Files;
	}

	public String getTarget() {
		return target;
	}

	public String getFormat() {
		return format;
	}

	
	
	
	
}
