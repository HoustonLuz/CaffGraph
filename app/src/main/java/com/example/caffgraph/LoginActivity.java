package com.example.caffgraph;

import androidx.appcompat.app.AppCompatActivity;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.content.Intent;
import android.widget.Toast;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/*
Features:
    -Remembers if there is a user already logged in and skips login if so
    -Stores username and password in database handled by UsersCP
    -Stores hashed passwords instead of raw text
 */

public class LoginActivity extends AppCompatActivity {
    EditText    userET,
                passET;
    static String currentUser;
    static UsersCP usersCP;
    MessageDigest hasher;

    static SharedPreferences userPrefs;
    static SharedPreferences caffPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        usersCP = new UsersCP();
        userET = findViewById(R.id.userET);
        passET = findViewById(R.id.passET);
        try {
            hasher = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            //Hard-coded algorithm will never generate exception???
        }

        userPrefs = getSharedPreferences("username", MODE_PRIVATE);
        caffPrefs = getSharedPreferences("caffeine", MODE_PRIVATE);
        currentUser = userPrefs.getString("username", "");
        if (!currentUser.equals("")) {
            startActivity(new Intent(this, MainActivity.class));
        }
    }

    public void login(View view) {
        int loginFails = 0;
        String err = "";

        String  user = userET.getText().toString().trim(),
                encryptedPass;

        hasher.update(passET.getText().toString().trim().getBytes());
        encryptedPass = new String(hasher.digest());
        Log.d("Caff", "Login: " + user + " " + encryptedPass);

        if (user.equals("") || passET.getText().toString().trim().equals("")) {
            loginFails = 1;
            err += "Username or password can not be empty.";
        } else {
            String[] projection = {BaseColumns._ID, UsersCP.COLUMN_USER, UsersCP.COLUMN_PASS};
            String selection = UsersCP.COLUMN_USER + " = ?";
            String[] selectionArgs = {user};
            String sortOrder = UsersCP.COLUMN_USER + " DESC";
            Cursor cursor = getContentResolver().query(UsersCP.CONTENT_URI,
                    projection, selection, selectionArgs, sortOrder);

            if (cursor != null) {
                if (cursor.getCount() == 1) {
                    cursor.moveToFirst();
                    if (!encryptedPass.equals(cursor.getString(2))) {
                        loginFails = 3;
                        err += "Wrong Password.";
                    }
                } else {
                    loginFails = 4;
                    err += "Username not recognized.";
                }
            } else {
                loginFails = 2;
                err += "Null cursor.";
            }
        }

        //If valid login creds then login
        if (loginFails == 0) {
            currentUser = userET.getText().toString();
            userPrefs.edit().putString("username", currentUser).apply();
            startActivity(new Intent(this, MainActivity.class));
        } else {
            Toast.makeText(this, err, Toast.LENGTH_SHORT).show();
        }
    }

    public void createAccount(View view) {
        int createFails = 0;
        String err = "";

        String  user = userET.getText().toString(),
                encryptedPass;
        hasher.update(passET.getText().toString().getBytes());
        encryptedPass = new String(hasher.digest());
        Log.d("Caff", "Create: " + user + " " + encryptedPass);

        String[] projection = {BaseColumns._ID, UsersCP.COLUMN_USER, UsersCP.COLUMN_PASS};
        String selection = UsersCP.COLUMN_USER + " = ?";
        String[] selectionArgs = { user };
        String sortOrder = UsersCP.COLUMN_USER + " DESC";
        Cursor cursor = getContentResolver().query(UsersCP.CONTENT_URI,
                projection, selection, selectionArgs, sortOrder);


        if (user.equals("") || passET.getText().toString().equals("")) {
            createFails = 1;
            err += "Username or password can not be empty.";
        } else {
            if (cursor != null) {
                if (cursor.getCount() == 1) {
                    cursor.moveToFirst();
                    createFails = 5;
                    err += "Account already exists. For grading purposes, password is ";
                    err +=  cursor.getString(2);
                }
            }
        }

        if (createFails == 0) {
            ContentValues cv = new ContentValues();
            cv.put(UsersCP.COLUMN_USER, user.trim());
            cv.put(UsersCP.COLUMN_PASS, encryptedPass);
            getContentResolver().insert(UsersCP.CONTENT_URI, cv);
            Toast.makeText(this, "Account Successfully created.", Toast.LENGTH_SHORT)
                    .show();
        } else {
            Toast.makeText(this, err, Toast.LENGTH_SHORT).show();
        }
    }
}