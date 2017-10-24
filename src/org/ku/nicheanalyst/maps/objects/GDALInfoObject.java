/******************************************************************************
 * Huijie Qiao
 *
 * Project:  NicheA
 * Purpose:  
 * Created date: Nov 26, 2012 3:56:50 PM
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


package org.ku.nicheanalyst.maps.objects;

import java.util.Enumeration;
import java.util.Vector;

import org.gdal.gdal.Band;
import org.gdal.gdal.ColorTable;
import org.gdal.gdal.Dataset;
import org.gdal.gdal.Driver;
import org.gdal.gdal.GCP;
import org.gdal.gdal.RasterAttributeTable;
import org.gdal.gdal.TermProgressCallback;
import org.gdal.gdal.gdal;
import org.gdal.gdalconst.gdalconstConstants;
import org.gdal.osr.CoordinateTransformation;
import org.gdal.osr.SpatialReference;
import org.ku.nicheanalyst.common.Const;

/**
 * @author Huijie Qiao
 *
 */
public class GDALInfoObject {
	private StringBuilder info;
	public GDALInfoObject(String filename){
		info = new StringBuilder();
		Dataset hDataset;
		Band hBand;
		int i, iBand;
		double[] adfGeoTransform = new double[6];
		Driver hDriver;
		Vector papszMetadata;
		boolean bComputeMinMax = false, bSample = false;
		boolean bShowGCPs = true, bShowMetadata = true;
		boolean bStats = false, bApproxStats = true;
        boolean bShowColorTable = true, bComputeChecksum = false;
        boolean bReportHistograms = false;
        boolean bShowRAT = true;
		String pszFilename = filename;
	    Vector papszFileList;
	    Vector papszExtraMDDomains = new Vector();

		gdal.AllRegister();

                   
		/* -------------------------------------------------------------------- */
		/*      Parse arguments.                                                */
		/* -------------------------------------------------------------------- */
		

		/* -------------------------------------------------------------------- */
		/*      Open dataset.                                                   */
		/* -------------------------------------------------------------------- */
		hDataset = gdal.Open(pszFilename, gdalconstConstants.GA_ReadOnly);

		if (hDataset == null) {
			info.append("GDALOpen failed - " + gdal.GetLastErrorNo() + Const.LineBreak);
			info.append(gdal.GetLastErrorMsg() + Const.LineBreak);
		}

		/* -------------------------------------------------------------------- */
		/*      Report general info.                                            */
		/* -------------------------------------------------------------------- */
		hDriver = hDataset.GetDriver();
		info.append("Driver: " + hDriver.getShortName() + "/"
				+ hDriver.getLongName() + Const.LineBreak);

                    papszFileList = hDataset.GetFileList( );
                    if( papszFileList.size() == 0 )
                    {
                    	info.append( "Files: none associated" + Const.LineBreak);
                    }
                    else
                    {
                        Enumeration e = papszFileList.elements();
                        info.append( "Files: " + (String)e.nextElement() + Const.LineBreak);
                        while(e.hasMoreElements())
                        	info.append( "       " +  (String)e.nextElement() + Const.LineBreak);
                    }

            info.append("Size is " + hDataset.getRasterXSize() + ", "
				+ hDataset.getRasterYSize() + Const.LineBreak);

		/* -------------------------------------------------------------------- */
		/*      Report projection.                                              */
		/* -------------------------------------------------------------------- */
		if (hDataset.GetProjectionRef() != null) {
			SpatialReference hSRS;
			String pszProjection;

			pszProjection = hDataset.GetProjectionRef();

			hSRS = new SpatialReference(pszProjection);
			if (hSRS != null && pszProjection.length() != 0) {
				String[] pszPrettyWkt = new String[1];

				hSRS.ExportToPrettyWkt(pszPrettyWkt, 0);
				info.append("Coordinate System is:" + Const.LineBreak);
				info.append(pszPrettyWkt[0] + Const.LineBreak);
				//gdal.CPLFree( pszPrettyWkt );
			} else
				info.append("Coordinate System is `"
						+ hDataset.GetProjectionRef() + "'" + Const.LineBreak);

			hSRS.delete();
		}

		/* -------------------------------------------------------------------- */
		/*      Report Geotransform.                                            */
		/* -------------------------------------------------------------------- */
		hDataset.GetGeoTransform(adfGeoTransform);
		{
			if (adfGeoTransform[2] == 0.0 && adfGeoTransform[4] == 0.0) {
				info.append("Origin = (" + adfGeoTransform[0] + ","
						+ adfGeoTransform[3] + ")" + Const.LineBreak);

				info.append("Pixel Size = (" + adfGeoTransform[1]
						+ "," + adfGeoTransform[5] + ")" + Const.LineBreak);
			} else {
				info.append("GeoTransform =" + Const.LineBreak);
                                    info.append("  " + adfGeoTransform[0] + ", "
                                                    + adfGeoTransform[1] + ", " + adfGeoTransform[2] + Const.LineBreak);
                                    info.append("  " + adfGeoTransform[3] + ", "
                                                    + adfGeoTransform[4] + ", " + adfGeoTransform[5] + Const.LineBreak);
                            }
		}

		/* -------------------------------------------------------------------- */
		/*      Report GCPs.                                                    */
		/* -------------------------------------------------------------------- */
		if (bShowGCPs && hDataset.GetGCPCount() > 0) {
			info.append("GCP Projection = "
					+ hDataset.GetGCPProjection() + Const.LineBreak);

			int count = 0;
			Vector GCPs = new Vector();
			hDataset.GetGCPs(GCPs);

			Enumeration e = GCPs.elements();
			while (e.hasMoreElements()) {
				GCP gcp = (GCP) e.nextElement();
				info.append("GCP[" + (count++) + "]: Id="
						+ gcp.getId() + ", Info=" + gcp.getInfo() + Const.LineBreak);
				info.append("    (" + gcp.getGCPPixel() + ","
						+ gcp.getGCPLine() + ") (" + gcp.getGCPX() + ","
						+ gcp.getGCPY() + "," + gcp.getGCPZ() + ")" + Const.LineBreak);
			}

		}

		/* -------------------------------------------------------------------- */
		/*      Report metadata.                                                */
		/* -------------------------------------------------------------------- */
		papszMetadata = hDataset.GetMetadata_List("");
		if (bShowMetadata && papszMetadata.size() > 0) {
			Enumeration keys = papszMetadata.elements();
			info.append("Metadata:" + Const.LineBreak);
			while (keys.hasMoreElements()) {
				info.append("  " + (String) keys.nextElement() + Const.LineBreak);
			}
		}
                    
                    Enumeration eExtraMDDDomains = papszExtraMDDomains.elements();
                    while(eExtraMDDDomains.hasMoreElements())
                    {
                        String pszDomain = (String)eExtraMDDDomains.nextElement();
                        papszMetadata = hDataset.GetMetadata_List(pszDomain);
                        if( bShowMetadata && papszMetadata.size() > 0 )
                        {
                            Enumeration keys = papszMetadata.elements();
                            info.append("Metadata (" + pszDomain + "):" + Const.LineBreak);
                            while (keys.hasMoreElements()) {
				info.append("  " + (String) keys.nextElement() + Const.LineBreak);
			}
                        }
                    }
                    /* -------------------------------------------------------------------- */
                    /*      Report "IMAGE_STRUCTURE" metadata.                              */
                    /* -------------------------------------------------------------------- */
                    papszMetadata = hDataset.GetMetadata_List("IMAGE_STRUCTURE" );
                    if( bShowMetadata && papszMetadata.size() > 0) {
			Enumeration keys = papszMetadata.elements();
			info.append("Image Structure Metadata:" + Const.LineBreak);
			while (keys.hasMoreElements()) {
				info.append("  " + (String) keys.nextElement() + Const.LineBreak);
			}
		}
		/* -------------------------------------------------------------------- */
		/*      Report subdatasets.                                             */
		/* -------------------------------------------------------------------- */
		papszMetadata = hDataset.GetMetadata_List("SUBDATASETS");
		if (papszMetadata.size() > 0) {
			info.append("Subdatasets:" + Const.LineBreak);
			Enumeration keys = papszMetadata.elements();
			while (keys.hasMoreElements()) {
				info.append("  " + (String) keys.nextElement() + Const.LineBreak);
			}
		}
                
                /* -------------------------------------------------------------------- */
                /*      Report geolocation.                                             */
                /* -------------------------------------------------------------------- */
                    papszMetadata = hDataset.GetMetadata_List("GEOLOCATION" );
                    if (papszMetadata.size() > 0) {
                        info.append( "Geolocation:"  + Const.LineBreak);
                        Enumeration keys = papszMetadata.elements();
                        while (keys.hasMoreElements()) {
                                info.append("  " + (String) keys.nextElement() + Const.LineBreak);
                        }
                    }
                
                /* -------------------------------------------------------------------- */
                /*      Report RPCs                                                     */
                /* -------------------------------------------------------------------- */
                    papszMetadata = hDataset.GetMetadata_List("RPC" );
                    if (papszMetadata.size() > 0) {
                        info.append( "RPC Metadata:"  + Const.LineBreak);
                        Enumeration keys = papszMetadata.elements();
                        while (keys.hasMoreElements()) {
                                info.append("  " + (String) keys.nextElement() + Const.LineBreak);
                        }
                    }

		/* -------------------------------------------------------------------- */
		/*      Report corners.                                                 */
		/* -------------------------------------------------------------------- */
		info.append("Corner Coordinates:" + Const.LineBreak);
		GDALInfoReportCorner(hDataset, "Upper Left ", 0.0, 0.0);
		GDALInfoReportCorner(hDataset, "Lower Left ", 0.0, hDataset
				.getRasterYSize());
		GDALInfoReportCorner(hDataset, "Upper Right", hDataset
				.getRasterXSize(), 0.0);
		GDALInfoReportCorner(hDataset, "Lower Right", hDataset
				.getRasterXSize(), hDataset.getRasterYSize());
		GDALInfoReportCorner(hDataset, "Center     ",
				hDataset.getRasterXSize() / 2.0,
				hDataset.getRasterYSize() / 2.0);

		/* ==================================================================== */
		/*      Loop over bands.                                                */
		/* ==================================================================== */
		for (iBand = 0; iBand < hDataset.getRasterCount(); iBand++) {
			Double[] pass1 = new Double[1], pass2 = new Double[1];
			double[] adfCMinMax = new double[2];
			ColorTable hTable;

			hBand = hDataset.GetRasterBand(iBand + 1);

			/*if( bSample )
			 {
			 float[] afSample = new float[10000];
			 int   nCount;

			 nCount = hBand.GetRandomRasterSample( 10000, afSample );
			 info.append( "Got " + nCount + " samples." );
			 }*/

                            int[] blockXSize = new int[1];
                            int[] blockYSize = new int[1];
                            hBand.GetBlockSize(blockXSize, blockYSize);
			info.append("Band "
					+ (iBand+1)
                                            + " Block="
                                            + blockXSize[0] + "x" + blockYSize[0]
					+ " Type="
					+ gdal.GetDataTypeName(hBand.getDataType())
					+ ", ColorInterp="
					+ gdal.GetColorInterpretationName(hBand
							.GetRasterColorInterpretation()) + Const.LineBreak);

			String hBandDesc = hBand.GetDescription();
			if (hBandDesc != null && hBandDesc.length() > 0)
				info.append("  Description = " + hBandDesc + Const.LineBreak);

			hBand.GetMinimum(pass1);
			hBand.GetMaximum(pass2);
			if(pass1[0] != null || pass2[0] != null || bComputeMinMax) {
                                info.append( "  " );
                                if( pass1[0] != null )
                                    info.append( "Min=" + pass1[0] + " ");
                                if( pass2[0] != null )
                                    info.append( "Max=" + pass2[0] + " ");
                            
                                if( bComputeMinMax )
                                {
                                    hBand.ComputeRasterMinMax(adfCMinMax, 0);
                                    info.append( "  Computed Min/Max=" + adfCMinMax[0]
						+ "," + adfCMinMax[1]);
                                }
                    
                                info.append( "\n" );
			}

                            double dfMin[] = new double[1];
                            double dfMax[] = new double[1];
                            double dfMean[] = new double[1];
                            double dfStdDev[] = new double[1];
			if( hBand.GetStatistics( bApproxStats, bStats,
                                                     dfMin, dfMax, dfMean, dfStdDev ) == gdalconstConstants.CE_None )
			{
			    info.append( "  Minimum=" + dfMin[0] + ", Maximum=" + dfMax[0] +
                                                    ", Mean=" + dfMean[0] + ", StdDev=" + dfStdDev[0]  + Const.LineBreak);
			}

                            if( bReportHistograms )
                            {
                                int[][] panHistogram = new int[1][];
                                int eErr = hBand.GetDefaultHistogram(dfMin, dfMax, panHistogram, true, new TermProgressCallback());
                                if( eErr == gdalconstConstants.CE_None )
                                {
                                    int iBucket;
                                    int nBucketCount = panHistogram[0].length;
                                    info.append( "  " + nBucketCount + " buckets from " +
                                                       dfMin[0] + " to " + dfMax[0] + ":\n  " );
                                    for( iBucket = 0; iBucket < nBucketCount; iBucket++ )
                                        info.append( panHistogram[0][iBucket] + " ");
                                    info.append( "\n" );
                                }
                            }

                            if ( bComputeChecksum)
                            {
                                info.append( "  Checksum=" + hBand.Checksum() + Const.LineBreak);
                            }

			hBand.GetNoDataValue(pass1);
			if(pass1[0] != null)
			{
				info.append("  NoData Value=" + pass1[0] + Const.LineBreak);
			}

			if (hBand.GetOverviewCount() > 0) {
				int iOverview;

				info.append("  Overviews: ");
				for (iOverview = 0; iOverview < hBand.GetOverviewCount(); iOverview++) {
					Band hOverview;

					if (iOverview != 0)
						info.append(", ");

					hOverview = hBand.GetOverview(iOverview);
					info.append(hOverview.getXSize() + "x"
							+ hOverview.getYSize());
				}
				info.append("\n");

                                    if ( bComputeChecksum)
                                    {
                                        info.append( "  Overviews checksum: " );
                                        for( iOverview = 0; 
                                            iOverview < hBand.GetOverviewCount();
                                            iOverview++ )
                                        {
                                            Band	hOverview;
                        
                                            if( iOverview != 0 )
                                                info.append( ", " );
                        
                                            hOverview = hBand.GetOverview(iOverview);
                                            info.append( hOverview.Checksum());
                                        }
                                        info.append( "\n" );
                                    }
			}

			if( hBand.HasArbitraryOverviews() )
			{
			    info.append( "  Overviews: arbitrary"  + Const.LineBreak);
			}


                            int nMaskFlags = hBand.GetMaskFlags(  );
                            if( (nMaskFlags & (gdalconstConstants.GMF_NODATA|gdalconstConstants.GMF_ALL_VALID)) == 0 )
                            {
                                Band hMaskBand = hBand.GetMaskBand() ;
                    
                                info.append( "  Mask Flags: " );
                                if( (nMaskFlags & gdalconstConstants.GMF_PER_DATASET) != 0 )
                                    info.append( "PER_DATASET " );
                                if( (nMaskFlags & gdalconstConstants.GMF_ALPHA) != 0 )
                                    info.append( "ALPHA " );
                                if( (nMaskFlags & gdalconstConstants.GMF_NODATA) != 0 )
                                    info.append( "NODATA " );
                                if( (nMaskFlags & gdalconstConstants.GMF_ALL_VALID) != 0 )
                                    info.append( "ALL_VALID " );
                                info.append( "\n" );
                    
                                if( hMaskBand != null &&
                                    hMaskBand.GetOverviewCount() > 0 )
                                {
                                    int		iOverview;
                    
                                    info.append( "  Overviews of mask band: " );
                                    for( iOverview = 0; 
                                        iOverview < hMaskBand.GetOverviewCount();
                                        iOverview++ )
                                    {
                                        Band	hOverview;
                    
                                        if( iOverview != 0 )
                                            info.append( ", " );
                    
                                        hOverview = hMaskBand.GetOverview( iOverview );
                                        info.append( 
                                                hOverview.getXSize() + "x" +
                                                hOverview.getYSize() );
                                    }
                                    info.append( "\n" );
                                }
                            }
                            
			if( hBand.GetUnitType() != null && hBand.GetUnitType().length() > 0)
			{
			     info.append( "  Unit Type: " + hBand.GetUnitType()  + Const.LineBreak);
			}

                            Vector papszCategories = hBand.GetRasterCategoryNames();
                            if (papszCategories.size() > 0)
                            {
                                info.append( "  Categories:"  + Const.LineBreak);
                                Enumeration eCategories = papszCategories.elements();
                                i = 0;
			    while (eCategories.hasMoreElements()) {
                                        info.append("    " + i + ": " + (String) eCategories.nextElement() + Const.LineBreak);
                                        i ++;
                                }
                            }

			hBand.GetOffset(pass1);
			if(pass1[0] != null && pass1[0].doubleValue() != 0) {
				info.append("  Offset: " + pass1[0]);
			}
			hBand.GetScale(pass1);
			if(pass1[0] != null && pass1[0].doubleValue() != 1) {
				info.append(",   Scale:" + pass1[0] + Const.LineBreak);
			}

			papszMetadata = hBand.GetMetadata_List("");
			 if( bShowMetadata && papszMetadata.size() > 0 ) {
					Enumeration keys = papszMetadata.elements();
					info.append("  Metadata:" + Const.LineBreak);
					while (keys.hasMoreElements()) {
						info.append("    " + (String) keys.nextElement() + Const.LineBreak);
					}
			 }
			if (hBand.GetRasterColorInterpretation() == gdalconstConstants.GCI_PaletteIndex
					&& (hTable = hBand.GetRasterColorTable()) != null) {
				int count;

				info.append("  Color Table ("
						+ gdal.GetPaletteInterpretationName(hTable
								.GetPaletteInterpretation()) + " with "
						+ hTable.GetCount() + " entries)" + Const.LineBreak);

                                    if (bShowColorTable)
                                    {
                                        for (count = 0; count < hTable.GetCount(); count++) {
                                                info.append(" " + count + ": "
                                                                + hTable.GetColorEntry(count) + Const.LineBreak);
                                        }
                                    }
			}

                            RasterAttributeTable rat = hBand.GetDefaultRAT();
                            if( bShowRAT && rat != null )
                            {
                                info.append("<GDALRasterAttributeTable ");
                                double[] pdfRow0Min = new double[1];
                                double[] pdfBinSize = new double[1];
                                if (rat.GetLinearBinning(pdfRow0Min, pdfBinSize))
                                {
                                    info.append("Row0Min=\"" + pdfRow0Min[0] + "\" BinSize=\"" + pdfBinSize[0] + "\">");
                                }
                                info.append("\n");
                                int colCount = rat.GetColumnCount();
                                for(int col=0;col<colCount;col++)
                                {
                                    info.append("  <FieldDefn index=\"" + col + "\">" + Const.LineBreak);
                                    info.append("    <Name>" + rat.GetNameOfCol(col) + "</Name>" + Const.LineBreak);
                                    info.append("    <Type>" + rat.GetTypeOfCol(col) + "</Type>" + Const.LineBreak);
                                    info.append("    <Usage>" + rat.GetUsageOfCol(col) + "</Usage>" + Const.LineBreak);
                                    info.append("  </FieldDefn>" + Const.LineBreak);
                                }
                                int rowCount = rat.GetRowCount();
                                for(int row=0;row<rowCount;row++)
                                {
                                    info.append("  <Row index=\"" + row + "\">" + Const.LineBreak);
                                    for(int col=0;col<colCount;col++)
                                    {
                                        info.append("    <F>" + rat.GetValueAsString(row, col)+ "</F>" + Const.LineBreak);
                                    }
                                    info.append("  </Row>" + Const.LineBreak);
                                }
                                info.append("</GDALRasterAttributeTable>" + Const.LineBreak);
                            }
		}

		hDataset.delete();
	}
	private boolean GDALInfoReportCorner(Dataset hDataset, String corner_name,
			double x, double y)

	{
		double dfGeoX, dfGeoY;
		String pszProjection;
		double[] adfGeoTransform = new double[6];
		CoordinateTransformation hTransform = null;

		info.append(corner_name + " ");

		/* -------------------------------------------------------------------- */
		/*      Transform the point into georeferenced coordinates.             */
		/* -------------------------------------------------------------------- */
		hDataset.GetGeoTransform(adfGeoTransform);
		{
			pszProjection = hDataset.GetProjectionRef();

			dfGeoX = adfGeoTransform[0] + adfGeoTransform[1] * x
					+ adfGeoTransform[2] * y;
			dfGeoY = adfGeoTransform[3] + adfGeoTransform[4] * x
					+ adfGeoTransform[5] * y;
		}

		if (adfGeoTransform[0] == 0 && adfGeoTransform[1] == 0
				&& adfGeoTransform[2] == 0 && adfGeoTransform[3] == 0
				&& adfGeoTransform[4] == 0 && adfGeoTransform[5] == 0) {
			info.append("(" + x + "," + y + ")" + Const.LineBreak);
			return false;
		}

		/* -------------------------------------------------------------------- */
		/*      Report the georeferenced coordinates.                           */
		/* -------------------------------------------------------------------- */
		info.append("(" + dfGeoX + "," + dfGeoY + ") ");

		/* -------------------------------------------------------------------- */
		/*      Setup transformation to lat/long.                               */
		/* -------------------------------------------------------------------- */
		if (pszProjection != null && pszProjection.length() > 0) {
			SpatialReference hProj, hLatLong = null;

			hProj = new SpatialReference(pszProjection);
			if (hProj != null)
				hLatLong = hProj.CloneGeogCS();

			if (hLatLong != null) {
				gdal.PushErrorHandler( "CPLQuietErrorHandler" );
				hTransform = new CoordinateTransformation(hProj, hLatLong);
				gdal.PopErrorHandler();
				hLatLong.delete();
				if (gdal.GetLastErrorMsg().indexOf("Unable to load PROJ.4 library") != -1)
					hTransform = null;
			}

			if (hProj != null)
				hProj.delete();
		}

		/* -------------------------------------------------------------------- */
		/*      Transform to latlong and report.                                */
		/* -------------------------------------------------------------------- */
		if (hTransform != null) {
			double[] transPoint = new double[3];
			hTransform.TransformPoint(transPoint, dfGeoX, dfGeoY, 0);
			info.append("(" + gdal.DecToDMS(transPoint[0], "Long", 2));
			System.out
					.print("," + gdal.DecToDMS(transPoint[1], "Lat", 2) + ")");
		}

		if (hTransform != null)
			hTransform.delete();

		info.append("" + Const.LineBreak);

		return true;
	}
	public StringBuilder getInfo() {
		return info;
	}
	public StringBuilder getHTMLInfo() {
		StringBuilder sb = new StringBuilder();
		sb.append("<div style=\"background-color:#EEEEEE; border-radius:10px; border: 1px black solid;\">");
		String[] lines = info.toString().split("\\n");
		for (String str : lines){
			sb.append("<br>" + str + "</br>");
		}
		sb.append("</div>");
		return sb;
	}
	
}
