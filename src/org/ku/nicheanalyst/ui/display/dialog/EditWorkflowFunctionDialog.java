package org.ku.nicheanalyst.ui.display.dialog;

import java.awt.Dialog.ModalityType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

import org.ku.nicheanalyst.common.Message;
import org.ku.nicheanalyst.dataset.WorkFlowFunction;
import org.ku.nicheanalyst.ui.display.component.ui.LabeledTextArea;
import org.ku.nicheanalyst.ui.display.component.ui.LabeledTextField;

public class EditWorkflowFunctionDialog extends JDialog {
	private LabeledTextField name;
	private LabeledTextArea message;
	public EditWorkflowFunctionDialog(WorkFlowFunction wf) {
		setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		name = new LabeledTextField(Message.getString("function_name"), wf.getName());
		message = new LabeledTextArea(Message.getString("function_message"), wf.getMessage(), 5, 40);
		add(name);
		add(message);
		JButton ok = new JButton(Message.getString("ok"));
        ok.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent event) {
            	setVisible(false);
            }
        });
        JButton cancel = new JButton(Message.getString("cancel"));
        cancel.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent event) {
                close();
            }
        });
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
        p.add(ok);
        p.add(cancel);
        add(p);
        setModalityType(ModalityType.APPLICATION_MODAL);
        setSize(500, 150);
        setTitle(Message.getString("manage_workflow"));
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);
		setModal(true);
	}
	protected void close() {
		this.name.setText(null);
		dispose();
	}
	public String getFuncMessage() {
		return this.message.getText();
	}
	public String getName(){
		return this.name.getText();
	}
}
