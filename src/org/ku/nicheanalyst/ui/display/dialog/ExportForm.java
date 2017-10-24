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
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.ProgressMonitor;
import javax.swing.SpringLayout;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.ku.nicheanalyst.common.CommonFun;
import org.ku.nicheanalyst.common.ConfigInfo;
import org.ku.nicheanalyst.common.Message;
import org.ku.nicheanalyst.dataset.GSpaceData;
import org.ku.nicheanalyst.dataset.SpeciesDataset;
import org.ku.nicheanalyst.ui.display.Displayer;
import org.ku.nicheanalyst.ui.display.component.ui.JNumberTextField;
import org.ku.nicheanalyst.ui.display.component.ui.LabeledTextField;

/**
 * @author Huijie Qiao
 *
 */
public class ExportForm extends JDialog {
	private String vp;
	private String enm;
	private int repeats;
	private int number;
	private String target;
	private JComboBox vplistcombox;
	private JComboBox enmscombox;
	private JNumberTextField repeatstext;
	private JNumberTextField percentagetext; 
	private JLabel allrecordlabel;
	private JTextField targettext;
	private JTextField species_labeltext;
	private JLabel recordEstimatedLabel;
	private JCheckBox isNumberCheckbox;
	private Displayer theApp;
	private String species_label;
	private SpeciesDataset selectedData;
	private JLabel number_of_repetitions;
	private JLabel percentage_number;
	private LabeledTextField alpha;
	private LabeledTextField beta;
	private JComboBox method;
	public ExportForm(String[] vplist, String[] ENMs, String[] methods, Displayer theApp){
		
		this.theApp = theApp;
		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
		
		JPanel p2 = new JPanel();
		p2.setLayout(new BoxLayout(p2, BoxLayout.X_AXIS));
		JLabel l = new JLabel(Message.getString("select_a_species"), JLabel.TRAILING);
		
		vplistcombox = new JComboBox(vplist);
		vplistcombox.setSelectedIndex(0);
		vplistcombox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (e.getID()==1001){
					changeVS();
				}
			}
		});
		p2.add(l);
		p2.add(vplistcombox);
		p.add(p2);
		
		p2 = new JPanel();
		p2.setLayout(new BoxLayout(p2, BoxLayout.X_AXIS));
		
		l = new JLabel(Message.getString("select_an_enm"), JLabel.TRAILING);
		enmscombox = new JComboBox(ENMs);
		enmscombox.setSelectedIndex(0);
		p2.add(l);
		p2.add(enmscombox);
		p.add(p2);
		
		JPanel panel_1 = new JPanel();
		panel_1.setLayout(new BoxLayout(panel_1, BoxLayout.X_AXIS));
		
		l = new JLabel(Message.getString("species_label"), JLabel.TRAILING);
		species_labeltext = new JTextField();
		species_labeltext.setText("noname");
		panel_1.add(l);
		panel_1.add(species_labeltext);
		
		number_of_repetitions = new JLabel(Message.getString("number_of_repetitions"), JLabel.TRAILING);
		repeatstext = new JNumberTextField(3, JNumberTextField.NUMERIC);
		repeatstext.setText("10");
		panel_1.add(number_of_repetitions);
		panel_1.add(repeatstext);
		
		percentage_number = new JLabel(Message.getString("percentage-number"), JLabel.TRAILING);
		
		JPanel pp = new JPanel();
		pp.setLayout(new BoxLayout(pp, BoxLayout.X_AXIS));
		recordEstimatedLabel = new JLabel();
		
		percentagetext = new JNumberTextField(10, JNumberTextField.NUMERIC);
		percentagetext.setText("20");
		percentagetext.getDocument().addDocumentListener(
		new DocumentListener() {
			public void changedUpdate(DocumentEvent e) {
				changeVS();
			}
			public void removeUpdate(DocumentEvent e) {
				changeVS();
			}
			public void insertUpdate(DocumentEvent e) {
				changeVS();
			}
		});

		isNumberCheckbox = new JCheckBox();
		isNumberCheckbox.setText(Message.getString("isnumber"));
		isNumberCheckbox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				changeVS();
			}
		});
		pp.add(percentage_number);
		pp.add(percentagetext);
		pp.add(recordEstimatedLabel);
		pp.add(isNumberCheckbox);
		panel_1.add(pp);
		
		p.add(panel_1);
		
		
		panel_1 = new JPanel();
		panel_1.setLayout(new BoxLayout(panel_1, BoxLayout.X_AXIS));
		
		l = new JLabel(Message.getString("export_method"));
		method = new JComboBox(methods);
		method.setSelectedIndex(0);
		method.addActionListener (new ActionListener () {
		    public void actionPerformed(ActionEvent e) {
		        changeMethod();
		    }
		});
		panel_1.add(l);
		panel_1.add(method);
		
		beta = new LabeledTextField(Message.getString("beta"), "random");
		panel_1.add(beta);
		
		alpha = new LabeledTextField(Message.getString("alpha"), "-0.05");
		panel_1.add(alpha);
		
		p.add(panel_1);
		
		
		allrecordlabel = new JLabel("", JLabel.TRAILING);
		l = new JLabel("", JLabel.TRAILING);
		p.add(l);
		p.add(allrecordlabel);
		
		p2 = new JPanel();
		p2.setLayout(new BoxLayout(p2, BoxLayout.X_AXIS));
		l = new JLabel(Message.getString("select_a_folder"), JLabel.TRAILING);
		JPanel p_file = new JPanel();
		p_file.setLayout(new BoxLayout(p_file, BoxLayout.X_AXIS));
		
		JButton select_folder = new JButton("...");
		select_folder.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				selectFolder();
			}
		});
		
		targettext = new JTextField("");
		p_file.add(targettext);
		p_file.add(select_folder);
		p2.add(l);
		p2.add(p_file);
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
		changeVS();
		
		setSize(new Dimension(700, 210));
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);
		
		setModal(true);
		
	}
	protected void changeMethod() {
		if (method.getSelectedItem().toString().equals(Message.getString("probability"))){
			if (!CommonFun.isDouble(this.beta.getText())){
				this.beta.setText("0.7");
			}
		}
		
	}
	/**
	 * 
	 */
	protected void changeVS() {
		String vsLabel = (String) vplistcombox.getSelectedItem();
		selectedData = this.theApp.getTheMenu().getVirtualspecies().get(vsLabel).getVs();
		if (selectedData!=null){
			getNumber();
			this.recordEstimatedLabel.setText(String.format("%d/%d", this.number, this.selectedData.getVs().size()));
		}
	}
	/**
	 * 
	 */
	public int getNumber() {
		this.number = 0;
		if (selectedData==null){
			return this.number;
		}
		if (CommonFun.isDouble(this.percentagetext.getText())){
			if (this.isNumberCheckbox.isSelected()){
				this.number = (int) Math.round(Double.valueOf(this.percentagetext.getText()));
				this.percentage_number.setText(Message.getString("number"));
			}else{
				this.number = (int) Math.round(this.selectedData.getVs().size() * Double.valueOf(this.percentagetext.getText()) / 100f);
				this.percentage_number.setText(Message.getString("percentage"));
			}
			if (this.number>this.selectedData.getVs().size()){
				this.number = this.selectedData.getVs().size();
			}
		}
		return this.number;
	}
	/**
	 * 
	 */
	protected void selectFolder() {
		JFileChooser fc = new JFileChooser();
		fc.setDialogTitle(Message.getString("select_a_folder"));
		fc.setSelectedFile(new File(theApp.getLastFolder()));
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int returnVal = fc.showSaveDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			String target = (fc.getSelectedFile().exists())?
					fc.getSelectedFile().getAbsolutePath():fc.getSelectedFile().getParent();
			theApp.setLastFolder(target);
			this.targettext.setText(target);
		}
		
	}
	private void cancelClick() {
		this.target = null;
		this.dispose();
		
	}
	private void okClick(){
		boolean ispass = true;
		boolean isshowerror = true;
		this.target = this.targettext.getText();
		File f = new File(target);
		if (!f.exists()){
			ispass = false;
			this.targettext.setForeground(Color.red);
		}else{
			this.targettext.setForeground(Color.black);
		}
		
		this.vp = this.vplistcombox.getSelectedItem().toString();
		this.enm = this.enmscombox.getSelectedItem().toString();
		
		if (CommonFun.isInteger(this.repeatstext.getText())){
			this.repeats = Integer.valueOf(this.repeatstext.getText());
			this.repeatstext.setForeground(Color.black);
		}else{
			ispass = false;
			this.repeatstext.setForeground(Color.red);
		}
		
		if (CommonFun.isDouble(this.percentagetext.getText())){
			getNumber();
			this.number = Integer.valueOf(this.percentagetext.getText());
			this.percentagetext.setForeground(Color.black);
		}else{
			ispass = false;
			this.percentagetext.setForeground(Color.red);
		}
		this.species_label = this.species_labeltext.getText();
		
		if (getAlpha()>0){
			int dialogButton = JOptionPane.YES_NO_OPTION;
			int dialogResult = JOptionPane.showConfirmDialog (null, Message.getString("na_alpha"), "Warning", dialogButton);
			if(dialogResult != JOptionPane.YES_OPTION){
				ispass = false;
				isshowerror = false;
			}
		}
		if (!ispass){
			if (isshowerror){
				theApp.ShowAlert(Message.getString("error_warning"));
			}
		}else{
			setVisible(false);
		}
	}
	public String getVp() {
		return vp;
	}
	public String getEnm() {
		return enm;
	}
	public int getRepeats() {
		return repeats;
	}
	
	public String getTarget() {
		return target;
	}
	public String getSpecies_label() {
		return species_label;
	}
	public double getBeta() {
		if (CommonFun.isDouble(this.beta.getText())){
			return Double.valueOf(this.beta.getText()).doubleValue();
		}
		return -1;
	}
	public double getAlpha() {
		if (CommonFun.isDouble(this.alpha.getText())){
			double v = Double.valueOf(this.alpha.getText()).doubleValue();			
			return v;
		}
		return -0.05;
	}
	public String getMethod(){
		return (String) this.method.getSelectedItem();
	}
}
