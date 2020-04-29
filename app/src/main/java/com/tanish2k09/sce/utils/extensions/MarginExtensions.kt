package com.tanish2k09.sce.utils.extensions

import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity

fun AppCompatActivity.changeBottomMargin(v: View, bMargin: Int) {
    if (v.layoutParams is ViewGroup.MarginLayoutParams) {
        val p = v.layoutParams as ViewGroup.MarginLayoutParams
        p.bottomMargin = bMargin
        v.requestLayout()
    }
}

fun AppCompatActivity.changeTopMargin(v: View, tMargin: Int) {
    if (v.layoutParams is ViewGroup.MarginLayoutParams) {
        val p = v.layoutParams as ViewGroup.MarginLayoutParams
        p.topMargin = tMargin
        v.requestLayout()
    }
}