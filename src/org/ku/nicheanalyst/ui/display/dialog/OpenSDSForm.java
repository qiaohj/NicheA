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
import java.io.File;
import java.io.FileNotFoundException;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.vecmath.Color3f;

import org.ku.nicheanalyst.common.CommonFun;
import org.ku.nicheanalyst.common.Const;
import org.ku.nicheanalyst.common.Message;
import org.ku.nicheanalyst.maps.objects.GeoTiffObject;
import org.ku.nicheanalyst.ui.display.Displayer;
import org.ku.nicheanalyst.ui.display.component.ui.JColorCombobox;
import org.ku.nicheanalyst.ui.filefilters.RasterFileFilter;

/**
 * @author Huijie Qiao
 *
 */
public class OpenSDSForm extends JDialog {
	private String[] target;
	private boolean isFolder;
	private Color3f mvecolor;
	private Color3f chcolor;
	private Color3f pointcolor;
	private double threshold;
	private JComboBox typecombobox;
	private JTextArea targettext;
	private JTextField thresholdText;
	private JComboBox mvepicker;
	private JComboBox chpicker;
	private JComboBox pointpicker;
	private JFileChooser fc;
	private String selectedType;
//	private JComboBox resultTypeCombobox;
	private JCheckBox gradualPoints;
	private boolean isGradual;
//	private int resultType;
	private SelectFilePanal v1;
	private SelectFilePanal v2;
	private SelectFilePanal v3;
	private Displayer theApp;
	public OpenSDSForm(Displayer theApp){
		Color[] colors = {
				Color.BLUE, 
				Color.CYAN, 
				Color.DARK_GRAY, 
				Color.GRAY, 
				Color.GREEN, 
				Color.LIGHT_GRAY, 
				Color.MAGENTA,
				Color.ORANGE,
				Color.PINK,
				Color.RED,
				Color.WHITE,
				Color.YELLOW};
		this.theApp = theApp;
		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		
		
		JLabel l = new JLabel(Message.getString("select_a_species"), JLabel.TRAILING);
		JPanel p_part = new JPanel();
		p_part.setLayout(new BoxLayout(p_part, BoxLayout.X_AXIS));
		String[] types = new String[]{Message.getString("sds_folder"), Message.getString("distribution_file")};
		selectedType = types[0];
		typecombobox = new JComboBox(types);
		fc = new JFileChooser();
		fc.setDialogTitle(Message.getString("select_a_folder"));
		fc.setSelectedFile(new File(theApp.getLastFolder()));
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fc.setMultiSelectionEnabled(true);
		typecombobox.addActionListener (new ActionListener () {
		    public void actionPerformed(ActionEvent e) {
		    	String selectText = (String) typecombobox.getSelectedItem();
		    	if (!selectText.equals(selectedType)){
		    		targettext.setText("");
		    		selectedType = selectText;
		    		if (selectedType.equals(Message.getString("sds_folder"))){
		    			fc.setDialogTitle(Message.getString("select_a_folder"));
		    			fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		    			fc.setFileFilter(null);
		    			thresholdText.setEnabled(false);
//		    			resultTypeCombobox.setEnabled(false);
		    			gradualPoints.setEnabled(false);
		    		}else{
		    			fc.setDialogTitle(Message.getString("select_a_species_tiff"));
		    			fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		    			fc.setFileFilter(new RasterFileFilter());
		    			thresholdText.setEnabled(true);
//		    			resultTypeCombobox.setEnabled(true);
		    			gradualPoints.setEnabled(true);
		    		}
		    	}
		    }
		});
		
		JButton select_folder = new JButton("...");
		select_folder.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				selectFolder();
			}
		});
		p_part.add(l);
		p_part.add(typecombobox);
		p_part.add(select_folder);
		add(p_part);
		
		
		p_part = new JPanel();
		p_part.setPreferredSize(new Dimension(100,320));
		p_part.setLayout(new BoxLayout(p_part, BoxLayout.LINE_AXIS));
		
		targettext = new JTextArea(5, 100);
		targettext.setEditable(false);
		p_part.add(targettext);
		add(p_part);
		
		p_part = new JPanel();
		p_part.setLayout(new BoxLayout(p_part, BoxLayout.LINE_AXIS));
		l = new JLabel(Message.getString("threshold"), JLabel.TRAILING);
		thresholdText = new JTextField();
		thresholdText.setPreferredSize(new Dimension(100, 20));
		thresholdText.setText("1");
		p_part.add(l);
		p_part.add(thresholdText);
//		l = new JLabel(Message.getString("result_type"), JLabel.TRAILING);
//		String[] results = new String[]{Message.getString("r0_1"), Message.getString("r0_100"), Message.getString("r0_255")};
//		resultTypeCombobox = new JComboBox(results);
//		resultTypeCombobox.setSelectedIndex(0);
//		p_part.add(l);
//		p_part.add(resultTypeCombobox);
//		add(p_part);
		
		if (this.theApp.isFullBackground()){
			JPanel pt = new JPanel();
			pt.setLayout(new GridLayout());
			JLabel label = new JLabel(Message.getString("distribution_with_layers"));
			pt.add(label);
			add(pt);
			p_part = new JPanel();
			p_part.setLayout(new BoxLayout(p_part, BoxLayout.Y_AXIS));
			v1 = new SelectFilePanal(this.theApp, Message.getString("x_axis_variable"), "", false, new RasterFileFilter(), false);
			v2 = new SelectFilePanal(this.theApp, Message.getString("y_axis_variable"), "", false, new RasterFileFilter(), false);
			v3 = new SelectFilePanal(this.theApp, Message.getString("z_axis_variable"), "", false, new RasterFileFilter(), false);
			p_part.add(v1);
			p_part.add(v2);
			p_part.add(v3);
			add(p_part);
		}
		
//		p_part = new JPanel();
		p_part.setLayout(new BoxLayout(p_part, BoxLayout.X_AXIS));		
		l = new JLabel(Message.getString("select_mve_color"), JLabel.TRAILING);
		mvepicker = new JComboBox(colors);
		JColorCombobox renderer= new JColorCombobox();
        renderer.setPreferredSize(new Dimension(20, 20));
        mvepicker.setRenderer(renderer);
        mvepicker.setSelectedItem(Color.YELLOW);
        mvepicker.setBackground(Color.YELLOW);
        mvepicker.addActionListener (new ActionListener () {
		    public void actionPerformed(ActionEvent e) {
		        Color color = (Color) mvepicker.getSelectedItem();
		        mvepicker.setBackground(color);
		    }
		});
		p_part.add(l);
		p_part.add(mvepicker);
		

		l = new JLabel(Message.getString("convexhull_color"), JLabel.TRAILING);
		chpicker = new JComboBox(colors);
		renderer= new JColorCombobox();
        renderer.setPreferredSize(new Dimension(20, 20));
        chpicker.setRenderer(renderer);
        chpicker.setSelectedItem(Color.WHITE);
        chpicker.setBackground(Color.WHITE);
        chpicker.addActionListener (new ActionListener () {
		    public void actionPerformed(ActionEvent e) {
		        Color color = (Color) chpicker.getSelectedItem();
		        chpicker.setBackground(color);
		    }
		});
		p_part.add(l);
		p_part.add(chpicker);
		
		
		l = new JLabel(Message.getString("point_color"), JLabel.TRAILING);
		pointpicker = new JComboBox(colors);
		renderer= new JColorCombobox();
        renderer.setPreferredSize(new Dimension(20, 20));
        pointpicker.setRenderer(renderer);
        pointpicker.setSelectedItem(Color.RED);
        pointpicker.setBackground(Color.RED);
        pointpicker.addActionListener (new ActionListener () {
		    public void actionPerformed(ActionEvent e) {
		        Color color = (Color) pointpicker.getSelectedItem();
		        pointpicker.setBackground(color);
		    }
		});
		p_part.add(l);
		p_part.add(pointpicker);
		gradualPoints = new JCheckBox(Message.getString("gradualColor"));
		p_part.add(gradualPoints);
		add(p_part);
		
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
		setSize(new Dimension(800, 350));
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);
		setModal(true);
		thresholdText.setEnabled(false);
//		resultTypeCombobox.setEnabled(false);
		gradualPoints.setEnabled(false);
	}
	/**
	 * 
	 */
	protected void selectFolder() {
		
		int returnVal = fc.showOpenDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			StringBuilder sb = new StringBuilder();
			for (File file : fc.getSelectedFiles()){
				String target = (file.exists())?
						file.getAbsolutePath():file.getParent();
				theApp.setLastFolder(target);
				sb.append(target + Const.LineBreak);
			}
			this.targettext.setText(sb.toString());	
		}
		
	}
	private void cancelClick() {
		this.target = null;
		this.dispose();
		
	}
	private void okClick(){
		boolean ispass = true;
		this.target = this.targettext.getText().split(Const.LineBreak);
		for (String item : this.target){
			File f = new File(item);
			if (!f.exists()){
				ispass = false;
			}
		}
		if (!CommonFun.isDouble(this.thresholdText.getText())){
			this.thresholdText.setForeground(Color.RED);
			ispass = false;
		}else{
			this.threshold = Double.valueOf(this.thresholdText.getText()).doubleValue();
		}
		
		isFolder = (this.typecombobox.getSelectedItem().equals(Message.getString("sds_folder")))?true:false;
		if ((this.theApp.isFullBackground())&&(isFolder)){
			theApp.ShowAlert(Message.getString("distribution_with_layers_error"));
			ispass = false;
		}
		if (this.theApp.isFullBackground()){
			if (ispass){
				try {
					GeoTiffObject geo = new GeoTiffObject(v1.getText());
					geo.release();
				} catch (FileNotFoundException e) {
					theApp.ShowAlert(String.format(Message.getString("error_read_tifffile"), v1.getText()));
					ispass = false;
					e.printStackTrace();
				}
			}
			if (ispass){
				try {
					GeoTiffObject geo = new GeoTiffObject(v2.getText());
					geo.release();
				} catch (FileNotFoundException e) {
					theApp.ShowAlert(String.format(Message.getString("error_read_tifffile"), v2.getText()));
					ispass = false;
					e.printStackTrace();
				}
			}
			if (ispass){
				try {
					GeoTiffObject geo = new GeoTiffObject(v3.getText());
					geo.release();
				} catch (FileNotFoundException e) {
					theApp.ShowAlert(String.format(Message.getString("error_read_tifffile"), v3.getText()));
					ispass = false;
					e.printStackTrace();
				}
			}
		}
		mvecolor = getColor3f((Color)this.mvepicker.getSelectedItem());
		chcolor = getColor3f((Color)this.chpicker.getSelectedItem());
		pointcolor = getColor3f((Color)this.pointpicker.getSelectedItem());
//		resultType = resultTypeCombobox.getSelectedIndex();
		isGradual = gradualPoints.isSelected();
		if (!ispass){
			theApp.ShowAlert(Message.getString("error_warning"));
		}else{
			setVisible(false);
		}
	}
	/**
	 * @param selectedItem
	 * @return
	 */
	private Color3f getColor3f(Color color) {
		return new Color3f(color);
	}
	public String[] getTarget() {
		return target;
	}
	public boolean isFolder() {
		return isFolder;
	}
	
	public double getThreshold() {
		return threshold;
	}
	public Color3f getMvecolor() {
		return mvecolor;
	}
	public Color3f getChcolor() {
		return chcolor;
	}
	public Color3f getPointcolor() {
		return pointcolor;
	}
//	public int getResultType() {
//		return resultType;
//	}
	public boolean isGradual() {
		return isGradual;
	}
	public String[] getLayers(){
		if (!this.theApp.isFullBackground()){
			return null;
		}
		String[] layers = new String[3];
		layers[0] = v1.getText();
		layers[1] = v2.getText();
		layers[2] = v3.getText();
		return layers;
	}
}
