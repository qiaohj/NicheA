package org.ku.nicheanalyst.common;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.ku.nicheanalyst.dataset.WorkFlow;
import org.ku.nicheanalyst.dataset.WorkFlowFunction;

public class WorkflowLoader {
	private static WorkflowLoader instance;
	private static ArrayList<WorkFlow> workflows;
	private static Element root;
	private static File workflow_xml;
	private WorkflowLoader(){
		SAXBuilder builder = new SAXBuilder();
		workflow_xml = new File(CommonFun.getUserFolder(), "nichea.workflow." + 
				Message.getString("version").replace(" ", ".") + 
				".xml");
		Document document = null;
		try{
			if (!workflow_xml.exists()){
				document = (Document) builder.build(this.getClass().getResourceAsStream("/workflows.xml"));
				Element root = (Element) document.getRootElement().clone();
				root.detach();
				CommonFun.writeXML(root, workflow_xml.getAbsolutePath());
			}else{
				document = (Document) builder.build(workflow_xml);
			}
			root = (Element) document.getRootElement().clone();
			root.detach();
			this.workflows = new ArrayList<WorkFlow>();
			for (Object item : root.getChildren()){
				this.workflows.add(new WorkFlow((Element) item));
			}
		}catch (Exception e){
			e.printStackTrace();
		}
	}
	public static final WorkflowLoader getInstance(){
		try{
			instance=new WorkflowLoader();
		}
		catch (Exception e){
			e.printStackTrace();
		}
		return instance;
	}
	
	public static final ArrayList<WorkFlow> getWorkflows(){
		return workflows;
	}
	public static boolean isExist(String workflow_name){
		for (Object item : root.getChildren()){
			Element t = (Element) item;
			if (t.getChild("name")!=null){
				if (t.getChildText("name").equalsIgnoreCase(workflow_name)){
					return true;
				}
			}
		}
		return false;
	}
	public static final String addWorkflow(String target){
		SAXBuilder builder = new SAXBuilder();
		File new_workflow_xml = new File(target);
		Document document = null;
		try {
			document = (Document) builder.build(new_workflow_xml);
		} catch (JDOMException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		if (document==null){
			return Message.getString("error_workflow_configuration");
		}
		Element workflow_root = (Element) document.getRootElement().clone();
		workflow_root.detach();
		String name = "";
		if (workflow_root.getChild("name")!=null){
			name = workflow_root.getChildText("name");
		}
		if (name.trim().equals("")){
			return Message.getString("workflow_should_has_a_name");
		}
		if (isExist(name)){
			return Message.getString("duplicated_workflow");
		}
		root.getChildren().add(workflow_root);
		try {
			Element rootcopy = (Element) root.clone();
			rootcopy.detach();
			CommonFun.writeXML(rootcopy, workflow_xml.getAbsolutePath());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return Message.getString("done");
	}
	public static final String removeWorkflow(String name){
		if (!isExist(name)){
			return String.format(Message.getString("no_workflow"), name);
		}
		for (int i=0;i<root.getContentSize();i++){
			Element t = (Element) root.getChildren().get(i);
			if (t.getChild("name")!=null){
				if (t.getChildText("name").equalsIgnoreCase(name)){
					root.getChildren().remove(i);
					break;
				}
			}
		}
		try {
			Element rootcopy = (Element) root.clone();
			rootcopy.detach();
			
			CommonFun.writeXML(rootcopy, workflow_xml.getAbsolutePath());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return Message.getString("done");
	}
	public WorkFlowFunction getWorkFlow(String workflow_name, String actionName) {
		for (WorkFlow workflow : this.workflows){
			if (workflow.getName().equals(workflow_name)){
				for (WorkFlowFunction function : workflow.getChildren()){
					if (Message.getString(function.getFunction_name()).equals(actionName)){
						return function;
					}
				}
			}
		}
		return null;
	}
	public ArrayList<WorkFlowFunction> getAllFunctions() {
		ArrayList<WorkFlowFunction> functions = new ArrayList<WorkFlowFunction>();
		SAXBuilder builder = new SAXBuilder();
		Document document = null;
		try{
			document = (Document) builder.build(this.getClass().getResourceAsStream("/workflow_functions.xml"));
			Element root = document.getRootElement();
			for (Object f : root.getChildren()){
				Element fitem = (Element) f;
				functions.add(new WorkFlowFunction(fitem));
			}
		}catch (Exception e){
			e.printStackTrace();
		}
		return functions;
	}
}

