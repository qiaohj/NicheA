package org.ku.chart;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.junit.Test;
import org.ku.nicheanalyst.common.CommonFun;
import org.ku.nicheanalyst.common.ModelResultAnalystObject;
import org.ku.nicheanalyst.maps.objects.GeoTiffObject;

public class ChartTest {
	@Test
	public void test() {
		CategoryDataset dataset = createDataset();
		JFreeChart chart = createChart(dataset);

		File filename_png = new File("/Users/huijieqiao/temp/Test.png");

		try {
			ChartUtilities.saveChartAsPNG(filename_png, chart, 980, 550);
		} catch (IOException ex) {
			throw new RuntimeException("Error saving a file", ex);
		}
	}

	private static CategoryDataset createDataset() {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		dataset.addValue(0.5, "series1", "1");
		dataset.addValue(0.3, "series1", "2");
		dataset.addValue(0.3, "series2", "1");
		dataset.addValue(0.5, "series2", "2");
		return dataset;
	}

	private static JFreeChart createChart(CategoryDataset dataset) {

		JFreeChart chart = ChartFactory.createLineChart(
				"SN/SP", "Threshold", "Value", dataset);

		return chart;
	}
	@Test
	public void testModel() throws IOException{
		GeoTiffObject modelResult = new GeoTiffObject("/Users/huijieqiao/Dropbox/Papers/NicheA_Case_Study/StudyCase3/Maxent/Sciurus_carolinensis.tif");
		ArrayList<String> lls = CommonFun.readFromFile("/Users/huijieqiao/Dropbox/Papers/NicheA_Case_Study/StudyCase3/Cae3-xyRandm-Environmet/Occurrences.csv");
		ArrayList<double[]> occ = new ArrayList<double[]>();
		for (String ll : lls){
			String[] llsplit = ll.replace("\t", ",").split(",");
			if (llsplit.length==2){
				if (CommonFun.isDouble(llsplit[0])&&(CommonFun.isDouble(llsplit[1]))){
					double[] o = {Double.valueOf(llsplit[0]).doubleValue(), Double.valueOf(llsplit[1]).doubleValue()};
					occ.add(o);
				}
			}
			if (llsplit.length==3){
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
		String outputFolder = "/Users/huijieqiao/Dropbox/Papers/NicheA_Case_Study/StudyCase3/test";
		for (int i=0;i<2;i++){
			ModelResultAnalystObject obj = new ModelResultAnalystObject(100, modelResult, occurrences, outputFolder + "/" + (i+1));
			if (i==0){
				System.out.println(obj.getMaxSSS());
			}else{
				System.out.println(obj.getSNequalSP());
			}
		}
	}
}
