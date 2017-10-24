/******************************************************************************
 * Huijie Qiao
 *
 * Project:  Niche Analyst
 * Purpose:  
 * Created date: Aug 11, 2012 2:24:10 PM
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


package org.ku.nicheanalyst.ui.display.dialog;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.ProgressMonitor;
import javax.swing.SpringLayout;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileFilter;

import org.ku.nicheanalyst.common.CommonFun;
import org.ku.nicheanalyst.common.ConfigInfo;
import org.ku.nicheanalyst.common.Message;
import org.ku.nicheanalyst.dataset.GSpaceData;
import org.ku.nicheanalyst.dataset.SpeciesDataset;
import org.ku.nicheanalyst.ui.display.Displayer;
import org.ku.nicheanalyst.ui.display.component.ui.JNumberTextField;
import org.ku.nicheanalyst.ui.display.component.ui.LabeledTextField;
import org.ku.nicheanalyst.ui.filefilters.CSVFileFilter;
import org.ku.nicheanalyst.ui.filefilters.RasterFileFilter;
import org.ku.nicheanalyst.ui.filefilters.TiffFileFilter;

/**
 * @author Huijie Qiao
 *
 */
public class LL2RasterForm extends JDialog implements ItemListener {
	
	private Displayer theApp;
	private SelectFilePanal mask;
	private SelectFilePanal llfile;
	
	private SelectFilePanal target;
	private JComboBox lon;
	private JComboBox lat;
	private JCheckBox col_head;
	private JComboBox value;
	private LabeledTextField sep;
	public String getMask(){
		return this.mask.getText();
	}
	public String getLLFile(){
		return this.llfile.getText();
	}
	public String getTarget(){
		return this.target.getText();
	}
	public String getLon_Col(){
		return (String) this.lon.getSelectedItem();
	}
	public String getLat_Col(){
		return (String) this.lat.getSelectedItem();
	}
	public String getValue_Col(){
		return (String) this.value.getSelectedItem();
	}
	public String getSep(){
		return this.sep.getText();
	}
	public boolean isColHead(){
		return this.col_head.isSelected();
	}
	public LL2RasterForm(Displayer theApp){
		
		this.theApp = theApp;
		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		
        this.mask = new SelectFilePanal(this.theApp, Message.getString("mask_file"), "", false, new RasterFileFilter(), false);
        JButton refresh = new JButton(Message.getString("refresh"));
        refresh.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				changeCombobox();
			}
		});
		
        this.llfile = new SelectFilePanal(theApp, Message.getString("ll_file"), "", false, new CSVFileFilter(), false);
        
        this.target = new SelectFilePanal(theApp, Message.getString("target"), "", false, new TiffFileFilter(), true);
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
        JLabel lon_l = new JLabel(Message.getString("longitude_field"));
        JLabel lat_l = new JLabel(Message.getString("latitude_field"));
        JLabel value_l = new JLabel(Message.getString("value_field"));
        lon = new JComboBox();
        lat = new JComboBox();
        value = new JComboBox();
        col_head = new JCheckBox(Message.getString("first_has_field"));
        sep = new LabeledTextField(Message.getString("sep_by"), ",");
        p.add(lon_l);
        p.add(lon);
        p.add(lat_l);
        p.add(lat);
        p.add(value_l);
        p.add(value);
        p.add(refresh);
        add(llfile);
        add(mask);
        add(target);
        JPanel p3 = new JPanel();
        p3.setLayout(new BoxLayout(p3, BoxLayout.X_AXIS));
        p3.add(col_head);
        p3.add(sep);
        add(p3);
        col_head.addItemListener(this);
        add(p);
        
        JPanel p2 = new JPanel(new GridLayout(1, 2));
        JButton cancel = new JButton(Message.getString("cancel"));
		cancel.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				cancelClick();
			}
		});
		JButton ok = new JButton(Message.getString("ok"));
		ok.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				okClick();
			}
		});
		p2.add(ok);
		p2.add(cancel);
		add(p2);
		
		setSize(new Dimension(800, 200));
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);
		
		setModal(true);
		
	}
	

	private void cancelClick() {
		this.target.setText("");
		this.dispose();
		
	}
	private void okClick(){
		boolean ispass = true;
		File f2 = new File(this.llfile.getText());
		if (!f2.exists()){
			this.llfile.requestFocus();
			ispass = false;
		}
		
		File f = new File(this.mask.getText());
		if (!f.exists()){
			this.mask.requestFocus();
			ispass = false;
		}
		
		f = new File(this.target.getText());
		if (f.isDirectory()){
			this.target.setText(this.target.getText() + "/" + f2.getName() + ".tif");
		}
		if (this.lon.getItemCount()==0){
			changeCombobox();
			ispass = false;
		}
		
		if (!ispass){
			theApp.ShowAlert(Message.getString("error_warning"));
		}else{
			setVisible(false);
		}
	}


	@Override
	public void itemStateChanged(ItemEvent e) {
		Object source = e.getItemSelectable();

	    if (source == this.col_head) {
	    	changeCombobox();
	    }
		
	}


	private void changeCombobox() {
		File f = new File(this.llfile.getText());
    	if (f.exists()){
    		try {
				ArrayList<String> a = CommonFun.readFromFile(f.getAbsolutePath());
				if (a.size()>0){
					String[] col_names = null;
					if (this.col_head.isSelected()){
						col_names = a.get(0).split(this.sep.getText());
					}else{
						col_names = new String[a.get(0).split(this.sep.getText()).length];
						for (int i=0;i<a.get(0).split(this.sep.getText()).length;i++){
							col_names[i] = Message.getString("col_label") + i;	
						}
					}
					this.lat.removeAllItems();
					this.lon.removeAllItems();
					this.value.removeAllItems();
					for (String col_name : col_names){
						this.lat.addItem(col_name);
						this.lon.addItem(col_name);
						this.value.addItem(col_name);
					}
					for (int i=0;i<col_names.length;i++){
						String col_name = col_names[i];
						if (col_name.toLowerCase().replace("\"", "").equalsIgnoreCase(("x"))){
							this.lon.setSelectedIndex(i);
							continue;
						}
						if (col_name.toLowerCase().contains("lon")){
							this.lon.setSelectedIndex(i);
							continue;
						}
						if (col_name.toLowerCase().replace("\"", "").equalsIgnoreCase(("y"))){
							this.lat.setSelectedIndex(i);
							continue;
						}
						if (col_name.toLowerCase().contains("lat")){
							this.lat.setSelectedIndex(i);
							continue;
						}
						this.value.setSelectedIndex(i);
					}
				}
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
    		
    	}
		
	}
	
}
