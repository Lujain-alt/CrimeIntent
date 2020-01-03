package com.example.crimeintent;

import android.os.Bundle;

import androidx.annotation.LayoutRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public abstract class SingleFragmentActivity extends AppCompatActivity {

    abstract  public Fragment createFragment();
    FragmentManager fm;

    @LayoutRes
    protected int getLayoutResId() {
        return R.layout.fragment_activity;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResId());


        fm=getSupportFragmentManager();

        fm.beginTransaction()
                .replace(R.id.fragment_container,createFragment())
                .commit();
    }

}
