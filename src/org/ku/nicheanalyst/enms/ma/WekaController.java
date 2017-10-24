package org.ku.nicheanalyst.enms.ma;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.gdal.gdal.Band;
import org.gdal.gdal.Dataset;
import org.gdal.gdal.Driver;
import org.gdal.gdal.gdal;
import org.gdal.osr.SpatialReference;
import org.gdal.osr.osr;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import weka.core.Instance;
import weka.core.InstanceComparator;
import weka.core.Instances;

public class WekaController {
	public void CreateTiff(ArrayList<int[]> xy, String resultFile, String maskFile){
		
		gdal.AllRegister();
		Driver driver = gdal.GetDriverByName("GTiff");
		File file = new File(maskFile);
		Dataset poDataset = (Dataset)gdal.Open(maskFile);
		Band band = poDataset.GetRasterBand(1);
		int xsize = band.getXSize();
		int ysize = band.getYSize();
		int[] values = new int[xsize * ysize];
		for (int j=0;j<ysize;j++){
			for (int i=0;i<xsize;i++){
				values[j * xsize + i] = 0;
			}
		}
		for (int[] xyp : xy){
			values[xyp[1] * xsize + xyp[0]] = 255;
		}
		double[] geoTransform = poDataset.GetGeoTransform(); 
		Dataset dataset = driver.Create(resultFile, xsize, ysize, 1, 1);
		SpatialReference spatialReference = new SpatialReference(osr.SRS_WKT_WGS84);
	    String wkt = spatialReference.ExportToWkt();
	    dataset.SetProjection(wkt);
	    
		dataset.SetGeoTransform(geoTransform);
		//dataset.AddBand();
		Band bandtarget = dataset.GetRasterBand(1);
		
		bandtarget.SetNoDataValue(0);
		
		bandtarget.WriteRaster(0, 0, xsize, ysize, values);
		//bandtarget.ComputeStatistics(true);
		bandtarget.FlushCache();
		dataset.FlushCache();
	}
	public Instances removeDuplicateInstance(Instances instances){
		ArrayList<Instance> removedInstances = new ArrayList<Instance>();
		InstanceComparator compare = new InstanceComparator(false);
		//remove duplicate instance
		for (int i=0;i<instances.size()-1;i++){
			for (int j=i+1;j<instances.size();j++){
				if (!removedInstances.contains(instances.instance(i))&&!(removedInstances.contains(instances.instance(j)))){
					if (compare.compare(instances.instance(i), instances.instance(j))==0){
						removedInstances.add(instances.instance(j));
					}
				}
			}
		}
		for (Instance instance : removedInstances){
			instances.remove(instance);
		}
		return instances;
	}
	
	public void createReport(String filename, double epsilon, int minPoint, double buffersize, String log) throws IOException{
		Document out_doc = new Document();
		Element rootElement = new Element("SerializedModel");
		Element element = new Element("Statistics");
		Element element2 = new Element("RocCurve");
		org.jdom.Attribute attribute = new org.jdom.Attribute("Auc", "1"); 
		element2.getAttributes().add(attribute);
		attribute = new org.jdom.Attribute("epsilon", String.valueOf(epsilon)); 
		element2.getAttributes().add(attribute);
		attribute = new org.jdom.Attribute("minPoint", String.valueOf(minPoint)); 
		element2.getAttributes().add(attribute);
		attribute = new org.jdom.Attribute("log", String.valueOf(log)); 
		element2.getAttributes().add(attribute);
		attribute = new org.jdom.Attribute("buffersize", String.valueOf(buffersize)); 
		element2.getAttributes().add(attribute);
		element.getChildren().add(element2);
		rootElement.getChildren().add(element);
		
		
		out_doc.setRootElement(rootElement);
		Format f = Format.getPrettyFormat();
		f.setEncoding("utf-8");
		XMLOutputter outputter = new XMLOutputter(f);
		//logger.info(filename);
		FileWriter file = new FileWriter(filename);
		outputter.output(out_doc, file);
		file.close();
	}
	public void createTiff(int[] values, String maskFile, String resultFile) throws IOException{
		gdal.AllRegister();
		Driver driver = gdal.GetDriverByName("GTiff");
		File file = new File(maskFile);
		Dataset poDataset = (Dataset)gdal.Open(maskFile);
		Band band = poDataset.GetRasterBand(1);
		int xsize = band.getXSize();
		int ysize = band.getYSize();
		
		double[] geoTransform = poDataset.GetGeoTransform(); 
		Dataset dataset = driver.Create(resultFile, xsize, ysize, 1, 1);
		SpatialReference spatialReference = new SpatialReference(osr.SRS_WKT_WGS84);
	    String wkt = spatialReference.ExportToWkt();
	    dataset.SetProjection(wkt);
	    
		dataset.SetGeoTransform(geoTransform);
		//dataset.AddBand();
		Band bandtarget = dataset.GetRasterBand(1);
		
		bandtarget.SetNoDataValue(0);
		
		bandtarget.WriteRaster(0, 0, xsize, ysize, values);
		//bandtarget.ComputeStatistics(true);
		bandtarget.FlushCache();
		dataset.FlushCache();
	}
}
