/******************************************************************************
 * Huijie Qiao
 *
 * Project:  Niche Analyst
 * Purpose:  
 * Created date: May 6, 2012 1:39:25 PM
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


package org.ku.nicheanalyst.dataset;

/**
 * @author Huijie Qiao
 *
 */
public class GSpaceData {
	private float xMax;
	private float yMax;
	private float zMax;
	private float xMin;
	private float yMin;
	private float zMin;
	private float[] xValues;
	private float[] yValues;
	private float[] zValues;
	private int xSize;
	private int ySize;
	private int zSize;
	private int vpCount;
	private int[] verticesIndex;
	public GSpaceData(int xSize, int ySize, int zSize){
		xValues = new float[xSize];
		yValues = new float[ySize];
		zValues = new float[zSize];
		this.xSize = xSize;
		this.ySize = ySize;
		this.zSize = zSize;
	}
	
	public float[] getxValues() {
		return xValues;
	}
	public void setxValues(float[] xValues) {
		this.xValues = xValues;
	}
	public float[] getyValues() {
		return yValues;
	}
	public void setyValues(float[] yValues) {
		this.yValues = yValues;
	}
	public float[] getzValues() {
		return zValues;
	}
	public void setzValues(float[] zValues) {
		this.zValues = zValues;
	}
	public int getxSize() {
		return xSize;
	}
	public void setxSize(int xSize) {
		this.xSize = xSize;
	}
	public int getySize() {
		return ySize;
	}
	public void setySize(int ySize) {
		this.ySize = ySize;
	}
	public int getzSize() {
		return zSize;
	}
	public void setzSize(int zSize) {
		this.zSize = zSize;
	}
	public int getVpCount() {
		return vpCount;
	}
	public void setVpCount(int vpCount) {
		this.vpCount = vpCount;
	}

	public float getxMax() {
		return xMax;
	}

	public void setxMax(float xMax) {
		this.xMax = xMax;
	}

	public float getyMax() {
		return yMax;
	}

	public void setyMax(float yMax) {
		this.yMax = yMax;
	}

	public float getzMax() {
		return zMax;
	}

	public void setzMax(float zMax) {
		this.zMax = zMax;
	}

	public float getxMin() {
		return xMin;
	}

	public void setxMin(float xMin) {
		this.xMin = xMin;
	}

	public float getyMin() {
		return yMin;
	}

	public void setyMin(float yMin) {
		this.yMin = yMin;
	}

	public float getzMin() {
		return zMin;
	}

	public void setzMin(float zMin) {
		this.zMin = zMin;
	}

	public int[] getVerticesIndex() {
		return verticesIndex;
	}

	public void setVerticesIndex(int[] verticesIndex) {
		this.verticesIndex = verticesIndex;
	}
	
}
