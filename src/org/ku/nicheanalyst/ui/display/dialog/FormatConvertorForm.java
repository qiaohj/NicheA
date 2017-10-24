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

import org.ku.nicheanalyst.common.CommonFun;
import org.ku.nicheanalyst.common.Message;
import org.ku.nicheanalyst.ui.display.Displayer;
import org.ku.nicheanalyst.ui.filefilters.ASCFileFilter;
import org.ku.nicheanalyst.ui.filefilters.TiffFileFilter;

/**
 * @author Huijie Qiao
 *
 */
public class FormatConvertorForm extends JDialog {
	private File[] selectFiles;
	private String target;
	private String targetFormat;
	private JList selectFilesList;
	private JTextField targetText;
	private JComboBox targetFormatCombobox;
	private Displayer theApp;
	private DefaultListModel listModel;
	public FormatConvertorForm(Displayer theApp){
		this.theApp = theApp;
		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
		JLabel l = new JLabel(Message.getString("select_format_to_convert"));
		p.add(l);
		String[] possibilities = {Message.getString("asc"), Message.getString("gtiff")};
		targetFormatCombobox = new JComboBox(possibilities);
		p.add(targetFormatCombobox);
		add(p);
		
		p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
		l = new JLabel(Message.getString("select_file_to_convert"), JLabel.TRAILING);
		p.add(l);
		JPanel p2 = new JPanel();
		p2.setLayout(new BoxLayout(p2, BoxLayout.X_AXIS));
		
		listModel = new DefaultListModel();
		selectFilesList = new JList(listModel);
		selectFilesList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);		
		selectFilesList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
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
		l = new JLabel(Message.getString("save_converted_file"), JLabel.TRAILING);
		p.add(l);
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
		add(p);
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
		setSize(new Dimension(500, 280));
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);
		setModal(true);
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
				this.targetText.setText(target.getParentFile().getAbsolutePath());
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
		fc.setDialogTitle(Message.getString("select_a_file"));
		if (this.targetFormatCombobox.getSelectedItem().toString().equalsIgnoreCase(Message.getString("asc"))){
			fc.setFileFilter(new TiffFileFilter());
		}
		if (this.targetFormatCombobox.getSelectedItem().toString().equalsIgnoreCase(Message.getString("gtiff"))){
			fc.setFileFilter(new ASCFileFilter());
		}
		
		fc.setSelectedFile(new File(theApp.getLastFolder()));
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fc.setMultiSelectionEnabled(true);
		int returnVal = fc.showOpenDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File[] target = fc.getSelectedFiles();
			
			ArrayList<String> targets = new ArrayList<String>();
			
				for (File f : target){
					if (f.isFile()){
						targets.add(f.getAbsolutePath());
//						String ext = CommonFun.getExtension(f);
//						if (this.targetFormatCombobox.getSelectedItem().toString().equalsIgnoreCase(Message.getString("asc"))){
//							if (ext.equalsIgnoreCase("tif")||ext.equalsIgnoreCase("tiff")){
//								
//							}
//						}
//						if (this.targetFormatCombobox.getSelectedItem().toString().equalsIgnoreCase(Message.getString("gtiff"))){
//							if (ext.equalsIgnoreCase("asc")){
//								targets.add(f.getAbsolutePath());
//							}
//						}
					}
//					if (f.isDirectory()){
//						for (File f2 : f.listFiles()){
//							String ext = CommonFun.getExtension(f2);
//							if (this.targetFormatCombobox.getSelectedItem().toString().equalsIgnoreCase(Message.getString("asc"))){
//								if (ext.equalsIgnoreCase("tif")||ext.equalsIgnoreCase("tiff")){
//									targets.add(f2.getAbsolutePath());
//								}
//							}
//							if (this.targetFormatCombobox.getSelectedItem().toString().equalsIgnoreCase(Message.getString("gtiff"))){
//								if (ext.equalsIgnoreCase("asc")){
//									targets.add(f2.getAbsolutePath());
//								}
//							}
//						}
//					}
				}
			
			for (String t : targets){
				listModel.add(listModel.size(), t);
			}
			if (targets.size()>0){
				theApp.setLastFolder(targets.get(0));
			}
//			this.targettext.setText(target);
		}
		
	}
	private void cancelClick() {
		this.target = null;
		this.dispose();
		
	}
	private void okClick(){
		this.selectFiles = new File[listModel.size()];
		for (int i=0;i<listModel.size();i++){
			this.selectFiles[i] = new File(listModel.get(i).toString());
		}
		this.target = this.targetText.getText();
		File f = new File(target);
		if (!f.exists()){
			this.theApp.ShowAlert(String.format(Message.getString("no_folder"), this.target));
		}else{
			this.targetFormat = this.targetFormatCombobox.getSelectedItem().toString();
			setVisible(false);
		}
	}
	public File[] getSelectFiles() {
		return selectFiles;
	}
	public String getTarget() {
		return target;
	}
	public String getTargetFormat() {
		return targetFormat;
	}
	
}
