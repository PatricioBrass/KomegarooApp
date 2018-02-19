package com.kome.hp.komegarooandroid.Fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ScaleDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kome.hp.komegarooandroid.MainActivity;
import com.kome.hp.komegarooandroid.MapsActivity;
import com.kome.hp.komegarooandroid.R;
import com.kome.hp.komegarooandroid.RoundedTransformation;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static android.R.attr.scaleHeight;
import static android.R.attr.scaleWidth;
import static android.app.Activity.RESULT_OK;
import static com.kome.hp.komegarooandroid.R.id.map;

/**
 * Created by HP on 18/10/2016.
 */

public class  MapsFragment extends Fragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private static final int PLACE_PICKER_FLAG = 1;
    public static final String MESSAGE_KEY = "com.kome.hp.komegarooandroid.message_key";
    public static final String MESSAGE_KEYS = "com.kome.hp.komegarooandroid.message_keys";
    private PlacesAutoCompleteAdapter mPlacesAdapter;
    Marker mCurrLocationMarker;
    Location mLastLocation;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    public GoogleMap mMap;
    private Button btnFindPath, fab, call;
    private ImageButton btnFindPath2, btnFindPath3;
    private AutoCompleteTextView etOrigin;
    private AutoCompleteTextView etDestination;
    private DatabaseReference mRef, sRef, mTravels, rDriverStatus, stateClient,tripState, travelCustomer, drivers,paymentStatus, payment, validation;
    private ArrayList<String> arrayClient = new ArrayList<>();
    private ArrayList<String> arrayClient2 = new ArrayList<>();
    private ArrayList<String> arrayDriver2 = new ArrayList<>();
    private ArrayList<String> arrayDriver = new ArrayList<>();
    private List<Double> arrayDistancia = new ArrayList<>();
    private String uidClient, origen, destino, estado, estadoTrip;
    public String uidDriver2, key, token;
    private List<Marker> driverMarkers = new ArrayList<>();
    View mMapView, travel, solicitado, mProgressView, mPerfilFormView, v;
    private Timer timer3;
    private Timer timer2;
    private ImageView imageDriver;
    private TextView nameD;
    private AlertDialog cancelDriver, cancelCustomer, alertMal;
    private Double lat, lng;
    protected String status;
    protected String statusDetail;
    protected String tokenPago;
    protected String payer;
    protected String paymentMethod;
    private View ready;
    private Button listo;
    private AlertDialog alertDialog;
    private LatLng latLng;
    public boolean returnDriver=true;
    private View active;
    private Button btnActivar;

    public MapsFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.content_main, container, false);
        buildGoogleApiClient();
        mRef = FirebaseDatabase.getInstance().getReference().child("driverStatus").child("availableDrivers").child("Santiago");
        sRef = FirebaseDatabase.getInstance().getReference().child("driverStatus").child("requestedDrivers").child("Santiago");
        rDriverStatus = FirebaseDatabase.getInstance().getReference().child("driverStatus").child("driverCoordenates").child("Santiago");
        stateClient = FirebaseDatabase.getInstance().getReference().child("customerState");
        tripState = FirebaseDatabase.getInstance().getReference().child("tripState");
        travelCustomer = FirebaseDatabase.getInstance().getReference().child("customerTravels");
        mTravels = FirebaseDatabase.getInstance().getReference().child("requestedTravels").child("Santiago");
        drivers = FirebaseDatabase.getInstance().getReference().child("drivers");
        paymentStatus = FirebaseDatabase.getInstance().getReference().child("paymentStatus");
        payment = FirebaseDatabase.getInstance().getReference().child("customerPayments");
        validation = FirebaseDatabase.getInstance().getReference().child("customerValidation");
        uidClient = FirebaseAuth.getInstance().getCurrentUser().getUid();
        ready = v.findViewById(R.id.layout_ready);
        listo = (Button)v.findViewById(R.id.buttonReady);
        active = v.findViewById(R.id.active_localiza);
        btnActivar = (Button)v.findViewById(R.id.btnActivar);
        btnActivar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent( Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(myIntent);
                ((MainActivity)getActivity()).showDrawer();
                active.setVisibility(View.GONE);
            }
        });
        alertMal = new AlertDialog.Builder(getActivity(), R.style.MyAlertDialogStyle).create();
        alertMal.setTitle("Komegaroo");
        alertMal.setMessage("Ups! Algo fallo, vuelva a intentarlo más tarde.");
        alertMal.setCancelable(false);
        alertMal.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        alertMal.dismiss();
                    }
                });
        listo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).showToolbar();
                ((MainActivity) getActivity()).loadData();
                ready.setVisibility(View.GONE);
                FirebaseAuth.getInstance().getCurrentUser().reload().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            verificationEmail();
                        }
                    }
                });
            }
        });
        verificationEmail();
        ImageView myImage = new ImageView(getActivity());
        myImage.setImageResource(R.drawable.powered_by_google_on_white);
        mPerfilFormView = v.findViewById(R.id.content);
        mProgressView = v.findViewById(R.id.progressBarContent);
        btnFindPath = (Button) v.findViewById(R.id.btnFindPath);
        btnFindPath2 = (ImageButton) v.findViewById(R.id.imageButton);
        btnFindPath3 = (ImageButton) v.findViewById(R.id.imageButton2);
        etOrigin = (AutoCompleteTextView) v.findViewById(R.id.etOrigin);
        etOrigin.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.inicio_edit, 0, 0, 0);
        etDestination = (AutoCompleteTextView) v.findViewById(R.id.etDestination);
        etDestination.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.final_edit, 0, 0, 0);
        etOrigin.setOnItemClickListener(mAutocompleteClickListener);
        etDestination.setOnItemClickListener(mAutocompleteClickListener);
        mPlacesAdapter = new PlacesAutoCompleteAdapter(getActivity(), android.R.layout.simple_list_item_1,
                mGoogleApiClient, null, null, myImage);
        etOrigin.setAdapter(mPlacesAdapter);
        etDestination.setAdapter(mPlacesAdapter);
        solicitado = v.findViewById(R.id.gif);
        solicitado.setVisibility(View.GONE);
        travel = v.findViewById(R.id.layoutTravel);
        nameD = (TextView) v.findViewById(R.id.txtNameDriver);
        imageDriver = (ImageView) v.findViewById(R.id.imageDrivers);
        call = (Button) v.findViewById(R.id.btnCall);
        cancelDriver = new AlertDialog.Builder(getActivity(), R.style.MyAlertDialogStyle).create();
        cancelCustomer = new AlertDialog.Builder(getActivity(), R.style.MyAlertDialogStyle).create();
        fab = (Button) v.findViewById(R.id.fab);
        btnFindPath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                send();
            }
        });
        btnFindPath2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etOrigin.setText("");
            }
        });
        btnFindPath3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etDestination.setText("");
            }
        });
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }
        showClearButton();
        btnFindPath2.setVisibility(View.GONE);
        btnFindPath3.setVisibility(View.GONE);
        solicitado.setVisibility(View.GONE);
        stateCustomer();
        validationReturn();
        updateData();
        canceled();
        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MapFragment fragment = (MapFragment) getChildFragmentManager().findFragmentById(map);
        mMapView = fragment.getView();
        fragment.getMapAsync(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PLACE_PICKER_FLAG:
                    Place place = PlaceAutocomplete.getPlace(getActivity(), data);
                    etOrigin.setText(place.getName() + ", " + place.getAddress());
                    etDestination.setText(place.getName() + ", " + place.getAddress());
                    break;
            }
        }
    }

    private AdapterView.OnItemClickListener mAutocompleteClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final PlacesAutoCompleteAdapter.PlaceAutocomplete item = mPlacesAdapter.getItem(position);
            final String placeId = String.valueOf(item.placeId);
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
            InputMethodManager in = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            in.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
        }
    };

    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback
            = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                Log.e("place", "Place query did not complete. Error: " +
                        places.getStatus().toString());
                return;
            }
            final Place place = places.get(0);
        }
    };

    public void valorizar(){
                    if(estado.equals("endTrip")){
                        callValorizarFragment();
                        etOrigin.setVisibility(View.VISIBLE);
                        etDestination.setVisibility(View.VISIBLE);
                        btnFindPath2.setVisibility(View.VISIBLE);
                        btnFindPath3.setVisibility(View.VISIBLE);
                        btnFindPath.setVisibility(View.VISIBLE);
                        travel.setVisibility(View.GONE);
                        etDestination.setText("");
                        etOrigin.setText("");
                        mMap.clear();
                        arrayDriver.clear();
                        arrayClient.clear();
                        arrayClient2.clear();
                        uidDriver2=null;
                        returnDriver = true;
                        buildGoogleApiClient();
                    }
    }

    public void Show() {
        if(uidDriver2!=null) {
            drivers.child(uidDriver2).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Map<String, String> mapS = (Map<String, String>) dataSnapshot.getValue();
                        String name = mapS.get("name");
                        String photo = mapS.get("photoUrl");
                        final String phone = mapS.get("phoneNumber");
                        nameD.setText(name);
                        Picasso.with(getActivity()).load(photo).transform(new RoundedTransformation(8, 1)).into(imageDriver);
                        final AlertDialog alertDialog = new AlertDialog.Builder(getActivity(), R.style.MyAlertDialogStyle).create();
                        alertDialog.setTitle(name);
                        alertDialog.setMessage(phone);
                        alertDialog.setCancelable(false);
                        alertDialog.setButton("Cancelar", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                alertDialog.closeOptionsMenu();
                            }
                        });
                        alertDialog.setButton2("Llamar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String uri = "tel:" + phone.trim();
                                Intent intent = new Intent(Intent.ACTION_CALL);
                                intent.setData(Uri.parse(uri));
                                startActivity(intent);
                            }
                        });
                        call.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                alertDialog.show();
                            }
                        });
                        imageDriver.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                callPopupDriverFragment();
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(DatabaseError firebaseError) {
                }
            });
        }
        }

    public void showClearButton() {
        etOrigin.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (s.toString().isEmpty()) {
                    btnFindPath2.setVisibility(View.GONE);
                } else {
                    btnFindPath2.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().isEmpty()) {
                    btnFindPath2.setVisibility(View.GONE);
                } else {
                    btnFindPath2.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().isEmpty()) {
                    btnFindPath2.setVisibility(View.GONE);
                } else {
                    btnFindPath2.setVisibility(View.VISIBLE);
                }
            }
        });
        etDestination.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (s.toString().isEmpty()) {
                    btnFindPath3.setVisibility(View.GONE);
                } else {
                    btnFindPath3.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().isEmpty()) {
                    btnFindPath3.setVisibility(View.GONE);
                } else {
                    btnFindPath3.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().isEmpty()) {
                    btnFindPath3.setVisibility(View.GONE);
                } else {
                    btnFindPath3.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    public void getDriver() {
        mTravels.child(uidClient).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    if(estado.equals("nil")&&latLng!=null){
                        solicitado.setVisibility(View.VISIBLE);
                        etOrigin.setVisibility(View.GONE);
                        etDestination.setVisibility(View.GONE);
                        btnFindPath2.setVisibility(View.GONE);
                        btnFindPath3.setVisibility(View.GONE);
                        btnFindPath.setVisibility(View.GONE);
                        fab.setVisibility(View.VISIBLE);
                        ((MainActivity) getActivity()).hideDrawer();
                        reCallDriver();
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError firebaseError) {}
        });
    }

    public static double distFrom(double lat1, double lng1, double lat2, double lng2) {
        double earthRadius = 6371000; //meters
        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lng2-lng1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLng/2) * Math.sin(dLng/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double dist = (double) (earthRadius * c);

        return dist;
    }
    public LatLng getLocationFromAddress(String strAddress){
        Geocoder coder = new Geocoder(getActivity() , Locale.ENGLISH);
        List<Address> address;
        LatLng p1 = null;
        try {
            address = coder.getFromLocationName(strAddress,4);
            if (address==null) {
                return null;
            }
            Address location=address.get(0);
            location.getLatitude();
            location.getLongitude();

            p1 = new LatLng(location.getLatitude(), location.getLongitude());
            Log.v("LATLNG",p1.toString());

        } catch (IOException e) {
            e.printStackTrace();
        }
        return p1;
    }

    public void reCallDriver() {
        timer2 = new Timer();
        timer2.schedule(new TimerTask() {
            @Override
            public void run() {
                mRef.orderByValue().addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(final DataSnapshot dataSnapshot2) {
                        if (dataSnapshot2.exists()) {
                            String uid;
                            Double lat, lng;
                            arrayDistancia.clear();
                            for (DataSnapshot infoSnapshot : dataSnapshot2.getChildren()) {
                                uid = infoSnapshot.getKey();
                                lat = (Double) infoSnapshot.child("latitude").getValue();
                                lng = (Double) infoSnapshot.child("longitude").getValue();
                                if (!arrayDriver.contains(uid)) {
                                    if (lng != null && lat != null && latLng != null) {
                                        final Set<String> hs = new HashSet<>();
                                        double distancia = distFrom(lat, lng, latLng.latitude, latLng.longitude);
                                        arrayDriver2.add(uid);
                                        hs.addAll(arrayDriver2);
                                        arrayDriver2.clear();
                                        arrayDriver2.addAll(hs);
                                        arrayDistancia.add(distancia);
                                    }
                                }
                            }
                        }
                        callDriver();
                    }
                    @Override
                    public void onCancelled(DatabaseError firebaseError) {}
                });
            }
        },9000, 9000);
        timer3 = new Timer();
        timer3.schedule(new TimerTask() {
            @Override
            public void run() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        timer2.cancel();
                        mTravels.child(uidClient).removeValue();
                        solicitado.setVisibility(View.GONE);
                        etOrigin.setVisibility(View.VISIBLE);
                        etDestination.setVisibility(View.VISIBLE);
                        btnFindPath2.setVisibility(View.VISIBLE);
                        btnFindPath3.setVisibility(View.VISIBLE);
                        btnFindPath.setVisibility(View.VISIBLE);
                        etDestination.setText("");
                        etOrigin.setText("");
                        ((MainActivity) getActivity()).showDrawer();
                        arrayClient.clear();
                        arrayDriver.clear();
                        arrayDriver2.clear();
                        arrayDistancia.clear();
                        uidDriver2 = null;
                        timer3.cancel();
                    }
                });
            }
        },95000, 95000);
    }

    public void callDriver(){
        if (!arrayDriver2.isEmpty() && !arrayDistancia.isEmpty() && !arrayClient2.contains(uidClient)) {
            if (uidDriver2 != null) {
                sRef.child(uidDriver2).removeValue();
            }
            final Double i = Collections.min(arrayDistancia);
            final int v = arrayDistancia.indexOf(i);
            uidDriver2 = arrayDriver2.get(v);
            Log.v("DistanciaArray", arrayDistancia.toString());
            Log.v("DriverArray", arrayDriver2.toString());
            Log.v("DistanciaDriver", i.toString());
            Log.v("Driver", uidDriver2);
            sRef.child(uidDriver2).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (!dataSnapshot.exists()) {
                        if (!arrayDriver.contains(uidDriver2) && i < 5000) {
                            getToken();
                            arrayDriver.add(uidDriver2);
                            arrayDriver2.clear();
                            arrayDistancia.clear();
                            sRef.child(uidDriver2).child("customerUid").setValue(uidClient);
                            mRef.child(uidDriver2).removeValue();
                        } else {
                            arrayDriver2.clear();
                            arrayDistancia.clear();
                        }
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }else {
            if (uidDriver2 != null) {
                sRef.child(uidDriver2).removeValue();
            }
        }
    }

    public void stateCustomer(){
        stateClient.child(uidClient).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    Map<String, String> mapS = (Map<String, String>) dataSnapshot.getValue();
                    estado = mapS.get("state");
                    showDriver();
                    getDataRequested();
                    valorizar();
                }
            }
            @Override
            public void onCancelled(DatabaseError firebaseError) {}
        });
    }

    public void onWaystatus(){
        if(uidDriver2!=null) {
            rDriverStatus.child(uidDriver2).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Map<Double, Double> map = (Map<Double, Double>) dataSnapshot.getValue();
                        lat = map.get("latitude");
                        lng = map.get("longitude");
                        if (lat != null && lng != null) {
                            LatLng driverU = new LatLng(lat,lng);
                            driverMarkers = new ArrayList<>();
                            mMap.clear();
                            driverMarkers.add(mMap.addMarker(new MarkerOptions()
                                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.kan))
                                    .position(driverU)));
                            switch (estado) {
                                case "onWay":
                                    mMap.moveCamera(CameraUpdateFactory.newLatLng(driverU));
                                    mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
                                    break;
                                case "onTrip":
                                    mMap.moveCamera(CameraUpdateFactory.newLatLng(driverU));
                                    mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
                                    //validationReturn();
                                    fab.setVisibility(View.GONE);
                                    break;
                            }
                        }
                    }
                }
                @Override
                public void onCancelled(DatabaseError firebaseError) {}
            });
        }
    }

    public void showDriver() {
        rDriverStatus.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()&&!estado.equals("endTrip")) {
                    Set<String> hs = new HashSet<>();
                    for (DataSnapshot infoSnapshot : dataSnapshot.getChildren()) {
                        String client2 = (String) infoSnapshot.child("customerUid").getValue();
                        arrayClient2.add(client2);
                        hs.addAll(arrayClient2);
                        arrayClient2.clear();
                        arrayClient2.addAll(hs);
                    }
                    if (arrayClient2.contains(uidClient)) {
                        arrayClient.clear();
                        if(timer2!=null) {
                            timer2.cancel();
                            timer3.cancel();
                        }
                        onWaystatus();
                        travel.setVisibility(View.VISIBLE);
                        ((MainActivity)getActivity()).hideDrawer();
                        Show();
                        etOrigin.setVisibility(View.GONE);
                        etDestination.setVisibility(View.GONE);
                        btnFindPath2.setVisibility(View.GONE);
                        btnFindPath3.setVisibility(View.GONE);
                        btnFindPath.setVisibility(View.GONE);
                        solicitado.setVisibility(View.GONE);
                        hideBtnCancel();
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError firebaseError) {}
        });
    }

    public void hideBtnCancel(){
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {

            }
        },121000);
    }

    public void canceled(){
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelCustomer.show();
            }
        });
        cancelDriver.setTitle("Viaje Cancelado");
        cancelDriver.setMessage("Driver ha cancelado el viaje.");
        cancelDriver.setCancelable(false);
        cancelDriver.setButton(AlertDialog.BUTTON_NEUTRAL, "Aceptar",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        mMap.clear();
                        if(timer3!=null){
                            timer3.cancel();
                        }
                        etOrigin.setVisibility(View.VISIBLE);
                        etDestination.setVisibility(View.VISIBLE);
                        btnFindPath2.setVisibility(View.VISIBLE);
                        btnFindPath3.setVisibility(View.VISIBLE);
                        btnFindPath.setVisibility(View.VISIBLE);
                        travel.setVisibility(View.GONE);
                        etDestination.setText("");
                        etOrigin.setText("");
                        buildGoogleApiClient();
                        ((MainActivity)getActivity()).showDrawer();
                        arrayDriver.clear();
                        arrayClient.clear();
                        arrayClient2.clear();
                        uidDriver2 = null;
                        cancelDriver.dismiss();
                    }
                });
        cancelCustomer.setTitle("Cancelar viaje");
        cancelCustomer.setMessage("La cancelación del viaje tiene un costo asociado de $1.000");
        cancelCustomer.setCancelable(false);
        cancelCustomer.setButton(AlertDialog.BUTTON_NEUTRAL, "Aceptar",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        mMap.clear();
                        tripState.child(uidClient).child("state").setValue("canceledByCustomer");
                        stateClient.child(uidClient).child("state").setValue("nil");
                        if(timer3!=null) {
                            timer3.cancel();
                        }
                        rDriverStatus.child(uidDriver2).removeValue();
                        arrayClient2.clear();
                        arrayClient.clear();
                        arrayDriver.clear();
                        arrayDriver2.clear();
                        arrayDistancia.clear();
                        mTravels.child(uidClient).removeValue();
                        etOrigin.setVisibility(View.VISIBLE);
                        etDestination.setVisibility(View.VISIBLE);
                        btnFindPath2.setVisibility(View.VISIBLE);
                        btnFindPath3.setVisibility(View.VISIBLE);
                        btnFindPath.setVisibility(View.VISIBLE);
                        travel.setVisibility(View.GONE);
                        etDestination.setText("");
                        etOrigin.setText("");
                        buildGoogleApiClient();
                        ((MainActivity)getActivity()).showDrawer();
                        getCard();
                        uidDriver2 = null;
                        cancelCustomer.dismiss();
                    }
                });
        cancelCustomer.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancelar",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        cancelCustomer.dismiss();
                    }
                });
        tripState.child(uidClient).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    Map<String, String> mapS = (Map<String, String>) dataSnapshot.getValue();
                    estadoTrip = mapS.get("state");
                    if(estadoTrip.equals("canceledByDriver")&& uidDriver2!=null){
                        stateClient.child(uidClient).child("state").setValue("nil");
                        cancelDriver.show();
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError firebaseError) {}
        });
    }

    public void onBackPressed() {
        getActivity().moveTaskToBack(true);
    }

    @Override
    public void onStart() {
        super.onStart();
        getDriver();
        showClearButton();
        checkLocationActive();
    }

    @Override
    public void onStop() {
        super.onStop();
        showClearButton();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void send() {
        origen = etOrigin.getText().toString();
        destino = etDestination.getText().toString();
        latLng = getLocationFromAddress(origen);
        Log.v("Latitudee", String.valueOf(latLng));
        if (origen.isEmpty()) {
            Toast.makeText(getActivity(), "Ingrese dirección de origen!", Toast.LENGTH_SHORT).show();
            return;
        } else if (destino.isEmpty()) {
            Toast.makeText(getActivity(), "Ingrese dirección de destino!", Toast.LENGTH_SHORT).show();
            return;
        } else if (origen.equals(destino)) {
            Toast.makeText(getActivity(), "No se puede generar la ruta, direcciones de origen y destino son iguales", Toast.LENGTH_SHORT).show();
            return;
        }else if (!origen.matches(".*\\d+.*")||!destino.matches(".*\\d+.*")) {
            Toast.makeText(getActivity(), "Debe ingresar número de dirección", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(getActivity(), MapsActivity.class);
        intent.putExtra(MESSAGE_KEY, origen);
        intent.putExtra(MESSAGE_KEYS, destino);
        Intent intent2 = new Intent(getActivity(), PopupFragmentCerti.class);
        intent2.putExtra(MESSAGE_KEY, origen);
        intent2.putExtra(MESSAGE_KEYS, destino);
        Intent intent3 = new Intent(getActivity(), PopupFragment.class);
        intent3.putExtra(MESSAGE_KEY, origen);
        intent3.putExtra(MESSAGE_KEYS, destino);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng sydney = new LatLng(-33.4724227, -70.7699159);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 9));
        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                mMap.setMyLocationEnabled(true);
            }
        } else {
            mMap.setMyLocationEnabled(true);
        }
        View locationButton = ((View) mMapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
        // position on right bottom
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
        rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        rlp.setMargins(0, 0, 30, 30);
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED&&mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {}

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        //move map camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        //stop location updates
        if (mGoogleApiClient != null&&mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
        //autocompletado edittext origen con localización actual
        /*try {
            Geocoder geocoder = new Geocoder(getActivity(), Locale.ENGLISH);
            List<Address> addresses = geocoder.getFromLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude(), 1);
            StringBuilder str = new StringBuilder();
            if (geocoder.isPresent()) {
                Address returnAddress = addresses.get(0);
                Log.v("Direccion0",returnAddress.getAddressLine(0));
                String direccion = returnAddress.getAddressLine(0);
                str.append(direccion);
                if(etDestination.getText().toString().isEmpty()) {
                    etOrigin.setText(str);
                }
            }
        } catch (IOException e) {
            Log.e("tag", e.getMessage());
        }*/
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {}

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission. ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission. ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(getActivity())
                        .setTitle("Permiso")
                        .setMessage("Concedes?")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(getActivity(),
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission. ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(getActivity(),
                            Manifest.permission. ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        //Request location updates:
                        buildGoogleApiClient();
                        mMap.setMyLocationEnabled(true);
                    }
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

        }
    }
    @Override
    public void onResume() {
        super.onResume();
        if (checkLocationPermission()) {
            if (ContextCompat.checkSelfPermission(getActivity(),
                    Manifest.permission. ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {

                //Request location updates:
                if(mMap!=null&&mGoogleApiClient.isConnected()) {
                    buildGoogleApiClient();
                    mMap.setMyLocationEnabled(true);
                }
            }
        }
    }

    public void updateData(){
        travelCustomer.child(uidClient).orderByKey().limitToLast(1)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            Map<String, String> mapS = (Map<String, String>) dataSnapshot.getValue();
                            key = mapS.keySet().toString().replace("[","").replace("]","");
                            getUidDriver();
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError firebaseError) {}
                });
    }

    public void getDataRequested(){
        mTravels.child(uidClient).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    Map<String, String> mapS = (Map<String, String>) dataSnapshot.getValue();
                    if(!estado.equals("nil")) {
                        origen = mapS.get("from");
                        destino = mapS.get("to");
                    }else{
                        mTravels.child(uidClient).removeValue();
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError firebaseError) {}
        });
    }

    public void getUidDriver(){
        travelCustomer.child(uidClient).child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, String> mapS = (Map<String, String>) dataSnapshot.getValue();
                uidDriver2 = mapS.get("driverUid");
                onWaystatus();
            }
            @Override
            public void onCancelled(DatabaseError firebaseError) {}
        });
    }

    public void getToken(){
        drivers.child(uidDriver2).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    Map<String, String> map = (Map<String, String>) dataSnapshot.getValue();
                    token = map.get("deviceToken");
                }
            }
            @Override
            public void onCancelled(DatabaseError firebaseError) {}
        });
    }

    public static MapsFragment newInstance(String text) {
        MapsFragment f = new MapsFragment();
        Bundle b = new Bundle();
        b.putString("msg", text);
        f.setArguments(b);
        return f;
    }

    public void getCard(){
        payment.child(uidClient).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    Map<String, String> mapS = (Map<String, String>) dataSnapshot.getValue();
                    payer = mapS.get("payer");
                    paymentMethod = mapS.get("paymentMethod");
                    tokenPago = mapS.get("token");
                    postGetPayments();
                }
            }
            @Override
            public void onCancelled(DatabaseError firebaseError) {}
        });
    }

    public void postGetPayments(){
        String url = "https://komegaroo-server.herokuapp.com/payments/payment";
        String body ="amount="+1000+"&token="+tokenPago+"&paymentMethod="+paymentMethod+"&payer="+payer;
        ((MainActivity)getActivity()).post(url,body,new okhttp3.Callback(){
            @Override
            public void onFailure(Call call, IOException e) {
                Log.v("POSTNoPayments!", e.getMessage());
                paymentStatus.child(uidClient).child("status").setValue("declined");
                paymentStatus.child(uidClient).child("error").setValue("Error when charging customer, no response from server");
                paymentStatus.child(uidClient).child("debt").setValue(1000);
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseStr = response.body().string();
                    Log.v("POSTYesPayments!", responseStr);
                    try {
                        parseJSon7(responseStr);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    String responseStr = response.body().string();
                    Log.v("POSTNoPayments!", responseStr);
                    paymentStatus.child(uidClient).child("status").setValue("declined");
                    paymentStatus.child(uidClient).child("error").setValue("Error when charging customer, failed payment");
                    paymentStatus.child(uidClient).child("debt").setValue(1000);
                }
            }
        });
    }

    private void parseJSon7(String data) throws JSONException {
        if (data == null)
            return;
        JSONObject jsonData = new JSONObject(data);
        status = jsonData.getString("status");
        statusDetail = jsonData.getString("status_detail");
        Log.v("Status",status);
        Log.v("StatusD",statusDetail);
        if( !status.equals("approved")) {
            switch (statusDetail) {
                case "cc_rejected_callfor_authorize":
                    paymentStatus.child(uidClient).child("status").setValue("declined");
                    paymentStatus.child(uidClient).child("error").setValue("Not authorized");
                    paymentStatus.child(uidClient).child("debt").setValue(1000);
                    break;
                case "cc_rejected_insufficient_amount":
                    paymentStatus.child(uidClient).child("status").setValue("declined");
                    paymentStatus.child(uidClient).child("error").setValue("Insufficient amount");
                    paymentStatus.child(uidClient).child("debt").setValue(1000);
                    break;
                case "cc_rejected_bad_filled_security_code":
                    paymentStatus.child(uidClient).child("status").setValue("declined");
                    paymentStatus.child(uidClient).child("error").setValue("Bad security code");
                    paymentStatus.child(uidClient).child("debt").setValue(1000);
                    break;
                case "cc_rejected_bad_filled_date":
                    paymentStatus.child(uidClient).child("status").setValue("declined");
                    paymentStatus.child(uidClient).child("error").setValue("Expired Date");
                    paymentStatus.child(uidClient).child("debt").setValue(1000);
                    break;
                case "cc_rejected_bad_filled_other":
                    paymentStatus.child(uidClient).child("status").setValue("declined");
                    paymentStatus.child(uidClient).child("error").setValue("From error");
                    paymentStatus.child(uidClient).child("debt").setValue(1000);
                    break;
                case "cc_rejected_other_reason":
                    paymentStatus.child(uidClient).child("status").setValue("declined");
                    paymentStatus.child(uidClient).child("error").setValue("Other");
                    paymentStatus.child(uidClient).child("debt").setValue(1000);
                    break;
            }
        }else{
            paymentStatus.child(uidClient).child("status").setValue("approved");
            paymentStatus.child(uidClient).child("error").setValue("nil");
            paymentStatus.child(uidClient).child("debt").setValue(0);
        }
    }

    public void verificationEmail(){
        alertDialog = new AlertDialog.Builder(getActivity(), R.style.MyAlertDialogStyle).create();
        alertDialog.setTitle("Komegaroo");
        alertDialog.setMessage("Mail no verificado. Te hemos enviado un mail para verificar tu cuenta.");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        alertDialog.dismiss();
                    }
                });
        if(!FirebaseAuth.getInstance().getCurrentUser().isEmailVerified()){
            alertDialog.show();
            ((MainActivity) getActivity()).hideToolbar();
            ready.setVisibility(View.VISIBLE);
        }else{
        }
    }

    public void validationReturn(){
        validation.child(uidClient).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    Map<String, String> mapS = (Map<String, String>) dataSnapshot.getValue();
                    String validate = mapS.get("validateReturn");
                    if (validate.equals("nil")&&returnDriver){
                        callReturnFragment();
                    } else {
                        returnDriver = false;
                        onWaystatus();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void checkLocationActive(){
        LocationManager lm = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch(Exception ex) {}

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch(Exception ex) {}

        if(!gps_enabled && !network_enabled) {
            active.setVisibility(View.VISIBLE);
            ((MainActivity) getActivity()).hideDrawer();
        }
    }

    public void callPopupDriverFragment() {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment fragment = PopupDriverFragment.newInstance("PopupDriver");
        fragmentTransaction.add(R.id.content_main, fragment);
        fragmentTransaction.commit();
    }
    public void callReturnFragment() {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment fragment = ReturnFragment.newInstance("Return");
        fragmentTransaction.add(R.id.content_main, fragment);
        fragmentTransaction.commit();
    }
    public void callValorizarFragment() {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment fragment = ValorizarFragment.newInstance("Valorizar");
        fragmentTransaction.add(R.id.content_main, fragment);
        fragmentTransaction.commit();
    }
}