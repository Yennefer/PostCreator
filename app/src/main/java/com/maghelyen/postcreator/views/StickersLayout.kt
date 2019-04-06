package com.maghelyen.postcreator.views

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.widget.FrameLayout

class StickersLayout : FrameLayout {
    private var mTouchSlop: Int = ViewConfiguration.get(context).scaledTouchSlop
    private var selectedSticker: View? = null
    private var dx: Float = 0f
    private var dy: Float = 0f
    private var lastx: Float = 0f
    private var lasty: Float = 0f
    private var stickerMoving = false

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun addView(child: View?) {
        super.addView(child)

        child?.let {
            selectedSticker?.isSelected = false
            it.isSelected = true
            selectedSticker = it
            it.setOnClickListener { view ->
                if (!view.isSelected) {
                    selectedSticker?.isSelected = false
                    view.isSelected = true
                    selectedSticker = view
                }

            }
        }
    }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        return when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                lastx = event.x
                lasty = event.y
                val ss = selectedSticker
                ss?.let {
                    dx = ss.x - event.x
                    dy = ss.y - event.y
                }
                false
            }
            MotionEvent.ACTION_MOVE -> {
                val xDiff = Math.abs(lastx - event.x)
                val yDiff = Math.abs(lasty - event.y)
                if (xDiff > mTouchSlop || yDiff > mTouchSlop) {
                    stickerMoving = true
                    true
                } else {
                    false
                }
            }
            MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
                false
            }
            else -> false
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val st = selectedSticker
        st?.let {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    lastx = event.x
                    lasty = event.y
                    dx = st.x - event.x
                    dy = st.y - event.y
                }
                MotionEvent.ACTION_MOVE -> {
                    val xDiff = Math.abs(lastx - event.x)
                    val yDiff = Math.abs(lasty - event.y)
                    if (xDiff > mTouchSlop || yDiff > mTouchSlop) {
                        stickerMoving = true
                        st.animate()
                            .x(event.x + dx)
                            .y(event.y + dy)
                            .setDuration(0)
                            .start()
                    }
                }
                MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
                    if (!stickerMoving) {
                        st.isSelected = false
                        selectedSticker = null
                    } else {
                        stickerMoving = false
                    }
                }
                else -> return false
            }
        }
        return true
    }
}