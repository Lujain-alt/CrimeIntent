package com.example.crimeintent;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.security.auth.callback.Callback;


/**
 * A simple {@link Fragment} subclass.
 */
public class CrimeListFragment extends Fragment {

    private RecyclerView mCrimeRecyclerView;
    private CrimeAdapter mAdapter;
    int position=0;
    TextView mTextDelete;
    private Callbacks mCallbacks;
    private OnDeleteCrimeListener mDeleteCallBack;

    public interface OnDeleteCrimeListener {
        void onCrimeIdSelected(UUID crimeId);
    }

    SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
    SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

    public interface Callbacks{
        void onCrimeSelected(CrimeModel crime);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallbacks=(Callbacks) context;
        mDeleteCallBack=(OnDeleteCrimeListener)context;
    }

    public CrimeListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
        mDeleteCallBack=null;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_crime_list, container, false);
        mCrimeRecyclerView=view.findViewById(R.id.crime_recycler);
        mCrimeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mCrimeRecyclerView.setHasFixedSize(true);
        setCrimeRecyclerViewItemTouchListener();

        mTextDelete= view.findViewById(R.id.no_crime_there);



        if(CrimeLab.get(getActivity()).getCrimes().size() <= 0) {
            mTextDelete.setVisibility(View.VISIBLE);
        } else {
            updateUI();
        }



       // updateUI();
        return view;
    }



    public void updateUI()
    {
        CrimeLab crimeLab = CrimeLab.get(getActivity());
        List<CrimeModel> crimes = crimeLab.getCrimes();

        if(mAdapter==null)
        {
            mAdapter = new CrimeAdapter(crimes);
            mCrimeRecyclerView.setAdapter(mAdapter);
        }
        else
        {
            if(position<0)
            { mAdapter.notifyDataSetChanged();}
            else
            {
                mAdapter.setCrimes(crimes);
                mAdapter.notifyItemChanged(position);
            }

        }

    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    class CrimeAdapter extends RecyclerView.Adapter<CrimeHolder>
    {
        List<CrimeModel>mCrimes;

        public CrimeAdapter(List<CrimeModel> crimes) {
           mCrimes = crimes;
        }

        @NonNull
        @Override
        public CrimeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater=LayoutInflater.from(getActivity());
            return new CrimeHolder(layoutInflater,parent);
        }

        @Override
        public void onBindViewHolder(@NonNull CrimeHolder holder, int position) {

            holder.bind(mCrimes.get(position));
        }

        @Override
        public int getItemCount()
        {
            return mCrimes.size();
        }

        public void setCrimes(List<CrimeModel> crimes) {
            mCrimes = crimes;
        }

    }

    class CrimeHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        TextView mTitleTextView;
        TextView mDateTextView;
        CrimeModel mCrime;


        public CrimeHolder(LayoutInflater inflater, ViewGroup viewGroup) {
            super(inflater.inflate(R.layout.crime_list_item,viewGroup,false));
           mTitleTextView= itemView.findViewById(R.id.c_title);
           mDateTextView= itemView.findViewById(R.id.c_date);

           itemView.setOnClickListener(this);

        }


        public void bind(CrimeModel crime) {
            mCrime = crime;
            mTitleTextView.setText(mCrime.getmTitle());
            mDateTextView.setText(dateFormat.format(mCrime.getmDate()) + ", " +
                    timeFormat.format(mCrime.getmTime()));

        }


        @Override
        public void onClick(View view) {
            Toast.makeText(getActivity(), mCrime.getmTitle(), Toast.LENGTH_SHORT).show();
           // Intent intent = CrimePagerActivity.newIntent(getActivity(), mCrime.getmId());

           position= getAdapterPosition();
          //  startActivity(intent);
            mCallbacks.onCrimeSelected(mCrime);


        }

    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime_list,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.new_crime:
                CrimeModel crime = new CrimeModel();
                CrimeLab.get(getActivity()).addCrime(crime);
               // Intent intent = CrimePagerActivity.newIntent(getActivity(), crime.getmId());
                //startActivity(intent);

                updateUI();
                mCallbacks.onCrimeSelected(crime);
                return true;

             default:
            return super.onOptionsItemSelected(item);
        }
    }

   public void setCrimeRecyclerViewItemTouchListener() {

        ItemTouchHelper.SimpleCallback itemTouchCallback =
                new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT){

                    @Override
                    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                        return false;
                    }

                    @Override
                    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                        int position = viewHolder.getAdapterPosition();
                        CrimeModel crime = mAdapter.mCrimes.get(position);
                        mDeleteCallBack.onCrimeIdSelected(crime.getmId());
                    }
                };
       ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemTouchCallback);
       itemTouchHelper.attachToRecyclerView(mCrimeRecyclerView);

    }

    public void deleteCrime(UUID crimeId) {
        CrimeModel crime = CrimeLab.get(getActivity()).getCrime(crimeId);
        CrimeLab.get(getActivity()).deleteCrime(crime);
    }




}





