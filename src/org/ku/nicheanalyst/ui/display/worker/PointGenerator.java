/******************************************************************************
 * Huijie Qiao
 *
 * Project:  Niche Analyst
 * Purpose:  
 * Created date: Jun 12, 2012 2:09:06 PM
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


package org.ku.nicheanalyst.ui.display.worker;

import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.SwingWorker;
import javax.vecmath.Color3f;

import org.ku.nicheanalyst.common.CommonFun;
import org.ku.nicheanalyst.common.ConfigInfo;
import org.ku.nicheanalyst.dataset.GSpaceData;
import org.ku.nicheanalyst.dataset.SpeciesData;
import org.ku.nicheanalyst.exceptions.ErrorSDSErrorException;
import org.ku.nicheanalyst.maps.objects.GeoTiffObject;
import org.ku.nicheanalyst.ui.display.Displayer;
import org.ku.nicheanalyst.ui.display.component.j3d.Color3D;

import quickhull3d.QuickHull2D;
import quickhull3d.QuickHull3D;

/**
 * @author Huijie Qiao
 *
 */
public class PointGenerator extends SwingWorker<Void, Void> {
	private int maxpoints;
	private HashMap<String, SpeciesData> values;
	private float[] xValues = null;
	private float[] yValues = null;
	private float[] zValues = null;
	private HashMap<String, SpeciesData> dotStatus;
	private int pointcount;
	private boolean isbackgroupd;
	private String filename;
	private GSpaceData gSpaceData;
	
	private boolean ifFolder;
	private boolean isResult;
	private HashMap<String, String> filenames;
	private double threshold;
//	private int resultType;
	private Color3f mve_color;
	private Color3f ch_color;
	private Color3f point_color;
	private boolean successed;
	private boolean isGradual;
	private String backgroundTiff;
	private boolean isFullBackground;
	private boolean is3D;
	private String[] layers;
	private quickhull3d.Point3d[] vertices;
	private int[][] faceIndices;
	double[] maxmin1_new;
	double[] maxmin2_new;
	double[] maxmin3_new;
	
	double[] maxmin1_original;
	double[] maxmin2_original;
	double[] maxmin3_original;
	
	double[][][] maxmin;
	
	private Displayer theApp;
	
	public PointGenerator(Displayer theApp, int maxpoints, boolean isbackgroupd, 
			String filename, boolean isResult, double threshold, 
			String backgroundTiff,
			Color3f mve_color, Color3f ch_color, Color3f point_color, 
			boolean isGradual, boolean isFullBackground, String[] layers,
			double[][][] maxmin, boolean is3D){
		this.theApp = theApp;
		this.layers = layers;
		this.isFullBackground = isFullBackground;
		this.backgroundTiff = backgroundTiff;
		this.threshold = threshold;
		this.maxpoints = maxpoints;
		this.isbackgroupd = isbackgroupd;
		this.filename = filename;
		this.ifFolder = true;
		this.isResult = isResult;
		this.threshold = threshold;
//		this.resultType = resultType;
		this.mve_color = mve_color;
		this.ch_color = ch_color;
		this.point_color = point_color;
		this.isGradual = isGradual;
		this.maxmin = maxmin;
		this.is3D = is3D;
	}
	/**
	 * @param backgroundPointCount
	 * @param filenames
	 */
	public PointGenerator(int maxpoints, HashMap<String, String> filenames, boolean is3D) {
		this.ifFolder = false;
		this.isbackgroupd = true;
		this.maxpoints = maxpoints;
		this.filenames = filenames;
		this.isFullBackground = false;
		this.layers = null;
		this.maxmin = null;
		this.is3D = is3D;
	}
	
	private ArrayList<String> download(String urlstr){
		ArrayList<String> rosterList = new ArrayList<String>();
		BufferedReader br = null;
		try {
			URL url;
			URLConnection urlConn;
			DataInputStream dis;
			url = new URL(urlstr);
			urlConn = url.openConnection();
			urlConn.setDoInput(true);
			urlConn.setUseCaches(false);
			dis = new DataInputStream(urlConn.getInputStream());
			String s;
			while ((s = dis.readLine()) != null) {
				String[] rowfields = s.split("[ ]+");
				rosterList.add(s);
				s = dis.readLine();
			}
			dis.close();
		} catch (MalformedURLException mue) {
		} catch (IOException ioe) {
		}
		return rosterList;
	}
	
	@Override
	protected Void doInBackground() throws ErrorSDSErrorException, FileNotFoundException, IOException {
		this.successed = true;
		setProgress(0);
		
		if (!ifFolder){
			values = new HashMap<String, SpeciesData>();
			GeoTiffObject geo1 = new GeoTiffObject(filenames.get("x"));
			GeoTiffObject geo2 = new GeoTiffObject(filenames.get("y"));
			GeoTiffObject geo3 = new GeoTiffObject(filenames.get("z"));
			maxmin1_original = geo1.getMaxMin();
			maxmin1_new = maxmin1_original;
			maxmin2_original = geo2.getMaxMin();
			maxmin2_new = maxmin2_original;
			maxmin3_original = geo3.getMaxMin();
			maxmin3_new = maxmin3_original;
			if (filenames.containsKey("xstandardized")){
				if (filenames.get("xstandardized").equals("t")){
					maxmin1_new = new double[]{-1d, 1d};
				}
			}
			if (filenames.containsKey("ystandardized")){
				if (filenames.get("ystandardized").equals("t")){
					maxmin2_new = new double[]{-1d, 1d};
				}
			}
			if (filenames.containsKey("zstandardized")){
				if (filenames.get("zstandardized").equals("t")){
					maxmin3_new = new double[]{-1d, 1d};
				}
			}
			
			if (filenames.containsKey("full")){
				if (filenames.get("full").equals("t")){
					this.isFullBackground = true;
					int totalRandomPoint = ConfigInfo.getInstance().getBackgroundPointCount();
					int currentprocess = -1;
					while (this.values.size()<totalRandomPoint){
						currentprocess = Math.round((float)values.size() * 99f/(float)totalRandomPoint);
						currentprocess = (currentprocess>=100)?99:currentprocess;
		        		setProgress(currentprocess);
						double v1 = Math.random() * (maxmin1_new[1] - maxmin1_new[0]) + maxmin1_new[0];
						double v2 = Math.random() * (maxmin2_new[1] - maxmin2_new[0]) + maxmin2_new[0];
						double v3 = Math.random() * (maxmin3_new[1] - maxmin3_new[0]) + maxmin3_new[0];
						SpeciesData vs = new SpeciesData(-1, -1, -1, -1, 
								new double[]{v1, v2, v3}, Color3D.grey);
						values.put(String.format("%.3f,%.3f,%.3f", v1, v2, v3), vs);
					}
				}else{
					double[] geotran = geo1.getDataset().GetGeoTransform();
					int currentprocess = -1;
					for (int x=0;x<geo1.getXSize();x++){
						currentprocess = Math.round((float)x * 99f/(float)geo1.getXSize());
						currentprocess = (currentprocess>=100)?99:currentprocess;
		        		setProgress(currentprocess);
						for (int y=0;y<geo1.getYSize();y++){
							double xvalue = geo1.readByXY(x, y);
							double yvalue = geo2.readByXY(x, y);
							double zvalue = geo3.readByXY(x, y);
							
							if ((!CommonFun.equal(xvalue, geo1.getNoData(), 1000))&&
									(!CommonFun.equal(yvalue, geo2.getNoData(), 1000))&&
									(!CommonFun.equal(zvalue, geo3.getNoData(), 1000))){
								double[] lls = CommonFun.PositionToLL(geotran, new int[]{x, y});
								double v1 = CommonFun.standardized(maxmin1_original, maxmin1_new, xvalue);
								double v2 = CommonFun.standardized(maxmin2_original, maxmin2_new, yvalue);
								double v3 = CommonFun.standardized(maxmin3_original, maxmin3_new, zvalue);
								
								SpeciesData vs = new SpeciesData(x, y, lls[0], lls[1], 
										new double[]{v1, v2, v3}, Color3D.grey);
								values.put(String.format("%d,%d", x, y), vs);
							}
						}
					}
				}
			}
			geo1.release();
			geo2.release();
			geo3.release();
		}else{
			//open a G+
			if (!isResult){
				//open a folder
				values = new HashMap<String, SpeciesData>();
				ArrayList<String> filevalues = CommonFun.readFromFile(filename + "/value.txt");
				ArrayList<String> xy = CommonFun.readFromFile(filename + "/xy.txt");
				ArrayList<String> ll = CommonFun.readFromFile(filename + "/ll.txt");
				Color3f color = point_color;
				if (isbackgroupd){
					color = Color3D.grey;
				}
				for (int i=0;i<filevalues.size();i++){
					try{
						SpeciesData vs = new SpeciesData(xy.get(i), ll.get(i), filevalues.get(i), color);
						values.put(String.format("%d,%d", vs.getX(), vs.getY()), vs);
					}catch (ErrorSDSErrorException e){
						
					}
				}
			}else{
				//open a tiff file
				GeoTiffObject geo = new GeoTiffObject(filename);
				values = new HashMap<String, SpeciesData>();
				GeoTiffObject geo2 = new GeoTiffObject(backgroundTiff);
				double[] transfer = geo2.getDataset().GetGeoTransform();
				geo2.release();
				GeoTiffObject layer1 = null;
				GeoTiffObject layer2 = null;
				GeoTiffObject layer3 = null;
				if (this.isFullBackground){
					layer1 = new GeoTiffObject(layers[0]);
					layer2 = new GeoTiffObject(layers[1]);
					layer3 = new GeoTiffObject(layers[2]);
				}
				for (int y=0;y<geo.getYSize();y++){
					setProgress((int) (99f * y/ geo.getYSize()));
					for (int x=0;x<geo.getXSize();x++){
						double value = geo.readByXY(x, y);
						if (CommonFun.equal(value, geo.getNoData(), 1000)){
							continue;
						}
//						switch (this.resultType){
//							//0-1
//							case 0:
//								break;
//							//0-100
//							case 1:
//								value = value/100f;
//								break;
//							//0-255
//							case 2:
//								value = value/255f;
//								break;
//						}
						if (value>=threshold){
							SpeciesData data = null;
							String keystr = null;
							if (this.isFullBackground){
								double v1 = layer1.readByXY(x, y);
								double v2 = layer2.readByXY(x, y);
								double v3 = layer3.readByXY(x, y);
								maxmin1_original = maxmin[0][0];
								maxmin2_original = maxmin[0][1];
								maxmin3_original = maxmin[0][2];
								
								maxmin1_new = maxmin[1][0];
								maxmin2_new = maxmin[1][1];
								maxmin3_new = maxmin[1][2];
								
								v1 = CommonFun.standardized(maxmin1_original, maxmin1_new, v1);
								v2 = CommonFun.standardized(maxmin2_original, maxmin2_new, v2);
								v3 = CommonFun.standardized(maxmin3_original, maxmin3_new, v3);
								
								keystr = String.format("%.3f,%.3f,%.3f", v1, v2, v3);
								keystr = getSpeciesData(new double[]{v1, v2, v3}, keystr);
							}else{
								double[] ll = CommonFun.PositionToLL(geo.getDataset().GetGeoTransform(), new int[]{x, y});
								int[] xy = CommonFun.LLToPosition(transfer, ll);
								keystr = String.format("%d,%d", xy[0], xy[1]);
								
							}
							data = getSpeciesData(keystr);
							if (data!=null){
								
								Color3f color = this.point_color;
								if (this.isGradual){
//									float rate = (float)((value - threshold) / (1f - threshold));
//									color = new Color3f(color.x * rate, color.y * rate, color.z * rate);
									float rate = (float)value;
//									RA+(BA-RA)/Step*N
									if (rate<.5){
										rate = rate * 2f;
										color = new Color3f(0, rate, 1f - rate);
									}else{
										rate = (1 - rate) * 2f;
										color = new Color3f(1f - rate, rate, 0);
									}

								}
								data = new SpeciesData(data.getX(), data.getY(), 
										data.getLongitude(), data.getLatitude(),
										data.getValues(), color);
								values.put(keystr, data);
							}
						}
					}
					
				}
				if (layer1!=null){
					layer1.release();
					layer2.release();
					layer3.release();
				}
				geo.release();
			}
		}
		
		if (values.size()<4){
			setProgress(100);
			this.successed = false;
			throw new ErrorSDSErrorException();
		}
		
		dotStatus = new HashMap<String, SpeciesData>();
		xValues = null;
		yValues = null;
		zValues = null;
		ArrayList<SpeciesData> new_values = new ArrayList<SpeciesData>();
		for (String key : values.keySet()){
			new_values.add(values.get(key));
		}
		if (!isbackgroupd){
			quickhull3d.Point3d[] points = new quickhull3d.Point3d[values.size()];
	        
			float[] xMaxMin = new float[2];
			float[] yMaxMin = new float[2];
			float[] zMaxMin = new float[2];
			xMaxMin[0] = Float.MAX_VALUE;
			xMaxMin[1] = Float.MIN_VALUE;
			yMaxMin[0] = Float.MAX_VALUE;
			yMaxMin[1] = Float.MIN_VALUE;
			zMaxMin[0] = Float.MAX_VALUE;
			zMaxMin[1] = Float.MIN_VALUE;
			int size = 0;
			if (values.size()>maxpoints){
				size = maxpoints;
			}else{
				size = values.size();
			}
			
			int ii = 0;
			for (SpeciesData vs : values.values()){
				double[] value = vs.getValues();
				points[ii] = new quickhull3d.Point3d(value[0], value[1], value[2]);
				xMaxMin[0] = (float) ((xMaxMin[0]>value[0])?value[0]:xMaxMin[0]);
				xMaxMin[1] = (float) ((xMaxMin[1]<value[0])?value[0]:xMaxMin[1]);
				
				yMaxMin[0] = (float) ((yMaxMin[0]>value[1])?value[1]:yMaxMin[0]);
				yMaxMin[1] = (float) ((yMaxMin[1]<value[1])?value[1]:yMaxMin[1]);
				
				zMaxMin[0] = (float) ((zMaxMin[0]>value[2])?value[2]:zMaxMin[0]);
				zMaxMin[1] = (float) ((zMaxMin[1]<value[2])?value[2]:zMaxMin[1]);
				ii++;
			}
			vertices = null;
		    if (this.is3D){
				QuickHull3D hull = new QuickHull3D();
				hull.build(points);
				vertices = hull.getVertices();
				faceIndices = hull.getFaces();
		    }else{
		    	QuickHull2D hull = new QuickHull2D();
		    	vertices = hull.execute(points);
		    	faceIndices = new int[vertices.length][1];
		    	for (int i=0;i<faceIndices.length;i++){
		    		faceIndices[i][0] = i;
		    	}
		    }
		    xValues = new float[size + vertices.length];
			yValues = new float[size + vertices.length];
			zValues = new float[size + vertices.length];
			gSpaceData = new GSpaceData(size + vertices.length, size + vertices.length, size + vertices.length);
			gSpaceData.setxMin(xMaxMin[0]);
			gSpaceData.setxMax(xMaxMin[1]);
			gSpaceData.setyMin(yMaxMin[0]);
			gSpaceData.setyMax(yMaxMin[1]);
			gSpaceData.setzMin(zMaxMin[0]);
			gSpaceData.setzMax(zMaxMin[1]);
			
			
			
			int pointcount = 0;
			int currentprocess = -1;
	        while (pointcount<maxpoints){
	        	if (Math.round((float)pointcount * 100f/(float)maxpoints)!=currentprocess){
	        		currentprocess = Math.round((float)pointcount * 100f/(float)maxpoints);
	        		currentprocess = (currentprocess>=100)?99:currentprocess;
	        		setProgress(currentprocess);
	        	}
	        	if (new_values.size()==0){
	        		break;
	        	}
	        	int index = (int) (Math.random() * new_values.size());
	        	SpeciesData value = new_values.get(index);
	        	
	        	xValues[pointcount] = (float) value.getValues()[0];
	        	yValues[pointcount] = (float) value.getValues()[1];
	        	zValues[pointcount] = (float) value.getValues()[2];
	        	dotStatus.put(String.format("%f,%f,%f", xValues[pointcount], yValues[pointcount], zValues[pointcount]), value);
	        	new_values.remove(index);
	        	pointcount++;
	        }
	        int[] verticesIndex = new int[vertices.length];
	        for (int i=0;i<vertices.length;i++){
	        	verticesIndex[i] = size + i;
	        	xValues[size + i] = (float) vertices[i].x;
	        	yValues[size + i] = (float) vertices[i].y;
	        	zValues[size + i] = (float) vertices[i].z;
	        	SpeciesData value = null;
	        	for (int j=0;j<new_values.size();j++){
	        		SpeciesData v = new_values.get(j);
	        		if (CommonFun.equal(xValues[size + i], v.getValues()[0], 1000)&&
	        				CommonFun.equal(yValues[size + i], v.getValues()[1], 1000)&&
	        				CommonFun.equal(zValues[size + i], v.getValues()[2], 1000)){
	        			value = v;
//	        			value.setColor(Color3D.blue);
//	        			System.out.println("Matched index : " + j);
	        			break;
	        		}
	        	}
	        	if (value==null){
//	        		System.out.println("null value");
	        	}else{
	        		dotStatus.put(String.format("%f,%f,%f", xValues[size + i], yValues[size + i], zValues[size + i]), value);
	        	}
	        }
	        gSpaceData.setVerticesIndex(verticesIndex);
	        gSpaceData.setxValues(xValues);
			gSpaceData.setyValues(yValues);
			gSpaceData.setzValues(zValues);
		}else{
			// if background
			int size = 0;
			if (values.size()>ConfigInfo.getInstance().getBackgroundPointCount()){
				size = ConfigInfo.getInstance().getBackgroundPointCount();
			}else{
				size = values.size();
			}
			xValues = new float[size];
			yValues = new float[size];
			zValues = new float[size];
			int pointcount = 0;
			int currentprocess = -1;
	        while (pointcount<size){
	        	if (Math.round((float)pointcount * 100f/(float)size)!=currentprocess){
	        		currentprocess = Math.round((float)pointcount * 100f/(float)size);
	        		currentprocess = (currentprocess>=100)?99:currentprocess;
	        		setProgress(currentprocess);
	        	}
	        	if (new_values.size()==0){
	        		break;
	        	}
	        	int index = (int) (Math.random() * new_values.size());
	        	SpeciesData value = new_values.get(index);
	        	
	        	xValues[pointcount] = (float)value.getValues()[0];
	        	yValues[pointcount] = (float)value.getValues()[1];
	        	zValues[pointcount] = (float)value.getValues()[2];
	        	
	        	dotStatus.put(String.format("%f,%f,%f", xValues[pointcount], yValues[pointcount], zValues[pointcount]), value);
	        	new_values.remove(index);
	        	pointcount++;
	        }
		}
		setProgress(100);
		return null;
	}
	private SpeciesData getSpeciesData(String keystr) {
		if (this.theApp.getBackgroundValues().containsKey(keystr)){
			return this.theApp.getBackgroundValues().get(keystr);
		}else{
			return null;
		}
	}
	private String getSpeciesData(double[] values, String keystr) {
		if (this.theApp.getBackgroundValues().containsKey(keystr)){
			return keystr;
		}else{
			SpeciesData vs = new SpeciesData(-1, -1, -1, -1, 
					values, Color3D.grey);
			this.theApp.addBackgroundValues(keystr, vs);
			
			
//			double minDistance = Double.MAX_VALUE;
//			for (String key : this.theApp.getBackgroundValues().keySet()){
//				SpeciesData data = this.theApp.getBackgroundValues().get(key);
//				double distance = CommonFun.getDistance(values, data.getValues());
//				if (minDistance>distance){
//					minDistance = distance;
//					itemKey = key;
//				}
//			}
			return keystr;
		}
	}
	private Exception msg;
	@Override
    public void done() {
        Toolkit.getDefaultToolkit().beep();
        try {
            get();
        } catch (Exception e) {
        	e.printStackTrace();
        	msg = e;
            firePropertyChange("done-exception", null, e);
        }
    }
	public Exception getException(){
		return msg;
	}
	
	public int getMaxpoints() {
		return maxpoints;
	}
	
	public float[] getxValues() {
		return xValues;
	}
	public float[] getyValues() {
		return yValues;
	}
	public float[] getzValues() {
		return zValues;
	}
	public HashMap<String, SpeciesData> getDotStatus() {
		return dotStatus;
	}
	public int getPointcount() {
		return pointcount;
	}
	public boolean isIsbackgroupd() {
		return isbackgroupd;
	}
	public String getFilename() {
		return filename;
	}
	public GSpaceData getgSpaceData() {
		return gSpaceData;
	}
	
	
	public boolean isIfFolder() {
		return ifFolder;
	}
	public boolean isResult() {
		return isResult;
	}
	public HashMap<String, String> getFilenames() {
		return filenames;
	}
	public double getThreshold() {
		return threshold;
	}
//	public int getResultType() {
//		return resultType;
//	}
	public Color3f getMve_color() {
		return mve_color;
	}
	public Color3f getCh_color() {
		return ch_color;
	}
	public Color3f getPoint_color() {
		return point_color;
	}
	public HashMap<String, SpeciesData> getValues() {
		return values;
	}
	public boolean isSuccessed() {
		return successed;
	}
	public boolean isFullBackground() {
		return isFullBackground;
	}
	public double[][][] getMaxMin() {
		double[][][] maxmin = new double[2][3][2];
		maxmin[0][0] = maxmin1_original;
		maxmin[0][1] = maxmin2_original;
		maxmin[0][2] = maxmin3_original;
		
		maxmin[1][0] = maxmin1_new;
		maxmin[1][1] = maxmin2_new;
		maxmin[1][2] = maxmin3_new;
		return maxmin;
	}
	public quickhull3d.Point3d[] getVertices() {
		return vertices;
	}
	public int[][] getFaceIndices() {
		
		return faceIndices;
	}

}
