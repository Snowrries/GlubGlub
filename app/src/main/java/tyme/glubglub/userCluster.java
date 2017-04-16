package tyme.glubglub;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

/**
 * Created by infer on 4/15/2017.
 */

public class userCluster implements ClusterItem {
    private final LatLng mPosition;
    private String mTitle;
    private String mSnippet;

    public userCluster(double lat, double lng) {
        mPosition = new LatLng(lat, lng);
    }

    public userCluster(double lat, double lng, String title, String snippet) {
        mPosition = new LatLng(lat, lng);
        mTitle = title;
        mSnippet = snippet;
    }

    @Override
    public LatLng getPosition() {
        return mPosition;
    }

    @Override
    public String getTitle() {
        return mTitle;
    }

    @Override
    public String getSnippet() {
        return mSnippet;
    }
}
