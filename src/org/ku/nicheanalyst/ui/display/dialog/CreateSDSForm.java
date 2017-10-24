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
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.vecmath.Color3f;

import org.ku.nicheanalyst.common.Comment;
import org.ku.nicheanalyst.common.Message;
import org.ku.nicheanalyst.ui.display.Displayer;


/**
 * @author Huijie Qiao
 *
 */
public class CreateSDSForm extends JDialog implements ActionListener {
	private String target;
	private int selection;
	private JRadioButton radioSelection;
	private JRadioButton radioMVE;
	private JRadioButton radioCH;
	private JComboBox comboBoxInout;
	private JComboBox comboBoxVSSelection;
	private JComboBox comboBoxPointPoor;
	private JTextField textFieldTarget;
	
	private String vsSelection;
	private String pointPoor;
	
	private Displayer theApp;
	public CreateSDSForm(Displayer theApp, String[] vplist){
		this.selection = 0;
		this.theApp = theApp;
		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
		p.setBorder(BorderFactory.createLineBorder(Color.black));
		JLabel l = new JLabel(Message.getString("choose_selection"), JLabel.TRAILING);
		l.setToolTipText(Comment.getString("choose_selection"));
		p.add(l);
		JPanel p2 = new JPanel();
		p2.setLayout(new FlowLayout(FlowLayout.LEFT));
		radioSelection = new JRadioButton(Message.getString("select_by_selection"));
		radioSelection.setSelected(true);
		radioSelection.setActionCommand(Message.getString("select_by_selection"));
		radioSelection.addActionListener(this);
		p2.add(radioSelection);
		p.add(p2);
		boolean enabled = (vplist.length>0);
		
		p2 = new JPanel();
		p2.setLayout(new FlowLayout(FlowLayout.LEFT));
		radioMVE = new JRadioButton(Message.getString("select_by_mve"));
		radioMVE.setSelected(false);
		radioMVE.setActionCommand(Message.getString("select_by_mve"));
		radioMVE.addActionListener(this);
		radioMVE.setEnabled(enabled);
		p2.add(radioMVE);
		p.add(p2);
		
		p2 = new JPanel();
		p2.setLayout(new FlowLayout(FlowLayout.LEFT));
		radioCH = new JRadioButton(Message.getString("select_by_ch"));
		radioCH.setSelected(false);
		radioCH.setActionCommand(Message.getString("select_by_ch"));
		radioCH.addActionListener(this);
		radioCH.setEnabled(enabled);
		p2.add(radioCH);
		p.add(p2);
		
		ButtonGroup group = new ButtonGroup();
		group.add(radioMVE);
		group.add(radioSelection);
		group.add(radioCH);
		
		
		
		l = new JLabel(Message.getString("use_sds_select"));
		p2 = new JPanel();
		p2.setLayout(new FlowLayout(FlowLayout.LEFT));
		p2.add(l);
		
		
		comboBoxVSSelection = new JComboBox(vplist);
		comboBoxVSSelection.setEnabled(false);
		p2.add(comboBoxVSSelection);
		
		String[] inout = new String[]{Message.getString("inside"), Message.getString("outside"), Message.getString("inoutside")};
		comboBoxInout = new JComboBox(inout);
		p2.add(comboBoxInout);
		
		p.add(p2);
			
		
		add(p);
		add(new JLabel());
		
		p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
		p.setBorder(BorderFactory.createLineBorder(Color.black));
		l = new JLabel(Message.getString("occurrence_pool"));
		p.add(l);
		
		l = new JLabel(Message.getString("use_sds_select"));
		p2 = new JPanel();
		p2.setLayout(new FlowLayout(FlowLayout.LEFT));
		p2.add(l);
		
		
		String[] vslist_with_background = new String[vplist.length + 1];
		for (int i=0;i<vplist.length;i++){
			vslist_with_background[i + 1] = vplist[i];
		}
		vslist_with_background[0] = Message.getString("background_cloud");
		comboBoxPointPoor = new JComboBox(vslist_with_background);
		p2.add(comboBoxPointPoor);
		p.add(p2);
		add(p);
		
		p2 = new JPanel();
		p2.setLayout(new BoxLayout(p2, BoxLayout.X_AXIS));
		l = new JLabel(Message.getString("save_species_dataset"));
		textFieldTarget = new JTextField();
		p2.add(l);
		p2.add(textFieldTarget);
		JButton targetbs = new JButton("...");
		p2.add(targetbs);
		targetbs.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				selectFolder();
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
		setSize(new Dimension(800, 300));
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);
		setModal(true);
		
	}
	/**
	 * 
	 */
	protected void selectFolder() {
		
		JFileChooser fc = new JFileChooser();
		fc.setSelectedFile(new File(theApp.getLastFolder()));
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int returnVal = fc.showSaveDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			String target = (fc.getSelectedFile().exists())?
					fc.getSelectedFile().getAbsolutePath():fc.getSelectedFile().getParent();
			theApp.setLastFolder(target);
			this.textFieldTarget.setText(target);
		}
		
	}
	private void cancelClick() {
		this.target = null;
		this.dispose();
		
	}
	private void okClick(){
		boolean ispass = true;
		this.target = this.textFieldTarget.getText();
		File f = new File(target);
		if (!f.exists()){
			ispass = false;
		}
		if (!ispass){
			theApp.ShowAlert(Message.getString("error_warning"));
		}else{
			setVisible(false);
		}
	}
	/**
	 * @param selectedItem
	 * @return
	 */
	private Color3f getColor3f(Color color) {
		return new Color3f(color);
	}
	public String getTarget() {
		target = this.textFieldTarget.getText();
		if (target==null){
			return target;
		}
		
		File f = new File(target);
		if (!f.exists()){
			target = null;
			return target;
		}
		return target;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		this.selection = 0;
		if (comboBoxVSSelection!=null){
			if (e.getActionCommand().equals(Message.getString("select_by_selection"))){
				comboBoxVSSelection.setEnabled(false);
			}else{
				comboBoxVSSelection.setEnabled(true);
			}
		}
		if (e.getActionCommand().equals(Message.getString("select_by_mve"))){
			this.selection = 1;
		}
		if (e.getActionCommand().equals(Message.getString("select_by_ch"))){
			this.selection = 2;
		}
	}
	public int getINOUT(){
		//0: in 1: out 2:in and out
		return this.comboBoxInout.getSelectedIndex();
	}
	public int getSelection() {
		return selection;
	}
	public String getVsSelection() {
		vsSelection = (String) comboBoxVSSelection.getSelectedItem();
		return vsSelection;
	}
	public String getPointPoor() {
		pointPoor = (String) comboBoxPointPoor.getSelectedItem();
		return pointPoor;
	}
	
}
