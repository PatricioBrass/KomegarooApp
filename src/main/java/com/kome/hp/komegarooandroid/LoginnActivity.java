package com.kome.hp.komegarooandroid;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ScaleDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.github.kittinunf.fuel.Fuel;
import com.github.kittinunf.fuel.core.FuelError;
import com.github.kittinunf.fuel.core.Handler;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.kome.hp.komegarooandroid.Notification.GCMRegistrationIntentService;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import kotlin.Pair;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.R.attr.scaleHeight;
import static android.R.attr.scaleWidth;

public class LoginnActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private View mProgressView;
    private View mLoginFormView;
    private DatabaseReference mRef;
    private EditText mPasswordView, mEmailView;
    private CallbackManager mCallbackManager;
    private TextView registro, noR;
    private Button googlebtn, mEmailSignInButton, fb, back, plomo;
    private static final int RC_SIGN_IN=1;
    private GoogleApiClient mGoogleApiClient;
    private static final String TAG = "LoginActivity";
    private LoginButton loginButton;
    private AlertDialog alertDialog, alertDialog1, alertSend;
    private EditText input;
    private String uid;
    private Uri photo;
    private BroadcastReceiver mRegistrationBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_login);
        mRef = FirebaseDatabase.getInstance().getReference().child("customers");
        Typeface face= Typeface.createFromAsset(getAssets(), "monserrat/Montserrat-Medium.ttf");
        mEmailView = (EditText) findViewById(R.id.email);
        mEmailView.setTypeface(face);
        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setTypeface(face);
        Drawable drawables = getResources().getDrawable(R.mipmap.ic_user);
        drawables.setBounds(0, -10, (int) (drawables.getIntrinsicWidth() * 0.5),
                (int) (drawables.getIntrinsicHeight() * 0.4));
        ScaleDrawable sds = new ScaleDrawable(drawables, 0, scaleWidth, scaleHeight);
        Drawable drawables1 = getResources().getDrawable(R.mipmap.password);
        drawables1.setBounds(0, -10, (int) (drawables1.getIntrinsicWidth() * 0.5),
                (int) (drawables1.getIntrinsicHeight() * 0.4));
        ScaleDrawable sds1 = new ScaleDrawable(drawables1, 0, scaleWidth, scaleHeight);
        mEmailView.setCompoundDrawables(sds.getDrawable(), null, null, null);
        mPasswordView.setCompoundDrawables(sds1.getDrawable(), null, null, null);
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener(){
            @Override
            public void onAuthStateChanged(@NonNull final FirebaseAuth firebaseAuth){
                if(firebaseAuth.getCurrentUser() != null) {
                    addDataFirebase();
                    finish();
                }
            }
        };

        mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkFormFields())
                    return;
                hideSoftKeyboard(LoginnActivity.this);
                showProgress(true);
                startSignIn();
            }
        });

        registro = (TextView)findViewById(R.id.registro);
        registro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginnActivity.this, RegistroActivity.class);
                startActivity(intent);
                finish();
            }
        });
        registro.setTypeface(face);
        input = new EditText(this);
        noR = (TextView)findViewById(R.id.textViewNoRecuerdo);
        noR.setTypeface(face);
        noR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                noRememberPass();
            }
        });
        googlebtn = (Button)findViewById(R.id.googleBtn);
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Toast.makeText(LoginnActivity.this, "Error", Toast.LENGTH_LONG).show();
                    }
                }).addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        googlebtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                showProgress(true);
                signIn();
            }
        });
        fb = (Button)findViewById(R.id.fb);
        mCallbackManager = CallbackManager.Factory.create();
        loginButton = (LoginButton) findViewById(R.id.login_face);
        loginButton.setReadPermissions(Arrays.asList("email", "public_profile"));
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Toast.makeText(getApplicationContext(), "Inicio de sesión cancelada", Toast.LENGTH_LONG).show();
                showProgress(false);
            }

            @Override
            public void onError(FacebookException error) {
                Log.v("ErrorFacebook",error.toString());
                Toast.makeText(getApplicationContext(), "Error al conectarse", Toast.LENGTH_LONG).show();
                showProgress(false);
            }
        });
        showButton();
        back = (Button)findViewById(R.id.btnBack);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        plomo = (Button)findViewById(R.id.buttonPlomo);
        mLoginFormView = findViewById(R.id.email_login_form);
        mProgressView = findViewById(R.id.login_progress);
        alertDialog = new AlertDialog.Builder(this, R.style.MyAlertDialogStyle).create();
        alertDialog1 = new AlertDialog.Builder(this, R.style.MyAlertDialogStyle).create();
        alertSend = new AlertDialog.Builder(this, R.style.MyAlertDialogStyle).create();
    }

    public void onClick(View v) {
        if (v == fb) {
            showProgress(true);
            loginButton.performClick();
        }
    }

    public void addDataFirebase(){
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                photo = FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl();
                String name = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
                String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                if (!dataSnapshot.hasChild(uid)) {
                    sendEmail();
                    mRef.child(uid).child("email").setValue(email);
                    mRef.child(uid).child("calification").setValue(3.01);
                    mRef.child(uid).child("name").setValue(name);
                    mRef.child(uid).child("trips").setValue(0);
                    post();
                    postToken();
                }
            }

            @Override
            public void onCancelled(DatabaseError firebaseError) {
            }
        });
    }

    public void sendEmail(){
        FirebaseAuth.getInstance().getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Log.v("Se envío a:", FirebaseAuth.getInstance().getCurrentUser().getEmail());
                }else{
                    Log.v("No se envío", "Error!");
                }
            }
        });
    }

    private void startSignIn() {
        String email = mEmailView.getText().toString();
        String pass = mPasswordView.getText().toString();

        mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (!task.isSuccessful()) {
                    Toast.makeText(LoginnActivity.this, "Usuario no registrado.", Toast.LENGTH_SHORT).show();
                    showProgress(false);
                }
            }
        });
    }

    public void showButton(){
        TextWatcher clear = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!mEmailView.getEditableText().toString().isEmpty()&&!mPasswordView.getEditableText().toString().isEmpty())
                {
                    plomo.setVisibility(View.GONE);
                    mEmailSignInButton.setVisibility(View.VISIBLE);
                }else{
                    mEmailSignInButton.setVisibility(View.GONE);
                    plomo.setVisibility(View.VISIBLE);
                }
            }
        };
        mEmailView.addTextChangedListener(clear);
        mPasswordView.addTextChangedListener(clear);
    }

    private void handleFacebookAccessToken(AccessToken accessToken) {
        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isComplete()){
                            //Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());
                        }
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(LoginnActivity.this, "Autenticación fallida.",
                                    Toast.LENGTH_SHORT).show();
                            showProgress(false);
                        }

                        // ...
                    }
                });
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                showProgress(false);
                // Google Sign In failed, update UI appropriately
                // ...
            }
        }

    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isComplete()) {
                            Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());
                        }
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(LoginnActivity.this, "Autenticación fallida.",
                                    Toast.LENGTH_SHORT).show();
                            showProgress(false);
                        }
                        // ...
                    }
                });
    }

    @Override
    protected void onStart(){
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(LoginnActivity.this, IndexActivity.class);
        startActivity(intent);
        finish();
    }

    private boolean checkFormFields() {
        String email, password;

        email = mEmailView.getText().toString();
        password = mPasswordView.getText().toString();
        alertDialog.setTitle("E-mail inválido");
        alertDialog.setMessage("Ingrese un e-mail válido");
        alertDialog.setCancelable(false);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        alertDialog.dismiss();
                    }
                });
        alertDialog1.setTitle("Contraseña inválida");
        alertDialog1.setMessage("La contraseña de ser mayor o igual a seis carácteres.");
        alertDialog1.setCancelable(false);
        alertDialog1.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        alertDialog1.dismiss();
                    }
                });

        if(!email.contains("@")){
            alertDialog.show();
            return false;
        }if (password.length() < 6){
            alertDialog1.show();
            return false;
        }

        return true;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);
    }

    public void noRememberPass(){
        alertSend.setTitle("Ingrese su correo electrónico.");
        input.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);//se puede agregar otro con el signo |
        input.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_mail, 0, 0, 0);
        alertSend.setView(input);
        alertSend.setButton(AlertDialog.BUTTON_POSITIVE, "Enviar",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        sendPassword();
                        alertSend.dismiss();
                    }
                });
        alertSend.show();
        alertSend.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
        checkAlertDialog();
        e();
    }

    public void checkAlertDialog(){
        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if(!s.toString().contains("@")){
                    alertSend.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                }else{
                    alertSend.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                }
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.toString().contains("@")){
                    alertSend.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                }else{
                    alertSend.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
                if(!s.toString().contains("@")){
                    alertSend.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                }else{
                    alertSend.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                }
            }
        });
    }

    public void e (){
        if(alertSend.isShowing()){
            input.setText("");
        }
    }

    public void sendPassword(){
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String emailAddress = input.getText().toString();

        auth.sendPasswordResetEmail(emailAddress)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(LoginnActivity.this, "El correo ha sido enviado.",
                                    Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(LoginnActivity.this, "Correo no se encuentra registrado.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    OkHttpClient client = new OkHttpClient();
    public Call post(String url, String json, okhttp3.Callback callback) {
        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        RequestBody body = RequestBody.create(mediaType, json);
        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(url)
                .post(body)
                .addHeader("content-type", "application/x-www-form-urlencoded")
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }

   public void post(){
        List<Pair<String, String>> params = new ArrayList<Pair<String, String>>() {{
        add(new Pair<String, String>("uri", photo.toString()));
        add(new Pair<String, String>("uid", uid));
        }};
       Fuel.post("https://server.komegaroo.com/mobile/uploadImage", params).responseString(new Handler<String>() {
           @Override
           public void failure(com.github.kittinunf.fuel.core.Request request, com.github.kittinunf.fuel.core.Response response, FuelError fuelError) {
               Log.v("POSTImageNo", fuelError.getMessage());
           }

           @Override
           public void success(com.github.kittinunf.fuel.core.Request request, com.github.kittinunf.fuel.core.Response response, String s) {
               Log.v("POSTImageYes", response.getResponseMessage());
           }
       });
   }

    public void postImage(){
        String url = "https://server.komegaroo.com/mobile/uploadImage";
        String body ="uri="+photo.toString()+"&uid="+uid;
        Log.v("POSTImage", photo.toString()+" - "+uid);
        post(url,body,new okhttp3.Callback(){
            @Override
            public void onFailure(Call call, IOException e) {
                Log.v("POSTImage21", e.getMessage());
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseStr = response.body().string();
                    Log.v("POSTImage1", responseStr);
                } else {
                    String responseStr = String.valueOf(response.code());
                    Log.v("POSTImage22", responseStr);
                    Log.v("POSTImage22", response.message());
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(GCMRegistrationIntentService.REGISTRATION_SUCCESS));
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(GCMRegistrationIntentService.REGISTRATION_ERROR));
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
    }

    public void postToken(){
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());
        if(ConnectionResult.SUCCESS != resultCode) {
            //Check type of error
            if(GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                Toast.makeText(getApplicationContext(), "Google Play Service is not install/enabled in this device!", Toast.LENGTH_LONG).show();
                //So notification
                GooglePlayServicesUtil.showErrorNotification(resultCode, getApplicationContext());
            } else {
                Toast.makeText(getApplicationContext(), "This device does not support for Google Play Service!", Toast.LENGTH_LONG).show();
            }
        } else {
            //Start service
            Intent itent = new Intent(this, GCMRegistrationIntentService.class);
            startService(itent);
        }
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //Check type of intent filter
                if(intent.getAction().equals(GCMRegistrationIntentService.REGISTRATION_SUCCESS)){
                    //Registration success
                    String token = intent.getStringExtra("token");
                    /*Toast.makeText(getApplicationContext(), "GCM token:" + token, Toast.LENGTH_LONG).show();*/
                } else if(intent.getAction().equals(GCMRegistrationIntentService.REGISTRATION_ERROR)){
                    //Registration error
                    Toast.makeText(getApplicationContext(), "GCM registration error!!!", Toast.LENGTH_LONG).show();
                } else {
                    //Tobe define
                }
            }
        };
    }
}
