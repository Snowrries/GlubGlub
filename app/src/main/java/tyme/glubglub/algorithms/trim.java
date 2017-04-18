package tyme.glubglub.algorithms;

import java.util.Set;
import java.util.HashSet;
import android.graphics.Point;

import static tyme.glubglub.algorithms.nearestNeighbor.distance;

public class trim{
	public static Set<Point> trimmer(Set<Point> pointsOfInterest, int threshold){
		Point[] p = pointsOfInterest.toArray(new Point[pointsOfInterest.size()]);
		Point newPoint = new Point(0,0);
		for(int i = 0; i < p.length;i++ ){
			if(pointsOfInterest.contains(p[i])){
				newPoint = new Point(p[i]);
				//newPoint.setLocation(p[i]);
				pointsOfInterest.remove(p[i]);
			}
			else{
				continue;
			}
			for(int j = i+1; j< p.length; j++ ){
				if(pointsOfInterest.contains(p[j])){		
					if(distance(newPoint, p[j]) <= threshold){
						int x = (int) (newPoint.x + p[j].x) / 2;
						int y = (int) (newPoint.y + p[j].y) / 2;
						newPoint.set(x,y);
						pointsOfInterest.remove(p[j]);
					}
				}
			}
			
			pointsOfInterest.add(newPoint);
		}
		//Copy constructor
		Set<Point> result = new HashSet<Point>(pointsOfInterest.size());
		result.addAll(pointsOfInterest);
		return result;
	}


}