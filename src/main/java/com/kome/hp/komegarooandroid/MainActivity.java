package com.kome.hp.komegarooandroid;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Layout;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kome.hp.komegarooandroid.Fragment.MapsFragment;
import com.kome.hp.komegarooandroid.MenuLaterales.HistorialActivity;
import com.kome.hp.komegarooandroid.MenuLaterales.NosotrosActivity;
import com.kome.hp.komegarooandroid.MenuLaterales.PagoActivity;
import com.kome.hp.komegarooandroid.MenuLaterales.PerfilActivity;
import com.kome.hp.komegarooandroid.MenuLaterales.PromoActivity;
import com.kome.hp.komegarooandroid.MenuLaterales.TutorialMLActivity;
import com.kome.hp.komegarooandroid.Notification.GCMRegistrationIntentService;

import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

public class MainActivity extends AppCompatActivity {

    private DatabaseReference mRef;
    private String uidClient;
    ActionBarDrawerToggle toggle;
    NavigationView leftNavigationView;
    DrawerLayout drawer;
    private AlertDialog alertDialog;
    private AlertDialog alertDialog2;
    private AlertDialog alertDialog4;
    static MainActivity main;
    private EditText input, input3;
    private View ready;
    private Button listo;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        main = this;
        mRef = FirebaseDatabase.getInstance().getReference().child("customers");
        uidClient = FirebaseAuth.getInstance().getCurrentUser().getUid();
        alertDialog = new AlertDialog.Builder(this, R.style.MyAlertDialogStyle).create();
        alertDialog2 = new AlertDialog.Builder(this, R.style.MyAlertDialogStyle).create();
        alertDialog4 = new AlertDialog.Builder(this, R.style.MyAlertDialogStyle).create();
        ready = findViewById(R.id.layout_ready);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        leftNavigationView = (NavigationView) findViewById(R.id.nav_view);
        leftNavigationView.getMenu();
        leftNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                // Handle navigation view item clicks here.
                int id = item.getItemId();

                if (id == R.id.nav_historial) {
                    Intent intent = new Intent(MainActivity.this, HistorialActivity.class);
                    startActivity(intent);
                }
                else if (id == R.id.nav_tutorial) {
                    Intent intent = new Intent(MainActivity.this, TutorialMLActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);

                } else if (id == R.id.nav_pago) {
                    Intent intent = new Intent(MainActivity.this, PagoActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);

                } else if (id == R.id.nav_perfil) {
                    Intent intent = new Intent(MainActivity.this, PerfilActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);

                }

                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });
        checkInternet();
        FragmentManager fm = getFragmentManager();
        fm.beginTransaction().add(R.id.linear_main, new MapsFragment()).commit();

        loadData();
    }
    public static MainActivity getInstance(){
        return   main;
    }

    public boolean isConnected(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netinfo = cm.getActiveNetworkInfo();

        if (netinfo != null && netinfo.isConnectedOrConnecting()) {
            android.net.NetworkInfo wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            android.net.NetworkInfo mobile = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

            if((mobile != null && mobile.isConnectedOrConnecting()) || (wifi != null && wifi.isConnectedOrConnecting())) return true;
            else return false;
        } else return false;
    }

    public void checkInternet(){
        alertDialog.setTitle("No existe una conexión a internet.");
        alertDialog.setMessage("Revise su conexón a internet.");
        alertDialog.setCancelable(false);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        alertDialog.dismiss();
                    }
                });
        if(!isConnected(this)) alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }


    public void loadData(){
        mRef.child(uidClient).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (!snapshot.hasChild("phoneNumber")) {
                    alertDialog2.setTitle("Ingrese número de celular o contacto");
                    alertDialog2.setCancelable(false);
                    InputFilter[] FilterArray = new InputFilter[1];
                    FilterArray[0] = new InputFilter.LengthFilter(8);
                    input = new EditText(MainActivity.this);
                    input.setFilters(FilterArray);
                    input.setInputType(InputType.TYPE_CLASS_NUMBER);//se puede agregar otro con el signo |
                    input.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.mas_569, 0, 0, 0);
                    alertDialog2.setView(input);
                    alertDialog2.setButton(AlertDialog.BUTTON_POSITIVE, "Ingresar",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    mRef.child(uidClient).child("phoneNumber").setValue("+569" + input.getText().toString());
                                    alertDialog2.dismiss();
                                }
                            });
                    alertDialog2.show();
                    alertDialog2.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                    checkAlertDialog2();
                }
            }
            @Override
            public void onCancelled(DatabaseError firebaseError) {

            }
        });
    }

    public void hideToolbar(){
        toolbar.setVisibility(View.GONE);
    }
    public void showToolbar(){
        toolbar.setVisibility(View.VISIBLE);
    }

    public void checkAlertDialog2(){
        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if(s.toString().length()<8){
                    alertDialog2.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                }else if(s.toString().length()>=8){
                    alertDialog2.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                }
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().length()<8){
                    alertDialog2.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                }else if(s.toString().length()>=8){
                    alertDialog2.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString().length()<8){
                    alertDialog2.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                }else if(s.toString().length()>=8){
                    alertDialog2.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                }
            }
        });
    }

    @Override
    protected void onStart(){
        super.onStart();
        checkInternet();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
        Log.d("VIVZq",""+newConfig.orientation);
    }

    public void hideDrawer(){
        toggle.setDrawerIndicatorEnabled(false);
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }
    public void showDrawer(){
        toggle.setDrawerIndicatorEnabled(true);
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
    }

    OkHttpClient client = new OkHttpClient();
    public Call post(String url, String json, okhttp3.Callback callback) {
        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        RequestBody body = RequestBody.create(mediaType, json);
        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(url)
                .post(body)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        // Handle action bar item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_historial) {
            Intent intent = new Intent(MainActivity.this, HistorialActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);

        }else if (id == R.id.nav_tutorial) {
            Intent intent = new Intent(MainActivity.this, TutorialMLActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);

        } else if (id == R.id.nav_pago) {
            Intent intent = new Intent(MainActivity.this, PagoActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);

        } else if (id == R.id.nav_perfil) {
            Intent intent = new Intent(MainActivity.this, PerfilActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);

        } /*else if (id == R.id.nav_notificaciones) {

        } else if (id == R.id.nav_promociones) {
            Intent intent = new Intent(MainActivity.this, PromoActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);

        } else if (id == R.id.nav_nosotros) {
            Intent intent = new Intent(MainActivity.this, NosotrosActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);

        }*/
        return super.onOptionsItemSelected(item);
    }

}