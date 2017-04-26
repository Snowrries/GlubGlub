package tyme.glubglub;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.PointF;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.maps.android.clustering.ClusterManager;

import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import tyme.glubglub.algorithms.mainLoop;

public class connect extends FragmentActivity implements OnMapReadyCallback, confirmBounds.confirmBoundsListener {

	private GoogleMap mMap;
	public Activity thisActivity;
	private ClusterManager<userCluster> mClusterManager;
	public Polyline bounds;
	mainLoop mission;
	Marker originMarker;
	final double ratio = 5.4896944; //*10^-6
	ChildEventListener userListener;

	FirebaseDatabase database = FirebaseDatabase.getInstance();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_connect);
		// Obtain the SupportMapFragment and get notified when the map is ready to be used.
		SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map);
		mapFragment.getMapAsync(this);
		//Check if there is already a mission in progress here.

		//If there is a mission in progress, show a dialogue that informs the user as such
		//Then return the user to the main screen.
		//We will implement a connection stage to the AUV in the future.
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
	//If we have reached here, we know that there is no mission in progress.
	//We may authenticate to confirm identity, but for this iteration we will assume.
	//User at this stage is given 'admin permissions' and the ability to create a mission.
	//We will assume there is no race conditions, and only one admin. This will be refined in the future.
	//An initial marker will be created, and polylines drawn around to mark a boundary.
	//The user will be shown a dialogue asking if they wish to move the marker.
	//The marker will be draggable, and the polylines will be redrawn when the marker is finished moving.
	//When the marker is finished moving, we will pop up a dialogue asking for confirmation.
	//Once we have confirmation, we will publish this data to the firebase.
	//Then we will initialize an AUV object.
	//In future iterations, this will be linked to a real AUV.
	//This instance will then begin the simulation, and publish updates to the database.
	@Override
	public void onMapReady(GoogleMap googleMap) {
		mMap = googleMap;
		thisActivity = this;
		// Add a marker on the Raritan
		//40.508060, -74.455448
		LatLng raritan = new LatLng(40.508060, -74.455448);
		originMarker = mMap.addMarker(new MarkerOptions().position(raritan).title("Raritan River").draggable(true));
		mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(raritan, 15));

		double addLat = 100 * ratio / 1000000;
		double addLong = 100 * ratio / 1000000;
		// draw a polybox around the marker with dimensions 100x100, translated to geoposition with a constant ratio
		double x = raritan.latitude;
		double y = raritan.longitude;
		bounds = mMap.addPolyline(new PolylineOptions()
				.clickable(false)
				.add(
						new LatLng(x, y),
						new LatLng(x, y+addLong),
						new LatLng(x+addLat, y+addLong),
						new LatLng(x+addLat, y),
						new LatLng(x, y)));

		// pathname - file of our simulated world
		//startLatitude - starting latitude position
		//startLongitude - starting longitude position
		//ratio - cell size
		//scale - the ratio's exponential multiplicative factor
		//size_x - Boundary of rectangle
		//size_y - Boundary of rectangle
		//step_size - same as matlab, the short step of the sparseTraverse
		///threshold - the threshold of our POIs.  Temperature Ranges any number.

		mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
			@Override
			public void onMarkerDragStart(Marker marker) {

			}

			@Override
			public void onMarkerDrag(Marker marker) {
				double addLat = 100 * ratio / 1000000;
				double addLong = 100 * ratio / 1000000;
				double x = marker.getPosition().latitude;
				double y = marker.getPosition().longitude;
				bounds.remove();
				bounds = mMap.addPolyline(new PolylineOptions()
						.clickable(false)
						.add(
								new LatLng(x, y),
								new LatLng(x, y+addLong),
								new LatLng(x+addLat, y+addLong),
								new LatLng(x+addLat, y),
								new LatLng(x, y)));
			}

			@Override
			public void onMarkerDragEnd(Marker marker) {
				//originMarker = marker;Bundle bundle = new Bundle();
				confirmBounds cb = new confirmBounds();
				Bundle bundle = new Bundle();
				bundle.putDouble("lat", marker.getPosition().latitude);
				bundle.putDouble("lon", marker.getPosition().longitude);
				cb.setArguments(bundle);
				cb.show(getFragmentManager(), "AlertDialogFragment");
			}
		});

		mMap.setOnMapClickListener(
				new GoogleMap.OnMapClickListener(){
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
				if(mission != null)
					mission.update(up);
			}

			@Override
			public void onChildChanged(DataSnapshot dataSnapshot, String s) {
				userPoint up = dataSnapshot.getValue(userPoint.class);
				if(mission != null)
					mission.update(up);
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
	}

	@Override
	public void onDialogPositiveClick(DialogFragment dialog) {
		FirebaseDatabase db = FirebaseDatabase.getInstance();
		db.getReference("Waypoints").removeValue();
		db.getReference("Current").removeValue();
		db.getReference("CurrentUser").removeValue();
		originMarker.setDraggable(false);
		float startLatitude = (float) originMarker.getPosition().latitude;
		float startLongitude = (float) originMarker.getPosition().longitude;
		String pathname = "simWorld.txt";
		System.out.println("Mission being created!");
		mission = new mainLoop(pathname, startLatitude, startLongitude, ratio, 1000000, 100, 100,5,80, getApplicationContext());
		Timer timer = new Timer();
		TimerTask simulate = new TimerTask() {
			public HashMap<String, LatLng> toMap(List<LatLng> currentList) {
			HashMap<String, LatLng> result = new HashMap<>();
			for(int i = 0; i < currentList.size(); i++){
				result.put(Integer.toString(i), currentList.get(i));
			}
			return result;
		}
			@Override
			public void run() {
				FirebaseDatabase database = FirebaseDatabase.getInstance();
				List<LatLng> currentList = mission.getEstimatedPath();
				DatabaseReference myPts = database.getReference("Waypoints");
				myPts.setValue(toMap(currentList));
				if(mission.isFinished())
					cancel();
				//Get a datapoint
				//Send datapoint to firebase
				//Update firebase's ordered GPS waypoints if there are any changes
				System.out.println("Iteration");
				dataPoint st = mission.simulatedTick();
				DatabaseReference myRef = database.getReference("Current");
				myRef.push().setValue(st);
				if(mission.foundPoi()){
					//Update GPS waypoint list
					currentList = mission.getEstimatedPath();
					myPts.setValue(toMap(currentList));
					mission.recordedPoi();
				}
			}
		};
		timer.schedule(simulate, 0, 1000);
	}

	@Override
	public void onDialogNegativeClick(DialogFragment dialog) {
	}
	@Override
	protected void onStop() {
		super.onStop();

		database.getReference().child("CurrentUser").removeEventListener(userListener);
	}
}
