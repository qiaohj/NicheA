package org.ku.nicheanalyst.maps.objects;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class PNGObject {
	public PNGObject(String filename, int[][] plist){
		RenderedImage rendImage = CreateImage(plist); 
		// Write generated image to a file 
		try { 
			// Save as PNG 
			File file = new File(filename); 
			ImageIO.write(rendImage, "png", file); 
		} catch (IOException e) { 
				
		}
	}
	private RenderedImage CreateImage(int[][] plist) { 
		int width = plist.length; 
		int height = plist[0].length; 
		// Create a buffered image in which to draw 
		BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB); 
		// Create a graphics contents on the buffered image 
		Graphics2D g2d = bufferedImage.createGraphics();
		g2d.setColor(Color.black);
		g2d.fillRect(0, 0, width, height);
		for (int x=0;x<width;x++){
			for (int y=0;y<height;y++){
				if (plist[x][y]!=-1){
					try{
						Color color = null;
						if (plist[x][y]>0){
							
							color = new Color(255, 255-plist[x][y], 255-plist[x][y]);
						}else{
							color = new Color(255, 255, 255);
						}
						g2d.setColor(color);
						g2d.drawRect(x, y, 1, 1);
					}catch(Exception e){
//						System.out.println(plist[x][y]);
					}
				}
			}
		}
		  
		
		//g2d.fillOval(0, 0, width, height);  
		// Graphics context no longer needed so dispose it 
		g2d.dispose(); 
		return bufferedImage;  
	}
}
