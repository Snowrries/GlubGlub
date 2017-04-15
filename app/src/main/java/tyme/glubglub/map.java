package tyme.glubglub;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;

import static android.R.attr.fragment;

public class map extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    public Activity thisActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        thisActivity = this;
        // Add a marker on the Raritan
        //40.508060, -74.455448
        LatLng raritan = new LatLng(40.508060, -74.455448);
        mMap.addMarker(new MarkerOptions().position(raritan).title("Raritan River"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(raritan, 15));
        mMap.setOnMapClickListener(
                new OnMapClickListener(){
                    @Override
                    public void onMapClick(LatLng latLng) {

                        dialogue promp = new dialogue();
                        Bundle bundle = new Bundle();
                        bundle.putDouble("lat", latLng.latitude);
                        bundle.putDouble("lon", latLng.longitude);
                        promp.setArguments(bundle);
                        promp.show(getFragmentManager(), "AlertDialogFragment");
                    }
                }
        );
    }

}
