package com.example.caffgraph;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
//import android.util.Log;

public class UsersCP extends ContentProvider {
    protected static final class MainDatabaseHelper extends SQLiteOpenHelper {
        MainDatabaseHelper(Context context) { super(context, DBNAME, null, 1); }
        @Override
        public void onCreate(SQLiteDatabase db) { db.execSQL(SQL_CREATE_MAIN); }
        @Override
        public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {}
    }

    private MainDatabaseHelper mOpenHelper;

    public final static String DBNAME = "UsersDB";
    public final static String TABLE_USERS = "Users";
    public final static String COLUMN_USER = "Username";
    public final static String COLUMN_PASS = "Password";
    public static final String AUTHORITY = "com.example.caffgraph.usersP";
    public static final Uri CONTENT_URI = Uri.parse(
            "content://" + AUTHORITY + "/" + TABLE_USERS);
    private static final String SQL_CREATE_MAIN = "CREATE TABLE " +
            TABLE_USERS +   // Table's name
            "(" +           // The columns in the table
            " _ID INTEGER PRIMARY KEY, " +
            COLUMN_USER +
            " TEXT," +
            COLUMN_PASS +
            " TEXT)";

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return mOpenHelper.getWritableDatabase().delete(TABLE_USERS, selection, selectionArgs);
    }
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        String  user = values.getAsString(COLUMN_USER).trim(),
                pass = values.getAsString(COLUMN_PASS).trim();

        if (user.equals("") || pass.equals("")) {
            //Log.d("Caff", "Insert Failed.");
            return null;
        }

        long id = mOpenHelper.getWritableDatabase().insert
                (TABLE_USERS, null, values);

        //Log.d("Caff", "Successfully inserted.");
        return Uri.withAppendedPath(CONTENT_URI, "" + id);
    }
    @Override
    public boolean onCreate() {
        mOpenHelper = new MainDatabaseHelper(getContext());
        return true;
    }
    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        return mOpenHelper.getReadableDatabase().query
                (TABLE_USERS, projection, selection, selectionArgs,
                        null, null, sortOrder);
    }
    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        return mOpenHelper.getReadableDatabase().update
                (TABLE_USERS, values, selection, selectionArgs);
    }
    @Override
    public String getType(Uri uri) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
