package com.tanish2k09.sce.fragments.containerFragments

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView

import com.tanish2k09.sce.R
import com.tanish2k09.sce.fragments.modals.ConfigOptionsModal
import com.tanish2k09.sce.utils.ConfigCache
import com.tanish2k09.sce.utils.StringVal

import android.content.Context.MODE_PRIVATE
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class ConfigVar : Fragment(), ConfigOptionsModal.Listener, View.OnClickListener {

    private var curVal: TextView? = null
    private var title: TextView? = null
    private var svc: StringVal? = null
    private var accentCol: Int = 0

    fun setupCardInfo(index: Int): Boolean {
        svc = ConfigCache.getStringVal(index)
        return svc != null
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_config_var, container, false)

        title = v.findViewById(R.id.title)
        curVal = v.findViewById(R.id.curVal)

        title!!.text = svc!!.name.toUpperCase(Locale.getDefault())
        curVal!!.text = svc!!.activeVal

        val llTopCard = v.findViewById<LinearLayout>(R.id.ll_topCard)
        llTopCard.setOnClickListener(this)

        val infoButton = v.findViewById<ImageButton>(R.id.infoButtonConfig)
        infoButton.setOnClickListener(this)

        return v
    }

    override fun onResume() {
        super.onResume()
        val sp = curVal!!.context.getSharedPreferences("settings", MODE_PRIVATE)
        accentCol = Color.parseColor(sp.getString("accentCol", "#00bfa5"))
        curVal!!.setTextColor(accentCol)

        if (sp.getBoolean("useTitlesOnCards", true)) {
            title!!.text = svc!!.title
            title!!.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
            title!!.setTypeface(Typeface.DEFAULT, Typeface.BOLD)
        } else {
            title!!.text = svc!!.name.toUpperCase(Locale.getDefault())
            title!!.setTypeface(Typeface.MONOSPACE, Typeface.BOLD)
        }
    }

    override fun onItemClickedGov(position: Int) {
        svc!!.activeVal = svc!!.getOption(position)!!
        curVal!!.text = svc!!.activeVal
    }

    private fun showInfoDialog(v: View) {
        val builder = AlertDialog.Builder(v.context, R.style.dialogCustomStyle)

        builder.setPositiveButton("Okay") { dialog, _ -> dialog.dismiss() }

        builder.setTitle(svc!!.title)
                .setMessage(svc!!.descriptionString)
                .setCancelable(true)
                .create()
                .show()
    }

    override fun onClick(v: View) {
        if (v.id == R.id.ll_topCard)
            ConfigOptionsModal.newInstance(svc, accentCol).show(childFragmentManager, svc!!.name + " Selector")
        else
            showInfoDialog(v)
    }
}// Required empty public constructor
