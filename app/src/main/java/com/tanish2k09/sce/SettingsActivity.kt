package com.tanish2k09.sce

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.ViewAnimationUtils
import android.view.ViewTreeObserver
import android.view.animation.LinearInterpolator
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import kotlin.math.hypot


class SettingsActivity : AppCompatActivity(){

    private var revealX = 0
    private var revealY = 0
    private lateinit var rootLayout: ConstraintLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null)
            overridePendingTransition(R.anim.no_op, R.anim.no_op)

        setContentView(R.layout.activity_settings)
        window.navigationBarColor = ContextCompat.getColor(this, R.color.black)
        rootLayout = findViewById(R.id.settingsLayout)

        if (intent.hasExtra("x"))
            revealX = intent.getIntExtra("x", 0)

        if (intent.hasExtra("y"))
            revealY = intent.getIntExtra("y", 0)

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
        /* TODO: Fix crash on color picker dialog
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
         */

        if (savedInstanceState == null) {
            rootLayout.visibility = View.INVISIBLE

            val vto: ViewTreeObserver = rootLayout.viewTreeObserver
            if (vto.isAlive) {
                vto.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                    override fun onGlobalLayout() {
                        revealActivity(revealX, revealY)
                        rootLayout.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    }
                })
            }
        }
    }

    private fun revealActivity(x: Int, y: Int) {
        val finalRadius = hypot(rootLayout.width.toDouble(), rootLayout.height.toDouble()).toFloat()
        // create the animator for this view (the start radius is zero)
        val circularReveal = ViewAnimationUtils.createCircularReveal(
                rootLayout,
                x, y,
                0f, finalRadius
        )
        circularReveal.duration = 500
        circularReveal.interpolator = LinearInterpolator()
        // make the view visible and start the animation
        rootLayout.visibility = View.VISIBLE
        circularReveal.start()
    }

    private fun unRevealActivity() {
        val finalRadius = hypot(rootLayout.width.toDouble(), rootLayout.height.toDouble()).toFloat()
        val circularReveal = ViewAnimationUtils.createCircularReveal(
                rootLayout, revealX, revealY, finalRadius, 0f)
        circularReveal.duration = 500
        circularReveal.interpolator = LinearInterpolator()
        circularReveal.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                rootLayout.visibility = View.INVISIBLE
                finish()
                overridePendingTransition(R.anim.no_op, R.anim.no_op)
            }
        })
        circularReveal.start()
    }

    private fun setColorCard(card: CardView, color: Int) {
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

    override fun onBackPressed() {
        unRevealActivity()
    }
}
