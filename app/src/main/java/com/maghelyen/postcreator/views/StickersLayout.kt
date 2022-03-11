package com.maghelyen.postcreator.views

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import android.widget.FrameLayout
import com.almeros.android.multitouch.MoveGestureDetector
import com.almeros.android.multitouch.RotateGestureDetector

const val MIN_SCALE_FACTOR = 0.1f
const val MAX_SCALE_FACTOR = 5.0f

class StickersLayout : FrameLayout {
    interface OnStickerDragListener {
        fun onStickerStartDragging(v: View)
        fun onStickerFinishDragging(v: View)
        fun onStickerDragging(touchX: Float, touchY: Float, v: View)
    }

    private var selectedSticker: View? = null
    private var onStickerDragListener: OnStickerDragListener? = null

    private var scaleFactor = 1f
    private var rotationDegrees = 0f
    private var focusX = 0f
    private var focusY = 0f

    private var moveDetector: MoveGestureDetector
    private var scaleDetector: ScaleGestureDetector
    private var rotateDetector: RotateGestureDetector

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        moveDetector = MoveGestureDetector(context, MoveListener())
        scaleDetector = ScaleGestureDetector(context, ScaleListener())
        rotateDetector = RotateGestureDetector(context, RotateListener())
    }

    override fun addView(child: View?) {
        child?.apply {
            setOnTouchListener { view, event ->
                if (event.actionMasked == MotionEvent.ACTION_DOWN) {

                    // Select touched sticker
                    selectedSticker = view

                    // Update movement/scaling/rotation parameters
                    scaleFactor = view.scaleX
                    rotationDegrees = view.rotation
                    focusX = view.x
                    focusY = view.y
                }
                return@setOnTouchListener false
            }
        }

        super.addView(child)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {

        // Handle touch event only if we have selected sticker to move/scale/rotate
        val sticker = selectedSticker
        sticker?.apply {

            // Provide touch event to detectors
            moveDetector.onTouchEvent(event)
            scaleDetector.onTouchEvent(event)
            rotateDetector.onTouchEvent(event)

            // Animate movement/scaling/rotation
            animate()
                .x(focusX)
                .y(focusY)
                .scaleX(scaleFactor)
                .scaleY(scaleFactor)
                .rotation(rotationDegrees)
                .setDuration(0)
                .start()

            // Notify sticker drag listener of sticker movement
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    onStickerDragListener?.onStickerStartDragging(this)
                }
                MotionEvent.ACTION_MOVE -> {
                    onStickerDragListener?.onStickerDragging(event.x, event.y, this)
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    onStickerDragListener?.onStickerFinishDragging(this)

                    // Deselect currently selected sticker
                    selectedSticker = null
                }
            }
        }

        return true
    }

    fun setOnStickerDragListener(onStickerDragListener: OnStickerDragListener) {
        this.onStickerDragListener = onStickerDragListener
    }

    private inner class MoveListener : MoveGestureDetector.SimpleOnMoveGestureListener() {
        override fun onMove(detector: MoveGestureDetector): Boolean {
            val d = detector.focusDelta
            focusX += d.x
            focusY += d.y
            return true
        }
    }

    private inner class ScaleListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            scaleFactor *= detector.scaleFactor
            scaleFactor = Math.max(MIN_SCALE_FACTOR, Math.min(scaleFactor, MAX_SCALE_FACTOR))
            return true
        }
    }

    private inner class RotateListener : RotateGestureDetector.SimpleOnRotateGestureListener() {
        override fun onRotate(detector: RotateGestureDetector): Boolean {
            rotationDegrees -= detector.rotationDegreesDelta
            return true
        }
    }
}