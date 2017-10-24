/******************************************************************************
 * Huijie Qiao
 *
 * Project:  Niche Analyst
 * Purpose:  
 * Created date: May 4, 2012 7:15:18 PM
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


package org.ku.nicheanalyst.ui.display;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Properties;
import java.util.TreeMap;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.MenuElement;

import org.jdom.JDOMException;
import org.ku.nicheanalyst.common.Comment;
import org.ku.nicheanalyst.common.CommonFun;
import org.ku.nicheanalyst.common.ConfigInfo;
import org.ku.nicheanalyst.common.Const;
import org.ku.nicheanalyst.common.Message;
import org.ku.nicheanalyst.common.WorkflowLoader;
import org.ku.nicheanalyst.dataset.SpeciesDataset;
import org.ku.nicheanalyst.dataset.WorkFlow;
import org.ku.nicheanalyst.dataset.WorkFlowFunction;
import org.ku.nicheanalyst.ui.display.component.ui.IDMenuItem;
import org.ku.nicheanalyst.ui.display.component.ui.Workflow_MenuItem;
import org.ku.nicheanalyst.ui.display.dialog.HTMLViewer;
import org.xml.sax.SAXException;

/**
 * @author Huijie Qiao
 *
 */
public class Menu extends JMenuBar implements ActionListener{
	private static final long serialVersionUID = -5321549021445720685L;
	private Displayer theApp;
//	private JMenuItem showPoint;
//	private JMenuItem showMVE;
	private JMenuItem showGrid;
	private JMenuItem showBackground;
	private JMenuItem showAxis;
//	private JMenuItem showConvexhull;
	private JMenuItem showSelection;
	private JMenuItem showMultiEllipsoid;
	private JMenuItem showAxes;
	private JMenu vsMenu;
	private TreeMap<String, IDMenuItem> virtualspecies;
	private JMenu workflowMenu;
	public Menu (Displayer theApp){
		this.theApp = theApp;
		JMenu fileMenu = new JMenu(Message.getString("file"));
	    JMenuItem item;
	    
	    fileMenu.add(item = new JMenuItem(Message.getString("open_species")));
	    item.setToolTipText(Comment.getString("open_species"));
	    item.addActionListener(this);
	    fileMenu.add(item = new JMenuItem(Message.getString("close_species")));
	    item.setToolTipText(Comment.getString("close_species"));
	    item.addActionListener(this);
	    fileMenu.add(item = new JMenuItem(Message.getString("clear_all")));
	    item.setToolTipText(Comment.getString("clear_all"));
	    item.addActionListener(this);
	    
	    fileMenu.addSeparator();
	    
	    fileMenu.add(item = new JMenuItem(Message.getString("save_current_selection")));
	    item.setToolTipText(Comment.getString("save_current_selection"));
	    item.addActionListener(this);
	    fileMenu.add(item = new JMenuItem(Message.getString("open_selection")));
	    item.setToolTipText(Comment.getString("open_selection"));
	    item.addActionListener(this);
	    fileMenu.add(item = new JMenuItem(Message.getString("open_selections")));
	    item.setToolTipText(Comment.getString("open_selections"));
	    item.addActionListener(this);
	    
	    
	    fileMenu.addSeparator();
	    
	    fileMenu.add(item = new JMenuItem(Message.getString("draw_background_from_saved_package")));
	    item.setToolTipText(Comment.getString("draw_background_from_saved_package"));
	    item.addActionListener(this);
	    fileMenu.add(item = new JMenuItem(Message.getString("draw_background_from_files")));
	    item.setToolTipText(Comment.getString("draw_background_from_files"));
	    item.addActionListener(this);
	    
	    fileMenu.addSeparator();
	    fileMenu.add(item = new JMenuItem(Message.getString("export_to_enm")));
	    item.setToolTipText(Comment.getString("export_to_enm"));
	    item.addActionListener(this);
	    
	    fileMenu.addSeparator();
	    fileMenu.add(item = new JMenuItem(Message.getString("close")));
	    item.setToolTipText(Comment.getString("close"));
	    item.addActionListener(this);
	    
	    
	    JMenu toolMenu = new JMenu(Message.getString("tools"));
	    toolMenu.add(item = new JMenuItem(Message.getString("create_virtual_species")));
	    item.setToolTipText(Comment.getString("create_virtual_species"));
	    item.addActionListener(this);
	    toolMenu.add(item = new JMenuItem(Message.getString("generate_species_dataset")));
	    item.setToolTipText(Comment.getString("generate_species_dataset"));
	    item.addActionListener(this);
	    toolMenu.add(item = new JMenuItem(Message.getString("generate_species_dataset_mve")));
	    item.setToolTipText(Comment.getString("generate_species_dataset_mve"));
	    item.addActionListener(this);
	    toolMenu.add(item = new JMenuItem(Message.getString("show_species_dataset_attributes")));
	    item.setToolTipText(Comment.getString("show_species_dataset_attributes"));
	    item.addActionListener(this);
	    
	    toolMenu.add(item = new JMenuItem(Message.getString("calculate_overlap")));
	    item.setToolTipText(Comment.getString("calculate_overlap"));
	    item.addActionListener(this);
//	    toolMenu.add(item = new JMenuItem(Message.getString("draw_overlap")));
//	    item.addActionListener(this);
	    toolMenu.add(item = new JMenuItem(Message.getString("design_barrier")));
	    item.setToolTipText(Comment.getString("design_barrier"));
	    item.addActionListener(this);
	    toolMenu.add(item = new JMenuItem(Message.getString("pca_generator")));
	    item.setToolTipText(Comment.getString("pca_generator"));
	    item.addActionListener(this);
	    toolMenu.add(item = new JMenuItem(Message.getString("raster_convert")));
	    item.setToolTipText(Comment.getString("raster_convert"));
	    item.addActionListener(this);
	    toolMenu.add(item = new JMenuItem(Message.getString("threshold_variables")));
	    item.setToolTipText(Comment.getString("threshold_variables"));
	    item.addActionListener(this);
	    toolMenu.add(item = new JMenuItem(Message.getString("occurrence_distribution")));
	    item.setToolTipText(Comment.getString("occurrence_distribution"));
	    item.addActionListener(this);
	    toolMenu.add(item = new JMenuItem(Message.getString("voccur2geo")));
	    item.setToolTipText(Comment.getString("voccur2geo"));
	    item.addActionListener(this);
	    toolMenu.add(item = new JMenuItem(Message.getString("stat_variables")));
	    item.setToolTipText(Comment.getString("stat_variables"));
	    item.addActionListener(this);
//	    toolMenu.add(item = new JMenuItem(Message.getString("multivarnormal")));
//	    item.setToolTipText(Comment.getString("multivarnormal"));
//	    item.addActionListener(this);
//	    toolMenu.add(item = new JMenuItem(Message.getString("design_curve")));
//	    item.addActionListener(this);
//	    toolMenu.add(item = new JMenuItem(Message.getString("niche_breadth")));
//	    item.addActionListener(this);
//	    toolMenu.add(item = new JMenuItem(Message.getString("threshold_calculate")));
//	    item.addActionListener(this);
	    toolMenu.add(item = new JMenuItem(Message.getString("read_data_from_multi_geotiff_files")));
	    item.setToolTipText(Comment.getString("read_data_from_multi_geotiff_files"));
	    item.addActionListener(this);
	    toolMenu.add(item = new JMenuItem(Message.getString("variable_standardization")));
	    item.setToolTipText(Comment.getString("variable_standardization"));
	    item.addActionListener(this);
	    toolMenu.add(item = new JMenuItem(Message.getString("set_nodata_value")));
	    item.setToolTipText(Comment.getString("set_nodata_value"));
	    item.addActionListener(this);
	    toolMenu.add(item = new JMenuItem(Message.getString("convert_text_to_raster")));
	    item.setToolTipText(Comment.getString("convert_text_to_raster"));
	    item.addActionListener(this);
	    toolMenu.add(item = new JMenuItem(Message.getString("setenmrange")));
	    item.setToolTipText(Comment.getString("setenmrange"));
	    item.addActionListener(this);
	    
	    toolMenu.add(item = new JMenuItem(Message.getString("partial_roc")));
	    item.setToolTipText(Comment.getString("partial_roc"));
	    item.addActionListener(this);
	    
	    toolMenu.add(item = new JMenuItem(Message.getString("aic_calculator")));
	    item.setToolTipText(Comment.getString("aic_calculator"));
	    item.addActionListener(this);
	    
	    
	    JMenu controlMenu = new JMenu(Message.getString("control"));

//	    showPoint = new JMenuItem(Message.getString("hide_point"));
//	    showPoint.setToolTipText(Comment.getString("hide_point"));
//	    showPoint.addActionListener(this);
//	    controlMenu.add(showPoint);
//	    
//	    showMVE = new JMenuItem(Message.getString("hide_mve"));
//	    showMVE.setToolTipText(Comment.getString("hide_mve"));
//	    showMVE.addActionListener(this);
//	    controlMenu.add(showMVE);
	    
	    showGrid = new JMenuItem(Message.getString("show_grid"));
	    showGrid.setToolTipText(Comment.getString("show_grid"));
	    showGrid.addActionListener(this);
	    controlMenu.add(showGrid);
	    
	    showBackground = new JMenuItem(Message.getString("hide_background_point"));
	    showBackground.setToolTipText(Comment.getString("hide_background_point"));
	    showBackground.addActionListener(this);
	    controlMenu.add(showBackground);
	    
//	    showConvexhull = new JMenuItem(Message.getString("hide_convexhull"));
//	    showConvexhull.setToolTipText(Comment.getString("hide_convexhull"));
//	    showConvexhull.addActionListener(this);
//	    controlMenu.add(showConvexhull);
	    
	    showSelection = new JMenuItem(Message.getString("hide_selection"));
	    showSelection.setToolTipText(Comment.getString("hide_selection"));
	    showSelection.addActionListener(this);
	    controlMenu.add(showSelection);
	    
	    showMultiEllipsoid = new JMenuItem(Message.getString("hide_multiple_selections"));
	    showMultiEllipsoid.setToolTipText(Comment.getString("hide_multiple_selections"));
	    showMultiEllipsoid.addActionListener(this);
	    controlMenu.add(showMultiEllipsoid);
	    
	    showAxes = new JMenuItem(Message.getString("hide_axes"));
	    showAxes.setToolTipText(Comment.getString("hide_axes"));
	    showAxes.addActionListener(this);
	    controlMenu.add(showAxes);
	    
	    controlMenu.add(item = new JMenuItem(Message.getString("reset_x_y")));
	    item.setToolTipText(Comment.getString("reset_x_y"));
	    item.addActionListener(this);
	    
	    controlMenu.add(item = new JMenuItem(Message.getString("reset_x_z")));
	    item.setToolTipText(Comment.getString("reset_x_z"));
	    item.addActionListener(this);
	    
	    controlMenu.add(item = new JMenuItem(Message.getString("reset_y_z")));
	    item.setToolTipText(Comment.getString("reset_y_z"));
	    item.addActionListener(this);
	    
	    controlMenu.add(item = new JMenuItem(Message.getString("change_background")));
	    item.addActionListener(this);
//	    
	    virtualspecies = new TreeMap<String, IDMenuItem>();
	    vsMenu = new JMenu(Message.getString("species_dataset"));
	    
	    vsMenu.add(item = new JMenuItem(Message.getString("set_vs_visibility")));
	    item.addActionListener(this);
	    
//	    JMenu troubleshootingMenu = new JMenu(Message.getString("troubleshooting"));
	    
	    
	    JMenu aboutMenu = new JMenu(Message.getString("about"));
	    
	    aboutMenu.add(item = new JMenuItem(Message.getString("application.title")));
	    item.setToolTipText(Comment.getString("application.title"));
	    item.addActionListener(this);
	    
	    aboutMenu.add(item = new JMenuItem(Message.getString("update")));
	    item.setToolTipText(Comment.getString("update"));
	    item.addActionListener(this);
	    
	    
	    aboutMenu.add(item = new JMenuItem(Message.getString("preferences")));
	    item.setToolTipText(Comment.getString("preferences"));
	    item.addActionListener(this);
	    aboutMenu.add(item = new JMenuItem(Message.getString("troubleshooting")));
	    item.setToolTipText(Comment.getString("troubleshooting"));
	    item.addActionListener(this);
	    
	    if (!CommonFun.isLinux()){
	    	aboutMenu.add(item = new JMenuItem(Message.getString("showlog")));
	    	item.setToolTipText(Comment.getString("showlog"));
	    	item.addActionListener(this);
	    }
	    
	    add(fileMenu);
	    add(sortedToolMenu());
	    //
	    workflowMenu = new JMenu(Message.getString("workflow"));
	    addWorkflowMenu();
	    add(workflowMenu);
	    add(controlMenu);
	    add(vsMenu);
//	    add(enmMenu);
//	    add(troubleshootingMenu);
	    //add(toolMenu);
	    add(aboutMenu);

	}

	

	public void addWorkflowMenu() {
		
		JMenuItem item;
	    workflowMenu.add(item = new JMenuItem(Message.getString("manage_workflow")));
	    item.addActionListener(this);
	    JMenu all_workflow = new JMenu(Message.getString("all_workflow")); 
	    workflowMenu.add(all_workflow);
	    
	    for (WorkFlow workflow : WorkflowLoader.getInstance().getWorkflows()){
	    	addWorkFlow(workflow, all_workflow);
	    }
	    
	}
	
	public void removeWorkflowMenu(){
		workflowMenu.removeAll();
	}
	private void addWorkFlow(WorkFlow workflow, JMenu parent) {
		String name = Message.getString("workflow") + " : " + workflow.getName();
		name = Message.getString("workflow") + " : " + workflow.getName();
		JMenu workflow_menu = new JMenu(name);
//		workflow_menu.addActionListener(this);
		Workflow_MenuItem workflow_function = new Workflow_MenuItem(
				String.format(Message.getString("workflow_help_for") , name),
				"", String.format(Message.getString("workflow_help_for") , name), workflow);
		workflow_function.addActionListener(this);
		workflow_menu.add(workflow_function);
		
		parent.add(workflow_menu);
		for (WorkFlowFunction sub_workflow : workflow.getChildren()){
			String function_name = Message.getString(sub_workflow.getFunction_name());
			workflow_function = new Workflow_MenuItem(function_name, sub_workflow.getMessage(), sub_workflow.getName(), sub_workflow);
			workflow_function.addActionListener(this);
			workflow_menu.add(workflow_function);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		String actionName = e.getActionCommand();
		if (actionName==null){
			return;
		}
		if (e.getSource() instanceof Workflow_MenuItem) {
			Workflow_MenuItem item = (Workflow_MenuItem) e.getSource();
			
			JPopupMenu parent = (JPopupMenu) item.getParent();
			JMenu actMenu = (JMenu)parent.getInvoker();
			String label = actMenu.getText();
			if (label.startsWith(Message.getString("workflow"))){
				File f = new File(ConfigInfo.getInstance().getTemp() + "/workflow_help.html");
				try {
					CommonFun.writeFile(item.getHTMLHelp(), f.getAbsolutePath());
					HTMLViewer viewer = new HTMLViewer(f, item.getName(), 800, 800, false);
					viewer.setVisible(true);
				} catch (SAXException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				actionName = item.getAction_text();
			}
			
		}
		if (actionName.equalsIgnoreCase(Message.getString("close"))){
			this.theApp.exit();
		}
		if (actionName.equalsIgnoreCase(Message.getString("close_species"))){
			try {
				this.theApp.closeSpecies(getVSList(0));
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		if (actionName.equalsIgnoreCase(Message.getString("clear_all"))){
			this.theApp.clearAll();
		}
		
		if (actionName.equalsIgnoreCase(Message.getString("reset_x_y"))){
			this.theApp.reset_x_y();
		}
		if (actionName.equalsIgnoreCase(Message.getString("reset_x_z"))){
			this.theApp.reset_x_z();
		}
		if (actionName.equalsIgnoreCase(Message.getString("reset_y_z"))){
			this.theApp.reset_y_z();
		}
		if (actionName.equalsIgnoreCase(Message.getString("save_current_selection"))){
			try {
				this.theApp.saveEllipsoid();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		if (actionName.equalsIgnoreCase(Message.getString("create_virtual_species"))){
			String[] vplist = getVSList(0);
			this.theApp.createVirtualSpecies(vplist);
		}
		if (actionName.equalsIgnoreCase(Message.getString("raster_convert"))){
			this.theApp.rasteConvert();
		}
		
		if (actionName.equalsIgnoreCase(Message.getString("generate_species_dataset"))){
			this.theApp.generateSpeciesDataset();
		}
		if (actionName.equalsIgnoreCase(Message.getString("generate_species_dataset_mve"))){
			this.theApp.generateSpeciesDataset_MVE();
		}
		if (actionName.equalsIgnoreCase(Message.getString("pca_generator"))){
			this.theApp.generatePCA();
		}
		if (actionName.equalsIgnoreCase(Message.getString("change_background"))){
			this.theApp.changeBackground();
		}
		if (actionName.equalsIgnoreCase(Message.getString("stat_variables"))){
			this.theApp.stat_variables();
		}
		if (actionName.equalsIgnoreCase(Message.getString("multivarnormal"))){
			this.theApp.multivarnormal();
		}
		if (actionName.equalsIgnoreCase(Message.getString("open_species"))){
			try {
				
				this.theApp.drawVPWithConvexHull();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		if (actionName.equalsIgnoreCase(Message.getString("occurrence_distribution"))){
			this.theApp.getOccurrenceDistribution();
		}
		if (actionName.equalsIgnoreCase(Message.getString("voccur2geo"))){
			this.theApp.CreateDistributionByPoints();
		}
		
		
		if (actionName.equalsIgnoreCase(Message.getString("open_selection"))){
			try {
				this.theApp.openEllipsoid();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (JDOMException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
		}
		
		if (actionName.equalsIgnoreCase(Message.getString("open_selections"))){
			try {
				this.theApp.createMultiEllipsoidGroup();
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (JDOMException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		if (actionName.equalsIgnoreCase(Message.getString("preferences"))){
			this.theApp.showConfig();
		}
		if (actionName.equalsIgnoreCase(Message.getString("update"))){
			this.theApp.update();
		}
		
		if (actionName.equalsIgnoreCase(Message.getString("application.title"))){
			Runtime runtime = Runtime.getRuntime();

		    NumberFormat format = NumberFormat.getInstance();

		    
		    long maxMemory = runtime.maxMemory();
		    long freeMemory = runtime.freeMemory();
		    long totalMemory = runtime.totalMemory();
		    long allocatedMemory = totalMemory - freeMemory;
		    
		    StringBuilder sb = new StringBuilder();
		    Properties props = System.getProperties();
		    sb.append("<br>Max memory: " + format.format(maxMemory / 1048576) + "m</br>" + Const.LineBreak);
		    sb.append("<br>Total memory: " + format.format(totalMemory / 1048576) + "m</br>" + Const.LineBreak);
		    sb.append("<br>Free memory: " + format.format(freeMemory / 1048576) + "m</br>" + Const.LineBreak);
		    sb.append("<br>Allocated memory: " + format.format(allocatedMemory / 1048576) + "m</br>" + Const.LineBreak);
		    sb.append("<br>Java system language: " + props.getProperty("user.country") + "</br>" + Const.LineBreak);
		    sb.append("<br>Java system locale: " + Locale.getDefault() + "</br>" + Const.LineBreak);
		    
		    StringBuilder sb_html = new StringBuilder();
		    sb_html.append(Message.getString("html_head"));
		    sb_html.append(String.format("<h2>%s</h2>", Message.getString("application.title") + " " + Message.getString("version")));
		    sb_html.append("<h3>Authors:</h3>");
		    sb_html.append(String.format("<li>%s (<a href='mailto:%s'>%s</a>)</li>", "Qiao, Huijie", "huijieqiao@gmail.com", "huijieqiao@gmail.com"));
		    sb_html.append(String.format("<li>%s (<a href='mailto:%s'>%s</a>)</li>", "Soberon, Jorge", "jsoberon@ku.edu", "jsoberon@ku.edu"));
		    sb_html.append(String.format("<li>%s (<a href='mailto:%s'>%s</a>)</li>", "Peterson, A. Townsend", "town@ku.edu", "town@ku.edu"));
		    sb_html.append(String.format("<li>%s (<a href='mailto:%s'>%s</a>)</li>", "Luis E. Escobar", "ecoguate2003@gmail.com", "ecoguate2003@gmail.com"));
		    sb_html.append(String.format("<li>%s (<a href='mailto:%s'>%s</a>)</li>", "Campbell, Lindsay", "lpcampbell@ku.edu", "lpcampbell@ku.edu"));
		    sb_html.append("<h3>Running status:</h3>");
		    sb_html.append(sb.toString() + Message.getString("html_tail"));
		    
		    File htmlFile = new File(ConfigInfo.getInstance().getTemp() + "/about.html");
		    try {
		    	if (!htmlFile.exists()){
		    		CommonFun.writeFile(sb_html.toString(), htmlFile.getAbsolutePath());
		    	}
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		    HTMLViewer viewer = null;
			try {
				viewer = new HTMLViewer(htmlFile, Message.getString("application.title"), 400, 550, false);
			} catch (MalformedURLException e1) {
				e1.printStackTrace();
			} catch (SAXException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		    viewer.setVisible(true);

		}
		
		if (actionName.equalsIgnoreCase(Message.getString("read_data_from_multi_geotiff_files"))){
			this.theApp.readMultiFile();
		}
		if (actionName.equalsIgnoreCase(Message.getString("draw_background_from_saved_package"))){
			try {
				this.theApp.drawBackgroup();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		
		if (actionName.equalsIgnoreCase(Message.getString("export_to_enm"))){
			try {
				this.theApp.export(getVSList(1));
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		
		if (actionName.equalsIgnoreCase(Message.getString("draw_background_from_files"))){
			try {
				this.theApp.drawBackgroupFromFiles();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		if (actionName.equalsIgnoreCase(Message.getString("design_barrier"))){
			this.theApp.designBarriers(getVSList(1));
		}
		if (actionName.equalsIgnoreCase(Message.getString("troubleshooting"))){
			try {
				this.theApp.troubleshooting();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		if (actionName.equalsIgnoreCase(Message.getString("design_curve"))){
			this.theApp.design_curve();
		}
		if (actionName.equalsIgnoreCase(Message.getString("convert_text_to_raster"))){
			try {
				this.theApp.convert_text_to_raster();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		
		
		if (actionName.equalsIgnoreCase(Message.getString("partial_roc"))){
			try {
				this.theApp.partial_roc();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (SAXException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		
		if (actionName.equalsIgnoreCase(Message.getString("aic_calculator"))){
			try {
				this.theApp.aic_calculator();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		
		if (actionName.equalsIgnoreCase(Message.getString("setenmrange"))){
			this.theApp.setenmrange();
		}
		
		
		if (actionName.equalsIgnoreCase(Message.getString("niche_breadth"))){
			this.theApp.niche_breadth();
		}
		if (actionName.equalsIgnoreCase(Message.getString("threshold_calculate"))){
			this.theApp.threshold_calculate();
		}
		if (actionName.equalsIgnoreCase(Message.getString("manage_workflow"))){
			this.theApp.manage_workflow();
		}
		if (actionName.equalsIgnoreCase(Message.getString("threshold_variables"))){
			try {
				this.theApp.threshold_variables();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (SAXException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		
		if (actionName.equalsIgnoreCase(Message.getString("draw_overlap"))){
			
//			if (vplist.length>1){
//				String vp1 = (String) JOptionPane.showInputDialog(this,
//						Message.getString("select_a_species"),vplist[0], JOptionPane.INFORMATION_MESSAGE, 
//						null, vplist, vplist[0]);
//				String vp2 = (String) JOptionPane.showInputDialog(this,
//						Message.getString("select_a_species"),vplist[1], JOptionPane.INFORMATION_MESSAGE, 
//						null, vplist, vplist[1]);
//				this.theApp.drawOverlap(vp1, vp2);
//			}else{
//				this.theApp.ShowAlert(Message.getString("noengouth_species_dataset"));
//			}
		}
		
		if (actionName.equalsIgnoreCase(Message.getString("calculate_overlap"))){
			String[] vplist = new String[virtualspecies.size()];
			int i = 0;
			for (String key : virtualspecies.keySet()){
				vplist[i] = key;
				i++;
			}
			if (vplist.length>1){
				this.theApp.CalculateOverlap(vplist);
			}else{
				this.theApp.ShowAlert(Message.getString("noengouth_species_dataset"));
			}
		}
		
		if (actionName.equalsIgnoreCase(Message.getString("hide_point"))
				||actionName.equalsIgnoreCase(Message.getString("show_point"))){
			JMenuItem item = (JMenuItem) e.getSource();
			String tipText = item.getToolTipText();
			if (tipText.contains(" of ")){
				JPopupMenu p = (JPopupMenu)item.getParent();
				this.theApp.hideShowPoint(((IDMenuItem)p.getInvoker()).getKey());

			}else{
				this.theApp.hideShowPoint();
			}
			handleShowHideMenu();
			
		}
		if (actionName.equalsIgnoreCase(Message.getString("hide_selection"))
				||actionName.equalsIgnoreCase(Message.getString("show_selection"))){
			this.theApp.hideShowSelection();
			if (this.theApp.isShowSelection()){
				((JMenuItem)e.getSource()).setText(Message.getString("hide_selection"));
				((JMenuItem)e.getSource()).setToolTipText(Comment.getString("hide_selection"));
			}else{
				((JMenuItem)e.getSource()).setText(Message.getString("show_selection"));
				((JMenuItem)e.getSource()).setToolTipText(Comment.getString("show_selection"));
			}
		}
		if (actionName.equalsIgnoreCase(Message.getString("show_species_dataset_attributes"))){
			this.theApp.show_species_dataset_attribute();
		}
		if (actionName.equalsIgnoreCase(Message.getString("showlog"))){
			this.theApp.showLog();
		}
		
		if (actionName.equalsIgnoreCase(Message.getString("hide_convexhull"))||
				actionName.equalsIgnoreCase(Message.getString("show_convexhull"))){
			JMenuItem item = (JMenuItem) e.getSource();
			String tipText = item.getToolTipText();
			if (tipText.contains(" of ")){
				JPopupMenu p = (JPopupMenu)item.getParent();
				this.theApp.hideShowConvexHull(((IDMenuItem)p.getInvoker()).getKey());

			}else{
				this.theApp.hideShowConvexHull();
			}
			handleShowHideMenu();
			
		}
		
		if (actionName.equalsIgnoreCase(Message.getString("hide_mve"))||
				actionName.equalsIgnoreCase(Message.getString("show_mve"))){
			JMenuItem item = (JMenuItem) e.getSource();
			String tipText = item.getToolTipText();
			if (tipText.contains(" of ")){
				JPopupMenu p = (JPopupMenu)item.getParent();
				this.theApp.hideShowMVE(((IDMenuItem)p.getInvoker()).getKey());

			}else{
				this.theApp.hideShowMVE();
			}
			handleShowHideMenu();
		}
		if (actionName.equalsIgnoreCase(Message.getString("hide_grid"))||
				actionName.equalsIgnoreCase(Message.getString("show_grid"))){
			this.theApp.showHideGrid();
			if (this.theApp.isShowGrid()){
				((JMenuItem)e.getSource()).setText(Message.getString("hide_grid"));
				((JMenuItem)e.getSource()).setToolTipText(Comment.getString("hide_grid"));
			}else{
				((JMenuItem)e.getSource()).setText(Message.getString("show_grid"));
				((JMenuItem)e.getSource()).setToolTipText(Comment.getString("show_grid"));
			}
		}
		if (actionName.equalsIgnoreCase(Message.getString("hide_background_point"))||
				actionName.equalsIgnoreCase(Message.getString("show_background_point"))){
			this.theApp.showHideBackgroup();
			if (this.theApp.isShowBackground()){
				((JMenuItem)e.getSource()).setText(Message.getString("hide_background_point"));
				((JMenuItem)e.getSource()).setToolTipText(Comment.getString("hide_background_point"));
			}else{
				((JMenuItem)e.getSource()).setText(Message.getString("show_background_point"));
				((JMenuItem)e.getSource()).setToolTipText(Comment.getString("show_background_point"));
			}
		}
		
		if (actionName.equalsIgnoreCase(Message.getString("variable_standardization"))){
			this.theApp.variableStandardization();
		}
		if (actionName.equalsIgnoreCase(Message.getString("set_nodata_value"))){
			this.theApp.setNODATA();
		}
		
		if (actionName.equalsIgnoreCase(Message.getString("hide_axes"))||
				actionName.equalsIgnoreCase(Message.getString("show_axes"))){
			this.theApp.hideShowAxes();
			if (this.theApp.isShowAxesBranch()){
				((JMenuItem)e.getSource()).setText(Message.getString("hide_axes"));
				((JMenuItem)e.getSource()).setToolTipText(Comment.getString("hide_axes"));
			}else{
				((JMenuItem)e.getSource()).setText(Message.getString("show_axes"));
				((JMenuItem)e.getSource()).setToolTipText(Comment.getString("show_axes"));
			}
		}
		
		if (actionName.equalsIgnoreCase(Message.getString("hide_multiple_selections"))||
				actionName.equalsIgnoreCase(Message.getString("show_multiple_selections"))){
			this.theApp.hideShowmulti_ellipsoidBranch();
			if (this.theApp.isShowmulti_ellipsoidBranch()){
				((JMenuItem)e.getSource()).setText(Message.getString("hide_multiple_selections"));
				((JMenuItem)e.getSource()).setToolTipText(Comment.getString("hide_multiple_selections"));
			}else{
				((JMenuItem)e.getSource()).setText(Message.getString("show_multiple_selections"));
				((JMenuItem)e.getSource()).setToolTipText(Comment.getString("show_multiple_selections"));
			}
		}
		if (actionName.equalsIgnoreCase(Message.getString("set_vs_visibility"))){
			this.theApp.setVSVisibility(getVSList(0));
			handleShowHideMenu();
		}
		if (actionName.contains("VS:")){
//			this.theApp.setVpStatus(getVPStatus());
//			IDMenuItem item = (IDMenuItem) e.getSource();
//			item.setChecked(!item.isChecked());
//			if (item.isChecked()){
//				item.setText("√ " + item.getText());
//			}else{
//				item.setText(item.getText().replace("√ ", ""));
//			}
//			this.theApp.showHideVP(getVPStatus());
		}
		
//		if (actionName.equalsIgnoreCase(Message.getString("ma"))){
//			try {
//				this.theApp.ma();
//			} catch (Exception e1) {
//				e1.printStackTrace();
//			}
//		}
	}
	

	private void handleShowHideMenu() {
//		if (this.theApp.isShowMVE()){
//			showMVE.setText(Message.getString("hide_mve"));
//			showMVE.setToolTipText(Comment.getString("hide_mve"));
//		}else{
//			showMVE.setText(Message.getString("show_mve"));
//			showMVE.setToolTipText(Comment.getString("show_mve"));
//		}
//		if (this.theApp.isShowConvexHull()){
//			showConvexhull.setText(Message.getString("hide_convexhull"));
//			showConvexhull.setToolTipText(Comment.getString("hide_convexhull"));
//		}else{
//			showConvexhull.setText(Message.getString("show_convexhull"));
//			showConvexhull.setToolTipText(Comment.getString("show_convexhull"));
//		}
//		if (this.theApp.isShowPoint()){
//			showPoint.setText(Message.getString("hide_point"));
//			showPoint.setToolTipText(Comment.getString("hide_point"));
//		}else{
//			showPoint.setText(Message.getString("show_point"));
//			showPoint.setToolTipText(Comment.getString("show_point"));
//		}
		
		for (String key : virtualspecies.keySet()){
			IDMenuItem item = virtualspecies.get(key);
			for (int i=0; i< item.getItemCount(); i++){
				JMenuItem submenu = item.getItem(i);
				if (submenu.getText().equals(Message.getString("hide_mve"))||
						submenu.getText().equals(Message.getString("show_mve"))){
					submenu.setEnabled(this.theApp.isShowMVE());
					submenu.setText(this.theApp.getVpStatus().get(item.getKey())[1]
							?Message.getString("hide_mve"):(Message.getString("show_mve")));
				}
				
				if (submenu.getText().equals(Message.getString("hide_point"))||
						submenu.getText().equals(Message.getString("show_point"))){
					submenu.setEnabled(this.theApp.isShowPoint());
					submenu.setText(this.theApp.getVpStatus().get(item.getKey())[0]
							?Message.getString("hide_point"):(Message.getString("show_point")));
				}
				
				if (submenu.getText().equals(Message.getString("hide_convexhull"))||
						submenu.getText().equals(Message.getString("show_convexhull"))){
					submenu.setEnabled(this.theApp.isShowConvexHull());
					submenu.setText(this.theApp.getVpStatus().get(item.getKey())[2]
							?Message.getString("hide_convexhull"):(Message.getString("show_convexhull")));
				}
			}
		}
	}

//	private HashMap<String, boolean[]> getVPStatus(){
//		HashMap<String, boolean[]> vpstatus = new HashMap<String, boolean[]>();
//		for (String key : virtualspecies.keySet()){
//			vpstatus.put(key, virtualspecies.get(key).getSubchecked());
//		}
//		return vpstatus;
//	}
	/**
	 * @param filename
	 */
	public void addVP(String filename, SpeciesDataset vs, boolean isVirtualSpecies, boolean isShowPoint, boolean isShowMVE, boolean isShowConvexhull) {
		File f = new File(filename);
		IDMenuItem item = new IDMenuItem(this, "VS:" + f.getParentFile().getName() + "/" + f.getName(), filename, vs, isVirtualSpecies,
				isShowPoint, isShowMVE, isShowConvexhull);
		if (isVirtualSpecies){
			item.setBackground(Color.GREEN);
		}else{
			
		}
		item.addActionListener(this);
		vsMenu.add(item);
		virtualspecies.put(filename, item);
	}

	public TreeMap<String, IDMenuItem> getVirtualspecies() {
		return virtualspecies;
	}

	/**
	 * @param key
	 */
	public void clearVS(String key) {
		vsMenu.remove(virtualspecies.get(key));
		virtualspecies.remove(key);
		
	}
	//vsType:0 all 1:virtual species 2:results
	private String[] getVSList(int vsType){
		int i = 0;
		if (vsType!=0){
			for (String key : virtualspecies.keySet()){
				if ((vsType==1)&&(virtualspecies.get(key).isVirtualSpecies())){
					i++;
				}
				if ((vsType==2)&&(!virtualspecies.get(key).isVirtualSpecies())){
					i++;
				}
			}
		}else{
			i = virtualspecies.size();
		}
		String[] vplist = new String[i];
		i = 0;
		for (String key : virtualspecies.keySet()){
			if (vsType==0){
				vplist[i] = key;
				i++;
			}else{
				if ((vsType==1)&&(virtualspecies.get(key).isVirtualSpecies())){
					vplist[i] = key;
					i++;
				}
				if ((vsType==2)&&(!virtualspecies.get(key).isVirtualSpecies())){
					vplist[i] = key;
					i++;
				}
			}
			
		}
		return vplist;
	}

	/**
	 * @param target
	 * @return
	 */
	public boolean checkVS(String target) {
		for (String key : virtualspecies.keySet()){
			if (target.equalsIgnoreCase(key)){
				return false;
			}
		}
		return true;
	}
	private JMenu sortedToolMenu() {
		JMenu alltools = new JMenu(Message.getString("alltools"));
		JMenuItem item;
		JMenu BackgroundMenu = new JMenu(Message.getString("background_menu"));
		BackgroundMenu.add(item = new JMenuItem(Message.getString("pca_generator")));
	    item.setToolTipText(Comment.getString("pca_generator"));
	    item.addActionListener(this);

	    
	    BackgroundMenu.add(item = new JMenuItem(Message.getString("draw_background_from_saved_package")));
	    item.setToolTipText(Comment.getString("draw_background_from_saved_package"));
	    item.addActionListener(this);
	    BackgroundMenu.add(item = new JMenuItem(Message.getString("draw_background_from_files")));
	    item.setToolTipText(Comment.getString("draw_background_from_files"));
	    item.addActionListener(this);
	    
	    JMenu niche_simulation = new JMenu(Message.getString("niche_simulation"));
	    niche_simulation.add(item = new JMenuItem(Message.getString("open_species")));
	    item.setToolTipText(Comment.getString("open_species"));
	    item.addActionListener(this);
//	    niche_simulation.add(item = new JMenuItem(Message.getString("show_species_dataset_attributes")));
//	    item.setToolTipText(Comment.getString("show_species_dataset_attributes"));
//	    item.addActionListener(this);
	    niche_simulation.add(item = new JMenuItem(Message.getString("design_barrier")));
	    item.setToolTipText(Comment.getString("design_barrier"));
	    item.addActionListener(this);
//	    niche_simulation.add(item = new JMenuItem(Message.getString("close_species")));
//	    item.setToolTipText(Comment.getString("close_species"));
//	    item.addActionListener(this);
	    niche_simulation.add(item = new JMenuItem(Message.getString("clear_all")));
	    item.setToolTipText(Comment.getString("clear_all"));
	    item.addActionListener(this);
	    
	    niche_simulation.addSeparator();
	    
	    niche_simulation.add(item = new JMenuItem(Message.getString("create_virtual_species")));
	    item.setToolTipText(Comment.getString("create_virtual_species"));
	    item.addActionListener(this);
	    niche_simulation.add(item = new JMenuItem(Message.getString("generate_species_dataset")));
	    item.setToolTipText(Comment.getString("generate_species_dataset"));
	    item.addActionListener(this);
	    niche_simulation.add(item = new JMenuItem(Message.getString("generate_species_dataset_mve")));
	    item.setToolTipText(Comment.getString("generate_species_dataset_mve"));
	    item.addActionListener(this);
	    niche_simulation.add(item = new JMenuItem(Message.getString("voccur2geo")));
	    item.setToolTipText(Comment.getString("voccur2geo"));
	    item.addActionListener(this);
	    
	    niche_simulation.addSeparator();
	    
	    niche_simulation.add(item = new JMenuItem(Message.getString("save_current_selection")));
	    item.setToolTipText(Comment.getString("save_current_selection"));
	    item.addActionListener(this);
	    niche_simulation.add(item = new JMenuItem(Message.getString("open_selection")));
	    item.setToolTipText(Comment.getString("open_selection"));
	    item.addActionListener(this);
	    niche_simulation.add(item = new JMenuItem(Message.getString("open_selections")));
	    item.setToolTipText(Comment.getString("open_selections"));
	    item.addActionListener(this);
	    
	    JMenu niche_analysis_tools  = new JMenu(Message.getString("niche_analysis_tools"));
	    niche_analysis_tools.add(item = new JMenuItem(Message.getString("calculate_overlap")));
	    item.setToolTipText(Comment.getString("calculate_overlap"));
	    item.addActionListener(this);
	    niche_analysis_tools.add(item = new JMenuItem(Message.getString("show_species_dataset_attributes")));
	    item.setToolTipText(Comment.getString("show_species_dataset_attributes"));
	    item.addActionListener(this);
	    
	    
	    JMenu import_export_tools = new JMenu(Message.getString("import_export_tools"));
	    import_export_tools.add(item = new JMenuItem(Message.getString("export_to_enm")));
	    item.setToolTipText(Comment.getString("export_to_enm"));
	    item.addActionListener(this);
	    import_export_tools.add(item = new JMenuItem(Message.getString("close_species")));
	    item.setToolTipText(Comment.getString("close_species"));
	    item.addActionListener(this);
	    
	    JMenu PostENM_analysis = new JMenu(Message.getString("PostENM_analysis"));
	    PostENM_analysis.add(item = new JMenuItem(Message.getString("threshold_variables")));
	    item.setToolTipText(Comment.getString("threshold_variables"));
	    item.addActionListener(this);
	    PostENM_analysis.add(item = new JMenuItem(Message.getString("occurrence_distribution")));
	    item.setToolTipText(Comment.getString("occurrence_distribution"));
	    item.addActionListener(this);
	    PostENM_analysis.add(item = new JMenuItem(Message.getString("setenmrange")));
	    item.setToolTipText(Comment.getString("setenmrange"));
	    item.addActionListener(this);
	    PostENM_analysis.add(item = new JMenuItem(Message.getString("partial_roc")));
	    item.setToolTipText(Comment.getString("partial_roc"));
	    item.addActionListener(this);
	    
	    PostENM_analysis.add(item = new JMenuItem(Message.getString("aic_calculator")));
	    item.setToolTipText(Comment.getString("aic_calculator"));
	    item.addActionListener(this);
	    
	    
	    JMenu Utility_functions = new JMenu(Message.getString("Utility_functions"));
//	    Utility_functions.add(item = new JMenuItem(Message.getString("pca_generator")));
//	    item.setToolTipText(Comment.getString("pca_generator"));
//	    item.addActionListener(this);
	    Utility_functions.add(item = new JMenuItem(Message.getString("raster_convert")));
	    item.setToolTipText(Comment.getString("raster_convert"));
	    item.addActionListener(this);
	    Utility_functions.add(item = new JMenuItem(Message.getString("stat_variables")));
	    item.setToolTipText(Comment.getString("stat_variables"));
	    item.addActionListener(this);
	    Utility_functions.add(item = new JMenuItem(Message.getString("read_data_from_multi_geotiff_files")));
	    item.setToolTipText(Comment.getString("read_data_from_multi_geotiff_files"));
	    item.addActionListener(this);
	    Utility_functions.add(item = new JMenuItem(Message.getString("variable_standardization")));
	    item.setToolTipText(Comment.getString("variable_standardization"));
	    item.addActionListener(this);
	    Utility_functions.add(item = new JMenuItem(Message.getString("set_nodata_value")));
	    item.setToolTipText(Comment.getString("set_nodata_value"));
	    item.addActionListener(this);
	    Utility_functions.add(item = new JMenuItem(Message.getString("convert_text_to_raster")));
	    item.setToolTipText(Comment.getString("convert_text_to_raster"));
	    item.addActionListener(this);
	    
	    
		alltools.add(BackgroundMenu);
		alltools.addSeparator();
		alltools.add(niche_simulation);
		alltools.addSeparator();
		alltools.add(niche_analysis_tools);
		alltools.addSeparator();
		alltools.add(import_export_tools);
		alltools.addSeparator();
		alltools.add(PostENM_analysis);
		alltools.addSeparator();
		alltools.add(Utility_functions);
		
		return alltools;
	}
}
