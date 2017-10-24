/******************************************************************************
 * Huijie Qiao
 *
 * Project:  Niche Analyst
 * Purpose:  
 * Created date: Apr 10, 2012 4:25:44 PM
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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.ku.nicheanalyst.common.CommonFun;
import org.ku.nicheanalyst.common.Const;
import org.ku.nicheanalyst.common.Message;
import org.ku.nicheanalyst.ui.display.Displayer;
import org.ku.nicheanalyst.ui.filefilters.CSVFileFilter;
import org.ku.nicheanalyst.ui.filefilters.TXTFileFilter;

/**
 * @author Huijie Qiao
 *
 */
public class LogForm extends JDialog {
	private StringBuilder log1;
	private StringBuilder log2;
	private String title;
	private Displayer theApp;
	private JTextArea text;
	public LogForm(String title, StringBuilder log, Displayer theApp){
		super();
		this.title = title;
		this.log1 = log;
		this.log2 = null;
		this.theApp = theApp;
        showIt();
	}
	private void showIt() {
		this.setTitle(title);
        setSize(new Dimension(800, 620));
        getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        
        text = new JTextArea(log1.toString());
//        text.setPreferredSize(new Dimension(450, 590));
        if (log2==null){
        	text.setText(log1.toString());
        }else{
        	text.setText(log1.toString() + Const.LineBreak + log2.toString());
        }
        JPanel panel = new JPanel(new BorderLayout());
        JScrollPane scrollPane = new JScrollPane(text);
        
        
//        scrollPane.setPreferredSize(new Dimension(500, 600));
//        scrollPane.setAutoscrolls(true);
        panel.add(scrollPane, BorderLayout.CENTER);
        add(panel);
        
        
        JPanel buttonPanel = new JPanel();
        final JButton saveButton = new JButton();
        saveButton.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                try {
					saveButtonPressed();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
            }
        });
        saveButton.setAlignmentY(CENTER_ALIGNMENT);
        saveButton.setAlignmentX(CENTER_ALIGNMENT);
        saveButton.setText(Message.getString("save"));
        buttonPanel.add(saveButton);
        if (log2!=null){
	        final JButton exportButton = new JButton();
	        exportButton.addActionListener(new ActionListener() {
	            public void actionPerformed(final ActionEvent e) {
	                try {
						exportButtonPressed();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
	            }
	        });
	        exportButton.setAlignmentY(CENTER_ALIGNMENT);
	        exportButton.setAlignmentX(CENTER_ALIGNMENT);
	        exportButton.setText(Message.getString("export_table"));
	        buttonPanel.add(exportButton);
        }
        final JButton cancelButton = new JButton();
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                cancelButtonPressed();
            }
        });
        cancelButton.setAlignmentY(CENTER_ALIGNMENT);
        cancelButton.setAlignmentX(CENTER_ALIGNMENT);
        cancelButton.setText(Message.getString("close"));
        buttonPanel.add(cancelButton);
        add(buttonPanel);
        setModal(true);
        setVisible(true);
		
	}
	protected void exportButtonPressed() throws IOException {
		JFileChooser fc = new JFileChooser();
		fc.setDialogTitle(Message.getString("save_log"));
		fc.setSelectedFile(new File(theApp.getLastFolder()));
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fc.setFileFilter(new CSVFileFilter());
		int returnVal = fc.showSaveDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			String target = fc.getSelectedFile().getAbsolutePath();
			theApp.setLastFolder(target);
			CommonFun.writeFile(this.log2.toString(), target);
			theApp.ShowAlert(Message.getString("done"));
		}
		
	}
	protected void saveButtonPressed() throws IOException {
		JFileChooser fc = new JFileChooser();
		fc.setDialogTitle(Message.getString("save_log"));
		fc.setSelectedFile(new File(theApp.getLastFolder()));
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fc.setFileFilter(new TXTFileFilter());
		int returnVal = fc.showSaveDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			String target = fc.getSelectedFile().getAbsolutePath();
			theApp.setLastFolder(target);
			CommonFun.writeFile(this.text.getText(), target);
			theApp.ShowAlert(Message.getString("done"));
		}
		
	}
	public LogForm(String title, StringBuilder log1,
			StringBuilder log2, Displayer theApp) {
		super();
		this.title = title;
		this.log1 = log1;
		this.log2 = log2;
		this.theApp = theApp;
        showIt();
	}
	private void cancelButtonPressed() {
        this.dispose();
    }
}
