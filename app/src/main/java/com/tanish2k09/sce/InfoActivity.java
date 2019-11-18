package com.tanish2k09.sce;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

public class InfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        LinearLayout colorPicker = findViewById(R.id.colorPickerLayout);
        LinearLayout source = findViewById(R.id.sourceCodeLayout);
        Button xda = findViewById(R.id.xdaButton);
        Button telegram = findViewById(R.id.tgButton);
        Button email = findViewById(R.id.emailButton);
        Button discord = findViewById(R.id.discordButton);

        colorPicker.setOnClickListener(view -> {
            Uri uri = Uri.parse(getString(R.string.colorPickerLink));
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        });

        source.setOnClickListener(view -> {
            Uri uri = Uri.parse(getString(R.string.SourceCodeLink));
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        });

        xda.setOnClickListener(view -> {
            Uri uri = Uri.parse(getString(R.string.xdaLink));
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        });

        telegram.setOnClickListener(view -> {
            Uri uri = Uri.parse(getString(R.string.telegramLink));
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        });

        email.setOnClickListener(view -> {
            Uri uri = Uri.parse(getString(R.string.emailLink));
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        });

        discord.setOnClickListener(view -> Toast.makeText(this, this.getResources().getString(R.string.quantum), Toast.LENGTH_LONG).show());
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sp = getSharedPreferences("settings",MODE_PRIVATE);
        ConstraintLayout infoTopLayout = findViewById(R.id.infoTopLayout);
        String color = "#121212";

        if (sp.getBoolean("useBlackNotDark", false))
            color = "#000000";

        int parsedColor = Color.parseColor(color);

        getWindow().setNavigationBarColor(parsedColor);
        getWindow().setStatusBarColor(parsedColor);
        infoTopLayout.setBackgroundColor(parsedColor);
    }
}
