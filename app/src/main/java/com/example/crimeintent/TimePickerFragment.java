package com.example.crimeintent;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TimePicker;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class TimePickerFragment extends DialogFragment {

    private static final String ARG_Time = "time";
    public static final String EXTRA_TIME ="com.example.crimeintent.time";
    private TimePicker mTimePicker;


    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        Date time = (Date) getArguments().getSerializable(ARG_Time);
        Calendar calendar=Calendar.getInstance();
        calendar.setTime(time);
        int hour=calendar.get(calendar.HOUR);
        int minuts = calendar.get(Calendar.MINUTE);


        View v = LayoutInflater.from(getActivity())
                .inflate(R.layout.time_dialog, null);
        mTimePicker=v.findViewById(R.id.dialog_time_picker);
        mTimePicker.setCurrentHour(hour);
        mTimePicker.setCurrentMinute(minuts);

        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.time_picker_title)
                .setView(v)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        int hour=mTimePicker.getCurrentHour();
                        int minute=mTimePicker.getCurrentMinute();

                        Calendar calendar = Calendar.getInstance();
                        calendar.set(Calendar.HOUR_OF_DAY, hour);
                        calendar.set(Calendar.MINUTE, minute);
                        Date time = calendar.getTime();
                        sendResult(Activity.RESULT_OK, time);
                    }
                })
                .create();

    }

    public static TimePickerFragment newInstance(Date time)
    {
        Bundle args=new Bundle();
        args.putSerializable(ARG_Time,time);

        TimePickerFragment fragment=new TimePickerFragment();
        fragment.setArguments(args);
        return fragment;
    }



    private void sendResult(int resultCode,Date time)
    {
        if(getTargetFragment()==null)
        {
            return;
        }

        Intent intent=new Intent();
        intent.putExtra(EXTRA_TIME,time);

        getTargetFragment().onActivityResult(getTargetRequestCode(),resultCode,intent);
    }
}
