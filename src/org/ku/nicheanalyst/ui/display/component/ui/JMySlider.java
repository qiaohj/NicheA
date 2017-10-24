package org.ku.nicheanalyst.ui.display.component.ui;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.ku.nicheanalyst.ui.display.Displayer;

public class JMySlider extends JPanel  implements ChangeListener{

	private JTextField value;
	private JSlider slider;
	private JLabel label;
	private boolean is_input;
	public JMySlider(String labelstr, int director, int min, int max, int v, final boolean isScale, final Displayer theApp){
		is_input = false;
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.value = new JTextField("0", 5);
		this.value.setHorizontalAlignment(JTextField.RIGHT);
		this.slider = new JSlider(director, min, max, v);
		this.label = new JLabel(labelstr);
		JPanel p = new JPanel();
		SpringLayout layout = new SpringLayout();
		p.setLayout(layout);
		layout.putConstraint(SpringLayout.WEST, label,
                5,
                SpringLayout.WEST, p);
		layout.putConstraint(SpringLayout.NORTH, label,
                15,
                SpringLayout.NORTH, p);
		layout.putConstraint(SpringLayout.WEST, value,
                5,
                SpringLayout.EAST, label);
		layout.putConstraint(SpringLayout.NORTH, value,
                15,
                SpringLayout.NORTH, p);
		p.add(label);
		p.add(value);
		slider.addChangeListener(new ChangeListener(){
            @Override
            public void stateChanged(ChangeEvent e) {
            	float v = -1;
            	if (!isScale){
            		v = slider.getValue();
            	}else{
            		v = (float)slider.getValue()/100f * (float)theApp.getMaxLength();
            	}
            	if (!is_input){
            		value.setText(String.format("%.2f", v));
            	}
            	theApp.createEllipsoidGroup();
            	is_input = false;
            }
        });
		value.addKeyListener(new KeyAdapter(){
            @Override
            public void keyReleased(KeyEvent ke) {
                String typed = value.getText();
                if(!typed.matches("[-+]?\\d+(\\.\\d*)?")) {
                    return;
                }
                double value = Double.parseDouble(typed) * 100f / theApp.getMaxLength();
                is_input = true;
                slider.setValue((int)value);
                
            }
        });
		this.add(p);
		this.add(slider);
		
	}
	@Override
	public void stateChanged(ChangeEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	public void setValue(int v) {
		this.slider.setValue(v);
		this.value.setText(String.valueOf(v));
		
	}
	public void setEnabled(boolean v){
		this.slider.setEnabled(v);
		this.value.setEnabled(v);
	}
	public int getValue() {
		
		return this.slider.getValue();
	}
}
