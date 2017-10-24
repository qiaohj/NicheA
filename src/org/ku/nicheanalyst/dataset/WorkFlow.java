package org.ku.nicheanalyst.dataset;

import java.io.IOException;
import java.util.ArrayList;

import org.jdom.Element;
import org.ku.nicheanalyst.common.CommonFun;
import org.ku.nicheanalyst.common.Const;
import org.ku.nicheanalyst.common.Message;

public class WorkFlow {

	private String name;
	private String author;
	private String email;
	private String message;
	private ArrayList<WorkFlowFunction> children;
	public WorkFlow (Element item){
		if (item.getChild("name")!=null){
			this.name = item.getChildText("name");
		}else{
			this.name = "";
		}
		if (item.getChild("author")!=null){
			this.author = item.getChildText("author");
		}else{
			this.author = "";
		}
		if (item.getChild("email")!=null){
			this.email = item.getChildText("email");
		}else{
			this.email = "";
		}
		if (item.getChild("message")!=null){
			this.message = item.getChildText("message");
		}else{
			this.message = "";
		}
		this.children = new ArrayList<WorkFlowFunction>();
		if (item.getChildren("function").size()!=0){
			for (Object sub_item : item.getChildren("function")){
				this.children.add(new WorkFlowFunction((Element) sub_item));
			}
		}
	}
	public WorkFlow() {
		this.name = "";
		this.author = "";
		this.email = "";
		this.message = "";
		this.children = new ArrayList<WorkFlowFunction>();
	}
	public String getName() {
		return name;
	}
	public String getAuthor() {
		return author;
	}
	public String getEmail() {
		return email;
	}
	public ArrayList<WorkFlowFunction> getChildren() {
		return children;
	}
	public String getMessage() {
		return message;
	}
	public String toString(){
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public void addChild(WorkFlowFunction child) {
		this.children.add(child);
	}
	public void removeAllChild() {
		this.children = new ArrayList<WorkFlowFunction>();
	}
	public void removeChild(int index) {
		this.children.remove(index);
	}
	public void save(String file) throws IOException {
		Element root = new Element("workflow");
		
		Element item = new Element("name");
		item.setText(this.name);
		root.getChildren().add(item);
		
		item = new Element("email");
		item.setText(this.email);
		root.getChildren().add(item);
		
		item = new Element("author");
		item.setText(this.author);
		root.getChildren().add(item);
		
		item = new Element("message");
		item.setText(this.message);
		root.getChildren().add(item);
		
		for (WorkFlowFunction func : this.children){
			root.getChildren().add(func.getElement());
		}
	    CommonFun.writeXML(root, file);
		
	}
	public String getHTMLHelp() {
		StringBuilder sb = new StringBuilder();
		sb.append(Message.getString("html_head"));
		sb.append("<h2>Workflow help</h2>" + Const.LineBreak);
		sb.append(String.format("<li>Name: <font color='red'>%s</font></li>", this.name));
		sb.append(String.format("<li>Authors: <font color='red'>%s</font></li>", this.author));
		sb.append(String.format("<li>Emails: %s</li>", this.email));
		sb.append("<h3>Description:</h3>");
		sb.append("<div style=\"background-color:#EEEEEE; border-radius:10px; border: 1px black solid;\">");
		sb.append(CommonFun.toHTML(this.message));
		sb.append("</div>");
		sb.append("<h3>Functions:</h3>");
		for (WorkFlowFunction function : this.children){
			sb.append(function.getHTMLHelp());
		}
		sb.append(Message.getString("html_tail"));
		return sb.toString();
	}
}
