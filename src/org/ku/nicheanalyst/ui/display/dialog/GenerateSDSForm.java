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

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.ku.nicheanalyst.common.Const;
import org.ku.nicheanalyst.common.Message;
import org.ku.nicheanalyst.ui.display.Displayer;
import org.ku.nicheanalyst.ui.filefilters.MVEFileFilter;

/**
 * @author Huijie Qiao
 *
 */
public class GenerateSDSForm extends JDialog {
	private String[] lls;
	private JTextArea textFieldll;
	private String target;
	private JTextField textFieldtarget;
	private Displayer theApp;
	private int type;
	public GenerateSDSForm(Displayer theApp, int type){
		//0: occurrence 1: mve
		this.type = type;
		this.theApp = theApp;
		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
		JLabel l = new JLabel(
				(this.type==1)?Message.getString("select_mve_file"):Message.getString("select_ll_file"), 
				JLabel.TRAILING);
		textFieldll = new JTextArea(12, 100);
		textFieldll.setEditable(false);
		
		JButton select_file = new JButton("...");
		select_file.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				selectFile();
			}
		});
		p.add(l);
		
		p.add(select_file);
		add(p);
		add(textFieldll);
		l = new JLabel(Message.getString("save_species_dataset"), JLabel.TRAILING);
		JPanel p_file = new JPanel();
		p_file.setLayout(new BoxLayout(p_file, BoxLayout.X_AXIS));
		
		JButton select_folder = new JButton("...");
		select_folder.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				selectFolder();
			}
		});
		p_file.add(l);
		textFieldtarget = new JTextField("");
		p_file.add(textFieldtarget);
		p_file.add(select_folder);
		
		add(p_file);
		
        
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
		
		
		setSize(new Dimension(600, 300));
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);
		
		setModal(true);
		
	}
	/**
	 * 
	 */
	protected void selectFile() {
		JFileChooser fc = new JFileChooser();
		fc.setDialogTitle((this.type==1)?Message.getString("select_mve_file"):Message.getString("select_ll_file"));
		fc.setSelectedFile(new File(theApp.getLastFolder()));
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fc.setMultiSelectionEnabled(true);
		if (this.type==1){
			fc.setFileFilter(new MVEFileFilter());
		}
		int returnVal = fc.showOpenDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			String target = fc.getSelectedFile().getAbsolutePath();
			theApp.setLastFolder(target);
			StringBuilder sb = new StringBuilder();
			for (File file : fc.getSelectedFiles()){
				target = file.getAbsolutePath();
				theApp.setLastFolder(target);
				sb.append(target + Const.LineBreak);
			}
			this.textFieldll.setText(sb.toString());	
		}
		
	}
	
	protected void selectFolder() {
		JFileChooser fc = new JFileChooser();
		fc.setDialogTitle(Message.getString("save_species_dataset"));
		fc.setSelectedFile(new File(theApp.getLastFolder()));
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int returnVal = fc.showSaveDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			String target = "";
			if (fc.getSelectedFile().isFile()){
				target = fc.getSelectedFile().getParent();
			}else{
				target = (fc.getSelectedFile().exists())?
					fc.getSelectedFile().getAbsolutePath():fc.getSelectedFile().getParent();
			}
			theApp.setLastFolder(target);
			this.textFieldtarget.setText(target);
		}
		
	}
	private void cancelClick() {
		this.target = null;
		this.dispose();
		
	}
	private void okClick(){
		boolean ispass = true;
		lls = textFieldll.getText().split(Const.LineBreak);
		for (String ll : lls){
			File f = new File(ll);
			if (!f.exists()){
				theApp.ShowAlert(String.format(Message.getString("no_file"), ll));
				ispass = false;
				this.textFieldll.selectAll();
			}
		}
		File f = new File(this.textFieldtarget.getText());
		if (!f.exists()){
			theApp.ShowAlert(String.format(Message.getString("no_file"), this.textFieldtarget.getText()));
			ispass = false;
			this.textFieldtarget.selectAll();
		}
		
		if (!ispass){
			theApp.ShowAlert(Message.getString("error_warning"));
		}else{
			setVisible(false);
		}
	}
	public String[] getLL() {
		lls = textFieldll.getText().split(Const.LineBreak);
		
		return lls;
	}
	public String getTarget() {
		target = textFieldtarget.getText();
		File f = new File(this.textFieldtarget.getText());
		if (!f.exists()){
			target = null;
		}
		return target;
	}
	
	
}
