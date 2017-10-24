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
import org.ku.nicheanalyst.ui.filefilters.RasterFileFilter;

/**
 * @author Huijie Qiao
 *
 */
public class PCAGeneratorForm extends JDialog {
	private File[] selectFiles_origin;
	private JList selectFilesList_origin;
	private DefaultListModel listModel_origin;
	
	private File[] selectFiles_target;
	private JList selectFilesList_target;
	private DefaultListModel listModel_target;
	
	private JTextField targetText;
	private Displayer theApp;
	private String target;
	private JCheckBox is_trans;
	
	public PCAGeneratorForm(Displayer theApp){
		this.theApp = theApp;
		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
		p.setBorder(BorderFactory.createTitledBorder(Message.getString("select_pca_files")));
		p.setToolTipText(Comment.getString("select_pca_files"));
		JPanel p2 = new JPanel();
		p2.setLayout(new BoxLayout(p2, BoxLayout.X_AXIS));
		
		listModel_origin = new DefaultListModel();
		selectFilesList_origin = new JList(listModel_origin);
		selectFilesList_origin.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);		
		selectFilesList_origin.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		selectFilesList_origin.setVisibleRowCount(-1);
		JScrollPane plist = new JScrollPane(selectFilesList_origin);
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
		is_trans = new JCheckBox(Message.getString("is_pca_trans"));
		is_trans.setSelected(false);
		add(is_trans);
		p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
		p.setBorder(BorderFactory.createTitledBorder(Message.getString("select_pca_files_target")));
		p.setToolTipText(Comment.getString("select_pca_files"));
		
		p2 = new JPanel();
		p2.setLayout(new BoxLayout(p2, BoxLayout.X_AXIS));
		
		listModel_target = new DefaultListModel();
		selectFilesList_target = new JList(listModel_target);
		selectFilesList_target.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);		
		selectFilesList_target.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		selectFilesList_target.setVisibleRowCount(-1);
		plist = new JScrollPane(selectFilesList_target);
		plist.setPreferredSize(new Dimension(250, 200));
		p2.add(plist);
		
		p3 = new JPanel();
		p3.setLayout(new BoxLayout(p3, BoxLayout.Y_AXIS));
		addB = new JButton(Message.getString("add_files"));
		delB = new JButton(Message.getString("del_files"));
		addB.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				selectFolder_target();
			}
		});
		delB.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				removeFiles_target();
			}
		});
		p3.add(addB);
		p3.add(delB);
		p2.add(p3);
		
		p.add(p2);
		add(p);
		
		p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
		p.setBorder(BorderFactory.createTitledBorder(Message.getString("select_pca_folder")));
		p.setToolTipText(Comment.getString("select_pca_folder"));
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
		setSize(new Dimension(600, 580));
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);
		setModal(true);
	}
	protected void removeFiles_target() {
		int[] selected = selectFilesList_target.getSelectedIndices();
		Arrays.sort(selected);
		for (int i=selected.length-1;i>=0;i--){
			listModel_target.remove(selected[i]);
		}
		
	}
	protected void selectFolder_target() {
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
				listModel_target.add(listModel_target.size(), t);
			}
			theApp.setLastFolder(target[0].getAbsolutePath());
//			this.targettext.setText(target);
		}
		
	}
	/**
	 * 
	 */
	protected void selectTarget() {
		JFileChooser fc = new JFileChooser();
		fc.setDialogTitle(Message.getString("select_a_folder"));
		fc.setSelectedFile(new File(theApp.getLastFolder()));
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int returnVal = fc.showSaveDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File target = (fc.getSelectedFile().exists())?
					fc.getSelectedFile().getAbsoluteFile():fc.getSelectedFile().getParentFile();
			this.targetText.setText(target.getAbsolutePath());
		}
		
	}
	/**
	 * 
	 */
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
	private void cancelClick() {
		this.target = null;
		this.dispose();
		
	}
	private void okClick(){
		boolean isPass = true;
		String errormsg = "";
		this.selectFiles_origin = new File[listModel_origin.size()];
		for (int i=0;i<listModel_origin.size();i++){
			this.selectFiles_origin[i] = new File(listModel_origin.get(i).toString());
		}
		
		this.selectFiles_target = new File[listModel_target.size()];
		for (int i=0;i<listModel_target.size();i++){
			this.selectFiles_target[i] = new File(listModel_target.get(i).toString());
		}
		if (selectFiles_origin.length<=1){
			isPass = false;
			errormsg = Message.getString("pca_no_enough_layer_error");
		}
		if (isTrans()){
			if (selectFiles_origin.length!=selectFiles_target.length){
				isPass = false;
				errormsg = Message.getString("pca_target_langth_error");
			}
		}
		this.target = this.targetText.getText();
		File f = new File(target);
		if (!f.exists()){
			isPass = false;
			errormsg = String.format(Message.getString("no_folder"), this.target);
		}
		
		if (isPass){
			setVisible(false);
		}else{
			this.theApp.ShowAlert(errormsg);
		}
	}
	public boolean isTrans(){
		return this.is_trans.isSelected();
	}
	public File[] getSelectFiles_origin() {
		return selectFiles_origin;
	}
	public File[] getSelectFiles_target() {
		return selectFiles_target;
	}
	public String getTarget() {
		return target;
	}
	
}
