package com.maghelyen.postcreator.views

import android.content.Context
import android.graphics.Matrix
import android.graphics.PointF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView


class StickersLayout : FrameLayout {
    private var selectedSticker: ImageView? = null
    private var dx: Float = 0f
    private var dy: Float = 0f

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun addView(child: View?) {
        super.addView(child)

        child?.let {
            selectedSticker?.isSelected = false
            it.isSelected = true
            selectedSticker = it as ImageView
        }
    }

    var lastEvent: FloatArray? = null
    var d = 0f
    var newRot = 0f
    private val matrixXXX = Matrix()
    private val savedMatrix = Matrix()
    var fileNAME: String? = null
    var framePos = 0

    private val scale = 0f
    private val newDist = 0f

    // Fields
    private val TAG = this.javaClass.simpleName

    // We can be in one of these 3 states
    private val NONE = 0
    private val DRAG = 1
    private val ZOOM = 2
    private var mode = NONE

    // Remember some things for zooming
    private val start = PointF()
    private val mid = PointF()
    var oldDist = 1.0

    override fun onTouchEvent(event: MotionEvent?): Boolean {
//        event?.let {
//            when (event.action) {
//                MotionEvent.ACTION_DOWN // нажатие
//                -> {
//                    Log.d("MyLogs", "finger x = " + event.x.toString() + ", y = " + event.y.toString())
//
//                    val st = selectedSticker
//                    st?.let {
//                        Log.d("MyLogs", "sticker x = " + st.x.toString() + ", y = " + st.y.toString())
//                        dx = event.x - st.x
//                        dy = event.y - st.y
//                        Log.d("MyLogs", "dx = $dx, dy = $dy")
//                    }
//                }
//                MotionEvent.ACTION_MOVE // движение
//                -> {
//                    val st = selectedSticker
//                    st?.let {
//                        Log.d("MyLogs", "finger x = " + event.x.toString() + ", y = " + event.y.toString())
//                        Log.d("MyLogs", "sticker x = " + (st.x - dx).toString() + ", y = " + (st.y - dy).toString())
//                        moveView(st.x - dx, st.y - dy)
//                    }
//                }
//                else -> return true
//            }
//
//        }
        val view = selectedSticker

        // Handle touch events here...
        when (event!!.action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> {
                savedMatrix.set(matrixXXX)
                start.set(event.x, event.y)
                mode = DRAG
                lastEvent = null
            }
            MotionEvent.ACTION_POINTER_DOWN -> {
                oldDist = spacing(event)
                if (oldDist > 10f) {
                    savedMatrix.set(matrixXXX)
                    midPoint(mid, event)
                    mode = ZOOM
                }
                val le = FloatArray(4)
                le[0] = event.getX(0)
                le[1] = event.getX(1)
                le[2] = event.getY(0)
                le[3] = event.getY(1)
                lastEvent = le
                d = rotation(event)
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP -> {
                mode = NONE
                lastEvent = null
            }
            MotionEvent.ACTION_MOVE -> if (mode == DRAG) {
                // ...
                matrixXXX.set(savedMatrix)
                matrixXXX.postTranslate(event.x - start.x, event.y - start.y)
            } else if (mode == ZOOM && event.pointerCount == 2) {
                val newDist = spacing(event)
                matrixXXX.set(savedMatrix)
                if (newDist > 10f) {
                    val scale = (newDist / oldDist).toFloat()
                    matrixXXX.postScale(scale, scale, mid.x, mid.y)
                }
                if (lastEvent == null) {
                    newRot = rotation(event)
                    val r = newRot - d
                    matrixXXX.postRotate(
                        r, view!!.measuredWidth / 2f,
                        view!!.measuredHeight / 2f
                    )
                }
            }
        }

        view!!.imageMatrix = matrixXXX

        return true
    }

    private fun rotation(event: MotionEvent): Float {
        val delta_x = (event.getX(0) - event.getX(1)).toDouble()
        val delta_y = (event.getY(0) - event.getY(1)).toDouble()
        val radians = Math.atan2(delta_y, delta_x)

        return Math.toDegrees(radians).toFloat()
    }

    private fun spacing(event: MotionEvent): Double {
        val x = event.getX(0) - event.getX(1)
        val y = event.getY(0) - event.getY(1)
        return Math.sqrt((x * x + y * y).toDouble())
    }

    private fun midPoint(point: PointF, event: MotionEvent) {
        val x = event.getX(0) + event.getX(1)
        val y = event.getY(0) + event.getY(1)
        point.set(x / 2, y / 2)
    }

    private fun moveView(x: Float, y: Float) {
        val params =
            FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        params.leftMargin = Math.round(x)
        params.topMargin = Math.round(y)
        selectedSticker?.layoutParams = params
    }
}