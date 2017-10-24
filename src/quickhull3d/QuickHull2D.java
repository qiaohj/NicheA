package quickhull3d;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class QuickHull2D {
	public quickhull3d.Point3d[] execute(quickhull3d.Point3d[] v_points)
    {
		ArrayList<Point3d> points = new ArrayList<Point3d>();
		for (int i=0;i<v_points.length;i++){
			points.add(v_points[i]);
		}
            ArrayList<Point3d> xSorted = (ArrayList<Point3d>) points.clone();
            Collections.sort(xSorted, new XCompare());
           
            int n = xSorted.size();
           
            Point3d[] lUpper = new Point3d[n];
           
            lUpper[0] = xSorted.get(0);
            lUpper[1] = xSorted.get(1);
           
            int lUpperSize = 2;
           
            for (int i = 2; i < n; i++)
            {
                    lUpper[lUpperSize] = xSorted.get(i);
                    lUpperSize++;
                   
                    while (lUpperSize > 2 && !rightTurn(lUpper[lUpperSize - 3], lUpper[lUpperSize - 2], lUpper[lUpperSize - 1]))
                    {
                            // Remove the middle point of the three last
                            lUpper[lUpperSize - 2] = lUpper[lUpperSize - 1];
                            lUpperSize--;
                    }
            }
           
            Point3d[] lLower = new Point3d[n];
           
            lLower[0] = xSorted.get(n - 1);
            lLower[1] = xSorted.get(n - 2);
           
            int lLowerSize = 2;
           
            for (int i = n - 3; i >= 0; i--)
            {
                    lLower[lLowerSize] = xSorted.get(i);
                    lLowerSize++;
                   
                    while (lLowerSize > 2 && !rightTurn(lLower[lLowerSize - 3], lLower[lLowerSize - 2], lLower[lLowerSize - 1]))
                    {
                            // Remove the middle point of the three last
                            lLower[lLowerSize - 2] = lLower[lLowerSize - 1];
                            lLowerSize--;
                    }
            }
           
            ArrayList<Point3d> result = new ArrayList<Point3d>();
           
            for (int i = 0; i < lUpperSize; i++)
            {
                    result.add(lUpper[i]);
            }
           
            for (int i = 1; i < lLowerSize - 1; i++)
            {
                    result.add(lLower[i]);
            }
            quickhull3d.Point3d[] results = new quickhull3d.Point3d[result.size()];
            for (int i=0;i<result.size();i++){
            	results[i] = result.get(i);
            }
            return results;
    }
   
    private boolean rightTurn(Point3d a, Point3d b, Point3d c)
    {
            return (b.x - a.x)*(c.y - a.y) - (b.y - a.y)*(c.x - a.x) > 0;
    }

    private class XCompare implements Comparator<Point3d>
    {
            @Override
            public int compare(Point3d o1, Point3d o2)
            {
                    return (new Double(o1.x)).compareTo(new Double(o2.x));
            }
    }

}
