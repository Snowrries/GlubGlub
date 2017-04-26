package tyme.glubglub;

import android.app.Activity;
import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.maps.model.TileProvider;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.heatmaps.HeatmapTileProvider;
import com.google.maps.android.heatmaps.WeightedLatLng;

import java.util.ArrayList;
import java.util.List;

import static android.R.id.list;


public class map extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    public Activity thisActivity;
    private ClusterManager<userCluster> mClusterManager;
	List<LatLng> waypoints = new ArrayList<LatLng>();
	ArrayList<WeightedLatLng> data = new ArrayList<WeightedLatLng>();
	Polyline path;
	FirebaseDatabase database = FirebaseDatabase.getInstance();
	ChildEventListener userListener, realData;
	ValueEventListener waypointListener;
	HeatmapTileProvider mProvider;
	TileOverlay mOverlay;


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
        mClusterManager = new ClusterManager<userCluster>(this, mMap);
        mMap.setOnCameraIdleListener(mClusterManager);
        mMap.setOnMarkerClickListener(mClusterManager);

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
        //Create a listener for changes to the firebase
        //Place the listener at the CurrentUser level
        userListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prev) {
                // Get userPoint object and use the values to update the UI
                userPoint up = dataSnapshot.getValue(userPoint.class);
                mClusterManager.addItem(new userCluster(up.Lat, up.Lng, ""+up.Sev, up.Time.toString()));
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                userPoint up = dataSnapshot.getValue(userPoint.class);
                mClusterManager.addItem(new userCluster(up.Lat, up.Lng, ""+up.Sev, up.Time.toString()));
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                //Do nothing, child should never be removed
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                //Do nothing, moving children should do nothing.
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("err", "loadUsersData:onCancelled", databaseError.toException());
                // ...
            }
        };
        database.getReference().child("CurrentUser").addChildEventListener(userListener);

		//Create another listener for updates to the GPS waypoints
		//Update a polyline path with this data in real time.
		waypointListener = new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {
				PolylineOptions po = new PolylineOptions().clickable(false)
						.width(2).color(Color.RED);
				for (DataSnapshot wp: dataSnapshot.getChildren()) {
					userCluster uc = wp.getValue(userCluster.class);
					LatLng L = new LatLng(uc.latitude,uc.longitude);
					waypoints.add(L);
					po.add(L);
					System.out.println("uc lat long: " + uc.latitude + " " + uc.longitude);
					//Currently this will not display spirals around pois
					//Implement this in the next iteration (or maybe don't?)
				}
				if(path!= null) path.remove();
				path = mMap.addPolyline(po);
			}

			@Override
			public void onCancelled(DatabaseError databaseError) {

			}
		};
		database.getReference().child("Waypoints").addValueEventListener(waypointListener);

		//Create another listener for updates to the firebase in real data
		//Update a heatmap with this data in real time.

		// Create a heat map tile provider, passing it the latlngs of the police stations.
		data.add(new WeightedLatLng(raritan, 0));
		mProvider = new HeatmapTileProvider.Builder()
				.weightedData(data).opacity(0).build();
		// Add a tile overlay to the map, using the heat map tile provider.
		mOverlay = mMap.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider));

		realData = new ChildEventListener() {
			@Override
			public void onChildAdded(DataSnapshot dataSnapshot, String s) {
				dataPoint dt = dataSnapshot.getValue(dataPoint.class);
				WeightedLatLng addme = new WeightedLatLng(new LatLng(dt.Lat,dt.Lng),dt.Temp/80);
				data.add(addme);
				mProvider.setOpacity(.5);
				mProvider.setWeightedData(data);
				mProvider.setRadius(10);
				mOverlay.clearTileCache();
			}

			@Override
			public void onChildChanged(DataSnapshot dataSnapshot, String s) {

			}

			@Override
			public void onChildRemoved(DataSnapshot dataSnapshot) {

			}

			@Override
			public void onChildMoved(DataSnapshot dataSnapshot, String s) {

			}

			@Override
			public void onCancelled(DatabaseError databaseError) {

			}
		};
		database.getReference().child("Current").addChildEventListener(realData);


    }

	@Override
	protected void onStop() {
		super.onStop();
		database.getReference().child("Current").removeEventListener(realData);
		database.getReference().child("Waypoints").removeEventListener(waypointListener);
		database.getReference().child("CurrentUser").removeEventListener(userListener);
	}
}
