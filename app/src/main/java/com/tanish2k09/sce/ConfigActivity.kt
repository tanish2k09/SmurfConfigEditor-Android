package com.tanish2k09.sce

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup.MarginLayoutParams
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.marginBottom
import androidx.core.view.updatePadding
import androidx.fragment.app.FragmentTransaction
import com.tanish2k09.sce.data.ConfigCache
import com.tanish2k09.sce.data.TopCommentStore
import com.tanish2k09.sce.fragments.containerFragments.CategoryFragment
import com.tanish2k09.sce.fragments.containerFragments.ConfigVar
import com.tanish2k09.sce.helpers.ConfigImportExport
import com.tanish2k09.sce.interfaces.ScriptCallback
import com.tanish2k09.sce.utils.extensions.changeBottomMargin
import com.tanish2k09.sce.utils.extensions.rippleAnimationActivityOpener


class ConfigActivity : AppCompatActivity(), ScriptCallback {

    private var cig: ConfigImportExport? = null
    private var runScript = true
    private var actionsBottomMarginDefault: Int? = 24
    private var actionsBar: LinearLayout? = null
    private lateinit var scriptButton: ImageButton

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_config)

        cig = ConfigImportExport(this)

        actionsBar = findViewById(R.id.configActionsBar)
        val topLayout = findViewById<ConstraintLayout>(R.id.topConfigLayout)

        actionsBottomMarginDefault =
                savedInstanceState?.getInt("ABMD", 24) ?: actionsBar!!.marginBottom

        topLayout!!.setOnApplyWindowInsetsListener {view, insets ->
            Log.d("SCE-INSETS", "Top: " + insets.systemWindowInsetTop)
            view.updatePadding(top = insets.systemWindowInsetTop)
            insets
        }

        actionsBar!!.setOnApplyWindowInsetsListener {view, insets ->
            Log.d("SCE-INSETS", "Bottom: " + insets.systemWindowInsetBottom)
            Log.d("SCE-INSETS", "Default: $actionsBottomMarginDefault")
            Log.d("SCE-INSETS", "Current: " + actionsBar!!.marginBottom)
            changeBottomMargin(
                    view,
                    insets.systemWindowInsetBottom + actionsBottomMarginDefault!!
            )
            insets
        }

        val saveButton = findViewById<ImageButton>(R.id.saveButton)
        saveButton.setOnClickListener {
            saveButton.isEnabled = false
            cig!!.saveConfig(false)
            saveButton.isEnabled = true
        }

        saveButton.setOnLongClickListener {
            Toast.makeText(this, "Save config", Toast.LENGTH_SHORT).show()
            return@setOnLongClickListener true
        }

        scriptButton = findViewById(R.id.applyConfigButton)
        scriptButton.setOnClickListener {
            scriptButton.isEnabled = false
            Toast.makeText(this, "Executing script...", Toast.LENGTH_SHORT).show()
            cig!!.runScript(this)
        }

        scriptButton.setOnLongClickListener {
            Toast.makeText(
                    this,
                    "Execute script to apply config",
                    Toast.LENGTH_SHORT).show()
            return@setOnLongClickListener true
        }

        val searchButton = findViewById<ImageButton>(R.id.searchConfigButton)
        searchButton.setOnClickListener {
            Toast.makeText(
                    this,
                    "Unavailable, search coming soon",
                    Toast.LENGTH_SHORT)
                .show()
        }

        searchButton.setOnLongClickListener {
            Toast.makeText(this, "Search within variables", Toast.LENGTH_SHORT).show()
            return@setOnLongClickListener true
        }

        val settingsButton = findViewById<ImageButton>(R.id.settingsConfigButton)
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

        if (runScript && savedInstanceState == null) {
            commenceConfigImport()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt("ABMD", actionsBottomMarginDefault!!)
        super.onSaveInstanceState(outState)
    }

    override fun onResume() {
        // TODO: Respect the theme settings
        actionsBar?.requestApplyInsets()
        super.onResume()
    }

    private fun commenceConfigImport(): Boolean {
        removeConfigFragments()
        TopCommentStore.clear()

        if (!cig!!.configImport()) {
            Toast.makeText(
                    this,
                    resources.getString(R.string.swwImport),
                    Toast.LENGTH_SHORT
            )
                    .show()
            return false
        }

        val size = ConfigCache.configListSize
        Log.d("SCE-CIE", "Found $size values")

        if (size > 0) {
            fillConfigFragments()
        } else {
            Toast.makeText(this, "No values could be extracted", Toast.LENGTH_SHORT).show()
        }

        return true
    }

    private fun removeConfigFragments() {
        val fm = supportFragmentManager

        for (idx in 1 until ConfigCache.configListSize) {
            val cf = fm.findFragmentByTag(
                    "catFrag" +
                        (ConfigCache.getStringVal(idx)?.category ?: "")
            ) as CategoryFragment?

            // Remove all variable fragments, and then the category
            if (cf != null && cf.popFragment(idx)) {
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
            val configVarFragment = ConfigVar()
            val category = ConfigCache.getStringVal(idx)?.category
            var cf = fm.findFragmentByTag("catFrag$category") as CategoryFragment?

            // Create a new category fragment if it's null
            if (cf == null) {
                ft = fm.beginTransaction()
                cf = CategoryFragment()
                cf.setInstanceCategory(category!!)
                ft.add(R.id.configContents, cf, "catFrag$category").commitNow()
            }

            if (!configVarFragment.setupCardInfo(idx)) {
                Log.d("SCE-CIE", "CONTINUING without DRAWING")
                continue
            }

            cf.pushConfigVarFragment(configVarFragment, idx)
        }
    }

    override fun callback() {
        scriptButton.isEnabled = true
    }
}
