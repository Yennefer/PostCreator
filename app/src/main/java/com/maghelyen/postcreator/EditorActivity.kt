package com.maghelyen.postcreator

import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.LinearLayoutManager.HORIZONTAL
import android.support.v7.widget.RecyclerView
import android.widget.Button
import android.widget.Toast
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.net.URI

class EditorActivity : AppCompatActivity(), ThumbSelectedListener {
    private val RESULT_LOAD_IMAGE = 1

    private lateinit var editor : EditorView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editor)

        initUI()
    }

    override fun onThumbSelected(thumbButtonData: ThumbButtonData) {
        editor.setBackground(thumbButtonData.background)
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
            Toast.makeText(this, "SAVE CLICKED", Toast.LENGTH_SHORT).show()
            editor.save()
        }
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
        val i = Intent(
            Intent.ACTION_PICK,
            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )

        startActivityForResult(i, RESULT_LOAD_IMAGE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            val selectedImage = data.data

            val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)

            val cursor = contentResolver.query(
                selectedImage,
                filePathColumn, null, null, null
            )

            cursor.moveToFirst()
            val columnIndex = cursor.getColumnIndex(filePathColumn[0])
            val picturePath = cursor.getString(columnIndex)

            cursor.close()

            val file = File(picturePath)

//            val file = File(data.data.path)
            val fileInputStream = contentResolver.openInputStream(data.data)

            editor.setBackground(fileInputStream)
        }
    }
}