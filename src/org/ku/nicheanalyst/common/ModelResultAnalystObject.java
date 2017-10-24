package org.ku.nicheanalyst.common;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.ku.nicheanalyst.maps.objects.GeoTiffObject;

public class ModelResultAnalystObject {
	private int resolution;
	private HashMap<Integer, ModelResult> modelResults;
	private double[][] statinfo;
	private int presence_count;
	private int xsize, ysize;
	private double maxvalue;
	private String outputFolder;
	public ModelResultAnalystObject(int resolution,
			GeoTiffObject modelResult, 
			double[][] occurrences, String outputFolder
			){
		this.maxvalue = modelResult.getMaxMin()[1];
		this.modelResults = new HashMap<Integer, ModelResultAnalystObject.ModelResult>(); 
		this.outputFolder = outputFolder;
		this.presence_count = 0;
		this.resolution = resolution;
		this.statinfo = new double[this.resolution + 1][5];
		this.xsize = modelResult.getXSize();
		this.ysize = modelResult.getYSize();
		double[] geoTransfer = modelResult.getDataset().GetGeoTransform();
		HashSet<Integer> trainingSet = new HashSet<Integer>();
		trainingSet.clear();
//		System.out.println("==================");
		for (double[] ll : occurrences){
			int[] xy = CommonFun.LLToPosition(geoTransfer, ll);
			double value = modelResult.readByXY(xy[0], xy[1]);
			
//			System.out.println(String.format("%d,%d;%f,%f;%f", xy[0], xy[1], ll[0], ll[1], value));
			if (CommonFun.equal(value, modelResult.getNoData(), 1000)){
				continue;
			}
			
			Integer label = xy[1] * modelResult.getXSize() + xy[0];
			
			if (!trainingSet.contains(label)){
//				System.out.println(String.format("Add %d,%d;%f,%f;%f;%d", xy[0], xy[1], ll[0], ll[1], value, label));
				
			}
			trainingSet.add(label);
			
		}
		
//		System.out.println("get " + trainingSet.size() + " training samples");
		presence_count = trainingSet.size();
		
		for (int x=0;x<modelResult.getXSize();x++){
			for (int y=0;y<modelResult.getYSize();y++){
				double value = modelResult.readByXY(x, y);
				if (CommonFun.equal(value, modelResult.getNoData(), 1000)){
					continue;
				}
				Integer label = y * modelResult.getXSize() + x;
				ModelResult result = new ModelResult(x, y, value, trainingSet.contains(label));
//				if (result.isTraining()){
//					System.out.println(String.format("x:%d,y:%d,value:%f,isTraining:%b", x, y, value, result.isTraining()));
//				}
				modelResults.put(label, result);
			}
		}
//		CommonFun.writeArray(occurrences, this.outputFolder + "/occurrences.csv", ",");
		
		calculate();
		createPNG();
	}
	

	public double getSNequalSP(){
		double mindiffer = Double.MAX_VALUE;
		double threshold = 0;
		for (int i=0;i<=this.resolution;i++){
			double[] snsp = getSNSP(i);
			double sensitivity = snsp[0];
			double specificity = snsp[1];
			if (mindiffer>(Math.abs(specificity - sensitivity))){
				mindiffer = Math.abs(specificity - sensitivity);
				threshold = statinfo[i][4];
			}
		}
		return threshold;
	}
	public double getMaxSSS(){
		double maxSSS = -1 * Double.MAX_VALUE;
		double threshold = 0;
		for (int i=0;i<=this.resolution;i++){
			double[] snsp = getSNSP(i);
			double sensitivity = snsp[0];
			double specificity = snsp[1];
			if (maxSSS<(specificity + sensitivity)){
				maxSSS = specificity + sensitivity;
				threshold = statinfo[i][4];
			}
		}
		return threshold;
	}
	private void createPNG() {
		CategoryDataset dataset = createDataset();
		JFreeChart chart = createChart(dataset);

		File filename_png = new File(this.outputFolder + "/output_chart.png");
		CommonFun.writeArray(statinfo, this.outputFolder + "/output_chart.csv", ",");
		try {
			ChartUtilities.saveChartAsPNG(filename_png, chart, 980, 550);
		} catch (IOException ex) {
			throw new RuntimeException("Error saving a file", ex);
		}
		
	}
	private double[] getSNSP(int i){
		double a = this.statinfo[i][0];
		double b = this.statinfo[i][1];
		double c = this.statinfo[i][2];
		double d = this.statinfo[i][3];
		double sensitivity = a / (a + c);
		double specificity = d / (b + d);
		return new double[]{sensitivity, specificity};
	}
	private CategoryDataset createDataset() {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		for (int i=0;i<=this.resolution;i++){
			double[] snsp = getSNSP(i);
			double sensitivity = snsp[0];
			double specificity = snsp[1];
			double TSS = sensitivity + specificity - 1;
			dataset.addValue(sensitivity, "Sensitivity", String.valueOf(this.statinfo[i][4]));
			dataset.addValue(specificity, "Specificity", String.valueOf(this.statinfo[i][4]));
			dataset.addValue(TSS, "TSS", String.valueOf(this.statinfo[i][4]));
		}
		return dataset;
	}

	private JFreeChart createChart(CategoryDataset dataset) {

		JFreeChart chart = ChartFactory.createLineChart(
				"Chart", "Threshold", "Value", dataset);

		return chart;
	}


	private void calculate() {
		for (int i=0;i<=resolution;i++){
			double threshold = (double)i/(double)resolution * (maxvalue);
			int a = 0;
			int b = 0;
			int c = 0;
			int d = 0;
			int absence_count = 0;
			for (Integer label : this.modelResults.keySet()){
				ModelResult result = modelResults.get(label);
				if (result.isTraining()){
					if (result.getValue()>=threshold){
						a++;
					}else{
						c++;
					}
				}
			}
			HashSet<Integer> selectedLabels = new HashSet<Integer>();
			while (absence_count<presence_count){
				int x = new Random().nextInt(this.xsize);
				int y = new Random().nextInt(this.ysize);
				Integer label = y * xsize + x;
				if (selectedLabels.contains(label)){
					continue;
				}
				
				if (modelResults.containsKey(label)){
					ModelResult result = modelResults.get(label);
					if (!result.isTraining()){
						absence_count++;
						if (result.getValue()>=threshold){
							b++;
						}else{
							d++;
						}
					}
				}
			}
			statinfo[i] = new double[]{a, b, c, d, threshold};
		}
		
	}


	class ModelResult{
		private int x;
		private int y;
		private double value;
		private boolean training;
		public ModelResult(int x, int y, double value, boolean is_training){
			this.x = x;
			this.y = y;
			this.value = value;
			this.training = is_training;
		}
		public int getX() {
			return x;
		}
		public int getY() {
			return y;
		}
		public double getValue() {
			return value;
		}
		public boolean isTraining() {
			return training;
		}
		
	}
	  
}
