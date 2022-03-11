package com.maghelyen.postcreator

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.activity_editor.*
import kotlinx.android.synthetic.main.editor_view.*
import kotlinx.android.synthetic.main.toolbar_view.*
import kotlinx.coroutines.*
import java.io.File
import java.io.FileOutputStream
import java.util.*
import kotlin.coroutines.CoroutineContext

const val WRITE_STORAGE_PERMISSIONS_REQUEST = 0
const val IMAGE_EXT = ".png"
const val IMAGE_QUALITY = 100
const val IMAGE_DIR = "/PostCreator"

class EditorActivity : AppCompatActivity(), CoroutineScope, ThumbSelectedListener, StickerDialogFragment.StickerSelectedListener {

    private lateinit var stickerDialogFragment: StickerDialogFragment

    private var job = Job()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editor)

        initUI()
    }

    override fun onDestroy() {
        job.cancel()
        super.onDestroy()
    }

    override fun onThumbSelected(thumbButtonData: ThumbButtonData) {
        editor_view.setBackground(thumbButtonData.background)
    }

    override fun onStickerSelected(sticker: Int) {
        stickerDialogFragment.dismiss()
        editor_view.addSticker(sticker)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == Activity.RESULT_OK) {
                val path = result.uri.path
                path?.let { editor_view.setBackground(path) }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == WRITE_STORAGE_PERMISSIONS_REQUEST &&
            grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                saveEditorViewToImage()
            }
    }

    private fun initUI() {
        val saveButton = findViewById<Button>(R.id.save_button)

        // Editor view
        editor_view.setBackground(R.drawable.bg_white)
        editor_view.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(editable: Editable?) {
                editable?.let {
                    saveButton.isEnabled = !editable.isEmpty()
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })

        // Backgrounds list
        val thumbButtons = arrayListOf(
            ThumbButtonData(R.drawable.thumb_gray, R.drawable.bg_white, null),
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
        saveButton.setOnClickListener {

            // Ask for storage permission before saving image
            requestStoragePermission()
        }

        // Change font style button
        toolbar_icon_left.setOnClickListener {
            editor_view.nextTextStyle()
        }

        // Select sticker button
        toolbar_icon_right.setOnClickListener {
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

        backgrounds_list.apply {
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

    private fun requestStoragePermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                WRITE_STORAGE_PERMISSIONS_REQUEST)
        } else {
            saveEditorViewToImage()
        }
    }

    private fun saveEditorViewToImage() {
        updateProgressBar(true)

        launch {
            lateinit var path: String
            val bitmap = viewToBitmap(editor_view)

            withContext(Dispatchers.IO) {
                path = saveImage(bitmap)
            }

            updateProgressBar(false)
            showSaveSuccessMessage(path)
        }
    }

    private fun showSaveSuccessMessage(path: String) {
        Toast.makeText(this, resources.getString(R.string.save_successful_message, path), Toast.LENGTH_LONG).show()
    }

    private fun updateProgressBar(show: Boolean) {
        editor_text_view.isCursorVisible = !show
        editor_view.visibility = if (show) INVISIBLE else VISIBLE
        progress_bar.visibility = if (show) VISIBLE else INVISIBLE
    }

    private fun viewToBitmap(view: View): Bitmap {
        val returnedBitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(returnedBitmap)
        view.draw(canvas)
        return returnedBitmap
    }

    private fun saveImage(bitmap: Bitmap): String {

        // Prepare file name and path
        val imageFileName = Date().time.toString() + IMAGE_EXT
        val storageDir = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString()
                    + IMAGE_DIR)

        // Create file and path if it doesn't exist
        if (!storageDir.exists()) storageDir.mkdirs()
        val imageFile = File(storageDir, imageFileName)

        // Save bitmap to path
        try {
            val outputStream = FileOutputStream(imageFile)
            bitmap.compress(Bitmap.CompressFormat.PNG, IMAGE_QUALITY, outputStream)
            outputStream.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // Add the image to the system gallery
        broadcastGalleryUpdate(imageFile.absolutePath)

        return imageFile.path
    }

    private fun broadcastGalleryUpdate(imagePath: String) {
        val contentUri = Uri.fromFile( File(imagePath) )
        val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
        mediaScanIntent.data = contentUri
        this.sendBroadcast(mediaScanIntent)
    }
}