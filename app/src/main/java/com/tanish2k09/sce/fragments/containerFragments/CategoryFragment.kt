package com.tanish2k09.sce.fragments.containerFragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.tanish2k09.sce.R

/**
 * A simple [Fragment] subclass.
 * create an instance of this fragment.
 */
class CategoryFragment : Fragment() {

    private var categoryName = "Not categorized"
    private var catText: TextView? = null

    fun setInstanceCategory(category: String) {
        categoryName = category
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_category, container, false)

        catText = v.findViewById(R.id.categoryTitle)
        catText!!.text = categoryName

        return v
    }

    fun pushConfigVarFragment(`var`: ConfigVar, index: Int) {
        childFragmentManager.beginTransaction().add(R.id.categoryList, `var`, "configCard$index").commit()
    }

    fun popFragment(index: Int): Boolean {
        childFragmentManager.beginTransaction()
                .remove(childFragmentManager.findFragmentByTag("configCard$index")!!)
                .commitNow()
        return childFragmentManager.fragments.size == 0
    }
}// Required empty public constructor
