/******************************************************************************
 * Huijie Qiao
 *
 * Project:  Niche Analyst
 * Purpose:  
 * Created date: Jun 18, 2012 11:55:53 AM
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

package org.ku.nicheanalyst.ui.display.component.ui;

//
//Polygons.java
//Polygons
//
//Created by Wagner on Mon Feb 23 2004.
//Copyright (c) 2004 W.L.Truppel. All rights reserved.
//

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.swing.JPanel;


public class DrawingPanel extends JPanel implements MouseListener, MouseMotionListener {
	/**
	 * This is how "constants" are defined in java.
	 */
	private String filename;
	private Image img;
	private boolean polygonIsNowComplete = false;
	private int height = -1;
	private int width = -1;
	private int si = 10;
	/**
	 * The 'dummy' point tracking the mouse.
	 */
	private final Point trackPoint = new Point();

	/**
	 * The list of points making up a polygon.
	 */
	private ArrayList<Point> points = new ArrayList<Point>();

	/**
	 * The only constructor needed for this class.
	 */
	public DrawingPanel() {
		super();
		addMouseListener(this);
		addMouseMotionListener(this);
	}

	/**
	 * This is where all the drawing action happens.
	 */
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		try {
			this.img = ImageIO.read(new File(filename));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		width = img.getWidth(null);
		height = img.getHeight(null);
        g.drawImage(img, si, si, this);
		int numPoints = points.size();
		if (numPoints == 0)
			return; // nothing to draw

		Point prevPoint = (Point) points.get(0);

		// draw polygon
		Iterator it = points.iterator();
		while (it.hasNext()) {
			Point curPoint = (Point) it.next();
			draw(g, prevPoint, curPoint);
			prevPoint = curPoint;
		}

		// now draw tracking line or complete the polygon
		if (polygonIsNowComplete)
			draw(g, prevPoint, (Point) points.get(0));
		else
			draw(g, prevPoint, trackPoint);
	}

	/**
	 * This method is required by the MouseListener interface, and is the only
	 * one that we really care about.
	 */
	public void mouseClicked(MouseEvent evt) {
		int x = evt.getX();
		int y = evt.getY();
		
		switch (evt.getClickCount()) {
		case 1: // single-click
			if (polygonIsNowComplete) {
				points.clear();
				polygonIsNowComplete = false;
			}
			points.add(new Point(x, y));
			repaint();
			break;

		case 2: // double-click
			polygonIsNowComplete = true;
			points.add(new Point(x, y));
			repaint();
			break;

		default: // ignore anything else
			break;
		}
	}

	/**
	 * This method is required by the MouseMotionListener interface, and is the
	 * only one that we really care about.
	 */
	public void mouseMoved(MouseEvent evt) {
		trackPoint.x = evt.getX();
		trackPoint.y = evt.getY();
		repaint();
	}

	/**
	 * Utility method used to draw points and lines.
	 */
	private void draw(Graphics g, Point p1, Point p2) {
		int x1 = p1.x;
		int y1 = p1.y;

		int x2 = p2.x;
		int y2 = p2.y;

		// draw the line first so that the points
		// appear on top of the line ends, not below
		g.setColor(Color.green.darker());
		g.drawLine(x1 + 3, y1 + 3, x2 + 3, y2 + 3);
		g.drawLine(x1 + 4, y1 + 4, x2 + 4, y2 + 4);
		g.drawLine(x1 + 5, y1 + 5, x2 + 5, y2 + 5);

		g.setColor(Color.red);
		g.fillOval(x1, y1, 8, 8);

		g.setColor(Color.blue);
		g.fillOval(x2, y2, 8, 8);
	}

	public void setImage(String filename) {
		this.filename = filename;
		repaint();
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
	}
	@Override
	public void mousePressed(MouseEvent arg0) {
	}
	@Override
	public void mouseReleased(MouseEvent arg0) {
	}

	@Override
	public void mouseDragged(MouseEvent e) {
	}

	public ArrayList<Point> getPoints() {
		return points;
	}
}
