/******************************************************************************
 * Huijie Qiao
 *
 * Project:  NicheA
 * Purpose:  
 * Created date: Jan 18, 2013 4:16:15 PM
 * Author:   Huijie Qiao
 *
 ******************************************************************************
 * Copyright (c) 2013, Huijie Qiao
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


package org.ku.nicheanalyst.ui.display.worker;

import weka.filters.unsupervised.attribute.PrincipalComponents;

/**
 * @author Huijie Qiao
 *
 */
public class MyPrincipalComponents extends PrincipalComponents {
	public double[][] getCorrelation(){
		return this.m_Correlation;
	}
	public double[] getEigenvalues(){
		return this.m_Eigenvalues;
	}
	public double[] getSortedEigenvalues(){
		double[] orderedValues = new double[m_Eigenvalues.length];
		for (int j=m_SortedEigens.length-1;j>=0;j--){
			orderedValues[m_SortedEigens.length - j - 1] = m_Eigenvalues[j];
		}
		return orderedValues;
	}
	public double[][] getEigenvectors(){
		return this.m_Eigenvectors;
	}
	public int[] getSortedEngen(){
		return this.m_SortedEigens;
	}
	public double[][] getSortedEigenvectors(){
		double [][] orderedVectors = 
	        new double [m_Eigenvectors.length][m_Eigenvectors.length];
	      
	      // try converting back to the original space
	      for (int i = m_Eigenvectors.length - 1; i >= 0; i--) {
	        for (int j = 0; j < m_Eigenvectors.length; j++) {
	          orderedVectors[j][m_Eigenvectors.length - i - 1] = 
	            m_Eigenvectors[j][m_SortedEigens[i]];
	        }
	      }
	      return orderedVectors;
	}
}
