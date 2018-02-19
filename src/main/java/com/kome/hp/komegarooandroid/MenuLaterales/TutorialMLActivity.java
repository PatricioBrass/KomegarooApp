package com.kome.hp.komegarooandroid.MenuLaterales;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.kome.hp.komegarooandroid.FragmentTutorial.EightFragment;
import com.kome.hp.komegarooandroid.FragmentTutorial.GifFiveFragment;
import com.kome.hp.komegarooandroid.FragmentTutorial.GifFourFragment;
import com.kome.hp.komegarooandroid.FragmentTutorial.GifOneFragment;
import com.kome.hp.komegarooandroid.FragmentTutorial.GifSevenFragment;
import com.kome.hp.komegarooandroid.FragmentTutorial.GifSixFragment;
import com.kome.hp.komegarooandroid.FragmentTutorial.GifThreeFragment;
import com.kome.hp.komegarooandroid.FragmentTutorial.GifTwoFragment;
import com.kome.hp.komegarooandroid.FragmentTutorial.ImgFinalMLFragment;
import com.kome.hp.komegarooandroid.FragmentTutorial.ImgOneFragment;
import com.kome.hp.komegarooandroid.R;

import me.relex.circleindicator.CircleIndicator;

public class TutorialMLActivity extends AppCompatActivity {

    public Button close;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial_ml);
        ViewPager pager = (ViewPager) findViewById(R.id.viewPagerML);
        pager.setAdapter(new TutorialMLActivity.MyPagerAdapter(getSupportFragmentManager()));
        CircleIndicator indicator = (CircleIndicator) findViewById(R.id.indicatorML);
        indicator.setViewPager(pager);
        close = (Button)findViewById(R.id.btnCloseTuto);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private class MyPagerAdapter extends FragmentPagerAdapter {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int pos) {
            switch(pos) {
                case 1: return GifOneFragment.newInstance("GifOneFragment, Instance 3");
                case 2: return GifTwoFragment.newInstance("GifTwoFragment, Instance 4");
                case 3: return GifThreeFragment.newInstance("GifThreeFragment, Instance 5");
                case 4: return GifFourFragment.newInstance("GifFourFragment, Instance 6");
                case 5: return GifFiveFragment.newInstance("GifFiveFragment, Instance 7");
                case 6: return GifSixFragment.newInstance("GifSixFragment, Instance 8");
                case 7: return GifSevenFragment.newInstance("GifSixFragment, Instance 8");
                case 8: return EightFragment.newInstance("GifSixFragment, Instance 8");
                case 9: close.setVisibility(View.GONE);
                        return ImgFinalMLFragment.newInstance("ImgFinalMLFragment, Instance 8");
                default: return ImgOneFragment.newInstance("ImgOneFragment, Instance 1");
            }
        }

        @Override
        public int getCount() {
            return 10;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
