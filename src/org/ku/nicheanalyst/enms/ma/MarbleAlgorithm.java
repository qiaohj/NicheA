/******************************************************************************
 * Huijie Qiao
 *
 * Project:  NicheA
 * Purpose:  
 * Created date: Oct 4, 2012 4:07:57 PM
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


package org.ku.nicheanalyst.enms.ma;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.gdal.gdal.Band;
import org.gdal.gdal.Dataset;
import org.gdal.gdalconst.gdalconst;
import org.ku.nicheanalyst.common.CommonFun;
import org.ku.nicheanalyst.common.ConfigInfo;
import org.ku.nicheanalyst.common.Const;
import org.ku.nicheanalyst.dataset.SpeciesData;
import org.ku.nicheanalyst.maps.controllers.GeoTiffController;
import org.ku.nicheanalyst.maps.objects.GeoTiffObject;

import weka.clusterers.DBScan;
import weka.clusterers.NoiseException;
import weka.clusterers.forOPTICSAndDBScan.DataObjects.DataObject;
import weka.clusterers.forOPTICSAndDBScan.Databases.SequentialDatabase;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

/**
 * @author Huijie Qiao
 *
 */
public class MarbleAlgorithm {
	private String arffFileName;
	private String outputFileName;
	private double epsilon;
	private int minPoint;
	private int maxNoisePoint;
	private String distanceType = "weka.clusterers.forOPTICSAndDBScan.DataObjects.EuclidianDataObject";
	private GeoTiffObject bg;
	private HashMap<String, SpeciesData> background;
	private String backgroundTiff;
	public MarbleAlgorithm(String llFileName, HashMap<String, SpeciesData> background, String backgroundTiff) throws IOException{
		this.background = background;
		this.backgroundTiff = backgroundTiff;
		this.arffFileName = ConfigInfo.getInstance().getTemp() + "/matemp.arff";
		this.bg = new GeoTiffObject(backgroundTiff);
		ArrayList<String> lls = CommonFun.readFromFile(llFileName);
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("@relation 'vs'%n@attribute a1 real%n@attribute a2 real%n@attribute a3 real%n@data%n"));
		for (String llstr : lls){
			String[] llstrs = null;
			if (llstr.contains(",")){
				llstrs = llstr.split(",");
			}else{
				llstrs = llstr.split("\t");
			}
			
			if (llstrs.length==2){
				double[] ll = new double[]{Double.valueOf(llstrs[0]), Double.valueOf(llstrs[1])};
				int[] xy = CommonFun.LLToPosition(this.bg.getDataset().GetGeoTransform(), ll);
				SpeciesData speciesData = background.get(String.format("%d,%d", xy[0], xy[1]));
				if (speciesData!=null){
					sb.append(String.format("%f,%f,%f%n", 
							speciesData.getValues()[0], speciesData.getValues()[1], speciesData.getValues()[2]));
				}
			}
			if (llstrs.length==5){
				if (CommonFun.isDouble(llstrs[2])){
					double[] ll = new double[]{Double.valueOf(llstrs[2]), Double.valueOf(llstrs[3])};
					int[] xy = CommonFun.LLToPosition(this.bg.getDataset().GetGeoTransform(), ll);
					SpeciesData speciesData = background.get(String.format("%d,%d", xy[0], xy[1]));
					if (speciesData!=null){
						sb.append(String.format("%f,%f,%f%n", 
								speciesData.getValues()[0], speciesData.getValues()[1], speciesData.getValues()[2]));
					}
				}
			}
		}
		CommonFun.writeFile(sb.toString(), this.arffFileName);
		this.maxNoisePoint = 0;
		this.outputFileName = ConfigInfo.getInstance().getTemp() + "/maoutput.tiff";
	}
	
	public void buildCluster() throws Exception{
		 
		DBScan dbScan = new DBScan();
		FileReader reader = new FileReader(this.arffFileName);
		Instances instances = new Instances(reader);

		WekaController wekaController = new WekaController();
//		instances = wekaController.removeDuplicateInstance(instances);
//		CommonFun.SaveArffToFile("./test_filter_instance.arff", instances);
		
		//System.out.println("instancesSize:" + instances.size());
		//instances.deleteAttributeType(1);
		dbScan.setDatabase_distanceType(distanceType);
		dbScan.setDatabase_Type("weka.clusterers.forOPTICSAndDBScan.Databases.SequentialDatabase");
		
		//remove the noise instance
		int filtedRecordCount = -1;
		DenseInstancesExtend denseInstancesExtend = new DenseInstancesExtend();
		if (instances.size()<=2){
			throw new NotEnoughRecordException("not enought instances");
		}
//		while ((filtedRecordCount!=0)&&(instances.size()!=0)){
//			caculateDistance(instances, dbScan, denseInstancesExtend);
//			//filtedRecordCount = 0;
//			filtedRecordCount = denseInstancesExtend.removeNoise(instances);
//		}
		
		denseInstancesExtend = new DenseInstancesExtend();
		caculateDistance(instances, dbScan, denseInstancesExtend);
		epsilon = denseInstancesExtend.getEpsilon() * 1.0000001;
		minPoint = 2;
		maxNoisePoint = (int) Math.round((instances.size() * (maxNoisePoint /100f) ));
		System.out.println("Optimizing Eps & minPts with init Eps=" + epsilon + " and minPts=" + minPoint + " and maxNoisePoint=" + maxNoisePoint);
		for (int i=3;i<instances.size();i++){
//			System.out.println(minPoint);
			minPoint = i;
			dbScan.setMinPoints(minPoint);
			dbScan.setEpsilon(epsilon);
			boolean isallclusted = true;
			dbScan.buildClusterer(instances);
			int noisecount = 0;
			for (Instance instance : instances){
				
				try{
					dbScan.clusterInstance(instance);
				}catch (NoiseException e){
					noisecount++;
					
//					System.out.print("Unclusted instance:");
//					System.out.println(instance);
//					break;
				}
			}
			if (noisecount>maxNoisePoint){
//				noisecount++;
				isallclusted=false;
			}
			System.out.println("noisecount: " + noisecount);
			if (!isallclusted){
				break;
			}
		}
		minPoint--;

		System.out.println("Eps:" + epsilon);
		System.out.println("minPts:" + minPoint);
//		epsilon = .8;
		dbScan.setEpsilon(epsilon);
		dbScan.setMinPoints(minPoint);
		
		System.out.println("Building model");
		dbScan.buildClusterer(instances);
		//get clusted instances
		SequentialDatabase database = (SequentialDatabase) dbScan.getDatabase();
		Iterator iterator = database.dataObjectIterator();
		ArrayList<DataObject> clustedObjects = new ArrayList<DataObject>();
        while (iterator.hasNext()) {
            DataObject dataObject = (DataObject) iterator.next();
            if (dataObject.getClusterLabel()!=DataObject.NOISE){
            	Iterator iterator2 = database.dataObjectIterator();
            	int clusteredNum = 0;
                while (iterator2.hasNext()) {
                	DataObject dataObject2 = (DataObject) iterator2.next();
                	if (dataObject.distance(dataObject2)<epsilon){
                		clusteredNum++;
                	}
                }
                if (clusteredNum>=minPoint){
                	clustedObjects.add(dataObject);
                }
            }else{
            	//System.out.println(dataObject.toString());
            }
        }
        System.out.println("Creating result with " + clustedObjects.size() + " clusted objects");
        
		
		int xsize = bg.getXSize();
		int ysize = bg.getYSize();
		double[] maskvalueArray = bg.getValueArray();
		
		
		double[] values = new double[xsize * ysize];
		
		int totalpoint = 0;
		
		for (int y=0;y<ysize;y++){
			System.out.println(y + "/" + ysize);
			for (int x=0;x<xsize;x++){	
				int currentpoint = y * xsize + x;
				double maskvalue = maskvalueArray[currentpoint];
				values[currentpoint] = bg.getNoData();
				if (!CommonFun.equal(maskvalue, bg.getNoData(), 1000)){
					DenseInstance instance = new DenseInstance(instances.firstInstance());
					instance.setDataset(instances);
					SpeciesData speciesData = background.get(String.format("%d,%d", x, y));
					instance.setValue(0, speciesData.getValues()[0]);
					instance.setValue(1, speciesData.getValues()[1]);
					instance.setValue(2, speciesData.getValues()[2]);
					
					DataObject testdataObject = dbScan.dataObjectForName(dbScan.getDatabase_distanceType(),
							instance, Integer.toString(x), database);
					double min_distance = Double.MAX_VALUE;
					for (DataObject traindataObject: clustedObjects){
						double distance = testdataObject.distance(traindataObject);
						min_distance = (distance<min_distance)?distance:min_distance;
						if (min_distance<=0.0001){
							break;
						}
						
					}
					if ( min_distance < dbScan.getEpsilon()){
						values[currentpoint] = (int)Math.round(100f * (dbScan.getEpsilon() - min_distance) / dbScan.getEpsilon());
						values[currentpoint] = (values[currentpoint]>100)?100:values[currentpoint];
						totalpoint++;
					}else{
//						values[currentpoint] = -1;
					}
				}
			}
		}
		
//		for (int y=0;y<ysize;y++){
//			System.out.println(y + "/" + ysize);
//			for (int x=0;x<xsize;x++){	
//				int currentpoint = y * xsize + x;
//				double maskvalue = maskvalueArray[currentpoint];
//				values[currentpoint] = Const.NoData;
//				if (!CommonFun.equal(maskvalue, bg.getNoData(), 100000)){
//					//clusted point:clustedid noise: -1 not instance:nodata
//					SpeciesData speciesData = background.get(String.format("%d,%d", x, y));
//					if (speciesData!=null){
//						DataObject dataObject = getDataObject(speciesData, database);
//						if (dataObject!=null){
//							if (dataObject.getClusterLabel()==DataObject.NOISE){
//								values[currentpoint] = -1;
//							}else{
//								values[currentpoint] = dataObject.getClusterLabel();
//							}
//						}
//					}
//				}
//			}
//		}
//		
		GeoTiffController.createTiff(outputFileName, xsize, ysize, 
				bg.getDataset().GetGeoTransform(), values, Const.NoData, gdalconst.GDT_Int32, bg.getDataset().GetProjection());
		System.out.println("Done");
	}
	private DataObject getDataObject(SpeciesData speciesData, SequentialDatabase database){
		Iterator iterator = database.dataObjectIterator();
		while (iterator.hasNext()) {
			DataObject dataObject = (DataObject) iterator.next();
			Instance instance = dataObject.getInstance();
			boolean is = true;
			for (int i=0;i<3;i++){
				if (!CommonFun.equal(instance.value(i), speciesData.getValues()[i], 100000)){
					is = false;
					break;
				}
			}
			if (is){
				return dataObject;
			}
		}
		return null;
	}
	private void caculateDistance(Instances instances, DBScan dbScan, DenseInstancesExtend denseInstancesExtend) throws Exception{
		dbScan.buildClusterer(instances);
		for (int i=0;i<instances.size();i++){
			DenseInstanceExtend dinstance1 = new DenseInstanceExtend(instances.get(i));
			denseInstancesExtend.addDenseInstanceExtend(dinstance1);
		}
		for (int i=0;i<instances.size()-1;i++){
			DenseInstanceExtend dinstance1 = denseInstancesExtend.getDenseInstanceExtend(i);
			DataObject dataObject1 = dbScan.dataObjectForName(dbScan.getDatabase_distanceType(),
					instances.get(i), Integer.toString(i), dbScan.getDatabase());
			
			for (int j=i+1;j<instances.size();j++){
				DenseInstanceExtend dinstance2 = denseInstancesExtend.getDenseInstanceExtend(j);
				DataObject dataObject2 = dbScan.dataObjectForName(dbScan.getDatabase_distanceType(),
						instances.get(j), Integer.toString(j), dbScan.getDatabase());
				double distance = dataObject1.distance(dataObject2);
				dinstance1.addDistance(distance);
				dinstance2.addDistance(distance);
				//System.out.print(distance + ",");
			}
			//System.out.println();
		}
	}

	public String getOutputFileName() {
		return outputFileName;
	}

	public double getEpsilon() {
		return epsilon;
	}
	
}
