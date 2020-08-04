package com.tanish2k09.sce

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.ViewAnimationUtils
import android.view.ViewTreeObserver
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.marginBottom
import androidx.core.view.marginTop
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.tanish2k09.sce.data.enums.ETheme
import com.tanish2k09.sce.databinding.ActivityInfoBinding
import com.tanish2k09.sce.utils.extensions.changeBottomMargin
import com.tanish2k09.sce.utils.extensions.changeTopMargin
import com.tanish2k09.sce.viewmodels.SharedPrefsVM
import kotlin.math.hypot

class InfoActivity : AppCompatActivity() {

    private var revealX = 0
    private var revealY = 0
    private var topMarginDefault: Int = 32
    private var bottomMarginDefault: Int = 24
    private lateinit var settingsVM: SharedPrefsVM
    private lateinit var binding: ActivityInfoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null)
            overridePendingTransition(R.anim.no_op, R.anim.no_op)

        binding = ActivityInfoBinding.inflate(layoutInflater)
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

        /* Commence reveal animation */
        binding.infoTopLayout.visibility = View.INVISIBLE
        val vto: ViewTreeObserver = binding.infoTopLayout.viewTreeObserver

        if (vto.isAlive.not()) {
            return
        }

        vto.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                revealActivity(revealX, revealY)
                binding.infoTopLayout.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })
    }

    private fun attachViewModelObservers() {
        settingsVM.theme.observe(this, Observer {
            handleTheme(it)
        })
    }

    private fun initUIListeners() {
        binding.colorPickerLayout.setOnClickListener {
            launchEvent(Uri.parse(getString(R.string.colorPickerLink)))
        }
        binding.sourceCodeLayout.setOnClickListener {
            launchEvent(Uri.parse(getString(R.string.SourceCodeLink)))
        }
        binding.stickyScrollViewLayout.setOnClickListener {
            launchEvent(Uri.parse(getString(R.string.stickyScrollLink)))
        }
        binding.xdaButton.setOnClickListener {
            launchEvent(Uri.parse(getString(R.string.xdaLink)))
        }
        binding.tgButton.setOnClickListener {
            launchEvent(Uri.parse(getString(R.string.telegramLink)))
        }
        binding.emailButton.setOnClickListener {
            launchEvent(Uri.parse(getString(R.string.emailLink)))
        }
        binding.discordButton.setOnClickListener {
            Toast.makeText(this, this.resources.getString(R.string.quantum), Toast.LENGTH_LONG).show()
        }

        topMarginDefault = binding.infoAppTitle.marginTop
        binding.infoAppTitle.setOnApplyWindowInsetsListener {view, insets ->
            changeTopMargin(view, topMarginDefault + insets.systemWindowInsetTop)
            insets
        }

        bottomMarginDefault = binding.versionText.marginBottom
        binding.versionText.setOnApplyWindowInsetsListener {view, insets ->
            changeBottomMargin(view, bottomMarginDefault + insets.systemWindowInsetBottom)
            insets
        }
    }

    override fun onResume() {
        super.onResume()
        settingsVM.readSettingsToFields()
    }

    private fun handleTheme(newTheme: ETheme) {
        val mainColor = Color.parseColor(newTheme.hex)
        binding.infoTopLayout.setBackgroundColor(mainColor)
    }

    private fun launchEvent(uri: Uri) {
        val i = Intent(Intent.ACTION_VIEW, uri)
        startActivity(i)
    }

    private fun revealActivity(x: Int, y: Int) {
        val finalRadius = hypot(
                binding.infoTopLayout.width.toDouble(),
                binding.infoTopLayout.height.toDouble()).toFloat()

        // create the animator for this view (the start radius is zero)
        val circularReveal = ViewAnimationUtils.createCircularReveal(
                binding.infoTopLayout,
                x, y,
                0f, finalRadius
        )

        circularReveal.duration = 500
        circularReveal.interpolator = AccelerateInterpolator()

        // make the view visible and start the animation
        binding.infoTopLayout.visibility = View.VISIBLE
        circularReveal.start()
    }

    private fun unRevealActivity() {
        val finalRadius = hypot(
                binding.infoTopLayout.width.toDouble(),
                binding.infoTopLayout.height.toDouble()).toFloat()

        val circularReveal = ViewAnimationUtils.createCircularReveal(
                binding.infoTopLayout,
                revealX,
                revealY,
                finalRadius,
                0f
        )

        circularReveal.duration = 500
        circularReveal.interpolator = DecelerateInterpolator()

        circularReveal.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                binding.infoTopLayout.visibility = View.INVISIBLE
                finish()
            }
        })

        circularReveal.start()
    }

    override fun onBackPressed() {
        unRevealActivity()
    }
}