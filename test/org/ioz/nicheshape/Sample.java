package org.ioz.nicheshape;

import java.io.IOException;
import java.util.HashSet;
import java.util.Random;

import org.junit.Test;
import org.ku.nicheanalyst.common.CommonFun;
import org.ku.nicheanalyst.common.Const;
import org.ku.nicheanalyst.ui.display.component.j3d.ConvesHullFace;

public class Sample {
	@Test
	public void createSample() throws IOException{
		int dem = 10;
		int range = 1;
		int number_of_sample = 10000;
		double[][] samples = new double[number_of_sample][dem + 4];
		for (int i=0;i<dem;i++){
			for (int j=0;j<number_of_sample;j++){
				samples[j][i] = Math.random();
				samples[j][dem+1] = 1;
			}
			
		}
		
		//select 100 of the sample randomly
		int sample_size = 10;
		double[][] subsamples = new double[sample_size][dem + 4];
		HashSet<Integer> current_samples = new HashSet<Integer>();
		while (current_samples.size()<sample_size){
			Integer index = new Random().nextInt(number_of_sample);
			if (!current_samples.contains(index)){
				boolean is_sampled = true;
				for (int i=0;i<dem;i++){
					if ((samples[index.intValue()][i]>0.80)||((samples[index.intValue()][i]<0.20))){
						is_sampled = false;
					}
				}
				if (!is_sampled){
					continue;
				}
				current_samples.add(index);
				System.out.println(String.format("%d,%d", current_samples.size() - 1, index.intValue()));
				samples[index.intValue()][dem] = 1;
				subsamples[current_samples.size() - 1] = samples[index.intValue()];
				
			}
				
		}
		//define a convex hull
		
		//define an elipsoid
		
		StringBuilder sb = new StringBuilder();
		sb.append("ID,");
		for (int i=0;i<dem;i++){
			sb.append("V" + i + ",");
		}
	
		sb.append("is_sample,in_box,in_ellipsoid,in_convex_hull,in_hypervolume" + Const.LineBreak);
		for (int j=0;j<number_of_sample;j++){
			sb.append((j + 1) + ",");
			for (int i=0;i<dem + 4;i++){
				if (i==(dem + 3)){
					sb.append(samples[j][i]);
				}else{
					sb.append(samples[j][i] + ",");
				}
			}
			sb.append(Const.LineBreak);
		}
		CommonFun.writeFile(sb.toString(), "/Users/huijieqiao/Dropbox/Experiments/NicheShape/sample.csv");
	}
	public void matrixTest(){
		double[][] x = new double[2][2];
		x[0][0] = -0.2884066;
		x[0][1] = -0.5659026;
		x[1][0] = 0.2017121;
		x[1][1] = 0.2017121;
	}
}
