package org.ku.nicheanalyst.dataset;

import org.ku.nicheanalyst.common.CommonFun;
import org.ku.nicheanalyst.common.Const;

import Jama.Matrix;

public class EllipsoidParameters {
	private Matrix eigenMatrix;
	private Matrix eigenValue;
	private String errorMsg;
	public EllipsoidParameters(Matrix eigenMatrix, Matrix eigenValue){
		this.eigenMatrix = eigenMatrix;
		this.eigenValue = eigenValue;
	}
	public EllipsoidParameters(String text) {
		try{
			String[] str = text.split(Const.LineBreak);
			StringBuilder sb_A = new StringBuilder();
			StringBuilder sb_C = new StringBuilder();
			boolean isA = true;
			for (String stritem : str){
				if (stritem.equalsIgnoreCase("A:")){
					isA = true;
					continue;
				}
				if (stritem.equalsIgnoreCase("C:")){
					isA = false;
					continue;
				}
				if (isA){
					sb_A.append(String.format("%s%n",	stritem));
				}else{
					sb_C.append(String.format("%s%n",	stritem));
				}
			}
			
			
			this.eigenMatrix = CommonFun.loadMatrixFromString(sb_A.toString());
			
			
			this.eigenValue = CommonFun.loadMatrixFromString(sb_C.toString());
			this.errorMsg = null;
		}catch(Exception e){
			errorMsg = "";
		}
	}
	public Matrix getEigenMatrix() {
		return eigenMatrix;
	}
	public Matrix getEigenValue() {
		return eigenValue;
	}
	public String getErrorMsg(){
		return this.errorMsg;
	}
	
}
