package org.ku.nicheanalyst.common;

import java.util.HashSet;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.ku.nicheanalyst.maps.objects.GeoTiffObject;

public class ThresholdMethod {
	public final static int MAX_Kappa_Value = 0;
	public final static int MINIMUM_TRAINING_PRESENCE = 1;
	public final static int PERCENTILE_TRAINING_PRESENCE = 2;
	public final static int MEAN_TRAINING_PRESENCE = 3;
	public final static int MEAN_OF_LOWER_PERCENTILE_TRAINING_PRESENCE = 4;
	public final static int MEAN_OF_HIGHER_PERCENTILE_TRAINING_PRESENCE = 5;
	public final static int FIXED_VALUE = 8;
	public final static int PERCENTILE_OF_MODEL = 6;
	public final static int Maximum_True_Skill_Statistic = 9;
	public final static int Equal_Sensitivity_Specificity = 10;
	public final static int MEAN_OF_MODEL = 7; 
	
	private int method;
	//value for Threshold method 
	private double value;
	//threshold for the model result
	//private double internalThreshold;
	private boolean removeZero;
	public ThresholdMethod (String method, double value, boolean removeZero){
		if (method.equalsIgnoreCase(Message.getString("MAX_Kappa_Value"))){
			this.method = MAX_Kappa_Value;
		}
		if (method.equalsIgnoreCase(Message.getString("MINIMUM_TRAINING_PRESENCE"))){
			this.method = MINIMUM_TRAINING_PRESENCE;
		}
		if (method.equalsIgnoreCase(Message.getString("PERCENTILE_TRAINING_PRESENCE"))){
			this.method = PERCENTILE_TRAINING_PRESENCE;
		}
		if (method.equalsIgnoreCase(Message.getString("MEAN_TRAINING_PRESENCE"))){
			this.method = MEAN_TRAINING_PRESENCE;
		}
		if (method.equalsIgnoreCase(Message.getString("MEAN_OF_LOWER_PERCENTILE_TRAINING_PRESENCE"))){
			this.method = MEAN_OF_LOWER_PERCENTILE_TRAINING_PRESENCE;
		}
		if (method.equalsIgnoreCase(Message.getString("MEAN_OF_HIGHER_PERCENTILE_TRAINING_PRESENCE"))){
			this.method = MEAN_OF_HIGHER_PERCENTILE_TRAINING_PRESENCE;
		}
		if (method.equalsIgnoreCase(Message.getString("FIXED_VALUE"))){
			this.method = FIXED_VALUE;
		}
		if (method.equalsIgnoreCase(Message.getString("PERCENTILE_OF_MODEL"))){
			this.method = PERCENTILE_OF_MODEL;
		}
		if (method.equalsIgnoreCase(Message.getString("Maximum_True_Skill_Statistic"))){
			this.method = Maximum_True_Skill_Statistic;
		}
		if (method.equalsIgnoreCase(Message.getString("Equal_Sensitivity_Specificity"))){
			this.method = Equal_Sensitivity_Specificity;
		}
		if (method.equalsIgnoreCase(Message.getString("MEAN_OF_MODEL"))){
			this.method = MEAN_OF_MODEL;
		}
		this.value = value;
		this.removeZero = removeZero;
	}

	public int getMethod() {
		return method;
	}

	public double getValue() {
		return value;
	}
	public double getThreshold(GeoTiffObject modelResult, double[][] occurrences, String outputFolder){
		ModelResultAnalystObject analyst = new ModelResultAnalystObject(1000, modelResult, occurrences, outputFolder);
		if (this.method==Equal_Sensitivity_Specificity){
			return analyst.getSNequalSP();
		}
		if (this.method==Maximum_True_Skill_Statistic){
			return analyst.getMaxSSS();
		}
		return 0;
	}
	public double getThreshold(double[] initvalues){
		int dataCount = 0;
		for (double value : initvalues){
			if (removeZero){
				if (value>0){
					dataCount++;
				}
			}else{
				if (value>=0){
					dataCount++;
				}
			}
		}
		double[] values = new double[dataCount];
		dataCount = 0;
		for (double value : initvalues){
			if (removeZero){
				if (value>0){
					values[dataCount] = value;
					dataCount++;
				}
			}else{
				if (value>=0){
					values[dataCount] = value;
					dataCount++;
				}
			}
		}
		DescriptiveStatistics stat = new DescriptiveStatistics(values);
		if (this.method==MAX_Kappa_Value){
			return 0;
		}
		if (this.method==MINIMUM_TRAINING_PRESENCE){
			return stat.getMin();
		}
		if ((this.method==PERCENTILE_TRAINING_PRESENCE)||(this.method==PERCENTILE_OF_MODEL)){
			if (value<=0){
				value = 1;
			}
			if (value>=100){
				value = 100;
			}
			return stat.getPercentile(value);
		}
		if ((this.method==MEAN_TRAINING_PRESENCE)||(this.method==MEAN_OF_MODEL)){
			return stat.getMean();
		}
		if (this.method==MEAN_OF_LOWER_PERCENTILE_TRAINING_PRESENCE){
			if (value<=0){
				value = 1;
			}
			if (value>=100){
				value = 100;
			}
			double threshold = stat.getPercentile(value);
			double summary = 0;
			double count = 0;
			for (double value : values){
				if (value<=threshold){
					summary += value;
					count = count +1;
				}
			}
			return summary / count;
		}
		if (this.method==MEAN_OF_HIGHER_PERCENTILE_TRAINING_PRESENCE){
			if (value<=0){
				value = 1;
			}
			if (value>=100){
				value = 100;
			}
			double threshold = stat.getPercentile(value);
			double summary = 0;
			double count = 0;
			for (double value : values){
				if (value>=threshold){
					summary += value;
					count = count +1;
				}
			}
			return summary / count;
		}
		
		return 0;
	}
	public double getThreshold(){
		if (this.method==FIXED_VALUE){
			return value;
		}
		return value;
	}
	public String toString(){
		if (this.method==MAX_Kappa_Value){
			return Message.getString("MAX_Kappa_Value");
		}
		if (this.method==MINIMUM_TRAINING_PRESENCE){
			return Message.getString("MINIMUM_TRAINING_PRESENCE");
		}
		if (this.method==PERCENTILE_TRAINING_PRESENCE){
			return Message.getString("PERCENTILE_TRAINING_PRESENCE").replaceAll("x%", value + "%");
		}
		if (this.method==MEAN_TRAINING_PRESENCE){
			return Message.getString("MEAN_TRAINING_PRESENCE");
		}
		if (this.method==MEAN_OF_LOWER_PERCENTILE_TRAINING_PRESENCE){
			return Message.getString("MEAN_OF_LOWER_PERCENTILE_TRAINING_PRESENCE").replace("x%", value + "%");
		}
		if (this.method==MEAN_OF_HIGHER_PERCENTILE_TRAINING_PRESENCE){
			return Message.getString("MEAN_OF_HIGHER_PERCENTILE_TRAINING_PRESENCE").replace("x%", value + "%");
		}
		if (this.method==FIXED_VALUE){
			return Message.getString("FIXED_VALUE").replace("(x)", String.valueOf(value));
		}
		if (this.method==FIXED_VALUE){
			return Message.getString("FIXED_VALUE").replace("(x)", String.valueOf(value));
		}
		if (this.method==MEAN_OF_MODEL){
			return Message.getString("MEAN_OF_MODEL");
		}
		if (this.method==PERCENTILE_OF_MODEL){
			return Message.getString("PERCENTILE_OF_MODEL").replace("x%", value + "%");
		}
		
		if (this.method==Maximum_True_Skill_Statistic){
			return Message.getString("Maximum_True_Skill_Statistic").replace("x%", value + "%");
		}
		if (this.method==Equal_Sensitivity_Specificity){
			return Message.getString("Equal_Sensitivity_Specificity").replace("x%", value + "%");
		}
		return "";
	}
}
