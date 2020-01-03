package com.example.crimeintent;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CrimeLab {
    private static CrimeLab sCrimeLab;

    private Context mContext;
    private SQLiteDatabase mDatabase;

    public static CrimeLab get(Context context)
    {
        if(sCrimeLab==null)
        {
            sCrimeLab=new CrimeLab(context);
        }
        return sCrimeLab;
    }

    private CrimeLab(Context context)
    {
        mContext=context.getApplicationContext();
        mDatabase=new CrimeBaseHelper(mContext).getWritableDatabase();

        /*for (int i = 0; i < 100; i++) {
            CrimeModel crime = new CrimeModel();
            crime.setmTitle("Crime #" + i);
            crime.setmSolved(i % 2 == 0);
            mCrimes.add(crime);
        }*/
    }
    /**************************************************************/

    public File getPhotoFile(CrimeModel crime) {
        File filesDir = mContext.getFilesDir();
        return new File(filesDir, crime.getPhotoFilename());
    }


    /******************************************************************/

    public List<CrimeModel> getCrimes() {
        List<CrimeModel>crimes=new ArrayList<>();
        CrimeCursorWrapper cursor = queryCrime(null, null);
        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                crimes.add(cursor.getCrime());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
        return crimes;

    }
    public CrimeModel getCrime(UUID id) {

        CrimeCursorWrapper cursor = queryCrime(
                CrimeDbSchema.CrimeTable.Cols.UUID + " = ?",
                new String[] { id.toString() }
        );
        try {
            if (cursor.getCount() == 0) {
                return null;
            }
            cursor.moveToFirst();
            return cursor.getCrime();
        } finally {
            cursor.close();
        }
    }

    public void addCrime(CrimeModel c)
    {
        ContentValues contentValues=getContentValues(c);
        mDatabase.insert(CrimeDbSchema.CrimeTable.NAME,null,contentValues);


    }

    public void deleteCrime(CrimeModel crime)
    {
        String uuidString=crime.getmId().toString();

        mDatabase.delete(CrimeDbSchema.CrimeTable.NAME, CrimeDbSchema.CrimeTable.Cols.UUID +"= ?"
                ,new String[]{uuidString});
    }


    public static ContentValues getContentValues(CrimeModel crime)
    {
        ContentValues contentValues=new ContentValues();
        contentValues.put(CrimeDbSchema.CrimeTable.Cols.UUID,crime.getmId().toString());
        contentValues.put(CrimeDbSchema.CrimeTable.Cols.DATE,crime.getmDate().toString());
        contentValues.put(CrimeDbSchema.CrimeTable.Cols.TITLE,crime.getmTitle());
        contentValues.put(CrimeDbSchema.CrimeTable.Cols.SOLVED,crime.ismSolved()? 1:0);
        contentValues.put(CrimeDbSchema.CrimeTable.Cols.SUSPECT, crime.getmSuspect());
        contentValues.put(CrimeDbSchema.CrimeTable.Cols.PHONE_NUMBER, crime. getmPhoneNumber());



        return contentValues;
    }

    public void updateCrime(CrimeModel crime)
    {
        String uuidString=crime.getmId().toString();
        ContentValues contentValues=getContentValues(crime);
        mDatabase.update(CrimeDbSchema.CrimeTable.NAME,contentValues, CrimeDbSchema.CrimeTable.Cols.UUID +"= ?"
                ,new String[]{uuidString});
    }

    public CrimeCursorWrapper queryCrime(String whereClause,String[]whereArgs)
    {
        Cursor cursor=mDatabase.query(CrimeDbSchema.CrimeTable.NAME,null,whereClause,whereArgs,null,null,null);
        return new CrimeCursorWrapper(cursor);
    }

}
