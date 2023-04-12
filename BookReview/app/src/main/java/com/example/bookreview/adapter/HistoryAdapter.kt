package com.example.bookreview.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.bookreview.databinding.ItemHistoryBinding
import com.example.bookreview.model.History

class HistoryAdapter(val historyDeleteClickedListner: (String)->Unit): ListAdapter<History,HistoryAdapter.HistoryItemViewHolder> (diffUtil) {

    inner class HistoryItemViewHolder(private val binding: ItemHistoryBinding) :  RecyclerView.ViewHolder(binding.root){

        fun bind(HistoryModel: History){
            binding.historyTextView.text = HistoryModel.keyword
            binding.historydeleteButton.setOnClickListener {
                historyDeleteClickedListner(HistoryModel.keyword.orEmpty())
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryItemViewHolder {
        // 미리 만들어진 뷰 홀더가 없을 경우 생성하는 함수
        return HistoryItemViewHolder(ItemHistoryBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: HistoryItemViewHolder, position: Int) {
        //뷰 홀더에 데이터를 바인드 하게 해주는 함수
        holder.bind(currentList[position])
    }
    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<History>(){
            override fun areItemsTheSame(oldItem: History, newItem: History): Boolean {
                // old item 과 new item의 content이 같냐
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: History, newItem: History): Boolean {
                // old item 과 new item의 content이 같냐
                return oldItem.uid == newItem.uid
            }

        }
    }

}