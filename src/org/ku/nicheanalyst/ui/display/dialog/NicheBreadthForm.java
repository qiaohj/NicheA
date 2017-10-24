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
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import org.ku.nicheanalyst.common.CommonFun;
import org.ku.nicheanalyst.common.Const;
import org.ku.nicheanalyst.common.Message;
import org.ku.nicheanalyst.dataset.EllipsoidParameters;
import org.ku.nicheanalyst.dataset.GompertzCurveParameters;
import org.ku.nicheanalyst.dataset.NicheBreadthParameters;
import org.ku.nicheanalyst.ui.display.Displayer;
import org.ku.nicheanalyst.ui.display.component.ui.LabeledTextArea;
import org.ku.nicheanalyst.ui.display.component.ui.LabeledTextField;

/**
 * @author Huijie Qiao
 *
 */
public class NicheBreadthForm extends JDialog implements ActionListener {
	private Displayer theApp;
	private NicheBreadthVariablePanel[] variables;
	private LabeledTextArea vs;
	private LabeledTextField duration;
	private LabeledTextField migrationAbility;
	private LabeledTextField initialRandomSpeciesSeeds;
	private SelectFilePanal target;
	private EllipsoidParameters ellipsoidParameters;
	private NicheBreadthParameters parameters;
	private JRadioButton radioRandomSeed;
	private JRadioButton radioManualSeed;
	private SelectFilePanal initialManualSpeciesSeeds;
	public NicheBreadthForm(Displayer theApp){
		
		this.theApp = theApp;
		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		
		variables = new NicheBreadthVariablePanel[3];
		for (int i=0;i<3;i++){
			variables[i] = new NicheBreadthVariablePanel(this.theApp, 
					String.format("%s %d: ", 
							Message.getString("niche_breadth_variable"), i + 1));
			add(variables[i]);
			
		}
		vs = new LabeledTextArea(LabeledTextArea.X_AXIS, Message.getString("niche_breadth_definition")
				, "", 40, 6);
		add(vs);
		
		duration = new LabeledTextField(LabeledTextField.X_AXIS, Message.getString("duration"), "10000");
		add(duration);
		
		migrationAbility = new LabeledTextField(Message.getString("migration_ability"), "1.5");
		add(migrationAbility);
		
		JPanel initialSpeciesPanel1 = new JPanel();
		initialSpeciesPanel1.setLayout(new BoxLayout(initialSpeciesPanel1, BoxLayout.X_AXIS));
		radioRandomSeed = new JRadioButton("");
		initialSpeciesPanel1.add(radioRandomSeed);
		radioRandomSeed.setSelected(false);
		radioRandomSeed.setActionCommand(Message.getString("initial_random_species_seeds"));
		radioRandomSeed.addActionListener(this);
		
		initialRandomSpeciesSeeds = new LabeledTextField(Message.getString("initial_random_species_seeds"), "1000");
		initialRandomSpeciesSeeds.setEnabled(false);
		
		initialSpeciesPanel1.add(initialRandomSpeciesSeeds);
		add(initialSpeciesPanel1);
		
		JPanel initialSpeciesPanel2 = new JPanel();
		initialSpeciesPanel2.setLayout(new BoxLayout(initialSpeciesPanel2, BoxLayout.X_AXIS));
		initialManualSpeciesSeeds = new SelectFilePanal(this.theApp,
				Message.getString("initial_manual_species_seeds"), "", false, null, false);
		initialManualSpeciesSeeds.setEnabled(true);
		
		radioManualSeed = new JRadioButton("");
		radioManualSeed.setSelected(true);
		radioManualSeed.setActionCommand(Message.getString("initial_manual_species_seeds"));
		radioManualSeed.addActionListener(this);
		initialSpeciesPanel2.add(radioManualSeed);
		initialSpeciesPanel2.add(initialManualSpeciesSeeds);
		add(initialSpeciesPanel2);
		ButtonGroup group = new ButtonGroup();
		group.add(radioRandomSeed);
		group.add(radioManualSeed);

		
		target = new SelectFilePanal(this.theApp, Message.getString("niche_breadth_target"), "", true, null, true);
		add(target);
		
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
		
		JButton show_info = new JButton(Message.getString("show_info"));
		show_info.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				showInfoClick();
			}
		});
		p2.add(show_info);
		p2.add(ok);
		p2.add(cancel);
		add(p2);
		
		setSize(new Dimension(600, 450));
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);

		setModal(true);
		
	}
	
	private StringBuilder getInfo(){
		StringBuilder log = new StringBuilder();
		
		for (int i=0;i<3;i++){
			log.append(String.format("-----------%s %d-----------%n" ,Message.getString("niche_breadth_variable"), i + 1));
			if (this.variables[i].getParameters()!=null){
				log.append(this.variables[i].getParameters().getDescription());
			}
		}
		
		log.append(String.format("-----------%s-----------%n", Message.getString("niche_breadth_definition")));
		log.append(this.vs.getText());
		log.append(String.format("%n-----------%s-----------%n", ""));
		
		log.append(String.format("%s%s%n", 
				Message.getString("duration"), duration.getText()));
		log.append(String.format("%s%s%n", 
				Message.getString("migration_ability"), migrationAbility.getText()));
		if (this.radioRandomSeed.isSelected()){
			log.append(String.format("%s%s%n", 
					Message.getString("initial_random_species_seeds"), initialRandomSpeciesSeeds.getText()));
		}else{
			log.append(String.format("%s%s%n", 
					Message.getString("initial_manual_species_seeds"), initialManualSpeciesSeeds.getText()));
		}
		log.append(String.format("%s%s%n", 
				Message.getString("niche_breadth_target"), target.getText()));
		return log;
	}
	protected void showInfoClick() {
		
		
		LogForm logForm = new LogForm("", getInfo(), this.theApp);
		
	}


	public String getTarget(){
		if (this.target==null){
			return null;
		}else{
			return this.target.getText();
		}
	}
	public NicheBreadthParameters getParameters(){
		return parameters;
	}
	public int getDuration(){
		return Integer.valueOf(this.duration.getText()).intValue();
	}
	public double getMigrationAbility(){
		return Double.valueOf(this.migrationAbility.getText()).doubleValue();
	}
	public int getInitialRandomSpeciesSeeds(){
		return Integer.valueOf(this.initialRandomSpeciesSeeds.getText()).intValue();
	}
	public String getInitialManualSpeciesSeeds(){
		return this.initialManualSpeciesSeeds.getText();
	}
	private void cancelClick() {
		this.target = null;
		this.dispose();
	}
	
	private void okClick(){
		parameters = null;
		boolean ispass = true;
		File f = new File(this.target.getText());
		if (!f.exists()){
			ispass = false;
			this.target.requestFocus();
			
		}else{
			for (int i=0;i<2;i++){
				if (variables[i]==null){
					ispass = false;
					this.variables[i].requestFocus();
					break;
				}else{
					if (variables[i].getParameters()==null){
						ispass = false;
						this.variables[i].requestFocus();
						break;
					}
				}
			}
			if (ispass){
				this.ellipsoidParameters = new EllipsoidParameters(this.vs.getText());
				if (this.ellipsoidParameters.getErrorMsg()!=null){
					
					this.vs.requestFocus();
					ispass = false;
				}
			}
			if (ispass){
				if (!CommonFun.isInteger(this.duration.getText())){
					ispass = false;
					this.duration.requestFocus();
				}
			}
			if (ispass){
				if (!CommonFun.isDouble(this.migrationAbility.getText())){
					ispass = false;
					this.migrationAbility.requestFocus();
				}
			}
			if (ispass){
				if (this.radioRandomSeed.isSelected()){
					if (!CommonFun.isInteger(this.initialRandomSpeciesSeeds.getText())){
						ispass = false;
						this.initialRandomSpeciesSeeds.requestFocus();
					}
				}
				if (this.radioManualSeed.isSelected()){
					f = new File(this.initialManualSpeciesSeeds.getText());
					if (!f.exists()){
						ispass = false;
						this.initialManualSpeciesSeeds.requestFocus();
					}
				}
			}
		}
		if (!ispass){
			theApp.ShowAlert(Message.getString("error_warning"));
		}else{
			String[] folder = new String[3];
			GompertzCurveParameters[] parameters = new GompertzCurveParameters[3];
			if (this.variables[2]!=null){
				folder = new String[2];
				parameters = new GompertzCurveParameters[2];
			}
			for (int i=0;i<folder.length;i++){
				folder[i] = this.variables[i].getFolder();
				parameters[i] = this.variables[i].getParameters();
			} 
			int initialType = 0;
			if (this.radioManualSeed.isSelected()){
				initialType = 1;
			}
			this.parameters = new NicheBreadthParameters(parameters, folder, 
					this.ellipsoidParameters, Integer.valueOf(this.duration.getText()).intValue(),
					Double.valueOf(this.migrationAbility.getText()).doubleValue(), 
					Integer.valueOf(this.initialRandomSpeciesSeeds.getText()).intValue(),
					this.initialManualSpeciesSeeds.getText(),
					getInfo().toString(), initialType);
			setVisible(false);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals(Message.getString("initial_random_species_seeds"))){
			this.initialManualSpeciesSeeds.setEnabled(false);
			this.initialRandomSpeciesSeeds.setEnabled(true);
		}
		if (e.getActionCommand().equals(Message.getString("initial_manual_species_seeds"))){
			this.initialManualSpeciesSeeds.setEnabled(true);
			this.initialRandomSpeciesSeeds.setEnabled(false);
		}
		
	}
	
}
