package com.kome.hp.komegarooandroid.MenuLaterales;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kome.hp.komegarooandroid.MenuLaterales.PagoFiles.ExamplesUtils;
import com.kome.hp.komegarooandroid.R;
import com.mercadopago.MercadoPagoBaseActivity;
import com.mercadopago.callbacks.Callback;
import com.mercadopago.callbacks.OnSelectedCallback;
import com.mercadopago.constants.Sites;
import com.mercadopago.core.MercadoPago;
import com.mercadopago.core.MerchantServer;
import com.mercadopago.customviews.MPEditText;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.model.ApiException;
import com.mercadopago.model.Card;
import com.mercadopago.model.Customer;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.PaymentPreference;
import com.mercadopago.model.Token;
import com.mercadopago.util.ApiUtil;
import com.mercadopago.util.JsonUtil;
import com.mercadopago.util.LayoutUtil;
import com.mercadopago.views.CustomerCardsView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PagoActivity extends AppCompatActivity {



    protected String mMerchantAccessToken;
    protected String mMerchantBaseUrl;
    protected String mMerchantGetCustomerUri;
    protected String mMerchantPublicKey;
    private BigDecimal mAmount;
    protected List<String> mExcludedPaymentTypes;
    protected List<String> mExcludedPaymentMethodIds;

    // Input controls

    // Current values
    protected List<Card> mCards;
    protected Token mToken;
    protected Card mSelectedCard;
    protected PaymentMethod mSelectedPaymentMethod;
    protected PaymentMethod mTempPaymentMethod;
    protected PaymentPreference mPaymentPreference;


    // Local vars
    protected Activity mActivity;
    protected String mExceptionOnMethod;
    protected MercadoPago mMercadoPago;
    protected JSONArray jsonCard;
    protected ProgressBar pro;
    protected JSONArray jsonResult;
    protected String error;
    protected String email;
    protected AlertDialog alertDialog;
    private Timer timer;
    public static final String MY_PREFS_NAME = "MyPrefsFile";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pago);
        email = FirebaseAuth.getInstance().getCurrentUser().getUid()+"@komegaroo.com";
        mAmount = new BigDecimal("100");

        alertDialog = new AlertDialog.Builder(this, R.style.MyAlertDialogStyle).create();
        alertDialog.setTitle("Komegaroo");
        alertDialog.setCancelable(false);
        alertDialog.setMessage("Ups, tenemos problemas con nuestros servicios, inténtelo más tarde.");
        alertDialog.setButton(android.app.AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        onBackPressed();
                        alertDialog.dismiss();
                    }
                });
        postDriver();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
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
    }

    @Override
    protected void onStop(){
        super.onStop();
        timer.cancel();
    }

    public void m(){
        if ((mMerchantPublicKey != null) && (!mMerchantPublicKey.equals(""))) {

            // Set activity
            mActivity = this;
            //mActivity.setTitle(getString(R.string.mpsdk_title_activity_vault));


            // Init controls visibility

            // Init MercadoPago object with public key
            mMercadoPago = new MercadoPago.Builder()
                    .setContext(mActivity)
                    .setPublicKey(mMerchantPublicKey)
                    .build();

            // Set customer method first value

            // Set "Go" button

            // Hide main layout and go for customer's cards
            if ((mMerchantBaseUrl != null) && (!mMerchantBaseUrl.equals("") && (mMerchantGetCustomerUri != null) && (!mMerchantGetCustomerUri.equals("")))) {
                getCustomerCardsAsync();

            }
            createPaymentPreference();
        } else {
            Intent returnIntent = new Intent();
            setResult(RESULT_CANCELED, returnIntent);
            returnIntent.putExtra("message", "Invalid parameters");
            finish();
        }
    }

    private void getActivityParameters() {
        mMerchantPublicKey = "APP_USR-6eb77772-5324-4737-ac8b-3840ebe010c6";
        mMerchantBaseUrl = "https://www.mercadopago.com";
        mMerchantGetCustomerUri = "/checkout/examples/getCustomer";
        mMerchantAccessToken = "mla-cards-data";
        timer.cancel();
        if (this.getIntent().getStringExtra("excludedPaymentTypes") != null) {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<String>>() {
            }.getType();
            mExcludedPaymentTypes = gson.fromJson(this.getIntent().getStringExtra("excludedPaymentTypes"), listType);
        }
        if (this.getIntent().getStringExtra("excludedPaymentMethodIds") != null) {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<String>>() {
            }.getType();
            mExcludedPaymentMethodIds = gson.fromJson(this.getIntent().getStringExtra("excludedPaymentMethodIds"), listType);
        }
        m();
    }



    protected void createPaymentPreference() {
        mPaymentPreference = new PaymentPreference();
        mPaymentPreference.setExcludedPaymentMethodIds(this.mExcludedPaymentMethodIds);
        mPaymentPreference.setExcludedPaymentTypeIds(this.mExcludedPaymentTypes);
    }

    public void onCustomerMethodsClick2() {

        if ((mCards != null) && (mCards.size() > 0) ) {  // customer cards activity
            new MercadoPago.StartActivityBuilder()
                    .setActivity(mActivity)
                    .setCards(mCards)
                    .startCustomerCardsActivity();
            finish();
        } else {  // payment method activity
            new MercadoPago.StartActivityBuilder()
                    .setActivity(this)
                    .setPublicKey(mMerchantPublicKey)
                    .setAmount(mAmount)
                    .setSite(Sites.CHILE)
                    .startCardVaultActivity();
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == MercadoPago.CUSTOMER_CARDS_REQUEST_CODE) {
            resolveCustomerCardsRequest(resultCode, data);
        } else if (requestCode == MercadoPago.PAYMENT_METHODS_REQUEST_CODE) {
            resolvePaymentMethodsRequest(resultCode, data);
        } else if (requestCode == ExamplesUtils.CARD_REQUEST_CODE) {
            resolveCardRequest(resultCode, data);
        }
    }

    protected void resolveCustomerCardsRequest(int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {

            Card card = null;
            if (card != null) {

                // Set selection status
                mToken = null;
                mSelectedCard = card;
                mSelectedPaymentMethod = null;
                mTempPaymentMethod = null;

                // Set customer method selection
                setCustomerMethodSelection();

            } else {

                startPaymentMethodsActivity();
            }
        } else {

            if ((data != null) && (data.getStringExtra("apiException") != null)) {
                finishWithApiException(data);
            }
        }
    }

    protected void resolvePaymentMethodsRequest(int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {

            mTempPaymentMethod = JsonUtil.getInstance().fromJson(data.getStringExtra("paymentMethod"), PaymentMethod.class);

            // Call new cards activity
            startCardActivity();

        } else {

            if ((data != null) && (data.getStringExtra("apiException") != null)) {
                finishWithApiException(data);
            } else if ((mSelectedCard == null) && (mToken == null)) {
                // if nothing is selected
                finish();
            }
        }
    }

    protected void resolveCardRequest(int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {

            // Set selection status
            mToken = JsonUtil.getInstance().fromJson(data.getStringExtra("token"), Token.class);

            mSelectedCard = null;

            mSelectedPaymentMethod = mTempPaymentMethod;

            // Set customer method selection
            //mPaymentMethodImage.setImageResource(MercadoPagoUtil.getPaymentMethodIcon(mActivity, mSelectedPaymentMethod.getId()));

            // Set security cards visibility
            showSecurityCodeCard(mSelectedPaymentMethod);

            // Set button visibility

        } else {

            if (data != null) {
                if (data.getStringExtra("apiException") != null) {
                    finishWithApiException(data);

                } else if (data.getBooleanExtra("backButtonPressed", false)) {
                    startPaymentMethodsActivity();
                }
            }
        }
    }

    protected String getPaymentMethodLabel(String name, String lastFourDigits) {
        return name + " " + getString(com.mercadopago.R.string.mpsdk_last_digits_label) + " " + lastFourDigits;
    }

    protected void getCustomerCardsAsync() {

        LayoutUtil.showProgressLayout(mActivity);
        MerchantServer.getCustomer(this, mMerchantBaseUrl, mMerchantGetCustomerUri, mMerchantAccessToken, new Callback<Customer>() {
            @Override
            public void success(Customer customer) {
                mCards = getSupportedCustomerCards(customer.getCards());
                Log.v("CreditCards ", mCards.toString());
                error = customer.getCards().toString();
                LayoutUtil.showRegularLayout(mActivity);
                onCustomerMethodsClick2();
            }

            @Override
            public void failure(ApiException error) {
                mExceptionOnMethod = "getCustomerCardsAsync";
                ApiUtil.finishWithApiException(mActivity, error);
            }
        });
    }

    protected void showSecurityCodeCard(PaymentMethod paymentMethod) {

        if (paymentMethod != null) {

            if (isSecurityCodeRequired()) {

            }
        }
    }

    protected String getSelectedPMBin() {

        if (mSelectedCard != null) {
            return mSelectedCard.getFirstSixDigits();
        } else {
            return mToken.getFirstSixDigits();
        }
    }

    private boolean isSecurityCodeRequired() {

        if (mSelectedCard != null) {
            return mSelectedCard.isSecurityCodeRequired();
        } else {
            return mSelectedPaymentMethod.isSecurityCodeRequired(getSelectedPMBin());
        }
    }

    private void setFormGoButton(final MPEditText editText) {

        editText.setOnKeyListener(new View.OnKeyListener() {

            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    submitForm(v);
                }
                return false;
            }
        });
    }

    protected void setCustomerMethodSelection() {

        // Set payment method
        mSelectedPaymentMethod = mSelectedCard.getPaymentMethod();

        // Set customer method selection
        //mPaymentMethodImage.setImageResource(MercadoPagoUtil.getPaymentMethodIcon(mActivity, mSelectedPaymentMethod.getId()));

        // Set security cards visibility
        showSecurityCodeCard(mSelectedCard.getPaymentMethod());

        // Set button visibility
    }

    public void submitForm(View view) {

        LayoutUtil.hideKeyboard(mActivity);

        // Create token
        if (mSelectedCard != null) {
            createSavedCardToken();
        } else if (mToken != null) {
            finishWithTokenResult(mToken);
        }
    }


    protected void createSavedCardToken() {



        // Validate CVV

        // Create token
        LayoutUtil.showProgressLayout(mActivity);
    }

    protected Callback<Token> getCreateTokenCallback() {

        return new Callback<Token>() {
            @Override
            public void success(Token token) {
                finishWithTokenResult(token);
            }

            @Override
            public void failure(ApiException error) {

                mExceptionOnMethod = "getCreateTokenCallback";
                ApiUtil.finishWithApiException(mActivity, error);
            }
        };
    }

    private void finishWithTokenResult(Token token) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("token", token.getId());
        returnIntent.putExtra("paymentMethod", JsonUtil.getInstance().toJson(mSelectedPaymentMethod));
        setResult(RESULT_OK, returnIntent);
        finish();
    }

    protected void finishWithApiException(Intent data) {

        setResult(RESULT_CANCELED, data);
        finish();
    }

    protected void startCardActivity() {

        ExamplesUtils.startCardActivity(this, mMerchantPublicKey, mTempPaymentMethod);
    }

    protected void startPaymentMethodsActivity() {

        new MercadoPago.StartActivityBuilder()
                .setActivity(mActivity)
                .setPublicKey(mMerchantPublicKey)
                .setPaymentPreference(mPaymentPreference)
                .startPaymentMethodsActivity();
    }

    private List<Card> getSupportedCustomerCards(List<Card> cards) {
        List<Card> supportedCards = new ArrayList<>();
        if (mPaymentPreference != null&&jsonCard != null) {

            for (int i=0;i<jsonCard.length();i++){
                try {
                    Card card = JsonUtil.getInstance().fromJson(jsonCard.getString(i), Card.class);
                    cards.add(card);
                    if (mPaymentPreference.isPaymentMethodSupported(card.getPaymentMethod()))
                        supportedCards.add(card);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return supportedCards;
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


    public void postDriver(){
        String url = "https://komegaroo-server.herokuapp.com/payments/getCardByEmail";
        String body ="email="+email;
        post(url,body,new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseStr = response.body().string();
                    Log.v("POSTYes!", responseStr);
                    try {
                        parseJSon(responseStr);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    String responseStr = response.body().string();
                    Log.v("POSTNo!", responseStr);
                }
            }
        });
    }

    private void parseJSon(String data) throws JSONException {
        if (data == null )
            return;
        JSONObject jsonData = new JSONObject(data);
        JSONObject jsonResponse = jsonData.getJSONObject("response");
        jsonResult = jsonResponse.getJSONArray("results");
        if(jsonResult.length()<1){
            Log.v("Error", "  No");
            postDriver2();
        }else {
            JSONObject jsonResults = jsonResult.getJSONObject(0);
            jsonCard = jsonResults.getJSONArray("cards");
            final Customer customer = new Customer();
            customer.id = jsonResults.getString("id");
            customer.liveMode = jsonResults.getBoolean("live_mode");
            customer.email = jsonResults.getString("email");
            customer.firstName = jsonResults.getString("first_name");
            customer.lastName = jsonResults.getString("last_name");
            customer.description = jsonResults.getString("description");
            getActivityParameters();
        }
    }

    public void postDriver2(){
        String url = "https://komegaroo-server.herokuapp.com/payments/newCustomer";
        String body ="email="+email;
        post(url,body,new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseStr = response.body().string();
                    Log.v("POSTYes!2", responseStr);
                    getActivityParameters();
                } else {
                    String responseStr = response.body().string();
                    Log.v("POSTNo!2", responseStr);
                }
            }
        });
    }

}
