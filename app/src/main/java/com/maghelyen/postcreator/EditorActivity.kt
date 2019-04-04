package com.maghelyen.postcreator

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL
import androidx.recyclerview.widget.RecyclerView
import com.maghelyen.postcreator.views.EditorView
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView


class EditorActivity : AppCompatActivity(), ThumbSelectedListener, StickerDialogFragment.StickerSelectedListener {
    private lateinit var editor : EditorView
    private lateinit var stickerDialogFragment: StickerDialogFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editor)

        initUI()
    }

    override fun onThumbSelected(thumbButtonData: ThumbButtonData) {
        editor.setBackground(thumbButtonData.background)
    }

    override fun onStickerSelected(sticker: Int) {
        stickerDialogFragment.dismiss()
        editor.addSticker(sticker)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == Activity.RESULT_OK) {
                val resultUri = result.uri
                editor.setBackground(resultUri.path)
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.error
            }
        }
    }

    private fun initUI() {
        // Editor view
        editor = findViewById(R.id.editor_view)

        // Backgrounds list
        val thumbButtons = arrayListOf(
            ThumbButtonData(R.drawable.thumb_gray, 0, null),
            ThumbButtonData(R.drawable.thumb_blue, R.drawable.bg_blue_gradient, null),
            ThumbButtonData(R.drawable.thumb_green, R.drawable.bg_green_gradient, null),
            ThumbButtonData(R.drawable.thumb_orange, R.drawable.bg_orange_gradient, null),
            ThumbButtonData(R.drawable.thumb_red, R.drawable.bg_red_gradient, null),
            ThumbButtonData(R.drawable.thumb_purple, R.drawable.bg_purple_gradient, null),
            ThumbButtonData(R.drawable.thumb_beach, R.drawable.bg_beach, null),
            ThumbButtonData(R.drawable.thumb_stars, R.drawable.bg_stars, null),
            ThumbButtonData(R.drawable.thumb_load, 0, ::loadClicked))
        initThumbnailsList(thumbButtons)

        // Save button
        findViewById<Button>(R.id.save_button).setOnClickListener {
            editor.save()
        }

        // Change font style button
        findViewById<ImageView>(R.id.toolbar_icon_left).setOnClickListener {
            editor.nextTextStyle()
        }

        // Select sticker button
        findViewById<ImageView>(R.id.toolbar_icon_right).setOnClickListener {
            stickerDialogFragment.show(
                supportFragmentManager,
                stickerDialogFragment.tag
            )
        }

        // Stickers dialog
        stickerDialogFragment = StickerDialogFragment.newInstance(this)
    }

    private fun initThumbnailsList(thumbButtonData: List<ThumbButtonData>) {
        val viewManager = LinearLayoutManager(this)
        viewManager.orientation = HORIZONTAL

        val viewAdapter = ThumbnailsAdapter(thumbButtonData, this)

        findViewById<RecyclerView>(R.id.backgrounds_list).apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }
    }

    private fun loadClicked() {
        CropImage.activity()
            .setCropShape(CropImageView.CropShape.RECTANGLE)
            .setAspectRatio(1, 1)
            .setGuidelines(CropImageView.Guidelines.ON)
            .start(this)
    }
}