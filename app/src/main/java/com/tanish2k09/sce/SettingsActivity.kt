package com.tanish2k09.sce

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewAnimationUtils
import android.view.ViewTreeObserver
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.marginTop
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.tanish2k09.sce.data.enums.ETheme
import com.tanish2k09.sce.databinding.ActivitySettingsBinding
import com.tanish2k09.sce.utils.extensions.changeTopMargin
import com.tanish2k09.sce.viewmodels.SharedPrefsVM
import kotlin.math.hypot


class SettingsActivity : AppCompatActivity(){

    private var revealX = 0
    private var revealY = 0
    private lateinit var binding: ActivitySettingsBinding
    private lateinit var settingsVM: SharedPrefsVM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null)
            overridePendingTransition(R.anim.no_op, R.anim.no_op)

        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        settingsVM = ViewModelProvider(this).get(SharedPrefsVM::class.java)
        attachViewModelObservers()

        initUIListeners()

        if (intent.hasExtra("x")) {
            revealX = intent.getIntExtra("x", 0)
        }

        if (intent.hasExtra("y")) {
            revealY = intent.getIntExtra("y", 0)
        }

        if (savedInstanceState != null) {
            return
        }

        binding.settingsLayout.visibility = View.INVISIBLE
        val vto: ViewTreeObserver = binding.settingsLayout.viewTreeObserver

        if (vto.isAlive.not()) {
            return
        }

        vto.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                revealActivity(revealX, revealY)
                binding.settingsLayout.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })
    }

    override fun onResume() {
        super.onResume()
        settingsVM.readSettingsToFields()
    }

    private fun initUIListeners() {
        binding.appTitle.setOnApplyWindowInsetsListener {view, insets ->
            changeTopMargin(view, binding.appTitle.marginTop + insets.systemWindowInsetTop)
            insets
        }

        binding.autoImportConfigSwitch.setOnCheckedChangeListener { _, b ->
            settingsVM.setAutoImport(b)
        }

        binding.autoScriptSwitch.setOnCheckedChangeListener { _, b ->
            settingsVM.setRunScript(b)
        }

        binding.useTitlesOnCards.setOnCheckedChangeListener { _, b ->
            settingsVM.setUseTitles(b)
        }

        binding.useBlackNotDark.setOnCheckedChangeListener { _, b ->
            if (b) {
                settingsVM.setTheme(ETheme.BLACK)
            } else {
                settingsVM.setTheme(ETheme.DARK)
            }
        }
    }

    private fun attachViewModelObservers() {
        settingsVM.autoImport.observe(this, Observer {
            binding.autoImportConfigSwitch.isChecked = it
        })

        settingsVM.runScript.observe(this, Observer {
            binding.autoScriptSwitch.isChecked = it
        })

        settingsVM.useTitles.observe(this, Observer {
            binding.useTitlesOnCards.isChecked = it
        })

        settingsVM.theme.observe(this, Observer {
            binding.useBlackNotDark.isChecked = (it == ETheme.BLACK)
            handleTheme(it)
        })
    }

    private fun handleTheme(newTheme: ETheme) {
        val mainColor = Color.parseColor(newTheme.hex)
        binding.settingsLayout.setBackgroundColor(mainColor)
    }

    private fun revealActivity(x: Int, y: Int) {
        val finalRadius = hypot(
                binding.settingsLayout.width.toDouble(),
                binding.settingsLayout.height.toDouble()).toFloat()

        // create the animator for this view (the start radius is zero)
        val circularReveal = ViewAnimationUtils.createCircularReveal(
                binding.settingsLayout,
                x, y,
                0f, finalRadius
        )

        circularReveal.duration = 500
        circularReveal.interpolator = AccelerateInterpolator()
        // make the view visible and start the animation
        binding.settingsLayout.visibility = View.VISIBLE
        circularReveal.start()
    }

    private fun unRevealActivity() {
        val finalRadius = hypot(
                binding.settingsLayout.width.toDouble(),
                binding.settingsLayout.height.toDouble()).toFloat()
        val circularReveal = ViewAnimationUtils.createCircularReveal(
                binding.settingsLayout, revealX, revealY, finalRadius, 0f)
        circularReveal.duration = 500
        circularReveal.interpolator = DecelerateInterpolator()
        circularReveal.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                binding.settingsLayout.visibility = View.INVISIBLE
                finish()
            }
        })
        circularReveal.start()
    }

    override fun onBackPressed() {
        unRevealActivity()
    }
}
