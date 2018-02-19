package com.kome.hp.komegarooandroid.FragmentTutorial;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kome.hp.komegarooandroid.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class EightFragment extends Fragment {


    public EightFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_eight, container, false);
    }

    public static EightFragment newInstance(String text) {

        EightFragment f = new EightFragment();
        Bundle b = new Bundle();
        b.putString("msg7", text);
        f.setArguments(b);
        return f;
    }

}
