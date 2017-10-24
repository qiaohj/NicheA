/******************************************************************************
 * Huijie Qiao
 *
 * Project:  Niche Analyst
 * Purpose:  
 * Created date: May 29, 2012 11:31:44 AM
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

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

import javax.media.j3d.Background;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Switch;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.VirtualUniverse;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSlider;
import javax.swing.JToolBar;
import javax.swing.ProgressMonitor;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3f;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.gdal.gdal.gdal;
import org.gdal.gdalconst.gdalconst;
import org.jdom.JDOMException;
import org.ku.nicheanalyst.algorithms.MinimumVolumeEllipsoid;
import org.ku.nicheanalyst.algorithms.MinimumVolumeEllipsoidResult;
import org.ku.nicheanalyst.common.Comment;
import org.ku.nicheanalyst.common.CommonFun;
import org.ku.nicheanalyst.common.ConfigInfo;
import org.ku.nicheanalyst.common.Const;
import org.ku.nicheanalyst.common.Message;
import org.ku.nicheanalyst.dataset.DrawingObject;
import org.ku.nicheanalyst.dataset.GSpaceData;
import org.ku.nicheanalyst.dataset.OverlapObject;
import org.ku.nicheanalyst.dataset.SpeciesData;
import org.ku.nicheanalyst.dataset.SpeciesDataset;
import org.ku.nicheanalyst.enms.ma.MarbleAlgorithm;
import org.ku.nicheanalyst.exceptions.IllegalSelectionException;
import org.ku.nicheanalyst.maps.controllers.GeoTiffController;
import org.ku.nicheanalyst.maps.objects.GDALInfoObject;
import org.ku.nicheanalyst.maps.objects.GeoTiffObject;
import org.ku.nicheanalyst.ui.display.component.j3d.Axes;
import org.ku.nicheanalyst.ui.display.component.j3d.AxesLabel;
import org.ku.nicheanalyst.ui.display.component.j3d.Color3D;
import org.ku.nicheanalyst.ui.display.component.j3d.ConvesHullFace;
import org.ku.nicheanalyst.ui.display.component.j3d.Dot;
import org.ku.nicheanalyst.ui.display.component.j3d.Ellipsoid;
import org.ku.nicheanalyst.ui.display.component.j3d.EllipsoidGroup;
import org.ku.nicheanalyst.ui.display.component.j3d.Grids;
import org.ku.nicheanalyst.ui.display.component.ui.IDMenuItem;
import org.ku.nicheanalyst.ui.display.component.ui.JMySlider;
import org.ku.nicheanalyst.ui.display.dialog.AICCalculatorForm;
import org.ku.nicheanalyst.ui.display.dialog.BackgroundLoadVariableForm;
import org.ku.nicheanalyst.ui.display.dialog.ConfigForm;
import org.ku.nicheanalyst.ui.display.dialog.CreateSDSForm;
import org.ku.nicheanalyst.ui.display.dialog.ExportForm;
import org.ku.nicheanalyst.ui.display.dialog.FormatConvertorForm;
import org.ku.nicheanalyst.ui.display.dialog.GenerateSDSForm;
import org.ku.nicheanalyst.ui.display.dialog.HTMLViewer;
import org.ku.nicheanalyst.ui.display.dialog.LL2RasterForm;
import org.ku.nicheanalyst.ui.display.dialog.ManageWorkflowDialog;
import org.ku.nicheanalyst.ui.display.dialog.ModelThresholdForm;
import org.ku.nicheanalyst.ui.display.dialog.MultiGeoLayerReaderForm;
import org.ku.nicheanalyst.ui.display.dialog.MultiVarMappingForm;
import org.ku.nicheanalyst.ui.display.dialog.NicheBreadthForm;
import org.ku.nicheanalyst.ui.display.dialog.OccurrenceDistributionForm;
import org.ku.nicheanalyst.ui.display.dialog.OccurrenceMappingForm;
import org.ku.nicheanalyst.ui.display.dialog.OpenSDSForm;
import org.ku.nicheanalyst.ui.display.dialog.OverlapSelectionForm;
import org.ku.nicheanalyst.ui.display.dialog.PCAGeneratorForm;
import org.ku.nicheanalyst.ui.display.dialog.PartialROCForm;
import org.ku.nicheanalyst.ui.display.dialog.SDSAttributeForm;
import org.ku.nicheanalyst.ui.display.dialog.SDSListForm;
import org.ku.nicheanalyst.ui.display.dialog.SetENMResultRangeForm;
import org.ku.nicheanalyst.ui.display.dialog.SetNodataGeoLayerForm;
import org.ku.nicheanalyst.ui.display.dialog.TextViewerWindow;
import org.ku.nicheanalyst.ui.display.dialog.ThresholdCalculateForm;
import org.ku.nicheanalyst.ui.display.dialog.VSVisibilityForm;
import org.ku.nicheanalyst.ui.display.dialog.VariableGeneratorForm;
import org.ku.nicheanalyst.ui.display.dialog.VariableStandardizationForm;
import org.ku.nicheanalyst.ui.display.dialog.VariablesStatForm;
import org.ku.nicheanalyst.ui.display.worker.AICCalculatorGenerator;
import org.ku.nicheanalyst.ui.display.worker.ENMRangeGenerator;
import org.ku.nicheanalyst.ui.display.worker.EllipsoidOverlapCalculator;
import org.ku.nicheanalyst.ui.display.worker.ExportToENMWorker;
import org.ku.nicheanalyst.ui.display.worker.FormatConvertor;
import org.ku.nicheanalyst.ui.display.worker.ModelThresholdGenerator;
import org.ku.nicheanalyst.ui.display.worker.MultiGeoLayerReaderProcessor;
import org.ku.nicheanalyst.ui.display.worker.MultiVarNormalDistributionGenerator;
import org.ku.nicheanalyst.ui.display.worker.NicheBreadthProcessor;
import org.ku.nicheanalyst.ui.display.worker.NicheMapGenerator;
import org.ku.nicheanalyst.ui.display.worker.OccurrenceDistributionStator;
import org.ku.nicheanalyst.ui.display.worker.OccurrenceMapper;
import org.ku.nicheanalyst.ui.display.worker.PCAGenerator;
import org.ku.nicheanalyst.ui.display.worker.PartialROCGenerator;
import org.ku.nicheanalyst.ui.display.worker.PointGenerator;
import org.ku.nicheanalyst.ui.display.worker.SetNodataGeoLayerProcessor;
import org.ku.nicheanalyst.ui.display.worker.SpeciesGenerator;
import org.ku.nicheanalyst.ui.display.worker.VariableGenerator;
import org.ku.nicheanalyst.ui.display.worker.VariableStandardizationProcessor;
import org.ku.nicheanalyst.ui.display.worker.VariablesStator;
import org.ku.nicheanalyst.ui.display.worker.VirtualSpeciesGenerator;
import org.ku.nicheanalyst.ui.filefilters.EllipsoidFileFilter;
import org.ku.nicheanalyst.ui.filefilters.RasterFileFilter;
import org.lobobrowser.util.Urls;
import org.xml.sax.SAXException;

import Jama.EigenvalueDecomposition;
import Jama.Matrix;

import com.sun.j3d.utils.behaviors.mouse.MouseRotate;
import com.sun.j3d.utils.behaviors.mouse.MouseTranslate;
import com.sun.j3d.utils.behaviors.mouse.MouseZoom;
import com.sun.j3d.utils.universe.SimpleUniverse;

/**
 * @author Huijie Qiao
 *
 */
public class Displayer extends JFrame implements ChangeListener, PropertyChangeListener, ActionListener{
	private static final long serialVersionUID = -8190614691619704338L;
	private BranchGroup objRoot;
	private BranchGroup backgroundBG_White;
	private BranchGroup backgroundBG_Black;
	private boolean axesOn;
	private Switch groupAxes;
	private int controlMode;
	private JLabel statusbar;
	private boolean showConvexHull;
	private boolean showMVE;
	private boolean showPoint;
	private boolean showOverlap;
	private boolean showGrid;
	private boolean showBackground;
	private boolean showSelection;
	private boolean showmulti_ellipsoidBranch;
	private boolean showAxes;
	private Canvas3D canvas3D;
	private BranchGroup scene;
	private TransformGroup transformGroup_axis;
	private BoundingSphere worldBounds;
	private HashMap<String, BranchGroup> pointBranches;
	private HashMap<String, BranchGroup> convexHullBranches;
	private HashMap<String, BranchGroup> mveBranches;
	private HashMap<String, GSpaceData> gSpaceDatasets;
	private HashMap<String, boolean[]> vpStatus;
	private HashMap<String, MinimumVolumeEllipsoidResult> vpMVE;
	private BranchGroup overlapBranch;
	private BranchGroup gridBranch;
	private BranchGroup backgroupdBranch;
	private BranchGroup ellipsoidBranch;
	private BranchGroup multi_ellipsoidBranch;
	private BranchGroup maBranch;
	private BranchGroup axisBranch;
	private Menu theMenu;
	private float maxLength;
	private float step;
	private int scale;
	private int maxpoints;
	private HashMap<Integer, JCheckBox> lockers;
	private HashMap<Integer, JMySlider> sliders;
	private HashMap<String, SpeciesData> backgroundValues;
	private EllipsoidGroup ellipsoidGroup;
	private ProgressMonitor progressMonitor;
	private PointGenerator pointGenerator;
	private VirtualSpeciesGenerator virtualSpeciesGenerator;
	private String lastFolder;
	private String backgroundTiff;
	private Background backgroundWhite;
	private Background backgroundBlack;
	private Color3f backgroundColor;
	private StringBuilder sb_log;
	private boolean isFullBackground = false;
	private double[][][] maxmin;
	private MouseRotate mRotate;
	private MouseTranslate mTrans;
	private boolean is3D;
	private ArrayList<DrawingObject> drawQueue;

	public Displayer() {
		sb_log = new StringBuilder();
		enableEvents(AWTEvent.WINDOW_EVENT_MASK);
		addWindowListener(new WindowAdapter() {
		@Override
		public void windowClosing(WindowEvent we)
		{ 
			exit();
		}
		});
		try {
//			debug();
			init();
		} catch (Exception e) {
			e.printStackTrace();
			ShowAlert(e.getMessage());
			
		}
		
		
	}
	private void debug() {
		if (true){
			this.is3D = true;
			String filename = "E:/Dropbox/Papers/NicheA/samples/bio_10m_tiff/pca/background";
			backgroundTiff = filename + "/present.tiff";
			draw(new String[]{filename}, true, false, 1d, null, null, null, false, null, null);
		}
		
	}
	public boolean isFullBackground(){
		return isFullBackground;
	}
	public double[][][] getMaxmin(){
		return maxmin;
	}
	public void addLog(String message){
		if (sb_log.length()>1000){
			sb_log.delete(0, 1);
		}
		sb_log.append(message + Const.LineBreak);
	}
	public void drawBackgroupFromFiles() throws IOException{
		if (this.backgroupdBranch!=null){
			ShowAlert(Message.getString("one_scenario_alert"));
			return;
		}
		BackgroundLoadVariableForm loader = new BackgroundLoadVariableForm(this);
		HashMap<String, String> selectedFiles = loader.showDialog();
		if (selectedFiles!=null){
			this.is3D = loader.is3D();
			drawBackgroundWithTiffFiles(selectedFiles);
			backgroundTiff = selectedFiles.get("x");
		}
		
	}
	//use a folder to draw bg
	public void drawBackgroup() throws IOException{
		if (this.backgroupdBranch!=null){
			ShowAlert(Message.getString("one_scenario_alert"));
			return;
		}
		final JFileChooser fc = new JFileChooser();
		fc.setDialogTitle(Message.getString("draw_background"));
		fc.setSelectedFile(new File(lastFolder));
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
//		fc.setAcceptAllFileFilterUsed(false);
		//In response to a button click:
		int returnVal = fc.showOpenDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			this.is3D = true;
			File file = fc.getSelectedFile();
	        String filename = file.getAbsolutePath();
	        setLastFolder(filename);
	        
	        backgroundTiff = filename + "/present.tiff";
	        
			draw(new String[]{filename}, true, false, 1d, null, null, null, false, null, null);
		}
		
	}
	public void ShowAlert(String message){
		JOptionPane.showMessageDialog(this, message,
                Message.getString("application.title"), JOptionPane.PLAIN_MESSAGE);
	}
	private void drawGrids(){
		gridBranch = new BranchGroup();
		gridBranch.setCapability(BranchGroup.ALLOW_DETACH);
		Grids grids = new Grids(1f, 5);
		gridBranch.addChild(grids);
		this.transformGroup_axis.addChild(gridBranch);
		this.showHideGrid();
	}
	public void initConsole() {
		java.io.PrintStream oldOut = System.out;
		ConsoleModel standard = ConsoleModel.getStandard();
		java.io.PrintStream ps = standard.getPrintStream();
		System.setOut(ps);
		System.setErr(ps);
//		if (this.isCodeLocationDirectory()) {
//			// Should only be shown when running from Eclipse.
//			oldOut.println("WARNING: initConsole(): Switching standard output and standard error to application console. If running EntryPoint, pass -debug to avoid this.");
//		}
	}
	public boolean isCodeLocationDirectory() {
		URL codeLocation = this.getClass().getProtectionDomain()
				.getCodeSource().getLocation();
		return Urls.isLocalFile(codeLocation)
				&& codeLocation.getPath().endsWith("/");
	}
	private void init() throws Exception {
		System.out.println("Current version v" + Message.getString("version") + " " + Message.getString("subversion"));

		boolean isPass = checkEnvironments();
		while (!isPass){
			isPass = showConfig();
		}
		if (!CommonFun.isLinux()){
			//initConsole();
		}
		
		backgroundColor = Color3D.black;
		lastFolder = ConfigInfo.getInstance().getLastFolder();
		ellipsoidGroup = null;
		backgroundValues = null;
		initMaxLength(10f);
		
		maxpoints = ConfigInfo.getInstance().getMaxPoint();
		pointBranches = new HashMap<String, BranchGroup>();
		convexHullBranches = new HashMap<String, BranchGroup>();
		mveBranches = new HashMap<String, BranchGroup>();
		gSpaceDatasets = new HashMap<String, GSpaceData>();
		vpStatus = new HashMap<String, boolean[]>();
		vpMVE = new HashMap<String, MinimumVolumeEllipsoidResult>();
		axesOn = true;
		showMVE = true;
		showPoint = true;
		showConvexHull = true;
		showOverlap = true;
		showGrid = true;
		showBackground = true;
		showSelection = true;
		showmulti_ellipsoidBranch = true;
		showAxes = true;
		getContentPane().setLayout(new BorderLayout());
	    setTitle(Message.getString("application.title"));
	    
	    
	    getContentPane().setBackground(Color.black);
	    getContentPane().setLayout(new BorderLayout());

	    
	    
	    JToolBar vertical = new JToolBar(JToolBar.VERTICAL);
        vertical.setFloatable(true);
        vertical.setMargin(new Insets(10, 5, 5, 5));
        JPanel verPanel = new JPanel();
        BoxLayout verlayout = new BoxLayout(verPanel, BoxLayout.Y_AXIS);
        verPanel.setLayout(verlayout);
        verPanel.setAlignmentY(CENTER_ALIGNMENT);
		
        vertical.add(verPanel);
        add(vertical, BorderLayout.WEST);
        
        statusbar = new JLabel("");
	    statusbar.setForeground(Color.white);
        statusbar.setPreferredSize(new Dimension(-1, 22));
        statusbar.setBorder(LineBorder.createGrayLineBorder());
        add(statusbar, BorderLayout.SOUTH);
        
	    JPopupMenu.setDefaultLightWeightPopupEnabled(false);
	    theMenu = new Menu(this);
	    setJMenuBar(theMenu);
	    setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	    
	    GraphicsConfiguration config=SimpleUniverse.getPreferredConfiguration();
	    
		canvas3D = new Canvas3D(config);
		
        add(BorderLayout.CENTER, canvas3D);
        
		scene = createSceneGraph(1f);
		
		int scalex = scale;
		
		sliders = new HashMap<Integer, JMySlider>();
        JMySlider slider = new JMySlider(String.format("%s %s:", Message.getString("x") , Message.getString("offset")), 
        		JSlider.HORIZONTAL, (scalex * -1), scalex, 0, true, this);
        slider.setToolTipText(String.format(Comment.getString("offset"), Comment.getString("x")));
        
		verPanel.add(slider);
		sliders.put(1, slider);
		
        slider = new JMySlider(String.format("%s %s:", Message.getString("y") , Message.getString("offset")), 
        		JSlider.HORIZONTAL, (scalex * -1), scalex, 0, true, this);
        slider.setToolTipText(String.format(Comment.getString("offset"), Comment.getString("y")));
		verPanel.add(slider);
		sliders.put(2, slider);
		
        slider = new JMySlider(String.format("%s %s:", Message.getString("z") , Message.getString("offset")), 
        		JSlider.HORIZONTAL, (scalex * -1), scalex, 0, true, this);
        slider.setToolTipText(String.format(Comment.getString("offset"), Comment.getString("z")));
		verPanel.add(slider);
		sliders.put(3, slider);
		
        slider = new JMySlider(String.format("%s %s:", Message.getString("x") , Message.getString("zoom")), 
        		JSlider.HORIZONTAL, 0, scalex, 0, true, this);
        slider.setToolTipText(String.format(Comment.getString("zoom"), Comment.getString("x")));
		verPanel.add(slider);
		sliders.put(4, slider);
		
        slider = new JMySlider(String.format("%s %s:", Message.getString("y") , Message.getString("zoom")), 
        		JSlider.HORIZONTAL, 0, scalex, 0, true, this);
        slider.setToolTipText(String.format(Comment.getString("zoom"), Comment.getString("y")));
		verPanel.add(slider);
		sliders.put(5, slider);
		
        slider = new JMySlider(String.format("%s %s:", Message.getString("z") , Message.getString("zoom")), 
        		JSlider.HORIZONTAL, 0, scalex, 0, true, this);
        slider.setToolTipText(String.format(Comment.getString("zoom"), Comment.getString("z")));
		verPanel.add(slider);
		sliders.put(6, slider);
		
        slider = new JMySlider(String.format("%s %s:", Message.getString("x") , Message.getString("rotate")), 
        		JSlider.HORIZONTAL, -180, 180, 0, false, this);
        slider.setToolTipText(String.format(Comment.getString("rotate"), Comment.getString("x")));
		verPanel.add(slider);
		sliders.put(7, slider);
		
        slider = new JMySlider(String.format("%s %s:", Message.getString("y") , Message.getString("rotate")), 
        		JSlider.HORIZONTAL, -180, 180, 0, false, this);
        slider.setToolTipText(String.format(Comment.getString("rotate"), Comment.getString("y")));
		verPanel.add(slider);
		sliders.put(8, slider);
		
        slider = new JMySlider(String.format("%s %s:", Message.getString("z") , Message.getString("rotate")), 
        		JSlider.HORIZONTAL, -180, 180, 0, false, this);
        slider.setToolTipText(String.format(Comment.getString("rotate"), Comment.getString("z")));
		verPanel.add(slider);
		sliders.put(9, slider);
		lockers = new HashMap<Integer, JCheckBox>();
		
		JPanel slider_panel_all = new JPanel();
		slider_panel_all.setLayout(new BoxLayout(slider_panel_all, BoxLayout.X_AXIS));
		JPanel slider_panel_left = new JPanel();
		slider_panel_left.setLayout(new BoxLayout(slider_panel_left, BoxLayout.Y_AXIS));
		JPanel slider_panel_right = new JPanel();
		slider_panel_right.setLayout(new BoxLayout(slider_panel_right, BoxLayout.Y_AXIS));
		slider_panel_all.add(slider_panel_left);
		slider_panel_all.add(slider_panel_right);
		
		JCheckBox checkbox = new JCheckBox(String.format("%s %s", Message.getString("lock") , Message.getString("x")));
		checkbox.setToolTipText(String.format(Comment.getString("lock"), Comment.getString("x")));
		checkbox.addChangeListener(this);
		slider_panel_left.add(checkbox);
		lockers.put(0, checkbox);
		
		checkbox = new JCheckBox(String.format("%s %s", Message.getString("lock") , Message.getString("y")));
		checkbox.setToolTipText(String.format(Comment.getString("lock"), Comment.getString("y")));
		checkbox.addChangeListener(this);
		slider_panel_left.add(checkbox);
		lockers.put(1, checkbox);
		
		checkbox = new JCheckBox(String.format("%s %s", Message.getString("lock") , Message.getString("z")));
		checkbox.setToolTipText(String.format(Comment.getString("lock"), Comment.getString("z")));
		
		checkbox.addChangeListener(this);
		slider_panel_left.add(checkbox);
		lockers.put(2, checkbox);
		
		checkbox = new JCheckBox(String.format("%s %s", Message.getString("lock") , Message.getString("offset")));
		checkbox.addChangeListener(this);
		slider_panel_right.add(checkbox);
		lockers.put(3, checkbox);
		
		checkbox = new JCheckBox(String.format("%s %s", Message.getString("lock") , Message.getString("zoom")));
		checkbox.addChangeListener(this);
		slider_panel_right.add(checkbox);
		lockers.put(4, checkbox);
		
		checkbox = new JCheckBox(String.format("%s %s", Message.getString("lock") , Message.getString("rotate")));
		checkbox.addChangeListener(this);
		slider_panel_right.add(checkbox);
		lockers.put(5, checkbox);
		verPanel.add(slider_panel_all);
        vertical.add(verPanel);
        add(vertical, BorderLayout.WEST);
        
        SimpleUniverse simpleU = new SimpleUniverse(canvas3D);
	    simpleU.getViewingPlatform().setNominalViewingTransform();
	    simpleU.addBranchGraph(scene);
	    
//	    Toolkit theKit = Toolkit.getDefaultToolkit();
	    setExtendedState(JFrame.MAXIMIZED_BOTH); 
//	    setVisible(true);
	    
//	    Dimension windowSize = theKit.getScreenSize();
//	    setSize(windowSize);
//	    
	    
	    
//	    setBounds(0, 0, windowSize.width, windowSize.height);
	    
	    
	}
	private boolean checkEnvironments() {
		UIManager.put("OptionPane.yesButtonText", Message.getString("Yes"));
    	UIManager.put("OptionPane.noButtonText", Message.getString("No"));
    	UIManager.put("OptionPane.okButtonText", Message.getString("Ok"));
    	UIManager.put("OptionPane.cancelButtonText", Message.getString("Cancel"));
    	UIManager.put("OptionPane.saveButtonText", Message.getString("Save"));
    	UIManager.put("OptionPane.openButtonText", Message.getString("Open"));
    	UIManager.put("FileChooser.acceptAllFileFilterText", Message.getString("all_files"));
    	JComponent.setDefaultLocale(Locale.ENGLISH);
    	
		System.setProperty("java.ext.dirs", ".");
		System.setProperty("java.class.path", ".");
		
		StringBuilder sb = new StringBuilder();
		System.out.println("Operating system is " + System.getProperty("os.name"));
		System.out.println("Operating system local is " + Locale.getDefault());
		if (Locale.getDefault()!=Locale.ENGLISH){
			System.out.println("Set Operating system local to " + Locale.ENGLISH);
			Locale.setDefault(Locale.ENGLISH);
		}
		System.out.println("Java version is " + System.getProperty("java.version"));
		String data_mode = System.getProperty("sun.arch.data.model");
		System.out.println("Java data mode is " + data_mode);
		if (!CommonFun.isInteger(data_mode)){
			ShowAlert(Message.getString("data_mode_error") + System.getProperty("java.version") + " " + data_mode + "bit");
			System.exit(1);
		}
		if (Integer.valueOf(data_mode)!=64){
			ShowAlert(Message.getString("data_mode_error") + System.getProperty("java.version") + " " + data_mode + "bit");
			System.exit(1);
		}
		System.out.println("Java class path is " + System.getProperty("java.class.path"));
		System.setProperty("java.library.path", ".libs:" + System.getProperty("java.library.path"));
		System.out.println("Java library path is " + System.getProperty("java.library.path"));
		System.out.println("Java ext path is " + System.getProperty("java.ext.dirs"));
		
		System.loadLibrary("gdaljni");
        gdal.AllRegister();
        final String versionInfo = gdal.VersionInfo("RELEASE_NAME");
        final String build_info = gdal.VersionInfo("BUILD_INFO");
        final String LICENSE = gdal.VersionInfo("LICENSE");
        
        if (versionInfo != null && versionInfo.trim().length() > 0) {
            System.out.println("GDAL Native Library loaded.");
            System.out.println("GDAL version: " + versionInfo);
        }else{
        	sb.append("Failed to load the GDAL native libs." + Const.LineBreak);
        }

		Class klass = VirtualUniverse.class;
		URL location = klass.getResource('/'+klass.getName().replace('.', '/')+".class");
		System.out.println(location.toString());
		try{
			VirtualUniverse vu = new VirtualUniverse();
			
			if (vu == null) {
				sb.append("Failed to get Java3D. Please download and install Java 3D via https://java3d.java.net/" + Const.LineBreak);
			} else {
				Map vuMap = vu.getProperties();
				System.out.println("Java3D version : " + vuMap.get("j3d.version"));
				System.out.println("Java3D vendor : " + vuMap.get("j3d.vendor"));
				System.out
						.println("Java3D renderer: " + vuMap.get("j3d.renderer"));
			}
		}catch(Throwable e){
			ShowAlert(Message.getString("error_j3d") + location.toString());
			System.exit(1);
		}
		boolean check_exe = true;
		if (check_exe){
			File gdal_translate = new File(ConfigInfo.getInstance().getGdalTranslate());
			boolean is_pass = true;
			if (gdal_translate.exists()&&gdal_translate.getName().toLowerCase().replace(".exe", "").endsWith("gdal_translate")){
				System.out.println("Find gdal_translate at " + gdal_translate.getAbsolutePath());
				ConfigInfo.getInstance().setProperty("gdal_translate", gdal_translate.getAbsolutePath());
			}else{
				sb.append("Failed to get gdal_translate at " + gdal_translate.getAbsolutePath() + Const.LineBreak);
				is_pass = false;
			}
			
			File gdalwarp = new File(ConfigInfo.getInstance().getGdalWarp());
			if (gdalwarp.exists()&&gdalwarp.getName().toLowerCase().replace(".exe", "").endsWith("gdalwarp")){
				System.out.println("Find gdalwarp at " + gdalwarp.getAbsolutePath());
				ConfigInfo.getInstance().setProperty("gdalwarp", gdalwarp.getAbsolutePath());
			}else{
				sb.append("Failed to get gdalwarp at " + gdalwarp.getAbsolutePath() + Const.LineBreak);
				is_pass = false;
			}
			
			File imagemagick = new File(ConfigInfo.getInstance().getImageMagick());
			if (imagemagick.exists()&&imagemagick.getName().toLowerCase().replace(".exe", "").endsWith("convert")){
				System.out.println("Find ImageMagick at " + imagemagick.getAbsolutePath());
				ConfigInfo.getInstance().setProperty("convert", imagemagick.getAbsolutePath());
			}else{
				sb.append("Failed to get ImageMagick convert command at " + imagemagick.getAbsolutePath() + Const.LineBreak);
				is_pass = false;
			}
			
			File rscript = new File(ConfigInfo.getInstance().getRScript());
			if (rscript.exists()&&rscript.getName().toLowerCase().replace(".exe", "").endsWith("rscript")){
				System.out.println("Find RScript at " + rscript.getAbsolutePath());
				ConfigInfo.getInstance().setProperty("rscript", rscript.getAbsolutePath());
			}else{
				sb.append("Failed to get RScript command at " + rscript.getAbsolutePath() + Const.LineBreak);
				is_pass = false;
			}
			
			if (!is_pass){
				sb.append("Please tell NicheA where it can find the commonds above." + Const.LineBreak);
			}
		}
		if (sb.toString().length()==0){
			return true;
		}else{
			Toolkit theKit = Toolkit.getDefaultToolkit();
		    
		    Dimension windowSize = theKit.getScreenSize();
		    setSize(windowSize);
			ShowAlert(sb.toString());
			return false;
		}
	}
	/**
	 * @param f
	 */
	private void initMaxLength(float f) {
		scale = 100;
		maxLength = f;
		step = maxLength/(float)scale;
		
	}
	public void getOccurrenceDistribution(){
		OccurrenceDistributionForm form = new OccurrenceDistributionForm(this);
		form.setVisible(true);
		if (form.getTarget()==null){
			return;
		}
		File[] selectedFiles = form.getSelectFiles();
		String target = form.getTarget();
		int steps = form.getSteps();
		String occurrenceFile = form.getOccurrenceFile();
		String enmFile = form.getEnmFile();
		this.progressMonitor = new ProgressMonitor(this, "", "", 0, 100);
		OccurrenceDistributionStator occurrenceDistributionStator = new OccurrenceDistributionStator(
				this, selectedFiles, target, steps, occurrenceFile, enmFile, form.isIsnodata(), form.getNodata(), form.getThreshold());
    	occurrenceDistributionStator.addPropertyChangeListener(this);
    	occurrenceDistributionStator.execute();
	}
	public void showLog(){
		//LogForm dialog = new LogForm(Message.getString("application.title"), this.sb_log, this);
		
		TextViewerWindow window = new TextViewerWindow(Message.getString("application.title"));
		window.setScrollsOnAppends(true);
		window.setSwingDocument(ConsoleModel.getStandard());
		window.setSize(new Dimension(600, 400));
		window.setLocationByPlatform(true);
		window.setVisible(true);
	}
	
	public void CreateDistributionByPoints(){
		OccurrenceMappingForm form = new OccurrenceMappingForm(this);
		
		form.setVisible(true);
		if (form.getResult()==null){
			return;
		}
		if (form.getResult().trim().equals("")){
			return;
		}
		ArrayList<Double> occurrences = form.getOccurrence();
		String result = form.getResult();
		double precision = form.getPrecision();
		String tiff = form.getTiff();
		this.progressMonitor = new ProgressMonitor(this, "", "", 0, 100);
		OccurrenceMapper OccurrenceMapper = new OccurrenceMapper(
				this, tiff, precision, occurrences, result);
		OccurrenceMapper.addPropertyChangeListener(this);
		OccurrenceMapper.execute();
	}
	
	public void createVirtualSpecies(String[] vplist){
		if (backgroundValues==null){
			ShowAlert(Message.getString("nobackground_alert"));
			return;
		}
		CreateSDSForm form = new CreateSDSForm(this, vplist);
		form.setVisible(true);
		
		if (form.getTarget()==null){
			return;
		}
		
		if ((form.getSelection()==0)&&(ellipsoidGroup==null)){
			ShowAlert(Message.getString("noellipsoid_alert"));
			return;
		}
		String target = form.getTarget();
    	this.progressMonitor = new ProgressMonitor(this, "", "", 0, 100);
    	HashMap<String, SpeciesData> point_pool = backgroundValues;
    	if (!form.getPointPoor().equals(Message.getString("background_cloud"))){
    		point_pool = theMenu.getVirtualspecies().get(form.getPointPoor()).getVs().getVs();
    	}
    	SpeciesDataset vs_use = null;
    	if (form.getSelection()!=0){
    		vs_use = theMenu.getVirtualspecies().get(form.getVsSelection()).getVs();
    	}
    	this.virtualSpeciesGenerator = new VirtualSpeciesGenerator(point_pool, 
    			target, this.ellipsoidGroup, maxLength, backgroundTiff, 
    			vs_use, form.getSelection(), this.is3D, form.getINOUT());
    	this.virtualSpeciesGenerator.addPropertyChangeListener(this);
    	this.virtualSpeciesGenerator.execute();
	}
	public void changeBackground(){
		//unfinished
		if (backgroundColor.equals(Color3D.black)){
			backgroundColor = Color3D.white;
			objRoot.removeChild(backgroundBG_Black);
			objRoot.addChild(backgroundBG_White);
		}else{
			objRoot.removeChild(backgroundBG_White);
			objRoot.addChild(backgroundBG_Black);
			backgroundColor = Color3D.black;
		}
		
	}
	private BranchGroup createSceneGraph(float size) throws IOException{
		// Create the root of the branch graph
		objRoot = new BranchGroup();
		objRoot.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
		objRoot.setCapability(BranchGroup.ALLOW_CHILDREN_READ);
		objRoot.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
		objRoot.setCapability(BranchGroup.ALLOW_DETACH);
		
		
		
		backgroundBG_White = new BranchGroup();
		backgroundBG_White.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
		backgroundBG_White.setCapability(BranchGroup.ALLOW_CHILDREN_READ);
		backgroundBG_White.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
		backgroundBG_White.setCapability(BranchGroup.ALLOW_DETACH);
		
		backgroundBG_Black = new BranchGroup();
		backgroundBG_Black.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
		backgroundBG_Black.setCapability(BranchGroup.ALLOW_CHILDREN_READ);
		backgroundBG_Black.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
		backgroundBG_Black.setCapability(BranchGroup.ALLOW_DETACH);
		
		
		worldBounds = new BoundingSphere(new Point3d( 0.0, 0.0, 0.0 ), size);
		
		backgroundWhite = new Background(Color3D.white);
		backgroundWhite.setCapability(Background.ALLOW_COLOR_WRITE);
		backgroundWhite.setApplicationBounds(worldBounds);
		backgroundBG_White.addChild(backgroundWhite);
//		objRoot.addChild(backgroundBG_White);
		
		backgroundBlack = new Background(Color3D.black);
		backgroundBlack.setCapability(Background.ALLOW_COLOR_WRITE);
		backgroundBlack.setApplicationBounds(worldBounds);
		backgroundBG_Black.addChild(backgroundBlack);
		objRoot.addChild(backgroundBG_Black);
		
		Transform3D transform3D_axis = new Transform3D();
        transform3D_axis.setTranslation(new Vector3f());
		transformGroup_axis = new TransformGroup(transform3D_axis);
		transformGroup_axis.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
    	transformGroup_axis.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
    	transformGroup_axis.setCapability(TransformGroup.ALLOW_CHILDREN_EXTEND);
    	transformGroup_axis.setCapability(TransformGroup.ALLOW_CHILDREN_READ);
    	transformGroup_axis.setCapability(TransformGroup.ALLOW_CHILDREN_WRITE);
    	transformGroup_axis.setCapability(TransformGroup.ALLOW_BOUNDS_READ);
    	transformGroup_axis.setCapability(TransformGroup.ALLOW_BOUNDS_WRITE);
    	
		objRoot.addChild(transformGroup_axis);
		this.backgroupdBranch = null;
		drawGrids();
		
		createAxis(size);
		
		mRotate = new MouseRotate();
		mRotate.setTransformGroup(transformGroup_axis);
		mRotate.setSchedulingBounds(new BoundingSphere());
		
        objRoot.addChild(mRotate);
        
        MouseZoom mZoom = new MouseZoom();
        mZoom.setTransformGroup(transformGroup_axis);
        mZoom.setSchedulingBounds(new BoundingSphere());
        objRoot.addChild(mZoom);
        
        mTrans = new MouseTranslate();
        mTrans.setTransformGroup(transformGroup_axis);
        mTrans.setSchedulingBounds(new BoundingSphere());
        objRoot.addChild(mTrans);
        // Let Java 3D perform optimizations on this scene graph.
        objRoot.compile();
        return objRoot;
	} // end of createSceneGraph method
	/**
	 * 
	 */
	private void createAxis(float size) {
		Color3f axis_color = Color3D.red;
		
		if (groupAxes!=null){
			transformGroup_axis.removeChild(groupAxes);
		}
		groupAxes = new Switch();
		groupAxes.setCapability(Switch.ALLOW_SWITCH_WRITE);
		axisBranch = new BranchGroup();
		axisBranch.setCapability(BranchGroup.ALLOW_DETACH);
		axisBranch.addChild(groupAxes);
		transformGroup_axis.addChild(axisBranch);

		Axes xAxis = new Axes(size, Message.getString("x"), axis_color);
		groupAxes.addChild(xAxis);
		Axes yAxis = new Axes(size, Message.getString("y"), axis_color);
		groupAxes.addChild(yAxis);
		Axes zAxis = new Axes(size, Message.getString("z"), axis_color);
		groupAxes.addChild(zAxis);
		groupAxes.addChild(new AxesLabel(Message.getString("x"), size, axis_color));
		groupAxes.addChild(new AxesLabel(Message.getString("y"), size, axis_color));
		groupAxes.addChild(new AxesLabel(Message.getString("z"), size, axis_color));
		groupAxes.setWhichChild(Switch.CHILD_MASK);
		groupAxes.setChildMask(getAxesMask());
		
	}
	public BitSet getAxesMask(){
		BitSet mask = new BitSet(6);
		if (axesOn){
			mask.set(0);
			mask.set(1);
			mask.set(2);
			mask.set(3);
			mask.set(4);
			mask.set(5);
		}
		return(mask);
	}
	public void exit() {
		String ObjButtons[] = { "Yes", "No" };
		int PromptResult = JOptionPane.showOptionDialog(null,
				Message.getString("close_confirm"),
				Message.getString("application.title"),
				JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null,
				ObjButtons, ObjButtons[1]);
		if (PromptResult == JOptionPane.YES_OPTION) {
			this.dispose();
			System.exit(0);
		}
		
	}
	
	public void setStatus(String message) {
		this.statusbar.setText(message);
		
	}
	public void reset_x_y(){
		Transform3D transform3D_axis = new Transform3D();
		transform3D_axis.setTranslation(new Vector3f(0f, 0f, 0f));
		transformGroup_axis.setTransform(transform3D_axis);
		transform3D_axis.invert();
	}
	public void reset_x_z(){
		Transform3D transform3D_axis = new Transform3D();
		transform3D_axis.setTranslation(new Vector3f(0f, 0f, 0f));
		transform3D_axis.rotX(-1 * Math.PI/2);
		transformGroup_axis.setTransform(transform3D_axis);
		transform3D_axis.invert();
	}
	public void reset_y_z(){
		Transform3D transform3D_axis = new Transform3D();
		transform3D_axis.setTranslation(new Vector3f(0f, 0f, 0f));
		transform3D_axis.rotY(Math.PI/2);
		transformGroup_axis.setTransform(transform3D_axis);
		transform3D_axis.invert();
	}
	public int getControlMode() {
		return controlMode;
	}
	public void setControlMode(int controlMode) {
		this.controlMode = controlMode;
	}
	private void drawMVE(String key, GSpaceData gSpaceData, Color3f color) {
		if (gSpaceData==null){
			return;
		}
		double[][] values = new double[3][gSpaceData.getVerticesIndex().length];
		int vpcount = 0;
		for (int x=0;x<gSpaceData.getVerticesIndex().length;x++){
			if ((gSpaceData.getxValues()[gSpaceData.getVerticesIndex()[x]]==Const.NoData)
					||(gSpaceData.getyValues()[gSpaceData.getVerticesIndex()[x]]==Const.NoData)
					||(gSpaceData.getzValues()[gSpaceData.getVerticesIndex()[x]]==Const.NoData)){
				continue;
			}
			values[0][vpcount] = gSpaceData.getxValues()[gSpaceData.getVerticesIndex()[x]];
			values[1][vpcount] = gSpaceData.getyValues()[gSpaceData.getVerticesIndex()[x]];
			values[2][vpcount] = gSpaceData.getzValues()[gSpaceData.getVerticesIndex()[x]];
			vpcount++;
		}

		MinimumVolumeEllipsoidResult mve = MinimumVolumeEllipsoid.getMatrix(values, (this.is3D)?3:2);
		
//		mve.print();
		BranchGroup mveBranch = new BranchGroup();
		mveBranch.setCapability(BranchGroup.ALLOW_DETACH);
		Matrix A = mve.getA();
		mveBranch.addChild(new Ellipsoid(maxLength, A, mve.getCenter(), 20, 20, 0, color, true, this.is3D, 2));
//		for (int i=1;i<3;i++){
//			double[][] f = new double[3][3];
//			f[0][0] = 3d/(double)i;
//			f[1][1] = 3d/(double)i;
//			f[2][2] = 3d/(double)i;
//			Matrix m = new Matrix(f);
//			mveBranch.addChild(new Ellipsoid(maxLength, A.times(m), mve.getCenter(), 20, 20, 0, Color3D.blue, true, this.is3D));
//		}
		
		
		if (this.showMVE){
			transformGroup_axis.addChild(mveBranch);
		}
		mveBranches.put(key, mveBranch);
		vpMVE.put(key, mve);
	}
	
	public void drawVPWithConvexHull() throws IOException {
		if (backgroundValues==null){
			ShowAlert(Message.getString("nobackground_alert"));
			return;
		}
		OpenSDSForm sdsform = new OpenSDSForm(this);
		sdsform.setVisible(true);
		if (sdsform.getTarget()!=null){
			if (!sdsform.isFolder()){
				if (backgroundValues==null){
					ShowAlert(Message.getString("nobackground_alert"));
					return;
				}
			}
			ArrayList<String> drawvs = new ArrayList<String>();
			for (String vs : sdsform.getTarget()){
				if (this.theMenu.checkVS(vs)){
					drawvs.add(vs);
				}else{
					ShowAlert(String.format(Message.getString("duplicated_vs"), vs));
				}
			}
			String[] vs = new String[drawvs.size()];
			for (int i=0;i<drawvs.size();i++){
				vs[i] = drawvs.get(i);
			}
			draw(vs, false, !sdsform.isFolder(), sdsform.getThreshold(),
					sdsform.getMvecolor(), sdsform.getChcolor(), sdsform.getPointcolor(), sdsform.isGradual(),
					sdsform.getLayers(), this.maxmin);
		}
	}
	public void propertyChange(PropertyChangeEvent evt) {
		
		
		if (evt.getSource() instanceof ExportToENMWorker){
			if ("progress".equals(evt.getPropertyName())){
				int progress = (Integer) evt.getNewValue();
				progressMonitor.setProgress(progress);
				String message =
	                String.format(Message.getString("complete"), progress);
				progressMonitor.setNote(message);
				
			}
			if (evt.getPropertyName().equals("state")){
				ExportToENMWorker generator = (ExportToENMWorker) evt.getSource();
				SwingWorker.StateValue state = (SwingWorker.StateValue)evt.getNewValue();
				if (state == SwingWorker.StateValue.DONE){
					ShowAlert(Message.getString("done"));
				}
			}
			if ("done-exception".equals(evt.getPropertyName())){
				ExportToENMWorker generator = (ExportToENMWorker) evt.getSource();
				generator.getException().printStackTrace();
			}
		}
		if (evt.getSource() instanceof NicheBreadthProcessor){
			if ("progress".equals(evt.getPropertyName())){
				int progress = (Integer) evt.getNewValue();
				progressMonitor.setProgress(progress);
				String message =
	                String.format(Message.getString("complete"), progress);
				progressMonitor.setNote(message);
				
			}
			if (evt.getPropertyName().equals("state")){
				NicheBreadthProcessor generator = (NicheBreadthProcessor) evt.getSource();
				SwingWorker.StateValue state = (SwingWorker.StateValue)evt.getNewValue();
				if (state == SwingWorker.StateValue.DONE){
					ShowAlert(Message.getString("done"));
				}
			}
			if ("done-exception".equals(evt.getPropertyName())){
				NicheBreadthProcessor generator = (NicheBreadthProcessor) evt.getSource();
				generator.getException().printStackTrace();
			}
		}
		
		if (evt.getSource() instanceof FormatConvertor){
			FormatConvertor generator = (FormatConvertor) evt.getSource();
			if ("progress".equals(evt.getPropertyName())){
				int progress = (Integer) evt.getNewValue();
				progressMonitor.setProgress(progress);
				String message =
	                String.format(Message.getString("complete"), progress);
				progressMonitor.setNote(message);
				if (progress==100){
					if (generator.isPass()){
						ShowAlert(String.format(Message.getString("finish_to_convert"), 
								generator.getFormat(), generator.getTarget()));
					}else{
						ShowAlert(String.format(Message.getString("error_to_convert"), 
								generator.getFormat(), generator.getTarget()));
					}
				}
			}
			if ("done-exception".equals(evt.getPropertyName())){
				generator = (FormatConvertor) evt.getSource();
				generator.getException().printStackTrace();
			}
		}
		if (evt.getSource() instanceof VariableGenerator){
			VariableGenerator generator = (VariableGenerator) evt.getSource();
			if ("progress".equals(evt.getPropertyName())){
				int progress = (Integer) evt.getNewValue();
				progressMonitor.setProgress(progress);
				String message =
	                String.format(Message.getString("complete"), progress);
				progressMonitor.setNote(message);
				if (progress==100){
					ShowAlert(Message.getString("done"));
				}
			}
			if ("done-exception".equals(evt.getPropertyName())){
				generator = (VariableGenerator) evt.getSource();
				generator.getException().printStackTrace();
			}
		}
		if (evt.getSource() instanceof SpeciesGenerator){
			if ("progress".equals(evt.getPropertyName())){
				int progress = (Integer) evt.getNewValue();
				progressMonitor.setProgress(progress);
				String message =
	                String.format(Message.getString("complete"), progress);
				progressMonitor.setNote(message);
			}
			if (evt.getPropertyName().equals("state")){
				SpeciesGenerator generator = (SpeciesGenerator) evt.getSource();
				SwingWorker.StateValue state = (SwingWorker.StateValue)evt.getNewValue();
				if (state == SwingWorker.StateValue.DONE){
					ShowAlert(String.format(Message.getString("sds_saved"), generator.getTarget()));
				}
			}
			if ("done-exception".equals(evt.getPropertyName())){
				SpeciesGenerator generator = (SpeciesGenerator) evt.getSource();
				generator.getException().printStackTrace();
			}
		}
		if (evt.getSource() instanceof MultiGeoLayerReaderProcessor){
			MultiGeoLayerReaderProcessor generator = (MultiGeoLayerReaderProcessor) evt.getSource();
			if ("progress".equals(evt.getPropertyName())){
				int progress = (Integer) evt.getNewValue();
				progressMonitor.setProgress(progress);
				String message =
	                String.format(Message.getString("complete"), progress);
				progressMonitor.setNote(message);
				if (progress==100){
					ShowAlert(Message.getString("done"));
				}
			}
			if ("done-exception".equals(evt.getPropertyName())){
				generator = (MultiGeoLayerReaderProcessor) evt.getSource();
				generator.getException().printStackTrace();
			}
		}
		
		if (evt.getSource() instanceof PCAGenerator){
			PCAGenerator generator = (PCAGenerator) evt.getSource();
			if ("progress".equals(evt.getPropertyName())){
				int progress = (Integer) evt.getNewValue();
				progressMonitor.setProgress(progress);
				String message =
	                String.format(Message.getString("complete"), progress);
	            if ((progress>=0)&&(progress<30)) {
	            	message = Message.getString("running_r");
	            }
	            if ((progress>=30)&&(progress<50)) {
	            	message = Message.getString("init_instances");
	            }
	            if ((progress>=50)&&(progress<80)) {
	            	message = Message.getString("calculating_pca");
	            }
	            if ((progress>=80)&&(progress<100)) {
	            	message = Message.getString("writting_result");
	            }
	            if (progress==100) {
	            	message = Message.getString("done");
	            }
	            progressMonitor.setNote(message);
			}
			if ("done-exception".equals(evt.getPropertyName())){
				
				generator.getException().printStackTrace();
			}
			if (evt.getPropertyName().equals("state")){

				SwingWorker.StateValue state = (SwingWorker.StateValue)evt.getNewValue();
				if (state == SwingWorker.StateValue.DONE){
					
					try {
						final String html = generator.getHTMLResult();
					    SwingUtilities.invokeLater(new Runnable() {
					        @Override
					        public void run() {
								try {
									File htmlfile = new File(html);
									HTMLViewer viewer;
									viewer = new HTMLViewer(htmlfile, Message.getString("pca_generator"), 800, 800, true);
									viewer.setVisible(true);
						        	viewer.dispose();
								} catch (SAXException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
					        	
					        }
					    });
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
			}
		}
		if (evt.getSource() instanceof PartialROCGenerator){
			PartialROCGenerator generator = (PartialROCGenerator) evt.getSource();
			if ("progress".equals(evt.getPropertyName())){
				int progress = (Integer) evt.getNewValue();
				progressMonitor.setProgress(progress);
				
			}
			if ("done-exception".equals(evt.getPropertyName())){
				
				generator.getException().printStackTrace();
			}
			if (evt.getPropertyName().equals("state")){

				SwingWorker.StateValue state = (SwingWorker.StateValue)evt.getNewValue();
				if (state == SwingWorker.StateValue.DONE){
					
					try {
						final String html = generator.getHTMLResult();
					    SwingUtilities.invokeLater(new Runnable() {
					        @Override
					        public void run() {
								try {
									File htmlfile = new File(html);
									HTMLViewer viewer;
									viewer = new HTMLViewer(htmlfile, Message.getString("partial_roc"), 800, 800, true);
									viewer.setVisible(true);
						        	viewer.dispose();
								} catch (SAXException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
					        	
					        }
					    });
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
			}
		}
		if (evt.getSource() instanceof ENMRangeGenerator){
			ENMRangeGenerator generator = (ENMRangeGenerator) evt.getSource();
			if ("progress".equals(evt.getPropertyName())){
				int progress = (Integer) evt.getNewValue();
				progressMonitor.setProgress(progress);
				
			}
			if ("done-exception".equals(evt.getPropertyName())){
				
				generator.getException().printStackTrace();
			}
			if (evt.getPropertyName().equals("state")){

				SwingWorker.StateValue state = (SwingWorker.StateValue)evt.getNewValue();
				if (state == SwingWorker.StateValue.DONE){
					
					try {
						final String html = generator.getHTMLResult();
					    SwingUtilities.invokeLater(new Runnable() {
					        @Override
					        public void run() {
								try {
									File htmlfile = new File(html);
									HTMLViewer viewer;
									viewer = new HTMLViewer(htmlfile, Message.getString("setenmrange"), 800, 800, true);
									viewer.setVisible(true);
						        	viewer.dispose();
								} catch (SAXException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
					        	
					        }
					    });
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
			}
		}
		
		if (evt.getSource() instanceof AICCalculatorGenerator){
			AICCalculatorGenerator generator = (AICCalculatorGenerator) evt.getSource();
			if ("progress".equals(evt.getPropertyName())){
				int progress = (Integer) evt.getNewValue();
				progressMonitor.setProgress(progress);
				
			}
			if ("done-exception".equals(evt.getPropertyName())){
				
				generator.getException().printStackTrace();
			}
			if (evt.getPropertyName().equals("state")){

				SwingWorker.StateValue state = (SwingWorker.StateValue)evt.getNewValue();
				if (state == SwingWorker.StateValue.DONE){
					
					try {
						final String html = generator.getHTMLResult();
					    SwingUtilities.invokeLater(new Runnable() {
					        @Override
					        public void run() {
								try {
									File htmlfile = new File(html);
									HTMLViewer viewer;
									viewer = new HTMLViewer(htmlfile, Message.getString("setenmrange"), 800, 800, true);
									viewer.setVisible(true);
						        	viewer.dispose();
								} catch (SAXException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
					        	
					        }
					    });
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
			}
		}
		
		if (evt.getSource() instanceof VariableStandardizationProcessor){
			if ("progress".equals(evt.getPropertyName())){
				int progress = (Integer) evt.getNewValue();
				progressMonitor.setProgress(progress);
	            String message =
	                String.format(Message.getString("complete"), progress);
	            progressMonitor.setNote(message);
	            if (progress==100) {
	            	
	            }
			}
			if (evt.getPropertyName().equals("state")){

				SwingWorker.StateValue state = (SwingWorker.StateValue)evt.getNewValue();
				if (state == SwingWorker.StateValue.DONE){
					ShowAlert(Message.getString("done"));
				}
			}
			if ("done-exception".equals(evt.getPropertyName())){
				VariableStandardizationProcessor generator = (VariableStandardizationProcessor) evt.getSource();
				generator.getException().printStackTrace();
			}
			
		}

		
		
		if (evt.getSource() instanceof SetNodataGeoLayerProcessor){
			if ("progress".equals(evt.getPropertyName())){
				int progress = (Integer) evt.getNewValue();
				progressMonitor.setProgress(progress);
	            String message =
	                String.format(Message.getString("complete"), progress);
	            progressMonitor.setNote(message);
	            if (progress==100) {
	            	
	            }
			}
			if (evt.getPropertyName().equals("state")){

				SwingWorker.StateValue state = (SwingWorker.StateValue)evt.getNewValue();
				if (state == SwingWorker.StateValue.DONE){
					ShowAlert(Message.getString("done"));
				}
			}
			if ("done-exception".equals(evt.getPropertyName())){
				SetNodataGeoLayerProcessor generator = (SetNodataGeoLayerProcessor) evt.getSource();
				generator.getException().printStackTrace();
			}
		}

		
		if (evt.getSource() instanceof VirtualSpeciesGenerator){
			if ("progress".equals(evt.getPropertyName())){
				int progress = (Integer) evt.getNewValue();
				progressMonitor.setProgress(progress);
	            String message =
	                String.format(Message.getString("complete"), progress);
	            progressMonitor.setNote(message);
	            if (progress==100) {
	            	
	            }
			}
			if (evt.getPropertyName().equals("state")){

				SwingWorker.StateValue state = (SwingWorker.StateValue)evt.getNewValue();
				if (state == SwingWorker.StateValue.DONE){
					ShowAlert(Message.getString("done"));
				}
			}
			if ("done-exception".equals(evt.getPropertyName())){
				VirtualSpeciesGenerator generator = (VirtualSpeciesGenerator) evt.getSource();
				generator.getException().printStackTrace();
			}
		}
		
		if (evt.getSource() instanceof ModelThresholdGenerator){
			
			if ("progress".equals(evt.getPropertyName())){
				int progress = (Integer) evt.getNewValue();
				progressMonitor.setProgress(progress);
	            String message =
	                String.format(Message.getString("complete"), progress);
	            progressMonitor.setNote(message);
	            if (progress==100) {
	            	
	            }
			}
			if (evt.getPropertyName().equals("state")){
				ModelThresholdGenerator c = (ModelThresholdGenerator) evt.getSource();
				SwingWorker.StateValue state = (SwingWorker.StateValue)evt.getNewValue();
				if (state == SwingWorker.StateValue.DONE){
					File html;
					try {
						html = new File(c.getHTMLResult());
						HTMLViewer viewer = new HTMLViewer(html, Message.getString("threshold_variables"), 1000, 800, true);
						viewer.setVisible(true);

					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (SAXException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
				
			}
			if ("done-exception".equals(evt.getPropertyName())){
				ModelThresholdGenerator generator = (ModelThresholdGenerator) evt.getSource();
				generator.getException().printStackTrace();
			}
			if (progressMonitor.isCanceled()) {
				ModelThresholdGenerator c = (ModelThresholdGenerator) evt.getSource();
                c.cancel(true);
            }
		}
		if (evt.getSource() instanceof NicheMapGenerator){
			
			if ("progress".equals(evt.getPropertyName())){
				int progress = (Integer) evt.getNewValue();
				progressMonitor.setProgress(progress);
	            String message =
	                String.format(Message.getString("complete"), progress);
	            progressMonitor.setNote(message);
	            if (progress==100) {
	            	
	            }
			}
			if (evt.getPropertyName().equals("state")){
				NicheMapGenerator c = (NicheMapGenerator) evt.getSource();
				SwingWorker.StateValue state = (SwingWorker.StateValue)evt.getNewValue();
				if (state == SwingWorker.StateValue.DONE){
					ShowAlert(Message.getString("done"));
				}
				
			}
			if ("done-exception".equals(evt.getPropertyName())){
				NicheMapGenerator generator = (NicheMapGenerator) evt.getSource();
				generator.getException().printStackTrace();
			}
			if (progressMonitor.isCanceled()) {
				NicheMapGenerator c = (NicheMapGenerator) evt.getSource();
                c.cancel(true);
            }
		}
		if (evt.getSource() instanceof MultiVarNormalDistributionGenerator){
			
			if ("progress".equals(evt.getPropertyName())){
				int progress = (Integer) evt.getNewValue();
				progressMonitor.setProgress(progress);
	            String message =
	                String.format(Message.getString("complete"), progress);
	            progressMonitor.setNote(message);
	            if (progress==100) {
	            	
	            }
			}
			if (evt.getPropertyName().equals("state")){
				MultiVarNormalDistributionGenerator c = (MultiVarNormalDistributionGenerator) evt.getSource();
				SwingWorker.StateValue state = (SwingWorker.StateValue)evt.getNewValue();
				if (state == SwingWorker.StateValue.DONE){
					if (c.isSuccessed()){
						ShowAlert(Message.getString("done"));
					}else{
						ShowAlert(String.format(Message.getString("error_in_precess"), c.getErrorMsg()));
						c.cancel(true);
					}
				}
			}
			if (progressMonitor.isCanceled()) {
				VariablesStator c = (VariablesStator) evt.getSource();
                c.cancel(true);
            }
			if ("done-exception".equals(evt.getPropertyName())){
				MultiVarNormalDistributionGenerator generator = (MultiVarNormalDistributionGenerator) evt.getSource();
				generator.getException().printStackTrace();
			}
		}
		if (evt.getSource() instanceof VariablesStator){
			
			if ("progress".equals(evt.getPropertyName())){
				int progress = (Integer) evt.getNewValue();
				progressMonitor.setProgress(progress);
	            String message =
	                String.format(Message.getString("complete"), progress);
	            progressMonitor.setNote(message);
	            if (progress==100) {
	            	
	            }
			}
			if (evt.getPropertyName().equals("state")){
				VariablesStator c = (VariablesStator) evt.getSource();
				SwingWorker.StateValue state = (SwingWorker.StateValue)evt.getNewValue();
				if (state == SwingWorker.StateValue.DONE){
					if (c.isSuccessed()){
						ShowAlert(Message.getString("done"));
					}else{
						ShowAlert(String.format(Message.getString("error_in_precess"), c.getErrorMsg()));
						c.cancel(true);
					}
				}
			}
			if (progressMonitor.isCanceled()) {
				VariablesStator c = (VariablesStator) evt.getSource();
                c.cancel(true);
            }
			if ("done-exception".equals(evt.getPropertyName())){
				VariablesStator generator = (VariablesStator) evt.getSource();
				generator.getException().printStackTrace();
			}
		}
		
		if (evt.getSource() instanceof OccurrenceMapper){
			if ("progress".equals(evt.getPropertyName())){
				int progress = (Integer) evt.getNewValue();
				progressMonitor.setProgress(progress);
	            String message =
	                String.format(Message.getString("complete"), progress);
	            progressMonitor.setNote(message);
	            if (progress==100) {
	            	
	            }
			}
			if (evt.getPropertyName().equals("state")){

				SwingWorker.StateValue state = (SwingWorker.StateValue)evt.getNewValue();
				if (state == SwingWorker.StateValue.DONE){
					OccurrenceMapper generator = (OccurrenceMapper) evt.getSource();
					try {
						File html = new File(generator.getHTMLResult());
						HTMLViewer viewer;
						viewer = new HTMLViewer(html, Message.getString("voccur2geo"), 800, 800, true);
						viewer.setVisible(true);
					} catch (SAXException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			if ("done-exception".equals(evt.getPropertyName())){
				OccurrenceMapper generator = (OccurrenceMapper) evt.getSource();
				generator.getException().printStackTrace();
			}
			
		}
		if (evt.getSource() instanceof OccurrenceDistributionStator){
			OccurrenceDistributionStator generator = (OccurrenceDistributionStator) evt.getSource();
			if ("progress".equals(evt.getPropertyName())){
				int progress = (Integer) evt.getNewValue();
				progressMonitor.setProgress(progress);
	            String message =
	                String.format(Message.getString("complete"), progress);
	            progressMonitor.setNote(message);
	            if (progress==100) {
	            	
	            }
			}
			if (evt.getPropertyName().equals("state")){

				SwingWorker.StateValue state = (SwingWorker.StateValue)evt.getNewValue();
				if (state == SwingWorker.StateValue.DONE){
					
					
					try {
						File html = new File(generator.getHTMLResult());
						HTMLViewer viewer = new HTMLViewer(html, Message.getString("occurrence_distribution"), 800, 800, true);
						viewer.setVisible(true);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (SAXException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					
				}
			}
			if ("done-exception".equals(evt.getPropertyName())){
				generator.getException().printStackTrace();
			}
			
		}
		
		if (evt.getSource() instanceof EllipsoidOverlapCalculator){
			if ("progress" == evt.getPropertyName()) {
				EllipsoidOverlapCalculator c = (EllipsoidOverlapCalculator) evt.getSource();
				String message =
	                String.format(Message.getString("overlap_dialog_label"), 
	                		this.overlapSpeciesDataSets.size(), c.getMinD_Value(), c.getPrecision(), c.getMaxsteps() - c.getAllSteps());
	            progressMonitor.setNote(message);
	            int progress = (Integer) evt.getNewValue();
	            progressMonitor.setProgress(progress);
	            if (progress==100) {
	            	

	            }
	            if (progressMonitor.isCanceled()) {
	                c.cancel(true);
	            }
	            
			}
			if ("done-exception".equals(evt.getPropertyName())){
            	EllipsoidOverlapCalculator generator = (EllipsoidOverlapCalculator) evt.getSource();
				generator.getException().printStackTrace();
			}
			if (evt.getPropertyName().equals("state")){
				SwingWorker.StateValue state = (SwingWorker.StateValue)evt.getNewValue();
				if (state == SwingWorker.StateValue.DONE){
					EllipsoidOverlapCalculator c = (EllipsoidOverlapCalculator) evt.getSource();
					overlapResult.append(String.format("<h4>Overlap result for <font color='red'>%s</font> vs <font color='red'>%s</font></h4>", c.getVs1_label(), c.getVs2_label()));
					overlapResult.append("<li>MVE Overlap:" + c.getMve_overlap() + "</li>" + Const.LineBreak);
	            	if (c.getOverlap_method().equalsIgnoreCase(Message.getString("convexhull"))){
	            		overlapResult.append("<li>Convex polyhedron Overlap:" + c.getConvex_overlap() + "</li>" + Const.LineBreak);
	            	}
	            	overlapResult.append("<li>Precision:" + c.getPrecision() + "</li>" + Const.LineBreak);
	            	overlapResult.append("<li>Steps/Max steps:" + c.getAllSteps() + "/" + c.getMaxsteps() + "</li>" + Const.LineBreak);
	            	overlapResult.append(Const.LineBreak);
	            	overlapResult.append("<h4>Information of Species 1:</h4>" + Const.LineBreak);
	            	overlapResult.append("<li>Label: " + c.getVs1_label() + "</li>" + Const.LineBreak);
	            	overlapResult.append("<li>Minimum Volume Ellipsoid" + "</li>" + Const.LineBreak);
	            	overlapResult.append(CommonFun.toHTML(c.getMve1().toString()) + Const.LineBreak);
	            	overlapResult.append(Const.LineBreak);
	            	overlapResult.append("<li>MVE Volume (formula):" + c.getMVEVolume1() + "</li>" + Const.LineBreak);
	            	overlapResult.append("<li>MVE Volume (estimated):" + c.getP_mve_volume1() + "</li>" + Const.LineBreak);
	            	if (c.getOverlap_method().equalsIgnoreCase(Message.getString("convexhull"))){
	            		overlapResult.append("<li>Convex polyhedron Volume (formula):" + c.getConvex_volume1() + "</li>" + Const.LineBreak);
	            		overlapResult.append("<li>Convex polyhedron Volume (estimated):" + c.getP_convex_volume1() + "</li>" + Const.LineBreak);
	            	}
//	            	overlapResult.append("-----------------------" + Const.LineBreak);
	            	
	            	overlapResult.append(Const.LineBreak);
	            	overlapResult.append("<h4>Information of Species 2:</h4>" + Const.LineBreak);
	            	overlapResult.append("<li>Label: " + c.getVs2_label() + "</li>" + Const.LineBreak);
	            	overlapResult.append("<li>Minimum Volume Ellipsoid" + "</li>" + Const.LineBreak);
	            	overlapResult.append(CommonFun.toHTML(c.getMve2().toString()) + Const.LineBreak);
	            	overlapResult.append(Const.LineBreak);
	            	overlapResult.append("<li>MVE Volume (formula):" + c.getMVEVolume2() + "</li>" + Const.LineBreak);
	            	overlapResult.append("<li>MVE Volume (estimated):" + c.getP_mve_volume2() + "</li>" + Const.LineBreak);
	            	if (c.getOverlap_method().equalsIgnoreCase(Message.getString("convexhull"))){
	            		overlapResult.append("<li>Convex polyhedron Volume (formula):" + c.getConvex_volume2() + "</li>" + Const.LineBreak);
	            		overlapResult.append("<li>Convex polyhedron Volume (estimated):" + c.getP_convex_volume2() + "</li>" + Const.LineBreak);
	            	}
//	            	overlapResult.append("-----------------------" + Const.LineBreak);
	            	overlapResultTable.append(String.format("%s,%f,%f,%f,%f,%s,%f,%f,%f,%f,%f,%f%n", 
	            			c.getVs1_label(), c.getMVEVolume1(), c.getP_mve_volume1(), 
	            			c.getConvex_volume1(),c.getP_convex_volume1(),
	            			c.getVs2_label(), c.getMVEVolume2(), c.getP_mve_volume2(), 
	            			c.getConvex_volume2(),c.getP_convex_volume2(),
	            			c.getMve_overlap(), c.getConvex_overlap()));
	            	
					handleOverlapQueue(c.getTarget());
				}
			}
		}
		if (evt.getSource() instanceof PointGenerator){
			PointGenerator c = (PointGenerator) evt.getSource();
	        if ("progress" == evt.getPropertyName()) {
	            int progress = (Integer) evt.getNewValue();
	            if (progress==1){
	            	String message = Message.getString("loading");
		            progressMonitor.setNote(message);
	            }
	            if (progress==99){
		            String message = Message.getString("loading");
		            progressMonitor.setNote(message);
	            }else{
		            progressMonitor.setProgress(progress);
		            String message =
		                String.format(Message.getString("complete"), progress);
		            progressMonitor.setNote(message);
	            }
	            if (progress==100) {
	                
	            }
	            if (progressMonitor.isCanceled()) {
	                c.cancel(true);

	            }
	        }
	        if ("done-exception".equals(evt.getPropertyName())){
	        	PointGenerator generator = (PointGenerator) evt.getSource();
				generator.getException().printStackTrace();
			}
	        if (evt.getPropertyName().equals("state")){
				SwingWorker.StateValue state = (SwingWorker.StateValue)evt.getNewValue();
				
				if (state == SwingWorker.StateValue.DONE){
					if (this.pointGenerator.isSuccessed()){
						if (!this.pointGenerator.isIsbackgroupd()){
		                	gSpaceDatasets.put(this.pointGenerator.getFilename(), this.pointGenerator.getgSpaceData());
		                	vpStatus.put(this.pointGenerator.getFilename(), new boolean[]{this.showPoint, this.showMVE, this.showConvexHull});
		                	drawPoints(this.pointGenerator.getFilename(), this.pointGenerator.getDotStatus(), 
		                			this.pointGenerator.isIsbackgroupd());
		    				//draw faces
		    				drawConvexHull(this.pointGenerator.getFilename(), 
		    						this.pointGenerator.getFaceIndices(), 
		    						this.pointGenerator.getVertices(), 
		    						this.pointGenerator.getCh_color());
		    				//calculate the MVE
		    				drawMVE(this.pointGenerator.getFilename(), this.pointGenerator.getgSpaceData(), 
		    						this.pointGenerator.getMve_color());
		    				SpeciesDataset vs = new SpeciesDataset(
		    						this.pointGenerator.getValues(), 
		    						this.is3D, this.pointGenerator.getFilename());
		    				this.theMenu.addVP(this.pointGenerator.getFilename(), vs, !this.pointGenerator.isResult(), this.isShowPoint(), this.isShowMVE(), this.isShowConvexHull());
		                }else{
		                	
		                	this.backgroundValues = this.pointGenerator.getValues();
		                	this.isFullBackground = this.pointGenerator.isFullBackground();
		                	this.maxmin = this.pointGenerator.getMaxMin();
		                	
		                	drawPoints(this.pointGenerator.getFilename(), this.pointGenerator.getDotStatus(), 
		                			this.pointGenerator.isIsbackgroupd());
		                	this.reset_x_y();
		                	if (is3D){
		                		
		                		sliders.get(3).setEnabled(true);
		                		sliders.get(6).setEnabled(true);
		                		sliders.get(7).setEnabled(true);
		                		sliders.get(8).setEnabled(true);
		                		
		                		lockers.get(0).setEnabled(true);
		                		lockers.get(1).setEnabled(true);
		                		lockers.get(2).setEnabled(true);
		                		lockers.get(3).setEnabled(true);
		                		lockers.get(4).setEnabled(true);
		                		lockers.get(5).setEnabled(true);
		                		
		                		mTrans.setEnable(true);
		                		mRotate.setEnable(true);
		                	}else{
		                		
		                		
		                		lockers.get(0).setEnabled(false);
		                		lockers.get(1).setEnabled(false);
		                		lockers.get(2).setEnabled(false);
		                		lockers.get(3).setEnabled(false);
		                		lockers.get(4).setEnabled(false);
		                		lockers.get(5).setEnabled(false);
		                		sliders.get(3).setValue(0);
		                		sliders.get(6).setValue(0);
		                		sliders.get(7).setValue(0);
		                		sliders.get(8).setValue(0);
		                		
		                		sliders.get(3).setEnabled(false);
		                		sliders.get(6).setEnabled(false);
		                		sliders.get(7).setEnabled(false);
		                		sliders.get(8).setEnabled(false);
		                		
		                		mTrans.setEnable(false);
		                		mRotate.setEnable(false);
		                	}
		                }
					}else{
						if (!this.pointGenerator.isIsbackgroupd()){
							ShowAlert(Message.getString("error_open_SDS"));
						}else{
							ShowAlert(Message.getString("error_open_background"));
						}
					}
					handleDrawQueue();
				}
			}
		}
 
    }
	
	private void drawBackgroundWithTiffFiles(HashMap<String, String> filenames){
		progressMonitor = new ProgressMonitor(this, Message.getString("drawing"), "", 0, 100);
		pointGenerator = new PointGenerator(ConfigInfo.getInstance().getBackgroundPointCount(), filenames, is3D);
		pointGenerator.addPropertyChangeListener(this);
		pointGenerator.execute();
		
//		progress.close();
	}
	
	private void draw(String[] filenames, boolean isbackgroupd, boolean isResult, double threshold,
			Color3f mve_color, Color3f ch_color, Color3f point_color, boolean isGradual, String[] layers, double[][][] maxmin){
		drawQueue = new ArrayList<DrawingObject>();
		for (String filename : filenames){
			DrawingObject drawingObject = new DrawingObject(
					filename, isbackgroupd, isResult, threshold, 
					mve_color, ch_color, point_color, isGradual, layers, maxmin);
			drawQueue.add(drawingObject);
		}
		handleDrawQueue();
//		progress.close();
	}
	
	private void handleDrawQueue(){
		if (drawQueue==null){
			return;
		}
		if (drawQueue.size()!=0){
			DrawingObject drawingObject = drawQueue.get(0);
			progressMonitor = new ProgressMonitor(this, Message.getString("drawing"), "", 0, 100);
			pointGenerator = new PointGenerator(this, (drawingObject.isIsbackgroupd())?
					ConfigInfo.getInstance().getBackgroundPointCount():maxpoints, 
					drawingObject.isIsbackgroupd(), drawingObject.getFilename(), 
					drawingObject.isResult(), 
					drawingObject.getThreshold(), 
					this.backgroundTiff, drawingObject.getMve_color(), 
					drawingObject.getCh_color(), drawingObject.getPoint_color(),
					drawingObject.isGradual(), isFullBackground, drawingObject.getLayers(), 
					drawingObject.getMaxmin(), is3D);
			pointGenerator.addPropertyChangeListener(this);
			pointGenerator.execute();
			drawQueue.remove(0);
		}else{
			ShowAlert(Message.getString("done"));
		}
	}
	private void drawConvexHull(String key, int[][] faceIndices, quickhull3d.Point3d[] vertices, Color3f color) {
		if (vertices==null){
			return;
		}
		BranchGroup convexHullBranch = new BranchGroup();
		convexHullBranch.setCapability(BranchGroup.ALLOW_DETACH);
		convexHullBranch.addChild(new ConvesHullFace(maxLength, faceIndices, vertices, color, is3D));
		if (this.showConvexHull){
			transformGroup_axis.addChild(convexHullBranch);
		}
		convexHullBranches.put(key, convexHullBranch);
		
	}
	private void drawPoints(String key, HashMap<String, SpeciesData> dotStatus, boolean isbackgroup) {
		
		if (dotStatus==null){
			dotStatus = new HashMap<String, SpeciesData>();
		}
		if (isbackgroup){
			float maxLangthf = -1 * Float.MAX_VALUE;
			for (String pos : dotStatus.keySet()){
				String[] poses = pos.split(",");
				
				float xpos = Math.abs(Float.valueOf(poses[0]));
				maxLangthf = (maxLangthf<xpos)?xpos:maxLangthf;
				float ypos = Math.abs(Float.valueOf(poses[1]));
				maxLangthf = (maxLangthf<ypos)?ypos:maxLangthf;
				float zpos = Math.abs(Float.valueOf(poses[2]));
				maxLangthf = (maxLangthf<zpos)?zpos:maxLangthf;
				
			}
			initMaxLength(maxLangthf);
		}
		BranchGroup pointBranch = new BranchGroup();
		pointBranch.setCapability(BranchGroup.ALLOW_DETACH);
		for (String pos:dotStatus.keySet()){
			String[] poses = pos.split(",");
			float xpos = Float.valueOf(poses[0]) / maxLength;
			float ypos = Float.valueOf(poses[1]) / maxLength;
			float zpos = Float.valueOf(poses[2]) / maxLength;
			Color3f color = null;
			if (dotStatus.get(pos)==null){
				color = Color3D.black;
			}
			color = dotStatus.get(pos).getColor();
			
			TransformGroup ot = new TransformGroup();
			Transform3D t = new Transform3D();
			t.setTranslation(new Vector3f(xpos, ypos, zpos));
			ot.setTransform(t);
			Dot dot = new Dot(color, 0.002f);
			dot.setCapability(Shape3D.ALLOW_BOUNDS_READ);
			dot.setCapability(Shape3D.ALLOW_BOUNDS_WRITE);
			dot.setCapability(Shape3D.ALLOW_LOCAL_TO_VWORLD_READ);
			ot.addChild(dot);
			pointBranch.addChild(ot);
		}
		if (isbackgroup){
			this.backgroupdBranch = new BranchGroup();
			this.backgroupdBranch.setCapability(BranchGroup.ALLOW_DETACH);
			this.backgroupdBranch.addChild(pointBranch);
			if (this.showBackground){
				this.transformGroup_axis.addChild(this.backgroupdBranch);
			}
		}else{
			if (this.showPoint){
				transformGroup_axis.addChild(pointBranch);
			}
			pointBranches.put(key, pointBranch);
		}
	}
	public void hideShowPoint(String key) {
		if (this.pointBranches==null){
			return;
		}
		this.vpStatus.get(key)[0] = !this.vpStatus.get(key)[0];
		BranchGroup pointBranch = pointBranches.get(key);
		if (!this.vpStatus.get(key)[0]){
			transformGroup_axis.removeChild(pointBranch);
		}else{
			transformGroup_axis.addChild(pointBranch);
		}
	}
	public void hideShowPoint() {
		if (this.pointBranches==null){
			return;
		}
		for (String key : pointBranches.keySet()){
			if (this.vpStatus.get(key)[0] == this.showPoint){
				hideShowPoint(key);
			}
		}
		this.showPoint = !this.showPoint;
	}
	public void hideShowMVE(String key) {
		if (this.mveBranches==null){
			return;
		}
		this.vpStatus.get(key)[1] = !this.vpStatus.get(key)[1];
		BranchGroup mveBranch = mveBranches.get(key);
		if (!this.vpStatus.get(key)[1]){
			transformGroup_axis.removeChild(mveBranch);
		}else{
			transformGroup_axis.addChild(mveBranch);
		}		
	}
	public void hideShowMVE() {
		if (this.mveBranches==null){
			return;
		}
		for (String key : mveBranches.keySet()){
			if ((this.vpStatus.get(key)[1]==this.showMVE)){
				hideShowMVE(key);
			}
		}
		this.showMVE = !this.showMVE;
	}
	public void hideShowConvexHull(String key) {
		if (this.convexHullBranches==null){
			return;
		}
		this.vpStatus.get(key)[2] = !this.vpStatus.get(key)[2];
		BranchGroup convexHullBranch = convexHullBranches.get(key);
		if (!this.vpStatus.get(key)[2]){
			transformGroup_axis.removeChild(convexHullBranch);
		}else{
			transformGroup_axis.addChild(convexHullBranch);
		}
	}
	public void hideShowConvexHull() {
		if (this.convexHullBranches==null){
			return;
		}
		for (String key : convexHullBranches.keySet()){
			if (this.vpStatus.get(key)[2] == this.showConvexHull){
				hideShowConvexHull(key);
			}
		}
		this.showConvexHull = !this.showConvexHull;
	}
	public void showHideVP(HashMap<String, boolean[]> vpstatus) {
		for (String key : vpstatus.keySet()){
			for (int i=0; i< vpstatus.get(key).length; i++){
				if (vpstatus.get(key)[i]!=this.vpStatus.get(key)[i]){
					if (vpstatus.get(key)[i]){
						if (i==0){
							this.transformGroup_axis.addChild(this.pointBranches.get(key));
						}
						if (i==1){
							this.transformGroup_axis.addChild(this.mveBranches.get(key));
						}
						if (i==2){
							this.transformGroup_axis.addChild(this.convexHullBranches.get(key));
						}
					}else{
						if (i==0){
							this.transformGroup_axis.removeChild(this.pointBranches.get(key));
						}
						if (i==1){
							this.transformGroup_axis.removeChild(this.mveBranches.get(key));
						}
						if (i==2){
							this.transformGroup_axis.removeChild(this.convexHullBranches.get(key));
						}
					}
				}
			}
		}
		this.vpStatus = vpstatus;
	}
	public void hideShowmulti_ellipsoidBranch(){
		if (this.multi_ellipsoidBranch==null){
			return;
		}
		if (this.showmulti_ellipsoidBranch){
			this.transformGroup_axis.removeChild(this.multi_ellipsoidBranch);
		}else{
			this.transformGroup_axis.addChild(this.multi_ellipsoidBranch);
		}
		this.showmulti_ellipsoidBranch = !this.showmulti_ellipsoidBranch;
	}
	public void hideShowSelection(){
		if (this.ellipsoidBranch==null){
			return;
		}
		if (this.showSelection){
			this.transformGroup_axis.removeChild(this.ellipsoidBranch);
		}else{
			this.transformGroup_axis.addChild(this.ellipsoidBranch);
		}
		this.showSelection = !this.showSelection;
	}
	public void showHideBackgroup(){
		if (this.backgroupdBranch==null){
			return;
		}
		if (this.showBackground){
			this.transformGroup_axis.removeChild(this.backgroupdBranch);
		}else{
			this.transformGroup_axis.addChild(this.backgroupdBranch);
		}
		this.showBackground = !this.showBackground;
	}
	public void showHideGrid(){
		if (this.gridBranch==null){
			return;
		}
		if (this.showGrid){
			this.transformGroup_axis.removeChild(this.gridBranch);
		}else{
			this.transformGroup_axis.addChild(this.gridBranch);
		}
		this.showGrid = !this.showGrid;
	}
	public void showHideOverLap(){
		if (this.overlapBranch==null){
			return;
		}
		if (this.showOverlap){
			this.transformGroup_axis.removeChild(this.overlapBranch);
		}else{
			this.transformGroup_axis.addChild(this.overlapBranch);
		}
		this.showOverlap = !this.showOverlap;
	}
	public void drawOverlap(String vp1, String vp2){
		GSpaceData g1 = gSpaceDatasets.get(vp1);
		GSpaceData g2 = gSpaceDatasets.get(vp2);
		if ((g1==null)||(g2==null)){
			return;
		}
		MinimumVolumeEllipsoidResult mve1 = vpMVE.get(vp1);
		MinimumVolumeEllipsoidResult mve2 = vpMVE.get(vp2);
		float[][] range1 = new float[3][2];
		range1[0][0] = g1.getxMin();
		range1[0][1] = g1.getxMax();
		range1[1][0] = g1.getyMin();
		range1[1][1] = g1.getyMax();
		range1[2][0] = g1.getzMin();
		range1[2][1] = g1.getzMax();
		Matrix a1 = mve1.getA();
		Matrix c1 = mve1.getCenter();
		float[][] values1 = new float[g1.getxValues().length][3];
		for (int i=0;i<g1.getxSize();i++){
			values1[i][0] = g1.getxValues()[i];
			values1[i][1] = g1.getyValues()[i];
			values1[i][2] = g1.getzValues()[i];
		}
		
		float[][] range2 = new float[3][2];
		range2[0][0] = g2.getxMin();
		range2[0][1] = g2.getxMax();
		range2[1][0] = g2.getyMin();
		range2[1][1] = g2.getyMax();
		range2[2][0] = g2.getzMin();
		range2[2][1] = g2.getzMax();
		Matrix a2 = mve2.getA();
		Matrix c2 = mve2.getCenter();
		float[][] values2 = new float[g2.getxValues().length][3];
		for (int i=0;i<g2.getxSize();i++){
			values2[i][0] = g2.getxValues()[i];
			values2[i][1] = g2.getyValues()[i];
			values2[i][2] = g2.getzValues()[i];
		}
		
		int volume1 = 0;
		int volume2 = 0;
		int volume_overlap = 0;
		int density1 = 0;
		int density2 = 0;
		if ((this.showOverlap)&&(overlapBranch!=null)){
			transformGroup_axis.removeChild(overlapBranch);
		}
		overlapBranch = new BranchGroup();
		overlapBranch.setCapability(BranchGroup.ALLOW_DETACH);
		
		EigenvalueDecomposition e = new EigenvalueDecomposition(a1.inverse());
		Matrix eigenValue = e.getD();
		Matrix eigenVector = e.getV();
		float aa1 = (float) Math.sqrt(eigenValue.get(0, 0));
		float bb1 = (float) Math.sqrt(eigenValue.get(1, 1));
		float cc1 = (float) Math.sqrt(eigenValue.get(2, 2));
		
		float max = CommonFun.getMaxValue(new float[]{aa1, bb1, cc1});
		range1[0][0] = (float) (c1.get(0, 0) - max);
		range1[0][1] = (float) (c1.get(0, 0) + max);
		
		range1[1][0] = (float) (c1.get(1, 0) - max);
		range1[1][1] = (float) (c1.get(1, 0) + max);
		
		range1[2][0] = (float) (c1.get(2, 0) - max);
		range1[2][1] = (float) (c1.get(2, 0) + max);
		
		e = new EigenvalueDecomposition(a2.inverse());
		eigenValue = e.getD();
		eigenVector = e.getV();
		float aa2 = (float) Math.sqrt(eigenValue.get(0, 0));
		float bb2 = (float) Math.sqrt(eigenValue.get(1, 1));
		float cc2 = (float) Math.sqrt(eigenValue.get(2, 2));
		
		max = CommonFun.getMaxValue(new float[]{aa2, bb2, cc2});
		range2[0][0] = (float) (c2.get(0, 0) - max);
		range2[0][1] = (float) (c2.get(0, 0) + max);
		
		range2[1][0] = (float) (c2.get(1, 0) - max);
		range2[1][1] = (float) (c2.get(1, 0) + max);
		
		range2[2][0] = (float) (c2.get(2, 0) - max);
		range2[2][1] = (float) (c2.get(2, 0) + max);
		
		for (float x=Math.min(range1[0][0], range2[0][0]);x<=Math.max(range1[0][1], range2[0][1]);x+=step){
			if (!(((range1[0][0]<=x)&&(x<=range1[0][1]))||((range2[0][0]<=x)&&(x<=range2[0][1])))){
				continue;
			}
//			System.out.println(x + "/" + Math.max(range1[0][1], range2[0][1]));
			for (float y=Math.min(range1[1][0], range2[1][0]);y<=Math.max(range1[1][1], range2[1][1]);y+=step){
				if (!(((range1[1][0]<=y)&&(y<=range1[1][1]))||((range2[1][0]<=y)&&(y<=range2[1][1])))){
					continue;
				}
				for (float z=Math.min(range1[2][0], range2[2][0]);z<=Math.max(range1[2][1], range2[2][1]);z+=step){
					if (!(((range1[2][0]<=z)&&(z<=range1[2][1]))||((range2[2][0]<=z)&&(z<=range2[2][1])))){
						continue;
					}	
					float xpos = Float.valueOf(x) / maxLength;
					float ypos = Float.valueOf(y) / maxLength;
					float zpos = Float.valueOf(z) / maxLength;

					
					boolean isin1 = isInEllipsoid(a1, c1, x, y, z);
					boolean isin2 = isInEllipsoid(a2, c2, x, y, z);
					boolean isvalued = isValued(values1, step, x, y, z);
					
					
					Color3f color = null;
					
					if (!isvalued){
						isvalued = isValued(values2, step, x, y, z);
					}
					if (isin1){
						color = Color3D.yellow;
						volume1++;
					}
					if (isin2){
						color = Color3D.yellow;
						volume2++;
					}
					
					if (isvalued){
						if (isin1){
							color = Color3D.white;
							density1++;
						}
						if (isin2){
							color = Color3D.white;
							density2++;
						}
					}
					if (isin1&&isin2){
						if (isvalued){
							color = Color3D.red;
						}else{
							color = Color3D.green;
						}
						volume_overlap++;
					}
					if (color!=null){
						TransformGroup ot = new TransformGroup();
						Transform3D t = new Transform3D();
						t.setTranslation(new Vector3f(xpos, ypos, zpos));
						ot.setTransform(t);
						Dot dot = new Dot(color, 0.001f);
						dot.setCapability(Shape3D.ALLOW_BOUNDS_READ);
						dot.setCapability(Shape3D.ALLOW_BOUNDS_WRITE);
						dot.setCapability(Shape3D.ALLOW_LOCAL_TO_VWORLD_READ);
						ot.addChild(dot);
						overlapBranch.addChild(ot);
					}
				}
			}
		}
		float unit_volume = (float) Math.pow(step, 3);
		String log = String.format("Volume 1: %f, Volume 2: %f, Overlap: %f. Density 1: %f Density 2: %f", 
				(float)volume1 * unit_volume,
				(float)volume2 * unit_volume,
				(float)volume_overlap * unit_volume,
				(float)density1 / (float)volume1,
				(float)density2 / (float)volume2);
		this.statusbar.setText(log);
		if (this.showOverlap){
			transformGroup_axis.addChild(overlapBranch);
		}
	}
	private boolean isValued(float[][] values, float step, float x, float y, float z) {
		for (float[] v : values){
			if ((x<=v[0])&&(v[0]<=(x+step))&&(y<=v[1])&&(v[1]<=(y+step))&&(v[2]<=z)&&(v[2]<=(z+step))){
				return true;
			}
		}
		return false;
	}
	private boolean isInEllipsoid(Matrix A, Matrix C, float x, float y, float z){
		double[] tt = new double[3];
		tt[0] = x;
		tt[1] = y;
		tt[2] = z;
		Matrix xM = new Matrix(tt, 3);
//		CommonFun.printMatrix(xM);
//		System.out.println("");
		Matrix tempMatrix = xM.minus(C);
		Matrix distanceMatrix = tempMatrix.transpose();
		distanceMatrix = distanceMatrix.times(A);
		distanceMatrix = distanceMatrix.times(tempMatrix);
		double distance = distanceMatrix.getArray()[0][0];
		
		if (distance<=1){
			return true;
		}else{
			return false;
		}
	}
	
	public boolean isShowPoint() {
		return showPoint;
	}
	public void setShowPoint(boolean showPoint) {
		this.showPoint = showPoint;
	}
	public boolean isShowMVE() {
		return showMVE;
	}
	public void setShowMVE(boolean showMVE) {
		this.showMVE = showMVE;
	}
	public boolean isShowConvexHull() {
		return showConvexHull;
	}
	public void setShowConvexHull(boolean showConvexHull) {
		this.showConvexHull = showConvexHull;
	}
	public HashMap<String, boolean[]> getVpStatus() {
		return vpStatus;
	}
	public void setVpStatus(HashMap<String, boolean[]> vpStatus) {
		this.vpStatus = vpStatus;
	}
	public boolean isShowOverlap() {
		return showOverlap;
	}
	public void setShowOverlap(boolean showOverlap) {
		this.showOverlap = showOverlap;
	}
	public boolean isShowGrid() {
		return showGrid;
	}
	public void setShowGrid(boolean showGrid) {
		this.showGrid = showGrid;
	}
	public boolean isShowBackground() {
		return showBackground;
	}
	public void setShowBackground(boolean showBackground) {
		this.showBackground = showBackground;
	}
	public boolean isShowSelection() {
		return showSelection;
	}
	public void setShowSelection(boolean showSelection) {
		this.showSelection = showSelection;
	}
	public boolean isShowmulti_ellipsoidBranch() {
		return showmulti_ellipsoidBranch;
	}
	public void setMulti_ellipsoidBranch(BranchGroup multiEllipsoidBranch) {
		multi_ellipsoidBranch = multiEllipsoidBranch;
	}
	public static void main(final String[] args){
		try{
			SwingUtilities.invokeLater(new Runnable() {
	            public void run() {
	            	Properties props = System.getProperties();
	            	props.setProperty("sun.java2d.d3d", "false");
	            	props.setProperty("sun.java2d.ddoffscreen", "false");
	            	props.setProperty("sun.java2d.noddraw", "true");
	            	props.setProperty("user.country", "US");
	//        		CommonFun.refreshSystem();

	            	Displayer v = new Displayer();
//	            	v.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	        		v.setVisible(true);
	        		v.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
	        		
	            }
	        });
		}catch (Exception e){
			e.printStackTrace();
			
		}
		
	}
	
	
	/**
	 * 
	 */
	private static void disableMenu() {
		// TODO Auto-generated method stub
		
	}
	public boolean showConfig(){
		ConfigForm con = new ConfigForm();
		con.setVisible(true);
		boolean returnv = checkEnvironments();
		if (!con.isOK()){
			returnv = true;
		}
		return returnv;
	}
	/* (non-Javadoc)
	 * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
	 */
	@Override
	public void stateChanged(ChangeEvent e) {
		if (e.getSource() instanceof JSlider){
			JSlider source = (JSlider)e.getSource();
	        if (!source.getValueIsAdjusting()) {
	        	createEllipsoidGroup();
	        }
		}
		if (e.getSource() instanceof JCheckBox){
			changeSliderStatus();
		}
		
	}
	private void changeSliderStatus(){
		for (JMySlider s : sliders.values()){
			s.setEnabled(true);
		}
		for (Integer key : lockers.keySet()){
			if (!lockers.get(key).isSelected()){
				continue;
			}
			switch (key.intValue()){
			case 0:
				sliders.get(1).setEnabled(false);
				sliders.get(4).setEnabled(false);
				sliders.get(7).setEnabled(false);
				break;
			case 1:
				sliders.get(2).setEnabled(false);
				sliders.get(5).setEnabled(false);
				sliders.get(8).setEnabled(false);
				break;
			case 2:
				sliders.get(3).setEnabled(false);
				sliders.get(6).setEnabled(false);
				sliders.get(9).setEnabled(false);
				break;
			case 3:
				sliders.get(1).setEnabled(false);
				sliders.get(2).setEnabled(false);
				sliders.get(3).setEnabled(false);
				break;
			case 4:
				sliders.get(4).setEnabled(false);
				sliders.get(5).setEnabled(false);
				sliders.get(6).setEnabled(false);
				break;
			case 5:
				sliders.get(7).setEnabled(false);
				sliders.get(8).setEnabled(false);
				sliders.get(9).setEnabled(false);
				break;
			}
		}
	}
	public void createMultiEllipsoidGroup() throws FileNotFoundException, JDOMException, IOException{
		final JFileChooser fc = new JFileChooser();
		fc.setDialogTitle("Select ellipsoid configure files...");
		fc.setSelectedFile(new File(lastFolder));
		fc.setFileFilter(new EllipsoidFileFilter());
		fc.setMultiSelectionEnabled(true);
		//In response to a button click:
		int returnVal = fc.showOpenDialog(Displayer.this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File[] files = fc.getSelectedFiles();
			if (files!=null){
				if ((this.multi_ellipsoidBranch!=null)&&(this.showmulti_ellipsoidBranch)){
					this.transformGroup_axis.removeChild(this.multi_ellipsoidBranch);
				}
				this.multi_ellipsoidBranch = new BranchGroup();
				this.multi_ellipsoidBranch.setCapability(BranchGroup.ALLOW_DETACH);
				for (File file : files){
					setLastFolder(file.getAbsolutePath());
					if (file.getAbsolutePath().endsWith(Const.ellipsoid)){
					Ellipsoid e = new Ellipsoid(file.getAbsolutePath());
					float offset_x = e.getOffset_x();
					float offset_y = e.getOffset_y();
					float offset_z = e.getOffset_z();
					float a = e.getA();
					float b = e.getB();
					float c = e.getC();
					float rotate_x = e.getRotate_x();
					float rotate_y = e.getRotate_y();
					float rotate_z = e.getRotate_z();
					EllipsoidGroup ellipsoidGroup = new EllipsoidGroup(maxLength, a, b, c, 20, 20, 0, 
							offset_x, offset_y, offset_z, rotate_x, rotate_y, rotate_z, this.is3D);
					multi_ellipsoidBranch.addChild(ellipsoidGroup);
					}
				}
				if (this.showmulti_ellipsoidBranch){
					this.transformGroup_axis.addChild(multi_ellipsoidBranch);
				}
			}
		}
	}
	public void createEllipsoidGroup() {
		if ((ellipsoidBranch!=null)&&(this.showSelection)){
			transformGroup_axis.removeChild(ellipsoidBranch);
		}
		ellipsoidBranch = new BranchGroup();
		ellipsoidBranch.setCapability(BranchGroup.ALLOW_DETACH);
		float offset_x = (float)sliders.get(1).getValue()/(float)scale;
		float offset_y = (float)sliders.get(2).getValue()/(float)scale;
		float offset_z = (float)sliders.get(3).getValue()/(float)scale;
		float a = (float)sliders.get(4).getValue()/(float)scale;
		float b = (float)sliders.get(5).getValue()/(float)scale;
		float c = (float)sliders.get(6).getValue()/(float)scale;
		float rotate_x = (float)sliders.get(7).getValue() * (float)(Math.PI/180f);
		float rotate_y = (float)sliders.get(8).getValue() * (float)(Math.PI/180f);
		float rotate_z = (float)sliders.get(9).getValue() * (float)(Math.PI/180f);
		ellipsoidGroup = new EllipsoidGroup(maxLength, a, b, c, 20, 20, 0, 
				offset_x, offset_y, offset_z, rotate_x, rotate_y, rotate_z, this.is3D);
		ellipsoidBranch.addChild(ellipsoidGroup);
		if (this.showSelection){
			transformGroup_axis.addChild(ellipsoidBranch);
		}
		
	}
	public float getMaxLength(){
		return this.maxLength;
	}
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	/**
	 * @throws IOException 
	 * 
	 */
	public void saveEllipsoid() throws IOException {
		if (this.ellipsoidGroup==null){
			ShowAlert(Message.getString("no_selection"));
			return;
		}
		final JFileChooser fc = new JFileChooser();
		fc.setDialogTitle(Message.getString("select_selection"));
		fc.setSelectedFile(new File(lastFolder));
		fc.setFileFilter(new EllipsoidFileFilter());
		//In response to a button click:
		int returnVal = fc.showSaveDialog(Displayer.this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			String filename = file.getAbsolutePath();
			if (!filename.endsWith("." + Const.ellipsoid)){
				filename += "." + Const.ellipsoid;
			}
			
	        try {
				this.ellipsoidGroup.getEllipsoid().save(filename);
			} catch (IllegalSelectionException e) {
				ShowAlert(Message.getString("cannot_save_selection"));
			}
			setLastFolder(filename);
		}
		
	}
	/**
	 * @throws IOException 
	 * @throws JDOMException 
	 * @throws FileNotFoundException 
	 * 
	 */
	public void openEllipsoid() throws FileNotFoundException, JDOMException, IOException {
		final JFileChooser fc = new JFileChooser();
		fc.setDialogTitle(Message.getString("open_ellipsoid"));
		fc.setSelectedFile(new File(lastFolder));
		fc.setFileFilter(new EllipsoidFileFilter());
		//In response to a button click:
		int returnVal = fc.showOpenDialog(Displayer.this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			String filename = file.getAbsolutePath();
			Ellipsoid e = new Ellipsoid(filename);
			if (!e.isEllipsoidType()){
				this.sliders.get(1).setValue((int)(e.getOffset_x() * scale));
				this.sliders.get(2).setValue((int)(e.getOffset_y() * scale));
				this.sliders.get(3).setValue((int)(e.getOffset_z() * scale));
				this.sliders.get(4).setValue((int)(e.getA() * scale));
				this.sliders.get(5).setValue((int)(e.getB() * scale));
				this.sliders.get(6).setValue((int)(e.getC() * scale));
				this.sliders.get(7).setValue((int)(e.getRotate_x() / (float)(Math.PI/180f)));
				this.sliders.get(8).setValue((int)(e.getRotate_y() / (float)(Math.PI/180f)));
				this.sliders.get(9).setValue((int)(e.getRotate_z() / (float)(Math.PI/180f)));
			}
			setLastFolder(filename);
		}
		
	}
	/**
	 * @param vp
	 */
	public void designBarriers(String[] vplist) {
		if (backgroundValues==null){
			ShowAlert(Message.getString("nobackground_alert"));
			return;
		}
		
		if (vplist.length==0){
			ShowAlert(Message.getString("noengouth_species_dataset1"));
		}else{
			if (vplist.length>0){
				String folder = (String) JOptionPane.showInputDialog(this,
						Message.getString("select_a_species"),vplist[0], JOptionPane.INFORMATION_MESSAGE, 
						null, vplist, vplist[0]);
				if (folder==null){
					return;
				}
				try {
					Barrier barrier;
					barrier = new Barrier(folder, this.backgroundTiff, 
							theMenu.getVirtualspecies().get(folder).getVs());
					barrier.setVisible(true);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		}
		
	}
	public BranchGroup getMulti_ellipsoidBranch() {
		return multi_ellipsoidBranch;
	}
	public String getLastFolder() {
		return lastFolder;
	}
	public void setLastFolder(String lastFolder) {
		this.lastFolder = lastFolder;
		ConfigInfo.getInstance().setProperty("lastFolder", lastFolder);
	}
	/**
	 * 
	 */
	public void generatePCA() {
		PCAGeneratorForm form = new PCAGeneratorForm(this);
		form.setVisible(true);
		if (form.getTarget()!=null){
			this.progressMonitor = new ProgressMonitor(this, "", "", 0, 100);
			ArrayList<String> filenames_origin = new ArrayList<String>();
			for (File file : form.getSelectFiles_origin()){
				filenames_origin.add(file.getAbsolutePath());
			}
			
			ArrayList<String> filenames_target = new ArrayList<String>();
			for (File file : form.getSelectFiles_target()){
				filenames_target.add(file.getAbsolutePath());
			}
			
			setLastFolder(form.getTarget());		
			this.progressMonitor = new ProgressMonitor(this, "", "", 0, 100);
			PCAGenerator pcageneragor = new PCAGenerator(filenames_origin,filenames_target, form.getTarget(), form.isTrans());
			pcageneragor.addPropertyChangeListener(this);
			pcageneragor.execute();
		}
		
	}
	/**
	 * @param vp1
	 * @param vp2
	 */
	private StringBuilder overlapResult;
	private StringBuilder overlapResultTable;
	private ArrayList<SpeciesDataset[]> overlapSpeciesDataSets;
	private OverlapObject overlapObject;
	private boolean overlap_isExist(String vs1, String vs2){
		boolean is_exist = false;
		for (SpeciesDataset[] dataset : overlapSpeciesDataSets){
			if (((dataset[0].getLabel().equalsIgnoreCase(vs1))&&(dataset[1].getLabel().equalsIgnoreCase(vs2)))||
				((dataset[0].getLabel().equalsIgnoreCase(vs2))&&(dataset[1].getLabel().equalsIgnoreCase(vs1)))){
				is_exist = true;
				break;
			}
		}
		return is_exist;
	}
	public void calculateOverlap(TreeMap<String, IDMenuItem> vps, double precision, 
			String[] vs1_label, String[] vs2_label, String overlap_method, String target) {
		overlapObject = new OverlapObject(precision, overlap_method);
		overlapResult = new StringBuilder();
		overlapResultTable = new StringBuilder();
		overlapResultTable.append("VS1_Label,VS1_MVE_Volume_formula,VS1_MVE_Volume_estimated," +
				"VS1_ConvexHull_Volume_formula,VS1_ConvexHull_Volume_estimated," +
				"VS2_Label,VS2_MVE_Volume_formula,VS2_MVE_Volume_estimated," +
				"VS2_ConvexHull_Volume_formula,VS2_ConvexHull_Volume_estimated," +
				"Overlap_MVE,Overlap_ConvexHull" + Const.LineBreak);
		overlapSpeciesDataSets = new ArrayList<SpeciesDataset[]>();
		for (String vs1 : vs1_label){
			for (String vs2 : vs2_label){
				if (vs1.equalsIgnoreCase(vs2)){
					continue;
				}
				if (!overlap_isExist(vs1, vs2)){
					SpeciesDataset[] dataset = {vps.get(vs1).getVs(), vps.get(vs2).getVs()};
					overlapSpeciesDataSets.add(dataset);
				}
			}
		}
		handleOverlapQueue(target);
		
		
	}
	private void handleOverlapQueue(String target) {
		if (this.overlapSpeciesDataSets.size()>0){
			this.progressMonitor = new ProgressMonitor(this, "", "", 0, 100);
			EllipsoidOverlapCalculator calculator = 
					new EllipsoidOverlapCalculator(
							overlapSpeciesDataSets.get(0)[0], overlapSpeciesDataSets.get(0)[1], 
							overlapObject.getPrecision(), 
							this.is3D, overlapObject.getOverlap_method(), target);
			calculator.addPropertyChangeListener(this);
			calculator.execute();
			this.overlapSpeciesDataSets.remove(0);
		}else{
			overlapResult.insert(0, Message.getString("html_head") + Const.LineBreak);
        	
        	overlapResult.append("<h4>Result</h4>");
        	overlapResult.append("<table border=1>");
        	
        	for (String s : overlapResultTable.toString().split(Const.LineBreak)){
        		if (s.startsWith("VS1_Label")){
        			overlapResult.append("<tr><th>" + s.replace(",", "</th><th>") + "</th></tr>");
        		}else{
        			overlapResult.append("<tr><td>" + s.replace(",", "</td><td>") + "</td></tr>");
        		}
        	}
        	overlapResult.append("</table>");
        	overlapResult.append(Message.getString("html_tail") + Const.LineBreak);
//			LogForm dialog = new LogForm(Message.getString("application.title"), overlapResult, overlapResultTable, this);
        	
        	try {
        		File html = new File(target + "/overlap_result.html");
            	CommonFun.writeFile(overlapResult.toString(), html.getAbsolutePath());
            	CommonFun.writeFile(overlapResultTable.toString(), target + "/overlap_result.csv");
				HTMLViewer viewer = new HTMLViewer(html, Message.getString("calculate_overlap"), 800, 800, true);
				viewer.setVisible(true);
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	/**
	 * 
	 */
	public void generateSpeciesDataset() {
		if (backgroundValues==null){
			ShowAlert(Message.getString("nobackground_alert"));
			return;
		}
		
		GenerateSDSForm form = new GenerateSDSForm(this, 0);
		form.setVisible(true);
		if (form.getTarget()==null){
			return;
		}
		this.progressMonitor = new ProgressMonitor(this, "", "", 0, 100);
		SpeciesGenerator generator = new SpeciesGenerator(form.getLL(), form.getTarget(), 
				backgroundValues, backgroundTiff, 0);
		generator.addPropertyChangeListener(this);
		generator.execute();
		
		
		
		
	}
	/**
	 * 
	 */
	public void rasteConvert() {
		FormatConvertorForm form = new FormatConvertorForm(this);
		form.setVisible(true);
		if (form.getTarget()!=null){
			this.progressMonitor = new ProgressMonitor(this, "", "", 0, 100);
			FormatConvertor generator = new FormatConvertor(form.getSelectFiles(), form.getTarget(), form.getTargetFormat());
			generator.addPropertyChangeListener(this);
			generator.execute();
		}
	}
	/**
	 * @throws IOException 
	 * 
	 */
	public void export(String[] vplist) throws IOException {
		if (this.backgroupdBranch==null){
			ShowAlert(Message.getString("nobackground_alert"));
			return;
		}
		
		if (vplist.length==0){
			ShowAlert(Message.getString("noengouth_species_dataset1"));
		}else{
			if (vplist.length>0){
				String[] enms = new String[]{
						Message.getString("openModeller"), 
						Message.getString("Maxent"),
						Message.getString("BIOMOD2"),
						Message.getString("dismo"),
						Message.getString("mkde")};
				String[] methods = new String[]{
						Message.getString("threshold"), 
						Message.getString("probability")};
				ExportForm exportForm = new ExportForm(vplist, enms, methods, this);
				exportForm.setVisible(true);
				if (exportForm.getTarget()!=null){
					String enm = exportForm.getEnm();
					String species_label = exportForm.getSpecies_label();
					SpeciesDataset vp = this.theMenu.getVirtualspecies().get(exportForm.getVp()).getVs();
					int repeat = exportForm.getRepeats();
					int number = exportForm.getNumber();
					String target = exportForm.getTarget();
					double beta = exportForm.getBeta();
					double alpha = exportForm.getAlpha();
					this.progressMonitor = new ProgressMonitor(this, "", "", 0, 100);
					ExportToENMWorker generator = new ExportToENMWorker(
							enm, species_label, vp, repeat, number, beta, alpha, 
							target,exportForm.getMethod(),  backgroundTiff, this);
					generator.addPropertyChangeListener(this);
					generator.execute();
				}
			}
		}
		
	}
	/**
	 * @param vplist
	 */
	public void CalculateOverlap(String[] vplist) {
		OverlapSelectionForm form = new OverlapSelectionForm(vplist, this);
		form.setVisible(true);
		if (form.getVs1()!=null){
			calculateOverlap(this.theMenu.getVirtualspecies(),
					form.getPrecision(), form.getVs1(), form.getVs2(), 
					form.getOverlap_method(), form.getTarget());
//			calculateOverlap(this.theMenu.getVirtualspecies().get(form.getVs1()).getVs(), 
//					this.theMenu.getVirtualspecies().get(form.getVs2()).getVs(), 
//					form.getPrecision(), form.getMaxsteps(), form.getVs1(), form.getVs2(), 
//					form.getOverlap_method());	
		}
	}
	/**
	 * @throws IOException 
	 * 
	 */
	public void closeSpecies(String[] vplist) throws IOException {
		SDSListForm form = new SDSListForm(vplist, this);
		form.setVisible(true);
		if (form.getSelectVs()!=null){
			if (form.getSelectVs().size()>0){
				//0: close 1:export map 2: export mve
				switch (form.getCommand()){
					case 0:
						int n = JOptionPane.showConfirmDialog(
							    this,
							    Message.getString("is_clear_SDS"),
							    Message.getString("application.title"),
							    JOptionPane.YES_NO_OPTION);
						if (n == JOptionPane.YES_OPTION){
							for (String key :  form.getSelectVs()){
								clearVS(key);
							}
						}
						break;
					case 1:
						NicheMapGenerator generator = new NicheMapGenerator(form.getFolder(), form.getSelectVs(), 
								this.backgroundValues, this.backgroundTiff, this.theMenu.getVirtualspecies());
						generator.addPropertyChangeListener(this);
						generator.execute();
						break;
					case 2:
						for (String key :  form.getSelectVs()){
							saveMVE(key, form.getFolder());
							ShowAlert(Message.getString("done"));
						}
						break;
					default:
				}
				
			}
		}
		
	}
	private void saveMVE(String key, String folder) throws IOException {
		String filename = CommonFun.getFileNameWithoutPathAndExtension(key);
		MinimumVolumeEllipsoidResult mve = this.theMenu.getVirtualspecies().get(key).getVs().getMve();
		CommonFun.writeFile(mve.toString(), folder + "/" + filename + ".mve");
	}
	/**
	 * 
	 */
	public void clearAll() {
		int n = JOptionPane.showConfirmDialog(
			    this,
			    Message.getString("is_clear"),
			    Message.getString("application.title"),
			    JOptionPane.YES_NO_OPTION);
		if (n == JOptionPane.YES_OPTION){
			this.backgroundTiff = null;
			this.backgroundValues = null;
			if (this.backgroupdBranch!=null){
				if (this.showBackground){
					this.transformGroup_axis.removeChild(this.backgroupdBranch);
				}
				this.backgroupdBranch = null;
			}
			ArrayList<String> keys = new ArrayList<String>();
			for (String key : this.mveBranches.keySet()){
				keys.add(key);
			}
			for (String key : keys){
				clearVS(key);
			}
		}
		
	}
	
	/**
	 * @param key
	 */
	private void clearVS(String key) {
		if (showPoint){
			this.transformGroup_axis.removeChild(pointBranches.get(key));
		}
		pointBranches.remove(key);
		
		if (showConvexHull){
			this.transformGroup_axis.removeChild(convexHullBranches.get(key));
		}
		convexHullBranches.remove(key);
		
		if (showMVE){
			this.transformGroup_axis.removeChild(mveBranches.get(key));
		}
		mveBranches.remove(key);
		gSpaceDatasets.remove(key);
		vpStatus.remove(key);
		vpMVE.remove(key);
		this.theMenu.clearVS(key);
		
	}
	public HashMap<String, GSpaceData> getgSpaceDatasets() {
		return gSpaceDatasets;
	}
	public Menu getTheMenu() {
		return theMenu;
	}
	/**
	 * @throws Exception 
	 * 
	 */
	public void ma() throws Exception {
		final JFileChooser fc = new JFileChooser();
		
		fc.setSelectedFile(new File(lastFolder));
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		//In response to a button click:
		int returnVal = fc.showOpenDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
	        String filename = file.getAbsolutePath();
	        MarbleAlgorithm ma = new MarbleAlgorithm(filename, this.backgroundValues, this.backgroundTiff);
	        ma.buildCluster();
	        if (this.maBranch!=null){
				this.transformGroup_axis.removeChild(this.maBranch);
			}
			this.maBranch = new BranchGroup();
			this.maBranch.setCapability(BranchGroup.ALLOW_DETACH);
			
			GeoTiffObject result = new GeoTiffObject(ma.getOutputFileName());
			int xsize = result.getXSize();
			int ysize = result.getYSize();
			HashMap<Integer, Color3f> colors = new HashMap<Integer, Color3f>();
			colors.put(0, Color3D.blue);
			colors.put(1, Color3D.green);
			colors.put(2, Color3D.yellow);
			colors.put(3, Color3D.cyan);
			colors.put(4, Color3D.skyBlue);
			colors.put(5, Color3D.magenta);
			
			for (int y = 0;y<ysize;y++){
				for (int x = 0;x<xsize;x++){
					if (Math.random()>0.00066667){
						continue;
					}
					int value = (int)result.readByXY(x, y);
					SpeciesData speciesData = backgroundValues.get(String.format("%d,%d", x, y));
					if (speciesData ==null){
						continue;
					}
					if (value==-1){
						TransformGroup ot = new TransformGroup();
						Transform3D t = new Transform3D();
						t.setTranslation(new Vector3f((float)speciesData.getValues()[0]/maxLength, 
								(float)speciesData.getValues()[1]/maxLength, (float)speciesData.getValues()[2]/maxLength));
						ot.setTransform(t);
						Dot dot = new Dot(Color3D.red, 0.001f);
						dot.setCapability(Shape3D.ALLOW_BOUNDS_READ);
						dot.setCapability(Shape3D.ALLOW_BOUNDS_WRITE);
						dot.setCapability(Shape3D.ALLOW_LOCAL_TO_VWORLD_READ);
						ot.addChild(dot);
						maBranch.addChild(ot);
					}
					if (value>=0){
						TransformGroup ot = new TransformGroup();
						Transform3D t = new Transform3D();
						t.setTranslation(new Vector3f((float)speciesData.getValues()[0]/maxLength, 
								(float)speciesData.getValues()[1]/maxLength, (float)speciesData.getValues()[2]/maxLength));
						ot.setTransform(t);
						Dot dot = new Dot(Color3D.green, 0.001f);
						dot.setCapability(Shape3D.ALLOW_BOUNDS_READ);
						dot.setCapability(Shape3D.ALLOW_BOUNDS_WRITE);
						dot.setCapability(Shape3D.ALLOW_LOCAL_TO_VWORLD_READ);
						ot.addChild(dot);
						maBranch.addChild(ot);
					}
					
				}
			}
			transformGroup_axis.addChild(maBranch);
			result.release();
		}
		
	}
	/**
	 * @throws IOException 
	 * 
	 */
	public void troubleshooting() throws IOException {
		final JFileChooser fc = new JFileChooser();
		fc.setDialogTitle(Message.getString("test_tiff_file"));
		fc.setSelectedFile(new File(lastFolder));
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fc.setFileFilter(new RasterFileFilter());
		//In response to a button click:
		int returnVal = fc.showOpenDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			
			File file = fc.getSelectedFile();
			String temp_folder = ConfigInfo.getInstance().getTemp();
			CommonFun.copyFile(file.getAbsolutePath(), temp_folder + "/" + file.getName());
			
	        String filename = file.getAbsolutePath();
	        setLastFolder(filename);
	        filename = temp_folder + "/" + file.getName();
	        StringBuilder sb = new StringBuilder();
	        sb.append(Message.getString("html_head"));
			sb.append("<h4>Check gdal library.</h4>" + Const.LineBreak);
			sb.append("<li>System Libraries Path is :<font color='red'>" + System.getProperty("java.library.path") + "</font></li>" + Const.LineBreak);
			try{
				gdal.AllRegister();
				boolean isTiff = false;
				boolean isASC = false;
				for (int i=0;i<gdal.GetDriverCount();i++){
					if (gdal.GetDriver(i).getLongName().equalsIgnoreCase("GeoTIFF")){
						isTiff = true;
					}
					if (gdal.GetDriver(i).getLongName().equalsIgnoreCase("Arc/Info ASCII Grid")){
						isASC = true;
					}
				}
				String temp = (isTiff)?"TRUE":"FALSE";
				sb.append("<li>GeoTIFF support: <font color='red'>" + temp + "</font></li>" + Const.LineBreak);
				temp = (isASC)?"TRUE":"FALSE";
				sb.append("<li>Arc/Info ASCII Grid support: <font color='red'>" + temp + "</font></li>" + Const.LineBreak);
				//sb.append("-------------------*******************-------------------" + Const.LineBreak + Const.LineBreak);
				sb.append("<h4>Read test file information</h4>");
				sb.append("<li>Open GeoTIFF file <font color='red'>" + filename+ "</font></li>" + Const.LineBreak);
				
				try {
					GeoTiffObject geo = new GeoTiffObject(filename);
					sb.append("<li>Get information <font color='red'>" + filename+ "</font></li>" + Const.LineBreak);
					GDALInfoObject info = new GDALInfoObject(filename);
					sb.append(info.getHTMLInfo().toString());
					geo.release();
				} catch (FileNotFoundException e) {
					sb.append("<li><font color='red'>Cannot open file '" + filename + "'.</font></li> Because of " + Const.LineBreak);
					sb.append(CommonFun.toHTML(e.getMessage()) + Const.LineBreak);
				}
//				sb.append("-------------------*******************-------------------" + Const.LineBreak + Const.LineBreak);
				
				sb.append("<h4>Convert GeoTIFF to ASCII file.</h4>" + Const.LineBreak);
				String command = GeoTiffController.toAAIGrid(filename, filename + ".asc");
				sb.append("<li>Execute command: <br>" + Const.LineBreak + "   " + command + "</li>" + Const.LineBreak);
				
				File f = new File(filename + ".asc");
				if (f.exists()){
					sb.append("<li>ASCII file was created.</li>" + Const.LineBreak);
					sb.append("<h4>Convert ASCII back to GeoTIFF file.</h4>" + Const.LineBreak);
					command = GeoTiffController.toGeoTIFF(f.getAbsolutePath(), f.getAbsolutePath() + ".tiff");
					sb.append("<li>Execute command: <br>" + Const.LineBreak + "   " + command + "</li>" + Const.LineBreak);
					
					File f2 = new File(f.getAbsolutePath() + ".tiff");
					if (f2.exists()){
						sb.append("<li>GeoTIFF file is created.</li>" + Const.LineBreak);
						sb.append("<h4>Open the created GeoTIFF file.</h4>" + Const.LineBreak);
						try {
							GeoTiffObject geo = new GeoTiffObject(f2.getAbsolutePath());
							sb.append("<li>Get information  <font color='red'>" + f2.getAbsolutePath()+ "</font></li>" + Const.LineBreak);
							GDALInfoObject info = new GDALInfoObject(f2.getAbsolutePath());
							sb.append(info.getHTMLInfo().toString());
							geo.release();
						} catch (FileNotFoundException e) {
							sb.append("<li><font color='red'>Cannot open file '" + f2.getAbsolutePath() + "'.</font></li>  Because of " + Const.LineBreak);
							sb.append(CommonFun.toHTML(e.getMessage()) + Const.LineBreak);
						}
						
					}else{
						sb.append("<li><font color='red'>Fail to create GeoTIFF file from ASCII file.</font></li>" + Const.LineBreak);
					}
				}else{
					sb.append("<li><font color='red'>Fail to create ASCII file from GeoTIFF file.</font></li>" + Const.LineBreak);
				}
//				sb.append("-------------------*******************-------------------" + Const.LineBreak + Const.LineBreak);
				
				sb.append("<h4>Convert GeoTIFF to PNG file.</h4>" + Const.LineBreak);
				command = GeoTiffController.toPNG(filename, filename + ".png");
				sb.append("<li>Execute command:<br>" + Const.LineBreak + "   " + command + "</li>" + Const.LineBreak);
				sb.append("<li>If you see an image below, PNG file was created, or you need to check the ImageMagick 'convert' command.</li>" + Const.LineBreak);
				sb.append("<img src='file://localhost/" + filename + ".png'/>");
				sb.append("<h4>Resize PNG file <font color='red'>" + filename + ".png" + ".</font></h4>" + Const.LineBreak);
				try {
					command = GeoTiffController.resizePNG(filename + ".png", filename + "_resized.png", 1000);
					sb.append("<li>Execute command:<br>" + Const.LineBreak + "   " + command + "</li>" + Const.LineBreak);
					sb.append("<li>If you see an image below, PNG file '" + filename + ".png" + "' was resized to file '" + filename + ".resized.png" + "'.</li>" + Const.LineBreak);
//					sb.append("<li>Please check both of these two files manually.</li>" + Const.LineBreak);
					sb.append("<img src='file://localhost/" + filename + "_resized.png'/>");
					
				} catch (IOException e) {
					sb.append("<li><font color='red'>Cannot do this process. Because of </font></li>" + Const.LineBreak);
					sb.append(CommonFun.toHTML(e.getMessage()) + Const.LineBreak);
				} catch (InterruptedException e) {
					sb.append("<li><font color='red'>Cannot do this process. Because of </font></li>" + Const.LineBreak);
					sb.append(CommonFun.toHTML(e.getMessage()) + Const.LineBreak);
				}
				
				sb.append("<h4>Check R runable environment.</h4>" + Const.LineBreak);
				
				sb.append("<li>NicheA is trying to run the following R script.</li>" + Const.LineBreak);
				
				InputStream rscript = this.getClass().getResourceAsStream("/troubleshoot.r");
				HashMap<String, String> parameters = new HashMap<String, String>();
				parameters.put("@Target", temp_folder.replace("\\", "/"));
				
				
				HashSet<String> libraries = new HashSet<String>();
				libraries.add("ggplot2");
				libraries.add("plyr");
				libraries.add("scales");
				libraries.add("grid");
				sb.append("<div style=\"background-color:#EEEEEE; border-radius:10px; border: 1px black solid;\">");
				String r_script = CommonFun.RunRScript(rscript, parameters, libraries, true, null);
				sb.append(CommonFun.toHTML(r_script));
				sb.append("</div>");
				sb.append("<li>If you see a diagram with black arrow from bottom-left to top-right, " +
						"and a diagram with three colors curves, it means R works on your computer, " +
						"or you need to check the R runtime environment.</li>" + Const.LineBreak);
				sb.append("<li>NicheA needs install the following packages in R. Please check them.</li>" + Const.LineBreak);
				sb.append("<ul>");
				for (String library : libraries){
					sb.append("<li>" + library + "</li>");
				}
				sb.append("</ul>" + Const.LineBreak);
				
				sb.append("<img width=800 src='file://localhost/" + temp_folder + "/fig1.png'/>");
				sb.append("<img width=800 src='file://localhost/" + temp_folder + "/fig2.png'/>");
				
				
			}catch(UnsatisfiedLinkError e){
				e.printStackTrace();
//				sb.append(e.getMessage());
				
				sb.append("<li><font color='red'>Fail to find gdal library.</font></li>" + Const.LineBreak);
				sb.append(CommonFun.toHTML(ExceptionUtils.getStackTrace(e)));
			}
//			sb.append("-------------------*******************-------------------" + Const.LineBreak + Const.LineBreak);
			sb.append(Message.getString("html_tail"));
			File f = new File(temp_folder + "/troubleshoot.html");
			try {
				CommonFun.writeFile(sb.toString(), f.getAbsolutePath());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			try {
				HTMLViewer dialog;
				dialog = new HTMLViewer(f, Message.getString("application.title"), 800, 800, false);
				dialog.setVisible(true);
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		
	}
	/**
	 * 
	 */
	public void show_species_dataset_attribute() {
		SDSAttributeForm form = new SDSAttributeForm(this.theMenu.getVirtualspecies(), this);
		
	}
	/**
	 * 
	 */
	public void stat_variables() {
		VariablesStatForm form = new VariablesStatForm(this);
		form.setVisible(true);
		if (form.getTarget()!=null){
			this.progressMonitor = new ProgressMonitor(this, "", "", 0, 100);
			VariablesStator variablesStator = new VariablesStator(
					this, form.getSelectFiles(), form.getTarget(), 
					form.getAveragePercentiles(), form.getNodata(),
					form.getStep());
			variablesStator.addPropertyChangeListener(this);
			variablesStator.execute();
		}
	}
	/**
	 * 
	 */
	public void multivarnormal() {
		MultiVarMappingForm form = new MultiVarMappingForm(this);
		form.setVisible(true);
		if (form.getTarget()!=null){
			this.progressMonitor = new ProgressMonitor(this, "", "", 0, 100);
			MultiVarNormalDistributionGenerator generator = new MultiVarNormalDistributionGenerator(
					this, form.getSelectFiles(), form.getMaxcountText(), form.getMaxTry(), form.getPrecision(), form.getNodata(),
					form.isIsnodata(), form.getTarget(), form.getParameterTarget());
			generator.addPropertyChangeListener(this);
			generator.execute();
		}
		
	}
	public void design_curve() {
		VariableGeneratorForm form = new VariableGeneratorForm(this);
		form.setVisible(true);
		if (form.getTarget()!=null){
			this.progressMonitor = new ProgressMonitor(this, "", "", 0, 100);
			VariableGenerator generator = new VariableGenerator(
					form.getGlacial(), form.getInterglacial(), form.getTarget(), form.getParameters());
			generator.addPropertyChangeListener(this);
			generator.execute();
		}
		
	}
	public void niche_breadth() {
		NicheBreadthForm form = new NicheBreadthForm(this);
		form.setVisible(true);
		if (form.getTarget()!=null){
			this.progressMonitor = new ProgressMonitor(this, "", "", 0, 100);
			NicheBreadthProcessor generator = new NicheBreadthProcessor(this, form.getTarget(), form.getParameters());
			generator.addPropertyChangeListener(this);
			generator.execute();
		}
		
	}
	public void threshold_calculate() {
		ThresholdCalculateForm form = new ThresholdCalculateForm(this);
		form.setVisible(true);
	}
	public HashMap<String, SpeciesData> getBackgroundValues() {
		return this.backgroundValues;
	}
	public void addBackgroundValues(String key, SpeciesData data){
		this.backgroundValues.put(key, data);
	}
	public void readMultiFile() {
		MultiGeoLayerReaderForm form = new MultiGeoLayerReaderForm(this);
		form.setVisible(true);
		if (form.getTarget()!=null){
			this.progressMonitor = new ProgressMonitor(this, "", "", 0, 100);
			ArrayList<String> filenames = new ArrayList<String>();
			for (File file : form.getSelectFiles()){
				filenames.add(file.getAbsolutePath());
			}
			MultiGeoLayerReaderProcessor generator = new MultiGeoLayerReaderProcessor(this, filenames, form.getTarget(), form.getLL());
			generator.addPropertyChangeListener(this);
			generator.execute();
		}
		
	}
	public void variableStandardization() {
		VariableStandardizationForm form = new VariableStandardizationForm(this);
		form.setVisible(true);
		if (form.getTarget()!=null){
			this.progressMonitor = new ProgressMonitor(this, "", "", 0, 100);
			ArrayList<String> filenames = new ArrayList<String>();
			for (File file : form.getSelectFiles()){
				filenames.add(file.getAbsolutePath());
			}
			VariableStandardizationProcessor generator = new VariableStandardizationProcessor(
					this, filenames, form.getTarget(), form.getStandardizationMethods(), form.is_individually());
			generator.addPropertyChangeListener(this);
			generator.execute();
		}
		
	}
	public void setNODATA() {
		SetNodataGeoLayerForm form = new SetNodataGeoLayerForm(this);
		form.setVisible(true);
		if (form.getTarget()!=null){
			this.progressMonitor = new ProgressMonitor(this, "", "", 0, 100);
			ArrayList<String> filenames = new ArrayList<String>();
			for (File file : form.getSelectFiles()){
				filenames.add(file.getAbsolutePath());
			}
			SetNodataGeoLayerProcessor generator = new SetNodataGeoLayerProcessor(this, filenames, form.getTarget(), form.getNodata());
			generator.addPropertyChangeListener(this);
			generator.execute();
		}
		
	}
	public void threshold_variables() throws IOException, SAXException {
		ModelThresholdForm form = new ModelThresholdForm(this);
		form.setVisible(true);
		if (form.getTarget()!=null){
			this.progressMonitor = new ProgressMonitor(this, "", "", 0, 100);
			String[] environmentalLayers = new String[form.getSelectFiles().length];
			for (int i=0; i<form.getSelectFiles().length; i++){
				environmentalLayers[i] = form.getSelectFiles()[i].getAbsolutePath();
			}
			ArrayList<String> lls = CommonFun.readFromFile(form.getOccurrences());
			ArrayList<double[]> occ = new ArrayList<double[]>();
			for (String ll : lls){
				String[] llsplit = ll.replace("\t", ",").split(",");
				if (llsplit.length==2){
					if (CommonFun.isDouble(llsplit[0])&&(CommonFun.isDouble(llsplit[1]))){
						double[] o = {Double.valueOf(llsplit[0]).doubleValue(), Double.valueOf(llsplit[1]).doubleValue()};
						occ.add(o);
					}
				}
				if (llsplit.length>=3){
					if (CommonFun.isDouble(llsplit[1])&&(CommonFun.isDouble(llsplit[2]))){
						double[] o = {Double.valueOf(llsplit[1]).doubleValue(), Double.valueOf(llsplit[2]).doubleValue()};
						occ.add(o);
					}
				}
			}
			double[][] occurrences = new double[occ.size()][2];
			for (int i=0;i<occ.size();i++){
				occurrences[i] = occ.get(i);
			}
			File[] results = form.getResult();
			for (File f : results){
				String modelResult = f.getAbsolutePath();
				
				ModelThresholdGenerator generator = new ModelThresholdGenerator(environmentalLayers, 
						form.getTarget() + "/" + f.getName(), occurrences, modelResult, 
						form.getThresholdMethods());
				generator.addPropertyChangeListener(this);
				generator.execute();
			}
		}
		
	}
	public void generateSpeciesDataset_MVE() {
		if (backgroundValues==null){
			ShowAlert(Message.getString("nobackground_alert"));
			return;
		}
		
		GenerateSDSForm form = new GenerateSDSForm(this, 1);
		form.setVisible(true);
		if (form.getTarget()==null){
			return;
		}
		this.progressMonitor = new ProgressMonitor(this, "", "", 0, 100);
		SpeciesGenerator generator = new SpeciesGenerator(form.getLL(), form.getTarget(), 
				backgroundValues, backgroundTiff, 1);
		generator.addPropertyChangeListener(this);
		generator.execute();
		
	}
	public void manage_workflow() {
		ManageWorkflowDialog form = new ManageWorkflowDialog(this);
		form.setVisible(true);
	}
	public void convert_text_to_raster() throws IOException {
		LL2RasterForm form = new LL2RasterForm(this);
		form.setVisible(true);
		String target = form.getTarget();
		if (target.trim().equals("")){
			return;
		}
		GeoTiffObject mask = new GeoTiffObject(form.getMask());
		String sep = form.getSep();
		ArrayList<String> values = CommonFun.readFromFile(form.getLLFile());
		if (values.size()==0){
			return;
		}
		boolean is_head = form.isColHead();
		String lon_col = form.getLon_Col();
		String lat_col = form.getLat_Col();
		String value_col = form.getValue_Col();
		int lon_index = -1;
		int lat_index = -1;
		int value_index = -1;
		if (is_head){
			String[] heads = values.get(0).split(sep);
			for (int i=0;i<heads.length;i++){
				if (heads[i].equalsIgnoreCase(lon_col)){
					lon_index = i;
				}
				if (heads[i].equalsIgnoreCase(lat_col)){
					lat_index = i;
				}
				if (heads[i].equalsIgnoreCase(value_col)){
					value_index = i;
				}
			}
		}else{
			lon_index = Integer.valueOf(lon_col.replace(Message.getString("col_label"), ""));
			lat_index = Integer.valueOf(lat_col.replace(Message.getString("col_label"), ""));
			value_index = Integer.valueOf(value_col.replace(Message.getString("col_label"), ""));
		}
		if ((lon_index==-1)||(lat_index==-1)||(value_index==-1)){
			ShowAlert(Message.getString("error_col_name"));
			return;
		}
		int max = Math.max(Math.max(lon_index, lat_index), value_index);
		max = max + 1;
		double[] varray = mask.getValueArray();
		for (int i=0;i<varray.length;i++){
			varray[i] = mask.getNoData();
		}
		for (int i=0;i<values.size();i++){
			if ((is_head)&&(i==0)){
				continue;
			}
			String[] vv = values.get(i).split(sep);
			if (vv.length>=max){
				if (CommonFun.isDouble(vv[lon_index])&&
						CommonFun.isDouble(vv[lat_index])&&
						CommonFun.isDouble(vv[value_index])){
					double lon = Double.valueOf(vv[lon_index]);
					double lat = Double.valueOf(vv[lat_index]);
					double value = Double.valueOf(vv[value_index]);
					int[] xy = CommonFun.LLToPosition(mask.getDataset().GetGeoTransform(), new double[]{lon, lat});
					if ((CommonFun.between(xy[0], 0, mask.getXSize()-1))&&CommonFun.between(xy[1], 0, mask.getYSize()-1)){
						varray[xy[1] * mask.getXSize() + xy[0]] = value;
					}
					
				}
			}
		}
		GeoTiffController.createTiff(form.getTarget(), mask.getXSize(), mask.getYSize(), 
				mask.getDataset().GetGeoTransform(), varray, mask.getNoData(), gdalconst.GDT_Float32, mask.getDataset().GetProjection());
		mask.release();
		ShowAlert(Message.getString("done"));
		
	}
	public void refreshWorkflow() {
		this.theMenu.removeWorkflowMenu();
		this.theMenu.addWorkflowMenu();
		
	}
	public void hideShowAxes() {
		if (this.showAxes){
			transformGroup_axis.removeChild(axisBranch);
		}else{
			transformGroup_axis.addChild(axisBranch);
		}
		this.showAxes = !this.showAxes;
		
	}
	public boolean isShowAxesBranch() {
		return showAxes;
	}
	public void partial_roc() throws IOException, SAXException {
		PartialROCForm form = new PartialROCForm(this);
		form.setVisible(true);
		if (form.getTarget().trim().equals("")){
			return;
		}
		
		this.progressMonitor = new ProgressMonitor(this, "", "", 0, 100);
		PartialROCGenerator generator = new PartialROCGenerator(form.getTarget(), form.getOCC(), form.getEs(), form.getSelectFiles());
		generator.addPropertyChangeListener(this);
		generator.execute();
	}
	public void setenmrange() {
		
		SetENMResultRangeForm form = new SetENMResultRangeForm(this);
		form.setVisible(true);
		if (form.getTarget()==null){
			return;
		}
		
		this.progressMonitor = new ProgressMonitor(this, "", "", 0, 100);
		ENMRangeGenerator generator = new ENMRangeGenerator(form.getTarget(), form.getSelectFiles(), form.getFromRange(), form.getToRange());
		generator.addPropertyChangeListener(this);
		generator.execute();
		
	}
	public void aic_calculator() throws IOException {
		AICCalculatorForm form = new AICCalculatorForm(this);
		form.setVisible(true);
		if (form.getTarget()==null){
			return;
		}
		
		this.progressMonitor = new ProgressMonitor(this, "", "", 0, 100);
		AICCalculatorGenerator generator = new AICCalculatorGenerator(form.getTarget(), form.getSelectFiles(), form.getOCC(), form.getK());
		generator.addPropertyChangeListener(this);
		generator.execute();
		
	}
	public void update() {
		try {
			String local_version = Message.getString("version") + " " + Message.getString("subversion"); 
			URL url = new URL(Message.getString("update_url"));
			// read text returned by server
		    BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
		    String line;
		    while ((line = in.readLine()) != null) {
		    	if (local_version.equals(line)){
		    		ShowAlert(String.format(Message.getString("newest_version"), local_version));
		    	}else{
		    		ShowAlert(String.format(Message.getString("need_update"), line, local_version));
		    	}
		    }
		    in.close();
		    
		}
		catch (MalformedURLException e) {
			ShowAlert(Message.getString("check_update_error"));
			System.out.println("Malformed URL: " + e.getMessage());
		}
		catch (IOException e) {
			ShowAlert(Message.getString("check_update_error"));
			System.out.println("I/O Error: " + e.getMessage());
		}
		
	}
	public void setVSVisibility(String[] vplist) {
		if (vplist.length==0){
			ShowAlert(Message.getString("noengouth_species_dataset2"));
		}else{
			VSVisibilityForm form = new VSVisibilityForm(vplist, this, vpStatus);
			form.setVisible(true);
			if (form.getVpStatus()!=null){
				showHideVP(form.getVpStatus());
			}
		}
	}
	
	
}
