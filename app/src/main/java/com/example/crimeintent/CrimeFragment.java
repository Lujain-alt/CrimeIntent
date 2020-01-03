package com.example.crimeintent;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ShareCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;


import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static androidx.constraintlayout.widget.Constraints.TAG;
import static androidx.core.content.PermissionChecker.checkSelfPermission;


/**
 * A simple {@link Fragment} subclass.
 */

public class CrimeFragment extends Fragment {
    private static final String ARG_CRIME_ID = "crime_id";
    private static final String DIALOG_DATE = "DialogDate";
    private static final String DIALOG_TIME = "DialogTime";
    private static final String DIALOG_PHOTO = "DialogPhoto";
    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_TIME = 1;
    private static final int REQUEST_CONTACT = 1;
    private static final int REQUEST_PHOTO= 2;
    // for permissions!
    private static final int REQUEST_CODE_READ_CONTACTS = 1;




    private EditText mTitleField;
    private Button mButton ,mTimeButton ;
    private CheckBox mSolvedCheckBox;
    CrimeModel mCrime=new CrimeModel();
    private Button mReportButton;
    private Button mSuspectButton;
    private ImageButton mPhotoButton;
    private ImageView mPhotoView;
    private File mPhotoFile;
    private int imageWidth, imageHeight;
    private Callbacks mCallbacks;
    Button  mCallSuspectButton;



    public interface Callbacks {
        void onCrimeUpdated(CrimeModel crime);
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_crime, container, false);


        mTitleField=v.findViewById(R.id.crime_title);
        mTitleField.setText(mCrime.getmTitle());

        mSolvedCheckBox=v.findViewById(R.id.crime_solved);
        mSolvedCheckBox.setChecked(mCrime.ismSolved());
        mSolvedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                mCrime.setmSolved(b);
                updateCrime();

            }
        });

        mButton=v.findViewById(R.id.crime_date);
        updateDate();
       mButton.setText(mCrime.getmDate().toString());
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager manager= getFragmentManager();
                DatePickerFragment dialog=DatePickerFragment.newInstance(mCrime.getmDate());
                dialog.setTargetFragment(CrimeFragment.this,REQUEST_DATE);
                dialog.show(manager, DIALOG_DATE);
            }
        });
        mTimeButton=v.findViewById(R.id.crime_time);
        updateTime();
        mTimeButton.setText(mCrime.getmTime().toString());
        mTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager manager= getFragmentManager();
                TimePickerFragment dialog=TimePickerFragment
                        .newInstance(mCrime.getmTime());
                dialog.setTargetFragment(CrimeFragment.this,REQUEST_TIME);
                dialog.show(manager, DIALOG_TIME);

            }
        });


        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mCrime.setmTitle(charSequence.toString());
                updateCrime();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mReportButton=v.findViewById(R.id.crime_report);
        mReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_TEXT, getCrimeReport());
                i.putExtra(Intent.EXTRA_SUBJECT,
                        getString(R.string.crime_report_subject));
                i = Intent.createChooser(i, getString(R.string.send_report));
                startActivity(i);*/

                Intent i=ShareCompat.IntentBuilder.from(CrimeFragment.this.getActivity())
                        .setType("text/plain").setSubject(CrimeFragment.this.getActivity().getString(R.string.crime_report_subject))
                        .setText(getCrimeReport()).setChooserTitle(CrimeFragment.this.getActivity().getString(R.string.send_report))
                        .createChooserIntent();
                startActivity(i);


            }
        });

        final Intent pickIntent=new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        mSuspectButton=v.findViewById(R.id.crime_suspect);
       mSuspectButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               startActivityForResult(pickIntent,REQUEST_CONTACT);
           }
       });
        if (mCrime.getmSuspect() != null) {
            mSuspectButton.setText(mCrime.getmSuspect());
        }


        mCallSuspectButton=v.findViewById(R.id.call_suspect);
        if (mCrime.getmPhoneNumber() != null) {
            mCallSuspectButton.setEnabled(true);
        }
        mCallSuspectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               if(mCrime.getmPhoneNumber()!=null)
               {
                   //Intent intent=new Intent(Intent.ACTION_CALL,Uri.parse("tel:"+mCrime.getmPhoneNumber()));
                   Log.d(TAG, "Printing phone number " + mCrime.getmPhoneNumber());
                   Log.d(TAG, "Suspect: " + mCrime.getmSuspect());
                   Log.d(TAG, "Title: " + mCrime.getmTitle());
                   Uri number = Uri.parse("tel:".concat(mCrime.getmPhoneNumber()));
                   final Intent callContact = new Intent(Intent.ACTION_DIAL, number);
                   startActivity(callContact);

               }

            }
        });



        PackageManager packageManager = getActivity().getPackageManager();
        if (packageManager.resolveActivity(pickIntent,
                PackageManager.MATCH_DEFAULT_ONLY) == null) {
            mSuspectButton.setEnabled(false);
            mCallSuspectButton.setEnabled(false);
        }

        mPhotoButton = v.findViewById(R.id.crime_camera);
        final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        boolean canTakePhoto = mPhotoFile != null &&
                captureImage.resolveActivity(packageManager) != null;
        mPhotoButton.setEnabled(canTakePhoto);
        mPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = FileProvider.getUriForFile(getActivity(),
                        "com.example.crimeintent.fileprovider",
                        mPhotoFile);
                captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                List<ResolveInfo> cameraActivities = getActivity()
                        .getPackageManager().queryIntentActivities(captureImage,
                                PackageManager.MATCH_DEFAULT_ONLY);
                for (ResolveInfo activity : cameraActivities) {
                    getActivity().grantUriPermission(activity.activityInfo.packageName,
                            uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                }
                startActivityForResult(captureImage, REQUEST_PHOTO);
            }
        });

        mPhotoView =v.findViewById(R.id.crime_photo);
        /*******************************************************************************/
        ViewTreeObserver observer = mPhotoView.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            // Interface definition for a callback to be invoked when the global layout state
            // or the visibility of views within the view tree changes.
            // Therefore it's a good idea to de-register the observer after the first "pass" happens.
            // It would be interesting, though, to know why we see the following logged lines TWICE.
            @Override
            public void onGlobalLayout() {
                imageWidth = mPhotoView.getMeasuredWidth();
                imageHeight = mPhotoView.getMeasuredHeight();
                Log.d(TAG, "imageWidth: " + imageWidth);
                Log.d(TAG, "imageHeight: " + imageHeight);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    mPhotoView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }

                updatePhotoView(imageWidth, imageHeight);
            }
        });


        /*******************************************************************************/
        mPhotoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager manager = getFragmentManager();
                PhotoViewerFragment dialog = PhotoViewerFragment.newInstance(mPhotoFile);
                dialog.show(manager, DIALOG_PHOTO);
            }
        });
        updatePhotoView(imageWidth, imageHeight);


        return v;
    }

    private void getContactPhoneNumberWrapper(){
        Log.d("TAG", "build version: " + Integer.toString(Build.VERSION.SDK_INT));
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            Log.d("TAG", "in IF");
            // See: https://developer.android.com/training/permissions/requesting.html
            int hasReadContactsPermission = checkSelfPermission(getActivity(), Manifest.permission.READ_CONTACTS);
            if(hasReadContactsPermission != PackageManager.PERMISSION_GRANTED) {
                // See: http://stackoverflow.com/a/33080682
                requestPermissions(
                        new String[]{Manifest.permission.READ_CONTACTS},
                        REQUEST_CODE_READ_CONTACTS
                );
            } else {
                Log.d("TAG", "in else of the if");
                mCrime.setmPhoneNumber(getContactPhoneNumber());
            }
        } else {
            mCrime.setmPhoneNumber(getContactPhoneNumber());
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d("TAG", "In onRequestPermissionsResult");
        switch (requestCode) {
            case REQUEST_CODE_READ_CONTACTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mCrime.setmPhoneNumber(getContactPhoneNumber());

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private String getContactPhoneNumber(){
        String[] fields = new String[] {
                ContactsContract.CommonDataKinds.Phone.NUMBER
        };

        Cursor cursor = getActivity().getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI, fields,
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                new String[]{mCrime.getmSuspectId()}, null
        );

        String phoneNumber = "";
        try {
            if(cursor.getCount() == 0) {
                return phoneNumber;
            }
            cursor.moveToFirst();
            phoneNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            Log.d("TAG", "Phone number: " + phoneNumber);
        } finally {
            cursor.close();
        }
        return phoneNumber;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode!= Activity.RESULT_OK)
        {
            return;
        }
        if(requestCode==REQUEST_DATE)
        {
            Date date=(Date)data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mCrime.setmDate(date);
            updateCrime();
            updateDate();
        }

        if(requestCode==REQUEST_CONTACT&&data!=null)
        {
           Uri contactUri=data.getData();
            String[] queryFields = new String[]
            {
                    ContactsContract.Contacts.DISPLAY_NAME
            };
            Cursor c = getActivity().getContentResolver()
                    .query(contactUri, queryFields, null, null, null);

            try{
                if(c.getCount()==0)
                {
                    return;
                }
                c.moveToFirst();
                String suspect=c.getString(0);
                mCrime.setmSuspect(suspect);
                updateCrime();
                mSuspectButton.setText(suspect);

            }

            finally {
                c.close();
            }


        }

        /*if(requestCode==REQUEST_TIME)
        {
            Date time=(Date)data.getSerializableExtra(TimePickerFragment.EXTRA_TIME);
            mCrime.setmTime(time);
            updateTime();
        }*/
        else if(requestCode == REQUEST_PHOTO)
        {
            Uri uri = FileProvider.getUriForFile(getActivity(),
                    "com.example.crimeintent.fileprovider",
                    mPhotoFile);
            getActivity().revokeUriPermission(uri,
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            updateCrime();
            updatePhotoView(imageWidth, imageHeight);
        }
    }

    private void updateCrime() {
        CrimeLab.get(getActivity()).updateCrime(mCrime);
        mCallbacks.onCrimeUpdated(mCrime);
    }


    private void updateDate()
    {
        mButton.setText(mCrime.getmDate().toString());
    }

    private void updateTime()
    {
        mTimeButton.setText(mCrime.getmTime().toString());
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallbacks=(Callbacks)context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //UUID crimeId= (UUID) getActivity().getIntent().getSerializableExtra(CrimeActivity.EXTRA_CRIME_ID);


        UUID crimeId=(UUID) getArguments().getSerializable(ARG_CRIME_ID);
     //   Toast.makeText(getActivity(),crimeId+"",Toast.LENGTH_SHORT).show();
      mCrime=  CrimeLab.get(getActivity()).getCrime(crimeId);

        mPhotoFile = CrimeLab.get(getActivity()).getPhotoFile(mCrime);

        setHasOptionsMenu(true);

    }

    public static CrimeFragment newInstance(UUID crimeId)
    {
        Bundle args=new Bundle();
        args.putSerializable(ARG_CRIME_ID,crimeId);
        CrimeFragment fragment=new CrimeFragment();
        fragment.setArguments(args);
        return fragment;

    }


    @Override
    public void onPause() {
        super.onPause();
        CrimeLab.get(getActivity()).updateCrime(mCrime);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks=null;
    }

    private String getCrimeReport()
    {
        String solvedString = null;
        if (mCrime.ismSolved()) {
            solvedString = getString(R.string.crime_report_solved);
        } else {
            solvedString = getString(R.string.crime_report_unsolved);
        }
        String dateFormat = "EEE, MMM dd";
        String dateString = DateFormat.format(dateFormat,mCrime.getmDate()).toString();
        String suspect = mCrime.getmSuspect();
        if (suspect == null) {
            suspect = getString(R.string.crime_report_no_suspect);
        } else {
            suspect = getString(R.string.crime_report_suspect, suspect);
        }
        String report = getString(R.string.crime_report,
                mCrime.getmTitle(), dateString, solvedString, suspect);
        return report;
    }

    private void updatePhotoView(int imageWidth, int imageHeight) {
        if (mPhotoFile == null || !mPhotoFile.exists()) {
            mPhotoView.setImageDrawable(null);
        } else {
            Bitmap bitmap = PictureUtils.getScaledBitmap(
                    mPhotoFile.getPath(), getActivity());
            mPhotoView.setImageBitmap(bitmap);
        }
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.delete_crime, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete_crime:

                // Delete crimeID from CrimeLab

                CrimeLab.get(getActivity()).deleteCrime(mCrime);


                // Back to CrimeListFragment by Intent.

                Intent intent = new Intent(getActivity(), CrimeListActivity.class);
                startActivity(intent);
                return true;
                default:
                return super.onOptionsItemSelected(item);
        }
    }
}
