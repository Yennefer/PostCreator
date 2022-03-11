package com.maghelyen.postcreator

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

const val COLUMNS_COUNT = 4

class StickerDialogFragment : BottomSheetDialogFragment() {
    companion object {
        fun newInstance(stickerSelectedListener: StickerSelectedListener) : StickerDialogFragment {
            return StickerDialogFragment().apply {
                this.stickerSelectedListener = stickerSelectedListener
            }
        }
    }

    lateinit var stickerSelectedListener: StickerSelectedListener

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.stickers_dialog, container, false)

        // Init sticker list
        val viewManager = GridLayoutManager(context, COLUMNS_COUNT)
        val viewAdapter = StickersAdapter(createStickersList(), stickerSelectedListener)

        view.findViewById<RecyclerView>(R.id.stickers_grid).apply {
            layoutManager = viewManager
            adapter = viewAdapter
        }

        return view
    }

    private fun createStickersList(): List<Int> = arrayListOf(
        R.drawable.sticker_1,
        R.drawable.sticker_2,
        R.drawable.sticker_3,
        R.drawable.sticker_4,
        R.drawable.sticker_5,
        R.drawable.sticker_6,
        R.drawable.sticker_7,
        R.drawable.sticker_8,
        R.drawable.sticker_9,
        R.drawable.sticker_10,
        R.drawable.sticker_11,
        R.drawable.sticker_12,
        R.drawable.sticker_13,
        R.drawable.sticker_14,
        R.drawable.sticker_15,
        R.drawable.sticker_16,
        R.drawable.sticker_17,
        R.drawable.sticker_18,
        R.drawable.sticker_19,
        R.drawable.sticker_20,
        R.drawable.sticker_21,
        R.drawable.sticker_22,
        R.drawable.sticker_23,
        R.drawable.sticker_24
    )

    class StickersAdapter (
        private val stickers: List<Int>,
        private val listener: StickerSelectedListener
    ) :
        RecyclerView.Adapter<StickersAdapter.StickerViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup,
                                        viewType: Int): StickerViewHolder {

            // Create view for sticker
            val context = parent.context
            val resources = context.resources
            val imageView = ImageView(context)
            val lp = RecyclerView.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
            lp.setMargins(
                resources.getDimensionPixelSize(R.dimen.small_margin),
                resources.getDimensionPixelSize(R.dimen.small_margin),
                resources.getDimensionPixelSize(R.dimen.small_margin),
                resources.getDimensionPixelSize(R.dimen.small_margin))
            imageView.layoutParams = lp
            return StickerViewHolder(imageView, context)
        }

        override fun onBindViewHolder(holder: StickerViewHolder, position: Int) {
            holder.bind(stickers[position])
        }

        override fun getItemCount() = stickers.size

        inner class StickerViewHolder(
            private val imageView: ImageView,
            private val context: Context
        ) : RecyclerView.ViewHolder(imageView) {

            fun bind(@DrawableRes sticker: Int) {

                // Init sticker view
                Glide.with(context)
                    .load(sticker)
                    .into(imageView)
                imageView.setOnClickListener {
                    listener.onStickerSelected(sticker)
                }
            }
        }
    }

    interface StickerSelectedListener {
        fun onStickerSelected(@DrawableRes sticker: Int)
    }
}