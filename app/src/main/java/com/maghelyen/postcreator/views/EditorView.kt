package com.maghelyen.postcreator.views

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.maghelyen.postcreator.R
import java.io.File

class EditorView : FrameLayout {
    enum class TextStyle(val textColor: Int, val backgroundColor: Int?) {
        BLACK(R.color.black, null) {
            override fun next() = WHITE
        },
        WHITE(R.color.white, null) {
            override fun next() = BLACK_BACKGROUND
        },
        BLACK_BACKGROUND(R.color.black, R.color.white) {
            override fun next() = WHITE_BACKGROUND
        },
        WHITE_BACKGROUND(R.color.white, R.color.white_alpha50) {
            override fun next() = BLACK
        };

        abstract fun next(): TextStyle
    }

    private lateinit var backgroundView: ImageView
    private lateinit var stickersLayer: FrameLayout
    private lateinit var textView: EditTextWithBackground

    private var textStyle: TextStyle =
        TextStyle.BLACK

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    fun setBackground(@DrawableRes drawable: Int) {
        Glide.with(context)
            .load(drawable)
            .into(backgroundView)
    }

    fun setBackground(filePath: String) {
        Glide.with(context)
            .asBitmap()
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .load(File(filePath))
            .into(backgroundView)
    }

    fun nextTextStyle() {
        textStyle = textStyle.next()
        updateTextColor()
        invalidate()
    }

    fun addSticker(sticker: Int) {
        val stickerView = ImageView(context)
        val lp = FrameLayout.LayoutParams(
            resources.getDimensionPixelSize(R.dimen.sticker_default_size),
            resources.getDimensionPixelSize(R.dimen.sticker_default_size))
        lp.gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
        stickerView.layoutParams = lp
        stickerView.setBackgroundResource(R.drawable.abc_ab_share_pack_mtrl_alpha)
        Glide.with(context)
            .load(sticker)
            .into(stickerView)

        stickersLayer.addView(stickerView)
    }

    fun save() {
        Toast.makeText(context, "SAVE CLICKED", Toast.LENGTH_SHORT).show()
    }

    private fun init() {
        this.setWillNotDraw(false)

        View.inflate(context, R.layout.editor_view, this)

        backgroundView = findViewById(R.id.editor_background_view)
        textView = findViewById(R.id.editor_text_view)
        stickersLayer = findViewById(R.id.editor_stickers_layer)

        updateTextColor()
    }

    private fun updateTextColor() {
        textView.setTextColor(ContextCompat.getColor(context, textStyle.textColor))
        textView.setBgdColor(textStyle.backgroundColor)
    }
}