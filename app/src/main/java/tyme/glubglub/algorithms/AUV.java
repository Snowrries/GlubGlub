package tyme.glubglub.algorithms;

import android.graphics.Point;
import android.graphics.PointF;


public class AUV{
	
	PointF currentLatLong;
	Point currentPos;
	double ratio; //Degree to cell size
	double scale; //Generally 10^6
	float startLong;
	float startLat;
	
	public AUV(float latitude, float longitude, double ratio, double scale){
		this.currentLatLong = new PointF(latitude,longitude);
		this.currentPos = new Point(0,0);
		this.ratio = ratio;
		this.scale = scale;
		this.startLong = longitude;
		this.startLat = latitude;
	}
	
	public void setX(int x){
		if(x >= 0){
			this.currentPos.set(x,this.currentPos.y);
			this.currentLatLong.set((float) (this.startLat + x/ratio * scale),this.currentLatLong.y);
		}
		
	}
	
	public int getX(){
		return this.currentPos.x;
	}
	
	public void setY(int y){
		if(y >= 0){
			this.currentPos.set(this.currentPos.x,y);
			this.currentLatLong.set(this.currentLatLong.x, (float) (this.startLong + y/ratio * scale));
		}
		
	}
	
	public int getY(){
		return this.currentPos.y;
	}
	
	public double getLat(){
		return this.currentLatLong.x;
	}
	
	public double getLong(){
		return this.currentLatLong.x;
	}
	
	
}