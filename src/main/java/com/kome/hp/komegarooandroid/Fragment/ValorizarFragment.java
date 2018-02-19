package com.kome.hp.komegarooandroid.Fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kome.hp.komegarooandroid.MainActivity;
import com.kome.hp.komegarooandroid.R;
import com.kome.hp.komegarooandroid.RoundedTransformation;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Map;

public class ValorizarFragment extends Fragment {
    private RatingBar rating;
    private Button btnV;
    private DatabaseReference travel, drivers, dTravels, stateCustomer;
    private String uidClient, key, uidDriver;
    private ImageView imageDriver;
    private View mProgressView, mPerfilFormView;
    private EditText coment;
    private Double calification;
    private Integer califica;
    private Integer trips;

    public ValorizarFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_valorizar, container, false);
        travel = FirebaseDatabase.getInstance().getReference().child("customerTravels");
        drivers = FirebaseDatabase.getInstance().getReference().child("drivers");
        dTravels = FirebaseDatabase.getInstance().getReference().child("driverTravels");
        stateCustomer = FirebaseDatabase.getInstance().getReference().child("customerState");
        uidClient = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mPerfilFormView = v.findViewById(R.id.valorLayout);
        mProgressView = v.findViewById(R.id.progressBarValorizar);
        rating = (RatingBar)v.findViewById(R.id.ratingBarDriver);
        rating.setClickable(true);
        coment = (EditText)v.findViewById(R.id.editTextComent);
        imageDriver = (ImageView)v.findViewById(R.id.imageViewDriver);
        btnV = (Button)v.findViewById(R.id.btnValor);
        btnV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stateCustomer.child(uidClient).child("state").setValue("nil");
                califica = Math.round(rating.getRating());
                dTravels.child(uidDriver).child(key).child("calification").setValue(califica.toString());
                dTravels.child(uidDriver).child(key).child("comments").setValue(coment.getText().toString());
                ((MainActivity)getActivity()).showDrawer();
                sendCalificationFirebase();
                removeFragment();
            }
        });
        showProgress(true);
        getKey();
        return v;
    }

    public void getKey(){
        travel.child(uidClient).orderByKey().limitToLast(1)
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    Map<String, String> mapS = (Map<String, String>) dataSnapshot.getValue();
                    key = mapS.keySet().toString().replace("[","").replace("]","");
                    getDriver();

                }
            }

            @Override
            public void onCancelled(DatabaseError firebaseError) {

            }
        });
    }
    public void getDriver(){
        travel.child(uidClient).child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, String> mapS = (Map<String, String>) dataSnapshot.getValue();
                uidDriver = mapS.get("driverUid");
                showDriver();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void showDriver(){
        if(uidDriver!=null) {
            drivers.child(uidDriver).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Map<String, String> mapS = (Map<String, String>) dataSnapshot.getValue();
                        Map<Double, Double> mapD = (Map<Double, Double>) dataSnapshot.getValue();
                        Map<Long, Long> mapI = (Map<Long, Long>) dataSnapshot.getValue();
                        calification = mapD.get("calification");
                        trips = mapI.get("trips").intValue();
                        String photo = mapS.get("photoUrl");
                        Picasso.with(getActivity()).load(photo).transform(new RoundedTransformation(8, 1)).into(imageDriver);
                        showProgress(false);
                    }
                }

                @Override
                public void onCancelled(DatabaseError firebaseError) {

                }
            });
        }

    }

    public void sendCalificationFirebase(){
        Double calificacion0 = ((calification+califica)/2);
        Double calificacion1 = (((trips*calification)+califica)/(trips + 1));
        Double calificacion2 = calificacion1+ 0.01;
        Integer viajes = trips + 1;
        drivers.child(uidDriver).child("trips").setValue(viajes);
        if ((calificacion1 == Math.floor(calificacion1)) && !Double.isInfinite(calificacion1)) {
            drivers.child(uidDriver).child("calification").setValue(calificacion2);
        }else{
            if (!trips.equals(0)) {
                drivers.child(uidDriver).child("calification").setValue(calificacion1);
            } else {
                drivers.child(uidDriver).child("calification").setValue(calificacion0);
            }
        }
    }


    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {

            mPerfilFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mPerfilFormView.animate().alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mPerfilFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mPerfilFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    public static ValorizarFragment newInstance(String text) {
        ValorizarFragment f = new ValorizarFragment();
        Bundle b = new Bundle();
        b.putString("Valorizar", text);
        f.setArguments(b);
        return f;
    }

    public void removeFragment(){
        FragmentManager fm = getFragmentManager();
        fm.beginTransaction().remove(ValorizarFragment.this).commit();
    }

}
