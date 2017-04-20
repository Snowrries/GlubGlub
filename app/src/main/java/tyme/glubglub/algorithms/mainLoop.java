package tyme.glubglub.algorithms;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import android.content.Context;
import android.graphics.Point;

import com.google.android.gms.maps.model.LatLng;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Set;

import tyme.glubglub.dataPoint;
import tyme.glubglub.userPoint;


public class mainLoop{
	AUV blueRov;
	World worldOrder;
	double[][] simWorld;
	List<Point> wayPoints;
	wayPointGen wpGenerator;
	
	int size_x;
	int size_y;
	double startLat;
	double startLong;
    double ratio;
	double scale;
    float tempThreshold = 0;
	boolean sparseTraverse = true;
	boolean finishedFlag = false;
	boolean poiFound = false;
	
	private Point nextPoint(List<Point> wPoints){
		int x = blueRov.getX();
		int y = blueRov.getY();
		Point p = wPoints.get(0);
		int diffx = (int) (p.x - x);
		int diffy = (int) (p.y - y);
		System.out.println(diffx + "," + diffy);
		if(diffx == 0 && diffy == 0){
			wPoints.remove(0);
			if(!wPoints.isEmpty()){
				p = wPoints.get(0);
				diffx = (int) (p.x - x);
				diffy = (int) (p.y - y);
			}
			else{
				System.out.println("Empty");
			}
		}
		
		if(Math.abs(diffx) < Math.abs(diffy) && (diffy != 0)){
	
			if(diffy < 0){
				y--;
			}
			else{
				y++;
			}
			blueRov.setY(y);
		
		}
		else if(diffx != 0){
			if(diffx < 0){
				x--;
			}
			else{
				x++;
			}
			blueRov.setX(x);
		}
		
		Point newPoint = new Point(x,y);
		
		return newPoint;
	}
	
	//pathname - file of our simulated world
	//startLatitude - starting latitude position
	//startLongitude - starting longitude position
	//ratio - cell size
	//size_x - Boundary of rectangle
	//size_y - Boundary of rectangle
	//step_size - same as matlab, the short step of the sparseTraverse
	///threshold - the threshold of our POIs.  Temperature Ranges any number.
	public mainLoop(String pathname, float startLatitude, float startLongitude, double ratio, double scale, int size_x, int size_y,int step_size,int threshold, Context context){
		this.size_x = size_x;
		this.size_y = size_y;
		this.tempThreshold = threshold;
		this.ratio = ratio;
		this.scale = scale;
		this.startLat = startLatitude;
		this.startLong = startLongitude;
		
		blueRov = new AUV(startLatitude, startLongitude,ratio, scale);
		wpGenerator = new wayPointGen(size_x,size_y);
		wayPoints = wpGenerator.sparseWayPointGen(step_size);
		worldOrder = new World(size_x,size_y);
		simWorld = new double[size_x][size_y];
		blueRov.setX(size_x - 1);
		//blueRov.setY(0);
		
		try {
			simWorld = txtRead.readFile(context, size_x, size_y);
		} catch (IOException e) {
			e.printStackTrace();
		}


	}
	
	public dataPoint simulatedTick(){

		Date date = new Date(); //grabs current time stamp
		dataPoint newData = new dataPoint(date,0,0,0);
		if(wayPoints.isEmpty()){
			sparseTraverse = false;
			worldOrder.pointsOfInterest = trim.trimmer(worldOrder.pointsOfInterest,2);
		}
		
		if(sparseTraverse){		
			Point p = nextPoint(wayPoints); //next coordinate to travel to ie. x+1,y or x,y+1
			double value = simWorld[(int) p.x][(int) p.y];
			newData = new dataPoint(date,(p.x * ratio / scale + this.startLat) ,(p.y* ratio / scale + this.startLong),value);
			//worldOrder.update(newData,startLat,startLong,ratio,scale);
			worldOrder.update(p.x,p.y,value);
			
			if(value >= this.tempThreshold){
				poiFound = true;
				worldOrder.pointsOfInterest.add(p);
			}
		}
		
		else if(!worldOrder.pointsOfInterest.isEmpty()){
			//System.out.println("Hello");
			//Calls trim every tick because user can add points at any time

			//worldOrder.pointsOfInterest.clear();
			//worldOrder.pointsOfInterest.addAll(tempSet);
			//on an empty waypoint list, grab nearest neighbor
			if(wayPoints.isEmpty()){
				Point nearest = nearestNeighbor.nearest(blueRov.currentPos, worldOrder.pointsOfInterest);
				List<Point> temp = wpGenerator.denseWayPointGen(nearest, 3);
				wayPoints.addAll(temp);
				worldOrder.pointsOfInterest.remove(nearest);
			}
			
			Point p = nextPoint(wayPoints);
			double value = simWorld[(int) p.x][(int) p.y];
			newData = new dataPoint(date,(p.x * ratio / scale + this.startLat) ,(p.y* ratio / scale + this.startLong),value);
			//worldOrder.update(newData,startLat,startLong,ratio,scale);
			worldOrder.update(p.x,p.y,value);
			
		}
		else{
			finishedFlag = true;
		}
		
		return newData;
	}
	
	public boolean isFinished(){
		return this.finishedFlag;
	}

	public boolean foundPoi() {return this.poiFound;}

	public void recordedPoi() { this.poiFound = false;}
	
	public List<LatLng> getEstimatedPath(){
		//Covert from cells to gps coor.
		List<LatLng> result = new ArrayList<LatLng>();
		result.add(new LatLng(blueRov.getLat(),blueRov.getLong()));
		for (Point p : wayPoints){
			result.add(new LatLng((p.x * ratio / scale + this.startLat) ,(p.y* ratio / scale + this.startLong)));
		}
		
		return result;
	}

	public void update(userPoint user){
			worldOrder.update(user,this.startLat,this.startLong,this.ratio, this.scale);
			worldOrder.pointsOfInterest = trim.trimmer(worldOrder.pointsOfInterest,2);

	}
	
}
