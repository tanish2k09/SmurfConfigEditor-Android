package com.tanish2k09.sce;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.cardview.widget.CardView;
import android.widget.Switch;
import android.widget.Toast;
import com.pes.androidmaterialcolorpickerdialog.ColorPicker;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.black));

        SharedPreferences sp = getSharedPreferences("settings",MODE_PRIVATE);

        Switch autoImportConfig = findViewById(R.id.autoImportConfigSwitch);
        Switch autoUpdateConfig = findViewById(R.id.autoUpdateConfigSwitch);
        Switch useTitles = findViewById(R.id.useTitlesOnCards);
        Switch useBlackBG = findViewById(R.id.useBlackNotDark);
        CardView colorCard = findViewById(R.id.colorCard);

        autoImportConfig.setChecked(sp.getBoolean("autoImportConfig", false));
        autoImportConfig.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sp.edit().putBoolean("autoImportConfig", autoImportConfig.isChecked()).apply();
            Toast.makeText(this, "autoImportConfig: Restart app to apply", Toast.LENGTH_SHORT).show();
        });

        autoUpdateConfig.setChecked(sp.getBoolean("autoUpdateConfig", false));
        autoUpdateConfig.setOnCheckedChangeListener((buttonView, isChecked) -> sp.edit().putBoolean("autoUpdateConfig", autoUpdateConfig.isChecked()).apply());

        useTitles.setChecked(sp.getBoolean("useTitlesOnCards", false));
        useTitles.setOnCheckedChangeListener((buttonView, isChecked) -> sp.edit().putBoolean("useTitlesOnCards", isChecked).apply());

        useBlackBG.setChecked(sp.getBoolean("useBlackNotDark", true));
        setThemeColor(useBlackBG.isChecked());
        useBlackBG.setOnCheckedChangeListener(((buttonView, isChecked) -> {
            sp.edit().putBoolean("useBlackNotDark", isChecked).apply();

            String color = "#121212";

            if (isChecked)
                color = "#000000";

            setThemeColor(color);
        }));

        setColorCard(colorCard, Color.parseColor(sp.getString("accentCol", "#00bfa5")));
        colorCard.setOnClickListener(v -> {
            int preColor = Color.parseColor(sp.getString("accentCol", "#00bfa5"));
            final ColorPicker cp = new ColorPicker(this,Color.red(preColor), Color.green(preColor), Color.blue(preColor));
            cp.enableAutoClose();
            cp.setCallback(color -> {
                sp.edit().putString("accentCol", String.format("#%06X", (0xFFFFFF & color))).apply();
                setColorCard(colorCard, color);
            });
            cp.show();
        });
    }

    private void setColorCard(CardView card, int color) {
        card.setCardBackgroundColor(color);
    }

    private void setThemeColor(String colorHex) {
        ConstraintLayout settingsLayout = findViewById(R.id.settingsLayout);

        ColorDrawable themeColor = new ColorDrawable(Color.parseColor(colorHex));
        settingsLayout.setBackground(themeColor);
        getWindow().setStatusBarColor(Color.parseColor(colorHex));
    }

    private void setThemeColor(boolean isBlackChecked) {
        if (isBlackChecked)
            setThemeColor("#000000");
        else
            setThemeColor("#121212");
    }
}
