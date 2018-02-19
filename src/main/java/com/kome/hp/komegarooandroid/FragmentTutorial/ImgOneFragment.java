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
public class ImgOneFragment extends Fragment {


    public ImgOneFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_img_one, container, false);
    }

    public static ImgOneFragment newInstance(String text) {

        ImgOneFragment f = new ImgOneFragment();
        Bundle b = new Bundle();
        b.putString("msg7", text);
        f.setArguments(b);
        return f;
    }

}
