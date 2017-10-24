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
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import org.ku.nicheanalyst.common.Comment;
import org.ku.nicheanalyst.common.CommonFun;
import org.ku.nicheanalyst.common.Message;
import org.ku.nicheanalyst.ui.display.Displayer;
import org.ku.nicheanalyst.ui.display.component.ui.LabeledTextField;
import org.ku.nicheanalyst.ui.filefilters.RasterFileFilter;

/**
 * @author Huijie Qiao
 *
 */
public class PartialROCForm extends JDialog {
	
	private SelectFilePanal target;
	private Displayer theApp;
	private File[] selectFiles;
	private JList selectFilesList;
	private DefaultListModel listModel;
	private SelectFilePanal occ;
	private LabeledTextField E;
	public PartialROCForm(Displayer theApp){
		this.theApp = theApp;
		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
		
		
		p.setBorder(BorderFactory.createTitledBorder(Message.getString("select_enm_file")));
		p.setToolTipText(Comment.getString("select_enm_file"));
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
		
		occ = new SelectFilePanal(theApp, Message.getString("select_ll_file"), "", false, null, false);
		add(occ);
		
		
		E = new LabeledTextField(Message.getString("E_string"), "0.1,0.5,0.9");
		add(E);
		
		this.target = new SelectFilePanal(theApp, Message.getString("target"), "", true, null, true);
		add(target);
		p2 = new JPanel();
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
		setSize(new Dimension(600, 380));
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);
//		this.enm_result.setText("E:\\DarwinFox\\ENM_Results\\BRT_ALL.tif");
//		this.occ.setText("E:\\DarwinFox\\GPlus\\DarwinFox_All\\ll.txt");
//		this.target.setText("E:\\DarwinFox\\ROC\\BRT_ALL");
//		this.E.setText("0.1,0.5");
		setModal(true);
	}
	
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
	protected void removeFiles() {
		int[] selected = selectFilesList.getSelectedIndices();
		Arrays.sort(selected);
		for (int i=selected.length-1;i>=0;i--){
			listModel.remove(selected[i]);
		}
		
	}
	private void cancelClick() {
		this.target.setText("");
		this.dispose();
		
	}
	public File[] getSelectFiles() {
		this.selectFiles = new File[listModel.size()];
		for (int i=0;i<listModel.size();i++){
			this.selectFiles[i] = new File(listModel.get(i).toString());
		}
		return selectFiles;
	}
	private void okClick(){
		boolean ispass = true;
		HashSet<Double> thresholds = getEs();
		if (thresholds.size()==0){
			this.E.requestFocus();
			ispass = false;
		}
		
		File[] seleFiles = getSelectFiles();
		if (seleFiles.length==0){
			this.selectFilesList.requestFocus();
			ispass = false;
		} 	
		
		File f = new File(this.occ.getText());
		if (!f.exists()){
			this.occ.requestFocus();
			ispass = false;
		}
		
		f = new File(this.target.getText());
		if (!f.exists()){
			this.target.requestFocus();
			ispass = false;
		}
		
		if (!ispass){
			theApp.ShowAlert(Message.getString("error_warning"));
		}else{
			setVisible(false);
		}
	}
	
	public HashSet<Double> getEs(){
		HashSet<Double> Es = new HashSet<Double>();
		String[] str = this.E.getText().split(",");
		for (String s : str){
			if (CommonFun.isDouble(s)){
				Es.add(Double.valueOf(s));
			}
		}
		return Es;
	}
	public String getTarget() {
		return target.getText();
	}
	public String getOCC(){
		return occ.getText();
	}
}
