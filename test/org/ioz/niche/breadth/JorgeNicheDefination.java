package org.ioz.niche.breadth;

import java.util.ArrayList;

import org.ku.nicheanalyst.common.CommonFun;


public class JorgeNicheDefination {
	private int id;
	private int temp_min;
	private int temp_max;
	private int prcp_min;
	private int prcp_max;
	private boolean isGood;
	private ArrayList<double[]> lls;
	public JorgeNicheDefination(String str, ArrayList<String> initialManualSpeciesSeeds){
		isGood = true;
		String[] item = str.split(",");
		if (item.length<5){
			isGood = false;
		}
		for (int i=0;i<5;i++){
			if (!CommonFun.isInteger(item[i])){
				isGood = false;
			}
		}
		lls = new ArrayList<double[]>();
		if (isGood){
			id = Integer.valueOf(item[0]);
			temp_min = Integer.valueOf(item[1]) * 10;
			temp_max = Integer.valueOf(item[2]) * 10;
			prcp_min = Integer.valueOf(item[3]);
			prcp_max = Integer.valueOf(item[4]);
			
			for (String llstr : initialManualSpeciesSeeds){
				String[] llstrs = llstr.split(",");
				if (llstrs.length<3){
					continue;
				}else{
					if ((CommonFun.isInteger(llstrs[0]))&&(CommonFun.isDouble(llstrs[1]))&&(CommonFun.isDouble(llstrs[2]))){
						if (Integer.valueOf(llstrs[0]).intValue()==id){
							lls.add(new double[]{Double.valueOf(llstrs[1]), Double.valueOf(llstrs[2])});
						}
					}
				}
			}
		}
		isGood = (lls.size()!=0);
	}
	public int getId() {
		return id;
	}
	public int getTemp_min() {
		return temp_min;
	}
	public int getTemp_max() {
		return temp_max;
	}
	public int getPrcp_min() {
		return prcp_min;
	}
	public int getPrcp_max() {
		return prcp_max;
	}
	public boolean isGood() {
		return isGood;
	}
	public ArrayList<double[]> getLls() {
		return lls;
	}
	
}
