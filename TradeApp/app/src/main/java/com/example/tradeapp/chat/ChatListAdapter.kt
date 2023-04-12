package com.example.tradeapp.chat

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.tradeapp.databinding.ItemArticleBinding
import com.example.tradeapp.databinding.ItemChatListBinding
import com.example.tradeapp.home.ArticleModel
import java.text.SimpleDateFormat
import java.util.*

class ChatListAdapter(val onItemClicked: (ChatList) -> Unit) : ListAdapter<ChatList, ChatListAdapter.ViewHolder>(diffUtil) {

    inner class ViewHolder(private val binding: ItemChatListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(ChatListitem: ChatList) {

            binding.root.setOnClickListener {
                onItemClicked(ChatListitem)
            }

            binding.chatRoomTitleTextView.text= ChatListitem.itemTitle

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemChatListBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<ChatList>() {
            override fun areItemsTheSame(oldItem: ChatList, newItem: ChatList): Boolean {
                return oldItem.key == newItem.key
            }

            override fun areContentsTheSame(oldItem: ChatList, newItem: ChatList): Boolean {
                return oldItem == newItem
            }

        }
    }
}