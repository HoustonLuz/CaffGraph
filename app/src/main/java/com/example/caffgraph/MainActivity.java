package com.example.caffgraph;

import androidx.appcompat.app.AppCompatActivity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;

// TODO -Move databases to cloud to be preserved if app is deleted
// TODO -Give app color scheme and redesign logo
// TODO -Implement way to change date without going to settings
// TODO -Implement test case to populate database with year's worth of caffeine intakes

/*
Features:
    -Automatically calculates and adds caffeine in milligrams
        from selection of choices such as coffee and tea
    -Has ability to manually enter custom amount of milligrams instead in case of energy drinks
    -Stores daily caffeine intake in database handled by IntakeCP
    -Remembers amount of caffeine if app is closed
    -Minimalistic Design
 */

public class MainActivity extends AppCompatActivity {
    TextView caffCounterTV;
    EditText coffeeET,
            teaET,
            caffET;
    IntakeCP intakeCP;

    static List<String> intakesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        caffCounterTV = findViewById(R.id.dailyTV);
        coffeeET = findViewById(R.id.coffeeET);
        teaET = findViewById(R.id.teaET);
        caffET = findViewById(R.id.caffET);

        intakesList = new ArrayList<String>();
        intakeCP = new IntakeCP();

        int counter = LoginActivity.caffPrefs.getInt("caffeine", 0);
        if (counter != 0) {
            caffCounterTV.setText(Integer.toString(counter));
        }

    }

    public void ouncesCoffee(View view) {
        //According to google there is 11 mg of caffeine in 1 ounce of coffee
        int currentValue = Integer.parseInt(caffCounterTV.getText().toString());
        if (!coffeeET.getText().toString().equals("")) {
            currentValue += (11 * Integer.parseInt(coffeeET.getText().toString()));
            caffCounterTV.setText(Integer.toString(currentValue));
            LoginActivity.caffPrefs.edit().putInt("caffeine", currentValue).apply();
            coffeeET.setText("");
        }
    }

    public void ouncesTea(View view) {
        //According to google there is 3 mg of caffeine in 1 ounce of tea
        int currentValue = Integer.parseInt(caffCounterTV.getText().toString());
        if (!teaET.getText().toString().equals("")) {
            currentValue += (3 * Integer.parseInt(teaET.getText().toString()));
            caffCounterTV.setText(Integer.toString(currentValue));
            LoginActivity.caffPrefs.edit().putInt("caffeine", currentValue).apply();
            teaET.setText("");
        }
    }

    public void mgCaffeiene(View view) {
        int currentValue = Integer.parseInt(caffCounterTV.getText().toString());
        if (!caffET.getText().toString().equals("")) {
            currentValue += Integer.parseInt(caffET.getText().toString());
            caffCounterTV.setText(Integer.toString(currentValue));
            LoginActivity.caffPrefs.edit().putInt("caffeine", currentValue).apply();
            caffET.setText("");
        }
    }

    public void submitDay(View view) {
        int currentValue = Integer.parseInt(caffCounterTV.getText().toString());
        String currentDate = java.time.LocalDate.now().toString();
        boolean insOrUpd = false;       //Insert if true, update if false

        Log.d("Caff", LoginActivity.currentUser + " "
                + currentDate + " " + currentValue);

        ContentValues cv = new ContentValues();
        cv.put(IntakeCP.COLUMN_USER, LoginActivity.currentUser);
        cv.put(IntakeCP.COLUMN_DATE, currentDate);
        cv.put(IntakeCP.COLUMN_CAFF, currentValue);

        String[] projection = {BaseColumns._ID, IntakeCP.COLUMN_USER,
                IntakeCP.COLUMN_DATE, IntakeCP.COLUMN_CAFF};
        String selection = IntakeCP.COLUMN_DATE + " = ? AND " + IntakeCP.COLUMN_USER + " = ?";
        String[] selectionArgs = {currentDate, LoginActivity.currentUser};
        String sortOrder = IntakeCP.COLUMN_USER + " DESC";
        Cursor cursor = getContentResolver().query(IntakeCP.CONTENT_URI,
                projection, selection, selectionArgs, sortOrder);

        if (cursor != null) {
            if (cursor.getCount() == 1) {
                getContentResolver().update(IntakeCP.CONTENT_URI, cv, selection, selectionArgs);
                Toast.makeText(this, "Intake Successfully updated.", Toast.LENGTH_SHORT)
                        .show();
            } else {
                getContentResolver().insert(IntakeCP.CONTENT_URI, cv);
                Toast.makeText(this, "Intake Successfully created.", Toast.LENGTH_SHORT)
                        .show();
            }
        } else {
            Log.d("Caff", "Query returned null cursor in submitDay");
        }
    }

    public void viewGraphAct(View view) {
        String[] projection = {BaseColumns._ID, IntakeCP.COLUMN_USER,
                IntakeCP.COLUMN_DATE, IntakeCP.COLUMN_CAFF};
        String selection = IntakeCP.COLUMN_USER + " = ?";
        String[] selectionArgs = {LoginActivity.currentUser};
        String sortOrder = IntakeCP.COLUMN_DATE + " DESC";
        Cursor cursor = getContentResolver().query(IntakeCP.CONTENT_URI,
                projection, selection, selectionArgs, sortOrder);

        intakesList.clear();
        if (cursor != null) {
            intakesList.clear();
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();

                while (!cursor.isLast()) {
                    Log.d("Caff", cursor.getString(2) +
                            " - " + cursor.getString(3));
                    intakesList.add(cursor.getString(2) +
                            " - " + cursor.getString(3));
                    cursor.moveToNext();
                }
                Log.d("Caff", cursor.getString(2) +
                        " - " + cursor.getString(3));
                intakesList.add(cursor.getString(2) +
                        " - " + cursor.getString(3));
            }
        } else {
            Log.d("Caff", "Query returned null cursor in submitDay");
        }

        startActivity(new Intent(this, GraphActivity.class));
    }

    public void resetDaily(View view) {
        caffCounterTV.setText("0");
        coffeeET.setText("");
        teaET.setText("");
        caffET.setText("");
        LoginActivity.caffPrefs.edit().putInt("caffeine", 0).apply();
    }

    public void logout(View view) {
        LoginActivity.userPrefs.edit().putString("username", "").apply();
        finish();
    }
}