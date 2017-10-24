/******************************************************************************
 * Huijie Qiao
 *
 * Project:  Niche Analyst
 * Purpose:  
 * Created date: Aug 21, 2012 1:24:52 PM
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


package org.ku.nicheanalyst.terminal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.TreeMap;

import org.ku.nicheanalyst.common.CommonFun;
import org.ku.nicheanalyst.common.Const;

/**
 * @author Huijie Qiao
 *
 */
public class ENMToolConvertion {
	
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		if (args==null){
			args = new String[]{};
		}
		if (args.length<=1){
			System.exit(0);
		}
		
		String file = args[0];
		ArrayList<String> strs = CommonFun.readFromFile(file);
		if (strs.size()<=1){
			System.exit(0);
		}
		String[] firstLine = strs.get(0).split(",");
		if (firstLine.length<=1){
			System.exit(0);
		}
		TreeMap<String, Record> values = new TreeMap<String, Record>();
		for (int i=1;i<firstLine.length;i++){
			for (int j=i;j<firstLine.length;j++){
				Record record = new Record(firstLine[i], firstLine[j], 0d);
				values.put(String.format("%s,%s", firstLine[i], firstLine[j]), record);
			}
		}
		for (int i=1;i<strs.size();i++){
			String[] v = strs.get(i).split(",");
			String name1 = v[0];
			for (int j=1;j<v.length;j++){
				if (CommonFun.isDouble(v[j])){
					String name2 =firstLine[j];
					Record record = values.get(String.format("%s,%s", name1, name2));
					if (record==null){
						record = values.get(String.format("%s,%s", name2, name1));
					}
					record.setValue(Double.valueOf(v[j]).doubleValue());
				}
				
			}
		}
		StringBuilder sb = new StringBuilder();
		sb.append("SPECIES1,SPECIES2,OVERLAP" + Const.LineBreak);
		for (String key : values.keySet()){
			sb.append(String.format("%s,%.9f", key, values.get(key).getValue()) + Const.LineBreak);
		}
		CommonFun.writeFile(sb.toString(), args[1]);
		System.out.println("Done");
	}

}

class Record{
	private String name1;
	private String name2;
	private double value;
	public Record(String name1, String name2, double value){
		this.name1 = name1;
		this.name2 = name2;
		this.value = value;
	}
	public String getName1() {
		return name1;
	}
	public void setName1(String name1) {
		this.name1 = name1;
	}
	public String getName2() {
		return name2;
	}
	public void setName2(String name2) {
		this.name2 = name2;
	}
	public double getValue() {
		return value;
	}
	public void setValue(double value) {
		this.value = value;
	}
	
}