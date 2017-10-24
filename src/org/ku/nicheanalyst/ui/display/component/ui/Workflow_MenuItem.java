package org.ku.nicheanalyst.ui.display.component.ui;

import javax.swing.Action;
import javax.swing.JMenuItem;

import org.ku.nicheanalyst.common.CommonFun;
import org.ku.nicheanalyst.common.Const;
import org.ku.nicheanalyst.common.Message;
import org.ku.nicheanalyst.dataset.WorkFlow;
import org.ku.nicheanalyst.dataset.WorkFlowFunction;

public class Workflow_MenuItem extends JMenuItem {
	private String message;
	private String action_text;
	private WorkFlow workflow;
	private WorkFlowFunction function;
	public Workflow_MenuItem(String text, String message, String label, WorkFlow workflow){
		
		super(label);
		this.action_text = text;
		this.message = message;
		this.workflow = workflow;
		this.function = null;
	}
	public Workflow_MenuItem(String text, String message,
			String label, WorkFlowFunction function) {
		super(label);
		this.action_text = text;
		this.message = message;
		this.workflow = null;
		this.function = function;
	}
	public String getMessage() {
		return message;
	}
	public String getAction_text() {
		return action_text;
	}
	public WorkFlow getWorkflow(){
		return workflow;
	}
	public String getHTMLHelp() {
		if (this.workflow!=null){
			return this.workflow.getHTMLHelp();
		}
		if (this.function!=null){
			return this.function.getHTMLHelp();
		}
		return "";
	}
}
