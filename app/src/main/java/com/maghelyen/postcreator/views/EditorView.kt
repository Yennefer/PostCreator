package com.maghelyen.postcreator.views

import android.content.Context
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.maghelyen.postcreator.R
import kotlinx.android.synthetic.main.editor_view.view.*
import java.io.File


class EditorView : FrameLayout, StickersLayout.OnStickerDragListener {
    enum class TextStyle {
        BLACK {
            override fun next() = WHITE
        },
        WHITE {
            override fun next() = BLACK_BACKGROUND
        },
        BLACK_BACKGROUND {
            override fun next() = WHITE_BACKGROUND
        },
        WHITE_BACKGROUND {
            override fun next() = BLACK
        };

        var textColor: Int? = null
        var backgroundColor: Int? = null
        abstract fun next(): TextStyle
    }

    private var textStyle: TextStyle = TextStyle.BLACK

    constructor(context: Context) : super(context) {
        initUI()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initUI()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initUI()
    }

    override fun onStickerStartDragging(v: View) {

        // Show trash icon
        editor_trash_icon.visibility = View.VISIBLE
    }

    override fun onStickerFinishDragging(v: View) {

        // Remove icon if necessary, hide trash icon
        if (editor_trash_icon.isSelected) {
            editor_stickers_layer.removeView(v)
            editor_trash_icon.isSelected = false
        }
        editor_trash_icon.visibility = View.GONE
    }

    override fun onStickerDragging(touchX: Float, touchY: Float, v: View) {

        // Update trash icon and sticker depending on sticker position
        val stickerOnTrash = touchX > editor_trash_icon.left &&
                touchX < editor_trash_icon.right &&
                touchY > editor_trash_icon.top &&
                touchY < editor_trash_icon.bottom

        editor_trash_icon.isSelected = stickerOnTrash
        v.alpha = if (stickerOnTrash) 0.5f else 1f
    }

    fun addTextChangedListener(watcher: TextWatcher) {
        editor_text_view.addTextChangedListener(watcher)
    }

    fun setBackground(@DrawableRes drawable: Int) {
        Glide.with(context)
            .load(drawable)
            .into(editor_background_view)
    }

    fun setBackground(filePath: String) {
        Glide.with(context)
            .asBitmap()
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .load(File(filePath))
            .into(editor_background_view)
    }

    fun nextTextStyle() {
        textStyle = textStyle.next()
        updateTextStyle()
    }

    fun addSticker(@DrawableRes sticker: Int) {

        // Setup sticker view and pass it to editor
        val stickerView = ImageView(context)

        val lp = FrameLayout.LayoutParams(
            resources.getDimensionPixelSize(R.dimen.sticker_default_size),
            resources.getDimensionPixelSize(R.dimen.sticker_default_size))
        lp.gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
        stickerView.layoutParams = lp

        Glide.with(context)
            .load(sticker)
            .into(stickerView)

        editor_stickers_layer.addView(stickerView)
    }

    private fun initUI() {
        View.inflate(context, R.layout.editor_view, this)

        editor_stickers_layer.setOnStickerDragListener(this)

        // Init text style enums
        val colorWhite = ContextCompat.getColor(context, R.color.white)
        val colorWhiteTransparent = ContextCompat.getColor(context, R.color.white_alpha50)
        val colorBlack = ContextCompat.getColor(context, R.color.black)
        TextStyle.BLACK.textColor = colorBlack
        TextStyle.WHITE.textColor = colorWhite
        TextStyle.BLACK_BACKGROUND.textColor = colorBlack
        TextStyle.BLACK_BACKGROUND.backgroundColor = colorWhite
        TextStyle.WHITE_BACKGROUND.textColor = colorWhite
        TextStyle.WHITE_BACKGROUND.backgroundColor = colorWhiteTransparent

        updateTextStyle()
    }

    private fun updateTextStyle() {
        editor_text_view.updateTextStyle(textStyle.textColor, textStyle.backgroundColor)
    }
}