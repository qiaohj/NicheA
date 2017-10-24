/******************************************************************************
 * Huijie Qiao
 *
 * Project:  Niche Analyst
 * Purpose:  
 * Created date: Apr 12, 2012 4:41:20 PM
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


package org.ku.nicheanalyst.common;

/**
 * @author Huijie Qiao
 *
 */
import java.awt.Image;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import javax.media.jai.NullOpImage;
import javax.media.jai.OpImage;
import javax.media.jai.PlanarImage;

import com.sun.media.jai.codec.ByteArraySeekableStream;
import com.sun.media.jai.codec.FileSeekableStream;
import com.sun.media.jai.codec.ImageCodec;
import com.sun.media.jai.codec.ImageDecoder;
import com.sun.media.jai.codec.SeekableStream;
import com.sun.media.jai.codec.TIFFDecodeParam;

public class ImageViewer  {

  static Image load(byte[] data) throws Exception{
    Image image = null;
    SeekableStream stream = new ByteArraySeekableStream(data);
    String[] names = ImageCodec.getDecoderNames(stream);
    ImageDecoder dec = 
      ImageCodec.createImageDecoder(names[0], stream, null);
    RenderedImage im = dec.decodeAsRenderedImage();
    image = PlanarImage.wrapRenderedImage(im).getAsBufferedImage();
    return image;
  }
  public static Image load(String filename, int scale) throws Exception{
	  
	  File file = new File(filename);
      SeekableStream s = new FileSeekableStream(file);

      TIFFDecodeParam param = null;

      ImageDecoder dec = ImageCodec.createImageDecoder("tiff", s, param);
      int imageToLoad = 0;
      RenderedImage op =
          new NullOpImage(dec.decodeAsRenderedImage(imageToLoad),
                          null,
                          OpImage.OP_IO_BOUND,
                          null);
      Image image = PlanarImage.wrapRenderedImage(op).getAsBufferedImage();
	
	if (scale>0){
		Image imageScaled = 
		      image.getScaledInstance(scale, -1,  Image.SCALE_SMOOTH);
		return imageScaled;
	}else{
		return image;
	}
  }
  public static void main(String[] args) throws Exception{
    
  }
}