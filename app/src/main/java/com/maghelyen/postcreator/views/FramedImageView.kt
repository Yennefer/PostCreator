package com.maghelyen.postcreator.views

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.maghelyen.postcreator.R

class FramedImageView : ImageView {
    var framedzzz: Boolean = false

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    fun setFramed(framed: Boolean) {
        if (framed) {
            setBackgroundColor(ContextCompat.getColor(context, R.color.light_gray))
        } else {
            setBackgroundColor(0)
        }
    }

    private fun init() {
        setOnClickListener {
            changeFramed()
        }

        changeFramed()
    }

    private fun changeFramed() {
        if (framedzzz) {
            setBackgroundColor(ContextCompat.getColor(context, R.color.light_gray))
        } else {
            setBackgroundColor(0)
        }

        framedzzz = !framedzzz
    }
}