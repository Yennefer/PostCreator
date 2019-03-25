package com.maghelyen.postcreator

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.LinearLayoutManager.HORIZONTAL
import android.support.v7.widget.RecyclerView
import android.widget.Button
import android.widget.Toast


class EditorActivity : AppCompatActivity(), ThumbSelectedListener {
    lateinit var editor : EditorView

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
        Toast.makeText(this, "LOAD CLICKED", Toast.LENGTH_SHORT).show()
    }
}
