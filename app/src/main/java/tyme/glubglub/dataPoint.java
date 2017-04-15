package tyme.glubglub;
import java.util.Date;
/**
 * Created by infer on 4/15/2017.
 */

public class dataPoint {
    public Date Time;
    public double Lat;
    public double Lng;
    public double Temp;

    public dataPoint(Date time, double lat, double lng, double temp){
        Time = time;
        Lat = lat;
        Lng = lng;
        Temp = temp;
    }
}
