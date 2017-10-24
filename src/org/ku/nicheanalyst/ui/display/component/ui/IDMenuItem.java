/******************************************************************************
 * Huijie Qiao
 *
 * Project:  Niche Analyst
 * Purpose:  
 * Created date: May 29, 2012 4:10:09 PM
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


package org.ku.nicheanalyst.ui.display.component.ui;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import org.ku.nicheanalyst.common.Message;
import org.ku.nicheanalyst.dataset.SpeciesDataset;
import org.ku.nicheanalyst.ui.display.Menu;

/**
 * @author Huijie Qiao
 *
 */
public class IDMenuItem extends JMenu {
	private String key;
	private SpeciesDataset vs;
	private boolean isVirtualSpecies;
	public IDMenuItem(Menu parent, String title, String key, SpeciesDataset vs, boolean isVirtualSpecies,
			boolean isShowPoint, boolean isShowMVE, boolean isShowConvexhull){
		super(title);
		this.vs = vs;
		this.key = key;
		this.isVirtualSpecies = isVirtualSpecies;
		
		JMenuItem item = null;
		if (isShowPoint){
			add(item = new JMenuItem(Message.getString("hide_point")));
			item.setToolTipText(" of " + key);
		    item.addActionListener(parent);
		}else{
			add(item = new JMenuItem(Message.getString("show_point")));
			item.setToolTipText(" of " + key);
		    item.addActionListener(parent);
		}
		item.setEnabled(isShowPoint);
		
		if (isShowMVE){
			add(item = new JMenuItem(Message.getString("hide_mve")));
			item.setToolTipText(" of " + key);
		    item.addActionListener(parent);
		}else{
			add(item = new JMenuItem(Message.getString("show_mve")));
			item.setToolTipText(" of " + key);
		    item.addActionListener(parent);
		}
		item.setEnabled(isShowMVE);
		
		if (isShowConvexhull){
			add(item = new JMenuItem(Message.getString("hide_convexhull")));
			item.setToolTipText(" of " + key);
		    item.addActionListener(parent);
		}else{
			add(item = new JMenuItem(Message.getString("show_convexhull")));
			item.setToolTipText(" of " + key);
		    item.addActionListener(parent);
		}
		item.setEnabled(isShowConvexhull);
	}
	public String getKey() {
		return key;
	}
//	public boolean isChecked() {
//		return checked;
//	}
//	public void setChecked(boolean checked) {
//		this.checked = checked;
//	}
	public SpeciesDataset getVs() {
		return vs;
	}
	public boolean isVirtualSpecies() {
		return isVirtualSpecies;
	}
//	public boolean[] getSubchecked(){
//		return subchecked;
//	}
//	public void setSubchecked(int index, boolean checked){
//		if (this.checked){
//			subchecked[index] = checked;
//		}else{
//			subchecked[index] = this.checked;
//		}
//	}
}
