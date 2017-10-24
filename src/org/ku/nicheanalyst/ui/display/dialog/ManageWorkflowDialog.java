/******************************************************************************
 * Huijie Qiao
 *
 * Project:  Niche Analyst
 * Purpose:  
 * Created date: Aug 3, 2012 6:57:02 PM
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
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JPanel;

import org.ku.nicheanalyst.common.Message;
import org.ku.nicheanalyst.common.WorkflowLoader;
import org.ku.nicheanalyst.dataset.WorkFlow;
import org.ku.nicheanalyst.ui.display.Displayer;
import org.ku.nicheanalyst.ui.display.component.ui.ObjectLinkedCheckBox;
import org.ku.nicheanalyst.ui.filefilters.XMLFileFilter;


public class ManageWorkflowDialog extends JDialog {
	
	private ArrayList<ObjectLinkedCheckBox> workflows;
	private Displayer theApp;
    public ManageWorkflowDialog(Displayer theApp) {
    	this.theApp = theApp;
        initUI();
    }

    public final void initUI() {

        setLayout(new FlowLayout(FlowLayout.LEADING));
        workflows = new ArrayList<ObjectLinkedCheckBox>();
        JPanel workflow_panel = new JPanel();
        workflow_panel.setBorder(BorderFactory.createTitledBorder(Message.getString("workflow_list")));
        workflow_panel.setLayout(new BoxLayout(workflow_panel, BoxLayout.Y_AXIS));
        for (WorkFlow workflow : WorkflowLoader.getInstance().getWorkflows()){
        	ObjectLinkedCheckBox c = new ObjectLinkedCheckBox(workflow.getName(), workflow);
        	workflow_panel.add(c);
        	workflows.add(c);
        }
        add(workflow_panel);
        JPanel button_panel = new JPanel();
        button_panel.setLayout(new BoxLayout(button_panel, BoxLayout.Y_AXIS));
//        button_panel.setBorder(BorderFactory.createLineBorder(Color.black));

        JButton add = new JButton(Message.getString("add_workflow"));
        add.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent event) {
            	add_workflow();
            }
        });

        button_panel.add(add);


        JButton edit = new JButton(Message.getString("edit_workflow"));
        edit.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent event) {
            	edit_workflow();
            }
        });

        button_panel.add(edit);
        
        JButton newl = new JButton(Message.getString("new_workflow"));
        newl.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent event) {
            	new_workflow();
            }
        });

        button_panel.add(newl);
        
        JButton export = new JButton(Message.getString("export_workflow"));
        export.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent event) {
            	try {
					export_workflow();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
        });
        
        button_panel.add(export);
        
        JButton remove = new JButton(Message.getString("remove_workflow"));
        remove.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent event) {
            	remove_workflow();
            }
        });

        button_panel.add(remove);

        
        JButton close = new JButton("Close");
        close.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent event) {
                dispose();
            }
        });

        button_panel.add(close);
        add(button_panel);
        setModalityType(ModalityType.APPLICATION_MODAL);

        setTitle(Message.getString("manage_workflow"));
        setSize(600, 300);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);
		setModal(true);
		
        
    }

	protected void export_workflow() throws IOException {
		ObjectLinkedCheckBox selected = null;
		for (ObjectLinkedCheckBox c : workflows){
			if (c.isSelected()){
				selected = c;
			}
		}
		if (selected!=null){
			JFileChooser fc = new JFileChooser();
			fc.setDialogTitle(Message.getString("select_a_file"));
			fc.setSelectedFile(new File(theApp.getLastFolder()));
			fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
			fc.setFileFilter(new XMLFileFilter());
			int returnVal = fc.showSaveDialog(this);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File target = fc.getSelectedFile();
				theApp.setLastFolder(target.getAbsolutePath());
				WorkFlow workflow = (WorkFlow) selected.getObj();
				workflow.save(target.getAbsolutePath());
				this.theApp.ShowAlert(String.format(Message.getString("export_workflow_success"), target.getAbsoluteFile()));
			}
			
		}
		
	}

	protected void new_workflow() {
		EditWorkflowDialog d = new EditWorkflowDialog(this.theApp, null);
		d.setVisible(true);
	}

	protected void edit_workflow() {
		ObjectLinkedCheckBox selected = null;
		for (ObjectLinkedCheckBox c : workflows){
			if (c.isSelected()){
				selected = c;
			}
		}
		if (selected!=null){
			EditWorkflowDialog d = new EditWorkflowDialog(this.theApp, (WorkFlow) selected.getObj());
			d.setVisible(true);
		}
		
	}

	protected void remove_workflow() {
		for (JCheckBox box : this.workflows){
			if (box.isSelected()){
				WorkflowLoader.removeWorkflow(box.getText());
			}
		}
		this.theApp.refreshWorkflow();
		this.theApp.ShowAlert(Message.getString("remove_workflow_success"));
	}

	protected void add_workflow() {
		
		JFileChooser fc = new JFileChooser();
		fc.setDialogTitle(Message.getString("select_a_workflow_config_file"));
		fc.setSelectedFile(new File(theApp.getLastFolder()));
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fc.setFileFilter(new XMLFileFilter());
		int returnVal = 0;
		returnVal = fc.showOpenDialog(this);
		
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			String target = (fc.getSelectedFile().exists())?
					fc.getSelectedFile().getAbsolutePath():fc.getSelectedFile().getParent();
			theApp.setLastFolder(target);
			add_workflow_via_file(target);
		}
		
	}
	private void add_workflow_via_file(String target){
		String msg = WorkflowLoader.addWorkflow(target);
		if (msg.equals(Message.getString("done"))){
			this.theApp.refreshWorkflow();
			this.theApp.ShowAlert(Message.getString("add_workflow_success"));
		}else{
			this.theApp.ShowAlert(msg);
		}
	}
}