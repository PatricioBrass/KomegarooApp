package com.kome.hp.komegarooandroid.Fragment;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kome.hp.komegarooandroid.MapsActivity;
import com.kome.hp.komegarooandroid.R;
import com.mercadopago.callbacks.Callback;
import com.mercadopago.core.MercadoPago;
import com.mercadopago.model.ApiException;
import com.mercadopago.model.SavedCardToken;
import com.mercadopago.model.Token;
import com.mercadopago.util.ApiUtil;
import com.mercadopago.util.LayoutUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;

public class VerificarFragment extends Fragment {

    protected String status;
    protected String statusDetail;
    protected String email;
    protected String uidClient;
    protected String mMerchantPublicKey;
    protected String mSecurityCodeText;
    protected String mExceptionOnMethod;
    protected String tokenPago;
    protected String verificar;
    protected String estado;
    protected String resVerified;
    protected Integer monto;
    protected AlertDialog alertFallo;
    protected AlertDialog alertPagar;
    protected AlertDialog alertMal;
    protected AlertDialog alertExito;
    protected AlertDialog alertVerifica;
    protected MercadoPago mMercadoPago;
    protected DatabaseReference payment;
    protected DatabaseReference paymentStatus;

    public VerificarFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_verificar, container, false);
        payment = FirebaseDatabase.getInstance().getReference().child("customerPayments");
        paymentStatus = FirebaseDatabase.getInstance().getReference().child("paymentStatus");
        mMerchantPublicKey = "APP_USR-6eb77772-5324-4737-ac8b-3840ebe010c6";
        uidClient = FirebaseAuth.getInstance().getCurrentUser().getUid();
        email = FirebaseAuth.getInstance().getCurrentUser().getUid()+"@komegaroo.com";
        mMercadoPago = new MercadoPago.Builder()
                .setContext(getActivity())
                .setPublicKey(mMerchantPublicKey)
                .build();
        alertMal = new AlertDialog.Builder(getActivity(), R.style.MyAlertDialogStyle).create();
        alertMal.setTitle("Komegaroo");
        alertMal.setMessage("Ups! Algo fallo, vuelva a intentarlo más tarde.");
        alertMal.setCancelable(false);
        alertMal.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        alertMal.dismiss();
                        getActivity().onBackPressed();
                        removeFragment();
                    }
                });
        alertVerifica = new AlertDialog.Builder(getActivity(), R.style.MyAlertDialogStyle).create();
        alertVerifica.setTitle("Komegaroo");
        alertVerifica.setMessage("Ups! Hay problemas al verificar tu tarjeta, intenta agregando otra.");
        alertVerifica.setCancelable(false);
        alertVerifica.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        alertVerifica.dismiss();
                        getActivity().onBackPressed();
                        removeFragment();
                    }
                });
        alertPagar = new AlertDialog.Builder(getActivity(), R.style.MyAlertDialogStyle).create();
        alertPagar.setTitle("KomeGaroo");
        alertPagar.setCancelable(false);
        alertPagar.setMessage("Tienes una deuda con nosotros. Para continuar debes cancelar la deuda.");
        alertPagar.setButton(AlertDialog.BUTTON_NEGATIVE,"Pagar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                postGetPayments();
            }
        });
        alertPagar.setButton(AlertDialog.BUTTON_POSITIVE,"Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alertPagar.dismiss();
                getActivity().onBackPressed();
                removeFragment();
            }
        });
        alertFallo = new AlertDialog.Builder(getActivity(), R.style.MyAlertDialogStyle).create();
        alertFallo.setTitle("Komegaroo");
        alertFallo.setCancelable(false);
        alertFallo.setMessage("No se pudo procesar el pago. Inténtelo más tarde o verifique su medio de pago.");
        alertFallo.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        alertFallo.dismiss();
                        getActivity().onBackPressed();
                        removeFragment();
                    }
                });
        alertExito = new AlertDialog.Builder(getActivity(), R.style.MyAlertDialogStyle).create();
        alertExito.setTitle("Komegaroo");
        alertExito.setCancelable(false);
        alertExito.setMessage("Pago realizado con éxito.");
        alertExito.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        alertExito.dismiss();
                        postGetCVV();
                    }
                });
        postGetCVV();
        return v;
    }

    public static VerificarFragment newInstance(String text) {
        VerificarFragment f = new VerificarFragment();
        Bundle b = new Bundle();
        b.putString("Verificar", text);
        f.setArguments(b);
        return f;
    }

    public void removeFragment(){
        FragmentManager fm = getFragmentManager();
        fm.beginTransaction().remove(VerificarFragment.this).commit();
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

    public void postGetCVV(){
        String url = "https://komegaroo-server.herokuapp.com/cards/cardInfo";
        String body ="email="+email+"&customerID="+((MapsActivity) getActivity())
                .mCard.getCustomerId()+"&digits="+((MapsActivity) getActivity())
                .mCard.getLastFourDigits();
        post(url,body,new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.v("ErrorCreateToken", e.getMessage().toString());
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        alertMal.show();
                    }
                });
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String t = response.body().string();
                    try {
                        parseJSon(t);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Log.v("POSTYes!CVV", t);
                } else {
                    String responseStr = response.body().string();
                    Log.v("POSTNo!CVV", responseStr);
                }
            }
        });
    }

    private void parseJSon(String data) throws JSONException {
        if (data == null )
            return;
        JSONObject jsonData = new JSONObject(data);
        mSecurityCodeText = jsonData.getString("cvv");
        verificar = jsonData.getString("verified");
        createSavedCardToken();
    }

    protected void createSavedCardToken() {

        SavedCardToken savedCardToken = new SavedCardToken(((MapsActivity) getActivity())
                .mCard.getId(), mSecurityCodeText);

        // Validate CVV
        try {
            savedCardToken.validateSecurityCode(getActivity(), ((MapsActivity) getActivity()).mCard);
            //mSecurityCodeText.setError(null);
        } catch (Exception ex) {
            //mSecurityCodeText.setError(ex.getMessage());
            //mSecurityCodeText.requestFocus();
            return;
        }

        // Create token
        LayoutUtil.showProgressLayout(getActivity());
        mMercadoPago.createToken(savedCardToken, getCreateTokenCallback());
    }

    protected Callback<Token> getCreateTokenCallback() {

        return new Callback<Token>() {
            @Override
            public void success(Token token) {
                tokenPago = token.getId();
                setCustomerPayments();
            }

            @Override
            public void failure(ApiException error) {
                Log.v("Error", error.getMessage().toString());
                mExceptionOnMethod = "getCreateTokenCallback";
                ApiUtil.finishWithApiException(getActivity(), error);
            }
        };
    }

    public void setCustomerPayments(){
        Log.v("setCustomerPayments", ((MapsActivity) getActivity()).
                mCard.getFirstSixDigits());
        String blackToken = ((MapsActivity) getActivity()).
                mCard.getFirstSixDigits()+((MapsActivity) getActivity()).
                mCard.getLastFourDigits()+mSecurityCodeText;
        payment.child(uidClient).child("payer")
                .setValue(((MapsActivity) getActivity()).mCard.getCustomerId());
        payment.child(uidClient).child("paymentMethod")
                .setValue(((MapsActivity) getActivity()).mCard.getPaymentMethod().getId());
        payment.child(uidClient).child("token").setValue(tokenPago);
        payment.child(uidClient).child("blackToken").setValue(blackToken);
        payment.child(uidClient).child("lastDigits")
                .setValue(((MapsActivity) getActivity()).mCard.getLastFourDigits());
        getStatusPayment();
    }

    public void getStatusPayment(){
        paymentStatus.child(uidClient).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    Map<String, String> mapS = (Map<String, String>) dataSnapshot.getValue();
                    Map<Long, Long> map = (Map<Long, Long>) dataSnapshot.getValue();
                    estado = mapS.get("status");
                    monto = map.get("debt").intValue();
                    if(estado.equals("approved")&&verificar.equals("false")){
                        postVerifiedPaymentCard();
                    } else if(estado.equals("approved")&&verificar.equals("true")){
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ((MapsActivity) getActivity()).alertDialog2.show();
                                removeFragment();
                            }
                        });
                    } else {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                alertPagar.show();
                            }
                        });
                    }
                }else{
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ((MapsActivity) getActivity()).alertDialog2.show();
                            removeFragment();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError firebaseError) {

            }
        });

    }

    public void postGetPayments(){
        String url = "https://komegaroo-server.herokuapp.com/payments/payment";
        String body ="amount="+monto+"&token="+tokenPago+"&paymentMethod="+
                ((MapsActivity) getActivity()).mCard.getPaymentMethod().getId()+
                "&payer="+((MapsActivity) getActivity()).mCard.getCustomerId();
        post(url,body,new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.v("POSTNoPayments!", e.getMessage());
                paymentStatus.child(uidClient).child("status").setValue("declined");
                paymentStatus.child(uidClient).child("error").setValue("Error when charging customer, no response from server");
                paymentStatus.child(uidClient).child("debt").setValue(1000);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        alertFallo.show();
                    }
                });
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
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            alertFallo.show();
                        }
                    });
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
        if( !status.equals("approved")) {
            switch (statusDetail) {
                case "cc_rejected_callfor_authorize":
                    paymentStatus.child(uidClient).child("status").setValue("declined");
                    paymentStatus.child(uidClient).child("error").setValue("Not authorized");
                    paymentStatus.child(uidClient).child("debt").setValue(1000);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            alertFallo.show();
                        }
                    });
                    break;
                case "cc_rejected_insufficient_amount":
                    paymentStatus.child(uidClient).child("status").setValue("declined");
                    paymentStatus.child(uidClient).child("error").setValue("Insufficient amount");
                    paymentStatus.child(uidClient).child("debt").setValue(1000);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            alertFallo.show();
                        }
                    });
                    break;
                case "cc_rejected_bad_filled_security_code":
                    paymentStatus.child(uidClient).child("status").setValue("declined");
                    paymentStatus.child(uidClient).child("error").setValue("Bad security code");
                    paymentStatus.child(uidClient).child("debt").setValue(1000);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            alertFallo.show();
                        }
                    });
                    break;
                case "cc_rejected_bad_filled_date":
                    paymentStatus.child(uidClient).child("status").setValue("declined");
                    paymentStatus.child(uidClient).child("error").setValue("Expired Date");
                    paymentStatus.child(uidClient).child("debt").setValue(1000);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            alertFallo.show();
                        }
                    });
                    break;
                case "cc_rejected_bad_filled_other":
                    paymentStatus.child(uidClient).child("status").setValue("declined");
                    paymentStatus.child(uidClient).child("error").setValue("From error");
                    paymentStatus.child(uidClient).child("debt").setValue(1000);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            alertFallo.show();
                        }
                    });
                    break;
                case "cc_rejected_other_reason":
                    paymentStatus.child(uidClient).child("status").setValue("declined");
                    paymentStatus.child(uidClient).child("error").setValue("Other");
                    paymentStatus.child(uidClient).child("debt").setValue(1000);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            alertFallo.show();
                        }
                    });
                    break;
                default:
                    paymentStatus.child(uidClient).child("status").setValue("in_process");
                    paymentStatus.child(uidClient).child("error").setValue("Other");
                    paymentStatus.child(uidClient).child("debt").setValue(1000);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            alertFallo.show();
                        }
                    });
                    break;
            }
        }else{
            paymentStatus.child(uidClient).child("status").setValue("approved");
            paymentStatus.child(uidClient).child("error").setValue("nil");
            paymentStatus.child(uidClient).child("debt").setValue(0);
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    alertExito.show();
                    postBlackList();
                }
            });
        }
    }
    private void parseJSon8(String data) throws JSONException {
        if (data == null)
            return;
        JSONObject jsonData = new JSONObject(data);
        Integer status = jsonData.getInt("status");
        if( !status.equals(200)) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    alertVerifica.show();
                    postAddBlackList();
                }
            });
        }else{
            postVerifiedCard();
        }
    }

    public void postVerifiedCard(){
        String url = "https://komegaroo-server.herokuapp.com/cards/verifiedCard";
        String body ="email="+email+"&digits="+
                ((MapsActivity) getActivity()).mCard.getLastFourDigits()+
                "&customerID="+((MapsActivity) getActivity()).mCard.getCustomerId();
        post(url,body,new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.v("verifiedCardNo", e.getMessage().toString());
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    resVerified = response.body().string();
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.v("verifiedCardYes", resVerified);
                            ((MapsActivity) getActivity()).alertDialog2.show();
                            removeFragment();
                        }
                    });
                } else {
                    String responseStr = response.body().string();
                    Log.v("verifiedCardNo", responseStr);
                }
            }
        });
    }

    public void postVerifiedPaymentCard(){
        String url = "https://komegaroo-server.herokuapp.com/payments/verifyPayment";
        String body ="amount="+1000+"&token="+tokenPago+"&paymentMethod="+
                ((MapsActivity) getActivity()).mCard.getPaymentMethod().getId()+
                "&payer="+((MapsActivity) getActivity()).mCard.getCustomerId();
        post(url,body,new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.v("verifiedPaymentCardNo1", e.getMessage().toString());
                alertVerifica.show();
                postAddBlackList();
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String resVerified = response.body().string();
                    Log.v("verifiedPaymentCardYes", resVerified);
                    try {
                        parseJSon8(resVerified);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    String responseStr = response.body().string();
                    Log.v("verifiedPaymentCardNo2", responseStr);
                    alertVerifica.show();
                    postAddBlackList();
                }
            }
        });
    }

    public void postBlackList(){
        String url = "https://komegaroo-server.herokuapp.com/cards/deleteBlackList";
        String body ="number="+((MapsActivity) getActivity()).mCard.getFirstSixDigits()+
                ((MapsActivity) getActivity()).mCard.getLastFourDigits()+mSecurityCodeText;
        post(url,body,new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseStr = response.body().string();
                    Log.v("postDeleteBlackList", responseStr);
                } else {
                    String responseStr = response.body().string();
                    Log.v("postDeleteBlackList", responseStr);
                }
            }
        });
    }

    public void postAddBlackList(){
        String url = "https://komegaroo-server.herokuapp.com/cards/addBlackList";
        String body ="number="+((MapsActivity) getActivity()).mCard.getFirstSixDigits()+
                ((MapsActivity) getActivity()).mCard.getLastFourDigits()+mSecurityCodeText;
        post(url,body,new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseStr = response.body().string();
                    Log.v("postAddBlackList", responseStr);
                } else {
                    String responseStr = response.body().string();
                    Log.v("postAddBlackList", responseStr);
                }
            }
        });
    }
}

