package com.tanish2k09.sce

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.ViewAnimationUtils
import android.view.ViewTreeObserver
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import kotlin.math.hypot

class InfoActivity : AppCompatActivity() {

    private var revealX = 0
    private var revealY = 0
    private lateinit var rootLayout: ConstraintLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null)
            overridePendingTransition(R.anim.no_op, R.anim.no_op)

        setContentView(R.layout.activity_info)
        val colorPicker = findViewById<LinearLayout>(R.id.colorPickerLayout)
        val source = findViewById<LinearLayout>(R.id.sourceCodeLayout)
        val xda = findViewById<Button>(R.id.xdaButton)
        val telegram = findViewById<Button>(R.id.tgButton)
        val email = findViewById<Button>(R.id.emailButton)
        val discord = findViewById<Button>(R.id.discordButton)
        rootLayout = findViewById(R.id.infoTopLayout)

        if (intent.hasExtra("x"))
            revealX = intent.getIntExtra("x", 0)

        if (intent.hasExtra("y"))
            revealY = intent.getIntExtra("y", 0)

        colorPicker.setOnClickListener {
            val uri = Uri.parse(getString(R.string.colorPickerLink))
            val i = Intent(Intent.ACTION_VIEW, uri)
            startActivity(i)
        }
        source.setOnClickListener {
            val uri = Uri.parse(getString(R.string.SourceCodeLink))
            val i = Intent(Intent.ACTION_VIEW, uri)
            startActivity(i)
        }
        xda.setOnClickListener {
            val uri = Uri.parse(getString(R.string.xdaLink))
            val i = Intent(Intent.ACTION_VIEW, uri)
            startActivity(i)
        }
        telegram.setOnClickListener {
            val uri = Uri.parse(getString(R.string.telegramLink))
            val i = Intent(Intent.ACTION_VIEW, uri)
            startActivity(i)
        }
        email.setOnClickListener {
            val uri = Uri.parse(getString(R.string.emailLink))
            val i = Intent(Intent.ACTION_VIEW, uri)
            startActivity(i)
        }
        discord.setOnClickListener {
            Toast.makeText(this, this.resources.getString(R.string.quantum), Toast.LENGTH_LONG).show()
        }

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

    override fun onResume() {
        super.onResume()
        val sp = getSharedPreferences("settings", Context.MODE_PRIVATE)
        val infoTopLayout = findViewById<ConstraintLayout>(R.id.infoTopLayout)
        var color = "#121212"
        if (sp.getBoolean("useBlackNotDark", false)) color = "#000000"
        val parsedColor = Color.parseColor(color)
        infoTopLayout.setBackgroundColor(parsedColor)
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
        circularReveal.interpolator = AccelerateInterpolator()
        // make the view visible and start the animation
        rootLayout.visibility = View.VISIBLE
        circularReveal.start()
    }

    private fun unRevealActivity() {
        val finalRadius = hypot(rootLayout.width.toDouble(), rootLayout.height.toDouble()).toFloat()
        val circularReveal = ViewAnimationUtils.createCircularReveal(
                rootLayout, revealX, revealY, finalRadius, 0f)
        circularReveal.duration = 500
        circularReveal.interpolator = DecelerateInterpolator()
        circularReveal.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                rootLayout.visibility = View.INVISIBLE
                finish()
            }
        })
        circularReveal.start()
    }

    override fun onBackPressed() {
        unRevealActivity()
    }
}