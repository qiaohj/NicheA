/******************************************************************************
 * Huijie Qiao
 *
 * Project:  Niche Analyst
 * Purpose:  
 * Created date: May 2, 2012 4:16:16 PM
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


package org.ku.nicheanalyst.ui.display.component.j3d;

import java.awt.BorderLayout;
import java.awt.GraphicsConfiguration;

import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.TransformGroup;
import javax.swing.JFrame;
import javax.vecmath.Point3d;

import com.sun.j3d.utils.behaviors.mouse.MouseRotate;
import com.sun.j3d.utils.geometry.ColorCube;
import com.sun.j3d.utils.universe.SimpleUniverse;

 

public class BasicScene extends JFrame

{
  public static void main(String args[]){
    new BasicScene();
  }

 

  public BasicScene(){
getContentPane().setLayout(new BorderLayout());

GraphicsConfiguration config=SimpleUniverse.getPreferredConfiguration();

 

//创建一个 Canvas3D 对象并将其加到frame中去

Canvas3D canvas=new Canvas3D(config);

getContentPane().add(canvas,BorderLayout.CENTER);

 

//创建一个SimpleUniverse对象,用来管理”view” 分支

SimpleUniverse u = new SimpleUniverse(canvas);

u.getViewingPlatform().setNominalViewingTransform();

 

//将 “content” 分支加入到SimpleUniverse中去

BranchGroup scene=createContentBranch();

u.addBranchGraph(scene);

 

setSize(256,256);

setVisible(true);

  }

 

public BranchGroup createContentBranch()

{

BranchGroup root=new BranchGroup();

 

//创建一个tansformGroup

TransformGroup tansformGroup = new TransformGroup();

tansformGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

root.addChild(tansformGroup);

 

//创建鼠标旋转行为

MouseRotate rotate=new MouseRotate();

 

rotate.setTransformGroup(tansformGroup);

rotate.setSchedulingBounds(new BoundingSphere(new Point3d(),1000));

tansformGroup.addChild(rotate);

 

//设置立方体颜色

ColorCube colorCube=new ColorCube(0.3);

tansformGroup.addChild(colorCube);

 

root.compile();

return root;

  }

}

 