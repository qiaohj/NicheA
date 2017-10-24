package org.ku.nicheanalyst.ui.display.component.ui;

import javax.swing.JCheckBox;

public class ObjectLinkedCheckBox extends JCheckBox {
	private Object obj;
	public ObjectLinkedCheckBox(String label, Object obj){
		super(label);
		this.obj = obj;
	}
	public Object getObj() {
		return obj;
	}
	public void setObj(Object obj) {
		this.obj = obj;
	}
	
}
