package com.example.crimeintent;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telecom.Call;
import android.view.View;
import android.widget.Button;

import java.util.List;
import java.util.UUID;

public class CrimePagerActivity extends AppCompatActivity implements CrimeFragment.Callbacks {
    private ViewPager mViewPager;
    private List<CrimeModel> mCrimes;
    private static final String EXTRA_CRIME_ID =
            "com.example.crimeintent.crime_id";

    Button firstBTN;
    Button lastBTN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crime_pager);
        mViewPager=findViewById(R.id.crime_view_pager);
        firstBTN=findViewById(R.id.pre_btn);
        lastBTN=findViewById(R.id.next_btn);

        mViewPager.setPageMargin(16);
        mCrimes = CrimeLab.get(this).getCrimes();
        FragmentManager fragmentManager = getSupportFragmentManager();


        UUID crimeId = (UUID) getIntent()
                .getSerializableExtra(EXTRA_CRIME_ID);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if(mViewPager.getCurrentItem()==0)
                {
                    firstBTN.setVisibility(View.INVISIBLE);
                }
                else
                {
                    firstBTN.setVisibility(View.VISIBLE);
                }
                if(position == mViewPager.getAdapter().getCount())
                {
                    lastBTN.setVisibility(View.INVISIBLE);
                }
                if(mViewPager.getCurrentItem() == (mCrimes.size() - 1))
                {
                    lastBTN.setVisibility(View.INVISIBLE);
                }
                else
                {
                    lastBTN.setVisibility(View.VISIBLE);
                }


            }

            @Override
            public void onPageSelected(int position) {


            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        firstBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mViewPager.setCurrentItem(0);
            }
        });

        lastBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mViewPager.setCurrentItem(mViewPager.getAdapter().getCount());
            }
        });


        mViewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) {
            @Override
            public Fragment getItem(int position) {
                CrimeModel crime=mCrimes.get(position);
                return CrimeFragment.newInstance(crime.getmId());

            }

            @Override
            public int getCount() {
                return mCrimes.size();
            }
        });


        for (int i = 0; i < mCrimes.size(); i++) {
            if (mCrimes.get(i).getmId().equals(crimeId)) {
                mViewPager.setCurrentItem(i);
                break;
            }
        }


    }
    public static Intent newIntent(Context packageContext, UUID crimeId) {
        Intent intent = new Intent(packageContext, CrimePagerActivity.class);
        intent.putExtra(EXTRA_CRIME_ID, crimeId);
        return intent;
    }

    @Override
    public void onCrimeUpdated(CrimeModel crime) {

    }
}
