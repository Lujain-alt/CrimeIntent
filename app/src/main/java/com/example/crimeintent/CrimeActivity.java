package com.example.crimeintent;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import java.util.UUID;

public class CrimeActivity extends SingleFragmentActivity{

    FragmentManager fm;
    public static final String EXTRA_CRIME_ID="com.example.crimeintent.crime_id";

    @Override
    public Fragment createFragment()
    {
       UUID crimeId=(UUID) getIntent().getSerializableExtra(EXTRA_CRIME_ID);
       return CrimeFragment.newInstance(crimeId);
    }

    public static Intent newIntent(Context packageContext, UUID crime_id)
    {
        Intent intent=new Intent(packageContext,CrimeActivity.class);
        intent.putExtra(EXTRA_CRIME_ID,crime_id);
        return intent;
    }
}
