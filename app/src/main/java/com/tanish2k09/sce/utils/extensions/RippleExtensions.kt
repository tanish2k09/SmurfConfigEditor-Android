package com.tanish2k09.sce.utils.extensions

import android.content.Intent
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity

fun AppCompatActivity.getRelativeLeft(view: View): Int {
    return if (view.parent == view.rootView)
        view.left
    else
        view.left + getRelativeLeft(view.parent as View)
}

fun AppCompatActivity.getRelativeTop(view: View): Int {
    return if (view.parent == view.rootView)
        view.top
    else
        view.top + getRelativeTop(view.parent as View)
}

fun AppCompatActivity.rippleAnimationActivityOpener(m: MotionEvent, v: View, i: Intent) {
    val revealX = (getRelativeLeft(v) + m.x.toInt())
    val revealY = (getRelativeTop(v) + m.y.toInt())
    i.putExtra("x", revealX)
    i.putExtra("y", revealY)
    this.startActivity(i)
}