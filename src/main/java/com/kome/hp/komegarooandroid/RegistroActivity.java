package com.kome.hp.komegarooandroid;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ScaleDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;

import java.io.IOException;
import java.util.Arrays;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.R.attr.scaleHeight;
import static android.R.attr.scaleWidth;

public class RegistroActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private EditText etPass, etPass2, nombre,telefonos, apellido;
    private EditText etEmail;
    private Button btningresar, back;
    private View mProgressView;
    private View mLoginFormView, phoneF;
    private DatabaseReference mRef;
    private CallbackManager mCallbackManager;
    private Button googlebtn, fb, plomo;
    private static final int RC_SIGN_IN=1;
    private GoogleApiClient mGoogleApiClient;
    private static final String TAG = "LoginActivity";
    private LoginButton loginButton;
    private TextView logi;
    public String uidClient, uid;
    private Uri photo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_registro);
        mRef = FirebaseDatabase.getInstance().getReference().child("customers");
        Typeface face= Typeface.createFromAsset(getAssets(), "monserrat/Montserrat-Medium.ttf");
        Drawable drawables = getResources().getDrawable(R.mipmap.ic_user);
        drawables.setBounds(0, -10, (int) (drawables.getIntrinsicWidth() * 0.5),
                (int) (drawables.getIntrinsicHeight() * 0.4));
        ScaleDrawable sds = new ScaleDrawable(drawables, 0, scaleWidth, scaleHeight);
        Drawable drawables1 = getResources().getDrawable(R.mipmap.password);
        drawables1.setBounds(0, -10, (int) (drawables1.getIntrinsicWidth() * 0.5),
                (int) (drawables1.getIntrinsicHeight() * 0.4));
        ScaleDrawable sds1 = new ScaleDrawable(drawables1, 0, scaleWidth, scaleHeight);
        Drawable drawables2 = getResources().getDrawable(R.mipmap.ic_arroba);
        drawables2.setBounds(0, -10, (int) (drawables2.getIntrinsicWidth() * 0.5),
                (int) (drawables2.getIntrinsicHeight() * 0.4));
        ScaleDrawable sds2 = new ScaleDrawable(drawables2, 0, scaleWidth, scaleHeight);
        nombre = (EditText)findViewById(R.id.nombreS);
        nombre.setTypeface(face);
        nombre.setCompoundDrawables(sds.getDrawable(), null, null, null);
        apellido = (EditText)findViewById(R.id.apellidoS);
        apellido.setTypeface(face);
        apellido.setCompoundDrawables(sds.getDrawable(), null, null, null);
        etEmail = (EditText)findViewById(R.id.email);
        etEmail.setTypeface(face);
        etEmail.setCompoundDrawables(sds2.getDrawable(), null, null, null);
        etPass = (EditText)findViewById(R.id.password);
        etPass.setTypeface(face);
        etPass.setCompoundDrawables(sds1.getDrawable(), null, null, null);
        etPass2 = (EditText)findViewById(R.id.password2);
        etPass2.setTypeface(face);
        etPass2.setCompoundDrawables(sds1.getDrawable(), null, null, null);
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener(){
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth){
                if(firebaseAuth.getCurrentUser() != null){
                    mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                            photo = FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl();
                            String name = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
                            String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                            if (!dataSnapshot.hasChild(uid)) {
                                if(photo != null){
                                    postImage();
                                }
                                sendEmail();
                                mRef.child(uid).child("calification").setValue(3.01);
                                mRef.child(uid).child("email").setValue(email);
                                mRef.child(uid).child("name").setValue(name);
                                mRef.child(uid).child("trips").setValue(0);
                            }
                        }
                        @Override
                        public void onCancelled(DatabaseError firebaseError) {
                        }
                    });
                    finish();
                }
            }
        };
        btningresar = (Button)findViewById(R.id.ingresar);
        btningresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkFormFields())
                    return;
                hideSoftKeyboard(RegistroActivity.this);
                showProgress(true);
                createUserAccount();

            }
        });
        logi = (TextView)findViewById(R.id.log);
        logi.setTypeface(face);
        logi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegistroActivity.this, LoginnActivity.class);
                startActivity(intent);
                finish();
            }
        });

        googlebtn = (Button)findViewById(R.id.googleBtn2);
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Toast.makeText(RegistroActivity.this, "Error", Toast.LENGTH_LONG).show();
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
        loginButton = (LoginButton) findViewById(R.id.login_face2);
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
                Toast.makeText(getApplicationContext(), "Error al conectarse", Toast.LENGTH_LONG).show();
                showProgress(false);
            }
        });
        plomo = (Button)findViewById(R.id.buttonPlomoR);
        back = (Button)findViewById(R.id.btnBackR);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mLoginFormView = findViewById(R.id.email_login_form);
        mProgressView = findViewById(R.id.login_progresss);
        phoneF = findViewById(R.id.phoneFrag);
        phoneF.setVisibility(View.GONE);
        showButton();
    }

    public void onClick(View v) {
        if (v == fb) {
            showProgress(true);
            loginButton.performClick();
        }
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
                if (!nombre.getEditableText().toString().isEmpty()&&!apellido.getEditableText().toString().isEmpty()&&!etEmail.getEditableText().toString().isEmpty()
                        &&!etPass.getEditableText().toString().isEmpty()&&!etPass2.getEditableText().toString().isEmpty())
                {
                    plomo.setVisibility(View.GONE);
                    btningresar.setVisibility(View.VISIBLE);
                }else{
                    btningresar.setVisibility(View.GONE);
                    plomo.setVisibility(View.VISIBLE);
                }
            }
        };
        nombre.addTextChangedListener(clear);
        apellido.addTextChangedListener(clear);
        etEmail.addTextChangedListener(clear);
        etPass.addTextChangedListener(clear);
        etPass2.addTextChangedListener(clear);
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

    private void createUserAccount() {

        String email = etEmail.getText().toString();
        String password = etPass.getText().toString();

        // TODO: Create the user account
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Uri myUri = Uri.parse("https://lh4.googleusercontent.com/-k0YDYiIWlAQ/AAAAAAAAAAI/AAAAAAAAAAA/AKB_U8sc4yp9o3yiG9tbs78q_6BeqM77YQ/s96-c/photo.jpg");
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(nombre.getText().toString()+" "+apellido.getText().toString()).build();
                            UserProfileChangeRequest profileUpdates2 = new UserProfileChangeRequest.Builder().setPhotoUri(myUri).build();
                            FirebaseAuth.getInstance().getCurrentUser().updateProfile(profileUpdates);
                            FirebaseAuth.getInstance().getCurrentUser().updateProfile(profileUpdates2);
                            Toast.makeText(RegistroActivity.this, "Usuario creado", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(RegistroActivity.this, "Usuario no creado", Toast.LENGTH_SHORT).show();
                            showProgress(false);
                        }
                    }
                });

    }
    private boolean checkFormFields() {

        final AlertDialog alertDialog2 = new AlertDialog.Builder(RegistroActivity.this).create();
        alertDialog2.setTitle("Dominio inválido");
        alertDialog2.setMessage("Dominio de correo electrónico no corresponde.");
        alertDialog2.setCancelable(false);
        alertDialog2.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        alertDialog2.closeOptionsMenu();
                    }
                });
        String email, password, password2, nombress, apellidoss;

        email = etEmail.getText().toString();
        password = etPass.getText().toString();
        password2 = etPass2.getText().toString();
        nombress =nombre.getText().toString();
        apellidoss = apellido.getText().toString();

        if (nombress.isEmpty()){
            nombre.setError("ingrese Nombre");
            return false;
        }if (apellidoss.isEmpty()){
            apellido.setError("ingrese Apellido");
            return false;
        }if (email.isEmpty()) {
            etEmail.setError("ingrese Email");
            return false;
        }
        if (password.isEmpty()){
            etPass.setError("ingrese Password");
            return false;
        }if (password2.isEmpty()){
            etPass2.setError("ingrese Password");
            return false;
        }if(!email.contains("@")){
            etEmail.setError("ingrese Email válido");
            return false;
        }if (password.length() < 6){
            etPass.setError("Password debe ser mayor o igual a 6 carácteres");
            return false;
        }if (password2.length() < 6){
            etPass2.setError("Password debe ser mayor o igual a 6 carácteres");
            return false;
        }if(!password2.equals(password)){
            etPass2.setError("Las password deben ser iguales");
            return false;
        }if(email.contains("@komegaroo.com")){
            alertDialog2.show();
            return false;
        }

        return true;
    }

    private void handleFacebookAccessToken(AccessToken accessToken) {
        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(RegistroActivity.this, "Autenticación fallida.",
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
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(RegistroActivity.this, "Autenticación fallida.",
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
        Intent intent = new Intent(RegistroActivity.this, IndexActivity.class);
        startActivity(intent);
        finish();
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

    OkHttpClient client = new OkHttpClient();
    public Call post(String url, String json, Callback callback) {
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

    public void postImage() {
        String url = "https://server.komegaroo.com/mobile/uploadImage";
        String body = "uri=" + photo.toString() + "&uid=" + uid;
        post(url, body, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.v("POSTImageNo!", e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseStr = response.body().string();
                    Log.v("POSTImageYes!", responseStr);
                } else {
                    String responseStr = response.body().string();
                    Log.v("POSTImageNo!", responseStr);
                }
            }
        });
    }
}
