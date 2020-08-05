package com.tanish2k09.sce.ui.fragments.containerFragments

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.tanish2k09.sce.R

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.tanish2k09.sce.data.config.ConfigVar
import com.tanish2k09.sce.data.constants.DefaultSettings
import com.tanish2k09.sce.databinding.FragmentConfigVarBinding
import com.tanish2k09.sce.ui.fragments.modals.ConfigOptionsModal
import com.tanish2k09.sce.interfaces.ISelectedItemCallback
import com.tanish2k09.sce.viewmodels.ConfigVarVM
import com.tanish2k09.sce.viewmodels.SharedPrefsVM

class ConfigVarFragment(private val configVar: ConfigVar) : Fragment(), ISelectedItemCallback {

    private lateinit var binding: FragmentConfigVarBinding
    private lateinit var configVarVM: ConfigVarVM
    private lateinit var sharedVM: SharedPrefsVM

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentConfigVarBinding.inflate(inflater)

        initVM()
        attachViewModelObservers()
        initClickListeners()

        return binding.root
    }

    private fun initVM() {
        configVarVM = ViewModelProvider(this).get(ConfigVarVM::class.java)
        sharedVM = requireActivity().run {
            ViewModelProvider(this).get(SharedPrefsVM::class.java)
        }

        updateConfigVar(configVar)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val handler = Handler()
        handler.postDelayed({
            binding.title.animateText(configVarVM.displayName.value)
        }, 100)
    }

    private fun attachViewModelObservers() {
        configVarVM.displayName.observe(viewLifecycleOwner, Observer {
            if (binding.title.text == it) {
                return@Observer
            }

            binding.title.animateText(it.trim())
        })

        configVarVM.value.observe(viewLifecycleOwner, Observer {
            binding.curVal.text = it
        })

        configVarVM.changed.observe(viewLifecycleOwner, Observer {
            binding.diffView.visibility = if (it) {
                View.VISIBLE
            } else {
                View.INVISIBLE
            }
        })

        sharedVM.useTitles.observe(viewLifecycleOwner, Observer {
            configVarVM.handleUseTitles(it)
        })

        sharedVM.accentColor.observe(viewLifecycleOwner, Observer {
            binding.curVal.setTextColor(Color.parseColor(it))
        })
    }

    private fun initClickListeners() {
        binding.containerLayout.setOnClickListener {
            ConfigOptionsModal(configVarVM.configVar, sharedVM.accentColor.value!!, this)
                    .show(childFragmentManager, configVarVM.displayName.value + " Selector")
        }

        binding.infoButtonConfig.setOnClickListener {
            showInfoDialog()
        }
    }

    fun updateConfigVar(configVariable: ConfigVar) {
        configVarVM.setDataFromConfigVar(
                configVariable,
                sharedVM.useTitles.value?:DefaultSettings.USE_TITLES)
    }

    override fun onItemClickedGov(position: Int) {
        configVarVM.setActiveValue(position)
    }

    private fun showInfoDialog() {
        val builder = AlertDialog.Builder(this.context, R.style.dialogCustomStyle)

        builder.setPositiveButton("Okay") { dialog, _ -> dialog.dismiss() }

        builder.setTitle(configVarVM.displayName.value)
                .setMessage(configVarVM.getInfoText())
                .setCancelable(true)
                .create()
                .show()
    }
}
