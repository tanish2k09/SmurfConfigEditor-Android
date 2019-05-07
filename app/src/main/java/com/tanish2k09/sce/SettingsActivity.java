package com.tanish2k09.sce;

import android.content.SharedPreferences;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.black));

        SharedPreferences sp = getSharedPreferences("settings",MODE_PRIVATE);

        Switch autoImportConfig = findViewById(R.id.autoImportConfigSwitch);

        autoImportConfig.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sp.edit().putBoolean("autoImportConfig", autoImportConfig.isChecked()).apply();
            Toast.makeText(this, "Restart app to apply", Toast.LENGTH_SHORT).show();
        });

        autoImportConfig.setChecked(sp.getBoolean("autoImportConfig", false));
    }
}
