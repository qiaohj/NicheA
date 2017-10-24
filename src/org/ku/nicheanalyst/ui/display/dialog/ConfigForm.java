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
import java.util.HashMap;
import java.util.HashSet;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

import org.ku.nicheanalyst.common.CommonFun;
import org.ku.nicheanalyst.common.ConfigInfo;
import org.ku.nicheanalyst.common.Message;

/**
 * @author Huijie Qiao
 *
 */
public class ConfigForm extends JDialog {
	private ArrayList<String> labels; 
	private HashSet<String> folderProperties;
	private HashMap<String, JTextField> values;
	private boolean isOK = false;
	public ConfigForm(){
		labels = new ArrayList<String>();
		labels.add("backgroupdpointcount");
		labels.add("maxpoint");
//		labels.add("gdaljni");
		labels.add("gdalwarp");
		labels.add("gdal_translate");
		labels.add("rscript");
		labels.add("convert");
		folderProperties = new HashSet<String>();
//		folderProperties.add("gdaljni");
		folderProperties.add("gdalwarp");
		folderProperties.add("gdal_translate");
		folderProperties.add("rscript");
		folderProperties.add("convert");
		values = new HashMap<String, JTextField>();
		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		JPanel p = new JPanel(new SpringLayout());
		int labelcount = labels.size();
        for (int i = 0; i < labelcount; i++) {
            JLabel l = new JLabel(Message.getString(labels.get(i)), JLabel.TRAILING);
            p.add(l);
            JTextField textField = new JTextField(10);
            textField.setText(ConfigInfo.getInstance().getProperty(labels.get(i)));
            if (folderProperties.contains(labels.get(i))){
            	File f = new File(ConfigInfo.getInstance().getProperty(labels.get(i)));
            	if (!f.exists()){
            		textField.setForeground(Color.red);
            	}
            }
            l.setLabelFor(textField);
            p.add(textField);
            values.put(labels.get(i), textField);
        }
        SpringUtilities.makeCompactGrid(p,
        		labelcount, 2, //rows, cols
                4, 4,        //initX, initY
                2, 2);       //xPad, yPad
        p.setOpaque(true);
        add(p);
        
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
		setSize(new Dimension(400, 250));
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);
		setModal(true);
	}
	private void cancelClick() {
		this.dispose();
		
	}
	private void okClick(){
		String backgroupdpointcount = values.get("backgroupdpointcount").getText();
		if (CommonFun.isInteger(backgroupdpointcount)){
			ConfigInfo.getInstance().setProperty("backgroupdpointcount", backgroupdpointcount);
		}
		String maxpoint = values.get("maxpoint").getText();
		if (CommonFun.isInteger(maxpoint)){
			ConfigInfo.getInstance().setProperty("maxpoint", maxpoint);
		}
		for (String property : folderProperties){
			String value = values.get(property).getText();
			ConfigInfo.getInstance().setProperty(property, value);
		}
//		File f = new File(ConfigInfo.getInstance().getGdaljni() + "/gdal.jar");
//		if (f.exists()){
//			CommonFun.refreshSystem();
//		}
		isOK = true;
		setVisible(false);
	}
	/**
	 * @return
	 */
	public boolean isOK() {
		return isOK;
	}
}
