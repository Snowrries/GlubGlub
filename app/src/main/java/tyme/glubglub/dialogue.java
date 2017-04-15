package tyme.glubglub;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 */
public class dialogue extends DialogFragment {


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
        builder.setTitle("Indicate severity")
                .setItems(lmh, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // The 'which' argument contains the index position
                        // of the selected item
                        // Send data to firebase with the given lat, long, and severity.
                        // Write a message to the database
                        if(which != 3) {
                            userPoint up = new userPoint(new Date(), lat,lon, which);
                            System.out.println("userPoint created: " + up);
                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            DatabaseReference myRef = database.getReference("CurrentUser");
                            myRef.push().setValue(up);
                        }
                    }
                });
        return builder.create();
    }

}
