package com.example.crimeintent;

import android.content.Intent;

import androidx.fragment.app.Fragment;

import java.util.UUID;

public class CrimeListActivity extends SingleFragmentActivity implements CrimeListFragment.Callbacks,CrimeFragment.Callbacks
        ,CrimeListFragment.OnDeleteCrimeListener{

    @Override
    public Fragment createFragment() {
        return new CrimeListFragment();
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_masterdetail;
    }

    @Override
    public void onCrimeSelected(CrimeModel crime) {
        if(findViewById(R.id.detaile_fragment_container)==null)
        {
            Intent intent = CrimePagerActivity.newIntent(this, crime.getmId());
            startActivity(intent);
        }
        else
        {
            Fragment detailFragment=CrimeFragment.newInstance(crime.getmId());
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detaile_fragment_container,detailFragment)
                    .commit();
        }
    }

    @Override
    public void onCrimeUpdated(CrimeModel crime) {
        CrimeListFragment listFragment= (CrimeListFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
         listFragment.updateUI();
    }


    @Override
    public void onCrimeIdSelected(UUID crimeId) {

        CrimeFragment crimeFragment = (CrimeFragment) getSupportFragmentManager().findFragmentById(R.id.detaile_fragment_container);
        CrimeListFragment listFragment = (CrimeListFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        listFragment.deleteCrime(crimeId);
        listFragment.updateUI();
// FIXME:For two- panel detail errors. when I want to delete item in list using the swipe.
//  I took an error that recyclerview.setNextAnim(int) on a null object reference.
//  And I added this code. Now swipe to remove  perfect in the two-pane layout.
        if(crimeFragment==null){

            // here is empty. Because to delete an item from the list without selecting it in the two-pane layout.

        }else {
            // when you remove list item with using swipe, the detail screen is disappear.
            listFragment.getActivity().getSupportFragmentManager().beginTransaction().remove(crimeFragment).commit();
        }

    }
}
