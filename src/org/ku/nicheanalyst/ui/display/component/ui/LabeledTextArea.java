package org.ku.nicheanalyst.ui.display.component.ui;

import java.awt.Color;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.ku.nicheanalyst.common.CommonFun;

import edu.hws.jcm.awt.Limits;

public class LabeledTextArea extends JPanel {
	public static final int X_AXIS = 0;
	public static final int Y_AXIS = 1;
	private JLabel label;
	private JTextArea textArea;

	private int layout;

	public void requestFocus(){
		this.textArea.setBackground(Color.red);
		this.textArea.requestFocus();
	}
	
	public LabeledTextArea(String label, String value, int rows, int columns){
		layout = X_AXIS;
		this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		this.label = new JLabel(label);
		this.textArea = new JTextArea(value, rows, columns);
		this.add(this.label);
		this.add(this.textArea);
	}
	public void setLineWrap(boolean t){
		this.textArea.setLineWrap(t);
	}
	public LabeledTextArea(int layout, String label, String value, int rows, int columns){

		this.layout = layout;
		if (layout==X_AXIS){
			this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		}else{
			this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		}
		this.label = new JLabel(label);
		this.textArea = new JTextArea(value, rows, columns);
		this.add(this.label);
		this.add(this.textArea);
	}
	
	public String getText(){
		return this.textArea.getText();
	}
	public double getDouble(){
		if (CommonFun.isDouble(this.textArea.getText())){
			return Double.valueOf(this.textArea.getText()).doubleValue();
		}else{
			return -1 * Double.MAX_VALUE;
		}
		
	}
	public int getInt(){
		if (CommonFun.isInteger(this.textArea.getText())){
			return Integer.valueOf(this.textArea.getText()).intValue();
		}else{
			return Integer.MIN_VALUE;
		}
	}
	public void setText(String text) {
		this.textArea.setText(text);
		
	}
}
