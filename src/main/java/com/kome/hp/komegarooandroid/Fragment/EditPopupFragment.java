package com.kome.hp.komegarooandroid.Fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.app.Fragment;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kome.hp.komegarooandroid.MapsActivity;
import com.kome.hp.komegarooandroid.MenuLaterales.PerfilActivity;
import com.kome.hp.komegarooandroid.R;

import java.util.Map;


public class EditPopupFragment extends Fragment {

    private Button cancel, enter;
    private DatabaseReference mRef;
    private String uidClient;
    private EditText mail, tele;
    private TextView dat, tel, correo;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_edit_popup, container, false);
        mRef = FirebaseDatabase.getInstance().getReference().child("customers");
        uidClient = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mail = (EditText)v.findViewById(R.id.editEmail);
        tele = (EditText)v.findViewById(R.id.editPhone);
        InputFilter[] FilterArray = new InputFilter[1];
        FilterArray[0] = new InputFilter.LengthFilter(8);
        tele.setFilters(FilterArray);
        loadEdtiText();
        cancel = (Button)v.findViewById(R.id.btnCancelEdit);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeFragment();
            }
        });
        enter = (Button)v.findViewById(R.id.btnAceptar);
        enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!check())
                    return;
                loadChange();
                hideSoftKeyboard2(EditPopupFragment.this);
                removeFragment();
            }
        });
        Typeface face2= Typeface.createFromAsset(getActivity().getAssets(), "monserrat/Montserrat-Light.ttf");
        Typeface face3= Typeface.createFromAsset(getActivity().getAssets(), "monserrat/Montserrat-Regular.ttf");
        Typeface face1= Typeface.createFromAsset(getActivity().getAssets(), "monserrat/Montserrat-SemiBold.ttf");
        dat = (TextView)v.findViewById(R.id.textViewDatos);
        dat.setTypeface(face1);
        tel = (TextView)v.findViewById(R.id.textViewTel);
        tel.setTypeface(face3);
        correo = (TextView)v.findViewById(R.id.textViewMail);
        correo.setTypeface(face3);
        mail.setTypeface(face2);
        tele.setTypeface(face2);
        return v;
    }

    public void loadEdtiText(){
        mRef.child(uidClient).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()&&dataSnapshot.hasChild("phoneNumber")&&dataSnapshot.hasChild("email")){
                    Map<String, String> mapS = (Map<String, String>) dataSnapshot.getValue();
                    String email = mapS.get("email");
                    String phones = mapS.get("phoneNumber");
                    String telefono = phones.substring(4,12);
                    mail.setText(email);
                    tele.setText(telefono);
                }
            }

            @Override
            public void onCancelled(DatabaseError firebaseError) {

            }
        });
    }

    public boolean check(){
        final AlertDialog alertDialog2 = new AlertDialog.Builder(getActivity(), R.style.MyAlertDialogStyle).create();
        alertDialog2.setTitle("E-mail inválido");
        alertDialog2.setMessage("Ingrese E-mail válido.");
        alertDialog2.setCancelable(false);
        alertDialog2.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        alertDialog2.dismiss();
                    }
                });
        final AlertDialog alertDialog = new AlertDialog.Builder(getActivity(), R.style.MyAlertDialogStyle).create();
        alertDialog.setTitle("Teléfono inválido");
        alertDialog.setMessage("Ingrese teléfono válido.");
        alertDialog.setCancelable(false);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        alertDialog.dismiss();
                    }
                });
        String email = mail.getText().toString();
        String telefono = tele.getText().toString();
        if (telefono.length() < 8) {
            alertDialog.show();
            return false;
        }if(!email.contains("@")){
            alertDialog2.show();
            return false;
        }
        return true;
    }

    public void loadChange(){
        mRef.child(uidClient).child("email").setValue(mail.getText().toString());
        mRef.child(uidClient).child("phoneNumber").setValue("+569"+tele.getText().toString());
        ((PerfilActivity) getActivity()).perfil();
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);
    }

    public static void hideSoftKeyboard2(Fragment activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getActivity().getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                activity.getActivity().getCurrentFocus().getWindowToken(), 0);
    }

    public static EditPopupFragment newInstance(String text) {
        EditPopupFragment f = new EditPopupFragment();
        Bundle b = new Bundle();
        b.putString("EditPopup", text);
        f.setArguments(b);
        return f;
    }

    public void removeFragment(){
        FragmentManager fm = getFragmentManager();
        fm.beginTransaction().remove(EditPopupFragment.this).commit();
    }


}
