package com.kome.hp.komegarooandroid.FragmentTutorial;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.kome.hp.komegarooandroid.MenuLaterales.TutorialMLActivity;
import com.kome.hp.komegarooandroid.R;
import com.kome.hp.komegarooandroid.TutorialActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class ImgFinalMLFragment extends Fragment {


    public ImgFinalMLFragment() {
        // Required empty public constructor
    }
    private Button exit;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_img_final_ml, container, false);
        exit = (Button)v.findViewById(R.id.btn_tuto_ml);
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
                getActivity().finish();
            }
        });
        return v;
    }

    public static ImgFinalMLFragment newInstance(String text) {

        ImgFinalMLFragment f = new ImgFinalMLFragment();
        Bundle b = new Bundle();
        b.putString("msg9", text);
        f.setArguments(b);
        return f;
    }

    @Override
    public void onStart() {
        super.onStart();
        ((TutorialMLActivity) getActivity()).close.setVisibility(View.GONE);
    }

    @Override
    public void onStop() {
        super.onStop();
        ((TutorialMLActivity) getActivity()).close.setVisibility(View.VISIBLE);
    }

}
