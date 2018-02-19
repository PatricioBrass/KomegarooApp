package com.kome.hp.komegarooandroid.Fragment;


import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kome.hp.komegarooandroid.IndexActivity;
import com.kome.hp.komegarooandroid.R;
import com.kome.hp.komegarooandroid.RegistroActivity;


public class PhoneFragment extends Fragment {
    private DatabaseReference mRef;
    private String uidClient;
    private EditText phoneF;
    private Button btnPhone;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_phone, container, false);
        mRef = FirebaseDatabase.getInstance().getReference().child("customers");
        phoneF = (EditText)v.findViewById(R.id.phone);
        InputFilter[] FilterArray = new InputFilter[1];
        FilterArray[0] = new InputFilter.LengthFilter(8);
        phoneF.setFilters(FilterArray);
        btnPhone = (Button)v.findViewById(R.id.buttonPhone);
        btnPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uidClient = ((RegistroActivity)getActivity()).uidClient;
                mRef.child(uidClient).child("phoneNumber").setValue("+569" + phoneF.getText().toString());
                Intent intent = new Intent(getActivity(), IndexActivity.class);
                FirebaseAuth.getInstance().signOut();
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                getActivity().finish();
            }
        });

        return v;
    }

}
