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

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

import org.ku.nicheanalyst.common.Comment;
import org.ku.nicheanalyst.common.CommonFun;
import org.ku.nicheanalyst.common.Const;
import org.ku.nicheanalyst.common.Message;
import org.ku.nicheanalyst.maps.objects.GeoTiffObject;
import org.ku.nicheanalyst.ui.display.Displayer;
import org.ku.nicheanalyst.ui.display.component.ui.LabeledTextField;
import org.ku.nicheanalyst.ui.filefilters.RasterFileFilter;

/**
 * @author Huijie Qiao
 *
 */
public class SetENMResultRangeForm extends JDialog {
	private File[] selectFiles;
	private JList selectFilesList;
	private JTextField targetText;
	private Displayer theApp;
	private DefaultListModel listModel;
	private LabeledTextField fromLabel;
	private LabeledTextField toLabel;
	private String target;
	public SetENMResultRangeForm(Displayer theApp){
		this.theApp = theApp;
		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
		p.setBorder(BorderFactory.createTitledBorder(Message.getString("select_geotiff_files")));
		p.setToolTipText(Comment.getString("select_geotiff_files"));
		JPanel p2 = new JPanel();
		p2.setLayout(new BoxLayout(p2, BoxLayout.X_AXIS));
		
		listModel = new DefaultListModel();
		selectFilesList = new JList(listModel);
		selectFilesList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);		
		selectFilesList.setLayoutOrientation(JList.VERTICAL);
		selectFilesList.setVisibleRowCount(-1);
		JScrollPane plist = new JScrollPane(selectFilesList);
		plist.setPreferredSize(new Dimension(250, 200));
		p2.add(plist);
		
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
		p2.add(p3);
		
		p.add(p2);
		add(p);
		
		p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
		JPanel p_item = new JPanel();
		p_item.setLayout(new BoxLayout(p_item, BoxLayout.X_AXIS));
		
		fromLabel = new LabeledTextField(Message.getString("from_range"), "0,100");
		p_item.add(fromLabel);
		JButton getrangeb = new JButton(Message.getString("read_range"));
		getrangeb.setToolTipText(Comment.getString("read_range"));
		p_item.add(getrangeb);
		getrangeb.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				try {
					getrange();
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
				}
			}
		});
		add(p_item);
		
		
		
		toLabel = new LabeledTextField(Message.getString("to_range"), "0,1");
		add(toLabel);
		
		add(p);
		
		p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
		p.setBorder(BorderFactory.createTitledBorder(Message.getString("select_result_folder")));
		p2 = new JPanel();
		p2.setLayout(new BoxLayout(p2, BoxLayout.X_AXIS));
		targetText = new JTextField();
		p2.add(targetText);
		JButton targetb = new JButton("...");
		p2.add(targetb);
		targetb.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				selectTarget();
			}
		});
		p.add(p2);
		add(p);
		
		
        p2 = new JPanel(new GridLayout(1, 2));
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
		setSize(new Dimension(500, 280));
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);
		setModal(true);
	}
	protected void getrange() throws FileNotFoundException {
		File[] enms = getSelectFiles();
		if (enms.length==0){
			this.theApp.ShowAlert(Message.getString("no_file_error"));
		}else{
			GeoTiffObject enm = new GeoTiffObject(enms[0].getAbsolutePath());
			double[] max_min = enm.getMaxMin();
			this.fromLabel.setText(String.format("%f,%f", max_min[0], max_min[1]));
			this.theApp.ShowAlert(String.format("Min value is %f, Max value is %f", max_min[0], max_min[1]));
		}
		
	}
	/**
	 * 
	 */
	protected void selectTarget() {
		JFileChooser fc = new JFileChooser();
		fc.setDialogTitle(Message.getString("select_result_folder"));
		fc.setSelectedFile(new File(theApp.getLastFolder()));
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int returnVal = fc.showSaveDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File target = fc.getSelectedFile().getAbsoluteFile();
			if (!target.exists()){
				target = target.getParentFile();
			}
			this.targetText.setText(target.getAbsolutePath());
			theApp.setLastFolder(target.getAbsolutePath());
		}
		
	}
	/**
	 * 
	 */
	protected void removeFiles() {
		int[] selected = selectFilesList.getSelectedIndices();
		Arrays.sort(selected);
		for (int i=selected.length-1;i>=0;i--){
			listModel.remove(selected[i]);
		}
		
	}
	/**
	 * 
	 */
	protected void selectFolder() {
		JFileChooser fc = new JFileChooser();
		fc.setDialogTitle(Message.getString("select_geotiff_files"));
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
				listModel.add(listModel.size(), t);
			}
			theApp.setLastFolder(target[0].getAbsolutePath());
//			this.targettext.setText(target);
		}
		
	}
	private void cancelClick() {
		this.target = null;
		this.dispose();
		
	}
	private void okClick(){
		
		boolean isPass = true;
		File[] seleFiles = getSelectFiles();
		if (seleFiles.length==0){
			this.selectFilesList.requestFocus();
			isPass = false;
		}
		File f = new File(this.targetText.getText());
		if (!f.exists()){
			this.targetText.requestFocus();
			isPass = false;
		}
		double[] fromRange = getFromRange();
		if (CommonFun.equal(fromRange[0], -9999, 1000)){
			this.fromLabel.requestFocus();
			isPass = false;
		}
		if (CommonFun.equal(fromRange[0], fromRange[1], 1000000)){
			this.fromLabel.requestFocus();
			isPass = false;
		}
		
		if (fromRange[0]>=fromRange[1]){
			this.fromLabel.requestFocus();
			isPass = false;
		}
		
		double[] toRange = getToRange();
		if (CommonFun.equal(toRange[0], -9999, 1000)){
			this.toLabel.requestFocus();
			isPass = false;
		}
		
		if (CommonFun.equal(toRange[0], toRange[1], 1000000)){
			this.fromLabel.requestFocus();
			isPass = false;
		}
		
		if (toRange[0]>=toRange[1]){
			this.fromLabel.requestFocus();
			isPass = false;
		}
		
		if (isPass){
			this.target = this.targetText.getText();
			
			setVisible(false);
		}
		
	}
	public File[] getSelectFiles() {
		this.selectFiles = new File[listModel.size()];
		for (int i=0;i<listModel.size();i++){
			this.selectFiles[i] = new File(listModel.get(i).toString());
		}
		return selectFiles;
	}
	public String getTarget() {
		return target;
	}
	public double[] getFromRange(){
		double[] range = new double[2];
		range[0] = -9999;
		String[] r = this.fromLabel.getText().split(",");
		if (r.length==2){
			if (CommonFun.isDouble(r[0])&&(CommonFun.isDouble(r[1]))){
				range[0] = Double.valueOf(r[0]).doubleValue();
				range[1] = Double.valueOf(r[1]).doubleValue();
			}
		}
		 return range;
	}
	
	public double[] getToRange(){
		double[] range = new double[2];
		range[0] = -9999;
		String[] r = this.toLabel.getText().split(",");
		if (r.length==2){
			if (CommonFun.isDouble(r[0])&&(CommonFun.isDouble(r[1]))){
				range[0] = Double.valueOf(r[0]).doubleValue();
				range[1] = Double.valueOf(r[1]).doubleValue();
			}
		}
		 return range;
	}
}
