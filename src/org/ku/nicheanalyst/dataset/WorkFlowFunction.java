package org.ku.nicheanalyst.dataset;

import org.jdom.Element;
import org.ku.nicheanalyst.common.CommonFun;

public class WorkFlowFunction{
	private String name;
	private String function_name;
	private String message;
	
	public WorkFlowFunction(String name, String function_name, String message) {
		super();
		this.name = name;
		this.function_name = function_name;
		this.message = message;
	}
	public WorkFlowFunction(Element item) {
		if (item.getChild("name")!=null){
			this.name = item.getChildText("name");
		}else{
			this.name = "";
		}
		if (item.getChild("function_name")!=null){
			this.function_name = item.getChildText("function_name");
		}else{
			this.function_name = "";
		}
		if (item.getChild("message")!=null){
			this.message = item.getChildText("message");
		}else{
			this.message = "";
		}
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getFunction_name() {
		return function_name;
	}
	public void setFunction_name(String function_name) {
		this.function_name = function_name;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
	public String toString(){
		return String.format("%s (%s)", this.name, this.function_name);
	}
	public Element getElement() {
		Element root = new Element("function");
		Element item = new Element("function_name");
		item.setText(this.function_name);
		root.getChildren().add(item);
		
		item = new Element("name");
		item.setText(this.name);
		root.getChildren().add(item);
		
		item = new Element("message");
		item.setText(this.message);
		root.getChildren().add(item);
		
		return root;
		
	}
	public String getHTMLHelp() {
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("<h4>Function name:%s</h4>", this.name));
		sb.append("<div style=\"background-color:#EEEEEE; border-radius:10px; border: 1px black solid;\">");
		sb.append(CommonFun.toHTML(this.message));
		sb.append("</div>");
		return sb.toString();
	}
}