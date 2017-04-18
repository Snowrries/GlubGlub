package tyme.glubglub;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;

/**
 * Created by infer on 4/18/2017.
 */

public class confirmBounds extends DialogFragment {

	public interface confirmBoundsListener{
		public void onDialogPositiveClick(DialogFragment dialog);
		public void onDialogNegativeClick(DialogFragment dialog);
	}
	confirmBoundsListener mListener;

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		// Verify that the host activity implements the callback interface
		try {
			// Instantiate the NoticeDialogListener so we can send events to the host
			mListener = (confirmBoundsListener) context;
		} catch (ClassCastException e) {
			// The activity doesn't implement the interface, throw exception
			throw new ClassCastException(context.toString()
					+ " must implement NoticeDialogListener");
		}
	}


	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState){

		Bundle latLong = this.getArguments();
		if(latLong == null){
			return null;
		}
		//if (latLong != null){
		final double lat = latLong.getDouble("lat", 0);
		final double lon = latLong.getDouble("lon", 0);
		//}
		System.out.println("lat: " + lat + "lon: " + lon);
		String[] lmh = new String[]{"Low", "Medium", "High", "Cancel"};
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setMessage("Your currently selected start point is: \nlat: "+lat+"\nlng: " + lon + "\n Is that ok?\n Mission will begin if ok.");
		builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				mListener.onDialogPositiveClick(confirmBounds.this);
			}});
		builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				mListener.onDialogNegativeClick(confirmBounds.this);
			}});
		return builder.create();
	}
}
