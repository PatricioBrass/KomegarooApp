package com.kome.hp.komegarooandroid.MenuLaterales;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.facebook.login.LoginManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kome.hp.komegarooandroid.Fragment.EditPopupFragment;
import com.kome.hp.komegarooandroid.Fragment.VerificarFragment;
import com.kome.hp.komegarooandroid.MainActivity;
import com.kome.hp.komegarooandroid.R;
import com.kome.hp.komegarooandroid.RoundedTransformation;
import com.kome.hp.komegarooandroid.TutorialActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class PerfilActivity extends AppCompatActivity {

    private Button close, sClose;
    private DatabaseReference mRef;
    private TextView ema, nom, ape, num, trip, edit, dat, nomT, telT, corrT, envT, calT, nomApe;
    private ImageView pho;
    private RatingBar stars;
    private String uidClient;
    private View mProgressView;
    private View mPerfilFormView;
    private AlertDialog alertDialog;
    private Timer timer;
    private Integer trips;
    private Double califi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);
        mRef = FirebaseDatabase.getInstance().getReference().child("customers");
        uidClient = FirebaseAuth.getInstance().getCurrentUser().getUid();
        alertDialog = new AlertDialog.Builder(this, R.style.MyAlertDialogStyle).create();
        alertDialog.setTitle("Komegaroo");
        alertDialog.setMessage("No hay conexión a internet.");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        onBackPressed();
                        alertDialog.dismiss();
                    }
                });
        close = (Button) findViewById(R.id.btnPerfil);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        ema = (TextView)findViewById(R.id.txtCorreo);
        nom = (TextView)findViewById(R.id.txtNombre);
        Typeface face= Typeface.createFromAsset(getAssets(), "monserrat/Montserrat-Medium.ttf");
        Typeface face1= Typeface.createFromAsset(getAssets(), "monserrat/Montserrat-SemiBold.ttf");
        Typeface face2= Typeface.createFromAsset(getAssets(), "monserrat/Montserrat-Light.ttf");
        Typeface face3= Typeface.createFromAsset(getAssets(), "monserrat/Montserrat-Regular.ttf");
        nom.setTypeface(face);
        ape = (TextView)findViewById(R.id.txtApellido);
        ape.setTypeface(face);
        pho = (ImageView)findViewById(R.id.imgPhoto);
        num = (TextView)findViewById(R.id.txtNumero);
        num.setTypeface(face2);
        ema.setTypeface(face2);
        trip = (TextView)findViewById(R.id.txtTrips);
        trip.setTypeface(face3);
        stars = (RatingBar) findViewById(R.id.ratingBar);
        mPerfilFormView = findViewById(R.id.perfil_form);
        mProgressView = findViewById(R.id.progressBarPerfil);
        edit = (TextView) findViewById(R.id.editTxt);
        edit.setClickable(true);
        edit.setTypeface(face);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callEditPopuprFragment();
            }
        });
        dat = (TextView)findViewById(R.id.txtDatos);
        dat.setTypeface(face1);
        sClose = (Button)findViewById(R.id.btnSesionClose);
        sClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                metodo();
            }
        });
        nomApe = (TextView)findViewById(R.id.txtNombreApellido);
        nomApe.setTypeface(face2);

        nomT = (TextView)findViewById(R.id.nombreT);
        nomT.setTypeface(face1);

        telT = (TextView)findViewById(R.id.telefonoT);
        telT.setTypeface(face1);

        corrT = (TextView)findViewById(R.id.correoT);
        corrT.setTypeface(face1);

        envT = (TextView)findViewById(R.id.enviosT);
        envT.setTypeface(face3);

        calT = (TextView)findViewById(R.id.califT);
        calT.setTypeface(face3);
        showProgress(true);
        perfil();
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        alertDialog.show();
                    }
                });
            }
        },10000);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onStart(){
        super.onStart();
    }

    public void perfil(){

        mRef.child(uidClient).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    Map<Long, Long> map = (Map<Long, Long>) dataSnapshot.getValue();
                    Map<Double, Double> ma = (Map<Double, Double>) dataSnapshot.getValue();
                    Map<String, String> mapS = (Map<String, String>) dataSnapshot.getValue();
                    califi = ma.get("calification");
                    String email = mapS.get("email");
                    String name = mapS.get("name");
                    String phones = mapS.get("phoneNumber");
                    String photos = mapS.get("photoUrl");
                    trips = map.get("trips").intValue();
                    String nombre = name.substring(0,name.indexOf(" "));
                    String apellido = name.replace(nombre+" " ,"");
                    nomApe.setText(name);
                    nom.setText(nombre);
                    ape.setText(apellido);
                    ema.setText(email);
                    num.setText(phones);
                    Picasso.with(PerfilActivity.this).load(photos).transform(new RoundedTransformation(10,2)).into(pho);
                    timer.cancel();
                    stars.setRating(califi.floatValue());
                    trip.setText(trips.toString());
                    showProgress(false);
                }
            }

            @Override
            public void onCancelled(DatabaseError firebaseError) {}
        });
    }

    private void metodo(){
        MainActivity.getInstance().finish();
        LoginManager.getInstance().logOut();
        FirebaseAuth.getInstance().signOut();
        finish();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mPerfilFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mPerfilFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mPerfilFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
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

    public void callEditPopuprFragment() {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment fragment = EditPopupFragment.newInstance("EditPopup");
        fragmentTransaction.add(R.id.layoutPerfil, fragment);
        fragmentTransaction.commit();
    }

}