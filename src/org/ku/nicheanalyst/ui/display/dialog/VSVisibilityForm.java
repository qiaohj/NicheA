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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JPanel;

import org.ku.nicheanalyst.common.Message;
import org.ku.nicheanalyst.ui.display.Displayer;

/**
 * @author Huijie Qiao
 *
 */
public class VSVisibilityForm extends JDialog {
	private HashMap<String, JCheckBox> vs_points;
	private HashMap<String, JCheckBox> vs_mve;
	private HashMap<String, JCheckBox> vs_cp;
	private String[] vplist;
	private Displayer theApp;
	private HashMap<String, boolean[]> vsStatus;
	public VSVisibilityForm(String[] vplist, Displayer theApp, HashMap<String, boolean[]> vsStatus){
		this.vsStatus = new HashMap<String, boolean[]>();
		for (String key : vsStatus.keySet()){
			this.vsStatus.put(key, new boolean[]{vsStatus.get(key)[0], vsStatus.get(key)[1], vsStatus.get(key)[2]});
		}
		this.theApp = theApp;
		this.vplist = vplist;
		this.vs_points = new HashMap<String, JCheckBox>();
		this.vs_mve = new HashMap<String, JCheckBox>();
		this.vs_cp = new HashMap<String, JCheckBox>();
//		this.setResizable(false);
		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
		p.setBorder(BorderFactory.createTitledBorder(Message.getString("species_dataset")));

		JPanel p_label = new JPanel();
		p_label.setLayout(new BoxLayout(p_label, BoxLayout.Y_AXIS));
		JPanel p_checkbox = new JPanel();
		p_checkbox.setLayout(new BoxLayout(p_checkbox, BoxLayout.Y_AXIS));
		for (final String vs : vplist){
			JPanel p_line = new JPanel();
			p_line.setLayout(new BoxLayout(p_line, BoxLayout.X_AXIS));
//			p_line.setBorder(BorderFactory.createTitledBorder(vs));
//			p_line.setPreferredSize(new Dimension(500, 48));;
			JCheckBox cb = new JCheckBox(Message.getString("show_points"));
			cb.setSelected(vsStatus.get(vs)[0]);
			
			vs_points.put(vs, cb);
			p_line.add(cb);
			cb = new JCheckBox(Message.getString("show_mve"));
			cb.setSelected(vsStatus.get(vs)[1]);
			vs_mve.put(vs, cb);
			p_line.add(cb);
			cb = new JCheckBox(Message.getString("show_cp"));
			cb.setSelected(vsStatus.get(vs)[2]);
			vs_cp.put(vs, cb);
			p_line.add(cb);
			p_checkbox.add(p_line);
			
			final JCheckBox cbx = new JCheckBox(String.format(Message.getString("show_all_vs"), vs));
			if (vsStatus.get(vs)[0]&&vsStatus.get(vs)[1]&&vsStatus.get(vs)[2]){
				cbx.setSelected(true);
			}else{
				cbx.setSelected(false);
			}
			cbx.addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent e) {
					changeChecked(vs, cbx.isSelected());
				}
			});
			p_label.add(cbx);
		}
		
		p.add(p_label);
		p.add(p_checkbox);
		
		JPanel p2 = new JPanel();
		JButton showallpoints = new JButton(Message.getString("show_all_points"));
		showallpoints.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				showhideClick(true, vs_points);
			}
		});
		
		JButton showallmve = new JButton(Message.getString("show_all_mve"));
		showallmve.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				showhideClick(true, vs_mve);
			}
		});
		
		JButton showallcp = new JButton(Message.getString("show_all_cp"));
		showallcp.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				showhideClick(true, vs_cp);
			}
		});
		
		JButton hideallpoints = new JButton(Message.getString("hide_all_points"));
		hideallpoints.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				showhideClick(false, vs_points);
			}
		});
		
		JButton hideallmve = new JButton(Message.getString("hide_all_mve"));
		hideallmve.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				showhideClick(false, vs_mve);
			}
		});
		
		JButton hideallcp = new JButton(Message.getString("hide_all_cp"));
		hideallcp.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				showhideClick(false, vs_cp);
			}
		});
		
		
        JButton ok = new JButton(Message.getString("ok"));
		ok.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				okClick();
			}
		});
		JButton cancel = new JButton(Message.getString("cancel"));
		cancel.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				cancelClick();
			}
		});
		
		p2.add(showallpoints);
		p2.add(hideallpoints);
		p2.add(showallmve);
		p2.add(hideallmve);
		p2.add(showallcp);
		p2.add(hideallcp);
		
		p2.add(ok);
		p2.add(cancel);
		

		add(p);
		add(p2);
        
		
		setSize(new Dimension(930, vplist.length * 30 + 80));
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);
		setModal(true);
	}
	
	
	protected void changeChecked(String vs, boolean selected) {
		vs_points.get(vs).setSelected(selected);
		vs_mve.get(vs).setSelected(selected);
		vs_cp.get(vs).setSelected(selected);
		
	}


	protected void showhideClick(boolean v, HashMap<String, JCheckBox> vs) {
		for (JCheckBox b : vs.values()){
			b.setSelected(v);
		}
		
	}

	private void cancelClick() {
		this.vsStatus = null;
		this.dispose();
		
	}
	private void okClick(){
		for (String key : this.vsStatus.keySet()){
			this.vsStatus.get(key)[0] = vs_points.get(key).isSelected();
			this.vsStatus.get(key)[1] = vs_mve.get(key).isSelected();
			this.vsStatus.get(key)[2] = vs_cp.get(key).isSelected();
		}
		setVisible(false);

	}

	public HashMap<String, boolean[]> getVpStatus() {
		return this.vsStatus;
	}

	
}
