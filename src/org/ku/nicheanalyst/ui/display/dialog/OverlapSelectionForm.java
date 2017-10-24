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
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.ku.nicheanalyst.common.CommonFun;
import org.ku.nicheanalyst.common.Message;
import org.ku.nicheanalyst.ui.display.Displayer;
import org.ku.nicheanalyst.ui.display.component.ui.JNumberTextField;

/**
 * @author Huijie Qiao
 *
 */
public class OverlapSelectionForm extends JDialog {
	private String[] vs1;
	private String[] vs2;
	private JComboBox vs_combobox1;
	private JComboBox vs_combobox2;
	private String[] vplist;
	private JNumberTextField precision_T;
//	private JNumberTextField maxsteps_T;
//	private int maxsteps;
	private double precision;
	private String overlap_method;
	private JComboBox overlap_method_combobox;
	private JCheckBox loop_comparison_checkbox1;
	private JCheckBox loop_comparison_checkbox2;
	private SelectFilePanal target;
	private Displayer theApp;
//	private JCheckBox unlimitedsteps_checkbox;
	public OverlapSelectionForm(String[] vplist, Displayer theApp){
		this.vplist = vplist;
		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		JPanel p = new JPanel(new SpringLayout());
		JLabel l = new JLabel(Message.getString("select_a_species"), JLabel.TRAILING);
		vs_combobox1 = new JComboBox(vplist);
		vs_combobox1.setSelectedIndex(0);
		loop_comparison_checkbox1 = new JCheckBox(Message.getString("loop_comparison"));
		loop_comparison_checkbox1.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                changeCombobox(1);
            }
        });
		p.add(l);
		JPanel listPanel = new JPanel();
		listPanel.add(vs_combobox1);
		listPanel.add(loop_comparison_checkbox1);
		p.add(listPanel);
		
		l = new JLabel(Message.getString("select_a_species"), JLabel.TRAILING);
		
		
		vs_combobox2 = new JComboBox(vplist);
		vs_combobox2.setSelectedIndex(1);
		loop_comparison_checkbox2 = new JCheckBox(Message.getString("loop_comparison"));
		loop_comparison_checkbox2.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                changeCombobox(2);
            }
        });
		
		p.add(l);
		listPanel = new JPanel();
		listPanel.add(vs_combobox2);
		listPanel.add(loop_comparison_checkbox2);
		p.add(listPanel);
		
		JPanel p2 = new JPanel();
		
		p2.setLayout(new BoxLayout(p2, BoxLayout.X_AXIS));
		p.add(p2);
		
		p2 = new JPanel();
		p2.setLayout(new BoxLayout(p2, BoxLayout.X_AXIS));
		JLabel ll = new JLabel(Message.getString("overlap_method"), JLabel.TRAILING);
		p2.add(ll);
		overlap_method_combobox = new JComboBox(new String[]{Message.getString("mve"), Message.getString("convexhull")});
		p2.add(overlap_method_combobox);
		ll = new JLabel(Message.getString("expected_precision"), JLabel.TRAILING);
		precision_T = new JNumberTextField(9, JNumberTextField.DECIMAL);
		precision_T.setPrecision(4);
		precision_T.setText("0.01");
		p2.add(ll);
		p2.add(precision_T);
		
		
		
		
		ll = new JLabel(Message.getString("max_steps"), JLabel.TRAILING);
//		maxsteps_T = new JNumberTextField(7, JNumberTextField.NUMERIC);
//		maxsteps_T.setText("100000");
//		p2.add(ll);
//		p2.add(maxsteps_T);
//		unlimitedsteps_checkbox = new JCheckBox(Message.getString("unlimited"));
//		p2.add(unlimitedsteps_checkbox);
//		unlimitedsteps_checkbox.addChangeListener(new ChangeListener() {
//            @Override
//            public void stateChanged(ChangeEvent e) {
//                changeCombobox(3);
//            }
//        });
		
		p.add(p2);
		
        SpringUtilities.makeCompactGrid(p,
        		3, 2, //rows, cols
                4, 4,        //initX, initY
                2, 2);       //xPad, yPad
        p.setOpaque(true);
        add(p);
        
        
        p2 = new JPanel();
        p2.setLayout(new BoxLayout(p2, BoxLayout.X_AXIS));
        this.theApp  = theApp;
		target = new SelectFilePanal(this.theApp, Message.getString("select_env_folder"), "", true, null, true);
		p2.add(target);
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
		
		setSize(new Dimension(900, 200));
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);
		setModal(true);
	}
	
	protected void changeCombobox(int index) {
		if ((index==1)||(index==2)){
			JComboBox comboBox = (index==1)?this.vs_combobox1:this.vs_combobox2;
			boolean isChecked = (index==1)?this.loop_comparison_checkbox1.isSelected():this.loop_comparison_checkbox2.isSelected();
			comboBox.setEnabled(!isChecked);
		}
//		if (index==3){
//			this.maxsteps_T.setEditable(!this.unlimitedsteps_checkbox.isSelected());
//			this.maxsteps_T.setEnabled(!this.unlimitedsteps_checkbox.isSelected());
//		}
		
	}

	private void cancelClick() {
		this.vs1 = null;
		this.dispose();
		
	}
	private void okClick(){
		File f = new File(this.getTarget());
		if (!f.exists()){
			theApp.ShowAlert(Message.getString("no_folder"));
			return;
		}
		if (f.isFile()){
			theApp.ShowAlert(Message.getString("no_folder"));
			return;
		}
		if (this.loop_comparison_checkbox1.isSelected()){
			this.vs1 = vplist;
		}else{
			this.vs1 = new String[]{vs_combobox1.getSelectedItem().toString()};
		}
		if (this.loop_comparison_checkbox2.isSelected()){
			this.vs2 = vplist;
		}else{
			this.vs2 = new String[]{vs_combobox2.getSelectedItem().toString()};
		}
		
		this.precision = (CommonFun.isDouble(this.precision_T.getText()))?Double.valueOf(this.precision_T.getText()):0.01d;
//		this.maxsteps = (CommonFun.isInteger(this.maxsteps_T.getText()))?Integer.valueOf(this.maxsteps_T.getText()):1000000;
//		if (this.unlimitedsteps_checkbox.isSelected()){
//			this.maxsteps = Integer.MAX_VALUE;
//		}
		this.overlap_method = (String) this.overlap_method_combobox.getSelectedItem();
		setVisible(false);

	}

	public String[] getVs1() {
		return vs1;
	}

	public String[] getVs2() {
		return vs2;
	}

	public String getTarget(){
		return this.target.getText();
	}
	public double getPrecision() {
		return precision;
	}

	public String getOverlap_method() {
		return overlap_method;
	}
	
	
}
