package com.tanish2k09.sce

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentTransaction
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.appcompat.widget.Toolbar

import android.util.Log
import android.util.TypedValue
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast

import com.tanish2k09.sce.fragments.containerFragments.CategoryFragment
import com.tanish2k09.sce.fragments.containerFragments.ConfigVar
import com.tanish2k09.sce.helpers.ConfigImportExport
import com.tanish2k09.sce.utils.ConfigCache
import com.tanish2k09.sce.utils.TopCommentStore
import com.topjohnwu.superuser.Shell

import java.io.IOException

class MainActivity : AppCompatActivity() {

    private var noNotesCard: CardView? = null
    private var saveCard: CardView? = null
    private var cig: ConfigImportExport? = null
    private var runScript = true
    private var sp: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        cig = ConfigImportExport(this)

        noNotesCard = findViewById(R.id.no_notes_card)
        noNotesCard!!.setOnClickListener { commenceConfigImport() }

        saveCard = findViewById(R.id.saveButton)
        saveCard!!.setOnClickListener { cig!!.saveConfig(runScript) }

        Shell.su("cd /").submit()

        sp = getSharedPreferences("settings", Context.MODE_PRIVATE)
        if (sp!!.getBoolean("autoImportConfig", true) && ContextCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            commenceConfigImport()
    }

    override fun onResume() {
        super.onResume()
        val saveTitle = findViewById<TextView>(R.id.saveTitle)

        if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE),
                    1)
        }

        runScript = sp!!.getBoolean("autoUpdateConfig", true)
        if (runScript) {
            saveTitle.text = resources.getString(R.string.saveAndApply)
            saveTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
        } else {
            saveTitle.text = resources.getString(R.string.save)
            saveTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24f)
        }

        updateTheme()
    }

    private fun updateTheme() {
        val accent = Color.parseColor(sp!!.getString("accentCol", "#00bfa5"))
        val saveTitle = findViewById<TextView>(R.id.saveTitle)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        val mainContentLayout = findViewById<ConstraintLayout>(R.id.mainContentLayout)

        saveCard!!.setCardBackgroundColor(accent)
        noNotesCard!!.setCardBackgroundColor(accent)

        var color = "#121212"
        if (sp!!.getBoolean("useBlackNotDark", false))
            color = "#000000"

        val parsedColor = Color.parseColor(color)

        val colDrawable = ColorDrawable(parsedColor)

        toolbar.background = colDrawable
        window.statusBarColor = parsedColor
        window.navigationBarColor = parsedColor
        mainContentLayout.background = colDrawable
        saveTitle.setTextColor(parsedColor)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            noNotesCard!!.outlineAmbientShadowColor = accent
            noNotesCard!!.outlineSpotShadowColor = accent
            saveCard!!.outlineAmbientShadowColor = accent
            saveCard!!.outlineSpotShadowColor = accent
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.


        when (item.itemId) {
            R.id.action_import_config -> return commenceConfigImport()

            R.id.action_info -> {
                val infoActivityIntent = Intent(this, InfoActivity::class.java)
                startActivity(infoActivityIntent)
            }

            R.id.action_settings -> {
                val settingsActivityIntent = Intent(this, SettingsActivity::class.java)
                startActivity(settingsActivityIntent)
            }
        }

        return super.onOptionsItemSelected(item)
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
            noNotesCard!!.visibility = View.INVISIBLE
            saveCard!!.visibility = View.VISIBLE
            saveCard!!.isEnabled = true
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
