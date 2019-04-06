package com.maghelyen.postcreator.views

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.net.Uri
import android.os.Environment
import android.util.AttributeSet
import android.util.Log
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
import com.muddzdev.viewtobitmaplibrary.OnBitmapSaveListener
import com.muddzdev.viewtobitmaplibrary.ViewToBitmap
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.lang.Exception
import java.util.*

class EditorView : FrameLayout {
    private var i = 0

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
        lp.gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
        stickerView.layoutParams = lp
        stickerView.setBackgroundResource(R.drawable.sticker_frame)
        stickerView.isSelected = true

        Glide.with(context)
            .load(sticker)
            .into(stickerView)

        stickersLayer.addView(stickerView)
    }

    fun save() {
//        val image = ViewToBitmap(this)
//        image.setOnBitmapSaveListener {
//                isSaved, path -> Toast.makeText(context, "Bitmap Saved at; $path", Toast.LENGTH_SHORT).show()
//        }
//        image.saveToGallery()
        val path = saveImage(viewToBitmap())
        Toast.makeText(context, "Bitmap Saved at; $path", Toast.LENGTH_SHORT).show()
    }

    private fun viewToBitmap(): Bitmap {
        //Define a bitmap with the same size as the view
        val returnedBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        //Bind a canvas to it
        val canvas = Canvas(returnedBitmap)
        //Get the view's background
        val bgDrawable = background
        //has background drawable, then draw it on the canvas
        bgDrawable.draw(canvas)
        // draw the view on the canvas
        draw(canvas)
        //return the bitmap
        return returnedBitmap
    }

    private fun saveImage(bitmap: Bitmap): String? {
        var savedImagePath: String? = null

        val imageFileName = "JPEG_" + "FILE_NAME" + ".png"
        val storageDir = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString()
                           + "/YOUR_FOLDER_NAME")
        var success = true
        if (!storageDir.exists()) {
            success = storageDir.mkdirs()
        }
        if (success) {
            val imageFile = File(storageDir, imageFileName)
            savedImagePath = imageFile.absolutePath
            try {
                val fOut = FileOutputStream(imageFile)
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut)
                fOut.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }

            // Add the image to the system gallery
            galleryAddPic(savedImagePath)
            Toast.makeText(context, "IMAGE SAVED", Toast.LENGTH_LONG).show()
        }
        return savedImagePath
    }

    private fun galleryAddPic(imagePath: String) {
        val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
        val file = File(imagePath)
        val contentUri = Uri.fromFile(file)
        mediaScanIntent.data = contentUri
        context.sendBroadcast(mediaScanIntent)
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