package com.example.youtubeplayer.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.youtubeplayer.R
import com.example.youtubeplayer.model.VideoModel
import kotlinx.coroutines.flow.callbackFlow

class VideoAdapter(val callback: (String, String)-> Unit): ListAdapter<VideoModel, VideoAdapter.ViewHolder>(diffUtil) {

    inner class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

        fun bind(item: VideoModel) {

            val titleTextview = view.findViewById<TextView>(R.id.TitleTextView)
            val subtitleTextView = view.findViewById<TextView>(R.id.subTitleTextView)
            val thumbnailImageView = view.findViewById<ImageView>(R.id.thumbnailImageView)

            titleTextview.text = item.title
            subtitleTextView.text = item.subtitle

            Glide.with(thumbnailImageView.context)
                .load(item.thumb)
                .into(thumbnailImageView)

            view.setOnClickListener {
                callback(item.sources, item.title)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_video, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        return holder.bind(currentList[position])
    }


    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<VideoModel>() {
            override fun areItemsTheSame(oldItem: VideoModel, newItem: VideoModel): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: VideoModel, newItem: VideoModel): Boolean {
                return oldItem == newItem
            }

        }
    }


}