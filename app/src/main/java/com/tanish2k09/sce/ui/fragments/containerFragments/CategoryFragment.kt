package com.tanish2k09.sce.ui.fragments.containerFragments

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider

import com.tanish2k09.sce.R
import com.tanish2k09.sce.data.config.ConfigVar
import com.tanish2k09.sce.data.enums.ETheme
import com.tanish2k09.sce.databinding.FragmentCategoryBinding
import com.tanish2k09.sce.viewmodels.SharedPrefsVM

class CategoryFragment(private val cat: String) : Fragment() {

    private lateinit var binding: FragmentCategoryBinding
    private lateinit var sharedVM: SharedPrefsVM

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentCategoryBinding.inflate(inflater, container, false)
        binding.categoryTitle.text = cat

        sharedVM = requireActivity().run {
            ViewModelProvider(this).get(SharedPrefsVM::class.java)
        }

        attachViewModelObservers()

        return binding.root
    }

    private fun attachViewModelObservers() {
        sharedVM.theme.observe(viewLifecycleOwner, Observer {
            handleTheme(it)
        })

        sharedVM.accentColor.observe(viewLifecycleOwner, Observer {
            binding.headingUnderline.setBackgroundColor(Color.parseColor(it))
        })
    }

    private fun handleTheme(newTheme: ETheme) {
        val mainColor = Color.parseColor(newTheme.hex)
        binding.titleGroupBg.setBackgroundColor(mainColor)
    }

    fun pushConfigVarFragment(configVarFragment: ConfigVarFragment, index: Int) {
        childFragmentManager
                .beginTransaction()
                .add(R.id.categoryList, configVarFragment, "configCard$index")
                .commitNow()
    }

    fun updateConfigVarFragment(configVar: ConfigVar, index: Int) {
        (childFragmentManager.findFragmentByTag("configCard$index")!! as ConfigVarFragment)
                .updateConfigVar(configVar)
    }

    fun popFragment(index: Int): Boolean {
        childFragmentManager.beginTransaction()
                .remove(childFragmentManager.findFragmentByTag("configCard$index")!!)
                .commitNow()
        return childFragmentManager.fragments.size == 0
    }
}
