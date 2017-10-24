package org.ku.nicheanalyst.dataset;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.jdom.Element;
import org.jdom.JDOMException;
import org.ku.nicheanalyst.common.CommonFun;
import org.ku.nicheanalyst.common.Message;

import edu.hws.jcm.data.Function;

public class GompertzCurveParameters {
	private double b1;
	private double c1;
	private double b2;
	private double c2;
	private int sampling_frequency;
	
	private Function func1;
	private Function func2;
	private String func1Str;
	private String func2Str;
	private int startingPoint;
	private int cold_years;
	private int warm_years;
	public GompertzCurveParameters(String filename) throws FileNotFoundException, JDOMException, IOException{
		this.loadFromXML(filename);
	}
	public GompertzCurveParameters(
			double b1, double c1, double b2, double c2,
			int sampling_frequency, 
			Function func1, Function func2, 
			String func1Str, String func2Str, 
			int startingPoint,
			int cold_years, int warm_years) {
		super();
		this.b1 = b1;
		this.c1 = c1;
		this.b2 = b2;
		this.c2 = c2;
		this.sampling_frequency = sampling_frequency;

		this.func1 = func1;
		this.func2 = func2;
		this.func1Str = func1Str;
		this.func2Str = func2Str;
		this.startingPoint = startingPoint;
		this.cold_years = cold_years;
		this.warm_years = warm_years;
	}
	public double getB1() {
		return b1;
	}
	public double getC1() {
		return c1;
	}
	public double getB2() {
		return b2;
	}
	public double getC2() {
		return c2;
	}
	public int getSamplingFrequency() {
		return sampling_frequency;
	}
	
	public Function getFunc1() {
		return func1;
	}
	public Function getFunc2() {
		return func2;
	}
	
	public String getDescription() {
		StringBuilder sb = new StringBuilder();
		sb.append(String.format(Message.getString("cold_func") + " %s%n", func1Str));
		sb.append(String.format("b: %f%n", b1));
		sb.append(String.format("c: %f%n", c1));
		sb.append(String.format(Message.getString("cold_years") + " %d%n", cold_years));
		sb.append(String.format("%n"));
		sb.append(String.format(Message.getString("warm_func") + " %s%n", func2Str));
		sb.append(String.format("b: %f%n", b2));
		sb.append(String.format("c: %f%n", c2));
		sb.append(String.format(Message.getString("warm_years") + " %d%n", warm_years));
		sb.append(String.format("%n"));
		sb.append(String.format(Message.getString("sampling_frequency") + " %d%n", sampling_frequency));
		sb.append(String.format(Message.getString("starting_point") + " %d%n", startingPoint));
		return sb.toString();
	}
	public void saveToXML(String filename) throws IOException{
		Element root = new Element("root");
		Element cold_func = new Element("cold_func");
		cold_func.setText(this.func1Str);
		Element b = new Element("b");
		b.setText(String.valueOf(this.b1));
		cold_func.getChildren().add(b);
		Element c = new Element("c");
		c.setText(String.valueOf(this.c1));
		cold_func.getChildren().add(c);
		Element years = new Element("years");
		years.setText(String.valueOf(this.cold_years));
		cold_func.getChildren().add(years);
		root.getChildren().add(cold_func);
		
		Element warm_func = new Element("warm_func");
		warm_func.setText(this.func2Str);
		b = new Element("b");
		b.setText(String.valueOf(this.b2));
		warm_func.getChildren().add(b);
		c = new Element("c");
		c.setText(String.valueOf(this.c2));
		warm_func.getChildren().add(c);
		years = new Element("years");
		years.setText(String.valueOf(this.warm_years));
		warm_func.getChildren().add(years);
		root.getChildren().add(warm_func);
		
		Element sampling_frequency = new Element("sampling_frequency");
		sampling_frequency.setText(String.valueOf(this.sampling_frequency));
		root.getChildren().add(sampling_frequency);
		
	
		Element startingPoint = new Element("startingPoint");
		startingPoint.setText(String.valueOf(this.startingPoint));
		root.getChildren().add(startingPoint);
		
		CommonFun.writeXML(root, filename);
		
	}
	public void loadFromXML(String filename) throws FileNotFoundException, JDOMException, IOException{
		Element root = CommonFun.readXML(filename);
		Element cold_func = root.getChild("cold_func");
		this.func1Str = cold_func.getText().trim();
		this.b1 = Double.valueOf(cold_func.getChildText("b")).doubleValue();
		this.c1 = Double.valueOf(cold_func.getChildText("c")).doubleValue();
		this.cold_years = Integer.valueOf(cold_func.getChildText("years")).intValue();
		
		Element warm_func = root.getChild("warm_func");
		this.func2Str = warm_func.getText().trim();
		this.b2 = Double.valueOf(warm_func.getChildText("b")).doubleValue();
		this.c2 = Double.valueOf(warm_func.getChildText("c")).doubleValue();
		this.warm_years = Integer.valueOf(warm_func.getChildText("years")).intValue();
		
		this.sampling_frequency = Integer.valueOf(root.getChildText("sampling_frequency"));
		
		this.startingPoint = Integer.valueOf(root.getChildText("startingPoint"));
	}
	public String getFunc1Str() {
		return func1Str;
	}
	public String getFunc2Str() {
		return func2Str;
	}
	public int getStartingPoint() {
		return startingPoint;
	}
	public int getCold_years() {
		return cold_years;
	}
	public int getWarm_years() {
		return warm_years;
	}
	
}
