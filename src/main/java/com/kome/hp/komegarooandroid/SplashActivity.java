package com.kome.hp.komegarooandroid;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

public class SplashActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference versionApp;
    private String versionName;
    private View actualizar;
    private Button playStore;
    private String playS;
    private DatabaseReference stateClient, mTravels;
    private String uidClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        versionName = "1.0.0";
        setContentView(R.layout.activity_splash);
        stateClient = FirebaseDatabase.getInstance().getReference().child("customerState");
        mTravels = FirebaseDatabase.getInstance().getReference().child("requestedTravels").child("Santiago");
        mAuth = FirebaseAuth.getInstance();
        versionApp = FirebaseDatabase.getInstance().getReference().child("appInfo");
        actualizar = findViewById(R.id.actualizar);
        playStore = (Button)findViewById(R.id.btn_playStore);
        getVersionApp();
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    public void getVersionApp(){
        versionApp.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, String> mapS = (Map<String, String>) dataSnapshot.getValue();
                String name = mapS.get("androidVersion");
                playS = mapS.get("androidApp");
                if(name.equals(versionName)){
                    new Handler().postDelayed(new Runnable(){
                        @Override
                        public void run() {
                            mAuthListener = new FirebaseAuth.AuthStateListener() {
                                @Override
                                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                                    if (firebaseAuth.getCurrentUser() != null) {
                                        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }else {
                                        Intent intent = new Intent(SplashActivity.this, TutorialActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            };
                            mAuth.addAuthStateListener(mAuthListener);
                        }
                    },5000);
                }else{
                    actualizar.setVisibility(View.VISIBLE);
                    playStore.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            launchMarket();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void launchMarket() {
        Uri uri = Uri.parse("market://details?id="+playS);
        Intent myAppLinkToMarket = new Intent(Intent.ACTION_VIEW, uri);
        try {
            startActivity(myAppLinkToMarket);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, " unable to find market app", Toast.LENGTH_LONG).show();
        }
    }
}
