/******************************************************************************
 * Huijie Qiao
 *
 * Project:  Niche Analyst
 * Purpose:  
 * Created date: May 5, 2012 7:52:52 PM
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

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpringLayout;

import org.gdal.gdalconst.gdalconst;
import org.ku.nicheanalyst.common.CommonFun;
import org.ku.nicheanalyst.common.ConfigInfo;
import org.ku.nicheanalyst.common.Const;
import org.ku.nicheanalyst.common.Message;
import org.ku.nicheanalyst.maps.controllers.GeoTiffController;
import org.ku.nicheanalyst.maps.objects.GeoTiffObject;
import org.ku.nicheanalyst.ui.display.Displayer;
import org.ku.nicheanalyst.ui.filefilters.RasterFileFilter;

/**
 * @author Huijie Qiao
 *
 */
public class BackgroundLoadVariableForm extends JDialog {
	private JTextField xvar;
	private JTextField yvar;
	private JTextField zvar;
	private JCheckBox xstandardized;
	private JCheckBox ystandardized;
	private JCheckBox zstandardized;
	private JCheckBox full;
	private File[] selectFiles_origin;
	private JList selectFilesList_origin;
	private DefaultListModel listModel_origin;
	
	private boolean is3D;
	
	private HashMap<String, String> returnValues;
	private Displayer theApp;
	
	public BackgroundLoadVariableForm(Displayer theApp){
		this.theApp = theApp;
		is3D = true;
//		getContentPane().setLayout(new GridLayout(4, 4));
		JPanel p = new JPanel(new SpringLayout());
		JLabel label = new JLabel(Message.getString("x_axis_variable"));
		p.add(label);
		xvar = new JTextField();
		xvar.setText("");
		xvar.setEditable(false);
		p.add(xvar);
		JButton selectx = new JButton("...");
		selectx.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				selectVariable(xvar);
			}
		});
		p.add(selectx);
		xstandardized = new JCheckBox(Message.getString("standardized"));
		xstandardized.setVisible(false);
		p.add(xstandardized);
		
		
		label = new JLabel(Message.getString("y_axis_variable"));
		p.add(label);
		yvar = new JTextField();
		yvar.setText("");
		yvar.setEditable(false);
		p.add(yvar);
		JButton selecty = new JButton("...");
		selecty.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				selectVariable(yvar);
			}
		});
		p.add(selecty);
		ystandardized = new JCheckBox(Message.getString("standardized"));
		ystandardized.setVisible(false);
		p.add(ystandardized);
		
		
		
		label = new JLabel(Message.getString("z_axis_variable"));
		p.add(label);
		zvar = new JTextField();
		zvar.setText("");
		zvar.setEditable(false);
		p.add(zvar);
		JButton selectz = new JButton("...");
		selectz.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				selectVariable(zvar);
			}
		});
		p.add(selectz);
		zstandardized = new JCheckBox(Message.getString("standardized"));
		zstandardized.setVisible(false);
		p.add(zstandardized);
		
		
		listModel_origin = new DefaultListModel();
		selectFilesList_origin = new JList(listModel_origin);
		selectFilesList_origin.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);		
		selectFilesList_origin.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		selectFilesList_origin.setVisibleRowCount(-1);
		
		
		JScrollPane plist = new JScrollPane(selectFilesList_origin);
		plist.setPreferredSize(new Dimension(250, 200));
		p.add(new JLabel(Message.getString("other_dim")));
		p.add(plist);
		
		
		
		JPanel p3 = new JPanel();
		p3.setLayout(new BoxLayout(p3, BoxLayout.Y_AXIS));
		JButton addB = new JButton(Message.getString("add_files"));
		JButton delB = new JButton(Message.getString("del_files"));
		addB.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				selectFolder();
			}
		});
		delB.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				removeFiles();
			}
		});
		p3.add(addB);
		p3.add(delB);
		p.add(p3);
		label = new JLabel();
		p.add(label);
		
		
		
		
		full = new JCheckBox(Message.getString("full_cloud"));
		full.setVisible(false);
		p.add(full);
		
		label = new JLabel();
		p.add(label);
		
		
		JButton cancel = new JButton(Message.getString("cancel"));
		cancel.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				cancelClick();
			}
		});
		JButton ok = new JButton(Message.getString("ok"));
		ok.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				try {
					okClick();
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		p.add(ok);
		p.add(cancel);
		
		SpringUtilities.makeCompactGrid(p,
        		5, 4, //rows, cols
                4, 4,        //initX, initY
                2, 2);       //xPad, yPad
        p.setOpaque(true);
        add(p);
		setSize(new Dimension(800, 350));
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);
		setModal(true);
	}
	protected void removeFiles() {
		int[] selected = selectFilesList_origin.getSelectedIndices();
		Arrays.sort(selected);
		for (int i=selected.length-1;i>=0;i--){
			listModel_origin.remove(selected[i]);
		}
		
	}
	/**
	 * 
	 */
	private void cancelClick() {
		returnValues = null;
		this.dispose();
		
	}
	private void okClick() throws FileNotFoundException{
		returnValues = null;
		File file = new File(xvar.getText());
		if (!file.exists()){
			JOptionPane.showMessageDialog(this, Message.getString("x_variable_required"));
			return;
		}
		file = new File(yvar.getText());
		if (!file.exists()){
			JOptionPane.showMessageDialog(this, Message.getString("y_variable_required"));
			return;
		}
		file = new File(zvar.getText());
		if (!file.exists()){
			//create a blank tiff layer based on x layer
			is3D = false;
			GeoTiffObject geo = new GeoTiffObject(xvar.getText());
			double[] values = new double[geo.getXSize() * geo.getYSize()];
			
			for (int x=0;x<geo.getXSize();x++){
				for (int y=0;y<geo.getYSize();y++){
					double value = geo.readByXY(x, y);
					if (CommonFun.equal(value, geo.getNoData(), 1000)){
						values[y * geo.getXSize() + x] = geo.getNoData();
					}else{
						values[y * geo.getXSize() + x] = 0;
					}
				}
			}
			file = new File(yvar.getText());
			String blankFile = ConfigInfo.getInstance().getTemp() + "/" + Math.abs(file.hashCode()) + ".tif";
			GeoTiffController.createTiff(blankFile, 
					geo.getXSize(), geo.getYSize(), 
					geo.getDataset().GetGeoTransform(), 
					values, geo.getNoData(), gdalconst.GDT_Float32, geo.getDataset().GetProjection());
			zvar.setText(blankFile);
			zstandardized.setSelected(false);
			geo.release();
		}
		returnValues = new HashMap<String, String>();
		returnValues.put("x", xvar.getText());
		returnValues.put("y", yvar.getText());
		returnValues.put("z", zvar.getText());
		returnValues.put("xstandardized", xstandardized.isSelected()?"t":"f");
		returnValues.put("ystandardized", ystandardized.isSelected()?"t":"f");
		returnValues.put("zstandardized", zstandardized.isSelected()?"t":"f");
		returnValues.put("full", full.isSelected()?"t":"f");
		setVisible(false);
	}
	protected void selectFolder() {
		JFileChooser fc = new JFileChooser();
		fc.setDialogTitle(Message.getString("select_pca_files"));
		fc.setFileFilter(new RasterFileFilter());
		
		fc.setSelectedFile(new File(theApp.getLastFolder()));
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fc.setMultiSelectionEnabled(true);
		int returnVal = fc.showOpenDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File[] target = fc.getSelectedFiles();
			if (target.length==0){
				return;
			}
			ArrayList<String> targets = new ArrayList<String>();
			for (File f : target){
				String ext = CommonFun.getExtension(f);
				if (ext.equalsIgnoreCase("tiff")||ext.equalsIgnoreCase("tif")||ext.equalsIgnoreCase("asc")){
					targets.add(f.getAbsolutePath());
				}
			}
			for (String t : targets){
				listModel_origin.add(listModel_origin.size(), t);
			}
			theApp.setLastFolder(target[0].getAbsolutePath());
		}
		
	}
	public HashMap<String, String> showDialog(){
		setVisible(true);
		return returnValues;
	}
	/**
	 * @param xvar2
	 */
	protected void selectVariable(JTextField var) {
		final JFileChooser fc = new JFileChooser();
		fc.setDialogTitle(Message.getString("select_variable"));
		fc.setSelectedFile(new File(theApp.getLastFolder()));
		fc.setFileFilter(new RasterFileFilter());
		//In response to a button click:
		int returnVal = fc.showOpenDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
	        String filename = file.getAbsolutePath();
	        var.setText(filename);
	        theApp.setLastFolder(filename);
		}
	}

	public boolean is3D() {
		return is3D;
	}
}
