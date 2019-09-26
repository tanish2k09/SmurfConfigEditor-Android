package com.tanish2k09.sce;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.google.android.material.chip.ChipGroup;
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
        ChipGroup themeSelector = findViewById(R.id.themeChipGrp);
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

        themeSelector.check(getResources().getIdentifier(sp.getString("theme", "dark") + "ThemeChip", "id", this.getPackageName()));
        themeSelector.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.lightThemeChip) {
                sp.edit().putString("theme", "light").apply();
                setThemeColor(0);
            } else if (checkedId == R.id.darkThemeChip) {
                sp.edit().putString("theme", "dark").apply();
                setThemeColor(1);
            } else if (checkedId == R.id.blackThemeChip) {
                sp.edit().putString("theme", "black").apply();
                setThemeColor(2);
            }
        });

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

    private void setBaseTheme(String colorHex) {
        ConstraintLayout settingsLayout = findViewById(R.id.settingsLayout);
        ColorDrawable themeColor = new ColorDrawable(Color.parseColor(colorHex));

        settingsLayout.setBackground(themeColor);
        getWindow().setStatusBarColor(Color.parseColor(colorHex));
        getWindow().setNavigationBarColor(Color.parseColor(colorHex));
    }

    private void setThemeColorLight(String colorHex) {

    }

    private void setThemeColor(int theme) {
        if (theme == 0) {
            setBaseTheme(getResources().getString(R.string.lightThemeMainColor));
        }
        else if (theme == 1) {
            setBaseTheme(getResources().getString(R.string.darkThemeMainColor));
        }
        else if (theme == 2) {
            setBaseTheme(getResources().getString(R.string.blackThemeMainColor));
        }
    }
}
