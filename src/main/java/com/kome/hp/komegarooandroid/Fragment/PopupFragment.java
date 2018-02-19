package com.kome.hp.komegarooandroid.Fragment;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kome.hp.komegarooandroid.MapsActivity;
import com.kome.hp.komegarooandroid.R;


public class PopupFragment extends Fragment {
    private Button cancel;
    private DatabaseReference mTravels, state;
    private String uidClient, msn1, msn2;
    private EditText block, number, coment;
    private Button solicitar;
    public static final String MESSAGE_KEY="com.kome.hp.komegarooandroid.message_key";
    public static final String MESSAGE_KEYS="com.kome.hp.komegarooandroid.message_keys";
    private AlertDialog alertDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
         View v = inflater.inflate(R.layout.fragment_popup, container, false);
        mTravels = FirebaseDatabase.getInstance().getReference().child("requestedTravels").child("Santiago");
        state = FirebaseDatabase.getInstance().getReference().child("customerState");
        uidClient = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Intent intent = getActivity().getIntent();
        msn1 = intent.getStringExtra(MESSAGE_KEY);
        msn2 =intent.getStringExtra(MESSAGE_KEYS);
        block = (EditText)v.findViewById(R.id.editTextBlock);
        coment = (EditText)v.findViewById(R.id.editTextComent);
        number = (EditText)v.findViewById(R.id.editTextNumber);
        InputFilter[] FilterArray = new InputFilter[1];
        FilterArray[0] = new InputFilter.LengthFilter(9);
        number.setFilters(FilterArray);
        solicitar = (Button)v.findViewById(R.id.buttonSolicitar);
        solicitar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkData();
            }
        });
        cancel = (Button)v.findViewById(R.id.btnCancelar);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeFragment();
            }
        });
        alertDialog = new AlertDialog.Builder(getActivity(), R.style.MyAlertDialogStyle).create();
        alertDialog.setTitle("K O M E G A R O O");
        alertDialog.setMessage("Se necesita número de contacto de 9 dígitos.");
        alertDialog.setCancelable(false);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        alertDialog.dismiss();
                    }
                });
        return v;
    }

    public void Send(){
        state.child(uidClient).child("state").setValue("nil");
        String prices = ((TextView) getActivity().findViewById(R.id.tvPrecio)).getText().toString().replace("CLP $","").replace(".","");
        Integer priceKM = ((MapsActivity) getActivity()).kmPrice;
        Integer priceTIME =((MapsActivity) getActivity()).timePrice;
        Integer valor = Integer.parseInt(prices);
        mTravels.child(uidClient).child("blockInfo").setValue(block.getText().toString());
        mTravels.child(uidClient).child("certificatedNumber").setValue("");
        mTravels.child(uidClient).child("comments").setValue(coment.getText().toString());
        mTravels.child(uidClient).child("contactNumber").setValue("+56"+number.getText().toString());
        mTravels.child(uidClient).child("from").setValue(msn1);
        mTravels.child(uidClient).child("price").setValue(valor);
        mTravels.child(uidClient).child("receptorName").setValue("");
        mTravels.child(uidClient).child("to").setValue(msn2);
        mTravels.child(uidClient).child("kPrice").setValue(priceKM);
        mTravels.child(uidClient).child("tPrice").setValue(priceTIME);
        mTravels.child(uidClient).child("return").setValue(((MapsActivity) getActivity()).checkReturn);
        ((MapsActivity)getActivity()).metodo();
        removeFragment();
    }

    public void checkData(){
        String numbeer = number.getText().toString();

        if (numbeer.isEmpty()||numbeer.length()<9) {
            alertDialog.show();
            return;
        }
        Send();
    }

    public static PopupFragment newInstance(String text) {
        PopupFragment f = new PopupFragment();
        Bundle b = new Bundle();
        b.putString("NoCertificado", text);
        f.setArguments(b);
        return f;
    }

    public void removeFragment(){
        FragmentManager fm = getFragmentManager();
        fm.beginTransaction().remove(PopupFragment.this).commit();
    }
}
