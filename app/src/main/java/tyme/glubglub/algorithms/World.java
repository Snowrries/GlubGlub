package tyme.glubglub.algorithms;

import java.util.Set;
import java.util.HashSet;
import android.graphics.Point;

import tyme.glubglub.dataPoint;
import tyme.glubglub.userPoint;

public class World{
	int size_x;
	int size_y;
	Set<Point> pointsOfInterest; 
	double[][] dataMap;
	
	public World(int size_x, int size_y){
		
		this.dataMap = new double[size_x][size_y];
		this.pointsOfInterest = new HashSet<Point>();
		this.size_x = size_x;
		this.size_y = size_y;
	}
	
	//receive data
	public void update(int x, int y, double value){
		if((0 <= x && x < this.size_x) && (0 <= y) && (y < this.size_y)){
			dataMap[x][y] = value;
		}
		
	}
	
	public void update(dataPoint newData, double startLat, double startLong, double ratio, double scale){
		int x =(int) ((int) (newData.Lat - startLat)*scale/ratio);
		int y =(int) ((int) (newData.Lng - startLong)*scale/ratio);
		int value = (int) newData.Temp;
		if((0 <= x && x < this.size_x) && (0 <= y) && (y < this.size_y)){
			dataMap[x][y] = value;
		}
		
	}
	
	public void update(userPoint newData, double startLat, double startLong, double ratio, double scale){
		int x =(int) ((int) (newData.Lat - startLat)*scale/ratio);
		int y =(int) ((int) (newData.Lng - startLong)*scale/ratio);
		
		Point p = new Point(x,y);
		if((0 <= x && x < this.size_x) && (0 <= y) && (y < this.size_y)){
			pointsOfInterest.add(p); // prevents duplicates, auto put.
		}
	}
	
	
	
	
}