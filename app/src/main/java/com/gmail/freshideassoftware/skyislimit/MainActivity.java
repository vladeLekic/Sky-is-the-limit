package com.gmail.freshideassoftware.skyislimit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

    private static ExperienceDatabaseOpenHelper experienceDatabaseOpenHelper;
    private static int experience;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.colorMain));
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorMain));
        getSupportActionBar().hide();

        experienceDatabaseOpenHelper = new ExperienceDatabaseOpenHelper(this);
        experience = experienceDatabaseOpenHelper.getExperience();


        TextView textView = (TextView) findViewById(R.id.textViewSandbox);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, RaceActivity.class);
                MainActivity.this.startActivity(intent);
            }
        });

        textView = (TextView) findViewById(R.id.textViewChooseAirship);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ModelGalleryActivity.class);
                MainActivity.this.startActivity(intent);
            }
        });

        textView = (TextView) findViewById(R.id.textViewGameRun);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, TwoPlayerGameActivity.class);
                MainActivity.this.startActivity(intent);
            }
        });

        textView = (TextView) findViewById(R.id.textViewCredits);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "All 3D models belong to Poly by Google\n               using a CC-BY licence", Toast.LENGTH_LONG).show();
            }
        });

        Profile.getProfile().setId(2);
    }


    public static int getExperience(){
        return experience;
    }

    public static void setExperience(int exp){
        experience = exp;
        experienceDatabaseOpenHelper.insert(experience);
    }
}
