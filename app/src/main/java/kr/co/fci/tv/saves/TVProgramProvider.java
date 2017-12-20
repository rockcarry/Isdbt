/********************************************************************************************************************/
/**
 *  @skip   $Id: TVProgramProvider.java 526 2015-10-16 05:34:03Z elliot.oh $
 *  @file   TVProgramProvider.java
 *  @brief  providing content to this application.
 *  @date   2015/05/21 FCI elliot create.
 *
 *  ALL Rights Reserved, Copyright(C) FCI 2015
 */
/********************************************************************************************************************/
package kr.co.fci.tv.saves;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.TextUtils;
import android.util.Log;

import java.util.HashMap;

import kr.co.fci.tv.saves.TVProgram.Programs;
import kr.co.fci.tv.util.TVlog;

//import com.android.isdbt.Logger;

public class TVProgramProvider extends ContentProvider {

    private static final String TAG = "TVProgramProvider";

    private static final String DATABASE_NAME = "fci_mobiletv_programs.db";
    private static final int DATABASE_VERSION = 5;
    private static final String PROGRAM_TABLE_NAME = "programs";

    private static HashMap<String, String> sProgramsProjectionMap;

    private static final int PROGRAMS = 2;
    private static final int PROGRAM_ID = 3;

    private static final UriMatcher sUriMatcher;

    /**
     * This class helps open, create, and upgrade the database file.
     */
    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + PROGRAM_TABLE_NAME + " ("
                    + BaseColumns._ID + " INTEGER PRIMARY KEY,"
                    + Programs.SERVICEID + " INTEGER,"
                    + Programs.SERVICENAME + " TEXT,"
                    + Programs.FREQ + " INTEGER,"
                    + Programs.FREE + " INTEGER,"
                    + Programs.TYPE + " INTEGER,"
                    + Programs.FAV + " INTEGER,"
                    + Programs.MTV + " INTEGER,"
                    + Programs.VIDFORM + " INTEGER,"
                    + Programs.AUDFORM + " INTEGER,"
                    + Programs.REMOTEKEY + " INTEGER,"
                    + Programs.SVCNUM + " INTEGER"
                    + ");");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
/*
            TVlog.i(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
*/
        	TVlog.i(TAG,"Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS programs");
            onCreate(db);
        }
    }

    private DatabaseHelper mOpenHelper;

    @Override
    public boolean onCreate() {
        mOpenHelper = new DatabaseHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
            String sortOrder) {
    	Log.d("PST","Query:"+sUriMatcher.match(uri));
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(PROGRAM_TABLE_NAME);

        switch (sUriMatcher.match(uri)) {
        case PROGRAMS:
            qb.setProjectionMap(sProgramsProjectionMap);
            break;

        case PROGRAM_ID:
            qb.setProjectionMap(sProgramsProjectionMap);
            qb.appendWhere(BaseColumns._ID + "=" + uri.getPathSegments().get(1));
            break;

        default:
            throw new IllegalArgumentException("Unknown URI " + uri);
        }
        // If no sort order is specified use the default
        String orderBy;
        if (TextUtils.isEmpty(sortOrder)) {
            orderBy = Programs.SORT_ORDER_BY_ID;
        } else {
            orderBy = sortOrder;
        }
        // Get the database and run the query
        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, orderBy);
        //c.moveToFirst();  // live
        
        // Tell the cursor what uri to watch, so it knows when its source data changes
        c.setNotificationUri(getContext().getContentResolver(), uri);

        return c;
    }

    @Override
    public String getType(Uri uri) {
        switch (sUriMatcher.match(uri)) {
        case PROGRAMS:
            return Programs.CONTENT_TYPE;

        case PROGRAM_ID:
            return Programs.CONTENT_ITEM_TYPE;

        default:
            throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues initialValues) {
        // Validate the requested uri
        if (sUriMatcher.match(uri) != PROGRAMS) {
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        ContentValues values;
        if (initialValues != null) {
            values = new ContentValues(initialValues);
        } else {
            values = new ContentValues();
        }

        if (values.containsKey(Programs.SERVICEID) == false) {
            values.put(Programs.SERVICEID, 0);
        }
        
        if (values.containsKey(Programs.SERVICENAME) == false) {
            Resources r = Resources.getSystem();
            values.put(Programs.SERVICENAME, r.getString(android.R.string.untitled));
        }

        if (values.containsKey(Programs.FREQ) == false) {
            values.put(Programs.FREQ, 0);
        }
        
        if (values.containsKey(Programs.FREE) == false) {
            values.put(Programs.FREE, 0);
        }
        
        if (values.containsKey(Programs.TYPE) == false) {
            values.put(Programs.TYPE, 0);
        }
        
        if (values.containsKey(Programs.FAV) == false) {
            values.put(Programs.FAV, 0);
        }
        
      if (values.containsKey(Programs.MTV) == false) {
            values.put(Programs.MTV, 0);
        }
        
          if (values.containsKey(Programs.VIDFORM) == false) {
            values.put(Programs.VIDFORM, 0);
        }

        if (values.containsKey(Programs.AUDFORM) == false) {
            values.put(Programs.AUDFORM, 0);
        }

        if (values.containsKey(Programs.REMOTEKEY) == false) {
            values.put(Programs.REMOTEKEY, 0);
        }

        if (values.containsKey(Programs.SVCNUM) == false) {
            values.put(Programs.SVCNUM, 0);
        }

        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        long rowId = db.insert(PROGRAM_TABLE_NAME, Programs.FREQ, values);
        if (rowId > 0) {
            Uri programUri = ContentUris.withAppendedId(Programs.CONTENT_URI, rowId);
            getContext().getContentResolver().notifyChange(programUri, null);
            return programUri;
        }

        throw new SQLException("Failed to insert row into " + uri);
    }

    @Override
    public int delete(Uri uri, String where, String[] whereArgs) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int count;
        Log.d("PST","delete uri:"+sUriMatcher.match(uri));
        switch (sUriMatcher.match(uri)) {
        case PROGRAMS:
            count = db.delete(PROGRAM_TABLE_NAME, where, whereArgs);
            break;

        case PROGRAM_ID:
            String noteId = uri.getPathSegments().get(1);
            count = db.delete(PROGRAM_TABLE_NAME, BaseColumns._ID + "=" + noteId
                    + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
            break;

        default:
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String where, String[] whereArgs) {
    	Log.d("PST","update db");
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int count;
        switch (sUriMatcher.match(uri)) {
        case PROGRAMS:
            count = db.update(PROGRAM_TABLE_NAME, values, where, whereArgs);
            break;

        case PROGRAM_ID:
            String serviceId = uri.getPathSegments().get(1);
            count = db.update(PROGRAM_TABLE_NAME, values, BaseColumns._ID + "=" + serviceId
                    + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
            break;

        default:
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(TVProgram.AUTHORITY, "programs", PROGRAMS);
        sUriMatcher.addURI(TVProgram.AUTHORITY, "programs/#", PROGRAM_ID);

        sProgramsProjectionMap = new HashMap<String, String>();
        sProgramsProjectionMap.put(BaseColumns._ID, BaseColumns._ID);
        sProgramsProjectionMap.put(Programs.SERVICEID, Programs.SERVICEID);
        sProgramsProjectionMap.put(Programs.SERVICENAME, Programs.SERVICENAME);
        sProgramsProjectionMap.put(Programs.FREQ, Programs.FREQ);
        sProgramsProjectionMap.put(Programs.FREE, Programs.FREE);
        sProgramsProjectionMap.put(Programs.TYPE, Programs.TYPE);
        sProgramsProjectionMap.put(Programs.FAV, Programs.FAV);
        sProgramsProjectionMap.put(Programs.MTV, Programs.MTV);
        sProgramsProjectionMap.put(Programs.VIDFORM, Programs.VIDFORM);
        sProgramsProjectionMap.put(Programs.AUDFORM, Programs.AUDFORM);
        sProgramsProjectionMap.put(Programs.REMOTEKEY, Programs.REMOTEKEY);
        sProgramsProjectionMap.put(Programs.SVCNUM, Programs.SVCNUM);

        // Support for Live Folders.
        // Add more columns here for more robust Live Folders.
    }
}
