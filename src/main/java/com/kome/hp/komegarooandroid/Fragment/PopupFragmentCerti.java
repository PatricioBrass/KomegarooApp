package com.kome.hp.komegarooandroid.Fragment;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.firebase.client.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kome.hp.komegarooandroid.MapsActivity;
import com.kome.hp.komegarooandroid.R;

import java.util.Random;


public class PopupFragmentCerti extends Fragment {
    private Button cancel, solicitud;
    private EditText block, name, number, coment;
    private String uidClient, msn1, msn2;
    public static final String MESSAGE_KEY="com.kome.hp.komegarooandroid.message_key";
    public static final String MESSAGE_KEYS="com.kome.hp.komegarooandroid.message_keys";
    private AlertDialog alertDialog, alertDialog2;
    private View codigos;
    private TextView txtcodigo;
    public Button btnAcep;
    private DatabaseReference mTravels, state;
    private Button compartir;
    private String codigo;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_popup_fragment_certi, container, false);
        mTravels = FirebaseDatabase.getInstance().getReference().child("requestedTravels").child("Santiago");
        state = FirebaseDatabase.getInstance().getReference().child("customerState");
        uidClient = FirebaseAuth.getInstance().getCurrentUser().getUid();
        txtcodigo = (TextView)v.findViewById(R.id.txtCodigo);
        btnAcep = (Button)v.findViewById(R.id.btnAceptar);
        compartir = (Button)v.findViewById(R.id.btnCompartir);
        btnAcep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendTravels();
            }
        });
        compartir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Share();
            }
        });
        Intent intent = getActivity().getIntent();
        msn1 = intent.getStringExtra(MESSAGE_KEY);
        msn2 =intent.getStringExtra(MESSAGE_KEYS);
        codigos = v.findViewById(R.id.codigo);
        block = (EditText)v.findViewById(R.id.etxtblock);
        name = (EditText)v.findViewById(R.id.etxtName);
        number = (EditText)v.findViewById(R.id.etxtNumber);
        InputFilter[] FilterArray = new InputFilter[1];
        FilterArray[0] = new InputFilter.LengthFilter(9);
        number.setFilters(FilterArray);
        coment = (EditText)v.findViewById(R.id.etxtComent);
        alertDialog = new AlertDialog.Builder(getActivity(), R.style.MyAlertDialogStyle).create();
        alertDialog.setTitle("K O M E G A R O O");
        alertDialog.setMessage("Debe rellenar los campos vacíos.");
        alertDialog.setCancelable(false);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        alertDialog.dismiss();
                    }
                });
        alertDialog2 = new AlertDialog.Builder(getActivity(), R.style.MyAlertDialogStyle).create();
        alertDialog2.setTitle("K O M E G A R O O");
        alertDialog2.setMessage("Se necesita número de contacto de 9 dígitos.");
        alertDialog2.setCancelable(false);
        alertDialog2.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        alertDialog2.dismiss();
                    }
                });
        cancel = (Button)v.findViewById(R.id.btnCancelarC);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                block.setText("");
                name.setText("");
                number.setText("");
                coment.setText("");
                removeFragment();
            }
        });
        solicitud = (Button)v.findViewById(R.id.btnSolici);
        solicitud.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkData();

            }
        });
        generateCod();
        return v;
    }

    public void Send(){
        String prices = ((TextView) getActivity().findViewById(R.id.tvPrecio)).getText().toString().replace("CLP $","").replace(".","");
        Integer priceKM = ((MapsActivity) getActivity()).kmPrice;
        Integer priceTIME =((MapsActivity) getActivity()).timePrice;
        Integer valor = Integer.parseInt(prices);
        mTravels.child(uidClient).child("blockInfo").setValue(block.getText().toString());
        mTravels.child(uidClient).child("certificatedNumber").setValue(codigo);
        mTravels.child(uidClient).child("comments").setValue(coment.getText().toString());
        mTravels.child(uidClient).child("contactNumber").setValue("+56"+number.getText().toString());
        mTravels.child(uidClient).child("from").setValue(msn1);
        mTravels.child(uidClient).child("price").setValue(valor);
        mTravels.child(uidClient).child("receptorName").setValue(name.getText().toString());
        mTravels.child(uidClient).child("to").setValue(msn2);
        mTravels.child(uidClient).child("kPrice").setValue(priceKM);
        mTravels.child(uidClient).child("tPrice").setValue(priceTIME);
        mTravels.child(uidClient).child("return").setValue(((MapsActivity) getActivity()).checkReturn);
    }

    public void checkData(){
        String blocke = block.getText().toString();
        String namee = name.getText().toString();
        String numbeer = number.getText().toString();

        if (blocke.isEmpty()) {
            alertDialog.show();
            return;
        } else if (namee.isEmpty()) {
            alertDialog.show();
            return;
        } else if (numbeer.isEmpty()||numbeer.length()<9) {
            alertDialog2.show();
            return;
        }
        codigos.setVisibility(View.VISIBLE);
    }

    public void generateCod(){
        char[] chars = "abcdefghijklmnopqrstuvwxyz0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 4; i++) {
            char c = chars[random.nextInt(chars.length)];
            sb.append(c);
        }
        codigo = sb.toString();
        txtcodigo.setText(codigo);
    }

    public void sendTravels(){
        state.child(uidClient).child("state").setValue("nil");
        mTravels.child(uidClient).child("certificatedNumber").setValue(txtcodigo.getText().toString());
        Send();
        ((MapsActivity)getActivity()).metodo();
        removeFragment();
    }

    public void Share(){
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Código de Seguridad");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, codigo);
        startActivity(Intent.createChooser(sharingIntent, "Share via"));
    }

    public static PopupFragmentCerti newInstance(String text) {
        PopupFragmentCerti f = new PopupFragmentCerti();
        Bundle b = new Bundle();
        b.putString("Certificado", text);
        f.setArguments(b);
        return f;
    }

    public void removeFragment(){
        FragmentManager fm = getFragmentManager();
        fm.beginTransaction().remove(PopupFragmentCerti.this).commit();
    }

}
