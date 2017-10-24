/******************************************************************************
 * Huijie Qiao
 *
 * Project:  Niche Analyst
 * Purpose:  
 * Created date: Apr 10, 2012 4:25:44 PM
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


package org.ku.nicheanalyst.ui.display.dialog;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.TreeMap;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.text.DefaultEditorKit;

import org.ku.nicheanalyst.common.CommonFun;
import org.ku.nicheanalyst.common.Const;
import org.ku.nicheanalyst.common.Message;
import org.ku.nicheanalyst.dataset.SpeciesDataset;
import org.ku.nicheanalyst.ui.display.Displayer;
import org.ku.nicheanalyst.ui.display.component.ui.IDMenuItem;
import org.ku.nicheanalyst.ui.filefilters.CSVFileFilter;

/**
 * @author Huijie Qiao
 *
 */
public class SDSAttributeForm extends JDialog implements ActionListener{
	private TreeMap<String, IDMenuItem> virtualspecies;
	private JTextArea message;
	private JPopupMenu jPopupMenu;
	private Displayer theApp;
    private JPopupMenu getJPopupMenu()  
    {  
        if (jPopupMenu == null)  
        {  
            jPopupMenu = new JPopupMenu();  
            Action copyAction = new AbstractAction(DefaultEditorKit.copyAction)  
            {  
                public void actionPerformed(ActionEvent e)  
                {  
                    // text is your text field  
                	message.copy();  
                }  
            };  
            JMenuItem copy = new JMenuItem(copyAction);  
            jPopupMenu.add(copy);  
        }  
        return jPopupMenu;  
    }
    private String getSubLabel(String label){
		return label.substring(0, 5) + "..." + label.substring(label.length()-10, label.length());
	}
	public SDSAttributeForm(TreeMap<String, IDMenuItem> virtualspecies, Displayer theApp){
		
		super();
		this.theApp = theApp;
		this.virtualspecies = virtualspecies;
        setSize(new Dimension(800, 620));
        getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        JPanel mainpanel = new JPanel();
        mainpanel.setLayout(new BoxLayout(mainpanel, BoxLayout.X_AXIS));
        JPanel vsgroup = new JPanel();
        vsgroup.setLayout(new BoxLayout(vsgroup, BoxLayout.Y_AXIS));
        ButtonGroup buttonGroup = new ButtonGroup();
        for (String key : virtualspecies.keySet()){
        	JRadioButton jr = new JRadioButton(getSubLabel(key));
        	jr.setActionCommand(key);
        	jr.addActionListener(this);
        	buttonGroup.add(jr);
        	vsgroup.add(jr);
        }
        
        
        message = new JTextArea();
        message.addMouseListener(new MouseAdapter()  
        {  
            public void mouseReleased(MouseEvent e)  
            {  
                if (e.isPopupTrigger())  
                {  
                    JPopupMenu popup = getJPopupMenu();  
                    popup.show(message, e.getX(), e.getY());  
                }  
            }  
        });  
        
        mainpanel.add(vsgroup);
        mainpanel.add(message);
        add(mainpanel);
        
        JPanel buttonPanel = new JPanel();
        
        final JButton exportButton = new JButton();
        exportButton.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                try {
					exportButtonPressed();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
            }
        });
        exportButton.setAlignmentY(CENTER_ALIGNMENT);
        exportButton.setAlignmentX(CENTER_ALIGNMENT);
        exportButton.setText(Message.getString("export_all"));
        buttonPanel.add(exportButton);
        
        
        final JButton cancelButton = new JButton();
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                cancelButtonPressed();
            }
        });
        cancelButton.setAlignmentY(CENTER_ALIGNMENT);
        cancelButton.setAlignmentX(CENTER_ALIGNMENT);
        cancelButton.setText(Message.getString("close"));
        buttonPanel.add(cancelButton);
        
        add(buttonPanel);
        setSize(new Dimension(800, 650));
        setModal(true);
        setVisible(true);
	}
	protected void exportButtonPressed() throws IOException {
		JFileChooser fc = new JFileChooser();
		fc.setDialogTitle(Message.getString("select_a_file"));
		fc.setSelectedFile(new File(theApp.getLastFolder()));
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fc.setFileFilter(new CSVFileFilter());
		int returnVal = fc.showSaveDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			String target = fc.getSelectedFile().getAbsolutePath();
			StringBuilder sb = new StringBuilder();
			sb.append("Name,Number_of_points,");
			
			for (int i=0;i<3;i++){
				sb.append(String.format("Min_v%d,Max_v%d,", i + 1, i + 1));
			}
			sb.append("Volume_Convex_Polyhedron,Eigenmatrix_of_MVE,Centra_of_MVE," +
					"Semi_Axis_X_of_MVE,Semi_Axis_Y_of_MVE,Semi_Axis_Z_of_MVE," +
					"Volume_of_MVE" + Const.LineBreak);
			for (String key : this.virtualspecies.keySet()){
				SpeciesDataset sds = this.virtualspecies.get(key).getVs();
				sb.append(String.format("%s,%d,", key, sds.getVs().size()));
				for (int i=0;i<3;i++){
					if (i==2){
						if (sds.isIs3D()){
							sb.append(String.format("%f,%f,", sds.getRanges()[i][0], sds.getRanges()[i][1]));
						}else{
							sb.append(String.format("%.0f,%.0f,", -9999f, -9999f));
						}
					}else{
						sb.append(String.format("%f,%f,", sds.getRanges()[i][0], sds.getRanges()[i][1]));
					}
				}
				sds.getMVEVolume();
				sb.append(String.format("%f,\"%s\",\"%s\",%f,%f,%f,%f%n", sds.getHull_volume(), 
						CommonFun.MatrixtoString_Single_Line(sds.getMve().getA()), 
						CommonFun.MatrixtoString_Single_Line(sds.getMve().getCenter()),
						sds.getMve().getSemi_axis_a(), sds.getMve().getSemi_axis_b(), sds.getMve().getSemi_axis_c(),
						sds.getMVEVolume()));
			}
			CommonFun.writeFile(sb.toString(), target);
			this.theApp.ShowAlert(Message.getString("done"));
		}
		
	}
	private void cancelButtonPressed() {
        this.dispose();
    }
	public void actionPerformed(ActionEvent e) {
	    SpeciesDataset sds = this.virtualspecies.get(e.getActionCommand()).getVs();
	    TreeMap<String, String> info = sds.getInfo(e.getActionCommand());
	    StringBuilder sb = new StringBuilder();
	    for (String key : info.keySet()){
	    	sb.append(String.format("----------- %s -----------%n", key));
	    	sb.append(String.format("%s%n%n", info.get(key)));
	    }
	    message.setText(sb.toString());
	}
}
