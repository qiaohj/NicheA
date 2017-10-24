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
import java.util.HashSet;

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
import org.ku.nicheanalyst.common.Message;
import org.ku.nicheanalyst.ui.display.Displayer;
import org.ku.nicheanalyst.ui.display.component.ui.JNumberTextField;
import org.ku.nicheanalyst.ui.filefilters.RasterFileFilter;

/**
 * @author Huijie Qiao
 *
 */
public class VariablesStatForm extends JDialog {
	private File[] selectFiles;
	private JList selectFilesList;
	private JTextField targetText;
	private Displayer theApp;
	private DefaultListModel listModel;
	private String target;
	private JComboBox percentileType;
	private HashSet<String> averagePercentiles;
	private JList calculatedPercentiles;
	private DefaultListModel listModel_calculatedPercentiles;
	private JNumberTextField percentileText;
	private JNumberTextField nodataText;
	private JNumberTextField stepText;
	private double nodata;
	private int step;
	public VariablesStatForm(Displayer theApp){
		this.theApp = theApp;
		averagePercentiles = new HashSet<String>();
		
		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		
		JPanel p = new JPanel();
		
		p.setBorder(BorderFactory.createTitledBorder(Message.getString("select_tiff_files")));
		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
		p.setToolTipText(Comment.getString("select_tiff_files_varuablestat"));
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
		p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
		p.setBorder(BorderFactory.createTitledBorder(Message.getString("env_nodata")));
		p.setToolTipText(Comment.getString("env_nodata"));
		nodataText = new JNumberTextField(10, JNumberTextField.DECIMAL);
		nodataText.setText("-9999");
		p.add(nodataText);
		
		JLabel l = new JLabel(Message.getString("step"));
		p.add(l);
		stepText = new JNumberTextField(10, JNumberTextField.NUMERIC);
		stepText.setText("1");
		p.add(stepText);
		
		add(p);
		p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
		p.setBorder(BorderFactory.createTitledBorder(Message.getString("select_percentile_method")));
		p.setToolTipText(Comment.getString("select_percentile_method"));
		p2 = new JPanel();
		p2.setLayout(new BoxLayout(p2, BoxLayout.X_AXIS));
		percentileType = new JComboBox(new String[]{
				Message.getString("percentile"),
				Message.getString("average_lower_percentile"),
				Message.getString("average_higher_percentile")});
		p2.add(percentileType);
		percentileText = new JNumberTextField(3, JNumberTextField.DECIMAL);
		percentileText.setText("50");
		p2.add(percentileText);
		JButton addB2 = new JButton(Message.getString("add_method"));
		
		addB2.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				add_Method();
			}
		});
		p2.add(addB2);
		p.add(p2);
		p2 = new JPanel();
		listModel_calculatedPercentiles = new DefaultListModel();
		calculatedPercentiles = new JList(listModel_calculatedPercentiles);
		calculatedPercentiles.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);		
		calculatedPercentiles.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		calculatedPercentiles.setVisibleRowCount(-1);
		plist = new JScrollPane(calculatedPercentiles);
		plist.setPreferredSize(new Dimension(300, 80));
		p2.add(plist);
		
		p3 = new JPanel();
		p3.setLayout(new BoxLayout(p3, BoxLayout.Y_AXIS));
		
		JButton delB2 = new JButton(Message.getString("del_method"));
		delB2.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				del_Method();
			}
		});
		
		
		p3.add(delB2);
		p2.add(p3);
		
		p.add(p2);
		add(p);
		
		
		p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
		p.setBorder(BorderFactory.createTitledBorder(Message.getString("select_result_folder")));
		p.setToolTipText(Comment.getString("select_result_folder"));
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
	/**
	 * 
	 */
	protected void del_Method() {
		int[] selected = calculatedPercentiles.getSelectedIndices();
		Arrays.sort(selected);
		for (int i=selected.length-1;i>=0;i--){
			listModel_calculatedPercentiles.remove(selected[i]);
		}
		
	}
	/**
	 * 
	 */
	protected void add_Method() {
		String t = percentileType.getSelectedItem() + ":" + percentileText.getText();
		if (!listModel_calculatedPercentiles.contains(t)){
			listModel_calculatedPercentiles.add(listModel_calculatedPercentiles.size(), t);
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
			if (target.isDirectory()){
				this.targetText.setText(target.getAbsolutePath());
			}else{
				this.targetText.setText(target.getParent());
			}
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
		if (CommonFun.isInteger(stepText.getText())){
			step = stepText.getInt();
		}else{
			step = 100000;
		}
		if (CommonFun.isDouble(nodataText.getText())){
			nodata = nodataText.getDouble();
		}else{
			nodata = -9999;
		}
		this.selectFiles = new File[listModel.size()];
		for (int i=0;i<listModel.size();i++){
			this.selectFiles[i] = new File(listModel.get(i).toString());
		}
		averagePercentiles = new HashSet<String>();
		for (int i=0;i<listModel_calculatedPercentiles.size();i++){
			averagePercentiles.add(listModel_calculatedPercentiles.get(i).toString());
		}
		this.target = this.targetText.getText();
		File f = new File(target);
		if (!f.exists()){
			this.theApp.ShowAlert(String.format(Message.getString("no_folder"), this.target));
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
	public HashSet<String> getAveragePercentiles() {
		return averagePercentiles;
	}
	public double getNodata() {
		return nodata;
	}
	/**
	 * @return
	 */
	public int getStep() {
		return step;
	}
	
}
