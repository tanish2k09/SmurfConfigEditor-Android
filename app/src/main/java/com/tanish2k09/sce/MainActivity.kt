package com.tanish2k09.sce

import android.Manifest
import android.animation.Animator
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentTransaction
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AppCompatActivity

import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewAnimationUtils
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.floatingactionbutton.FloatingActionButton

import com.tanish2k09.sce.fragments.containerFragments.CategoryFragment
import com.tanish2k09.sce.fragments.containerFragments.ConfigVar
import com.tanish2k09.sce.helpers.ConfigImportExport
import com.tanish2k09.sce.utils.ConfigCache
import com.tanish2k09.sce.utils.TopCommentStore
import com.topjohnwu.superuser.Shell

import java.io.IOException

class MainActivity : AppCompatActivity() {

    private var saveFab: FloatingActionButton? = null
    private var cig: ConfigImportExport? = null
    private var runScript = true
    private var sp: SharedPreferences? = null
    private var masterLayout: ConstraintLayout? = null
    private var fabLayout: ConstraintLayout? = null
    private var winkAnim: LottieAnimationView? = null

    /* Greet screen controls */
    private var winkContainer: ConstraintLayout? = null


    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fabLayout = findViewById(R.id.fabs_layout)
        masterLayout = findViewById(R.id.master_layout)

        cig = ConfigImportExport(this)

        saveFab = findViewById(R.id.save_fab)
        saveFab!!.setOnClickListener { cig!!.saveConfig(runScript) }
        saveFab!!.setOnLongClickListener {
            optionsVisibilityHandler()
            return@setOnLongClickListener true
        }

        winkAnim = findViewById(R.id.wink_anim)

        val settingsButton = findViewById<TextView>(R.id.settings_button)
        settingsButton.setOnTouchListener(object: View.OnTouchListener {
            override fun onTouch(v: View, m: MotionEvent): Boolean {
                if (m.action == MotionEvent.ACTION_UP) {
                    val intent = Intent(v.context, SettingsActivity::class.java)
                    rippleAnimationActivityOpener(m, settingsButton, intent)
                    return true
                }
                return false
            }
        })

        val infoButton = findViewById<TextView>(R.id.info_button)
        infoButton.setOnTouchListener(object: View.OnTouchListener {
            override fun onTouch(v: View, m: MotionEvent): Boolean {
                if (m.action == MotionEvent.ACTION_UP) {
                    val intent = Intent(v.context, InfoActivity::class.java)
                    rippleAnimationActivityOpener(m, infoButton, intent)
                    return true
                }
                return false
            }
        })

        val importDirectButton = findViewById<LinearLayout>(R.id.import_direct_button)
        importDirectButton.setOnClickListener {
            commenceConfigImport()
        }

        try {
            Shell.su("cd /").exec()
        } catch (e: IOException) {
            Log.d("SCE-MAIN", "Caught exception executing shell, probably not rooted")
        }

        sp = getSharedPreferences("settings", Context.MODE_PRIVATE)

        /* TODO: Fix auto importing config
        if (sp!!.getBoolean("autoImportConfig", true) && ContextCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            commenceConfigImport()
         */
    }

    private fun getRelativeLeft(view: View): Int {
        return if (view.parent == view.rootView)
            view.left
        else
            view.left + getRelativeLeft(view.parent as View)
    }

    private fun getRelativeTop(view: View): Int {
        return if (view.parent == view.rootView)
            view.top
        else
            view.top + getRelativeTop(view.parent as View)
    }

    private fun rippleAnimationActivityOpener(m: MotionEvent,v: View, i:Intent) {
        val revealX = (getRelativeLeft(v) + m.x.toInt())
        val revealY = (getRelativeTop(v) + m.y.toInt())
        i.putExtra("x", revealX)
        i.putExtra("y", revealY)
        winkAnim?.pauseAnimation()
        this.startActivity(i)
    }

    override fun onStart() {
        super.onStart()
        winkContainer = findViewById(R.id.greet_layout)
    }

    override fun onResume() {
        super.onResume()

        if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE),
                    1)
        }

        runScript = sp!!.getBoolean("autoUpdateConfig", true)
        winkAnim?.playAnimation()

        updateTheme()
    }

    private fun updateTheme() {
        val accent = Color.parseColor(sp!!.getString("accentCol", "#00bfa5"))

        saveFab!!.setBackgroundColor(accent)

        var color = "#121212"
        if (sp!!.getBoolean("useBlackNotDark", false))
            color = "#000000"

        val parsedColor = Color.parseColor(color)

        window.statusBarColor = parsedColor

        if (winkContainer!!.isVisible) {
            window.navigationBarColor = ContextCompat.getColor(this, R.color.colorAccent)
        } else {
            window.navigationBarColor = parsedColor
        }
        //mainContentLayout!!.background = colDrawable

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            saveFab!!.outlineAmbientShadowColor = accent
            saveFab!!.outlineSpotShadowColor = accent
        }
    }

    private fun optionsVisibilityHandler() {
        if (!fabLayout!!.isVisible) {
            // Isn't visible, start animation to show more options
            saveFab!!.bottom

            val anim: Animator = ViewAnimationUtils.createCircularReveal(
                    fabLayout,
                    (saveFab!!.left + saveFab!!.right)/2,
                    (saveFab!!.top + saveFab!!.bottom)/2,
                    0.toFloat(),
                    1080.toFloat()
            )

            anim.start()
            fabLayout!!.visibility = View.VISIBLE
            saveFab!!.isLongClickable = false
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 1 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
            Toast.makeText(this, R.string.StoragePermDenied, Toast.LENGTH_LONG).show()
            finish()
            moveTaskToBack(true)
        }
    }

    private fun commenceConfigImport(): Boolean {
        removeConfigFragments()
        TopCommentStore.clear()

        if (!cig!!.configImport()) {
            Toast.makeText(this, resources.getString(R.string.swwImport), Toast.LENGTH_SHORT).show()
            return false
        }

        val size = ConfigCache.configListSize
        Log.d("SCE-CIE", "Found $size values")

        if (size > 0) {
            fillConfigFragments()
            winkContainer!!.visibility = View.GONE
            saveFab!!.visibility = View.VISIBLE
            saveFab!!.isEnabled = true
        } else {
            Toast.makeText(this, "No values could be extracted", Toast.LENGTH_SHORT).show()
        }
        return true
    }

    private fun removeConfigFragments() {
        val fm = supportFragmentManager

        for (idx in 1 until ConfigCache.configListSize) {
            val cf = fm.findFragmentByTag("catFrag" + (ConfigCache.getStringVal(idx)?.category ?: "")) as CategoryFragment?
            if (cf != null) {
                if (cf.popFragment(idx))
                    fm.beginTransaction().remove(cf).commitNow()
            }
        }
    }

    private fun fillConfigFragments() {
        val fm = supportFragmentManager
        var ft: FragmentTransaction

        /* Assumes the first config is PROFILE.VERSION, so we skip that for fragment inflation*/
        val pv = "Profile version: " + (ConfigCache.getStringVal(0)?.activeVal ?: "Null?")
        Toast.makeText(this, pv, Toast.LENGTH_SHORT).show()
        Log.d("SCE-CIE", pv)

        for (idx in 1 until ConfigCache.configListSize) {
            val configFragment = ConfigVar()
            val category = ConfigCache.getStringVal(idx)?.category
            var cf = fm.findFragmentByTag("catFrag$category") as CategoryFragment?

            if (cf == null) {
                ft = fm.beginTransaction()
                cf = CategoryFragment()
                cf.setInstanceCategory(category!!)
                ft.add(R.id.listLayout, cf, "catFrag$category").commitNow()
            }

            if (!configFragment.setupCardInfo(idx))
                continue

            cf.pushConfigVarFragment(configFragment, idx)
        }
    }

    public override fun onDestroy() {
        try {
            val shell = Shell.getCachedShell()
            shell?.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        super.onDestroy()
    }

    companion object {

        init {
            /* Shell.Config methods shall be called before any shell is created
         * This is the reason why you should call it in a static block
         * The followings are some examples, check Javadoc for more details */
            Shell.Config.setFlags(Shell.FLAG_REDIRECT_STDERR or Shell.FLAG_VERBOSE_LOGGING)
            Shell.Config.setTimeout(5)
        }
    }
}
