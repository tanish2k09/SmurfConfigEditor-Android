package com.pes.androidmaterialcolorpickerdialog

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import android.util.AttributeSet
import android.util.TypedValue
import androidx.annotation.ColorInt
import androidx.annotation.Dimension
import androidx.appcompat.widget.AppCompatSeekBar
import com.tanish2k09.sce.R

/**
 * Created by Patrick Geselbracht on 2017-03-04
 *
 * @author Patrick Geselbracht <github@pattafeufeu.de>
 * @since v1.1.0
 */
class MaterialColorPickerTextSeekBar : AppCompatSeekBar {

    private lateinit var textPaint: Paint
    private lateinit var textRect: Rect

    @ColorInt
    private var textColor: Int = 0

    @Dimension(unit = 2)
    private var textSize: Float = 0f

    private lateinit var text: String

    constructor(context: Context, attrs: AttributeSet): super(context) {
        init(attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int)
            : super(context, attrs, defStyleAttr) {
        init(attrs)
    }

    private fun init(attrs: AttributeSet) {
        textPaint = Paint(Paint.LINEAR_TEXT_FLAG or Paint.ANTI_ALIAS_FLAG)
        textRect = Rect()

        val typedArray = context.obtainStyledAttributes(
                attrs, R.styleable.MaterialColorPickerTextSeekBar
        )

        try {
            textColor = typedArray.getColor(
                    R.styleable.MaterialColorPickerTextSeekBar_android_textColor,
                    0xff000000.toInt()
            )

            textSize = typedArray.getDimension(
                    R.styleable.MaterialColorPickerTextSeekBar_android_textSize,
                    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                            18F, resources.displayMetrics)
            )

            text = typedArray.getString(R.styleable
                    .MaterialColorPickerTextSeekBar_android_text).toString()

        } finally {
            typedArray.recycle()
        }

        textPaint.color = textColor
        textPaint.typeface = Typeface.DEFAULT_BOLD
        textPaint.textSize = textSize
        textPaint.textAlign = Paint.Align.CENTER

        /* Measures 255 instead of the actual text because otherwise the padding would jump up
         * and down each time the text with its ascender and descenders changes.
         *
         * --
         *
         * Since we're only interested in a roundabout height depending on the text's font size
         * anyway, calculating the text bounds of this value is enough in this case.
         */
        textPaint.getTextBounds("255", 0, 3, textRect)

        setPadding(paddingLeft, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                (0.6f * textRect.height().toFloat()), resources.displayMetrics).toInt(),
                paddingRight, paddingBottom)
    }

     override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.drawText(
                text,
                (thumb.bounds.left + paddingLeft).toFloat(),
                (textRect.height() + (paddingTop shr 2)).toFloat(),
                textPaint
        )
    }
}
