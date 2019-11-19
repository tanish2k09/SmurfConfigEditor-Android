package com.tanish2k09.sce

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.cardview.widget.CardView
import android.widget.Switch
import android.widget.Toast
import com.pes.androidmaterialcolorpickerdialog.ColorPicker
import com.pes.androidmaterialcolorpickerdialog.ColorPickerCallback

class SettingsActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        window.navigationBarColor = ContextCompat.getColor(this, R.color.black)

        val sp = getSharedPreferences("settings", Context.MODE_PRIVATE)

        val autoImportConfig = findViewById<Switch>(R.id.autoImportConfigSwitch)
        val autoUpdateConfig = findViewById<Switch>(R.id.autoUpdateConfigSwitch)
        val useTitles = findViewById<Switch>(R.id.useTitlesOnCards)
        val useBlackBG = findViewById<Switch>(R.id.useBlackNotDark)
        val colorCard = findViewById<CardView>(R.id.colorCard)

        autoImportConfig.isChecked = sp.getBoolean("autoImportConfig", true)
        autoImportConfig.setOnCheckedChangeListener { _, _ ->
            sp.edit().putBoolean("autoImportConfig", autoImportConfig.isChecked).apply()
            Toast.makeText(this, "autoImportConfig: Restart app to apply", Toast.LENGTH_SHORT).show()
        }

        autoUpdateConfig.isChecked = sp.getBoolean("autoUpdateConfig", true)
        autoUpdateConfig.setOnCheckedChangeListener { _, _ -> sp.edit().putBoolean("autoUpdateConfig", autoUpdateConfig.isChecked).apply() }

        useTitles.isChecked = sp.getBoolean("useTitlesOnCards", true)
        useTitles.setOnCheckedChangeListener { _, isChecked -> sp.edit().putBoolean("useTitlesOnCards", isChecked).apply() }

        useBlackBG.isChecked = sp.getBoolean("useBlackNotDark", false)
        setThemeColor(useBlackBG.isChecked)
        useBlackBG.setOnCheckedChangeListener { _, isChecked ->
            sp.edit().putBoolean("useBlackNotDark", isChecked).apply()

            var color = "#121212"

            if (isChecked)
                color = "#000000"

            setThemeColor(color)
        }

        setColorCard(colorCard, Color.parseColor(sp.getString("accentCol", "#00bfa5")))
        colorCard.setOnClickListener {
            val preColor = Color.parseColor(sp.getString("accentCol", "#00bfa5"))
            val cp = ColorPicker(this, Color.red(preColor), Color.green(preColor), Color.blue(preColor))
            cp.enableAutoClose()
            cp.setCallback(object : ColorPickerCallback {
                override fun onColorChosen(color: Int) {
                    sp.edit().putString("accentCol", String.format("#%06X", 0xFFFFFF and color)).apply()
                    setColorCard(colorCard, color)
                }})
            cp.show()
        }
    }

    fun setColorCard(card: CardView, color: Int) {
        card.setCardBackgroundColor(color)
    }

    private fun setThemeColor(colorHex: String) {
        val settingsLayout = findViewById<ConstraintLayout>(R.id.settingsLayout)

        val themeColor = ColorDrawable(Color.parseColor(colorHex))
        settingsLayout.background = themeColor
        window.statusBarColor = Color.parseColor(colorHex)
        window.navigationBarColor = Color.parseColor(colorHex)
    }

    private fun setThemeColor(isBlackChecked: Boolean) {
        if (isBlackChecked)
            setThemeColor("#000000")
        else
            setThemeColor("#121212")
    }
}
