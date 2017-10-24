package org.ku.nicheanalyst.common;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

public class AUCResult {
	private HashMap<Double, Double> roc;
	private double E;
	private double Ex;
	private double Ey;
	private double auc;
	private double fixedAUC;
	private double aucNull;
	private String id;
	private String enmResult;
	private HashSet<double[]> pointlist;
	private HashSet<double[]> point_nulllist;
	public AUCResult(double E){
		this.E = E;
		pointlist = new HashSet<double[]>();
		point_nulllist = new HashSet<double[]>();
	}
	
	public double getAucNull() {
		return aucNull;
	}

	public void setAucNull(double aucNull) {
		this.aucNull = aucNull;
	}

	public double getE() {
		return E;
	}

	public void setE(double e) {
		E = e;
	}

	public HashSet<double[]> getPointlist() {
		return pointlist;
	}

	public HashMap<Double, Double> getRoc() {
		return roc;
	}
	public String getEnmResult() {
		return enmResult;
	}

	public void setEnmResult(String enmResult) {
		this.enmResult = enmResult;
	}

	public void setRoc(HashMap<Double, Double> roc) {
		this.roc = roc;
	}
	public double getEx() {
		return Ex;
	}
	public void setEx(double ex) {
		Ex = ex;
	}
	public double getEy() {
		return Ey;
	}
	public void setEy(double ey) {
		Ey = ey;
	}
	public double getAuc() {
		return auc;
	}
	public void setAuc(double auc) {
		this.auc = auc;
	}
	public double getFixedAUC() {
		return fixedAUC;
	}
	public void setFixedAUC(double fixedAUC) {
		this.fixedAUC = fixedAUC;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public void addPoint(double[] p1, double[] p2, double[] p3, double[] p4){
		pointlist.add(new double[]{p1[0], p1[1], p2[0], p2[1], p3[0], p3[1], p4[0], p4[1]});
	}
	
	public void addPointNull(double[] p1, double[] p2, double[] p3, double[] p4){
		point_nulllist.add(new double[]{p1[0], p1[1], p2[0], p2[1], p3[0], p3[1], p4[0], p4[1]});
	}
	public double getRatio(){
		return this.auc/this.aucNull;
	}
	public String save(String target) throws IOException {
		File f = new File(target);
		if (!f.exists()){
			CommonFun.mkdirs(f.getAbsolutePath(), true);
		}
		StringBuilder sb_html = new StringBuilder();
		
		sb_html.append("<h3>ENM result:</h3>" + Const.LineBreak);
		sb_html.append(this.enmResult + Const.LineBreak);
		sb_html.append("<h3>E:</h3>" + Const.LineBreak);
		sb_html.append(this.E + Const.LineBreak);
		sb_html.append("<h3>Partial AUC:</h3>" + Const.LineBreak);
		sb_html.append(this.auc + Const.LineBreak);
		sb_html.append("<h3>AUC null:</h3>" + Const.LineBreak);
		sb_html.append(this.aucNull + Const.LineBreak);
		sb_html.append("<h3>Ratio of observed to null expectations:</h3>" + Const.LineBreak);
		sb_html.append(getRatio() + Const.LineBreak);
		sb_html.append("<h3>Fixed partial AUC:</h3>" + Const.LineBreak);
		sb_html.append(this.fixedAUC + Const.LineBreak);
		
		sb_html.append("<h3>Figure. Partial ROC</h3>" + Const.LineBreak);
		sb_html.append("<img width=600 src='file://localhost/" + target + "/roc." + this.E + ".png'/>");
		
		

		
		StringBuilder sb = new StringBuilder();
		sb.append("ProportionOfPredictedArea,1-omission" + Const.LineBreak);
		Object [] keys = roc.keySet().toArray();
		Arrays.sort(keys);
		for (int i=0;i<keys.length;i++){
			Double p = (Double) keys[i];
			sb.append(String.format("%f,%f%n", p, roc.get(p)));
		}
		
		CommonFun.writeFile(sb.toString(), target + "/roc." + this.E + ".csv");
		sb = new StringBuilder();
		sb.append("Ex,Ey,E,auc,fixedauc,aucNULL,tail" + Const.LineBreak);
		sb.append(String.format("%f,%f,%f,%f,%f,%f,%f%n", Ex, Ey, E, auc, fixedAUC, aucNull, 1f));
		CommonFun.writeFile(sb.toString(), target + "/conf." + this.E + ".csv");
		
		sb = new StringBuilder();
		sb.append("x,y,group" + Const.LineBreak);
		int group=1;
		for (double[] p : pointlist){
			sb.append(String.format("%f,%f,%d%n", p[0], p[1], group));
			sb.append(String.format("%f,%f,%d%n", p[2], p[3], group));
			sb.append(String.format("%f,%f,%d%n", p[4], p[5], group));
			sb.append(String.format("%f,%f,%d%n", p[6], p[7], group));
			group++;
		}
		CommonFun.writeFile(sb.toString(), target + "/points." + this.E + ".csv");
		
		sb = new StringBuilder();
		sb.append("x,y,group" + Const.LineBreak);
		group=1;
		for (double[] p : point_nulllist){
			sb.append(String.format("%f,%f,%d%n", p[0], p[1], group));
			sb.append(String.format("%f,%f,%d%n", p[2], p[3], group));
			sb.append(String.format("%f,%f,%d%n", p[4], p[5], group));
			sb.append(String.format("%f,%f,%d%n", p[6], p[7], group));
			group++;
		}
		CommonFun.writeFile(sb.toString(), target + "/points_null." + this.E + ".csv");
		
		sb = new StringBuilder();
		sb.append("x,y,group" + Const.LineBreak);
		group=1;
		sb.append(String.format("%f,%f,%d%n", Ex, 0d, group));
		sb.append(String.format("%f,%f,%d%n", 1d, 0d, group));
		sb.append(String.format("%f,%f,%d%n", 1d, 1d, group));
		sb.append(String.format("%f,%f,%d%n", Ex, 1d, group));
		CommonFun.writeFile(sb.toString(), target + "/points_all." + this.E + ".csv");
		
		InputStream rscript = this.getClass().getResourceAsStream("/roc.r");
		HashMap<String, String> parameters = new HashMap<String, String>();
		parameters.put("@Target", target.replace("\\", "/"));
		parameters.put("@Threshold", String.valueOf(this.E));
		
		HashSet<String> libraries = new HashSet<String>();
		libraries.add("ggplot2");

		
		String r_script = CommonFun.RunRScript(rscript, parameters, libraries, true, target + "/rscript." + this.E + ".r");
		
		
		
		return sb_html.toString();
	}

}