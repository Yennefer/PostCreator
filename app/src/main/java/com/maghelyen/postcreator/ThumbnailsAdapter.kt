package com.maghelyen.postcreator

import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.recyclerview.widget.RecyclerView

class ThumbnailsAdapter (
    private val thumbButtonData: List<ThumbButtonData>,
    private val thumbSelectedListener: ThumbSelectedListener) :
    RecyclerView.Adapter<ThumbnailsAdapter.SingleSelectionViewHolder>() {

    var selectedPosition = 0

    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): SingleSelectionViewHolder {
        val context = parent.context
        val resources = context.resources
        val thumbnailView = ImageView(context)
        val lp = RecyclerView.LayoutParams(
            resources.getDimensionPixelSize(R.dimen.thumb_size),
            resources.getDimensionPixelSize(R.dimen.thumb_size))
        lp.setMargins(
            resources.getDimensionPixelSize(R.dimen.small_margin),
            resources.getDimensionPixelSize(R.dimen.small_margin),
            resources.getDimensionPixelSize(R.dimen.small_margin),
            resources.getDimensionPixelSize(R.dimen.small_margin))
        thumbnailView.layoutParams = lp
        return SingleSelectionViewHolder(thumbnailView)
    }

    override fun onBindViewHolder(holder: SingleSelectionViewHolder, position: Int) {
        holder.bind(thumbButtonData[position])
    }

    override fun getItemCount() = thumbButtonData.size

    inner class SingleSelectionViewHolder(private val thumbView: ImageView) : RecyclerView.ViewHolder(thumbView) {

        fun bind(thumbButtonData: ThumbButtonData) {
            thumbView.setImageResource(thumbButtonData.thumbnail)
            thumbView.isSelected = selectedPosition == adapterPosition
            thumbView.setOnClickListener {

                if (selectedPosition != adapterPosition) {
                    notifyItemChanged(selectedPosition)

                    selectedPosition = adapterPosition
                    thumbView.isSelected = true

                    thumbButtonData.onClickAction?.invoke() ?: run {
                        thumbSelectedListener.onThumbSelected(thumbButtonData)
                    }
                }
            }
        }
    }
}

data class ThumbButtonData (
    @DrawableRes val thumbnail: Int,
    @DrawableRes val background: Int,
    val onClickAction: (() -> Unit)?
)

interface ThumbSelectedListener {
    fun onThumbSelected(thumbButtonData: ThumbButtonData)
}