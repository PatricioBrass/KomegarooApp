package com.kome.hp.komegarooandroid.Fragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kome.hp.komegarooandroid.R;
import com.kome.hp.komegarooandroid.RoundedTransformation;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Map;


public class PopupDriverFragment extends Fragment {

    private ImageView photoDriver;
    private TextView nombre, certif;
    private RatingBar ratingBar;
    private DatabaseReference drivers, rTravel, rDriverStatus;
    private String uidClient, uidDriver, certi;
    private int index;
    private ArrayList<String> arrayDriver = new ArrayList<String>();
    private ArrayList<String> arrayClient = new ArrayList<String>();
    private Button compartir, close;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_popup_driver, container, false);
        rDriverStatus = FirebaseDatabase.getInstance().getReference().child("driverStatus").child("driverCoordenates").child("Santiago");
        drivers = FirebaseDatabase.getInstance().getReference().child("drivers");
        rTravel = FirebaseDatabase.getInstance().getReference().child("requestedTravels").child("Santiago");
        uidClient = FirebaseAuth.getInstance().getCurrentUser().getUid();
        photoDriver = (ImageView)v.findViewById(R.id.imgDriverPopup);
        nombre = (TextView)v.findViewById(R.id.txtNameDriver);
        certif = (TextView)v.findViewById(R.id.txtCerti);
        ratingBar = (RatingBar)v.findViewById(R.id.ratingBarDriverView);
        compartir = (Button)v.findViewById(R.id.buttonCompartir);
        compartir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Share();
            }
        });
        close = (Button)v.findViewById(R.id.btnCloseDriver);
        close.setClickable(true);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeFragment();
            }
        });
        getDriver();
        return v;
    }

    public void getDriver(){
        rDriverStatus.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for (DataSnapshot infoSnapshot : dataSnapshot.getChildren()) {
                    String uid = infoSnapshot.getKey();
                    String client = (String) infoSnapshot.child("customerUid").getValue();
                    arrayDriver.add(uid);
                    arrayClient.add(client);
                    }
                    if(arrayClient.contains(uidClient)){
                        index = arrayClient.indexOf(uidClient);
                        uidDriver = arrayDriver.get(index);
                        showDriver();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError firebaseError) {

            }
        });

    }

    public void showDriver(){
        drivers.child(uidDriver).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    Map<String, String> mapS = (Map<String, String>) dataSnapshot.getValue();
                    Map<Double, Double> mapD = (Map<Double, Double>) dataSnapshot.getValue();
                    String photo = mapS.get("photoUrl");
                    String name = mapS.get("name");
                    Double calif = mapD.get("calification");
                    Picasso.with(getActivity()).load(photo).transform(new RoundedTransformation(9,1)).into(photoDriver);
                    nombre.setText(name);
                    ratingBar.setRating(calif.floatValue());
                }
            }
            @Override
            public void onCancelled(DatabaseError firebaseError) {}
        });
        rTravel.child(uidClient).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    Map<String, String> mapS = (Map<String, String>) dataSnapshot.getValue();
                    certi = mapS.get("certificatedNumber");
                    Log.v("Certificado", String.valueOf(certi));
                    if(!certi.equals("")){
                        certif.setText(certi);
                        compartir.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError firebaseError) {

            }
        });
    }

    public void Share(){

        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Código de Seguridad");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, certi);
        startActivity(Intent.createChooser(sharingIntent, "Share via"));
    }

    public static PopupDriverFragment newInstance(String text) {
        PopupDriverFragment f = new PopupDriverFragment();
        Bundle b = new Bundle();
        b.putString("PopupDriver", text);
        f.setArguments(b);
        return f;
    }

    public void removeFragment(){
        FragmentManager fm = getFragmentManager();
        fm.beginTransaction().remove(PopupDriverFragment.this).commit();
    }


}
