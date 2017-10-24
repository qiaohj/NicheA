package org.ioz.beta;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import org.junit.Test;
import org.ku.nicheanalyst.common.CommonFun;
import org.ku.nicheanalyst.common.Const;

public class VariableGenerator {
	@Test
	public void calculateEDistance() throws IOException{
		StringBuilder sb = new StringBuilder();
		sb.append("Method,CC,Type,Possibility_Scale,Distance_Pred_Y_Plus,Distance_Pred_Y_Multi" + Const.LineBreak);
		int steps = 100;
		File folder = new File("/Users/huijieqiao/ENM_Shape/Variables/results");
		for (File f : folder.listFiles()){
			if (f.getName().endsWith(".csv")){
				String[] fileInfo = f.getName().replace(".csv", "").split("_");
				String type = (fileInfo.length==3)?"self":"all";
				String model = fileInfo[2];
				double cc = Double.valueOf(fileInfo[0].replace("P", "").replace("N", "-")).doubleValue() / 10d;
				ArrayList<String> valuesStr = CommonFun.readFromFile(f.getAbsolutePath());
				double distance_train_Plus = 0;
				double distance_test_Plus = 0;
				double distance_train_Multi = 0;
				double distance_test_Multi = 0;
				double[] distance_train_Plus_step = new double[steps];
				double[] distance_test_Plus_step = new double[steps];
				double[] distance_train_Multi_step = new double[steps];
				double[] distance_test_Multi_step = new double[steps]; 
				for (String vStr : valuesStr){
					String[] temp = vStr.split(",");
					if (temp.length==0){
						continue;
					}
					if (!CommonFun.isInteger(temp[0])){
						continue;
					}
					double y1 = Double.valueOf(temp[4]).doubleValue();
					double y2 = Double.valueOf(temp[5]).doubleValue();
					boolean isTraining = temp[11].equals("1");
					double pred_Y_Plus = Double.valueOf(temp[23]).doubleValue();
					double pred_Y_Multi = Double.valueOf(temp[24]).doubleValue();
					double distance_plus = Math.pow(y1 + y2 - pred_Y_Plus, 2);
					double distance_multi = Math.pow(y1 * y2 - pred_Y_Multi, 2);
					if (isTraining){
						distance_train_Plus += distance_plus;
						distance_train_Multi += distance_multi;
					}
					distance_test_Plus += distance_plus;
					distance_test_Multi += distance_multi;
					for (int i=1;i<steps;i++){
						double y_value = (double)(i * 4)/100d;
						if ((y1 + y2) > y_value){
							if (isTraining){
								distance_train_Plus_step[i] += distance_plus;
								distance_train_Multi_step[i] += distance_multi;
							}
							distance_test_Plus_step[i] += distance_plus;
							distance_test_Multi_step[i] += distance_multi;
						}
					}
				}
				distance_train_Plus_step[0] = distance_train_Plus;
				distance_train_Multi_step[0] = distance_train_Multi;
				distance_test_Plus_step[0] = distance_test_Plus;
				distance_test_Multi_step[0] = distance_test_Multi;
				for (int i=0;i<steps;i++){
					distance_train_Plus_step[i] = Math.sqrt(distance_train_Plus_step[i]);
					distance_train_Multi_step[i] = Math.sqrt(distance_train_Multi_step[i]);
					distance_test_Plus_step[i] = Math.sqrt(distance_test_Plus_step[i]);
					distance_test_Multi_step[i] = Math.sqrt(distance_test_Multi_step[i]);
				}
				for (int i=0;i<steps;i++){
//					sb.append("Method,CC,Type,CC_Scale,Distance_Pred_Y_Plus,Distance_Pred_Y_Multi" + Const.LineBreak);
					sb.append(String.format("%s,%f,%s,%f,%f,%f%n", model, cc, type, (double)(i * 4)/100d, 
							distance_test_Plus_step[i], distance_test_Multi_step[i]));
					if (type.equals("self")){
						sb.append(String.format("%s,%f,%s,%f,%f,%f%n", model, cc, "train", (double)(i * 4)/100d, 
								distance_train_Plus_step[i], distance_train_Multi_step[i]));
					}
				}
			}
			
		}
		CommonFun.writeFile(sb.toString(), "/Users/huijieqiao/ENM_Shape/Variables/distance.csv");
		
		String t = "8.13661e-07";
		double v = Double.valueOf(t);
		System.out.println(v);
	}
	@Test
	public void rScript() throws IOException{
		StringBuilder sb = new StringBuilder();
		String folder = "/Users/huijieqiao/ENM_Shape/Variables";
		ArrayList<String> rScript = CommonFun.readFromFile("/Users/huijieqiao/ENM_Shape/scripts/Correlation_model.r");
		File csvFile = new File(folder);
		for (File f : csvFile.listFiles()){
			if (f.getName().endsWith(".csv")){
				String dataSetName = f.getName().replace(".csv", "");
				//double r = Double.valueOf(dataSetName.replace("N", "-").replace("P", "")) / 10d;
//				for (int i=1;i<=5;i++){
				for (int i=1;i<=1;i++){
					for (String r : rScript){
						sb.append(
								r.replace("@folder", folder)
									.replace("@dataSetName", dataSetName)
									.replace("@sp", String.valueOf(i)) + Const.LineBreak);
					}
				}
			}
		}
		CommonFun.writeFile(sb.toString(), "/Users/huijieqiao/ENM_Shape/scripts/final_Correlation_model_script.r");
	}
	
	@Test
	public void randomNormal() throws IOException{
		
		 NormalDistribution norm = new NormalDistribution(0d, 0.2);
		 int length = 100 * 100;
		 double[] x1 = new double[length];
		 double[] x2 = new double[length];
		 double[] x2_new = new double[length];
		 double[] y1 = new double[length];
		 double[] y2 = new double[length];
		 int[] sp1 = new int[length];
		 int[] sp2 = new int[length];
		 int[] sp3 = new int[length];
		 int[] sp4 = new int[length];
		 int[] sp5 = new int[length];
		 int[] sp1_sample = new int[length];
		 int[] sp2_sample = new int[length];
		 int[] sp3_sample = new int[length];
		 int[] sp4_sample = new int[length];
		 int[] sp5_sample = new int[length];
		 int[] sp1_sample_out = new int[length];
		 int[] sp2_sample_out = new int[length];
		 int[] sp3_sample_out = new int[length];
		 int[] sp4_sample_out = new int[length];
		 int[] sp5_sample_out = new int[length];
		 int step = 10;
		 double sample_persent = 0.1;
		 double sample_persent_out = 0.01;
		 double sample_donut_limit = 0.5;
		 double x_limit = 1;
		 double y_value = norm.density(x_limit) * 2;
		 for (int i=0;i<length;i++){
				double x1_item = Math.random() * 2d - 1;
				x1[i] = x1_item;
				double x2_item = Math.random() * 2d - 1;
				x2[i] = x2_item;
				sp1[i] = 0;
				sp2[i] = 0;
				sp3[i] = 0;
				sp4[i] = 0;
				sp5[i] = 0;
				
		 }
		 PearsonsCorrelation corr = new PearsonsCorrelation();
		 for (int p=-1 * step;p<=step;p++){
			double p_value = ((double)p)/10d;
			StringBuilder sb = new StringBuilder();
			sb.append("NO\tX1\tX2\tX2_New\tY1\tY2\t" +
					"SP1\tSP2\tSP3\tSP4\tSP5\t" +
					"SP1_SAMPLE\tSP2_SAMPLE\tSP3_SAMPLE\tSP4_SAMPLE\tSP5_SAMPLE\t" + 
					"SP1_SAMPLE_OUT\tSP2_SAMPLE_OUT\tSP3_SAMPLE_OUT\tSP4_SAMPLE_OUT\tSP5_SAMPLE_OUT\t"+ 
					Const.LineBreak);
			for (int i=0;i<length;i++){
				double x1_item = x1[i];
				double x2_item = x2[i];
				x2_new[i] = p_value * x1_item + Math.sqrt(1 - Math.pow(p_value, 2)) * x2_item;
				y1[i] = norm.density(x1_item);
				y2[i] = norm.density(x2_new[i]);
				if ((y1[i] + y2[i])>y_value){
					sp1[i] = 1;
					if (x1_item>0){
						sp2[i] = 1;
						if (x2_item>0){
							sp3[i] = 1;
						}
						//if ((x2_item>(-1 * Math.sin(Math.PI/4d) * x_limit))&&(x2_item<(Math.sin(Math.PI/4d) * x_limit))){
							if (x1_item>=Math.abs(x2_item)){
								sp4[i] = 1;
							}
						//}
					}
					if (Math.sqrt(Math.pow(x1_item, 2) + Math.pow(x2_item, 2))>=sample_donut_limit){
						sp5[i] = 1;
					}
				}
				
				if (Math.random() < sample_persent){
					sp1_sample[i] = sp1[i];
					sp2_sample[i] = sp2[i];
					sp3_sample[i] = sp3[i];
					sp4_sample[i] = sp4[i];
					sp5_sample[i] = sp5[i];
					sp1_sample_out[i] = sp1[i];
					sp2_sample_out[i] = sp2[i];
					sp3_sample_out[i] = sp3[i];
					sp4_sample_out[i] = sp4[i];
					sp5_sample_out[i] = sp5[i];
					
				}else{
					if (Math.random()<sample_persent_out){
						sp1_sample_out[i] = sp1[i];
						sp2_sample_out[i] = sp1[i];
						sp3_sample_out[i] = sp1[i];
						sp4_sample_out[i] = sp1[i];
						sp5_sample_out[i] = sp1[i];
					}else{
						sp1_sample_out[i] = 0;
						sp2_sample_out[i] = 0;
						sp3_sample_out[i] = 0;
						sp4_sample_out[i] = 0;
						sp5_sample_out[i] = 0;
					}
					sp1_sample[i] = 0;
					sp2_sample[i] = 0;
					sp3_sample[i] = 0;
					sp4_sample[i] = 0;
					sp5_sample[i] = 0;
				}
				
				sb.append(String.format("%d\t%f\t%f\t%f\t%f\t%f\t%d\t%d\t%d\t%d\t%d\t%d\t%d\t%d\t%d\t%d\t%d\t%d\t%d\t%d\t%d%n", 
						i, x1_item, x2_item, x2_new[i], y1[i], y2[i], sp1[i], sp2[i], sp3[i], sp4[i], sp5[i],
						sp1_sample[i], sp2_sample[i], sp3_sample[i], sp4_sample[i], sp5_sample[i],
						sp1_sample_out[i], sp2_sample_out[i], sp3_sample_out[i], sp4_sample_out[i], sp5_sample_out[i]
						));
			}
			System.out.println(String.format("%f, %d", corr.correlation(x1, x2_new), p));
			String filename =  "/Users/huijieqiao/ENM_Shape/Variables/";
			filename += (p>0)?"P":"N";
			filename += CommonFun.Integer2String(Math.abs(p), 3) + ".csv";
			CommonFun.writeFile(sb.toString(), filename);
		 }
		 
		 
		 
	}
	@Test
	public void getyValue(){
		NormalDistribution norm = new NormalDistribution(0d, 0.2);
		System.out.println(norm.density(0.1));
	}
	@Test
	public void generateVariablePlot(){
		String sb = "";
//		sb += "dat@N = read.table('/Users/huijieqiao/ENM_Shape/Variables/@N.csv', header=T)" +Const.LineBreak;
//		sb += "dat@N = cbind(dat@N, Y_Plus=dat@N$Y1 + dat@N$Y2)" +Const.LineBreak;
//		sb += "dat@N = cbind(dat@N, Y_Multi=dat@N$Y1 * dat@N$Y2)" +Const.LineBreak;
//		sb += "Y_Plus_Norm@N=1 - fun(dat@N$Y_Plus);" + Const.LineBreak;
//		sb += "plot(dat@N$X1, dat@N$X2_New, col=grey(Y_Plus_Norm@N), xlim=c(-1, 1), ylim=c(-1, 1))";
		sb += "Y_Multi_Norm@N=1 - fun(dat@N$Y_Multi);" + Const.LineBreak;
		sb += "plot(dat@N$X1, dat@N$X2_New, col=heat.colors(length(Y_Multi_Norm@N))[rank(Y_Multi_Norm@N)], xlim=c(-1, 1), ylim=c(-1, 1))";
		for (int i=-10;i<=10;i++){
			String title = (i>0)?"P":"N";
			title += CommonFun.Integer2String(Math.abs(i), 3);
			System.out.println(sb.replace("@N", title));
		}
	}
}
