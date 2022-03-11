package com.maghelyen.postcreator.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.widget.EditText

const val BACKGROUND_RADIUS = 20f

class EditTextWithBackground : EditText {
    private val rect = RectF()
    private val paint = Paint()
    private var backgroundColor: Int? = null

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    fun updateTextStyle(textColor: Int?, backgroundColor: Int?) {
        textColor?.let {
            setTextColor(it)
        }

        this.backgroundColor = backgroundColor
    }

    override fun onDraw(canvas: Canvas?) {
        val color = backgroundColor
        color?.let {
            val lineCount = layout.lineCount
            paint.color = it
            for (i in 0 until lineCount) {
                rect.top = layout.getLineTop(i).toFloat()
                rect.left = layout.getLineLeft(i) + paddingLeft - BACKGROUND_RADIUS
                rect.right = layout.getLineRight(i) + paddingRight + BACKGROUND_RADIUS
                rect.bottom = layout.getLineBottom(i).toFloat() - if (i + 1 == lineCount) 0 else layout.spacingAdd.toInt()

                canvas?.drawRoundRect(rect, BACKGROUND_RADIUS, BACKGROUND_RADIUS, paint)
            }
        }

        super.onDraw(canvas)
    }
}