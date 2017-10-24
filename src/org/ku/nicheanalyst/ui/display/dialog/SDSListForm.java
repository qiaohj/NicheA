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
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.HashSet;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.SpringLayout;

import org.ku.nicheanalyst.common.Const;
import org.ku.nicheanalyst.common.Message;
import org.ku.nicheanalyst.dataset.GSpaceData;
import org.ku.nicheanalyst.ui.display.Displayer;

/**
 * @author Huijie Qiao
 *
 */
public class SDSListForm extends JDialog {
	private HashSet<String> selectVs;
	private HashMap<String, JCheckBox> vs_combobox;
	private String[] vplist;
	private Displayer theApp;
	//0: close 1:export map 2: export mve
	private int command;
	private String folder;
	public SDSListForm(String[] vplist, Displayer theApp){
		command = 0;
		this.theApp = theApp;
		this.vplist = vplist;
		this.vs_combobox = new HashMap<String, JCheckBox>();
		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		JPanel p = new JPanel(new SpringLayout());
		for (String vs : vplist){
			JCheckBox cb = new JCheckBox(vs);
			vs_combobox.put(vs, cb);
			p.add(cb);
		}
		JPanel p2 = new JPanel();
		JButton selectall = new JButton(Message.getString("select_all"));
		selectall.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				selectallClick();
			}
		});
		JButton deselectall = new JButton(Message.getString("deselect_all"));
		deselectall.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				deselectallClick();
			}
		});
		JButton invest_selection = new JButton(Message.getString("invest_selection"));
		invest_selection.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				invest_selectionClick();
			}
		});
        JButton cancel = new JButton(Message.getString("cancel"));
		cancel.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				cancelClick();
			}
		});
		JButton ok = new JButton(Message.getString("close"));
		ok.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				okClick();
			}
		});
		JButton export_niche_map = new JButton(Message.getString("export_niche_map"));
		export_niche_map.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				try {
					export_niche_mapClick();
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		JButton export_mve = new JButton(Message.getString("export_mve"));
		export_mve.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				try {
					export_mve_click();
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		p2.add(selectall);
		p2.add(deselectall);
		p2.add(invest_selection);
		p2.add(ok);
		p2.add(export_niche_map);
		p2.add(export_mve);
		p2.add(cancel);
		p.add(p2);
        SpringUtilities.makeCompactGrid(p,
        		vplist.length + 1, 1, //rows, cols
                4, 4,        //initX, initY
                2, 2);       //xPad, yPad
        p.setOpaque(true);
        add(p);
        
        
		
		setSize(new Dimension(930, vplist.length * 30 + 70));
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);
		setModal(true);
	}
	
	protected void export_mve_click() throws FileNotFoundException {
		folder = selectFolder();
		if (folder!=null){
			getSelectedVS();
			command = 2;
			setVisible(false);
		}
		
	}

	protected void export_niche_mapClick() throws FileNotFoundException {
		folder = selectFolder();
		if (folder!=null){
			getSelectedVS();
			command = 1;
			setVisible(false);
		}
		
	}
	public String getFolder(){
		return this.folder;
	}
	protected String selectFolder() {
		
		JFileChooser fc = new JFileChooser();
		fc.setDialogTitle(Message.getString("select_a_folder"));
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
			return target;
		}
		return null;
		
	}
	protected void invest_selectionClick() {
		for (JCheckBox b : vs_combobox.values()){
			b.setSelected(!b.isSelected());
		}
		
	}

	protected void deselectallClick() {
		for (JCheckBox b : vs_combobox.values()){
			b.setSelected(false);
		}
		
	}

	protected void selectallClick() {
		for (JCheckBox b : vs_combobox.values()){
			b.setSelected(true);
		}
		
	}

	private void cancelClick() {
		this.selectVs = null;
		this.dispose();
		
	}
	private void okClick(){
		getSelectedVS();
		command = 0;
		setVisible(false);

	}
	public int getCommand(){
		return command;
	}
	public void getSelectedVS(){
		this.selectVs = new HashSet<String>();
		for (String key :  vs_combobox.keySet()){
			if (vs_combobox.get(key).isSelected()){
				selectVs.add(key);
			}
		}
	}
	public HashSet<String> getSelectVs() {
		
		return selectVs;
	}

	
	
}
