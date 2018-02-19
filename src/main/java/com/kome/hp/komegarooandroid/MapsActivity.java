package com.kome.hp.komegarooandroid;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.kome.hp.komegarooandroid.Fragment.Modules.DirectionFinder;
import com.kome.hp.komegarooandroid.Fragment.Modules.DirectionFinderListener;
import com.kome.hp.komegarooandroid.Fragment.Modules.Route;
import com.kome.hp.komegarooandroid.Fragment.PopupFragment;
import com.kome.hp.komegarooandroid.Fragment.PopupFragmentCerti;
import com.kome.hp.komegarooandroid.Fragment.VerificarFragment;
import com.mercadopago.model.Card;

import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, DirectionFinderListener {

    private static final String TAG = "maps";
    private GoogleMap mMap;
    private Button btn;
    public static final String MESSAGE_KEY="com.kome.hp.komegarooandroid.message_key";
    public static final String MESSAGE_KEYS="com.kome.hp.komegarooandroid.message_keys";
    private List<Marker> originMarkers = new ArrayList<>();
    private List<Marker> destinationMarkers = new ArrayList<>();
    private List<Polyline> polylinePaths = new ArrayList<>();
    private ProgressDialog progressDialog;
    private Button pedirK;
    private AlertDialog alertDialog;
    private Timer timer;
    public static final String MY_PREFS_NAME = "MyPrefsFile";
    public Card mCard;
    public AlertDialog alertDialog2;
    protected AlertDialog alertCard;
    protected ProgressBar progress;
    public Integer kmPrice;
    public Integer timePrice;
    public String msn1;
    public String msn2;
    private CheckBox checkRegreso;
    public Boolean checkReturn = false;
    private double pre5;
    private Double tiempo;
    private Double tiempoHrs;
    private Double tiempoMin;
    private Double distancia;
    private String tiempoLargo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        progress = (ProgressBar)findViewById(R.id.progressBarMaps);
        checkRegreso = (CheckBox) findViewById(R.id.checkBox);
        btn = (Button) findViewById(R.id.button);
        pedirK = (Button)findViewById(R.id.pedirKame);
        Intent intent = getIntent();
        msn1 = intent.getStringExtra(MESSAGE_KEY);
        msn2 =intent.getStringExtra(MESSAGE_KEYS);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        alertDialog = new AlertDialog.Builder(this, R.style.MyAlertDialogStyle).create();
        alertDialog.setTitle("Komegaroo");
        alertDialog.setMessage("No hay conexión a internet.");
        alertDialog.setCancelable(false);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        progressDialog.dismiss();
                        alertDialog.dismiss();
                        onBackPressed();
                    }
                });
        alertDialog2 = new AlertDialog.Builder(MapsActivity.this, R.style.MyAlertDialogStyle).create();
        alertDialog2.setTitle("KomeGaroo");
        String alert1 = "¿Cómo deseas tu envío?";
        String alert2 = "Los envíos certificados tienen un costo adicional de $0... GRATIS";
        alertDialog2.setMessage(alert1 +"\n"+"\n"+ alert2);
        alertDialog2.setButton(AlertDialog.BUTTON_NEGATIVE,"Sin Certificar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                callNoCertificadoFragment();
            }
        });
        alertDialog2.setButton(AlertDialog.BUTTON_POSITIVE,"Certificado", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                callCertificadoFragment();
            }
        });
        alertCard = new AlertDialog.Builder(this, R.style.MyAlertDialogStyle).create();
        alertCard.setTitle("Komegaroo");
        alertCard.setCancelable(false);
        alertCard.setMessage("Debe seleccionar una tarjeta como medio de pago.");
        alertCard.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        onBackPressed();
                        alertCard.dismiss();
                    }
                });
        pedirK = (Button)findViewById(R.id.pedirKame);
        pedirK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callVerificarFragment();
            }
        });
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
        },20000);
        getCard();
        checkRegreso();
    }

    public void checkRegreso(){
        checkRegreso.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                DecimalFormatSymbols simb = new DecimalFormatSymbols();
                simb.setGroupingSeparator('.');
                DecimalFormat form = new DecimalFormat("###,###", simb);
                if(isChecked){
                    checkReturn = true;
                    if(tiempoLargo.length()>5){
                        Double km = ((distancia * 190)+100)*2;
                        Double tm = ((((tiempoHrs * 60) + (tiempoMin *2)) *90)+100)*2;
                        kmPrice = km.intValue();
                        timePrice = tm.intValue();
                        double valor = kmPrice+timePrice + 500;
                        String duration = String.valueOf(form.format(((tiempoHrs * 2)*60)+(tiempoMin*2)));
                        String distance = String.valueOf(form.format(distancia * 2));
                        ((TextView) findViewById(R.id.tvPrecio)).setText("CLP $" + String.valueOf(form.format(valor)));
                        ((TextView) findViewById(R.id.tvDuration)).setText(duration + " mins.");
                        ((TextView) findViewById(R.id.tvDistance)).setText(distance +" km.");
                    } else {
                        Double km = ((distancia * 190) + 100)*2;
                        Double tm = ((tiempo * 90) + 100)*2;
                        kmPrice = km.intValue();
                        timePrice = tm.intValue();
                        double valor = kmPrice + timePrice + 500;
                        String duration = String.valueOf(form.format(tiempo * 2));
                        String distance = String.valueOf(form.format(distancia * 2));
                        if(valor<1800){
                            ((TextView) findViewById(R.id.tvPrecio)).setText("CLP $1.800");
                        }else {
                            ((TextView) findViewById(R.id.tvPrecio)).setText("CLP $" + String.valueOf(form.format(valor)));
                            ((TextView) findViewById(R.id.tvDuration)).setText(duration + " mins.");
                            ((TextView) findViewById(R.id.tvDistance)).setText(distance + " km.");
                        }
                    }
                }else{
                    checkReturn = false;
                    if(pre5>1800&&tiempoLargo.length()<5) {
                        ((TextView) findViewById(R.id.tvPrecio)).setText("CLP $" + String.valueOf(form.format(pre5)));
                        ((TextView) findViewById(R.id.tvDuration)).setText(String.valueOf(form.format(tiempo)) + " mins.");
                        ((TextView) findViewById(R.id.tvDistance)).setText(String.valueOf(form.format(distancia)) +" km.");
                        Double km = ((distancia * 190)+100);
                        Double tm = ((tiempo * 90)+100);
                        kmPrice = km.intValue();
                        timePrice = tm.intValue();
                    }else if (tiempoLargo.length()>5){
                        ((TextView) findViewById(R.id.tvPrecio)).setText("CLP $" + String.valueOf(form.format(pre5)));
                        ((TextView) findViewById(R.id.tvDuration)).setText(tiempoLargo + " mins.");
                        ((TextView) findViewById(R.id.tvDistance)).setText(String.valueOf(form.format(distancia)) +" km.");
                        Double km = ((distancia * 190)+100);
                        Double tm = ((((tiempoHrs * 60) + (tiempoMin *2)) *90)+100);
                        kmPrice = km.intValue();
                        timePrice = tm.intValue();
                    }else {
                        ((TextView) findViewById(R.id.tvPrecio)).setText("CLP $1.800");
                        ((TextView) findViewById(R.id.tvDuration)).setText(String.valueOf(form.format(tiempo)) + " mins.");
                        ((TextView) findViewById(R.id.tvDistance)).setText(String.valueOf(form.format(distancia)) +" km.");
                        kmPrice = 850;
                        timePrice = 450;
                    }
                }
            }
        });
    }

    public void metodo(){
        super.onBackPressed();
        finish();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng santiago = new LatLng(-33.4724227, -70.7699159);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(santiago, 9));
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop(){
        super.onStop();
    }

    @Override
    public void onDirectionFinderStart() {
        progressDialog = ProgressDialog.show(this, "Un momento.",
                "Generando ruta..!", true);
            if (originMarkers != null) {
                for (Marker marker : originMarkers) {
                    marker.remove();
                }
            }
            if (destinationMarkers != null) {
                for (Marker marker : destinationMarkers) {
                    marker.remove();
                }
            }
            if (polylinePaths != null) {
                for (Polyline polyline : polylinePaths) {
                    polyline.remove();
                }
            }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void onDirectionFinderSuccess(List<Route> routes) {
        timer.cancel();
        progressDialog.dismiss();
        polylinePaths = new ArrayList<>();
        originMarkers = new ArrayList<>();
        destinationMarkers = new ArrayList<>();
        AlertDialog alertDialog = new AlertDialog.Builder(MapsActivity.this, R.style.MyAlertDialogStyle).create();
        alertDialog.setTitle("Dirección fallida");
        alertDialog.setMessage("La dirección ingresada está fuera de alcance.");
        alertDialog.setCancelable(false);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        onBackPressed();
                    }
                });
        AlertDialog alertDialog2 = new AlertDialog.Builder(MapsActivity.this, R.style.MyAlertDialogStyle).create();
        alertDialog2.setTitle("Dirección fallida");
        alertDialog2.setMessage("La dirección ingresada debe ser más específica.");
        alertDialog2.setCancelable(false);
        alertDialog2.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        onBackPressed();
                    }
                });
        if (!routes.isEmpty()){
            for (Route routess : routes){
                LatLngBounds mBounds= new LatLngBounds(
                        new LatLng(-33.9012253,-70.899347),
                        new LatLng(-33.2575545, -70.2504896));
                if(mBounds.contains(new LatLng(routess.startLocation.latitude, routess.startLocation.longitude))&&mBounds.contains(new LatLng(routess.endLocation.latitude, routess.endLocation.longitude))){
        for (Route route : routes) {
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            ((TextView) this.findViewById(R.id.tvDuration)).setText(route.duration.text + " mins.");
            ((TextView) this.findViewById(R.id.tvDistance)).setText(route.distance.text+".");
            Log.v("Tiempo",route.duration.text);
            DecimalFormatSymbols simb = new DecimalFormatSymbols();
            simb.setGroupingSeparator('.');
            DecimalFormat form = new DecimalFormat("###,###", simb);
            tiempoLargo = route.duration.text;
                if (tiempoLargo.length() > 5) {
                    distancia = Double.parseDouble((route.distance.text).replace("km", ""));
                    tiempoHrs = Double.parseDouble((route.duration.text).substring(0, 1));
                    tiempoMin = Double.parseDouble((route.duration.text).substring(route.duration.text.length() - 2));
                    pre5 = (((((tiempoHrs * 60) + tiempoMin) *60)+100) + ((distancia * 170)+100) + 500);
                    ((TextView) this.findViewById(R.id.tvPrecio)).setText("CLP $" + String.valueOf(form.format(pre5)));
                    Double kPrice = ((distancia * 170)+100);
                    Double tPrice = ((((tiempoHrs * 60) + tiempoMin) *60)+100);
                    kmPrice = kPrice.intValue();
                    timePrice = tPrice.intValue();
                } else {
                    if(route.duration.text.contains("min")){
                        tiempo = Double.parseDouble((route.duration.text).replace(" min",""));
                        ((TextView) this.findViewById(R.id.tvDuration)).setText(route.duration.text);
                    }else{
                        tiempo = Double.parseDouble(route.duration.text);
                    }
                    distancia = Double.parseDouble((route.distance.text).replace("km", ""));
                    pre5 = (((tiempo * 60)+100) + ((distancia * 170)+100) + 500);
                    Double kPrice2 = ((distancia * 170)+100);
                    Double tPrice2 = ((tiempo * 60)+100);
                    if(pre5>1800) {
                        ((TextView) this.findViewById(R.id.tvPrecio)).setText("CLP $" + String.valueOf(form.format(pre5)));
                        kmPrice = kPrice2.intValue();
                        timePrice = tPrice2.intValue();
                    }else{
                        ((TextView) this.findViewById(R.id.tvPrecio)).setText("CLP $1.800");
                        kmPrice = 850;
                        timePrice = 450;
                    }
                }
                mMap.getUiSettings().setScrollGesturesEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mMap.getUiSettings().setZoomControlsEnabled(false);
                originMarkers.add(mMap.addMarker(new MarkerOptions()
                        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.inicio))
                        .title(route.startAddress)
                        .position(route.startLocation)));
                destinationMarkers.add(mMap.addMarker(new MarkerOptions()
                        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_final))
                        .title(route.endAddress)
                        .position(route.endLocation)));
                PolylineOptions polylineOptions = new PolylineOptions().
                        geodesic(true).
                        color(Color.rgb(119, 21, 204)).
                        width(8);
                for (int i = 0; i < route.points.size(); i++){
                    polylineOptions.add(route.points.get(i));
                    builder.include(route.points.get(i));}
                polylinePaths.add(mMap.addPolyline(polylineOptions));
            builder.include(route.startLocation);
            builder.include(route.endLocation);
            LatLngBounds bounds = builder.build();
            mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
            } }else {
                    alertDialog.show();
                    return;
                }}} else {
            alertDialog2.show();
            return;
        }

    }

    public void getCard(){
        SharedPreferences mPrefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        Gson gson = new Gson();
        String json = mPrefs.getString("Card","");
        mCard = gson.fromJson(json, Card.class);
        if(mCard==null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    timer.cancel();
                    alertCard.show();
                }
            });
        }else{
            try {
                new DirectionFinder(this, msn1, msn2).execute();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }

    public void callCertificadoFragment() {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment fragment = PopupFragmentCerti.newInstance("Certificado");
        fragmentTransaction.add(R.id.mapsActivity, fragment);
        fragmentTransaction.commit();
    }
    public void callNoCertificadoFragment() {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment fragment = PopupFragment.newInstance("NoCertificado");
        fragmentTransaction.add(R.id.mapsActivity, fragment);
        fragmentTransaction.commit();
    }
    public void callVerificarFragment() {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment fragment = VerificarFragment.newInstance("Verificar");
        fragmentTransaction.add(R.id.mapsActivity, fragment);
        fragmentTransaction.commit();
    }
}
