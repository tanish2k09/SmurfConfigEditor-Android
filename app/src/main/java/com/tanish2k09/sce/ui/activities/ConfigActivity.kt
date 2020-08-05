package com.tanish2k09.sce.ui.activities

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.tanish2k09.sce.R
import com.tanish2k09.sce.data.enums.ETheme
import com.tanish2k09.sce.databinding.ActivityConfigBinding
import com.tanish2k09.sce.ui.fragments.containerFragments.CategoryFragment
import com.tanish2k09.sce.ui.fragments.containerFragments.ConfigVarFragment
import com.tanish2k09.sce.helpers.config.ConfigScript
import com.tanish2k09.sce.interfaces.IScriptCallback
import com.tanish2k09.sce.utils.exceptions.ConfigFormatException
import com.tanish2k09.sce.utils.extensions.rippleAnimationActivityOpener
import com.tanish2k09.sce.viewmodels.ConfigActivityVM
import com.tanish2k09.sce.viewmodels.SharedPrefsVM
import java.lang.ref.WeakReference

class ConfigActivity : AppCompatActivity(), IScriptCallback {

    private lateinit var binding: ActivityConfigBinding
    private lateinit var configVM: ConfigActivityVM
    private lateinit var sharedVM: SharedPrefsVM
    private lateinit var configScript: ConfigScript

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConfigBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configVM = ViewModelProvider(this).get(ConfigActivityVM::class.java)
        sharedVM = ViewModelProvider(this).get(SharedPrefsVM::class.java)

        initClickListeners()
        attachViewModelObservers()

        commenceConfigImport()
        createWeakConfigScript()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initClickListeners() {
        binding.saveButton.setOnClickListener {
            it.isEnabled = false
            configVM.exportConfigFile("/SmurfKernel", getString(R.string.configFile))
            configVM.configStore.commitActiveValues()
            updateConfigFragments()
            handleRunScript()
            it.isEnabled = true
        }

        binding.saveButton.setOnLongClickListener {
            Toast.makeText(this, "Save config", Toast.LENGTH_SHORT).show()
            return@setOnLongClickListener true
        }

        binding.applyConfigButton.setOnClickListener {
            it.isEnabled = false
            Toast.makeText(this, "Executing script...", Toast.LENGTH_SHORT).show()
            configScript.runScript(this)
        }

        binding.applyConfigButton.setOnLongClickListener {
            Toast.makeText(
                    this,
                    "Execute script to apply config",
                    Toast.LENGTH_SHORT).show()
            return@setOnLongClickListener true
        }

        binding.searchConfigButton.setOnClickListener {
            Toast.makeText(
                    this,
                    "Unavailable, search coming soon",
                    Toast.LENGTH_SHORT)
                    .show()
        }

        binding.searchConfigButton.setOnLongClickListener {
            Toast.makeText(this, "Search within variables", Toast.LENGTH_SHORT).show()
            return@setOnLongClickListener true
        }

        binding.settingsConfigButton.setOnTouchListener(object: View.OnTouchListener {
            override fun onTouch(v: View, m: MotionEvent): Boolean {
                if (m.action == MotionEvent.ACTION_UP) {
                    val intent = Intent(v.context, SettingsActivity::class.java)
                    rippleAnimationActivityOpener(m, binding.settingsConfigButton, intent)
                    return true
                }
                return false
            }
        })
    }

    private fun attachViewModelObservers() {
        sharedVM.theme.observe(this, Observer {
            handleTheme(it)
        })

        sharedVM.runScript.observe(this, Observer {
            if (it) {
                binding.saveButton.setImageResource(R.drawable.ic_save_apply)
            } else {
                binding.saveButton.setImageResource(R.drawable.ic_save)
            }
        })

        sharedVM.accentColor.observe(this, Observer {
            binding.configActionsBar.setCardBackgroundColor(Color.parseColor(it))
        })
    }

    private fun createWeakConfigScript() {
        val weakContext = WeakReference<Context>(applicationContext)
        configScript = ConfigScript(weakContext)
    }

    override fun onResume() {
        super.onResume()
        sharedVM.readSettingsToFields()
    }

    private fun handleTheme(newTheme: ETheme) {
        val mainColor = Color.parseColor(newTheme.hex)
        binding.topConfigLayout.setBackgroundColor(mainColor)
        window.statusBarColor = mainColor
        window.navigationBarColor = mainColor
    }

    private fun handleRunScript() {
        if (sharedVM.runScript.value == true) {
            binding.applyConfigButton.performClick()
        }
    }

    private fun commenceConfigImport() {
        removeConfigFragments()

        try {
            configVM.importConfigFile("/SmurfKernel", resources.getString(R.string.configFile))
        } catch (cfe: ConfigFormatException) {
            showErrorDialog(cfe)
            return
        }

        val size = configVM.configStore.linearCachedCodes.size
        Log.d("SCE-CIE", "Found $size values")

        if (size > 0) {
            fillConfigFragments()
        } else {
            Toast.makeText(this, "No values could be extracted", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun removeConfigFragments() {
        val fm = supportFragmentManager

        for (idx in configVM.configStore.linearCachedCodes.indices) {
            val cf = fm.findFragmentByTag(
                    "catFrag" +
                        (
                                configVM.configStore.getVar(
                                        configVM.configStore.linearCachedCodes[idx]
                                )?.category)
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
        val configStore = configVM.configStore

        for (idx in configStore.linearCachedCodes.indices) {
            val category = configStore.getVar(configStore.linearCachedCodes[idx])?.category
            var cf = fm.findFragmentByTag("catFrag$category") as CategoryFragment?

            // Create a new category fragment if it's null
            if (cf == null) {
                ft = fm.beginTransaction()
                cf = CategoryFragment(category?:getString(R.string.noCategoryLiteral))
                ft.add(binding.configContents.id, cf, "catFrag$category").commitNow()
            }

            val configVarFragment = ConfigVarFragment(configStore.getVar(configStore.linearCachedCodes[idx])!!)
            cf.pushConfigVarFragment(configVarFragment, idx)
        }
    }

    private fun updateConfigFragments() {
        val fm = supportFragmentManager
        val configStore = configVM.configStore

        for (idx in configStore.linearCachedCodes.indices) {
            val configVar = configStore.getVar(configStore.linearCachedCodes[idx])!!
            val cf = fm.findFragmentByTag("catFrag${configVar.category}") as CategoryFragment

            cf.updateConfigVarFragment(configVar, idx)
        }
    }

    private fun showErrorDialog(cfe: ConfigFormatException) {
        val builder = AlertDialog.Builder(this, R.style.dialogCustomStyle)

        builder.setPositiveButton("Aw snap") {
            dialog, _ ->
            run {
                dialog.dismiss()
                finish()
            }
        }

        val title = if (cfe.getLineNumber() != null) {
            getString(R.string.importErrorLine, cfe.getLineNumber().toString())
        } else {
            getString(R.string.importError)
        }

        builder.setTitle(title)
                .setMessage(cfe.response.description)
                .setCancelable(false)
                .create()
                .show()
    }

    override fun callback() {
        binding.applyConfigButton.isEnabled = true
    }
}
