package tyme.glubglub.algorithms;

import android.graphics.Point;
import java.util.Set;
public class nearestNeighbor{
	
	public static Point nearest(Point currentPos, Set<Point> pointsOfInterest){
		double min_dis = Double.MAX_VALUE;
		Point closest_point = null;
		for(Point p : pointsOfInterest){
			if(min_dis > distance(currentPos, p)){
				closest_point = p;
				min_dis = distance(currentPos, p);
			}
		}
		return closest_point;
	}
	
	public static double distance(Point current, Point next){
		return Math.sqrt(Math.pow((current.x - next.x),2) + Math.pow((current.y - next.y),2));
	}
}