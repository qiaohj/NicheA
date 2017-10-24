/******************************************************************************
 * Huijie Qiao
 *
 * Project:  Niche Analyst
 * Purpose:  
 * Created date: Jun 18, 2012 11:53:45 AM
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
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.ProgressMonitor;

import org.ku.nicheanalyst.common.CommonFun;
import org.ku.nicheanalyst.common.Message;
import org.ku.nicheanalyst.dataset.SpeciesData;
import org.ku.nicheanalyst.dataset.SpeciesDataset;
import org.ku.nicheanalyst.maps.controllers.GeoTiffController;
import org.ku.nicheanalyst.maps.objects.GeoTiffObject;
import org.ku.nicheanalyst.ui.display.component.ui.DrawingPanel;
import org.ku.nicheanalyst.ui.display.worker.BarrierSpeciesGenerator;
import org.ku.nicheanalyst.ui.display.worker.VirtualSpeciesGenerator;



/**
 * @author Huijie Qiao
 *
 */
public class Barrier extends JFrame implements PropertyChangeListener {
	private int xsize;
    private String folder;
    private JPanel mainPanel;
    private DrawingPanel drawpanel;
    private BarrierSpeciesGenerator generator;
    private ProgressMonitor progressMonitor;
    private SpeciesDataset vs;
    private String sampleTiff;
	public Barrier(String folder, String sampleTiff, SpeciesDataset vs) throws FileNotFoundException{
		this.vs = vs;
		this.sampleTiff = sampleTiff;
		GeoTiffObject geo = new GeoTiffObject(sampleTiff);
		this.xsize = geo.getXSize();
		this.folder = folder;
		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.LINE_AXIS));
		createDraw();
		
		add(mainPanel);
		
		JPanel blankPanel = new JPanel();
		blankPanel.setLayout(new BoxLayout(blankPanel, BoxLayout.LINE_AXIS));
		
		JButton generateButton = new JButton("Generate");
		generateButton.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
            	try {
					generateButtonPressed();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
            }
        });
		

		
		JButton closeButton = new JButton("Close");
		closeButton.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                closeButtonPressed();
            }
        });
		blankPanel.add(generateButton);
		blankPanel.add(closeButton);
		add(blankPanel);
		setSize(1260, 800);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        geo.release();
	}
	private void createDraw() {
		drawpanel = new DrawingPanel();
		drawpanel.setBackground(Color.black);
		File fff = new File(this.folder);
		File f = null;
		if (fff.isDirectory()){
			f = new File(this.folder + "/present_1000.png");
		}else{
			f = new File(fff.getParent() + "/" + fff.getName().toLowerCase().replace("." + CommonFun.getExtension(fff), ".png"));
			if (!f.exists()){
				GeoTiffController.toPNG(fff.getAbsolutePath(), f.getAbsolutePath());
			}
			f = new File(f.getAbsolutePath().replace(".png", "_1000.png"));
		}
		if (!f.exists()){
			try {
				if (fff.isDirectory()){
					GeoTiffController.resizePNG(this.folder + "/present.png", f.getAbsolutePath(), 1000);
				}else{
					GeoTiffController.resizePNG(f.getAbsolutePath().replace("_1000.png", ".png"), f.getAbsolutePath(), 1000);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		drawpanel.setImage(f.getAbsolutePath());
		mainPanel.add(drawpanel);
	}
	private void generateButtonPressed() throws IOException{
		
		String s = (String)JOptionPane.showInputDialog(
                this,
                "Input the barrier name:", "Virtual species displayer", 
                JOptionPane.PLAIN_MESSAGE,
                null,
                null,
                "barrier");

		//If a string was returned, say so.
		if ((s != null) && (s.length() > 0)) {
			double scale = (double)this.xsize / 1000d;
			ArrayList<Point> points = drawpanel.getPoints();
			ArrayList<Point> barriers = new ArrayList<Point>();
			for (Point p : points){
				Point pp = new Point((int)(p.getX() * scale), (int)(p.getY() * scale));
				barriers.add(pp);
			}
			this.progressMonitor = new ProgressMonitor(this, Message.getString("calculating"), "", 0, 100);
			generator = new BarrierSpeciesGenerator(this.folder, this.vs, this.sampleTiff, s, barriers);
			generator.addPropertyChangeListener(this);
			generator.execute();
		}

		
		
	}
	private void ShowAlert(String message){
		JOptionPane.showMessageDialog(this, message,
                Message.getString("application.title"), JOptionPane.PLAIN_MESSAGE);
	}
	private void closeButtonPressed(){
		this.dispose();
	}
	/* (non-Javadoc)
	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
	 */
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getSource() instanceof BarrierSpeciesGenerator){
			if ("progress".equals(evt.getPropertyName())){
				int progress = (Integer) evt.getNewValue();
				progressMonitor.setProgress(progress);
	            String message =
	                String.format("Completed %d%%.\n", progress);
	            progressMonitor.setNote(message);
	            if (progress==100){
	            	ShowAlert("Done!");
	            }
			}
			if ("done-exception".equals(evt.getPropertyName())){
				BarrierSpeciesGenerator generator = (BarrierSpeciesGenerator) evt.getSource();
				generator.getException().printStackTrace();
			}
		}
		
	}
}
