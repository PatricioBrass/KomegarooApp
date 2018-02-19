package com.kome.hp.komegarooandroid;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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
import com.kome.hp.komegarooandroid.FragmentTutorial.ImgFinalFragment;
import com.kome.hp.komegarooandroid.FragmentTutorial.ImgOneFragment;

import me.relex.circleindicator.CircleIndicator;

public class TutorialActivity extends AppCompatActivity {
    public Button saltar;
    public static final String MESSAGE_KEY="com.kome.hp.komegarooandroid.message_key";
    private String uidClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);
        Intent intent = getIntent();
        //uidClient = intent.getStringExtra(MESSAGE_KEY);
        //indexGo();
        ViewPager pager = (ViewPager) findViewById(R.id.viewPager);
        pager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
        CircleIndicator indicator = (CircleIndicator) findViewById(R.id.indicator);
        indicator.setViewPager(pager);
        saltar = (Button)findViewById(R.id.btnSaltar);
        saltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saltar();
            }
        });
    }

    public void indexGo(){
        Log.v("UDI!",String.valueOf(uidClient));
        if(uidClient!=null){
            saltar();
        }
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
                case 9: saltar.setVisibility(View.GONE);
                        return ImgFinalFragment.newInstance("GifSixFragment, Instance 8");
                default: return ImgOneFragment.newInstance("ImgOneFragment, Instance 1");
            }
        }

        @Override
        public int getCount() {
            return 10;
        }
    }
    public void saltar(){
        Intent intent = new Intent(this, IndexActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}
