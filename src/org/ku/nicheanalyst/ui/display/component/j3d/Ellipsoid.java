/******************************************************************************
 * Huijie Qiao
 *
 * Project:  Niche Analyst
 * Purpose:  
 * Created date: May 2, 2012 4:50:20 PM
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


package org.ku.nicheanalyst.ui.display.component.j3d;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.TreeMap;

import javax.media.j3d.Appearance;
import javax.media.j3d.Geometry;
import javax.media.j3d.IndexedLineArray;
import javax.media.j3d.LineAttributes;
import javax.media.j3d.Material;
import javax.media.j3d.PointArray;
import javax.media.j3d.Shape3D;
import javax.vecmath.Color3f;
import javax.vecmath.Point3f;

import org.jdom.Attribute;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.ku.nicheanalyst.algorithms.MinimumVolumeEllipsoid;
import org.ku.nicheanalyst.algorithms.MinimumVolumeEllipsoidResult;
import org.ku.nicheanalyst.common.CommonFun;
import org.ku.nicheanalyst.exceptions.IllegalSelectionException;

import Jama.EigenvalueDecomposition;
import Jama.Matrix;

/**
 * @author Huijie Qiao
 *
 */
public class Ellipsoid extends Shape3D {
	private float a;
	private float b;
	private float c;
	private int u_resolution;
	private int v_resolution;
	private int type;
	private float offset_x;
	private float offset_y;
	private float offset_z;
	private float rotate_x;
	private float rotate_y;
	private float rotate_z;
	private Color3f color;
	private boolean createVertexes;
	private HashMap<Integer, Point3f> vertexes;
	private Matrix mveA;
	private Matrix mveV;
	private boolean ellipsoidType;
	private float scale;
	private boolean is3D;
	private float linewidth;
	public Ellipsoid(float scale, Matrix A, Matrix v, int u_resolution, int v_resolution, 
			int type, Color3f color, boolean createVertexes, boolean is3D, float linewidth){
		this.is3D = is3D;
		this.scale = scale;
		this.ellipsoidType = true;
		this.mveA = A;
		this.mveV = v;
		this.color = color;
		this.v_resolution = v_resolution;
		this.u_resolution = u_resolution;
		this.type = type;
		this.createVertexes = createVertexes;
		this.linewidth = linewidth;
		EigenvalueDecomposition e = new EigenvalueDecomposition(A.inverse());
		Matrix eigenValue = e.getD();
		Matrix eigenVector = e.getV();
		this.a = (float) Math.sqrt(eigenValue.get(0, 0)) / scale;
		this.b = (float) Math.sqrt(eigenValue.get(1, 1)) / scale;
		if (is3D){
			this.c = (float) Math.sqrt(eigenValue.get(2, 2)) / scale;
		}else{
			this.c = 0;
		}
		this.offset_x = (float) v.get(0, 0) / scale;
		this.offset_y = (float) v.get(1, 0) / scale;
		if (this.is3D){
			this.offset_z = (float) v.get(2, 0) / scale;
		}else{
			this.offset_z = 0;
		}
		this.rotate_x = 0;
		this.rotate_y = 0;
		this.rotate_z = 0;
		vertexes = new HashMap<Integer, Point3f>();
		if (createVertexes){
			vertexes.put(EllipsoidVertexType.Centre, new Point3f(offset_x, offset_y, offset_z));
		}
	    this.setCapability(Shape3D.ALLOW_APPEARANCE_READ);
	    this.setCapability(Shape3D.ALLOW_APPEARANCE_OVERRIDE_READ);
	    this.setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);
	    this.setCapability(Shape3D.ALLOW_APPEARANCE_OVERRIDE_WRITE);
	    this.setGeometry(createGeometry(eigenVector, scale));
	    this.setAppearance(createAppearance());
	}
	public Ellipsoid(float scale, float a, float b, float c, int u_resolution, int v_resolution, int type, 
			float offset_x, float offset_y, float offset_z, 
			float rotate_x, float rotate_y, float rotate_z, Color3f color, 
			boolean createVertexes, boolean is3D) {
		this.is3D = is3D;
		this.scale = scale;
		this.ellipsoidType = false;
		this.mveA = null;
		this.mveV = null;
		this.color = color;
		this.a = a;
		this.b = b;
		this.c = c;
		this.v_resolution = v_resolution;
		this.u_resolution = u_resolution;
		this.type = type;
		this.offset_x = offset_x;
		this.offset_y = offset_y;
		this.offset_z = offset_z;
		this.rotate_x = rotate_x;
		this.rotate_y = rotate_y;
		this.rotate_z = rotate_z;
		this.createVertexes = createVertexes;
		vertexes = new HashMap<Integer, Point3f>();
		if (createVertexes){
			vertexes.put(EllipsoidVertexType.Centre, new Point3f(offset_x, offset_y, offset_z));
		}
	    this.setCapability(Shape3D.ALLOW_APPEARANCE_READ);
	    this.setCapability(Shape3D.ALLOW_APPEARANCE_OVERRIDE_READ);
	    this.setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);
	    this.setCapability(Shape3D.ALLOW_APPEARANCE_OVERRIDE_WRITE);
	    this.setGeometry(createGeometry(null, scale));
	    this.setAppearance(createAppearance());
	    
	}
	

	private Geometry createGeometry(Matrix eigenVector, float scale) {
		Point3f[] allpoints = new Point3f[(v_resolution + 1) * u_resolution];
		double[][] values = null;
		if (eigenVector==null){
			values = new double[3][u_resolution * (v_resolution + 1)];
		}
		for (int i=0;i<u_resolution;i++){
			for (int j=0;j<=v_resolution;j++){ 
				float ui = (float) ((Math.PI*2) * (float)i/(float)u_resolution);
				float vi = (float) (Math.PI * (float)j/(float)v_resolution);
				float x = (float) (a * Math.cos(ui) * Math.sin(vi));
				float y = (float) (b * Math.sin(ui) * Math.sin(vi));
				float z = (float) (c * Math.cos(vi));
				boolean isDdded = true;
				if (eigenVector!=null){
					if (this.is3D){
						double[][] t = new double[3][1];
						t[0][0] = x * scale;
						t[1][0] = y * scale;
						t[2][0] = z * scale;
						Matrix p = new Matrix(t);
						p = eigenVector.times(p);
						x = (float) p.get(0, 0) / scale;
						y = (float) p.get(1, 0) / scale;
						z = (float) p.get(2, 0) / scale;
					}else{
						double[][] t = new double[2][1];
						t[0][0] = x * scale;
						t[1][0] = y * scale;
						
						Matrix p = new Matrix(t);
						p = eigenVector.times(p);
						x = (float) p.get(0, 0) / scale;
						y = (float) p.get(1, 0) / scale;
//						System.out.println(
//								String.format("x:%f, y:%f  x_scale:%f, y_scale:%f, distance:%f, distance_scale:%f", 
//								p.get(0, 0), p.get(1, 0), x, y, 
//								(Math.pow(p.get(0, 0), 2))/a + (Math.pow(p.get(1, 0), 2))/b, 
//								(Math.pow(x, 2))/a + (Math.pow(y, 2))/b));
						z = 0;
					}
				}else{
//					System.out.println(i + "," + j);
					values[0][i * (v_resolution + 1) + j] = x;
					values[1][i * (v_resolution + 1) + j] = y;
					values[2][i * (v_resolution + 1) + j] = z;
					
				}
				
				
				int pointType = EllipsoidVertexType.Normal;
				if (CommonFun.equal(x, a, 1000000)&&CommonFun.equal(y, 0f, 1000000)&&CommonFun.equal(z, 0f, 1000000)){
					pointType = EllipsoidVertexType.PositiveX;
				}
				if (CommonFun.equal(x, a * -1f, 1000000)&&CommonFun.equal(y, 0f, 1000000)&&CommonFun.equal(z, 0f, 1000000)){
					pointType = EllipsoidVertexType.NegativeX;
				}
				if (CommonFun.equal(x, 0f, 1000000)&&CommonFun.equal(y, b, 1000000)&&CommonFun.equal(z, 0f, 1000000)){
					pointType = EllipsoidVertexType.PositiveY;
				}
				if (CommonFun.equal(x, 0f, 1000000)&&CommonFun.equal(y, b * -1f, 1000000)&&CommonFun.equal(z, 0f, 1000000)){
					pointType = EllipsoidVertexType.NegativeY;
				}
				if (CommonFun.equal(x, 0f, 1000000)&&CommonFun.equal(y, 0f, 1000000)&&CommonFun.equal(z, c, 1000000)){
					pointType = EllipsoidVertexType.PositiveZ;
				}
				if (CommonFun.equal(x, 0f, 1000000)&&CommonFun.equal(y, 0f, 1000000)&&CommonFun.equal(z, c * -1f, 1000000)){
					pointType = EllipsoidVertexType.NegativeZ;
				}
				Point3f point = getPoint(x, y, z, true);
				allpoints[j * u_resolution + i] = point;
				if (createVertexes){
					if (pointType!=EllipsoidVertexType.Normal){
						this.vertexes.put(pointType, point);
					}
				}
			}
			
//			lines.setColor(i, new float[]{ui * 16f, ui * 16f, ui * 16f});
			
		}
		if (eigenVector==null){
			try{
				MinimumVolumeEllipsoidResult mve = MinimumVolumeEllipsoid.getMatrix(values, (this.is3D)?3:2);
				this.mveA = mve.getA();
				this.mveV = mve.getCenter();
			}catch (Exception e){
				this.mveA = null;
				this.mveV = null;
			}
		}
		Point3f[] points = allpoints;
		if ((!this.is3D)&&(this.mveA!=null)){
			int pointcount = 0;
			for (Point3f p :  allpoints){
				
				if (isOnBorder(p.x, p.y, p.z)){
					pointcount++;
				}
			}
			points = new Point3f[pointcount];
			pointcount = 0;
			
			for (Point3f p :  allpoints){
				if (isOnBorder(p.x, p.y, p.z)){
					points[pointcount] = p;
					pointcount++;
				}
			}
		}
		if (points.length<=4){
			points = allpoints;
		}
		if (type==0){
			IndexedLineArray lines = null;
			int indexcount = 0;
			TreeMap<Integer, Integer> index = new TreeMap<Integer, Integer>();
			if (points.length==allpoints.length){
				for (int i=0;i<u_resolution;i++){
					for (int j=0;j<=v_resolution;j++){ 
						index.put(indexcount, j * u_resolution + i);
						indexcount++;
						if (j!=0){
							index.put(indexcount, (j - 1) * u_resolution + i);
							indexcount++;
						}else{
							index.put(indexcount, v_resolution * u_resolution + i);
							indexcount++;
						}
						if (i!=0){
							index.put(indexcount, j * u_resolution + i - 1);
							indexcount++;
						}else{
							index.put(indexcount, j * u_resolution + u_resolution - 1);
							indexcount++;
						}
					}
				}
				lines = new IndexedLineArray(points.length, IndexedLineArray.COORDINATES | IndexedLineArray.COLOR_3, 
						index.size());
			
				for (int i=0;i<u_resolution;i++){
					for (int j=0;j<=v_resolution;j++){ 
						Point3f p = points[j * u_resolution + i];
						lines.setColor(j * u_resolution + i, color);
						lines.setCoordinate(j * u_resolution + i, p);
					}
				}
			}else{
				int ii = 0;
				for (int i=0;i<points.length;i++){
					
					index.put(ii, i);
					ii++;
					
					if (i==points.length-1){
						index.put(ii, 0);
						ii++;
					}else{
						index.put(ii, i + 1);
						ii++;
					}
					
				}
				
				lines = new IndexedLineArray(points.length, IndexedLineArray.COORDINATES | IndexedLineArray.COLOR_3, 
						index.size());
				for (int i=0;i<points.length;i++){
					lines.setColor(i, color);
					lines.setCoordinate(i, points[i]);
				}
			}
			for (Integer ind : index.keySet()){
				lines.setCoordinateIndex(ind, index.get(ind));
			}
			
			return lines;
		}
		if (type==1){
			PointArray lines = new PointArray(points.length, PointArray.COORDINATES | PointArray.ALLOW_COLOR_WRITE | PointArray.COLOR_3);
			lines.setCoordinates(0, points);
			lines.setColor(0, color);
			return lines;
		}
		return null;
	}
	private Appearance createAppearance(){
		LineAttributes dashLa = new LineAttributes();
	    dashLa.setLineWidth(linewidth);
	    dashLa.setLinePattern(LineAttributes.PATTERN_SOLID);
	    Appearance appearance = new Appearance();
	    appearance.setLineAttributes(dashLa);
	    appearance.setMaterial(new Material(color, color, color, color, 1f));
	    return appearance;
	    
	}
	public Point3f getPoint(double x, double y, double z, boolean isForward) {
		if (isForward){
			double xx, yy, zz;
			//x rotate
			xx = x;
			yy = (y * Math.cos(this.rotate_x) - z * Math.sin(this.rotate_x));
			zz = (y * Math.sin(this.rotate_x) + z * Math.cos(this.rotate_x));
			x = xx;
			y = yy;
			z = zz;
			//y rotate
			xx = (x * Math.cos(this.rotate_y) + z * Math.sin(this.rotate_y));
			yy = y;
			zz = (-1f * x * Math.sin(this.rotate_y) + z * Math.cos(this.rotate_y));
			x = xx;
			y = yy;
			z = zz;
			//z rotate
			xx = (x * Math.cos(this.rotate_z) - y * Math.sin(this.rotate_z));
			yy = (x * Math.sin(this.rotate_z) + y * Math.cos(this.rotate_z));
			zz = z;
			x = xx;
			y = yy;
			z = zz;
	//		//offset
			x += this.offset_x;
			y += this.offset_y;
			z += this.offset_z;
		}else{
			x -= this.offset_x;
			y -= this.offset_y;
			z -= this.offset_z;
			
			double rotate_x = -1f * this.rotate_x;
			double rotate_y = -1f * this.rotate_y;
			double rotate_z = -1f * this.rotate_z;
			
			double xx, yy, zz;
			//z rotate
			xx = (x * Math.cos(rotate_z) - y * Math.sin(rotate_z));
			yy = (x * Math.sin(rotate_z) + y * Math.cos(rotate_z));
			zz = z;
			x = xx;
			y = yy;
			z = zz;
			//y rotate
			xx = (x * Math.cos(rotate_y) + z * Math.sin(rotate_y));
			yy = y;
			zz = (-1f * x * Math.sin(rotate_y) + z * Math.cos(rotate_y));
			x = xx;
			y = yy;
			z = zz;
			//x rotate
			xx = x;
			yy = (y * Math.cos(rotate_x) - z * Math.sin(rotate_x));
			zz = (y * Math.sin(rotate_x) + z * Math.cos(rotate_x));
			x = xx;
			y = yy;
			z = zz;
			
		}
		Point3f point = new Point3f((float)x, (float)y, (float)z);
		return point;
	}
	public boolean isOnBorder(double x, double y, double z) {
		
		if ((a==0)||(b==0)){
			return false;
		}
		
//		if ((a==0)||(b==0)||(c==0)){
//			return false;
//		}
		if (c!=0){
			Point3f point = getPoint(x, y, z, false);
//			System.out.println((Math.pow(point.x, 2)/Math.pow(a, 2)
//					+ Math.pow(point.y, 2)/Math.pow(b, 2)
//					+ Math.pow(point.z, 2)/Math.pow(c, 2)));
			if (CommonFun.equal((Math.pow(point.x, 2)/Math.pow(a, 2)
					+ Math.pow(point.y, 2)/Math.pow(b, 2)
					+ Math.pow(point.z, 2)/Math.pow(c, 2)), 1, 1000)){
				return true;
			}else{
				return false;
			}
		}else{
			Point3f point = getPoint(x, y, z, false);
//			System.out.println((Math.pow(point.x, 2)/Math.pow(a, 2)
//					+ Math.pow(point.y, 2)/Math.pow(b, 2)));
			if (CommonFun.equal((Math.pow(point.x, 2)/Math.pow(a, 2)
					+ Math.pow(point.y, 2)/Math.pow(b, 2)), 1, 1000)){
				return true;
			}else{
				return false;
			}
		}
	}
	public boolean isInEllipsoid(double x, double y, double z) {
		
		if ((a==0)||(b==0)){
			return false;
		}
		
//		if ((a==0)||(b==0)||(c==0)){
//			return false;
//		}
		if (c!=0){
			Point3f point = getPoint(x, y, z, false);
			if ((Math.pow(point.x, 2)/Math.pow(a, 2)
					+ Math.pow(point.y, 2)/Math.pow(b, 2)
					+ Math.pow(point.z, 2)/Math.pow(c, 2))<=1){
				return true;
			}else{
				return false;
			}
		}else{
			Point3f point = getPoint(x, y, z, false);
			if ((Math.pow(point.x, 2)/Math.pow(a, 2)
					+ Math.pow(point.y, 2)/Math.pow(b, 2))<=1){
				return true;
			}else{
				return false;
			}
		}
	}
	/**
	 * @return
	 */
	public HashMap<Integer, Point3f> getVertexes() {
		return this.vertexes;
	}


	public float getA() {
		return a;
	}


	public float getB() {
		return b;
	}


	public float getC() {
		return c;
	}


	public float getOffset_x() {
		return offset_x;
	}


	public float getOffset_y() {
		return offset_y;
	}


	public float getOffset_z() {
		return offset_z;
	}


	public float getRotate_x() {
		return rotate_x;
	}


	public float getRotate_y() {
		return rotate_y;
	}


	public float getRotate_z() {
		return rotate_z;
	}
	public Ellipsoid(String filename) throws FileNotFoundException, JDOMException, IOException{
		Element rootElement = CommonFun.readXML(filename);
		this.ellipsoidType = Boolean.valueOf(rootElement.getAttributeValue("ellipsoidType"));
		this.createVertexes = Boolean.valueOf(rootElement.getAttributeValue("createVertexes"));
		this.a = Float.valueOf(rootElement.getAttributeValue("a"));
		this.b = Float.valueOf(rootElement.getAttributeValue("b"));
		this.c = Float.valueOf(rootElement.getAttributeValue("c"));
		this.offset_x = Float.valueOf(rootElement.getAttributeValue("offset_x"));
		this.offset_y = Float.valueOf(rootElement.getAttributeValue("offset_y"));
		this.offset_z = Float.valueOf(rootElement.getAttributeValue("offset_z"));
		this.rotate_x = Float.valueOf(rootElement.getAttributeValue("rotate_x"));
		this.rotate_y = Float.valueOf(rootElement.getAttributeValue("rotate_y"));
		this.rotate_z = Float.valueOf(rootElement.getAttributeValue("rotate_z"));
		this.scale = Float.valueOf(rootElement.getAttributeValue("scale"));
		this.u_resolution = Integer.valueOf(rootElement.getAttributeValue("u_resolution"));
		this.v_resolution = Integer.valueOf(rootElement.getAttributeValue("v_resolution"));
		this.type = Integer.valueOf(rootElement.getAttributeValue("type"));
		String[] colorStr = rootElement.getAttributeValue("color").split(",");
		this.color = new Color3f(Float.valueOf(colorStr[0]), Float.valueOf(colorStr[1]), Float.valueOf(colorStr[2]));
		this.mveA = CommonFun.setMatrixString(rootElement.getAttributeValue("mveA"));
		this.mveV = CommonFun.setMatrixString(rootElement.getAttributeValue("mveV"));
	}
	/**
	 * @param file
	 * @throws IOException 
	 * @throws IllegalSelectionException 
	 */
	public void save(String filename) throws IOException, IllegalSelectionException {
		if ((this.mveA==null)||(this.mveV==null)){
			throw new IllegalSelectionException();
		}
		Element rootElement = new Element("root");
		Attribute attribute = new Attribute("ellipsoidType", String.valueOf(ellipsoidType));
		rootElement.getAttributes().add(attribute);
		attribute = new Attribute("a", String.valueOf(a));
		rootElement.getAttributes().add(attribute);
		attribute = new Attribute("b", String.valueOf(b));
		rootElement.getAttributes().add(attribute);
		attribute = new Attribute("c", String.valueOf(c));
		rootElement.getAttributes().add(attribute);
		attribute = new Attribute("u_resolution", String.valueOf(u_resolution));
		rootElement.getAttributes().add(attribute);
		attribute = new Attribute("v_resolution", String.valueOf(v_resolution));
		rootElement.getAttributes().add(attribute);
		attribute = new Attribute("type", String.valueOf(type));
		rootElement.getAttributes().add(attribute);
		attribute = new Attribute("offset_x", String.valueOf(offset_x));
		rootElement.getAttributes().add(attribute);
		attribute = new Attribute("offset_y", String.valueOf(offset_y));
		rootElement.getAttributes().add(attribute);
		attribute = new Attribute("offset_z", String.valueOf(offset_z));
		rootElement.getAttributes().add(attribute);
		attribute = new Attribute("rotate_x", String.valueOf(rotate_x));
		rootElement.getAttributes().add(attribute);
		attribute = new Attribute("rotate_y", String.valueOf(rotate_y));
		rootElement.getAttributes().add(attribute);
		attribute = new Attribute("rotate_z", String.valueOf(rotate_z));
		rootElement.getAttributes().add(attribute);
		attribute = new Attribute("color", String.format("%f,%f,%f", color.x, color.y, color.z));
		rootElement.getAttributes().add(attribute);
		attribute = new Attribute("createVertexes", String.valueOf(createVertexes));
		rootElement.getAttributes().add(attribute);
		attribute = new Attribute("mveA", CommonFun.getMatrixString(this.mveA));
		rootElement.getAttributes().add(attribute);
		attribute = new Attribute("mveV", CommonFun.getMatrixString(this.mveV));
		rootElement.getAttributes().add(attribute);
		attribute = new Attribute("scale", String.valueOf(scale));
		rootElement.getAttributes().add(attribute);
		CommonFun.writeXML(rootElement, filename);
		
	}
	public int getU_resolution() {
		return u_resolution;
	}
	public int getV_resolution() {
		return v_resolution;
	}
	public int getType() {
		return type;
	}
	public Color3f getColor() {
		return color;
	}
	public boolean isCreateVertexes() {
		return createVertexes;
	}
	
	public boolean isEllipsoidType() {
		return ellipsoidType;
	}
	public float getScale() {
		return scale;
	}
	public Matrix getMveA() {
		return mveA;
	}
	public Matrix getMveV() {
		return mveV;
	}
	
}
