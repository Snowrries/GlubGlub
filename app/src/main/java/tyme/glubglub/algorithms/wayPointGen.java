package tyme.glubglub.algorithms;

import android.graphics.Point;
import java.util.ArrayList;
import java.util.List;

public class wayPointGen{
	int size_x;
	int size_y;
//	int short_width;
	
	public wayPointGen(int size_x,int size_y){
		this.size_x = size_x;
		this.size_y = size_y;
		//this.short_width = short_width;
	}
	private int[] genX(int short_width){
		int size = this.size_x/short_width * 2 + 1;
		int[] wayPointx = new int[size];
		int multiplier = 1;
		wayPointx[0] = 0;
		for(int i = 1; i < size; i+=2){
			wayPointx[i] = short_width * multiplier - 1;
			if(i+1 >= size){
				break;
			}
			wayPointx[i+1] = short_width * multiplier - 1;
			multiplier++;
		}
		return wayPointx;
	}
	
	private int[] genY(int short_width){
		int size = this.size_y/short_width * 2 + 1;
		int[] wayPointy = new int[size];
		int high = this.size_y - 1;
		int count = 0;
		for(int i = 0; i < size; i+=2){
			int val = high;
			if(count % 2 != 0){
				val = 0;
			}
			wayPointy[i] = val;
			if(i+1 >= size){
				break;
			}
			wayPointy[i+1] = val;
			count++;
		}
		return wayPointy;
	}
	
	public List<Point> sparseWayPointGen(int short_width){
		int[] x = genX(short_width);
		int[] y = genY(short_width);
		int len = Math.min(x.length, y.length);
		List<Point> wayPoints = new ArrayList<Point>();
		for(int i = 0; i < len; i++){
			wayPoints.add(new Point(x[i],y[i]));
		
		}
		
		return wayPoints;
	}
	
	public List<Point> denseWayPointGen(Point p, int width){
			List<Point> wayPoints = new ArrayList<Point>();
			wayPoints.add(p);
			Point prev = new Point(p);
			int multi = 0;
			for(int i = 0; i < 6; i++){
				int k = i % 4;
				int x = (int) prev.x;
				int y = (int) prev.y;
				switch (k) {
					case 0:
						multi++;
						x = (int) prev.x + width * multi;
						x = Math.min(x, this.size_x-1);
						break;
					case 1:
						y = (int) prev.y + width * multi;
						y = Math.min(y, this.size_y-1);
						break;
					case 2:
						multi++;
						x = (int) prev.x - width * multi;
						x = Math.max(x, 0);
						break;
					case 3:
						y = (int) prev.y - width * multi;
						y = Math.max(y, 0);
						break;
					
				}
				
				prev.set(x,y);
				wayPoints.add(new Point(x,y));
			}
			
			return wayPoints;
		
	}

	
}