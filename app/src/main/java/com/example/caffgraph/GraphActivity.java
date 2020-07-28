package com.example.caffgraph;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

// TODO -Implement graph somewhere in this Activity
// TODO -Implement TextView of average intake per day
// TODO -Implement way to only display intakes per month or year and update average
// TODO -Implement customizable goal that visualizes difference in entries

/*
Features:
    -Shows all daily intakes
 */


public class GraphActivity extends AppCompatActivity {
    ListView intakeLV;
    ArrayAdapter<String> intakeAA;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        intakeLV = findViewById(R.id.intakeLV);
        intakeAA = new ArrayAdapter<String>
                (this, android.R.layout.simple_list_item_1, MainActivity.intakesList);
        intakeLV.setAdapter(intakeAA);
    }

    public void goBack(View view) {
        finish();
    }
}