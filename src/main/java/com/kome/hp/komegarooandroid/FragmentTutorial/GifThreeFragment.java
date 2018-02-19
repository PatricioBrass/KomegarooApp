package com.kome.hp.komegarooandroid.FragmentTutorial;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kome.hp.komegarooandroid.R;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class GifThreeFragment extends Fragment {


    public GifThreeFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_gif_three, container, false);
        return v;
    }

    public static GifThreeFragment newInstance(String text) {

        GifThreeFragment f = new GifThreeFragment();
        Bundle b = new Bundle();
        b.putString("msg3", text);
        f.setArguments(b);
        return f;
    }

}
