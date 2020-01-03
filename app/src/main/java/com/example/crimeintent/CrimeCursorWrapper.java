package com.example.crimeintent;

import android.database.Cursor;
import android.database.CursorWrapper;

import java.util.Date;
import java.util.UUID;

public class CrimeCursorWrapper extends CursorWrapper {

    public CrimeCursorWrapper(Cursor cursor) {
        super(cursor);
    }




    public CrimeModel getCrime()
    {
        String uuidString=getString(getColumnIndex(CrimeDbSchema.CrimeTable.Cols.UUID));
        String title=getString(getColumnIndex(CrimeDbSchema.CrimeTable.Cols.TITLE));
        long date=getLong(getColumnIndex(CrimeDbSchema.CrimeTable.Cols.DATE));
        int isSolved=getInt(getColumnIndex(CrimeDbSchema.CrimeTable.Cols.SOLVED));
        String suspect = getString(getColumnIndex(CrimeDbSchema.CrimeTable.Cols.SUSPECT));
        String phoneNumber = getString(getColumnIndex(CrimeDbSchema.CrimeTable.Cols.PHONE_NUMBER));

        CrimeModel crime = new CrimeModel(UUID.fromString(uuidString));
        crime.setmTitle(title);
        crime.setmDate(new Date(date));
        crime.setmSolved(isSolved!=0);
        crime.setmSuspect(suspect);
        crime.setmPhoneNumber(phoneNumber);
        return crime;
    }
}
