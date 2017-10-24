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
import javax.swing.JPanel;

import org.ku.nicheanalyst.common.Comment;
import org.ku.nicheanalyst.common.CommonFun;
import org.ku.nicheanalyst.common.Message;
import org.ku.nicheanalyst.common.ThresholdMethod;
import org.ku.nicheanalyst.ui.display.component.ui.LabeledTextField;

public class AddThresholdMethodForm extends JDialog{
	private LabeledTextField value;
	private JComboBox thresholdMethodList;
	private ThresholdMethod selectedMethod;
	private JCheckBox isRemoveZero;
	private boolean[] isEditable;
	public AddThresholdMethodForm(){
		BoxLayout layout = new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS);
		this.getContentPane().setLayout(layout);
		
		String[] methods = {
				Message.getString("MINIMUM_TRAINING_PRESENCE"),
				Message.getString("PERCENTILE_TRAINING_PRESENCE"),
				Message.getString("MEAN_TRAINING_PRESENCE"),
				Message.getString("MEAN_OF_LOWER_PERCENTILE_TRAINING_PRESENCE"),
				Message.getString("MEAN_OF_HIGHER_PERCENTILE_TRAINING_PRESENCE"),
				Message.getString("PERCENTILE_OF_MODEL"),
				Message.getString("MEAN_OF_MODEL"),
				Message.getString("Maximum_True_Skill_Statistic"),
				Message.getString("Equal_Sensitivity_Specificity"),
				Message.getString("FIXED_VALUE")}; 
		isEditable = new boolean[]{false, true, false, true, true, true, false, false, false, true};
		
		thresholdMethodList = new JComboBox(methods);
		thresholdMethodList.addActionListener (new ActionListener () {
		    public void actionPerformed(ActionEvent e) {
		        changeSelection();
		    }
		});
		this.add(thresholdMethodList);
		JPanel p = new JPanel(new GridLayout(1, 2));
		
		value = new LabeledTextField(Message.getString("set_value"), "0");
		value.setToolTipText(Comment.getString("threshold_value_tip"));
		p.add(value);
		isRemoveZero = new JCheckBox(Message.getString("remove_zero"));
		isRemoveZero.setToolTipText(Comment.getString("remove_zero"));
		isRemoveZero.setSelected(true);
		p.add(isRemoveZero);
		
		this.add(p);
		
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
		setSize(new Dimension(400, 120));
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);
		changeSelection();
		setModal(true);
		
	}
	
	protected void changeSelection() {
		this.value.setEnabled(isEditable[thresholdMethodList.getSelectedIndex()]);
		this.value.setEditable(isEditable[thresholdMethodList.getSelectedIndex()]);
		this.value.setVisible(isEditable[thresholdMethodList.getSelectedIndex()]);
	}

	private void okClick(){
		if (CommonFun.isDouble(this.value.getText())){
			this.selectedMethod = new ThresholdMethod(
					(String)this.thresholdMethodList.getSelectedItem(), 
					Double.valueOf(this.value.getText()).doubleValue(), 
					this.isRemoveZero.isSelected());
		}else{
			this.selectedMethod = null;
		}
		setVisible(false);
	}
	
	public ThresholdMethod getSelectedMethod(){
		
		return this.selectedMethod;
	}
	private void cancelClick() {
		this.selectedMethod = null;
		this.dispose();
		
	}
	
	
}
