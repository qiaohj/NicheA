/******************************************************************************
 * Huijie Qiao
 *
 * Project:  NicheA
 * Purpose:  
 * Created date: Jan 31, 2013 11:18:14 AM
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


package org.ku.multivar;

import org.apache.commons.math3.distribution.MultivariateNormalDistribution;
import org.apache.commons.math3.linear.BlockRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.stat.correlation.Covariance;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.junit.Test;
import org.ku.nicheanalyst.common.CommonFun;

/**
 * @author Huijie Qiao
 *
 */
public class MultiVar {
	@Test
	public void CovarianceTest(){
		RealMatrix matrix = new BlockRealMatrix(3, 2);
		for (int row=0;row<matrix.getRowDimension();row++){
			for (int col=0;col<matrix.getColumnDimension();col++){
				matrix.setEntry(row, col, col * matrix.getRowDimension() + row);
			}
		}
		Mean mean = new Mean();
		System.out.println(mean.evaluate(new double[]{1, 2}));
		System.out.println(CommonFun.Realmatrix2String(matrix));
		Covariance cov = new Covariance(matrix);
		System.out.println(CommonFun.Realmatrix2String(cov.getCovarianceMatrix()));
		MultivariateNormalDistribution distribution = new MultivariateNormalDistribution(new double[]{1, 2}, cov.getCovarianceMatrix().getData());
		for (int i=0;i<10;i++){
			System.out.println(CommonFun.printArray(distribution.sample()));
		}
	}
}
