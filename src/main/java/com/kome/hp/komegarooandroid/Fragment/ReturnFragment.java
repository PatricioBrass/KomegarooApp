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
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kome.hp.komegarooandroid.R;

import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class ReturnFragment extends Fragment {

    protected Button aceptar, cancelar;
    private DatabaseReference validation;
    protected String uidClient;
    private boolean returnDriver=true;

    public ReturnFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_return, container, false);
        uidClient = FirebaseAuth.getInstance().getCurrentUser().getUid();
        validation = FirebaseDatabase.getInstance().getReference().child("customerValidation");
        aceptar = (Button) v.findViewById(R.id.btnReturnA);
        cancelar = (Button) v.findViewById(R.id.btnReturnC);
        aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validation.child(uidClient).child("validateReturn").setValue("yes");
                removeEvent();
                removeFragment();
            }
        });
        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validation.child(uidClient).child("validateReturn").setValue("no");
            }
        });
        changeValidation();
        return v;
    }

    public void changeValidation(){
        validation.child(uidClient).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Map<String, String> mapS = (Map<String, String>) dataSnapshot.getValue();
                    String validate = mapS.get("validateReturn");
                    Log.v("Return", validate);
                    if (validate.equals("no")&&returnDriver) {
                        returnDriver = false;
                        removeFragment();
                        validation.removeEventListener(this);

                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void removeEvent(){
        validation.removeEventListener((ValueEventListener) this);
    }

    public static ReturnFragment newInstance(String text) {
        ReturnFragment f = new ReturnFragment();
        Bundle b = new Bundle();
        b.putString("Return", text);
        f.setArguments(b);
        return f;
    }

    public void removeFragment(){
        FragmentManager fm = getFragmentManager();
        fm.beginTransaction().remove(ReturnFragment.this).commit();
    }

}
