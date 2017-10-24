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
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import org.ku.nicheanalyst.common.Message;
import org.ku.nicheanalyst.dataset.GompertzCurveParameters;
import org.ku.nicheanalyst.ui.display.Displayer;
import org.ku.nicheanalyst.ui.display.component.ui.LabeledTextField;
import org.ku.nicheanalyst.ui.filefilters.RasterFileFilter;

/**
 * @author Huijie Qiao
 *
 */
public class VariableGeneratorForm extends JDialog {
	private String interglacial;
	private String glacial;
	private LabeledTextField glacialText;
	private LabeledTextField interglacialText;
	private LabeledTextField targetText;
	private Displayer theApp;
	private DefaultListModel listModel;
	private String target;
	private GompertzCurveParameters parameters;
	private JTextArea parametersTextArea;
	public VariableGeneratorForm(Displayer theApp){
		this.parameters = null;
		this.theApp = theApp;
		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
		JPanel p2 = new JPanel();
		p2.setLayout(new BoxLayout(p2, BoxLayout.X_AXIS));
		interglacialText = new LabeledTextField(LabeledTextField.Y_AXIS, Message.getString("interglacial_environmental_layer"), "");
		p2.add(interglacialText);
		JButton targetb = new JButton("...");
		p2.add(targetb);
		targetb.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				selectInterglacial();
			}
		});
		
		add(p2);
		
		
		p2 = new JPanel();
		p2.setLayout(new BoxLayout(p2, BoxLayout.X_AXIS));
		glacialText = new LabeledTextField(LabeledTextField.Y_AXIS, Message.getString("glacial_environmental_layer"), "");
		p2.add(glacialText);
		targetb = new JButton("...");
		p2.add(targetb);
		targetb.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				selectGlacial();
			}
		});
		add(p2);
		
		p2 = new JPanel();
		p2.setLayout(new BoxLayout(p2, BoxLayout.X_AXIS));
		targetText = new LabeledTextField(LabeledTextField.Y_AXIS, Message.getString("select_variable_folder"), "");
		p2.add(targetText);
		targetb = new JButton("...");
		p2.add(targetb);
		targetb.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				selectTarget();
			}
		});
		
		add(p2);
		
		
		
		
		
		p2 = new JPanel();
		p2.setLayout(new BoxLayout(p2, BoxLayout.X_AXIS));
		parametersTextArea = new JTextArea(9, 50);
		parametersTextArea.setEditable(false);
		parametersTextArea.setAutoscrolls(true);
		p2.add(parametersTextArea);
		targetb = new JButton(Message.getString("set_parameters"));
		p2.add(targetb);
		targetb.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				setParameters();
			}
		});
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
		setSize(new Dimension(500, 410));
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);
		setModal(true);
	}
	protected void setParameters() {
		CurveForm form = new CurveForm(this.parameters, this.theApp);
		form.setVisible(true);
		if (form.getParemeters()!=null){
			this.parameters = form.getParemeters();
			this.parametersTextArea.setText(this.parameters.getDescription());
		}
		
	}
	protected void selectInterglacial() {
		JFileChooser fc = new JFileChooser();
		fc.setDialogTitle(Message.getString("select_a_geotiff_file"));
		fc.setSelectedFile(new File(theApp.getLastFolder()));
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fc.setFileFilter(new RasterFileFilter());
		int returnVal = fc.showOpenDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			this.interglacialText.setText(fc.getSelectedFile().getAbsolutePath());
			this.theApp.setLastFolder(fc.getSelectedFile().getAbsolutePath());
		}
	}
	protected void selectGlacial() {
		JFileChooser fc = new JFileChooser();
		fc.setDialogTitle(Message.getString("select_a_geotiff_file"));
		fc.setSelectedFile(new File(theApp.getLastFolder()));
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fc.setFileFilter(new RasterFileFilter());
		int returnVal = fc.showOpenDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			this.glacialText.setText(fc.getSelectedFile().getAbsolutePath());
			this.theApp.setLastFolder(fc.getSelectedFile().getAbsolutePath());
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
	
	private void cancelClick() {
		this.target = null;
		this.dispose();
		
	}
	private void okClick(){
		this.interglacial = this.interglacialText.getText();
		File f = new File(interglacial);
		if (!f.exists()){
			this.theApp.ShowAlert(String.format(Message.getString("no_file"), this.interglacial));
		}else{
			this.glacial = this.glacialText.getText();
			f = new File(glacial);
			if (!f.exists()){
				this.theApp.ShowAlert(String.format(Message.getString("no_file"), this.glacial));
			}else{
				this.target = this.targetText.getText();
				f = new File(target);
				if (!f.exists()){
					this.theApp.ShowAlert(String.format(Message.getString("no_folder"), this.target));
				}else{
					setVisible(false);
				}
			}
		}
	}
	
	public String getTarget() {
		return target;
	}
	public String getInterglacial() {
		return interglacial;
	}
	public String getGlacial() {
		return glacial;
	}
	public GompertzCurveParameters getParameters() {
		return parameters;
	}
	
}
