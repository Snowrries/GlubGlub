package tyme.glubglub;

import java.util.Date;
/**
 * Created by infer on 4/15/2017.
 */

public class userPoint {
    public Date Time;
    public double Lat;
    public double Lng;
    public int Sev;

    public userPoint() {

    }
    public userPoint(Date time, double lat, double lng, int sev){
        Time = time;
        Lat = lat;
        Lng = lng;
        Sev = sev;
    }
}
