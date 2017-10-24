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
import javax.swing.JCheckBox;
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
import org.ku.nicheanalyst.common.Message;
import org.ku.nicheanalyst.ui.display.Displayer;
import org.ku.nicheanalyst.ui.display.component.ui.JNumberTextField;
import org.ku.nicheanalyst.ui.display.component.ui.LabeledTextField;
import org.ku.nicheanalyst.ui.filefilters.RasterFileFilter;

/**
 * @author Huijie Qiao
 *
 */
public class OccurrenceDistributionForm extends JDialog {
	private File[] selectFiles;
	private JList selectFilesList;
	private JTextField targetText;
	private JTextField occurrenceFileText;
	private JTextField enmFileText;
	private JNumberTextField stepText;
	private Displayer theApp;
	private DefaultListModel listModel;
	private String target;
	private String occurrenceFile;
	private int steps;
	private JCheckBox isnodata_checkbox;
	private JNumberTextField nodataText;
	private LabeledTextField thresholdText;
	private boolean isnodata;
	private int nodata;
	private String enmFile;
	public OccurrenceDistributionForm(Displayer theApp){
		isnodata = false;
		this.theApp = theApp;
		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
		p.setBorder(BorderFactory.createTitledBorder(Message.getString("select_env_files")));
		p.setToolTipText(Comment.getString("select_env_files"));
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
		p.setBorder(BorderFactory.createTitledBorder(Message.getString("select_env_folder")));
		p.setToolTipText(Comment.getString("select_env_folder"));
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

		
		p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
		p.setBorder(BorderFactory.createTitledBorder(Message.getString("select_occurrence_file")));
		p.setToolTipText(Comment.getString("select_occurrence_file"));
		p2 = new JPanel();
		p2.setLayout(new BoxLayout(p2, BoxLayout.X_AXIS));
		occurrenceFileText = new JTextField();
		p2.add(occurrenceFileText);
		JButton targetbs = new JButton("...");
		p2.add(targetbs);
		targetbs.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				selectOccurrenceFile();
			}

			
		});
		p.add(p2);
		add(p);
		
		p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
		p.setBorder(BorderFactory.createTitledBorder(Message.getString("select_enm_file")));
		p.setToolTipText(Comment.getString("select_enm_file"));
		p2 = new JPanel();
		p2.setLayout(new BoxLayout(p2, BoxLayout.X_AXIS));
		enmFileText = new JTextField();
		p2.add(enmFileText);
		targetbs = new JButton("...");
		p2.add(targetbs);
		targetbs.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				selectEnmFile();
			}

			
		});
		p.add(p2);
		add(p);
		
		
		
		
		
		p2 = new JPanel(new GridLayout(1, 4));
        JLabel label = new JLabel(Message.getString("env_steps"));
        label.setToolTipText(Comment.getString("env_steps"));
        p2.add(label);
        
        stepText = new JNumberTextField(4, JNumberTextField.NUMERIC);
        stepText.setText("100");
        
		
		p2.add(stepText);
		
		
		isnodata_checkbox = new JCheckBox(Message.getString("env_nodata"));
		isnodata_checkbox.setToolTipText(Comment.getString("env_nodata"));
        p2.add(isnodata_checkbox);
        
        nodataText = new JNumberTextField(4, JNumberTextField.NUMERIC);
        nodataText.setText("-9999");
        
        thresholdText = new LabeledTextField(Message.getString("threshold"), "0");
        thresholdText.setToolTipText(Comment.getString("threshold"));
		
		p2.add(nodataText);
		p2.add(thresholdText);
		
		
		add(p2);
		
		
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
		setSize(new Dimension(650, 400));
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);
		setModal(true);
		
		
	}
	protected void selectEnmFile() {
		JFileChooser fc = new JFileChooser();
		fc.setDialogTitle(Message.getString("select_enm_file"));
		fc.setSelectedFile(new File(theApp.getLastFolder()));
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fc.setFileFilter(new RasterFileFilter());
		int returnVal = fc.showOpenDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File target = fc.getSelectedFile().getAbsoluteFile();
			this.enmFileText.setText(target.getAbsolutePath());
		}
		
	}
	/**
	 * 
	 */
	protected void selectOccurrenceFile() {
		JFileChooser fc = new JFileChooser();
		fc.setDialogTitle(Message.getString("select_occurrence_file"));
		fc.setSelectedFile(new File(theApp.getLastFolder()));
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		int returnVal = fc.showOpenDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File target = fc.getSelectedFile().getAbsoluteFile();
			this.occurrenceFileText.setText(target.getAbsolutePath());
		}
		
	}
	/**
	 * 
	 */
	protected void selectTarget() {
		JFileChooser fc = new JFileChooser();
		fc.setDialogTitle(Message.getString("select_a_folder"));
		File f = new File(theApp.getLastFolder());
		f = (f.isDirectory())?f:f.getParentFile();
		fc.setSelectedFile(f);
		fc.setSelectedFile(new File(theApp.getLastFolder()));
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int returnVal = fc.showSaveDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File target = (fc.getSelectedFile().exists())?
					fc.getSelectedFile().getAbsoluteFile():fc.getSelectedFile().getParentFile();
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
		fc.setDialogTitle(Message.getString("select_env_files"));
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
		if (isnodata_checkbox.isSelected()){
			isnodata = true;
			if (CommonFun.isInteger(nodataText.getText())){
				nodata = Integer.valueOf(nodataText.getText());	
			}else{
				nodata = -9999;
			}
		}else{
			isnodata = false;
			nodata = 0;
		}
		this.selectFiles = new File[listModel.size()];
		for (int i=0;i<listModel.size();i++){
			this.selectFiles[i] = new File(listModel.get(i).toString());
		}
		this.target = this.targetText.getText();
		File f = new File(target);
		if (!f.exists()){
			this.theApp.ShowAlert(String.format(Message.getString("no_folder"), this.target));
		}else{
			setVisible(false);
		}
		this.occurrenceFile = this.occurrenceFileText.getText();
		f = new File(occurrenceFile);
		if (!f.exists()){
			this.occurrenceFile = null;
			setVisible(false);
		}else{
			setVisible(false);
		}
		
		this.enmFile = this.enmFileText.getText();
		f = new File(enmFile);
		if (!f.exists()){
			this.enmFile = null;
			setVisible(false);
		}else{
			setVisible(false);
		}
		
	}
	public File[] getSelectFiles() {
		return selectFiles;
	}
	public String getTarget() {
		return target;
	}
	public int getSteps(){
		String s = stepText.getText();
		
		if (CommonFun.isInteger(s)){
			return Integer.valueOf(s);
		}else{
			return 1000;
		}
	}
	public String getOccurrenceFile() {
		return occurrenceFile;
	}
	public boolean isIsnodata() {
		return isnodata;
	}
	public int getNodata() {
		return nodata;
	}
	public String getEnmFile() {
		return enmFile;
	}
	public double getThreshold(){
		if (CommonFun.isDouble(this.thresholdText.getText())){
			return Double.valueOf(this.thresholdText.getText()).doubleValue();
		}
		return 0;
	}
	
}
