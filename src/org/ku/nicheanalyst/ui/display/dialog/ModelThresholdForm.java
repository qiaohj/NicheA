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
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.TitledBorder;

import org.ku.nicheanalyst.common.Comment;
import org.ku.nicheanalyst.common.CommonFun;
import org.ku.nicheanalyst.common.Const;
import org.ku.nicheanalyst.common.Message;
import org.ku.nicheanalyst.common.ThresholdMethod;
import org.ku.nicheanalyst.ui.display.Displayer;
import org.ku.nicheanalyst.ui.display.component.ui.LabeledTextField;
import org.ku.nicheanalyst.ui.filefilters.CSVFileFilter;
import org.ku.nicheanalyst.ui.filefilters.RasterFileFilter;

/**
 * @author Huijie Qiao
 *
 */
public class ModelThresholdForm extends JDialog {
	private Displayer theApp; 
	private SelectFilePanal occurrences;
	private File[] resultFiles;
	private JList resultFilesList;
	private DefaultListModel resultFileslistModel;
	private LabeledTextField min;
	private LabeledTextField max;
	private File[] selectFiles;
	private JList selectFilesList;
	private DefaultListModel listModel;
	private SelectFilePanal target;
	private ThresholdMethod[] thresholdMethods;
	private JList thresholdMethodsList;
	private DefaultListModel thresholdMethodModel;
	
	public ModelThresholdForm(Displayer theApp){
		
		this.theApp = theApp;
		
		BoxLayout layout = new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS);
		this.getContentPane().setLayout(layout);
		
		occurrences = new SelectFilePanal(theApp, Message.getString("select_occurrence_file"), "", false, new CSVFileFilter(), false);
		occurrences.setToolTipText(Comment.getString("threshold_occurrence_tip"));
		this.add(occurrences);
		
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
		TitledBorder title = BorderFactory.createTitledBorder(Message.getString("select_model_result_file"));
		p.setBorder(title);
		p.setToolTipText(Comment.getString("threshold_model_result_tip"));
		
		JPanel p2 = new JPanel();
		p2.setLayout(new BoxLayout(p2, BoxLayout.X_AXIS));
		
		resultFileslistModel = new DefaultListModel();
		resultFilesList = new JList(resultFileslistModel);
		resultFilesList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);		
		resultFilesList.setLayoutOrientation(JList.VERTICAL);
		resultFilesList.setVisibleRowCount(-1);
		JScrollPane plist1 = new JScrollPane(resultFilesList);
		plist1.setPreferredSize(new Dimension(250, 150));
		p2.add(plist1);
		
		JPanel p3 = new JPanel();
		p3.setLayout(new BoxLayout(p3, BoxLayout.Y_AXIS));
		JButton addB = new JButton(Message.getString("add_files"));
		JButton delB = new JButton(Message.getString("del_files"));
		addB.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				selectFolder2();
			}
		});
		delB.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				removeFiles2();
			}
		});
		p3.add(addB);
		p3.add(delB);
		p2.add(p3);
		
		p.add(p2);
		add(p);
		
		
		
		
		p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
		title = BorderFactory.createTitledBorder(Message.getString("raster_threshold") + " " + Message.getString("optional"));
		p.setBorder(title);
		p.setToolTipText(Comment.getString("raster_threshold"));

		p2 = new JPanel();
		p2.setLayout(new BoxLayout(p2, BoxLayout.X_AXIS));
		
		listModel = new DefaultListModel();
		selectFilesList = new JList(listModel);
		selectFilesList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);		
		selectFilesList.setLayoutOrientation(JList.VERTICAL);
		selectFilesList.setVisibleRowCount(-1);
		JScrollPane plist = new JScrollPane(selectFilesList);
		plist.setPreferredSize(new Dimension(250, 200));
		p2.add(plist);
		
		p3 = new JPanel();
		p3.setLayout(new BoxLayout(p3, BoxLayout.Y_AXIS));
		addB = new JButton(Message.getString("add_files"));
		delB = new JButton(Message.getString("del_files"));
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
		title = BorderFactory.createTitledBorder(Message.getString("threshold_methods"));
		p.setBorder(title);
		p.setToolTipText(Comment.getString("threshold_methods"));
		
		p2 = new JPanel();
		p2.setLayout(new BoxLayout(p2, BoxLayout.X_AXIS));
		
		thresholdMethodModel = new DefaultListModel();
		thresholdMethodsList = new JList(thresholdMethodModel);
		thresholdMethodsList.setLayoutOrientation(JList.VERTICAL);
		thresholdMethodsList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);		
//		thresholdMethodsList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		thresholdMethodsList.setVisibleRowCount(-1);
		plist = new JScrollPane(thresholdMethodsList);
		plist.setPreferredSize(new Dimension(250, 200));
		p2.add(plist);
		
		p3 = new JPanel();
		p3.setLayout(new BoxLayout(p3, BoxLayout.Y_AXIS));
		JButton addB2 = new JButton(Message.getString("add_method"));
		JButton delB2 = new JButton(Message.getString("del_method"));
		addB2.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				addMethod();
			}
		});
		delB2.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				delMethod();
			}
		});
		p3.add(addB2);
		p3.add(delB2);
		p2.add(p3);
		
		p.add(p2);
		add(p);
		
		target = new SelectFilePanal(this.theApp, Message.getString("target"), "", true, null, true);
		this.add(target);
		
		
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
		
		setSize(new Dimension(800, 500));
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);

		
		setModal(true);
		
	}
	protected void delMethod() {
		int[] selected = thresholdMethodsList.getSelectedIndices();
		Arrays.sort(selected);
		for (int i=selected.length-1;i>=0;i--){
			thresholdMethodModel.remove(selected[i]);
		}
		
	}
	protected void addMethod() {
		AddThresholdMethodForm form = new AddThresholdMethodForm();
		form.setVisible(true);
		if (form.getSelectedMethod()!=null){
			thresholdMethodModel.add(thresholdMethodModel.size(), form.getSelectedMethod());
		}
	}
	protected void selectFolder2() {
		JFileChooser fc = new JFileChooser();
		fc.setDialogTitle("");
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
				resultFileslistModel.add(resultFileslistModel.size(), t);
			}
			theApp.setLastFolder(target[0].getAbsolutePath());
//			this.targettext.setText(target);
		}
		
	}
	
	protected void selectFolder() {
		JFileChooser fc = new JFileChooser();
		fc.setDialogTitle(Message.getString("select_environmental_files"));
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
	protected void removeFiles2() {
		int[] selected = resultFilesList.getSelectedIndices();
		Arrays.sort(selected);
		for (int i=selected.length-1;i>=0;i--){
			resultFileslistModel.remove(selected[i]);
		}
		
	}
	protected void removeFiles() {
		int[] selected = selectFilesList.getSelectedIndices();
		Arrays.sort(selected);
		for (int i=selected.length-1;i>=0;i--){
			listModel.remove(selected[i]);
		}
		
	}
	private void okClick(){
		this.resultFiles = new File[resultFileslistModel.size()];
		for (int i=0;i<resultFileslistModel.size();i++){
			this.resultFiles[i] = new File(resultFileslistModel.get(i).toString());
		}
		
		this.selectFiles = new File[listModel.size()];
		for (int i=0;i<listModel.size();i++){
			this.selectFiles[i] = new File(listModel.get(i).toString());
		}
		
		this.thresholdMethods = new ThresholdMethod[thresholdMethodModel.size()];
		for (int i=0;i<thresholdMethodModel.size();i++){
			this.thresholdMethods[i] = (ThresholdMethod) thresholdMethodModel.get(i);
		}
		
		File f = new File(target.getText());
		if (!f.exists()){
			this.theApp.ShowAlert(String.format(Message.getString("no_folder"), this.target.getText()));
		}else{
			setVisible(false);
		}
	}
	private void cancelClick() {
		this.target = null;
		this.dispose();
		
	}
	public String getOccurrences() {
		return occurrences.getText();
	}
	public File[] getResult() {
		return resultFiles;
	}
	public double getMin() {
		if (CommonFun.isDouble(min.getText())){
			return Double.valueOf(min.getDouble()).doubleValue();
		}
		return Double.MAX_VALUE;
	}
	public double getMax() {
		if (CommonFun.isDouble(max.getText())){
			return Double.valueOf(max.getDouble()).doubleValue();
		}
		return -1 * Double.MAX_VALUE;
	}
	
	public File[] getSelectFiles() {
		return selectFiles;
	}
	
	public String getTarget() {
		if (target==null){
			return null;
		}
		return target.getText();
	}
	public ThresholdMethod[] getThresholdMethods() {
		return thresholdMethods;
	}
	
	
}
