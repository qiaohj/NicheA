/*    */ package quickhull3d;
/*    */ 
/*    */ import java.util.ArrayList;
/*    */ import quickhull3d.Point3d;
/*    */ 
/*    */ public class Point3dArrayList
/*    */ {
/* 15 */   private ArrayList<Point3d> points = new ArrayList();
/*    */ 
/*    */   public void add(Point3d point)
/*    */   {
/* 21 */     this.points.add(point);
/*    */   }
/*    */ 
/*    */   public Point3d[] toArray() {
/* 25 */     Point3d[] RegArray = new Point3d[this.points.size()];
/* 26 */     return (Point3d[])this.points.toArray(RegArray);
/*    */   }
/*    */ 
/*    */   public Point3d get(int index) {
/* 30 */     return (Point3d)this.points.get(index);
/*    */   }
/*    */ 
/*    */   public int size() {
/* 34 */     return this.points.size();
/*    */   }
/*    */ }

/* Location:           /Users/huijieqiao/Downloads/3D_Convex_Hull.jar
 * Qualified Name:     util.Point3dArrayList
 * JD-Core Version:    0.6.2
 */