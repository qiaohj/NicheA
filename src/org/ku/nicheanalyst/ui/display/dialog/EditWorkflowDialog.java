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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import org.ku.nicheanalyst.common.CommonFun;
import org.ku.nicheanalyst.common.Message;
import org.ku.nicheanalyst.common.WorkflowLoader;
import org.ku.nicheanalyst.dataset.WorkFlow;
import org.ku.nicheanalyst.dataset.WorkFlowFunction;
import org.ku.nicheanalyst.ui.display.Displayer;
import org.ku.nicheanalyst.ui.display.component.ui.LabeledTextArea;
import org.ku.nicheanalyst.ui.display.component.ui.LabeledTextField;


public class EditWorkflowDialog extends JDialog {
	
	private Displayer theApp;
	private LabeledTextField name;
	private LabeledTextField author;
	private LabeledTextField email;
	private LabeledTextArea message;
//	private LabeledTextField function_name;
//	private LabeledTextArea function_message;
	private JList all_functions;
	private JList selected_functions;
	private DefaultListModel all_function_data;
	private DefaultListModel selected_function_data;
	private WorkFlow selected_workflow;
	private boolean isNew;
    public EditWorkflowDialog(Displayer theApp, WorkFlow selected_workflow) {
    	this.theApp = theApp;
    	this.selected_workflow = selected_workflow;
    	if (this.selected_workflow==null){
    		isNew = true;
    		this.selected_workflow = new WorkFlow();
    	}
        initUI();
    }

    public final void initUI() {
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        JPanel editor_panel = new JPanel();
        editor_panel.setLayout(new BoxLayout(editor_panel, BoxLayout.Y_AXIS));
//        editor_panel.setBorder(BorderFactory.createTitledBorder(Message.getString("workflow_editor")));
        
        //base information panel
        JPanel base_info_panel = new JPanel();
        base_info_panel.setLayout(new BoxLayout(base_info_panel, BoxLayout.Y_AXIS));
        base_info_panel.setBorder(BorderFactory.createTitledBorder(Message.getString("workflow_base_info")));
        
        name = new LabeledTextField(Message.getString("workflow_name"), this.selected_workflow.getName());
        if (!this.isNew){
        	name.setEditable(false);
        }
        author = new LabeledTextField(Message.getString("workflow_author"), this.selected_workflow.getAuthor());
        email = new LabeledTextField(Message.getString("workflow_email"), this.selected_workflow.getEmail());
        message = new LabeledTextArea(Message.getString("workflow_message"), this.selected_workflow.getMessage(), 5, 40);
        base_info_panel.add(name);
        base_info_panel.add(author);
        base_info_panel.add(email);
        base_info_panel.add(message);
        add(base_info_panel);
        
        JPanel function_panel = new JPanel();
        function_panel.setLayout(new BoxLayout(function_panel, BoxLayout.X_AXIS));
        function_panel.setBorder(BorderFactory.createTitledBorder(Message.getString("workflow_funtion_panel")));
        
        //for all the functions
        ArrayList<WorkFlowFunction> functions = WorkflowLoader.getInstance().getAllFunctions();
        all_function_data = new DefaultListModel();
        for (WorkFlowFunction function : functions){
        	all_function_data.add(all_function_data.size(), function);
        }
        all_functions = new JList(all_function_data);
        all_functions.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setViewportView(all_functions);
        scrollPane.setBorder(BorderFactory.createTitledBorder(Message.getString("workflow_all_function_panel")));
        function_panel.add(scrollPane);
        
        //for buttons
        JPanel control_button_panel = new JPanel();
        control_button_panel.setLayout(new BoxLayout(control_button_panel, BoxLayout.Y_AXIS));
        
        JButton up = new JButton(Message.getString("function_move_up"));
        up.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent event) {
            	up();
            }
        });
        JButton down = new JButton(Message.getString("function_move_down"));
        down.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent event) {
            	down();
            }
        });
        JButton add_function = new JButton(Message.getString("function_add"));
        add_function.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent event) {
            	add_function();
            }
        });
        
        JButton remove_function = new JButton(Message.getString("function_remove"));
        remove_function.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent event) {
            	remove_function();
            }
        });
        control_button_panel.add(add_function);
        control_button_panel.add(remove_function);
        control_button_panel.add(up);
        control_button_panel.add(down);
        function_panel.add(control_button_panel);
        
        
        
        //for selected list
        
        selected_function_data = new DefaultListModel();
        for (WorkFlowFunction function : this.selected_workflow.getChildren()){
        	selected_function_data.add(selected_function_data.size(), function);
        }
        selected_functions = new JList(selected_function_data);
        selected_functions.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        selected_functions.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                JList list = (JList)evt.getSource();
                if (evt.getClickCount() == 2) {
                    int index = list.locationToIndex(evt.getPoint());
                    showFunctionAttributes(index);
                }
            }
        });
		JScrollPane scrollPane2 = new JScrollPane();
		scrollPane2.setBorder(BorderFactory.createTitledBorder(Message.getString("workflow_selected_function_panel")));
		scrollPane2.setViewportView(selected_functions);
		function_panel.add(scrollPane2);
		
        
        add(function_panel);
        
        JPanel button_panel = new JPanel();
        button_panel.setLayout(new BoxLayout(button_panel, BoxLayout.X_AXIS));

        JButton save = new JButton(Message.getString("save"));
        save.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent event) {
            	try {
					save();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
        });
        
        JButton close = new JButton("Close");
        close.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent event) {
                dispose();
            }
        });
        button_panel.add(save);
        button_panel.add(close);
        add(button_panel);
        setModalityType(ModalityType.APPLICATION_MODAL);
        setSize(1000, 550);
        setTitle(Message.getString("manage_workflow"));
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);
		setModal(true);
		
        
    }


	protected void save() throws IOException {
		this.selected_workflow.setName(this.name.getText());
		this.selected_workflow.setEmail(this.email.getText());
		this.selected_workflow.setAuthor(this.author.getText());
		this.selected_workflow.setMessage(this.message.getText());
		this.selected_workflow.removeAllChild();
		for (int i=0;i<this.selected_function_data.size();i++){
			WorkFlowFunction func = (WorkFlowFunction) this.selected_function_data.get(i);
			this.selected_workflow.addChild(func);
		}
		if (isNew){
			if (WorkflowLoader.isExist(this.selected_workflow.getName())){
				int dialogButton = JOptionPane.YES_NO_OPTION;
				int dialogResult = JOptionPane.showConfirmDialog (null, Message.getString("over_workflow"), "Warning", dialogButton);
				if(dialogResult != JOptionPane.YES_OPTION){
					return;
				}
			}
		}
		
		String file = "workflow.tmp";
		this.selected_workflow.save(file);
		WorkflowLoader.removeWorkflow(this.selected_workflow.getName());
		WorkflowLoader.addWorkflow(file);
		CommonFun.rmfile(file);
		theApp.refreshWorkflow();
		if (isNew){
			theApp.ShowAlert(Message.getString("add_workflow_success"));
		}else{
			theApp.ShowAlert(Message.getString("edit_workflow_succees"));
		}
		
		
	}

	protected void showFunctionAttributes(int index) {
		WorkFlowFunction wf = (WorkFlowFunction) selected_function_data.get(index);
		EditWorkflowFunctionDialog d = new EditWorkflowFunctionDialog(wf);
		d.setVisible(true);
		if (d.getName()==null){
			return;
		}
		if (d.getName().trim().equals("")){
			return;
		}
		wf.setName(d.getName());
		wf.setMessage(d.getFuncMessage());
		selected_function_data.set(index, wf);
		
	}

	protected void remove_function() {
		int index = this.selected_functions.getSelectedIndex();
		if (index>=0){
			this.selected_function_data.remove(index);
			index = (index>=this.selected_function_data.size())?index-1:index;
			this.selected_functions.setSelectedIndex(index);
		}
	}

	protected void add_function() {
		
		int index = this.all_functions.getSelectedIndex();
		if (index>=0){
			WorkFlowFunction temp = (WorkFlowFunction) this.all_function_data.get(index);
			WorkFlowFunction new_temp = new WorkFlowFunction(temp.getName(), temp.getFunction_name(), temp.getMessage());
			this.selected_function_data.add(this.selected_function_data.size(), new_temp);
		}
	}

	protected void down() {
		int index = this.selected_functions.getSelectedIndex();
		if (index < this.selected_function_data.size() - 1) {
			WorkFlowFunction temp = (WorkFlowFunction) this.selected_function_data.remove(index);
			this.selected_function_data.add(index + 1, temp);
			this.selected_functions.setSelectedIndex(index + 1);
		}
	}

	protected void up() {
		int index = this.selected_functions.getSelectedIndex();
		if (index > 0){
			WorkFlowFunction temp = (WorkFlowFunction) this.selected_function_data.remove(index);
			this.selected_function_data.add(index - 1, temp);
			this.selected_functions.setSelectedIndex(index - 1);
		}
	}

}